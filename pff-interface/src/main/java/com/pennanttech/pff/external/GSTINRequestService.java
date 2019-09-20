package com.pennanttech.pff.external;

import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;

public interface GSTINRequestService {
	public GSTINInfo gstinValidation(String gstnNumber);

	public GSTINInfo gstinValidation(String gstnNumber, String cif, String loginId);
}