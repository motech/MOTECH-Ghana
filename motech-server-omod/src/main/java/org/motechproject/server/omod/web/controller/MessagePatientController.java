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
