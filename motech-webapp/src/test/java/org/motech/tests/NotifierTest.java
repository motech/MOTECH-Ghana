package org.motech.tests;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.motech.dao.SimpleDao;
import org.motech.model.FutureServiceDelivery;
import org.motech.model.LogType;
import org.motech.model.Nurse;
import org.motech.model.Patient;
import org.motech.svc.Logger;
import org.motech.svc.Notifier;
import org.motech.svc.NotifierBean;

/**
 * Unit test for notifier service.
 * 
 * @author batkinson
 * 
 */
public class NotifierTest extends TestCase {

	Logger mockLogger;
	SimpleDao mockDao;
	Notifier notifier;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mockLogger = createMock(Logger.class);
		mockDao = createMock(SimpleDao.class);
		NotifierBean notifierBean = new NotifierBean();
		notifierBean.setDao(mockDao);
		notifierBean.setLogger(mockLogger);
		notifier = notifierBean;
	}

	public void testSendNoNotifications() {
		int interval = 60;
		List<FutureServiceDelivery> ds = new ArrayList<FutureServiceDelivery>();
		expect(
				mockDao.getFutureServiceDeliveries((Date) anyObject(),
						(Date) anyObject())).andReturn(ds);
		replay(mockDao);
		notifier.sendNotifications(interval);
		verify(mockDao);
	}

	public void testSendOneNotifications() {
		int interval = 60;
		List<FutureServiceDelivery> fsds = new ArrayList<FutureServiceDelivery>();
		FutureServiceDelivery fsd = new FutureServiceDelivery();
		fsd.setNurse(new Nurse());
		fsd.setPatient(new Patient());
		fsds.add(fsd);
		expect(
				mockDao.getFutureServiceDeliveries((Date) anyObject(),
						(Date) anyObject())).andReturn(fsds);
		mockLogger.log((LogType) anyObject(), (String) anyObject());
		mockDao.updateFutureServiceDelivery(fsds.get(0));
		replay(mockDao, mockLogger);
		notifier.sendNotifications(interval);
		verify(mockDao, mockLogger);
	}

	public void testSendManyNotifications() {
		int interval = 60;
		List<FutureServiceDelivery> fsds = new ArrayList<FutureServiceDelivery>();
		int numNotifications = 5;

		for (int i = 0; i < numNotifications; i++) {
			FutureServiceDelivery fsd = new FutureServiceDelivery();
			fsd.setNurse(new Nurse());
			fsd.setPatient(new Patient());
			fsds.add(fsd);
		}

		expect(
				mockDao.getFutureServiceDeliveries((Date) anyObject(),
						(Date) anyObject())).andReturn(fsds);
		for (int i = 0; i < numNotifications; i++) {
			mockLogger.log((LogType) anyObject(), (String) anyObject());
			mockDao.updateFutureServiceDelivery(fsds.get(i));
		}
		replay(mockDao, mockLogger);
		notifier.sendNotifications(interval);
		verify(mockDao, mockLogger);
	}
}
