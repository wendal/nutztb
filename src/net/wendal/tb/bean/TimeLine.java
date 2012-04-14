package net.wendal.tb.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("tb_timeline")
@TableIndexes({
			   @Index(fields={"uid"}, name = "uid", unique=false),
			   @Index(fields={"type"}, name = "type", unique=false),
			   @Index(fields={"ref"}, name = "ref", unique=false),
			   @Index(fields={"uid","type","ref"}, name = "a_tweet", unique=true)})
public class TimeLine extends BaseBean {
	
	private static final long serialVersionUID = -220942210095969277L;

	@Column
	private long uid;
	
	@Column("tp")
	private int type;
	
	@Column
	private long ref;
	
	@One(field="uid", target=User.class)
	private User owner;

//

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getRef() {
		return ref;
	}

	public void setRef(long ref) {
		this.ref = ref;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}
	
	
}
