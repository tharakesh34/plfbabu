package com.pennant.backend.service.reports;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.systemmasters.AverageYieldReport;

public interface AverageYieldReportService {

	List<AverageYieldReport> getAverageYieldLoanReportList(Date fromDate, Date toDate);

	List<AverageYieldReport> getAverageYieldProductReportList(Date startDate, Date endDate);

}
