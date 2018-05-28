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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.jaxen.JaxenException;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface FinanceDetailService {
	
	FinanceDetail getFinanceDetail(boolean isWIF);
	FinanceDetail getNewFinanceDetail(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF) ;
	FinanceDetail getOriginationFinance(String financeReference,String nextRoleCode,String procEdtEvent,String userrole);
	FinanceDetail getServicingFinance(String financeReference, String eventCode, String procEdtEvent,String userrole);
	FinanceDetail getWIFFinance(String financeReference, boolean reqCustDetail, String procEdtEvent);
	FinanceDetail getFinanceDetailById(String financeReference,boolean isWIF, String eventCode, boolean reqCustDetail, String procEdtEvent,String userrole);
	FinanceDetail getApprovedFinanceDetailById(String financeReference,boolean isWIF);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF) throws InterfaceException, JaxenException;
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF) ;
	FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String userRole,String screenCode, String eventCode, String procEdtEvent, boolean extFieldsReq);
	boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF);
	FinScheduleData getFinSchDataByFinRef(String financeReference, String type, long logKey);
	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,String postingGroubBy);
	FinScheduleData getFinSchDataById(String finReference, String type, boolean summaryRequired);
	AuditHeader doCheckLimits(AuditHeader auditHeader);
	void updateCustCIF(long custID, String finReference);
	FinContributorHeader getFinContributorHeaderById(String finReference);
	List<DocumentDetails> getFinDocByFinRef(String finReference, String finEvent, String type);
	DocumentDetails getFinDocDetailByDocId(long docId);
	
	FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType, String finType, String userRole, String procEdtEvent);
	List<FinanceRepayments> getFinanceRepaymentsByFinRef(final String id, boolean isRpyCancelProc);
	AuditHeader doCheckExceptions(AuditHeader auditHeader);
	
	List<String> getFinanceReferenceList();
	String getCustStatusByMinDueDays();
	CustomerEligibilityCheck getCustEligibilityDetail(Customer customer, String productCode,String finReference, String finCcy, 
				BigDecimal curFinRpyAmount,int months,  BigDecimal custDSR, List<JointAccountDetail> jointAccountDetails);
	FinanceSummary getFinanceProfitDetails(String finRef);
	List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);

	boolean bulkRateChangeFinances(List<BulkProcessDetails> bulkRateChangeFinances, 
	String recalType, BigDecimal rateChange) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate);
	boolean bulkDefermentChanges(List<BulkDefermentChange> defermentChangeFinances, String recalType, boolean excludeDeferment, 
	String addTermAfter, Date calFromDate, Date calToDate) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	
	FinanceMain fetchConvertedAmounts(FinanceMain financeMain, boolean calAllAmounts);
	FinanceProfitDetail getFinProfitDetailsById(String finReference);
	boolean checkFirstTaskOwnerAccess(Set<String> userroles, String event, String moduleName);
	List<ContractorAssetDetail> getContractorAssetDetailList(String finReference);
	List<Rule> getFeeRuleDetails(FinanceType finType, Date startDate, boolean isWIF);
	List<ErrorDetail> getDiscrepancies(FinanceDetail financeDetail);
	List<FeeRule> getApprovedFeeRules(String finReference,String finEvent,  boolean isWIF);
	List<CustomerIncome> prepareIncomeDetails();
	CustomerEligibilityCheck getWIFCustEligibilityDetail(WIFCustomer customer ,String finCcy) throws IllegalAccessException, InvocationTargetException;
	boolean checkExistCustIsBlackListed(long custID);
	List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId);
	BigDecimal getCustRepayBankTotal(long custId);
	FeeRule getInsFee(String finReference);
	FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData);
	BigDecimal getAccrueAmount(String finReference);
	List<FinanceSummary> getFinExposureByCustId(long custId);
	
	FeeRule getFeeChargesByFinRefAndFeeCode(String finReference, String feeCode, String tableType);
	boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType);
	
	String getUserRoleCodeByRefernce(long userId, String reference,List<String> roleCodes);
	
	void updateFinancePriority();
	void updateFinApprovalStatus(String finReference, String approvalStatus);
	String getNextRoleCodeByRef(String finReference, String type);
	FinanceMain getFinanceMain(String finReference, String type);
	AuditHeader doPreApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException;

	List<String> getRollOverLimitRefList();
	List<String> getRollOverFinTypeList(String limitRef);
	List<Date> getRollOverNextDateList(String limitRef, String finType);
	List<RolledoverFinanceDetail> getRolloverFinanceList(String value, String value2, Date dbDate);
	FinanceDetail getPreApprovalFinanceDetailsById(String finReference);
	FinanceDetail getFinanceOrgDetails(FinanceMain financeMain, String type);
	FinanceDetail getFinSchdDetailById(String finReference, String type, boolean isWIF);
	TATDetail getTATDetail(String reference,String rolecode);
	void saveTATDetail(TATDetail tatDetail);
	void updateTATDetail(TATDetail tatDetail);
	String getApprovedRepayMethod(String finReference, String type);
	DocumentDetails getFinDocDetailByDocId(long docId, String type, boolean readAttachment);
	List<DocumentDetails> getDocumentDetails(String finReference, String finProcEvent);
	List<String> getScheduleEffectModuleList(boolean schdChangeReq);
	List<FinTypeFees> getFinTypeFees(String finType,String eventCode, boolean origination, int moduleId);
	BigDecimal getTotalRepayAmount(String finReference);
	List<String> getUsersLoginList(List<String> nextRoleCodes);
	FinanceDetail getWIFFinanceDetailById(String finReference, 	String procEdtEvent);
	List<FinanceDisbursement> getFinanceDisbursements(final String id, String type, boolean isWIF);
	FinanceMain getFinanceMainParms(String finReference);
	public void doSaveAddlFieldDetails(FinanceDetail financeDetail, String tableType);
	BigDecimal getFinAssetValue(String finReference);
	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid);
	FinScheduleData getFinSchDataForReceipt(String finReference, String type);
	List<FinanceStepPolicyDetail> getFinStepPolicyDetails(String finReference, String type, boolean isWIF);
	
	// EOD Process Checking
	int getProgressCountByCust(long custID);
	List<ReturnDataSet> prepareVasAccounting(AEEvent aeEvent, List<VASRecording> vasRecordings);
	FinanceMain getFinanceMainForBatch(String finReference);
	BigDecimal getOutStandingBalFromFees(String finReference);
	
	public FinanceDetail getFinanceDetailForCovenants(FinanceMain financeMain);
	AuditHeader executeWorkflowServiceTasks(AuditHeader auditHeader, String role, String usrAction, WorkflowEngine engine) 
			throws AppException, JaxenException, Exception;
	FinanceMain setDefaultFinanceMain(FinanceMain financeMain, FinanceType financeType);
	FinODPenaltyRate setDefaultODPenalty(FinODPenaltyRate finODPenaltyRate, FinanceType financeType);
	DocumentDetails getDocumentDetails(long id, String type);
	
	//GST
	HashMap<String, Object> prepareGstMappingDetails(FinanceDetail financeDetail, String branchCode);
 }