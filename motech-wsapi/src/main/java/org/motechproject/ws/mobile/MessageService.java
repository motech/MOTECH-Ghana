package org.motechproject.ws.mobile;

import java.io.Serializable;
import java.util.Date;

import org.motechproject.ws.Care;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.MediaType;
import org.motechproject.ws.MessageStatus;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.Patient;
import org.motechproject.ws.PatientMessage;

/**
 * An interface providing functionality for sending messages
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created 30-07-09
 */
public interface MessageService extends Serializable{

    /**
     * Sends messages to registered patients
     *
     * @param messages List of messages to be sent
     */
    public void sendPatientMessages(PatientMessage[] messages);
	
    /**
     * Sends a message to a registered patient
     *
     * @param messageId Id of the message to send
     * @param personalInfo List of name value pairs containing patient information
     * @param patientNumber Patient mobile contact number
     * @param patientNumberType Type of contact number. Possible values include PERSONAL, SHARED
     * @param langCode Code representing preferred communication language
     * @param mediaType Patient's preferred communication medium
     * @param notificationType Type of message to send to patient
     * @param startDate Date to begin message sending attempts
     * @param endDate Date to stop message sending attempts
     * @param recipientId String unique identifier of the recipient
     * @return The status of the message
     */
    public MessageStatus sendPatientMessage(String messageId, 
    										NameValuePair[] personalInfo, 
    										String patientNumber, 
    										ContactNumberType patientNumberType, 
    										String langCode, 
    										MediaType mediaType, 
    										Long notificationType, 
    										Date startDate, 
    										Date endDate,
    										String recipientId);

    /**
     * Sends a message to a registered CHPS worker
     *
     * @param messageId Id of the message to send
     * @param personalInfo List of name value pairs containing patient information
     * @param workerNumber CHPS worker's mobile contact number
     * @param patients A List of patients requiring service from CHPS worker
     * @param langCode  Code representing preferred communication language
     * @param mediaType Patient's preferred communication medium
     * @param notificationType Type of message to send to patient
     * @param startDate Date to begin message sending attempts
     * @param endDate Date to stop message sending attempts
     * @return The status of the message
     */
    public MessageStatus sendCHPSMessage(String messageId, NameValuePair[] personalInfo, String workerNumber, Patient[] patients, String langCode, MediaType mediaType, Long notificationType, Date startDate, Date endDate);

    /**
     * Sends a list of care defaulters to a CHPS worker
     *
     * @param messageId Id of the message to send
     * @param workerNumber CHPS worker's mobile contact number
     * @param cares List of patient care options which have defaulters
     * @param mediaType Patient's preferred communication medium
     * @param startDate Date to begin message sending attempts
     * @param endDate Date to stop message sending attempts
     * @return The status of the message
     */
    public MessageStatus sendDefaulterMessage(String messageId,
                                              String workerNumber,
                                              Care[] cares,
                                              MediaType mediaType,
                                              Date startDate,
                                              Date endDate);

    /**
     * Sends a list of patients within a delivery schedule to a CHPS worker
     *
     * @param messageId Id of the message to send
     * @param workerNumber CHPS worker's mobile contact number
     * @param patients List of patients with matching delivery status
     * @param deliveryStatus Status of patient delivery. Expected values are 'Upcoming', 'Recent' and 'Overdue'
     * @param mediaType Patient's preferred communication medium
     * @param startDate Date to begin message sending attempts
     * @param endDate Date to stop message sending attempts
     * @return The status of the message
     */
    public MessageStatus sendDeliveriesMessage(String messageId,
                                               String workerNumber,
                                               Patient[] patients,
                                               String deliveryStatus,
                                               MediaType mediaType,
                                               Date startDate,
                                               Date endDate);

    /**
     * Sends a list of upcoming care for a particular patient to a CHPS worker
     *
     * @param messageId Id of the message to send
     * @param workerNumber CHPS worker's mobile contact number
     * @param patient patient due for care
     * @param mediaType Patient's preferred communication medium
     * @param startDate Date to begin message sending attempts
     * @param endDate Date to stop message sending attempts
     * @return The status of the message
     */
    public MessageStatus sendUpcomingCaresMessage(String messageId,
                                                  String workerNumber,
                                                  Patient patient,
                                                  MediaType mediaType,
                                                  Date startDate,
                                                  Date endDate);

    /**
     * Sends an SMS message
     *
     * @param content the message to send
     * @param recipient the phone number to receive the message
     * @return
     */
    public MessageStatus sendMessage(String content,
                                     String recipient);

    /**
     * Sends multiple upcoming care messages to a CHPS worker
     *
     * @param messageId Id of the message to send
     * @param workerNumber CHPS worker's mobile contact number
     * @param cares List of upcoming care
     * @param mediaType Patient's preferred communication medium
     * @param startDate Date to begin message sending attempts
     * @param endDate Date to stop message sending attempts
     * @return The status of the message
     */
    public MessageStatus sendBulkCaresMessage(String messageId,
                                              String workerNumber,
                                              Care[] cares,
                                              MediaType mediaType,
                                              Date startDate,
                                              Date endDate);
}
