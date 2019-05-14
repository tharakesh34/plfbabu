package com.pennanttech.pff.external;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface HunterService {
	String getHunterStatus(FinanceDetail detail) throws InterfaceException, Exception;
}
