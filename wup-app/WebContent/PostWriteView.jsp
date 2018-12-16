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

let context = "${pageContext.request.contextPath}";
let imageFiles = [];
let videoFiles = [];
let postid;
let imageSuccess = false;
let videoSuccess = false;
let success = false;

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

function MakeFormdata(type) {
	let filedata = new FormData();
	let nameArr = [];
	
    let media = document.getElementsByClassName("mediaElement");
	
    if(media == null || media.length == 0) {
    	return null;
    }
    
	if(type == "image") {
		for(let e of media) {
			for(let c of e.childNodes) {
				if(c.nodeName == "IMG") {
					nameArr.push(c.getAttribute("data-name"));
				}
			}
		}
		if(nameArr.length == 0) {return null;}
		for(let i = 0; i<imageFiles.length; i++) {
			if(nameArr.indexOf(imageFiles[i].name) != -1){
				filedata.append("file",imageFiles[i]);
			}
		}
	} else {
		for(let e of media) {
			for(let c of e.childNodes) {
				if(c.nodeName == "VIDEO") {
					nameArr.push(c.getAttribute("data-name"));
				}
			}
		}
		if(nameArr.length == 0) {return null;}
		for(let i = 0; i<videoFiles.length; i++) {
			if(nameArr.indexOf(videoFiles[i].name) != -1){
				filedata.append("file",videoFiles[i]);
			}
		}
	}
	
	return filedata;
}

function AjaxMediaUpload(id, type) {
	
	let filedata = MakeFormdata(type);
	
	if(filedata == null) {return false;}
	
	$.ajax({
	    url:context + "/media/" + id + "/" + type,
	    type:"POST",
	    contentType:false,
	    processData:false,
	    data:filedata,
	    beforeSend:function(){
	    	//console.log(filedata);
	    },
	    success:function(data) {
	    	$.each(data, function(idx, item){
	            if(data["result"] == "success" && idx == "data") {
	            	if(type == "image") {
	            		imageSuccess = true;
	            	} else {
	            		videoSuccess = true;
	            	}
	            	console.log(type + "를 정상적으로 추가하였습니다.");
	            	return false;
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
	    async:false,
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
	    success:function(data) {
	    	$.each(data, function(idx, item){
	            if(data["result"] == "success" && idx == "data") {
	            	console.log("post를 정상적으로 게시하였습니다.");
	            	let post = JSON.parse(item);
	            	postid = post["id"];
	            	
					if (MakeFormdata("image") != null) {
						AjaxMediaUpload(postid, "image");
						if(!imageSuccess){
							alert("사진 업로드에 실패하였습니다.");
							deletePost(postid);
							return false;
						}
					}
					
					if (MakeFormdata("video") != null) {
						AjaxMediaUpload(postid, "video");
						if(!videoSuccess){
							alert("영상 업로드에 실패하였습니다.");
							deletePost(postid);
							return false;
						}
					}
	            	success = true;
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

function deletePost(id) {
	$.ajaxSetup({
	    type:"POST",
	    async:true,
	    dataType:"json",
	    error:function(xhr) {
	        console.log("error html = " + xhr.statusText);
	    }
	});
	
	$.ajax({
	    url:context + "/postdelete/",
	    data:{id:id},
	    success:function(data) {
            $.each(data, function (idx, item) {
                if (data["result"] == "success" && idx == "data") {
					console.log("post를 정상적으로 삭제했습니다.");
                    return false;
                }
                else if (data["result"] == "fail") {
                    console.log("post db 삭제 접근 에러");
                    alert("db error : " + data["data"]);
                    return false;
                }
            });
	    }
	});
}

function upload(input,type) {
    if (input.files && input.files[0]) {
    	for(let i = 0; i < input.files.length; i++) {
	        let reader = new FileReader();
	        reader.addEventListener("load", event => {
	        	let mediaDiv = document.createElement("div");
	        	mediaDiv.className = "mediaElement";
	        	mediaDiv.style = "display:inline-block;";
	        	
	        	let button = document.createElement("input");
	        	button.type = "button";
	        	button.id = "cancel";
	        	button.value = "x";
	        	
	        	let img = document.createElement("img");
	        	let video = document.createElement("video");
	        	
	        	if(type == "image") {
	        		imageFiles.push(input.files[i]);
	        		img.width = "300";
	        		img.src = event.target.result;
	        		img.setAttribute("data-name", input.files[i].name);
	        		mediaDiv.appendChild(img);
	        	} else {
	        		videoFiles.push(input.files[i]);
	        		video.width = "300";
	        		video.src = event.target.result;
	        		video.muted = true;
	        		video.autoplay = true;
	        		video.loop = true;
	        		video.setAttribute("data-name", input.files[i].name);
	        		mediaDiv.appendChild(video);
	        	}

	            $('.media').append(mediaDiv);
	            
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
			if (success){
				alert("포스트 작성을 완료하였습니다.");
				window.location.href = context + "/board/1";
			}
			else {
				alert("포스트 작성에 실패했습니다.");
			}
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