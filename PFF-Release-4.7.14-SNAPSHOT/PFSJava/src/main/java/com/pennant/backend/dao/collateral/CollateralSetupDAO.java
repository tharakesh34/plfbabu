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
 * FileName    		:  CollateralSetupDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-12-2016    														*
 *                                                                  						*
 * Modified Date    :  13-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-12-2016       PENNANT	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.model.collateral.CollateralSetup;

public interface CollateralSetupDAO {
	CollateralSetup getCollateralSetupByRef(String collateralRef, String type);

	void update(CollateralSetup collateralSetup, String type);

	void delete(CollateralSetup collateralSetup, String type);

	String save(CollateralSetup collateralSetup, String type);

	boolean isCollReferenceExists(String generatedSeqNo, String type);

	boolean updateCollReferene(long oldReference, long newReference);

	int getVersion(String collateralRef, String type);

	int getCollateralCountByref(String collateralRef, String tableType);

	CollateralSetup getCollateralSetup(String collateralRef, long depositorId, String tableType);

	List<CollateralSetup> getApprovedCollateralByCustId(long depositorId, String tableType);
	
	int getCountByCollateralRef(String collateralRef);
}