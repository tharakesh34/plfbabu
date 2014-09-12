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
 * FileName    		:  CustomerStatusCodeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.applicationmaster;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;

/**
 * DAO methods declaration for the <b>CustomerStatusCode model</b> class.<br>
 * 
 */
public interface CustomerStatusCodeDAO {

	public CustomerStatusCode getCustomerStatusCode();

	public CustomerStatusCode getNewCustomerStatusCode();

	public CustomerStatusCode getCustomerStatusCodeById(String id, String type);
	
	public void update(CustomerStatusCode customerStatusCode, String type);

	public void delete(CustomerStatusCode customerStatusCode, String type);

	public String save(CustomerStatusCode customerStatusCode, String type);

	public void initialize(CustomerStatusCode customerStatusCode);

	public void refresh(CustomerStatusCode entity);
	
	public boolean getFinanceSuspendStatus(int curODDays);

	public String getFinanceStatus(String finReference, boolean isCurFinSts);

	public CustomerStatusCode getCustStatusByMinDueDays(String type);
	
}