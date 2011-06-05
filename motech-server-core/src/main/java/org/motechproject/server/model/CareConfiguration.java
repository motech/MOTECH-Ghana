package org.motechproject.server.model;

public class CareConfiguration {

    private Long id ;
    private String name ;
    private Integer maxAlertsToBeSent;

    public CareConfiguration(){}

    public CareConfiguration(Long id, String name, Integer maxAlertsToBeSent) {
        this.id = id;
        this.name = name;
        this.maxAlertsToBeSent = maxAlertsToBeSent;
    }

    public Boolean canAlertBeSent(Integer alertCount) {
        return alertCount < maxAlertsToBeSent ;
    }

}
