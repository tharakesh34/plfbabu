package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface FinanceWriteoffService {

	FinanceWriteoffHeader getFinanceWriteoffDetailById(String finReference, String type);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader aAuditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	List<FinanceScheduleDetail> getFinScheduleDetails(String finReference);
}
