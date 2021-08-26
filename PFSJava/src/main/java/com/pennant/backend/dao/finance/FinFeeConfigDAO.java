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
 * * FileName : FinFeeDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified Date
 * : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.FinFeeConfig;

public interface FinFeeConfigDAO extends BasicCrudDao<FinFeeConfig> {

	void saveList(List<FinFeeConfig> finFeeDetailConfig, String type);

	List<FinFeeConfig> getFinFeeConfigList(long finID, String eventCode, boolean origination, String type);

	int getFinFeeConfigCountByRuleCode(String ruleCode, String type);

}