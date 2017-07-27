package com.pennant.backend.dao.receipts;

import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptHeaderDAO {

	FinReceiptHeader getReceiptHeaderByRef(String finReference, String rcdMaintainSts, String type);
	long save(FinReceiptHeader receiptHeader, TableType tableType);
	void update(FinReceiptHeader receiptHeader, TableType tableType);
	void deleteByReceiptID(long receiptID, TableType tableType);
	FinReceiptHeader getReceiptHeaderByID(long receiptID, String type);
	int geFeeReceiptCount(String reference, String receiptPurpose,long receiptID);
}
