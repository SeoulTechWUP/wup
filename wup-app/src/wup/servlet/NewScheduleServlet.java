package wup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.Planner;
import wup.data.Schedule;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.ScheduleDao;

/**
 * Servlet implementation class NewScheduleServlet
 */
@WebServlet("/newSchedule")
public class NewScheduleServlet extends HttpServlet {
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

        String plannerId = request.getParameter("plannerId");

        if (plannerId == null || plannerId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);

            return;
        }

        request.setAttribute("mode", "new");
        request.setAttribute("plannerId", plannerId);
        request.getRequestDispatcher("/scheduleView.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");

        if (authenticatedUser == null || authenticatedUser.getEmail() == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");

            return;
        }

        Planner planner = new Planner();
        Schedule schedule = ScheduleHelper.getScheduleFromRequest(request);

        try {
            planner.setId(Integer.parseInt(request.getParameter("plannerId")));
        } catch (NumberFormatException e) {
            throw new ServletException(e);
        }

        MariaDbDaoFactory daoFactory = new DaoFactory();
        ScheduleDao scheduleDao = (ScheduleDao) daoFactory.getDao(Schedule.class);
        DaoResult<Schedule> createScheduleResult = scheduleDao.createSchedule(planner, schedule);

        if (!createScheduleResult.didSucceed()) {
            throw new ServletException(createScheduleResult.getException());
        }

        Schedule createdSchedule = createScheduleResult.getData();

        response.sendRedirect(request.getContextPath() + "/schedule/" + createdSchedule.getId());
    }

}
