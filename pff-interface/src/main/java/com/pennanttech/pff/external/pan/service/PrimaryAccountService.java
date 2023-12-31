package com.pennanttech.pff.external.pan.service;

import com.pennant.backend.model.PrimaryAccount;

public interface PrimaryAccountService {

	boolean panValidationRequired();

	PrimaryAccount retrivePanDetails(PrimaryAccount primaryAccount);

	void savePanVerificationDetails(PrimaryAccount primaryAccount);

	int isPanVerified(String panNo);

	PrimaryAccount getPrimaryAccountDetails(String primaryID);
}
