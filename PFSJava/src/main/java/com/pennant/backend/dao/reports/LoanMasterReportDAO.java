package com.pennant.backend.dao.reports;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.LoanReport;

public interface LoanMasterReportDAO {
	public List<LoanReport> getLoanReports(String finReference);

	List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type);

	List<FinRepayHeader> getFinRepayHeader(String reference);

	List<FinanceScheduleDetail> getFinPftPaid(String id, Date appDate);

	public int getMaxPendingOverDuePayment(String custCIF);

	public int getRevisedTenure(String finReference);

	List<FinanceScheduleDetail> getScheduleDetail(String finreference, Date date);

	public FinanceScheduleDetail getNextSchPayment(String finReference, Date curBussDate);

}
