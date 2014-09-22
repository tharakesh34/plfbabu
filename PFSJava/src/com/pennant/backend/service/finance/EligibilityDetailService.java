package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

public interface EligibilityDetailService {

	List<FinanceEligibilityDetail> setFinanceEligibilityDetails(String finReference, String finCcy, BigDecimal finAmount, 
			boolean isNewRecord, String finType, String userRole);
	FinanceEligibilityDetail getElgResult(FinanceEligibilityDetail financeEligibilityDetail,  FinanceDetail financeDetail);
	FinanceEligibilityDetail prepareElgDetail(FinanceReferenceDetail referenceDetail, String finReference);
	boolean getEligibilityStatus(FinanceEligibilityDetail financeEligibilityDetail, String finCcy, BigDecimal finAmount);
	boolean isCustEligible(List<FinanceEligibilityDetail> financeEligibilityDetails);
	List<FinanceEligibilityDetail> getFinElgDetailList(String finReference);
	void validate(List<FinanceEligibilityDetail> financeEligibilityDetailList, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage);
	void saveOrUpdate(FinanceDetail financeDetail);
	RuleExecutionUtil getRuleExecutionUtil();
	void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil);
}