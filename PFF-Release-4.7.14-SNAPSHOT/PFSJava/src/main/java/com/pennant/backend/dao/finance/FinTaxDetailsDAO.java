package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinTaxDetails;

public interface FinTaxDetailsDAO {
	void save(FinTaxDetails finTaxDetails, String tableType);
	void deleteByFeeID(long feeId, String tableType);
	FinTaxDetails getFinTaxByFeeID(long feeId, String tableType);
	void update(FinTaxDetails finTaxDetails, String tableType);
	void delete(FinTaxDetails finTaxDetails, String type);
}
