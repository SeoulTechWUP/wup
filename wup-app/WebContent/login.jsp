<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>로그인 | WUP</title>
        <link rel="stylesheet" href="<c:url value="/assets/css/ui.css" />">
        <link rel="stylesheet" href="<c:url value="/assets/css/layout.css" />">
    </head>
<body>
    <div id="app-main" class="app">
        <header>
            <div id="app-title">
                <span>WUP &mdash; What's Your Plan?</span>
            </div>
            <div>
                <span id="user-settings">로그인 해주세요</span>
            </div>
        </header>
        <main>
            <div class="modal-container">
                <div class="contents">
                    <div id="signin-form" class="modal">
                        <div class="top-tabs">
                            <div class="tab active">로그인</div>
                            <div class="tab">사용자 등록</div>
                        </div>
                        <div class="contents">
                            <div class="error-message">테스트</div>
                            <form method="post">
                                <div class="form-item">
                                    <input type="text" name="email" placeholder="이메일 주소">
                                </div>
                                <div class="form-item">
                                    <input type="password" name="password" placeholder="암호">
                                </div>
                                <div class="form-item">
                                    <input type="submit" value="로그인" class="tinted">
                                </div>
                            </form>
                            <form method="post" style="display: none">
                                <div class="form-item">
                                    <input type="text" name="email" placeholder="이메일 주소">
                                </div>
                                <div class="form-item">
                                    <input type="text" name="fullName" placeholder="이름">
                                </div>
                                <div class="form-item">
                                    <input type="text" name="nickname" placeholder="별명">
                                </div>
                                <div class="form-item">
                                    <input type="password" name="password" placeholder="암호">
                                </div>
                                <div class="form-item">
                                    <input type="password" name="passwordConfirm" placeholder="암호 확인">
                                </div>
                                <div class="form-item">
                                    <input type="submit" value="등록" class="tinted">
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
</body>
</html>