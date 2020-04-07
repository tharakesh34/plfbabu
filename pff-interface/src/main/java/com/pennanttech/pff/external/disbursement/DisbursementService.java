package com.pennanttech.pff.external.disbursement;

import java.util.List;

import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public interface DisbursementService {
	public List<DataEngineStatus> sendReqest(DisbursementRequest disbursementReqHeader);
}
