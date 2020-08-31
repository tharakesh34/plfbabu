package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.ReceiptCancelDetail;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptHeaderDAO {

	List<FinReceiptHeader> getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type);

	long save(FinReceiptHeader receiptHeader, TableType tableType);

	String getReceiptModeStatus(long receiptID, String type);

	void update(FinReceiptHeader receiptHeader, TableType tableType);

	void deleteByReceiptID(long receiptID, TableType tableType);

	FinReceiptHeader getReceiptHeaderByID(long receiptID, String type);

	int geFeeReceiptCount(String reference, String receiptPurpose, long receiptID);

	FinReceiptHeader getServicingFinanceHeader(long receiptID, String userRole, String type);

	List<ReceiptCancelDetail> getReceiptCancelDetailList(Date cancelReqDate, String finReference);

	void updateReceiptStatus(long receiptID, String status);

	long generatedReceiptID(FinReceiptHeader receiptHeader);

	void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type); //Cash Management Change

	void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type); //Cash Management Change

	BigDecimal getTotalCashReceiptAmount(String depositBranch, String type); //Cash Management Change

	boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type, long receiptId); //Cash Management Change

	List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type);

	void updateReference(String extReference, String finReference, String type);

	List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type);

	List<Long> fetchReceiptIdList(String finreference);

	boolean checkInProcessPresentments(String reference);

	boolean checkInProcessReceipts(String refernce, long receiptId);

	public void cancelReceipts(String finReference);

	public boolean isExtRefAssigned(String finReference);

	List<FinReceiptHeader> getReceiptHeadersByRef(String finReference, String type);

	boolean isReceiptDetailsExits(String reference, String paytypeCheque, String chequeNo, String favourNumber,
			String type);

	//### 29-10-2018, Ticket id:124998
	void updateReceiptStatusAndRealizationDate(long receiptID, String status, Date realizationDate);

	List<FinReceiptHeader> getInProcessReceipts(String Reference);

	List<Long> getInProcessReceiptId(String finReference);

	void updateLoanInActive(long receiptId);

	// ### For MultiReceipt
	void saveMultiReceipt(FinReceiptHeader finReceiptHeader, FinReceiptDetail finReceiptDetail,
			Map<String, String> valueMap);

	void updateMultiReceiptLog(FinReceiptQueueLog finReceiptQueue);

	void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	List<Long> getInProcessMultiReceiptRecord();

	boolean checkEarlySettlementInitiation(String reference);

	boolean checkPartialSettlementInitiation(String reference);

	boolean isChequeExists(String reference, String paytypeCheque, String chequeNo, String favourNumber, String type);

	boolean isOnlineExists(String reference, String subReceiptMode, String tranRef, String type);

	String getLoanReferenc(String finReference, String receiptFileName);

	boolean isReceiptsInProcess(String reference, String receiptPurpose, long receiptId, String type);
}