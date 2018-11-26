package wup.data.access;

import wup.data.Post;
import wup.data.User;

/**
 * 공개 게시물의 "좋아요" 표시 동작 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface LikeDao {
    /**
     * 주어진 <code>post</code>가 "좋아요" 표시 된 횟수를 가져옵니다.
     *
     * @param post 횟수를 조회할 공개 게시물
     */
    public DaoResult<Integer> getLikeCount(Post post);

    /**
     * 주어진 <code>user</code>가 주어진 <code>post</code>에 "좋아요" 표시를 했는지 여부를 가져옵니다.
     *
     * @param post 공개 게시물
     * @param user 정보를 조회할 사용자
     */
    public DaoResult<Boolean> getLike(Post post, User user);

    /**
     * 주어진 <code>post</code>에 "좋아요" 표시를 합니다.
     *
     * @param post "좋아요" 표시할 공개 게시물
     * @param user "좋아요" 표시를 할 사용자
     */
    public DaoResult<Boolean> likePost(Post post, User user);

    /**
     * 주어진 <code>post</code>의 "좋아요" 표시를 해제합니다.
     *
     * @param post "좋아요" 표시를 해제할 공개 게시물
     * @param user "좋아요" 표시를 해제할 사용자
     */
    public DaoResult<Boolean> unlikePost(Post post, User user);
}
