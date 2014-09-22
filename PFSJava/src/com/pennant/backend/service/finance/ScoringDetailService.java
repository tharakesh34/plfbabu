package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;

public interface ScoringDetailService {
	
	FinanceDetail setFinanceScoringDetails(FinanceDetail financeDetail, String finType, String userRole, String ctgType);
	List<ScoringMetrics> executeScoringMetrics(List<ScoringMetrics> scoringMetricsList, CustomerScoringCheck customerScoringCheck);
	void saveOrUpdate(FinanceDetail financeDetail);
	List<Object> getFinScoreDetailList(String finReference);
	void validate(FinanceDetail financeDetail, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage);
}
