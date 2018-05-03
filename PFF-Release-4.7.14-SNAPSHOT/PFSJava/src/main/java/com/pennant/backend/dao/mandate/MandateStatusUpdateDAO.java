package com.pennant.backend.dao.mandate;

import com.pennant.backend.model.mandate.MandateStatusUpdate;

public interface MandateStatusUpdateDAO {
	MandateStatusUpdate getFileUploadById(long id, String type);

	void update(MandateStatusUpdate mandateStatusUpdate, String type);

	void delete(MandateStatusUpdate mandateStatusUpdate, String type);

	long save(MandateStatusUpdate mandateStatusUpdate, String type);

	int getFileCount(String fileName);
}
