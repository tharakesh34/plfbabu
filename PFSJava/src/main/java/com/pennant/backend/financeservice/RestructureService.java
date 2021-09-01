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

	FinScheduleData doRestructure(FinScheduleData schdData, FinServiceInstruction finServiceInstruction);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);

	FinScheduleData doResetOverdraftSchd(FinScheduleData schdData);

	RestructureDetail getRestructureDetailByRef(long finID, String type);

	FinanceProfitDetail getFinProfitDetailsById(long finID);

	List<RepayInstruction> getRepayInstructions(long finID, String type, boolean isWIF);

	AuditDetail deleteRestructureDetail(FinanceDetail fd, String type, String transType);

	List<AuditDetail> doApproveRestructureDetail(FinanceDetail fd, String type, String transType);

	AuditDetail saveOrUpdateRestructureDetail(FinanceDetail fd, String type, String transType);

	AuditDetail validationRestructureDetail(FinanceDetail fd, String method, String usrLanguage);

	BigDecimal getReceivableAmt(long finID, boolean isBounce);

	BigDecimal getTotalPenaltyBal(long finID, List<Date> presentmentDates);

	void computeLPPandUpdateOD(FinanceDetail fd);

	void processRestructureAccounting(AEEvent aeEvent, FinanceDetail fd);
}
