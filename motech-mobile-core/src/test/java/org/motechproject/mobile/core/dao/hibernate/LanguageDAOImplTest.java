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
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.Language;
import org.motechproject.mobile.core.model.LanguageImpl;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
public class LanguageDAOImplTest {

    public LanguageDAOImplTest() {
    }
    LanguageDAO lDao;
    @Autowired
    Language l1;
    @Autowired
    Language l2;
    @Autowired
    Language l3;
    @Autowired
    Language l4;
    @Autowired
    Language l5;
    @Autowired
    CoreManager coreManager;
    String code;

    @Before
    public void setUp() {

        lDao = coreManager.createLanguageDAO();
        l1.setId(70000000001l);
        l1.setCode("aul887");
        l1.setName("day notifier");
        l1.setDescription("some description");


        code = "de";


        l2.setId(70000000002l);
        l2.setCode(code);
        l2.setName("german");
        l2.setDescription("description for l2");

        l3.setId(70000000003l);
        l3.setCode("fr");
        l3.setName("francais");
        l3.setDescription("description for l3");

        l4.setId(70000000004l);
        l4.setCode("es");
        l4.setName("espagnol");
        l4.setDescription("description for l4");

        lDao.save(l2);
        lDao.save(l3);
        lDao.save(l4);

    }

    @After
    public void tearDown() {

        lDao.delete(l1);
        lDao.delete(l2);
        lDao.delete(l3);
        lDao.delete(l4);

    }

    /**
     * Test of save method, of class LanguageDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("Test save Language object");

        lDao.save(l1);


        Language fromdb = (LanguageImpl) lDao.getSessionFactory().getCurrentSession().get(LanguageImpl.class, l1.getId());
        Assert.assertNotNull(fromdb);
        Assert.assertSame(l1, fromdb);
        Assert.assertEquals(l1.getId(), fromdb.getId());
        Assert.assertEquals(l1.getCode(), fromdb.getCode());
        Assert.assertEquals(l1.getName(), fromdb.getName());
        Assert.assertEquals(l1.getDescription(), fromdb.getDescription());
        System.out.println(fromdb.toString());
    }

    /**
     * Test of getByCode method, of class LanguageDAOImpl.
     */
    @Test
    public void testGeByCode() {
        System.out.print("test getIdByCode");
        Language expResult = l2;
        Language Result = lDao.getByCode(code);
        Assert.assertNotNull(Result);
        Assert.assertEquals(expResult, Result);
    }

    /**
     * Test of getAll method, of class LanguageDAOImpl.
     */
    @Test
    public void testGetAll() {
        System.out.print("test Language getAll");
        List all = new ArrayList();

        all.add(l1);
        all.add(l2);
        all.add(l3);
        all.add(l4);

        List result = lDao.getAll();
        Assert.assertNotNull(result);
        //due to the import.sql this test cannot include assertequal for the list nor the assertequals for the sizes of the result and all lists
        Assert.assertTrue(all.contains(l1));
        Assert.assertTrue(all.contains(l2));
        Assert.assertTrue(all.contains(l3));
        Assert.assertTrue(all.contains(l4));

    }

    /**
     * Test of delete method, of class LanguageDAOImpl.
     */
    @Test
    public void testDelete() {
        System.out.print("test Language Delete");

        lDao.delete(l4);

        Language fromdb = (LanguageImpl) lDao.getSessionFactory().getCurrentSession().get(LanguageImpl.class, l4.getId());
        Assert.assertNull(fromdb);

    }

    /**
     * Test of save method with update purpose, of class LanguageDAOImpl.
     */
    @Test
    public void testUpdate() {

        System.out.print("test Language Update");
        String code = "nl";
        String name = "Netherland";
        l4.setCode(code);
        l4.setName(name);

        lDao.save(l4);

        Language fromdb = (LanguageImpl) lDao.getSessionFactory().getCurrentSession().get(LanguageImpl.class, l4.getId());
        Assert.assertNotNull(l4);
        Assert.assertEquals(code, fromdb.getCode());
        Assert.assertEquals(name, fromdb.getName());

    }

    /**
     * Test of getByCode method with null fecthing, of class LanguageDAOImpl.
     */
    @Test
    public void testGetByUnexistantCode() {
        System.out.print("test Language getByCode with unexistant code");
        String code = "dd";
        Language result = lDao.getByCode(code);
        Assert.assertNull(result);
    }

    /**
     * Test of findByExample method, of class LanguageDAOImpl.
     */
    @Test
    public void testGetByExample() {
        System.out.print("test Language getByTemplate");
        List expResult = new ArrayList();
        expResult.add(l3);

        l5.setCode(l3.getCode());
        List result = lDao.findByExample(l5);
        Assert.assertNotNull(result);
        Assert.assertEquals(expResult, result);
    }
}
