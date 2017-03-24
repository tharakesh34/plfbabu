package com.pennant.backend.ws.service;

import com.pennant.backend.model.RequestDetail;
import com.pennant.backend.ws.dao.APIRequestDAO;
import com.pennant.ws.exception.APIException;

public class APIRequestServiceImpl implements APIRequestService {

	private APIRequestDAO apiRequestDAO;

	@Override
	public long saveRequest(RequestDetail request) throws APIException{
		return apiRequestDAO.saveRequest(request);
	}

	@Override
	public void updateRequest(RequestDetail request) {
		apiRequestDAO.updateRequest(request);
	}

	public void setApiRequestDAO(APIRequestDAO apiRequestDAO) {
		this.apiRequestDAO = apiRequestDAO;
	}

}
