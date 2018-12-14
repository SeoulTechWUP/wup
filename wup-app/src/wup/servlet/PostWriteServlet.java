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
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import wup.data.Comment;
import wup.data.Group;
import wup.data.Media;
import wup.data.Post;
import wup.data.Schedule;
import wup.data.User;
import wup.data.access.CommentDao;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.MediaDao;
import wup.data.access.PostDao;
import wup.data.access.ScheduleDao;



@WebServlet("/postwrite/*")
public class PostWriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static final Pattern ScheduleURLPattern; // URL 검사 표현식

    static {
        ScheduleURLPattern = Pattern.compile("^/(?<schedule>[1-9][0-9]*)$");
    }
    
    // URL 검사 메소드
    private int ValidatePath(String pathString) {
        int schedule = -1;

        Matcher mat = ScheduleURLPattern.matcher(pathString);

        if (mat.matches()) {
            schedule = Integer.parseInt(mat.group("schedule"));
        }

        return schedule;
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
        
        response.setContentType("application/json");
        
        ServletContext app = this.getServletContext();
        RequestDispatcher dispatcher = app.getRequestDispatcher("/PostWriteView.jsp");
        
        MariaDbDaoFactory daoFactory = new DaoFactory();
        ScheduleDao ScheduleDao = (ScheduleDao) daoFactory.getDao(Schedule.class);

        DaoResult<Schedule> getSchedule;
        
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("authenticatedUser");
        
        if (user == null || user.getEmail() == null) {
            response.getWriter().write(makeJSON(null, "userfail"));
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        
        int scheduleNum = ValidatePath(ServletHelper.trimString(request.getPathInfo()));
        
        if(scheduleNum < 0) {
            // 경로가 정확하지 않음
            request.setAttribute("GetScheduleErrorMessage", "Wrong Path: " + request.getPathInfo());
            System.out.println("Wrong Path: " + request.getPathInfo());
            return;
        }
        
        getSchedule = ScheduleDao.getSchedule(scheduleNum);
        
        if(getSchedule.didSucceed()) {
            request.setAttribute("schedule", getSchedule.getData());
        } else {
            request.setAttribute("GetScheduleErrorMessage", getSchedule.getException().getMessage());
            System.out.println(getSchedule.getException().getMessage());
            return;
        }
        
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            System.out.println(e);
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    response.setContentType("application/json");
	    
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("authenticatedUser");
	    
        if (user == null || user.getEmail() == null) {
            response.getWriter().write(makeJSON(null, "userfail"));
            return;
        }
        
        MariaDbDaoFactory daoFactory = new DaoFactory();
        PostDao PostDao = (PostDao) daoFactory.getDao(Post.class);
        
        Schedule schedule = new Schedule();
        schedule.setId(Integer.parseInt(request.getParameter("scheduleid")));
        
        Post post = new Post();
        post.setTitle(request.getParameter("title"));
        post.setText(request.getParameter("content"));
        post.setSchedule(schedule);
        
        DaoResult<Post> createPost;
        
        System.out.println(request.getParameter("ownertype"));
        System.out.println(request.getParameter("ownerid"));
        
        if(request.getParameter("ownertype") == "USER") {
            createPost = PostDao.createPost(user, post);
        } else {
            Group group = new Group();
            group.setId(Integer.parseInt(request.getParameter("ownerid")));
            createPost = PostDao.createPost(group, post);
        }
         
        if(createPost.didSucceed()) {
            response.getWriter().write(makeJSON(null, "success"));
            return;
        } else {
            response.getWriter().write(makeJSON(createPost.getException().getMessage(), "dbfail"));
            System.out.println(createPost.getException().getMessage());
            return;
        }
	}

}
