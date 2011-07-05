package org.motechproject.server.model;

public class MotechUserType {

    private Integer id;
    private String type;
    private String description;

    public Boolean hasType(String type) {
        return this.type.equals(type);
    }

    @Override
    public String toString() {
        return type;
    }
}
