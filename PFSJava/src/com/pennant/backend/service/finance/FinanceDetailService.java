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
 * FileName    		:  FinanceMainService.java                                                   * 	  
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

package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface FinanceDetailService {
	
	FinanceDetail getFinanceDetail(boolean isWIF);
	FinanceDetail getNewFinanceDetail(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF) throws AccountNotFoundException;
	FinanceDetail getFinanceDetailById(String financeReference,boolean isWIF, String eventCode, boolean reqCustDetail);
	FinanceDetail getApprovedFinanceDetailById(String financeReference,boolean isWIF);
	FinanceDetail refresh(FinanceDetail financeDetail);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF) throws AccountNotFoundException;
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF);
	FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String userRole,String screenCode, String eventCode);
	boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF);
	void maintainWorkSchedules(String finReference, long userId,
	List<FinanceScheduleDetail> financeScheduleDetails );
	FinanceDetail getStaticFinanceDetailById(String financeReference, String type);
	FinScheduleData getFinSchDataByFinRef(String financeReference, String type, long logKey);
	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal);
	FinScheduleData getFinSchDataById(String finReference, String type, boolean summaryRequired);
	AuditHeader doCheckLimits(AuditHeader auditHeader);
	void updateCustCIF(long custID, String finReference);
	void updateFinBlackListStatus(String finReference);
	FinContributorHeader getFinContributorHeaderById(String finReference);
	List<DocumentDetails> getFinDocByFinRef(String finReference, String type);
	DocumentDetails getFinDocDetailByDocId(long docId);
	
	FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType, String finType, 
	String userRole);
	List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id, boolean isRpyCancelProc);
	AuditHeader doCheckExceptions(AuditHeader auditHeader);
	
	List<String> getFinanceReferenceList();
	String getCustStatusByMinDueDays();
	CustomerEligibilityCheck getCustEligibilityDetail(Customer customer, String productCode,String finCcy, 
	BigDecimal curFinRpyAmount,int months, BigDecimal finAmount, BigDecimal custDSR);
	FinanceSummary getFinanceProfitDetails(String finRef);
	List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);

	boolean bulkRateChangeFinances(List<BulkProcessDetails> bulkRateChangeFinances, 
	String recalType, BigDecimal rateChange) throws AccountNotFoundException;
	List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate);
	boolean bulkDefermentChanges(List<BulkDefermentChange> defermentChangeFinances, String recalType, boolean excludeDeferment, 
	String addTermAfter, Date calFromDate, Date calToDate) throws AccountNotFoundException;
	
	FinanceMain fetchConvertedAmounts(FinanceMain financeMain, boolean calAllAmounts);
 	CustomerFinanceDetail getCustomerFinanceById(String financeReference);
	CustomerFinanceDetail getApprovedCustomerFinanceById(String financeReference);
	FinanceProfitDetail getFinProfitDetailsById(String finReference);
	boolean checkFirstTaskOwnerAccess(String productCode, long usrLogin);
	List<ContractorAssetDetail> getContractorAssetDetailList(String finReference);
	List<Rule> getFeeRuleDetails(FinanceType finType, Date startDate, boolean isWIF);
	List<ErrorDetails> getDiscrepancies(FinanceDetail financeDetail);
	List<FeeRule> getApprovedFeeRules(String finReference, boolean isWIF);
	List<CustomerIncome> prepareIncomeDetails();
	CustomerEligibilityCheck getWIFCustEligibilityDetail(WIFCustomer customer, String productCode,
            String finCcy, BigDecimal curFinRpyAmount, int months, BigDecimal finAmount) throws IllegalAccessException, InvocationTargetException;
	boolean checkExistCustIsBlackListed(long custID);
	List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId);
	FinScheduleData getFinSchDataByFinRef(String finReference);
	BigDecimal getCustRepayBankTotal(long custId);
	List<Long> getMailTemplatesByFinType(String financeType, String roleCode);
	FeeRule getTakafulFee(String finReference);
	FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData);
	BigDecimal getAccrueAmount(String finReference);
	
	List<FeeRule> getFeeChargesByFeeCode(String feeCode, String tableType);
	FeeRule getFeeChargesByFinRefAndFeeCode(String finReference, String feeCode, String tableType);
	boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType);
	FinanceDetail getFinAssetDetails(String finReference, String type); 
	
	long getNewUserId(String module, String nextRoleCode, long userId);
	long getNextUserIdFromUserActivity(String module, String reference, String roleCode, boolean multipleRoles);
	void save(QueueAssignment queueAssignment);
	void updateUserCounts(String module, String increaseRoleCode, long increaseUserId, String decreaseRoleCode, long decreaseUserId);
	void updateUserCounts(String module, String roleCode, long UserId);
	void updateFailedRecordCount(List<QueueAssignment> queueAssignmentList, QueueAssignment rollBackUser);
	
	void updateFinancePriority();
 }