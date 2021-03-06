/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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

package org.motechproject.server.omod.advice;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.omod.impl.ContextServiceImpl;
import org.motechproject.server.service.ScheduleMaintService;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon an
 * observation being saved, whether that operation knows about it or not.
 * Currently, this is how we are handling calling the event engine.
 */
public class ObsAdvice implements AfterReturningAdvice {

    private static Log log = LogFactory.getLog(ObsAdvice.class);

    private ContextService contextService;

    public ObsAdvice() {
        contextService = new ContextServiceImpl();
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    /**
     * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object,
     *      java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
     */
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        log.debug("intercepting method invocation: " + method.getName());
        Obs obs = (Obs) returnValue;
        Person person = obs.getPerson();
        ScheduleMaintService schedService = contextService.getScheduleMaintService();

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            schedService.addAffectedPatient(person.getId());
            schedService.requestSynch();
        } else {
            // FIXME: Remove this when advice can exec in tx
            schedService.updateSchedule(person.getId());
        }
    }

}
