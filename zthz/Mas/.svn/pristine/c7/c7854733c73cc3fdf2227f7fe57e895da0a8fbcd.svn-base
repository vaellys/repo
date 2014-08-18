package cn.zthz.tool.queue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.zthz.tool.common.Tuple;

public class DUQueue implements UQueue {
	private static final Log log = LogFactory.getLog(DUQueue.class);
	final BlockingQueue<Tuple<String, Object>> subjectQueue = new LinkedBlockingQueue<>();
	final BlockingQueue<Object> classQueue = new LinkedBlockingQueue<>();
	final Map<String, List<OnMessage>> subjectCallbackMap = new HashMap<>();
	final Map<Pattern, List<OnMessage>> subjectExCallbackMap = new HashMap<>();
	final Map<Class<?>, List<OnClassMessage>> classCallbackMap = new HashMap<>();
	final ExecutorService subjectExecutorService = Executors.newFixedThreadPool(10);
	final ExecutorService classService = Executors.newFixedThreadPool(10);
	long pollTimeout = 3;

	@Override
	public void start() {
		if (log.isInfoEnabled()) {
			log.info("start DUQueue");
		}
		subjectExecutorService.execute(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						final Tuple<String, Object> tuple = subjectQueue.poll(pollTimeout, TimeUnit.SECONDS);
						if (null == tuple) {
							continue;
						}
						final String subject = tuple.key;
						final List<OnMessage> onMessages = subjectCallbackMap.get(subject);
						subjectExecutorService.submit(new Runnable() {

							@Override
							public void run() {
								if (null != onMessages) {
									for (OnMessage onMessage : onMessages) {
										onMessage.handle(tuple.key, tuple.value);
									}
								}
							}
						});
						for (Entry<Pattern, List<OnMessage>> entry : subjectExCallbackMap.entrySet()) {
							if (!entry.getKey().matcher(subject).matches()) {
								continue;
							}
							final List<OnMessage> onMessagesEx = entry.getValue();
							subjectExecutorService.submit(new Runnable() {

								@Override
								public void run() {
									if (null != onMessagesEx) {
										for (OnMessage onMessage : onMessagesEx) {
											onMessage.handle(tuple.key, tuple.value);
										}
									}
								}
							});
						}
					} catch (Exception e) {
						log.error(e);
					}
				}
			}
		});
		classService.execute(new Runnable() {

			@Override
			public void run() {

				while (true) {
					try {
						final Object message = classQueue.poll(pollTimeout, TimeUnit.SECONDS);
						if (null == message) {
							continue;
						}
						final Class<?> clazz = message.getClass();
						classService.execute(new Runnable() {
							@Override
							public void run() {
								for (Entry<Class<?>, List<OnClassMessage>> entry : classCallbackMap.entrySet()) {
									if (entry.getKey().isAssignableFrom(clazz)) {
										List<OnClassMessage> onMessages = entry.getValue();
										if (null != onMessages) {
											for (OnClassMessage onMessage : onMessages) {
												onMessage.handle(message);
											}
										}
									}
								}
							}
						});
					} catch (Exception e) {
						log.error(e);
					}
				}
			}
		});
	}

	@Override
	public void publish(String subject, Object message) {
		try {
			subjectQueue.put(new Tuple<String, Object>(subject, message));
		} catch (InterruptedException e) {
			throw new UQueueException("put subject message error!", e);
		}

	}

	@Override
	public void subscribe(OnMessage messageHandler, String... subjects) {
		for (String subject : subjects) {

			List<OnMessage> onMessages = subjectCallbackMap.get(subject);
			if (null == onMessages) {
				onMessages = new LinkedList<>();
				subjectCallbackMap.put(subject, onMessages);
			}
			onMessages.add(messageHandler);
		}
	}

	@Override
	public void publish(Object message) {
		if (null != message) {
			try {
				classQueue.put(message);
			} catch (InterruptedException e) {
				throw new UQueueException("put class message error!", e);
			}
		}
	}

	@Override
	public void subscribeEx(OnMessage messageHandler, String... subjectExps) {
		for (String subjectExp : subjectExps) {

			List<OnMessage> onMessages = subjectExCallbackMap.get(subjectExp);
			if (null == onMessages) {
				onMessages = new LinkedList<>();
				subjectExCallbackMap.put(Pattern.compile(subjectExp), onMessages);
			}
			onMessages.add(messageHandler);
		}
	}

	@Override
	public void subscribe(Class<?> clazz, OnClassMessage messageHandler) {
		List<OnClassMessage> onMessages = classCallbackMap.get(clazz);
		if (null == onMessages) {
			onMessages = new LinkedList<>();
			classCallbackMap.put(clazz, onMessages);
		}
		onMessages.add(messageHandler);
	}

	public static void main(String[] args) throws InterruptedException {
		UQueue queue = new DUQueue();
		queue.start();
		// queue.subscribe(String.class, new OnClassMessage() {
		//
		// @Override
		// public void handle(Object message) {
		// try {
		// Thread.sleep(1000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// System.out.println(message+":"+Thread.currentThread());
		// }
		// });
		// for (int i = 0; i < 1000; i++) {
		//
		// queue.publish("hello" + i+"-");
		// }
		queue.subscribe(new OnMessage() {

			@Override
			public void handle(String subject, Object message) {
				System.out.println(subject + ":" + message + "-" + Thread.currentThread());
			}
		}, "yeah");
		// queue.subscribeEx("^y.*$", new OnMessage() {
		//
		// @Override
		// public void handle(String subject, Object message) {
		// System.out.println(subject+"Ex:"+message+"-"+Thread.currentThread());
		// }
		// });
		queue.publish("yeah", "fdfdfd");
		// Thread.sleep(1000);
	}

}
