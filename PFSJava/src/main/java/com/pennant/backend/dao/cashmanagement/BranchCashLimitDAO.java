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
 * * FileName : BranchCashLimitDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-01-2018 * * Modified
 * Date : 29-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.cashmanagement;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;
import com.pennanttech.pff.core.TableType;

public interface BranchCashLimitDAO extends BasicCrudDao<BranchCashLimit> {

	/**
	 * Fetch the Record BranchCashLimit Details by key field
	 * 
	 * @param branchCode branchCode of the BranchCashLimit.
	 * @param tableType  The type of the table.
	 * @return BranchCashLimit
	 */
	BranchCashLimit getBranchCashLimit(String branchCode, String type);

	boolean isDuplicateKey(String branchCode, TableType tableType);

	/**
	 * Fetch all Approved BranchCashLimit Details
	 * 
	 * @return List of BranchCashLimit
	 */
	List<BranchCashLimit> getAutoReplenishmentLimitList(String branchCode);
}