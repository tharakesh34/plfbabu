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

import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceProfitDetail;

/**
 * DAO methods declaration for the <b>FinanceProfitDetail model</b> class.<br>
 * 
 */
public interface FinanceProfitDetailDAO {

	FinanceProfitDetail getFinProfitDetailsById(String finReference);

	void update(FinanceProfitDetail finProfitDetails, boolean isRpyProcess);

	void update(List<FinanceProfitDetail> finProfitDetails, String type);

	void save(FinanceProfitDetail finProfitDetails);

	BigDecimal getAccrueAmount(String finReference);

	void updateLBDAccruals(FinanceProfitDetail finProfitDetails, boolean isMonthEnd);

	FinanceProfitDetail getFinProfitDetailsByRef(String finReference);

	void updateCpzDetail(List<FinanceProfitDetail> pftDetailsList, String type);

	void refreshTemp();

	FinanceProfitDetail getProfitDetailForWriteOff(String finReference);

	FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);

	void updateLatestRpyDetails(FinanceProfitDetail financeProfitDetail);

	void updateRpyAccount(String finReference, String repayAccountId);

	void saveAccumulates(Date valueDate);

	void resetAcrTsfdInSusp();

	void updateAcrTsfdInSusp(List<AccountHoldStatus> list);

	FinanceProfitDetail getFinProfitDetailsForSummary(String finReference);

	List<FinanceProfitDetail> getFinProfitDetailsByCustId(long custID, boolean isActive);
	FinanceProfitDetail getFinProfitDetailsByFinRef(String finReference, boolean isActive);

	void updateEOD(FinanceProfitDetail finProfitDetails, boolean posted, boolean monthend);

	void UpdateActiveSts(String finReference, boolean isActive);

	void updateODDetailsEOD(Date valueDate);

	void updateTDDetailsEOD(Date valueDate);

	void updateReceivableDetailsEOD(Date valueDate);
	
	void updateBounceDetailsEOD(Date valueDate);

	int getCurOddays(String finReference, String type);
	
	boolean isSuspenseFinance(String finReference);
}
