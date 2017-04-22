package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennanttech.pff.core.TableType;

public interface ReceiptAllocationDetailDAO {

	List<ReceiptAllocationDetail> getAllocationsByReceiptID(long receiptID, String type);
	void deleteByReceiptID(long receiptID, TableType tableType);
	void saveAllocations(List<ReceiptAllocationDetail> allocations, TableType tableType);

}
