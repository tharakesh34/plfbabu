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
 * * FileName : CityDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date :
 * 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.City;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>City model</b> class.<br>
 * 
 */
public interface CityDAO extends BasicCrudDao<City> {

	City getCityById(String pCCountry, String pCProvince, String pCCity, String type);

	boolean isDuplicateKey(String country, String state, String city, TableType tableType);

	int getPCProvinceCount(String pcProvince, String type);

	boolean isDuplicateKey(String city, String district, TableType tableType);

	boolean isActiveCity(String city);
}