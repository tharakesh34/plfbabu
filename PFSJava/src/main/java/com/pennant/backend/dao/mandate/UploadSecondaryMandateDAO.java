package com.pennant.backend.dao.mandate;

import java.util.List;

import com.pennant.backend.model.mandate.UploadSecondaryMandate;

public interface UploadSecondaryMandateDAO {
	void save(UploadSecondaryMandate secondaryMandateStatus);

	boolean fileIsExists(String name);

	List<UploadSecondaryMandate> getReportData(long headerId, long userId,String module);
}
