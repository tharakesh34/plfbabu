package com.pennant.backend.ws.dao;

import com.pennant.backend.model.RequestDetail;
import com.pennant.ws.exception.APIException;

public interface APIRequestDAO {
	long saveRequest(RequestDetail request) throws APIException ;

	void updateRequest(RequestDetail request) ;
}
