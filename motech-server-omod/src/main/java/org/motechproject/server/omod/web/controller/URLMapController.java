package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.MessageProcessorURL;
import org.motechproject.server.model.db.MessageProcessorDAO;
import org.motechproject.server.omod.web.model.MappedURLs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class URLMapController {
    private static final String VIEW = "/module/motechmodule/urlmap";

    @Autowired
    private MessageProcessorDAO dao;
    private static final String REDIRECT_TO_VIEW = "redirect:/module/motechmodule/urlmap.form";

    @RequestMapping(value = "/module/motechmodule/urlmap", method = RequestMethod.GET)
    public ModelAndView list() {
        return modelAndView(false);
    }

    @RequestMapping(value = "/module/motechmodule/urlmap/add", method = RequestMethod.GET)
    public ModelAndView add() {
        return modelAndView(true);
    }

    @Transactional
    @RequestMapping(value = "/module/motechmodule/urlmap/edit", method = RequestMethod.POST)
    public String edit(@ModelAttribute("messageProcessorURL") MessageProcessorURL modifiedURL) {
            MessageProcessorURL existingURL = dao.urlFor(modifiedURL.getKey());
            if (null == existingURL) {
                dao.save(modifiedURL);
                return REDIRECT_TO_VIEW;
            }
            existingURL.updateWith(modifiedURL);
            dao.update(existingURL);

        return REDIRECT_TO_VIEW;
    }

    private ModelAndView modelAndView(Boolean addNew) {
        ModelAndView modelAndView = new ModelAndView(VIEW);
        modelAndView.getModelMap().addAttribute("mappedURLs", new MappedURLs(dao.list()));
        modelAndView.getModelMap().addAttribute("addNew", addNew);
        if(addNew){
            modelAndView.getModelMap().addAttribute("messageProcessorURL",new MessageProcessorURL());
        }
        return modelAndView;
    }

    public void setDao(MessageProcessorDAO dao) {
        this.dao = dao;
    }
}
