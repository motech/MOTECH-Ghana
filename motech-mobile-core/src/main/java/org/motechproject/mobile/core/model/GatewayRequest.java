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
import java.util.Set;

/**
 *
 * MessageDetails interface is a POJO to hold MessageDetails information for data storage and manipulation
 * It has properties for example the text message itself or the numberofpages of that sms text in case it's
 * sms send
 *
 * Date: Jul 24, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
public interface GatewayRequest {

    /**
    * 
    * @return the id
    */
    public Long getId();


    /**
     * @return the dateSent
     */
    public Date getDateSent();

    /**
     * @return the dateFrom
     */
    public Date getDateFrom();

    /**
     * @return the message
     */
    public String getMessage();

    /**
     * @return the gatewayRequestDetails Object
     */
    public GatewayRequestDetails getGatewayRequestDetails();

    /**
     * @return the RequestId
     */
    public String getRequestId();

    /**
     * @return the  dateTo
     */
    public Date getDateTo();

    /**
     * @return the recipientsNumber
     */
    public String getRecipientsNumber();

    /**
     * @return the responseDetails
     */
    public Set<GatewayResponse> getResponseDetails();

    /**
     * @return the try number
     */
    public int getTryNumber();

    /**
     * @return the messageStatus
     */
    public MStatus getMessageStatus();

    /**
     * @return the lastModified
     */
    public Date getLastModified();

    /**
     * @return the MessageRequest for which this GatewayRequest was generated
     */
    public MessageRequest getMessageRequest();


    /**
     *
     * @param id the id to set
     */
    public void setId(Long id);


    /**
     *
     * @param tryNumber the tryNumber to set
     */
    public void setTryNumber(int tryNumber);

    /**
     * @param dateSent the dateSent to set
     */
    public void setDateSent(Date dateSent);

    /**
     * @param dateFrom the dateFrom to set
     */
    public void setDateFrom(Date dateFrom);

    /**
     * @param messageText the message to set
     */
    public void setMessage(String message);

    /**
     * @param messageType the requestId to set
     */
    public void setGatewayRequestDetails(GatewayRequestDetails gatewayRequestDetails);

    /**
     * @param numberOfPages the dateTo to set
     */
    public void setDateTo(Date dateTo);

    /**
     * @param recipientsNumbers the recipientsNumbers to set
     */
    public void setRecipientsNumber(String recipientsNumber);

    /**
     *
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId);

    /**
     * @param responseDetails the responseDetails to set
     */
    public void setResponseDetails(Set<GatewayResponse> responseDetails);

    /**
     * @param MStatus the status to set
     */
    public void setMessageStatus(MStatus status);

    /**
     * @param lastModified lastModified to set
     */
    public void setLastModified(Date lastModified);

    /**
     * MessageRequest on which this GatewayRequest is based
     * @param messageRequest MessageRequest Object that corresponds to this GatewayRequest
     */
    public void setMessageRequest(MessageRequest messageRequest);
    
    /**
     * Helper method to add a GatewayResponse Object to GatewayRequest
     * @param  GatewayResponse GatewayResponse Object to pass
     */
    public void addResponse(GatewayResponse response);

    /**
     * Helper method to remove the passed GatewayResponse  object from GatewayRequest
     * @param  GatewayRespone the GatewayResponse to pass
     */
    public void removeResponse(GatewayResponse response);

    /**
     * Helper method to add a list of  GatewayResponse Objects to GatewayRequest
     * @param  List the GatewayRespone List to pass
     */
    public void addResponse(List<GatewayResponse> responses);

    /**
     *Helper method to remove the passed List of GatewayResponse objects from GatewayRequest
     * @param  List the List of GatewayResponse to pass
     */
    public void removeResponse(List<GatewayResponse> responses);

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
