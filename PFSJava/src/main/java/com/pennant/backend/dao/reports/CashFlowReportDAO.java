package com.pennant.backend.dao.reports;

import java.util.List;

import com.pennant.backend.model.finance.CashFlow;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinRepayHeader;

public interface CashFlowReportDAO {

	List<CashFlow> getCashFlowDetails();

	List<FinRepayHeader> getFinRepayHeader(String reference);

	List<CashFlow> getFinDisbDetails(String reference, String type);

	List<com.pennant.backend.model.finance.FinanceScheduleDetail> getFinScheduleDetails(String reference, String type);

	List<FinODDetails> getFinODDetailsByFinRef(String finReference, String type);

}
