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
 * * FileName : ProfitCenterDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified Date
 * : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennanttech.pff.core.TableType;

public interface ProfitCenterDAO extends BasicCrudDao<ProfitCenter> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param profitCenterID profitCenterID of the ProfitCenter.
	 * @param tableType      The type of the table.
	 * @return ProfitCenter
	 */
	ProfitCenter getProfitCenter(long profitCenterID, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param profitCenterID   profitCenterID of the ProfitCenter.
	 * @param profitCenterCode profitCenterCode of the ProfitCenter.
	 * @param tableType        The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long profitCenterID, String profitCenterCode, TableType tableType);

	Long getPftCenterIDByCode(String profitCenterCode);
}