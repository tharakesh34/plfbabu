package com.pennant.backend.service.mandate;

import java.util.List;

import com.pennant.backend.model.mandate.UploadSecondaryMandate;

public interface UploadSecondaryMandateService {
	void save(UploadSecondaryMandate uploadSecondaryMandate);

	boolean fileIsExists(String name);

	List<UploadSecondaryMandate> getReportData(long headerId, long userId,String module);
}
