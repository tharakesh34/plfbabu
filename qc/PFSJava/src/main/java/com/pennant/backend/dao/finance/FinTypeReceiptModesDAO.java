package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.financemanagement.FinTypeReceiptModes;

public interface FinTypeReceiptModesDAO {
	void save(FinTypeReceiptModes finTypeReceiptModes, String type);
	void delete(String finType, String receiptMode, String type);
	void update(FinTypeReceiptModes finTypeReceiptModes, String type);
	void deleteList(String finType, String type);
	List<FinTypeReceiptModes> getReceiptModesByFinType(String finType, String type);
	FinTypeReceiptModes getFinTypeReceiptModes(String finType, String receiptMode, String type);

}
