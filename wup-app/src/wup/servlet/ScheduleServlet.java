package wup.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.User;

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

        ScheduleHelper.RequireResult rs = ScheduleHelper.requireSchedule(id, authenticatedUser);

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

}
