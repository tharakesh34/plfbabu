package com.pennant.pff.receipt.dao;

import java.util.List;

import com.pennant.pff.receipt.model.CreateReceiptUpload;

public interface CreateReceiptUploadDAO {

	List<CreateReceiptUpload> getDetails(long headerID);

	List<CreateReceiptUpload> getAllocations(long uploadId, long headerID);

	void saveAllocations(List<CreateReceiptUpload> details);

	void update(List<CreateReceiptUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	List<String> isDuplicateExists(CreateReceiptUpload rud);

	String getLoanReference(String finReference, String fileName);

	long save(CreateReceiptUpload cru);
}
