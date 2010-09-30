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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import java.util.Collections;
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
    public String viewAddForm(ModelMap modelMap){
        modelMap.addAttribute("community", new WebCommunity());
        return "/module/motechmodule/addcommunity";
    }

    @RequestMapping(value = "/module/motechmodule/community/submit.form", method = RequestMethod.POST)
    public String submitForm(@ModelAttribute("community")WebCommunity webCommunity, Errors errors, ModelMap modelMap, SessionStatus status){
        if(webCommunity.getName().trim().isEmpty()){
            errors.rejectValue("name", "motechmodule.communityName.invalid");
        }

        Community community;
        if(webCommunity.getCommunityId() == null){
            community = new Community();
            if(contextService.getMotechService().getCommunityByFacilityIdAndName(webCommunity.getFacilityId(), webCommunity.getName()) != null){
                errors.rejectValue("name", "motechmodule.communityName.duplicate");
            }
            community.setName(webCommunity.getName().trim());
            community.setFacility(contextService.getMotechService().getFacilityById(webCommunity.getFacilityId()));
        }else{
            community = contextService.getMotechService().getCommunityById(webCommunity.getCommunityId());
            community.setFacility(contextService.getMotechService().getFacilityById(webCommunity.getFacilityId()));
            community.setName(webCommunity.getName().trim());
        }
        if(!errors.hasErrors()){
            contextService.getRegistrarBean().saveCommunity(community);
            modelMap.addAttribute("successMsg", "Community added");
            status.setComplete();
            return "redirect:/module/motechmodule/community.form";
        }
        modelMap.addAttribute("community", webCommunity);
        return "/module/motechmodule/addcommunity";
    }

    @RequestMapping(value = "/module/motechmodule/community/editcommunity.form", method = RequestMethod.GET)
    public String viewEditForm(@RequestParam(required = true) Integer communityId, ModelMap modelMap){
        Community community = contextService.getMotechService().getCommunityById(communityId);
        modelMap.addAttribute("community", new WebCommunity(community));
        return "/module/motechmodule/editcommunity";
    }

    @ModelAttribute("communities")
    public List<Community> getCommunities(){
        return contextService.getMotechService().getAllCommunities(true);
    }

    @ModelAttribute("facilities")
    public List<Facility> getFacilities(){
        return contextService.getMotechService().getAllFacilities();
    }

}
