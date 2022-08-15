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
 * * FileName : AssignmentDealDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-09-2018 * * Modified
 * Date : 12-09-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.AssignmentDeal;
import com.pennant.backend.model.applicationmaster.AssignmentDealExcludedFee;
import com.pennant.backend.model.applicationmaster.AssignmentDealLoanType;
import com.pennanttech.pff.core.TableType;

public interface AssignmentDealDAO {

	/**
	 * Fetch the Record AssignmentDeal by key field
	 * 
	 * @param id        id of the AssignmentDeal.
	 * @param tableType The type of the table.
	 * @return AssignmentDeal
	 */
	AssignmentDeal getAssignmentDeal(long id, String type);

	AssignmentDealLoanType getAssignmentDealLoanType(long dealId, String type);

	String saveLoanType(AssignmentDealLoanType assignmentDealLoanType, String tableType);

	void updateLoanType(AssignmentDealLoanType assignmentDealLoanType, String tableType);

	List<AssignmentDealLoanType> getAssignmentDealLoanTypeList(long id, String string);

	AssignmentDealExcludedFee getAssignmentDealExcludedFee(long dealId, String type);

	String saveExcludedFee(AssignmentDealExcludedFee assignmentDealExcludedFee, String tableType);

	void updateExcludedFee(AssignmentDealExcludedFee assignmentDealExcludedFee, String tableType);

	List<AssignmentDealExcludedFee> getAssignmentDealExcludedFeeList(long id, String string);

	String save(AssignmentDeal assignmentDeal, String tableType);

	void update(AssignmentDeal assignmentDeal, String tableType);

	void delete(AssignmentDeal assignmentDeal, String tableType);

	void deleteLoanTypeList(List<AssignmentDealLoanType> assignmentDealLoanType, String tableType);

	void deleteExcFeeList(List<AssignmentDealExcludedFee> assignmentDealLoanType, String tableType);

	void deleteExcludedFee(AssignmentDealExcludedFee assignmentDealExcFee, String type);

	void deleteLoanType(AssignmentDealLoanType assignmentDealLoanType, String type);

	boolean isDuplicateKey(long id, String code, TableType tableType);

	int getMappedAssignmentDeals(long id);

	List<AssignmentDealExcludedFee> getApprovedAssignmentDealExcludedFeeList(long dealId);

}