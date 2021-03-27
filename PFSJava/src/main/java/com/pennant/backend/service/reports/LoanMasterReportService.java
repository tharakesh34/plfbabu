package com.pennant.backend.service.reports;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.LoanReport;

public interface LoanMasterReportService {
	List<LoanReport> getLoanReports(String finReference, Date fromDate, Date toDate);
}
