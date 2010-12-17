/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010 The Trustees of Columbia University in the City of
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

package org.motechproject.server.svc;

import java.util.Date;
import java.util.List;

import org.motechproject.server.annotation.RunWithPrivileges;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.Facility;
import org.motechproject.ws.ContactNumberType;
import org.motechproject.ws.DayOfWeek;
import org.motechproject.ws.MediaType;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.Person;
import org.openmrs.PersonAttributeType;
import org.openmrs.Relationship;
import org.openmrs.User;
import org.openmrs.util.OpenmrsConstants;

/**
 * An interface providing methods commonly required of openmrs by motech. This
 * isn't intended to provide high level operations meaningful to motech. It is
 * intended to provide a testable interface for simpler operations. It is
 * expected that this service will be used by other higher level services that
 * provide higher level operations.
 * 
 * @author batkinson
 * 
 */
public interface OpenmrsBean {

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_PATIENTS,
			OpenmrsConstants.PRIV_VIEW_IDENTIFIER_TYPES })
	public Patient getPatientByMotechId(String motechId);

	@RunWithPrivileges( { OpenmrsConstants.PRIV_VIEW_USERS,
			OpenmrsConstants.PRIV_VIEW_PERSON_ATTRIBUTE_TYPES })
	public User getStaffBySystemId(String systemId);

	public Patient getPatientById(Integer patientId);

	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, String nhisNumber, String motechId);

	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer communityId, String phoneNumber, String nhisNumber,
			String motechId);

	public PatientIdentifierType getMotechPatientIdType();

	public PatientIdentifierType getStaffPatientIdType();

	public PatientIdentifierType getFacilityPatientIdType();

	public PatientIdentifierType getCommunityPatientIdType();

	public PersonAttributeType getPhoneNumberAttributeType();

	public PersonAttributeType getNHISNumberAttributeType();

	public PersonAttributeType getNHISExpirationDateAttributeType();

	public PersonAttributeType getPhoneTypeAttributeType();

	public PersonAttributeType getLanguageAttributeType();

	public PersonAttributeType getMediaTypeAttributeType();

	public PersonAttributeType getDeliveryTimeAttributeType();

	public PersonAttributeType getInsuredAttributeType();

	public PersonAttributeType getHowLearnedAttributeType();

	public PersonAttributeType getInterestReasonAttributeType();

	public PersonAttributeType getDeliveryDayAttributeType();

	public Location getGhanaLocation();

	public EncounterType getANCVisitEncounterType();

	public EncounterType getPregnancyRegistrationVisitEncounterType();

	public EncounterType getPregnancyTerminationVisitEncounterType();

	public EncounterType getPregnancyDeliveryVisitEncounterType();

	public EncounterType getPregnancyDeliveryNotificationEncounterType();

	public EncounterType getOutpatientVisitEncounterType();

	public EncounterType getTTVisitEncounterType();

	public EncounterType getCWCVisitEncounterType();

	public EncounterType getMotherPNCVisitEncounterType();

	public EncounterType getChildPNCVisitEncounterType();

	public EncounterType getANCRegistrationEncounterType();

	public EncounterType getCWCRegistrationEncounterType();

	public EncounterType getBirthEncounterType();

	public EncounterType getPatientRegistrationEncounterType();

	public EncounterType getPatientHistoryEncounterType();

	public Concept getImmunizationsOrderedConcept();

	public Concept getTetanusDoseConcept();

	public Concept getIPTDoseConcept();

	public Concept getHIVTestResultConcept();

	public Concept getTerminationTypeConcept();

	public Concept getTerminationComplicationConcept();

	public Concept getVitaminAConcept();

	public Concept getITNConcept();

	public Concept getVisitNumberConcept();

	public Concept getPregnancyConcept();

	public Concept getPregnancyStatusConcept();

	public Concept getDueDateConcept();

	public Concept getParityConcept();

	public Concept getGravidaConcept();

	public Concept getDueDateConfirmedConcept();

	public Concept getEnrollmentReferenceDateConcept();

	public Concept getDeathCauseConcept();

	public Concept getBCGConcept();

	public Concept getOPVDoseConcept();

	public Concept getPentaDoseConcept();

	public Concept getYellowFeverConcept();

	public Concept getCSMConcept();

	public Concept getMeaslesConcept();

	public Concept getIPTiDoseConcept();

	public Concept getSerialNumberConcept();

	public Concept getNewCaseConcept();

	public Concept getReferredConcept();

	public Concept getPrimaryDiagnosisConcept();

	public Concept getSecondaryDiagnosisConcept();

	public Concept getDeliveryModeConcept();

	public Concept getDeliveryLocationConcept();

	public Concept getDeliveredByConcept();

	public Concept getDeliveryOutcomeConcept();

	public Concept getBirthOutcomeConcept();

	public Concept getMalariaRDTConcept();

	public Concept getVDRLTreatmentConcept();

	public Concept getUrineProteinTestConcept();

	public Concept getUrineGlucoseTestConcept();

	public Concept getFetalHeartRateConcept();

	public Concept getFundalHeightConcept();

	public Concept getVVFRepairConcept();

	public Concept getDewormerConcept();

	public Concept getPMTCTConcept();

	public Concept getPMTCTTreatmentConcept();

	public Concept getACTTreatmentConcept();

	public Concept getPreHIVTestCounselingConcept();

	public Concept getPostHIVTestCounselingConcept();

	public Concept getDeliveryComplicationConcept();

	public Concept getPostAbortionFPCounselingConcept();

	public Concept getPostAbortionFPAcceptedConcept();

	public Concept getIPTReactionConcept();

	public Concept getLochiaColourConcept();

	public Concept getLochiaExcessConcept();

	public Concept getLochiaFoulConcept();

	public Concept getMUACConcept();

	public Concept getMaternalDeathConcept();

	public Concept getTerminationProcedureConcept();

	public Concept getCordConditionConcept();

	public Concept getConditionBabyConcept();

	public Concept getNextANCDateConcept();

	public Concept getMaleInvolvementConcept();

	public Concept getCommunityConcept();

	public Concept getHouseConcept();

	public Concept getANCPNCLocationConcept();

	public Concept getCWCLocationConcept();

	public Concept getCommentsConcept();

	public Concept getVDRLConcept();

	public Concept getRespiratoryRateConcept();

	public Concept getDiastolicBloodPressureConcept();

	public Concept getSystolicBloodPressureConcept();

	public Concept getHemoglobinConcept();

	public Concept getWeightConcept();

	public Concept getHeightConcept();

	public Concept getTemperatureConcept();

	public Concept getReactiveConcept();

	public Concept getNonReactiveConcept();

	public Concept getPositiveConcept();

	public Concept getNegativeConcept();

	public Concept getTraceConcept();

	public Concept getANCRegistrationNumberConcept();

	public Concept getCWCRegistrationNumberConcept();

	public Concept getInsuredConcept();

	public String getTroubledPhoneProperty();

	public String getPatientCareRemindersProperty();

	public String getPatientDayOfWeekProperty();

	public String getPatientTimeOfDayProperty();

	public String getMaxQueryResultsProperty();

	public List<Obs> getObs(Patient patient, String conceptName,
			String valueConceptName, Date minDate);

	public List<Encounter> getEncounters(Patient patient,
			String encounterTypeName, Date minDate);

	public int getNumberOfObs(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsCreationDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastObsDate(Integer personId, String conceptName,
			String conceptValue);

	public Date getLastDoseObsDate(Integer personId, String conceptName,
			Integer doseNumber);

	public Date getLastObsValue(Integer personId, String conceptName);

	public int getNumberOfObs(Person person, Concept concept, Concept value);

	public Date getLastObsCreationDate(Person person, Concept concept,
			Concept value);

	public Date getLastObsDate(Person person, Concept concept, Concept value);

	public Date getLastObsValue(Person person, Concept concept);

	public Date getObsValue(Integer obsId);

	public Integer getObsId(Integer personId, String conceptName,
			String conceptValue, Date earliest, Date latest);

	public Integer getObsId(Integer personId, String conceptName,
			Integer doseNumber, Date earliest, Date latest);

	public Integer getEncounterId(Integer patientId, String encounterType,
			Date earliest, Date latest);

	public List<Encounter> getRecentDeliveries(Facility facility);

	public Date getCurrentDeliveryDate(Patient patient);

	public List<Obs> getUpcomingPregnanciesDueDate(Facility facility);

	public List<Obs> getOverduePregnanciesDueDate(Facility facility);

	public Obs getActivePregnancy(Integer patientId);

	public Date getPatientBirthDate(Integer patientId);

	public Date getLastDoseObsDateInActivePregnancy(Integer patientId,
			String conceptName, Integer doseNumber);

	public Obs getActivePregnancyDueDateObs(Integer patientId, Obs pregnancy);

	public Date getActivePregnancyDueDate(Integer patientId);

	public Date getLastPregnancyEndDate(Integer patientId);

	public String getPersonPhoneNumber(Person person);

	public String getPersonLanguageCode(Person person);

	public ContactNumberType getPersonPhoneType(Person person);

	public MediaType getPersonMediaType(Person person);

	public Integer getMaxPhoneNumberFailures();

	public Integer getMaxPatientCareReminders();

	public DayOfWeek getPersonMessageDayOfWeek(Person person);

	public Date getPersonMessageTimeOfDay(Person person);

	public DayOfWeek getDefaultPatientDayOfWeek();

	public Date getDefaultPatientTimeOfDay();

	public Integer getMaxQueryResults();

	public Integer getMotherMotechId(Patient patient);

	public Relationship getMotherRelationship(Patient patient);

	public Integer getMotechId(Integer patientId);

	public Community getCommunityByPatient(Patient patient);

}
