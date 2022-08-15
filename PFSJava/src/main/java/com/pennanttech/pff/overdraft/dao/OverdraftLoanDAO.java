package com.pennanttech.pff.overdraft.dao;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.overdraft.model.OverdraftDTO;

public interface OverdraftLoanDAO {

	OverdraftDTO getLoanDetails(long finID);

	FinanceMain getLoanBasicDetails(long finID);

	OverdraftDTO getChargeConfig(long finID);

	long getOverdraftTxnChrgFeeType(String finType);

}
