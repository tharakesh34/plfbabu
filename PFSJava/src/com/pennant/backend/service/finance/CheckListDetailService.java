package com.pennant.backend.service.finance;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;

public interface CheckListDetailService {
	public void setFinanceCheckListDetails(FinanceDetail financeDetail, String finType, String userRole);
	public List<FinanceCheckListReference> getCheckListByFinRef(final String id, String tableType);
	public List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType);
	public List<AuditDetail> doApprove(FinanceDetail financeDetail, String recordType);
	public List<AuditDetail> delete(FinanceDetail finDetail, String tableType, String auditTranType);
	public List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method);
	List<AuditDetail> validate(FinanceDetail financeDetail,  String method, String usrLanguage);
	
	
}
