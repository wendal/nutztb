<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>推爸主页</title>
<!-- Le styles -->
<link href="${base}/css/bootstrap.css" rel="stylesheet">
<style type="text/css">
body {
	padding-top: 60px;
	padding-bottom: 40px;
}
</style>
<link href="${base}/css/bootstrap-responsive.css" rel="stylesheet">

<!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
<!--[if lt IE 9]>
      <script src="${base}/js/html5.js"></script>
<![endif]-->
<!-- 载入console支持 -->
<style type="text/css">@import '${base}/css/fauxconsole.css';</style>
<script type="text/javascript" src="${base}/js/fauxconsole.js"></script>
</head>
<body>
	<div class="navbar navbar-fixed-top">
		<div class="navbar-inner">
			<div class="container">
				<a class="brand" href="${base}/home/index">推爸!</a>
				<div class="nav-collapse">
					<ul class="nav">
						<li class="active"><a href="${base}/home">Home</a></li>
						<li><a href="" id="a_login">登陆</a></li>
						<li>
							<form id="loginForm" class="navbar-form pull-left" style="display: none; ">
								<input type="text" name="email" ></input>
								<input type="password" name="passwd"></input>
								<input type="submit" value="提交"></input>
							</form>
						</li>
						<li><a href="" id="a_logout">登出</a></li>
						<li><a href="" id="a_regiter">注册</a></li>
						<li>
							<form id="regForm" class="navbar-form pull-left" style="display: none; ">
								<input type="text" name="email"></input>
								<input type="submit" value="提交"></input>
							</form>
						</li>
						<li><a href="" id="a_forget_passwd">忘记密码</a></li>
						<li>
							<form id="passwdresetForm" class="navbar-form pull-left" style="display: none; ">
								<input type="text" name="email"></input>
								<input type="submit" value="提交"></input>
							</form>
						</li>
						<li><a href="" id="a_user_setting">设置</a></li>
						<li>
							<form action="#" id="updateForm" style="display: none;">
							昵称:<input type="text" name="nickName"></input>
							密码:<input type="password" name="passwd"></input>
								<input type="submit" value="更新"></input>
							</form>
						</li>
					</ul>
				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
	</div>

	<div class="container">
		<div class="row-fluid">
			<div class="span3"><div></div></div>
			<div class="span6">
				<div id="tweet_div">
					<form id="tweetForm" class="well">
						<textarea name="content" ></textarea>
						<p />
						<input type="submit" value="Tweet!!" class="btn btn-primary btn-large"></input>
					</form>
				</div>
				<div id="timeline_div"></div>
			</div>
			<div class="span3">
				<div id="user_div">
							用户昵称:<b id="user_nickName">推爸</b>
							<p />
							tweet:<a id="user_tweet" href="">0</a>
							<p />
							following:<a id="user_following" href="">0</a>
							<p />
							followed: <a id="user_followed" href="">0</a>
							<p />

							<button id="follow_me" class="btn btn-info">follow!</button>
							<button id="unfollow_me" class="btn btn-info">unfollow!</button>
				</div>
			</div>
		</div>


	</div>
	<!-- 放在最后面 -->

	<script type="text/javascript" src="${base}/js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="${base}/js/jquery.json-2.3.js"></script>
	<script type="text/javascript" src="${base}/js/ejs.js"></script>
	<script type="text/javascript" src="${base}/js/view.js"></script>
	<script type="text/javascript" src="${base}/js/bootstrap.js"></script>
	<script type="text/javascript">
		var uid = "${obj}"; //通过入口方法来设置
		var tb_ctx = new Object();
		tb_ctx.login = false;
		tb_ctx.uid = -1; //当前页面的UID
		tb_ctx.my_uid = -1; // 当前已经登陆用户的UID
		tb_ctx.base = "${base}";
	</script>
	<script type="text/javascript" src="${base}/js/tb_main.js"></script>
	
</body>
</html>