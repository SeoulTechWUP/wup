<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="wup.data.access.*" %>
<%@ page import="wup.data.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="wup" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>post</title>
</head>
<body>
<div class="Post">
	<div class="summary">
		<fmt:formatDate value="${post.createdAt}" pattern="yyyy.MM.dd"/> &nbsp;&nbsp;
		<a href="#" data-toggle="dropdown" onclick="expandToggle(${post.id})">${post.title}</a> &nbsp;&nbsp;
		${post.owner.getNickname()} &nbsp;&nbsp;
	</div>
	<div class="expand" id="expand${post.id}" style="display:none;">
		<div id = "Media">
	    <div id="Image">
	    </div>
	    	<img alt="" src=""></img>
	    <div id="Text">
	    <p>
	    	${post.getText()}
	    </p>
	    </div>
	    
		</div>

		<div id = "Like">
			<button id = "LikeButotn">
			좋아요
			</button>
		</div>
		<div id = "Comment">
			<div id = "CommentList">
				<c:choose>
					<c:when test="${commentlist.size() eq '0'}">
						<td colspan="3"><p>댓글이 없습니다.</p></td>
					</c:when>
					<c:otherwise>
						<c:forEach items="${commentlist}" var="comment">
							<c:set var="comment" value="${comment}" scope="request"/>
								(Avatar) &nbsp;
								<a href="#" data-toggle="dropdown">${comment.getText()}</a> &nbsp;
								<fmt:formatDate value="${comment.createdAt}" pattern="yyyy.MM.dd"/> &nbsp;
								${comment.getUser()}
						</c:forEach>
					</c:otherwise>
				</c:choose>
				
			</div>
			<div id="CommentInput">
				<textarea id="ContentArea">
				</textarea>
				<button id="SubmitButton">
				댓글달기
				</button>
			</div>
		</div>
	</div>
</div>

<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">

function expandToggle(id) {
	var x = document.getElementById("expand"+ id);
	if (x.style.display === "none") {
	  x.style.display = "block";
	} else {
	  x.style.display = "none";
	}
}

$.ajaxSetup({
    type:"GET",
    async:true,
    dataType:"json",
    error:function(xhr) {
        console.log("error html = " + xhr.statusText);
    }
});

$.ajax({
    url:"${pageContext.request.contextPath}"+"/comment/"+"${post.id}",
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
				showComments(item);
            }
        });
    }
});

function showComments(item){
    var comments = JSON.parse(item);
    comments.forEach(function(value) {
       	console.log("${post.id}"+" = "+ value["text"]);
		$("#CommentList").append(value["text"]);
		$("#CommentList").append("<br>");
    });
}

</script>
</body>

</html>