package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface ManualPaymentService {

	List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc,String type);
	FinanceProfitDetail getFinProfitDetailsById(String finReference);
	RepayData getRepayDataById(String finReference, String eventCode);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef);
	FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);
}
