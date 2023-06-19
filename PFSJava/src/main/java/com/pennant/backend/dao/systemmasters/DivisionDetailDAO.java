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
 * * FileName : DivisionDetailDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-08-2013 * * Modified
 * Date : 02-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennanttech.pff.core.TableType;

public interface DivisionDetailDAO extends BasicCrudDao<DivisionDetail> {

	DivisionDetail getDivisionDetailById(String id, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param divisionCode divisionCode of the divisionDetail.
	 * @param tableType    The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String divisionCode, TableType tableType);

	boolean isEntityCodeExistsInDivisionDetails(String entityCode, String type);

	String getEntityCodeByDivision(String finDivision, String type);

	boolean isActiveDivision(String division);

	boolean isValidEntityCode(String division, String entity);
}