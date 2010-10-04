package org.motechproject.server.omod.web.validator;

import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.web.model.WebCommunity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class WebCommunityValidator implements Validator {

    @Autowired
    ContextService contextService;

    public boolean supports(Class aClass) {
        return aClass.equals(WebCommunity.class);
    }

    public void validate(Object o, Errors errors) {
        WebCommunity webCommunity = (WebCommunity) o;
        if (webCommunity.getName().isEmpty()) {
            errors.rejectValue("name", "motechmodule.communityName.invalid");
        }
        if (contextService.getMotechService().getCommunityByFacilityIdAndName(webCommunity.getFacilityId(), webCommunity.getName()) != null) {
            errors.rejectValue("name", "motechmodule.communityName.duplicate");
        }
    }
}
