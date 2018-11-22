package wup.data.access;

import wup.data.User;

/**
 * 사용자 정보 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface UserDao {
    /**
     * 주어진 id에 해당하는 사용자를 가져옵니다.
     * 
     * @param id 사용자 일련번호
     */
    public DaoResult<User> getUser(int id);

    /**
     * 새로운 사용자를 생성합니다.
     * 
     * @param user 사용자 생성 뷰에서 입력된 정보
     * @param auth 암호(평문). 데이터베이스에 해시되어 저장됩니다.
     */
    public DaoResult<User> createUser(User user, String auth);

    /**
     * 주어진 id에 해당하는 사용자의 정보를 변경합니다.
     * 
     * @param id      변경할 사용자의 일련번호
     * @param user    변경할 정보가 들어 있는 {@link User} 객체
     * @param oldAuth 암호를 변경하는 경우에는 기존 암호, 그렇지 않으면 <code>null</code> 또는 빈 문자열
     * @param newAuth 암호를 변경하는 경우에는 새로운 암호, 그렇지 않으면 <code>null</code> 또는 빈 문자열
     */
    public DaoResult<User> updateUser(int id, User user, String oldAuth, String newAuth);

    /**
     * 주어진 id에 해당하는 사용자를 삭제합니다.
     * 
     * @param id 삭제할 사용자의 일련번호
     */
    public DaoResult<Boolean> deleteUser(int id);
}
