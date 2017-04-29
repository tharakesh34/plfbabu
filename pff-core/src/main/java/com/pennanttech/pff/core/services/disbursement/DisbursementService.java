package com.pennanttech.pff.core.services.disbursement;

public interface DisbursementService {
	public void sendDisbursementRequest(Object... params) throws Exception;

	public Object receiveDisbursementResponse(Object object) throws Exception;
}
