package com.pennanttech.pff.external;

import java.util.List;

public interface PresentmentProcess {
	void sendReqest(List<Long> idList, long headerId, boolean isError) throws Exception;

	void receiveResponse();
}
