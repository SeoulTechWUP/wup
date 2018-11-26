package wup.data.access;

import java.util.List;

import wup.data.Media;
import wup.data.Post;

/**
 * 공개 게시물의 첨부 미디어 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface MediaDao {
    /**
     * 주어진 <code>id</code>에 해당하는 첨부 미디어를 가져옵니다.
     *
     * @param id 첨부 미디어의 일련번호
     */
    public DaoResult<Media> getMedia(int id);

    /**
     * 주어진 <code>post</code>에 첨부된 모든 미디어의 리스트를 가져옵니다.
     *
     * @param post 첨부 미디어를 조회할 공개 게시물
     */
    public DaoResult<List<Media>> getMedia(Post post);

    /**
     * 주어진 <code>post</code>에 새로운 미디어를 첨부합니다.
     *
     * @param post  미디어를 첨부할 공개 게시물
     * @param media 첨부할 미디어
     */
    public DaoResult<Media> createMedia(Post post, Media media);

    /**
     * 주어진 <code>id</code>에 해당하는 첨부 미디어를 삭제합니다.
     *
     * @param id 삭제할 미디어의 일련번호
     */
    public DaoResult<Boolean> deleteMedia(int id);
}
