<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="wup.data.User" %>
<%
User authenticatedUser = (User) session.getAttribute("authenticatedUser");

if (authenticatedUser == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
${ authenticatedUser.fullName }
</body>
</html>