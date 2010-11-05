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

import org.motechproject.mobile.core.dao.GatewayRequestDAO;
import org.motechproject.mobile.core.dao.GatewayResponseDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.GatewayResponseImpl;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.util.DateProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.dao.MessageRequestDAO;
import org.motechproject.mobile.core.model.MessageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Date : Aug 4, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class GatewayResponseDAOImplTest {

    public GatewayResponseDAOImplTest() {
    }
    @Autowired
    CoreManager coreManager;
    GatewayResponseDAO rDDAO;
    GatewayRequestDAO grDAO;
    MessageRequestDAO mrDAO;

    @Autowired
    private GatewayResponse gr1;
    @Autowired
    private GatewayResponse gr2;
    @Autowired
    private GatewayResponse gr3;
    @Autowired
    private GatewayResponse gr4;
    @Autowired
    private GatewayResponse gr5;
    @Autowired
    private GatewayResponse gr6;
    @Autowired
    private GatewayResponse gr7;
    @Autowired
    private GatewayResponse gr8;

    @Autowired
    private GatewayRequest grq1;

    @Autowired
    private MessageRequest mr1;

    String requestId = "dfpoiufkdl";

    @Before
    public void setUp() {

        rDDAO = coreManager.createGatewayResponseDAO();
        grDAO = coreManager.createGatewayRequestDAO();
        mrDAO = coreManager.createMessageRequestDAO();

        gr1.setId(60000000001l);
        gr1.setRecipientNumber("123445");
        gr1.setMessageStatus(MStatus.PENDING);
        gr1.setDateCreated(DateProvider.convertToDateTime("2009-08-01"));
        gr1.setRequestId("dfpoi234ufkdl");

        gr2.setId(60000000002l);
        gr2.setRecipientNumber("123445");
        gr2.setMessageStatus(MStatus.PENDING);
        gr2.setDateCreated(DateProvider.convertToDateTime("2009-08-01"));
        gr2.setRequestId(requestId);

        gr3.setId(60000000003l);
        gr3.setRecipientNumber("234567");
        gr3.setMessageStatus(MStatus.DELIVERED);
        gr3.setDateCreated(DateProvider.convertToDateTime("2009-08-01"));
        gr3.setRequestId(requestId);

        gr4.setId(60000000004l);
        gr4.setRecipientNumber("345678");
        gr4.setMessageStatus(MStatus.DELIVERED);
        gr4.setDateCreated(DateProvider.convertToDateTime("2009-09-01"));
        gr4.setRequestId(requestId);

        gr5.setId(60000000005l);
        gr5.setRecipientNumber("765432");
        gr5.setMessageStatus(MStatus.FAILED);
        gr5.setDateCreated(DateProvider.convertToDateTime("2009-09-01"));
        gr5.setRequestId(requestId);

        gr7.setId(60000000006l);
        gr7.setRecipientNumber("23459");
        gr7.setMessageStatus(MStatus.FAILED);
        gr7.setDateCreated(DateProvider.convertToDateTime("2009-09-01"));
        gr7.setRequestId("88787");


        gr8.setId(60000000007l);
        gr8.setRecipientNumber("23459");
        gr8.setMessageStatus(MStatus.FAILED);
        gr8.setDateCreated(new Date());
        gr8.setRequestId(requestId);

        mr1.setId(60000000008l);

        grq1.setId(30000000001l);
        grq1.setMessage("message to be tested with trynumber and requestID");
        grq1.setMessageStatus(MStatus.FAILED);
        grq1.setRequestId("88787");
        grq1.setTryNumber(2);
        grq1.setRecipientsNumber("7788899000");
        grq1.setMessageRequest(mr1);

        grq1.addResponse(gr7);

        setUpInitialData();

    }

    public void setUpInitialData() {

   
        mrDAO.save(mr1);
        grDAO.save(grq1);
        rDDAO.save(gr2);
        rDDAO.save(gr3);
        rDDAO.save(gr4);
        rDDAO.save(gr5);
        rDDAO.save(gr7);
        rDDAO.save(gr8);


    }

    @After
    public void teardown(){

grDAO.delete(grq1);
        rDDAO.delete(gr1);
        rDDAO.delete(gr2);
        rDDAO.delete(gr3);
        rDDAO.delete(gr4);
        rDDAO.delete(gr5);
        rDDAO.delete(gr7);
        rDDAO.delete(gr8);
        
        mrDAO.delete(mr1);

    }


    @Test
    public void testSave() {
        System.out.println("Save Response");
   
        rDDAO.save(gr1);
 

        GatewayResponse fromdb = (GatewayResponse) rDDAO.getSessionFactory().getCurrentSession().get(GatewayResponseImpl.class, gr1.getId());


        Assert.assertEquals(gr1.getId(), fromdb.getId());
        Assert.assertEquals(gr1.getRecipientNumber(), fromdb.getRecipientNumber());
        Assert.assertEquals(gr1.getRecipientNumber(), fromdb.getRecipientNumber());
        System.out.println(fromdb.toString());
    }

    @Test
    public void testDelete() {
        System.out.println("Delete Response");
 
        rDDAO.delete(gr2);

        GatewayResponse fromdb = (GatewayResponseImpl) rDDAO.getSessionFactory().getCurrentSession().get(GatewayResponseImpl.class, gr2.getId());
        Assert.assertNull(fromdb);
    }

    @Test
    public void testUpdate() {
        System.out.println("Update Response");
        gr3.setMessageStatus(MStatus.FAILED);
        gr3.setRecipientNumber("4444444");
 
        rDDAO.save(gr3);

        GatewayResponse rd3fromdb = (GatewayResponseImpl) rDDAO.getSessionFactory().getCurrentSession().get(GatewayResponseImpl.class, gr3.getId());
   
        Assert.assertEquals(gr3.getId(), rd3fromdb.getId());
        Assert.assertEquals(MStatus.FAILED, rd3fromdb.getMessageStatus());
        Assert.assertEquals("4444444", rd3fromdb.getRecipientNumber());
    }

    @Test
    public void testGetById() {
        System.out.println("Find by ResponseDetails id ");

        GatewayResponse fromdb = (GatewayResponseImpl) rDDAO.getById(gr4.getId());
        System.out.println("last modified field: " + fromdb.getLastModified());


        Assert.assertEquals(gr4.getId(), fromdb.getId());
        Assert.assertEquals(gr4.getMessageStatus(), fromdb.getMessageStatus());
        Assert.assertEquals(gr4.getRecipientNumber(), fromdb.getRecipientNumber());
    }

    @Ignore
    @Test
    public void testFindByExample() {
        System.out.println("Find byResponseDetails example");
        gr6.setMessageStatus(MStatus.FAILED);
        List<GatewayResponse> expResult = new ArrayList<GatewayResponse>();
        expResult.add(gr4);
        expResult.add(gr5);
        List<GatewayResponse> result = rDDAO.findByExample(gr6);

        Assert.assertNotNull(result);
        Assert.assertEquals(expResult.size(), result.size());
        Assert.assertTrue(expResult.contains(gr4));
        Assert.assertTrue(expResult.contains(gr5));
    }

   
    /**
     * Test of getMostRecentResponseByRequestId method, of class GatewayResponseDAOImpl.
     */
    @Test
    public void testGetMostRecentResponseByRequestId() {
        System.out.println("getMostRecentResponseByRequestId");

        GatewayResponse result = rDDAO.getMostRecentResponseByMessageId(grq1.getMessageRequest().getId());
        System.out.println(result.getId());
        Assert.assertNotNull(result);
        Assert.assertEquals(gr7, result);
        Assert.assertEquals(gr7.getId(), result.getId());
        System.out.println("====================================================================================================");
        System.out.println(result.toString());

    }

    /**
     * Test of getByRequestIdAndTryNumber method, of class GatewayResponseDAOImpl.
     */
//    @Test
//    public void testGetByRequestIdAndTryNumber() {
//        System.out.println("getByRequestIdAndTryNumber");
//        String requestId = "88787";
//        int tryNumber = 2;
//        GatewayResponse result = rDDAO.getByMessageIdAndTryNumber(grq1.getMessageRequest().getId(), tryNumber);
//        Assert.assertNotNull(result);
//        Assert.assertEquals(gr7, result);
//        Assert.assertEquals(gr7.getId(), result.getId());
//        Assert.assertEquals(gr7.getMessageStatus(), result.getMessageStatus());
//        Assert.assertEquals(gr7.getGatewayRequest(), result.getGatewayRequest());
//        Assert.assertEquals(gr7.getGatewayRequest().getTryNumber(), result.getGatewayRequest().getTryNumber());
//
//    }
}
