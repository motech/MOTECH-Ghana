package org.motechproject.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.motechproject.server.model.Service;
import org.motechproject.server.model.ServiceStatus;
import org.motechproject.server.service.Requirement;
import org.motechproject.server.service.ServiceDelivery;
import org.motechproject.server.service.ServiceDeliverySequence;
import org.motechproject.server.svc.RegistrarBean;
import org.motechproject.server.time.TimeBean;
import org.motechproject.server.time.TimePeriod;
import org.motechproject.server.time.TimeReference;

public class ServiceDeliverySequenceImpl implements ServiceDeliverySequence {

	protected String name;

	protected Integer earliestTimeValue;
	protected TimePeriod earliestTimePeriod;
	protected TimeReference earliestTimeReference;

	protected Integer latestTimeValue;
	protected TimePeriod latestTimePeriod;
	protected TimeReference latestTimeReference;

	protected List<ServiceDelivery> services = new ArrayList<ServiceDelivery>();
	protected List<Requirement> requirements = new ArrayList<Requirement>();

	protected TimeBean timeBean;
	protected RegistrarBean registrarBean;

	public void updateServiceDeliveries(Integer patientId, Date date) {
		if (!meetsRequirements(patientId, date)) {
			List<Service> services = registrarBean.getIncompleteServices(
					patientId, name);
			for (Service service : services) {
				service.setStatus(ServiceStatus.MISSED);
				registrarBean.saveService(service);
			}
		} else {
			Map<String, Service> serviceMap = registrarBean
					.getIncompleteServicesMap(patientId, name);

			for (ServiceDelivery serviceDelivery : services) {
				Integer deliveryId = getDeliveryId(serviceDelivery, patientId);

				if (serviceMap.containsKey(serviceDelivery.getName())) {
					Service service = serviceMap.get(serviceDelivery.getName());
					if (deliveryId != null) {
						service.setDeliveryId(deliveryId);
						service.setStatus(ServiceStatus.COMPLETE);
					} else {
						Date latest = getLatestDate(serviceDelivery, patientId);
						if (date.after(latest)) {
							service.setStatus(ServiceStatus.MISSED);
						}
					}
					registrarBean.saveService(service);

				} else if (deliveryId == null) {
					Date earliest = getEarliestDate(serviceDelivery, patientId);
					Date start = getPreferredStartDate(serviceDelivery,
							patientId);
					Date end = getPreferredEndDate(serviceDelivery, patientId);
					Date latest = getLatestDate(serviceDelivery, patientId);

					if (start != null && end != null && !date.after(latest)) {
						Service service = new Service();
						service.setPatientId(patientId);
						service.setService(serviceDelivery.getName());
						service.setSequence(name);
						service.setStatus(ServiceStatus.INCOMPLETE);
						service.setEarliest(earliest);
						service.setPreferredStart(start);
						service.setPreferredEnd(end);
						service.setLatest(latest);
						registrarBean.saveService(service);
					}
				}
			}
		}
	}

	public boolean meetsRequirements(Integer patientId, Date date) {
		for (Requirement requirement : requirements) {
			if (!requirement.meetsRequirement(patientId, date)) {
				return false;
			}
		}
		return true;
	}

	public Integer getDeliveryId(ServiceDelivery service, Integer patientId) {
		return null;
	}

	public Date getEarliestDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(getEarliestTimePeriod(service),
				getEarliestTimeReference(service),
				getEarliestTimeValue(service), patientId, null, null, null,
				null, null);
	}

	public Date getLatestDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(getLatestTimePeriod(service),
				getLatestTimeReference(service), getLatestTimeValue(service),
				patientId, null, null, null, null, null);
	}

	public Date getPreferredStartDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(service.getStartTimePeriod(), service
				.getStartTimeReference(), service.getStartTimeValue(),
				patientId, null, null, null, null, null);
	}

	public Date getPreferredEndDate(ServiceDelivery service, Integer patientId) {
		return timeBean.determineTime(service.getEndTimePeriod(), service
				.getEndTimeReference(), service.getEndTimeValue(), patientId,
				null, null, null, null, null);
	}

	protected TimePeriod getEarliestTimePeriod(ServiceDelivery service) {
		if (service.getEarliestTimePeriod() != null) {
			return service.getEarliestTimePeriod();
		}
		return earliestTimePeriod;
	}

	protected TimeReference getEarliestTimeReference(ServiceDelivery service) {
		if (service.getEarliestTimeReference() != null) {
			return service.getEarliestTimeReference();
		}
		return earliestTimeReference;
	}

	protected Integer getEarliestTimeValue(ServiceDelivery service) {
		if (service.getEarliestTimeValue() != null) {
			return service.getEarliestTimeValue();
		}
		return earliestTimeValue;
	}

	protected TimePeriod getLatestTimePeriod(ServiceDelivery service) {
		if (service.getLatestTimePeriod() != null) {
			return service.getLatestTimePeriod();
		}
		return latestTimePeriod;
	}

	protected TimeReference getLatestTimeReference(ServiceDelivery service) {
		if (service.getLatestTimeReference() != null) {
			return service.getLatestTimeReference();
		}
		return latestTimeReference;
	}

	protected Integer getLatestTimeValue(ServiceDelivery service) {
		if (service.getLatestTimeValue() != null) {
			return service.getLatestTimeValue();
		}
		return latestTimeValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getEarliestTimeValue() {
		return earliestTimeValue;
	}

	public void setEarliestTimeValue(Integer earliestTimeValue) {
		this.earliestTimeValue = earliestTimeValue;
	}

	public TimePeriod getEarliestTimePeriod() {
		return earliestTimePeriod;
	}

	public void setEarliestTimePeriod(TimePeriod earliestTimePeriod) {
		this.earliestTimePeriod = earliestTimePeriod;
	}

	public TimeReference getEarliestTimeReference() {
		return earliestTimeReference;
	}

	public void setEarliestTimeReference(TimeReference earliestTimeReference) {
		this.earliestTimeReference = earliestTimeReference;
	}

	public Integer getLatestTimeValue() {
		return latestTimeValue;
	}

	public void setLatestTimeValue(Integer latestTimeValue) {
		this.latestTimeValue = latestTimeValue;
	}

	public TimePeriod getLatestTimePeriod() {
		return latestTimePeriod;
	}

	public void setLatestTimePeriod(TimePeriod latestTimePeriod) {
		this.latestTimePeriod = latestTimePeriod;
	}

	public TimeReference getLatestTimeReference() {
		return latestTimeReference;
	}

	public void setLatestTimeReference(TimeReference latestTimeReference) {
		this.latestTimeReference = latestTimeReference;
	}

	public List<ServiceDelivery> getServices() {
		return services;
	}

	public void setServices(List<ServiceDelivery> services) {
		this.services = services;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<Requirement> requirements) {
		this.requirements = requirements;
	}

	public TimeBean getTimeBean() {
		return timeBean;
	}

	public void setTimeBean(TimeBean timeBean) {
		this.timeBean = timeBean;
	}

	public RegistrarBean getRegistrarBean() {
		return registrarBean;
	}

	public void setRegistrarBean(RegistrarBean registrarBean) {
		this.registrarBean = registrarBean;
	}
}
