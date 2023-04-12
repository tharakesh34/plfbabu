package com.pennant.pff.settlement.service;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.pff.settlement.model.FinSettlementHeader;

public interface SettlementService {

	FinSettlementHeader getsettlementById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	FinReceiptData getDues(String finReference, Date valueDate);

	FinSettlementHeader getSettlementByRef(String finReference, String type);

	boolean isSettlementInitiated(long finId, String type);

	void processSettlementCancellation(FinSettlementHeader fsh);

	void processSettlement(FinSettlementHeader fsh);

	BigDecimal getSettlementAountReceived(long finId);

	void loadSettlementData(FinSettlementHeader header);

	boolean isValidSettlementProcess(FinSettlementHeader fsh);

	FinSettlementHeader loadDataForCancellation(long finID, Date settlementDate);

	long getQueueCount();

	int updateThreadID(long from, long to, int threadID);

	long prepareQueue();

	void updateProgress(long settlementId, int progress);
}
