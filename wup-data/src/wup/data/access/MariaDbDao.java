package wup.data.access;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * MariaDB를 사용하는 DAO를 지원하기 위한 추상 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public abstract class MariaDbDao {
    @FunctionalInterface
    protected interface ResultSetFunction<U> {
        public DaoResult<U> process(ResultSet rs) throws Exception;
    }

    protected final JdbcConnectionProvider connectionProvider;

    protected MariaDbDao(JdbcConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    protected <T> DaoResult<T> querySingleItem(String tableName, int id, ResultSetFunction<T> func) {
        String sql = String.format("SELECT * FROM `%s` WHERE `id` = ?", tableName);

        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return func.process(rs);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.READ, e);
        }
    }

    protected <T> DaoResult<Boolean> updateSingleItem(String tableName, int id, T oldItem, T newItem, List<Entry<String, String>> fieldMap) {
        try (Connection conn = connectionProvider.getConnection()) {
            boolean shouldUpdateModifiedAt = false;

            Class<?> dataObjectClass = newItem.getClass();
            StringBuilder updateParamBuilder = new StringBuilder();
            List<Object> updateValueList = new ArrayList<>();
            int fieldCount = fieldMap.size();

            for (Entry<String, String> prop : fieldMap) {
                Field field = dataObjectClass.getDeclaredField(prop.getKey());

                field.setAccessible(true);

                Object newValue = field.get(newItem);

                updateParamBuilder.append(String.format(", `%s` = ?", prop.getValue()));
                updateValueList.add(newValue);

                if (!Objects.equals(field.get(oldItem), newValue)) {
                    shouldUpdateModifiedAt = true;
                }
            }

            Field modifiedAtField = dataObjectClass.getDeclaredField("modifiedAt");

            modifiedAtField.setAccessible(true);

            Date oldModifiedAt = (Date) modifiedAtField.get(oldItem);
            Timestamp newModifiedAt = shouldUpdateModifiedAt ? new Timestamp(new Date().getTime())
                    : new Timestamp(oldModifiedAt.getTime());

            String sql = String.format("UPDATE `%s` SET `modified_at` = ?%s WHERE `id` = ?", tableName, updateParamBuilder.toString());

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setTimestamp(1, newModifiedAt);

                for (int i = 0; i < fieldCount; i++) {
                    stmt.setObject(i + 2, updateValueList.get(i));
                }

                stmt.setInt(fieldCount + 2, id);
                stmt.executeUpdate();

                return DaoResult.succeed(DaoResult.Action.UPDATE, true);
            }
        } catch (Exception e) {
            return DaoResult.fail(DaoResult.Action.UPDATE, e);
        }
    }
}
