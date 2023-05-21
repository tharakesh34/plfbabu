package com.pennant.pff.holdmarking.upload.dao;

import java.util.List;

import com.pennant.pff.holdmarking.upload.model.HoldMarkingUpload;

public interface HoldMarkingUploadDAO {

	List<HoldMarkingUpload> getDetails(long id);

	void update(List<HoldMarkingUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isValidateAction(long finId, String action, long headerId);

	int getReference(String reference, String accountNumber, int progressSuccess);

	void delete(String reference, String accountNumber, int progressSuccess);

	long save(HoldMarkingUpload hmu);
}