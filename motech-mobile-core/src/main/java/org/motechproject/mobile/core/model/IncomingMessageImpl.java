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

import java.util.Date;

/**
 * IncomingMessageImpl class is an implementation of IncomingMessage interface'
 * which is actually mapped in hibernate.It provides properties to handle incomingMessage
 * operations
 * Date: Dec 14, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class IncomingMessageImpl implements IncomingMessage {

    private Long id;
    private String content;
    private Date dateCreated;
    private Date lastModified;
    private IncomingMessageSession incomingMsgSession;
    private IncomingMessageResponse incomingMessageResponse;
    private IncomingMessageForm incomingMessageForm;
    private IncMessageStatus messageStatus;


    public IncomingMessageImpl(){
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
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the incomingMsgSession
     */
    public IncomingMessageSession getIncomingMsgSession() {
        return incomingMsgSession;
    }

    /**
     * @param incomingMsgSession the incomingMsgSession to set
     */
    public void setIncomingMsgSession(IncomingMessageSession incomingMsgSession) {
        this.incomingMsgSession = incomingMsgSession;
    }

    /**
     * @return the incomingMessageResponse
     */
    public IncomingMessageResponse getIncomingMessageResponse() {
        return incomingMessageResponse;
    }

    /**
     * @param incomingMessageResponse the incomingMessageResponse to set
     */
    public void setIncomingMessageResponse(IncomingMessageResponse incomingMessageResponse) {
        this.incomingMessageResponse = incomingMessageResponse;
    }

    /**
     * @return the incomingMessageForm
     */
    public IncomingMessageForm getIncomingMessageForm() {
        return incomingMessageForm;
    }

    /**
     * @param incomingMessageForm the incomingMessageForm to set
     */
    public void setIncomingMessageForm(IncomingMessageForm incomingMessageForm) {
        this.incomingMessageForm = incomingMessageForm;
    }

    /**
     * @return the messageStatus
     */
    public IncMessageStatus getMessageStatus() {
        return messageStatus;
    }

    /**
     * @param messageStatus the messageStatus to set
     */
    public void setMessageStatus(IncMessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        if (this != null) {
            sb.append((this.getId() != null) ? "key=Id value=" + this.getId().toString() : "Id is null ");
            sb.append(newLine);
            sb.append((this.content != null) ? "key=content value=" + this.content : "content is null  ");
            sb.append(newLine);

            sb.append((this.incomingMessageForm != null) ? "key=IncomingMessageForm.Id value=" + this.incomingMessageForm.getId() : "incomingMessageForm.Id is null ");
            sb.append(newLine);
            sb.append((this.incomingMessageResponse != null) ? "key=IncomingMessageResponse.Id value=" + this.incomingMessageResponse.getId() : "incomingMessageResponse.Id is null ");
            sb.append(newLine);
            sb.append((this.incomingMsgSession != null) ? "key=IncomingMsgSession.Id value=" + this.incomingMsgSession.getId() : "incomingMsgSession.Id is null ");
            sb.append(newLine);
            sb.append((this.dateCreated != null) ? "key=dateCreated value=" + this.dateCreated.toString() : "dateCreated is null ");
            sb.append(newLine);
            sb.append((this.lastModified != null) ? "key=lastModified value=" + this.lastModified.toString() : "lastModified is null ");
            sb.append(newLine);
            sb.append((this.messageStatus != null) ? "key=messageStatus value=" + this.messageStatus.toString() : "messageStatus is null ");
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
