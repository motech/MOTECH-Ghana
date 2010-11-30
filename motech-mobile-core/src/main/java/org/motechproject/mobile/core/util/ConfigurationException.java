package org.motechproject.mobile.core.util;

/**
 * The exception that is thrown when an incorrect configuration  has detected.
 *
 * @author Igor Opushnyev (iopushnyev@2paths.com)
 * Created: 29-Nov-2010
 *
 */
public class ConfigurationException extends Exception{

    public ConfigurationException() {
    }

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
