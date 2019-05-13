package com.pennanttech.pff.external;

import com.pennant.backend.model.documentdetails.DocumentDetails;

public interface DocumentManagementService {

	DocumentDetails getExternalDocument(String docExternalRefId, String sourceReference);
}
