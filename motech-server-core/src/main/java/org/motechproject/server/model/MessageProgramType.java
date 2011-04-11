package org.motechproject.server.model;

public enum MessageProgramType {
    ExpectedCare("Expected Care Message Program"),
    WeeklyPregnancy("Weekly Pregnancy Message Program"),
    WeeklyPregnancyInfo("Weekly Info Pregnancy Message Program"),
    WeeklyChildInfo("Weekly Info Child Message Program"),
    DemoMinuteMessageProgram("Demo Minute Message Program"),
    InputDemoMessageProgram("Input Demo Message Program");



    private String programName;

    private MessageProgramType(String programName){
        this.programName = programName;
    }

    public String program() {
        return programName;
    }
}
