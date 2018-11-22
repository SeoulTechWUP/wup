package wup.data.access;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * JDBC를 사용하는 DAO의 상위 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public abstract class JdbcDao {
    /**
     * <code>name</code>으로 지정한 JDBC 연결을 가져옵니다.
     * 
     * @param name 리소스 이름
     * @return <code>name</code>이 존재하는 경우, 해당하는 {@link java.sql.Connection} 객체를
     *         반환하고, 없으면 <code>null</code>을 반환합니다.
     * @throws SQLException
     */
    protected final Connection getConnection(String name) throws SQLException {
        try {
            Context initCtx = new InitialContext();
            Context ctx = (Context) initCtx.lookup("java:/comp/env");
            DataSource ds = (DataSource) ctx.lookup(name);

            return ds.getConnection();
        } catch (NamingException e) {
            e.printStackTrace();

            return null;
        }
    }
}
