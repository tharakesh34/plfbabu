package com.pennanttech.pff.external;

import java.util.List;

public interface PresentmentRequest {
	void sendReqest(List<Long> idList, long headerId, boolean isError, boolean isPDC) throws Exception;
}
