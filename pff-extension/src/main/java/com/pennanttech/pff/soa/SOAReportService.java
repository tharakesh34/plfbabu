package com.pennanttech.pff.soa;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.systemmasters.SOAScheduleReport;

public interface SOAReportService {

	public List<Map<String, Object>> extendedFieldDetailsService(String finReference);

	public List<SOAScheduleReport> getSOAScheduleReport(List<FinanceScheduleDetail> financeScheduleDetail,
			int ccyEditField);
}
