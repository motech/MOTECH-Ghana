package org.motechproject.server.model;

public class SupportCase implements ExtractedMessage{

    private String sender ;
    private String phoneNumber ;
    private String dateRaisedOn ;
    private String description;

    public SupportCase(){

    }

    public SupportCase(String sender, String phoneNumber, String dateRaisedOn, String description) {
        this.sender = sender;
        this.phoneNumber = phoneNumber;
        this.dateRaisedOn = dateRaisedOn;
        this.description = description;
    }

    public String getRaisedBy() {
        return sender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateRaisedOn() {
        return dateRaisedOn;
    }

    public String getDescription() {
        return description;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateRaisedOn(String dateRaisedOn) {
        this.dateRaisedOn = dateRaisedOn;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
