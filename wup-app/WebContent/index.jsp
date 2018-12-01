<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="wup.data.User" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<jsp:useBean id="authenticatedUser" class="wup.data.User" scope="session" />
<c:if test="${authenticatedUser.email == null}">
    <% response.sendRedirect(request.getContextPath() + "/login.jsp"); %>
</c:if>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>WUP</title>
    <wup:includeAssets />
</head>

<body>
    <div id="app-main" class="app">
        <wup:appHeader />
        <main>
            <div id="planner-list">
                <header>
                    <h1>내 플래너</h1>
                </header>
                <div class="list">
                    <div class="add-new">
                        <div>
                            <img src="<c:url value="/assets/images/icon_addplanner.svg" />"><br>
                            새로 만들기
                        </div>
                    </div>
                </div>
            </div>
        </main>
        <div id="modal-container" class="modal-container" style="display: none">
            <div class="fader" style="opacity: 0"></div>
            <div class="contents"></div>
        </div>
        <div id="ui-templates" style="display: none">
            <div id="modal-new-planner" class="modal">
                <div class="top-tabs">
                    <div class="tab active">New Planner</div>
                </div>
                <div class="contents">
                    <form>
                        <div class="error-message"></div>
                        <div class="form-item">
                            <input name="title" type="text" placeholder="Title (required)" size="40" required>
                        </div>
                        <div class="form-item" style="text-align: right">
                            <button name="cancel" type="button">Cancel</button>
                            <input name="submit" class="tinted" type="submit" value="Create">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>

</html>