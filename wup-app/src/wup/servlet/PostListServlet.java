package wup.servlet;

import wup.data.access.*;
import wup.data.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/board/*")
public class PostListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int PAGE_VIEW = 20;
	private static final Pattern BoardURLPattern;
	
	static {
	    BoardURLPattern = Pattern.compile("^/(?<page>[1-9][0-9]*)$");
	}
	
	private int VaildatePath(String pathString, int total) {
	    int page = -1;

        if (pathString == null) {
            return page;
        }
	    
	    Matcher mat = BoardURLPattern.matcher(pathString);
	    
	    if(mat.matches()) {
	        page = Integer.parseInt(mat.group("page"));
            if(page < (total/PAGE_VIEW) + 2) {
                return page;
            }
            else {
                page = -1;
            }
	    }
	    
	    return page;
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	       
	    ServletContext app = this.getServletContext();
	    String contextPath = request.getContextPath(); //redirect시에
	    RequestDispatcher dispatcher = app.getRequestDispatcher("/board.jsp");
	    
        MariaDbDaoFactory daoFactory = new DaoFactory();
        PostDao PostDao = (PostDao) daoFactory.getDao(Post.class);

        List<Post> postlist = new ArrayList<Post>();
        int total = 0;
        
        DaoResult<Integer> getTotalCount = PostDao.getPostCount();
        
        if(getTotalCount.didSucceed()) {
            total = getTotalCount.getData();
            
            request.setAttribute("TotalPage", total);
        }
        else {
            //error 페이지로 forwarding 해야함
            request.setAttribute("BoardErrorMessage", getTotalCount.getException().getMessage());
            System.out.println(getTotalCount.getException().getMessage());
        }

        int pageNum = VaildatePath(request.getPathInfo(), total);
        
        if (pageNum == -1) {
            response.sendRedirect(contextPath + "/board/1");
            return;
        }
        
        DaoResult<List<Post>> getPostList = PostDao.getPosts((pageNum - 1)*PAGE_VIEW, PAGE_VIEW);


        if(getPostList.didSucceed()) {
            postlist = getPostList.getData();
            request.setAttribute("postlist", postlist);
        }
        else {
            //error 페이지로 forwarding 해야함
            request.setAttribute("BoardErrorMessage", getPostList.getException().getMessage());
            System.out.println(getPostList.getException().getMessage());
        }

        try { dispatcher.forward(request, response); } 
        catch (ServletException e) {  System.out.println(e); }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
		
	}

}
