package wup.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.Group;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.GroupDao;
import wup.data.access.MariaDbDaoFactory;

/**
 * Servlet implementation class GroupListServlet
 */
@WebServlet("/groups")
public class GroupListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");

        if (authenticatedUser == null || authenticatedUser.getEmail() == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");

            return;
        }

        MariaDbDaoFactory daoFactory = new DaoFactory();
        GroupDao groupDao = (GroupDao) daoFactory.getDao(Group.class);
        DaoResult<List<Group>> getGroupsResult = groupDao.getGroups(authenticatedUser);

        if (!getGroupsResult.didSucceed()) {
            throw new ServletException(getGroupsResult.getException());
        } else {
            request.setAttribute("groups", getGroupsResult.getData());
            request.getRequestDispatcher("/groupList.jsp").forward(request, response);
        }
    }

}
