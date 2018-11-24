package wup.data.access;

import java.lang.reflect.Constructor;

/**
 * MariaDB를 사용하는 DAO를 생성하는 클래스를 위한 추상 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public abstract class MariaDbDaoFactory {
    public final <T> MariaDbDao getDao(Class<T> dataObjectClass) {
        try {
            String clsName = String.format("wup.data.access.MariaDb%sDao", dataObjectClass.getSimpleName());
            Class<?> daoClass = Class.forName(clsName);
            Constructor<?> ctor = daoClass.getConstructor(JdbcConnectionProvider.class);

            return (MariaDbDao) ctor.newInstance(getConnectionProvider());
        } catch (Exception e) {
            return null;
        }
    }

    protected abstract JdbcConnectionProvider getConnectionProvider();
}
