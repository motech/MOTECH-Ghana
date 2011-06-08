package org.motechproject.server.model;

public class DefaultedExpectedEncounterAlert {

    private Long id;
    private ExpectedEncounter expectedEncounter;
    private CareConfiguration careConfiguration;
    private Integer alertsDelivered;
    private Integer alertAttempts;

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
    }

    public Boolean isFor(ExpectedEncounter expectedEncounter) {
        return expectedEncounter.equals(expectedEncounter);
    }

    public Boolean canBeSent() {
        return careConfiguration == null ? true : careConfiguration.canAlertBeSent(alertsDelivered);
    }

    public void delivered() {
        alertsDelivered = alertsDelivered + 1;
    }

    public void attempted() {
        alertAttempts = alertAttempts + 1;
    }
}
