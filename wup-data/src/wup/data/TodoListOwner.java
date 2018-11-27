package wup.data;

/**
 * 할 일 목록을 소유할 수 있는 개체에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface TodoListOwner {
    public enum Type {
        PLANNER, SCHEDULE
    }

    public int getId();

    public void setId(int id);
}
