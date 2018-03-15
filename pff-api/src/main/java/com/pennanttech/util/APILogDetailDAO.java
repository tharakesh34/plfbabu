package com.pennanttech.util;

import com.pennanttech.ws.log.model.APILogDetail;

public interface APILogDetailDAO {
	void saveLogDetails(APILogDetail aPILogDetail);

	APILogDetail getLogByMessageId(String messageId);

}
