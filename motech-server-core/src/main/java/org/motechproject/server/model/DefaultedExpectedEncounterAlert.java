package org.motechproject.server.model;

public class DefaultedExpectedEncounterAlert {

    private Long id;
    private ExpectedEncounter expectedEncounter;
    private CareConfiguration careConfiguration;
    private Integer alertsSent;

    public DefaultedExpectedEncounterAlert() {
    }

    public DefaultedExpectedEncounterAlert(Long id, ExpectedEncounter expectedEncounter, CareConfiguration careConfiguration, Integer alertsSent) {
        this(expectedEncounter, careConfiguration, alertsSent);
        this.id = id;
    }

    public DefaultedExpectedEncounterAlert(ExpectedEncounter expectedEncounter, CareConfiguration careConfiguration, Integer alertsSent) {
        this.expectedEncounter = expectedEncounter;
        this.careConfiguration = careConfiguration;
        this.alertsSent = alertsSent;
    }

    public Boolean isFor(ExpectedEncounter expectedEncounter) {
        return expectedEncounter.equals(expectedEncounter);
    }

    public Boolean canBeSent() {
        return careConfiguration == null ? true :careConfiguration.canAlertBeSent(alertsSent);
    }

    public void incrementCount() {
        alertsSent = alertsSent + 1;
    }
}
