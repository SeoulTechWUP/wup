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
		<a href="#" onclick="expandToggle(${post.id})">${post.title}</a> &nbsp;&nbsp;
		${post.owner.getNickname()} &nbsp;&nbsp;
	</div>
	<div class="expand" id="expand-${post.id}" style="display:none;">
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
		<div class = "Comment">
			<div id = "CommentList-${post.id}">
				
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

var loadedList = new Array();

$.ajaxSetup({
    type:"GET",
    async:true,
    dataType:"json",
    error:function(xhr) {
        console.log("error html = " + xhr.statusText);
    }
});

function expandToggle(id) {
	var x = document.getElementById("expand-"+ id);
	if (x.style.display === "none") {
		
		if(loadedList.indexOf(id) == -1) {
			requestAjax(id);
			loadedList.push(id);
		}
		
		x.style.display = "block";
	} 
	else {
		x.style.display = "none";
	}
}

function requestAjax(id) {
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

function showComments(item, id){
    var comments = JSON.parse(item);
    if(comments.length < 1) {
		$("#CommentList-" + id).append("댓글이 없습니다.");
		$("#CommentList-" + id).append("<br>");
    }
    else {
        comments.forEach(function(value) {
           	console.log(id + " = "+ value["text"]);
    		$("#CommentList-" + id).append(value["text"]);
    		$("#CommentList-" + id).append("<br>");
        });
    }
}

</script>
</body>

</html>