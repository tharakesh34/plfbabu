package com.pennant.backend.service.reports;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.LoanReport;

public interface LoanMasterReportService {
	List<LoanReport> getLoanReports(String finReference);

	Map<String, BigDecimal> getAmountsByRef(String finReference, BigDecimal finCurrAssetValue);
}
