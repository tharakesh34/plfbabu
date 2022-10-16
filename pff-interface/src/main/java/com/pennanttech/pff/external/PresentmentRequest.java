package com.pennanttech.pff.external;

import java.util.List;

public interface PresentmentRequest {
	void sendReqest(List<Long> idList, List<Long> idExcludeEmiList, long headerId, boolean isError, String mandateType,
			String presentmentRef, String bankAccNo) throws Exception;
}
