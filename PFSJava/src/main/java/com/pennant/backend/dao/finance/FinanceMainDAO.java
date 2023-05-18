/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinanceMainDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified Date
 * : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.app.core.CustEODEvent;
import com.pennant.backend.model.applicationmaster.LoanPendingData;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerCoreBank;
import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExtension;
import com.pennant.backend.model.finance.FinanceStatusEnquiry;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.UserPendingCases;
import com.pennant.backend.model.sourcingdetails.SourcingDetails;
import com.pennanttech.pennapps.dms.model.DMSQueue;
import com.pennanttech.pff.core.TableType;

public interface FinanceMainDAO {
	FinanceMain getFinanceMain(boolean isWIF);

	FinanceMain getFinanceMain(long finID, String nextRoleCode, String type);

	FinanceMain getFinanceMainByRef(String finReferece, String type, boolean isWIF);

	FinanceMain getFinanceMainById(long finID, String type, boolean isWIF);

	long save(FinanceMain financeMain, TableType tableType, boolean wif);

	void update(FinanceMain financeMain, TableType tableType, boolean wif);

	void delete(FinanceMain financeMain, TableType tableType, boolean wif, boolean finalize);

	boolean isFinReferenceExists(String reference, String type, boolean isWIF);

	List<Long> getFinanceMainListByBatch(Date curBD, Date nextBD, String type);

	List<BigDecimal> getActualPftBal(long finID, String type);

