package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

public interface EligibilityDetailService {

	public List<FinanceEligibilityDetail> setFinanceEligibilityDetails(String finReference, String finCcy, BigDecimal finAmount, 
			boolean isNewRecord, String finType, String userRole);
	public FinanceEligibilityDetail getElgResult(FinanceEligibilityDetail financeEligibilityDetail,  FinanceDetail financeDetail);
	public FinanceEligibilityDetail prepareElgDetail(FinanceReferenceDetail referenceDetail, String finReference);
	public boolean getEligibilityStatus(FinanceEligibilityDetail financeEligibilityDetail, String finCcy, BigDecimal finAmount);
	public boolean isCustEligible(List<FinanceEligibilityDetail> financeEligibilityDetails);
	public List<FinanceEligibilityDetail> getFinElgDetailList(String finReference);
	public void validate(List<FinanceEligibilityDetail> FinanceEligibilityDetailList, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage);
	public void saveOrUpdate(FinanceDetail financeDetail);
	public RuleExecutionUtil getRuleExecutionUtil();
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil);

}