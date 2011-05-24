package org.motechproject.server.omod.builder;

import org.motechproject.server.model.Community;
import org.motechproject.server.omod.IdentifierGenerator;
import org.motechproject.server.omod.MotechService;
import org.motechproject.server.omod.PersonAttributeTypeEnum;
import org.motechproject.server.util.GenderTypeConverter;
import org.motechproject.server.util.MotechConstants;
import org.motechproject.ws.Gender;
import org.motechproject.ws.RegistrantType;
import org.openmrs.*;
import org.openmrs.api.LocationService;
import org.openmrs.api.PatientService;
import org.openmrs.api.PersonService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.motechproject.server.omod.PatientIdentifierTypeEnum.PATIENT_IDENTIFIER_MOTECH_ID;


public class PatientBuilder {
    private String lastName;
    private String address1;
    private String firstName;
    private String middleName;
    private String preferredName;
    private Gender gender;
    private Date birthDate;
    private boolean birthDateEstimated;
    private final PersonService personService;
    private final IdentifierGenerator idGenerator;
    private final RegistrantType registrantType;
    private final MotechService motechService;
    private final PatientService patientService;
    private final LocationService locationService;
    List<PersonAttribute> personAttributes;
    protected Patient parent;
    private Community community;
    private Integer motechId;

    public PatientBuilder(PersonService personService, MotechService motechService, IdentifierGenerator idGenerator, RegistrantType registrantType, PatientService patientService, LocationService locationService) {
        this.personService = personService;
        this.idGenerator = idGenerator;
        this.registrantType = registrantType;
        this.motechService = motechService;
        this.patientService = patientService;
        this.locationService = locationService;
        personAttributes = new ArrayList<PersonAttribute>();
    }

    public PatientBuilder setMotechId(Integer motechId) {
        this.motechId = motechId;
        return this;
    }

    public PatientBuilder setName(String fistName, String middleName, String lastName) {
        this.firstName = fistName;
        this.middleName = middleName;
        this.lastName = lastName;
        return this;
    }

    public PatientBuilder setAddress1(String address1) {
        this.address1 = address1;
        return this;
    }

    public PatientBuilder setPreferredName(String preferredName) {
        this.preferredName = preferredName;
        return this;
    }

    public PatientBuilder setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public PatientBuilder setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public PatientBuilder setBirthDateEstimated(boolean birthDateEstimated) {
        this.birthDateEstimated = birthDateEstimated;
        return this;
    }

    public PatientBuilder setParent(Patient parent) {
        this.parent = parent;
        return this;
    }

    public PatientBuilder addAttribute(PersonAttributeTypeEnum patientAttributeType, Object originalValue, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (originalValue != null) {
            Method valueMethod = originalValue.getClass().getMethod(methodName);
            String stringValue = valueMethod.invoke(originalValue).toString();
            personAttributes.add(new PersonAttribute(patientAttributeType.getAttributeType(personService), stringValue));
        }
        return this;
    }

    public PatientBuilder addAttribute(PersonAttributeTypeEnum patientAttributeType, Object originalValue, Object invocationTarget, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (originalValue != null) {
            Method valueMethod = invocationTarget.getClass().getMethod(methodName,originalValue.getClass());
            String stringValue = valueMethod.invoke(invocationTarget, originalValue).toString();
            personAttributes.add(new PersonAttribute(patientAttributeType.getAttributeType(personService), stringValue));
        }
        return this;
    }

    public PatientBuilder setCommunity(Community community) {
        this.community = community;
        return this;
    }

    public Patient build() {
        if (registrantType == RegistrantType.CHILD_UNDER_FIVE) {
            return buildChild(parent);
        } else {
            return buildPatient();
        }
    }

    protected Patient buildPatient() {
        Patient patient = new Patient();

        setPatientId(patient);
        patient.addName(new PersonName(firstName, middleName, lastName));
        patientAddress(patient);
        preferredName(patient);
        patient.setGender(GenderTypeConverter.toOpenMRSString(gender));
        patient.setBirthdate(birthDate);
        patient.setBirthdateEstimated(birthDateEstimated);

        addCommunityToPatient(patient);
        addAttributesToPatient(patient);
        return patient;
    }

    String getFirstName() {
        return firstName;
    }

    String getMiddleName() {
        return middleName;
    }

    String getLastName() {
        return lastName;
    }

    String getAddress1() {
        return address1;
    }

    MotechService getMothechService() {
        return motechService;
    }

    Community getCommunity() {
        return community;
    }

    Patient getParent() {
        return parent;
    }

    boolean isAttributePresent(String attributeName) {
        for (PersonAttribute attribute : personAttributes) {
            if (attributeName.equalsIgnoreCase(attribute.getAttributeType().getName()))
                return true;
        }
        return false;
    }

    private void addCommunityToPatient(Patient patient) {
        if (community != null) {
            community.add(patient);
        }
    }

    private PatientBuilder setPatientId(Patient patient) {
        String motechIdString = null;
        if (motechId == null) {
            motechIdString = idGenerator.generateMotechId();
        } else {
            motechIdString = motechId.toString();
        }
        patient.addIdentifier(new PatientIdentifier(motechIdString, getPatientIdentifierTypeForMotechId()
                , getGhanaLocation()));
        return this;
    }

    private PatientIdentifierType getPatientIdentifierTypeForMotechId() {
        return PATIENT_IDENTIFIER_MOTECH_ID.getIdentifierType(patientService);
    }


    private Location getGhanaLocation() {
        return locationService.getLocation(MotechConstants.LOCATION_GHANA);
    }

    private Patient buildChild(Patient parent) {
        if (parent == null) {
            return buildPatient();
        } else {
            return new ChildBuilder(personService, motechService, idGenerator, patientService, locationService).build();
        }
    }


    private void addAttributesToPatient(Patient patient) {
        for (PersonAttribute attribute : personAttributes) {
            patient.addAttribute(attribute);
        }
    }

    private void preferredName(Patient patient) {
        if (preferredName != null) {
            PersonName preferredName = new PersonName(this.preferredName, middleName, lastName);
            preferredName.setPreferred(true);
            patient.addName(preferredName);
        }
    }

    private void patientAddress(Patient patient) {
        if (address1 != null) {
            PersonAddress personAddress = new PersonAddress();
            personAddress.setAddress1(address1);
            patient.addAddress(personAddress);
        }
    }
}
