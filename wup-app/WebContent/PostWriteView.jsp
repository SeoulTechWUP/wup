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
	<div class="titleInput">
		<label>제목 : </label>
		<input id="title" type="text" value="${schedule.title}">
	</div>
	<div class="scheduleInfo">
		<label>일정 : </label>${schedule.startsAt} ~ ${schedule.endsAt} <br>
		<label>장소 : </label>${schedule.location}
	</div>
	<div class="media" style="width:50%;overflow:auto;overflow-y:hidden;white-space: nowrap;">
	</div>
	<div class="textContent">
		<textarea id="content">${schedule.description}</textarea>
	</div>
	<div class="fileInput">
		<input id="imageBtn" type="button" value="사진 추가">
		<input id="videoBtn" type="button" value="영상 추가">
		<input id="image-file" multiple="multiple" type="file" accept="image/*" style="display:none;"/>
		<input id="video-file" multiple="multiple" type="file" accept="video/*" style="display:none;"/>
	</div>
	<div class="button">
		<input id="submit" type="button" value="게시하기">
		<input id="abort" type="button" value="취소">
	</div>
<script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script type="text/javascript">

var context = "${pageContext.request.contextPath}";
var imageFiles = [];
var videoFiles = [];

window.onload = function () {
	document.getElementById("imageBtn").addEventListener("click", event => {
		document.getElementById("image-file").click();
	});
	
	document.getElementById("videoBtn").addEventListener("click", event => {
		document.getElementById("video-file").click();
	});
	
	document.getElementById("image-file").addEventListener("change", event => {
		upload(document.getElementById("image-file"),"image");
	});
	
	document.getElementById("video-file").addEventListener("change", event => {
		upload(document.getElementById("video-file"),"video");
	});
	
	document.getElementById("submit").addEventListener("click", event => {
		submit();
	});
	
	document.getElementById("abort").addEventListener("click", event => {
		abort();
	});
};

function AjaxMediaUpload(id, type) {
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
	            	console.log(type + "를 정상적으로 추가하였습니다.");
	            	return true;
	            }
	            else if(data["result"] == "dbfail"){
	            	console.log("media db 추가 접근 에러");
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
	            if(data["result"] == "success" && idx == "data") {
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

function upload(input,type) {
    if (input.files && input.files[0]) {
    	for(var i = 0; i < input.files.length; i++) {
	        var reader = new FileReader();
	 		
	        reader.addEventListener("load", event => {
	        	var button = document.createElement("input");
	        	button.type = "button";
	        	button.id = "cancel";
	        	button.value = "x";
	        	
	        	var string = "<div class=\"mediaElement\" style=\"display:inline-block;\" >";

	        	if(type == "image") {
	        		string += "<img width=300px src=\" " + event.target.result + " \"></img>";
	        	} else {
	        		string += "<video width=300px src=\" " + event.target.result + " \" autoplay muted loop></video>";
	        	}
	            $('.media').append(string);
	            
	            $('.media div:last-child').append(button);
	            
	        	button.addEventListener("click", e => {
	        		$(e.target).closest("div").remove();
	        	});
	        });
	        reader.readAsDataURL(input.files[i]);
    	}
    }
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