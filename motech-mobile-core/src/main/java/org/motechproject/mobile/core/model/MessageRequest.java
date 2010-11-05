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
 *  MessageRequest interface is a POJO to hold MessageRequest information for data storage and manipulation
 *  Date : Sep 25, 2009
 * @author joseph Djomeda(joseph@dreamoval.com)
 */
public interface MessageRequest {

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

    public String getRecipientName();

    public Language getLanguage();

    public Date getSchedule();

    public MessageType getMessageType();

    public String getP13nData();

    public NotificationType getNotificationType();

    public Date getDateCreated();

    public Date getDateProcessed();

    public String getRecipientNumber();
    
    /**
     * 
     * @return type of the phone 
     */
    public String getPhoneNumberType();

    public Date getDateFrom();

    public Date getDateTo();

    public MStatus getStatus();

    public int getTryNumber();
    
    /**
     * get the number of days on which attempts have been made to deliver
     * @return days
     */
    public int getDaysAttempted();

    public String getRequestId();

    public Set getPersInfos();

    public Date getLastModified();
    
    /**
     * 
     * @return unique identifier of the recipient
     */
    public String getRecipientId();
    
    public GatewayRequestDetails getGatewayRequestDetails();

    public void setRecipientName(String recipientName);

    public void setLanguage(Language language);

    public void setSchedule(Date schedule);

    public void setMessageType(MessageType messageType);

    public void setP13nData(String p13nData);

    public void setNotificationType(NotificationType notificationType);

    public void setDateCreated(Date dateCreated);

    public void setDateProcessed(Date dateProcessed);

    public void setRecipientNumber(String recipientNumber);
    
    /**
     * 
     * @param phoneNumberType the type of phone number
     */
    public void setPhoneNumberType(String phoneNumberType);

    public void setDateFrom(Date dateFrom);

    public void setDateTo(Date dateTo);

    public void setTryNumber(int maxTryNumber);

    /**
     * set the number of days on which attempts have been made to deliver
     * @param days
     */
    public void setDaysAttempted(int days);
    
    public void setStatus(MStatus status);

    public void setRequestId(String requestId);

    public void setPersInfos(Set persInfos);

    public void setLastModified(Date lastModified);
    
    /**
     * 
     * @param recipientId unique identifier of the recipient
     */
    public void setRecipientId(String recipientId);
    
    public void setGatewayRequestDetails(GatewayRequestDetails gatewayRequestDetails);

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
