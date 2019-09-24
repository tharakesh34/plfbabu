package com.pennanttech.pff.external.pan.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pennant.backend.model.PrimaryAccount;
import com.pennanttech.pennapps.core.resource.Literal;

@Component
public class PrimaryAccountServiceImpl implements PrimaryAccountService {
	private static final Logger logger = Logger.getLogger(PrimaryAccountServiceImpl.class);

	@Value("${pan.enquiry:false}")
	private boolean panValidationRequired = false;

	private PANService panService;
	private PANService nsdlPANService;

	@Override
	public PrimaryAccount retrivePanDetails(PrimaryAccount primaryAccount) {
		logger.debug(Literal.ENTERING);

		getPanService().getPANDetails(primaryAccount);

		logger.debug(Literal.LEAVING);

		return primaryAccount;
	}

	@Override
	public boolean panValidationRequired() {
		return panValidationRequired;
	}

	@Autowired(required = false)
	public void setPanService(PANService panService) {
		this.panService = panService;
	}

	@Autowired
	@Qualifier("nsdlPANService")
	public void setNsdlPANService(PANService nsdlPANService) {
		this.nsdlPANService = nsdlPANService;
	}

	private PANService getPanService() {
		return panService == null ? nsdlPANService : panService;
	}

}
