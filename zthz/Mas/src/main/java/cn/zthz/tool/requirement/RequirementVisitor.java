package cn.zthz.tool.requirement;

import java.sql.Timestamp;

import cn.zthz.tool.db.Ignore;
import cn.zthz.tool.db.QueryField;

public class RequirementVisitor {
	private Integer id;
	private String requirementId;
	private String userId;
	private Timestamp viewTime;
	@Ignore
	@QueryField
	private String userName;
	@Ignore
	@QueryField
	private String userPicture;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(String requirementId) {
		this.requirementId = requirementId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getViewTime() {
		return viewTime;
	}

	public void setViewTime(Timestamp viewTime) {
		this.viewTime = viewTime;
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
