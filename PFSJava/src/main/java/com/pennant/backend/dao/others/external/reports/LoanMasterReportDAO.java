package com.pennant.backend.dao.others.external.reports;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.model.others.external.reports.LoanReport;

public interface LoanMasterReportDAO {
	public List<LoanReport> getLoanReports(String finReference, Date fromDate, Date toDate);

	List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type);

	List<FinRepayHeader> getFinRepayHeader(String reference);

	List<FinanceScheduleDetail> getFinPftPaid(String id, Date appDate);

	public int getMaxPendingOverDuePayment(String custCIF);

	public int getRevisedTenure(String finReference);

	List<FinanceScheduleDetail> getScheduleDetail(String finreference, Date date);

	public FinanceScheduleDetail getNextSchPayment(String finReference, Date curBussDate);

	LoanReport getFinanceDisbursementDetails(String id, String type, boolean isWIF);

	List<CollateralAssignment> getCollateralAssignmentByFinRef(String reference, String moduleName, String type);

	List<VASRecording> getLoanReportVasRecordingByRef(String finReference);

	LoanReport getFinODBalByFinRef(String finReference);

	List<FinAdvancePayments> getFinAdvancePaymentsByFinRef(String id, String type);

	List<VasMovementDetail> getVasMovementDetailByRef(String finReference, String type);

	List<FinFeeDetail> getFinFeeDetailByFinRef(String id, boolean isWIF, String type);
}
