package com.pennant.backend.financeservice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.rulefactory.AEEvent;

public interface RestructureService {

	FinScheduleData doRestructure(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);

	FinScheduleData doResetOverdraftSchd(FinScheduleData finScheduleData);

	RestructureDetail getRestructureDetailByRef(String finReference, String type);

	FinanceProfitDetail getFinProfitDetailsById(String fineference);

	List<RepayInstruction> getRepayInstructions(String id, String type, boolean isWIF);

	AuditDetail deleteRestructureDetail(FinanceDetail financeDetail, String type, String transType);

	List<AuditDetail> doApproveRestructureDetail(FinanceDetail financeDetail, String type, String transType);

	AuditDetail saveOrUpdateRestructureDetail(FinanceDetail financeDetail, String type, String transType);

	AuditDetail validationRestructureDetail(FinanceDetail financeDetail, String method, String usrLanguage);

	BigDecimal getReceivableAmt(String finReference, boolean isBounce);

	BigDecimal getTotalPenaltyBal(String finReference, List<Date> presentmentDates);

	void computeLPPandUpdateOD(FinanceDetail financeDetail);

	void processRestructureAccounting(AEEvent aeEvent, FinanceDetail financeDetail);
}
