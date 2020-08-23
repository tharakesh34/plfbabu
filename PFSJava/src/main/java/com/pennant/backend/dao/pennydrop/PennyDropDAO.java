package com.pennant.backend.dao.pennydrop;

import com.pennant.backend.model.pennydrop.BankAccountValidation;
public interface PennyDropDAO {
	void savePennyDropSts(BankAccountValidation bankAccountValidations);

	int getPennyDropCount(String accNumber, String ifsc);

	BankAccountValidation getPennyDropStatusByAcc(String accNum, String ifsc);
}
