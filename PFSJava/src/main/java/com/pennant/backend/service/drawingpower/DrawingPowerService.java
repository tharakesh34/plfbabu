package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinanceDetail;

public interface DrawingPowerService {

	String doDrawingPowerCheck(FinanceDetail financeDetail, String moduleDefiner);

	String doRevolvingValidations(FinanceDetail financeDetail);
	
	BigDecimal getDrawingPower(String finreference);
}
