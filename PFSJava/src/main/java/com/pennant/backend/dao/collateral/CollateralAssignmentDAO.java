/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CollateralAssignmentDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-05-2016    														*
 *                                                                  						*
 * Modified Date    :  07-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-05-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.dao.collateral;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.collateral.AssignmentDetails;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;

public interface CollateralAssignmentDAO {

	void save(CollateralAssignment collateralAssignment, String type);

	void update(CollateralAssignment collateralAssignment, String type);

	List<CollateralAssignment> getCollateralAssignmentByFinRef(String reference, String moduleName, String type);

	CollateralAssignment getCollateralAssignmentbyID(CollateralAssignment collateralAssignment, String type);

	void delete(CollateralAssignment collateralAssignment, String type);
	
	void deleteByReference(String reference, String type);

	List<AssignmentDetails> getCollateralAssignmentByColRef(String collateralRef, String collateralType);

	BigDecimal getAssignedPerc(String collateralRef, String reference,	String type);

	int getAssignedCollateralCount(String collateralRef, String type);

	void deLinkCollateral(String finReference);

	void save(CollateralMovement movement);

	List<CollateralMovement> getCollateralMovements(String collateralRef);
	
}

