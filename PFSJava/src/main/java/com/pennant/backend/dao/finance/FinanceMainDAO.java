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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExtension;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.UserPendingCases;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.dms.model.DMSQueue;
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

	void updateMaturity(String finReference, String closingStatus, boolean finIsActive, Date date);

	List<String> getScheduleEffectModuleList(boolean schdChangeReq);

	boolean updateSeqNumber(long oldNumber, long newNumber);

	List<FinanceMain> getFinanceMainbyCustId(long id);

	int getFinanceCountById(String finReference, String type, boolean isWIF);

	int getFinanceCountByMandateId(long mandateID);

	int getFinanceCountById(String finReference, long mandateID);

	int loanMandateSwapping(String finReference, long newMandateID, String repayMethod, String type);

	FinanceMain getFinanceDetailsForService(String finReference, String type, boolean isWIF);

	int updateFinanceBasicDetails(FinanceMain finacneMain, String type);

	List<String> getUsersLoginList(List<String> nextRoleCodes);

	FinanceMain getFinanceMainParms(String finReference);

	List<FinanceMain> getFinanceByCustId(long custId, String type);

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

	void updateBucketStatus(String finReference, String status, int bucket, String statusReason);

	List<FinanceMain> getFinMainsForEODByCustId(long custId, boolean isActive);

	FinanceMain getFinMainsForEODByFinRef(String finReference, boolean isActive);

	int getFinanceMainByBank(String bankCode, String type);

	void updateFinanceInEOD(FinanceMain financeMain, List<String> updateFields, boolean rateRvw);

	void updatePaymentInEOD(FinanceMain financeMain);

	List<FinanceMain> getBYCustIdForLimitRebuild(long id, boolean orgination);

	FinanceMain getFinanceBasicDetailByRef(String finReference, boolean isWIF);

	int getFinCountByCustId(long custID);

	void updateFinMandateId(long mandateId, String finReference, String type);

	long getMandateIdByRef(String finReference, String type);

	int getFinanceCountById(String finReference);

	boolean isAppNoExists(String applicationNo, TableType type);

	String getApplicationNoById(String finReference, String type);

	List<String> getFinReferencesByCustID(long custID);

	List<FinanceMain> getFinancesByExpenseType(String finType, Date finApprovalStartDate, Date finApprovalEndDate);

	boolean isFinTypeExistsInFinanceMain(String finType, String string);

	boolean isLoanPurposeExits(String purposeCode, String string);

	String getEarlyPayMethodsByFinRefernce(String finReference);

	FinanceMain getDMFinanceMainByRef(String finReference, String type);

	List<String> getFinanceReferenceList(String type);

	List<LoanPendingData> getCustomerODLoanDetails(long userID);

	void updateNextUserId(String finReference, String nextUserId);

	String getNextUserId(String finReference);

	int getActiveCount(String finType, long custID);

	int getODLoanCount(String finType, long custID);

	List<FinanceMain> getUnApprovedFinances();

	long getPartnerBankIdByReference(String finReference, String paymentMode, String depositAc, String type,
			String purpose, boolean wif);// ### 18-07-2018 Ticket ID :
																																			// 124998,receipt upload

	boolean isFinReferenceExitsWithEntity(String finReference, String type, String entity);// ###
																							// 12-07-2018
																							// Ticket
																							// ID
																							// :
																							// 12499

	// ### 10-09-2018,Ticket id:124998
	FinanceMain getEntityNEntityDesc(String finRefence, String type, boolean wif);

	FinanceType getFinTypeDetailsByFinreferene(String finReference, String string, boolean b);

	// ### 10-10-2018,Ticket id:124998
	FinanceMain getClosingStatus(String finReference, TableType tempTab, boolean wif);

	boolean isDeveloperFinance(String finReference, String type, boolean wif);

	FinanceMain getFinanceDetailsByFinRefence(String reference, String type);

	List<String> getFinanceMainbyCustId(long custId, String type);

	String getFinanceTypeFinReference(String reference, String type);

	void updateFinPftMaturity(String finReference, String closingStatus, boolean finIsActive);

	void updateFinAssetValue(FinanceMain finMain);

	FinanceMain getFinanceForAssignments(String finReference);

	void updateAssignmentId(String finReference, long assignmentId);

	Map<String, Object> getGLSubHeadCodes(String finRef);

	int getCountByBlockedFinances(String finReference);

	void updateFromReceipt(FinanceMain financeMain, TableType tableType);

	FinanceMain isFlexiLoan(String finReference);

	boolean isFinReferenceExitsinLQ(String finReference, TableType tempTab, boolean wif);// ###
																							// 17-07-2018
																							// Ticket
																							// ID
																							// :
																							// 127950

	List<FinanceMain> getFinanceMainForLinkedLoans(long custId);

	List<FinanceMain> getFinanceMainForLinkedLoans(String finReference);

	Map<String, Object> getGSTDataMap(String finReference, TableType tableType);

	Map<String, Object> getGSTDataMap(long custId, TableType tableType);

	boolean isFinActive(String finReference);

	String getFinanceMainByRcdMaintenance(String finReference, String type);

	FinanceMain getRcdMaintenanceByRef(String finReference, String type);

	void deleteFinreference(FinanceMain financeMain, TableType tableType, boolean wifi, boolean finilize);

	// Income Amortization
	FinanceMain getFinanceForIncomeAMZ(String finReference);

	List<FinanceMain> getFinListForIncomeAMZ(Date curMonthStart);

	List<FinanceMain> getFinListForAMZ(Date monthEndDate);

	FinanceMain getFinanceMainByOldFinReference(String oldFinReference, boolean active);

	// Calculate Average POS
	List<FinanceMain> getFinancesByFinApprovedDate(Date finApprovalStartDate, Date finApprovalEndDate);

	int getCountByFinReference(String finReference, boolean active);

	int getCountByOldFinReference(String oldFinReference);

	long getLoanWorkFlowIdByFinRef(String loanReference, String type);

	String getLovDescEntityCode(String finReference, String string);

	long saveHostRef(FinanceMainExtension financeMainExtension);

	FinanceMain getFinanceMainByHostReference(String oldFinReference, boolean active);

	int getCountByExternalReference(String oldFinReference);

	int getCountByOldHostReference(String oldFinReference);

	// Reinstate Loan
	String saveRejectFinanace(FinanceMain financeMain);

	void updateRejectFinanceMain(FinanceMain financeMain, TableType tempTab, boolean isWIF);

	FinanceMain getFinanceMainStutusById(String id, String type);

	FinanceMain getFinanceDetailsForInsurance(String finReference, String type);

	List<FinanceMain> getFinMainListBySQLQueryRule(String whereClause, String type);

	FinanceMain getFinanceMainDetails(String reference);

	boolean isFinExistsByPromotionSeqID(long referenceId);

	boolean isRepayFrqExists(String brType);

	boolean isGrcRepayFrqExists(String brType);

	Date getFinStartDate(String finReference);

	FinanceMain getFinanceMain(String finReference, String[] columns);

	List<FinanceEnquiry> getAllFinanceDetailsByCustId(long custId);

	void updateCustChange(long newCustId, long mandateId, String finReference, String type);

	List<UserPendingCases> getUserPendingCasesDetails(long userID, String roleCodes);

	Long getCustomerIdByFin(String FinReference);

	FinanceMain getEHFinanceMain(String finReference);

	void updateEHFinanceMain(FinanceMain financeMain);

	String getFinBranch(String finReference);

	Date getClosedDateByFinRef(String finReference);

	FinanceMain getFinBasicDetails(String finReference, String type);

	void updateDeductFeeDisb(FinanceMain financeMain, TableType tableType);

	FinanceMain getFinanceMain(String finReference, String[] columns, String type);

	List<UserPendingCases> getUserPendingCasesDetails(String userLogin, String roleCode);

	FinanceMain getFinDetailsForHunter(String leadId, String type);

	DMSQueue getOfferIdByFin(DMSQueue dmsQueue);

	void updatePmay(String finReference, boolean pmay, String type);

	FinCustomerDetails getDetailsByOfferID(String offerID);

	List<FinanceMain> getFinanceByInvReference(String finReference, String type);

	List<String> getInvestmentFinRef(String finReference, String type);

	List<String> getParentRefifAny(String finReference, String type, boolean isFromAgr);

	Date getClosedDate(String finReference);

	void updateTdsApplicable(FinanceMain financeMain);

	boolean ispmayApplicable(String finReference, String type);

	void updateRepaymentAmount(FinanceMain financeMain);

	void updateRestructure(String finReference, boolean restructure);

	void updateWriteOffStatus(String finReference, boolean writeoffLoan);

	FinanceMain getFinCategoryByFinRef(String finReference);

	void updateMaintainceStatus(String finReference, String rcdMaintainSts);

}