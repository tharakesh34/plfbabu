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
 * FileName    		:  CustomerIncomeDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  06-05-2011    														*
 *                                                                  						*
 * Modified Date    :  06-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 06-05-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.customermasters.CustomerIncome;

/**
 * DAO methods declaration for the <b>CustomerIncome model</b> class.<br>
 * 
 */
public interface CustomerIncomeDAO {
	CustomerIncome getCustomerIncomeById(CustomerIncome customerIncome, String type, String inputSource);

	void deleteByCustomer(final long id, String type, boolean isWIF);

	void saveBatch(List<CustomerIncome> customerIncome, String type, boolean isWIF);

	int getVersion(CustomerIncome customerIncome);

	void setLinkId(CustomerIncome customerIncome);

	List<CustomerIncome> getIncomesByFinReference(String finReference);

	List<CustomerIncome> getIncomesBySamplingId(long samplingId);

	long getLinkId(long custId);
}