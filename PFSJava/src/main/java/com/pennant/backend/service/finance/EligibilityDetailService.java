package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

public interface EligibilityDetailService {

	List<FinanceEligibilityDetail> setFinanceEligibilityDetails(long finID, String finCcy, BigDecimal finAmount,
			boolean isNewRecord, String finType, String userRole, String screenEvent);

	List<FinanceEligibilityDetail> fetchEligibilityDetails(FinanceMain financeMain,
			List<FinanceReferenceDetail> finReferenceDetails);

	FinanceEligibilityDetail getElgResult(FinanceEligibilityDetail financeEligibilityDetail,
			FinanceDetail financeDetail);

	FinanceEligibilityDetail prepareElgDetail(FinanceReferenceDetail referenceDetail, long finID);

	boolean getEligibilityStatus(FinanceEligibilityDetail financeEligibilityDetail, String finCcy,
			BigDecimal finAmount);

	boolean isCustEligible(List<FinanceEligibilityDetail> financeEligibilityDetails);

	List<FinanceEligibilityDetail> getFinElgDetailList(long finID);

	void validate(List<FinanceEligibilityDetail> financeEligibilityDetailList, AuditDetail auditDetail,
			String[] errParm, String[] valueParm, String usrLanguage);

	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail);

	void deleteByFinRef(long finID);

	void saveList(List<FinanceEligibilityDetail> list, String type);
}