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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * LanguageImpl class is an implementation of the Language interface which is actuaally
 * mapped in hibernate.It provides properties to handle Language operations
 * Date : Sep 27, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
@SuppressWarnings("serial")
public class LanguageImpl implements Language, Serializable {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Set<MessageRequest> messageRequests = new HashSet<MessageRequest>();

    public LanguageImpl() {
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
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

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the messageRequests
     */
    public Set<MessageRequest> getMessageRequests() {
        return messageRequests;
    }

    /**
     * @param messageRequests the messageRequests to set
     */
    public void setMessageRequests(Set<MessageRequest> messageRequests) {
        this.messageRequests = messageRequests;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        if (this != null) {
            sb.append((this.getId() != null) ? "key=Id--------------------value=" + this.getId().toString() : "Id is null ");
            sb.append(newLine);
            sb.append((this.code != null) ? "key=code------------------value=" + this.code : "code is null  ");
            sb.append(newLine);
            sb.append((this.name != null) ? "key=recipientsNumber value=" + this.name : "name is null ");
            sb.append(newLine);
            sb.append((this.description != null) ? "key=description value=" + this.description : "description is null ");
            sb.append(newLine);

            sb.append((this.messageRequests.isEmpty()) ? "key=messageRequests length=" + Integer.toString(this.messageRequests.size()) : "responseDetails is empty ");
            sb.append(newLine);

            for (Iterator it = this.messageRequests.iterator(); it.hasNext();) {
                MessageRequest resp = (MessageRequest) it.next();
                sb.append((resp != null) ? "key=MessageRequest.Id value=" + resp.getId().toString() : "MessageRequest.Id is null ");
                sb.append(newLine);
            }


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
