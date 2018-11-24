package wup.data.access;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * {@link java.sql.Connection} 개체를 가져오는 기능이 있는 클래스에 대한 인터페이스입니다.
 *
 * @author Eunbin Jeong
 */
public interface JdbcConnectionProvider {
    /**
     * JDBC를 통해 데이터베이스 연결을 엽니다.
     *
     * @return 연결이 성공적으로 열린 경우 {@link java.sql.Connection} 개체를 반환합니다.
     * @throws SQLException 데이터베이스 연결을 열지 못한 경우
     */
    public Connection getConnection() throws SQLException;
}
