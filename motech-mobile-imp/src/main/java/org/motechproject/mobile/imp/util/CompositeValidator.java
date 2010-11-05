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

import java.util.List;
import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;

/**
 *
 * @author user
 */
public class CompositeValidator{
    private List<String> fields;
    private int requiredMatches;
    private IncomingMessageFormParameterValidator validator;

    public boolean validate(IncomingMessageForm form)
    {
        boolean valid = false;
        int matchCount = 0;
        int errorCode = 0;
        String errorMsg = "";

        for(String field : fields)
        {
            if(form.getIncomingMsgFormParameters().containsKey(field)){
                IncomingMessageFormParameter param = form.getIncomingMsgFormParameters().get(field.toLowerCase());
                if(validator.validate(param))
                    matchCount++;
                else{
                    errorCode = param.getErrCode();
                    errorMsg = param.getErrText();
                }
            }
        }

        if(matchCount >= requiredMatches)
            valid = true;
        else
        {
            for(String field : fields)
            {
                if(form.getIncomingMsgFormParameters().containsKey(field)){
                    IncomingMessageFormParameter param = form.getIncomingMsgFormParameters().get(field);
                    param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
                    param.setErrCode(errorCode);
                    param.setErrText(errorMsg+"(at least "+requiredMatches+" valid values required)");
                }
            }
        }
        return valid;
    }

    /**
     * @param fields the fields to set
     */
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    /**
     * @param requiredMatches the requiredMatches to set
     */
    public void setRequiredMatches(int requiredMatches) {
        this.requiredMatches = requiredMatches;
    }

    /**
     * @param validator the validator to set
     */
    public void setValidator(IncomingMessageFormParameterValidator validator) {
        this.validator = validator;
    }
}
