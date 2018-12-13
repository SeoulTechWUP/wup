package wup.data.access;

import java.util.List;

import wup.data.Comment;
import wup.data.Post;
import wup.data.User;

/**
 * 공개 게시물 댓글 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface CommentDao {
    /**
     * 주어진 <code>id</code>에 해당하는 댓글의 정보를 가져옵니다.
     *
     * @param id 댓글의 일련번호
     */
    public DaoResult<Comment> getComment(int id);
    /**
     * 주어진 <code>post</code>에 작성된 모든 댓글의 리스트를 가져옵니다.
     *
     * @param post 댓글을 조회할 공개 게시물
     */
    public DaoResult<List<Comment>> getComments(Post post);

    /**
     * 주어진 <code>post</code>에 새로운 댓글을 작성합니다.
     *
     * @param post    댓글을 작성할 공개 게시물
     * @param user    댓글을 작성할 사용자
     * @param comment 작성할 댓글
     */
    public DaoResult<Comment> createComment(Post post, User user, Comment comment);

    /**
     * 주어진 <code>id</code>에 해당하는 댓글의 정보를 변경합니다.
     *
     * @param id      정보를 변경할 댓글의 일련번호
     * @param comment 변경할 정보가 들어 있는 {@link Comment} 개체
     */
    public DaoResult<Comment> updateComment(int id, Comment comment);

    /**
     * 주어진 <code>id</code>에 해당하는 댓글을 삭제합니다.
     *
     * @param id 삭제할 댓글의 일련번호
     */
    public DaoResult<Boolean> deleteComment(int id);
}
