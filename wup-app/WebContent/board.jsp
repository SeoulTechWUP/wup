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
							<div class="Post">
								<div class="summary">
									<fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd"/> &nbsp;&nbsp;
									<a href="#" onclick="expandToggle(${post.id})">${post.title}</a> &nbsp;&nbsp;
									${post.owner.getNickname()} &nbsp;&nbsp;
								</div>
								<div class="expand" id="expand-${post.id}" style="display:none;">
									<div id = "Media">
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
										<div id = "CommentList-${post.id}">
										</div>
										<div id="CommentInput">
											<textarea id="ContentArea" placeholder="댓글을 입력하세요."></textarea>
											<button id="CommentSubmitButton" type="submit" onclick="createComment(${post.id})">댓글달기</button>
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

var loadedList = new Array();

function expandToggle(id) {
	var x = document.getElementById("expand-"+ id);
	if (x.style.display === "none") {
		if(loadedList.indexOf(id) == -1) {
			requestGetAjax(id);
			loadedList.push(id);
		}
		x.style.display = "block";
	} 
	else {
		x.style.display = "none";
	}
}

function createComment(id){
	console.log($("#ContentArea").val());
	if($("#ContentArea").val() != "") {
		console.log("안빔");
	}
	else {
		console.log("빔");
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
	    url:"${pageContext.request.contextPath}"+"/comment/" + id,
	    beforeSend:function() {
	        console.log("읽어오기 시작 전...");
	    },
	    complete:function() {
	        console.log("읽어오기 완료 후...");
	    },
	    success:function(data) {
	        console.log("comment를 정상적으로 조회하였습니다.");
	        $.each(data, function(idx, item){
	            if(data["result"] == "success" && idx == "data") {
	            	showComments(item, id);
	            }
	        });
	    }
	});
}

function requestPostAjax(id) {
	var post = "${post}";
	$.ajaxSetup({
	    type:"POST",
	    async:true,
	    dataType:"json",
	    error:function(xhr) {
	        console.log("error html = " + xhr.statusText);
	    }
	});
	
	$.ajax({
	    url:"${pageContext.request.contextPath}"+"/comment/",
	    data:{
	    	content:$("#ContentArea").val(),
	    	post:post
	    },
	    beforeSend:function() {
	        console.log("읽어오기 시작 전...");
	    },
	    complete:function() {
	        console.log("읽어오기 완료 후...");
	    },
	    success:function(data) {
	        console.log("comment를 정상적으로 추가하였습니다.");
			
	    }
	});
}

function showComments(item, id){
    var comments = JSON.parse(item);
    if(comments.length < 1) {
		$("#CommentList-" + id).append("댓글이 없습니다.");
		$("#CommentList-" + id).append("<br>");
    }
    else {
        comments.forEach(function(value) {
        	$("#CommentList-" + id).append(value["createdAt"]);
        	$("#CommentList-" + id).append("&nbsp;&nbsp;");
        	$("#CommentList-" + id).append(value["user"].nickname);
        	$("#CommentList-" + id).append("&nbsp;&nbsp;");
    		$("#CommentList-" + id).append(value["text"]);
    		$("#CommentList-" + id).append("<br>");
        });
    }
}

</script>
</body>
</html>