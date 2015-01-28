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

import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.reports.AvailFinance;

public interface FinanceMainDAO {

	FinanceMain getFinanceMain(boolean isWIF);
	FinanceMain getNewFinanceMain(boolean isWIF);
	FinanceMain getFinanceMainById(String id,String type,boolean isWIF);
	void update(FinanceMain financeMain,String type,boolean isWIF);
	void delete(FinanceMain financeMain,String type,boolean isWIF);
	String save(FinanceMain financeMain,String type,boolean isWIF);
	void initialize(FinanceMain financeMain);
	void refresh(FinanceMain entity);
	boolean isFinReferenceExists(String id, String type, boolean isWIF);
	void listUpdate(ArrayList<FinanceMain> financeMain, String type);
	List<String> getFinanceMainListByBatch(Date curBD, Date nextBD, String type);
	 List<BigDecimal> getActualPftBal(String finReference,String type);
	void updateRepaymentAmount(String finReference, BigDecimal finAmount, BigDecimal repaymentAmount, 
			String finStatus, String finStsReason, boolean isCancelProc, boolean pftFullyPaid);
	List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);
	void updateCustCIF(long custID, String finReference);
	void updateFinBlackListStatus(String finReference);
	List<String> getFinanceReferenceList();
	FinanceSummary getFinanceProfitDetails(String finRef);
	List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);
	List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate);
 	CustomerFinanceDetail getCustomerFinanceMainById(String id,String type);
	List<AuditTransaction> getFinTransactionsList(String id, boolean approvedFinance);
 	boolean checkFirstTaskOwnerAccess(String productCode, long usrLogin);
	Boolean saveRejectFinanceDetails(FinanceMain financeMain);
	//void updateInvestmentFinance(FinanceMain financeMain,String type);
//	String saveInvestmentFinance1(FinanceMain financeMain, String type);
	List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId);
	void updateFinanceERR(String finReference, Date lastRepayDate, Date lastRepayPftDate, BigDecimal effectiveRate, String type);
	FinanceMain getFinanceMainForBatch(String finReference);
	FinanceMain getFinanceMainForPftCalc(String finReference);
	FinanceMain getFinanceMainForRpyCancel(String id);
	void updateFinAccounts(String finReference, String finAccount);
	void updateActiveStatus(List<String> finRefList);
}