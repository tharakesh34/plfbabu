package com.pennanttech.pff.external;

import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;

public interface GSTINRequestService {
	
	@Deprecated
	public GSTINInfo gstinValidation(String gstnNumber);

	public GSTINInfo gstinValidation(GSTINInfo gstInfo);
}