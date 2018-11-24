package wup.data;

import java.util.Date;

/**
 * 공개 게시물에 첨부된 이미지나 동영상 등의 미디어를 관리하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class Media {
    public enum Type {
        IMAGE, VIDEO
    }

    private int id;
    private Date createdAt;
    private Date modifiedAt;
    private Type type;
    private String path;

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public Type getType() {
        return type;
    }

    public String getPath() {
        return path;
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

    public void setType(Type type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
