package com.pennanttech.pff.external;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface HoldFinanceService {
	public AuditHeader executeHoldFinance(AuditHeader auditHeader) throws InterfaceException;
}
