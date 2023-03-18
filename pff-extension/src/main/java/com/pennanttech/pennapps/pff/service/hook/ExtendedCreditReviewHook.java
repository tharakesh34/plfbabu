package com.pennanttech.pennapps.pff.service.hook;

import com.pennant.backend.model.finance.FinanceDetail;

public interface ExtendedCreditReviewHook {

	public void saveExtCreditReviewDetails(FinanceDetail afd);
}
