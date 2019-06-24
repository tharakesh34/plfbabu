package com.pennanttech.pff.external;

import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;

public interface GSTINRequestService {
	public GSTINInfo gstinValidation(FinanceTaxDetail financeTaxDetail);
}
