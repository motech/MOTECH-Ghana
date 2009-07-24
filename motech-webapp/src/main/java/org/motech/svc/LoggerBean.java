package org.motech.svc;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motech.dao.SimpleDao;
import org.motech.model.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoggerBean implements Logger {

	private static Log log = LogFactory.getLog(LoggerBean.class);

	SimpleDao dao;

	@Autowired
	public void setDao(SimpleDao dao) {
		log.debug("setting dao" + dao);
		this.dao = dao;
	}

	public void log(LogType type, String message) {

		org.motech.model.Log l = new org.motech.model.Log();
		l.setDate(new Date());
		l.setType(type);
		l.setMessage(message);

		dao.saveLog(l);
	}

	public List<org.motech.model.Log> getLogs() {
		return dao.getLogs();
	}
}
