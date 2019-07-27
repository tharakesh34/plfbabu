package com.pennant.backend.service.drawingpower;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pff.external.DrawingPower;

public class DrawingPowerServiceImpl implements DrawingPowerService {
	@Autowired(required = false)
	private DrawingPower drawingPower;

	@Override
	public String doDrawingPowerCheck(String finReference, BigDecimal disbursementAmt) {
		if (drawingPower != null) {
			StringBuilder sb = new StringBuilder();

			BigDecimal drawingPowerAmt = drawingPower.getDrawingPower(finReference);
			
			String drawingPowerAmte = PennantApplicationUtil.amountFormate(drawingPowerAmt, 2);
			String disbursementAmte = PennantApplicationUtil.amountFormate(disbursementAmt, 2);
			sb.append("Drawing power amount : ");
			sb.append(drawingPowerAmte);
			sb.append(", Disbursement amount :");
			sb.append(disbursementAmte);
			
			return sb.toString();
		}
		return null;
	}
}