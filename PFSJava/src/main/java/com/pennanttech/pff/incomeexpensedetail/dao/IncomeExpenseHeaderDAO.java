package com.pennanttech.pff.incomeexpensedetail.dao;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public interface IncomeExpenseHeaderDAO {

    IncomeExpenseHeader getIncomeExpenseHeader(long id, String type);
	
	long save(IncomeExpenseHeader incomeExpenseHeader, TableType tableType);
	
	void update(IncomeExpenseHeader incomeExpenseHeader, TableType tableType);
	
	void delete(IncomeExpenseHeader incomeExpenseHeader	, TableType tableType);
	
	boolean isExist(String  custCif, int financialYear, String type);
}
