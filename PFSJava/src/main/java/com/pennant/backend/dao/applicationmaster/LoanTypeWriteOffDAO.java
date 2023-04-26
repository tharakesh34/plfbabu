package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennanttech.pff.core.TableType;

public interface LoanTypeWriteOffDAO {

	FinTypeWriteOff getLoanWriteOffMappingByID(FinTypeWriteOff writeMapping, String type);

	List<FinTypeWriteOff> getLoanWriteOffMappingListByLoanType(String finType, String type);

	void update(FinTypeWriteOff writeMapping, String type);

	long save(FinTypeWriteOff writeMapping, String type);

	void delete(FinTypeWriteOff writeMapping, String type);

	void delete(long writeOffId, String tableType);

	boolean isDuplicateKey(String loanType, TableType tableType);

	boolean isExistWriteoffCode(long writeoffId, TableType tableType);
}