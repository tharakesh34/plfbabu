package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.applicationmaster.ClosureType;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinReceiptQueueLog;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReceiptAPIRequest;
import com.pennant.backend.model.receiptupload.ReceiptUploadDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.pff.receipt.model.CreateReceiptUpload;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptHeaderDAO {

	List<FinReceiptHeader> getReceiptHeaderByRef(String reference, String rcdMaintainSts, String type);

	long save(FinReceiptHeader receiptHeader, TableType tableType);

	String getReceiptModeStatus(long receiptID);

	void update(FinReceiptHeader receiptHeader, TableType tableType);

	void deleteByReceiptID(long receiptID, TableType tableType);

	FinReceiptHeader getReceiptHeaderByID(long receiptID, String type);

	int geFeeReceiptCount(String reference, String receiptPurpose, long receiptID);

	FinReceiptHeader getServicingFinanceHeader(long receiptID, String userRole, String type);

	long generatedReceiptID(FinReceiptHeader receiptHeader);

	void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type); // Cash Management Change

	void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type); // Cash Management Change

	BigDecimal getTotalCashReceiptAmount(String depositBranch, String type); // Cash Management Change

	boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type, long receiptId);

	List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type);

	void updateReference(String extReference, String reference, String type);

	List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type);

	List<Long> fetchReceiptIdList(String reference);

	boolean checkInProcessPresentments(long finID);

	boolean checkInProcessReceipts(long finID, long receiptId);

	public void cancelReceipts(String reference);

	public boolean isExtRefAssigned(String reference);

	List<FinReceiptHeader> getReceiptHeadersByRef(String reference, String type);

	boolean isReceiptDetailsExits(String reference, String paytypeCheque, String chequeNo, String favourNumber,
			String type);

	// ### 29-10-2018, Ticket id:124998
	void updateReceiptStatusAndRealizationDate(long receiptID, String status, Date realizationDate);

	List<Long> getInProcessReceiptId(String reference);

	void updateLoanInActive(long receiptId);

	// ### For MultiReceipt
	void saveMultiReceipt(FinReceiptHeader finReceiptHeader, FinReceiptDetail finReceiptDetail,
			Map<String, String> valueMap);

	void updateMultiReceiptLog(FinReceiptQueueLog finReceiptQueue);

	void batchUpdateMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	void saveMultiReceiptLog(List<FinReceiptQueueLog> finReceiptQueueList);

	List<Long> getInProcessMultiReceiptRecord();

	String getLoanReferenc(String reference, String receiptFileName);

	boolean isReceiptsInProcess(String reference, String receiptPurpose, long receiptId, String type);

	FinReceiptHeader getFinTypeByReceiptID(long receiptID);

	int geFeeReceiptCountByExtReference(String reference, String receiptPurpose, String extReference);

	boolean isReceiptExists(String reference, String type);

	List<Long> isDedupReceiptExists(FinServiceInstruction fsi);

	// ### 15-12-2020, ST#1627
	FinReceiptHeader getNonLanReceiptHeader(long receiptID, String type);

	long getCollectionAgencyId(String collectionAgency);

	void updateCollectionMobAgencyLimit(ReceiptAPIRequest request);

	long saveCollectionAPILog(ReceiptAPIRequest request);

	List<ReceiptAPIRequest> getCollectionAPILog();

	Date getMaxReceiptDateByRef(long finID);

	List<FinReceiptHeader> getReceiptHeaderByID(String reference, String receiptPurpose, Date startDate, Date endDate,
			String type);

	boolean checkPresentmentsInQueue(long finID);

	List<FinReceiptHeader> getInprocessReceipts(long finID);

	List<FinReceiptHeader> getLastMntOn(long receiptID);

	ClosureType getClosureType(String closureType);

	FinReceiptHeader getReceiptById(long receiptId);

	FinReceiptHeader getInititatedReceipts(String reference, String type, String receiptPurpose);

	String getCustCIF(String finRef);

	boolean isReceiptExistsByOnlineAndMob(String reference, long receiptId);

	FinReceiptHeader getReceiptHeader(long receiptId);

	boolean isReceiptExists(ReceiptUploadDetail rud, String type);

	boolean isChequeExists(ReceiptUploadDetail rud);

	boolean isOnlineExists(ReceiptUploadDetail rud);

	int isReceiptExists(String finreference, long receiptId);

	int updateUTRNum(long receiptId, String utrNum, String receiptMode);

	void updateReceiptHeader(long receiptId, String utrNum);

	String getReceiptMode(long receiptId);

	BigDecimal getClosureAmountByFinType(String finType);

	List<FinReceiptHeader> getSettlementReceipts(long finID, Date fromDate);

	BigDecimal getReceiptAmount(Date fromDate, Date toDate);

	void updateExcessAdjustTo(long receiptID, String excessAdjustTo);

	String getReceiptModeStatuByExcessId(long excessID);

	Long getReceiptIdByChequeSerialNo(String chequeSerialNo);

	boolean isReceiptExists(CreateReceiptUpload rud, String type);

	boolean isOnlineExists(CreateReceiptUpload rud);

	boolean isChequeExists(CreateReceiptUpload rud);

	String getClosureTypeValue(Long finID);

	FinReceiptHeader getReceiptById(long receiptId, String type);

	long getMaxReceiptIdFinRef(String finReference);

	FinanceType getRepayHierarchy(FinanceMain fm);

	void updateClosureType(long receiptID, String closureType);
}