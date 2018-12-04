<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>그룹 플래너 | WUP</title>
    <wup:includeAssets />
</head>

<body>
    <div id="app-main" class="app">
        <wup:appHeader />
        <main>
            <div id="planner-list">
                <header>
                    <h1>그룹 플래너<a class="switch-category" href="<c:url value="/planners" />">개인</a><a class="switch-category" href="<c:url value="/board" />">공개</a></h1>
                </header>
                <div class="list">
                    <c:forEach items="${requestScope.groups}" var="group">
                        <a href="<c:url value="/group/${group.id}" />"><div class="item">${group.name}</div></a>
                    </c:forEach>
                    <div id="add-new-button" class="add-new">
                        <div>
                            <img src="<c:url value="/assets/images/icon_addplanner.svg" />"><br>
                            새로 만들기
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
        <div id="ui-templates" style="display: none">
        </div>
    </div>
</body>

</html>