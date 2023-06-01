package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUpload;
import com.pennant.pff.model.paymentmethodupload.PaymentMethodUploadHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface PaymentMethodUploadDAO {

	boolean isFileExists(String name);

	void updateRemarks(PaymentMethodUploadHeader header);

	List<PaymentMethodUpload> getChangePaymentUploadDetails(long batchId);

	int logRcUpload(List<ErrorDetail> errDetail, Long id);

	void updateDeRemarks(DataEngineStatus deStatus);

	List<FinanceMain> getFinanceMain(long batchId);

	long saveHeader(String fileName);

	void updateChangePaymentDetails(PaymentMethodUpload paymentUpload);

	void updateFinRepaymethod(PaymentMethodUpload changePayment);

	boolean isMandateIdExists(long mandateId);

	boolean isValidMandate(PaymentMethodUpload pmu);

}
