package org.motechproject.server.svc.impl;

import org.easymock.Capture;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.ConceptService;
import org.openmrs.api.EncounterService;

import java.util.Date;
import java.util.Locale;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;
import static org.motechproject.server.service.ConceptEnum.*;

public class RegistrarBeanRecordCWCChildVisitImmunizationsTest {


    @Test
    public void shouldCreateObservationsForImmunizationsGiven() {

        RegistrarBeanImpl registrarBean = new RegistrarBeanImpl();

        ConceptService conceptService = createMock(ConceptService.class);
        EncounterService encounterService = createMock(EncounterService.class);

        registrarBean.setConceptService(conceptService);
        registrarBean.setEncounterService(encounterService);

        String immunizationsGiven = "bcg dewormer csm measles yellowfever vitamina";

        Capture<Encounter> encounterCapture = new Capture<Encounter>();

        expect(encounterService.getEncounterType("CWCVISIT")).andReturn(new EncounterType());

        expect(conceptService.getConcept(CONCEPT_SERIAL_NUMBER.conceptName()))
                .andReturn(conceptWithName(CONCEPT_SERIAL_NUMBER.conceptName()));
        expect(conceptService.getConcept(CONCEPT_IMMUNIZATIONS_ORDERED.conceptName()))
                .andReturn(conceptWithName(CONCEPT_IMMUNIZATIONS_ORDERED.conceptName())).times(5);
        expect(conceptService.getConcept(CONCEPT_BCG_VACCINATION.conceptName()))
                .andReturn(conceptWithName(CONCEPT_BCG_VACCINATION.conceptName()));
        expect(conceptService.getConcept(CONCEPT_YELLOW_FEVER_VACCINATION.conceptName()))
                .andReturn(conceptWithName(CONCEPT_YELLOW_FEVER_VACCINATION.conceptName()));
        expect(conceptService.getConcept(CONCEPT_MEASLES_VACCINATION.conceptName()))
                .andReturn(conceptWithName(CONCEPT_MEASLES_VACCINATION.conceptName()));
        expect(conceptService.getConcept(CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION.conceptName()))
                .andReturn(conceptWithName(CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION.conceptName()));
        expect(conceptService.getConcept(CONCEPT_VITAMIN_A.conceptName()))
                .andReturn(conceptWithName(CONCEPT_VITAMIN_A.conceptName()));
        expect(conceptService.getConcept(CONCEPT_DEWORMER.conceptName()))
                .andReturn(conceptWithName(CONCEPT_DEWORMER.conceptName()));
        expect(conceptService.getConcept(CONCEPT_MALE_INVOLVEMENT.conceptName()))
                .andReturn(conceptWithName(CONCEPT_MALE_INVOLVEMENT.conceptName()));

        expect(encounterService.saveEncounter(capture(encounterCapture))).andReturn(new Encounter(1));

        replay(conceptService, encounterService);

        registrarBean.recordChildCWCVisit(null, new Location(1), new Date(), new Patient(1), "11",
                null, null, null, immunizationsGiven, null, null, null, null, null, null, false, null);

        verify(conceptService, encounterService);
        Encounter savedEncounter = encounterCapture.getValue();

        assertNotNull(savedEncounter);

        Set<Obs> allObs = savedEncounter.getAllObs();
        assertEquals(8, allObs.size());
        assertTrue(obsExistsWith(CONCEPT_BCG_VACCINATION.conceptName(), allObs));
        assertTrue(obsExistsWith(CONCEPT_YELLOW_FEVER_VACCINATION.conceptName(), allObs));
        assertTrue(obsExistsWith(CONCEPT_VITAMIN_A.conceptName(), allObs));
        assertTrue(obsExistsWith(CONCEPT_MEASLES_VACCINATION.conceptName(), allObs));
        assertTrue(obsExistsWith(CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION.conceptName(), allObs));
        assertTrue(obsExistsWith(CONCEPT_DEWORMER.conceptName(),Boolean.TRUE, allObs));
        assertFalse(obsExistsWith(CONCEPT_ORAL_POLIO_VACCINATION_DOSE.conceptName(),1, allObs));
    }

    private Boolean obsExistsWith(String expectedValueConceptName, Set<Obs> allObs) {
        for (Obs obs : allObs) {
            Concept valueConcept = obs.getValueCoded();
            if(valueConcept != null && expectedValueConceptName.equals(valueConcept.getName().getName())){
                return true;
            }
        }
        return false;
    }

    private Boolean obsExistsWith(String conceptName, Object value, Set<Obs> allObs) {
        for (Obs obs : allObs) {
            if(conceptName.equals(obs.getConcept().getName().getName())){
                return value.equals(obs.getValueAsBoolean());
            }
        }
        return false;
    }

    private Concept conceptWithName(String name) {
        Concept concept = new Concept();
        concept.addName(new ConceptName(name, Locale.getDefault()));
        return concept;
    }

}
