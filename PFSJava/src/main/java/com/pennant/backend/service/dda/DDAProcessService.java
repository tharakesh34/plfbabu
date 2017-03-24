package com.pennant.backend.service.dda;

import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.rmtmasters.FinanceType;

public interface DDAProcessService {

	long save(DDAProcessData ddaProcessData);
	
	void updateActiveStatus(String finrefrence);

	DDAProcessData getDDADetailsById(String finReference, String reqTypeValidate);
	
	DDAProcessData getDDADetailsByReference(String finReference, String reqTypeValidate);
	
	FinanceType getFinTypeDetails(String finType);
}
