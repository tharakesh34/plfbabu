package com.pennanttech.pennapps.pff.service.hook;

import java.util.List;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;

/**
 * Post validation hook that uses to raise custom auto deviations.
 */
public interface PostDeviationHook {
	/**
	 * Gets the custom auto deviations.
	 * 
	 * @param financeDetail
	 *            The finance detail object.
	 * @return The custom auto deviations.
	 */
	List<FinanceDeviations> raiseDeviations(FinanceDetail financeDetail);
}
