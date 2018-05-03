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

	InvestmentFinHeader getTreasuaryFinHeader();
	InvestmentFinHeader getNewTreasuaryFinHeader();
	InvestmentFinHeader getTreasuaryFinHeaderById(String id,String type);
	InvestmentFinHeader getTreasuaryFinHeader(String finReference, String tableType);
	void update(InvestmentFinHeader treasuaryFinance,String type);
	void delete(InvestmentFinHeader treasuaryFinance,String type);
	String save(InvestmentFinHeader treasuaryFinance,String type);
	List<FinanceMain> getInvestmentDealList(InvestmentFinHeader investmentFinHeader, String tableType);
	FinanceMain getInvestmentDealById(FinanceMain financeMain, String tableType);
	void updateDealsStatus(String investmentReference);
}