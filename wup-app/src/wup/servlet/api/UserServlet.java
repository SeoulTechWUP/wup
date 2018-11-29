package wup.servlet.api;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserServlet
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();

        if (pathInfo == null || pathInfo.isEmpty()) {
            out.println("현재 로그인되어 있는 사용자의 정보 표시");

            return;
        }

        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);

            try {
                int userId = Integer.parseInt(pathInfo);

                out.println("일련번호가 " + userId + "인 사용자의 정보 표시");

                return;
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);

                return;
            }
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);

        return;
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("새로운 사용자 생성");
    }

    /**
     * @see HttpServlet#doPut(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("현재 로그인되어 있는 사용자의 정보 수정");
    }

    /**
     * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain; charset=UTF-8");

        PrintWriter out = response.getWriter();

        out.println("현재 로그인되어 있는 사용자의 계정 영구 삭제");
    }

}
