package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.external.dms.model.ExternalDocument;

public interface DocumentManagementService {

	DocumentDetails getExternalDocument(String docExternalRefId, String sourceReference);

	String insertExternalDocument(DocumentDetails details);

	List<ExternalDocument> getExternalDocument(ExternalDocument externalDocument);

	List<ExternalDocument> updateExternalDocuments(List<ExternalDocument> externalDocument);

	List<ExternalDocument> addExternalDocument(List<ExternalDocument> externalDocument);

	void updateExternalDocuments(ExternalDocument extDoc, Long custId);

}
