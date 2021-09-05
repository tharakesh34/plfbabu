package com.pennant.backend.service.finance.putcall;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennanttech.pff.core.TableType;

public interface FinOptionService {

	List<FinOption> getFinOptions(long finID, TableType tableType);

	List<AuditDetail> saveOrUpdate(List<FinOption> FinOptions, TableType tableType, String auditTranType);

	List<AuditDetail> doApprove(List<FinOption> FinOptions, TableType tableType, String auditTranType);

	List<AuditDetail> delete(List<FinOption> FinOptions, TableType tableType, String auditTranType);

	List<AuditDetail> validate(List<FinOption> FinOptions, long workflowId, String method, String auditTranType,
			String usrLanguage);

	List<AuditDetail> processFinOptions(List<FinOption> FinOptions, TableType tableType, String auditTranType,
			boolean isApproveRcd);

	List<AuditDetail> doProcess(List<FinOption> FinOptions, TableType tableType, String auditTranType,
			boolean isApproveRcd);

	List<AuditDetail> validateFinOptions(List<AuditDetail> auditDetails, String usrLanguage, String method);

	FinanceDetail getFinanceDetailById(long finID, String type, String userRole, String moduleDefiner,
			String eventCodeRef);
}
