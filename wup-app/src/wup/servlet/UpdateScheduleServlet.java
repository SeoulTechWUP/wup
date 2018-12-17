package wup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.Schedule;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.ScheduleDao;

/**
 * Servlet implementation class UpdateScheduleServlet
 */
@WebServlet("/updateSchedule")
public class UpdateScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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

        String scheduleIdStr = ServletHelper.trimString(request.getParameter("scheduleId"));
        int scheduleId;

        try {
            scheduleId = Integer.parseInt(scheduleIdStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        ScheduleHelper.RequireResult rs = ScheduleHelper.requireSchedule(scheduleId, authenticatedUser);

        if (rs.exception != null) {
            throw new ServletException(rs.exception);
        } else if (rs.errorCode > 0) {
            response.sendError(rs.errorCode);

            return;
        }

        Schedule schedule = ScheduleHelper.getScheduleFromRequest(request);
        MariaDbDaoFactory daoFactory = new DaoFactory();
        ScheduleDao scheduleDao = (ScheduleDao) daoFactory.getDao(Schedule.class);
        DaoResult<Schedule> updateScheduleResult = scheduleDao.updateSchedule(scheduleId, schedule);

        if (!updateScheduleResult.didSucceed()) {
            throw new ServletException(updateScheduleResult.getException());
        }

        response.sendRedirect(request.getContextPath() + "/schedule/" + scheduleId);
    }

}
