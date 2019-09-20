package com.pennanttech.pff.external;

import com.pennant.backend.model.beneficiary.Beneficiary;

public interface AccountValidationService {
	Beneficiary validateAccount(Beneficiary beneficiary, String loginId);
}
