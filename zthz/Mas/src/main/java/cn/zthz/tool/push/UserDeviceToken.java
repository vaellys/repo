package cn.zthz.tool.push;

public class UserDeviceToken {
	private String userId;
	private String iosToken;
	private String androidToken;

	public UserDeviceToken() {
		super();
	}

	public UserDeviceToken(String userId, String iosToken, String androidToken) {
		super();
		this.userId = userId;
		this.iosToken = iosToken;
		this.androidToken = androidToken;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIosToken() {
		return iosToken;
	}

	public void setIosToken(String iosToken) {
		this.iosToken = iosToken;
	}

	public String getAndroidToken() {
		return androidToken;
	}

	public void setAndroidToken(String androidToken) {
		this.androidToken = androidToken;
	}

}
