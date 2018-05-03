package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;



public interface ReturnedChequeService {
	
    ReturnedChequeDetails getReturnedChequesById(String custCIF,String chequeNo);
	ReturnedChequeDetails getApprovedReturnedChequesById(String custCIF,String chequeNo);
	AuditHeader	saveOrUpdate (AuditHeader auditHeader);
	AuditHeader	delete(AuditHeader auditHeader);
	AuditHeader	doApprove(AuditHeader auditHeader);
	AuditHeader	doReject(AuditHeader auditHeader);
	List<ReturnedCheques> fetchReturnedCheques(ReturnedCheques  returnedCheques );
	
}
