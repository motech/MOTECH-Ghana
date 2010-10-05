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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.server.omod.ContextService;
import org.openmrs.Location;
import org.springframework.ui.ModelMap;

public class BasePatientController {

	ContextService contextService;
	Comparator<Community> communityNameComparator;

	public BasePatientController() {
		communityNameComparator = new Comparator<Community>() {
			public int compare(Community c1, Community c2) {
				return c1.getName().compareTo(c2.getName());
			}
		};
	}

	void populateJavascriptMaps(ModelMap model) {
		Map<String, TreeSet<String>> regionMap = new HashMap<String, TreeSet<String>>();
		Map<String, TreeSet<Community>> districtMap = new HashMap<String, TreeSet<Community>>();

		List<Facility> facilities = contextService.getMotechService()
				.getAllFacilities();
		for (Facility facility : facilities) {
			Location location = facility.getLocation();
			if (location != null) {
				String region = location.getRegion();
				String district = location.getCountyDistrict();
				TreeSet<String> districts = regionMap.get(region);
				if (districts == null) {
					districts = new TreeSet<String>();
				}
				districts.add(district);
				regionMap.put(region, districts);
				TreeSet<Community> communities = districtMap.get(district);
				if (communities == null) {
					communities = new TreeSet<Community>(
							communityNameComparator);
				}
				communities.addAll(facility.getCommunities());
				districtMap.put(district, communities);
			}
		}

		model.addAttribute("regionMap", regionMap);
		model.addAttribute("districtMap", districtMap);
	}

}
