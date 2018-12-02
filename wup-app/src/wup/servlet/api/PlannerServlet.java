package wup.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import wup.api.Error;
import wup.api.GsonHolder;
import wup.data.Group;
import wup.data.ItemOwner;
import wup.data.Planner;
import wup.data.User;
import wup.data.access.DaoFactory;
import wup.data.access.DaoResult;
import wup.data.access.GroupDao;
import wup.data.access.MariaDbDaoFactory;
import wup.data.access.PlannerDao;
import wup.servlet.ServletHelper;

/**
 * Servlet implementation class PlannerServlet
 */
@WebServlet("/api/planner/*")
public class PlannerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");

        User authenticatedUser = ServletHelper.checkAuth(request, response);

        if (authenticatedUser == null) {
            return;
        }

        super.service(request, response);
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");
        Planner planner = requirePlanner(request, response, authenticatedUser);

        if (planner == null) {
            return;
        }

        PrintWriter out = response.getWriter();
        Gson gson = GsonHolder.getGson();

        response.setStatus(HttpServletResponse.SC_OK);
        out.println(gson.toJson(planner));
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");
        Planner planner = requirePlanner(request, response, authenticatedUser);

        if (planner == null) {
            return;
        }
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");
        Planner planner = requirePlanner(request, response, authenticatedUser);

        if (planner == null) {
            return;
        }
    }

    private Planner requirePlanner(HttpServletRequest request, HttpServletResponse response, User user)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        int plannerId = parsePlannerId(pathInfo);

        if (plannerId < 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new Error(Error.E_NOENT).toJson());

            return null;
        }

        MariaDbDaoFactory daoFactory = new DaoFactory();
        PlannerDao plannerDao = (PlannerDao) daoFactory.getDao(Planner.class);
        DaoResult<Planner> getPlannerResult = plannerDao.getPlanner(plannerId);

        if (!getPlannerResult.didSucceed()) {
            ServletHelper.onDaoError(response, getPlannerResult);

            return null;
        }

        Planner planner = getPlannerResult.getData();

        if (planner == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println(new Error(Error.E_NOENT).toJson());

            return null;
        }

        DaoResult<Boolean> getAccessResult = hasAccess(planner, user);

        if (!getAccessResult.didSucceed()) {
            ServletHelper.onDaoError(response, getAccessResult);

            return null;
        } else if (!getAccessResult.getData()) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(new Error(Error.E_NOACCESS).toJson());

            return null;
        } else {
            return planner;
        }
    }

    private int parsePlannerId(String pathInfo) {
        if (pathInfo == null || pathInfo.isEmpty()) {
            return -1;
        }

        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        try {
            return Integer.parseInt(pathInfo);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private DaoResult<Boolean> hasAccess(Planner planner, User user) {
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
