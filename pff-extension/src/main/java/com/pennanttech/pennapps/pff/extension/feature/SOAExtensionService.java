package com.pennanttech.pennapps.pff.extension.feature;

import java.util.List;

import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;

public interface SOAExtensionService {

	public int getMortoriumTerms(String finReference);

	void setRequiredFields(StatementOfAccount statementOfAccount);

	void setRcvAndBounceWaivers(List<FeeWaiverDetail> feeWaiverDetailList,
			List<SOATransactionReport> soaTransactionReports);

}
