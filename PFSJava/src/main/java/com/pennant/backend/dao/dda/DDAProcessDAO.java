package com.pennant.backend.dao.dda;

import com.pennant.backend.model.finance.DDAProcessData;
import com.pennant.backend.model.rmtmasters.FinanceType;

public interface DDAProcessDAO {

	long save(DDAProcessData ddaProcessData);
	
	void updateActiveStatus(String finrefrence);

	DDAProcessData getDDADetailsById(String finReference, String reqTypeValidate);

	FinanceType getFinTypeDetails(String finType);
	
	DDAProcessData getDDADetailsByReference(String finReference, String reqTypeValidate);
	
}
