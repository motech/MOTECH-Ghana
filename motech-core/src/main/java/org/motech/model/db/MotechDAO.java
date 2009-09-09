package org.motech.model.db;

import java.util.Date;
import java.util.List;

import org.motech.model.FutureServiceDelivery;
import org.motech.model.Log;

public interface MotechDAO {

	Integer saveFutureServiceDelivery(FutureServiceDelivery fsd);

	void updateFutureServiceDelivery(FutureServiceDelivery fsd);

	List<FutureServiceDelivery> getFutureServiceDeliveries(Date startDate,
			Date endDate);

	List<FutureServiceDelivery> getFutureServiceDeliveries();

	Integer saveLog(Log log);

	List<Log> getLogs();

	List<Integer> getUsersByPersonAttribute(Integer personAttributeTypeId,
			String personAttributeValue);
}
