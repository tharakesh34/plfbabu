package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import org.jaxen.JaxenException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.exception.PFFInterfaceException;


public interface FinanceCancellationService {

	FinanceDetail getFinanceDetailById(String finReference, String type, String userRole, String procEdtEvent);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, JaxenException, IllegalAccessException, InvocationTargetException;

}

