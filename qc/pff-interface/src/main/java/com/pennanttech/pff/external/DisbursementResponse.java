package com.pennanttech.pff.external;

public interface DisbursementResponse {
	public void receiveResponse(Object... params) throws Exception;

	public void processResponseFile(Object... params) throws Exception;
}
