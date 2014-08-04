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
 * FileName    		:  TreasuaryFinanceDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-11-2013    														*
 *                                                                  						*
 * Modified Date    :  04-11-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-11-2013       Pennant	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;

public interface TreasuaryFinHeaderDAO {

	public InvestmentFinHeader getTreasuaryFinHeader();
	public InvestmentFinHeader getNewTreasuaryFinHeader();
	public InvestmentFinHeader getTreasuaryFinHeaderById(String id,String type);
	public InvestmentFinHeader getTreasuaryFinHeader(String finReference, String tableType);
	public void update(InvestmentFinHeader treasuaryFinance,String type);
	public void delete(InvestmentFinHeader treasuaryFinance,String type);
	public String save(InvestmentFinHeader treasuaryFinance,String type);
	public void initialize(InvestmentFinHeader treasuaryFinance);
	public void refresh(InvestmentFinHeader entity);
	public List<FinanceMain> getInvestmentDealList(InvestmentFinHeader investmentFinHeader, String tableType);
	public FinanceMain getInvestmentDealById(FinanceMain financeMain, String tableType);
	public void updateDealsStatus(String investmentReference);
}