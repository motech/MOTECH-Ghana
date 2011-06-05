package org.motechproject.server.model;

public class DefaultedExpectedEncounterAlert {

    private Long id ;
    private ExpectedEncounter expectedEncounter;
    private CareConfiguration careConfiguration;
    private Integer alertsSent ;

    public DefaultedExpectedEncounterAlert() {
    }

    public DefaultedExpectedEncounterAlert(Long id, ExpectedEncounter expectedEncounter, CareConfiguration careConfiguration, Integer alertsSent) {
        this.id = id;
        this.expectedEncounter = expectedEncounter;
        this.careConfiguration = careConfiguration;
        this.alertsSent = alertsSent;
    }

    public Boolean isFor(ExpectedEncounter expectedEncounter) {
        return expectedEncounter.equals(expectedEncounter);
    }

    public Boolean canBeSent() {
        return careConfiguration.canAlertBeSent(alertsSent);
    }
}
