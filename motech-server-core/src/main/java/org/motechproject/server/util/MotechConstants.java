package org.motechproject.server.util;

public final class MotechConstants {

	/*
	 * Values added and used by Motech
	 */
	public static final String PERSON_ATTRIBUTE_PRIMARY_PHONE_NUMBER = "Primary Phone Number";
	public static final String PERSON_ATTRIBUTE_SECONDARY_PHONE_NUMBER = "Secondary Phone Number";
	public static final String PERSON_ATTRIBUTE_NHIS_NUMBER = "NHIS Number";
	public static final String PERSON_ATTRIBUTE_NHIS_EXP_DATE = "NHIS Expiration Date";
	public static final String PERSON_ATTRIBUTE_LANGUAGE_TEXT = "Language Text";
	public static final String PERSON_ATTRIBUTE_LANGUAGE_VOICE = "Language Voice";
	public static final String PERSON_ATTRIBUTE_PRIMARY_PHONE_TYPE = "Primary Phone Type";
	public static final String PERSON_ATTRIBUTE_SECONDARY_PHONE_TYPE = "Secondary Phone Type";
	public static final String PERSON_ATTRIBUTE_MEDIA_TYPE_INFORMATIONAL = "Media Type Informational";
	public static final String PERSON_ATTRIBUTE_MEDIA_TYPE_REMINDER = "Media Type Reminder";
	public static final String PERSON_ATTRIBUTE_DELIVERY_TIME = "Delivery Time";
	public static final String PERSON_ATTRIBUTE_CHPS_ID = "CHPS ID";
	public static final String PERSON_ATTRIBUTE_GHS_ANC_REG_NUMBER = "GHS ANC Registration Number ";
	public static final String PERSON_ATTRIBUTE_GHS_CWC_REG_NUMBER = "GHS CWC Registration Number";
	public static final String PERSON_ATTRIBUTE_GHS_REGISTERED = "Registered with GHS";
	public static final String PERSON_ATTRIBUTE_INSURED = "Insured";
	public static final String PERSON_ATTRIBUTE_HIV_STATUS = "HIV Status";
	public static final String PERSON_ATTRIBUTE_WHO_REGISTERED = "Who registered";
	public static final String PERSON_ATTRIBUTE_RELIGION = "Religion";
	public static final String PERSON_ATTRIBUTE_OCCUPATION = "Occupation";
	public static final String PERSON_ATTRIBUTE_HOW_LEARNED = "How learned of service";
	public static final String PERSON_ATTRIBUTE_WHY_INTERESTED = "Why interested in service";
	public static final String PATIENT_IDENTIFIER_MOTECH_ID = "MoTeCH Id";
	public static final String LOCATION_GHANA = "Ghana";
	public static final String LOCATION_UPPER_EAST = "Upper East";
	public static final String LOCATION_KASSENA_NANKANA = "Kassena-Nankana";
	public static final String LOCATION_KASSENA_NANKANA_WEST = "Kassena-Nankana West";
	public static final String ENCOUNTER_TYPE_ANCVISIT = "ANCVISIT";
	public static final String ENCOUNTER_TYPE_PREGREGVISIT = "PREGREGVISIT";
	public static final String ENCOUNTER_TYPE_PREGTERMVISIT = "PREGTERMVISIT";
	public static final String ENCOUNTER_TYPE_PREGDELVISIT = "PREGDELVISIT";
	public static final String ENCOUNTER_TYPE_GENERALVISIT = "GENERALVISIT";
	public static final String ENCOUNTER_TYPE_TTVISIT = "TTVISIT";
	public static final String ENCOUNTER_TYPE_CWCVISIT = "CWCVISIT";
	public static final String ENCOUNTER_TYPE_PNCMOTHERVISIT = "PNCMOTHERVISIT";
	public static final String ENCOUNTER_TYPE_PNCCHILDVISIT = "PNCCHILDVISIT";
	public static final String CONCEPT_VISIT_NUMBER = "VISIT NUMBER";
	public static final String CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_INFANTS = "INTERMITTENT PREVENTATIVE TREATMENT INFANTS";
	public static final String CONCEPT_ORAL_POLIO_VACCINATION_DOSE = "ORAL POLIO VACCINATION DOSE";
	public static final String CONCEPT_PENTA_VACCINATION_DOSE = "PENTA VACCINATION DOSE";
	public static final String CONCEPT_INSECTICIDE_TREATED_NET_USAGE = "INSECTICIDE TREATED NET USAGE";
	public static final String CONCEPT_CEREBRO_SPINAL_MENINGITIS_VACCINATION = "CEREBRO-SPINAL MENINGITIS VACCINATION";
	public static final String CONCEPT_VITAMIN_A = "VITAMIN A";
	public static final String CONCEPT_DATE_OF_CONFINEMENT_CONFIRMED = "DATE OF CONFINEMENT CONFIRMED";
	public static final String CONCEPT_ENROLLMENT_REFERENCE_DATE = "MESSAGE PROGRAM ENROLLMENT REFERENCE DATE";
	public static final String CONCEPT_TETANUS_TOXOID_DOSE = "TETANUS TOXOID DOSE";
	public static final String CONCEPT_INTERMITTENT_PREVENTATIVE_TREATMENT_DOSE = "INTERMITTENT PREVENTATIVE TREATMENT DOSE";
	public static final String CONCEPT_HIV_STATUS = "HIV STATUS";
	public static final String CONCEPT_TERMINATION_TYPE = "TERMINATION TYPE";
	public static final String CONCEPT_TERMINATION_COMPLICATION = "TERMINATION COMPLICATION";
	public static final String CONCEPT_MATERNAL_CAUSE_OF_DEATH = "MATERNAL CAUSE OF DEATH";
	public static final String CONCEPT_DELIVERY_METHOD = "DELIVERY METHOD";
	public static final String CONCEPT_DELIVERY_LOCATION = "DELIVERY LOCATION";
	public static final String CONCEPT_DELIVERED_BY = "DELIVERED BY";
	public static final String CONCEPT_DELIVERY_OUTCOME = "DELIVERY OUTCOME";
	public static final String CONCEPT_BIRTH_OUTCOME = "BIRTH OUTCOME";
	public static final String CONCEPT_CAUSE_OF_DEATH = "CAUSE OF DEATH";
	public static final String CONCEPT_SERIAL_NUMBER = "SERIAL NUMBER";
	public static final String CONCEPT_NEW_CASE = "NEW CASE";
	public static final String CONCEPT_REFERRAL = "REFERRAL";
	public static final String CONCEPT_PRIMARY_DIAGNOSIS = "PRIMARY DIAGNOSIS ";
	public static final String CONCEPT_SECONDARY_DIAGNOSIS = "SECONDARY DIAGNOSIS";
	public static final String CONCEPT_MALARIA_RAPID_TEST = "MALARIA RAPID TEST";
	public static final String CONCEPT_VDRL_TREATMENT = "VDRL TREATMENT";
	public static final String CONCEPT_URINE_PROTEIN_TEST = "URINE PROTEIN TEST";
	public static final String CONCEPT_URINE_GLUCOSE_TEST = "URINE GLUCOSE TEST";
	public static final String CONCEPT_FETAL_HEART_RATE = "FETAL HEART RATE";
	public static final String CONCEPT_FUNDAL_HEIGHT = "FUNDAL HEIGHT";
	public static final String CONCEPT_VVF_REPAIR = "VVF REPAIR";
	public static final String CONCEPT_DEWORMER = "DEWORMER";
	public static final String CONCEPT_PMTCT = "PMTCT";
	public static final String CONCEPT_PMTCT_TREATMENT = "PMTCT TREATMENT";
	public static final String CONCEPT_ACT_TREATMENT = "ACT TREATMENT";
	public static final String CONCEPT_HIV_PRE_TEST_COUNSELING = "HIV PRE-TEST COUNSELING";
	public static final String CONCEPT_HIV_POST_TEST_COUNSELING = "HIV POST-TEST COUNSELING";
	public static final String CONCEPT_DELIVERY_COMPLICATION = "DELIVERY COMPLICATION";
	public static final String CONCEPT_POST_ABORTION_FP_COUNSELING = "POST-ABORTION FP COUNSELING";
	public static final String CONCEPT_POST_ABORTION_FP_ACCEPTED = "POST-ABORTION FP ACCEPTED";
	public static final String CONCEPT_IPT_REACTION = "IPT REACTION";
	public static final String CONCEPT_LOCHIA_COLOUR = "LOCHIA COLOUR";
	public static final String CONCEPT_LOCHIA_EXCESS_AMOUNT = "LOCHIA EXCESS AMOUNT";
	public static final String CONCEPT_MIDDLE_UPPER_ARM_CIRCUMFERENCE = "MIDDLE UPPER ARM CIRCUMFERENCE";
	public static final String CONCEPT_MATERNAL_DEATH = "MATERNAL DEATH";
	public static final String CONCEPT_TERMINATION_PROCEDURE = "PREGNANCY, TERMINATION PROCEDURE";
	public static final String CONCEPT_CORD_CONDITION = "CORD CONDITION ";
	public static final String CONCEPT_CONDITION_OF_BABY = "CONDITION OF BABY";
	public static final String CONCEPT_NEXT_ANC_DATE = "NEXT ANC DATE";
	public static final String CONCEPT_MALE_INVOLVEMENT = "MALE INVOLVEMENT";
	public static final String CONCEPT_COMMUNITY = "COMMUNITY";
	public static final String CONCEPT_HOUSE = "HOUSE";
	public static final String CONCEPT_LOCATION = "LOCATION";
	public static final String CONCEPT_COMMENTS = "COMMENTS";
	public static final String TASK_PROPERTY_SEND_IMMEDIATE = "sendImmediate";
	public static final String TASK_PROPERTY_TIME_OFFSET = "timeOffset";
	public static final String TASK_DAILY_NOTIFICATION = "Daily Notification Task";
	public static final String TASK_IMMEDIATE_NOTIFICATION = "Immediate Notification Task";
	public static final String TASK_MESSAGEPROGRAM_UPDATE = "MessageProgram Update Task";
	public static final String TASK_DAILY_NURSE_CARE_MESSAGING = "Daily Nurse Care Messaging Task";
	public static final String TASK_WEEKLY_NURSE_CARE_MESSAGING = "Weekly Nurse Care Messaging Task";
	public static final String TASK_PROPERTY_DELIVERY_TIME_OFFSET = "deliveryTimeOffset";
	public static final String TASK_PROPERTY_SEND_UPCOMING = "sendUpcoming";
	public static final String TASK_PROPERTY_CARE_GROUPS = "careGroups";
	public static final String TASK_PROPERTY_CARE_GROUPS_DELIMITER = ",";
	public static final String GLOBAL_PROPERTY_TROUBLED_PHONE = "motechmodule.troubled_phone_failures";
	public static final String GLOBAL_PROPERTY_CARE_REMINDERS = "motechmodule.patient_care_reminders";

