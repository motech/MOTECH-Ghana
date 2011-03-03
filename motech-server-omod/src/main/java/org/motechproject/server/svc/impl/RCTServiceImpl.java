package org.motechproject.server.svc.impl;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.db.RctDAO;
import org.motechproject.server.model.rct.PhoneOwnershipType;
import org.motechproject.server.model.rct.RCTPatient;
import org.motechproject.server.model.rct.Stratum;
import org.motechproject.server.svc.RCTService;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.Patient;
import org.motechproject.ws.rct.ControlGroup;
import org.motechproject.ws.rct.PregnancyTrimester;
import org.motechproject.ws.rct.RCTRegistrationConfirmation;
import org.openmrs.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public class RCTServiceImpl implements RCTService {

    private RctDAO dao;

    @Transactional
    public RCTRegistrationConfirmation register(Patient patient, User staff, Facility facility) {
        PregnancyTrimester trimester = pregnancyTrimester(patient);
        ContactNumberType contactNumberType = patient.getContactNumberType();
        Stratum stratum = stratumFor(facility, PhoneOwnershipType.mapTo(contactNumberType), trimester);
        ControlGroup group = stratum.groupAssigned();
        enrollPatientForRCT(patient.getMotechId(),stratum, group, staff);
        determineNextAssignment(stratum);
        return new RCTRegistrationConfirmation(patient, group);
    }

    private void determineNextAssignment(Stratum stratum) {
        stratum.determineNextAssignment();
        dao.updateStratum(stratum);
    }

    private void enrollPatientForRCT(String motechId, Stratum stratum, ControlGroup controlGroup, User enrolledBy) {
        dao.saveRCTPatient(new RCTPatient(motechId,stratum,controlGroup,enrolledBy));
    }

    private Stratum stratumFor(Facility facility, PhoneOwnershipType phoneOwnershipType, PregnancyTrimester trimester) {
        return dao.stratumWith(facility,phoneOwnershipType,trimester);
    }

    private PregnancyTrimester pregnancyTrimester(Patient patient) {
        DateTime deliveryDate = new DateTime(patient.getEstimateDueDate().getTime());
        DateTime today = new DateTime(new Date().getTime());
        Months months = Months.monthsBetween(today,deliveryDate);
        int monthsDiff = Math.abs(months.getMonths());

        if(monthsDiff <= 3 )return PregnancyTrimester.THIRD;
        if(monthsDiff <= 6) return PregnancyTrimester.SECOND;

        return PregnancyTrimester.FIRST;
    }

    public void setDao(RctDAO dao) {
        this.dao = dao;
    }
}
