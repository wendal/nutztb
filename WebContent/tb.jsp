<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>推爸主页</title>
<script type="text/javascript" src="${base}/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="${base}/js/jquery.json-2.3.js"></script>
<script type="text/javascript" src="${base}/js/ejs.js"></script>
<script type="text/javascript" src="${base}/js/view.js"></script>
<script type="text/javascript">
$(function () {
	var loc = window.location.href;
	var uid = "index";
	if (loc.indexOf("#") > 0 && (loc.indexOf("#") < (loc.length - 6))) {
		uid = loc.substring(loc.indexOf('#') + 1);
	}
	$.get("${base}/user/me", null, function (resp){
		if (resp.data) {
			if (uid == "index") {
				uid = "me";
			} else if (uid == resp.data.id) {
				uid = "me";
			}
		} else {
			console.log("Not login yet, use default index");
		}
		console.log("UID=" + uid);
		loadData(uid);
	}, "json");
	
	$("#unfollow_me").click(function(){
		if (uid != "me") {
			$.get("${base}/unfollow/"+uid, null, function(resp){
				$("#follow_me").removeAttr("disable");
				$("#unfollow_me").attr("disable","disable");
			}, "json");
		}
	});
	

	$("#follow_me").click(function(){
		if (uid != "me") {
			$.get("${base}/follow/"+uid, null, function(resp){
				$("#unfollow_me").removeAttr("disable");
				$("#follow_me").attr("disable","disable");
			}, "json");
		}
	});
});

function loadData(uid) {
	$.get("${base}/u/"+uid, null, function (resp) {
		if (! resp.ok) {
			alert("Fail to Load data!!" + resp.msg);
			return;
		}
		//把用户信息读取一下
		if (resp.data.user) {
			var user = resp.data.user;
			$("#user_nickName").html(user.nickName);
			$("#user_following").html(user.followingCount);
			$("#user_followed").html(user.followedCount);
			if (uid != "me") {
				$.post("${base}/query/follow", "uid="+uid, function (resp) {
					if (resp.ok ) {
						if (resp.data) {
							$("#unfollow_me").removeAttr("disable");
						} else {
							$("#follow_me").removeAttr("disable");
						}
					}
				}, "json");
			}
		}
		if (resp.data.data) {
			renderData(resp.data.data);
		}
	}, "json");
}

function renderData(data) {
	var timeline = $("#timeline_div");
	timeline.html("");
	for (tw in data) {
		var html = new EJS({url: '${base}/ejs/tweet.ejs'}).render(tw);
		timeline.append(html);
		timeline.append("<p/>");
	}
}

function toUser(uid) {
	var loc = window.location.href;
	window.location = loc.substring(0, loc.indexOf("#"));
}

function retweet(id) {
	
}

function unretweet(id) {
	
}
</script>
</head>
<body>
<div id="user_div">
	用户昵称:<b id="user_nickName">推爸</b><p/>
	following:<a id="user_following" href="#">0</a><p/>
	followed: <a id="user_followed"  href="#">0</a><p/>
	
	<button id="follow_me" disabled="disabled">follow!</button>
	<button id="unfollow_me" disabled="disabled">unfollow!</button>
</div>

<div id="tweet_div">
	<form id="tweetForm">
		<textarea></textarea><p/>
		<input type="submit" value="Tweet!!"></input>
	</form>
</div>

<div id="timeline_div">
</div>

</body>
</html>