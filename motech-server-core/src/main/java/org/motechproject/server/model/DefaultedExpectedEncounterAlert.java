package org.motechproject.server.model;

public class DefaultedExpectedEncounterAlert {

    private Long id ;
    private ExpectedEncounter expectedEncounter;
    private CareConfiguration careConfiguration;
    private Integer alertsSent ;

    public ExpectedEncounter getExpectedEncounter() {
        return expectedEncounter;
    }
}
