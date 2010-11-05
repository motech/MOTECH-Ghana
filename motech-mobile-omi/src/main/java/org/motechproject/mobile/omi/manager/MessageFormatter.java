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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.motechproject.mobile.omi.manager;

import org.motechproject.ws.Care;
import org.motechproject.ws.Patient;

/**
 *
 * @author user
 */
public interface MessageFormatter {

    /**
     * Constructs a formatted patient care defaulter message
     * @param care object containing patient information
     * @return the formatted message
     */
    String formatDefaulterMessage(Care care);

    /**
     * Constructs a formatted patient care defaulter message
     * @param care object containing patient information
     * @return the formatted message
     */
    String formatDefaulterMessage(Care[] cares);

    /**
     * Constructs a formatted patient delivery schedule message
     * @param type of schedule
     * @param patients list of patients within schedule
     * @return the formatted message
     */
    String formatDeliveriesMessage(String type, Patient[] patients);

    /**
     * Constructs a formatted patient delivery schedule message
     * @param type of schedule
     * @param patients list of patients within schedule
     * @return the formatted message
     */
    String formatUpcomingDeliveriesMessage(Patient[] patients);

    /**
     * Constructs a formatted patient delivery schedule message
     * @param type of schedule
     * @param patients list of patients within schedule
     * @return the formatted message
     */
    String formatRecentDeliveriesMessage(Patient[] patients);

    /**
     * Constructs a formatted patient delivery schedule message
     * @param type of schedule
     * @param patients list of patients within schedule
     * @return the formatted message
     */
    String formatOverdueDeliveriesMessage(Patient[] patients);

    /**
     * Constructs a patient query response message
     * @param patients list of patients matching query
     * @return the formatted message
     */
    String formatMatchingPatientsMessage(Patient[] patients);

    /**
     * Constructs a formatted patient details message
     * @param patient object containing patient details
     * @return the formatted message
     */
    String formatPatientDetailsMessage(Patient patient);

    /**
     * Constructs a formatted patient upcoming care message
     * @param patient object containing list of upcoming care
     * @return the formatted message
     */
    String formatUpcomingCaresMessage(Patient patient);

    /**
     * Constructs a upcoming care message for multiple patients
     * @param patient object containing list of upcoming care
     * @return the formatted message
     */
    String formatBulkCaresMessage(Care[] cares);

    /**
     * Constructs a formatted patient registration response message
     * @param patient object containing patient details
     * @return the formatted message
     */
    String formatPatientRegistrationMessage(Patient patient);

    /**
     * Constructs formatted patient registration response messages for multiple patients
     * @param patient array of patient objects
     * @return the formatted message
     */
    String formatBabyRegistrationMessage(Patient[] patients);

    /**
     * @param dateFormat the dateFormat to set
     */
    void setDateFormat(String dateFormat);

    /**
     * @param omiManager the omiManager to set
     */
    void setOmiManager(OMIManager omiManager);

}
