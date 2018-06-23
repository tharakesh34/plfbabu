package com.pennant.backend.dao.dms;

import java.util.List;

import com.pennanttech.model.dms.DMSDocumentDetails;

public interface DMSIdentificationDAO {
	public void saveDMSDocumentReferences(List<DMSDocumentDetails> dmsDocumentDetailList);
	public List<DMSDocumentDetails> retrieveDMSDocumentReference();
	public void processSuccessResponse(DMSDocumentDetails dmsDocumentDetails,DMSDocumentDetails responseDmsDocumentDetails);
	public void processFailure(DMSDocumentDetails dmsDocumentDetails, int configRetryCount);
}
