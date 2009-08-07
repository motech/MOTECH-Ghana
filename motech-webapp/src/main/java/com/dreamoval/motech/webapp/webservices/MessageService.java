/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dreamoval.motech.webapp.webservices;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.dreamoval.motech.omi.service.ContactNumberType;
import com.dreamoval.motech.omi.service.MessageType;
import com.dreamoval.motech.omi.service.PatientImpl;

/**
 *
 * @author Yoofi
 */
@WebService()
public interface MessageService extends Serializable{

    /**
     * Sends a message to a CHPS patient
     *
     * @param messageId Numeric key of the message to send
     * @param clinic Location of patient's default CHPS compound
     * @param serviceDate Date of current service delivery
     * @param patientNumber Patient mobile contact number
     * @param patientNumberType Type of contact number. Possible values include PERSONAL, SHARED
     * @param messageType Preferred message type. Possible values include TEXT, VOICE
     * @return The id of the message sent
     */
    public Long sendPatientMessage(@WebParam(name="messageId") Long messageId, @WebParam(name="clinic") String clinic, @WebParam(name="serviceDate") Date serviceDate, @WebParam(name="patientNumber") String patientNumber, @WebParam(name="patientNumberType") ContactNumberType patientNumberType, @WebParam(name="messageType") MessageType messageType);

    /**
     * Sends a message to a CHPS Worker
     *
     * @param messageId Numeric key of the message to send
     * @param workerName Name of CHPS worker recieving message
     * @param workerNumber Worker mobile contact number
     * @param patientList A List of patients requiring service from CHPS worker
     * @return The id of the message sent
     */
    public Long sendCHPSMessage(@WebParam(name="messageId") Long messageId, @WebParam(name="workerName") String workerName, @WebParam(name="workerNumber") String workerNumber, @WebParam(name="patientList") List<PatientImpl> patientList);
}