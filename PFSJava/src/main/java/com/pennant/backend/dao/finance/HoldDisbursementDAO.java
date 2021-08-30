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
 * * FileName : HoldDisbursementDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-10-2018 * * Modified
 * Date : 09-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-10-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennanttech.pff.core.TableType;

public interface HoldDisbursementDAO extends BasicCrudDao<HoldDisbursement> {

	/**
	 * Fetch the Record HoldDisbursement by key field
	 * 
	 * @param entityCode   entityCode of the HoldDisbursement.
	 * @param finReference finReference of the HoldDisbursement.
	 * @param tableType    The type of the table.
	 * @return HoldDisbursement
	 */
	HoldDisbursement getHoldDisbursement(long finID, String type);

	boolean isDuplicateKey(long finID, TableType tableType);

	public boolean isholdDisbursementProcess(long finID, String type);

}