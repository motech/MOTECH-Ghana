package org.motechproject.server.factory;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MotechMailAuthenticator extends Authenticator {
    private PasswordAuthentication authentication;

    public MotechMailAuthenticator(String user,String password){
        authentication = new PasswordAuthentication(user,password);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return authentication;
    }
}
