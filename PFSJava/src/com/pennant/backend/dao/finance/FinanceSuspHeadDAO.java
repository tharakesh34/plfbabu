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
 * FileName    		:  FinanceSuspHeadDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-02-2012    														*
 *                                                                  						*
 * Modified Date    :  04-02-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-02-2012       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennant.backend.model.finance.FinanceSuspDetails;
import com.pennant.backend.model.finance.FinanceSuspHead;

public interface FinanceSuspHeadDAO {

	public FinanceSuspHead getFinanceSuspHead();
	public FinanceSuspHead getNewFinanceSuspHead();
	public FinanceSuspHead getFinanceSuspHeadById(String finReference,String type);
	public String save(FinanceSuspHead financeSuspHead, String type);
	public void update(FinanceSuspHead financeSuspHead, String type);
	public String saveSuspenseDetails(FinanceSuspDetails suspDetails, String type);
	public List<FinanceSuspDetails> getFinanceSuspDetailsListById(String finReference);
	public void initialize(FinanceSuspHead suspHead);
	public void refresh(FinanceSuspHead suspHead);
	public List<String> getSuspFinanceList();
	public void delete(FinanceSuspHead financeSuspHead, String string);
	public List<FinStatusDetail> getCustSuspDate(List<Long> CustIdList);
	public Date getCustSuspDate(long custId);
	public void updateSuspFlag(String finReference);
	 
}
