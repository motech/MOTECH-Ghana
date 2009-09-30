package org.motech.event;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.openmrs.Patient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RegimenNumStateChangeTest extends TestCase {

	ApplicationContext ctx;

	Patient patient;
	Date obs1;
	Date obs2;
	Date obs3;
	Date obs4;
	Date obs5;
	PatientObsService patientObsService;
	Regimen polioRegimen;
	RegimenState polioState1;
	RegimenState polioState2;
	RegimenState polioState3;
	RegimenState polioState4;
	RegimenState polioState5;
	RegimenState currentPatientState;

	@Override
	protected void setUp() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.MONTH, -1); // 1 month in past

		patient = new Patient();
		patient.setBirthdate(calendar.getTime()); // 1 month old

		obs1 = calendar.getTime();

		calendar.add(Calendar.DATE, 6 * 7 + 1); // 6 weeks and 1 day
		obs2 = calendar.getTime();

		calendar.add(Calendar.DATE, 4 * 7 + 2); // 4 weeks and 2 days
		obs3 = calendar.getTime();

		calendar.add(Calendar.DATE, 4 * 7 + 3); // 4 weeks and 3 days
		obs4 = calendar.getTime();

		calendar.add(Calendar.DATE, 4 * 7 + 4); // 4 weeks and 4 days
		obs5 = calendar.getTime();

		ctx = new ClassPathXmlApplicationContext(
				"polio-regimen-test-context.xml");
		polioRegimen = (Regimen) ctx.getBean("polioRegimen");

		polioState1 = (RegimenState) ctx.getBean("polioState1");
		polioState2 = (RegimenState) ctx.getBean("polioState2");
		polioState3 = (RegimenState) ctx.getBean("polioState3");
		polioState4 = (RegimenState) ctx.getBean("polioState4");
		polioState5 = (RegimenState) ctx.getBean("polioState5");

		// EasyMock setup in Spring config
		patientObsService = (PatientObsService) ctx
				.getBean("patientObsService");
	}

	@Override
	protected void tearDown() throws Exception {
		ctx = null;
		polioRegimen = null;
		polioState1 = null;
		polioState2 = null;
		polioState3 = null;
		polioState4 = null;
		polioState5 = null;
		patientObsService = null;
	}

	public void testDetermineStartState() {
		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(0).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.getState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState1.getName());
	}

	public void testDetermineSecondState() {
		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(1).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.getState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState2.getName());
	}

	public void testDetermineEndState() {
		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(4).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.getState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState5.getName());
	}

	public void testMoveState() {
		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(2).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.getState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState3.getName());

		// State will change with the number of Obs increasing
		reset(patientObsService);

		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(3).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.updateState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState4.getName());
	}

	public void testNotMoveState() {
		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(3).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.getState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState4.getName());

		// State does not change with the same number of Obs
		reset(patientObsService);

		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(3).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.updateState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState4.getName());
	}

	public void testNotMoveEndState() {
		expect(
				patientObsService.getNumberOfObs(patient, polioRegimen
						.getConceptName(), polioRegimen.getConceptValue()))
				.andReturn(4).atLeastOnce();

		replay(patientObsService);

		currentPatientState = polioRegimen.getState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState5.getName());

		// Future calls to updateState return the end state with no actions
		reset(patientObsService);

		replay(patientObsService);

		currentPatientState = polioRegimen.updateState(patient);

		verify(patientObsService);

		assertEquals(currentPatientState.getName(), polioState5.getName());

		reset(patientObsService);
	}

}
