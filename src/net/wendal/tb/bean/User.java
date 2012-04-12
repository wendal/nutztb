package net.wendal.tb.bean;

import java.sql.Timestamp;
import java.util.Set;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.EL;
import org.nutz.dao.entity.annotation.Index;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.TableIndexes;
import org.nutz.json.JsonField;
import org.nutz.lang.random.R;

@Table("tb_user")
@TableIndexes({@Index(fields={"email"}, name="email", unique=true),
	           @Index(fields={"nickName"}, name="nickName", unique=true)})
public class User {
	
	@Name
	@Prev(els=@EL("$me.uuid()"))
	private String id;

	@Column
	@ColDefine(notNull=true, type = ColType.VARCHAR)
	private String email;
	
	@JsonField(ignore=true)
	@Column
	private String passwd;
	
	@Column
	@Prev(els=@EL("$me.uuid()"))
	private String nickName;
	

	@Column
	@Readonly
	private Timestamp createTime = new Timestamp(System.currentTimeMillis());
	
	@JsonField(ignore=true)
	@Column
	@ManyMany(from="from_uid",to="to_uid",relation="tb_following", target=User.class)
	private Set<User> followings;
	
	public String uuid() {
		return R.UU64();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public Set<User> getFollowings() {
		return followings;
	}

	public void setFollowings(Set<User> followings) {
		this.followings = followings;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	
}
