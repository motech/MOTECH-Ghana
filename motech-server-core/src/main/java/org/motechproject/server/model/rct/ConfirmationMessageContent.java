package org.motechproject.server.model.rct;

import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.ControlGroup;

public class ConfirmationMessageContent {
    private Patient patient;
    private ControlGroup controlGroup;

    public ConfirmationMessageContent(Patient patient, ControlGroup controlGroup) {
        this.patient = patient;
        this.controlGroup = controlGroup;
    }

    public String text(){
        StringBuilder message = new StringBuilder();
        if(null != patient){
            message.append(patient.getPreferredName());
            message.append(" With MoTeCH ID ");
            message.append(patient.getMotechId());
            message.append(" has been successfully placed in the  ");
            message.append(controlGroup.value());
            message.append(" group");
        }
        return message.toString();
    }
}
