package cn.zthz.tool.queue;

public interface UQueue {
	void publish(String subject , Object message);
//	void subscribe(String subject , OnMessage messageHandler);
	void subscribe(OnMessage messageHandler,String... subject );
	void subscribeEx(OnMessage messageHandler,String... subjectExp );
	
	void publish(Object message);
	void subscribe(Class<?>  clazz , OnClassMessage messageHandler);
	void start();
	
}
