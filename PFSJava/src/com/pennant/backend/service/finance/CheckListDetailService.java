package com.pennant.backend.service.finance;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;

public interface CheckListDetailService {
	
	void setFinanceCheckListDetails(FinanceDetail financeDetail, String finType, String userRole);
	List<FinanceCheckListReference> getCheckListByFinRef(final String id, String tableType);
	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType);
	List<AuditDetail> doApprove(FinanceDetail financeDetail, String recordType);
	List<AuditDetail> delete(FinanceDetail finDetail, String tableType, String auditTranType);
	List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method);
	List<AuditDetail> validate(FinanceDetail financeDetail,  String method, String usrLanguage);
}
