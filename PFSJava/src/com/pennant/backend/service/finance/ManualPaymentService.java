package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RepayMain;
import com.pennant.backend.model.finance.RepayScheduleDetail;

public interface ManualPaymentService {

	public boolean saveOrUpdate(FinanceMain financeMain, List<FinanceScheduleDetail> scheduleDetails, 
			List<RepayInstruction> repayInstructions, RepayMain repayMain, 
			List<RepayScheduleDetail> repaySchdList, boolean isSchdlRemodified);
	public List<FinanceRepayments> getFinRepayListByFinRef(String finRef, String type);
	public FinanceProfitDetail getFinProfitDetailsById(String finReference);
}
