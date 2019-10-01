package com.pennant.backend.service.gstn.validation.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.finance.financetaxdetail.GSTINInfo;
import com.pennant.backend.service.gstn.validation.GSTNValidationService;
import com.pennanttech.pff.external.GSTINRequestService;

public class GSTNValidationServiceImpl implements GSTNValidationService {

	@Autowired(required = false)
	private GSTINRequestService gstinRequestService;

	@Override
	public GSTINInfo validateGSTNNumber(GSTINInfo gstinInfo) {

		if (gstinRequestService != null) {
			return gstinRequestService.gstinValidation(gstinInfo);
		}
		return null;
	}
}
