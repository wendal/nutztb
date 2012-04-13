package net.wendal.tb.module;

import net.wendal.tb.bean.User;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.View;
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
	
	private static final View HOME = new ServerRedirectView("/home");
	
	@At({"/home/?", "/home"})
	@Ok("jsp:jsp.tb")
	public Object home(String id, @Attr("me") User me) {
		if ((id == null && me != null) || ("me".equals(id) && me != null))
			return "me";
		if (id == null || "index".equals(id))
			return "index";
		try {
			long uid = Long.parseLong(id);
			if (0 != dao.count(User.class, Cnd.where("id", "=", uid)))
				return id;
		} catch (Throwable e) {}
		return HOME;
	}
}
