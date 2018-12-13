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

import wup.data.Group;
import wup.data.ItemOwner;
import wup.data.Post;
import wup.data.Schedule;
import wup.data.User;

/**
 * MariaDB를 통해 공개 게시물 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong, Won Hyun
 */
public class MariaDbPostDao extends MariaDbDao implements PostDao {

    private static final String TABLE_NAME = "post";

    private static final String SQL_GET_RECENT = "SELECT * FROM `post` ORDER BY `created_at` DESC LIMIT ?";
    private static final String SQL_GET_PAGING = "SELECT * FROM `post` ORDER BY `created_at` DESC LIMIT ?, ?";
    private static final String SQL_GET_COUNT = "SELECT COUNT(*) FROM `post`";
    private static final String SQL_GET_RANGE = "SELECT * FROM `post` WHERE `created_at` BETWEEN ? AND ? ORDER BY `created_at` DESC";
    private static final String SQL_GET_BY_USER = "SELECT * FROM `post` WHERE `type` = 'user' AND `user_id` = ?";
    private static final String SQL_GET_BY_GROUP = "SELECT * FROM `post` WHERE `type` = 'group` AND `group_id` = ?";
    private static final String STUB_ALL = " ORDER BY `created_at` DESC";
    private static final String STUB_LIMIT = " ORDER BY `created_at` DESC LIMIT ?";
    private static final String STUB_RANGE = " AND `created_at` BETWEEN ? AND ? ORDER BY `created_at` DESC";
    private static final String SQL_INSERT_FORMAT = "INSERT INTO `post` (`created_at`, `modified_at`, `type`, `%s_id`, `schedule_id`, `title`, `text) VALUES (?, ?, ?, ?, ?, ?, ?)";

