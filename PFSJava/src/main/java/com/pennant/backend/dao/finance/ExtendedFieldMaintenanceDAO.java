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
 * * FileName : FinanceMainDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified Date
 * : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.ExtendedFieldMaintenance;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.core.TableType;

public interface ExtendedFieldMaintenanceDAO {

	void save(FinanceMain financeMain);

	ExtendedFieldMaintenance getExtendedFieldMaintenanceByFinRef(String finReference, String type);

	boolean isDuplicateKey(String finReference, TableType tableType);

	void save(ExtendedFieldMaintenance efm, TableType tableType);

	void update(ExtendedFieldMaintenance extendedFieldMaintenance, TableType tableType);

	void delete(ExtendedFieldMaintenance extendedFieldMaintenance, TableType tableType);

}