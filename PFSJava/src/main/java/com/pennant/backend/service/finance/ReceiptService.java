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
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinTaxReceivable;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.receipt.ReceiptPurpose;
import com.pennattech.pff.receipt.model.ReceiptDTO;

public interface ReceiptService {

	FinReceiptData getFinReceiptDataById(String finReference, Date valueDate, String eventCode, String procEditEvent,
			String userRole);

	FinReceiptData getFinReceiptDataByReceiptId(long receiptId, String eventCode, String procEditEvent,
			String userRole);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReversal(AuditHeader auditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doApprove(AuditHeader aAuditHeader);

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type);

	FinReceiptData calculateRepayments(FinReceiptData finReceiptData, boolean isPresentment);

	List<FinODDetails> getValueDatePenalties(FinScheduleData finScheduleData, BigDecimal totReceiptAmount,
			Date valueDate, List<FinanceRepayments> repayments, boolean resetReq);

	FinReceiptData doBusinessValidations(FinReceiptData receiptData, int methodCtg);

	void validateDual(FinReceiptData receiptData);

	// ### Ticket id:124998
	FinanceMain getClosingStatus(long finID, TableType tempTab, boolean wif);

	BigDecimal getClosingBalance(long finID, Date valueDate);

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

	BigDecimal getReceiptAmountPerMonthByFinreference(Date receiptDate, List<String> finreference);

	FinReceiptHeader getInititatedReceipts(String reference);

	ErrorDetail validateThreshHoldLimit(FinReceiptHeader rch, BigDecimal totalDues);

	void waiveThresholdLimit(FinReceiptData receiptData);

	ReceiptDTO prepareReceiptDTO(FinReceiptData rd);

	List<ReceiptAllocationDetail> getReceiptAllocDetail(long finID, String allocType);

	FinReceiptData doApproveReceipt(FinReceiptData rd);

	FinReceiptData getExcessAndManualAdviseData(FinReceiptData receiptData, long fromLanFinid);

	boolean doProcessTerminationExcess(FinReceiptData receiptData);

	List<FinReceiptDetail> prepareRCDForExcess(List<FinExcessAmount> excessList, ReceiptUploadDetail rud);

	List<FinReceiptDetail> prepareRCDForMA(List<ManualAdvise> manualAdviseList, ReceiptUploadDetail rud);

	List<OverdueChargeRecovery> prepareODCRecovery(Long finID);

	FinReceiptData getDues(String finReference, Date valueDate, Date appDate, String event);

	FinReceiptData prepareFinReceiptData(FinServiceInstruction fsi, FinanceDetail fd);

	Date getExcessBasedValueDate(Date receiptDt, long finID, Date appDate, FinExcessAmount fea, String receiptPurpose);

	BigDecimal[] getEmiSplitForManualAlloc(FinanceMain toFm, Date valueDate, BigDecimal emiAmt);

	void validateAdjustedAlloc(FinReceiptData rd);
}
