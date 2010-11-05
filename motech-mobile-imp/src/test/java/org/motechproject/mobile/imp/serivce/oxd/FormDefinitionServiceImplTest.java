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

package org.motechproject.mobile.imp.serivce.oxd;

import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author user
 */
public class FormDefinitionServiceImplTest {

    public FormDefinitionServiceImplTest() {
    }

    @Test
    public void testInit() throws Exception {
        String resource = "/MoTeCHDataEntry.xml";
        FormDefinitionServiceImpl fds = new FormDefinitionServiceImpl();
        Set resources = new HashSet();
        resources.add(resource);
        fds.setOxdFormDefResources(resources);
        fds.init();
        Assert.assertEquals(1, fds.getStudies().size());
        Assert.assertEquals(9, fds.getStudyForms(0).size());
    }

//    @Test
//    public void testGetXForms() throws Exception {
//    }
//
//    @Test
//    public void testGetOxdFormDefResources() {
//    }
//
//    @Test
//    public void testSetOxdFormDefResources() {
//    }
//
//    @Test
//    public void testAddOxdFormDefResources() {
//    }
//
//    @Test
//    public void testGetStudies() {
//    }
//
//    @Test
//    public void testGetUsers() {
//    }
//
//    @Test
//    public void testGetStudyName() {
//    }
//
//    @Test
//    public void testGetStudyForms() {
//    }

}