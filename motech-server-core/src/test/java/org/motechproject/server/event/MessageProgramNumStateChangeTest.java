/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.event;

import junit.framework.TestCase;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageProgramStateTransition;
import org.motechproject.server.svc.RegistrarBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Calendar;
import java.util.Date;

import static org.easymock.EasyMock.*;

public class MessageProgramNumStateChangeTest extends TestCase {

    ApplicationContext ctx;

    Integer patientId;
    MessageProgramEnrollment enrollment;
    Date obs1;
    Date obs2;
    Date obs3;
    Date obs4;
    Date obs5;
    RegistrarBean registrarBean;
    MessageProgram polioProgram;
    MessageProgramState polioState1;
    MessageProgramState polioState2;
    MessageProgramState polioState3;
    MessageProgramState polioState4;
    MessageProgramState polioState5;
    MessageProgramState currentPatientState;
    private String polioConceptName;
    private String polioConceptValue;

    @Override
    protected void setUp() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1); // 1 month in past

        obs1 = calendar.getTime();

        calendar.add(Calendar.DATE, 6 * 7 + 1); // 6 weeks and 1 day
        obs2 = calendar.getTime();

        calendar.add(Calendar.DATE, 4 * 7 + 2); // 4 weeks and 2 days
        obs3 = calendar.getTime();

        calendar.add(Calendar.DATE, 4 * 7 + 3); // 4 weeks and 3 days
        obs4 = calendar.getTime();

        calendar.add(Calendar.DATE, 4 * 7 + 4); // 4 weeks and 4 days
        obs5 = calendar.getTime();

        patientId = 1;

        enrollment = new MessageProgramEnrollment();
        enrollment.setPersonId(patientId);

        ctx = new ClassPathXmlApplicationContext(new String[]{
                "test-common-program-beans.xml",
                "polio-program-test-context.xml"});
        polioProgram = (MessageProgram) ctx.getBean("polioProgram");

        polioState1 = (MessageProgramState) ctx.getBean("polioState1");
        polioState2 = (MessageProgramState) ctx.getBean("polioState2");
        polioState3 = (MessageProgramState) ctx.getBean("polioState3");
        polioState4 = (MessageProgramState) ctx.getBean("polioState4");
        polioState5 = (MessageProgramState) ctx.getBean("polioState5");

        polioConceptName = polioState1.getConceptName();

        polioConceptValue = polioState1.getConceptValue();

        // EasyMock setup in Spring config
        registrarBean = (RegistrarBean) ctx.getBean("registrarBean");
    }

    @Override
    protected void tearDown() throws Exception {
        enrollment = null;
        ctx = null;
        polioProgram = null;
        polioState1 = null;
        polioState2 = null;
        polioState3 = null;
        polioState4 = null;
        polioState5 = null;
        registrarBean = null;
    }

    public void testDetermineStartState() {
        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(0).atLeastOnce();
        expect(registrarBean.getPatientBirthDate(patientId)).andReturn(
                new Date());

        replay(registrarBean);

        currentPatientState = polioProgram.determineState(enrollment, new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState1.getName());
    }

    public void testDetermineSecondState() {
        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName,polioConceptValue))
                .andReturn(1).atLeastOnce();
        expect(
                registrarBean.getLastObsDate(patientId, polioConceptName,polioConceptValue))
                .andReturn(new Date());

        replay(registrarBean);

        currentPatientState = polioProgram.determineState(enrollment, new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState2.getName());
    }

    public void testDetermineEndState() {
        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName,polioConceptValue))
                .andReturn(4).atLeastOnce();

        replay(registrarBean);

        currentPatientState = polioProgram.determineState(enrollment,new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState5.getName());
    }

    public void testMoveState() {
        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(2).atLeastOnce();
        expect(
                registrarBean.getLastObsDate(patientId, polioConceptName , polioConceptValue))
                .andReturn(new Date());

        replay(registrarBean);

        currentPatientState = polioProgram.determineState(enrollment,new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState3.getName());

        // State will change with the number of Obs increasing
        reset(registrarBean);

        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(3).atLeastOnce();
        expect(
                registrarBean.getLastObsDate(patientId, polioConceptName, polioConceptValue))
                .andReturn(new Date()).times(1);

        replay(registrarBean);

        currentPatientState = updateState(polioProgram,enrollment, new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState4.getName());
    }

    public void testNotMoveState() {
        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(3).atLeastOnce();
        expect(
                registrarBean.getLastObsDate(patientId, polioConceptName, polioConceptValue))
                .andReturn(new Date());

        replay(registrarBean);

        currentPatientState = polioProgram.determineState(enrollment,
                new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState4.getName());

        // State does not change with the same number of Obs
        reset(registrarBean);

        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(3).atLeastOnce();
        expect(
                registrarBean.getLastObsDate(patientId, polioConceptName, polioConceptValue))
                .andReturn(new Date()).times(1);

        replay(registrarBean);

        currentPatientState = updateState(polioProgram,enrollment, new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState4.getName());
    }

    public void testNotMoveEndState() {
        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(4).atLeastOnce();

        replay(registrarBean);

        currentPatientState = polioProgram.determineState(enrollment,
                new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState5.getName());

        // Future calls to updateState return the end state with no actions
        reset(registrarBean);

        expect(
                registrarBean.getNumberOfObs(patientId, polioConceptName, polioConceptValue))
                .andReturn(4).atLeastOnce();

        replay(registrarBean);

        currentPatientState = updateState(polioProgram,enrollment, new Date());

        verify(registrarBean);

        assertEquals(currentPatientState.getName(), polioState5.getName());

        reset(registrarBean);
    }

    private MessageProgramState updateState(MessageProgram program,MessageProgramEnrollment enrollment, Date currentDate) {
		MessageProgramState state = program.determineState(enrollment, currentDate);
		if (state.equals(program.getEndState())) {
			return state;
		}
		MessageProgramStateTransition transition = state.getTransition(enrollment, currentDate, registrarBean);
		MessageProgramState newState = transition.getNextState();
		return newState;
	}


}
