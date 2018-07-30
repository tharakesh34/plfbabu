package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public interface DepositDetailsService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	DepositDetails getDepositDetailsById(long depositId);
	DepositDetails getApprovedDepositDetailsById(long depositId);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	//Deposit Movements
	DepositMovements getDepositMovementsById(long movementId);
	DepositMovements getApprovedDepositMovementsById(long movementId);
	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid);
	
	//Deposit Cheques
	List<DepositCheques> getDepositChequesList(String branchCode);
	
}
