package com.pennanttech.pff.external.insurance;

public interface InsuranceCalculatorService<PLCalculatorResponse> {

	public PLCalculatorResponse getPLCalculation(String finReference);
}
