package com.pennant.backend.service.finance;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.systemmasters.Province;

public interface GSTInvoiceTxnService {
	
	String save(GSTInvoiceTxn gstInvoiceTxn);

	Entity getEntity(String entityCode);

	Entity getEntityByFinDivision(String divisionCode, String type);

	Province getApprovedProvince(String cPCountry, String cPProvince);
}
