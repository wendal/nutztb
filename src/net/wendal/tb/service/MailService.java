package net.wendal.tb.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import net.wendal.tb.bean.EmailInfo;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean(depose="close", create="init")
public class MailService {
	
	private static final Log log = Logs.get();

	@Inject("java:$conf.get('mail_smtp')")
	private String smtpServer;
	@Inject("java:$conf.get('mail_user')")
	private String user;
	@Inject("java:$conf.get('mail_passwd')")
	private String passwd;
	@Inject("java:$conf.get('mail_from')")
	private String from;
	
	@Inject
	Dao dao;
	
	public boolean add2Queue(String to, String subject, String txt) {
		EmailInfo info = new EmailInfo(to, subject, txt);
		dao.insert(info);
		return true;
	}

	protected void send(String to, String subject, String txt) throws Throwable {
		if (log.isDebugEnabled())
			log.debugf("start send mail to " + to);
		Email email = new SimpleEmail();
		email.setHostName(smtpServer);
		//email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(user, passwd));
		email.setTLS(true);
		email.setFrom(from);
		email.setSubject(subject);
		email.setMsg(txt);
		email.addTo(to);
		//email.setSocketConnectionTimeout(5);
		//email.setSocketTimeout(15);
		email.send();
		if (log.isDebugEnabled())
			log.debugf("mail(to:" + to + ") add to queue");
	}
	
	private Thread mailSendThread;
	
	public void init() {
		//重置之前的错误记录,然后重新发一次
		dao.update(EmailInfo.class, Chain.make("retry", 2), Cnd.where("retry", ">=", 3));
		
		mailSendThread = new Thread() {
			
			public void run() {
				while (running) {
					try {
						final boolean[] re = new boolean[1];
						dao.run(new ConnCallback() {
							
							public void invoke(Connection conn) throws Exception {
								PreparedStatement ps = conn.prepareStatement("select count(1) from tb_email where retry<3 limit 1");
								ResultSet rs = ps.executeQuery();
								if (rs.next())
									re[0] = rs.getInt(1) != 0;
							}
						});
						if (!re[0]) {
							Thread.sleep(900);
							continue;
						}
						EmailInfo info = dao.fetch(EmailInfo.class, Cnd.where("retry", "<", 3).asc("retry"));
						if (info == null) //以免NPE
							Thread.sleep(900);
						else {
							try {
								send(info.getTo_add(), info.getSubject(), info.getTxt());
								dao.delete(EmailInfo.class, info.getId());
							} catch (Throwable e) {
								if (log.isDebugEnabled())
									log.debug("Fail to send mail to " + info.getTo_add(), e);
								dao.update(EmailInfo.class, Chain.makeSpecial("retry", "retry+1"), Cnd.where("id", "=", info.getId()));
							}
						}
					} catch (InterruptedException e) {
						break;
					} catch (ThreadDeath e) {
						break;
					} catch (Throwable e) {
						log.warn("ERROR when loop mail queue", e);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							break;
						}
					} finally {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							break;
						}
					}
				}
			}
		};
		mailSendThread.start();
	}
	
	private boolean running = true;
	
	@SuppressWarnings("deprecation")
	public void close() {
		try {
			running = false;
			if (mailSendThread.isAlive()) {
				Thread.sleep(1);//等1秒,然后退出
				if (mailSendThread.isAlive())
					mailSendThread.stop();
			}
		} catch (Throwable e) {}
	};
}
