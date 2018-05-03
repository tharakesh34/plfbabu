package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;

public interface AgreementDetailService {	
	
	List<FinanceReferenceDetail> getAggrementDetailList(String finType, String finEvent,String nextRoleCode);
	List<FinAgreementDetail> getFinAgrByFinRef(String finReference, String tableType);
	FinAgreementDetail getFinAgreementDetailById(String finReference, long agrId, String tableType);	
	FinAgreementDetail getFinAgrDetailByAgrId(String finReference, long agrId);
	List<AuditDetail> delete(FinanceDetail financeDetail, String tableType, String auditTranType);
	List<AuditDetail> validate(List<AuditDetail> auditDetails,  String method, String usrLanguage);
	
}
