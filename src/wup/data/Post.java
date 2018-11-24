package wup.data;

import java.util.Date;
import java.util.List;

/**
 * 공개 게시물 정보를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class Post {
    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private ItemOwner.Type type;
    private ItemOwner owner;
    private Schedule schedule;
    private String title;
    private String text;
    private List<Media> media;

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

    public Schedule getSchedule() {
        return schedule;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<Media> getMedia() {
        return media;
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

    public void setType(ItemOwner.Type type) {
        this.type = type;
    }

    public void setOwner(ItemOwner owner) {
        this.owner = owner;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setMedia(List<Media> media) {
        this.media = media;
    }
}
