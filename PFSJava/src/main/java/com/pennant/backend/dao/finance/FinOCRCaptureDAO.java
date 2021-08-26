package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinOCRCapture;

public interface FinOCRCaptureDAO {
	List<FinOCRCapture> getFinOCRCaptureDetailsByRef(long finID, String type);

	void update(FinOCRCapture finOCRDetail, String type);

	void delete(FinOCRCapture finOCRDetail, String type);

	long save(FinOCRCapture finOCRDetail, String type);

	void deleteList(long finID, String type);
}
