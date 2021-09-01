package com.pennant.backend.service.systemmasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PmayEligibilityLog;
import com.pennanttech.pff.core.TableType;

public interface PMAYService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	PMAY getPMAY(long finID, String tableType);

	PMAY getApprovedPMAY(long finID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditDetail saveOrUpdate(PMAY pmay, TableType tableType, String auditTranType);

	AuditDetail doApprove(PMAY pmay, TableType tableType, String auditTranType);

	AuditDetail delete(PMAY pmay, TableType tableType, String auditTranType);

	long generateDocSeq();

	List<PmayEligibilityLog> getAllRecordIdForPmay();

	void update(PmayEligibilityLog pmayEligibilityLog);
}
