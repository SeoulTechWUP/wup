package wup.data.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import wup.data.Media;
import wup.data.Post;

/**
 * MariaDB를 통해 공개 게시물 첨부 미디어 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong
 */
public class MariaDbMediaDao extends MariaDbDao implements MediaDao {

    private static final String TABLE_NAME = "media";

    private static final String SQL_GET_BY_POST = "SELECT * FROM `media` WHERE `post_id` = ?";
    private static final String SQL_INSERT = "INSERT INTO `media` (`created_at`, `modified_at`, `type`, `path`, `post_id`) VALUES (?, ?, ?, ?, ?)";

    public MariaDbMediaDao(JdbcConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.MediaDao#getMedia(int)
     */
    @Override
    public DaoResult<Media> getMedia(int id) {
        return querySingleItem(TABLE_NAME, id, (rs) -> {
            Media media = null;

            if (rs.next()) {
                media = getMediaFromResultSet(rs);
            }

            return DaoResult.succeed(DaoResult.Action.READ, media);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.MediaDao#getMedia(wup.data.Post)
     */
    @Override
    public DaoResult<List<Media>> getMedia(Post post) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_POST)) {
            stmt.setInt(1, post.getId());

            try (ResultSet result = stmt.executeQuery()) {
                List<Media> mediaList = new ArrayList<>();

                while (result.next()) {
                    Media media = getMediaFromResultSet(result);

                    mediaList.add(media);
                }

                return DaoResult.succeed(DaoResult.Action.READ, mediaList);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.MediaDao#createMedia(wup.data.Post, wup.data.Media)
     */
    @Override
    public DaoResult<Media> createMedia(Post post, Media media) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setString(3, media.getType().name());
            stmt.setString(4, media.getPath());
            stmt.setInt(5, post.getId());

            stmt.executeQuery();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdMediaId = generatedKeys.getInt(1);

                return getMedia(createdMediaId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.MediaDao#deleteMedia(int)
     */
    @Override
    public DaoResult<Boolean> deleteMedia(int id) {
        return deleteSingleItem(TABLE_NAME, id);
    }

    private Media getMediaFromResultSet(ResultSet rs) throws Exception {
        Media media = new Media();

        media.setId(rs.getInt("id"));
        media.setCreatedAt(rs.getTimestamp("created_at"));
        media.setModifiedAt(rs.getTimestamp("modified_at"));
        media.setType(Media.Type.valueOf(rs.getString("type").toUpperCase()));
        media.setPath(rs.getString("path"));

        return media;
    }

}
