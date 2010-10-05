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

package org.motechproject.server.omod.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebMessage;
import org.motechproject.ws.NameValuePair;
import org.motechproject.ws.mobile.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

@Controller
@RequestMapping("/module/motechmodule/message-patient")
@SessionAttributes("message")
public class MessagePatientController {

	private static Log log = LogFactory.getLog(MessagePatientController.class);

	@Autowired
	@Qualifier("mobileClient")
	private MessageService messageService;

	@Autowired
	private ContextService contextService;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		String datePattern = "yyyy-MM-dd HH:mm:ss";
		SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);
		dateFormat.setLenient(false);

		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, true, datePattern.length()));
		binder
				.registerCustomEditor(String.class, new StringTrimmerEditor(
						true));
	}

	@ModelAttribute("notificationTypes")
	public List<MessageDefinition> getNotificationTypes() {
		return contextService.getMotechService().getAllMessageDefinitions();
	}

	@ModelAttribute("message")
	public WebMessage getMessage() {
		return new WebMessage();
	}

	@RequestMapping(method = RequestMethod.GET)
	public void viewForm(ModelMap model) {
	}

	@RequestMapping(method = RequestMethod.POST)
	public void submitForm(@ModelAttribute("message") WebMessage message,
			Errors errors, ModelMap model, SessionStatus status) {

		log.debug("Sending Patient Message");

		if (!errors.hasErrors()) {
			String motechIdString = null;
			if (message.getMotechId() != null) {
				motechIdString = message.getMotechId().toString();
			}
			messageService.sendPatientMessage(null, new NameValuePair[0],
					message.getPhoneNumber(), message.getPhoneType(), message
							.getLanguage(), message.getMediaType(), message
							.getNotificationType(), message.getStartDate(),
					message.getEndDate(), motechIdString);

			model.addAttribute("successMsg",
					"motechmodule.Demo.Patient.message.success");

			status.setComplete();
		}
	}

}
