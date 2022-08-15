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
 * * FileName : BusinessVerticalDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2018 * * Modified
 * Date : 14-12-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennanttech.pff.core.TableType;

public interface BusinessVerticalDAO extends BasicCrudDao<BusinessVertical> {

	/**
	 * Fetch the Record BusinessVertical by key field
	 * 
	 * @param id        id of the BusinessVertical.
	 * @param tableType The type of the table.
	 * @return BusinessVertical
	 */
	BusinessVertical getBusinessVertical(long id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param id        id of the BusinessVertical.
	 * @param id        id of the BusinessVertical.
	 * @param code      code of the BusinessVertical.
	 * @param tableType The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long id, String code, TableType tableType);

}