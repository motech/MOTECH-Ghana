package org.motechproject.server.omod.web.model;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.server.model.MailTemplate;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

public class SupportCaseMailTemplate implements MailTemplate {

    private VelocityEngine velocityEngine;

    private String template;
    private Map data;
    private String subjectTemplate;
    private String textTemplate;
    private static final String SPACE = " ";

    public SupportCaseMailTemplate(String subjectTemplate, String textTemplate) {
        this.subjectTemplate = subjectTemplate;
        this.textTemplate = textTemplate;
    }


    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String subject(Map data) {
        return renderWith(subjectTemplate,data);
    }

    public String text(Map data) {
        return renderWith(textTemplate,data);
    }

    private String renderWith(String template, Map data) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, data).trim();
    }
}
