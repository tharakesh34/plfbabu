package com.pennanttech.pff.external.pan.dao;

import com.pennant.backend.model.PrimaryAccount;

public interface PrimaryAccountDAO {

	void savePanVerificationDetails(PrimaryAccount primaryAccount);

	int isPanVerified(String panNo);

	PrimaryAccount getPrimaryAccountDetails(String primaryID);

}
