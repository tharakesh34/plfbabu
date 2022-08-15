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
 * * FileName : TanAssignmentDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 08-09-2020 * * Modified
 * Date : 08-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 08-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.tandetails;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.pff.core.TableType;

public interface TanAssignmentDAO extends BasicCrudDao<TanAssignment> {

	boolean isDuplicateKey(long id, String finReference, long CustId, long tanID, TableType tableType);

	List<TanAssignment> getTanAssignmentsByFinReference(String finReference, TableType tableType);

	List<TanAssignment> getTanAssignments(long custId, String finReference, TableType view);

	long getIdByFinReferenceAndTanId(String finReference, long tanID, TableType view);

	List<TanAssignment> getTanDetailsByReference(String finReference);

	List<TanAssignment> getTanNumberList(long custId);

	List<TanAssignment> getTanAssignmentsByCustId(long custId, String finReference, TableType mainTab);

	int isTanNumberAvailable(long tanID);

	List<String> getFinReferenceByTanNumber(String finReference, String tanNumber, String type);
}