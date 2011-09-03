package org.motechproject.server.model;

import java.util.Date;

public class DefaultedExpectedObsAlert {

    private Long id;
    private ExpectedObs expectedObs;
    private CareConfiguration careConfiguration;
    private Integer alertsDelivered;
    private Integer alertAttempts;
    private Date lastAttempted;
    private Date lastDelivered;

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
        this.lastAttempted = new Date();
        if (alertsDelivered > 0) this.lastDelivered = new Date();
    }

    public Boolean isFor(ExpectedObs obs) {
        return expectedObs.equals(obs);
    }

    public Boolean canBeSent() {
        return null == careConfiguration ? true : careConfiguration.canAlertBeSent(alertsDelivered);
    }

    public void delivered() {
        alertsDelivered = alertsDelivered + 1;
        lastDelivered = new Date();
    }

    public void attempted() {
        alertAttempts = alertAttempts + 1;
        lastAttempted = new Date();
    }

    public Boolean isSameAs(DefaultedExpectedObsAlert other) {
        return expectedObs.equals(other.expectedObs)
                && alertsDelivered.equals(other.alertsDelivered)
                && alertAttempts.equals(other.alertAttempts);

    }
}
