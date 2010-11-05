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

import java.util.Set;

/**
 * MessageRequestDetails interface is a POJO to hold MessageRequestDetails information for data storage and manipulation
 *  Date : Sep 24, 2009
 * @author joseph Djomeda(joseph@dreamoval.com)
 */
public interface GatewayRequestDetails {


    /**
     * 
     * @return
     */
    public void setId(Long id);

    /**
     * 
     * @return
     */
    public Long getId();

    /**
     * @return the messageType
     */
    MessageType getMessageType();

    /**
     * @return the message
     */
    String getMessage();

    /**
     * @return the numberOfPages
     */
    int getNumberOfPages();

    /**
     * @return the gatewayRequest set
     */
    Set getGatewayRequests();

    /**
     * @param gatewayRequests the gatewayRequest to set
     */
    void setGatewayRequests(Set gatewayRequests);

    /**
     * @param messageType the messageType to set
     */
    void setMessageType(MessageType messageType);

    /**
     * @param message the message to set
     */
    void setMessage(String message);

    /**
     * @param numberOfPages the numberOFPages to set
     */
    void setNumberOfPages(int numberOfPages);

    /**
     * Helper method to add GatewayRequest to GatewayRequestDetails
     * @param gatewayRequest GatewayRequest to add
     */
    void addGatewayRequest(GatewayRequest gatewayRequest);

    /**
     * Helper method to remove GatewayRequest from GatewayRequestDetails
     * @param gatewayRequest GatewayRequest to remove
     */
    void removeGatewayRequest(GatewayRequest gatewayRequest);

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
