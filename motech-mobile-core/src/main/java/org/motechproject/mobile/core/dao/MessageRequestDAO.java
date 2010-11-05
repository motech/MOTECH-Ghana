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

package org.motechproject.mobile.core.dao;

import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageRequest;
import java.util.Date;
import java.util.List;

/**
 * MessageRequestDao is an interface that defines only methods and attributes that are specific to MessageRequest entity
 *  Date : Sep 25, 2009
 * @author joseph Djomeda(joseph@dreamoval.com)
 */
public interface MessageRequestDAO<T extends MessageRequest> extends GenericDAO<T> {

    /**
     *  Method to select MessageRequest Objects based on its status and its schedule time
     * @param status the Status of the message
     * @param schedule the Date schedule
     * @return a list of MessageRequest
     */
    public List getMsgRequestByStatusAndSchedule(MStatus status, Date schedule);

    /**
     * Method to select MessageRequest Objects based on status and tryNumber
     * @param status the Status of the message
     * @param tryNumber number of retries for the message
     * @return list of MessageRequest Objects
     */
    public List getMsgRequestByStatusAndTryNumber(MStatus status, int tryNumber);

    /**
     * Method to select MessageRequest Objects based on status
     * @param status the Status of the message
     * @return list of MessageRequest Objects
     */
    public List<MessageRequest> getMsgByStatus(MStatus status);
    
    /**
     * Method to select MessageRequest object based on recipient ID and status
     * @param recipientID id of the recipient
     * @param status status of the message request
     * @return list of matching message requests
     */
    public List<MessageRequest> getMsgRequestByRecipientAndStatus(String recipientID, MStatus status);
    
    /**
     * Method to select MessageRequest object based on recipient ID and status
     * @param recipientID id of the recipient
     * @param schedule the Date schedule
     * @return list of matching message requests
     */
    public List<MessageRequest> getMsgRequestByRecipientAndSchedule(String recipientID, Date schedule);
    
    /**
     * Method to select MessageRequest objects for a recipient with dateFrom between startDate and endDate
     * @param recipientID
     * @param startDate
     * @param endDate
     * @return
     */
    public List<MessageRequest> getMsgRequestByRecipientDateFromBetweenDates(String recipientID, Date startDate, Date endDate);
}
