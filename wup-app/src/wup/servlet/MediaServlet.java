package wup.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import wup.data.Media;
import wup.data.access.MediaDao;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;


@WebServlet("/media/*")
public class MediaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern MediaURLPattern; //URL 검사 표현식   
	
    static {
        MediaURLPattern = Pattern.compile("^/(?<post>[1-9][0-9]*)$");
    }
	
    //URL 검사 메소드
    private int ValidatePath(String pathString) {
        int post = -1;
        
        Matcher mat = MediaURLPattern.matcher(pathString);
        
        if(mat.matches()) {
            post = Integer.parseInt(mat.group("post"));
        }
        
        return post;
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
	    //post 로딩시에 media 요청
	    //요청시에 받아올 post id 가 url에 붙어있음
	    //성공 시에 media가 저장된 path를 반환
	    
	    request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/json");
	    
        MariaDbDaoFactory daoFactory = new DaoFactory();
        MediaDao CommentDao = (MediaDao) daoFactory.getDao(Media.class);
        
        DaoResult<List<Media>> getMedia;
        List<Media> Media = new ArrayList<Media>();
        
        int postNum = ValidatePath(ServletHelper.trimString(request.getPathInfo()));
        
        if (postNum > 0) {
            
        } else {
            //경로가 정확하지 않음
            request.setAttribute("GetMediaErrorMessage", "Wrong Path: " + request.getPathInfo());
            System.out.println("Wrong Path: " + request.getPathInfo());
            return;
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    //post 작성시에 media 삽입
	    //요청시에 data에 media 타입이 명시되어 있음
	    //성공 시에 media가 저장된 path를 반환
	}

}
