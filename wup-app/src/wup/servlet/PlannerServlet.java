package wup.servlet;

import java.io.IOException;

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

/**
 * Servlet implementation class PlannerServlet
 */
@WebServlet("/planner/*")
public class PlannerServlet extends HttpServlet {
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

        RequirePlannerResult rp = requirePlanner(request, authenticatedUser);

        if (rp.exception != null) {
            throw new ServletException(rp.exception);
        } else if (rp.errorCode > 0) {
            response.sendError(rp.errorCode);

            return;
        }

        Planner planner = rp.planner;

        request.setAttribute("planner", planner);
        request.getRequestDispatcher("/planner.jsp").forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json; charset=utf-8");

        User authenticatedUser = ServletHelper.checkAuth(request, response);

        String title = ServletHelper.trimString(request.getParameter("title"));

        if (title.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(new Error(Error.E_ARGMISSING).toJson());

            return;
        }

        Gson gson = GsonHolder.getGson();
        MariaDbDaoFactory daoFactory = new DaoFactory();
        PlannerDao plannerDao = (PlannerDao) daoFactory.getDao(Planner.class);
        Planner planner = new Planner();

        planner.setType(ItemOwner.Type.USER);
        planner.setTitle(title);

        DaoResult<Planner> createPlannerResult = plannerDao.createPlanner(authenticatedUser, planner);

        if (!createPlannerResult.didSucceed()) {
            ServletHelper.onDaoError(response, createPlannerResult);
        } else {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().println(gson.toJson(createPlannerResult.getData()));
        }
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = ServletHelper.checkAuth(request, response);
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User authenticatedUser = ServletHelper.checkAuth(request, response);
    }

    private static class RequirePlannerResult {
        public final Planner planner;
        public final int errorCode;
        public final Exception exception;

        private RequirePlannerResult(Planner planner, int errorCode, Exception exception) {
            this.planner = planner;
            this.errorCode = errorCode;
            this.exception = exception;
        }

        public static RequirePlannerResult succeed(Planner planner) {
            return new RequirePlannerResult(planner, -1, null);
        }

        public static RequirePlannerResult fail(int errorCode) {
            return new RequirePlannerResult(null, errorCode, null);
        }

        public static RequirePlannerResult fail(Exception exception) {
            return new RequirePlannerResult(null, -1, exception);
        }
    }

    private RequirePlannerResult requirePlanner(HttpServletRequest request, User user)
            throws ServletException, IOException {
        int plannerId = parsePlannerId(request.getPathInfo());

        if (plannerId < 0) {
            return RequirePlannerResult.fail(HttpServletResponse.SC_NOT_FOUND);
        }

        MariaDbDaoFactory daoFactory = new DaoFactory();
        PlannerDao plannerDao = (PlannerDao) daoFactory.getDao(Planner.class);
        DaoResult<Planner> getPlannerResult = plannerDao.getPlanner(plannerId);

        if (!getPlannerResult.didSucceed()) {
            return RequirePlannerResult.fail(getPlannerResult.getException());
        }

        Planner planner = getPlannerResult.getData();

        if (planner == null) {
            return RequirePlannerResult.fail(HttpServletResponse.SC_NOT_FOUND);
        }

        DaoResult<Boolean> getAccessResult = hasAccess(planner, user);

        if (!getAccessResult.didSucceed()) {
            return RequirePlannerResult.fail(getAccessResult.getException());
        } else if (!getAccessResult.getData()) {
            return RequirePlannerResult.fail(HttpServletResponse.SC_FORBIDDEN);
        } else {
            return RequirePlannerResult.succeed(planner);
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
