package org.motechproject.server.omod.web.controller;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.sql.Time;
import java.text.ParseException;

import junit.framework.TestCase;

import org.easymock.Capture;
import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.User;
import org.springframework.ui.ModelMap;

public class MotechModuleFormControllerTest extends TestCase {

	RegistrarBean registrarBean;
	MotechModuleFormController controller;
	ContextService contextService;
	MotechService motechService;

	@Override
	protected void setUp() {
		registrarBean = createMock(RegistrarBean.class);
		motechService = createMock(MotechService.class);
		contextService = createMock(ContextService.class);
		controller = new MotechModuleFormController();
		controller.setRegistrarBean(registrarBean);
		controller.setContextService(contextService);
	}

	@Override
	protected void tearDown() {
		controller = null;
		registrarBean = null;
	}

	public void testRegisterClinicNoParent() throws Exception {
		String name = "Clinic Name";
		String parentId = "";
		Integer integerParentId = null;

		registrarBean.registerClinic(name, integerParentId);

		replay(registrarBean);

		controller.registerClinic(name, parentId);

		verify(registrarBean);
	}

	public void testRegisterClinicWithParent() throws Exception {
		String name = "Clinic Name";
		String parentId = "2";
		Integer integerParentId = 2;

		registrarBean.registerClinic(name, integerParentId);

		replay(registrarBean);

		controller.registerClinic(name, parentId);

		verify(registrarBean);
	}

	public void testRegiserNurse() throws Exception {
		String firstName = "First Name", lastName = "Last Name", phone = "7777777777", staffType = "CHO";

		User createdUser = new User();
		expect(
				registrarBean.registerNurse(firstName, lastName, phone,
						staffType)).andReturn(createdUser);

		replay(registrarBean);

		ModelMap model = new ModelMap();
		controller.registerNurse(firstName, lastName, phone, staffType, model);

		verify(registrarBean);

		assertNotNull(model.get("successMsg"));
	}

	public void testViewBlackoutForm() throws ParseException {

		Time startTime = Time.valueOf("07:00:00"), endTime = Time
				.valueOf("19:00:00");

		Blackout interval = new Blackout(startTime, endTime);
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(interval);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.viewBlackoutSettings(model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
		assertEquals(model.get("startTime"), startTime);
		assertEquals(model.get("endTime"), endTime);
	}

	public void testViewBlackoutFormNoData() throws ParseException {
		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getBlackoutSettings()).andReturn(null);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.viewBlackoutSettings(model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
	}

	public void testSaveBlackoutSettings() throws ParseException {

		String startTime = "07:00:00", endTime = "19:00:00";

		Capture<Blackout> boCap = new Capture<Blackout>();

		expect(contextService.getMotechService()).andReturn(motechService);

		expect(motechService.getBlackoutSettings()).andReturn(null);
		motechService.setBlackoutSettings(capture(boCap));

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller
				.saveBlackoutSettings(startTime, endTime, model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
		assertEquals(startTime, model.get("startTime").toString());
		assertEquals(endTime, model.get("endTime").toString());
		assertEquals(startTime, boCap.getValue().getStartTime().toString());
		assertEquals(endTime, boCap.getValue().getEndTime().toString());
	}

	public void testUpdateBlackoutSettings() throws ParseException {

		String startTime = "07:00:00", endTime = "19:00:00";

		Capture<Blackout> boCap = new Capture<Blackout>();

		expect(contextService.getMotechService()).andReturn(motechService);

		Blackout blackout = new Blackout(null, null);
		expect(motechService.getBlackoutSettings()).andReturn(blackout);
		motechService.setBlackoutSettings(capture(boCap));

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller
				.saveBlackoutSettings(startTime, endTime, model);

		verify(contextService, motechService);

		assertEquals("/module/motechmodule/blackout", path);
		assertEquals(startTime, model.get("startTime").toString());
		assertEquals(endTime, model.get("endTime").toString());
		assertEquals(startTime, boCap.getValue().getStartTime().toString());
		assertEquals(endTime, boCap.getValue().getEndTime().toString());
	}

	public void testLookupTroubledPhoneNoPhone() {

		String phone = null;

		ModelMap model = new ModelMap();
		String path = controller.handleTroubledPhone(phone, null, model);

		assertNull(model.get("troubledPhone"));
		assertEquals("/module/motechmodule/troubledphone", path);
	}

	public void testLookupTroubledPhone() {

		String phone = "378378373";
		TroubledPhone tp = new TroubledPhone();
		tp.setId(38903L);
		tp.setPhoneNumber(phone);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getTroubledPhone(phone)).andReturn(tp);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.handleTroubledPhone(phone, null, model);

		verify(contextService, motechService);

		assertEquals(tp, model.get("troubledPhone"));
		assertEquals("/module/motechmodule/troubledphone", path);
	}

	public void testRemoveTroubledPhone() {
		String phone = "378378373";
		TroubledPhone tp = new TroubledPhone();
		tp.setId(38903L);
		tp.setPhoneNumber(phone);

		expect(contextService.getMotechService()).andReturn(motechService);
		expect(motechService.getTroubledPhone(phone)).andReturn(tp);
		motechService.removeTroubledPhone(phone);

		replay(contextService, motechService);

		ModelMap model = new ModelMap();
		String path = controller.handleTroubledPhone(phone, true, model);

		verify(contextService, motechService);

		assertNull(model.get("troubledPhone"));
		assertEquals("redirect:/module/motechmodule/troubledphone.form", path);
	}
}
