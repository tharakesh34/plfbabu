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
 * * FileName : PinCodeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-06-2017 * * Modified Date :
 * 01-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennanttech.pennapps.jdbc.search.ISearch;
import com.pennanttech.pff.core.TableType;

public interface PinCodeDAO extends BasicCrudDao<PinCode> {

	/**
	 * Fetch the Record Academic Details details by key field
	 * 
	 * @param pinCodeId pinCodeId of the PinCode.
	 * @param tableType The type of the table.
	 * @return PinCode
	 */
	PinCode getPinCode(long pinCodeId, String type);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param pinCodeId pinCodeId of the PinCode.
	 * @param pinCode   pinCode of the PinCode.
	 * @param tableType The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long pinCodeId, String city, String area, TableType tableType);

	boolean isCityCodeExists(String pcCity);

	PinCode getPinCode(String pinCode, String type);

	int getPinCodeCount(String pinCode, String type);

	PinCode getPinCodeById(long pinCodeId, String type);

	List<PinCode> getResult(ISearch search, List<String> roleCodes);

	PinCode getPinCodeById(long pinCodeId);

	PinCode getPinCode(String pinCode);

}