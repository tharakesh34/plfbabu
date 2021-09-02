package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoKnockOff;

public interface AutoKnockOffService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AutoKnockOff getAutoKnockOffCode(long id);

	List<AutoKnockOff> getKnockOffDetails(long finID);

}
