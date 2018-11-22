package wup.data;

import java.util.Date;
import java.util.List;

/**
 * 사용자 그룹 정보를 관리하는 클래스입니다.
 *
 * @author dmsql
 */
public class Group implements ItemOwner {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private User owner;
    private String name;
    private List<User> members;

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

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
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

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }
}
