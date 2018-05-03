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
 * FileName    		:  ReinstateFinanceDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ReinstateFinance;


/**
 * DAO methods declaration for the <b>ReinstateFinance model</b> class.<br>
 * 
 */
public interface ReinstateFinanceDAO {

	ReinstateFinance getReinstateFinance();
	ReinstateFinance getNewReinstateFinance();
	ReinstateFinance getReinstateFinanceById(String finReference,String type);
	void update(ReinstateFinance reinstateFinance,String type);
	void delete(ReinstateFinance reinstateFinance,String type);
	String save(ReinstateFinance reinstateFinance,String type);
	ReinstateFinance getFinanceDetailsById(String finReference);
	FinanceMain getRejectedFinanceById(final String id);
	void processReInstateFinance(FinanceMain financeMain);
	void deleteRejectFinance(ReinstateFinance reinstateFinance);
}