	List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);

	void updateCustCIF(long custID, long finID);

	void updateFinBlackListStatus(long finID);

	FinanceSummary getFinanceProfitDetails(long finID);

	Boolean saveRejectFinanceDetails(FinanceMain financeMain);

	FinanceMain getFinanceMainForBatch(long finID);

	FinanceMain getFinanceMainForPftCalc(long finID);

	FinanceMain getFinanceMainForRpyCancel(long finID);

	String getNextRoleCodeByRef(long finID);

	List<String> getFinanceWorlflowFirstTaskOwners(String event, String moduleName);

	void updateNextUserId(List<Long> finRefList, String oldUserId, String newUserId, boolean singleUser);

	void updateDeviationApproval(FinanceMain financeMain, boolean rejected, String type);

	List<FinanceSummary> getFinExposureByCustId(long custId);

	List<FinanceMain> getFinanceRefByPriority();

	void saveFinanceSnapshot(FinanceMain financeMain);

	String getApprovedRepayMethod(long finID, String type);

	void updateMaturity(long finID, String closingStatus, boolean finIsActive, Date date);

	List<String> getScheduleEffectModuleList(boolean schdChangeReq);

	List<FinanceMain> getFinanceMainbyCustId(long id);

	int getFinanceCountById(long finID, String type, boolean isWIF);

	Long getFinIDByFinReference(String finReference, String type, boolean isWIF);

	int getFinanceCountByMandateId(long mandateID);

	Long getFinIDForMandate(String finReference, long mandateID);

	int loanMandateSwapping(long finID, long newMandateID, String repayMethod, String type, boolean securityMandate);

	FinanceMain getFinanceDetailsForService(long finID, String type, boolean isWIF);

	FinanceMain getFinanceDetailsForService1(String finReference, String type, boolean isWIF);// FIXME Remove 1

	int updateFinanceBasicDetails(FinanceMain finacneMain, String type);

	List<String> getUsersLoginList(List<String> nextRoleCodes);

	FinanceMain getFinanceMainParms(long finID);

	List<FinanceMain> getFinanceByCustId(long custId, String type);

	List<FinanceMain> getFinanceByCollateralRef(String collateralRef);

	List<Long> getFinReferencesByMandateId(long mandateId);

	List<Long> getFinIDList(String custCIF, String closingStatus);

	BigDecimal getFinAssetValue(long finID);

	FinanceMain getDisbursmentFinMainById(long finID, TableType tableType);

	FinanceMain getDisbursmentFinMainById(String finReference, TableType tableType);

	List<FinanceMain> getFinMainsForEODByCustId(Customer customer);

	FinanceMain getFinMainsForEODByFinRef(long finID, boolean isActive);

	void updateFinanceInEOD(FinanceMain financeMain, List<String> updateFields, boolean rateRvw);

	void updatePaymentInEOD(FinanceMain financeMain);

	List<FinanceMain> getBYCustIdForLimitRebuild(long id, boolean orgination);

	FinanceMain getFinanceBasicDetailByRef(long finID, boolean isWIF);

	int getFinCountByCustId(long custID);

	void updateFinMandateId(Long mandateId, long finID, String type);

	long getMandateIdByRef(long finID, String type);

	int getFinanceCountById(long finID);

	boolean isAppNoExists(String applicationNo, TableType type);

	String getApplicationNoById(long finID, String type);

	List<FinanceMain> getFinancesByExpenseType(String finType, Date finApprovalStartDate, Date finApprovalEndDate);

	boolean isFinTypeExistsInFinanceMain(String finType, String string);

	boolean isLoanPurposeExits(String purposeCode, String string);

	String getEarlyPayMethodsByFinRefernce(long finID);

	List<LoanPendingData> getCustomerODLoanDetails(long userID);

	void updateNextUserId(long finID, String nextUserId);

	String getNextUserId(long finID);

	int getActiveCount(String finType, long custID);

	int getODLoanCount(String finType, long custID);

	List<FinanceMain> getUnApprovedFinances();

	Long getFinID(String finReference, String entity, TableType tableType);

	// ### 10-09-2018,Ticket id:124998
	FinanceMain getEntityNEntityDesc(long finID, String type, boolean wif);

	// ### 10-10-2018,Ticket id:124998
	FinanceMain getClosingStatus(long finID, TableType tempTab, boolean wif);

	boolean isDeveloperFinance(long finID, String type, boolean wif);

	FinanceMain getFinanceDetailsByFinRefence(long finID, String type);

	String getFinanceType(String finReference, TableType tabelType);

	String getFinanceType(long finID, TableType tabelType);

	void updateFinAssetValue(FinanceMain finMain);

	FinanceMain getFinanceForAssignments(long finID);

	void updateAssignmentId(long finID, long assignmentId);

	Map<String, Object> getGLSubHeadCodes(long finID);

	int getCountByBlockedFinances(long finID);

	void updateFromReceipt(FinanceMain financeMain, TableType tableType);

	FinanceMain isFlexiLoan(long finID);

	boolean isFinReferenceExitsinLQ(long finID, TableType tempTab, boolean wif);

	List<FinanceMain> getFinanceMainForLinkedLoans(long custId);

	List<FinanceMain> getFinanceMainForLinkedLoans(String finReference);

	Map<String, Object> getGSTDataMap(long finID, TableType tableType);

	Map<String, Object> getCustGSTDataMap(long custId, TableType tableType);

	boolean isFinActive(long finID);

	String getFinanceMainByRcdMaintenance(long finID);

	FinanceMain getRcdMaintenanceByRef(long finID, String type);

	void deleteFinreference(FinanceMain financeMain, TableType tableType, boolean wifi, boolean finilize);

	// Income Amortization
	FinanceMain getFinanceForIncomeAMZ(long finID);

	List<FinanceMain> getFinListForIncomeAMZ(Date curMonthStart);

	List<FinanceMain> getFinListForAMZ(Date monthEndDate);

	FinanceMain getFinanceMainByOldFinReference(String oldFinReference, boolean active);

	// Calculate Average POS
	List<FinanceMain> getFinancesByFinApprovedDate(Date finApprovalStartDate, Date finApprovalEndDate);

	int getCountByFinReference(long finID, boolean active);

	int getCountByOldFinReference(String oldFinReference);

	long getLoanWorkFlowIdByFinRef(String finReference, String type);

	String getLovDescEntityCode(long finID, String string);

	void saveHostRef(FinanceMainExtension financeMainExtension);

	FinanceMain getFinanceMainByHostReference(String oldFinReference, boolean active);

	int getCountByExternalReference(String oldFinReference);

	int getCountByOldHostReference(String oldFinReference);

	// Reinstate Loan
	String saveRejectFinanace(FinanceMain financeMain);

	void updateRejectFinanceMain(FinanceMain financeMain, TableType tempTab, boolean isWIF);

	FinanceMain getUserActions(String finReference);

	FinanceMain getFinanceDetailsForInsurance(long finID, String type);

	List<FinanceMain> getFinMainListBySQLQueryRule(String whereClause, String type);

	FinanceMain getFinanceMainDetails(long finID);

	boolean isFinExistsByPromotionSeqID(long referenceId);

	boolean isRepayFrqExists(String brType);

	boolean isGrcRepayFrqExists(String brType);

	Date getFinStartDate(long finID);

	FinanceMain getFinanceMain(long finID, String[] columns);

	List<FinanceEnquiry> getAllFinanceDetailsByCustId(long custId);

	void updateCustChange(long newCustId, long mandateId, long finID, String type);

	List<UserPendingCases> getUserPendingCasesDetails(long userID, String roleCodes);

	Long getCustomerIdByFin(String finReference);

	FinanceMain getEHFinanceMain(long finID);

	void updateEHFinanceMain(FinanceMain financeMain);

	String getFinBranch(long finID);

	Date getClosedDateByFinRef(long finID);

	FinanceMain getFinBasicDetails(long finID, String type);

	void updateDeductFeeDisb(FinanceMain financeMain, TableType tableType);

	FinanceMain getFinanceMain(long finID, String[] columns, String type);

	List<UserPendingCases> getUserPendingCasesDetails(String userLogin, String roleCode);

	FinanceMain getFinDetailsForHunter(String leadId, String type);

	DMSQueue getOfferIdByFin(DMSQueue dmsQueue);

	void updatePmay(long finID, boolean pmay, String type);

	FinCustomerDetails getDetailsByOfferID(String offerID);

	List<FinanceMain> getFinanceByInvReference(long finID, String type);

	List<Long> getInvestmentFinRef(String investmentRef, String type);

	List<Long> getParentRefifAny(String parentRef, String type, boolean isFromAgr);

	Date getClosedDate(long finID);

	void updateTdsApplicable(FinanceMain financeMain);

	boolean isPmayApplicable(long finID, String type);

	void updateRepaymentAmount(FinanceMain financeMain);

	void updateRestructure(long finID, boolean restructure);

	void updateWriteOffStatus(long finID, boolean writeoffLoan);

	String getFinCategory(String finReference);

	void updateMaintainceStatus(long finID, String rcdMaintainSts);

	void updateMaintainceStatus(String finReference, String rcdMaintainSts);

	List<Long> getChildFinRefByParentRef(String parentRef);

	void updateChildFinance(List<FinanceMain> fm, String type);

	void updateRejectFinanceMain(List<FinanceMain> list, String string);

	void updateSchdVersion(FinanceMain fm, boolean isPresentment);

	int getSchdVersion(long finID);

	Map<String, Object> getGSTDataMapForDealer(long manufacturerDealerId);

	FinanceMain getFinMainLinkedFinancesByFinRef(long finID);

	FinanceMain getFinanceMain(String finReference);

	FinanceMain getFinanceMain(String finReference, TableType tableType);

	FinanceMain getFinanceMain(long finID);

	FinanceMain getFinanceMain(long finID, TableType tableType);

	Long getFinID(String finReference);

	Long getActiveFinID(String finReference);

	Long getFinID(String finReference, TableType tableType);

	Long getActiveFinID(String finReference, TableType tableType);

	Long getActiveWIFFinID(String finReference, TableType tableType);

	FinanceMain getFMForVAS(String finReference);

	FinanceStatusEnquiry getLoanStatusDetailsByFinReference(long finID);

	String getFinCategoryByFinType(String finType);

	int getCustomerBankCountById(Long BankId, long custId);

	FinanceMain getRejectFinanceMainByRef(String finReference);

	FinanceMain getFinanceMainForAdviseUpload(String finReference);

	FinanceMain getFinanceMainForLMSEvent(long finID);

	String getLovDescFinDivisionByReference(String finReference);

	Map<String, Object> getExtendedFields(String reference, String tableName);

	// FIXME the below method should be moved to corresponding DAO classes of CD/OverDraft
	FinanceMain getFinanceMainByReference(String finReference, boolean active);

	boolean isOverDraft(String finReference);

	String getEntityCodeByRef(String finReference);

	Long getCustomerIdByFinRef(String finReference);

	Long getCustomerIdByFinID(long finID);

	void updateFinanceForGraceEndInEOD(FinanceMain financeMain);

	List<FinanceMain> getForFinanceExposer(long custId);

	int getBucketByFinStatus(long finID);

	List<FinanceMain> getFinanceMainActiveList(Date fromDate, Date toDate, String finType);

	String getOrgFinCategory(String finReference);

	FinanceMain getFinanceMain(String finReference, String entity);

	Date getMaturityDate(String finReference);

	FinanceMain getEntityByRef(String finReference);

	List<AutoRefundLoan> getAutoRefunds(CustEODEvent cee);

	void updateSettlementFlag(long finID, boolean isUnderSettlement);

	FinanceMain getFinanceMainForExcessTransfer(long finId);

	List<Long> getFinIDsByCustomer(CustomerCoreBank customerCoreBank);

	FinanceMain getBasicDetails(String finReference, TableType tableType);

	SourcingDetails getSourcingDetails(long finID, TableType tableType);

	List<Long> getFinIDsByCustID(Long custID, TableType tableType);
}