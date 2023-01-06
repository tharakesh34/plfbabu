package com.pennant.backend.dao.excessheadmaster;

import com.pennant.backend.model.excessheadmaster.FinExcessTransfer;
import com.pennanttech.pff.core.TableType;

public interface FinExcessTransferDAO {

	FinExcessTransfer getExcessTransferByFinId(long finId, long transferId, String type);

	String save(FinExcessTransfer finExcessTransfer, TableType tableType);

	void update(FinExcessTransfer finExcessTransfer, TableType tableType);

	FinExcessTransfer getExcessTransferByTransferId(long transferId, String string);

	boolean isIdExists(long transferId);

	void delete(FinExcessTransfer finExcessTransfer, TableType mainTab);

	boolean isFinReceferenceExist(String finReference, String type);

}
