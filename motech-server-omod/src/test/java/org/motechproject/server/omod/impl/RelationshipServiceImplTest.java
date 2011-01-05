package org.motechproject.server.omod.impl;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.server.omod.ContextService;
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;

import java.util.ArrayList;
import java.util.List;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RelationshipServiceImplTest {


    private ContextService contextService;
    private PersonService personService;
    private RelationshipServiceImpl service;

    RelationshipType relationshipType;

    private static final String PARENT_CHILD = "Parent/Child";

    @Before
    public void setUp() {
        contextService = createMock(ContextService.class);
        personService = createMock(PersonService.class);

        relationshipType = new RelationshipType(1);
        relationshipType.setName(PARENT_CHILD);
        relationshipType.setaIsToB("Parent");
        relationshipType.setbIsToA("Child");

        service = new RelationshipServiceImpl();
        service.setContextService(contextService);
    }

    @Test
    public void shouldCreateMotherRelationshipIfNoneExists() {
        Person mother = new Person(1);
        Person child = new Person(2);

        List<Relationship> emptyRelationships = new ArrayList<Relationship>();

        Relationship relationship = new Relationship(mother, child, relationshipType);
        relationship.setId(1);

        expect(contextService.getPersonService()).andReturn(personService).anyTimes();
        expect(personService.getRelationshipTypeByName(PARENT_CHILD)).andReturn(relationshipType).times(2);
        expect(personService.getRelationships(null, child, relationshipType)).andReturn(emptyRelationships);
        expect(personService.saveRelationship(EasyMock.<Relationship>anyObject())).andReturn(relationship);

        replay(contextService, personService);

        Relationship motherChildRelationship = service.saveOrUpdateMotherRelationship(mother, child, true);

        verify(contextService, personService);

        assertEquals(motherChildRelationship.getPersonA().getId(), relationship.getPersonA().getId());
        assertEquals(motherChildRelationship.getPersonB().getId(), relationship.getPersonB().getId());
        assertEquals(motherChildRelationship.getRelationshipType(), relationship.getRelationshipType());

    }

    @Test
    public void shouldEditExistingMotherChildRelationship() {
        Person mother = new Person(1);
        Person oldMother = new Person(4);
        Person child = new Person(2);

        List<Relationship> oldRelationships = new ArrayList<Relationship>();
        Relationship oldRelationship = new Relationship(oldMother, child, relationshipType);
        oldRelationship.setId(1);
        oldRelationships.add(oldRelationship);


        Relationship expectedRelationship = new Relationship(mother, child, relationshipType);

        expect(contextService.getPersonService()).andReturn(personService).anyTimes();
        expect(personService.getRelationshipTypeByName(eq(PARENT_CHILD))).andReturn(relationshipType).atLeastOnce();
        expect(personService.getRelationships(null, child, relationshipType)).andReturn(oldRelationships).times(2);
        expect(personService.saveRelationship(EasyMock.<Relationship>anyObject())).andReturn(oldRelationship);

        replay(contextService, personService);

        Relationship motherChildRelationship = service.saveOrUpdateMotherRelationship(mother, child, true);

        verify(contextService, personService);

        assertEquals(oldRelationship,motherChildRelationship);
        assertEquals(expectedRelationship.getPersonA().getId(), oldRelationship.getPersonA().getId());
        assertEquals(expectedRelationship.getPersonB().getId(), oldRelationship.getPersonB().getId());
        assertEquals(expectedRelationship.getRelationshipType(), oldRelationship.getRelationshipType());

    }

    @Test
    public void shouldVoidMotherChildRelationship() {
        Person oldMother = new Person(4);
        Person child = new Person(2);

        List<Relationship> oldRelationships = new ArrayList<Relationship>();
        Relationship oldRelationship = new Relationship(oldMother, child, relationshipType);
        oldRelationship.setId(1);
        oldRelationships.add(oldRelationship);


        Relationship voidRelationship = oldRelationship.copy();
        voidRelationship.setVoided(true);

        expect(contextService.getPersonService()).andReturn(personService).anyTimes();
        expect(personService.getRelationshipTypeByName(eq(PARENT_CHILD))).andReturn(relationshipType);
        expect(personService.getRelationships(null, child, relationshipType)).andReturn(oldRelationships);
        expect(personService.voidRelationship(EasyMock.<Relationship>anyObject(),EasyMock.<String>anyObject())).andReturn(voidRelationship);
        expect(personService.saveRelationship(EasyMock.<Relationship>anyObject())).andReturn(voidRelationship);

        replay(contextService, personService);

        Relationship motherChildRelationship = service.saveOrUpdateMotherRelationship(null, child, true);

        verify(contextService, personService);

        assertTrue(motherChildRelationship.isVoided());

    }

    @Test
    public void shouldNotVoidMotherChildRelationship() {
        Person oldMother = new Person(4);
        Person child = new Person(2);

        List<Relationship> oldRelationships = new ArrayList<Relationship>();
        Relationship oldRelationship = new Relationship(oldMother, child, relationshipType);
        oldRelationship.setId(1);
        oldRelationships.add(oldRelationship);


        Relationship voidRelationship = oldRelationship.copy();
        voidRelationship.setVoided(true);

        expect(contextService.getPersonService()).andReturn(personService).anyTimes();
        expect(personService.getRelationshipTypeByName(eq(PARENT_CHILD))).andReturn(relationshipType);
        expect(personService.getRelationships(null, child, relationshipType)).andReturn(oldRelationships);

        replay(contextService, personService);

        Relationship motherChildRelationship = service.saveOrUpdateMotherRelationship(null, child, false);

        verify(contextService, personService);

        assertFalse(motherChildRelationship.isVoided());
    }


}

