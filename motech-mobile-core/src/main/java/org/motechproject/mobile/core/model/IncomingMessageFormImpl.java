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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * IncomingMessageFormImpl is the implementation of the IncomingMessageForm interface
 * which is the actually mapped class in the hibernate.It provides properties to handle IncomingMessageForm operations
 *
 * Date: Dec 03, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public class IncomingMessageFormImpl implements IncomingMessageForm {

    private Long id;
    private IncomingMessageFormDefinition incomingMsgFormDefinition;
    private Date dateCreated;
    private Date lastModified;
    private IncMessageFormStatus messageFormStatus;
    private Map<String,IncomingMessageFormParameter> incomingMsgFormParameters = new HashMap<String,IncomingMessageFormParameter>();
    private List<String> errors = new ArrayList<String>();


    public IncomingMessageFormImpl() {
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
     * @return the incomingMsgFormDefinition
     */
    public IncomingMessageFormDefinition getIncomingMsgFormDefinition() {
        return incomingMsgFormDefinition;
    }

    /**
     * @param incomingMsgFormDefinition the incomingMsgFormDefinition to set
     */
    public void setIncomingMsgFormDefinition(IncomingMessageFormDefinition incomingMsgFormDefinition) {
        this.incomingMsgFormDefinition = incomingMsgFormDefinition;
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
     * @return the messageStatus
     */
    public IncMessageFormStatus getMessageFormStatus() {
        return messageFormStatus;
    }

    /**
     * @param messageFormStatus the messageStatus to set
     */
    public void setMessageFormStatus(IncMessageFormStatus messageFormStatus) {
        this.messageFormStatus = messageFormStatus;
    }

    /**
     * @return the incomingMsgFormParameters
     */
    public Map<String, IncomingMessageFormParameter> getIncomingMsgFormParameters() {
        return incomingMsgFormParameters;
    }

    /**
     * @param incomingMsgFormParameters the incomingMsgFormParameters to set
     */
    public void setIncomingMsgFormParameters(Map<String, IncomingMessageFormParameter> incomingMsgFormParameters) {
        this.incomingMsgFormParameters = incomingMsgFormParameters;
    }

    /**
     * Helper method to add IncomingMessageFormParameter to IncomingMessageForm
     * @param key key of the map
     * @param param the IncomingMessageFormParameter to add
     */
    public void addIncomingMsgFormParam(String key, IncomingMessageFormParameter param){
        this.incomingMsgFormParameters.put(key, param);
        param.setIncomingMsgForm(this);
    }
    /**
     * Helper method to remover IncomingMessageFormParameter to IncomingMessageForm
     * @param key key of the map
     * @param param the IncomingMessageFormParameter to remove
     */
     public void removeIncomingMsgFormParm(String key){
         if(this.incomingMsgFormParameters.containsKey(key)){
             this.incomingMsgFormParameters.remove(key);
         }
     }

    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

       if(this != null) {
           sb.append((this.getId()!= null) ? "key=Id value=" + this.getId().toString() : "Id is null ");
           sb.append(newLine);

           sb.append((this.incomingMsgFormDefinition != null) ? "key=IncomingMessageFormDefinition.Id value=" + this.incomingMsgFormDefinition.getId() : "IncomingMessageFormDefinition.Id is null ");
           sb.append(newLine);

           sb.append((this.incomingMsgFormParameters.isEmpty() ) ? "key=IncomingMessageFormParameters length=" + Integer.toString(this.incomingMsgFormParameters.size()) : "IncomingMessageFormParameters is empty ");
           sb.append(newLine);

           for(Iterator it =this.incomingMsgFormParameters.entrySet().iterator(); it.hasNext();){
               Map.Entry  entry = (Map.Entry) it.next();
               String key = (String) entry.getKey();
               IncomingMessageFormParameter  resp = (IncomingMessageFormParameter) entry.getValue();

               sb.append((resp != null ) ? "key="+key+" value=" + resp.getId().toString() : "IncomingMessageFormParameter.Id is null ");
               sb.append(newLine);
           }


           sb.append((this.dateCreated != null) ? "key=dateCreated value=" + this.dateCreated.toString() : "dateCreated is null ");
           sb.append(newLine);
           sb.append((this.lastModified != null) ? "key=lastModified value=" + this.lastModified.toString() : "lastModified is null ");
           sb.append(newLine);
           sb.append((this.messageFormStatus != null) ? "key=messageFormStatus value=" + this.messageFormStatus.toString() : "messageFormStatus is null ");
           sb.append(newLine);

           return sb.toString();

       } else {
           return "Object is null";
       }


    }

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
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
