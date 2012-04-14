<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>推爸</title>
<link href="${theme_base}/css/base.css"  rel="stylesheet" type="text/css" />
</head>

<body>
<!-- 
<div id="logo">材料来源于互联网</div>
<div id="desc">请自觉遵守相关法律</div>
-->
<div class="container">
        <div class="search"><input type="text" id="search" value="回车搜索 直接有效" onblur="if (this.value == '') {this.value = '回车搜索 直接有效';}" onfocus="if (this.value == '回车搜索 直接有效') {this.value = '';}"></input></div>
        <div class="header">
             
             <div id="time"></div>
        </div>
       
        <div class="conentGroup">
            <div class="content">
                  <div class="bar">
                        <div class="back"><img src="${theme_base}/images/arrow_right.png" /></div>
                  </div>
                  
                  <div class="card"><img src="${theme_base}/images/login.png" alt="登陆" title="登陆"/></div>
                  <div class="card"><img src="${theme_base}/images/logout.png" alt="登出" title="登出"/></div>
                  <div class="card"><img src="${theme_base}/images/blank.png" alt="注册" title="注册"/></div>
                  <div class="card"><img src="${theme_base}/images/forget.png" alt="忘记密码" title="忘记密码"/></div>
                  <div class="card long"><img src="${theme_base}/images/blank.png" alt="推爸" title="推爸"/></div>
                  <div class="card"><img src="${theme_base}/images/setting.png" alt="设置" title="设置"/></div>
                  <div class="card"><img src="${theme_base}/images/about.png" alt="关于" title="关于"/></div>
                  
                 
                  
                  <div class="clear"></div>
            </div>
            
             <div class="content2 skew">
                    <span></span>
             </div>
         </div>
         
        <div class="control">
             <img  class="button left" src="${theme_base}/images/arrow_left.png" />
             <img  class="button middle" src="${theme_base}/images/MS WINDOWS 7.png" />
             <img  class="button right" src="${theme_base}/images/zoom.png" />
        </div>
      
</div>





<script type="text/javascript" src="${theme_base}/js/jquery-1.6.1.js"></script>

<script>
$(function(){  
		  
		   var card = $(".card");
		   //Card——————————————————————————————————————————
		  $(".card").click(function(){		
			$(".search").hide();	   						 
			$(this).fadeTo(1300,0);
			$(".content").fadeOut(900,skew);
			
			function skew(){
			$(".content2").removeClass("skew");
			}
			
			var list = $(this).siblings(".card");
			var i = 0;
			(function cover() {
				list.eq(i++).addClass("cover").fadeTo(90,0,cover);
			})();
			
			var contentHtml = $(this).find("img").attr("alt");
			//粗糙实现
			if("登陆"==contentHtml)
				contentHtml = ' <form method="post" action=""><div class="row"><label>帐号:</label><input type="text" class="txt"/></div><div class="row"><label>密码:</label><input type="password" class="txt"/></div><div class="row"><input type="submit" class="btn" value="提交"/><input type="reset" class="btn" value="重置"/></div></form>';
			if("注册"==contentHtml)
				contentHtml = ' <form method="post" action=""><div class="row"><label>邮箱:</label><input type="text" class="txt"/></div><div class="row"><input type="submit" class="btn" value="注册"/></div></form>';
			if("忘记密码"==contentHtml)
				contentHtml = ' <form method="post" action=""><div class="row"><label>邮箱:</label><input type="text" class="txt"/></div><div class="row"><input type="submit" class="btn" value="取回密码"/></div></form>';
			if("推爸"==contentHtml)
				contentHtml = '<div class="row ctx"><a href="#">@大笨猫</a></div><div class="row ctx">内容.......</div><div class="row ctx"><a href="#">@兽</a></div><div class="row ctx">内容22.......</div>';
			if("设置"==contentHtml)
				contentHtml = ' <form method="post" action=""><div class="row"><label>昵称:</label><input type="text" class="txt"/></div><div class="row"><label>密码:</label><input type="password" class="txt"/></div><div class="row"><input type="submit" class="btn" value="提交"/></div></form>';
			if("关于"==contentHtml)
				contentHtml = '威猛兽的作品，我只是打酱油而已';
			$(".content2 span").html(contentHtml).fadeTo(900,1);
		  });
		   
		   $(".back").click(function(){
				$(".content").fadeOut(900,skew);
				function skew(){
				$(".content2").removeClass("skew");
				}
				$(".content2 span").html("干嘛").fadeTo(900,1);
		   });
		   var globalData = []; 

		   //Back——————————————————————————————————————————
		  $(".middle").click(function(){
		   $(".search").hide();
		   $(".content2").addClass("skew");
		   $(".content").fadeIn(900);
				//globalData.push("middle");
				
			var j = 0;
			(function cover2() {
				card.eq(j++).removeClass("cover").fadeTo(90,1,cover2);
			})();
			
			$(".content2 span").fadeTo(100,0);
		  });
		   
		  //MultiTasking——————————————————————————————————————————
		   $(".right").click(function(){
			$(".search").slideToggle(300);
				//globalData.push("right");
				
		   });
		   
		    $(".left").click(function(){
			$(".search").hide();	   						 
			$(".content").fadeOut(900,skew);
			
			function skew(){
			$(".content2").removeClass("skew");
			}
			
			var list = $(".card");
			var i = 0;
			(function cover() {
				list.eq(i++).addClass("cover").fadeTo(90,0,cover);
			})();

			//globalData.push($(".content2 span").html());

			var text = "展示缓存上一次的内容,我困了";
			$(".content2 span").html(text).fadeTo(900,1);

		   });
		   
		  //搜索——————————————————————————————————————————
		  $("#search").keyup(function(event){
		      if(13==event.keyCode) {
					//alert("回车搜索")
			  }
		  });
		 
});
           startTime(); 
		  //时钟——————————————————————————————————————————
		  function startTime()
		  {
		  var today=new Date();
		  var h=today.getHours();
		  var m=today.getMinutes();
		  var s=today.getSeconds();
		  // add a zero in front of numbers<10
		  m=checkTime(m);
		  s=checkTime(s);
		  
		  document.getElementById("time").innerHTML=h+":"+m;
		  t=setTimeout('startTime()',500);
		  }
		  
		  function checkTime(i)
		  {
		  if (i<10) 
			{i="0" + i;}
			return i;
		  }

</script>

</body>
</html>