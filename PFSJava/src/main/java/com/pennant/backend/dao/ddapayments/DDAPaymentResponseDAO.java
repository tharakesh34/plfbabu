package com.pennant.backend.dao.ddapayments;

import java.util.List;

import com.pennant.backend.model.ddapayments.DDAPayments;

public interface DDAPaymentResponseDAO {

	List<DDAPayments> getDDAPaymentResDetails();
	
	void deleteDDAPaymentResDetails();

	void logDDAPaymentResDetails(List<DDAPayments> ddaPaymentResList);
}
