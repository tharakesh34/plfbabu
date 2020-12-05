package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;

public interface ReceiptService {

	FinReceiptData getFinReceiptDataById(String finReference, String eventCode, String procEditEvent, String userRole);

	FinReceiptData getFinReceiptDataByReceiptId(long receiptId, String eventCode, String procEditEvent,
			String userRole);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReversal(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException, Exception;

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type);

	FinReceiptData calculateRepayments(FinReceiptData finReceiptData, boolean isPresentment);

	List<FinODDetails> getValueDatePenalties(FinScheduleData finScheduleData, BigDecimal totReceiptAmount,
			Date valueDate, List<FinanceRepayments> repayments, boolean resetReq);

	Date getMaxReceiptDate(String finReference);

	FinReceiptData doReceiptValidations(FinanceDetail financeDetail, String method);

	FinReceiptData doBasicValidations(FinReceiptData receiptData, int methodCtg);

	FinReceiptData doDataValidations(FinReceiptData receiptData, int methodCtg);

	FinReceiptData doFunctionalValidations(FinReceiptData receiptData, int methodCtg);

	FinReceiptData doBusinessValidations(FinReceiptData receiptData, int methodCtg);

	FinReceiptData validateDual(FinReceiptData receiptData, int methodCtg);

	FinReceiptData getServicingFinance(String id, String nextRoleCode, String screenEvent, String role);

	Map<Long, String> getOrnamentDescriptions(List<Long> idList);

	// ### Ticket id:124998
	String getClosingStatus(String finReference, TableType tempTab, boolean wif);

	// ### 29-10-2018, Ticket id:124998
	boolean dedupCheckRequest(FinReceiptHeader receiptHeader, String purpose);

	// ### 29-10-2018, Ticket id:124998
	FinanceDetail getFinanceDetail(FinServiceInstruction finSrvcInst, String eventCode, FinanceDetail financeDetail);

	// ### 29-10-2018, Ticket id:124998
	long CheckDedupSP(FinReceiptHeader receiptHeader, String purpose);

	BigDecimal getClosingBalance(String finReference, Date valueDate);// ## PSD
																		// Ticket
																		// id:124998,Receipt
																		// Upload

	boolean isReceiptsPending(String finreference, long receiptId);

	boolean canProcessReceipt(long receiptId);

	Assignment getAssignment(long id, String type);

	List<AssignmentDealExcludedFee> getApprovedAssignmentDealExcludedFeeList(long id);

	boolean isInSubVention(FinanceMain financeMain, Date receivedDate);

	Date getFirstInstDate(List<FinanceScheduleDetail> financeScheduleDetails);

	Date getManualAdviseMaxDate(String reference, Date valueDate);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2);

	FinScheduleData setErrorToFSD(FinScheduleData finScheduleData, String errorCode, String parm0, String parm1,
			String parm2, String parm3);

	FinReceiptData setReceiptData(FinReceiptData receiptData);

	FinReceiptData setReceiptDetail(FinReceiptData receiptData);

	FinanceType getFinanceType(String finType);

	List<ValueLabel> getEarlyPaySchdMethods(FinanceDetail financeDetail, Date valueDate);

	FinReceiptData recalEarlyPaySchedule(FinReceiptData receiptData);

	ErrorDetail doInstrumentValidation(FinReceiptData receiptData);

	FinanceDetail receiptTransaction(FinServiceInstruction finServiceInstruction, String moduleDefiner);

	FinServiceInstruction buildFinServiceInstruction(ReceiptUploadDetail rud, String entity);

	FinReceiptData updateExcessPay(FinReceiptData receiptData, String rcMode, long id, BigDecimal amount);

	FinReceiptData calcuateDues(FinReceiptData receiptData);

	boolean checkDueAdjusted(List<ReceiptAllocationDetail> allocations, FinReceiptData receiptData);

	FinReceiptData adjustToExcess(FinReceiptData receiptData);

	FinTaxReceivable getTaxReceivable(String finReference, String taxFor);

	// ## For MultiReceipt 
	void saveMultiReceipt(List<AuditHeader> auditHeaderList) throws Exception;

	void saveMultiReceipt(AuditHeader auditHeader) throws Exception;

	void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	List<Long> getInProcessMultiReceiptRecord();

	long getUploadSeqId();

	FinReceiptData createXcessRCD(FinReceiptData receiptData);

	boolean isEarlySettlementInitiated(String finreference);

	boolean isPartialSettlementInitiated(String finreference);

	String getLoanReferenc(String finreference, String fileName);

	List<FinExcessAmount> xcessList(String finreference);

	FinReceiptData recalculateReceipt(FinReceiptData receiptData);

	List<FeeWaiverHeader> getFeeWaiverHeaderEnqByFinRef(String finReference, String type);

	int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference);

}
