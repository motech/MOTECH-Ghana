package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.omod.web.model.MappedURLs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class URLMapController {
    private static final String VIEW = "/module/motechmodule/urlmap";

    @Autowired
    private MessageProcessorDAO dao;

    @RequestMapping(value = "/module/motechmodule/urlmap.form",method = RequestMethod.GET)
    public ModelAndView list() {
        ModelAndView modelAndView = new ModelAndView(VIEW);
        MappedURLs mappedURLs = new MappedURLs(dao.list());
        modelAndView.getModelMap().addAttribute("mappedURLs",mappedURLs);
        return modelAndView;
    }

    public void setDao(MessageProcessorDAO dao) {
        this.dao = dao;
    }
}
