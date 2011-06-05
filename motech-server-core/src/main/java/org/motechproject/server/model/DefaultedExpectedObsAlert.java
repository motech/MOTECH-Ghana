package org.motechproject.server.model;

public class DefaultedExpectedObsAlert {

    private Long id;
    private ExpectedObs expectedObs;
    private CareConfiguration careConfiguration;
    private Integer alertsSent;

    public DefaultedExpectedObsAlert() {
    }

    public DefaultedExpectedObsAlert(Long id, ExpectedObs expectedObs, CareConfiguration careConfiguration, Integer alertsSent) {
        this.id = id;
        this.expectedObs = expectedObs;
        this.careConfiguration = careConfiguration;
        this.alertsSent = alertsSent;
    }

    public Boolean isFor(ExpectedObs obs) {
        return expectedObs.equals(obs);
    }

    public Boolean canBeSent() {
        return careConfiguration.canAlertBeSent(alertsSent);
    }
}
