package wup.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.Planner;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.PlannerDao;

/**
 * Servlet implementation class PlannerListServlet
 */
@WebServlet("/planners")
public class PlannerListServlet extends HttpServlet {
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
        PlannerDao plannerDao = (PlannerDao) daoFactory.getDao(Planner.class);
        DaoResult<List<Planner>> getPlannersResult = plannerDao.getPlanners(authenticatedUser);

        if (!getPlannersResult.didSucceed()) {
            throw new ServletException(getPlannersResult.getException());
        } else {
            request.setAttribute("planners", getPlannersResult.getData());
            request.getRequestDispatcher("/plannerList.jsp").forward(request, response);
        }
    }

}
