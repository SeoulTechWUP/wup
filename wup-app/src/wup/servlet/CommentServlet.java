package wup.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import wup.data.Comment;
import wup.data.Post;
import wup.data.access.CommentDao;
import wup.data.access.PostDao;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;

/**
 * Servlet implementation class CommentServlet
 */
@WebServlet("/comment/*")
public class CommentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern CommentURLPattern; //URL 검사 표현식
	
   static {
       CommentURLPattern = Pattern.compile("^/(?<post>[1-9][0-9]*)$");
    }

   //URL 검사 메소드
   private int ValidatePath(String pathString) {
       int post = -1;
       
       Matcher mat = CommentURLPattern.matcher(pathString);
       
       if(mat.matches()) {
           post = Integer.parseInt(mat.group("post"));
       }
       
       return post;
   }
   
   //포스트 객체 반환 메소드
   private Object selectPost(int id) {
       MariaDbDaoFactory daoFactory = new DaoFactory();
       PostDao PostDao = (PostDao) daoFactory.getDao(Post.class);
       DaoResult<Post> getPost = PostDao.getPost(id);
       
       if(getPost.didSucceed()) {
           return getPost.getData();
       } else {
           return getPost.getException().getMessage();
       }
   }
   
   //json 변환 메소드
   private String makeJSON(Object obj, String result) {
       Gson gson = new Gson();
       JsonObject jobj = new JsonObject();
       
       String objson = gson.toJson(obj);
       String json;
       
       jobj.addProperty("data", objson);
       jobj.addProperty("result", result);
       
       json = gson.toJson(jobj);
       
       return json;
   }
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    
        MariaDbDaoFactory daoFactory = new DaoFactory();
        CommentDao CommentDao = (CommentDao) daoFactory.getDao(Comment.class);
        
        DaoResult<List<Comment>> getComments;
        List<Comment> comments = new ArrayList<Comment>();
        
        Object post;
        
        int postNum = ValidatePath(ServletHelper.trimString(request.getPathInfo()));
        
        if (postNum > 0) {
            post = selectPost(postNum);
            
            if(post instanceof Post) {
                getComments = CommentDao.getComments((Post)post);
            } else {
                //요청 에러
                request.setAttribute("GetCommentErrorMessage", (String)post);
                System.out.println("db error: " + (String)post);
                return;
            }
        } else {
            //경로가 정확하지 않음
            request.setAttribute("GetCommentErrorMessage", "Wrong Path: " + request.getPathInfo());
            System.out.println("Wrong Path: " + request.getPathInfo());
            return;
        }


        if(getComments.didSucceed()) {
            comments = getComments.getData();
            request.setAttribute("Comments", comments);
        }
        else {
            //요청 에러
            request.setAttribute("GetCommentErrorMessage", getComments.getException().getMessage());
            System.out.println(getComments.getException().getMessage());
            return;
        }   
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
        MariaDbDaoFactory daoFactory = new DaoFactory();
        CommentDao CommentDao = (CommentDao) daoFactory.getDao(Comment.class);
        
	}

}
