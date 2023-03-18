/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : VASConfigurationDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-11-2016 * * Modified
 * Date : 29-11-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-11-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.configuration;

import java.util.List;

import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASPremiumCalcDetails;

public interface VASConfigurationDAO {
	VASConfiguration getVASConfiguration();

	VASConfiguration getNewVASConfiguration();

	List<VASConfiguration> getVASConfigurations(String type);

	VASConfiguration getVASConfigurationByCode(String productCode, String type);

	void update(VASConfiguration vasConfiguration, String type);

	void delete(VASConfiguration vasConfiguration, String type);

	String save(VASConfiguration vasConfiguration, String type);

	boolean isVASTypeExists(String productType);

	int getFeeAccountingCount(long feeAccountId, String type);

	void deletePremiumCalcDetails(String productCode, String tableType);

	void savePremiumCalcDetails(List<VASPremiumCalcDetails> premiumCalcDetList, String tableType);

	List<VASPremiumCalcDetails> getPremiumCalcDetails(String productCode, String tableType);
}