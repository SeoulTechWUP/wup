<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="schedule" class="wup.data.Schedule" scope="request" />
<jsp:useBean id="now" class="java.util.Date" />
<c:set var="startsAt" value="${schedule.startsAt == null ? now : schedule.startsAt}" />
<c:set var="endsAt" value="${schedule.endsAt == null ? now : schedule.endsAt}" />
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>${mode eq 'new' ? '새로운 일정 추가' : schedule.title} | WUP</title>
    <wup:includeAssets />
</head>

<body>
    <div id="app-main" class="app">
        <wup:appHeader />
        <main>
            <div id="planner-view">
                <c:choose>
                    <c:when test="${mode eq 'new'}">
                        <c:url var="formAction" value="/newSchedule" />
                        <form method="post" action="${formAction}" style="height: 100%">
                    </c:when>
                    <c:otherwise>
                        <form method="post" style="height: 100%">
                    </c:otherwise>
                </c:choose>
                    <input type="hidden" name="plannerId" value="${plannerId}">
                    <div class="border">
                        <div class="left">
                            <div class="paper">
                                <div class="contents">
                                    <header>
                                        <h2>${mode eq 'new' ? '새로운 일정 추가' : '일정 보기 및 편집'}</h2>
                                    </header>
                                    <div class="main">
                                        <div id="schedule-info">
                                            <table>
                                                <tbody>
                                                    <tr>
                                                        <th>제목</th>
                                                        <td><input name="title" type="text" value="${schedule.title}" required></td>
                                                    </tr>
                                                    <tr>
                                                        <th>장소</th>
                                                        <td><input name="location" type="text" value="${schedule.location}"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>시작 일시</th>
                                                        <td class="start-date-picker">
                                                            <span>
                                                                <input name="start_year" type="number" min="0" max="9999" value="${startsAt.year + 1900}" required style="width: 80px">
                                                                /
                                                                <input name="start_month" type="number" min="1" max="12" value="${startsAt.month + 1}" required style="width: 60px">
                                                                /
                                                                <input name="start_date" type="number" min="1" max="31" value="${startsAt.date}" required style="width: 60px">
                                                                <input name="start_hour" type="number" min="0" max="23" value="${startsAt.hours}" required style="width:60px">
                                                                :
                                                                <input name="start_minute" type="number" min="0" max="59" value="${startsAt.minutes}" required style="width:60px">
                                                            </span>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th>&nbsp;</th>
                                                        <td>
                                                            <label class="cb-container">
                                                                <input name="allday" type="checkbox" ${schedule.allDay ? 'checked' : ''}>
                                                                <div></div>
                                                                <span>하루 종일</span>
                                                            </label>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th>종료 일시</th>
                                                        <td class="end-date-picker">
                                                            <span>
                                                                <input name="end_year" type="number" min="0" max="9999" value="${endsAt.year + 1900}" required style="width: 80px">
                                                                /
                                                                <input name="end_month" type="number" min="1" max="12" value="${endsAt.month + 1}" required style="width: 60px">
                                                                /
                                                                <input name="end_date" type="number" min="1" max="31" value="${endsAt.date}" required style="width: 60px">
                                                                <input name="end_hour" type="number" min="0" max="23" value="${endsAt.hours}" required style="width:60px">
                                                                :
                                                                <input name="end_minute" type="number" min="0" max="59" value="${endsAt.minutes}" required style="width:60px">
                                                            </span>
                                                        </td>
                                                    </tr>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="right">
                            <div class="paper">
                                <div class="contents">
                                    <header>
                                        <h2>&nbsp;</h2>
                                    </header>
                                    <div class="main">
                                        <div id="schedule-description">
                                            <div class="textarea">
                                                <textarea name="description">${schedule.description}</textarea>
                                            </div>
                                            <div style="text-align: center">
                                                <button id="cancelButton" type="button">취소</button>
                                                <c:if test="${mode eq 'edit'}">
                                                    <button name="delete" type="button" class="red">일정 삭제</button>
                                                </c:if>
                                                <input type="submit" class="tinted" value="저장"></button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </main>
        <div id="dropdown-container" class="dropdown-container"></div>
        <div id="modal-container" class="modal-container" style="display: none">
            <div class="fader" style="opacity: 0"></div>
            <div class="contents"></div>
        </div>
    </div>
    <script>
    (function () {
        let cancelButton = document.getElementById("cancelButton");

        cancelButton.addEventListener("click", e => {
            history.go(-1);
        }, false);
    })();
    </script>
</body>

</html>