<%@ tag language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" rtexprvalue="true" %>
<%@ attribute name="href" rtexprvalue="true" %>
<jsp:useBean id="authenticatedUser" class="wup.data.User" scope="session" />
<c:choose>
    <c:when test="${title == null}">
        <c:set var="titleText" value="WUP &mdash; What's Your Plan?" />
    </c:when>
    <c:otherwise>
        <c:set var="titleText" value="${title}" />
    </c:otherwise>
</c:choose>
<header>
    <div id="app-title">
        <c:choose>
            <c:when test="${href != null}">
                <a href="${href}"><span><span class="icon arrow left"></span>&nbsp; ${titleText}</span></a>
            </c:when>
            <c:otherwise>
                <span>${titleText}</span>
            </c:otherwise>
        </c:choose>
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