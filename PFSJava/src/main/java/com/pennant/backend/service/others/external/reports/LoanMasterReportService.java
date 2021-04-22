package com.pennant.backend.service.others.external.reports;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.others.external.reports.LoanReport;

public interface LoanMasterReportService {
	List<LoanReport> getLoanReports(String finReference, Date fromDate, Date toDate);

}
