package org.motechproject.server.ws;

public enum ANCRegisterOption {
    TODAY("0"),
    IN_THE_PAST("1"),
    IN_THE_PAST_IN_OTHER_FACILITY("2");

    private String key;

    ANCRegisterOption(String key) {
        this.key = key;
    }

    public boolean isSameAs(String key) {
        return this.key.equals(key);
    }
}
