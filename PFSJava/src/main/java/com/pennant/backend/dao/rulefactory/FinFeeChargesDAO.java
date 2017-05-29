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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  FinFeeChargesDAO.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  10-06-2014    
 *                                                                  
 * Modified Date    :  10-06-2014    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *10-06-2014       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory;

import java.util.List;

import com.pennant.backend.model.rulefactory.FeeRule;

public interface FinFeeChargesDAO {

	FeeRule getFeeChargesByFinRefAndFee(String finReference, String feeCode, String tableType);
	boolean updateFeeChargesByFinRefAndFee(FeeRule feeRule, String tableType);
	
	FeeRule getInsFee(String finReference, String type);
	void saveChargesBatch(List<FeeRule> chargeList, boolean isWIF, String tableType);
	void deleteChargesBatch(String finReference, String finEvent, boolean isWIF, String tableType);
	List<FeeRule> getFeeChargesByFinRef(String finReference, String finEvent, boolean isWIF, String tableType);
}
