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
 * FileName    		:  FinCreditRevSubCategoryDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-11-2013    														*
 *                                                                  						*
 * Modified Date    :  13-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-11-2013       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.customermasters;
import java.util.List;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;

public interface FinCreditRevSubCategoryDAO {

	 FinCreditRevSubCategory getFinCreditRevSubCategory();
	 FinCreditRevSubCategory getNewFinCreditRevSubCategory();
	 FinCreditRevSubCategory getFinCreditRevSubCategoryById(String id,String type);
	 void update(FinCreditRevSubCategory finCreditRevSubCategory,String type);
	 void delete(FinCreditRevSubCategory finCreditRevSubCategory,String type);
	 String save(FinCreditRevSubCategory finCreditRevSubCategory,String type);
	 boolean updateSubCategories(List<FinCreditRevSubCategory> finCreditRevSubCategoryList);
}