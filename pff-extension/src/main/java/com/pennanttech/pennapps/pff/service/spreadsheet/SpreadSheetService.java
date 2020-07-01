package com.pennanttech.pennapps.pff.service.spreadsheet;

import java.util.Map;

import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FinanceDetail;

public interface SpreadSheetService {
	Map<String, Object> prepareDataMap(FinanceDetail financeDetail, CreditReviewDetails creditReviewDetail);
}
