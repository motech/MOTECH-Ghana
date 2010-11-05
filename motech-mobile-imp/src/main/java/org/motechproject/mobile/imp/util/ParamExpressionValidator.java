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

import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author user
 */
public class ParamExpressionValidator implements IncomingMessageFormParameterValidator {

    private String expression;
    private String dateFormat;
    private String defaultDateFormat;
    private DateFormatRegexMap dateFormateExpMap;

    private static Logger logger = Logger.getLogger(ParamExpressionValidator.class);

    public boolean validate(IncomingMessageFormParameter param) {
        if(param.getMessageFormParamStatus() == IncMessageFormParameterStatus.INVALID)
            return false;

        String paramType = param.getIncomingMsgFormParamDefinition().getParamType().toUpperCase();

        if(paramType.indexOf("TIME") >= 0){
            try{
                SimpleDateFormat dFormat = new SimpleDateFormat(dateFormat);
                dFormat.setLenient(true);
                Date val = dFormat.parse(param.getValue());

                dFormat.applyPattern(defaultDateFormat);
                param.setValue(dFormat.format(val));
                param.setMessageFormParamStatus(IncMessageFormParameterStatus.VALID);
            }catch (ParseException ex) {
                logger.error("Invalid datetime format - " + param.getValue(), ex);

                param.setErrCode(1);
                param.setErrText("wrong format");
                param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
            }
        }else if (!Pattern.matches(expression, param.getValue().trim())) {
            param.setErrCode(1);
            param.setErrText("wrong format");
            param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
        } else {
            param.setMessageFormParamStatus(IncMessageFormParameterStatus.VALID);
            if (paramType.indexOf("DATE") >= 0) {
                try {
                    String dateInputFormat = "";
                    for (String regex : dateFormateExpMap.getDateFormatRegexMap().keySet()){
                        boolean match = Pattern.matches(regex, param.getValue());
                        if (match){
                            dateInputFormat = dateFormateExpMap.getDateFormatRegexMap().get(regex);
                            break;
                        }
                    }

                    SimpleDateFormat dFormat = new SimpleDateFormat(dateInputFormat);
                    dFormat.setLenient(true);
                    Date val = dFormat.parse(param.getValue());

                    if (paramType.equalsIgnoreCase("DATE") && val.after(new Date())) {
                        param.setErrCode(1);
                        param.setErrText("invalid date");
                        param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
                    }
                    else if (paramType.equalsIgnoreCase("DELIVERYDATE")) {
                        long timeDiff =  val.getTime() - new Date().getTime();
                        long dayDiff = timeDiff / (1000 * 60 * 60 * 24);

                        if(val.before(new Date()) || (dayDiff > 280)){
                            param.setErrCode(1);
                            param.setErrText("invalid date");
                            param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
                        }
                    }
                    
                    dFormat.applyPattern(defaultDateFormat);
                    param.setValue(dFormat.format(val));
                } catch (ParseException ex) {
                    logger.error("Invalid date format - " + param.getValue(), ex);

                    param.setErrCode(1);
                    param.setErrText("wrong format");
                    param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
                }
            }
        }

        param.setLastModified(new Date());
        return param.getMessageFormParamStatus().equals(IncMessageFormParameterStatus.VALID);
    }

    /**
     * @param expression the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * @return the dateFormateExpMap
     */
    public DateFormatRegexMap getDateFormateExpMap() {
        return dateFormateExpMap;
    }

    /**
     * @param dateFormateExpMap the dateFormateExpMap to set
     */
    public void setDateFormateExpMap(DateFormatRegexMap dateFormateExpMap) {
        this.dateFormateExpMap = dateFormateExpMap;
    }

    /**
     * @return the defaultDateFormat
     */
    public String getDefaultDateFormat() {
        return defaultDateFormat;
    }

    /**
     * @param defaultDateFormat the defaultDateFormat to set
     */
    public void setDefaultDateFormat(String defaultDateFormat) {
        this.defaultDateFormat = defaultDateFormat;
    }
}
