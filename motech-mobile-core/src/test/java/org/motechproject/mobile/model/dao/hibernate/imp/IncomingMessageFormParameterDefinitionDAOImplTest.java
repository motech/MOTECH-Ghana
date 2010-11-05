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

package org.motechproject.mobile.model.dao.hibernate.imp;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterDefinitionImpl;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormParameterDefinitionDAO;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author joseph Djomeda (joseph@dreamoval.com)
 * @Date Dec 11, 2009
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class IncomingMessageFormParameterDefinitionDAOImplTest {

    @Autowired
    CoreManager coreManager;
    IncomingMessageFormParameterDefinitionDAO impdDAO;
    @Autowired
    private IncomingMessageFormParameterDefinition impd1;
    @Autowired
    private IncomingMessageFormParameterDefinition impd2;
    @Autowired
    private IncomingMessageFormParameterDefinition impd3;

    public IncomingMessageFormParameterDefinitionDAOImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {


        impdDAO = coreManager.createIncomingMessageFormParameterDefinitionDAO();


        impd1.setId(12000000017l);
        impd1.setDateCreated(new Date());
        impd1.setLength(34);
        impd1.setName("paramdefinition name");
        impd1.setRequired(true);

    }

    @After
    public void tearDown() {
    }

    /**
     * Test of save method, of class IncomingMessageFormParameterDefinitionDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("save IncomingMessageFromParameterDefinition");

        impdDAO.save(impd1);

        IncomingMessageFormParameterDefinition fromdb = (IncomingMessageFormParameterDefinition) impdDAO.getSessionFactory().getCurrentSession().get(IncomingMessageFormParameterDefinitionImpl.class, impd1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(fromdb, impd1);
        Assert.assertEquals(fromdb.getId(), impd1.getId());
        System.out.println("the form content: " + fromdb.getId());
        System.out.println(fromdb.toString());

    }
}
