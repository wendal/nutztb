package net.wendal.tb.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.wendal.tb.bean.TbContent;
import net.wendal.tb.bean.TimeLine;
import net.wendal.tb.bean.User;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
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
	
	private static final Log log = Logs.get();

	@Inject
	private Dao dao;
	
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
		dao.insert(ct);
		TimeLine tl = new TimeLine();
		tl.setRef(ct.getId());
		tl.setUid(me.getId());
		dao.insertWith(tl, "content");
		
		return Ajax.ok();
	}
	
	@At("/retweet/?")
	public Object retweet(long id, @Attr("me") User me) {
		if (0 == dao.count(TimeLine.class, Cnd.where("id", "=", id)))
			return Ajax.fail();
		if (0 != dao.count(TimeLine.class, Cnd.where("uid", "=", me.getId()).and("type", "=", 1).and("ref", "=", id)))
			return Ajax.fail().setMsg("Aleady retweet !!");
		TimeLine tl = new TimeLine();
		tl.setRef(id);
		tl.setType(1); //retweet
		tl.setUid(me.getId());
		dao.insert(tl);
		return Ajax.ok();
	}
	
	@At("/unretweet/?")
	public void removeRetweet(String id, @Attr("me") User me) {
		if (Strings.isBlank(id))
			return;
		dao.clear(TimeLine.class, Cnd.where("uid", "=", me.getId()).and("type", "=", 1).and("ref", "=", id));
	}
	
	@At("/u/me")
	public Object home(int page, @Attr("me") User me) {
		if (page < 1)
			page = 1;
		Pager pager = dao.createPager(page, 20);
		Condition cnd = Cnd.format("uid=%d or uid in (select to_uid from tb_following where from_uid=%d) order by id desc", me.getId(), me.getId());
		pager.setRecordCount(dao.count(TimeLine.class, cnd));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", setCountData(me));
		map.put("data", convert2VO(dao.query(TimeLine.class, cnd, pager)));
		return map;
	}
	
	@Filters()
	@At("/u/index")
	public Object index(int page) {
		if (page < 1)
			page = 1;
		Pager pager = dao.createPager(page, 20);
		pager.setRecordCount(dao.count(TimeLine.class));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", convert2VO(dao.query(TimeLine.class, Cnd.orderBy().desc("id"), pager)));
		return map;
	}
	
	@Filters()
	@At("/u/?")
	public Object user(long uid, int page) {
		User user = dao.fetch(User.class, Cnd.where("id", "=", uid));
		if (user == null)
			return new HttpStatusView(404);
		if (page < 1)
			page = 1;
		Pager pager = dao.createPager(page, 20);
		pager.setRecordCount(dao.count(TimeLine.class, Cnd.where("uid", "=", uid)));
		List<TimeLine> tls = dao.query(TimeLine.class, Cnd.where("uid", "=", uid).desc("id"), pager);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", setCountData(user));
		map.put("data", convert2VO(tls));
		return map;
	}
	
	@At({"/follow","/follow/?"})
	public Object follow(long uid, @Attr("me") User me) {
		User user = dao.fetch(User.class, Cnd.where("id", "=", uid));
		if (user == null)
			return Ajax.fail().setMsg("No such user!");
		int count = dao.count("tb_following", Cnd.where("from_uid", "=", me.getFollowings()));
		if (count > 1000)
			return Ajax.fail().setMsg("Sorry, Only allow to follow 1000 person!!");
		if (0 != dao.count("tb_following", Cnd.where("from_uid", "=", me.getId()).and("to_uid", "=", uid)))
			return Ajax.fail().setMsg("Aleady following!");
		dao.insert("tb_following", Chain.make("from_uid", me.getId()).add("to_uid", uid));
		return Ajax.ok();
	}
	
	@At({"/unfollow", "/unfollow/?"})
	public Object unfollow(long uid, @Attr("me") User me) {
		dao.clear("tb_following", Cnd.where("from_uid", "=", me.getId()).and("to_uid", "=", uid));
		return Ajax.ok();
	}
	
	@At("/follow/query")
	public Object queryFollow(long uid, @Attr("me") User me) {
		return 1 == dao.count("tb_following", Cnd.where("from_uid", "=", me.getId()).and("to_uid", "=", uid));
	}
	
	public User setCountData(User user) {
		user.setFollowingCount(dao.count("tb_following", Cnd.where("from_uid", "=", user.getId())));
		user.setFollowedCount(dao.count("tb_following", Cnd.where("to_uid", "=", user.getId())));
		user.setTweetCount(dao.count(TimeLine.class, Cnd.where("uid", "=", user.getId())));
		return user;
	}
	
	public Object convert2VO(List<TimeLine> tls) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		for (TimeLine tl : tls) {
			Map<String, Object> map = convert2VO(tl);
			if (map != null)
				data.add(map);
			else {
				if (log.isInfoEnabled())
					log.info("Timeline delete while query --> " + tl.getId());
			}
		}
		return data;
	}
	
	public Map<String, Object> convert2VO(TimeLine tl) {
		Map<String, Object> map = new HashMap<String, Object>();
		switch (tl.getType()) {
		case 0:
			TbContent tc = dao.fetch(TbContent.class, tl.getRef());
			if (tc == null)
				return null;
			map.put("txt", tc.getText());
			map.put("retweet", false);
			map.put("owner", dao.fetch(User.class, tl.getUid()));
			break;
		case 1:
			TimeLine tl_ref = dao.fetch(TimeLine.class, tl.getRef());
			if (tl_ref == null) {
				return null;
			}
			TbContent tc2 = dao.fetch(TbContent.class, tl_ref.getRef());
			if (tc2 == null)
				map.put("txt", "Deleted by origin user!");
			else
				map.put("txt", tc2.getText());
			map.put("owner", dao.fetch(User.class, tl_ref.getUid()));
			map.put("retweet", true);
			
			break;
		default:
			return Collections.emptyMap();
		}
		//检查一下,是否我自己是否已经Retwee过
		User me = (User) Mvcs.getReq().getSession().getAttribute("me");
		if (me != null) {
			map.put("retweeted_by_me", 0 != dao.count(TimeLine.class, Cnd.where("uid", "=", me.getId()).and("type", "=", 1).and("ref", "=", tl.getRef())));
		}
		map.put("id", tl.getId());
		return map;
	}
}
