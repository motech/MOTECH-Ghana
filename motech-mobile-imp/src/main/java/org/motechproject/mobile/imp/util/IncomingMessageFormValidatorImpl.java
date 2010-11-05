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
import org.motechproject.mobile.core.model.IncMessageFormParameterStatus;
import org.motechproject.mobile.core.model.IncMessageFormStatus;
import org.motechproject.mobile.core.model.IncomingMessageForm;
import org.motechproject.mobile.core.model.IncomingMessageFormParameter;
import org.motechproject.mobile.core.model.IncomingMessageFormParameterDefinition;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;

/**
 * Validate an IncominMessageForm
 *
 * @author Kofi A. Asamoah (yoofi@dreamoval.com)
 *  Date : Dec 6, 2009
 */
public class IncomingMessageFormValidatorImpl implements IncomingMessageFormValidator {

    private CoreManager coreManager;
    private Map<String, List<SubField>> subFields;
    private Map<String, List<SubField>> conditionalRequirements;
    private ConditionalRequirementValidator conditionalValidator;
    private LinkedHashMap<String, ValidatorGroup> paramValidators;
    private Map<String, List<CompositeRequirementValidator>> compositeRequirements;
    private static Logger logger = Logger.getLogger(IncomingMessageFormValidatorImpl.class);

    /**
     * 
     * @see IncomingMessageFormValidator.validate
     */
    public IncMessageFormStatus validate(IncomingMessageForm form, String requesterPhone) {
        ValidatorGroup group;
        IncMessageFormStatus status;
        form.setMessageFormStatus(IncMessageFormStatus.VALID);
        List<SubField> subs = null;
        List<SubField> conditionals = null;

        if (subFields != null && subFields.containsKey(form.getIncomingMsgFormDefinition().getFormCode().toUpperCase())) {
            subs = subFields.get(form.getIncomingMsgFormDefinition().getFormCode().toUpperCase());
        }

        if (conditionalRequirements != null && conditionalRequirements.containsKey(form.getIncomingMsgFormDefinition().getFormCode().toUpperCase())) {
            conditionals = conditionalRequirements.get(form.getIncomingMsgFormDefinition().getFormCode().toUpperCase());
        }

        Map<String, IncomingMessageFormParameter> params = form.getIncomingMsgFormParameters();

        if (subs != null) {
            for (SubField sub : subs) {
                if (params.containsKey(sub.getFieldName().toLowerCase())) {
                    if (params.containsKey(sub.getParentField().toLowerCase()) && (params.get(sub.getParentField().toLowerCase()).getValue().equalsIgnoreCase(sub.getReplaceOn()) || ("*".equalsIgnoreCase(sub.getReplaceOn()) && params.get(sub.getFieldName().toLowerCase()) != null && params.get(sub.getFieldName().toLowerCase()).getValue() != null && !"".equals(params.get(sub.getFieldName().toLowerCase()).getValue().trim()))) && params.containsKey(sub.getFieldName().toLowerCase())) {
                        params.get(sub.getParentField().toLowerCase()).setValue(params.get(sub.getFieldName().toLowerCase()).getValue());
                    } else if (sub.getReplaceOn() == null || sub.getReplaceOn().isEmpty()) {
                        if (!params.containsKey(sub.getParentField().toLowerCase())) {
                            IncomingMessageFormParameter param = coreManager.createIncomingMessageFormParameter();
                            param.setDateCreated(new Date());
                            param.setName(sub.getParentField().toLowerCase());
                            param.setIncomingMsgForm(form);
                            param.setMessageFormParamStatus(IncMessageFormParameterStatus.NEW);

                            params.put(sub.getParentField().toLowerCase(), param);
                        }
                        if (params.get(sub.getParentField().toLowerCase()).getValue() == null || params.get(sub.getParentField().toLowerCase()).getValue().isEmpty()) {
                            params.get(sub.getParentField().toLowerCase()).setValue(params.get(sub.getFieldName().toLowerCase()).getValue());
                        }
                    }
                }
            }
        }

        if(conditionals != null)
            conditionalValidator.validate(form, conditionals, coreManager);

        try {
            for (IncomingMessageFormParameterDefinition paramDef : form.getIncomingMsgFormDefinition().getIncomingMsgParamDefinitions()) {
                paramDef.getParamType();

                if (form.getIncomingMsgFormParameters().containsKey(paramDef.getName().toLowerCase())) {
                    form.getIncomingMsgFormParameters().get(paramDef.getName().toLowerCase()).setIncomingMsgFormParamDefinition(paramDef);
                    form.setLastModified(new Date());

                    if (paramDef.getParamType().endsWith("_ARRAY")) {
                        String type = paramDef.getParamType().substring(0, paramDef.getParamType().lastIndexOf("_"));
                        group = paramValidators.get(type);
                        if (group == null) {
                            throw new Exception("Validator [" + paramDef.getParamType().toUpperCase() + "] not found");
                        }
                        status = validateArray(form.getIncomingMsgFormParameters().get(paramDef.getName().toLowerCase()), group);
                    } else {
                        group = paramValidators.get(paramDef.getParamType().toUpperCase());
                        if (group == null) {
                            throw new Exception("Validator [" + paramDef.getParamType().toUpperCase() + "] not found");
                        }
                        status = validateSingle(form.getIncomingMsgFormParameters().get(paramDef.getName().toLowerCase()), group);
                    }

                    if (status != IncMessageFormStatus.VALID) {
                        form.setMessageFormStatus(status);
                    }

                    form.setLastModified(new Date());
                } else {
                    if (paramDef.isRequired()) {
                        IncomingMessageFormParameter param = coreManager.createIncomingMessageFormParameter();
                        param.setIncomingMsgFormParamDefinition(paramDef);
                        param.setName(paramDef.getName());
                        param.setDateCreated(new Date());
                        param.setIncomingMsgForm(form);
                        param.setValue(null);

                        param.setErrCode(0);
                        param.setErrText("missing");
                        param.setMessageFormParamStatus(IncMessageFormParameterStatus.INVALID);
                        form.setMessageFormStatus(IncMessageFormStatus.INVALID);

                        form.setLastModified(new Date());
                        form.getIncomingMsgFormParameters().put(paramDef.getName().toLowerCase(), param);
                    }
                }
            }
        } catch (Exception ex) {
            logger.fatal("Error validating form", ex);
            form.setMessageFormStatus(IncMessageFormStatus.ERROR);
        }

        if (compositeRequirements.containsKey(form.getIncomingMsgFormDefinition().getFormCode().toUpperCase())) {
            List<CompositeRequirementValidator> requirements = compositeRequirements.get(form.getIncomingMsgFormDefinition().getFormCode().toUpperCase());
            for (CompositeRequirementValidator validator : requirements) {
                if (!validator.validate(form, coreManager)) {
                    form.setMessageFormStatus(IncMessageFormStatus.INVALID);
                }
            }
        }

        return form.getMessageFormStatus();
    }

