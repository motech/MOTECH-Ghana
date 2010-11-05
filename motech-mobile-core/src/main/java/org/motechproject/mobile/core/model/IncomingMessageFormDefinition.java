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
import java.util.Set;

/**
 * IncomingMessageFormDefinition interface is a POJO to hold IncomingMessageFormDefinition information for data storage and manipulation
 * Date: Dec 14, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public interface IncomingMessageFormDefinition  {

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
     * @return the dateCreated
     */
    public Date getDateCreated();

    /**
     * @return the formCode
     */
    public String getFormCode();

    /**
     * @return the lastModified
     */
    public Date getLastModified();

    /**
     * @return the incomingMsgParamDefinition
     */
    public Set<IncomingMessageFormParameterDefinition> getIncomingMsgParamDefinitions();

    /**
     * @param dateCreated the dateCreated to set
     */
    public void setDateCreated(Date dateCreated);

    /**
     * @param formCode the formCode to set
     */
    public void setFormCode(String formCode);

    /**
     * @param lastModified the lastModified to set
     */
    public void setLastModified(Date lastModified);

    /**
     * @param incomingMsgParamDefinition the incomingMsgParamDefinition to set
     */
    public void setIncomingMsgParamDefinitions(Set<IncomingMessageFormParameterDefinition> incomingMsgParamDefinition);

    /**
     * Helper method to display string value of all properties of the object
     * @return formated string value of all properties
     */
    @Override
    public String toString();

    /**
     * @return the incomingMessageForms
     */
    Set<IncomingMessageForm> getIncomingMessageForms();

    /**
     * @param incomingMessageForms the incomingMessageForms to set
     */
    void setIncomingMessageForms(Set<IncomingMessageForm> incomingMessageForms);

    /**
     * Helper method to add IncomingMesasgeForm to IncomingMessageFormDefinition
     * @param form the IncomingMessageForm object to add
     */
    void addIncomingMessageForm(IncomingMessageForm form);

    /**
     * Helper method to remove IncomingMesasgeForm to IncomingMessageFormDefinition
     * @param form the IncomingMessageForm object to remove
     */
    void removeIncomingMessageForm(IncomingMessageForm form);

    /**
     * @return the duplicatable
     */
    Duplicatable getDuplicatable();

    /**
     * @param duplicatable the duplicatable to set
     */
    void setDuplicatable(Duplicatable duplicatable);

    /**
     * @return the type
     */
    IncMessageFormDefinitionType getType();

    /**
     * @param type the type to set
     */
    void setType(IncMessageFormDefinitionType type);

    /**
     * @return the sendResponse
     */
    Boolean getSendResponse();

    /**
     * @param sendResponse the sendResponse to set
     */
    void setSendResponse(Boolean sendResponse);

      /**
     * @return the version
     */
    int getVersion();

    /**
     * @param version the version to set
     */
    void setVersion(int version);
}
