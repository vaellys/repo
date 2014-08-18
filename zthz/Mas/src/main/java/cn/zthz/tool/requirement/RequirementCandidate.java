package cn.zthz.tool.requirement;

import java.sql.Timestamp;

import cn.zthz.tool.db.Ignore;
import cn.zthz.tool.db.QueryField;

public class RequirementCandidate {
	private String id;
	private String userId;
	private String words;
	// private String pictureId;
	private String mainPicture;
	private String thumbs;

	private Double longitude;
	private Double latitude;
	private String address;
	/**
	 * ["a.jpb"]
	 */
	private String pictures;
	private String requirementId;
	private Timestamp createTime;
	private int status;

	@Ignore
	@QueryField
	private String userName;
	@Ignore
	@QueryField
	private String userPicture;

	
	public String getThumbs() {
		return thumbs;
	}
	
	public void setThumbs(String thumbs) {
		this.thumbs = thumbs;
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getWords() {
		return words;
	}

	public void setWords(String words) {
		this.words = words;
	}

	// public String getPictureId() {
	// return pictureId;
	// }
	//
	// public void setPictureId(String pictureId) {
	// this.pictureId = pictureId;
	// }

	public String getRequirementId() {
		return requirementId;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMainPicture() {
		return mainPicture;
	}

	public void setMainPicture(String mainPicture) {
		this.mainPicture = mainPicture;
	}

	public String getPictures() {
		return pictures;
	}

	public void setPictures(String pictures) {
		this.pictures = pictures;
	}

	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPicture() {
		return userPicture;
	}

	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}

}
