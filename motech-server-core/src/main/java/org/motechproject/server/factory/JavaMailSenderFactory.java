package org.motechproject.server.factory;

import org.apache.commons.lang.StringUtils;
import org.motechproject.server.util.MailingConstants;
import org.openmrs.api.AdministrationService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Authenticator;
import javax.mail.Session;
import java.util.Properties;

public class JavaMailSenderFactory implements MailSenderFactory{

    private AdministrationService administrationService;

    public JavaMailSender mailSender(){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setSession(getSession());
        return sender;
    }

    private Session getSession() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host());
		properties.setProperty("mail.smtp.port", port());
        if(needAuthentication()){
            properties.setProperty("mail.smtp.starttls.enable","true");
            properties.setProperty("mail.smtp.auth", "true");
        }
        return Session.getInstance(properties,authenticator());
    }

    private String host() {
        String host = administrationService.getGlobalProperty(MailingConstants.SMTP_HOST);
        return StringUtils.isNotBlank(host) ? host : "localhost";
    }

    private String port() {
        String port = administrationService.getGlobalProperty(MailingConstants.SMTP_PORT);
        return StringUtils.isNotBlank(port) ? port : "25";
    }

    private Authenticator authenticator(){
        if(needAuthentication()){
           return new MotechMailAuthenticator(user(),password());  
        }
        return null;
    }

    private String user() {
        return administrationService.getGlobalProperty(MailingConstants.SMTP_USER);
    }

    private String password() {
        return administrationService.getGlobalProperty(MailingConstants.SMTP_PASSWORD);
    }

    private String protocol() {
        String protocol = administrationService.getGlobalProperty(MailingConstants.TRANSPORT_PROTOCOL);
        return StringUtils.isNotBlank(protocol) ? protocol : "smtp";
    }



    private Boolean needAuthentication() {
        String authenticationRequired = administrationService.getGlobalProperty(MailingConstants.SMTP_AUTH);
        return StringUtils.isNotBlank(authenticationRequired) ? authenticationRequired.equals("true") : false;
    }

    public void setAdministrationService(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }
}

