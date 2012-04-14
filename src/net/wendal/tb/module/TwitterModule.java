package net.wendal.tb.module;

import java.util.ArrayList;
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
		content = content.replace('<', ' ').replace('>', ' '); //简单防html注入
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
	public void removeRetweet(long id, @Attr("me") User me) {
		dao.clear(TimeLine.class, Cnd.where("uid", "=", me.getId()).and("type", "=", 1).and("ref", "=", id));
	}
	
	@At("/u/me")
	public Object home(int page, @Attr("me") User me) {
		if (page < 1)
			page = 1;
		Pager pager = dao.createPager(page, 20);
		String cnd = String.format("(uid=%d and tp=0) or (id in (select ref from tb_timeline where uid=%d and tp=1)) " + /*本人原创及转发的记录*/
				"or (uid in (select to_uid from tb_following where from_uid=%d) and tp=0) " + /*所关注的人原创的记录*/
				"or (id in (select ref from tb_timeline where uid in (select to_uid from tb_following where from_uid=%d) and tp=1))",
				me.getId(), me.getId(), me.getId(), me.getId()); /*所关注的人的转发记录*/
		pager.setRecordCount(dao.count(TimeLine.class, Cnd.wrap(cnd)));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", setCountData(me));
		map.put("data", convert2VO(dao.query(TimeLine.class, Cnd.wrap(cnd + " order by id desc"), pager)));
		map.put("pager", pager);
		return map;
	}
	
	@Filters()
	@At("/u/index")
	public Object index(int page) {
		if (page < 1)
			page = 1;
		Pager pager = dao.createPager(page, 20);
		pager.setRecordCount(dao.count(TimeLine.class, Cnd.where("type", "=", 0)));
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("data", convert2VO(dao.query(TimeLine.class, Cnd.where("type", "=", 0).desc("id"), pager)));
		map.put("pager", pager);
		return map;
	}
	
	@Filters()
	@At("/u/?")
	public Object user(long uid, int page) {
		User user = dao.fetch(User.class, uid);
		if (user == null)
			return new HttpStatusView(404);
		if (page < 1)
			page = 1;
		Pager pager = dao.createPager(page, 20);
		String str = String.format("(uid=%d and tp=0) or id in (select ref from tb_timeline where uid=%s and tp=1)", uid, uid);
		pager.setRecordCount(dao.count(TimeLine.class, Cnd.wrap(str)));
		List<TimeLine> tls = dao.query(TimeLine.class, Cnd.wrap(str+" order by id desc"), pager);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("user", setCountData(user));
		map.put("data", convert2VO(tls));
		map.put("pager", pager);
		return map;
	}
	
	@At({"/follow","/follow/?"})
	public Object follow(long uid, @Attr("me") User me) {
		User user = dao.fetch(User.class, uid);
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
		user.setTweetCount(dao.count(TimeLine.class, Cnd.where("uid", "=", user.getId()).and("type", "=", 0)));
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
		User owner = dao.fetch(User.class, tl.getUid());
		User me = (User) Mvcs.getReq().getSession().getAttribute("me");
		TbContent tc = dao.fetch(TbContent.class, tl.getRef());
		if (tc == null)
			return null;
		map.put("id", tl.getId());
		map.put("txt", tc.getText());
		map.put("retweet", false);
		map.put("owner", owner);
		map.put("my_tweet", false);
		map.put("retweeted_by_me", false);
		if (me != null) {
			if (me.getId() == owner.getId())
				map.put("my_tweet", true);
			else {
				map.put("retweeted_by_me", 0 != dao.count(TimeLine.class, Cnd.where("uid", "=", me.getId()).and("type", "=", 1).and("ref", "=", tl.getId())));
			}
		}
		List<User> retweetByUsers = dao.query(User.class, Cnd.format("id in (select uid from tb_timeline where uid=%d and tp=1 and ref=%d)", tl.getUid(), tl.getId()), dao.createPager(1, 5));
		map.put("retweet_by_users", retweetByUsers);
		return map;
	}
}
