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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.motechproject.mobile.omp.manager;

import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.util.MotechIDGenerator;
import java.util.Set;

/**
 * <p>A dummy gateway manager for testing purposes</p>
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 * Date Created: Jul 31, 2009
 */
public class DummyGatewayManagerImpl implements GatewayManager {

    private static Log log = LogFactory.getLog(DummyGatewayManagerImpl.class);
    private long sleepTime;
    private boolean throwRandomExceptions;
    private GatewayMessageHandler messageHandler;
    private ArrayList<Integer> exceptionPoints;
    private int exceptionPointRange;

    /**
     *
     * @see GatewayManager.send
     */
    public Set<GatewayResponse> sendMessage(GatewayRequest messageDetails) {
        if (log.isInfoEnabled()) {
            log.info(messageDetails.getId() + "|"
                    + messageDetails.getRecipientsNumber() + "|"
                    + messageDetails.getMessage());
        }
        String msgResponse = (messageDetails.getRecipientsNumber().length() < 8) ? "failed" : "ID: " + MotechIDGenerator.generateID(10);

        if (sleepTime > 0) {
            try {
                log.debug("going to sleep to simulate latency");
                Thread.sleep(sleepTime);
                log.debug("finished simulated latency");
            } catch (InterruptedException ie) {
                log.debug("interrupted while sleeping");
            }
        }

        if(isThrowRandomExceptions() && getExceptionPoints().contains(new Integer((int)(Math.random() * exceptionPointRange)))){
            log.error("Throwing Exception to mimic possible fault behaviour");
            throw new RuntimeException("Arbitrary exception thrown to mimic fault behaviour");
        }

        return messageHandler.parseMessageResponse(messageDetails, msgResponse);
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    /**
     *
     * @see GatewayManager.getMessageStatus
     */
    public String getMessageStatus(GatewayResponse response) {
        return "004";
    }

    public MStatus mapMessageStatus(GatewayResponse response) {
        return messageHandler.parseMessageStatus(response.getResponseText());
    }

    /**
     * @return the messageHandler
     */
    public GatewayMessageHandler getMessageHandler() {
        return messageHandler;
    }

    /**
     * @param messageHandler the messageHandler to set
     */
    public void setMessageHandler(GatewayMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    /**
     * @return the throwRandomExceptions
     */
    public boolean isThrowRandomExceptions() {
        return throwRandomExceptions;
    }

    /**
     * @param throwRandomExceptions the throwRandomExceptions to set
     */
    public void setThrowRandomExceptions(boolean throwRandomExceptions) {
        this.throwRandomExceptions = throwRandomExceptions;
    }

    /**
     * @return the exceptionPoints
     */
    public ArrayList<Integer> getExceptionPoints() {
        return exceptionPoints;
    }

    /**
     * @param exceptionPoints the exceptionPoints to set
     */
    public void setExceptionPoints(ArrayList<Integer> exceptionPoints) {
        this.exceptionPoints = exceptionPoints;
    }

    /**
     * @return the exceptionPointRange
     */
    public int getExceptionPointRange() {
        return exceptionPointRange;
    }

    /**
     * @param exceptionPointRange the exceptionPointRange to set
     */
    public void setExceptionPointRange(int exceptionPointRange) {
        this.exceptionPointRange = exceptionPointRange;
    }
}
