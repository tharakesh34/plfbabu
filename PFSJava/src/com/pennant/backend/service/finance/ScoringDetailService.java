package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;

public interface ScoringDetailService {
	public FinanceDetail setFinanceScoringDetails(FinanceDetail financeDetail, String finType, String userRole, String ctgType);
	public List<ScoringMetrics> executeScoringMetrics(List<ScoringMetrics> scoringMetricsList, CustomerScoringCheck customerScoringCheck);
	public void saveOrUpdate(FinanceDetail financeDetail);
	public List<Object> getFinScoreDetailList(String finReference);
	public void validate(FinanceDetail financeDetail, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage);
}
