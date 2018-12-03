package wup.servlet;

import wup.data.access.*;
import wup.data.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/PostListServlet")
public class PostListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	       
	    ServletContext app = this.getServletContext();
	    String contextPath = request.getContextPath();
	    RequestDispatcher dispatcher = app.getRequestDispatcher("/board.jsp");
	    
        MariaDbDaoFactory daoFactory = new DaoFactory();
        PostDao PostDao = (PostDao) daoFactory.getDao(Post.class);

        List<Post> postlist = new ArrayList<Post>();
        
        DaoResult<List<Post>> getPostList = PostDao.getPosts(10);
        
        if(getPostList.didSucceed()) {
            postlist = getPostList.getData();
            request.setAttribute("postlist", postlist);
        }
        else {
            System.out.println(getPostList.getException().getMessage());
        }

        try { dispatcher.forward(request, response); } 
        catch (ServletException e) {  System.out.println(e); }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
		
	}

}
