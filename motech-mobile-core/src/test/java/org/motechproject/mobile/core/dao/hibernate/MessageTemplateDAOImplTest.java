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

import org.motechproject.mobile.core.dao.LanguageDAO;
import org.motechproject.mobile.core.dao.MessageTemplateDAO;
import org.motechproject.mobile.core.dao.NotificationTypeDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.MessageTemplate;
import org.motechproject.mobile.core.model.MessageTemplateImpl;
import org.motechproject.mobile.core.model.MessageType;
import org.motechproject.mobile.core.model.NotificationType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Date : Sep 27, 2009
 * @author joseph Djomeda (joseph@dreamoval.com)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/test-core-config.xml"})
@TransactionConfiguration
@Transactional
public class MessageTemplateDAOImplTest {

    @Autowired
    CoreManager coreManager;
    @Autowired
    MessageTemplate mt1;
    @Autowired
    MessageTemplate mt2;
    @Autowired
    MessageTemplate mt3;
    @Autowired
    MessageTemplate mt4;
    @Autowired
    MessageTemplate mt5;
    @Autowired
    MessageTemplate mt6;
    MessageTemplateDAO mtDao;
    @Autowired
    NotificationType nt1;
    @Autowired
    NotificationType nt2;
    @Autowired
    NotificationType nt3;
    @Autowired
    Language l1;
    @Autowired
    Language l2;
    NotificationTypeDAO ntDao;
    LanguageDAO lDao;
    String template = "some template";
    MessageType type = MessageType.TEXT;

    public MessageTemplateDAOImplTest() {
    }

    @Before
    public void setUp() {
      
        nt1.setId(401L);
        nt1.setName("some name");

        nt2.setId(402L);
        nt2.setName("second name");
        nt2.setDescription("for second test");

        nt3.setId(407L);
        nt3.setName("third name");
        nt3.setDescription("for third test");

        l1.setId(90000000001l);
        l1.setCode("sk");

        l2.setId(90000000002l);
        l2.setCode("ch");

        mtDao = coreManager.createMessageTemplateDAO();
        lDao = coreManager.createLanguageDAO();
        ntDao = coreManager.createNotificationTypeDAO();

        mt1.setId(90000000003l);
        mt1.setNotificationType(nt1);
        mt1.setLanguage(l1);
        mt1.setMessageType(type);
        mt1.setTemplate("test template for test 1");

        mt2.setId(90000000004l);
        mt2.setNotificationType(nt2);
        mt2.setLanguage(l1);
        mt2.setMessageType(type);
        mt2.setTemplate(template);

        mt3.setId(90000000005l);
        mt3.setNotificationType(nt3);
        mt3.setLanguage(l1);
        mt3.setMessageType(type);
        mt3.setTemplate(template);

        mt4.setId(90000000006l);
        mt4.setNotificationType(nt1);
        mt4.setLanguage(l1);
        mt4.setMessageType(type);
        mt4.setTemplate("template for message 4");

        mt5.setId(90000000007l);
        mt5.setNotificationType(nt3);
        mt5.setLanguage(l1);
        mt5.setMessageType(type);
        mt5.setTemplate("template for message 5");

   
        lDao.save(l1);
        lDao.save(l2);
        ntDao.save(nt1);
        ntDao.save(nt2);
        ntDao.save(nt3);
        mtDao.save(mt3);
        mtDao.save(mt4);
        mtDao.save(mt5);

    }

    @After
    public void tearDown() {
   
        mtDao.delete(mt1);
        mtDao.delete(mt2);
        mtDao.delete(mt3);
        mtDao.delete(mt4);
        mtDao.delete(mt5);
        ntDao.delete(nt1);
        ntDao.delete(nt2);
        ntDao.delete(nt3);
        lDao.delete(l1);
        lDao.delete(l2);

    }

    /**
     * Test of save method, of class MessagTemplateDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.print("test save MessageTemplate Object");

        mtDao.save(mt1);


        MessageTemplate fromdb = (MessageTemplate) mtDao.getSessionFactory().getCurrentSession().get(MessageTemplateImpl.class, mt1.getId());
        Assert.assertNotNull(fromdb);

    }

    /**
     * Test of save method, of class MessagTemplateDAOImpl.
     */
    @Test
    public void testGetTemplateByLangNotifMType() {
        System.out.println("getTemplateByLangNotifMType");

     
        mtDao.getSessionFactory().getCurrentSession().save(mt2);

        MessageTemplate result = mtDao.getTemplateByLangNotifMType(l1, nt2, type);
        Assert.assertNotNull(result);
        Assert.assertEquals(mt2, result);

    }

    /**
     * Test of getById method, of class MessagTemplateDAOImpl.
     */
    @Test
    public void testGetById() {
        System.out.println("test MessageTemplate getById");
        MessageTemplate mt = (MessageTemplate) mtDao.getById(mt3.getId());
        Assert.assertNotNull(mt);
        Assert.assertEquals(mt3, mt);
        Assert.assertEquals(mt3.getId(), mt.getId());
        Assert.assertEquals(mt3.getLanguage(), mt.getLanguage());
        Assert.assertEquals(mt3.getTemplate(), mt.getTemplate());

    }



    /**
     * Test of save method with update purpose, of class MessagTemplateDAOImpl.
     */
    @Test
    public void testUpdate() {
        System.out.println("test MessageTemplateDAO update");
        String alttemplate = "altered template";
        Date altdate = new Date();



        MessageTemplate fromdb1 = (MessageTemplateImpl) mtDao.getSessionFactory().getCurrentSession().get(MessageTemplateImpl.class, mt5.getId());
        fromdb1.setDateCreated(altdate);
        fromdb1.setMessageType(type);
        fromdb1.setNotificationType(nt2);
        fromdb1.setTemplate(alttemplate);

        mtDao.save(fromdb1);



        MessageTemplate fromdb = (MessageTemplateImpl) mtDao.getSessionFactory().getCurrentSession().get(MessageTemplateImpl.class, fromdb1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(mt5, fromdb);
        Assert.assertEquals(nt2, fromdb.getNotificationType());
        Assert.assertEquals(alttemplate, fromdb.getTemplate());
        Assert.assertEquals(altdate, fromdb.getDateCreated());
    }

    /**
     * Test of findByExample method, of class MessagTemplateDAOImpl.
     */
//    @Test
//    public void testFindByExample() {
//        System.out.print("test MessageTemplate findByExample");
//        List expResult = new ArrayList();
//        expResult.add(mt4);
//        mt6.setTemplate(mt4.getTemplate());
//        List result = mtDao.findByExample(mt6);
//        Assert.assertNotNull(result);
//        Assert.assertEquals(expResult, result);
//        Assert.assertEquals(expResult.size(), result.size());
//
//    }
    /**
     * Test of delete method, of class MessagTemplateDAOImpl.
     */
    @Test
    public void testDelete() {
        System.out.println("test MessageTemplate delete");
   
        mtDao.delete(mt4);


        MessageTemplate fromdb = (MessageTemplate) mtDao.getSessionFactory().getCurrentSession().get(MessageTemplateImpl.class, mt4.getId());
        Assert.assertNull(fromdb);
    }
}
