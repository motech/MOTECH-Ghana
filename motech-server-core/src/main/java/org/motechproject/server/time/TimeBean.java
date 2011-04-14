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

package org.motechproject.server.time;

import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.svc.RegistrarBean;

import java.util.Calendar;
import java.util.Date;

public class TimeBean {

    private RegistrarBean registrarBean;

    public RegistrarBean getRegistrarBean() {
        return registrarBean;
    }

    public void setRegistrarBean(RegistrarBean registrarBean) {
        this.registrarBean = registrarBean;
    }

    public Date determineTime(TimePeriod timePeriod,
                              TimeReference timeReference, Integer timeValue, Integer personId,
                              MessageProgramEnrollment enrollment, String conceptName,
                              String valueConceptName, Integer currentDoseNumber,
                              String encounterTypeName) {

        if (timePeriod != null && timeReference != null && timeValue != null) {

            Calendar calendar = Calendar.getInstance();
            Date timeReferenceDate = null;

            if (personId == null && enrollment != null) {
                personId = enrollment.getPersonId();
            }

            switch (timeReference) {
                case patient_birthdate:
                    if (personId != null) {
                        timeReferenceDate = registrarBean
                                .getPatientBirthDate(personId);
                    }
                    break;
                case last_obs_date:
                    if (personId != null && conceptName != null
                            && valueConceptName != null) {
                        timeReferenceDate = registrarBean.getLastObsDate(personId,
                                conceptName, valueConceptName);
                    }
                    break;
                case last_dose_obs_date:
                    if (personId != null && conceptName != null
                            && currentDoseNumber != null) {
                        timeReferenceDate = registrarBean.getLastDoseObsDate(
                                personId, conceptName, currentDoseNumber - 1);
                    }
                    break;
                case last_dose_obs_date_current_pregnancy:
                    if (personId != null && conceptName != null
                            && currentDoseNumber != null) {
                        timeReferenceDate = registrarBean
                                .getLastDoseObsDateInActivePregnancy(personId,
                                        conceptName, currentDoseNumber - 1);
                    }
                    break;
                case last_obs_datevalue:
                    if (personId != null && conceptName != null) {
                        timeReferenceDate = registrarBean.getLastObsValue(personId,
                                conceptName);
                    }
                    break;
                case current_pregnancy_duedate:
                    if (personId != null) {
                        timeReferenceDate = registrarBean
                                .getActivePregnancyDueDate(personId);
                    }
                    break;
                case last_pregnancy_end_date:
                    if (personId != null) {
                        timeReferenceDate = registrarBean
                                .getLastPregnancyEndDate(personId);
                    }
                    break;
                case enrollment_startdate:
                    if (enrollment != null) {
                        timeReferenceDate = enrollment.getStartDate();
                    }
                    break;
                case enrollment_obs_datevalue:
                    if (enrollment != null) {
                        timeReferenceDate = registrarBean.getObsValue(enrollment
                                .getObsId());
                    }
                    break;
            }

            if (timeReferenceDate == null) {
                return null;
            }
            calendar.setTime(timeReferenceDate);

            if (timePeriod.equals(TimePeriod.week))
                calendar.add(timePeriod.getCalendarPeriod(), timeValue * 7);
            else
                calendar.add(timePeriod.getCalendarPeriod(), timeValue);

            return calendar.getTime();
        }
        return null;
    }

}
