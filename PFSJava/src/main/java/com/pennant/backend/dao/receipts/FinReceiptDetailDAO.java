package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptDetailDAO {

	List<FinReceiptDetail> getReceiptHeaderByID(long receiptID, String type);

	long save(FinReceiptDetail receiptDetail, TableType tableType);

	void deleteByReceiptID(long receiptID, TableType tableType);

	void updateReceiptStatus(long receiptID, long receiptSeqID, String status);

	int getReceiptHeaderByBank(String bankCode, String type);

	List<FinReceiptDetail> getFinReceiptDetailByFinRef(String finReference);

	Date getMaxReceivedDateByReference(String finReference);

	//Cash Management
	void updateFundingAcByReceiptID(long receiptID, long fundingAc, String type);

	List<FinReceiptDetail> getFinReceiptDetailByExternalReference(String finReference);

	void cancelReceiptDetails(List<Long> receiptID);

	List<FinReceiptDetail> getFinReceiptDetailByFinReference(String finReference);

	List<FinReceiptDetail> getDMFinReceiptDetailByFinRef(String finReference, String type);

	BigDecimal getFinReceiptDetailsByFinRef(String finReference);
	
	//### 29-10-2018, Ticket id:124998
	boolean isFinReceiptDetailExitsByFavourNo(FinReceiptHeader receiptHeader, String purpose);
	//### 29-10-2018, Ticket id:124998
	boolean isFinReceiptDetailExitsByTransactionRef(FinReceiptHeader receiptHeader, String purpose);
	//### 29-10-2018, Ticket id:124998
	long getReceiptIdByReceiptDetails(FinReceiptHeader receiptHeader, String purpose);

	//### 30-10-2018, Ticket id:124998
	void updateReceiptStatusByReceiptId(long receiptId, String status);

	BigDecimal getUtilizedPartPayAmtByDate(FinReceiptHeader receiptHeader, Date startDate, Date endDate);
}
