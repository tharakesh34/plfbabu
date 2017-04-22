package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennanttech.pff.core.TableType;

public interface FinReceiptDetailDAO {

	List<FinReceiptDetail> getReceiptHeaderByID(long receiptID, String type);
	long save(FinReceiptDetail receiptDetail, TableType tableType);
	void deleteByReceiptID(long receiptID, TableType tableType);

}
