package org.motechproject.server.omod.impl;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.event.impl.ExpectedCareMessageProgram;
import org.motechproject.server.model.*;
import org.motechproject.server.svc.MessageProgramService;
import org.motechproject.server.svc.RegistrarBean;
import org.openmrs.Patient;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ExpectedCareMessageProgramTest extends BaseModuleContextSensitiveTest {

    ApplicationContext ctx;

    ExpectedCareMessageProgram careProgram;

    @Autowired
    MessageProgramService messageProgramService;

    MessageProgramEnrollment enrollment;
    Patient patient;
    Integer patientId;
    private RegistrarBean registrarBean;

    @Before
    public void setUp() throws Exception {

        executeDataSet("message-program-dataset.xml");

        careProgram = (ExpectedCareMessageProgram) messageProgramService.program("Expected Care Message Program");
        registrarBean = EasyMock.createMock(RegistrarBean.class);
        careProgram.setRegistrarBean(registrarBean);

        enrollment = new MessageProgramEnrollment();
        patientId = 1;
        enrollment.setPersonId(1);
        patient = new Patient(patientId);
    }

    @After
    public void tearDown() throws Exception {
        ctx = null;
        careProgram = null;
        registrarBean = null;
        enrollment = null;
        patient = null;
    }


    @Test
    public void testNoActionForNoData() {
        Integer maxReminders = 3;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();


        expect(registrarBean.getMaxPatientCareReminders()).andReturn(maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(schMsgs);

        replay(registrarBean);

        MessageProgramState state = careProgram.determineState(enrollment,new Date());

        verify(registrarBean);

        assertNull("State returned is not null", state);
    }

    @Test
    public void testNoExpectedWithScheduled() {
        Integer maxReminders = 3;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();

        String realMessageKey1 = "tt.upcoming";
        String realMessageKey2 = "pnc.baby.overdue";
        String fakeMessageKey = "fakecare.upcoming";

        Date date = new Date();

        MessageDefinition msg1Def = new MessageDefinition();
        msg1Def.setMessageKey(realMessageKey1);
        MessageDefinition msg2Def = new MessageDefinition();
        msg2Def.setMessageKey(realMessageKey2);
        MessageDefinition msg3Def = new MessageDefinition();
        msg3Def.setMessageKey(fakeMessageKey);

        ScheduledMessage msg1 = new ScheduledMessage();
        msg1.setScheduledFor(date);
        msg1.setMessage(msg1Def);
        msg1.setCare("TT1");
        ScheduledMessage msg2 = new ScheduledMessage();
        msg2.setScheduledFor(date);
        msg2.setMessage(msg2Def);
        msg2.setCare("PNC1");
        ScheduledMessage msg3 = new ScheduledMessage();
        msg3.setScheduledFor(date);
        msg3.setMessage(msg3Def);
        msg3.setCare("FAKE1");

        schMsgs.add(msg1);
        schMsgs.add(msg2);
        schMsgs.add(msg3);

        Capture<List<ScheduledMessage>> removedCare1Msgs = new Capture<List<ScheduledMessage>>();
        Capture<List<ScheduledMessage>> removedCare2Msgs = new Capture<List<ScheduledMessage>>();
        Capture<List<ScheduledMessage>> removedEnrollMsgs = new Capture<List<ScheduledMessage>>();




        expect(registrarBean.getMaxPatientCareReminders()).andReturn(
                maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(schMsgs);

        registrarBean.removeUnsentMessages(capture(removedCare1Msgs));
        registrarBean.removeUnsentMessages(capture(removedCare2Msgs));

        registrarBean.removeUnsentMessages(capture(removedEnrollMsgs));

        replay(registrarBean);

        MessageProgramState state = careProgram.determineState(enrollment,
                new Date());

       
        assertNull("State returned is not null", state);

        List<ScheduledMessage> care1Msgs = removedCare1Msgs.getValue();
        assertEquals(1, care1Msgs.size());
        ScheduledMessage care1Msg = care1Msgs.get(0);
        assertEquals(realMessageKey1, care1Msg.getMessage().getMessageKey());

        List<ScheduledMessage> care2Msgs = removedCare2Msgs.getValue();
        assertEquals(1, care2Msgs.size());
        ScheduledMessage care2Msg = care2Msgs.get(0);
        assertEquals(realMessageKey2, care2Msg.getMessage().getMessageKey());

        List<ScheduledMessage> enrollMsgs = removedEnrollMsgs.getValue();
        assertEquals(1, enrollMsgs.size());
        ScheduledMessage enrollMsg = enrollMsgs.get(0);
        assertEquals(fakeMessageKey, enrollMsg.getMessage().getMessageKey());
    }

    @Test
    public void testExpectedWithNoScheduled() {
        Integer maxReminders = 3;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // Upcoming ANC Encounter
        ExpectedEncounter enc1 = new ExpectedEncounter();
        enc1.setGroup("ANC");
        enc1.setName("ANC1");
        calendar.add(Calendar.DATE, 3);
        enc1.setDueEncounterDatetime(calendar.getTime());
        calendar.add(Calendar.DATE, 20);
        enc1.setLateEncounterDatetime(calendar.getTime());
        // Overdue BCG Obs
        ExpectedObs obs1 = new ExpectedObs();
        obs1.setGroup("BCG");
        obs1.setName("BCG");
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -10);
        obs1.setDueObsDatetime(calendar.getTime());
        obs1.setLateObsDatetime(calendar.getTime());

        expEnc.add(enc1);
        expObs.add(obs1);

        String enc1MessageKey = "anc.upcoming";
        String obs1MessageKey = "bcg.overdue";

        Capture<Date> message1DateCapture = new Capture<Date>();
        Capture<Date> message2DateCapture = new Capture<Date>();

        expect(registrarBean.getMaxPatientCareReminders()).andReturn(
                maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(
                schMsgs);

        expect(
                registrarBean.scheduleCareMessage(eq(enc1MessageKey),
                        eq(enrollment), capture(message1DateCapture), eq(true),
                        eq(enc1.getName()), eq(date))).andReturn(
                new ScheduledMessage());
        expect(
                registrarBean.scheduleCareMessage(eq(obs1MessageKey),
                        eq(enrollment), capture(message2DateCapture), eq(true),
                        eq(obs1.getName()), eq(date))).andReturn(
                new ScheduledMessage());

        replay(registrarBean);

        MessageProgramState state = careProgram
                .determineState(enrollment, date);

        verify(registrarBean);

        assertNull("State returned is not null", state);

        Date message1Date = message1DateCapture.getValue();
        assertTrue("Message 1 date is not before ANC due date", message1Date
                .before(enc1.getDueEncounterDatetime()));

        Date message2Date = message1DateCapture.getValue();
        assertTrue("Message 2 date is not after BCG late date", message2Date
                .after(obs1.getLateObsDatetime()));
    }

    @Test
    public void testExpectedWithReminderScheduled() {
        Integer maxReminders = 3;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // Overdue TT Obs
        ExpectedObs obs1 = new ExpectedObs();
        obs1.setGroup("TT");
        obs1.setName("TT1");
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -30);
        obs1.setDueObsDatetime(calendar.getTime());
        obs1.setLateObsDatetime(calendar.getTime());
        expObs.add(obs1);

        String obs1MessageKey = "tt.overdue";

        MessageDefinition msg1Def = new MessageDefinition();
        msg1Def.setMessageKey(obs1MessageKey);
        ScheduledMessage msg1 = new ScheduledMessage();
        calendar.add(Calendar.DATE, 7);
        msg1.setScheduledFor(calendar.getTime());
        msg1.setMessage(msg1Def);
        msg1.setCare("TT1");
        schMsgs.add(msg1);

        Capture<ScheduledMessage> reminderSchMsg = new Capture<ScheduledMessage>();
        Capture<Date> reminderDate = new Capture<Date>();

        expect(registrarBean.getMaxPatientCareReminders()).andReturn(
                maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(
                schMsgs);

        registrarBean.verifyMessageAttemptDate(eq(msg1), eq(true), eq(date));
        registrarBean.addMessageAttempt(capture(reminderSchMsg),
                capture(reminderDate), (Date) anyObject(), eq(true), eq(date));

        replay(registrarBean);

        MessageProgramState state = careProgram
                .determineState(enrollment, date);

        verify(registrarBean);

        assertNull("State returned is not null", state);

        ScheduledMessage reminderMsg = reminderSchMsg.getValue();
        assertEquals(msg1, reminderMsg);

        Date reminderAttemptDate = reminderDate.getValue();
        assertTrue("Reminder date is not after TT late date",
                reminderAttemptDate.after(obs1.getLateObsDatetime()));
    }

    @Test
    public void testUpdateExpectedWithExistingReminder() {
        Integer maxReminders = 3;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // Overdue TT Obs
        ExpectedObs obs1 = new ExpectedObs();
        obs1.setGroup("TT");
        obs1.setName("TT1");
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -3);
        obs1.setDueObsDatetime(calendar.getTime());
        obs1.setLateObsDatetime(calendar.getTime());
        expObs.add(obs1);

        String obs1MessageKey = "tt.overdue";

        MessageDefinition msg1Def = new MessageDefinition();
        msg1Def.setMessageKey(obs1MessageKey);
        ScheduledMessage msg1 = new ScheduledMessage();
        calendar.add(Calendar.DATE, 7);
        msg1.setScheduledFor(calendar.getTime());
        msg1.setMessage(msg1Def);
        msg1.setCare("TT1");
        Message msgAttempt1 = new Message();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        msgAttempt1.setAttemptDate(calendar.getTime());
        msg1.getMessageAttempts().add(msgAttempt1);
        schMsgs.add(msg1);

        expect(registrarBean.getMaxPatientCareReminders()).andReturn(
                maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(
                schMsgs);

        registrarBean.verifyMessageAttemptDate(eq(msg1), eq(true), eq(date));

        replay(registrarBean);

        MessageProgramState state = careProgram
                .determineState(enrollment, date);

        verify(registrarBean);

        assertNull("State returned is not null", state);
    }

    @Test
    public void testUpdateExpectedWithMaxRemindersScheduled() {
        Integer maxReminders = 1;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // Overdue TT Obs
        ExpectedObs obs1 = new ExpectedObs();
        obs1.setGroup("TT");
        obs1.setName("TT1");
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -30);
        obs1.setDueObsDatetime(calendar.getTime());
        obs1.setLateObsDatetime(calendar.getTime());
        expObs.add(obs1);

        String obs1MessageKey = "tt.overdue";

        MessageDefinition msg1Def = new MessageDefinition();
        msg1Def.setMessageKey(obs1MessageKey);
        ScheduledMessage msg1 = new ScheduledMessage();
        calendar.add(Calendar.DATE, 7);
        msg1.setScheduledFor(calendar.getTime());
        msg1.setMessage(msg1Def);
        msg1.setCare("TT1");
        // Add max number of attempts allowed (1)
        Message msgAttempt1 = new Message();
        msgAttempt1.setAttemptDate(calendar.getTime());
        msg1.getMessageAttempts().add(msgAttempt1);
        schMsgs.add(msg1);

        expect(registrarBean.getMaxPatientCareReminders()).andReturn(
                maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(
                schMsgs);

        registrarBean.verifyMessageAttemptDate(eq(msg1), eq(true), eq(date));

        replay(registrarBean);

        MessageProgramState state = careProgram
                .determineState(enrollment, date);

        verify(registrarBean);

        assertNull("State returned is not null", state);
    }

    @Test
    public void testUpdateExpectedWithScheduled() {
        Integer maxReminders = 3;
        List<ExpectedEncounter> expEnc = new ArrayList<ExpectedEncounter>();
        List<ExpectedObs> expObs = new ArrayList<ExpectedObs>();
        List<ScheduledMessage> schMsgs = new ArrayList<ScheduledMessage>();

        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        // Upcoming TT Obs
        ExpectedObs obs1 = new ExpectedObs();
        obs1.setGroup("TT");
        obs1.setName("TT1");
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 3);
        obs1.setDueObsDatetime(calendar.getTime());
        obs1.setLateObsDatetime(calendar.getTime());
        expObs.add(obs1);

        String obs1MessageKey = "tt.upcoming";

        MessageDefinition msg1Def = new MessageDefinition();
        msg1Def.setMessageKey(obs1MessageKey);
        ScheduledMessage msg1 = new ScheduledMessage();
        calendar.add(Calendar.DATE, -7);
        msg1.setScheduledFor(calendar.getTime());
        msg1.setMessage(msg1Def);
        msg1.setCare("TT1");
        schMsgs.add(msg1);

        expect(registrarBean.getMaxPatientCareReminders()).andReturn(
                maxReminders);
        expect(registrarBean.getPatientById(patientId)).andReturn(patient);
        expect(registrarBean.getExpectedEncounters(patient)).andReturn(expEnc);
        expect(registrarBean.getExpectedObs(patient)).andReturn(expObs);
        expect(registrarBean.getScheduledMessages(enrollment)).andReturn(
                schMsgs);

        registrarBean.verifyMessageAttemptDate(eq(msg1), eq(true), eq(date));

        replay(registrarBean);

        MessageProgramState state = careProgram
                .determineState(enrollment, date);

        verify(registrarBean);

        assertNull("State returned is not null", state);
    }
}
