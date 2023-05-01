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
 * * FileName : AccountMappingDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * * Modified
 * Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennanttech.pff.core.TableType;

public interface AccountMappingDAO extends BasicCrudDao<AccountMapping> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param account   account of the AccountMapping.
	 * @param tableType The type of the table.
	 * @return AccountMapping
	 */
	AccountMapping getAccountMapping(String account, String type);

	void delete(String finType, TableType mainTab);

	List<AccountMapping> getAccountMappingFinType(String finType, String type);

	String getAccountMappingByAccount(String account);

	boolean isValidAccount(String account, String trantypeBoth, String trantypeDebit, String status);
}