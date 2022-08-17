package com.pennanttech.pff.overdraft.service;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;

public interface VariableOverdraftSchdService {

	boolean isFileNameExist(String fileName);

	VariableOverdraftSchdHeader getHeader(String finReference, String FinEvent, TableType tableType);

	void saveOrUpdate(FinanceDetail fd, String moduleDefiner, TableType tableType);

	void doApprove(FinanceDetail fd);

	void doReject(FinanceDetail fd);

}
