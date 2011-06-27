package org.motechproject.server.omod.web.model;

import org.apache.velocity.app.VelocityEngine;
import org.motechproject.server.model.SupportCase;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

public class SupportCaseMailTemplate implements MailTemplate {

    private VelocityEngine velocityEngine;

    private String template;
    private Map data;
    private String subjectTemplate;
    private String bodyTemplate;
    private static final String SPACE = " ";

    public SupportCaseMailTemplate(String subjectTemplate, String bodyTemplate) {
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
    }


    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public String subject(Map data) {
        SupportCase supportCase = (SupportCase) data.get("case");
        WebStaff staff = (WebStaff) data.get("staff");
        StringBuilder subject = new StringBuilder("Support: Case reported by ");
        subject.append(staff.getFirstName()).append(SPACE)
                .append(staff.getLastName()).append(SPACE)
                .append("on").append(SPACE)
                .append(supportCase.getDateRaisedOn());
        return subject.toString();
    }

    public String text(Map data) {
        SupportCase supportCase = (SupportCase) data.get("case");
        WebStaff staff = (WebStaff) data.get("staff");
        StringBuilder text = new StringBuilder();
        text.append(staff.getFirstName()).append(SPACE)
                .append(staff.getLastName()).append(SPACE)
                .append("with Staff id").append(SPACE)
                .append(staff.getStaffId()).append(SPACE)
                .append("reported the following issue:").append(SPACE)
                .append(supportCase.getDescription());
        return text.toString();
    }

    private String renderWith(String template, Map data) {
        return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, template, data);
    }
}
