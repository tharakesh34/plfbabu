package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;

public interface DrawingPowerService {

	String doDrawingPowerCheck(String finReference, BigDecimal disbursementAmt);
}
