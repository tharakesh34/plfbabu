package com.pennanttech.service;

import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitTransactionDetail;

public interface LimitWebService {

	public LimitStructure getCustomerLimitStructure(String structureCode);
	
	public LimitHeader getLimitSetup(LimitHeader limitHeader);
	
	public LimitHeader createLimitSetup(LimitHeader limitHeader);
	
	public WSReturnStatus updateLimitSetup(LimitHeader limitHeader);
	
	public WSReturnStatus reserveLimit(LimitTransactionDetail limitTransDetail);
	
	public WSReturnStatus cancelLimitReserve(LimitTransactionDetail limitTransDetail);
}
