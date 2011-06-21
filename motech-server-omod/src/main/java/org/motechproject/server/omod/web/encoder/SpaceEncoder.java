package org.motechproject.server.omod.web.encoder;

public class SpaceEncoder implements Encoder{

    private static final String SPACE = "\\s";
    private static final String ENCODING = "%2B";


    public String encode(String toEncode) {
        return toEncode.replaceAll(SPACE, ENCODING);
    }
}
