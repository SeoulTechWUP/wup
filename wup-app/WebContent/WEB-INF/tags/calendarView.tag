<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag import="java.util.Iterator" %>
<%@ tag import="wup.data.Schedule" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="scheduleSource" rtexprvalue="true" required="true" type="java.util.List" %>
<%@ attribute name="startingWeekday" rtexprvalue="true" required="true" type="java.lang.Integer" %>
<%@ attribute name="lastDate" rtexprvalue="true" required="true" type="java.lang.Integer" %>
<%@ attribute name="weekdayFrom" rtexprvalue="true" required="true" type="java.lang.Integer" %>
<%@ attribute name="weekdayTo" rtexprvalue="true" required="true" type="java.lang.Integer" %>
<%@ attribute name="rightPage" required="false" type="java.lang.Boolean" %>
<%
Iterator<Schedule> iterator = scheduleSource.iterator();
Schedule current = iterator.hasNext() ? iterator.next() : null;
%>
<c:forEach begin="0" end="5" var="row">
    <tr>
        <c:forEach begin="${weekdayFrom}" end="${weekdayTo}" var="col">
            <c:set var="date" value="${row * 7 + col - startingWeekday + 1}" />
            <td><div><c:if test="${date >= 1 && date <= lastDate}">
                ${date}
                <%
                while (current != null) {
                    long date = (long)jspContext.getAttribute("date");
                    int startDate = current.getStartsAt().getDate(); // deprecated
                    if (startDate > date) {
                        break;
                    }
                    if (startDate == date) {
                %>
                    <br><%= current.getTitle() %>
                <%
                    }
                    current = iterator.hasNext() ? iterator.next() : null;
                }
                %>
            </c:if></div></td>
        </c:forEach>
        <c:if test="${rightPage}"><td></td></c:if>
    </tr>
</c:forEach>