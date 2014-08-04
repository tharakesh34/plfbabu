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
 * FileName    		:  FinanceProfitDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-02-2012    														*
 *                                                                  						*
 * Modified Date    :  09-02-2012   													*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-02-2012       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;

import com.pennant.backend.model.finance.FinancePremiumDetail;

/**
 * DAO methods declaration for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public interface FinancePremiumDetailDAO {

	public FinancePremiumDetail getFinPremiumDetailsById(String finReference, String type);
	public void update(FinancePremiumDetail premiumDetail, String type);
	public String save(FinancePremiumDetail premiumDetail, String type);
	public void delete(FinancePremiumDetail premiumDetail, String type);
	public void updateAccruedAmount(FinancePremiumDetail premiumDetail);
	public BigDecimal getFairValueAmount(String finReference, String type);
	
}
