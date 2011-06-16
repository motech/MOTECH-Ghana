/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.motechproject.server.omod.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.service.ContextService;
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

    public Relationship saveOrUpdateMotherRelationship(Person mother, Person child, Boolean canVoidRelationship) {

        Relationship motherRelationship = getMotherRelationship(child);

        boolean motherRelationshipCreated = mother != null && motherRelationship == null;
        boolean motherRelationshipEdited = mother != null && motherRelationship != null;
        boolean motherRelationshipRemoved = mother == null && motherRelationship != null;

        if (motherRelationshipCreated)
            motherRelationship = createMotherChildRelationship(mother, child);
        else if (motherRelationshipEdited)
            motherRelationship = updateMotherRelationship(mother, child);
        else if (canVoidRelationship && motherRelationshipRemoved)
            motherRelationship = voidRelationship(motherRelationship);


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
        relationship = personService.voidRelationship(relationship, "Removed in web or mobile form");
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
        return contextService.getPersonService();
    }

    private void logMultipleParentRelationships(Person child, List<Relationship> parentRelations) {
        if (parentRelations.size() > 1) {
            log.warn("Multiple parent relationships found for id: "
                    + child.getPersonId());
        }
    }
}