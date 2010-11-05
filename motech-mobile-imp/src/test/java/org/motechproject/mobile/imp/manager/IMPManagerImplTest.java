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

package org.motechproject.mobile.imp.manager;

import org.motechproject.mobile.imp.serivce.IMPService;
import org.motechproject.mobile.imp.util.CommandAction;
import org.motechproject.mobile.imp.util.IncomingMessageFormValidator;
import org.motechproject.mobile.imp.util.IncomingMessageParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

/**
 * Test for IMPManagerImpl class
 *
 *  Date : Dec 5, 2009
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class IMPManagerImplTest {
    @Autowired
    IMPManager impManager;

    public IMPManagerImplTest() {
    }

    /**
     * Test of createIMPService method, of class IMPManagerImpl.
     */
    @Test
    public void testCreateIMPService() {
        System.out.println("createIMPService");

        IMPService result = impManager.createIMPService();
        assertNotNull(result);
    }

    /**
     * Test of createIncomingMessageParser method, of class IMPManagerImpl.
     */
    @Test
    public void testCreateIncomingMessageParser() {
        System.out.println("createIncomingMessageParser");

        IncomingMessageParser result = impManager.createIncomingMessageParser();
        assertNotNull(result);
    }

    /**
     * Test of createIncomingMessageFormValidator method, of class IMPManagerImpl.
     */
    @Test
    public void testCreateIncomingMessageFormValidator() {
        System.out.println("createIncomingMessageFormValidator");

        IncomingMessageFormValidator result = impManager.createIncomingMessageFormValidator();
        assertNotNull(result);
    }

    /**
     * Test of createIncomingMessageFormParameterValidator method, of class IMPManagerImpl.
     */
//    @Test
//    public void testCreateIncomingMessageFormParameterValidator() {
//        System.out.println("createIncomingMessageFormParameterValidator");
//
//        IncomingMessageFormParameterValidator result = impManager.createIncomingMessageFormParameterValidator();
//        assertNotNull(result);
//    }

    /**
     * Test of createIncomingMessageFormParameterValidator method, of class IMPManagerImpl.
     */
    @Test
    public void testCreateCommandAction() {
        System.out.println("createCommandAction");

        CommandAction result = impManager.createCommandAction();
        assertNotNull(result);
    }

}