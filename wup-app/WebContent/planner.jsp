<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="planner" class="wup.data.Planner" scope="request" />
<c:choose>
    <c:when test="${requestScope.currentMonth == 0}">
        <c:set var="prevMonth" value="12" />
        <c:set var="prevYear" value="${requestScope.currentYear - 1}" />
    </c:when>
    <c:otherwise>
        <c:set var="prevMonth" value="${requestScope.currentMonth}" />
        <c:set var="prevYear" value="${requestScope.currentYear}" />
    </c:otherwise>
</c:choose>
<c:choose>
    <c:when test="${requestScope.currentMonth == 11}">
        <c:set var="nextMonth" value="1" />
        <c:set var="nextYear" value="${requestScope.currentYear + 1}" />
    </c:when>
    <c:otherwise>
        <c:set var="nextMonth" value="${requestScope.currentMonth + 2}" />
        <c:set var="nextYear" value="${requestScope.currentYear}" />
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
        <wup:appHeader title="${planner.title}" href="#" />
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