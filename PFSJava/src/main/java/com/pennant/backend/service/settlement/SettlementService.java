package com.pennant.backend.service.settlement;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.settlement.FinSettlementHeader;

public interface SettlementService {

	FinSettlementHeader getsettlementById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	FinReceiptData getDues(String finReference, Date valueDate);

	FinSettlementHeader getSettlementByRef(String finReference, String type);

	void processSettlementCancellation(long finID, Date settlementDate);

	void processSettlement(long finID, Date settlementDate);

	BigDecimal getSettlementAountReceived(long finId);

}
