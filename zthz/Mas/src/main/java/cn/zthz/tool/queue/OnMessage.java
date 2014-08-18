package cn.zthz.tool.queue;

public interface OnMessage {
	public void handle(String subject , Object message);
}
