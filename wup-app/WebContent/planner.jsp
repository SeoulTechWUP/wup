<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.GregorianCalendar" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="wup.data.Schedule" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="planner" class="wup.data.Planner" scope="request" />
<%
    List<Schedule> schedules = (List<Schedule>)request.getAttribute("schedules");
    Iterator<Schedule> leftIterator = schedules.iterator();
    Iterator<Schedule> rightIterator = schedules.iterator();
%>
<c:choose>
    <c:when test="${currentMonth == 0}">
        <c:set var="prevMonth" value="12" />
        <c:set var="prevYear" value="${currentYear - 1}" />
    </c:when>
    <c:otherwise>
        <c:set var="prevMonth" value="${currentMonth}" />
        <c:set var="prevYear" value="${currentYear}" />
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${currentMonth == 11}">
        <c:set var="nextMonth" value="1" />
        <c:set var="nextYear" value="${currentYear + 1}" />
    </c:when>
    <c:otherwise>
        <c:set var="nextMonth" value="${currentMonth + 2}" />
        <c:set var="nextYear" value="${currentYear}" />
    </c:otherwise>
</c:choose>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>${planner.title} | WUP</title>
    <wup:includeAssets />
</head>

<body>
    <div id="app-main" class="app">
        <wup:appHeader title="${planner.title}" href="${pageContext.request.contextPath}/${planner.type eq 'USER' ? 'planners' : 'groups'}" />
        <main>
            <div id="planner-view">
                <div class="mode-tabs">
                    <div class="tab">
                        <img src="<c:url value="/assets/images/tab_schedules.svg" />" alt="일정" height="100">
                    </div>
                    <div class="tab">
                        <img src="<c:url value="/assets/images/tab_todo.svg" />" alt="할 일 목록" height="64">
                    </div>
                    <div class="tab">
                        <img src="<c:url value="/assets/images/tab_settings.svg" />" alt="설정" height="90">
                    </div>
                </div>
                <div class="border">
                    <div class="left">
                        <div class="paper">
                            <div class="contents">
                                <header>
                                    <div id="planner-date-spinner">
                                        <a href="<c:url value="/planner/${planner.id}/${prevYear}/${prevMonth}" />">
                                            <div class="spin-btn prev">
                                                <span class="icon arrow left dark"></span>
                                            </div>
                                        </a>
                                        <div class="date-display">
                                            <span class="big">${currentMonth + 1}</span>
                                            <span class="detail">${currentYear}</span>
                                        </div>
                                        <a href="<c:url value="/planner/${planner.id}/${nextYear}/${nextMonth}" />">
                                            <div class="spin-btn next">
                                                <span class="icon arrow right dark"></span>
                                            </div>
                                        </a>
                                    </div>
                                </header>
                                <div class="main">
                                    <table class="calendar">
                                        <thead>
                                            <tr>
                                                <th>일</th>
                                                <th>월</th>
                                                <th>화</th>
                                                <th>수</th>
                                            </tr>
                                        </thead>
                                        <tbody class="small">
                                            <% Schedule leftCurrent = leftIterator.hasNext() ? leftIterator.next() : null; %>
                                            <c:forEach begin="0" end="5" var="row">
                                                <tr>
                                                    <c:forEach begin="1" end="4" var="col">
                                                        <c:set var="date" value="${row * 7 + col - startingWeekday + 1}" />
                                                        <td><div><c:if test="${date >= 1 && date <= lastDate}">
                                                            ${date}
                                                            <%
                                                            while (leftCurrent != null) {
                                                                long date = (long)pageContext.getAttribute("date");
                                                                int startDate = leftCurrent.getStartsAt().getDate(); // deprecated
                                                                if (startDate > date) {
                                                                    break;
                                                                }
                                                                if (startDate == date) {
                                                            %>
                                                                <br><%= leftCurrent.getTitle() %>
                                                            <%
                                                                }
                                                                leftCurrent = leftIterator.hasNext() ? leftIterator.next() : null;
                                                            }
                                                            %>
                                                        </c:if></div></td>
                                                    </c:forEach>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="right">
                        <div class="paper">
                            <div class="contents">
                                <header>
                                </header>
                                <div class="main">
                                    <table class="calendar">
                                        <thead>
                                            <tr>
                                                <th>목</th>
                                                <th>금</th>
                                                <th>토</th>
                                                <th>&nbsp;</th>
                                            </tr>
                                        </thead>
                                        <tbody class="small">
                                            <% Schedule rightCurrent = rightIterator.hasNext() ? rightIterator.next() : null; %>
                                            <c:forEach begin="0" end="5" var="row">
                                                <tr>
                                                    <c:forEach begin="5" end="7" var="col">
                                                        <c:set var="date" value="${row * 7 + col - startingWeekday + 1}" />
                                                        <td><div><c:if test="${date >= 1 && date <= lastDate}">
                                                            ${date}
                                                            <%
                                                            while (rightCurrent != null) {
                                                                long date = (long)pageContext.getAttribute("date");
                                                                int startDate = rightCurrent.getStartsAt().getDate(); // deprecated
                                                                if (startDate > date) {
                                                                    break;
                                                                }
                                                                if (startDate == date) {
                                                            %>
                                                                <br><%= rightCurrent.getTitle() %>
                                                            <%
                                                                }
                                                                rightCurrent = rightIterator.hasNext() ? rightIterator.next() : null;
                                                            }
                                                            %>
                                                        </c:if></div></td>
                                                    </c:forEach>
                                                    <td></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <div id="dropdown-container" class="dropdown-container"></div>
        <div id="modal-container" class="modal-container" style="display: none">
            <div class="fader" style="opacity: 0"></div>
            <div class="contents"></div>
        </div>
    </div>
</body>

</html>