package com.pennant.backend.service.finance;

import com.pennant.backend.model.finance.TaxHeader;

public interface TaxHeaderDetailsService {

	TaxHeader saveOrUpdate(TaxHeader taxHeader, String tableType, String auditTranType);

	void delete(TaxHeader taxHeader, String type);

	void deleteTaxDetails(long headerId, String type);
}
