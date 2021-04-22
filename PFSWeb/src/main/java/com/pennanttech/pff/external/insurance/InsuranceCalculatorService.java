package com.pennanttech.pff.external.insurance;

import com.pennanttech.pff.model.InsPremiumCalculatorRequest;
import com.pennanttech.pff.model.InsPremiumCalculatorResponse;

public interface InsuranceCalculatorService {

	public InsPremiumCalculatorResponse calculatePremiumAmt(InsPremiumCalculatorRequest insPremiumCalculatorRequest);
}
