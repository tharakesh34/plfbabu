package com.pennanttech.util;

import com.pennanttech.ws.log.model.APILogDetail;

public interface APILogDetailDAO {
	long saveLogDetails(APILogDetail aPILogDetail);

	APILogDetail getAPILog(String messageId, String entityCode);

	void updateLogDetails(APILogDetail aPILogDetail);
}
