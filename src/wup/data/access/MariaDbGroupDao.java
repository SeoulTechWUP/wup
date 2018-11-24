package wup.data.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wup.data.Group;
import wup.data.User;

/**
 * MariaDB를 통해 사용자 그룹 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong
 */
public class MariaDbGroupDao extends JdbcDao implements GroupDao {

    private static final String CONN_NAME = "jdbc/mariadb";

    private static final String SQL_GET_BY_ID = "SELECT * FROM `group` WHERE `id` = ?";
    private static final String SQL_GET_BY_OWNER = "SELECT * FROM `group` WHERE `owner` = ?";
    private static final String SQL_INSERT = "INSERT INTO `group` (`created_at`, `modified_at`, `owner`, `name`) VALUES (?, ?, ?, ?)";
    private static final String SQL_UPDATE_BY_ID = "UPDATE `group` SET `modified_at` = ?, `name` = ? WHERE `id` = ?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM `group` WHERE `id` = ?";
    private static final String SQL_ADD_MEMBER = "INSERT INTO `membership` (`created_at`, `modified_at`, `user_id`, `group_id`) VALUES (?, ?, ?, ?)";
    private static final String SQL_REMOVE_MEMBER = "DELETE FROM `membership` WHERE `user_id` = ? AND `group_id` = ?";

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#getGroup(int)
     */
    @Override
    public DaoResult<Group> getGroup(int id) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_ID)) {
            stmt.setInt(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                Group group = null;

                if (result.next()) {
                    group = getGroupFromResultSet(result, true);
                }

                return DaoResult.succeed(DaoResult.Action.READ, group);
            }

        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#getGroups(wup.data.User)
     */
    @Override
    public DaoResult<List<Group>> getGroups(User user) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_OWNER)) {
            stmt.setInt(1, user.getId());

            try (ResultSet result = stmt.executeQuery()) {
                List<Group> groups = new ArrayList<Group>();

                while (result.next()) {
                    Group group = getGroupFromResultSet(result, false);

                    group.setOwner(user);

                    groups.add(group);
                }

                return DaoResult.succeed(DaoResult.Action.READ, groups);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#createGroup(wup.data.User, wup.data.Group)
     */
    @Override
    public DaoResult<Group> createGroup(User user, Group group) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setInt(3, user.getId());
            stmt.setString(4, group.getName());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdGroupId = generatedKeys.getInt(1);

                group.setId(createdGroupId);

                DaoResult<Boolean> addMemberResult = addMember(group, user);

                if (!addMemberResult.didSucceed()) {
                    return DaoResult.fail(DaoResult.Action.CREATE, addMemberResult.getException());
                }

                return getGroup(createdGroupId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#updateGroup(int, wup.data.Group)
     */
    @Override
    public DaoResult<Group> updateGroup(int id, Group group) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_BY_ID, Statement.RETURN_GENERATED_KEYS)) {
            DaoResult<Group> getGroupResult = getGroup(id);

            if (!getGroupResult.didSucceed()) {
                return getGroupResult;
            }

            boolean shouldUpdateModifiedAt = false;
            Group oldGroup = getGroupResult.getData();
            String newName = group.getName();

            if (!newName.equals(oldGroup.getName())) {
                shouldUpdateModifiedAt = true;
            }

            Timestamp newModifiedAt = shouldUpdateModifiedAt ? new Timestamp(new Date().getTime())
                    : new Timestamp(oldGroup.getModifiedAt().getTime());

            stmt.setTimestamp(1, newModifiedAt);
            stmt.setString(2, newName);
            stmt.setInt(3, id);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int modifiedGroupId = generatedKeys.getInt(1);

                return getGroup(modifiedGroupId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.UPDATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#deleteGroup(int)
     */
    @Override
    public DaoResult<Boolean> deleteGroup(int id) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_ID)) {
            stmt.setInt(1, id);

            int deletedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.DELETE, deletedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.DELETE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#addMember(wup.data.Group, wup.data.User)
     */
    @Override
    public DaoResult<Boolean> addMember(Group group, User user) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_ADD_MEMBER)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setInt(3, user.getId());
            stmt.setInt(4, group.getId());

            int insertedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.CREATE, insertedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.GroupDao#removeMember(wup.data.Group, wup.data.User)
     */
    @Override
    public DaoResult<Boolean> removeMember(Group group, User user) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_REMOVE_MEMBER)) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, group.getId());

            int deletedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.DELETE, deletedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.DELETE, e);
        }
    }

    private Group getGroupFromResultSet(ResultSet rs, boolean includeOwner) throws Exception {
        Group group = new Group();

        group.setId(rs.getInt("id"));
        group.setCreatedAt(rs.getTimestamp("created_at"));
        group.setModifiedAt(rs.getTimestamp("modified_at"));
        group.setName(rs.getString("name"));

        DaoResult<List<User>> getMembersResult = new MariaDbUserDao().getMembers(group);

        if (!getMembersResult.didSucceed()) {
            throw getMembersResult.getException();
        }

        group.setMembers(getMembersResult.getData());

        if (includeOwner) {
            DaoResult<User> getUserResult = new MariaDbUserDao().getUser(rs.getInt("owner"));

            if (!getUserResult.didSucceed()) {
                throw getUserResult.getException();
            }

            group.setOwner(getUserResult.getData());
        }

        return group;
    }

}
