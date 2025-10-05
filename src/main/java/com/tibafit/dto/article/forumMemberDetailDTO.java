package com.tibafit.dto.article;

import java.util.Date;

public class forumMemberDetailDTO {
	private String profilePicture;
	private String nickName;
	private String email;
	private Date createTime;
	private Integer userId;
	
	
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getProfilePicture() {
		return profilePicture;
	}
	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public forumMemberDetailDTO() {
		super();
	}
	@Override
	public String toString() {
		return "forumMemberDetailDTO [profilePicture=" + profilePicture + ", nickName=" + nickName + ", email=" + email
				+ ", createTime=" + createTime + ", userId=" + userId + "]";
	}
	public forumMemberDetailDTO(String profilePicture, String nickName, String email, Date createTime, Integer userId) {
		super();
		this.profilePicture = profilePicture;
		this.nickName = nickName;
		this.email = email;
		this.createTime = createTime;
		this.userId = userId;
	}
	
	
	
	
}
