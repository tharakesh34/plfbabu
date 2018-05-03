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
 * FileName    		:  CustomerExtLiabilityDAO.java                                                   * 	  
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
import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerExtLiability;

/**
 * DAO methods declaration for the <b>CustomerExtLiability model</b> class.<br>
 * 
 */
public interface CustomerExtLiabilityDAO {

	 CustomerExtLiability getCustomerExtLiabilityById(long id,int liabilitySeq,String type);
	 List<CustomerExtLiability> getExtLiabilityByCustomer(final long id,String type);
	 void update(CustomerExtLiability customerExtLiability,String type);
	 void delete(CustomerExtLiability customerExtLiability,String type);
	 void deleteByCustomer(long custID,String type);
	 long save(CustomerExtLiability customerExtLiability,String type);
	 int getBankNameCount(String bankCode);
	 int getFinTypeCount(String finType);
	 int getFinStatusCount(String finStatus);
	 int getVersion(long custID,int liabilitySeq);
	int getCustomerExtLiabilityByBank(String bankCode, String type);
	BigDecimal getSumAmtCustomerExtLiabilityById(long custId);
}