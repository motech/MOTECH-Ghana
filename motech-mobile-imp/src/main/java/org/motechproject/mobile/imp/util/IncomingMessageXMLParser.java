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
import org.motechproject.mobile.imp.util.exception.MotechParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import org.jdom.JDOMException;

/**
 *
 * @author Henry Sampson (henry@dreamoval.com)
 * Date Created: Feb 14, 2010
 */
public interface IncomingMessageXMLParser {

    /**
     * @return the coreManager
     */
    public CoreManager getCoreManager();

    /**
     * @return the delimiter
     */
    public String getDelimiter();

    /**
     * @return the messageParser
     */
    public IncomingMessageParser getMessageParser();

    /**
     * @return the separator
     */
    public String getSeparator();

    /**
     * @return the typeTagName
     */
    public String getFormTypeTagName();

    /**
     * Parses the List of XMLs to Motech incoming SMSes
     *
     * @param xmls the List of XMLs to be parsed
     * @return the list of Motech incoming SMSes representing the parsed XMLs
     * @throws org.jdom.JDOMException thrown if an error occurs while parsing an XML Document
     * @throws java.io.IOException thrown if an error occurs reading the file or stream
     * @throws org.motechproject.mobile.imp.util.exception.MotechParseException
     */
    public ArrayList<String> parseXML(ArrayList<String> xml) throws JDOMException, IOException, MotechParseException;

    /**
     * @param coreManager the coreManager to set
     */
    public void setCoreManager(CoreManager coreManager);

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter);

    /**
     * @param messageParser the messageParser to set
     */
    public void setMessageParser(IncomingMessageParser messageParser);

    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator);

    /**
     * @param typeTagName the typeTagName to set
     */
    public void setFormTypeTagName(String typeTagName);

    /**
     * @return the formTypeLookup
     */
    Map<String, String> getFormTypeLookup();

    /**
     * @return the xmlUtil
     */
    XMLUtil getXmlUtil();

    /**
     * @param formTypeLookup the formTypeLookup to set
     */
    void setFormTypeLookup(Map<String, String> formTypeLookup);

    /**
     * @param xmlUtil the xmlUtil to set
     */
    void setXmlUtil(XMLUtil xmlUtil);

    /**
     * Returns a String similar to SMS forms represnting the xml argument passed
     *
     * @param xml the XML to parse
     * @return a string representing a name/value pair or xml-element/value[CDATA]
     * @throws org.jdom.JDOMException thrown if an error occurs while parsing the XML a Document
     * @throws java.io.IOException thrown if an error occurs `reading the file or stream
     */
    String toSMSMessage(String xml) throws JDOMException, IOException, MotechParseException;

    /**
     * @return the formNameTagName
     */
    String getFormNameTagName();

    /**
     * @param formNameTagName the formNameTagName to set
     */
    void setFormNameTagName(String formNameTagName);

    /**
     * @param oxdDateFormat the oxdDateFormat to set
     */
    void setOxdDateFormat(String oxdDateFormat);

    /**
     * @param oxdDateRegex the oxdDateRegex to set
     */
    void setOxdDateRegex(String oxdDateRegex);

    /**
     * @param impDateFormat the impDateFormat to set
     */
    void setImpDateFormat(String impDateFormat);
}
