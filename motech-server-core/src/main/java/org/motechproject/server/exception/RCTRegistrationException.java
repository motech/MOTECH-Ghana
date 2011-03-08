package org.motechproject.server.exception;

public class RCTRegistrationException extends Exception{

    private String messageKey ;

    public RCTRegistrationException(String messageKey){
        this.messageKey = messageKey ;
    }

    public String messageKey(){
        return messageKey;
    }
}
