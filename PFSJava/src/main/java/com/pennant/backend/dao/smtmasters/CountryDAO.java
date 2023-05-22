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
 * * FileName : CountryDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date :
 * 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.smtmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.Country;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>Country model</b> class.<br>
 * 
 */
public interface CountryDAO extends BasicCrudDao<Country> {

	Country getCountryById(String id, String type);

	String getSystemDefaultCount(String countryCode);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param countryCode countryCode of the country.
	 * @param tableType   The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String countryCode, TableType tableType);

	boolean isExistCountryCode(String code);

	boolean isActiveCountry(String custAddrCountry);
}