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
 * FileName    		:  FinanceCheckListReferenceDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-12-2011    														*
 *                                                                  						*
 * Modified Date    :  08-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.lmtmasters;
import java.util.List;

import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;

public interface FinanceCheckListReferenceDAO {

	FinanceCheckListReference getFinanceCheckListReference();
	FinanceCheckListReference getNewFinanceCheckListReference();
	FinanceCheckListReference getFinanceCheckListReferenceById(final String id,long questionId,long answerId , String type);
	void update(FinanceCheckListReference financeCheckListReference,String type);
	void delete(FinanceCheckListReference financeCheckListReference,String type);
	String save(FinanceCheckListReference financeCheckListReference,String type);
	List<FinanceCheckListReference> getCheckListByFinRef(final String id, String showinStageCheckListIds, String type);
	void delete(String  finReference,String type) ;
}