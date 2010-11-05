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

import org.motechproject.mobile.core.dao.NotificationTypeDAO;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.NotificationType;
import org.motechproject.mobile.core.model.NotificationTypeImpl;
import java.util.ArrayList;
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
public class NotificationTypeDAOImplTest {

    public NotificationTypeDAOImplTest() {
    }
    NotificationTypeDAO nDao;
    @Autowired
    CoreManager coreManager;
    @Autowired
    NotificationType nt1;
    @Autowired
    NotificationType nt2;
    @Autowired
    NotificationType nt3;
    @Autowired
    NotificationType nt4;
    @Autowired
    NotificationType nt5;
    @Autowired
    NotificationType nt6;

    @Before
    public void setUp() {
        nDao = coreManager.createNotificationTypeDAO();
        nt1.setId(701L);
        nt1.setName("the name");
        nt1.setDescription("the description");

        nt2.setId(702L);
        nt2.setName("the name of notif 2");
        nt2.setDescription("the description for notif 2");

        nt3.setId(703L);
        nt3.setName("the name of notif 3");
        nt3.setDescription("the description for notif 3");

        nt4.setId(704L);
        nt4.setName("the name of notif 4");
        nt4.setDescription("the description for notif 4");

        nt5.setId(705L);
        nt5.setName("the name of notif 5");
        nt5.setDescription("the description fo notif 5");


        nDao.save(nt2);
        nDao.save(nt3);
        nDao.save(nt4);

    }

    @After
    public void tearDown() {

        nDao.delete(nt1);
        nDao.delete(nt2);
        nDao.delete(nt3);
        nDao.delete(nt4);

    }

    /**
     * Test of save method, of NotificationTypeDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("test save NotificationType");
  
        nDao.save(nt1);



        NotificationType fromdb = (NotificationType) nDao.getSessionFactory().getCurrentSession().get(NotificationTypeImpl.class, nt1.getId());
        Assert.assertNotNull(fromdb);
    }

    /**
     * Test of save method with update purpose, of NotificationTypeDAOImpl.
     */
    @Test
    public void testUpdate() {
        System.out.print("test NotificationType update");
        String altname = "altered name 2";
        String description = "description";
        nt2.setName(altname);
        nt2.setDescription(description);

        nDao.save(nt2);

        NotificationType fromdb = (NotificationTypeImpl) nDao.getSessionFactory().getCurrentSession().get(NotificationTypeImpl.class, nt2.getId());
        Assert.assertNotNull(fromdb);
        Assert.assertEquals(altname, fromdb.getName());
        Assert.assertEquals(description, fromdb.getDescription());
    }

    /**
     * Test of delete method, of NotificationTypeDAOImpl.
     */
    @Test
    public void testDelete() {
        System.out.print("test NotificationType delete");
    
        nDao.delete(nt3);

        NotificationType fromdb = (NotificationTypeImpl) nDao.getSessionFactory().getCurrentSession().get(NotificationTypeImpl.class, nt3.getId());
        Assert.assertNull(fromdb);
    }

    /**
     * Test of getAll method, of NotificationTypeDAOImpl.
     */
    @Test
    public void testGetAll() {
        System.out.print("test NotificationType delete");
        List all = new ArrayList();
        all.add(nt2);
        all.add(nt3);
        all.add(nt4);

        List allfromdb = nDao.getAll();
        Assert.assertNotNull(allfromdb);
        Assert.assertTrue(allfromdb.contains(nt2));
        Assert.assertTrue(allfromdb.contains(nt3));
        Assert.assertTrue(allfromdb.contains(nt4));

    }

    /**
     * Test of getById method, of NotificationTypeDAOImpl.
     */
    @Test
    public void testGetById() {
        System.out.print("test NotificationType getById");
        NotificationType fromdb = (NotificationTypeImpl) nDao.getById(nt3.getId());
        Assert.assertNotNull(fromdb);
        Assert.assertEquals(fromdb, nt3);
        Assert.assertEquals(fromdb.getId(), nt3.getId());
    }

    /**
     * Test of findByExample  method, of NotificationTypeDAOImpl.
     */
    @Test
    public void testFindByExample() {
        System.out.println("test NotificationType findByExample");
        List expResult = new ArrayList();
        expResult.add(nt3);
        nt6.setName(nt3.getName());
        nt6.setDescription(nt3.getDescription());
        List result = nDao.findByExample(nt6);
        Assert.assertNotNull(result);
        Assert.assertEquals(expResult, result);

    }
}
