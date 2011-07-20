package org.motechproject.server.model;

import java.util.Date;

public class DefaultedExpectedEncounterAlert {

    private Long id;
    private ExpectedEncounter expectedEncounter;
    private CareConfiguration careConfiguration;
    private Integer alertsDelivered;
    private Integer alertAttempts;
    private Date lastAttempted;
    private Date lastDelivered;


    public DefaultedExpectedEncounterAlert() {
    }

    public DefaultedExpectedEncounterAlert(Long id, ExpectedEncounter expectedEncounter, CareConfiguration careConfiguration, Integer alertsDelivered, Integer alertAttempts) {
        this(expectedEncounter, careConfiguration, alertsDelivered, alertAttempts);
        this.id = id;
    }

    public DefaultedExpectedEncounterAlert(ExpectedEncounter expectedEncounter, CareConfiguration careConfiguration, Integer alertsDelivered, Integer alertAttempts) {
        this.expectedEncounter = expectedEncounter;
        this.careConfiguration = careConfiguration;
        this.alertsDelivered = alertsDelivered;
        this.alertAttempts = alertAttempts;
        this.lastAttempted = new Date();
        if (alertsDelivered > 0) {
            this.lastDelivered = new Date();
        }
    }

    public Boolean isFor(ExpectedEncounter expectedEncounter) {
        return expectedEncounter.equals(expectedEncounter);
    }

    public Boolean canBeSent() {
        return careConfiguration == null ? true : careConfiguration.canAlertBeSent(alertsDelivered);
    }

    public void delivered() {
        alertsDelivered = alertsDelivered + 1;
        lastDelivered = new Date();
    }

    public void attempted() {
        alertAttempts = alertAttempts + 1;
        lastAttempted = new Date();
    }

    public Boolean isSameAs(DefaultedExpectedEncounterAlert other) {
        return expectedEncounter.equals(other.expectedEncounter)
                && alertsDelivered.equals(other.alertsDelivered)
                && alertAttempts.equals(other.alertAttempts);
    }
}
