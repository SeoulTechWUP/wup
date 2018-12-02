package wup.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import wup.api.Error;
import wup.api.GsonHolder;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.UserDao;
import wup.servlet.ServletHelper;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        User authenticatedUser = ServletHelper.checkAuth(request, response);

        if (authenticatedUser == null) {
            return;
        }

        PrintWriter out = response.getWriter();
        String pathInfo = request.getPathInfo();
        Gson gson = GsonHolder.getGson();
        MariaDbDaoFactory daoFactory = new DaoFactory();
        UserDao userDao = (UserDao) daoFactory.getDao(User.class);

        if (pathInfo == null || pathInfo.isEmpty()) {
            DaoResult<User> getUserResult = userDao.getUser(authenticatedUser.getId());

            if (getUserResult.didSucceed()) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.println(gson.toJson(getUserResult.getData()));
            } else {
                ServletHelper.onDaoError(response, getUserResult);
            }

            return;
        }

        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);

            try {
                int userId = Integer.parseInt(pathInfo);
                DaoResult<User> getUserResult = userDao.getUser(userId);

                if (getUserResult.didSucceed()) {
                    User retrievedUser = getUserResult.getData();

                    if (retrievedUser == null) {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.println(new Error(Error.E_NOENT).toJson());
                    } else {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.println(gson.toJson(getUserResult.getData()));
                    }
                } else {
                    ServletHelper.onDaoError(response, getUserResult);
                }

                return;
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.println(new Error(Error.E_NOENT).toJson());

                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        out.println(new Error(Error.E_NOENT).toJson());

        return;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("새로운 사용자 생성");
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("현재 로그인되어 있는 사용자의 정보 수정");
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("현재 로그인되어 있는 사용자의 계정 영구 삭제");
    }

}
