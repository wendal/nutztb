package net.wendal.tb.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.wendal.tb.bean.User;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.ServerRedirectView;

@IocBean
public class PageModule {
	
	@Inject
	Dao dao;

	@Ok(">>:/home/")
	@At
	public void index() {}
	
	@At({"/home/?", "/home"})
	@Ok("jsp:jsp.tb_${a.theme}")
	public Object home(String id, @Attr("me") User me, @Attr("theme") String theme, HttpServletRequest req) {
		if (id == null) {
			if (me != null)
				return new ServerRedirectView("/home/me");
			else
				return new ServerRedirectView("/home/index");
		}
		if (me != null && (""+me.getId()).equals(id))
			return new ServerRedirectView("/home/me");
		if ("me".equals(id) && me == null)
			return new ServerRedirectView("/home/index");
		if (theme == null) {
			theme = "default";
			req.getSession().setAttribute("theme", theme);
		}
		req.setAttribute("theme", theme);
		req.setAttribute("theme_base", req.getContextPath() + "/theme/"+theme);
		return id;
	}
	
	@At({"/theme", "/theme/?"})
	public Object theme(String theme, @Attr("theme") String session_theme, HttpSession session) {
		if (theme == null)
			return session_theme;
		session.setAttribute("theme", theme);
		return theme;
	}
}
