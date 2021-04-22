package com.pennanttech.pennapps.pff.service.spreadsheet;

import java.util.Map;

import com.pennant.backend.model.finance.FinanceDetail;

public interface SpreadSheetService {
	Map<String, Object> setSpreadSheetData(Map<String, Object> screenData, FinanceDetail financeDetail);
}
