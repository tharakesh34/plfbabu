package com.pennanttech.pff.external.pan.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pennant.backend.model.PrimaryAccount;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.pan.dao.PrimaryAccountDAO;

@Deprecated
@Component
public class PrimaryAccountServiceImpl implements PrimaryAccountService {
	private static final Logger logger = LogManager.getLogger(PrimaryAccountServiceImpl.class);

	@Value("${pan.enquiry:false}")
	private boolean panValidationRequired = false;

	private PANService panService;
	private PANService nsdlPANService;
	private PrimaryAccountDAO primaryAccountDAO;

	@Override
	public PrimaryAccount retrivePanDetails(PrimaryAccount primaryAccount) {
		logger.debug(Literal.ENTERING);

		int count = primaryAccountDAO.isPanVerified(primaryAccount.getPanNumber());

		if (count == 0) {
			getPanService().getPANDetails(primaryAccount);
			primaryAccountDAO.savePanVerificationDetails(primaryAccount);
		} else {
			return primaryAccount;
		}
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

	@Qualifier("nsdlPANService")
	public void setNsdlPANService(PANService nsdlPANService) {
		this.nsdlPANService = nsdlPANService;
	}

	private PANService getPanService() {
		return panService == null ? nsdlPANService : panService;
	}

	@Override
	public void savePanVerificationDetails(PrimaryAccount primaryAccount) {
		this.primaryAccountDAO.savePanVerificationDetails(primaryAccount);

	}

	@Override
	public int isPanVerified(String panNo) {
		return primaryAccountDAO.isPanVerified(panNo);
	}

	@Override
	public PrimaryAccount getPrimaryAccountDetails(String primaryID) {

		return primaryAccountDAO.getPrimaryAccountDetails(primaryID);
	}

	public PrimaryAccountDAO getPrimaryAccountDao() {
		return primaryAccountDAO;
	}

	@Autowired
	public void setPrimaryAccountDao(PrimaryAccountDAO primaryAccountDao) {
		this.primaryAccountDAO = primaryAccountDao;
	}

}
