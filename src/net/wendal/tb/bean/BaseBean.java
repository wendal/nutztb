package net.wendal.tb.bean;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Prev;

public abstract class BaseBean {
	
	@Id(auto=false)
	@Prev(els={@EL("$me.nextId()")})
	private long id;
	public static Map<Class<?>, AtomicLong> ids = new HashMap<Class<?>, AtomicLong>();
	public long nextId() {
		return ids.get(getClass()).getAndIncrement();
	}

	@Column
	@Prev(els=@EL("$me.now()"))
	private Timestamp createTime;
	public Timestamp now() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}


	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
}
