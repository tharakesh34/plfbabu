package com.pennanttech.pff.external;

import com.pennanttech.pff.model.disbursment.DisbursementData;

public interface DisbursementRequest {
	public void sendReqest(Object... params) throws Exception;

	public void sendReqest(DisbursementData disbursementData) throws Exception;
}
