package com.pennanttech.pff.external;

import com.pennant.backend.model.finance.FinanceDetail;

public interface DocumentVerificationService {
	public void saveOrUpdateDocuments(FinanceDetail detail);
}
