package wup.data;

import java.util.Date;

/**
 * 공개 게시물에 작성된 댓글 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class Comment {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private User user;
    private String text;

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setText(String text) {
        this.text = text;
    }
}
