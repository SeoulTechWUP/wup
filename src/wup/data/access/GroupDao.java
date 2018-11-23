package wup.data.access;

import java.util.List;

import wup.data.Group;
import wup.data.User;

/**
 * 사용자 그룹 정보 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface GroupDao {
    /**
     * 주어진 <code>id</code>에 해당하는 사용자 그룹을 가져옵니다.
     *
     * @param id 사용자 그룹의 일련번호
     */
    public DaoResult<Group> getGroup(int id);

    /**
     * 주어진 <code>user</code>가 소유하고 있는 모든 사용자 그룹의 리스트를 가져옵니다.
     *
     * @param user 조회할 사용자 개체
     */
    public DaoResult<List<Group>> getGroups(User user);

    /**
     * 주어진 <code>user</code>가 소유하게 될 새로운 사용자 그룹을 생성합니다.
     *
     * @param user  새로운 사용자 그룹의 소유자
     * @param group 생성할 사용자 그룹의 정보
     */
    public DaoResult<Group> createGroup(User user, Group group);

    /**
     * 주어진 <code>id</code>에 해당하는 사용자 그룹의 정보를 변경합니다.
     *
     * @param id    변경할 사용자 그룹의 일련번호
     * @param group 변경할 정보가 들어 있는 {@link Group} 개체
     */
    public DaoResult<Group> updateGroup(int id, Group group);

    /**
     * 주어진 <code>id</code>에 해당하는 사용자 그룹을 삭제합니다.
     *
     * @param id 삭제할 사용자 그룹의 일련번호
     */
    public DaoResult<Boolean> deleteGroup(int id);

    /**
     * 주어진 <code>group</code>의 모든 구성원의 리스트를 가져옵니다.
     *
     * @param group 구성원을 조회할 그룹
     */
    public DaoResult<List<User>> getMembers(Group group);

    /**
     * 주어진 <code>group</code>에 구성원을 추가합니다.
     *
     * @param group 구성원을 추가할 그룹
     * @param user  추가할 사용자
     */
    public DaoResult<Boolean> addMember(Group group, User user);

    /**
     * 주어진 <code>group</code>에서 구성원을 제거합니다.
     *
     * @param group 구성원을 제거할 그룹
     * @param user  제거할 사용자
     */
    public DaoResult<Boolean> removeMember(Group group, User user);
}
