package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennanttech.pff.core.TableType;

public interface LoanTypeKnockOffDAO {

	FinTypeKnockOff getLoanKnockOffMappingByID(FinTypeKnockOff kCodeMapping, String type);

	List<FinTypeKnockOff> getLoanKnockOffMappingListByLoanType(String finType, String type);

	void update(FinTypeKnockOff kCodeMapping, String type);

	long save(FinTypeKnockOff kCodeMapping, String type);

	void delete(FinTypeKnockOff kCodeMapping, String type);

	void delete(long knockOffId, String tableType);

	boolean isDuplicateKey(String loanTypeCode, TableType tableType);

	boolean isExistKnockoffCode(long knockoffId, TableType tableType);
}
