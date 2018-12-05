package wup.data.access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import wup.data.Planner;
import wup.data.Schedule;

/**
 * MariaDB를 통해 일정 정보에 접근하는 DAO입니다.
 *
 * @author Eunbin Jeong
 */
public class MariaDbScheduleDao extends MariaDbDao implements ScheduleDao {

    private static final String TABLE_NAME = "schedule";

    private static final String SQL_GET_BY_PLANNER = "SELECT * FROM `schedule` WHERE `planner_id` = ? ORDER BY `starts_at` ASC";
    private static final String SQL_GET_BY_RANGE = "SELECT * FROM `schedule` WHERE `planner_id` = ? AND (`starts_at` BETWEEN ? AND ? OR `ends_at` BETWEEN ? AND ?) ORDER BY `starts_at` ASC";
    private static final String SQL_CREATE = "INSERT INTO `schedule` (`created_at`, `modified_at`, `planner_id`, `title`, `description`, `starts_at`, `ends_at`, `allday`) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    public MariaDbScheduleDao(JdbcConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.ScheduleDao#getSchedule(int)
     */
    @Override
    public DaoResult<Schedule> getSchedule(int id) {
        return querySingleItem(TABLE_NAME, id, (rs) -> {
            Schedule schedule = null;

            if (rs.next()) {
                schedule = getScheduleFromResultSet(rs, true);
            }

            return DaoResult.succeed(DaoResult.Action.READ, schedule);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.ScheduleDao#getSchedules(wup.data.Planner)
     */
    @Override
    public DaoResult<List<Schedule>> getSchedules(Planner planner) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_PLANNER)) {
            stmt.setInt(1, planner.getId());

            try (ResultSet result = stmt.executeQuery()) {
                List<Schedule> schedules = new ArrayList<Schedule>();

                while (result.next()) {
                    Schedule schedule = getScheduleFromResultSet(result, false);

                    schedule.setPlanner(planner);
                    schedules.add(schedule);
                }

                return DaoResult.succeed(DaoResult.Action.READ, schedules);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.ScheduleDao#getSchedules(wup.data.Planner,
     * java.util.Date, java.util.Date)
     */
    @Override
    public DaoResult<List<Schedule>> getSchedules(Planner planner, Date from, Date to) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_BY_RANGE)) {
            Timestamp fromTS = new Timestamp(from.getTime());
            Timestamp toTS = new Timestamp(to.getTime());

            stmt.setInt(1, planner.getId());
            stmt.setTimestamp(2, fromTS);
            stmt.setTimestamp(3, toTS);
            stmt.setTimestamp(4, fromTS);
            stmt.setTimestamp(5, toTS);

            try (ResultSet result = stmt.executeQuery()) {
                List<Schedule> schedules = new ArrayList<Schedule>();

                while (result.next()) {
                    Schedule schedule = getScheduleFromResultSet(result, false);

                    schedule.setPlanner(planner);
                    schedules.add(schedule);
                }

                return DaoResult.succeed(DaoResult.Action.READ, schedules);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.ScheduleDao#createSchedule(wup.data.Planner,
     * wup.data.Schedule)
     */
    @Override
    public DaoResult<Schedule> createSchedule(Planner planner, Schedule schedule) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());
            Timestamp startTS = new Timestamp(schedule.getStartsAt().getTime());
            Timestamp endTS = new Timestamp(schedule.getEndsAt().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setInt(3, planner.getId());
            stmt.setString(4, schedule.getTitle());
            stmt.setString(5, schedule.getDescription());
            stmt.setTimestamp(6, startTS);
            stmt.setTimestamp(7, endTS);
            stmt.setBoolean(8, schedule.getAllDay());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdScheduleId = generatedKeys.getInt(1);

                return getSchedule(createdScheduleId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.ScheduleDao#updateSchedule(int, wup.data.Schedule)
     */
    @Override
    public DaoResult<Schedule> updateSchedule(int id, Schedule schedule) {
        DaoResult<Schedule> getScheduleResult = getSchedule(id);

        if (!getScheduleResult.didSucceed()) {
            return getScheduleResult;
        }

        Schedule oldSchedule = getScheduleResult.getData();
        List<Entry<String, String>> fieldMap = new ArrayList<>();

        fieldMap.add(new SimpleEntry<String, String>("title", "title"));
        fieldMap.add(new SimpleEntry<String, String>("description", "description"));
        fieldMap.add(new SimpleEntry<String, String>("location", "location"));
        fieldMap.add(new SimpleEntry<String, String>("startsAt", "starts_at"));
        fieldMap.add(new SimpleEntry<String, String>("endsAt", "ends_at"));
        fieldMap.add(new SimpleEntry<String, String>("allDay", "allday"));

        DaoResult<Boolean> updateScheduleResult = updateSingleItem(TABLE_NAME, id, oldSchedule, schedule, fieldMap);

        if (!updateScheduleResult.didSucceed()) {
            return DaoResult.fail(DaoResult.Action.UPDATE, updateScheduleResult.getException());
        }

        return getSchedule(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.ScheduleDao#deleteSchedule(int)
     */
    @Override
    public DaoResult<Boolean> deleteSchedule(int id) {
        return deleteSingleItem(TABLE_NAME, id);
    }

    private Schedule getScheduleFromResultSet(ResultSet rs, boolean includePlanner) throws Exception {
        Schedule schedule = new Schedule();

        schedule.setId(rs.getInt("id"));
        schedule.setCreatedAt(rs.getTimestamp("created_at"));
        schedule.setModifiedAt(rs.getTimestamp("modified_at"));
        schedule.setTitle(rs.getString("title"));
        schedule.setDescription(rs.getString("description"));
        schedule.setLocation(rs.getString("location"));
        schedule.setStartsAt(rs.getTimestamp("starts_at"));
        schedule.setEndsAt(rs.getTimestamp("ends_at"));
        schedule.setAllDay(rs.getBoolean("allday"));
        // schedule.setLabels( ? );

        if (includePlanner) {
            DaoResult<Planner> getPlannerResult = new MariaDbPlannerDao(connectionProvider).getPlanner(rs.getInt("planner_id"));

            if (!getPlannerResult.didSucceed()) {
                throw getPlannerResult.getException();
            }

            schedule.setPlanner(getPlannerResult.getData());
        }

        return schedule;
    }

}
