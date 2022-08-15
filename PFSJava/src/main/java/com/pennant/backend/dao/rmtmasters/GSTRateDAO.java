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
 * * FileName : GSTRateDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-05-2019 * * Modified Date :
 * 20-05-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-05-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennanttech.pff.core.TableType;

public interface GSTRateDAO extends BasicCrudDao<GSTRate> {

	/**
	 * Fetch the Record GSTRate by key field
	 * 
	 * @param id        id of the GSTRate.
	 * @param tableType The type of the table.
	 * @return GSTRate
	 */
	GSTRate getGSTRate(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id        id of the GSTRate.
	 * @param fromState fromState of the GSTRate.
	 * @param toState   toState of the GSTRate.
	 * @param taxType   taxType of the GSTRate.
	 * @param tableType The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String fromState, String toState, String taxType, TableType tableType);

	boolean isGSTExist(String fromState, String toState, String calOn);

	List<GSTRate> getGSTRateByStates(String fromState, String toState, String tableType);

}