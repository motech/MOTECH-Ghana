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

package org.motechproject.mobile.core.dao.hibernate;

import org.motechproject.mobile.core.dao.GatewayRequestDetailsDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayRequestDetailsImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Date : Oct 1, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class GatewayRequestDetailsDAOImplTest {

    @Autowired
    CoreManager coreManager;
    GatewayRequestDetailsDAO grDao;
    @Autowired
    GatewayRequestDetails grd1;
    @Autowired
    GatewayRequest gr1;
    @Autowired
    GatewayRequest gr2;
    @Autowired
    GatewayRequestDetails grd2;
    @Autowired
    GatewayRequestDetails grd3;
    @Autowired
    GatewayRequestDetails grd4;
    @Autowired
    GatewayRequestDetails grd5;
    @Autowired
    GatewayRequestDetails grd6;

    @Before
    public void setUp() {

        grDao = coreManager.createGatewayRequestDetailsDAO();
        grd1.setId(50000000005l);
        grd1.setMessage("message to send");
        grd1.setNumberOfPages(2);

        gr1.setId(20000000001l);
        gr1.setRecipientsNumber("234556");

        gr2.setId(20000000002l);
        gr2.setRecipientsNumber("12345");

        grd2.setId(50000000001l);
        grd2.setMessage("Message for id 802");
        grd2.setMessageType(MessageType.TEXT);
        grd2.setNumberOfPages(2);

        grd3.setId(50000000002l);
        grd3.setMessage("Message for id 803");
        grd3.setMessageType(MessageType.TEXT);
        grd3.setNumberOfPages(2);

        grd4.setId(50000000003l);
        grd4.setMessage("Message for id 803");
        grd4.setMessageType(MessageType.TEXT);
        grd4.setNumberOfPages(2);

        grd5.setId(50000000004l);
        grd5.setMessage("Message for id 805");
        grd5.setMessageType(MessageType.TEXT);
        grd5.setNumberOfPages(2);

        grDao.save(grd5);

    }

    @After
    public void tearDown() {
        grDao.delete(grd1);
        grDao.delete(grd5);

    }

    /**
     * Test of save method, of class GatewayRequestDetailsDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("test save GatewayRequestDetails object");


        grDao.save(grd1);

        GatewayRequestDetails fromdb = (GatewayRequestDetailsImpl) grDao.getSessionFactory().getCurrentSession().get(GatewayRequestDetailsImpl.class, grd1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(grd1, fromdb);
        Assert.assertEquals(grd1.getId(), fromdb.getId());
        System.out.println(fromdb.toString());
    }

    /**
     * Test of save method with child persisting purpose, of class GatewayRequestDetailsDAOImpl.
     */
    @Test
    public void testSaveWithChild() {
        System.out.println("test save GatewayRequestDetails object");

        grd1.addGatewayRequest(gr1);
        grd1.addGatewayRequest(gr2);
        grDao.save(grd1);
   
        GatewayRequestDetails fromdb = (GatewayRequestDetailsImpl) grDao.getSessionFactory().getCurrentSession().get(GatewayRequestDetailsImpl.class, grd1.getId());
   
        Assert.assertNotNull(fromdb);
        Assert.assertEquals(grd1, fromdb);
        Assert.assertEquals(2, fromdb.getGatewayRequests().size());
        Assert.assertEquals(true, fromdb.getGatewayRequests().contains(gr1));
        Assert.assertEquals(true, fromdb.getGatewayRequests().contains(gr2));

    }

    /**
     * Test of save method with updating purpose, of class GatewayRequestDetailsDAOImpl.
     */
    @Test
    public void testUpdate() {
        System.out.print("test GatewayRequestDetails Update");
        String alteredmsg = "Altered message";
        int altnumofpage = 5;

        GatewayRequestDetails gotfromdb = (GatewayRequestDetailsImpl) grDao.getSessionFactory().getCurrentSession().get(GatewayRequestDetailsImpl.class, grd5.getId());
        gotfromdb.setMessage(alteredmsg);
        gotfromdb.setNumberOfPages(altnumofpage);

        grDao.save(gotfromdb);

        GatewayRequestDetails gotfromdbagain = (GatewayRequestDetailsImpl) grDao.getSessionFactory().getCurrentSession().get(GatewayRequestDetailsImpl.class, gotfromdb.getId());
        Assert.assertNotNull(gotfromdbagain);
        Assert.assertEquals(gotfromdb.getMessage(), gotfromdbagain.getMessage());
        Assert.assertEquals(gotfromdb.getNumberOfPages(), gotfromdbagain.getNumberOfPages());


    }
}
