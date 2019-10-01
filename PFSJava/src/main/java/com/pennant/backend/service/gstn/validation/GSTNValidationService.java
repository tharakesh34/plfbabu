package com.pennant.backend.service.gstn.validation;

import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;

public interface GSTNValidationService {

	GSTINInfo validateGSTNNumber(GSTINInfo gstinInfo);

}
