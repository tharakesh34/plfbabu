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

import org.springframework.dao.DataAccessException;

import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pff.core.TableType;

public interface FinanceMainDAO {
	FinanceMain getFinanceMain(boolean isWIF);

	FinanceMain getNewFinanceMain(boolean isWIF);

	FinanceMain getFinanceMain(String id, String nextRoleCode, String type);

	FinanceMain getFinanceMainById(String id, String type, boolean isWIF);

	/**
	 * Saves the record. If required, this will generate the identity sequence number for the record before saving.
	 * 
	 * @param financeMain
	 *            The model object that contains the parameters.
	 * @param tableType
	 *            The type of the table.
	 * @param wif
	 *            Whether the record is for what-if or the loan.
	 * @return Identity sequence number as string or primary key code of the saved record.
	 * @throws DataAccessException
	 *             If there is any problem issuing the save.
	 */
	String save(FinanceMain financeMain, TableType tableType, boolean wif);

	/**
	 * Updates the record.
	 * 
	 * @param financeMain
	 *            The model object that contains the parameters.
	 * @param tableType
	 *            The type of the table.
	 * @param wif
	 *            Whether the record is for what-if or the loan.
	 * @throws ConcurrencyException
	 *             If failure due to concurrency.
	 * @throws DataAccessException
	 *             If there is any problem issuing the update.
	 */
	void update(FinanceMain financeMain, TableType tableType, boolean wif);

	/**
	 * Deletes the record.
	 * 
	 * @param financeMain
	 *            The model object that contains the parameters.
	 * @param tableType
	 *            The type of the table.
	 * @param wif
	 *            Whether the record is for what-if or the loan.
	 * @param finalize
	 *            Specifies whether the record was finalized or not.
	 * @throws DependencyFoundException
	 *             If there are any dependencies for the record.
	 * @throws ConcurrencyException
	 *             If failure due to concurrency.
	 * @throws DataAccessException
	 *             If there is any problem issuing the delete.
	 */
	void delete(FinanceMain financeMain, TableType tableType, boolean wif, boolean finalize);

	boolean isFinReferenceExists(String id, String type, boolean isWIF);

	void listUpdate(ArrayList<FinanceMain> financeMain, String type);

	List<String> getFinanceMainListByBatch(Date curBD, Date nextBD, String type);

	List<BigDecimal> getActualPftBal(String finReference, String type);

	void updateRepaymentAmount(String finReference, BigDecimal finAmount, BigDecimal repaymentAmount, String finStatus,
			String finStsReason, boolean isCancelProc, boolean pftFullyPaid);

	List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);

	void updateCustCIF(long custID, String finReference);

	void updateFinBlackListStatus(String finReference);

	List<String> getFinanceReferenceList();

	FinanceSummary getFinanceProfitDetails(String finRef);

	List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);

	List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate);

	Boolean saveRejectFinanceDetails(FinanceMain financeMain);

	List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId);

	void updateFinanceERR(String finReference, Date lastRepayDate, Date lastRepayPftDate, BigDecimal effectiveRate,
			String type);

	FinanceMain getFinanceMainForBatch(String finReference);

	FinanceMain getFinanceMainForPftCalc(String finReference);

	FinanceMain getFinanceMainForRpyCancel(String id);

	void updateFinAccounts(String finReference, String finAccount);

	void updateFinancePriority();

	void updateApprovalStatus(String finReference, String approvalStatus);

	String getNextRoleCodeByRef(String finReference, String type);

	List<String> getFinanceWorlflowFirstTaskOwners(String event, String moduleName);

	void updateNextUserId(List<String> finRefList, String oldUserId, String newUserId, boolean singleUser);

	void updateDeviationApproval(FinanceMain financeMain, boolean rejected, String type);

	List<FinanceSummary> getFinExposureByCustId(long custId);

	List<FinanceMain> getFinanceRefByValueDate(Date appDate, int days);

	List<FinanceMain> getFinGraceDetails(Date grcEnd, int allowedDays);

	List<FinanceMain> getFinanceRefByPriority();

	void saveFinanceSnapshot(FinanceMain financeMain);

	FinanceMain getFinanceMainByRef(String reference, String type, boolean isRejectFinance);

	// Rollover Finance Details
	List<String> getRollOverLimitRefList();

	List<String> getRollOverFinTypeList(String limitRef);

	List<Date> getRollOverDateList(String limitRef, String finType);

	List<RolledoverFinanceDetail> getFinanceList(String limitRef, String finType, Date rolloverDate);

	List<DDAPayments> getDDAPaymentsList(String repaymthAutodda, Date appDate);

	FinanceMain getFinanceMainForManagerCheque(String finReference, String type);

	void updateRepaymentAmount(String finReference, BigDecimal repaymentAmount);

	void updateStatus(String finReference, String status, String statusReason);

	String getApprovedRepayMethod(String finReference, String type);

	String getCurrencyByAccountNo(String accountNo);

	void updateMaturity(String finReference, String closingStatus, boolean finIsActive);

	List<String> getScheduleEffectModuleList(boolean schdChangeReq);

	boolean updateSeqNumber(long oldNumber, long newNumber);

	List<FinanceMain> getFinanceMainbyCustId(long id);

	int getFinanceCountById(String finReference, String type, boolean isWIF);

	int getFinanceCountByMandateId(long mandateID);

	int getFinanceCountById(String finReference, long mandateID);

	int loanMandateSwapping(String finReference, long newMandateID);

	FinanceMain getFinanceDetailsForService(String finReference, String type, boolean isWIF);

	int updateFinanceBasicDetails(FinanceMain finacneMain, String type);

	List<String> getUsersLoginList(List<String> nextRoleCodes);

	FinanceMain getFinanceMainParms(String finReference);

	List<FinanceMain> getFinanceByCustId(long custId);

	List<FinanceMain> getFinanceByCollateralRef(String collateralRef);

	List<String> getFinReferencesByMandateId(long mandateId);

	List<String> getFinReferencesByCustID(long custId, String finActiveStatus);

	BigDecimal getFinAssetValue(String finReference);

	FinanceMain getDisbursmentFinMainById(String finReference, TableType tableType);

	/**
	 * Get the total maximum re-payment amount against the specified mandate excluding the finance.
	 * 
	 * @param mandateId
	 *            Mandate id for which the total maximum re-payment amount to be fetched.
	 * @param finReference
	 *            Finance reference that need to be excluded.
	 * @return The total maximum re-payment amount against the specified mandate excluding the finance.
	 */
	BigDecimal getTotalMaxRepayAmount(long mandateId, String finReference);

	void updateBucketStatus(String finReference, String status, int bucket,
			String statusReason);
	
	List<FinanceMain> getFinMainsForEODByCustId(long custId, boolean isActive);

	int getFinanceMainByBank(String bankCode, String type);


	void updateFinanceInEOD(FinanceMain financeMain, List<String> updateFields, boolean rateRvw);

	void updatePaymentInEOD(FinanceMain financeMain);

	List<FinanceMain> getBYCustIdForLimitRebuild(long id, boolean orgination);
	
	FinanceMain getFinanceBasicDetailByRef(String finReference, boolean isWIF);
}