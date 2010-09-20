package org.motechproject.server.model.db.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.motechproject.server.messaging.MessageDefDate;
import org.motechproject.server.model.Blackout;
import org.motechproject.server.model.Community;
import org.motechproject.server.model.ExpectedEncounter;
import org.motechproject.server.model.ExpectedObs;
import org.motechproject.server.model.Facility;
import org.motechproject.server.model.GeneralOutpatientEncounter;
import org.motechproject.server.model.Message;
import org.motechproject.server.model.MessageAttribute;
import org.motechproject.server.model.MessageDefinition;
import org.motechproject.server.model.MessageProgramEnrollment;
import org.motechproject.server.model.MessageStatus;
import org.motechproject.server.model.ScheduledMessage;
import org.motechproject.server.model.TroubledPhone;
import org.motechproject.server.model.db.MotechDAO;
import org.openmrs.Concept;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;

/**
 * An implementation of the motech data access object interface, implemented
 * using the hibernate object relational mapping library.
 */
public class HibernateMotechDAO implements MotechDAO {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getUsersByPersonAttribute(
			Integer personAttributeTypeId, String personAttributeValue) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Integer>) session
				.createSQLQuery(
						"select u.user_id from person p inner join users u on u.user_id = p.person_id "
								+ "inner join person_attribute a on p.person_id = a.person_id "
								+ "where a.voided = false and p.voided = false and u.voided = false "
								+ "and a.person_attribute_type_id = :typeId and a.value = :value group by u.user_id")
				.setInteger("typeId", personAttributeTypeId).setString("value",
						personAttributeValue).list();
	}

	public ScheduledMessage saveScheduledMessage(
			ScheduledMessage scheduledMessage) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(scheduledMessage);
		return scheduledMessage;
	}

	public Message saveMessage(Message message) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(message);
		return message;
	}

	public MessageDefinition saveMessageDefinition(
			MessageDefinition messageDefinition) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(messageDefinition);
		return messageDefinition;
	}

	public MessageAttribute saveMessageAttribute(
			MessageAttribute messageAttribute) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(messageAttribute);
		return messageAttribute;
	}

	@SuppressWarnings("unchecked")
	public List<ScheduledMessage> getScheduledMessages() {
		Session session = sessionFactory.getCurrentSession();
		return (List<ScheduledMessage>) session.createCriteria(
				ScheduledMessage.class).addOrder(Order.asc("scheduledFor"))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<ScheduledMessage> getScheduledMessages(Date startDate,
			Date endDate) {
		Session session = sessionFactory.getCurrentSession();
		return (List<ScheduledMessage>) session.createCriteria(
				ScheduledMessage.class).add(
				Restrictions.between("scheduledFor", startDate, endDate))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<ScheduledMessage> getScheduledMessages(Integer recipientId,
			MessageDefinition definition, MessageProgramEnrollment enrollment,
			Date messageDate) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ScheduledMessage.class);
		if (recipientId != null) {
			criteria.add(Restrictions.eq("recipientId", recipientId));
		}
		if (definition != null) {
			criteria.add(Restrictions.eq("message", definition));
		}
		if (enrollment != null) {
			criteria.add(Restrictions.eq("enrollment", enrollment));
		}
		if (messageDate != null) {
			criteria.add(Restrictions.eq("scheduledFor", messageDate));
		}
		criteria.addOrder(Order.desc("scheduledFor"));
		criteria.addOrder(Order.desc("id"));
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages() {
		Session session = sessionFactory.getCurrentSession();
		return (List<Message>) session.createCriteria(Message.class).list();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages(Date startDate, Date endDate,
			MessageStatus status) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Message>) session.createCriteria(Message.class).add(
				Restrictions.between("attemptDate", startDate, endDate)).add(
				Restrictions.eq("attemptStatus", status)).list();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages(MessageProgramEnrollment enrollment,
			MessageStatus status) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Message>) session.createCriteria(Message.class).add(
				Restrictions.eq("attemptStatus", status)).createCriteria(
				"schedule").add(Restrictions.eq("enrollment", enrollment))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages(Integer recipientId,
			MessageProgramEnrollment enrollment, MessageDefinition definition,
			Date messageDate, MessageStatus status) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Message>) session.createCriteria(Message.class).add(
				Restrictions.eq("attemptStatus", status)).createCriteria(
				"schedule").add(Restrictions.eq("recipientId", recipientId))
				.add(Restrictions.eq("enrollment", enrollment)).add(
						Restrictions.or(Restrictions.ne("message", definition),
								Restrictions.ne("scheduledFor", messageDate)))
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages(Integer recipientId,
			MessageProgramEnrollment enrollment,
			MessageDefDate[] messageDefDates, MessageStatus status) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Message.class).add(
				Restrictions.eq("attemptStatus", status)).createCriteria(
				"schedule").add(Restrictions.eq("recipientId", recipientId))
				.add(Restrictions.eq("enrollment", enrollment));

		Conjunction notMatchMessages = Restrictions.conjunction();
		for (MessageDefDate messageDefDate : messageDefDates) {
			notMatchMessages.add(Restrictions.or(Restrictions.ne("message",
					messageDefDate.getMessage()), Restrictions.ne(
					"scheduledFor", messageDefDate.getDate())));
		}
		criteria.add(notMatchMessages);

		return criteria.list();
	}

	public Message getMessage(String publicId) {
		Session session = sessionFactory.getCurrentSession();
		return (Message) session.createCriteria(Message.class).add(
				Restrictions.eq("publicId", publicId)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Message> getMessages(ScheduledMessage scheduledMessage) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Message>) session.createCriteria(Message.class).add(
				Restrictions.eq("schedule", scheduledMessage)).list();
	}

	@SuppressWarnings("unchecked")
	public List<MessageDefinition> getMessageDefinitions() {
		Session session = sessionFactory.getCurrentSession();
		return (List<MessageDefinition>) session.createCriteria(
				MessageDefinition.class).list();
	}

	public MessageDefinition getMessageDefinition(String messageKey) {
		Session session = sessionFactory.getCurrentSession();
		return (MessageDefinition) session.createCriteria(
				MessageDefinition.class).add(
				Restrictions.eq("messageKey", messageKey)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<MessageAttribute> getMessageAttributes() {
		Session session = sessionFactory.getCurrentSession();
		return (List<MessageAttribute>) session.createCriteria(
				MessageAttribute.class).list();
	}

	public Blackout getBlackoutSettings() {
		Blackout settings = (Blackout) sessionFactory.getCurrentSession()
				.createCriteria(Blackout.class).uniqueResult();
		return settings;
	}

	public void setBlackoutSettings(Blackout settings) {
		sessionFactory.getCurrentSession().saveOrUpdate(settings);
	}

	public void saveTroubledPhone(TroubledPhone phone) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(phone);
	}

	public TroubledPhone getTroubledPhone(Long id) {
		Session session = sessionFactory.getCurrentSession();
		TroubledPhone tp = (TroubledPhone) session.get(TroubledPhone.class, id);
		return tp;
	}

	public void removeTroubledPhone(Long id) {
		Session session = sessionFactory.getCurrentSession();
		TroubledPhone tp = getTroubledPhone(id);
		session.delete(tp);
	}

	public TroubledPhone getTroubledPhoneByNumber(String phoneNumber) {
		Session session = sessionFactory.getCurrentSession();
		TroubledPhone tp = (TroubledPhone) session.createCriteria(
				TroubledPhone.class).add(
				Restrictions.eq("phoneNumber", phoneNumber)).uniqueResult();
		return tp;
	}

	public MessageProgramEnrollment saveMessageProgramEnrollment(
			MessageProgramEnrollment enrollment) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(enrollment);
		return enrollment;
	}

	@SuppressWarnings("unchecked")
	public List<MessageProgramEnrollment> getActiveMessageProgramEnrollments(
			Integer personId, String program, Integer obsId,
			Long minExclusiveId, Long maxInclusiveId, Integer maxResults) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session
				.createCriteria(MessageProgramEnrollment.class);
		criteria.add(Restrictions.isNotNull("startDate"));
		criteria.add(Restrictions.isNull("endDate"));
		if (personId != null) {
			criteria.add(Restrictions.eq("personId", personId));
		}
		if (program != null) {
			criteria.add(Restrictions.eq("program", program));
		}
		if (obsId != null) {
			criteria.add(Restrictions.eq("obsId", obsId));
		}
		if (minExclusiveId != null) {
			criteria.add(Restrictions.gt("id", minExclusiveId));
		}
		if (maxInclusiveId != null) {
			criteria.add(Restrictions.le("id", maxInclusiveId));
		}
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return (List<MessageProgramEnrollment>) criteria.list();
	}

	public Long getMaxMessageProgramEnrollmentId() {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session
				.createCriteria(MessageProgramEnrollment.class);
		criteria.setProjection(Projections.max("id"));
		return (Long) criteria.uniqueResult();
	}

	public GeneralOutpatientEncounter saveGeneralOutpatientEncounter(
			GeneralOutpatientEncounter encounter) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(encounter);
		return encounter;
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllCountries() {
		Session session = sessionFactory.getCurrentSession();
		return (List<String>) session.createQuery(
				"select distinct country from " + Location.class.getName()
						+ " where country is not null "
						+ " and region != 'Field Agent' " + "order by country")
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllRegions() {
		Session session = sessionFactory.getCurrentSession();
		return (List<String>) session.createQuery(
				"select distinct region from " + Location.class.getName()
						+ " where region is not null"
						+ " and region != 'Field Agent' " + " order by region")
				.list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getRegions(String country) {
		Session session = sessionFactory.getCurrentSession();
		return (List<String>) session.createQuery(
				"select distinct region from " + Location.class.getName()
						+ " where region is not null and country = :country "
						+ " and region != 'Field Agent' " + "order by region")
				.setString("country", country).list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getAllDistricts() {
		Session session = sessionFactory.getCurrentSession();
		return (List<String>) session.createQuery(
				"select distinct countyDistrict from "
						+ Location.class.getName()
						+ " where countyDistrict is not null "
						+ " and countyDistrict != 'Field Agent' "
						+ "order by countyDistrict").list();
	}

	@SuppressWarnings("unchecked")
	public List<String> getDistricts(String country, String region) {
		Session session = sessionFactory.getCurrentSession();
		return (List<String>) session.createQuery(
				"select distinct countyDistrict from "
						+ Location.class.getName()
						+ " where countyDistrict is not null and "
						+ "country = :country and region = :region "
						+ "order by countyDistrict").setString("country",
				country).setString("region", region).list();
	}

	@SuppressWarnings("unchecked")
	public List<Obs> getActivePregnancies(Integer patientId,
			Concept pregnancyConcept, Concept pregnancyStatusConcept) {
		Session session = sessionFactory.getCurrentSession();
		return (List<Obs>) session.createQuery(
				"select p from " + Obs.class.getName()
						+ " p where p.person.personId = :patientId and "
						+ "p.concept = :pregnancyConcept and "
						+ "exists (select s from " + Obs.class.getName()
						+ " s where s.obsGroup = p and "
						+ "s.concept = :pregnancyStatusConcept and "
						+ "s.valueNumeric = :trueDouble) and "
						+ "not exists (select e from " + Obs.class.getName()
						+ " e where e.obsGroup = p and "
						+ "e.concept = :pregnancyStatusConcept and "
						+ "e.valueNumeric = :falseDouble)").setInteger(
				"patientId", patientId).setEntity("pregnancyConcept",
				pregnancyConcept).setEntity("pregnancyStatusConcept",
				pregnancyStatusConcept).setDouble("trueDouble", 1.0).setDouble(
				"falseDouble", 0.0).list();
	}

	@SuppressWarnings("unchecked")
	public List<Obs> getActivePregnanciesDueDateObs(Facility facility,
			Date fromDueDate, Date toDueDate, Concept pregnancyDueDateConcept,
			Concept pregnancyConcept, Concept pregnancyStatusConcept,
			Integer maxResults) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(Obs.class, "o");

		criteria.add(Restrictions.eq("o.voided", false));
		criteria.add(Restrictions.eq("o.concept", pregnancyDueDateConcept));

		if (fromDueDate != null) {
			criteria.add(Restrictions.ge("o.valueDatetime", fromDueDate));
		}
		if (toDueDate != null) {
			criteria.add(Restrictions.le("o.valueDatetime", toDueDate));
		}

		criteria.createAlias("o.person", "p");
		criteria.add(Restrictions.eq("p.personVoided", false));

		criteria.createAlias("o.obsGroup", "g");
		criteria.add(Restrictions.eq("g.concept", pregnancyConcept));
		criteria.add(Restrictions.eq("g.voided", false));

		DetachedCriteria pregnancyActiveCriteria = DetachedCriteria.forClass(
				Obs.class, "s").setProjection(Projections.id()).add(
				Restrictions.eq("s.voided", false)).add(
				Restrictions.eq("s.concept", pregnancyStatusConcept)).add(
				Restrictions.eq("s.valueNumeric", 1.0)).add(
				Restrictions.eqProperty("s.obsGroup.obsId", "g.obsId")).add(
				Restrictions.eqProperty("s.person.personId", "p.personId"));

		criteria.add(Subqueries.exists(pregnancyActiveCriteria));

		DetachedCriteria pregnancyInactiveCriteria = DetachedCriteria.forClass(
				Obs.class, "e").setProjection(Projections.id()).add(
				Restrictions.eq("e.voided", false)).add(
				Restrictions.eq("e.concept", pregnancyStatusConcept)).add(
				Restrictions.eq("e.valueNumeric", 0.0)).add(
				Restrictions.eqProperty("e.obsGroup.obsId", "g.obsId")).add(
				Restrictions.eqProperty("e.person.personId", "p.personId"));

		criteria.add(Subqueries.notExists(pregnancyInactiveCriteria));

		if (facility != null) {
			criteria
					.add(Restrictions
							.sqlRestriction(
									"exists (select c.id from motechmodule_community c "
											+ "inner join motechmodule_community_patient cp "
											+ "on c.id = cp.community_id "
											+ "where c.facility_id = ? and cp.patient_id = {alias}.person_id)",
									facility.getId(), Hibernate.LONG));
		}

		criteria.addOrder(Order.asc("o.valueDatetime"));
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Encounter> getEncounters(Facility facility,
			EncounterType encounterType, Date fromDate, Date toDate,
			Integer maxResults) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Encounter.class, "e");
		criteria.add(Restrictions.eq("e.voided", false));
		criteria.add(Restrictions.eq("e.encounterType", encounterType));
		criteria.createAlias("e.patient", "p");
		criteria.add(Restrictions.eq("p.voided", false));
		if (fromDate != null) {
			criteria.add(Restrictions.ge("e.encounterDatetime", fromDate));
		}
		if (toDate != null) {
			criteria.add(Restrictions.le("e.encounterDatetime", toDate));
		}
		if (facility != null) {
			criteria
					.add(Restrictions
							.sqlRestriction(
									"exists (select c.id from motechmodule_community c "
											+ "inner join motechmodule_community_patient cp "
											+ "on c.id = cp.community_id "
											+ "where c.facility_id = ? and cp.patient_id = {alias}.patient_id)",
									facility.getId(), Hibernate.LONG));
		}
		criteria.addOrder(Order.asc("e.encounterDatetime"));
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return criteria.list();
	}

	public ExpectedObs saveExpectedObs(ExpectedObs expectedObs) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(expectedObs);
		return expectedObs;
	}

	@SuppressWarnings("unchecked")
	public List<ExpectedObs> getExpectedObs(Patient patient, Facility facility,
			String[] groups, Date minDueDate, Date maxDueDate,
			Date maxLateDate, Date minMaxDate, Integer maxResults) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ExpectedObs.class);
		if (patient != null) {
			criteria.add(Restrictions.eq("patient", patient));
		}
		if (groups != null && groups.length != 0) {
			criteria.add(Restrictions.in("group", groups));
		}
		if (minDueDate != null) {
			criteria.add(Restrictions.ge("dueObsDatetime", minDueDate));
		}
		if (maxDueDate != null) {
			criteria.add(Restrictions.le("dueObsDatetime", maxDueDate));
		}
		if (maxLateDate != null) {
			criteria.add(Restrictions.le("lateObsDatetime", maxLateDate));
		}
		if (minMaxDate != null) {
			criteria.add(Restrictions.or(Restrictions.isNull("maxObsDatetime"),
					Restrictions.gt("maxObsDatetime", minMaxDate)));
		}
		if (facility != null) {
			criteria
					.add(Restrictions
							.sqlRestriction(
									"exists (select c.id from motechmodule_community c "
											+ "inner join motechmodule_community_patient cp "
											+ "on c.id = cp.community_id "
											+ "where c.facility_id = ? and cp.patient_id = {alias}.patient_id)",
									facility.getId(), Hibernate.LONG));
		}
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(Order.asc("dueObsDatetime"));
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return criteria.list();
	}

	public ExpectedEncounter saveExpectedEncounter(
			ExpectedEncounter expectedEncounter) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(expectedEncounter);
		return expectedEncounter;
	}

	public Facility saveFacility(Facility facility) {
		Session session = sessionFactory.getCurrentSession();
		session.saveOrUpdate(facility);
		return facility;
	}

	@SuppressWarnings("unchecked")
	public List<ExpectedEncounter> getExpectedEncounter(Patient patient,
			Facility facility, String[] groups, Date minDueDate,
			Date maxDueDate, Date maxLateDate, Date minMaxDate,
			Integer maxResults) {
		Session session = sessionFactory.getCurrentSession();
		Criteria criteria = session.createCriteria(ExpectedEncounter.class);
		if (patient != null) {
			criteria.add(Restrictions.eq("patient", patient));
		}
		if (groups != null && groups.length != 0) {
			criteria.add(Restrictions.in("group", groups));
		}
		if (minDueDate != null) {
			criteria.add(Restrictions.ge("dueEncounterDatetime", minDueDate));
		}
		if (maxDueDate != null) {
			criteria.add(Restrictions.le("dueEncounterDatetime", maxDueDate));
		}
		if (maxLateDate != null) {
			criteria.add(Restrictions.le("lateEncounterDatetime", maxLateDate));
		}
		if (minMaxDate != null) {
			criteria.add(Restrictions.or(Restrictions
					.isNull("maxEncounterDatetime"), Restrictions.gt(
					"maxEncounterDatetime", minMaxDate)));
		}
		if (facility != null) {
			criteria
					.add(Restrictions
							.sqlRestriction(
									"exists (select c.id from motechmodule_community c "
											+ "inner join motechmodule_community_patient cp "
											+ "on c.id = cp.community_id "
											+ "where c.facility_id = ? and cp.patient_id = {alias}.patient_id)",
									facility.getId(), Hibernate.LONG));
		}
		criteria.add(Restrictions.eq("voided", false));
		criteria.addOrder(Order.asc("dueEncounterDatetime"));
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Patient> getDuplicatePatients(String firstName,
			String lastName, String preferredName, Date birthDate,
			Integer communityId, String phoneNumber,
			PersonAttributeType phoneNumberAttrType, String nhisNumber,
			PersonAttributeType nhisAttrType, String patientId,
			PatientIdentifierType patientIdType, Integer maxResults) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Patient.class);

		criteria.createAlias("names", "name");
		criteria.createAlias("attributes", "att");
		criteria.createAlias("identifiers", "id");

		criteria.add(Restrictions.eq("voided", false));
		criteria.add(Restrictions.eq("name.voided", false));
		criteria.add(Restrictions.eq("att.voided", false));
		criteria.add(Restrictions.eq("id.voided", false));

		Criterion patientIdCriterion = Restrictions.and(Restrictions.eq(
				"id.identifierType", patientIdType), Restrictions.eq(
				"id.identifier", patientId));
		Criterion nhisCriterion = Restrictions.and(Restrictions.eq(
				"att.attributeType", nhisAttrType), Restrictions.eq(
				"att.value", nhisNumber));
		Criterion nameCriterion = Restrictions.or(Restrictions.and(Restrictions
				.eq("name.givenName", firstName), Restrictions.eq(
				"name.familyName", lastName)), Restrictions.and(Restrictions
				.eq("name.givenName", preferredName), Restrictions.eq(
				"name.familyName", lastName)));
		Criterion phoneCriterion = Restrictions.and(Restrictions.eq(
				"att.attributeType", phoneNumberAttrType), Restrictions.eq(
				"att.value", phoneNumber));

		Disjunction otherCriterion = Restrictions.disjunction();
		otherCriterion.add(Restrictions.eq("birthdate", birthDate));
		otherCriterion
				.add(Restrictions
						.sqlRestriction(
								"exists (select c.id from motechmodule_community c "
										+ "inner join motechmodule_community_patient cp "
										+ "on c.id = cp.community_id "
										+ "where c.community_id = ? and cp.patient_id = {alias}.patient_id)",
								communityId, Hibernate.INTEGER));
		otherCriterion.add(phoneCriterion);

		// Get Patients by PatientId or NHIS or
		// (((FirstName and LastName) or (PreferredName and LastName)) and
		// (BirthDate or Community or PhoneNumber))))
		Disjunction finalCriterion = Restrictions.disjunction();
		finalCriterion.add(patientIdCriterion);
		finalCriterion.add(nhisCriterion);
		finalCriterion.add(Restrictions.and(nameCriterion, otherCriterion));

		criteria.add(finalCriterion);

		criteria.addOrder(Order.asc("name.givenName"));
		criteria.addOrder(Order.asc("name.familyName"));
		criteria.addOrder(Order.asc("birthdate"));

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Patient> getPatients(String firstName, String lastName,
			String preferredName, Date birthDate, Integer communityId,
			String phoneNumber, PersonAttributeType phoneNumberAttrType,
			String nhisNumber, PersonAttributeType nhisAttrType,
			String patientId, PatientIdentifierType patientIdType,
			Integer maxResults) {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Patient.class, "p");

		criteria.createAlias("p.names", "name");
		criteria.createAlias("p.identifiers", "id");

		criteria.add(Restrictions.eq("p.voided", false));
		criteria.add(Restrictions.eq("name.voided", false));
		criteria.add(Restrictions.eq("id.voided", false));

		if (patientId != null && patientIdType != null) {
			criteria.add(Restrictions
					.and(Restrictions.eq("id.identifierType", patientIdType),
							Restrictions.eq("id.identifier", patientId)));
		}

		Criterion firstNameCriterion = Restrictions.like("name.givenName",
				firstName, MatchMode.ANYWHERE);
		Criterion preferredNameCriterion = Restrictions.like("name.givenName",
				preferredName, MatchMode.ANYWHERE);
		if (firstName != null && preferredName != null) {
			criteria.add(Restrictions.or(firstNameCriterion,
					preferredNameCriterion));
		} else if (firstName != null) {
			criteria.add(firstNameCriterion);
		} else if (preferredName != null) {
			criteria.add(preferredNameCriterion);
		}

		if (lastName != null) {
			criteria.add(Restrictions.like("name.familyName", lastName,
					MatchMode.ANYWHERE));
		}

		if (birthDate != null) {
			criteria.add(Restrictions.eq("p.birthdate", birthDate));
		}

		if (communityId != null) {
			criteria
					.add(Restrictions
							.sqlRestriction(
									"exists (select c.id from motechmodule_community c "
											+ "inner join motechmodule_community_patient cp "
											+ "on c.id = cp.community_id "
											+ "where c.community_id = ? and cp.patient_id = {alias}.patient_id)",
									communityId, Hibernate.INTEGER));
		}

		if (nhisNumber != null && nhisAttrType != null) {
			DetachedCriteria nhisCriteria = DetachedCriteria.forClass(
					PersonAttribute.class, "nattr").setProjection(
					Projections.id()).add(
					Restrictions.eq("nattr.voided", false)).add(
					Restrictions.eqProperty("nattr.person.personId",
							"p.patientId")).add(
					Restrictions.and(Restrictions.eq("nattr.attributeType",
							nhisAttrType), Restrictions.eq("nattr.value",
							nhisNumber)));

			criteria.add(Subqueries.exists(nhisCriteria));
		}

		if (phoneNumber != null && phoneNumberAttrType != null) {
			DetachedCriteria phoneCriteria = DetachedCriteria.forClass(
					PersonAttribute.class, "pattr").setProjection(
					Projections.id()).add(
					Restrictions.eq("pattr.voided", false)).add(
					Restrictions.eqProperty("pattr.person.personId",
							"p.patientId")).add(
					Restrictions.and(Restrictions.eq("pattr.attributeType",
							phoneNumberAttrType), Restrictions.eq(
							"pattr.value", phoneNumber)));

			criteria.add(Subqueries.exists(phoneCriteria));
		}

		criteria.addOrder(Order.asc("name.givenName"));
		criteria.addOrder(Order.asc("name.familyName"));
		criteria.addOrder(Order.asc("p.birthdate"));

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		if (maxResults != null) {
			criteria.setMaxResults(maxResults);
		}
		return criteria.list();
	}

	public Facility getFacilityByFacilityId(Integer facilityId) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Facility.class);
		criteria.add(Restrictions.eq("facilityId", facilityId));
		return (Facility) criteria.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Facility> getFacilityByLocation(Location location) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Facility.class);
		criteria.add(Restrictions.eq("location", location));
		return (List<Facility>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Facility> getAllFacilities() {
		return (List<Facility>) sessionFactory.getCurrentSession()
				.createCriteria(Facility.class).add(
						Restrictions.not(Restrictions.eq("facilityId", 9999)))
				.list();
	}

	public Community getCommunityByCommunityId(Integer communityId) {
		return (Community) sessionFactory.getCurrentSession().createCriteria(
				Community.class).add(
				Restrictions.eq("communityId", communityId)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public List<Community> getAllCommunities(boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Community.class);
		if (!includeRetired) {
			criteria.add(Restrictions.eq("retired", false));
		}
		criteria.addOrder(Order.asc("name"));
		return (List<Community>) criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Community> getCommunities(String country, String region,
			String district, boolean includeRetired) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(
				Community.class, "c");
		criteria.createAlias("c.facility", "f");
		criteria.createAlias("f.location", "loc");
		criteria.add(Restrictions.eq("loc.country", country));
		criteria.add(Restrictions.eq("loc.region", region));
		criteria.add(Restrictions.eq("loc.countyDistrict", district));

		if (!includeRetired) {
			criteria.add(Restrictions.eq("c.retired", false));
		}
		criteria.addOrder(Order.asc("c.name"));

		return (List<Community>) criteria.list();
	}

	public Community getCommunityByPatient(Patient patient) {
		return (Community) sessionFactory.getCurrentSession().createQuery(
				"from " + Community.class.getName()
						+ " c where :patient in elements(c.residents)")
				.setEntity("patient", patient).uniqueResult();
	}
}
