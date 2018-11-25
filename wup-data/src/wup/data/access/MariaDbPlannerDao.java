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

import wup.data.Group;
import wup.data.ItemOwner;
import wup.data.Planner;
import wup.data.User;

/**
 * MariaDB를 통해 플래너 정보에 접근하는 DAO입니다
 *
 * @author Eunbin Jeong
 */
public class MariaDbPlannerDao extends MariaDbDao implements PlannerDao {

    private static final String TABLE_NAME = "planner";

    private static final String SQL_GET_BY_USER = "SELECT * FROM `planners` WHERE `type` = 'user' AND `user_id` = ?";
    private static final String SQL_GET_BY_GROUP = "SELECT * FROM `planners` WHERE `type` = 'group' AND `group_id` = ?";
    private static final String SQL_INSERT_FORMAT = "INSERT INTO `planners` (`created_at`, `modified_at`, `type`, `%s_id`, `title`) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_DELETE_BY_ID = "DELETE FROM `planners` WHERE `id` = ?";

    public MariaDbPlannerDao(JdbcConnectionProvider connectionProvider) {
        super(connectionProvider);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PlannerDao#getPlanner(int)
     */
    @Override
    public DaoResult<Planner> getPlanner(int id) {
        return querySingleItem(TABLE_NAME, id, (rs) -> {
            Planner planner = null;

            if (rs.next()) {
                planner = getPlannerFromResultSet(rs, true);
            }

            return DaoResult.succeed(DaoResult.Action.READ, planner);
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PlannerDao#getPlanners(wup.data.ItemOwner)
     */
    @Override
    public DaoResult<List<Planner>> getPlanners(ItemOwner owner) {
        String sql = owner instanceof User ? SQL_GET_BY_USER : SQL_GET_BY_GROUP;

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, owner.getId());

            try (ResultSet result = stmt.executeQuery()) {
                List<Planner> planners = new ArrayList<Planner>();

                while (result.next()) {
                    Planner planner = getPlannerFromResultSet(result, false);

                    planner.setOwner(owner);
                    planners.add(planner);
                }

                return DaoResult.succeed(DaoResult.Action.READ, planners);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PlannerDao#createPlanner(wup.data.ItemOwner,
     * wup.data.Planner)
     */
    @Override
    public DaoResult<Planner> createPlanner(ItemOwner owner, Planner planner) {
        String type = owner instanceof User ? "user" : "group";
        String sql = String.format(SQL_INSERT_FORMAT, type);

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            Timestamp now = new Timestamp(new Date().getTime());

            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            stmt.setString(3, type);
            stmt.setInt(4, owner.getId());
            stmt.setString(5, planner.getTitle());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                generatedKeys.next();

                int createdPlannerId = generatedKeys.getInt(1);

                return getPlanner(createdPlannerId);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.CREATE, e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PlannerDao#updatePlanner(int, wup.data.Planner)
     */
    @Override
    public DaoResult<Planner> updatePlanner(int id, Planner planner) {
        DaoResult<Planner> getPlannerResult = getPlanner(id);

        if (!getPlannerResult.didSucceed()) {
            return getPlannerResult;
        }

        Planner oldPlanner = getPlannerResult.getData();
        List<Entry<String, String>> fieldMap = new ArrayList<>();

        fieldMap.add(new SimpleEntry<String, String>("title", "title"));

        DaoResult<Boolean> updatePlannerResult = updateSingleItem(TABLE_NAME, id, oldPlanner, planner, fieldMap);

        if (!updatePlannerResult.didSucceed()) {
            return DaoResult.fail(DaoResult.Action.UPDATE, updatePlannerResult.getException());
        }

        return getPlanner(id);
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.PlannerDao#deletePlanner(int)
     */
    @Override
    public DaoResult<Boolean> deletePlanner(int id) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_BY_ID)) {
            stmt.setInt(1, id);

            int deletedRows = stmt.executeUpdate();

            return DaoResult.succeed(DaoResult.Action.DELETE, deletedRows > 0);
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.DELETE, e);
        }
    }

    private Planner getPlannerFromResultSet(ResultSet rs, boolean includeOwner) throws Exception {
        Planner planner = new Planner();
        ItemOwner.Type ownerType = ItemOwner.Type.valueOf(rs.getString("type").toUpperCase());

        planner.setId(rs.getInt("id"));
        planner.setCreatedAt(rs.getTimestamp("created_at"));
        planner.setModifiedAt(rs.getTimestamp("modified_at"));
        planner.setType(ownerType);
        planner.setTitle(rs.getString("title"));

        if (includeOwner) {
            if (ownerType == ItemOwner.Type.USER) {
                DaoResult<User> getUserResult = new MariaDbUserDao(connectionProvider).getUser(rs.getInt("user_id"));

                if (getUserResult.didSucceed()) {
                    planner.setOwner(getUserResult.getData());
                } else {
                    throw getUserResult.getException();
                }
            } else {
                DaoResult<Group> getGroupResult = new MariaDbGroupDao(connectionProvider).getGroup(rs.getInt("group_id"));

                if (getGroupResult.didSucceed()) {
                    planner.setOwner(getGroupResult.getData());
                } else {
                    throw getGroupResult.getException();
                }
            }
        }

        return planner;
    }

}