    public MariaDbPostDao(JdbcConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    @Override
    public DaoResult<Integer> getPostCount() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_COUNT)) {

            try (ResultSet result = stmt.executeQuery()) {
                result.next();
                int count = result.getInt(1);

                return DaoResult.succeed(DaoResult.Action.READ, count);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#getPost(int)
     */
    @Override
    public DaoResult<Post> getPost(int id) {
        return querySingleItem(TABLE_NAME, id, (rs) -> {
            Post post = null;

            if (rs.next()) {
                post = getPostFromResultSet(rs, true);
            }

            return DaoResult.succeed(DaoResult.Action.READ, post);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#getPosts(int)
     */
    @Override
    public DaoResult<List<Post>> getPosts(int count) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_RECENT)) {
            stmt.setInt(1, count);

            try (ResultSet result = stmt.executeQuery()) {
                List<Post> posts = new ArrayList<Post>(count);

                while (result.next()) {
                    posts.add(getPostFromResultSet(result, true));
                }

                return DaoResult.succeed(DaoResult.Action.READ, posts);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    @Override
    public DaoResult<List<Post>> getPosts(int start, int viewCount) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PAGING)) {
            stmt.setInt(1, start);
            stmt.setInt(2, viewCount);

            try (ResultSet result = stmt.executeQuery()) {
                List<Post> posts = new ArrayList<Post>();

                while (result.next()) {
                    posts.add(getPostFromResultSet(result, true));
                }

                return DaoResult.succeed(DaoResult.Action.READ, posts);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#getPosts(java.util.Date, java.util.Date)
     */
    @Override
    public DaoResult<List<Post>> getPosts(Date from, Date to) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_RANGE)) {
            stmt.setTimestamp(1, new Timestamp(from.getTime()));
            stmt.setTimestamp(2, new Timestamp(to.getTime()));

            try (ResultSet result = stmt.executeQuery()) {
                List<Post> posts = new ArrayList<Post>();

                while (result.next()) {
                    posts.add(getPostFromResultSet(result, true));
                }

                return DaoResult.succeed(DaoResult.Action.READ, posts);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#getPosts(wup.data.ItemOwner)
     */
    @Override
    public DaoResult<List<Post>> getPosts(ItemOwner owner) {
        String sql = (owner instanceof User ? SQL_GET_BY_USER : SQL_GET_BY_GROUP) + STUB_ALL;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, owner.getId());

            try (ResultSet result = stmt.executeQuery()) {
                List<Post> posts = new ArrayList<Post>();

                while (result.next()) {
                    Post post = getPostFromResultSet(result, false);

                    post.setOwner(owner);
                    posts.add(post);
                }

                return DaoResult.succeed(DaoResult.Action.READ, posts);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#getPosts(wup.data.ItemOwner, int)
     */
    @Override
    public DaoResult<List<Post>> getPosts(ItemOwner owner, int count) {
        String sql = (owner instanceof User ? SQL_GET_BY_USER : SQL_GET_BY_GROUP) + STUB_LIMIT;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, owner.getId());
            stmt.setInt(2, count);

            try (ResultSet result = stmt.executeQuery()) {
                List<Post> posts = new ArrayList<Post>();

                while (result.next()) {
                    Post post = getPostFromResultSet(result, false);

                    post.setOwner(owner);
                    posts.add(post);
                }

                return DaoResult.succeed(DaoResult.Action.READ, posts);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#getPosts(wup.data.ItemOwner, java.util.Date,
     * java.util.Date)
     */
    @Override
    public DaoResult<List<Post>> getPosts(ItemOwner owner, Date from, Date to) {
        String sql = (owner instanceof User ? SQL_GET_BY_USER : SQL_GET_BY_GROUP) + STUB_RANGE;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, owner.getId());
            stmt.setTimestamp(2, new Timestamp(from.getTime()));
            stmt.setTimestamp(3, new Timestamp(to.getTime()));

            try (ResultSet result = stmt.executeQuery()) {
                List<Post> posts = new ArrayList<Post>();

                while (result.next()) {
                    Post post = getPostFromResultSet(result, false);

                    post.setOwner(owner);
                    posts.add(post);
                }

                return DaoResult.succeed(DaoResult.Action.READ, posts);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#createPost(wup.data.ItemOwner, wup.data.Post)
     */
    @Override
    public DaoResult<Post> createPost(ItemOwner owner, Post post) {
        String type = owner instanceof User ? "user" : "group";
        String sql = String.format(SQL_INSERT_FORMAT, type);

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setString(3, type);
            stmt.setInt(4, owner.getId());
            stmt.setInt(5, post.getSchedule().getId());
            stmt.setString(6, post.getTitle());
            stmt.setString(7, post.getText());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdPostId = generatedKeys.getInt(1);

                return getPost(createdPostId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#updatePost(int, wup.data.Post)
     */
    @Override
    public DaoResult<Post> updatePost(int id, Post post) {
        DaoResult<Post> getPostResult = getPost(id);

        if (!getPostResult.didSucceed()) {
            return getPostResult;
        }

        Post oldPost = getPostResult.getData();
        List<Entry<String, String>> fieldMap = new ArrayList<>();

        fieldMap.add(new SimpleEntry<String, String>("title", "title"));
        fieldMap.add(new SimpleEntry<String, String>("text", "text"));

        DaoResult<Boolean> updatePostResult = updateSingleItem(TABLE_NAME, id, oldPost, post, fieldMap);

        if (!updatePostResult.didSucceed()) {
            return DaoResult.fail(DaoResult.Action.UPDATE, updatePostResult.getException());
        }

        return getPost(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PostDao#deletePost(int)
     */
    @Override
    public DaoResult<Boolean> deletePost(int id) {
        return deleteSingleItem(TABLE_NAME, id);
    }

    private Post getPostFromResultSet(ResultSet rs, boolean includeOwner) throws Exception {
        Post post = new Post();
        ItemOwner.Type ownerType = ItemOwner.Type.valueOf(rs.getString("type").toUpperCase());

        post.setId(rs.getInt("id"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setModifiedAt(rs.getTimestamp("modified_at"));
        post.setType(ownerType);
        post.setTitle(rs.getString("title"));
        post.setText(rs.getString("text"));

        DaoResult<Schedule> getScheduleResult = new MariaDbScheduleDao(connectionProvider)
                .getSchedule(rs.getInt("schedule_id"));

        if (!getScheduleResult.didSucceed()) {
            throw getScheduleResult.getException();
        }

        post.setSchedule(getScheduleResult.getData());

        if (includeOwner) {
            if (ownerType == ItemOwner.Type.USER) {
                DaoResult<User> getUserResult = new MariaDbUserDao(connectionProvider).getUser(rs.getInt("user_id"));

                if (getUserResult.didSucceed()) {
                    post.setOwner(getUserResult.getData());
                } else {
                    throw getUserResult.getException();
                }
            } else {
                DaoResult<Group> getGroupResult = new MariaDbGroupDao(connectionProvider)
                        .getGroup(rs.getInt("group_id"));

                if (getGroupResult.didSucceed()) {
                    post.setOwner(getGroupResult.getData());
                } else {
                    throw getGroupResult.getException();
                }
            }
        }

        return post;
    }

}
