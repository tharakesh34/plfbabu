package com.pennant.backend.dao.ddapayments;

import java.util.List;

import com.pennant.backend.model.ddapayments.DDAPayments;

public interface DDAPaymentInitiateDAO {

	void saveDDAPaymentInitDetails(DDAPayments ddaPaymentInitiation);

	void deleteDDAPaymentInitDetails();

	List<DDAPayments> fetchDDAInitDetails();

	void logDDAPaymentInitDetails(List<DDAPayments> backUpDDAPaymentList);

}
