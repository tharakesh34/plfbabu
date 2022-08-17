package com.pennanttech.pennapps.pff.extension.feature;

import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.FinanceDetail;

public interface AgreementGenerationService {

	void setAdditionalRequiredFields(AgreementDetail agreement, FinanceDetail detail);
}
