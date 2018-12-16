<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="schedule" class="wup.data.Schedule" scope="request" />
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>${schedule.title} | WUP</title>
    <wup:includeAssets />
</head>

<body>
    <div id="app-main" class="app">
        <wup:appHeader />
        <main>
            <div id="planner-view">
                <form style="height: 100%">
                    <div class="border">
                        <div class="left">
                            <div class="paper">
                                <div class="contents">
                                    <header>
                                        <h2>일정 편집</h2>
                                    </header>
                                    <div class="main">
                                        <div id="schedule-info">
                                            <table>
                                                <tbody>
                                                    <tr>
                                                        <th>제목</th>
                                                        <td><input name="title" type="text"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>장소</th>
                                                        <td><input name="location" type="text"></td>
                                                    </tr>
                                                    <tr>
                                                        <th>시작 일시</th>
                                                        <td class="start-date-picker">
                                                            <span>
                                                                <input name="year" type="number" min="0" max="9999"
                                                                    style="width: 80px">
                                                                /
                                                                <input name="month" type="number" min="1" max="12"
                                                                    style="width: 60px">
                                                                /
                                                                <input name="date" type="number" min="1" max="31" style="width: 60px">
                                                                <select name="hour"></select> :
                                                                <select name="minute"></select>
                                                            </span>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th>&nbsp;</th>
                                                        <td>
                                                            <label class="cb-container">
                                                                <input name="allday" type="checkbox">
                                                                <div></div>
                                                                <span>하루 종일</span>
                                                            </label>
                                                        </td>
                                                    </tr>
                                                    <tr>
                                                        <th>종료 일시</th>
                                                        <td class="end-date-picker">
                                                            <span>
                                                                <input name="year" type="number" min="0" max="9999"
                                                                    style="width: 80px">
                                                                /
                                                                <input name="month" type="number" min="1" max="12"
                                                                    style="width: 60px">
                                                                /
                                                                <input name="date" type="number" min="1" max="31" style="width: 60px">
                                                                <select name="hour"></select> :
                                                                <select name="minute"></select>
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
                                                <textarea name="description"></textarea>
                                            </div>
                                            <div style="text-align: center">
                                                <button name="cancel" type="button">취소</button>
                                                <button name="delete" type="button" class="red">일정 삭제</button>
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
</body>

</html>