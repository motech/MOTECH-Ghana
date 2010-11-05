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

/*
 * IncomingMessageFormParameterImpl is the implementation of the IncomingMessageFormParameter interface
 * which is the actually mapped class in the hibernate.It provides properties to handle IncomingMessageFormParameter operations
 *
 * Date: Dec 03, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class IncomingMessageFormParameterImpl implements IncomingMessageFormParameter {

    private Long id;
    private IncomingMessageForm incomingMsgForm;
    private IncomingMessageFormParameterDefinition incomingMsgFormParamDefinition;
    private String name;
    private String value;
    private int errCode;
    private String errText;
    private IncMessageFormParameterStatus messageFormParamStatus;
    private Date dateCreated;
    private Date lastModified;

    public IncomingMessageFormParameterImpl() {
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
     * @return the incomingMsgForm
     */
    public IncomingMessageForm getIncomingMsgForm() {
        return incomingMsgForm;
    }

    /**
     * @param incomingMsgForm the incomingMsgForm to set
     */
    public void setIncomingMsgForm(IncomingMessageForm incomingMsgForm) {
        this.incomingMsgForm = incomingMsgForm;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the errCode
     */
    public int getErrCode() {
        return errCode;
    }

    /**
     * @param errCode the errCode to set
     */
    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    /**
     * @return the errText
     */
    public String getErrText() {
        return errText;
    }

    /**
     * @param errText the errText to set
     */
    public void setErrText(String errText) {
        this.errText = errText;
    }

    /**
     * @return the messageFormParamStatus
     */
    public IncMessageFormParameterStatus getMessageFormParamStatus() {
        return messageFormParamStatus;
    }

    /**
     * @param messageFormParamStatus the messageFormParamStatus to set
     */
    public void setMessageFormParamStatus(IncMessageFormParameterStatus messageFormParamStatus) {
        this.messageFormParamStatus = messageFormParamStatus;
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
     * @return the incomingMsgFormParamDefinition
     */
    public IncomingMessageFormParameterDefinition getIncomingMsgFormParamDefinition() {
        return incomingMsgFormParamDefinition;
    }

    /**
     * @param incomingMsgFormParamDefinition the incomingMsgFormParamDefinition to set
     */
    public void setIncomingMsgFormParamDefinition(IncomingMessageFormParameterDefinition incomingMsgFormParamDefinition) {
        this.incomingMsgFormParamDefinition = incomingMsgFormParamDefinition;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        if (this != null) {
            sb.append((this.getId() != null) ? "key=Id value=" + this.getId().toString() : "Id is null ");
            sb.append(newLine);
            sb.append((this.name != null) ? "key=name value=" + this.name : "message is null  ");
            sb.append(newLine);
            sb.append((this.value != null) ? "key=value value=" + this.value : "value is null ");
            sb.append(newLine);
            sb.append((this.errCode != -1) ? "key=errCode value=" + Integer.toString(this.errCode) : "errCode is null ");
            sb.append(newLine);
            sb.append((this.errText != null) ? "key=errText value=" + this.errText : "errText is null ");
            sb.append(newLine);
            sb.append((this.incomingMsgForm != null) ? "key=IncomingMessageForm.Id value=" + this.incomingMsgForm.getId() : "IncomingMessageForm.Id is null ");
            sb.append(newLine);
            sb.append((this.incomingMsgFormParamDefinition != null) ? "key=IncomingMsgFormDefinition.Id value=" + this.incomingMsgFormParamDefinition.getId() : "incomingMessageFormDefinition.Id is null ");
            sb.append(newLine);
            sb.append((this.dateCreated != null) ? "key=dateCreated value=" + this.dateCreated.toString() : "dateCreated is null ");
            sb.append(newLine);
            sb.append((this.lastModified != null) ? "key=lastModified value=" + this.lastModified.toString() : "lastModified is null ");
            sb.append(newLine);
            sb.append((this.messageFormParamStatus != null) ? "key=messageFormParamStatus value=" + this.messageFormParamStatus.toString() : "messageFormParamStatus is null ");
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
