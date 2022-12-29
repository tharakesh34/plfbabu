package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennanttech.pff.core.TableType;

public interface ReceiptAllocationDetailDAO {

	List<ReceiptAllocationDetail> getAllocationsByReceiptID(long receiptID, String type);

	void deleteByReceiptID(long receiptID, TableType tableType);

	void saveAllocations(List<ReceiptAllocationDetail> allocations, TableType tableType);

	List<ReceiptAllocationDetail> getDMAllocationsByReference(String reference, String type);

	List<ReceiptAllocationDetail> getManualAllocationsByRef(long finID, long curReceiptID);

	BigDecimal getPartPayAmount(long finID, Date fromDate, Date toDate);
}
