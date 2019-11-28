package com.pennanttech.pff.external;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.model.disbursment.DisbursementData;

public interface DisbursementRequest {
	public void sendReqest(Object... params) throws Exception;

	public void sendReqest(DisbursementData disbursementData, LoggedInUser userDetails) throws Exception;
}
