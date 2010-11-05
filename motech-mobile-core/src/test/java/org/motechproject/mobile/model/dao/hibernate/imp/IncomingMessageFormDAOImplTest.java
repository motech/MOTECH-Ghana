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
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormDefinition;
import org.motechproject.mobile.core.model.IncomingMessageFormImpl;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormDefinitionDAO;
import org.motechproject.mobile.model.dao.imp.IncomingMessageFormParameterDAO;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class IncomingMessageFormDAOImplTest {

    @Autowired
    CoreManager coreManager;
    IncomingMessageFormDAO imfDAO;
    IncomingMessageFormDefinitionDAO imfdDAO;
    @Autowired
    private IncomingMessageFormDefinition imfd1;
    @Autowired
    private IncomingMessageFormDefinition imfd2;
    @Autowired
    private IncomingMessageFormDefinition imfd3;
    @Autowired
    private IncomingMessageFormDefinition imfd4;
    @Autowired
    private IncomingMessageFormDefinition imfd5;
    @Autowired
    private IncomingMessageForm imf1;
    @Autowired
    private IncomingMessageForm imf2;
    @Autowired
    private IncomingMessageForm imf3;
    @Autowired
    private IncomingMessageForm imf4;
    @Autowired
    private IncomingMessageForm imf5;
    @Autowired
    private IncomingMessageForm exampleForm;
    @Autowired
    private IncomingMessageFormParameter imfP1;
    @Autowired
    private IncomingMessageFormParameter imfP2;
    @Autowired
    private IncomingMessageFormParameter imfP3;
    @Autowired
    private IncomingMessageFormParameter imfP4;
    @Autowired
    private IncomingMessageFormParameter imfP5;

    public IncomingMessageFormDAOImplTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {

        imfDAO = coreManager.createIncomingMessageFormDAO();
        imfdDAO = coreManager.createIncomingMessageFormDefinitionDAO();

        imf1.setId(12000000001l);
        imf1.setDateCreated(new Date());
        imf1.setMessageFormStatus(IncMessageFormStatus.NEW);

        imf2.setId(12000000002l);
        imf2.setDateCreated(new Date());
        imf2.setMessageFormStatus(IncMessageFormStatus.NEW);

        imf3.setId(12000000003l);
        imf3.setDateCreated(new Date());
        imf3.setMessageFormStatus(IncMessageFormStatus.NEW);

        imf4.setId(12000000004l);
        imf4.setDateCreated(new Date());
        imf4.setMessageFormStatus(IncMessageFormStatus.NEW);

        imf5.setId(12000000005l);
        imf5.setDateCreated(new Date());
        imf5.setMessageFormStatus(IncMessageFormStatus.INVALID);

        imfd1.setId(12000000006l);
        imfd1.setFormCode("test_formdef");
        imfd1.setDateCreated(new Date());

        imfd2.setId(12000000007l);
        imfd2.setFormCode("test_formdef 2");
        imfd2.setDateCreated(new Date());

        imfd3.setId(12000000008l);
        imfd3.setFormCode("test_formdef 3");
        imfd3.setDateCreated(new Date());

        imfd4.setId(12000000009l);
        imfd4.setFormCode("test_formdef 4");
        imfd4.setDateCreated(new Date());

        imfd5.setId(12000000010l);
        imfd5.setFormCode("test_formdef 5");
        imfd5.setDateCreated(new Date());

        imfP1.setId(12000000011l);
        imfP1.setDateCreated(new Date());
        imfP1.setErrCode(12);
        imfP1.setErrText("err_code1");
        imfP1.setName("param1");
        imfP1.setValue("value1");

        imfP2.setId(12000000012l);
        imfP2.setDateCreated(new Date());
        imfP2.setErrCode(159);
        imfP2.setErrText("err_code159");
        imfP2.setName("param2");
        imfP2.setValue("value2");

        imfP3.setId(12000000013l);
        imfP3.setDateCreated(new Date());
        imfP3.setErrCode(125);
        imfP3.setErrText("err_code125");
        imfP3.setName("param3");
        imfP3.setValue("value3");

        imfP4.setId(12000000014l);
        imfP4.setDateCreated(new Date());
        imfP4.setErrCode(200);
        imfP4.setErrText("err_code200");
        imfP4.setName("param4");
        imfP4.setValue("value4");

        imfP5.setId(12000000015l);
        imfP5.setDateCreated(new Date());
        imfP5.setErrCode(201);
        imfP5.setErrText("err_code201");
        imfP5.setName("param5");
        imfP5.setValue("value5");

        imf3.addIncomingMsgFormParam(imfP3.getName(), imfP3);
        imfd3.addIncomingMessageForm(imf3);

        imf4.addIncomingMsgFormParam(imfP4.getName(), imfP4);
        imfd4.addIncomingMessageForm(imf4);

        imf5.addIncomingMsgFormParam(imfP5.getName(), imfP5);
        imfd5.addIncomingMessageForm(imf5);


        imfdDAO.save(imfd1);
        imfdDAO.save(imfd2);
        imfdDAO.save(imfd3);
        imfdDAO.save(imfd4);
        imfdDAO.save(imfd5);
        imfDAO.save(imf3);
        imfDAO.save(imf4);
        imfDAO.save(imf5);



    }

    @After
    public void tearDown() {

        imfDAO.delete(imf1);
        imfDAO.delete(imf2);
        imfDAO.delete(imf4);
        imfDAO.delete(imf5);
        imfdDAO.delete(imfd1);
        imfdDAO.delete(imfd2);
        imfdDAO.delete(imfd3);
        imfdDAO.delete(imfd4);
        imfdDAO.delete(imfd5);
        imfDAO.delete(imf3);


    }

    /**
     * Test of save method, of class IncomingMessageFormDAOImpl.
     */
    @Test
    public void testSave() {
        System.out.println("IncomingMessageFormDAOImpl save");
  
        imfd1.addIncomingMessageForm(imf1);
        imfDAO.save(imf1);



        IncomingMessageForm fromdb = (IncomingMessageForm) imfDAO.getSessionFactory().getCurrentSession().get(IncomingMessageFormImpl.class, imf1.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(fromdb, imf1);
        Assert.assertEquals(fromdb.getId(), imf1.getId());
        System.out.println("the form date created: " + fromdb.getDateCreated());
        System.out.println(fromdb.toString());
    }

    /**
     * Test of save method with form parameters, of class IncomingMessageFormDAOImpl.
     */
    @Test
    public void testSaveWithParam() {
        System.out.println("IncomingMessageFormDAO save with param");

        Map<String, IncomingMessageFormParameter> expMap = new HashMap<String, IncomingMessageFormParameter>();
        expMap.put(imfP2.getName(), imfP2);

        imf2.addIncomingMsgFormParam(imfP2.getName(), imfP2);
        imfd2.addIncomingMessageForm(imf2);
        imfDAO.save(imf2);

        IncomingMessageForm fromdb = (IncomingMessageForm) imfDAO.getSessionFactory().getCurrentSession().get(IncomingMessageFormImpl.class, imf2.getId());

        Assert.assertNotNull(fromdb);
        Assert.assertEquals(fromdb, imf2);
        Assert.assertEquals(fromdb.getId(), imf2.getId());
        Assert.assertEquals(fromdb.getId(), imf2.getId());
        Assert.assertEquals(expMap.size(), imf2.getIncomingMsgFormParameters().size());
        Assert.assertEquals(expMap, imf2.getIncomingMsgFormParameters());
        Assert.assertEquals(expMap.get(imfP2.getName()), imf2.getIncomingMsgFormParameters().get(imfP2.getName()));
        System.out.println("the form date created: " + fromdb.getDateCreated());
        System.out.println(fromdb.toString());

    }

    /**
     * Test of findByExample method, of class IncomingMessageFormDAOImpl.
     */
    @Test
    public void testFindByExample() {
        System.out.println("IncomingMessageFormDAO findByExample");

        List expResult = new ArrayList();
        expResult.add(imf3);
        expResult.add(imf4);
        exampleForm.setMessageFormStatus(IncMessageFormStatus.NEW);
    
        List result = imfDAO.findByExample(exampleForm);

        Assert.assertEquals(expResult, result);
        Assert.assertEquals(expResult.size(), result.size());



    }


}
