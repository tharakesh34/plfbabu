package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;

public interface FinanceDeviationsService {
	
	void processDevaitions( String finreference,List<FinanceDeviations> newlist,AuditHeader auditHeader);
	List<FinanceDeviations> getApprovedFinanceDeviations(final String finReference);
	List<FinanceDeviations> getFinanceDeviations(String finReference);
	void processApproval(List<FinanceDeviations> list,AuditHeader auditHeader,String finreference);
	FinanceDetail getFinanceDetailById(String id);
	FinanceMain getFinanceMain(String finReference);
	//### 01-05-2018 story #361(tuleap server) Manual Deviations
	AuditHeader doCheckDeviationApproval(AuditHeader auditHeader);
}
