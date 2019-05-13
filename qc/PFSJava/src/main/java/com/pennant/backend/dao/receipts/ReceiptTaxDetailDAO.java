package com.pennant.backend.dao.receipts;

import com.pennant.backend.model.finance.ReceiptTaxDetail;
import com.pennanttech.pff.core.TableType;

public interface ReceiptTaxDetailDAO {

	ReceiptTaxDetail getTaxDetailByID(long receiptSeqID, String type);

	void save(ReceiptTaxDetail taxDetail, TableType tableType);

	void delete(long receiptSeqID, TableType tableType);

	void deleteByReceiptID(long receiptID, TableType tableType);

}
