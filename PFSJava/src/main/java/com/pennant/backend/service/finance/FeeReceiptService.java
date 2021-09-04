package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface FeeReceiptService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException;

	AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	List<FinFeeDetail> getPaidFinFeeDetails(long finID, long receiptID, String type);

	ErrorDetail processFeePayment(FinServiceInstruction finServInst) throws Exception;

	Map<String, Object> getGLSubHeadCodes(long finID);

	Long getAccountingSetId(String eventCode, String accSetCode);

	SecurityUser getSecurityUserById(long userId, String type);

	void calculateFees(FinFeeDetail finFeeDetail, FinanceMain financeMain, Map<String, BigDecimal> taxPercentages);

	void prepareFeeRulesMap(FinReceiptHeader finReceiptHeader, Map<String, Object> dataMap);

	void calculateGST(List<FinFeeDetail> finFeeDetails, Map<String, BigDecimal> taxPercentages, boolean isApprove);

}
