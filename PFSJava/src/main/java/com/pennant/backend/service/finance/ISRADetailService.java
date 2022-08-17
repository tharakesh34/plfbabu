package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.isradetail.ISRADetail;

public interface ISRADetailService {

	List<AuditDetail> saveOrUpdate(FinanceDetail fd, String type, String tranType);

	List<AuditDetail> doApprove(FinanceDetail fd, String type, String tranType);

	List<AuditDetail> doReject(FinanceDetail fd, String type, String tranType);

	ISRADetail getIsraDetailsByRef(String finReference, String type);

	List<AuditDetail> getISRALiquidDeatils(ISRADetail israDetail, String auditTranType, String method);
}
