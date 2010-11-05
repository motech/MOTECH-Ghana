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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.motechproject.mobile.imp.util;

import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.imp.manager.IMPManager;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

/**
 *
 * @author administrator
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/imp-test-config.xml"})
public class IncomingMessageXMLParserImplTest {
    CoreManager mockCore;
    IMPManager mockIMP;
    IncomingMessageXMLParser xmlParser;

    @Autowired
    IncomingMessageParser imParser;

    public IncomingMessageXMLParserImplTest() {
    }

    @Before
    public void setUp() throws Exception {
        mockIMP = createMock(IMPManager.class);

        expect(
                mockIMP.createIncomingMessageXMLParser()
                ).andReturn(new IncomingMessageXMLParserImpl());
        replay(mockIMP);
        xmlParser = mockIMP.createIncomingMessageXMLParser();
        xmlParser.setDelimiter("\n");
        xmlParser.setSeparator("=");
        xmlParser.setFormTypeTagName("formType");
        xmlParser.setXmlUtil(new XMLUtil());
        xmlParser.setFormNameTagName("formName");
        Map<String, String> lookup = new HashMap<String, String>();
        lookup.put("data", "Type");
        xmlParser.setFormTypeLookup(lookup);
        mockCore = createMock(CoreManager.class);
        xmlParser.setCoreManager(mockCore);
        xmlParser.setOxdDateRegex("(19|20)\\d\\d\\-(0[1-9]|1[012])\\-(0[1-9]|[12][0-9]|3[01])");
        xmlParser.setImpDateFormat("dd/MM/yyyy");
        xmlParser.setOxdDateFormat("yyyy-MM-dd");
        verify(mockIMP);
    }

    
    @Test
    public void testToSMSMessage() throws Exception{
        System.out.println("toSMSMessage");

        String xml = "<?xml version='1.0' encoding='UTF-8' ?><patientreg description-template=\"${/patientreg/lastname}$ in ${/patientreg/continent}$\" id=\"1\" name=\"Patient Registration\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><patientid>123</patientid><title>mrs</title><firstname>Test</firstname><lastname>Test</lastname><sex>female</sex><birthdate>1990-06-03</birthdate><weight>40</weight><height>20</height><pregnant>false</pregnant><continent>africa</continent><country>uganda</country><district>mbale</district><formType>data</formType><formName>patientreg</formName></patientreg>";


        String expResult = "Type=patientreg\npatientid=123\ntitle=mrs\nfirstname=Test\nlastname=Test\nsex=female\nbirthdate=03/06/1990\nweight=40\nheight=20\npregnant=false\ncontinent=africa\ncountry=uganda\ndistrict=mbale";
        String result = ((IncomingMessageXMLParserImpl)xmlParser).toSMSMessage(xml);
        
        assertEquals(result, expResult);

    }

}