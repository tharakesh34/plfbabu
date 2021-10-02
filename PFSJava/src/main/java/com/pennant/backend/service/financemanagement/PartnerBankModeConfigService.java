package com.pennant.backend.service.financemanagement;

public interface PartnerBankModeConfigService {

	String getConfigName(String mode, long partnerBankID, String type, String reqType, Boolean isPDC);

}
