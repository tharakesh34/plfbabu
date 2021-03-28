package com.pennant.backend.dao.reports;

import java.util.List;

import com.pennant.backend.model.finance.CashFlow;
import com.pennant.backend.model.finance.FinRepayHeader;

public interface CashFlowReportDAO {

	List<CashFlow> getCashFlowDetails();

	List<FinRepayHeader> getFinRepayHeader(String reference);
}
