package com.pennant.backend.service.finance.impl;

import java.util.List;

import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.service.finance.DPDEnquiryService;

public class DPDEnquiryServiceImpl implements DPDEnquiryService {

	private FinStatusDetailDAO finStatusDetailDAO;

	public FinStatusDetailDAO getFinStatusDetailDAO() {
		return finStatusDetailDAO;
	}

	public void setFinStatusDetailDAO(FinStatusDetailDAO finStatusDetailDAO) {
		this.finStatusDetailDAO = finStatusDetailDAO;
	}

	@Override
	public List<FinStatusDetail> getFinStatusDetailByRefId(long finID) {
		return finStatusDetailDAO.getFinStatusDetailByRefId(finID);
	}

}