	/*
	 * Values already existing in OpenMRS, used by Motech
	 */
	public static final String GENDER_MALE_OPENMRS = "M";
	public static final String GENDER_FEMALE_OPENMRS = "F";
	public static final String PERSON_ATTRIBUTE_HEALTH_CENTER = "Health Center";
	public static final String CONCEPT_CLASS_DRUG = "Drug";
	public static final String CONCEPT_CLASS_MISC = "Misc";
	public static final String CONCEPT_CLASS_QUESTION = "Question";
	public static final String CONCEPT_CLASS_TEST = "Test";
	public static final String CONCEPT_CLASS_FINDING = "Finding";
	public static final String CONCEPT_CLASS_DIAGNOSIS = "Diagnosis";
	public static final String CONCEPT_CLASS_PROCEDURE = "Procedure";
	public static final String CONCEPT_DATATYPE_NUMERIC = "Numeric";
	public static final String CONCEPT_DATATYPE_N_A = "N/A";
	public static final String CONCEPT_DATATYPE_BOOLEAN = "Boolean";
	public static final String CONCEPT_DATATYPE_TEXT = "Text";
	public static final String CONCEPT_DATATYPE_DATETIME = "Datetime";
	public static final String CONCEPT_DATATYPE_CODED = "Coded";
	public static final String CONCEPT_YELLOW_FEVER_VACCINATION = "YELLOW FEVER VACCINATION";
	public static final String CONCEPT_IMMUNIZATIONS_ORDERED = "IMMUNIZATIONS ORDERED";
	public static final String CONCEPT_PREGNANCY = "PREGNANCY";
	public static final String CONCEPT_PREGNANCY_STATUS = "PREGNANCY STATUS";
	public static final String CONCEPT_ESTIMATED_DATE_OF_CONFINEMENT = "ESTIMATED DATE OF CONFINEMENT";
	public static final String CONCEPT_GRAVIDA = "GRAVIDA";
	public static final String CONCEPT_PARITY = "PARITY";
	public static final String CONCEPT_BCG_VACCINATION = "BACILLE CAMILE-GUERIN VACCINATION";
	public static final String CONCEPT_MEASLES_VACCINATION = "MEASLES VACCINATION";
	public static final String CONCEPT_VDRL = "VDRL";
	public static final String CONCEPT_RESPIRATORY_RATE = "RESPIRATORY RATE";
	public static final String CONCEPT_DIASTOLIC_BLOOD_PRESSURE = "DIASTOLIC BLOOD PRESSURE";
	public static final String CONCEPT_SYSTOLIC_BLOOD_PRESSURE = "SYSTOLIC BLOOD PRESSURE";
	public static final String CONCEPT_HEMOGLOBIN = "HEMOGLOBIN";
	public static final String CONCEPT_WEIGHT = "WEIGHT (KG)";
	public static final String CONCEPT_HEIGHT = "HEIGHT (CM)";
	public static final String CONCEPT_TEMPERATURE = "TEMPERATURE (C)";
	public static final String CONCEPT_REACTIVE = "REACTIVE";
	public static final String CONCEPT_NON_REACTIVE = "NON-REACTIVE";
	public static final String CONCEPT_POSITIVE = "POSITIVE";
	public static final String CONCEPT_NEGATIVE = "NEGATIVE";
	public static final String RELATIONSHIP_TYPE_PARENT_CHILD = "Parent/Child";
}
