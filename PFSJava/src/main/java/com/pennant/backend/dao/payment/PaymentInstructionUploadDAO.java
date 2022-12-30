package com.pennant.backend.dao.payment;

import java.util.List;

import com.pennant.backend.model.payment.PaymentInstUploadDetail;

public interface PaymentInstructionUploadDAO {

	void update(List<Long> headerIds, String errorCode, String errorDesc, int progress);

	List<PaymentInstUploadDetail> getDetails(long headerID);

	void update(List<PaymentInstUploadDetail> detailsList);

	String getSqlQuery();

	void update(PaymentInstUploadDetail detail);

}
