package org.motech.ejb;

import java.util.Date;

import javax.ejb.Local;

@Local
public interface Registrar {

	void registerMother(String nurseId, String serialId, String name,
			String community, String location, Date dueDate, Integer age,
			Integer parity, Integer hemoglobin);

}
