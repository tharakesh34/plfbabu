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
 * * FileName : AccountTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * * Modified Date
 * : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.rmtmasters.AccountType;

/**
 * DAO methods declaration for the <b>AccountType model</b> class.<br>
 * 
 */
public interface AccountTypeDAO {

	AccountType getAccountTypeById(String id, String type);

	void update(AccountType accountType, String type);

	void delete(AccountType accountType, String type);

	String save(AccountType accountType, String type);

	List<ValueLabel> getAccountTypeDesc(List<String> acTypeList);

	int getgetAccountTypeByProfit(long profitCenterID, String type);

	int getgetAccountTypeByCost(long costCenterID, String type);

	boolean isExsistAccountGroup(long groupId);

	boolean isExsistAccountType(String acctype);
}