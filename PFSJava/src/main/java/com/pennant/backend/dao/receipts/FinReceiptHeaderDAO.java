package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptHeaderDAO {

	FinReceiptHeader getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type);
	long save(FinReceiptHeader receiptHeader, TableType tableType);
	void update(FinReceiptHeader receiptHeader, TableType tableType);
	void deleteByReceiptID(long receiptID, TableType tableType);
	FinReceiptHeader getReceiptHeaderByID(long receiptID, String type);
	int geFeeReceiptCount(String reference, String receiptPurpose,long receiptID);
	long generatedReceiptID(FinReceiptHeader receiptHeader);
	void updateDepositProcessByReceiptID(long receiptID, boolean depositProcess, String type);	//Cash Management Change
	void updateDepositBranchByReceiptID(long receiptID, String depositBranch, String type); //Cash Management Change
	BigDecimal getTotalReceiptAmount(String depositBranch, List<String> paymentTypes, String type); //Cash Management Change
	boolean isReceiptCancelProcess(String depositBranch, List<String> paymentTypes, String type, long receiptId); //Cash Management Change
	
	List<FinReceiptHeader> getUpFrontReceiptHeaderByID(List<Long> receipts, String type);
	void updateReference(String extReference, String finReference, String type);
	List<FinReceiptHeader> getUpFrontReceiptHeaderByExtRef(String extRef, String type);
}
