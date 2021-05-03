package com.pennanttech.pennapps.pff.extension.feature;

import com.pennant.backend.model.systemmasters.StatementOfAccount;

public interface SOAExtensionService {

	public int getMortoriumTerms(String finReference);

	void setRequiredFields(StatementOfAccount statementOfAccount);
}
