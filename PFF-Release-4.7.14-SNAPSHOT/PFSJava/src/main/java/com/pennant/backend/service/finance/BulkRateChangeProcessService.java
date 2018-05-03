package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.ScheduleMapDetails;

public interface BulkRateChangeProcessService {
	BulkRateChangeHeader getBulkRateChangeHeader();
	BulkRateChangeHeader getNewBulkRateChangeHeader();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws Exception;
	AuditHeader doReject(AuditHeader auditHeader);

	List<BulkRateChangeDetails> getBulkRateChangeFinList(String frinType, Date schFromDate, String whereClause);
	BulkRateChangeHeader getBulkRateChangeHeaderByRef(String bulkRateChangeRef);
	BulkRateChangeHeader getApprovedBulkRateChangeHeaderByRef(String bulkRateChangeRef);
	String getBulkRateChangeReference();

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF);
	FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData);
	BigDecimal getAccrueAmount(String finReference);

	List<ScheduleMapDetails> getDeferedDates(List<BulkRateChangeDetails> bulkRateChangeDetails, String cbRecalType, Date reCalFromDate, Date reCalToDate);

	boolean processBulkRateChangeDetail(BulkRateChangeHeader bulkRateChangeHeader, BulkRateChangeDetails bulkRateChangeDetail) throws Exception;
	void doApproveBulkRateChangeHeader(BulkRateChangeHeader bulkRateChangeHeader, long success, long failure);
 }
