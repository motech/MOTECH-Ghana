package org.motechproject.server.svc.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.motechproject.server.omod.ContextService;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.DayOfWeek;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.AdministrationService;

public class RegistrarBeanImplTest extends TestCase {

	RegistrarBeanImpl regBean;

	ContextService contextService;
	AdministrationService adminService;

	@Override
	protected void setUp() throws Exception {
		contextService = createMock(ContextService.class);
		adminService = createMock(AdministrationService.class);

		regBean = new RegistrarBeanImpl();
		regBean.setContextService(contextService);
	}

	@Override
	protected void tearDown() throws Exception {
		regBean = null;
		contextService = null;
		adminService = null;
	}

	public void testDeterminePersonPrefDate() {
		DayOfWeek day = DayOfWeek.MONDAY;
		int hour = 9;
		int minute = 0;
		String timeAsString = "09:00";
		Date messageDate = new Date();

		Person person = new Person(1);
		PersonAttributeType dayType = new PersonAttributeType(1);
		dayType.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_DAY);
		PersonAttributeType timeType = new PersonAttributeType(2);
		timeType.setName(MotechConstants.PERSON_ATTRIBUTE_DELIVERY_TIME);
		person.addAttribute(new PersonAttribute(dayType, day.toString()));
		person.addAttribute(new PersonAttribute(timeType, timeAsString));

		Date prefDate = regBean.determineMessageStartDate(person, messageDate);

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertTrue(prefCal.after(messageCal));
		assertEquals(day.getCalendarValue(), prefCal.get(Calendar.DAY_OF_WEEK));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertEquals(0, prefCal.get(Calendar.SECOND));
	}

	public void testDetermineDefaultPrefDate() {
		DayOfWeek day = DayOfWeek.MONDAY;
		int hour = 9;
		int minute = 0;
		String timeAsString = "09:00";
		Date messageDate = new Date();

		Person person = new Person(1);

		expect(contextService.getAdministrationService()).andReturn(
				adminService).times(2);
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK))
				.andReturn(day.toString());
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY))
				.andReturn(timeAsString);

		replay(contextService, adminService);

		Date prefDate = regBean.determineMessageStartDate(person, messageDate);

		verify(contextService, adminService);

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertTrue(prefCal.after(messageCal));
		assertEquals(day.getCalendarValue(), prefCal.get(Calendar.DAY_OF_WEEK));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertEquals(0, prefCal.get(Calendar.SECOND));
	}

	public void testDetermineNoPrefDate() {
		Date messageDate = new Date();

		Person person = new Person(1);

		expect(contextService.getAdministrationService()).andReturn(
				adminService).times(2);
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_DAY_OF_WEEK))
				.andReturn(null);
		expect(
				adminService
						.getGlobalProperty(MotechConstants.GLOBAL_PROPERTY_TIME_OF_DAY))
				.andReturn(null);

		replay(contextService, adminService);

		Date prefDate = regBean.determineMessageStartDate(person, messageDate);

		verify(contextService, adminService);

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertEquals(messageCal.get(Calendar.YEAR), prefCal.get(Calendar.YEAR));
		assertEquals(messageCal.get(Calendar.MONTH), prefCal
				.get(Calendar.MONTH));
		assertEquals(messageCal.get(Calendar.DATE), prefCal.get(Calendar.DATE));
		assertEquals(messageCal.get(Calendar.HOUR_OF_DAY), prefCal
				.get(Calendar.HOUR_OF_DAY));
		assertEquals(messageCal.get(Calendar.MINUTE), prefCal
				.get(Calendar.MINUTE));
		assertEquals(0, prefCal.get(Calendar.SECOND));
	}

	public void testAdjustDateTime() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 22);
		calendar.set(Calendar.MINUTE, 13);
		calendar.set(Calendar.SECOND, 54);
		Date messageDate = calendar.getTime();

		int hour = 15;
		int minute = 37;
		int second = 0;

		Calendar timeCalendar = Calendar.getInstance();
		timeCalendar.set(Calendar.YEAR, 1986);
		timeCalendar.set(Calendar.MONTH, 6);
		timeCalendar.set(Calendar.DAY_OF_MONTH, 4);
		timeCalendar.set(Calendar.HOUR_OF_DAY, hour);
		timeCalendar.set(Calendar.MINUTE, minute);
		timeCalendar.set(Calendar.SECOND, 34);

		Date prefDate = regBean.adjustTime(messageDate, timeCalendar.getTime());

		Calendar messageCal = Calendar.getInstance();
		messageCal.setTime(messageDate);
		Calendar prefCal = Calendar.getInstance();
		prefCal.setTime(prefDate);

		assertEquals(messageCal.get(Calendar.YEAR), prefCal.get(Calendar.YEAR));
		assertEquals(messageCal.get(Calendar.MONTH), prefCal
				.get(Calendar.MONTH));
		// TODO: Slap Matt for this
		// assertEquals(messageCal.get(Calendar.DATE), prefCal.get(Calendar.DATE));
		assertFalse("Hour not updated",
				messageCal.get(Calendar.HOUR_OF_DAY) == prefCal
						.get(Calendar.HOUR_OF_DAY));
		assertEquals(hour, prefCal.get(Calendar.HOUR_OF_DAY));
		assertFalse("Minute not updated",
				messageCal.get(Calendar.MINUTE) == prefCal.get(Calendar.MINUTE));
		assertEquals(minute, prefCal.get(Calendar.MINUTE));
		assertFalse("Second not updated",
				messageCal.get(Calendar.SECOND) == prefCal.get(Calendar.SECOND));
		assertEquals(second, prefCal.get(Calendar.SECOND));
	}
}