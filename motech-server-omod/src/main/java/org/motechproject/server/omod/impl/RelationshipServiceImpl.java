package org.motechproject.server.omod.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.omod.ContextService;
import org.motechproject.server.omod.RelationshipService;
import org.motechproject.server.util.MotechConstants;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;

import java.util.List;

public class RelationshipServiceImpl implements RelationshipService {

        private static Log log = LogFactory.getLog(RelationshipService.class);

        private ContextService contextService;

        public Relationship saveOrUpdateMotherRelationship(Person mother, Person child) {

            Relationship motherRelationship = getMotherRelationship(child);

            boolean motherRelationshipCreated = mother != null && motherRelationship == null;
            boolean motherRelationshipEdited = mother != null && motherRelationship != null;
            boolean motherRelationshipRemoved = mother == null && motherRelationship != null;

            if(motherRelationshipCreated)
              motherRelationship = createMotherChildRelationship(mother,child);
            else if(motherRelationshipRemoved)
              motherRelationship = voidRelationship(motherRelationship);
            else if(motherRelationshipEdited)
              motherRelationship = updateMotherRelationship(mother,child);

            return motherRelationship;
        }

        public Relationship updateMotherRelationship(Person mother, Person child) {
            PersonService personService = personService();
            Relationship motherRelationship = getMotherRelationship(child);
            Person currentMother = motherRelationship.getPersonA();
            if (!currentMother.getPersonId().equals(mother.getPersonId())) {
                motherRelationship.setPersonA(mother);
                personService.saveRelationship(motherRelationship);
            }
            return motherRelationship;
        }

        public Relationship voidRelationship(Relationship relationship) {
            PersonService personService = personService();
            relationship  = personService.voidRelationship(relationship, "Removed in web or mobile form");
            // Saving relationship since voidRelationship will not save with
            // required advice and void handler
            personService.saveRelationship(relationship);
            return relationship;
        }

        public Relationship createMotherChildRelationship(Person mother, Person child) {
            PersonService personService = personService();
            RelationshipType parentChildRelationshipType
                    = personService.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD);
            Relationship relationship = new Relationship(mother, child, parentChildRelationshipType);
            personService.saveRelationship(relationship);
            return relationship;
        }

        public Relationship getMotherRelationship(Person child) {
            PersonService personService = personService();
            RelationshipType parentChildType
                    = personService.getRelationshipTypeByName(MotechConstants.RELATIONSHIP_TYPE_PARENT_CHILD);
            List<Relationship> parentRelations
                    = personService.getRelationships(null, child, parentChildType);
            if (!parentRelations.isEmpty()) {
                logMultipleParentRelationships(child, parentRelations);
                return parentRelations.get(0);
            }
            return null;
        }

        public void setContextService(ContextService contextService) {
            this.contextService = contextService;
        }

        private PersonService personService() {
            PersonService personService = contextService.getPersonService();
            return personService;
        }

        private void logMultipleParentRelationships(Person child, List<Relationship> parentRelations) {
            if (parentRelations.size() > 1) {
                log.warn("Multiple parent relationships found for id: "
                        + child.getPersonId());
            }
        }
    }