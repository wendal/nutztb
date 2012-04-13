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
var tb_ctx = new Object();
tb_ctx.login = false;
tb_ctx.uid = -1; //当前页面的UID
tb_ctx.my_uid = -1; // 当前已经登陆用户的UID
tb_ctx.base = "${base}";
$(function () {

	$("#unfollow_me").hide();
	$("#follow_me").hide();
	
	var uid = "${obj}"; //通过入口方法来设置
	$.get("${base}/user/me", null, function (resp){
		if (resp.data) {
			if (uid == "index" || uid == resp.data.id) {
				uid = "me";
			}
			tb_ctx.login = true;
			tb_ctx.my_uid = resp.data.id;
		} else if (uid == "index"){
			console.log("Not login yet, use default index");
		}
		console.log("UID=" + uid);
		tb_ctx.uid = uid;
		loadData(uid);
	}, "json");
	
	$("#unfollow_me").click(function(){
		if (uid != "me") {
			$.get("${base}/unfollow/"+uid, null, function(resp){
				$("#follow_me").show();
				$("#unfollow_me").hide();
			}, "json");
		}
	});
	

	$("#follow_me").click(function(){
		if (uid != "me") {
			$.get("${base}/follow/"+uid, null, function(resp){
				$("#unfollow_me").show();
				$("#follow_me").hide();
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
			$("#user_tweet").html(user.tweetCount);
			if (uid != "me") {
				$.post("${base}/follow/query", "uid="+uid, function (resp) {
					if (resp.ok ) {
						if (resp.data) {
							$("#unfollow_me").show();
							$("#follow_me").hide();
						} else {
							$("#follow_me").show();
							$("#unfollow_me").hide();
						}
					}
				}, "json");
				$("#tweetForm").hide();
			} else {
				$("#tweetForm").show();
				$("#tweetForm").submit(function (){
					var data = $(this).serialize();
					$.post("${base}/tweet",data, function(resp) {
						if (resp.ok) {
							alert("Tweet成功!");
						} else {
							alert("Tweet失败! " + resp.msg);
						}
					},"json");
					return false;
				});
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
	//alert($.toJSON(data));
	for (var i in data) {
		//alert($.toJSON(data[i]));
		//timline.append("<h5>-----------------------------------------<h5>");
		var d = data[i];
		d.ctx = tb_ctx;
		var html = new EJS({url: '${base}/ejs/tweet.ejs'}).render(d);
		timeline.append(html);
	}
	timeline.append("");
}

function toUser(uid) {
	var loc = window.location.href;
	window.location = loc.substring(0, loc.indexOf("#"));
}

function retweet(id) {
	$.get("${base}/retweet/"+id, null, function (resp){
		if (resp.ok) {
			$("#tweet_"+id).html("Undo Retweet");
			$("#tweet_"+id).attr("onclick", "unretweet("+id+");");
		} else {
			alert("Retweet Fail!! " + resp.msg);
		}
	}, "json");
}

function unretweet(id) {
	$.get("${base}/unretweet/"+id, null, function (resp){
		if (resp.ok) {
			$("#tweet_"+id).html("Retweet");
			$("#tweet_"+id).attr("onclick", "retweet("+id+");");
		} else {
			alert("Retweet Fail!! " + resp.msg);
		}
	}, "json");
}
</script>
</head>
<body>
<div>
<h3><a href="${base}/home">Home</a></h3>
<h3><a href="${base}/user_function.jsp">用户功能(登陆,注册等等)</a></h3>
</div>

<div id="user_div">
	用户昵称:<b id="user_nickName">推爸</b><p/>
	tweet:<a id="user_tweet" href="#">0</a><p/>
	following:<a id="user_following" href="#">0</a><p/>
	followed: <a id="user_followed"  href="#">0</a><p/>
	
	<button id="follow_me">follow!</button>
	<button id="unfollow_me">unfollow!</button>
</div>

<div id="tweet_div" class="display:none;">
	<form id="tweetForm">
		<textarea name="content"></textarea><p/>
		<input type="submit" value="Tweet!!"></input>
	</form>
</div>

<div id="timeline_div">
</div>

</body>
</html>