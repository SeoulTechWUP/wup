<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" rtexprvalue="true" %>
<%@ attribute name="href" rtexprvalue="true" %>
<jsp:useBean id="authenticatedUser" class="wup.data.User" scope="session" />
<header>
    <div id="app-title">
        <c:if test="${href != null}"><a href="${href}"></c:if>
        <span>${title == null ? "WUP &mdash; What's Your Plan?" : title}</span>
        <c:if test="${href != null}"></a></c:if>
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
                        location.href = `<c:url value="/processLogout" />`;
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