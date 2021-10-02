package com.pennant.backend.dao.financemanagement;

public interface PartnerBankModeConfigDAO {

	String getConfigName(String mode, long partnerBankID, String type, String reqType);

	String getConfigNameByMode(String mode, String type, String reqType);

}