    private IncMessageFormStatus validateSingle(IncomingMessageFormParameter param, ValidatorGroup group) {
        Map<String, IncomingMessageFormParameterValidator> validators = new LinkedHashMap<String, IncomingMessageFormParameterValidator>();

        if (group.getParent() != null && !group.getParent().isEmpty()) {
            ValidatorGroup parent = paramValidators.get(group.getParent());
            validators.putAll(parent.getValidators());
        }
        for (Entry<String, IncomingMessageFormParameterValidator> validator : group.getValidators().entrySet()) {
            validators.put(validator.getKey(), validator.getValue());
        }

        for (Entry<String, IncomingMessageFormParameterValidator> entry : validators.entrySet()) {
            IncomingMessageFormParameterValidator v = entry.getValue();
            boolean valid = v.validate(param);
            if (!valid) {
                return IncMessageFormStatus.INVALID;
            }
        }

        return IncMessageFormStatus.VALID;
    }

    private IncMessageFormStatus validateArray(IncomingMessageFormParameter param, ValidatorGroup group) {
        String value = param.getValue();
        String[] elements = param.getValue().split(" ");
        IncMessageFormStatus status = IncMessageFormStatus.INVALID;

        for (int i = 0; i < elements.length; i++) {
            param.setValue(elements[i]);
            status = validateSingle(param, group);

            if (status != IncMessageFormStatus.VALID) {
                String error = param.getErrText() + " (item " + (i + 1) + ")";
                param.setErrText(error);
                break;
            }
        }
        param.setValue(value);
        return status;
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
     * @param paramValidators the paramValidators to set
     */
    public void setParamValidators(LinkedHashMap<String, ValidatorGroup> paramValidators) {
        this.paramValidators = paramValidators;
    }

    /**
     * @param subFields the subFields to set
     */
    public void setSubFields(Map<String, List<SubField>> subFields) {
        this.subFields = subFields;
    }

    /**
     * @param compositeRequirements the compositeRequirements to set
     */
    public void setCompositeRequirements(Map<String, List<CompositeRequirementValidator>> compositeRequirements) {
        this.compositeRequirements = compositeRequirements;
    }

    /**
     * @return the conditionalRequirements
     */
    public Map<String, List<SubField>> getConditionalRequirements() {
        return conditionalRequirements;
    }

    /**
     * @param conditionalRequirements the conditionalRequirements to set
     */
    public void setConditionalRequirements(Map<String, List<SubField>> conditionalRequirements) {
        this.conditionalRequirements = conditionalRequirements;
    }

    /**
     * @return the conditionalValidator
     */
    public ConditionalRequirementValidator getConditionalValidator() {
        return conditionalValidator;
    }

    /**
     * @param conditionalValidator the conditionalValidator to set
     */
    public void setConditionalValidator(ConditionalRequirementValidator conditionalValidator) {
        this.conditionalValidator = conditionalValidator;
    }
}