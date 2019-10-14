package com.pennanttech.pff.external;

import com.pennant.backend.model.pennydrop.PennyDropStatus;

public interface BankAccountValidationService {

	public boolean getBankTransactionDetails(PennyDropStatus pennyDropStatus);

}
