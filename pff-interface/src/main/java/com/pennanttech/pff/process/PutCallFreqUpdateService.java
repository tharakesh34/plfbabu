package com.pennanttech.pff.process;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.finoption.FinOption;

public interface PutCallFreqUpdateService {

	void updatePutCallDates(FinOption finOption, FinanceMain financeMain);

}
