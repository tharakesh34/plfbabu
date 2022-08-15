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
 * * FileName : InterfaceMappingDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 01-12-2016 * * Modified
 * Date : 01-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 01-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.interfacemapping;

import java.util.List;

import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennanttech.pff.core.TableType;

public interface InterfaceMappingDAO {

	InterfaceMapping getInterfaceMappingById(long id, String type);

	void update(InterfaceMapping beneficiary, String type);

	void delete(InterfaceMapping beneficiary, String type);

	long save(InterfaceMapping beneficiary, String type);

	List<String> getTableNameColumnsList(String tableName);

	boolean isDuplicateKey(InterfaceMapping interfaceMapping, TableType tableType);

}