package org.motechproject.server.model;

public enum MessageProgramType {
    ExpectedCare("Expected Care Message Program"),
    WeeklyPregnancy("Weekly Pregnancy Message Program");

    private String programName;

    private MessageProgramType(String programName){
        this.programName = programName;
    }

    public String program() {
        return programName;
    }
}
