package org.motech.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SampleServiceImpl implements SampleService {

	private static Log log = LogFactory.getLog(SampleServiceImpl.class);

	SessionFactory factory;

	public void doSomething() {
		Session session = factory.getCurrentSession();
		SampleEntity e = new SampleEntity();
		e.setName("Brent");
		session.save(e);
	}

	@Autowired
	public void setSessionFactory(SessionFactory sf) {
		log.debug("setting session factory" + sf);
		factory = sf;
	}
}
