package net.wendal.tb.bean;

import java.sql.Timestamp;

import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("tb_password_reset")
@TableIndexes({@Index(fields={"token"}, name="reset_token", unique=true)})
public class PasswordReset {

	@Name
	private String uid;
	private String token;
	private Timestamp createTime;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
