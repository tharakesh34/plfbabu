package com.pennanttech.pff.external;

import com.pennant.backend.model.pennydrop.BankAccountValidation;

public interface BankAccountValidationService {

	public boolean validateBankAccount(BankAccountValidation bankAccountValidations);

}
