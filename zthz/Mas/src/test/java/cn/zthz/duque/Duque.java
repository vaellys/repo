package cn.zthz.duque;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import cn.zthz.tool.common.Tuple;
import cn.zthz.tool.queue.OnMessage;

public class Duque {
	public static final BlockingQueue<Tuple<String, Object>> subjectQueue = new LinkedBlockingQueue<>();
	public static final Map<String, List<OnMessage>> subjectCallbackMap = new HashMap<>();
	public static final Map<Pattern, List<OnMessage>> subjectCallbackExMap = new HashMap<>();
	public static final ExecutorService executorService = Executors.newFixedThreadPool(10);
	static final int pollTime = 3;
	public static void start(){
		executorService.execute(new Runnable() {
			
			@Override
			public void run() {
				while(true){
				try {
					
					final Tuple<String, Object> tuple = subjectQueue.poll(pollTime, TimeUnit.SECONDS);
					if(null == tuple){
						continue;
					}
					final String subject = (String)tuple.key;
					final List<OnMessage> messages = subjectCallbackMap.get(subject);
					executorService.submit(new Runnable() {
						
						@Override
						public void run() {
							if(null != messages){
								for(OnMessage onMessage : messages){
									onMessage.handle(tuple.key, tuple.value);
								}
							}
							
						}
					});
					for(Map.Entry<Pattern, List<OnMessage>> entry : subjectCallbackExMap.entrySet()){
						if(!entry.getKey().matcher(subject).matches()){
							continue;
						}
						final List<OnMessage> messageEx = entry.getValue();
						executorService.submit(new Runnable() {
							
							@Override
							public void run() {
								if(null != messageEx){
								for(OnMessage message : messageEx){
									message.handle(tuple.key, tuple.value);
								}
								}
							}
						});
						
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
		});
		
	}
	
	
	public void subscribe(OnMessage messageHandler, String... subjects){
		for(String subject : subjects){
			List<OnMessage> messages = subjectCallbackMap.get(subject);
			if(messages == null){
				messages = new LinkedList<>();
				subjectCallbackMap.put(subject, messages);
			}
			messages.add(messageHandler);
		}
	}
}
