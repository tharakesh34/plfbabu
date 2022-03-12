package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
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
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.ReceiptPurpose;

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

	FinReceiptData doBusinessValidations(FinReceiptData receiptData, int methodCtg);

	void validateDual(FinReceiptData receiptData);

	// ### Ticket id:124998
	FinanceMain getClosingStatus(long finID, TableType tempTab, boolean wif);

	// ### 29-10-2018, Ticket id:124998
	boolean dedupCheckRequest(FinReceiptHeader receiptHeader, String purpose);

	// ### 29-10-2018, Ticket id:124998
	long checkDedupSP(FinReceiptHeader receiptHeader, String purpose);

	BigDecimal getClosingBalance(long finID, Date valueDate);// ## PSD
																// Ticket
																// id:124998,Receipt
																// Upload

	boolean isReceiptsPending(long finID, long receiptId);

	boolean canProcessReceipt(long receiptId);

	boolean isInSubVention(FinanceMain financeMain, Date receivedDate);

	Date getManualAdviseMaxDate(long finID, Date valueDate);

	FinanceType getFinanceType(String finType);

	List<ValueLabel> getEarlyPaySchdMethods(FinanceDetail financeDetail, Date valueDate);

	FinReceiptData recalEarlyPaySchedule(FinReceiptData receiptData);

	void doInstrumentValidation(FinReceiptData receiptData);

	FinanceDetail receiptTransaction(FinServiceInstruction fsi);

	FinServiceInstruction buildFinServiceInstruction(ReceiptUploadDetail rud, String entity);

	FinReceiptData updateExcessPay(FinReceiptData receiptData, String rcMode, long id, BigDecimal amount);

	FinReceiptData calcuateDues(FinReceiptData receiptData);

	boolean checkDueAdjusted(List<ReceiptAllocationDetail> allocations, FinReceiptData receiptData);

	FinReceiptData adjustToExcess(FinReceiptData receiptData);

	FinTaxReceivable getTaxReceivable(long finID, String taxFor);

	// ## For MultiReceipt
	void saveMultiReceipt(List<AuditHeader> auditHeaderList) throws Exception;

	void saveMultiReceipt(AuditHeader auditHeader) throws Exception;

	void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	List<Long> getInProcessMultiReceiptRecord();

	long getUploadSeqId();

	FinReceiptData createXcessRCD(FinReceiptData receiptData);

	String getLoanReferenc(String finreference, String fileName);

	List<FinExcessAmount> xcessList(long finID);

	FinReceiptData recalculateReceipt(FinReceiptData receiptData);

	Date getLastWaiverDate(long finID, Date appDate, Date receiptDate);

	int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference);

	ErrorDetail getWaiverValidation(long finID, String receiptPurpose, Date valueDate);

	ErrorDetail receiptCancelValidation(long finID, Date lastReceivedDate);

	List<ErrorDetail> dedupCheck(FinServiceInstruction fsi);

	boolean checkPresentmentsInQueue(long finID);

	Date getFinSchdDate(FinReceiptHeader rh);

	ErrorDetail checkInprocessReceipts(long finID, ReceiptPurpose receiptPurpose);

	void setFinanceData(FinReceiptData rd);
}
