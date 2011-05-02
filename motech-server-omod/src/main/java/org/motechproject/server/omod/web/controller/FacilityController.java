/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
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

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.web.model.WebFacility;
import org.openmrs.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

@Controller
public class FacilityController {

    @Autowired
	private ContextService contextService;

    public FacilityController(ContextService contextService) {
        this.contextService = contextService;
    }

    @ModelAttribute("facilities")
	public List<Facility> getFacilities() {
		return contextService.getMotechService().getAllFacilities();
	}

	@RequestMapping(value = "/module/motechmodule/facility.form", method = RequestMethod.GET)
	public String viewFacilities() {
		return "/module/motechmodule/facility";
	}

    @RequestMapping(value = "/module/motechmodule/addfacility.form", method = RequestMethod.GET)
    public String viewAddFacilityForm(ModelMap modelMap){
        populateLocation(modelMap);
        modelMap.addAttribute("facility", new WebFacility());
        return "/module/motechmodule/addfacility";
    }

    @RequestMapping(value = "/module/motechmodule/addfacility.form", method = RequestMethod.POST)
    public String submitAddFacility(@ModelAttribute("facility") WebFacility facility, Errors errors,ModelMap modelMap, SessionStatus status){
        if(contextService.getMotechService().getLocationByName(facility.getName()) != null){
            errors.rejectValue("name","motechmodule.Facility.duplicate.location");
        }
        if(errors.hasErrors()){
            populateLocation(modelMap);
            return "/module/motechmodule/addfacility";
        }
        contextService.getLocationService().saveLocation(facility.getFacility().getLocation());
        contextService.getRegistrarBean().saveNewFacility(facility.getFacility());
        return "redirect:/module/motechmodule/facility.form";
    }

    @ModelAttribute("locations")
    public List<Location> getLocations(){
        return contextService.getLocationService().getAllLocations(false);
    }

    private void populateLocation(ModelMap modelMap) {
        List<Location> locations = contextService.getLocationService().getAllLocations();
        Set<String> countries = new TreeSet<String>();
        Map<String, TreeSet<String>> regions = new HashMap<String, TreeSet<String>>();
        Map<String, TreeSet<String>> districts = new HashMap<String, TreeSet<String>>();
        Map<String, TreeSet<String>> provinces = new HashMap<String, TreeSet<String>>();
        for (Location location : locations) {
            countries.add(location.getCountry());
            populate(regions, location.getCountry(), location.getRegion());
            populate(districts, location.getRegion(), location.getCountyDistrict());
            populate(provinces, location.getCountyDistrict(), location.getStateProvince());
        }
        modelMap.addAttribute("countries",countries);
        modelMap.addAttribute("regions",regions);
        modelMap.addAttribute("districts",districts);
        modelMap.addAttribute("provinces", provinces);
    }

    private void populate(Map<String, TreeSet<String>> map, String key, String value) {
        if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) return;
        if (map.containsKey(key)) {
            map.get(key).add(value);
            return;
        }
        TreeSet<String> values = new TreeSet<String>();
        values.add(value);
        map.put(key, values);
    }
}
