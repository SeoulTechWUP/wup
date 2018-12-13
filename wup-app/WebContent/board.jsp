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
<title>WUP! - 공유 게시판</title>
</head>
<body>	 
	<div>
		<h1>공유 게시판</h1>
	</div>
	<div>
		<table>
			<tr>
				<td>작성일</td>
				<td>제목</td>
				<td>작성자</td>
			</tr>
			<c:choose>
				<c:when test="${postlist.size() eq '0'}">
				<tr>
					<td colspan="3"><p>게시물이 없습니다.</p></td>
				</tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${postlist}" var="post">
						<tr>
							<td colspan="3">
							<div class="Post" data-post-id="${post.id}">
								<div class="summary">
									<fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd"/> &nbsp;&nbsp;
									<a class="expandPost" href="#">${post.title}</a> &nbsp;&nbsp;
									${post.owner.getNickname()} &nbsp;&nbsp;
								</div>
								<div class="expand" style="display:none;">
									<div id="Media">
									    <div id="Image">
									    	<img alt="" src=""></img>
									    </div>
									    <div id="Text">
									    <p>
									    	${post.getText()}
									    </p>
									    </div>
									</div>
							
									<div id = "Like">
										<button id = "LikeButton">
										좋아요
										</button>
									</div>
									<div class = "Comment">
										<div class="CommentList">
										</div>
										<div class="CommentInput">
											<textarea class="ContentArea" placeholder="댓글을 입력하세요."></textarea>
											<button class="CommentSubmitButton" type="submit">댓글달기</button>
										</div>
									</div>
								</div>
							</div>
							</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
			<tr>
				<td colspan="3">
					<a href="<c:url value="/board/${PageBlockStart - PageBlockRange - 1}"/>">이전</a> &nbsp;
					<c:forEach var="page" begin="${PageBlockStart}" end="${PageBlockStart + PageBlockRange - 1}" step="1">
						<a href="<c:url value="/board/${page}"/>"><c:out value="${page}"/></a> &nbsp;
					</c:forEach>
					<a href="<c:url value="/board/${PageBlockStart + PageBlockRange}"/>">다음</a>
				</td>
			</tr>
		</table>
	</div>
	
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">
//
//
//

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
		if(e.getAttribute("data-post-id") == id) {
			return e.getElementsByClassName(elementName)[0];
		}
	}
	return null;
}

function expandToggle(id) {
	let e = selectElement(id, "expand");
	if (e.style.display == "none") {
		if(loadedList.indexOf(id) == -1) {
			requestGetAjax(id);
			loadedList.push(id);
		}
		e.style.display = "block";
	} 
	else {
		e.style.display = "none";
	}
}

function createComment(id){
	let e = selectElement(id, "ContentArea");

	if($(e).val() != "") {
		requestPostAjax(id, $(e).val());
		e.value = "";
	}
	else {
		alert("댓글을 입력해 주세요.");
	}
}

function requestGetAjax(id) {
	$.ajaxSetup({
	    type:"GET",
	    async:true,
	    dataType:"json",
	    error:function(xhr) {
	        console.log("error html = " + xhr.statusText);
	    }
	});
	
	$.ajax({
	    url:context + "/comment/" + id,
	    complete:function() {
	        console.log("읽어오기 완료 후...");
	    },
	    success:function(data) {
	        $.each(data, function(idx, item){
	            if(data["result"] == "success" && idx == "data") {
	            	console.log("comment를 정상적으로 조회하였습니다.");
	            	showComments(item, id);
	            	return false;
	            }
	            else if(data["result"] == "fail"){
	            	console.log("comment db 조회 접근 에러");
	            	return false;
	            }
	        });
	    }
	});
}

function requestPostAjax(id, content) {
	$.ajaxSetup({
	    type:"POST",
	    async:true,
	    dataType:"json",
	    error:function(xhr) {
	        console.log("error html = " + xhr.statusText);
	    }
	});
	
	$.ajax({
	    url:context + "/comment/",
	    data:{
	    	content:content,
	    	postnum:id
	    },
	    complete:function() {
	        console.log("읽어오기 완료 후...");
	    },
	    success:function(data) {
	    	$.each(data, function(idx, item){
	            if(data["result"] == "success" && idx == "data") {
	            	console.log("comment를 정상적으로 추가하였습니다.");
	            	requestGetAjax(id);
	            	return false;
	            }
	            else if(data["result"] == "userfail"){
	            	alert("로그인 후에 댓글을 작성하실 수 있습니다.");
	            	window.location.href = context + "/login.jsp";
	            	return false;
	            }
	            else if(data["result"] == "dbfail"){
	            	console.log("comment db 추가 접근 에러");
	            	alert("db error : " + data["data"]);
	            	return false;
	            }
	        });
	    }
	});
}

function showComments(item, id){
    var comments = JSON.parse(item);
    let e = selectElement(id, "CommentList");
    $(e).empty();
    
 	if(comments.length < 1) {
		$(e).append("댓글이 없습니다.");
		$(e).append("<br>");
    }
    else {
        comments.forEach(function(value) {
        	$(e).append(value["createdAt"]);
        	$(e).append("&nbsp;&nbsp;");
        	$(e).append(value["user"].nickname);
        	$(e).append("&nbsp;&nbsp;");
    		$(e).append(value["text"]);
    		$(e).append("<br>");
        });
    }
}

</script>
</body>
</html>
