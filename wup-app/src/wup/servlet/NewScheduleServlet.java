package wup.servlet;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Map;

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

        Map<String, String> strings = ScheduleHelper.getStringFields(request);
        Map<String, Integer> numbers = ScheduleHelper.getNumberFields(request);

        Planner planner = new Planner();
        Schedule schedule = new Schedule();

        planner.setId(numbers.get("plannerId"));

        schedule.setTitle(strings.get("title"));
        schedule.setLocation(strings.get("location"));
        schedule.setStartsAt(new GregorianCalendar(numbers.get("start_year"), numbers.get("start_month") - 1,
                numbers.get("start_date"), numbers.get("start_hour"), numbers.get("start_minute")).getTime());
        schedule.setEndsAt(new GregorianCalendar(numbers.get("end_year"), numbers.get("end_month") - 1,
                numbers.get("end_date"), numbers.get("end_hour"), numbers.get("end_minute")).getTime());
        schedule.setAllDay(strings.get("allday").equals("on"));
        schedule.setDescription(strings.get("description"));

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
