package com.pennant.pff.document.dao;

import com.pennant.app.util.MasterDefUtil.DocType;
import com.pennant.pff.document.model.DocVerificationAddress;
import com.pennant.pff.document.model.DocVerificationDetail;
import com.pennant.pff.document.model.DocVerificationHeader;

public interface DocVerificationDAO {

	boolean isVerified(String docNumber, DocType docType);

	long saveHeader(DocVerificationHeader header);

	void saveDetail(DocVerificationDetail docDetails);

	void saveAddress(DocVerificationAddress docAddress);

	DocVerificationHeader getHeader(String referenceKey, String docType);

	DocVerificationDetail getDetail(String referenceKey);

	void updateHeader(DocVerificationHeader docHeader);

	void updateDetail(DocVerificationDetail docDetails);

	void updateAddress(DocVerificationAddress docAdress);
}
