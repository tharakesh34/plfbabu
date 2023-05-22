package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennanttech.pff.core.TableType;

public interface ReceiptAllocationDetailDAO {

	List<ReceiptAllocationDetail> getAllocationsByReceiptID(long receiptID, String type);

	void deleteByReceiptID(long receiptID, TableType tableType);

	void saveAllocations(List<ReceiptAllocationDetail> allocations, TableType tableType);

	List<ReceiptAllocationDetail> getDMAllocationsByReference(String reference, String type);

	List<ReceiptAllocationDetail> getManualAllocationsByRef(long finID, long curReceiptID);

	List<ReceiptAllocationDetail> getReceiptPaidAmount(long receiptId, String finReference);

	List<ReceiptAllocationDetail> getReceiptAllocDetail(long finID, String allocType);
}
