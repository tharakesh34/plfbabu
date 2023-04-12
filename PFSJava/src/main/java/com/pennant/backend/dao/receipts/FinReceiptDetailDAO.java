package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptDetailDAO {

	List<FinReceiptDetail> getReceiptHeaderByID(long receiptID, String type);

	List<FinReceiptDetail> getReceiptDetailForCancelReversalByID(long receiptID, String type);

	long save(FinReceiptDetail receiptDetail, TableType tableType);

	void deleteByReceiptID(long receiptID, TableType tableType);

	void updateReceiptStatus(long receiptID, long receiptSeqID, String status);

	int getReceiptHeaderByBank(String bankCode, String type);

	Date getMaxReceivedDate(long finID);

	List<RepayScheduleDetail> fetchRepaySchduleList(long receiptSeqId);

	BigDecimal getReceiptAmountPerDay(String product, Date receiptDate, String receiptMode, long custID);

	// Cash Management
	void updateFundingAcByReceiptID(long receiptID, long fundingAc, String type);

	List<FinReceiptDetail> getFinReceiptDetailByReference(String finReference);

	boolean isDuplicateReceipt(String finReference, String txnReference, BigDecimal receiptAmount);

	BigDecimal getReceiptAmountPerDay(Date appDate, String paymentType, long custID);

	void cancelReceiptDetails(List<Long> receiptID);

	List<FinReceiptDetail> getDMFinReceiptDetailByFinRef(String finReference, String type);

	BigDecimal getFinReceiptDetailsByFinRef(String finReference);

	// ### 30-10-2018, Ticket id:124998
	void updateReceiptStatusByReceiptId(long receiptId, String status);

	BigDecimal getUtilizedPartPayAmtByDate(FinReceiptHeader receiptHeader, Date startDate, Date endDate);

	Date getMaxReceiptDate(String reference, String receiptPurpose, TableType tableType);

	// ### 17-12-2020, ST#1627
	List<FinReceiptDetail> getNonLanReceiptHeader(long receiptID, String type);

	String getReceiptSourceAccType(String receiptSource);

	Date getMaxValueDate(long finID, String receiptPurpose);

	List<FinReceiptHeader> getReceiptsForDuplicateCheck(long finID, String reference);

	List<FinReceiptHeader> getReceiptsForDuplicateCheck(long finID);

	long getReceiptIDForSP(FinReceiptHeader rh);

	BigDecimal getReceiptAmountPerMonthByFinreference(Date receiptDate, List<String> finreference);

	List<FinReceiptDetail> getFinReceiptDetailByExternalReference(String externalReference);

	void updatePartnerBankByReceiptId(long receiptID, Long partnerBankId);
}
