package org.motechproject.server.model;

import org.motechproject.server.builder.ObsBuilder;
import org.motechproject.server.service.ConceptEnum;
import org.openmrs.*;
import org.openmrs.api.ConceptService;

import java.util.Date;

public enum Immunizations {

    BCG("bcg", ConceptEnum.CONCEPT_BCG_VACCINATION),
    YELLOW_FEVER("yellowfever", ConceptEnum.CONCEPT_YELLOW_FEVER_VACCINATION),
    MEASLES("measles", ConceptEnum.CONCEPT_MEASLES_VACCINATION),
    CSM("csm", ConceptEnum.CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION),
    VITAMIN_A("vitamina", ConceptEnum.CONCEPT_VITAMIN_A),
    DEWORMER("dewormer", ObsValueType.BOOLEAN, ConceptEnum.CONCEPT_DEWORMER, null) {
        @Override
        public Object getValue(ConceptService conceptService) {
            return Boolean.TRUE;
        }};


    private String key;
    private ObsValueType valueType;
    private ConceptEnum concept;
    private ConceptEnum valueConcept;

    private Immunizations(String key, ConceptEnum valueConcept) {
        this(key, ObsValueType.CODED, ConceptEnum.CONCEPT_IMMUNIZATIONS_ORDERED, valueConcept);
    }

    private Immunizations(String key, ObsValueType type, ConceptEnum concept, ConceptEnum valueConcept) {
        this.key = key;
        this.valueType = type;
        this.concept = concept;
        this.valueConcept = valueConcept;
    }

    public Obs obsWith(Date date, Patient patient, Location location, Encounter encounter,
                       User user, ConceptService conceptService) {
        Obs obs = new ObsBuilder()
                .withValue(getValue(conceptService))
                .withConcept(concept.getConcept(conceptService))
                .withValueType(valueType)
                .forPerson(patient).against(encounter)
                .recordedOn(date).recordedBy(user).recordedAt(location)
                .done();
        return obs;
    }

    public Object getValue(ConceptService conceptService) {
        return this.valueConcept.getConcept(conceptService);
    }


    public static Immunizations enumFor(String key) {
        for (Immunizations immunization : values()) {
            if (immunization.key.equals(key)) {
                return immunization;
            }
        }
        return null;
    }
}
