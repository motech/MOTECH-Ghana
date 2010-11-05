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

import java.text.ParseException;
import org.motechproject.mobile.core.manager.CoreManager;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.imp.util.exception.MotechParseException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Utility class for converting xForms into SMS Forms
 * @author Henry Sampson (henry@dreamoval.com)
 * Date Created: Feb 14, 2010
 */
public class IncomingMessageXMLParserImpl implements IncomingMessageXMLParser {

    private static Logger log = Logger.getLogger(IncomingMessageXMLParserImpl.class);
    private CoreManager coreManager;
    private IncomingMessageParser messageParser;
    private String separator;
    private String delimiter;
    private String formTypeTagName;
    private String formNameTagName;
    private String oxdDateFormat;
    private String oxdDateRegex;
    private String impDateFormat;
    private Map<String, String> formTypeLookup;
    private XMLUtil xmlUtil;

    /**
     * Parses the List of XMLs to Motech incoming SMSes
     * 
     * @param xmls the List of XMLs to be parsed
     * @return a {@link org.motechproject.mobile.core.model.IncomingMessageForm} representing the parsed XML
     * @throws org.jdom.JDOMException thrown if an error occurs while parsing the XML a Document
     * @throws java.io.IOException thrown if an error occurs reading the file or stream
     * @throws org.motechproject.mobile.imp.util.exception.MotechParseException 
     */
    public ArrayList<String> parseXML(ArrayList<String> xmls) throws JDOMException, IOException, MotechParseException {
        ArrayList<String> result = null;

        if (xmls != null) {
            result = new ArrayList<String>();
            for (String xml : xmls) {
                log.debug("parseXML(" + xml + ")");
                String message = toSMSMessage(xml);
                log.debug("SMS Message Createed ===> " + message);
                result.add(message);
            }
        }

        return result;
    }

    /**
     * Returns a String similar to SMS forms represnting the xml argument passed
     * 
     * @param xml the XML to parse
     * @return a string representing a name/value pair or xml-element/value[CDATA]
     * @throws org.jdom.JDOMException thrown if an error occurs while parsing the XML a Document
     * @throws java.io.IOException thrown if an error occurs `reading the file or stream
     */
    public String toSMSMessage(String xml) throws JDOMException, IOException, MotechParseException {
        String result = "";

        InputStream in = new ByteArrayInputStream(xml.getBytes());
        SAXBuilder saxb = new SAXBuilder();
        Document doc = null;

        doc = saxb.build(in);

        Element root = doc.getRootElement();
        Element formTypeElement = getXmlUtil().getElement(doc, formTypeTagName);
        String formType = formTypeElement == null ? null : formTypeElement.getText();

        if (formType == null || "".equals(formType.trim())) {
            String error = "Empty or No form type defined in xml with root element: " + root.getName() + " and id: " + root.getAttributeValue("id");
            log.error(error);
            throw new MotechParseException(error);
        }

        String formTypeFieldName = formTypeLookup.get(formType);

        if (formTypeFieldName == null || formTypeFieldName.trim().equals("")) {
            String error = "Could not find a valid (non-null-or-white-space) form type field name associated with form type: " + formType;
            log.error(error);
            throw new MotechParseException(error);
        }

        Element formNameElement = xmlUtil.getElement(doc, formNameTagName);

        if (formNameElement == null) {
            throw new MotechParseException("No element (representing the Form Name) found by name " + formNameTagName);
        }

        result += formTypeFieldName + getSeparator() + formNameElement.getText();

        List children = root.getChildren();
        for (Object o : children) {
            Element child = (Element) o;
            if (!(child.getName().equalsIgnoreCase(formTypeTagName) || child.getName().equalsIgnoreCase(formNameTagName))) {
                result += getDelimiter() + child.getName() + getSeparator();
                String text = child.getText();

                Pattern p = Pattern.compile(oxdDateRegex);
                Matcher m = p.matcher(child.getText());

                if (m.matches()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat(oxdDateFormat);
                        Date date = sdf.parse(child.getText());
                        sdf = new SimpleDateFormat(impDateFormat);
                        text = sdf.format(date);
                    } catch (ParseException ex) {
                        log.error("Error changing date format", ex);
                    }
                }

                result += text;
            }
        }

        return result;
    }

    private Map<String, IncomingMessageFormParameter> toIncomingMessageParameters(Map<String, String> params) {
        Map<String, IncomingMessageFormParameter> result = null;


        return result;
    }

    /**
     * @return the coreManager
     */
    public CoreManager getCoreManager() {
        return coreManager;
    }

    /**
     * @param coreManager the coreManager to set
     */
    public void setCoreManager(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    /**
     * @return the messageParser
     */
    public IncomingMessageParser getMessageParser() {
        return messageParser;
    }

    /**
     * @param messageParser the messageParser to set
     */
    public void setMessageParser(IncomingMessageParser messageParser) {
        this.messageParser = messageParser;
    }

    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * @return the delimiter
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * @return the formTypeTagName
     */
    public String getFormTypeTagName() {
        return formTypeTagName;
    }

    /**
     * @param formTypeTagName the formTypeTagName to set
     */
    public void setFormTypeTagName(String formTypeTagName) {
        this.formTypeTagName = formTypeTagName;
    }

    /**
     * @return the formTypeLookup
     */
    public Map<String, String> getFormTypeLookup() {
        return formTypeLookup;
    }

    /**
     * @param formTypeLookup the formTypeLookup to set
     */
    public void setFormTypeLookup(Map<String, String> formTypeLookup) {
        this.formTypeLookup = formTypeLookup;
    }

    /**
     * @return the xmlUtil
     */
    public XMLUtil getXmlUtil() {
        return xmlUtil;
    }

    /**
     * @param xmlUtil the xmlUtil to set
     */
    public void setXmlUtil(XMLUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
    }

    /**
     * @return the formNameTagName
     */
    public String getFormNameTagName() {
        return formNameTagName;
    }

    /**
     * @param formNameTagName the formNameTagName to set
     */
    public void setFormNameTagName(String formNameTagName) {
        this.formNameTagName = formNameTagName;
    }

    /**
     * @param oxdDateFormat the oxdDateFormat to set
     */
    public void setOxdDateFormat(String oxdDateFormat) {
        this.oxdDateFormat = oxdDateFormat;
    }

    /**
     * @param oxdDateRegex the oxdDateRegex to set
     */
    public void setOxdDateRegex(String oxdDateRegex) {
        this.oxdDateRegex = oxdDateRegex;
    }

    /**
     * @param impDateFormat the impDateFormat to set
     */
    public void setImpDateFormat(String impDateFormat) {
        this.impDateFormat = impDateFormat;
    }
}
