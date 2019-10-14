package com.pennanttech.pff.core;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.pennant.app.util.GSTCalculator;

@Component
public class CalculatorServiceImpl implements CalculatorService {

	public CalculatorServiceImpl() {
		super();
	}

	@Override
	public BigDecimal getTotalGST(String finReferece, BigDecimal amount, TaxComponent taxComponent) {
		return GSTCalculator.getTotalGST(finReferece, amount, taxComponent.getTaxComponent());
	}

}
