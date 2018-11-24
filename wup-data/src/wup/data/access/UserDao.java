package wup.data.access;

import java.util.List;

import wup.data.Group;
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
     * 주어진 <code>group</code>의 모든 구성원의 리스트를 가져옵니다.
     *
     * @param group 구성원을 조회할 그룹
     */
    public DaoResult<List<User>> getMembers(Group group);

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
     * @param id   변경할 사용자의 일련번호
     * @param user 변경할 정보가 들어 있는 {@link User} 객체
     */
    public DaoResult<User> updateUser(int id, User user);

    /**
     * 주어진 id에 해당하는 사용자를 삭제합니다.
     *
     * @param id 삭제할 사용자의 일련번호
     */
    public DaoResult<Boolean> deleteUser(int id);

    /**
     * 주어진 이메일 주소와 암호를 이용하여 인증을 시도합니다.
     *
     * @param email 사용자 이메일 주소
     * @param auth  암호
     */
    public DaoResult<Boolean> authenticate(String email, String auth);

    /**
     * 주어진 id에 해당하는 사용자의 암호를 변경합니다.
     *
     * @param id      암호를 변경할 사용자의 일련번호
     * @param oldAuth 이전 암호
     * @param newAuth 새로운 암호
     */
    public DaoResult<Boolean> updateAuth(int id, String oldAuth, String newAuth);
}
