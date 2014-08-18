package cn.zthz.tool.message;

import java.sql.Timestamp;

import cn.zthz.tool.db.Ignore;

public class Message {

	@Ignore
	private Long id;
	private String message;
	private String senderId;
	@Ignore
	public int type;
	@Ignore
	public Long mType;
	private String receiverId;
	private String uuid;
	private Timestamp sendTime;
	private int soundLength;
	/**
	 * 消息类型 4：邀请消息
	 */
	private Integer iType;
	/**
	 * 0:未发送，1：已发送成功 2:发送失败
	 */
	private int sendStatus;

	 private int receiveStatus;
	// private String refRequirementName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	// public String getRefRequirementName() {
	// return refRequirementName;
	// }
	//
	// public void setRefRequirementName(String refRequirementName) {
	// this.refRequirementName = refRequirementName;
	// }

	public Timestamp getSendTime() {
		return sendTime;
	}

	public void setSendTime(Timestamp sendTime) {
		this.sendTime = sendTime;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	 public int getReceiveStatus() {
	 return receiveStatus;
	 }
	
	 public void setReceiveStatus(int receiveStatus) {
	 this.receiveStatus = receiveStatus;
	 }

	public int getSoundLength() {
		return soundLength;
	}

	public void setSoundLength(int soundLength) {
		this.soundLength = soundLength;
	}

	public Integer getiType() {
		return iType;
	}

	public void setiType(Integer iType) {
		this.iType = iType;
	}
	
	

}
