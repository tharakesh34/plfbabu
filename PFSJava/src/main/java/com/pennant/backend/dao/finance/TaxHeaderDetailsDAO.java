package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.Taxes;

public interface TaxHeaderDetailsDAO {

	List<Taxes> getTaxDetailById(long headerId, String type);

	void update(Taxes taxes, String type);

	void update(TaxHeader taxHeader, String type);

	void delete(long headerId, String type);

	long save(TaxHeader taxes, String type);

	void delete(TaxHeader taxes, String type);

	void saveTaxes(List<Taxes> taxes, String type);

	List<Long> getHeaderIdsByReceiptId(long receiptId, String type);

	void deleteById(long id, String type);

	TaxHeader getTaxHeaderDetailsById(long headerId, String type);

	long save(Taxes taxes, String type);

}