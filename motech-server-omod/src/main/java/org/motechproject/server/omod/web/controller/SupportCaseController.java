package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.IncomingMessage;
import org.motechproject.server.svc.SupportCaseService;
import org.motechproject.ws.Response;
import org.motechproject.ws.SMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class SupportCaseController {

    @Autowired
    private SupportCaseService supportCaseService;
    private static final String VIEW = "/module/motechmodule/support_response";

    @RequestMapping(value = "/module/motechmodule/support.form",method = RequestMethod.GET)
    public ModelAndView raiseSupportCase(SMS sms){
        ModelAndView modelAndView = new ModelAndView(VIEW);
        Response response = supportCaseService.mailToSupport(new IncomingMessage(sms));
        modelAndView.addObject("response",response.getContent());
        return modelAndView;
    }

    public void setSupportCaseService(SupportCaseService supportCaseService) {
        this.supportCaseService = supportCaseService;
    }
}
