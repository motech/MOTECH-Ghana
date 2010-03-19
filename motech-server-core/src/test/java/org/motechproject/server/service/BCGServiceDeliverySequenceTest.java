package org.motechproject.server.service;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.Service;
import org.motechproject.server.model.ServiceStatus;
import org.motechproject.server.service.impl.ServiceDeliverySequenceObsImpl;
import org.motechproject.server.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BCGServiceDeliverySequenceTest extends TestCase {

	ApplicationContext ctx;

	Integer patientId;
	RegistrarBean registrarBean;
	ServiceDeliverySequenceObsImpl bcgSequence;
	ServiceDelivery bcg1Service;

	@Override
	protected void setUp() throws Exception {
		patientId = 1;

		ctx = new ClassPathXmlApplicationContext(new String[] {
				"test-common-program-beans.xml",
				"services/child-bcg-service.xml" });
		bcgSequence = (ServiceDeliverySequenceObsImpl) ctx
				.getBean("childBCGService");
		bcg1Service = bcgSequence.getServices().get(0);

		// EasyMock setup in Spring config
		registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		bcgSequence = null;
		bcg1Service = null;
		registrarBean = null;
	}

	public void testUpdateBCGServicesNoPreviousServiceNoDelivery() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1); // age is 1 day
		Date patientBirthDate = calendar.getTime();

		Map<String, Service> servicesMap = new HashMap<String, Service>();

		Capture<Date> latestDateCapture = new Capture<Date>();
		Capture<Service> serviceCapture = new Capture<Service>();

		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getIncompleteServicesMap(patientId, bcgSequence
						.getName())).andReturn(servicesMap);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getObsId(eq(patientId), eq(bcgSequence
						.getServiceConceptName()), eq(bcgSequence
						.getServiceValueConceptName()), eq((Date) null),
						capture(latestDateCapture))).andReturn(null);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		registrarBean.saveService(capture(serviceCapture));

		replay(registrarBean);

		bcgSequence.updateServiceDeliveries(patientId, date);

		verify(registrarBean);

		Date latestDate = latestDateCapture.getValue();
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), latestDate);

		Service service = serviceCapture.getValue();
		assertEquals(bcg1Service.getName(), service.getService());
		assertEquals(bcgSequence.getName(), service.getSequence());
		assertEquals(patientId, service.getPatientId());
		assertNull("Delivery Id should be null for new service", service
				.getDeliveryId());
		assertEquals(ServiceStatus.INCOMPLETE, service.getStatus());

		assertEquals(patientBirthDate, service.getPreferredStart());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		assertEquals(calendar.getTime(), service.getPreferredEnd());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), service.getLatest());

		assertNull("Earliest Date should be null for BCG", service
				.getEarliest());
	}

	public void testUpdateBCGServicesNoPreviousServiceWithDelivery() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1); // age is 1 day
		Date patientBirthDate = calendar.getTime();

		Map<String, Service> servicesMap = new HashMap<String, Service>();
		Integer deliveryObsId = 1;

		Capture<Date> latestDateCapture = new Capture<Date>();

		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getIncompleteServicesMap(patientId, bcgSequence
						.getName())).andReturn(servicesMap);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getObsId(eq(patientId), eq(bcgSequence
						.getServiceConceptName()), eq(bcgSequence
						.getServiceValueConceptName()), eq((Date) null),
						capture(latestDateCapture))).andReturn(deliveryObsId);

		replay(registrarBean);

		bcgSequence.updateServiceDeliveries(patientId, date);

		verify(registrarBean);

		Date latestDate = latestDateCapture.getValue();
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), latestDate);
	}

	public void testUpdateBCGServicesWithPreviousServiceWithDelivery() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1); // age is 1 day
		Date patientBirthDate = calendar.getTime();

		Map<String, Service> servicesMap = new HashMap<String, Service>();
		Service previousService = new Service();
		previousService.setId(2L);
		previousService.setPatientId(patientId);
		previousService.setService(bcg1Service.getName());
		previousService.setSequence(bcgSequence.getName());
		previousService.setStatus(ServiceStatus.INCOMPLETE);
		previousService.setPreferredStart(patientBirthDate);
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		previousService.setPreferredEnd(calendar.getTime());
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		previousService.setLatest(calendar.getTime());
		servicesMap.put("BCG1", previousService);

		Integer deliveryObsId = 1;

		Capture<Date> latestDateCapture = new Capture<Date>();
		Capture<Service> serviceCapture = new Capture<Service>();

		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getIncompleteServicesMap(patientId, bcgSequence
						.getName())).andReturn(servicesMap);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getObsId(eq(patientId), eq(bcgSequence
						.getServiceConceptName()), eq(bcgSequence
						.getServiceValueConceptName()), eq((Date) null),
						capture(latestDateCapture))).andReturn(deliveryObsId);
		registrarBean.saveService(capture(serviceCapture));

		replay(registrarBean);

		bcgSequence.updateServiceDeliveries(patientId, date);

		verify(registrarBean);

		Date latestDate = latestDateCapture.getValue();
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), latestDate);

		Service service = serviceCapture.getValue();
		assertEquals(previousService.getId(), service.getId());
		assertEquals(bcg1Service.getName(), service.getService());
		assertEquals(bcgSequence.getName(), service.getSequence());
		assertEquals(patientId, service.getPatientId());
		assertEquals(deliveryObsId, service.getDeliveryId());
		assertEquals(ServiceStatus.COMPLETE, service.getStatus());

		assertEquals(patientBirthDate, service.getPreferredStart());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		assertEquals(calendar.getTime(), service.getPreferredEnd());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), service.getLatest());

		assertNull("Earliest Date should be null for BCG", service
				.getEarliest());
	}

	public void testUpdateBCGServicesWithPreviousServiceNoDelivery() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1); // age is 1 day
		Date patientBirthDate = calendar.getTime();

		Map<String, Service> servicesMap = new HashMap<String, Service>();
		Service previousService = new Service();
		previousService.setId(2L);
		previousService.setPatientId(patientId);
		previousService.setService(bcg1Service.getName());
		previousService.setSequence(bcgSequence.getName());
		previousService.setStatus(ServiceStatus.INCOMPLETE);
		previousService.setPreferredStart(patientBirthDate);
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		previousService.setPreferredEnd(calendar.getTime());
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		previousService.setLatest(calendar.getTime());
		servicesMap.put("BCG1", previousService);

		Capture<Date> latestDateCapture = new Capture<Date>();
		Capture<Service> serviceCapture = new Capture<Service>();

		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getIncompleteServicesMap(patientId, bcgSequence
						.getName())).andReturn(servicesMap);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getObsId(eq(patientId), eq(bcgSequence
						.getServiceConceptName()), eq(bcgSequence
						.getServiceValueConceptName()), eq((Date) null),
						capture(latestDateCapture))).andReturn(null);
		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		registrarBean.saveService(capture(serviceCapture));

		replay(registrarBean);

		bcgSequence.updateServiceDeliveries(patientId, date);

		verify(registrarBean);

		Date latestDate = latestDateCapture.getValue();
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), latestDate);

		Service service = serviceCapture.getValue();
		assertEquals(previousService.getId(), service.getId());
		assertEquals(bcg1Service.getName(), service.getService());
		assertEquals(bcgSequence.getName(), service.getSequence());
		assertEquals(patientId, service.getPatientId());
		assertNull("Delivery Id should be null for new service", service
				.getDeliveryId());
		assertEquals(ServiceStatus.INCOMPLETE, service.getStatus());

		assertEquals(patientBirthDate, service.getPreferredStart());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		assertEquals(calendar.getTime(), service.getPreferredEnd());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), service.getLatest());

		assertNull("Earliest Date should be null for BCG", service
				.getEarliest());
	}

	public void testUpdateBCGServicesWithPreviousServicePastAge() {
		Date date = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.YEAR, -2); // age is 2 years
		Date patientBirthDate = calendar.getTime();

		List<Service> servicesList = new ArrayList<Service>();
		Service previousService = new Service();
		previousService.setId(2L);
		previousService.setPatientId(patientId);
		previousService.setService(bcg1Service.getName());
		previousService.setSequence(bcgSequence.getName());
		previousService.setStatus(ServiceStatus.INCOMPLETE);
		previousService.setPreferredStart(patientBirthDate);
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		previousService.setPreferredEnd(calendar.getTime());
		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		previousService.setLatest(calendar.getTime());
		servicesList.add(previousService);

		Capture<Service> serviceCapture = new Capture<Service>();

		expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
				patientBirthDate);
		expect(
				registrarBean.getIncompleteServices(patientId, bcgSequence
						.getName())).andReturn(servicesList);
		registrarBean.saveService(capture(serviceCapture));

		replay(registrarBean);

		bcgSequence.updateServiceDeliveries(patientId, date);

		verify(registrarBean);

		Service service = serviceCapture.getValue();
		assertEquals(previousService.getId(), service.getId());
		assertEquals(bcg1Service.getName(), service.getService());
		assertEquals(bcgSequence.getName(), service.getSequence());
		assertEquals(patientId, service.getPatientId());
		assertNull("Delivery Id should be null without delivery", service
				.getDeliveryId());
		assertEquals(ServiceStatus.MISSED, service.getStatus());

		assertEquals(patientBirthDate, service.getPreferredStart());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.DATE, 2);
		assertEquals(calendar.getTime(), service.getPreferredEnd());

		calendar.setTime(patientBirthDate);
		calendar.add(Calendar.YEAR, 1);
		assertEquals(calendar.getTime(), service.getLatest());

		assertNull("Earliest Date should be null for BCG", service
				.getEarliest());
	}

}
