package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinOCRDetail;

public interface FinOCRDetailDAO {
	List<FinOCRDetail> getFinOCRDetailsByHeaderID(long headerID, String type);

	FinOCRDetail getFinOCRDetailById(long detailID, String type);

	void update(FinOCRDetail finOCRDetail, String type);

	void delete(FinOCRDetail finOCRDetail, String type);

	long save(FinOCRDetail finOCRDetail, String type);

	void deleteList(long headerId, String type);
}
