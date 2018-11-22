package wup.data;

import java.util.Date;

/**
 * 캘린더 사용자 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class User {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private String email;
    private String fullName;
    private String nickname;
    private boolean isVerified;
    private String avatar;

    // Getters

    public int getId() {
	return id;
    }

    public Date getCreatedAt() {
	return createdAt;
    }

    public Date getModifiedAt() {
	return modifiedAt;
    }

    public String getEmail() {
	return email;
    }

    public String getFullName() {
	return fullName;
    }

    public String getNickname() {
	return nickname;
    }

    public boolean getIsVerified() {
	return isVerified;
    }

    public String getAvatar() {
	return avatar;
    }

    // Setters

    public void setId(int id) {
	this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
	this.createdAt = createdAt;
    }

    public void setModifiedAt(Date modifiedAt) {
	this.modifiedAt = modifiedAt;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public void setFullName(String fullName) {
	this.fullName = fullName;
    }

    public void setNickname(String nickname) {
	this.nickname = nickname;
    }

    public void setIsVerified(boolean isVerified) {
	this.isVerified = isVerified;
    }

    public void setAvatar(String avatar) {
	this.avatar = avatar;
    }
}




































































































