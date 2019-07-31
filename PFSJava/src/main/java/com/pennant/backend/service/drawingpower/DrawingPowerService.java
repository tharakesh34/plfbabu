package com.pennant.backend.service.drawingpower;

import com.pennant.backend.model.finance.FinanceDetail;

public interface DrawingPowerService {

	String doDrawingPowerCheck(FinanceDetail financeDetail);
}
