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
 * * FileName : BounceReasonDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-04-2017 * * Modified Date
 * : 22-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennanttech.pff.core.TableType;

public interface BounceReasonDAO extends BasicCrudDao<BounceReason> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param bounceID  bounceID of the BounceReason.
	 * @param tableType The type of the table.
	 * @return BounceReason
	 */
	BounceReason getBounceReason(long bounceID, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param bounceID   bounceID of the BounceReason.
	 * @param bounceCode bounceCode of the BounceReason.
	 * @param tableType  The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long bounceID, String bounceCode, TableType tableType);

	BounceReason getBounceReasonByReturnCode(String returnCode, String type);

	int getBounceReasonByRuleCode(long ruleId);

	boolean isDuplicateReturnCode(long bounceID, String returnCode, TableType tableType);

	Long getBounceIDByCode(String returnCode);

	int getBounceCodeCount(String bouncecode);
}