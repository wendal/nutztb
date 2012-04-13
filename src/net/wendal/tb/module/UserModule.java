package net.wendal.tb.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.wendal.tb.bean.PasswordReset;
import net.wendal.tb.bean.User;
import net.wendal.tb.service.MailService;
import net.wendal.tb.tool.Tbs;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Param;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxCheckSession;

@IocBean
@At("/user")
public class UserModule {
	
	@Inject
	private Dao dao;
	
	@Inject
	private MailService mailService;

	@At
	public Object login(String email, String passwd, HttpServletRequest req) {
		if (Strings.isBlank(email) || Strings.isBlank(passwd))
			return Ajax.fail();
		User me = dao.fetch(User.class, Cnd.where("email", "=", email).and("passwd", "=", xMD5(passwd)));
		if (me == null)
			return Ajax.fail();
		req.getSession().setAttribute("me", me);
		return Ajax.ok();
	}
	
	@At
	public void logout(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		if (session != null)
			session.invalidate();
	}
	
	@At
	public User me(@Attr("me") User me) {
		return me;
	}
	
	@At
	public Object reg(@Param("email")String email) {
		if (Strings.isBlank(email) || !Strings.isEmail(email)) {
			return Ajax.fail().setMsg("email is blank or invaild!");
		} else {
			if (0 != dao.count(User.class, Cnd.where("email", "=", email))) {
				return Ajax.fail().setMsg("email is exist!");
			} else {
				final User me = new User();
				me.setEmail(email);
				String passwd = R.sg(12).next();
				me.setPasswd(xMD5(passwd));
				dao.insert(me);
				if (mailService.send(email, "推爸注册确认邮件", "Your password : " + passwd)) {
					return Ajax.ok();
				} else {
					dao.delete(me);
					return Ajax.fail().setMsg("Fail to send comfig email!!");
				}
			}
		}
	}
	
	@Filters(@By(type=AjaxCheckSession.class, args="me"))
	@At("/update")
	public Object updateInfo(String nickName, String passwd, @Attr("me") User me, HttpSession session) {
		if (!Strings.isBlank(nickName) && nickName.trim().length() > 1) {
			try {
				dao.update(User.class, Chain.make("nickName", nickName.trim()), Cnd.where("id", "=", me.getId()));
			} catch (Throwable e) {
				return Ajax.fail().setMsg("Nickname is dup!");
			}
		}
		if (!Strings.isBlank(passwd) && passwd.trim().length() > 5) {
			dao.update(User.class, Chain.make("passwd", xMD5(passwd.trim())), Cnd.where("id", "=", me.getId()));
		}
		session.setAttribute("me", dao.fetch(User.class, Cnd.where("id", "=", me.getId())));
		return Ajax.ok();
	}
	
	@At("/passwd/reset")
	public void resetPassword(String email, HttpServletRequest req) {
		if (Strings.isBlank(email))
			return;
		User user = dao.fetch(User.class, Cnd.where("email", "=", email));
		if (user == null)
			return;
		dao.clear(PasswordReset.class, Cnd.where("uid", "=", user.getId()));
		String token = R.UU64() + R.UU64();
		PasswordReset reset = new PasswordReset();
		reset.setUid(dao.fetch(User.class, Cnd.where("email", "=", email)).getId());
		reset.setToken(token);
		dao.insert(reset);
		String url = req.getRequestURL() +"/callback?token=" + token;
		mailService.send(email, "推爸 密码重置请求", "Reset URL --> " + url);
	}
	
	@At("/passwd/reset/callback") 
	public Object resetPasswdCallback(String token) {
		PasswordReset reset = dao.fetch(PasswordReset.class, Cnd.where("token", "=", token));
		if (reset != null) {
			dao.clear(PasswordReset.class, Cnd.where("token", "=", token));
			if (System.currentTimeMillis() - reset.getCreateTime().getTime() > 30*60*1000 )
				return Ajax.fail().setMsg("token is expise");
			String passwd = R.sg(12).next();
			dao.update(User.class, Chain.make("passwd", xMD5(passwd)), Cnd.where("id", "=", reset.getUid()));
			String email = dao.fetch(User.class, Cnd.where("id", "=", reset.getUid())).getEmail();
			mailService.send(email, "推爸密码重置邮件", "Your password : " + passwd);
			return Ajax.ok().setMsg("Reset success!! Check you email!");
		}
		return Ajax.fail().setMsg("Token not found!!");
	}
	
	public String xMD5(String str) {
		return Lang.md5(Tbs.md5_key +"bhu7tv"+str+"*%(%"+Tbs.md5_key);
	}
}
