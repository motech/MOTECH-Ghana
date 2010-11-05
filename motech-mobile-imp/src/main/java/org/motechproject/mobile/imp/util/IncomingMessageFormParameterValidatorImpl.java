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
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validates a form parameter
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 *  Date : Dec 6, 2009
 */
public class IncomingMessageFormParameterValidatorImpl implements IncomingMessageFormParameterValidator {

    private Map<String, String> paramTypeMap;

    /**
     * @see IncomingMessageFormParameterValidator.validate
     */
    public boolean validate(IncomingMessageFormParameter param) {
        if (!param.getMessageFormParamStatus().equals(IncMessageFormParameterStatus.NEW)) {
            return param.getMessageFormParamStatus().equals(IncMessageFormParameterStatus.VALID);
        }

        String paramRegex = getParamTypeMap().get(param.getIncomingMsgFormParamDefinition().getParamType());

        if (param.getIncomingMsgFormParamDefinition().getParamType().toUpperCase().equals("DATE")) {
            try {
                new SimpleDateFormat(paramRegex).parse(param.getValue());
                param.setMessageFormParamStatus(IncMessageFormParameterStatus.VALID);
            } catch (ParseException ex) {
                param.setErrCode(1);
                param.setErrText("wrong format");
                param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
            }
        } else if (!Pattern.matches(paramRegex, param.getValue().trim())) {
            param.setErrCode(1);
            param.setErrText("wrong format");
            param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
        } else if (param.getValue().trim().length() > param.getIncomingMsgFormParamDefinition().getLength()) {
            param.setErrCode(2);
            param.setErrText("too long");
            param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
        } else {
            if(param.getIncomingMsgFormParamDefinition().getParamType().toUpperCase().equals("BOOLEAN"))
                param.setValue(param.getValue().toLowerCase().equals("y") ? "true" : "false");
            else if(param.getIncomingMsgFormParamDefinition().getParamType().toUpperCase().equals("GENDER"))
                param.setValue(param.getValue().toLowerCase().equals("m") ? "MALE" : "FEMALE");

            param.setMessageFormParamStatus(IncMessageFormParameterStatus.VALID);
        }

        param.setLastModified(new Date());
        return param.getMessageFormParamStatus().equals(IncMessageFormParameterStatus.VALID);
    }

    /**
     * @return the paramTypeMap
     */
    public Map<String, String> getParamTypeMap() {
        return paramTypeMap;
    }

    /**
     * @param paramTypeMap the paramTypeMap to set
     */
    public void setParamTypeMap(Map<String, String> paramTypeMap) {
        this.paramTypeMap = paramTypeMap;
    }
}
