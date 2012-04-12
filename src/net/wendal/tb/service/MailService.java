package net.wendal.tb.service;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

@IocBean
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
	

	public boolean send(String to, String subject, String txt) {
		if (log.isDebugEnabled())
			log.debugf("start send mail to " + to);
		Email email = new SimpleEmail();
		email.setHostName(smtpServer);
		//email.setSmtpPort(587);
		email.setAuthenticator(new DefaultAuthenticator(user, passwd));
		email.setTLS(true);
		try {
			email.setFrom(from);
			email.setSubject(subject);
			email.setMsg(txt);
			email.addTo(to);
			email.send();
			if (log.isDebugEnabled())
				log.debugf("Complete send mail to " + to);
		} catch (Throwable e) {
			if (log.isDebugEnabled())
				log.debug("Fail to send mail to " + to, e);
			return false;
		}
		return true;
	}
}
