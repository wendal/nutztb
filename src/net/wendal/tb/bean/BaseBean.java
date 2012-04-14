package net.wendal.tb.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Prev;

public abstract class BaseBean implements Serializable {
	
	private static final long serialVersionUID = 7506502506839085403L;
	
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
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof BaseBean))
			return false;
		BaseBean other = (BaseBean) obj;
		if (id != other.id)
			return false;
		return true;
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
