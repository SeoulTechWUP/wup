<%@ page import="wup.data.access.*" %>
<%@ page import="wup.data.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>공유 게시판 | WUP</title>
    <wup:includeAssets />
    <c:url var="boardCSS" value="/assets/css/board.css" />
    <link rel="stylesheet" href="${boardCSS}">
</head>

<body>
    <div id="app-main" class="app">
        <wup:appHeader />
        <main>
            <div id="planner-list">
                <header>
                    <h1>공유 게시판<a class="switch-category" href="<c:url value="/planners" />">개인</a><a class="switch-category" href="<c:url value="/groups" />">그룹</a></h1>
                </header>
                <div>
                    <div class="post-list">
                        <c:choose>
                            <c:when test="${postlist.size() eq '0'}">
                                <div class="empty">
                                    <p>게시물이 없습니다.</p>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <c:forEach items="${postlist}" var="post">
                                    <div class="Post" data-post-id="${post.id}">
                                        <div class="summary expandPost">
                                            <h3 class="title">${post.title}</h3>
                                            <p>
                                                <span class="date"><fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd" /></span><!--
                                             --><span class="author">${post.type eq 'USER' ? post.owner.nickname : post.owner.name}</span>
                                            </p>
                                        </div>
                                        <div class="expand" style="display:none;">
                                            <div class="Media">
                                            </div>
                                            <div class="Text">
                                                <div class="form-item">
                                                    <label class="header">일정</label><fmt:formatDate value="${post.getSchedule().startsAt}" pattern="yyyy.MM.dd" /> ~ <fmt:formatDate value="${post.getSchedule().endsAt}" pattern="yyyy.MM.dd" /><br>
                                                </div>
                                                <div class="form-item">
                                                    <label class="header">장소</label>${post.getSchedule().location}
                                                </div>
                                                <p>
                                                    ${post.getText()}
                                                </p>
                                            </div>
                                            <hr>
                                            <%--<div class="Like">
                                                <button id="LikeButton">
                                                    좋아요
                                                </button>
                                            </div>--%>
                                            <div class="Comment">
                                                <div class="CommentList">
                                                </div>
                                                <div class="CommentInput">
                                                    <textarea class="ContentArea" placeholder="댓글을 입력하세요."></textarea>
                                                    <button class="CommentSubmitButton" type="submit">댓글달기</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                <div id="page-control">
                    <div>
                        <a href="<c:url value="/board/${PageBlockStart - PageBlockRange - 1}" />">이전</a> &nbsp;
                        <c:forEach var="page" begin="${PageBlockStart}" end="${PageBlockStart + PageBlockRange - 1}"
                            step="1">
                            <a class="page-num" href="<c:url value="/board/${page}"/>"> <c:out value="${page}" /></a> &nbsp;
                        </c:forEach>
                        <a href="<c:url value="/board/${PageBlockStart + PageBlockRange}" />">다음</a>
                    </div>
                </div>
            </div>
        </main>
        <div id="dropdown-container" class="dropdown-container"></div>
        <div id="modal-container" class="modal-container" style="display: none">
            <div class="fader" style="opacity: 0"></div>
            <div class="contents"></div>
        </div>
    </div>

    <script src="http://code.jquery.com/jquery-latest.min.js"></script>
    <script type="text/javascript">

        let loadedList = new Array();
        var context = "${pageContext.request.contextPath}";

        window.onload = function () {
            let elements = document.getElementsByClassName("Post");

            for (let e of elements) {
                for (let el of e.getElementsByClassName("expandPost")) {
                    el.addEventListener("click", event => {
                        expandToggle(e.getAttribute("data-post-id"));
                    });
                }

                for (let el of e.getElementsByClassName("CommentSubmitButton")) {
                    el.addEventListener("click", event => {
                        createComment(e.getAttribute("data-post-id"));
                    });
                }
            }
        };

        function selectElement(id, elementName) {
            let elements = document.getElementsByClassName("Post");
            for (let e of elements) {
                if (e.getAttribute("data-post-id") == id) {
                    return e.getElementsByClassName(elementName)[0];
                }
            }
            return null;
        }

        function expandToggle(id) {
            let e = selectElement(id, "expand");
            if (e.style.display == "none") {
                if (loadedList.indexOf(id) == -1) {
                    AjaxGetComments(id);
                    AjaxGetMedia(id);
                    loadedList.push(id);
                }
                videoToggle(id);
                e.style.display = "block";
            }
            else {
                videoToggle(id);
                e.style.display = "none";
            }
        }

        function videoToggle(id) {
            let e = selectElement(id, "Media");
            for(let el of e.children){
                if(el.tagName == "VIDEO"){
                    if(el.paused) {
                        el.play();
                    } else {
                        el.pause();
                    }
                }
            }
        }
        
        function createComment(id) {
            let e = selectElement(id, "ContentArea");

            if ($(e).val() != "") {
                AjaxPostComments(id, $(e).val());
                e.value = "";
            }
            else {
                alert("댓글을 입력해 주세요.");
            }
        }

        function AjaxGetComments(id) {
            $.ajaxSetup({
                type: "GET",
                async: true,
                dataType: "json",
                error: function (xhr) {
                    console.log("error html = " + xhr.statusText);
                }
            });

            $.ajax({
                url: context + "/comment/" + id,
                beforeSend: function () {
                    console.log("읽어오기 전...");
                },
                success: function (data) {
                    $.each(data, function (idx, item) {
                        if (data["result"] == "success" && idx == "data") {
                            console.log("comment를 정상적으로 조회하였습니다.");
                            showComments(item, id);
                            return false;
                        }
                        else if (data["result"] == "fail") {
                            console.log("comment db 조회 접근 에러");
                            return false;
                        }
                    });
                }
            });
        }

        function AjaxPostComments(id, content) {
            $.ajaxSetup({
                type: "POST",
                async: true,
                dataType: "json",
                error: function (xhr) {
                    console.log("error html = " + xhr.statusText);
                }
            });

            $.ajax({
                url: context + "/comment/",
                data: {
                    content: content,
                    postnum: id
                },
                beforeSend: function () {
                    console.log("읽어오기 전...");
                },
                success: function (data) {
                    $.each(data, function (idx, item) {
                        if (data["result"] == "success" && idx == "data") {
                            console.log("comment를 정상적으로 추가하였습니다.");
                            AjaxGetComments(id);
                            return false;
                        }
                        else if (data["result"] == "userfail") {
                            alert("로그인 후에 댓글을 작성하실 수 있습니다.");
                            window.location.href = context + "/login.jsp";
                            return false;
                        }
                        else if (data["result"] == "dbfail") {
                            console.log("comment db 추가 접근 에러");
                            alert("db error : " + data["data"]);
                            return false;
                        }
                    });
                }
            });
        }

        function AjaxGetMedia(id) {
            $.ajaxSetup({
                type: "GET",
                async: true,
                dataType: "json",
                error: function (xhr) {
                    console.log("error html = " + xhr.statusText);
                }
            });

            $.ajax({
                url: context + "/media/" + id,
                beforeSend: function () {
                    console.log("읽어오기 전...");
                },
                success: function (data) {
                    $.each(data, function (idx, item) {
                        if (data["result"] == "success" && idx == "data") {
                            console.log("media를 정상적으로 조회하였습니다.");
                            showMedia(item, id);
                            return false;
                        }
                        else if (data["result"] == "fail") {
                            console.log("media db 조회 접근 에러");
                            return false;
                        }
                    });
                }
            });
        }
        
        function showMedia(item, id) {
            let media = JSON.parse(item);
            let e = selectElement(id, "Media");
            
            media.forEach(function (value) {
                if (value["type"] == "IMAGE") {
                    let img = document.createElement("img");
                    img.src = context + value["path"];
                    $(e).append(img);
                } else {
                    let video = document.createElement("video");
                    video.src = context + value["path"];
                    video.muted = true;
                    video.autoplay = true;
                    video.loop = true;
                    $(video).hover(function(){video.setAttribute("controls","controls");},
                        function(){video.removeAttribute("controls");});
                    $(e).append(video);
                }
            });
        }
        
        function showComments(item, id) {
            let comments = JSON.parse(item);
            let e = selectElement(id, "CommentList");

            e.innerHTML = "";

            comments.forEach(function (value) {
                e.innerHTML +=
                    `<div class="comment-item">
                        <span class="author">\${value["user"].nickname}</span><span class="date">\${value["createdAt"]}</span>
                        <p>\${value["text"]}</p>
                    </div>`;
            });
        }

    </script>
</body>

</html>