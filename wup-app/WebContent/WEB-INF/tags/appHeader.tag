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
                <span id="user-settings">${authenticatedUser.fullName}</span>
            </c:otherwise>
        </c:choose>
    </div>
</header>