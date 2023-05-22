package com.pennant.backend.service.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennanttech.pennapps.core.AppException;

public interface FinanceWriteoffService {

	FinanceWriteoffHeader getFinanceWriteoffDetailById(long finID, String type, String userRole, String procEdtEvent);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AppException;

	AuditHeader doReject(AuditHeader auditHeader) throws AppException;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws AppException;

	List<FinanceScheduleDetail> getFinScheduleDetails(long finID);

	List<ManualAdvise> getPayableAdvises(long finID, String string);

	int getMaxFinanceWriteoffSeq(long finID, Date writeoffDate, String string);

	boolean isWriteoffLoan(long finID, String string);

}
