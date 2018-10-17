package com.pennant.backend.service.finance.impl;

import com.pennant.backend.dao.finance.DemoGraphicDetailsDAO;
import com.pennant.backend.model.finance.DemographicDetails;
import com.pennant.backend.service.finance.DemoGraphicDetailsService;

public class DemoGraphicDetailsServiceImpl implements DemoGraphicDetailsService {

	private DemoGraphicDetailsDAO demoGraphicDetailsDAO;

	/**
	 * Method for fetch DemoGraphic Details of corresponding pinCode
	 * 
	 * @param pinCode
	 * 
	 * @return DemographicDetails
	 */

	@Override
	public DemographicDetails getDemoGraphicDetails(String pinCode) {

		return demoGraphicDetailsDAO.getPinCodeDetail(pinCode);
	}

	public void setDemoGraphicDetailsDAO(DemoGraphicDetailsDAO demoGraphicDetailsDAO) {
		this.demoGraphicDetailsDAO = demoGraphicDetailsDAO;
	}

}
