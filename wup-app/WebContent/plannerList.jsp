<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>내 플래너 | WUP</title>
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
                    <c:forEach items="${requestScope.planners}" var="planner">
                        <a href="<c:url value="/planner/${planner.id}" />"><div class="item">${planner.title}</div></a>
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
            <div id="modal-new-planner" class="modal">
                <div class="top-tabs">
                    <div class="tab active">새 플래너 만들기</div>
                </div>
                <div class="contents">
                    <form>
                        <div class="error-message"></div>
                        <div class="form-item">
                            <input name="title" type="text" placeholder="제목 (필수 사항)" size="40" required>
                        </div>
                        <div class="form-item" style="text-align: right">
                            <button name="cancel" type="button">취소</button>
                            <input name="submit" class="tinted" type="submit" value="만들기">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <script>
    (function () {
        let addNewButton = document.getElementById("add-new-button");
        let modalElement = document.getElementById("modal-new-planner");
        let modalForm = modalElement.getElementsByTagName("form")[0];

        modalForm.cancel.addEventListener("click", e => {
            modalManager.end();
        }, false);

        modalForm.addEventListener("submit", e => {
            alert("submit");
            e.preventDefault();
        }, false);

        addNewButton.addEventListener("click", e => {
            modalManager.start({element: modalElement});
        }, false);
    })();
    </script>
</body>

</html>