package org.motech.tests;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motech.dao.SimpleDao;
import org.motech.model.Log;
import org.motech.model.LogType;
import org.motech.svc.Logger;
import org.motech.svc.LoggerBean;

/**
 * Unit test for Logger service.
 * 
 * @author batkinson
 * 
 */
public class LoggerTest extends TestCase {

	SimpleDao mockDao;
	Logger logger;

	@Override
	protected void setUp() throws Exception {
		mockDao = createMock(SimpleDao.class);
		LoggerBean loggerBean = new LoggerBean();
		loggerBean.setDao(mockDao);
		logger = loggerBean;
	}

	public void testLog() {
		Capture<Log> lCap = new Capture<Log>();
		mockDao.saveLog(capture(lCap));
		replay(mockDao);

		logger.log(LogType.success, "Test Log");

		verify(mockDao);

		Log log = lCap.getValue();
		assertEquals(LogType.success, log.getType());
		assertEquals("Test Log", log.getMessage());
		assertNotNull(log.getDate());
	}

	public void testGetLogs() {
		List<Log> sampleLogs = new ArrayList<Log>();
		Log log = new Log();
		log.setDate(new Date());
		log.setType(LogType.failure);
		log.setMessage("Sample Message");
		log.setId(3L);
		sampleLogs.add(log);
		expect(mockDao.getLogs()).andReturn(sampleLogs);
		replay(mockDao);

		List<Log> logs = logger.getLogs();

		verify(mockDao);

		assertEquals(sampleLogs, logs);
	}
}
