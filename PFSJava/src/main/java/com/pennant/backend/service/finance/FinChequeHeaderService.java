package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennanttech.pff.core.TableType;

public interface FinChequeHeaderService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader, TableType tableType);

	AuditHeader delete(AuditHeader auditHeader);

	ChequeHeader getChequeHeader(long finID);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	ChequeHeader getChequeHeaderByRef(long finID);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage);

	List<AuditDetail> processingChequeDetailList(List<AuditDetail> auditDetails, TableType type, long headerID);

	List<AuditDetail> setChequeDetailAuditData(ChequeHeader chequeHeader, String auditTranType, String method);
}
