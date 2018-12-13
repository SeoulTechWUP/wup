package wup.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.Post;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.PostDao;

/**
 * 포스트처리에 관한 서블릿
 *
 * @author WonHyun
 */
@WebServlet("/board/*")
public class PostListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_VIEW = 20; // 한 페이지에 표시되는 게시물 수
    private static final int PageBlockMaxRange = 5; // 한 페이지 블럭에 표시되는 페이지 수
    private static final Pattern BoardURLPattern; // URL 검사 표현식

    static {
        BoardURLPattern = Pattern.compile("^/(?<page>[1-9][0-9]*)$");
    }

    // URL 검사 메소드
    private int ValidatePath(String pathString, int total) {
        int page = 0;

        Matcher mat = BoardURLPattern.matcher(pathString);

        if (mat.matches()) {
            page = Integer.parseInt(mat.group("page"));
            if (page < (total / PAGE_VIEW) + 2) {
                return page;
            } else {
                page *= -1;
            }
        }

        return page;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ServletContext app = this.getServletContext();
        String contextPath = request.getContextPath(); // redirect시에
        RequestDispatcher dispatcher = app.getRequestDispatcher("/board.jsp"); // forwarding 시에 dispatcher

        MariaDbDaoFactory daoFactory = new DaoFactory();
        PostDao PostDao = (PostDao) daoFactory.getDao(Post.class);

        List<Post> postlist = new ArrayList<Post>(); // 현재 페이지의 게시물 객체 리스트
        int total = 0; // 총 게시물 개수

        int pageBlockRange = 0; // 현재 페이지의 표시 범위
        int maxPage = 0; // 최대 페이지 수
        int blockStart = 0; // 현재 블럭이 시작되는 페이지 수
        int blockNum = 0; // 현재 블럭 넘버

        DaoResult<Integer> getTotalCount = PostDao.getPostCount();

        if (getTotalCount.didSucceed()) {
            total = getTotalCount.getData();
        } else {
            // error 페이지로 forwarding 해야함
            request.setAttribute("BoardErrorMessage", getTotalCount.getException().getMessage());
            System.out.println(getTotalCount.getException().getMessage());
            return;
        }

        maxPage = (total / PAGE_VIEW) + 1;
        // 현재 페이지 수 (0 이하 값일 경우 잘못된 경로
        int pageNum = ValidatePath(ServletHelper.trimString(request.getPathInfo()), total);

        if (pageNum <= 0) {
            if (pageNum < 0) { // 요청 페이지가 최대 페이지를 넘는 경우
                response.sendRedirect(contextPath + "/board/" + maxPage);
                return;
            } else { // 요청 페이지가 숫자가 아니거나 0일 경우
                response.sendRedirect(contextPath + "/board/1");
                return;
            }
        }

        blockNum = (pageNum - 1) / PageBlockMaxRange;
        blockStart = blockNum * PageBlockMaxRange + 1;

        if (blockStart + PageBlockMaxRange - 1 <= maxPage) {
            pageBlockRange = PageBlockMaxRange;
        } else {
            pageBlockRange = maxPage % PageBlockMaxRange;
        }

        request.setAttribute("CurrentPage", pageNum);
        request.setAttribute("PageBlockMaxRange", PageBlockMaxRange);
        request.setAttribute("PageBlockStart", blockStart);
        request.setAttribute("PageBlockRange", pageBlockRange);

        DaoResult<List<Post>> getPostList = PostDao.getPosts((pageNum - 1) * PAGE_VIEW, PAGE_VIEW);

        if (getPostList.didSucceed()) {
            postlist = getPostList.getData();
            request.setAttribute("postlist", postlist);
        } else {
            // error 페이지로 forwarding 해야함
            request.setAttribute("BoardErrorMessage", getPostList.getException().getMessage());
            System.out.println(getPostList.getException().getMessage());
        }

        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            System.out.println(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

}
