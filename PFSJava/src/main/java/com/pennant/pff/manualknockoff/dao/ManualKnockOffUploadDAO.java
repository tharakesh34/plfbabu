package com.pennant.pff.manualknockoff.dao;

import java.util.List;

import com.pennanttech.model.knockoff.ManualKnockOffUpload;

public interface ManualKnockOffUploadDAO {

	List<ManualKnockOffUpload> getDetails(long headerID);

	List<ManualKnockOffUpload> getAllocations(long uploadId, long headerID);

	long save(ManualKnockOffUpload mk);

	void saveAllocations(List<ManualKnockOffUpload> details);

	void update(List<ManualKnockOffUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	String getSqlQuery();

	boolean isInProgress(long headerID, String reference);
}