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
 * FileName    		:  FinBillingDetailDAO.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-01-2013    														*
 *                                                                  						*
 * Modified Date    :  				   														*
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

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinBillingDetail;

/**
 * DAO methods declaration for the <b>FinBillingDetail model</b> class.<br>
 * 
 */
public interface FinBillingDetailDAO {

	FinBillingDetail getFinBillingDetail();
	FinBillingDetail getNewFinBillingDetail();
	FinBillingDetail getFinBillingDetailByID(String finReference,Date progClaimDate,String type);
	List<FinBillingDetail> getFinBillingDetailByFinRef(final String id,String type);
	void update(FinBillingDetail  billingDetail,String type);
	void updateClaim(FinBillingDetail  billingDetail,String type);
	void delete(FinBillingDetail billingDetail,String type);
	void save(FinBillingDetail billingDetail,String type);
	void initialize(FinBillingDetail type);
	void refresh(FinBillingDetail entity);
	void deleteByFinRef(final String finReference,String type);
}
