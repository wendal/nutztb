package net.wendal.tb.bean;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("tb_timeline")
@TableIndexes({@Index(fields={"uid"}, name = "uid"),
			   @Index(fields={"type"}, name = "type"),
			   @Index(fields={"contentId"}, name = "contentId")})
public class TimeLine {

	@Column
	private String uid;
	
	@Column("tp")
	private int type;
	
	@Column("cid")
	private String contentId;
	
	@Column
	private String data;
	
	@Column
	private Timestamp createTime;
	
	@One(field="uid", target=User.class)
	private User owner;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	
}
