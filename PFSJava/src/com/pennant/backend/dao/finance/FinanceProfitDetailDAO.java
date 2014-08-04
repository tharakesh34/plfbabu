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
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinanceProfitDetail;

/**
 * DAO methods declaration for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public interface FinanceProfitDetailDAO {

	public FinanceProfitDetail getFinProfitDetailsById(String finReference);
	public void update(FinanceProfitDetail finProfitDetails, boolean isRpyProcess);
	public void update(List<FinanceProfitDetail> finProfitDetails, String type);
	public String save(FinanceProfitDetail finProfitDetails, String type);
	public void save(List<FinanceProfitDetail> finProfitDetails, String type);
	public BigDecimal getAccrueAmount(String finReference);
	public void updateBatchList(List<FinanceProfitDetail> finProfitDetails, String type);
	public FinanceProfitDetail getFinProfitDetailsByRef(String finReference);
	public FinanceProfitDetail getFinPftDetailForBatch(String finReference);
	public void updateCpzDetail(List<FinanceProfitDetail> pftDetailsList, String type);
	public void refreshTemp();
	public FinanceProfitDetail getProfitDetailForWriteOff(String finReference);
	public FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);
	public void updateLatestRpyDetails(FinanceProfitDetail financeProfitDetail);
	public void updateRpyAccount(String finReference, String repayAccountId);
	public void saveAccumulates(Date valueDate);
}
