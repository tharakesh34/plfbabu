package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.LowerTaxDeduction;

public interface LowerTaxDeductionDAO {

	List<LowerTaxDeduction> getLowerTaxDeductionDetails(long finID, String type);

	void save(LowerTaxDeduction ltd, String type);

	void update(LowerTaxDeduction ltd, String type);

	void delete(LowerTaxDeduction ltd, String type);

}
