package com.pennant.backend.dao.ddapayments;

import java.util.List;

import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.finance.DdaPresentment;

public interface DDARepresentmentDAO {
	void save(DDAPayments ddaPaymentInitiation);

	void logRepresentmentData(DDAPayments ddaRepresentments);
	
	void represent(List<DdaPresentment> list);
}
