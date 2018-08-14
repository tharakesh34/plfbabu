package com.pennanttech.pff.incomeexpensedetail.dao;

import java.util.List;
import java.util.Map;

import com.pennanttech.pff.organization.model.IncomeExpenseDetail;

public interface IncomeExpenseDetailDAO {

	long save(IncomeExpenseDetail incomeExpenseDetail, String tableType);

	void update(IncomeExpenseDetail incomeExpenseDetail, String tableType);

	void delete(Long id, String tableType);

	List<IncomeExpenseDetail> getIncomeExpenseList(Long id, String incomeType, String type);

	Map<String, Object> getTotal(Long custId, Integer financialYear);

}
