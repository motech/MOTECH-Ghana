package org.motech.svc;

import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.motech.model.Log;
import org.motech.model.LogType;

@Stateless
public class LoggerBean implements Logger {

	@PersistenceContext
	EntityManager em;

	public void log(LogType type, String message) {
		Log l = new Log();
		l.setDate(new Date());
		l.setType(type);
		l.setMessage(message);

		em.persist(l);
	}

}
