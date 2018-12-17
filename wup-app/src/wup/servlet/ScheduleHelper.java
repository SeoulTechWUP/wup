package wup.servlet;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
 * 서블릿에서 일정 관련 정보를 가져오는 데 도움을 주는 메서드를 제공하는 클래스입니다.
 *
 * @author Eunbin Jeong
 */
class ScheduleHelper {
    private static final String[] stringFields;
    private static final String[] numberFields;

    static {
        stringFields = new String[] {
                "title", "location", "allday", "description"
        };

        numberFields = new String[] {
                "plannerId", "scheduleId", "start_year", "start_month", "start_date", "start_hour", "start_minute",
                "end_year", "end_month", "end_date", "end_hour", "end_minute"
        };
    }

    public static Map<String, String> getStringFields(HttpServletRequest request) {
        return Arrays.stream(stringFields)
                .collect(Collectors.toMap(x -> x, x -> ServletHelper.trimString(request.getParameter(x))));
    }

    public static Map<String, Integer> getNumberFields(HttpServletRequest request) {
        return Arrays.stream(numberFields).collect(Collectors.toMap(x -> x, x -> {
            try {
                return Integer.parseInt(ServletHelper.trimString(request.getParameter(x)));
            } catch (NumberFormatException e) {
                return null;
            }
        }));
    }

    public static class RequireResult {
        public final Schedule schedule;
        public final int errorCode;
        public final Exception exception;

        private RequireResult(Schedule schedule, int errorCode, Exception exception) {
            this.schedule = schedule;
            this.errorCode = errorCode;
            this.exception = exception;
        }

        public static RequireResult succeed(Schedule schedule) {
            return new RequireResult(schedule, -1, null);
        }

        public static RequireResult fail(int errorCode) {
            return new RequireResult(null, errorCode, null);
        }

        public static RequireResult fail(Exception exception) {
            return new RequireResult(null, -1, exception);
        }
    }

    public static RequireResult requireSchedule(int scheduleId, User user) {
        MariaDbDaoFactory daoFactory = new DaoFactory();
        ScheduleDao scheduleDao = (ScheduleDao) daoFactory.getDao(Schedule.class);
        DaoResult<Schedule> getScheduleResult = scheduleDao.getSchedule(scheduleId);

        if (!getScheduleResult.didSucceed()) {
            return RequireResult.fail(getScheduleResult.getException());
        }

        Schedule schedule = getScheduleResult.getData();

        if (schedule == null) {
            return RequireResult.fail(HttpServletResponse.SC_NOT_FOUND);
        }

        DaoResult<Boolean> getAccessResult = hasAccess(schedule, user);

        if (!getAccessResult.didSucceed()) {
            return RequireResult.fail(getAccessResult.getException());
        } else if (!getAccessResult.getData()) {
            return RequireResult.fail(HttpServletResponse.SC_FORBIDDEN);
        } else {
            return RequireResult.succeed(schedule);
        }
    }

    private static DaoResult<Boolean> hasAccess(Schedule schedule, User user) {
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
