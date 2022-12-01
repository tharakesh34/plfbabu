package com.pennant.pff.document.service;

import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.pff.document.model.DocVerificationHeader;

public interface DocumentValidation {

	boolean isVerified(String docNumber, DocType docType);

	DocVerificationHeader validate(DocType docType, DocVerificationHeader header);

	DocVerificationHeader validateOTP(DocVerificationHeader dh, String value);
}
