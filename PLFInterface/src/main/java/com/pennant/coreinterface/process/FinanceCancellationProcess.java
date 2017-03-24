package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.exception.PFFInterfaceException;

public interface FinanceCancellationProcess {

	List<FinanceCancellation> fetchCancelledFinancePostings(
			String finReference, String linkedTranId) throws PFFInterfaceException;

}
