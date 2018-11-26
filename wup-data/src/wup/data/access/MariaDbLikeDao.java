package wup.data.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;

import wup.data.Post;
import wup.data.User;

/**
 * MariaDB를 통해 공개 게시물에 대한 "좋아요" 표시 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong
 */
public class MariaDbLikeDao extends MariaDbDao implements LikeDao {

    private static final String SQL_GET_COUNT = "SELECT COUNT(`user_id`) FROM `like` WHERE `post_id` = ?";
    private static final String SQL_GET_LIKE = "SELECT * FROM `like` WHERE `post_id` = ? AND `user_id` = ?";
    private static final String SQL_LIKE_POST = "INSERT INTO `like` (`post_id`, `user_id`, `created_at`, `modified_at`) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE `post_id` = `post_id`";
    private static final String SQL_UNLIKE_POST = "DELETE FROM `like` WHERE `post_id` = ? AND `user_id` = ?";

    public MariaDbLikeDao(JdbcConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.LikeDao#getLikeCount(wup.data.Post)
     */
    @Override
    public DaoResult<Integer> getLikeCount(Post post) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_COUNT)) {
            stmt.setInt(1, post.getId());

            try (ResultSet result = stmt.executeQuery()) {
                int count = 0;

                if (result.next()) {
                    count = result.getInt(1);
                }

                return DaoResult.succeed(DaoResult.Action.READ, count);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.LikeDao#getLike(wup.data.Post, wup.data.User)
     */
    @Override
    public DaoResult<Boolean> getLike(Post post, User user) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_LIKE)) {
            stmt.setInt(1, post.getId());
            stmt.setInt(2, user.getId());

            try (ResultSet result = stmt.executeQuery()) {
                return DaoResult.succeed(DaoResult.Action.READ, result.next());
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.LikeDao#likePost(wup.data.Post, wup.data.User)
     */
    @Override
    public DaoResult<Boolean> likePost(Post post, User user) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_LIKE_POST)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setInt(1, post.getId());
            stmt.setInt(2, user.getId());
            stmt.setTimestamp(3, now);
            stmt.setTimestamp(4, now);

            int affectedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.CREATE, affectedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.LikeDao#unlikePost(wup.data.Post, wup.data.User)
     */
    @Override
    public DaoResult<Boolean> unlikePost(Post post, User user) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UNLIKE_POST)) {
            stmt.setInt(1, post.getId());
            stmt.setInt(2, user.getId());

            int deletedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.DELETE, deletedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.DELETE, e);
        }
    }

}
