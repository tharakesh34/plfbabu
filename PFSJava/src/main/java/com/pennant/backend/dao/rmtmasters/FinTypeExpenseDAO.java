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
 * FileName    		:  FinTypeExpenseDAO.java                                               * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-12-2017    														*
 *                                                                  						*
 * Modified Date    :  			    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-12-2017       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.rmtmasters;

import java.util.List;

import com.pennant.backend.model.rmtmasters.FinTypeExpense;

/**
 * DAO methods declaration for the <b>FinTypeExpense model</b> class.<br>
 * 
 */
public interface FinTypeExpenseDAO {

	FinTypeExpense getFinTypeExpense();

	FinTypeExpense getNewFinTypeExpense();

	FinTypeExpense getFinTypeExpenseByID(FinTypeExpense finTypeExpense, String type);

	List<FinTypeExpense> getFinTypeExpenseListByFinType(String finType, String type);

	void update(FinTypeExpense finTypeExpense, String type);

	long save(FinTypeExpense finTypeExpense, String type);

	void delete(FinTypeExpense finTypeExpense, String type);

	void deleteByFinType(String finType, String type);
	
	FinTypeExpense getFinTypeExpenseByFinType(String finType, long expenseTypeId, String type);
	
	boolean expenseExistingFinTypeExpense(long expenseId, String type);

}