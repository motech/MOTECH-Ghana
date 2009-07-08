package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.motech.model.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoggerBean implements Logger {

	private static Log log = LogFactory
			.getLog(LoggerBean.class);

	SessionFactory factory;

	@Autowired
	public void setSessionFactory(SessionFactory sf) {
		log.debug("setting session factory" + sf);
		factory = sf;
	}

	public void log(LogType type, String message) {
		Session session = factory.getCurrentSession();

		org.motech.model.Log l = new org.motech.model.Log();
		l.setDate(new Date());
		l.setType(type);
		l.setMessage(message);

		session.save(l);
	}

	@SuppressWarnings("unchecked")
	public List<org.motech.model.Log> getLogs() {
		Session session = factory.getCurrentSession();
		return (List<org.motech.model.Log>) session.createCriteria(
				org.motech.model.Log.class).list();
	}
}
