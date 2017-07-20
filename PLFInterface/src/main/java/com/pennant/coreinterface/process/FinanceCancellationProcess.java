package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennanttech.pennapps.core.InterfaceException;

public interface FinanceCancellationProcess {

	List<FinanceCancellation> fetchCancelledFinancePostings(
			String finReference, String linkedTranId) throws InterfaceException;

}
