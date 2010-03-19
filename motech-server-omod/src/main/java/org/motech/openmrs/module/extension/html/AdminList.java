/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.motech.openmrs.module.extension.html;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;

/**
 * This class defines the links that will appear on the administration page
 * under the "basicmodule.title" heading. This extension is enabled by defining
 * (uncommenting) it in the /metadata/config.xml file.
 */
public class AdminList extends AdministrationSectionExt {

	@Override
	public String getRequiredPrivilege() {
		return "Manage MoTeCH";
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
	 */
	public String getTitle() {
		return "motechmodule.title";
	}

	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 */
	public Map<String, String> getLinks() {

		Map<String, String> map = new HashMap<String, String>();

		map.put("module/motechmodule/mother.form", "Register Pregnant Mother");
		map.put("module/motechmodule/clinic.form", "Register Clinic");
		map.put("module/motechmodule/nurse.form", "Register Nurse");
		map.put("module/motechmodule/child.form", "Register Child");
		map.put("module/motechmodule/person.form", "Register Person");
		map.put("module/motechmodule/viewdata.form", "View Data");
		map.put("module/motechmodule/search.form", "Search");
		map.put("module/motechmodule/blackout.form", "Blackout Interval");
		map.put("module/motechmodule/troubledphone.form", "Troubled Phones");

		return map;
	}

}
