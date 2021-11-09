package com.pennant.backend.dao.reports;

import java.sql.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RescheduleLog;
import com.pennant.backend.model.finance.RescheduleLogHeader;

public interface ReschedulReportGenerationDAO {

	List<FinLogEntryDetail> getFinLogEntryDetailList(long finID, Date fromDate, Date toDate);

	RescheduleLog getFinBasicDetails(long finID);

	List<FinServiceInstruction> getFinServiceInstructions(long finID);

	List<FinServiceInstruction> getFinServiceInstructions(long finID, long logkey);

	List<FinanceScheduleDetail> getScheduleDetails(long finID, String type, long logkey);

	FinanceProfitDetail getProfitDetail(long finID);

	List<RescheduleLogHeader> getFinBasicDetails();

}
