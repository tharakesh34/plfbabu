package com.pennant.backend.dao.dms;

import java.util.List;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;

public interface DMSIdentificationDAO {
	public void saveDMSDocumentReferences(List<DocumentDetails> dmsDocumentDetailList);

	public List<DocumentDetails> retrieveDMSDocumentReference();

	public void processSuccessResponse(DocumentDetails dmsDocumentDetails);

	public void processFailure(DocumentDetails dmsDocumentDetails, int configRetryCount);

	public List<DocumentDetails> retrieveDMSDocumentLogs(long dmsId);

	public DocumentManager retrieveDocumentManagerDocImage(long docRefId);
}
