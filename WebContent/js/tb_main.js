$(function() {
	$.get(tb_ctx.base+"/user/me", null, function(resp) {
		if (resp.ok && resp.data) {
			tb_ctx.login = true;
			tb_ctx.my_uid = resp.data.id;
		}
		tb_ctx.uid = uid;
		loadData(uid);
	}, "json");
	
		$("#unfollow_me").hide();
		$("#follow_me").hide();
		
		if (tb_ctx.login) {
			$("#a_login").hide();
			$("#a_regiter").hide();
			$("#a_forget_passwd").hide();
		} else {
			$("#a_logout").hide();
			$("#a_user_setting").hide();
		}
		
		if (uid != "me") {
			$("#tweetForm").hide();
		} 
		if (uid == "index") {
			$("#user_div").hide();
		}
		
		//导航条的action
		$("#a_logout").click(function () {
			$.post(tb_ctx.base+"/user/logout", null, function() {
				window.location.reload();
			});
		});
		$("#a_login").click(function () {
			$("#loginForm").toggle("show");
			$("#regForm").hide();
			$("#passwdresetForm").hide();
			$("#updateForm").hide();
			return false;
		});
		$("#a_regiter").click(function () {
			$("#loginForm").hide();
			$("#regForm").toggle("show");
			$("#passwdresetForm").hide();
			$("#updateForm").hide();
			return false;
		});
		$("#a_forget_passwd").click(function () {
			$("#loginForm").hide();
			$("#regForm").hide();
			$("#passwdresetForm").toggle("show");
			$("#updateForm").hide();
			return false;
		});
		$("#a_user_setting").click(function () {
			$("#loginForm").hide();
			$("#regForm").hide();
			$("#passwdresetForm").hide();
			$("#updateForm").toggle("show");
			return false;
		});
		
		//----------------------------------
		$("#regForm").submit(function () {
			$(this).attr("disable","disable");
			var data = $(this).serialize();
			$.post(tb_ctx.base+"/user/reg",data, function(resp) {
				if (resp.ok) {
					alert("注册成功! 密码已经发送到您的邮箱,请查收!!");
					$("#regForm").hide();
					//window.location = "${base}/";
				} else {
					alert("注册失败! " + resp.msg);
				}
				$(this).removeattr("disable","disable");
			},"json");
			return false;
		});

		$("#passwdresetForm").submit(function () {
			var data = $(this).serialize();
			$.post(tb_ctx.base+"/user/passwd/reset",data, function(resp) {
				if (resp.ok) {
					alert("密码重置,申请成功!! 请查收邮件!");
					$("#passwdresetForm").hide();
					//window.location = "${base}/";
				} else {
					alert("密码重置,申请失败! " + resp.msg);
				}
			},"json");
			return false;
		});

		$("#loginForm").submit(function () {
			var data = $(this).serialize();
			$.post(tb_ctx.base+"/user/login",data, function(resp) {
				if (resp.ok) {
					alert("登陆成功!");
					window.location.reload();
				} else {
					alert("登陆失败! " + resp.msg);
				}
			},"json");
			return false;
		});
		
		$("#updateForm").submit(function () {
			var data = $(this).serialize();
			$.post(tb_ctx.base+"/user/update",data, function(resp) {
				if (resp.ok) {
					alert("更新成功!");
					$("#updateForm").hide();
					//window.location = "${base}/";
				} else {
					alert("更新失败! " + resp.msg);
				}
			},"json");
			return false;
		});
		//----------------------------------

		$("#unfollow_me").click(function() {
			if (uid != "me") {
				$.get(tb_ctx.base+"/unfollow/" + uid, null, function(resp) {
					$("#follow_me").show();
					$("#unfollow_me").hide();
				}, "json");
			}
		});

		$("#follow_me").click(function() {
			if (uid != "me") {
				$.get(tb_ctx.base+"/follow/" + uid, null, function(resp) {
					$("#unfollow_me").show();
					$("#follow_me").hide();
				}, "json");
			}
		});
		
		$("#tweetForm").submit(function() {
			var data = $(this).serialize();
			$.post(tb_ctx.base+"/tweet", data, function(resp) {
				if (resp.ok) {
					alert("Tweet成功!");
				} else {
					alert("Tweet失败! " + resp.msg);
				}
				}, "json");
			return false;
		});
});

function loadData(uid) {
	$.get(tb_ctx.base+"/u/" + uid, null, function(resp) {
		if (!resp.ok) {
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
				$.post(tb_ctx.base+"/follow/query", "uid=" + uid,
					function(resp) {
						if (resp.ok) {
							if (resp.data) {
								$("#unfollow_me").show();
								$("#follow_me").hide();
							} else {
								$("#follow_me").show();
								$("#unfollow_me").hide();
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
	for ( var i in data) {
		var d = data[i];
		d.ctx = tb_ctx;
		var html = new EJS({
			url : tb_ctx.base+"/ejs/tweet.ejs"
			}).render(d);
		timeline.append(html);
	}
	//timeline.append("");
}

function retweet(id) {
	$.get(tb_ctx.base+"/retweet/" + id, null, function(resp) {
		if (resp.ok) {
			$("#tweet_" + id).html("Undo Retweet");
			$("#tweet_" + id).attr("onclick", "unretweet(" + id + ");");
		} else {
			alert("Retweet Fail!! " + resp.msg);
		}
	}, "json");
}

function unretweet(id) {
	$.get(tb_ctx.base+"/unretweet/" + id, null, function(resp) {
		if (resp.ok) {
			$("#tweet_" + id).html("Retweet");
			$("#tweet_" + id).attr("onclick", "retweet(" + id + ");");
		} else {
			alert("Retweet Fail!! " + resp.msg);
		}
	}, "json");
}