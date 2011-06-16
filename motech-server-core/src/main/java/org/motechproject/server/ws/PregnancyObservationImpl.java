package org.motechproject.server.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.server.service.ConceptEnum;
import org.motechproject.server.service.ContextService;
import org.motechproject.server.service.MotechService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.api.PersonService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class PregnancyObservationImpl implements PregnancyObservation {

    private static Log log = LogFactory.getLog(PregnancyObservationImpl.class);

    @Autowired
    private ContextService contextService;

    @Autowired
    private ObservationBean observationBean;


    public Date getActivePregnancyDueDate(Integer patientId) {
        Obs pregnancy = getActivePregnancy(patientId);
        Obs dueDateObs = getActivePregnancyDueDateObs(patientId, pregnancy);
        if (dueDateObs != null) {
            return dueDateObs.getValueDatetime();
        }
        return null;
    }

    public Obs getActivePregnancy(Integer patientId) {
        List<Obs> pregnancies = motechService().getActivePregnancies(patientId,
                concept(ConceptEnum.CONCEPT_PREGNANCY), concept(ConceptEnum.CONCEPT_PREGNANCY_STATUS));
        if (pregnancies.isEmpty()) {
            return null;
        } else if (pregnancies.size() > 1) {
            log.warn("More than 1 active pregnancy found for patient: "
                    + patientId);
        }
        return pregnancies.get(0);
    }

    private MotechService motechService() {
        return contextService.getMotechService();
    }

    private PersonService personService() {
        return contextService.getPersonService();
    }

    private Concept concept(ConceptEnum conceptEnum) {
        return conceptEnum.getConcept(contextService.getConceptService());
    }

    public Obs getActivePregnancyDueDateObs(Integer patientId, Obs pregnancy) {
        if (pregnancy != null) {
            Integer pregnancyObsId = pregnancy.getObsId();
            List<Obs> dueDateObsList = observationBean.getMatchingObs(personService()
                    .getPerson(patientId), concept(ConceptEnum.CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT), null,
                    pregnancyObsId, null, null);
            if (dueDateObsList.size() > 0) {
                return dueDateObsList.get(0);
            }
        }
        return null;
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }

    public void setObservationBean(ObservationBean observationBean) {
        this.observationBean = observationBean;
    }
}
