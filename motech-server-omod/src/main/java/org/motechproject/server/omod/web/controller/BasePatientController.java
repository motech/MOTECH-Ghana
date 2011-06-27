package org.motechproject.server.omod.web.controller;


import org.motechproject.server.omod.web.localization.LocationController;
import org.motechproject.server.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;

public class BasePatientController {

    protected ContextService contextService;

    protected LocationController locationController;

    @Autowired
	public void setLocationController(LocationController locationController) {
		this.locationController = locationController;
	}
}
