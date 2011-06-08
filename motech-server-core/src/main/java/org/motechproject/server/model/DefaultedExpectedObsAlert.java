package org.motechproject.server.model;

public class DefaultedExpectedObsAlert {

    private Long id;
    private ExpectedObs expectedObs;
    private CareConfiguration careConfiguration;
    private Integer alertsDelivered;
    private Integer alertAttempts;

    public DefaultedExpectedObsAlert() {
    }

    public DefaultedExpectedObsAlert(Long id, ExpectedObs expectedObs, CareConfiguration careConfiguration, Integer alertsDelivered, Integer alertAttempts) {
        this(expectedObs, careConfiguration, alertsDelivered, alertAttempts);
        this.id = id;
    }

    public DefaultedExpectedObsAlert(ExpectedObs expectedObs, CareConfiguration careConfiguration, Integer alertsDelivered, Integer alertAttempts) {
        this.expectedObs = expectedObs;
        this.careConfiguration = careConfiguration;
        this.alertsDelivered = alertsDelivered;
        this.alertAttempts = alertAttempts;
    }

    public Boolean isFor(ExpectedObs obs) {
        return expectedObs.equals(obs);
    }

    public Boolean canBeSent() {
        return null == careConfiguration ? true : careConfiguration.canAlertBeSent(alertsDelivered);
    }

    public void delivered() {
        alertsDelivered = alertsDelivered + 1;
    }

    public void attempted() {
        alertAttempts = alertAttempts + 1;
    }
}
