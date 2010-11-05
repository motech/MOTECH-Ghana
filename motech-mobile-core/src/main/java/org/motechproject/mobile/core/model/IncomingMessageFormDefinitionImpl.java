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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 * IncomingMessageFormDefinitionImpl is the implementation of the IncomingMessageFormDefinitionImpl interface
 * which is the actually mapped class in the hibernate.It provides properties to handle IncomingMessageFormDefinitionImpl operations
 *
 * Date: Dec 02, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class IncomingMessageFormDefinitionImpl implements IncomingMessageFormDefinition {

    private Long id;
    private String formCode;
    private Date dateCreated;
    private Date lastModified;
    private Duplicatable duplicatable;
    private Boolean sendResponse;
    private IncMessageFormDefinitionType type;
    private Set<IncomingMessageFormParameterDefinition> incomingMsgParamDefinitions = new HashSet<IncomingMessageFormParameterDefinition>();
    private Set<IncomingMessageForm> incomingMessageForms = new HashSet<IncomingMessageForm>();

    public IncomingMessageFormDefinitionImpl() {
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
     * @return the form_code
     */
    public String getFormCode() {
        return formCode;
    }

    /**
     * @param form_code the form_code to set
     */
    public void setFormCode(String form_code) {
        this.formCode = form_code;
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
     * @return the incomingMsgParamDefinition
     */
    public Set<IncomingMessageFormParameterDefinition> getIncomingMsgParamDefinitions() {
        return incomingMsgParamDefinitions;
    }

    /**
     * @param incomingMsgParamDefinition the incomingMsgParamDefinition to set
     */
    public void setIncomingMsgParamDefinitions(Set<IncomingMessageFormParameterDefinition> incomingMsgParamDefinition) {
        this.incomingMsgParamDefinitions = incomingMsgParamDefinition;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");
        if (this != null) {
            sb.append((this.getId() != null) ? "key=Id value=" + this.getId().toString() : "Id is null ");
            sb.append(newLine);
            sb.append((this.formCode != null) ? "key=formCode value=" + this.formCode : "formCode is null  ");
            sb.append(newLine);

            for (Iterator it = this.incomingMsgParamDefinitions.iterator(); it.hasNext();) {
                IncomingMessageFormParameterDefinition resp = (IncomingMessageFormParameterDefinition) it.next();
                sb.append((resp != null) ? "key=IncomingMessageFormParameterDefinition.Id value=" + resp.getId().toString() : "IncomingMessageFormParameterDefinition.Id is null ");
                sb.append(newLine);
            }

            sb.append((this.dateCreated != null) ? "key=dateCreated value=" + this.dateCreated.toString() : "dateCreated is null ");
            sb.append(newLine);


            return sb.toString();

        } else {
            return "Object is null";
        }


    }

    /**
     * @return the incomingMessageForms
     */
    public Set<IncomingMessageForm> getIncomingMessageForms() {
        return incomingMessageForms;
    }

    /**
     * @param incomingMessageForms the incomingMessageForms to set
     */
    public void setIncomingMessageForms(Set<IncomingMessageForm> incomingMessageForms) {
        this.incomingMessageForms = incomingMessageForms;
    }

    /**
     * Helper method to add IncomingMesasgeForm to IncomingMessageFormDefinition
     * @param form the IncomingMessageForm object to add
     */
    public void addIncomingMessageForm(IncomingMessageForm form) {
        this.incomingMessageForms.add(form);
        form.setIncomingMsgFormDefinition(this);
    }

    /**
     * Helper method to remove IncomingMesasgeForm to IncomingMessageFormDefinition
     * @param form the IncomingMessageForm object to remove
     */
    public void removeIncomingMessageForm(IncomingMessageForm form) {
        if (this.incomingMessageForms.contains(form)) {
            form.setIncomingMsgFormDefinition(null);
            this.incomingMessageForms.remove(form);
        }
    }

    /**
     * @return the duplicatable
     */
    public Duplicatable getDuplicatable() {
        return duplicatable;
    }

    /**
     * @param duplicatable the duplicatable to set
     */
    public void setDuplicatable(Duplicatable duplicatable) {
        this.duplicatable = duplicatable;
    }

    /**
     * @return the type
     */
    public IncMessageFormDefinitionType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(IncMessageFormDefinitionType type) {
        this.type = type;
    }

    /**
     * @return the sendResponse
     */
    public Boolean getSendResponse() {
        return sendResponse;
    }

    /**
     * @param sendResponse the sendResponse to set
     */
    public void setSendResponse(Boolean sendResponse) {
        this.sendResponse = sendResponse;
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
