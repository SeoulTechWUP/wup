package wup.servlet;

import java.io.File;
import java.io.IOException;
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
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import wup.data.Media;
import wup.data.Media.Type;
import wup.data.Post;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.MediaDao;

@WebServlet("/media/*")
public class MediaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern MediaURLPattern; // URL 검사 표현식
    private static final Pattern MediaPathURLPattern; // URL 검사 표현식
    private static final int FileMaxSize = 1024 * 1024 * 50;

    static {
        MediaURLPattern = Pattern.compile("^/(?<post>[1-9][0-9]*)$");
        MediaPathURLPattern = Pattern.compile("^/(?<post>[1-9][0-9]*)/(?<type>image|video)$");
    }

    // URL 검사 메소드
    private int ValidatePath(String pathString) {
        int post = -1;

        Matcher mat = MediaURLPattern.matcher(pathString);

        if (mat.matches()) {
            post = Integer.parseInt(mat.group("post"));
        }

        return post;
    }

    // json 변환 메소드
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        MariaDbDaoFactory daoFactory = new DaoFactory();
        MediaDao MediaDao = (MediaDao) daoFactory.getDao(Media.class);

        DaoResult<List<Media>> getMedia;

        Post post = new Post();

        int postNum = ValidatePath(ServletHelper.trimString(request.getPathInfo()));

        if (postNum < 0) {
            response.getWriter().write(makeJSON(null, "pathfail"));
            return;
        }

        post.setId(postNum);
        getMedia = MediaDao.getMedia(post);

        if (getMedia.didSucceed()) {
            response.getWriter().write(makeJSON(getMedia.getData(), "success"));
        } else {
            response.getWriter().write(makeJSON(getMedia.getException().getMessage(), "dbfail"));
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");

        MariaDbDaoFactory daoFactory = new DaoFactory();
        MediaDao MediaDao = (MediaDao) daoFactory.getDao(Media.class);

        DaoResult<Media> createMedia;

        Media tempMedia = new Media();
        Post tempPost = new Post();

        File[] fileList;

        MultipartRequest mul;
        String realPath = this.getServletContext().getRealPath("/res/");
        String path = "";

        String type = null;
        int post = -1;

        Matcher mat = MediaPathURLPattern.matcher(ServletHelper.trimString(request.getPathInfo()));

        if (mat.matches()) {
            type = mat.group("type");
            post = Integer.parseInt(mat.group("post"));
            if (mat.group("type").equals("image")) {
                tempMedia.setType(Type.IMAGE);
            } else {
                tempMedia.setType(Type.VIDEO);
            }
            tempPost.setId(Integer.parseInt(mat.group("post")));
        } else {
            response.getWriter().write(makeJSON(null, "pathfail"));
        }

        path = "/res/" + post + "/" + type + "/";
        File dir = new File(realPath + post + "/" + type);

        if (!dir.isDirectory()) {
            dir.mkdirs();
        }

        try {
            mul = new MultipartRequest(request, dir.getPath(), FileMaxSize, "UTF-8", new DefaultFileRenamePolicy());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write(makeJSON(e.toString(), "uploadfail"));
            return;
        }

        fileList = dir.listFiles();

        for (File temp : fileList) {
            String tempPath = path + temp.getName();
            tempMedia.setPath(tempPath);
            createMedia = MediaDao.createMedia(tempPost, tempMedia);

            if (!createMedia.didSucceed()) {
                response.getWriter().write(makeJSON(createMedia.getException().getMessage(), "dbfail"));
                return;
            }
        }

        response.getWriter().write(makeJSON(null, "success"));
    }
}
