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

import org.motechproject.mobile.model.dao.imp.IncomingMessageDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncMessageResponseStatus;
import org.motechproject.mobile.core.model.IncMessageSessionStatus;
import org.motechproject.mobile.core.model.IncMessageStatus;
import org.motechproject.mobile.core.model.IncomingMessage;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageImpl;
import org.motechproject.mobile.core.model.IncomingMessageResponse;
import org.motechproject.mobile.core.model.IncomingMessageSession;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDAO;
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
 * Date: Dec 14, 2009
 * @author Joseph Djomeda (joseph@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class IncomingMessageDAOImplTest {

    @Autowired
    CoreManager coreManager;
    IncomingMessageDAO imDAO;
    IncomingMessageSessionDAO imsDAO;
    IncomingMessageFormDAO imfDAO;
    @Autowired
    private IncomingMessageForm imf2;
    @Autowired
    private IncomingMessageForm imf4;
    @Autowired
    private IncomingMessageForm imf5;

    @Autowired
    private IncomingMessageResponse imfr3;
    @Autowired
    private IncomingMessageResponse imfr4;
    @Autowired
    private IncomingMessageResponse imfr5;

    @Autowired
    private IncomingMessageSession ims1;
    @Autowired
    private IncomingMessageSession ims4;
    @Autowired
    private IncomingMessageSession ims5;

    @Autowired
    private IncomingMessage im1;
    @Autowired
    private IncomingMessage im2;
    @Autowired
    private IncomingMessage im3;
    @Autowired
    private IncomingMessage im4;
    @Autowired
    private IncomingMessage im5;

    public IncomingMessageDAOImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
     
        imDAO = coreManager.createIncomingMessageDAO();
        imsDAO = coreManager.createIncomingMessageSessionDAO();


        im1.setId(11000000001l);
        im1.setContent("content im1");
        im1.setLastModified(new Date());
        im1.setMessageStatus(IncMessageStatus.PROCESSING);

        im2.setId(11000000002l);
        im2.setContent("content im2");
        im2.setLastModified(new Date());
        im2.setMessageStatus(IncMessageStatus.PROCESSING);

        im3.setId(11000000003l);
        im3.setContent("content im3");
        im3.setLastModified(new Date());
        im3.setMessageStatus(IncMessageStatus.PROCESSING);

        im4.setId(11000000004l);
        im4.setContent("content im4");
        im4.setDateCreated(new Date());
        im4.setMessageStatus(IncMessageStatus.PROCESSING);

        imfr3.setId(11000000005l);
        imfr3.setContent("response 3");
        imfr3.setDateCreated(new Date());
        imfr3.setMessageResponseStatus(IncMessageResponseStatus.SAVED);

        imfr4.setId(11000000006l);
        imfr4.setContent("response 4");
        imfr4.setDateCreated(new Date());
        imfr4.setMessageResponseStatus(IncMessageResponseStatus.SAVED);

        ims1.setId(11000000007l);
        ims1.setFormCode("code_IM");
        ims1.setRequesterPhone("1122334455");
        ims1.setMessageSessionStatus(IncMessageSessionStatus.STARTED);
        ims1.setLastActivity(new Date());

        ims4.setId(11000000008l);
        ims4.setFormCode("code_IM654");
        ims4.setRequesterPhone("1122334455");
        ims4.setMessageSessionStatus(IncMessageSessionStatus.STARTED);
        ims4.setLastActivity(new Date());

        imf2.setId(11000000009l);
        imf2.setDateCreated(new Date());
        imf2.setMessageFormStatus(IncMessageFormStatus.NEW);

        imf4.setId(11000000010l);
        imf4.setDateCreated(new Date());
        imf4.setMessageFormStatus(IncMessageFormStatus.NEW);

        im4.setIncomingMessageForm(imf4);
        im4.setIncomingMessageResponse(imfr4);
        ims4.addIncomingMessage(im4);

   
        imsDAO.save(ims1);
        imsDAO.save(ims4);
        imDAO.save(im4);


    }

    @After
    public void tearDown() {
  
        imsDAO.delete(ims1);
        imsDAO.delete(ims4);
        imDAO.delete(im1);
        imDAO.delete(im2);
        imDAO.delete(im4);


    }

    /**
     * Test of save method, of class IncomingMessageDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("IncomingMessageDAOImpl save");

        imDAO.save(im1);


        IncomingMessage fromdb = (IncomingMessage) imDAO.getSessionFactory().getCurrentSession().get(IncomingMessageImpl.class, im1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(im1, fromdb);
        Assert.assertEquals(im1.getId(), fromdb.getId());
        System.out.println("the form content:\n " + fromdb.getContent());
        System.out.println(fromdb.toString());

    }

    /**
     * Test of save method with IncominingForm, of class IncomingMessageDAOImpl.
     */
    @Test
    public void testSaveWithIncomingForm() {
        System.out.println("IncomingMessageDAOImpl save with IncomingMessageForm");
    
        im2.setIncomingMessageForm(imf2);

        imDAO.save(im2);


        IncomingMessage fromdb = (IncomingMessage) imDAO.getSessionFactory().getCurrentSession().get(IncomingMessageImpl.class, im2.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(im2, fromdb);
        Assert.assertEquals(im2.getId(), fromdb.getId());
        Assert.assertEquals(imf2, fromdb.getIncomingMessageForm());
        System.out.println(fromdb.toString());

    }

    /**
     * Test of save method with IncomingMessageSession, of class IncomingMessageDAOImpl.
     */
    @Test
    public void testSaveWithIncomingMessageSession() {
        System.out.println("IncomingMessageDAOImpl save with Session");
    
        ims1.addIncomingMessage(im1);
   
        imDAO.save(im1);
   

        IncomingMessage fromdb = (IncomingMessage) imDAO.getSessionFactory().getCurrentSession().get(IncomingMessageImpl.class, im1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(im1, fromdb);
        Assert.assertEquals(im1.getId(), fromdb.getId());
        Assert.assertEquals(im1.getIncomingMsgSession(), fromdb.getIncomingMsgSession());
        Assert.assertEquals(im1.getIncomingMsgSession().getId(), fromdb.getIncomingMsgSession().getId());
        Assert.assertEquals(im1.getIncomingMsgSession().getIncomingMessages(), fromdb.getIncomingMsgSession().getIncomingMessages());
        System.out.println("the form content:\n " + fromdb.getContent());
        System.out.println(fromdb.toString());

    }

    /**
     * Test of save method with IncomingMessageResponse, of class IncomingMessageDAOImpl.
     */
    @Test
    public void testSaveWithIncomingMessageResponse() {
        System.out.println("IncomingMessageDAOImpl save with IncomingMessageResponse");



    }

    /**
     * Test of delete method, of class IncomingMessageDAOImpl.
     */
    @Test
    public void testDelete() {
        System.out.println("IncomingMessageDAOImpl delete");
     
        imDAO.delete(im4);


        IncomingMessage fromdb = (IncomingMessage) imDAO.getSessionFactory().getCurrentSession().get(IncomingMessageImpl.class, im4.getId());

        Assert.assertNull(fromdb);
    }

    /**
     * Test of update method, of class IncomingMessageDAOImpl.
     */
    @Test
    public void testUpdate() {
        System.out.println("IncomingMessageDAOImpl update");

        String updated ="updated content";
        IncMessageStatus updatedStat = IncMessageStatus.PROCESSED;
        IncomingMessage fromdb1 = (IncomingMessage) imDAO.getSessionFactory().getCurrentSession().get(IncomingMessageImpl.class, im4.getId());
        fromdb1.setContent(updated);
        fromdb1.setMessageStatus(updatedStat);

        imDAO.save(fromdb1);
;

        IncomingMessage fromdb = (IncomingMessage) imDAO.getSessionFactory().getCurrentSession().get(IncomingMessageImpl.class, fromdb1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(fromdb1, fromdb);
        Assert.assertEquals(fromdb1.getId(), fromdb.getId());
        Assert.assertEquals(updated, fromdb.getContent());
        Assert.assertEquals(updatedStat, fromdb.getMessageStatus());
        System.out.println(fromdb.toString());

    }
}
