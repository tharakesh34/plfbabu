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
 * FileName    		:  FinanceMainDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pennant.app.model.CustomerCalData;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;

public interface FinanceMainDAO {

	public FinanceMain getFinanceMain(boolean isWIF);
	public FinanceMain getNewFinanceMain(boolean isWIF);
	public FinanceMain getFinanceMainById(String id,String type,boolean isWIF);
	public void update(FinanceMain financeMain,String type,boolean isWIF);
	public void delete(FinanceMain financeMain,String type,boolean isWIF);
	public String save(FinanceMain financeMain,String type,boolean isWIF);
	public void initialize(FinanceMain financeMain);
	public void refresh(FinanceMain entity);
	public boolean isFinReferenceExists(String id, String type, boolean isWIF);
	CustomerCalData calculateData(CustomerCalData calData, String type);
	public void listUpdate(ArrayList<FinanceMain> financeMain, String type);
	public List<String> getFinanceMainListByBatch(Date curBD, Date nextBD, String type);
	public  List<BigDecimal> getActualPftBal(String finReference,String type);
	public void updateFinAccounts(FinanceMain financeMain);
	public FinanceMain getFinanceMainForDataSet(String finReference);
	public void updateRepaymentAmount(String finReference, BigDecimal repaymentAmount);
	public List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);
	public void updateCustCIF(long custID, String finReference);
	public void updateFinBlackListStatus(String finReference);
	public void updateCancelStatus(FinanceMain financeMain, String type);

}