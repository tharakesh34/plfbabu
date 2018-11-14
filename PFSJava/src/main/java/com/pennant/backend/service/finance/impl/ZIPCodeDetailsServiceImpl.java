package com.pennant.backend.service.finance.impl;

import com.pennant.backend.dao.finance.ZIPCodeDetailsDAO;
import com.pennant.backend.model.finance.ZIPCodeDetails;
import com.pennant.backend.service.finance.ZIPCodeDetailsService;

public class ZIPCodeDetailsServiceImpl implements ZIPCodeDetailsService {

	private ZIPCodeDetailsDAO zIPCodeDetailsDAO;

	/**
	 * Method for fetch ZIPCode Details of corresponding pinCode
	 * 
	 * @param pinCode
	 * 
	 * @return ZIPCodeDetails
	 */

	@Override
	public ZIPCodeDetails getZIPCodeDetails(String pinCode) {

		return zIPCodeDetailsDAO.getPinCodeDetail(pinCode);
	}

	public void setzIPCodeDetailsDAO(ZIPCodeDetailsDAO zIPCodeDetailsDAO) {
		this.zIPCodeDetailsDAO = zIPCodeDetailsDAO;
	}

}
