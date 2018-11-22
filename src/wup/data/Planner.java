package wup.data;

import java.util.Date;

/**
 * 플래너 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class Planner {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private ItemOwner.Type type;
    private ItemOwner owner;
    private String title;

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

    public ItemOwner.Type getType() {
        return type;
    }

    public ItemOwner getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
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

    public void setType(ItemOwner.Type type) {
        this.type = type;
    }

    public void setOwner(ItemOwner owner) {
        this.owner = owner;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
