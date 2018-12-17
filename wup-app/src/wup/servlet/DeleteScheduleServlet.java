package wup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.Group;
import wup.data.ItemOwner;
import wup.data.Planner;
import wup.data.Schedule;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.ScheduleDao;

/**
 * Servlet implementation class DeleteScheduleServlet
 */
@WebServlet("/deleteSchedule")
public class DeleteScheduleServlet extends HttpServlet {
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

        Planner planner = rs.schedule.getPlanner();
        MariaDbDaoFactory daoFactory = new DaoFactory();
        ScheduleDao scheduleDao = (ScheduleDao) daoFactory.getDao(Schedule.class);
        DaoResult<Boolean> deleteScheduleResult = scheduleDao.deleteSchedule(scheduleId);

        if (!deleteScheduleResult.didSucceed()) {
            throw new ServletException(deleteScheduleResult.getException());
        }

        if (planner.getType() == ItemOwner.Type.USER) {
            response.sendRedirect(request.getContextPath() + "/planner/" + planner.getId());
        } else {
            Group group = (Group) planner.getOwner();

            response.sendRedirect(request.getContextPath() + "/group/" + group.getId());
        }
    }

}
