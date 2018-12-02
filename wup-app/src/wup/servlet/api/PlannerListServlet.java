package wup.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import wup.api.GsonHolder;
import wup.data.Planner;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.PlannerDao;
import wup.servlet.ServletHelper;

/**
 * Servlet implementation class PlannerListServlet
 */
@WebServlet("/api/planners")
public class PlannerListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");

        User authenticatedUser = ServletHelper.checkAuth(request, response);

        if (authenticatedUser == null) {
            return;
        }

        PrintWriter out = response.getWriter();
        Gson gson = GsonHolder.getGson();
        MariaDbDaoFactory daoFactory = new DaoFactory();
        PlannerDao plannerDao = (PlannerDao) daoFactory.getDao(Planner.class);

        DaoResult<List<Planner>> getPlannersResult = plannerDao.getPlanners(authenticatedUser);

        if (!getPlannersResult.didSucceed()) {
            ServletHelper.onDaoError(response, getPlannersResult);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            out.println(gson.toJson(getPlannersResult.getData()));
        }
    }

}
