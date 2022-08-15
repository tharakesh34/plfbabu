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
 * * FileName : TDSReceivableDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * * Modified
 * Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.tds.receivables;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennanttech.pff.core.TableType;

public interface TdsReceivableDAO extends BasicCrudDao<TdsReceivable> {

	/**
	 * Fetch the Record TDSReceivable by key field
	 * 
	 * @param iD        iD of the TDSReceivable.
	 * @param tableType The type of the table.
	 * @return TDSReceivable
	 */
	TdsReceivable getTdsReceivable(long iD, TableType type);

	String save(TdsReceivable tdsReceivable, TableType tableType);

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param iD                iD of the TDSReceivable.
	 * @param certificateNumber certificateNumber of the TDSReceivable.
	 * @param tableType         The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(long iD, String certificateNumber, TableType tableType);

	public void update(TdsReceivable tdsReceivable, TableType tableType);

	void updateReceivableBalances(TdsReceivable tdsReceivable);

	String getStatus(String certificatenumber);

}