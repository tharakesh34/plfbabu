package com.pennant.backend.service.financemanagement.impl;

import com.pennant.backend.dao.financemanagement.PartnerBankModeConfigDAO;
import com.pennant.backend.service.financemanagement.PartnerBankModeConfigService;

public class PartnerBankModeConfigServiceImpl implements PartnerBankModeConfigService {

	public PartnerBankModeConfigDAO partnerBankModeConfigDAO;

	public String getConfigName(String mode, long partnerBankID, String type, String reqType, Boolean isPDC) {
		if (isPDC) {
			return partnerBankModeConfigDAO.getConfigNameByMode(mode, type, reqType);
		} else {
			return partnerBankModeConfigDAO.getConfigName(mode, partnerBankID, type, reqType);
		}
	}

	public void setPartnerBankModeConfigDAO(PartnerBankModeConfigDAO partnerBankModeConfigDAO) {
		this.partnerBankModeConfigDAO = partnerBankModeConfigDAO;
	}

}
