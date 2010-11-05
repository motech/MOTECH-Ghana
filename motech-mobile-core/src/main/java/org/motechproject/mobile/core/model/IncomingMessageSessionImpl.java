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

import org.motechproject.mobile.core.util.MotechIDGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/*
 * IncomingMessageSessionImpl is the implementation of the IncomingMessageSession interface
 * which is the actually mapped class in the hibernate.It provides properties to handle IncomingMessageSession operations
 *
 * Date: Dec 02, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class IncomingMessageSessionImpl implements IncomingMessageSession {

    private Long id;
    private String requesterPhone;
    private Date dateStarted;
    private Date dateEnded;
    private Date lastActivity;
    private String formCode;
    private List<IncomingMessage> incomingMessages = new ArrayList<IncomingMessage>();
    private IncMessageSessionStatus messageSessionStatus;

    public IncomingMessageSessionImpl(){
        this.id = MotechIDGenerator.generateID();
    }


private int version=-1;
    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(int version) {
        this.version = version;
    }
    /**
     * @return the requesterPhone
     */
    public String getRequesterPhone() {
        return requesterPhone;
    }

    /**
     * @param requesterPhone the requesterPhone to set
     */
    public void setRequesterPhone(String requesterPhone) {
        this.requesterPhone = requesterPhone;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateStarted() {
        return dateStarted;
    }

    /**
     * @param dateStarted the dateCreated to set
     */
    public void setDateStarted(Date dateStarted) {
        this.dateStarted = dateStarted;
    }

    /**
     * @return the dateEnded
     */
    public Date getDateEnded() {
        return dateEnded;
    }

    /**
     * @param dateEnded the dateEnded to set
     */
    public void setDateEnded(Date dateEnded) {
        this.dateEnded = dateEnded;
    }

    /**
     * @return the lastActivity
     */
    public Date getLastActivity() {
        return lastActivity;
    }

    /**
     * @param lastActivity the lastActivity to set
     */
    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }

    /**
     * @return the formCode
     */
    public String getFormCode() {
        return formCode;
    }

    /**
     * @param formCode the formCode to set
     */
    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    /**
     * @return the incomingMessages
     */
    public List<IncomingMessage> getIncomingMessages() {
        return incomingMessages;
    }

    /**
     * @param incomingMessages the incomingMessages to set
     */
    public void setIncomingMessages(List<IncomingMessage> incomingMessages) {
        this.incomingMessages = incomingMessages;
    }

    /**
     * @return the messageSessionStatus
     */
    public IncMessageSessionStatus getMessageSessionStatus() {
        return messageSessionStatus;
    }

    /**
     * @param messageSessionStatus the messageSessionStatus to set
     */
    public void setMessageSessionStatus(IncMessageSessionStatus messageSessionStatus) {
        this.messageSessionStatus = messageSessionStatus;
    }

    public void addIncomingMessage(IncomingMessage msg) {
        this.incomingMessages.add(msg);
        msg.setIncomingMsgSession(this);
    }

    public void removeIncomingMessage(IncomingMessage msg) {
        if (this.incomingMessages.contains(msg)) {
            msg.setIncomingMsgSession(null);
            this.incomingMessages.remove(msg);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        if (this != null) {
            sb.append((this.getId() != null) ? "key=Id value=" + this.getId().toString() : "Id is null ");
            sb.append(newLine);
            sb.append((this.requesterPhone != null) ? "key=requestedPhone value=" + this.requesterPhone : "requesterPhone is null  ");
            sb.append(newLine);
            sb.append((this.formCode != null) ? "key=formCode value=" + this.formCode : "formCode is null ");
            sb.append(newLine);

            sb.append((this.incomingMessages.isEmpty()) ? "key=incomingMessages length=" + Integer.toString(this.incomingMessages.size()) : "incomingMessages is empty ");
            sb.append(newLine);

            for (Iterator it = this.incomingMessages.iterator(); it.hasNext();) {
                IncomingMessage resp = (IncomingMessage) it.next();
                sb.append((resp != null) ? "key=incomingMessage.Id value=" + resp.getId().toString() : "incomingMessages.Id is null ");
                sb.append(newLine);
            }

            sb.append((this.dateStarted != null) ? "key=dateStarted value=" + this.dateStarted.toString() : "dateStarted is null ");
            sb.append(newLine);
            sb.append((this.dateEnded != null) ? "key=dateEnded value=" + this.dateEnded.toString() : "dateEnded is null ");
            sb.append(newLine);
            sb.append((this.lastActivity != null) ? "key=lastActivity value=" + this.lastActivity.toString() : "lastActivity is null ");
            sb.append(newLine);
            sb.append((this.messageSessionStatus != null) ? "key=messageSessionStatus value=" + this.messageSessionStatus.toString() : "messageSessionStatus is null ");
            sb.append(newLine);

            return sb.toString();

        } else {
            return "Object is null";
        }


    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
}
