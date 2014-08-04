package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface BulkRateChangeProcessService {
	BulkProcessHeader getBulkProcessHeader();
	BulkProcessHeader getNewBulkProcessHeader();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	BulkProcessHeader getBulkProcessHeaderById(long bulkProcessId, String bulkProcessFor);
	BulkProcessHeader getApprovedBulkProcessHeaderById(long bulkProcessId);
	BulkProcessHeader refresh(BulkProcessHeader bulkProcessHeader);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws Exception;
	AuditHeader doReject(AuditHeader auditHeader);
	List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);
	List<BulkProcessDetails> getBulkDefermentFinList(Date fromDate, Date toDate, String whereClause);
	boolean bulkRateChangeFinances(List<BulkProcessDetails> bulkRateChangeFinances, String recalType, BigDecimal rateChange) throws AccountNotFoundException;
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF) throws AccountNotFoundException;
	FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData);
	BigDecimal getAccrueAmount(String finReference);
	boolean bulkDefermentChanges(List<BulkProcessDetails> defermentChangeFinances,String recalType, boolean excludeDeferment, 
			String addTermAfter, Date calFromDate, Date calToDate, String cbRecalType) throws AccountNotFoundException, Exception;
	public List<ScheduleMapDetails> getDeferedDates(List<BulkProcessDetails> defermentChangeFinances, String cbRecalType,
			Date reCalFromDate, Date reCalToDate);
	public BulkProcessHeader getBulkProcessHeaderByFromAndToDates( Date fromDate, Date toDate, String type);
}
