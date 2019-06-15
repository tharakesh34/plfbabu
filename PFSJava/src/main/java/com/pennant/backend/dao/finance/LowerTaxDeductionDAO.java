package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.LowerTaxDeduction;

public interface LowerTaxDeductionDAO {

	List<LowerTaxDeduction> getLowerTaxDeductionDetails(String finReference, String type);

	void save(LowerTaxDeduction lowerTaxDeduction, String type);

	void update(LowerTaxDeduction lowerTaxDeduction, String type);

	void delete(LowerTaxDeduction lowerTaxDeduction, String type);

}
