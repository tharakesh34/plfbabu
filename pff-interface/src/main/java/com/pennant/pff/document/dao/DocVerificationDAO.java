package com.pennant.pff.document.dao;

import com.pennant.pff.document.model.DocVerificationAddress;
import com.pennant.pff.document.model.DocVerificationDetail;
import com.pennant.pff.document.model.DocVerificationHeader;

public interface DocVerificationDAO {

	boolean isVerified(String referenceKey);

	long saveHeader(DocVerificationHeader header);

	long saveDetail(DocVerificationDetail docDetails);

	long saveAddress(DocVerificationAddress docAddress);

	DocVerificationHeader getHeader(String referenceKey);

	DocVerificationDetail getDetail(String referenceKey);

	void updateHeader(DocVerificationHeader docHeader);

	void updateDetail(DocVerificationDetail docDetails);

	void updateAddress(DocVerificationAddress docAdress);
}
