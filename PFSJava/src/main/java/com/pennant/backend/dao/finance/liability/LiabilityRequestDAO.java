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
 * FileName    		:  LiabilityRequestDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2015    														*
 *                                                                  						*
 * Modified Date    :  31-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2015       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.liability;
import com.pennant.backend.model.finance.liability.LiabilityRequest;

public interface LiabilityRequestDAO {

	LiabilityRequest getLiabilityRequest();
	LiabilityRequest getNewLiabilityRequest();
	LiabilityRequest getLiabilityRequestById(long id, String type);
	LiabilityRequest getLiabilityRequestByFinReference(String finReference, String type);
	void update(LiabilityRequest liabilityRequest,String type);
	void delete(LiabilityRequest liabilityRequest,String type);
	String save(LiabilityRequest liabilityRequest,String type);
	String getProceedingWorkflow(String finType, String finEvent);
	int getFinareferenceCount(String finReference, String type);
}