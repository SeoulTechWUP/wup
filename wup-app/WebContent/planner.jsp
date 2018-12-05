<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="planner" class="wup.data.Planner" scope="request" />
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>${planner.title} | WUP</title>
</head>

<body>
    ${planner.title}<br>
    ${planner.createdAt}<br>
    ${planner.modifiedAt}<br>
    ${planner.type}<br>
    ${planner.owner.id}<br>
    ${requestScope.currentYear}. ${requestScope.currentMonth}<br>
    <hr>
    <ul>
        <c:forEach items="${requestScope.schedules}" var="schedule">
            <li>
                ${schedule.id}<br>
                ${schedule.startsAt} ~ ${schedule.endsAt}<br>
                ${schedule.title}
            </li>
        </c:forEach>
    </ul>
</body>

</html>