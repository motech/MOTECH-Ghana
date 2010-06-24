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
