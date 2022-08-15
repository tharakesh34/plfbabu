package com.pennanttech.pff.overdraft.dao;

import java.util.List;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.model.OverdraftLimitTransation;

public interface OverdraftLimitDAO {

	long createLimit(OverdraftLimit odl, TableType tableType);

	void createTransactions(List<OverdraftLimitTransation> transactions);

	void updateLimit(OverdraftLimit odlh, TableType tableType);

	void deleteLimit(long id, TableType tableType);

	OverdraftLimit getLimit(long finID);

	long createTransaction(OverdraftLimitTransation txn);

	void logLimt(long limitID);

	void updateBalances(OverdraftLimit limit);

	void blockLimit(List<OverdraftLimit> limits);

	void unBlockLimit(long finID);

	void unBlockLimit(OverdraftLimit limit);

	boolean isAutoBlock(long finID);

	boolean isLimitBlock(long finID);

	List<OverdraftLimitTransation> getTransactions(long finID);

	OverdraftLimit getLimit(long finID, String type);
}
