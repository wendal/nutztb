package net.wendal.tb.bean;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table("tb_email")
public class EmailInfo extends BaseBean {

	private static final long serialVersionUID = 3377531238622296014L;
	@Column
	private String to_add;
	@Column
	private String subject;
	@ColDefine(type=ColType.VARCHAR, width=1024)
	@Column
	private String txt;
	@Column
	private int retry;
	
	public EmailInfo() {
	}
	
	public EmailInfo(String to_add, String subject, String txt) {
		super();
		this.to_add = to_add;
		this.subject = subject;
		this.txt = txt;
	}



	public String getTo_add() {
		return to_add;
	}
	public void setTo_add(String to_add) {
		this.to_add = to_add;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getTxt() {
		return txt;
	}
	public void setTxt(String txt) {
		this.txt = txt;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	
}
