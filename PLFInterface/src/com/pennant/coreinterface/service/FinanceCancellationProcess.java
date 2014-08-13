package com.pennant.coreinterface.service;

import java.util.List;

import com.pennant.coreinterface.model.FinanceCancellation;

public interface FinanceCancellationProcess {

	List<FinanceCancellation> fetchCancelledFinancePostings(
			String finReference, String linkedTranId) throws Exception;

}
