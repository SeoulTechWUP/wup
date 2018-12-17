package wup.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import wup.data.access.GroupDao;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.ScheduleDao;

/**
 * Servlet implementation class ScheduleServlet
 */
@WebServlet("/schedule/*")
public class ScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern scheduleRegex;

    static {
        scheduleRegex = Pattern.compile("^\\/(?<id>\\d+)$");
    }

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

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        Matcher scheduleMatcher = scheduleRegex.matcher(pathInfo);
        int id;

        try {
            if (scheduleMatcher.matches()) {
                id = Integer.parseInt(scheduleMatcher.group("id"));
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return;
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }

        RequireScheduleResult rs = requireSchedule(id, authenticatedUser);

        if (rs.exception != null) {
            throw new ServletException(rs.exception);
        } else if (rs.errorCode > 0) {
            response.sendError(rs.errorCode);

            return;
        }

        request.setAttribute("schedule", rs.schedule);
        request.setAttribute("mode", "edit");
        request.getRequestDispatcher("/scheduleView.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");

        if (authenticatedUser == null || authenticatedUser.getEmail() == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");

            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);

            return;
        }
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    private static class RequireScheduleResult {
        public final Schedule schedule;
        public final int errorCode;
        public final Exception exception;

        private RequireScheduleResult(Schedule schedule, int errorCode, Exception exception) {
            this.schedule = schedule;
            this.errorCode = errorCode;
            this.exception = exception;
        }

        public static RequireScheduleResult succeed(Schedule schedule) {
            return new RequireScheduleResult(schedule, -1, null);
        }

        public static RequireScheduleResult fail(int errorCode) {
            return new RequireScheduleResult(null, errorCode, null);
        }

        public static RequireScheduleResult fail(Exception exception) {
            return new RequireScheduleResult(null, -1, exception);
        }
    }

    private RequireScheduleResult requireSchedule(int scheduleId, User user) {
        MariaDbDaoFactory daoFactory = new DaoFactory();
        ScheduleDao scheduleDao = (ScheduleDao) daoFactory.getDao(Schedule.class);
        DaoResult<Schedule> getScheduleResult = scheduleDao.getSchedule(scheduleId);

        if (!getScheduleResult.didSucceed()) {
            return RequireScheduleResult.fail(getScheduleResult.getException());
        }

        Schedule schedule = getScheduleResult.getData();

        if (schedule == null) {
            return RequireScheduleResult.fail(HttpServletResponse.SC_NOT_FOUND);
        }

        DaoResult<Boolean> getAccessResult = hasAccess(schedule, user);

        if (!getAccessResult.didSucceed()) {
            return RequireScheduleResult.fail(getAccessResult.getException());
        } else if (!getAccessResult.getData()) {
            return RequireScheduleResult.fail(HttpServletResponse.SC_FORBIDDEN);
        } else {
            return RequireScheduleResult.succeed(schedule);
        }
    }

    private DaoResult<Boolean> hasAccess(Schedule schedule, User user) {
        Planner planner = schedule.getPlanner();

        if (planner.getType() == ItemOwner.Type.USER) {
            // 플래너 소유자 일치 여부 확인
            return DaoResult.succeed(DaoResult.Action.READ, planner.getOwner().getId() == user.getId());
        } else {
            // 플래너를 소유하고 있는 그룹에 소속되어 있는지 여부 확인
            MariaDbDaoFactory daoFactory = new DaoFactory();
            GroupDao groupDao = (GroupDao) daoFactory.getDao(Group.class);
            Group owningGroup = (Group) planner.getOwner();

            return groupDao.isMember(owningGroup, user);
        }
    }

}
