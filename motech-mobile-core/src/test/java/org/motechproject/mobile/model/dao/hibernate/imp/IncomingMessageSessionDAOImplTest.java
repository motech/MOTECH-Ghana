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
import org.motechproject.mobile.core.model.IncomingMessageSession;
import org.motechproject.mobile.core.model.IncomingMessageSessionImpl;
import org.motechproject.mobile.model.dao.imp.IncomingMessageSessionDAO;
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
 * @Date
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class IncomingMessageSessionDAOImplTest {


    @Autowired
    CoreManager coreManager;
    IncomingMessageSessionDAO imsDAO;

    @Autowired
    private IncomingMessageSession ims1;
    @Autowired
    private IncomingMessageSession ims2;
    @Autowired
    private IncomingMessageSession ims3;

    String requesterPhone = "2233445566";

    public IncomingMessageSessionDAOImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {


    }

    @Before
    public void setUp() {
;
        imsDAO = coreManager.createIncomingMessageSessionDAO();

        ims1.setId(12000000019l);
        ims1.setDateStarted(new Date());
        ims1.setFormCode("fc_ims1");
        ims1.setLastActivity(new Date());
        ims1.setRequesterPhone("233243667788");


        ims2.setId(12000000020l);
        ims2.setDateStarted(new Date());
        ims2.setFormCode("fc_ims2");
        ims2.setLastActivity(new Date());
        ims2.setRequesterPhone(requesterPhone);

        ims3.setId(12000000021l);
        ims3.setDateStarted(new Date());
        ims3.setFormCode("fc_ims3");
        ims3.setLastActivity(new Date());
        ims3.setRequesterPhone(requesterPhone);


        imsDAO.save(ims2);
        imsDAO.save(ims3);

    }

    @After
    public void tearDown() {
   
        imsDAO.delete(ims2);
        imsDAO.delete(ims3);
    }


    /**
     * Test of save method, of class IncomingMessageSessionDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("save IncominMessageSession");

        imsDAO.save(ims1);

        IncomingMessageSession fromdb = (IncomingMessageSession) imsDAO.getSessionFactory().getCurrentSession().get(IncomingMessageSessionImpl.class, ims1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(fromdb, ims1);
        Assert.assertEquals(fromdb.getId(), ims1.getId());
        System.out.println("the formcode: " + fromdb.getFormCode());
        System.out.println(fromdb.toString());


    }

}
