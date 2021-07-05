package com.pennanttech.pff.external.disbursement;

import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public interface OnlineDisbursement {
	DataEngineStatus processRequest(DisbursementRequest request);
}
