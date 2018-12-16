package wup.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.data.User;

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
    }

}
