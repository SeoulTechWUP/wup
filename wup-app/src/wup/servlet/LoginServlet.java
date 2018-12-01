package wup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import wup.api.Error;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.UserDao;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/processLogin")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MariaDbDaoFactory daoFactory = new DaoFactory();
        UserDao userDao = (UserDao) daoFactory.getDao(User.class);

        String email = request.getParameter("email");
        String auth = request.getParameter("password");

        if (email == null) {
            email = "";
        }

        if (auth == null) {
            auth = "";
        }

        DaoResult<User> authResult = userDao.authenticate(email, auth);
        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();

        if (authResult.didSucceed()) {
            User authenticatedUser = authResult.getData();

            if (authenticatedUser == null) {
                session.setAttribute("loginErrorMessage", Error.messages.get(Error.E_LOGINFAIL));
                response.sendRedirect(contextPath + "/login.jsp");
            } else {
                session.setAttribute("authenticatedUser", authenticatedUser);
                response.sendRedirect(contextPath);
            }
        } else {
            session.setAttribute("loginErrorMessage", Error.messages.get(Error.E_DBERROR));
            response.sendRedirect(contextPath + "/login.jsp");
        }
    }

}
