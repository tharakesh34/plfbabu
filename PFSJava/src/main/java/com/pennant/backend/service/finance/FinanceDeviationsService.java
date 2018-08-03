package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceMain;

public interface FinanceDeviationsService {
	void processDevaitions( String finreference,List<FinanceDeviations> newlist,AuditHeader auditHeader);

	void processApprovedDevaitions(String finReference, List<FinanceDeviations> deviations, AuditHeader auditHeader);

	List<FinanceDeviations> getApprovedFinanceDeviations(final String finReference);
	List<FinanceDeviations> getFinanceDeviations(String finReference);
	void processApproval(List<FinanceDeviations> list,AuditHeader auditHeader,String finreference);
	FinanceDetail getFinanceDetailById(String id);
	FinanceMain getFinanceMain(String finReference);

	//### 01-05-2018 story #361(tuleap server) Manual Deviations
	/**
	 * Checks whether all the manual deviations were approved or not. If there are any pending / rejected deviations it
	 * will raise an error and set to the audit header. The caller has to handle the error.
	 * 
	 * @param auditHeader
	 *            Finance detail audit header.
	 * @return The audit header with error message set if any pending / rejected deviations available.
	 */
	AuditHeader doCheckDeviationApproval(AuditHeader auditHeader);
}
