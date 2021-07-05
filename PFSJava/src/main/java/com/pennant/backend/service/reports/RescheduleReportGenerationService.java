package com.pennant.backend.service.reports;

import java.sql.Date;
import java.util.List;

import com.pennant.backend.model.finance.RescheduleLog;

public interface RescheduleReportGenerationService {

	List<RescheduleLog> getReschedulementList(String finreference, Date fromDate, Date toDate);
}
