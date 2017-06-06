package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.exception.InterfaceException;

public interface FinanceCancellationProcess {

	List<FinanceCancellation> fetchCancelledFinancePostings(
			String finReference, String linkedTranId) throws InterfaceException;

}
