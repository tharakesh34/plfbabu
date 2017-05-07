package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.exception.PFFInterfaceException;

public interface FinanceWriteoffService {

	FinanceWriteoffHeader getFinanceWriteoffDetailById(String finReference, String type, String userRole, String procEdtEvent);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	List<FinanceScheduleDetail> getFinScheduleDetails(String finReference);
}
