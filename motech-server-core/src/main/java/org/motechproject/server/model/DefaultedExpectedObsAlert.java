package org.motechproject.server.model;

public class DefaultedExpectedObsAlert {

    private Long id;
    private ExpectedObs expectedObs;
    private CareConfiguration careConfiguration;
    private Integer alertsSent;

    public Boolean isFor(ExpectedObs obs) {
        return expectedObs.equals(obs);
    }
}
