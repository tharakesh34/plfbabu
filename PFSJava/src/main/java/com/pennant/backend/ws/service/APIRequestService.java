package com.pennant.backend.ws.service;

import com.pennant.backend.model.RequestDetail;
import com.pennant.ws.exception.APIException;

public interface APIRequestService {
	long saveRequest(RequestDetail request) throws APIException;

	void updateRequest(RequestDetail request);
}
