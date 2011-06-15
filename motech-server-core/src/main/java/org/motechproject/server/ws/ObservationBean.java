package org.motechproject.server.ws;

import org.motechproject.server.omod.ContextService;
import org.openmrs.Concept;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.api.ObsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObservationBean {

    @Autowired
    private ContextService contextService;

    private ObsService obsService() {
        return contextService.getObsService();
    }

    public List<Obs> getMatchingObs(Person person, Concept question, Concept answer, Integer obsGroupId, Date from, Date to) {
        List<Concept> questions = null;
        if (question != null) {
            questions = new ArrayList<Concept>();
            questions.add(question);
        }

        List<Concept> answers = null;
        if (answer != null) {
            answers = new ArrayList<Concept>();
            answers.add(answer);
        }

        List<Person> whom = new ArrayList<Person>();
        whom.add(person);

        // patients, encounters, questions, answers, persontype, locations,
        // sort, max returned, group id, from date, to date, include voided
        List<Obs> obsList = obsService().getObservations(whom, null, questions,
                answers, null, null, null, null, obsGroupId, from, to, false);

        return obsList;
    }

    public void setContextService(ContextService contextService) {
        this.contextService = contextService;
    }
}
