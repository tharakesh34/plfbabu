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
 * * FileName : FinanceMainService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * * Modified
 * Date : 15-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jaxen.JaxenException;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.pff.verification.VerificationType;

public interface FinanceDetailService {

	FinanceDetail getFinanceDetail(boolean isWIF);

	FinanceDetail getNewFinanceDetail(boolean isWIF);

	AuditHeader saveOrUpdate(AuditHeader auditHeader, boolean isWIF);

	FinanceDetail getOriginationFinance(long finID, String nextRoleCode, String procEdtEvent, String userrole);

	FinanceDetail getServicingFinance(String financeReference, String eventCode, String procEdtEvent, String userrole);

	FinanceDetail getServicingFinanceForQDP(long finID, String eventCode, String procEdtEvent, String userrole);

	FinanceDetail getWIFFinance(String financeReference, boolean reqCustDetail, String procEdtEvent);

	FinanceDetail getFinanceDetailById(String financeReference, boolean isWIF, String eventCode, boolean reqCustDetail,
			String procEdtEvent, String userrole);

	FinanceDetail getApprovedFinanceDetailById(String financeReference, boolean isWIF);

	AuditHeader delete(AuditHeader auditHeader, boolean isWIF);

	AuditHeader doApprove(AuditHeader auditHeader, boolean isWIF) throws InterfaceException, JaxenException;

	AuditHeader doReject(AuditHeader auditHeader, boolean isWIF, boolean isAutoReject);

	FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String userRole, String screenCode,
			String eventCode, String procEdtEvent, boolean extFieldsReq);

	boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF);

	FinScheduleData getFinSchDataByFinRef(String financeReference, String type, long logKey);

	List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,
			String postingGroubBy, String type);

	FinScheduleData getFinSchDataById(String finReference, String type, boolean summaryRequired);

	AuditHeader doCheckLimits(AuditHeader auditHeader);

	void updateCustCIF(long custID, String finReference);

	FinContributorHeader getFinContributorHeaderById(String finReference);

	List<DocumentDetails> getFinDocByFinRef(String finReference, String finEvent, String type);

	DocumentDetails getFinDocDetailByDocId(long docId);

	FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType, String finType, String userRole,
			String procEdtEvent);

	List<FinanceRepayments> getFinanceRepaymentsByFinRef(long finID, boolean isRpyCancelProc);

	AuditHeader doCheckExceptions(AuditHeader auditHeader);

	String getCustStatusByMinDueDays();

	CustomerEligibilityCheck getCustEligibilityDetail(Customer customer, String productCode, String finReference,
			String finCcy, BigDecimal curFinRpyAmount, int months, BigDecimal custDSR,
			List<JointAccountDetail> jointAccountDetails);

	FinanceSummary getFinanceProfitDetails(String finRef);

	FinanceProfitDetail getFinProfitDetailsById(String finReference);

	boolean checkFirstTaskOwnerAccess(Set<String> userroles, String event, String moduleName);

	List<Rule> getFeeRuleDetails(FinanceType finType, Date startDate, boolean isWIF);

	// List<ErrorDetail> getDiscrepancies(FinanceDetail financeDetail);

	List<FeeRule> getApprovedFeeRules(String finReference, String finEvent, boolean isWIF);

	List<CustomerIncome> prepareIncomeDetails();

	CustomerEligibilityCheck getWIFCustEligibilityDetail(WIFCustomer customer, String finCcy)
			throws IllegalAccessException, InvocationTargetException;

	boolean checkExistCustIsBlackListed(long custID);

	BigDecimal getCustRepayBankTotal(long custId);

	FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData);

	BigDecimal getAccrueAmount(String finReference);

	List<FinanceSummary> getFinExposureByCustId(long custId);

	FeeRule getFeeChargesByFinRefAndFeeCode(String finReference, String feeCode, String tableType);

	boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType);

	String getUserRoleCodeByRefernce(long userId, String reference, List<String> roleCodes);

	String getNextRoleCodeByRef(long finID);

	FinanceMain getFinanceMain(String finReference, String type);

	AuditHeader doPreApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException;

	FinanceDetail getPreApprovalFinanceDetailsById(String finReference);

	FinanceDetail getFinanceOrgDetails(FinanceMain financeMain, String type);

	FinanceDetail getFinSchdDetailById(String finReference, String type, boolean isWIF);

	TATDetail getTATDetail(String reference, String rolecode);

	void saveTATDetail(TATDetail tatDetail);

	void updateTATDetail(TATDetail tatDetail);

	String getApprovedRepayMethod(String finReference, String type);

	DocumentDetails getFinDocDetailByDocId(long docId, String type, boolean readAttachment);

	List<DocumentDetails> getDocumentDetails(String finReference, String finProcEvent);

	List<String> getScheduleEffectModuleList(boolean schdChangeReq);

	List<FinTypeFees> getFinTypeFees(String finType, String eventCode, boolean origination, int moduleId);

	List<FinTypeFees> getSchemeFeesList(long referenceId, String finEvent, String type, boolean origination,
			int moduleId);

	BigDecimal getTotalRepayAmount(String finReference);

	List<String> getUsersLoginList(List<String> nextRoleCodes);

	FinanceDetail getWIFFinanceDetailById(String finReference, String procEdtEvent);

	List<FinanceDisbursement> getFinanceDisbursements(final String id, String type, boolean isWIF);

	FinanceMain getFinanceMainParms(String finReference);

	public void doSaveAddlFieldDetails(FinanceDetail financeDetail, String tableType);

	BigDecimal getFinAssetValue(String finReference);

	List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid);

	FinScheduleData getFinSchDataForReceipt(String finReference, String type);

	List<FinanceStepPolicyDetail> getFinStepPolicyDetails(String finReference, String type, boolean isWIF);

	List<FinanceScheduleDetail> getFinScheduleList(String finReference);

	// EOD Process Checking
	int getProgressCountByCust(long custID);

	FinanceMain getFinanceMainForBatch(String finReference);

	BigDecimal getOutStandingBalFromFees(String finReference);

	public FinanceDetail getFinanceDetailForCovenants(FinanceMain financeMain);

	public FinanceDetail getFinanceDetailForCollateral(FinanceMain financeMain);

	public FinanceDetail getFinanceDetailForFinOptions(FinanceMain financeMain);

	AuditHeader executeWorkflowServiceTasks(AuditHeader auditHeader, String role, String usrAction,
			WorkflowEngine engine) throws AppException, JaxenException, Exception;

	FinanceMain setDefaultFinanceMain(FinanceMain financeMain, FinanceType financeType);

	FinODPenaltyRate setDefaultODPenalty(FinODPenaltyRate finODPenaltyRate, FinanceType financeType);

	DocumentDetails getDocumentDetails(long id, String type);

	FinanceScheduleDetail getFinSchduleDetails(String finReference, Date schDate);

	void updateNextUserId(String finReference, String nextUserId);

	String getNextUserId(String finReference);

	CustomerEligibilityCheck getODLoanCustElgDetail(FinanceDetail detail);

	boolean isholdDisbursementProcess(String finReference);

	void executeAutoFinRejectProcess();

	List<FinAssetTypes> getFinAssetTypesByFinRef(String reference, String type);

	List<Integer> getFinanceDisbSeqs(String finReferecne, boolean isWIF);

	// Linked Loans
	List<FinanceProfitDetail> getFinProfitListByFinRefList(List<String> finRefList);

	List<FinanceMain> getFinanceMainForLinkedLoans(String finReference);

	List<FinanceMain> getFinanceMainForLinkedLoans(long custId);

	String getCustomerDueFinReferces(long custID);

	List<FinanceScheduleDetail> getFinSchdDetailsForRateReport(String finReference);

	FinanceMain getFinanceMainForRateReport(String finReference, String type);

	String getFinanceMainByRcdMaintenance(long finID, String type);

	FinanceMain getRcdMaintenanceByRef(String reference, String type);

	List<FinTypeFees> getSchemeFeesList(long referenceId, String eventCode, boolean origination, int moduleId);

	Date getFinStartDate(String finReference);

	FinanceDetail getVerificationInitiationDetails(String finReference, VerificationType verificationType,
			String tableType);

	Map<String, Object> getUpLevelUsers(long usrId, String branch);

	FinanceDetail getFinanceDetailsForPmay(String finReference);

	FinCustomerDetails getDetailsByOfferID(String offerID);

	void saveDisbDetails(List<FinanceDisbursement> disbursementDetails, String finReference);

	void saveFinSchdDetail(List<FinanceScheduleDetail> financeScheduleDetails, String finReference);

	FinanceDetail getFinSchdDetailByRef(String finReference, String string, boolean b);

	List<FinanceScheduleDetail> getFinScheduleDetails(String finReference, String string, boolean b);

	List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(String finReference, String string, boolean b);

	List<RepayInstruction> getRepayInstructions(String finReference, String string, boolean b);

	void processRestructureAccounting(AEEvent aeEvent, FinanceDetail financeDetail);

	List<ReturnDataSet> prepareSubVenAccounting(AEEvent aeEvent, FinanceDetail financeDetail);
}
