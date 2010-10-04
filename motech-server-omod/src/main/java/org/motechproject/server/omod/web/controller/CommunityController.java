/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) ${year} The Trustees of Columbia University in the City of
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

import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.web.model.WebCommunity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;

import javax.annotation.Resource;
import java.util.List;

@Controller
public class CommunityController {

    @Autowired
    ContextService contextService;

    @Resource(name = "webCommunityValidator")
    Validator webCommunityValidator;
    
    public void setWebCommunityValidator(Validator webCommunityValidator) {
        this.webCommunityValidator = webCommunityValidator;
    }
    
    @RequestMapping(value = "/module/motechmodule/community", method = RequestMethod.GET)
    public String list(ModelMap modelMap){
        return "/module/motechmodule/communities";
    }

    @RequestMapping(value = "/module/motechmodule/community/add.form", method = RequestMethod.GET)
    public String viewAddForm(ModelMap modelMap){
        modelMap.addAttribute("community", new WebCommunity());
        return "/module/motechmodule/addcommunity";
    }

    @RequestMapping(value = "/module/motechmodule/community/add.form", method = RequestMethod.POST)
    public String submitForm(@ModelAttribute("community")WebCommunity webCommunity, Errors errors, ModelMap modelMap, SessionStatus status) {
        MotechService motechService = contextService.getMotechService();

        webCommunityValidator.validate(webCommunity, errors);

        if (errors.hasErrors()) {
            modelMap.addAttribute("community", webCommunity);
            return "/module/motechmodule/addcommunity";
        }
        Community community = new Community(webCommunity.getName(), motechService.getFacilityById(webCommunity.getFacilityId()));
        contextService.getRegistrarBean().saveCommunity(community);
        modelMap.addAttribute("successMsg", "Community added");
        status.setComplete();
        return "redirect:/module/motechmodule/community.form";
    }

    @RequestMapping(value = "/module/motechmodule/community/editcommunity.form", method = RequestMethod.GET)
    public String viewEditForm(@RequestParam(required = true) Integer communityId, ModelMap modelMap){
        Community community = contextService.getMotechService().getCommunityById(communityId);
        modelMap.addAttribute("community", new WebCommunity(community));
        return "/module/motechmodule/editcommunity";
    }
    
    @RequestMapping(value = "/module/motechmodule/community/editcommunity.form", method = RequestMethod.POST)
    public String viewEditForm(@ModelAttribute("community")WebCommunity webCommunity, Errors errors, ModelMap modelMap, SessionStatus status){
        MotechService motechService = contextService.getMotechService();
        webCommunityValidator.validate(webCommunity, errors);

        if (errors.hasErrors()) {
            modelMap.addAttribute("community", webCommunity);
            return "/module/motechmodule/editcommunity";
        }
        Community community = motechService.getCommunityById(webCommunity.getCommunityId());
        community.setFacility(motechService.getFacilityById(webCommunity.getFacilityId()));
        community.setName(webCommunity.getName());
        contextService.getRegistrarBean().saveCommunity(community);
        status.setComplete();
        return "redirect:/module/motechmodule/community.form";
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
