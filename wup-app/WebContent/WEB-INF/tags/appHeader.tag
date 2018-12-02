<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="authenticatedUser" class="wup.data.User" scope="session" />
<header>
    <div id="app-title">
        <span>WUP &mdash; What's Your Plan?</span>
    </div>
    <div>
        <c:choose>
            <c:when test="${authenticatedUser.email == null}">
                <span id="user-settings">로그인 해주세요</span>
            </c:when>
            <c:otherwise>
                <span id="user-settings" class="active">${authenticatedUser.fullName}</span>
                <script>
                (function() {
                    let userSettingsMenu = new DropdownMenu("사용자 설정", "로그아웃");

                    userSettingsMenu.itemClicked(1, e => {
                        alert("bye");
                    });

                    document.getElementById("user-settings").addEventListener("click", e => {
                        dropdownManager.show(2147483647, 48, userSettingsMenu);
                        e.stopPropagation();
                    }, false);
                })();
                </script>
            </c:otherwise>
        </c:choose>
    </div>
</header>