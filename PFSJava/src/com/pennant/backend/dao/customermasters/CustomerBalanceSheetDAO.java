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
 * FileName    		:  CustomerBalanceSheetDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  07-12-2011    														*
 *                                                                  						*
 * Modified Date    :  07-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 07-12-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.customermasters.CustomerBalanceSheet;

/**
 * DAO methods declaration for the <b>CustomerBalanceSheet model</b> class.<br>
 * 
 */
public interface CustomerBalanceSheetDAO {

	public CustomerBalanceSheet getCustomerBalanceSheet();
	public CustomerBalanceSheet getNewCustomerBalanceSheet();
	public CustomerBalanceSheet getCustomerBalanceSheetById(String financialYear, long custId,String type);
	public void update(CustomerBalanceSheet customerBalanceSheet,String type);
	public void delete(CustomerBalanceSheet customerBalanceSheet,String type);
	public String save(CustomerBalanceSheet customerBalanceSheet,String type);
	public void initialize(CustomerBalanceSheet customerBalanceSheet);
	public void refresh(CustomerBalanceSheet entity);
	public List<CustomerBalanceSheet> getBalanceSheetsByCustomer(long id, String type);
	public void deleteByCustomer(long custID, String tableType);
}