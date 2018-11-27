package wup.data.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import wup.data.Comment;
import wup.data.Post;
import wup.data.User;

/**
 * MariaDB를 통해 댓글 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong
 */
public class MariaDbCommentDao extends MariaDbDao implements CommentDao {

    private static final String TABLE_NAME = "comment";

    private static final String SQL_INSERT = "INSERT INTO `comment` (`created_at`, `modified_at`, `post_id`, `user_id`, `text`) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_GET_BY_POST = "SELECT * FROM `comment` WHERE `post_id` = ? ORDER BY `created_at` DESC";

    public MariaDbCommentDao(JdbcConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.CommentDao#getComment(int)
     */
    @Override
    public DaoResult<Comment> getComment(int id) {
        return querySingleItem(TABLE_NAME, id, (rs) -> {
            Comment comment = null;

            if (rs.next()) {
                comment = getCommentFromResultSet(rs);
            }

            return DaoResult.succeed(DaoResult.Action.READ, comment);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.CommentDao#getComments(wup.data.Post)
     */
    @Override
    public DaoResult<List<Comment>> getComments(Post post) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_POST)) {
            stmt.setInt(1, post.getId());

            try (ResultSet result = stmt.executeQuery()) {
                List<Comment> comments = new ArrayList<>();

                while (result.next()) {
                    comments.add(getCommentFromResultSet(result));
                }

                return DaoResult.succeed(DaoResult.Action.READ, comments);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.CommentDao#createComment(wup.data.Post, wup.data.User,
     * wup.data.Comment)
     */
    @Override
    public DaoResult<Comment> createComment(Post post, User user, Comment comment) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setInt(3, post.getId());
            stmt.setInt(4, user.getId());
            stmt.setString(5, comment.getText());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdCommentId = generatedKeys.getInt(1);

                return getComment(createdCommentId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.CommentDao#updateComment(int, wup.data.Comment)
     */
    @Override
    public DaoResult<Comment> updateComment(int id, Comment comment) {
        DaoResult<Comment> getCommentResult = getComment(id);

        if (!getCommentResult.didSucceed()) {
            return getCommentResult;
        }

        Comment oldComment = getCommentResult.getData();
        List<Entry<String, String>> fieldMap = new ArrayList<>();
        fieldMap.add(new SimpleEntry<String, String>("text", "text"));

        DaoResult<Boolean> updateCommentResult = updateSingleItem(TABLE_NAME, id, oldComment, comment, fieldMap);

        if (!updateCommentResult.didSucceed()) {
            return DaoResult.fail(DaoResult.Action.UPDATE, updateCommentResult.getException());
        }

        return getComment(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.CommentDao#deleteComment(int)
     */
    @Override
    public DaoResult<Boolean> deleteComment(int id) {
        return deleteSingleItem(TABLE_NAME, id);
    }

    private Comment getCommentFromResultSet(ResultSet rs) throws Exception {
        Comment comment = new Comment();

        comment.setId(rs.getInt("id"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        comment.setModifiedAt(rs.getTimestamp("modified_at"));
        comment.setText(rs.getString("text"));

        DaoResult<User> getUserResult = new MariaDbUserDao(connectionProvider).getUser(rs.getInt("user_id"));

        if (!getUserResult.didSucceed()) {
            throw getUserResult.getException();
        }

        comment.setUser(getUserResult.getData());

        return comment;
    }

}
