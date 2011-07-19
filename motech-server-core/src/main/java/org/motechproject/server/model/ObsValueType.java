package org.motechproject.server.model;

import org.openmrs.Concept;
import org.openmrs.Obs;

import java.util.Date;

public enum ObsValueType {
    NUMERIC {
        @Override
        public Obs setValue(Obs obs, Object value) {
            obs.setValueNumeric((Double) value);
            return obs;
        }},
    DATE {
        @Override
        public Obs setValue(Obs obs, Object value) {
            obs.setValueDatetime((Date) value);
            return obs;
        }},
    CODED {
        @Override
        public Obs setValue(Obs obs, Object value) {
            obs.setValueCoded((Concept) value);
            return obs;
        }},
    BOOLEAN {
        @Override
        public Obs setValue(Obs obs, Object value) {
            Boolean boolValue = (Boolean) value;
            Double numeric = Boolean.TRUE.equals(boolValue) ? new Double(1) : new Double(0);
            return NUMERIC.setValue(obs, numeric);
        }},
    TEXT {
        @Override
        public Obs setValue(Obs obs, Object value) {
            obs.setValueText((String) value);
            return obs;
        }};

    public abstract Obs setValue(Obs obs, Object value);
}
