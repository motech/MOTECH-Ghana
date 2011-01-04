package org.motechproject.server.omod;

import org.openmrs.Person;
import org.openmrs.Relationship;

public interface RelationshipService {

    public Relationship saveOrUpdateMotherRelationship(Person mother,Person Child);

    public Relationship createMotherChildRelationship(Person mother, Person child);

    public Relationship updateMotherRelationship(Person mother,Person child);

    public Relationship voidRelationship(Relationship relationship);

    public Relationship getMotherRelationship(Person child);
}
