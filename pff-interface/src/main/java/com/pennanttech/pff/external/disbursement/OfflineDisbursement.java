package com.pennanttech.pff.external.disbursement;

import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.disbursement.PaymentType;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public interface OfflineDisbursement {
	DataEngineStatus downloadFile(String configName, DisbursementRequest request, PaymentType disbursementType);
}
