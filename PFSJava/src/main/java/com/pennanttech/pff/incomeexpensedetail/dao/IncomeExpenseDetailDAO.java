package com.pennanttech.pff.incomeexpensedetail.dao;

import java.util.List;

import com.pennanttech.pff.organization.school.model.IncomeExpenseDetail;

public interface IncomeExpenseDetailDAO {
	
	long save(IncomeExpenseDetail incomeExpenseDetail, String tableType);
	
	void update(IncomeExpenseDetail incomeExpenseDetail, String tableType);
	
	void delete(Long id	, String tableType);
	
	List<IncomeExpenseDetail> getCoreIncomeList(Long id, String incomeType, String type);
	
	List<IncomeExpenseDetail> getNonCoreIncomeList(Long id, String incomeType, String type);
	
	List<IncomeExpenseDetail> getExpenseList(Long id, String incomeType, String type);

}
