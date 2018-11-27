package wup.data;

import java.util.Date;

/**
 * 할 일 목록 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class TodoList {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private TodoListOwner.Type type;
    private TodoListOwner owner;
    private Date date;
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

    public TodoListOwner.Type getType() {
        return type;
    }

    public TodoListOwner getOwner() {
        return owner;
    }

    public Date getDate() throws IllegalStateException {
        if (type != TodoListOwner.Type.SCHEDULE) {
            throw new IllegalStateException();
        }

        return date;
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

    public void setType(TodoListOwner.Type type) {
        this.type = type;
    }

    public void setOwner(TodoListOwner owner) {
        this.owner = owner;
    }

    public void setDate(Date date) throws IllegalStateException {
        if (type != TodoListOwner.Type.SCHEDULE) {
            throw new IllegalStateException();
        }

        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
