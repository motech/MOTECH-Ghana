/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.mobile.omi.ws;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.Gender;
import org.motechproject.ws.HowLearned;
import org.motechproject.ws.InterestReason;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.RegistrantType;
import org.motechproject.ws.RegistrationMode;
import org.motechproject.ws.server.RegistrarService;
import org.motechproject.ws.server.ValidationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@Ignore
public class TestAddPatients {

    static ApplicationContext ctx;
    static RegistrarService regWs;

    @BeforeClass
    public static void setUpClass() throws Exception {
        ctx = new ClassPathXmlApplicationContext("test-ws-context.xml");
        regWs = (RegistrarService) ctx.getBean("registrarClient");
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        ctx = null;
        regWs = null;
    }

    @Test
    public void testRegisterPatient() throws ValidationException {
        Random r = new Random();

        for (int i = 1; i <= 1500; i++) {
            Integer staffId = 465, facilityId = 11117, motechId = null, motherMotechId = null;
            String firstName = ("New2MotherFirst" + i), middleName = ("New2MotherMiddle" + i), lastName = ("New2MotherLast" + i), prefName = ("New2MotherPref" + i);
            String nhis = ("New2MotherNHIS" + i), address = ("New2MotherAddress" + i), language = "en";
            Integer community = 11211;
            Gender gender = Gender.FEMALE;
            Boolean estBirthDate = false, insured = true, delivDateConf = true, enroll = true, consent = true;
            //Date dueDate = new Date(System.currentTimeMillis() - 761011200000l);
            Date dueDate = new Date(System.currentTimeMillis() + 9676800000l);
            Integer messageWeek = 24;
            String phone = ("082000" + i);

            RegistrationMode mode = RegistrationMode.AUTO_GENERATE_ID;
            RegistrantType type = RegistrantType.PREGNANT_MOTHER;
            ContactNumberType phoneType = ContactNumberType.PERSONAL;
            MediaType format = null;
            if ((i % 10) == 0) {
                format = MediaType.TEXT;
            } else {
                format = MediaType.VOICE;
            }
            DayOfWeek day = DayOfWeek.THURSDAY;
            InterestReason reason = InterestReason.CURRENTLY_PREGNANT;
            HowLearned how = HowLearned.GHS_NURSE;

            Date date = new Date();

            int weeksOld = r.nextInt(7);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -7 * weeksOld);
            //Date birthDate = cal.getTime();
            Date birthDate = new Date(System.currentTimeMillis() - 756864000000l);
            cal.setTime(date);
            cal.add(Calendar.MINUTE, 30);
            Date prefDelivTime = cal.getTime();

            try {
                Patient patient = regWs.registerPatient(staffId, facilityId,
                        date, mode, motechId, type, firstName, middleName,
                        lastName, prefName, birthDate, estBirthDate, gender,
                        insured, nhis, date, motherMotechId, community,
                        address, phone, dueDate, delivDateConf, enroll,
                        consent, phoneType, format, language, day,
                        prefDelivTime, reason, how, messageWeek);

                System.out.println("motechid=" + patient.getMotechId());
            } catch (ValidationException e) {
                List<String> errors = e.getFaultInfo().getErrors();
                System.out.println(errors.size());
                for (String error : errors) {
                    System.out.println(error);
                }
            } catch (Exception e) {
                System.out.println("error: " + e.getMessage());
            }
        }

        System.out.println("Test Complete");
    }

    @Test
    public void nothing() {
    }
}
