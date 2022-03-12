package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennanttech.pennapps.core.AppException;

public interface ManualPaymentService {

	List<FinanceRepayments> getFinRepayList(long finID);

	FinanceProfitDetail getFinProfitDetailsById(long finID);

	RepayData getRepayDataById(long finID, String eventCode, String procEdtEvent, String userRole);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AppException;

	AuditHeader doReject(AuditHeader auditHeader) throws AppException;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws AppException;

	FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef);

	FinanceProfitDetail getPftDetailForEarlyStlReport(long finID);

	RepayData doCalcRepayments(RepayData repayData, FinanceDetail aFinanceDetail, FinServiceInstruction finServiceInst);

	RepayData setEarlyRepayEffectOnSchedule(RepayData repayData, FinServiceInstruction finServiceInst);
}
