<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>WUP!</title>
</head>
<body>
	<div class="titleinput">
		<label>제목 : </label>
		<input id="title" type="text" value="${schedule.title}">
	</div>
	<div class="scheduleinfo">
		<label>일정 : </label>${schedule.startsAt} ~ ${schedule.endsAt} <br>
		<label>장소 : </label>${schedule.location}
	</div>
	<div class="media">
	</div>
	<textarea id="content">${schedule.description}</textarea>
	<div class="fileinput">
		<input id="image-file" type="file"/>
		<input id="video-file" type="file"/>
	</div>
	<div class="button">
		<input id="submit" type="button" value="게시하기">
		<input id="abort" type="button" value="취소">
	</div>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">

var context = "${pageContext.request.contextPath}";

window.onload = function () {
	document.getElementById("submit").addEventListener("click", event => {
		submit();
	});
	
	document.getElementById("abort").addEventListener("click", event => {
		abort();
	});
};

function AjaxMediaUpload(type) {
	$.ajaxSetup({
	    type:"POST",
	    async:true,
	    dataType:"json",
	    error:function(xhr) {
	        console.log("error html = " + xhr.statusText);
	    }
	});
	
	$.ajax({
	    url:context + "/media/" + type,
	    enctype: "multipart/form-data",
	    complete:function() {
	        console.log("읽어오기 완료 후...");
	    },
	    success:function(data) {
	    	$.each(data, function(idx, item){
	            if(data["result"] == "success" && idx == "data") {
	            	console.log("comment를 정상적으로 추가하였습니다.");
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

function AjaxPostRequest(title, content) {
	$.ajaxSetup({
	    type:"POST",
	    async:true,
	    dataType:"json",
	    error:function(xhr) {
	        console.log("error html = " + xhr.statusText);
	    }
	});
	
	$.ajax({
	    url:context + "/postwrite/",
	    data:{
	    	scheduleid: "${schedule.getId()}",
	    	ownerid:"${schedule.getPlanner().getOwner().getId()}",
	    	ownertype:"${schedule.getPlanner().getType()}",
	    	title: title,
	    	content: content
	    },
	    complete:function() {
	        console.log("읽어오기 완료 후...");
	    },
	    success:function(data) {
	    	$.each(data, function(idx, item){
	            if(data["result"] == "success") {
	            	alert("정상적으로 게시하였습니다.");
	            	window.location.href = context + "/board/1";
	            	return false;
	            }
	            else if(data["result"] == "userfail"){
	            	alert("로그인 세션이 만료되었습니다.");
	            	window.location.href = context + "/login.jsp";
	            	return false;
	            }
	            else if(data["result"] == "dbfail"){
	            	console.log("post db 추가 접근 에러");
	            	alert("db error : " + data["data"]);
	            	return false;
	            }
	        });
	    }
	});
}

function upload(type) {
	
}

function submit() {
	if(confirm("글을 게시 하시겠습니까?")){
		if($("#content").val() == ""){
		    alert("내용을 입력해 주세요.");
		    $("#content").focus();
		} 
		else if($("#title").val() == "") {
		    alert("제목을 입력해 주세요.");
		    $("#title").focus();
		}
		else {
			AjaxPostRequest($("#title").val(), $("#content").val());
		}
	}
}

function abort() {
	if(confirm("글쓰기를 취소 하시겠습니까?")){
		history.go(-1);
	}
}
</script>
</body>
</html>