package net.wendal.tb.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;

@Table("tb_password_reset")
@TableIndexes({@Index(fields={"id"}, name = "id", unique=true),
			   @Index(fields={"token"}, name="reset_token", unique=true)})
public class PasswordReset extends BaseBean {

	@Name
	private long uid;
	@Column
	private String token;
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	
}
