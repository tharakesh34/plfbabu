package com.pennant.backend.dao.payment;

import java.util.List;

import com.pennant.backend.model.payment.PaymentInstUploadDetail;
import com.pennant.pff.upload.model.FileUploadHeader;

public interface PaymentInstructionUploadDAO {

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	List<PaymentInstUploadDetail> getDetails(long headerID);

	void update(List<PaymentInstUploadDetail> detailsList);

	String getSqlQuery();

	void update(PaymentInstUploadDetail detail);

	PaymentInstUploadDetail getDetails(long headerID, long detailId);

	List<Integer> getHeaderStatusCnt(long uploadId);

	void uploadHeaderStatusCnt(FileUploadHeader header);

}
