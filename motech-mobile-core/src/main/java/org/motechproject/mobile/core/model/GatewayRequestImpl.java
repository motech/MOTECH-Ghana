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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Date :Jul 24, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
@SuppressWarnings("serial")
public class GatewayRequestImpl implements GatewayRequest, Serializable {

    private Long id;
    private GatewayRequestDetails gatewayRequestDetails;
    private Date dateTo;
    private String message;
    private String recipientsNumber;
    private Date dateFrom;
    private Set<GatewayResponse> responseDetails = new HashSet<GatewayResponse>();
    private Date dateSent;
    private int tryNumber;
    private String requestId;
    private MStatus messageStatus;
    private Date lastModified;
    private MessageRequest messageRequest;

    public GatewayRequestImpl() {
        this.id = MotechIDGenerator.generateID();
    }

    public GatewayRequestImpl(Date dateTo, String messageText, String recipientsNumber, Date dateFrom, Date dateSent) {
        this();
        this.dateTo = dateTo;
        this.message = messageText;
        this.dateFrom = dateFrom;
        this.dateSent = dateSent;
        this.recipientsNumber = recipientsNumber;

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
     * @return the messageType
     */
    /**
     * @return the numberOfPages
     */
    public Date getDateTo() {
        return dateTo;
    }

    /**
     * @param numberOfPages the numberOfPages to set
     */
    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    /**
     * @return the messageText
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param messageText the messageText to set
     */
    public void setMessage(String messageText) {
        this.message = messageText;
    }

    /**
     * @return the dateFrom
     */
    public Date getDateFrom() {
        return dateFrom;
    }

    /**
     * @param dateFrom the dateFrom to set
     */
    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    /**
     * @return the responseDetails
     */
    public Set<GatewayResponse> getResponseDetails() {
        return responseDetails;
    }

    /**
     * @param responseDetails the responseDetails to set
     */
    public void setResponseDetails(Set<GatewayResponse> responseDetails) {
        this.responseDetails = responseDetails;
    }

    /**
     * @return the dateSent
     */
    public Date getDateSent() {
        return dateSent;
    }

    /**
     * @param dateSent the dateSent to set
     */
    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    /**
     * @return the recipientsNumbers
     */
    public String getRecipientsNumber() {
        return recipientsNumber;
    }

    /**
     * @param recipientsNumbers the recipientsNumbers to set
     */
    public void setRecipientsNumber(String recipientsNumbers) {
        this.recipientsNumber = recipientsNumbers;
    }

    public MessageRequest getMessageRequest() {
		return messageRequest;
	}

	public void setMessageRequest(MessageRequest messageRequest) {
		this.messageRequest = messageRequest;
	}

	/**
     * @see {@link org.motechproject.mobile.core.model.GatewayRequest#addResponse(org.motechproject.mobile.core.model.GatewayResponse)  }
     */
    public void addResponse(GatewayResponse response) {
        response.setGatewayRequest(this);
        this.responseDetails.add(response);
    }

    /**
     * @see {@link  org.motechproject.mobile.core.model.GatewayRequest#removeResponse(org.motechproject.mobile.core.model.GatewayResponse)  }
     */
    public void removeResponse(GatewayResponse response) {
        if (this.responseDetails.contains(response)) {
            this.responseDetails.remove(response);
        }

    }

    /**
     * @see {@link org.motechproject.mobile.core.model.GatewayRequest#addResponse(java.util.List)  }
     */
    public void addResponse(List<GatewayResponse> responses) {

        for (GatewayResponse r : responses) {
            r.setGatewayRequest(this);
            this.responseDetails.add(r);
        }

    }

    /**
     * @see {@link org.motechproject.mobile.core.model.GatewayRequest#removeResponse(java.util.List)  }
     */
    public void removeResponse(List<GatewayResponse> responses) {
        for (GatewayResponse r : responses) {
            if (this.responseDetails.contains(r)) {
                this.responseDetails.remove(r);
            }
        }
    }

    /**
     * @return the gatewayRequestDetails
     */
    public GatewayRequestDetails getGatewayRequestDetails() {
        return gatewayRequestDetails;
    }

    /**
     * @param gatewayRequestDetails the gatewayRequestDetails to set
     */
    public void setGatewayRequestDetails(GatewayRequestDetails gatewayRequestDetails) {
        this.gatewayRequestDetails = gatewayRequestDetails;
    }

    /**
     * @return the requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * @param requestId the requestId to set
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * @return the tryNumber
     */
    public int getTryNumber() {
        return tryNumber;
    }

    /**
     * @param tryNumber the tryNumber to set
     */
    public void setTryNumber(int tryNumber) {
        this.tryNumber = tryNumber;
    }

    /**
     * @return the messageStatus
     */
    public MStatus getMessageStatus() {
        return messageStatus;
    }

    /**
     * @param messageStatus the messageStatus to set
     */
    public void setMessageStatus(MStatus messageStatus) {
        this.messageStatus = messageStatus;
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        String newLine = System.getProperty("line.separator");

        if (this != null) {
            sb.append((this.getId() != null) ? "key=Id value=" + this.getId().toString() : "Id is null ");
            sb.append(newLine);
            sb.append((this.message != null) ? "key=message value=" + this.message : "message is null  ");
            sb.append(newLine);
            sb.append((this.recipientsNumber != null) ? "key=recipientsNumber value=" + this.recipientsNumber : "recipientsNumber is null ");
            sb.append(newLine);
            sb.append((this.requestId != null) ? "key=requestId value=" + this.requestId : "requestId is null ");
            sb.append(newLine);
            sb.append((this.gatewayRequestDetails != null) ? "key=gatewayRequestDetails.Id value=" + this.gatewayRequestDetails.getId() : "gatewayRequestDetails.Id is null ");
            sb.append(newLine);
            sb.append((this.getMessageRequest() != null) ? "key=messageRequest.Id value=" + this.messageRequest.getId() : "messageRequest.Id is null ");
            sb.append(newLine);
            sb.append((this.tryNumber != -1) ? "key=tryNumber.Id value=" + Integer.toString(this.tryNumber) : "tryNumber is null ");
            sb.append(newLine);
            sb.append((this.responseDetails.isEmpty()) ? "key=responseDetails length=" + Integer.toString(this.responseDetails.size()) : "responseDetails is empty ");
            sb.append(newLine);

            for (Iterator it = this.responseDetails.iterator(); it.hasNext();) {
                GatewayResponse resp = (GatewayResponse) it.next();
                sb.append((resp != null) ? "key=GatewayResponse.Id value=" + resp.getId().toString() : "GatewayResponse.Id is null ");
                sb.append(newLine);
            }

            sb.append((this.dateSent != null) ? "key=dateSent value=" + this.dateSent.toString() : "dateSent is null ");
            sb.append(newLine);
            sb.append((this.dateTo != null) ? "key=dateTo value=" + this.dateTo.toString() : "dateTo is null ");
            sb.append(newLine);
            sb.append((this.dateFrom != null) ? "key=dateFrom value=" + this.dateFrom.toString() : "dateFrom is null ");
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
