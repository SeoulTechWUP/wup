package wup.data;

import java.util.Date;

/**
 * 할 일 목록의 각 항목에 대한 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class TodoListItem {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private String title;
    private boolean complete;

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public String getTitle() {
        return title;
    }

    public boolean getComplete() {
        return complete;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
