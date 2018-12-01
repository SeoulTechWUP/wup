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
 * Servlet implementation class JoinServlet
 */
@WebServlet("/processJoin")
public class JoinServlet extends HttpServlet {
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

        String email = ServletHelper.trimString(request.getParameter("email"));
        String fullName = ServletHelper.trimString(request.getParameter("fullName"));
        String nickname = ServletHelper.trimString(request.getParameter("nickname"));
        String password = ServletHelper.trimString(request.getParameter("password"));
        String passwordConfirm = ServletHelper.trimString(request.getParameter("passwordConfirm"));

        HttpSession session = request.getSession();
        String contextPath = request.getContextPath();

        if (email.isEmpty() || fullName.isEmpty() || nickname.isEmpty() || password.isEmpty()
                || passwordConfirm.isEmpty()) {
            showError(request, response, "모든 정보를 입력해 주세요.");

            return;
        }

        if (!password.equals(passwordConfirm)) {
            showError(request, response, "암호가 일치하지 않습니다.");

            return;
        }

        DaoResult<User> getUserResult = userDao.getUser(email);

        if (getUserResult.didSucceed()) {
            if (getUserResult.getData() != null) {
                showError(request, response, Error.messages.get(Error.E_EMAILDUP));

                return;
            }
        } else {
            showError(request, response, Error.messages.get(Error.E_DBERROR));

            return;
        }

        User newUser = new User();

        newUser.setEmail(email);
        newUser.setFullName(fullName);
        newUser.setNickname(nickname);

        DaoResult<User> createUserResult = userDao.createUser(newUser, password);

        if (createUserResult.didSucceed()) {
            session.setAttribute("authenticatedUser", createUserResult.getData());
            response.sendRedirect(contextPath);
        } else {
            showError(request, response, Error.messages.get(Error.E_DBERROR));
        }
    }

    private void showError(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        request.getSession().setAttribute("loginErrorMessage", message);
        response.sendRedirect(request.getContextPath() + "/login.jsp#join");
    }

}
