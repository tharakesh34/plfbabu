package com.pennant.backend.service.pennydrop;

import com.pennant.backend.model.pennydrop.BankAccountValidation;

public interface PennyDropService {

	void savePennyDropSts(BankAccountValidation bankAccountValidations);

	int getPennyDropCount(String accNumber, String ifsc);

	BankAccountValidation getPennyDropStatusDataByAcc(String accNumber, String ifsc);
}
