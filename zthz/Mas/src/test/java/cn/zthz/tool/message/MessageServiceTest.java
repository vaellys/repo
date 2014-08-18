package cn.zthz.tool.message;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import cn.zthz.tool.common.HttpUtils;
import cn.zthz.tool.common.LogUtils;
import cn.zthz.tool.push.PushException;

public class MessageServiceTest {

	@Test
	public void testSave() throws MessageException {
		List<Message> messages = new LinkedList<>();
		for (int i = 0; i < 10; i++) {

			Message message = new Message();
			message.setMessage("hello");
			message.setReceiverId("1");
			message.setSenderId("10");
			message.setSendStatus(MessageStatus.SEND_SUCCESS);
//			message.setReceiveStatus(MessageStatus.RECEIVER_UNREAD);
			message.setSendTime(new Timestamp(System.currentTimeMillis()));
			// messages.add(message);
			MessageService.instance.save(message);
		}

	}

	@Test
	public void testGetSenders() throws MessageException {
		List<Map<String, Object>> result = MessageService.instance.getSenders("1", 0, 100);
		System.out.println(LogUtils.format("result", result));
	}

	@Test
	public void testQueryMessages() throws MessageException {
		List<Map<String, Object>> result = MessageService.instance.queryMessages("1", "10", 39L, 1, 10);
		System.out.println(LogUtils.format("result", result));
	}
	@Test 
	public void testSend() throws PushException{
		MessageService.instance.send("402880173f07abcf013f08a0f97f002d", "402880173f07abcf013f08a0f039002c", "how are you!");
	}
	
	@Test
	public void testMessageWithSound() throws IOException{
		String url = "http://localhost:8080/message/send.json";
		//String images = "http://220.113.10.142/images/a4/e5/a4e5660b23764e7a56689bcf3b103227.640x960.jpg";
		File sound_file = new File("E:\\download\\国歌.mp3");
		System.out.println(sound_file.length()/1024);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", "402880173f07abcf013f08a0f97f002d");
		params.put("userToken", "e0ddde3a4c40439e8160f03f310e94e9");
		params.put("receiverId", "402880173f07abcf013f08a0f039002c");
		params.put("soundLength", "1");
		params.put("sound", sound_file);
		try {
			HttpUtils.doMultipart(url,params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
