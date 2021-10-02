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

	List<FinLogEntryDetail> getFinLogEntryDetailList(String finreference, Date fromDate, Date toDate);

	RescheduleLog getFinBasicDetails(String finreference);

	List<FinServiceInstruction> getFinServiceInstructions(String finreference);

	List<FinServiceInstruction> getFinServiceInstructions(String finReference, long logkey);

	List<FinanceScheduleDetail> getScheduleDetails(String finreference, String type, long logkey);

	FinanceProfitDetail getProfitDetail(String finreference);

	List<RescheduleLogHeader> getFinBasicDetails();

}
