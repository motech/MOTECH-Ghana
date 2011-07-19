package org.motechproject.server.builder;

import org.motechproject.server.model.ObsValueType;
import org.openmrs.*;

import java.util.Date;

public class ObsBuilder {

    private Object value;
    private Concept concept;
    private Person person;
    private Encounter encounter;
    private Location location;
    private User recordedBy;
    private Date observationDate;
    private ObsValueType type;


    public ObsBuilder withValue(Object value) {
        this.value = value;
        return this;
    }

    public ObsBuilder withConcept(Concept concept) {
        this.concept = concept;
        return this;
    }

    public ObsBuilder withValueType(ObsValueType valueType) {
        this.type = valueType;
        return this;
    }

    public ObsBuilder forPerson(Person person) {
        this.person = person;
        return this;
    }

    public ObsBuilder against(Encounter encounter) {
        this.encounter = encounter;
        return this;
    }

    public ObsBuilder recordedOn(Date date) {
        this.observationDate = date;
        return this;
    }

    public ObsBuilder recordedAt(Location location) {
        this.location = location;
        return this;
    }

    public ObsBuilder recordedBy(User user) {
        this.recordedBy = user;
        return this;
    }

    public Obs done() {
        Obs obs = new Obs();
        obs.setConcept(concept);
        obs.setObsDatetime(observationDate);
        obs.setPerson(person);
        obs.setLocation(location);
        if (encounter != null) {
            obs.setEncounter(encounter);
        }
        if (recordedBy != null) {
            obs.setCreator(recordedBy);
        }
        return type.setValue(obs,value);
    }
}
