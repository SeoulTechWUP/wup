package wup.data.access;

import java.util.Date;
import java.util.List;

import wup.data.ItemOwner;
import wup.data.Post;

/**
 * 공개 게시물 DAO에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong, Won Hyun
 */
public interface PostDao {
    /**
     * 포스트의 총 개수를 반환합니다.
     */
    public DaoResult<Integer> getPostCount();
    
    /**
     * 주어진 <code>id</code>에 해당하는 공개 게시물의 정보를 가져옵니다.
     *
     * @param id 공개 게시물의 일련번호
     */
    public DaoResult<Post> getPost(int id);

    /**
     * 최근에 등록된 모든 공개 게시물 중 최대 <code>count</code>개 항목의 리스트를 가져옵니다.
     *
     * @param count 가져올 공개 게시물의 최대 개수
     */
    public DaoResult<List<Post>> getPosts(int count);

    /**
     * 특정 범위의 게시글 리스트를 반환합니다. 
     * 
     * @param Start 검색할 게시글 범위 시작 값
     * @param ViewCount 표시할 포스트의 수량
     */
    public DaoResult<List<Post>> getPosts(int Start, int ViewCount);
    
    /**
     * 모든 사용자/그룹이 등록한 공개 게시물 중 지정된 기간 내에 작성된 항목의 리스트를 가져옵니다.
     *
     * @param from 시작 기간
     * @param to   종료 기간
     */
    public DaoResult<List<Post>> getPosts(Date from, Date to);

    /**
     * 주어진 <code>owner</code>가 작성한 모든 공개 게시물의 리스트를 가져옵니다.
     *
     * @param owner 공개 게시물을 조회할 작성자
     */
    public DaoResult<List<Post>> getPosts(ItemOwner owner);

    /**
     * 주어진 <code>owner</code>가 최근에 작성한 공개 게시물 중 최대 <code>count</code>개 항목의 리스트를
     * 가져옵니다.
     *
     * @param owner 공개 게시물을 조회할 작성자
     * @param count 가져올 공개 게시물의 최대 개수
     */
    public DaoResult<List<Post>> getPosts(ItemOwner owner, int count);

    /**
     * 주어진 <code>owner</code>가 지정된 기간 내에 작성한 공개 게시물의 리스트를 가져옵니다.
     *
     * @param owner 공개 게시물을 조회할 작성자
     * @param from  시작 기간
     * @param to    종료 기간
     */
    public DaoResult<List<Post>> getPosts(ItemOwner owner, Date from, Date to);

    /**
     * 새로운 공개 게시물을 작성합니다.
     *
     * @param owner 공개 게시물의 작성자
     * @param post  작성할 공개 게시물의 정보
     */
    public DaoResult<Post> createPost(ItemOwner owner, Post post);

    /**
     * 주어진 <code>id</code>에 해당하는 공개 게시물의 정보를 변경합니다.
     *
     * @param id   변경할 공개 게시물의 일련번호
     * @param post 변경할 정보가 들어 있는 {@link Post} 개체
     */
    public DaoResult<Post> updatePost(int id, Post post);

    /**
     * 주어진 <code>id</code>에 해당하는 공개 게시물을 삭제합니다.
     *
     * @param id 삭제할 공개 게시물의 일련번호
     */
    public DaoResult<Boolean> deletePost(int id);
}
