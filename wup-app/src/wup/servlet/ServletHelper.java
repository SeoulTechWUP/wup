package wup.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wup.api.Error;
import wup.data.User;
import wup.data.access.DaoResult;

/**
 * Servlet에서 유용하게 사용할 수 있는 메서드를 모아놓은 클래스입니다.
 *
 * @author Eunbin Jeong
 */
public class ServletHelper {
    public static User checkAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User authenticatedUser = (User) request.getSession().getAttribute("authenticatedUser");

        if (authenticatedUser == null) {
            PrintWriter out = response.getWriter();

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.println(new Error(Error.E_NOAUTH).toJson());
        }

        return authenticatedUser;
    }

    public static void onDaoError(HttpServletResponse response, DaoResult<?> failedResult) throws IOException {
        PrintWriter out = response.getWriter();
        Exception e = failedResult.getException();

        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        if (e instanceof SQLException) {
            out.println(new Error(Error.E_DBERROR).toJson());
        } else {
            out.println(new Error(-1).toJson());
        }
    }

    public static String trimString(String string) {
        if (string == null) {
            return "";
        }

        return string.trim();
    }
}
