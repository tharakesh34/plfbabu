package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinOCRCapture;

public interface FinOCRCaptureDAO {
	List<FinOCRCapture> getFinOCRCaptureDetailsByRef(String finReference, String type);

	FinOCRCapture getFinOCRCaptureDetailById(long ID, String type);

	void update(FinOCRCapture finOCRDetail, String type);

	void delete(FinOCRCapture finOCRDetail, String type);

	long save(FinOCRCapture finOCRDetail, String type);

	void deleteList(String finReference, String type);
}
