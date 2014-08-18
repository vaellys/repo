package cn.zthz.tool.message;

import java.sql.Timestamp;

public class UnreceivedMessage {
	private long id;
	private long sessionId;
	private String message;
	private Timestamp sendTime;
	/**
	 * 0:未发送，1：已发送成功 2:发送失败 
	 */
	private int status;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

}
