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

package org.motechproject.mobile.core.model;

import java.util.Date;
import java.util.List;

/**
 * IncomingMessageSession interface is a POJO to hold IncomingMessageSession information for data storage and manipulation
 * Date: Dec 14, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public interface IncomingMessageSession {

    /**
     * 
     * @param id the id to set
     */
    public void setId(Long id);

    /**
     *
     * @return id to get
     */
    public Long getId();

    /**
     * @return the dateStarted
     */
    public Date getDateStarted();

    /**
     * @return the dateEnded
     */
    public Date getDateEnded();

    /**
     * @return the formCode
     */
    public String getFormCode();

    /**
     * @return the lastActivity
     */
    public Date getLastActivity();

    /**
     * @return the requesterPhone
     */
    public String getRequesterPhone();

    /**
     * @return the incomingMessages
     */
    public List<IncomingMessage> getIncomingMessages();

    /**
     * @return the messageSessionStatus
     */
    public IncMessageSessionStatus getMessageSessionStatus();

    /**
     * @param dateStarted the dateCreated to set
     */
    public void setDateStarted(Date dateStarted);

    /**
     * @param dateEnded the dateEnded to set
     */
    public void setDateEnded(Date dateEnded);

    /**
     * @param formCode the formCode to set
     */
    public void setFormCode(String formCode);

    /**
     * @param lastActivity the lastActivity to set
     */
    public void setLastActivity(Date lastActivity);

    /**
     * @param requesterPhone the requesterPhone to set
     */
    public void setRequesterPhone(String requesterPhone);

    /**
     * @param incomingMessages the incomingMessages to set
     */
    public void setIncomingMessages(List<IncomingMessage> incomingMessages);

    /**
     * @param messageSessionStatus the messageSessionStatus to set
     */
    public void setMessageSessionStatus(IncMessageSessionStatus messageSessionStatus);

    /**
     * Helper Method to add IncomingMessage to IncomingMessageSession
     * @param msg the IncomingMessage to add
     */
    public void addIncomingMessage(IncomingMessage msg);

    /**
     * Helper method to remove IncomingMessage from IncomingMessageSession
     * @param msg the IncomingMessage to add
     */
    public void removeIncomingMessage(IncomingMessage msg);

    /**
     * Helper method to display string value of all properties of the object
     * @return formated string value of all properties
     */
    @Override
    public String toString();


      /**
     * @return the version
     */
    int getVersion();

    /**
     * @param version the version to set
     */
    void setVersion(int version);
}
