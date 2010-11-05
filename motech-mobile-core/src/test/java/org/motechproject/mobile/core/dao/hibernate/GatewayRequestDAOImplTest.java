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
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.GatewayRequest;
import org.motechproject.mobile.core.model.GatewayRequestDetails;
import org.motechproject.mobile.core.model.GatewayRequestImpl;
import org.motechproject.mobile.core.model.GatewayResponse;
import org.motechproject.mobile.core.model.MStatus;
import org.motechproject.mobile.core.model.MessageType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import org.hibernate.Session;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mobile.core.model.GatewayRequestDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Henry Sampson (henry@dreamoval.com)
 * @author joseph Djomeda (joseph@dreamoval.com)
 * Date Created 03-08-2009
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class GatewayRequestDAOImplTest {

    @Autowired
    ApplicationContext applicationContext;
    
    GatewayRequestDAO mDDAO;
    @Autowired
    private GatewayRequest md1;
    @Autowired
    private GatewayRequest md2;
    @Autowired
    private GatewayRequest md3;
    @Autowired
    private GatewayRequest md4;
    @Autowired
    private GatewayRequest md5;
    @Autowired
    private GatewayRequest md6;
    String text;
    @Autowired
    private GatewayRequest md7;
    @Autowired
    private GatewayResponse rd1;
    @Autowired
    private GatewayResponse rd2;
    @Autowired
    private GatewayRequestDetails grd4;
    @Autowired
    private GatewayRequestDetails grd5;

     Date dateFrom1;
        Date dateFrom2;
        Date dateTo1;
        Date dateTo2;
        Date schedule;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        mDDAO = (GatewayRequestDAO) applicationContext.getBean("gatewayRequestDAO", GatewayRequestDAO.class);
        
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFrom1 = df.parse("2009-08-21");
            dateFrom2 = df.parse("2009-09-04");
            dateTo1 = df.parse("2009-10-30");
            dateTo2 = df.parse("2009-11-04");
            schedule = df.parse("2009-10-02");
        } catch (ParseException e) {
            e.printStackTrace();
        }


        text = "First";
        md1.setId(1000000001l);
        md1.setMessage(text);
        md1.setRecipientsNumber("123445");
        md1.setDateFrom(new Date());

        md2.setId(10000000002l);
        md2.setMessage("Second");
        md2.setRecipientsNumber("123445");
        md2.setDateFrom(new Date());

        md3.setId(10000000003l);
        md3.setMessage("Third");
        md3.setRecipientsNumber("123445");
        md3.setDateFrom(new Date());

        md4.setId(10000000004l);
        md4.setMessage("Test for dummies 4");
        md4.setRecipientsNumber("123445, 54321");
        md4.setDateFrom(dateFrom1);
        md4.setDateTo(dateTo1);
        md4.setMessageStatus(MStatus.FAILED);

        md5.setId(10000000005l);
        md5.setDateSent(new Date());
        md5.setRecipientsNumber("12345,54321");
        md5.setMessage("insertion with responsedetailsobject");
        md5.setDateFrom(dateFrom2);
        md5.setDateTo(dateTo2);
        md5.setMessageStatus(MStatus.FAILED);

        md6.setId(10000000006l);
        md6.setDateSent(new Date());
        md6.setRecipientsNumber("12345,54321");
        md6.setMessage("another test for dummies");
        md6.setDateFrom(new Date());


        rd1.setId(40000000001l);
        rd1.setMessageStatus(MStatus.PENDING);
        rd1.setRecipientNumber("123445");

        rd2.setId(40000000002l);
        rd2.setMessageStatus(MStatus.FAILED);
        rd2.setRecipientNumber("54321");

        grd4.setId(40000000003l);
        grd4.setMessage("some messaege 4");
        grd4.setMessageType(MessageType.TEXT);
        grd4.setNumberOfPages(1);
        Set<GatewayRequest> gwrs4 = new HashSet<GatewayRequest>();
        gwrs4.add(md4);
        grd4.setGatewayRequests(gwrs4);

        grd5.setId(40000000004l);
        grd5.setMessage("some messaege 5");
        grd5.setMessageType(MessageType.TEXT);
        grd5.setNumberOfPages(1);
        Set<GatewayRequest> gwrs5 = new HashSet<GatewayRequest>();
        gwrs5.add(md4);
        grd5.setGatewayRequests(gwrs5);

        setUpInitialData();
    }

    @After
    public void tearDown() {
//        Transaction tx = ((Session) mDDAO.getSessionFactory().getCurrentSession()).beginTransaction();

        mDDAO.delete(md1);
        mDDAO.delete(md2);
        mDDAO.delete(md3);
        mDDAO.delete(md4);
        mDDAO.delete(md5);
        mDDAO.delete(md6);

//        tx.commit();

    }

    public void setUpInitialData() {
//        Transaction tx = ((Session) mDDAO.getSessionFactory().getCurrentSession()).beginTransaction();

        mDDAO.save(md1);
        mDDAO.save(md2);
        mDDAO.save(md3);
        mDDAO.save(md4);
        mDDAO.save(md5);
        mDDAO.save(md6);

//        tx.commit();

    }

    public GatewayRequestDAOImplTest() {
    }

    /**
     * Test of delete method, of class GatewayRequestDAOImpl.
     */
    @Test
    public void testDelete() {
        System.out.println("Delete");

        mDDAO.delete(md2);
        
        GatewayRequest fromdb = (GatewayRequestImpl) mDDAO.getSessionFactory().getCurrentSession().get(GatewayRequestImpl.class, md2.getId());
        Assert.assertNull(fromdb);


    }

    /**
     * Test of save method with update purpose, of class GatewayRequestDAOImpl.
     */
    @Test
    public void testUpdate() {
        System.out.println("Update");

        md1.setMessage("First altered");
        md1.setDateFrom(new Date());
        md1.setDateTo(new Date());
        mDDAO.save(md1);
        GatewayRequest fromdb = (GatewayRequestImpl)mDDAO.getSessionFactory().getCurrentSession().get(GatewayRequestImpl.class, md1.getId());
        Assert.assertFalse(text.equals(fromdb.getMessage()));
    }

    /**
     * Test of save method with child saving purpose, of class GatewayRequestDAOImpl.
     */
    @Test
    public void testSaveWithResponse() {
        System.out.println("saving with response object");
        List<GatewayResponse> res = new ArrayList<GatewayResponse>();
        res.add(rd1);
        res.add(rd2);
        md5.addResponse(res);
        Session session = (Session) mDDAO.getSessionFactory().getCurrentSession();

        mDDAO.save(md5);

        GatewayRequest fromdb = (GatewayRequestImpl) session.get(GatewayRequestImpl.class, md5.getId());
        Set<GatewayResponse> fromdbchild = fromdb.getResponseDetails();
        ArrayList<GatewayResponse> children = new ArrayList<GatewayResponse>();

        for (Iterator it = fromdbchild.iterator(); it.hasNext();) {
            children.add((GatewayResponse) it.next());
        }

        Assert.assertEquals(2, fromdbchild.size());
    }

    /**
     * Test of save method with GatewayRequestDetails.
     */
    @Test
    public void testSaveWithDetails() {
        System.out.println("saving with with request details");
        GatewayRequestDetails grd = (GatewayRequestDetails) applicationContext.getBean("gatewayRequestDetails", GatewayRequestDetails.class);
        grd.setMessage("Test message");
        grd.setMessageType(MessageType.TEXT);
        grd.setNumberOfPages(1);

        GatewayRequest md8 = (GatewayRequest) applicationContext.getBean("gatewayRequest", GatewayRequest.class);
        md8.setMessage("Test message");
        md8.setRecipientsNumber("123445");
        md8.setDateFrom(new Date());
        md8.setGatewayRequestDetails(grd);

        mDDAO.save(md8);
        GatewayRequest result = (GatewayRequest) mDDAO.getById(md8.getId());

        Assert.assertEquals(result.getGatewayRequestDetails().getId(), md8.getGatewayRequestDetails().getId());
    }

    /**
     * Test of save method with GatewayRequestDetails.
     */
    @Test
    public void testMergeWithDetails() {
        System.out.println("saving with with request details");
        GatewayRequestDetails grd = (GatewayRequestDetails) applicationContext.getBean("gatewayRequestDetails",GatewayRequestDetails.class);
        grd.setMessage("Test message");
        grd.setMessageType(MessageType.TEXT);
        grd.setNumberOfPages(1);

        GatewayRequest md9 = (GatewayRequest) applicationContext.getBean("gatewayRequest", GatewayRequest.class);
        md9.setMessage("Test message");
        md9.setRecipientsNumber("123445");
        md9.setDateFrom(new Date());
        md9.setGatewayRequestDetails(grd);

        mDDAO.merge(md9);
        GatewayRequest result = (GatewayRequest) mDDAO.getById(md9.getId());

        Assert.assertEquals(result.getGatewayRequestDetails().getId(), md9.getGatewayRequestDetails().getId());
    }

    /**
     * Test of getById method, of class GatewayRequestDAOImpl.
     */
    @Test
    public void testGetById() {
        System.out.println("testing FindById");

        GatewayRequest result = (GatewayRequest) mDDAO.getById(md1.getId());
           System.out.println("the date lastModified: " + result.getLastModified());
        Assert.assertSame(md1, result);
        Assert.assertEquals(md1.getId(), result.getId());
        Assert.assertEquals(md1.getMessage(), result.getMessage());
        Assert.assertEquals(md1.getRecipientsNumber(), result.getRecipientsNumber());
        Assert.assertEquals(md1.getDateFrom(), result.getDateFrom());

         System.out.println(result.toString());
    }

    /**
     * Test of findByExample method, of class GatewayRequestDAOImpl.
     */
    @Test
    public void testFindByExample() {
        System.out.println("testing findByExample");

        List<GatewayRequest> expResult = new ArrayList<GatewayRequest>();
        expResult.add(md1);
        expResult.add(md2);
        expResult.add(md3);

        md7.setRecipientsNumber("123445");

        List<GatewayRequest> result = mDDAO.findByExample(md7);

        Assert.assertEquals(expResult.size(), result.size());
        Assert.assertEquals(true, result.contains(md1));
        Assert.assertEquals(true, result.contains(md2));
        Assert.assertEquals(true, result.contains(md3));

    }


    /**
     * Test of getByStatusAndSchedule method, of class GatewayRequestDAOImpl.
     */
    @Ignore
    @Test
    public void testGetByStatusAndSchedule() {
        System.out.println("getByStatusAndSchedule");
        List<GatewayRequest> expResult = new ArrayList<GatewayRequest>();
        md4.setGatewayRequestDetails(grd4);
        md5.setGatewayRequestDetails(grd5);
        expResult.add(md4);
        expResult.add(md5);
        MStatus status = MStatus.FAILED;
        List result = mDDAO.getByStatusAndSchedule(status, schedule);
        Assert.assertFalse(result.isEmpty());
        Assert.assertEquals(expResult.size(), result.size());
        Assert.assertEquals(true, result.contains(md4));
        Assert.assertEquals(true, result.contains(md5));
      
    }
}
