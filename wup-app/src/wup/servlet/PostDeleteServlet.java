package wup.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import wup.data.Post;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.PostDao;

@WebServlet("/postdelete/*")
public class PostDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    response.setContentType("application/json");
	    
	    MariaDbDaoFactory daoFactory = new DaoFactory();
        PostDao PostDao = (PostDao) daoFactory.getDao(Post.class);
        
        DaoResult <Boolean> deletePost = PostDao.deletePost(Integer.parseInt(request.getParameter("id")));
        
        if(deletePost.didSucceed()) {
            response.getWriter().write(makeJSON(null, "success"));
        } else {
            response.getWriter().write(makeJSON(deletePost.getException().getMessage(), "fail"));
        }
	}

}
