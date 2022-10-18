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
package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer for <code>CustTypePANMapping</code> with set of CRUD operations.
 * 
 * @param <T>
 */
public interface CustTypePANMappingDAO extends BasicCrudDao<CustTypePANMapping> {
	CustTypePANMapping getCustTypePANMappingById(long mappingID, String type);

	boolean isDuplicateKey(long id, String custType, String pANLetter, TableType tableType);

	boolean isValidPANLetter(String custType, String custCategory, String panLetter);
}
