package net.wendal.tb.module;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.wendal.tb.bean.TbContent;
import net.wendal.tb.bean.TimeLine;
import net.wendal.tb.bean.User;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.view.HttpStatusView;
import org.nutz.web.ajax.Ajax;
import org.nutz.web.ajax.AjaxCheckSession;

@IocBean
@Filters(@By(type=AjaxCheckSession.class, args="me"))
public class TwitterModule {

	@Inject
	private Dao dao;
	
	@Inject 
	private Dao lazyDao;
	
	@At
	public Object tweet(String content, @Attr("me") User me , @Attr("last_tweet_time") Long lastTweetTime, HttpSession session) {
		if (lastTweetTime != null && (System.currentTimeMillis() - lastTweetTime.longValue()) < 3000)
			return Ajax.fail().setMsg("Tweet too fast!!");
		if (Strings.isBlank(content))
			return Ajax.fail();
		content = content.trim().intern();
		if (content.length() > 140 || content.length() < 3)
			return Ajax.fail();
		session.setAttribute("last_tweet_time", System.currentTimeMillis());
		
		TbContent ct = new TbContent();
		ct.setText(content);
		ct.setCreateTime(new Timestamp(System.currentTimeMillis()));
		dao.insert(ct);
		TimeLine tl = new TimeLine();
		tl.setData(ct.getId());
		tl.setUid(me.getId());
		tl.setCreateTime(new Timestamp(System.currentTimeMillis()));
		dao.insertWith(tl, "content");
		
		return Ajax.ok();
	}
	
	@At("/retweet/?")
	public Object retweet(String id, @Attr("me") User me) {
		if (Strings.isBlank(id) || 0 == dao.count(TimeLine.class, Cnd.where("id", "=", id)))
			return Ajax.fail();
		TimeLine tl = new TimeLine();
		tl.setData(id);
		tl.setType(1); //retweet
		tl.setUid(me.getId());
		tl.setCreateTime(new Timestamp(System.currentTimeMillis()));
		dao.insert(tl);
		return Ajax.ok();
	}
	
	@At("/unretweet/?")
	public void removeRetweet(String id, @Attr("me") User me) {
		if (Strings.isBlank(id))
			return;
		dao.clear(TimeLine.class, Cnd.where("uid", "=", me.getId()).and("type", "=", 1).and("data", "=", id));
	}
	
	@At("/u/me")
	public Object home(int page, @Attr("me") User me) {
		if (page < 1)
			page = 1;
		Pager pager = lazyDao.createPager(page, 20);
		Sql sql = Sqls.queryEntity("select * from tb_timeline where uid=@uid or uid in (select to_uid from tb_following where from_uid=@uid)");
		sql.params().set("uid", me.getId());
		sql.setPager(pager);
		sql.setEntity(lazyDao.getEntity(TimeLine.class));
		lazyDao.execute(sql);
		pager.setRecordCount(Daos.queryCount(lazyDao, sql.toString()));
		List<TimeLine> tls = sql.getList(TimeLine.class);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", me);
		map.put("data", tls);
		return map;
	}
	
	@Filters()
	@At("/u/index")
	public Object index(int page) {
		if (page < 1)
			page = 1;
		Pager pager = lazyDao.createPager(page, 20);
		Cnd cnd = Cnd.where("type", "=", 0);
		pager.setRecordCount(lazyDao.count(TimeLine.class, cnd));
		List<TimeLine> tls = lazyDao.query(TimeLine.class, cnd, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", tls);
		return map;
	}
	
	@Filters()
	@At("/u/?")
	public Object user(String uid, int page) {
		if (Strings.isBlank(uid))
			return new HttpStatusView(404);
		User user = dao.fetch(User.class, Cnd.where("id", "=", uid));
		if (user == null)
			return new HttpStatusView(404);
		if (page < 1)
			page = 1;
		Pager pager = lazyDao.createPager(page, 20);
		Cnd cnd = Cnd.where("uid", "=", uid);
		pager.setRecordCount(lazyDao.count(TimeLine.class, cnd));
		List<TimeLine> tls = lazyDao.query(TimeLine.class, cnd, pager);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", user);
		map.put("data", tls);
		return map;
	}
	
	@At
	public Object follow(String uid, @Attr("me") User me) {
		if (Strings.isBlank(uid))
			return Ajax.fail();
		User user = dao.fetch(User.class, Cnd.where("id", "=", uid));
		if (user == null)
			return Ajax.fail().setMsg("No such user!");
		int count = dao.count("tb_following", Cnd.where("from_uid", "=", me.getFollowings()));
		if (count > 1000)
			return Ajax.fail().setMsg("Sorry, Only allow to follow 1000 person!!");
		dao.insert("tb_following", Chain.make("from_uid", me.getId()).add("to_uid", user.getId()));
		return Ajax.ok();
	}
	
	@At
	public Object unfollow(String uid, @Attr("me") User me) {
		if (Strings.isBlank(uid))
			return Ajax.fail();
		User user = dao.fetch(User.class, Cnd.where("id", "=", uid));
		if (user == null)
			return Ajax.fail().setMsg("No such user!");
		dao.clear("tb_following", Cnd.where("from_uid", "=", me.getId()).and("to_uid", "=", user.getId()));
		return Ajax.ok();
	}
	
	@At("/follow/query")
	public Object queryFollow(String uid, @Attr("me") User me) {
		if (Strings.isBlank(uid))
			return Ajax.fail();
		return 1 == dao.count("tb_following", Cnd.where("from_uid", "=", me.getId()).and("to_uid", "=", uid));
	}
}
