package wup.data.access;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;

import wup.data.User;

/**
 * MariaDB를 통해 사용자 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong
 */
public class MariaDbUserDao extends JdbcDao implements UserDao {

    private static final String CONN_NAME = "jdbc/mariadb";

    private static final String SQL_GET_BY_ID = "SELECT * FROM `user` WHERE `id`=?";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM `user` WHERE `id`=?";
    private static final String SQL_PARAM_NAMES = "(`created_at`, `modified_at`, `email`, `auth`, `full_name`, `nickname`, `verified`, `avatar`)";
    private static final String SQL_PARAM_VALUES = "(?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT = "INSERT INTO `user` " + SQL_PARAM_NAMES + " VALUES " + SQL_PARAM_VALUES;
    private static final String SQL_UPDATE_BY_ID = "UPDATE `user` SET `modified_at`=?, `full_name`=?, `nickname`=?, `avatar`=? WHERE `id`=?";

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.UserDao#getUser(int)
     */
    @Override
    public DaoResult<User> getUser(int id) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_ID)) {
            stmt.setInt(1, id);

            try (ResultSet result = stmt.executeQuery()) {
                User user = null;

                if (result.next()) {
                    user = new User();

                    user.setId(result.getInt("id"));
                    user.setCreatedAt(result.getTimestamp("created_at"));
                    user.setModifiedAt(result.getTimestamp("modified_at"));
                    user.setEmail(result.getString("email"));
                    user.setFullName(result.getString("full_name"));
                    user.setNickname(result.getString("nickname"));
                    user.setIsVerified(result.getBoolean("verified"));
                    user.setAvatar(result.getString("avatar"));
                }

                return DaoResult.succeed(DaoResult.Action.READ, user);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.UserDao#createUser(wup.data.User, java.lang.String)
     */
    @Override
    public DaoResult<User> createUser(User user, String auth) {
        try (Connection conn = getConnection("jdbc/mariadb");
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setString(3, user.getEmail());
            stmt.setString(4, hashAuth(auth));
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getNickname());
            stmt.setBoolean(7, false);
            stmt.setString(8, user.getAvatar());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdUserId = generatedKeys.getInt(1);

                return getUser(createdUserId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.UserDao#updateUser(int, wup.data.User, java.lang.String,
     * java.lang.String)
     */
    @Override
    public DaoResult<User> updateUser(int id, User user, String oldAuth, String newAuth) {
        try (Connection conn = getConnection("jdbc/mariadb");
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_BY_ID, Statement.RETURN_GENERATED_KEYS)) {
            DaoResult<User> getUserResult = getUser(id);

            if (!getUserResult.didSucceed()) {
                return getUserResult;
            }

            boolean shouldUpdateModifiedAt = false;
            User oldUser = getUserResult.getData();
            String newFullName = user.getFullName();
            String newNickname = user.getNickname();
            String newAvatar = user.getAvatar();

            if (!newFullName.equals(oldUser.getFullName())) {
                shouldUpdateModifiedAt = true;
            }

            if (!newNickname.equals(oldUser.getNickname())) {
                shouldUpdateModifiedAt = true;
            }

            if (!newAvatar.equals(oldUser.getAvatar())) {
                shouldUpdateModifiedAt = true;
            }

            Timestamp newModifiedAt = shouldUpdateModifiedAt ? new Timestamp(new Date().getTime())
                    : new Timestamp(oldUser.getCreatedAt().getTime());

            stmt.setTimestamp(1, newModifiedAt);
            stmt.setString(2, newFullName);
            stmt.setString(3, newNickname);
            stmt.setString(4, newAvatar);
            stmt.setInt(5, id);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int modifiedUserId = generatedKeys.getInt(1);

                return getUser(modifiedUserId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.UPDATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.UserDao#deleteUser(int)
     */
    @Override
    public DaoResult<Boolean> deleteUser(int id) {
        try (Connection conn = getConnection(CONN_NAME);
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_ID)) {
            stmt.setInt(1, id);

            int deletedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.DELETE, deletedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.DELETE, e);
        }
    }

    private String hashAuth(String auth) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        Base64.Encoder enc = Base64.getEncoder();

        md.update(auth.getBytes(StandardCharsets.UTF_8));

        return enc.encodeToString(md.digest());
    }

}
