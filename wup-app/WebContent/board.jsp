<%@ page import="wup.data.access.*" %>
<%@ page import="wup.data.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>WUP! - 공유 게시판</title>
</head>
<body>	 
	<div>
		<h1>공유 게시판</h1>
	</div>
	<div>
		<table>
			<tr>
				<td>작성일</td>
				<td>제목</td>
				<td>작성자</td>
			</tr>
			<c:choose>
				<c:when test="${postlist.size() eq '0'}">
				<tr>
					<td colspan="3"><p>게시물이 없습니다.</p></td>
				</tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${postlist}" var="post">
						<tr>
							<td><fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd"/></td>
							<td>${post.title}</td>
							<td>${post.owner.getNickname()}</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</table>
	</div>
</body>
</html>