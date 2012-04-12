package net.wendal.tb.module;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
public class PageModule {

	@Ok("jsp:jsp.index")
	@At
	public void index() {}
}
