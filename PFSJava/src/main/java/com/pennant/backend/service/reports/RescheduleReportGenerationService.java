package com.pennant.backend.service.reports;

import java.sql.Date;
import java.util.List;

import com.pennant.backend.model.finance.RescheduleLog;
import com.pennant.backend.model.finance.RescheduleLogHeader;

public interface RescheduleReportGenerationService {

	List<RescheduleLog> getReschedulementList(long finID, Date fromDate, Date toDate);

	List<RescheduleLogHeader> getReschedulementList(Date fromDate, Date toDate);

	long getFinIDByFinReference(String finReference);
}
