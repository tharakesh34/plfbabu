package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;

public interface ScoringDetailService {
	
	FinanceDetail fetchFinScoringDetails(FinanceDetail financeDetail, List<FinanceReferenceDetail> scoringGroupList, String ctgType);
	FinanceDetail setFinanceScoringDetails(FinanceDetail financeDetail, String finType, String userRole, String ctgType,String screenEvent);
	List<ScoringMetrics> executeScoringMetrics(List<ScoringMetrics> scoringMetricsList, CustomerEligibilityCheck customerEligibilityCheck);
	List<Object> getFinScoreDetailList(String finReference);
	void validate(FinanceDetail financeDetail, AuditDetail auditDetail, String[] errParm, String[] valueParm, String usrLanguage);
	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail);
	void deleteHeaderList(String finReference, String type);
	void deleteDetailList(List<Long> headerList, String type);
	List<FinanceScoreHeader> getFinScoreHeaderList(String finReference, String type);
	List<FinanceScoreDetail> getFinScoreDetailList(List<Long> headerIds, String type);
	long saveHeader(FinanceScoreHeader header, String type);
	void saveDetailList(List<FinanceScoreDetail> scoreDetailList, String type);
}
