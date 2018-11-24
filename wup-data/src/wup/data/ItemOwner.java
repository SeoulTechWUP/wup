package wup.data;

/**
 * 플래너 또는 게시물을 소유할 수 있는 주체를 나타내는 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface ItemOwner {
    public enum Type {
        USER, GROUP
    }

    public int getId();

    public void setId(int id);
}
