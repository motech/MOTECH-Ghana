package org.motechproject.server.omod.web.controller;

import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebCommunity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class CommunityController {

    @Autowired
    ContextService contextService;
    
    @RequestMapping(value = "/module/motechmodule/community", method = RequestMethod.GET)
    public String list(ModelMap modelMap){
        return "/module/motechmodule/communities";
    }

    @RequestMapping(value = "/module/motechmodule/community/add.form", method = RequestMethod.GET)
    public String viewForm(ModelMap modelMap){
        return "/module/motechmodule/addcommunity";
    }

    @RequestMapping(value = "/module/motechmodule/community/submit.form", method = RequestMethod.POST)
    public String submitForm(@ModelAttribute("community") WebCommunity community, Errors errors, ModelMap modelMap){
        if(community.getName().equals("")){
            errors.reject("name", "Name cannot be blank");
        }

        if(!errors.hasErrors()){
            contextService.getRegistrarBean().saveCommunity(community.getName(), community.getFacilityId());
            modelMap.addAttribute("successMsg", "Community added");
            return "/module/motechmodule/communities";
        }
        return "/module/motechmodule/addcommunity";
    }

    @ModelAttribute("communities")
    public List<Community> getCommunities(){
        return contextService.getMotechService().getAllCommunities(true);
    }

    @ModelAttribute("facilities")
    public List<Facility> getFacilities(){
        return contextService.getMotechService().getAllFacilities();
    }

    @ModelAttribute("community")
    public WebCommunity getWebCommunity(){
        return new WebCommunity();
    }

}
