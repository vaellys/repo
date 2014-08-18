package cn.zthz.tool.requirement;

import java.sql.Timestamp;

import cn.zthz.tool.db.Ignore;
import cn.zthz.tool.db.QueryField;

public class RequirementComment {
	private String id;
	private String requirementId;
	private String userId;
	private String comment;
	private Timestamp commentTime;
	@Ignore
	@QueryField
	private String userPicture;
	@Ignore
	@QueryField
	private String userName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public String getUserPicture() {
		return userPicture;
	}

	public void setUserPicture(String userPicture) {
		this.userPicture = userPicture;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Timestamp getCommentTime() {
		return commentTime;
	}

	public void setCommentTime(Timestamp commentTime) {
		this.commentTime = commentTime;
	}

}
