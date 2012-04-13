<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>功能页面</title>
<script type="text/javascript" src="${base}/js/jquery-1.7.2.js"></script>
<script type="text/javascript" src="${base}/js/jquery.json-2.3.js"></script>
<script type="text/javascript">
$(function () {
	$("#regForm").submit(function () {
		$(this).attr("disable","disable");
		var data = $(this).serialize();
		$.post("${base}/user/reg",data, function(resp) {
			if (resp.ok) {
				alert("注册成功!");
				//window.location = "${base}/";
			} else {
				alert("注册失败! " + resp.msg);
			}
			$(this).removeattr("disable","disable");
		},"json");
		$(this).hide();
		return false;
	});

	$("#passwdresetForm").submit(function () {
		var data = $(this).serialize();
		$.post("${base}/user/passwd/reset",data, function(resp) {
			if (resp.ok) {
				alert("密码重置,申请成功!");
				//window.location = "${base}/";
			} else {
				alert("密码重置,申请失败! " + resp.msg);
			}
		},"json");
		return false;
	});

	$("#loginForm").submit(function () {
		var data = $(this).serialize();
		$.post("${base}/user/login",data, function(resp) {
			if (resp.ok) {
				alert("登陆成功!");
				//window.location = "${base}/";
			} else {
				alert("登陆失败! " + resp.msg);
			}
		},"json");
		return false;
	});

	$("#meForm").submit(function () {
		var data = $(this).serialize();
		$.post("${base}/user/me",data, function(resp) {
			if (resp.ok && resp.data) {
				alert("获取成功!\n Your id=" + resp.data.id +
						"\n Your nickName=" + resp.data.nickName);
				//window.location = "${base}/";
			} else {
				alert("获取失败! " + resp.msg);
			}
		},"json");
		return false;
	});

	$("#updateForm").submit(function () {
		var data = $(this).serialize();
		$.post("${base}/user/update",data, function(resp) {
			if (resp.ok) {
				alert("更新成功!");
				//window.location = "${base}/";
			} else {
				alert("更新失败! " + resp.msg);
			}
		},"json");
		return false;
	});
	
	$("#logoutForm").submit(function () {
		$.post("${base}/user/logout", null, function(resp) {
			if (resp.ok) {
				alert("登出成功!");
			} else {
				alert("登出失败! " + resp.msg);
			}
		},"json");
		return false;
	});
});
</script>
</head>
<body>
<div>
	<h1>注册</h1><p/>
	<form action="#" id="regForm">
		请输入你的邮箱:<input type="text" name="email"></input>
		<input type="submit" value="提交"></input><p/>
		密码将发送到你的邮箱,请注意查收^_^<p/>
	</form>
</div>

<div>
	<h1>密码重置</h1><p/>
	<form action="#" id="passwdresetForm">
		请输入你的邮箱:<input type="text" name="email"></input>
		<input type="submit" value="提交"></input><p/>
		如果信息正确,你将会受到一封邮件,点击其中的URL进行密码重置^_^<p/>
	</form>
</div>


<div>
	<h1>登陆</h1><p/>
	<form action="#" id="loginForm">
		邮箱:<input type="text" name="email"></input><p/>
		密码:<input type="password" name="passwd"></input><p/>
		<input type="submit" value="提交"></input><p/>
	</form>
</div>

<div>
	<h1>退出登陆</h1><p/>
	<form action="#" id="logoutForm">
		<input type="submit" value="登出"></input><p/>
	</form>
</div>

<div>
	<h1>关于我</h1><p/>
	<form action="#" id="meForm">
		<input type="submit" value="获取"></input><p/>
	</form>
</div>

<div>
	<h1>更新信息</h1><p/>
	<form action="#" id="updateForm">
		昵称:<input type="text" name="nickName"></input><p/>
		密码:<input type="password" name="passwd"></input><p/>
		<input type="submit" value="更新"></input><p/>
	</form>
</div>

</body>
</html>