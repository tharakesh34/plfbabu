package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinOCRHeader;

public interface FinOCRHeaderDAO {

	FinOCRHeader getFinOCRHeaderByRef(String ref, String type);

	FinOCRHeader getFinOCRHeaderById(long headerId, String type);

	void update(FinOCRHeader finOCRHeader, String type);

	void delete(FinOCRHeader finOCRHeader, String type);

	long save(FinOCRHeader finOCRHeader, String type);

}
