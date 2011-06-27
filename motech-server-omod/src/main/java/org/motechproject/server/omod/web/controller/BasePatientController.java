package org.motechproject.server.omod.web.controller;


import org.motechproject.server.omod.web.model.JSONLocationSerializer;
import org.motechproject.server.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;

public class BasePatientController {

    protected ContextService contextService;

    protected JSONLocationSerializer JSONLocationSerializer;

    @Autowired
	public void setJSONLocationSerializer(JSONLocationSerializer JSONLocationSerializer) {
		this.JSONLocationSerializer = JSONLocationSerializer;
	}
}
