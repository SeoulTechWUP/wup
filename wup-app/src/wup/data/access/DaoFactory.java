package wup.data.access;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author Eunbin Jeong
 */
public class DaoFactory extends MariaDbDaoFactory {

    private final JdbcConnectionProvider connectionProvider;

    public DaoFactory() {
        connectionProvider = new JdbcConnectionProvider() {

            @Override
            public Connection getConnection() throws SQLException {
                try {
                    Context initCtx = new InitialContext();
                    Context ctx = (Context) initCtx.lookup("java:/comp/env");
                    DataSource ds = (DataSource) ctx.lookup("jdbc/mariadb");

                    return ds.getConnection();
                } catch (NamingException e) {
                    e.printStackTrace();

                    return null;
                }
            }

        };
    }

    /*
     * (non-Javadoc)
     *
     * @see wup.data.access.MariaDbDaoFactory#getConnectionProvider()
     */
    @Override
    protected JdbcConnectionProvider getConnectionProvider() {
        return connectionProvider;
    }

}
