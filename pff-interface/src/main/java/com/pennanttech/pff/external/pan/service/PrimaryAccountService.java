package com.pennanttech.pff.external.pan.service;

import com.pennant.backend.model.PrimaryAccount;

public interface PrimaryAccountService {

	boolean panValidationRequired();

	PrimaryAccount retrivePanDetails(PrimaryAccount primaryAccount);
}
