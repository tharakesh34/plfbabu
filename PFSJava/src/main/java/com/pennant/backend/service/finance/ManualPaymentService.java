package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ManualPaymentService {

	List<FinanceRepayments> getFinRepayListByFinRef(String finRef, boolean isRpyCancelProc,String type);
	FinanceProfitDetail getFinProfitDetailsById(String finReference);
	RepayData getRepayDataById(String finReference, String eventCode,String procEdtEvent, String userRole);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef);
	FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);
	AuditDetail doValidations(FinServiceInstruction finServiceInstruction, String method);
	RepayData doCalcRepayments(RepayData repayData, FinanceDetail aFinanceDetail, FinServiceInstruction finServiceInst);
	RepayData setEarlyRepayEffectOnSchedule(RepayData repayData, FinServiceInstruction finServiceInst);
}
