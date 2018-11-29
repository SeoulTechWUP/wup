package wup.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.api.Error;
import wup.api.GsonHolder;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.UserDao;

/**
 * Servlet implementation class UserAuthServlet
 */
@WebServlet("/api/user/auth")
public class UserAuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=utf-8");

        MariaDbDaoFactory daoFactory = new DaoFactory();
        UserDao userDao = (UserDao) daoFactory.getDao(User.class);

        String email = request.getParameter("email");
        String auth = request.getParameter("auth");

        if (email == null) {
            email = "";
        }

        if (auth == null) {
            auth = "";
        }

        DaoResult<User> authResult = userDao.authenticate(email, auth);
        PrintWriter out = response.getWriter();

        if (authResult.didSucceed()) {
            User authenticatedUser = authResult.getData();

            if (authenticatedUser == null) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.println(new Error(Error.E_LOGINFAIL).toJson());
            } else {
                request.getSession().setAttribute("authenticatedUser", authenticatedUser);
                response.setStatus(HttpServletResponse.SC_OK);
                out.println(GsonHolder.getGson().toJson(authenticatedUser));
            }
        } else {
            Exception e = authResult.getException();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            if (e instanceof SQLException) {
                out.println(new Error(Error.E_DBERROR).toJson());
            } else {
                out.println(new Error(-1));
            }
        }
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("암호 변경");
    }

}
