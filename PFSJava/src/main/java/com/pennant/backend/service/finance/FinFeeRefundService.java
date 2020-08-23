package com.pennant.backend.service.finance;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.PrvsFinFeeRefund;
import com.pennant.backend.model.rulefactory.AEEvent;

public interface FinFeeRefundService {

	FinFeeRefundHeader getFinFeeRefundHeaderById(long headerID, String type);

	List<FinFeeRefundDetails> getFinFeeRefundDetailsByHeaderId(long headerID, String type);

	FinFeeRefundHeader getPaidFeeDetails(FinFeeRefundHeader header, String type);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	PrvsFinFeeRefund getPrvsRefundsByFeeId(long feeID);

	void prepareFeeRulesMap(FinFeeRefundHeader feeRefundHeader, Map<String, Object> dataMap, String userBranch);

	FinanceDetail getFinanceDetailById(String finReference);

	AEEvent processAccounting(FinFeeRefundHeader feeRefundHeader);

}
