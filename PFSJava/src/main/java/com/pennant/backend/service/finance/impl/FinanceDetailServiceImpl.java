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
 * * FileName : FinanceDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * *
 * Modified Date : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.TATDetailDAO;
import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.dao.UserActivityLogDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinContributorHeaderDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.finance.FinanceRejectDetailDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.IndicativeTermDetailDAO;
import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.dao.finance.RolledoverFinanceDAO;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeInsuranceDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.staticparms.ExtendedFieldHeaderDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.UserActivityLog;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BundledProductsDetail;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.RolledoverFinanceHeader;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.staticparms.ExtendedFieldRender;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.backend.service.collateral.impl.ExtendedFieldDetailsValidation;
import com.pennant.backend.service.collateral.impl.FlagDetailValidation;
import com.pennant.backend.service.configuration.impl.VasRecordingValidation;
import com.pennant.backend.service.customermasters.GCDCustomerService;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.handlinstruction.HandlingInstructionService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.<br>
 * 
 */
public class FinanceDetailServiceImpl extends GenericFinanceDetailService implements FinanceDetailService {
	private static final Logger				logger	= Logger.getLogger(FinanceDetailServiceImpl.class);

	private CustomerIncomeDAO				customerIncomeDAO;
	private IncomeTypeDAO					incomeTypeDAO;
	private FinContributorHeaderDAO			finContributorHeaderDAO;
	private FinContributorDetailDAO			finContributorDetailDAO;
	private FinanceReferenceDetailDAO		financeReferenceDetailDAO;
	private RuleDAO							ruleDAO;
	private ExtendedFieldHeaderDAO			extendedFieldHeaderDAO;
	private ExtendedFieldDetailDAO			extendedFieldDetailDAO;
	private AccountingSetDAO				accountingSetDAO;
	private RuleExecutionUtil				ruleExecutionUtil;
	private AccountTypeDAO					accountTypeDAO;
	private CustomerLimitIntefaceService	custLimitIntefaceService;
	private FinanceWriteoffDAO				financeWriteoffDAO;
	private IndicativeTermDetailDAO			indicativeTermDetailDAO;
	private NotesDAO						notesDAO;
	private QueueAssignmentDAO				queueAssignmentDAO;
	private UserActivityLogDAO				userActivityLogDAO;
	private TaskOwnersDAO					taskOwnersDAO;
	private LimitInterfaceDAO				limitInterfaceDAO;
	private DedupParmService				dedupParmService;
	private DDAControllerService			ddaControllerService;
	protected FinanceRejectDetailDAO		financeRejectDetailDAO;
	protected RolledoverFinanceDAO			rolledoverFinanceDAO;
	private PayOrderIssueHeaderDAO			payOrderIssueHeaderDAO;
	private TATDetailDAO					tatDetailDAO;
	private CollateralMarkProcess			collateralMarkProcess;
	private HandlingInstructionService		handlingInstructionService;

	private LimitManagement					limitManagement;
	private LimitCheckDetails				limitCheckDetails;

	private OverdraftScheduleDetailDAO		overdraftScheduleDetailDAO;
	private FlagDetailValidation			flagDetailValidation;
	private FinFlagDetailsDAO				finFlagDetailsDAO;
	private FinTypeInsuranceDAO				finTypeInsuranceDAO;
	private ExtendedFieldRenderDAO			extendedFieldRenderDAO;
	private VASRecordingDAO					vasRecordingDAO;
	private ExtendedFieldDetailsValidation	extendedFieldDetailsValidation;
	private FinTypeFeesDAO					finTypeFeesDAO;
	private VasRecordingValidation			vasRecordingValidation;
	private FinTypeVASProductsDAO			finTypeVASProductsDAO;
	private PromotionDAO					promotionDAO;
	private FinFeeDetailDAO					finFeeDetailDAO;
	private FinanceTaxDetailDAO				financeTaxDetailDAO;
	private FinanceTaxDetailService			financeTaxDetailService;
	private GCDCustomerService				gCDCustomerService;
	
	public FinanceDetailServiceImpl() {
		super();
	}

	@Override
	public FinanceDetail getFinanceDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(getFinanceMainDAO().getFinanceMain(isWIF));
		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceDetail getNewFinanceDetail(boolean isWIF) {
		logger.debug("Entering");
		FinanceDetail financeDetail = getFinanceDetail(isWIF);
		financeDetail.setNewRecord(true);
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for Check Have to Creating New Finance Accessibility for User or not
	 */
	@Override
	public boolean checkFirstTaskOwnerAccess(Set<String> userroles, String event, String moduleName) {
		logger.debug("Entering");

		if (userroles == null || userroles.isEmpty()) {
			return false;
		}

		List<String> listFirsttaskOwners = getFinanceMainDAO().getFinanceWorlflowFirstTaskOwners(event, moduleName);

		if (listFirsttaskOwners != null && !listFirsttaskOwners.isEmpty()) {

			for (String firsttaskonwer : listFirsttaskOwners) {

				if (firsttaskonwer.contains(PennantConstants.DELIMITER_COMMA)) {

					String[] firstTaskOners = firsttaskonwer.split(PennantConstants.DELIMITER_COMMA);

					for (String firsttask : firstTaskOners) {

						if (userroles.contains(firsttask)) {
							return true;
						}
					}

				} else {

					if (userroles.contains(firsttaskonwer)) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailsOrg method.
	 * 
	 * @param finReference
	 *            (String)
	 * @param procEdtEvent
	 *            (String)
	 * @param userrole
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getOriginationFinance(String finReference, String nextRoleCode, String procEdtEvent,
			String userrole) {
		logger.debug("Entering");

		//Finance Details
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMain(finReference, nextRoleCode, "_TView");
		if (financeMain == null) {
			return null;
		}
		FinanceDetail financeDetail = getFinanceOrgDetails(financeMain, "_TView");
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		financeMain = scheduleData.getFinanceMain();

		// Customer Details			
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
					financeMain.getCustID(), true, "_AView"));
		}

		//Guaranteer Details			
		financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_TView"));

		//Joint Account Details
		financeDetail.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference,
				"_TView"));

		// Finance Fee Details
		financeDetail.getFinScheduleData().setFinFeeDetailList(
				getFinFeeDetailService().getFinFeeDetailById(finReference, false, "_TView"));
		
		//Finance Receipt Details
		scheduleData.setFinReceiptDetails(getFinFeeDetailService().getFinReceiptDetais(finReference));
		List<Long> feeIds = new ArrayList<Long>();
		for(FinFeeDetail finFeeDetail : scheduleData.getFinFeeDetailList()) {
			feeIds.add(finFeeDetail.getFeeID());
		}
		
		if (!feeIds.isEmpty()) {
			scheduleData.setFinFeeReceipts(getFinFeeDetailService().getFinFeeReceiptsById(feeIds, "_TView"));
		}

		//insurance Details
		scheduleData.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, "_TView", false));

		// FinSchFrqInsurance Details
		if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {

			List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO().getFinSchFrqInsuranceFinRef(
					finReference, false, "_TView");

			if (finSchFrqInsurances != null && !finSchFrqInsurances.isEmpty()) {

				HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();
				for (int i = 0; i < finSchFrqInsurances.size(); i++) {
					FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

					List<FinSchFrqInsurance> schList = new ArrayList<>();
					if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
						schList = schInsMap.get(finSchFrqInsurance.getInsId());
						schInsMap.remove(finSchFrqInsurance.getInsId());
					}
					schList.add(finSchFrqInsurance);
					schInsMap.put(finSchFrqInsurance.getInsId(), schList);

				}

				for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
					FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
					if (schInsMap.containsKey(finInsurance.getInsId())) {
						finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
					}
				}
			}
		}

		//Process Editor Details List
		financeDetail = getFinanceReferenceDetails(financeDetail, userrole, "DDE", "", procEdtEvent, true);

		//Document Details
		List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_TView");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		// Deviations
		if (ImplementationConstants.ALLOW_DEVIATIONS) {
			financeDetail.setFinanceDeviations(getDeviationDetailsService().getFinanceDeviations(finReference));
			financeDetail.setApprovedFinanceDeviations(getDeviationDetailsService().getApprovedFinanceDeviations(
					finReference));
		}

		//Mandate
		if (financeMain.getMandateID() != 0) {
			financeDetail.setMandate(getFinMandateService().getMnadateByID(financeMain.getMandateID()));
		}
		
		// Finance Tax Detail
		financeDetail.setTaxDetail(getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference, "_TView"));

		//Contributor Details
		if (scheduleData.getFinanceType().isAllowRIAInvestment()) {
			financeDetail.setFinContributorHeader(getFinContributorHeaderDAO().getFinContributorHeaderById(
					finReference, "_TView"));
			if (financeDetail.getFinContributorHeader() != null) {
				financeDetail.getFinContributorHeader().setContributorDetailList(
						getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_TView"));
			}
		}

		//Contract Asset Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ISTISNA, financeDetail.getFinScheduleData().getFinanceType()
				.getProductCategory())) {
			getContractorAssetDetailService().setContractorAssetDetails(financeDetail, "_TView");
		}

		// Multiple Party Disbursement Details
		financeDetail.setAdvancePaymentsList(getFinAdvancePaymentsService().getFinAdvancePaymentsById(finReference,
				"_View"));

		//Etihad Credit Bureau Details
		if (ImplementationConstants.ALLOW_CREDITBUREAU) {
			financeDetail.setEtihadCreditBureauDetail(getEtihadCreditBureauDetailService()
					.getEtihadCreditBureauDetailById(finReference, "_TView"));
		}

		//Bundled Products Details
		if (ImplementationConstants.ALLOW_BUNDLEDPRODUCT) {
			financeDetail.setBundledProductsDetail(getBundledProductsDetailService().getBundledProductsDetailById(
					finReference, "_TView"));
		}

		// Ijarah Related Changes
		if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)
				&& (StringUtils.equals(scheduleData.getFinanceType().getFinCategory(), FinanceConstants.PRODUCT_IJARAH) || StringUtils
						.equals(scheduleData.getFinanceType().getFinCategory(), FinanceConstants.PRODUCT_FWIJARAH))) {

			// Agreement Field Details
			financeDetail.setAgreementFieldDetails(getAgreementFieldsDetailService().getAgreementFieldDetailsById(
					finReference, "_TView"));

			// Asset Evaluation Details
			financeDetail.setFinAssetEvaluation(getFinAssetEvaluationService().getFinAssetEvaluationById(finReference,
					"_TView"));
		}

		// Covenant Type Details
		if (ImplementationConstants.ALLOW_COVENANT_TYPES) {
			financeDetail
					.setCovenantTypeList(getFinCovenantTypeService().getFinCovenantTypeById(finReference, "_View",false));
		}

		// Asset Type Details
		financeDetail.setFinAssetTypesList(getFinAssetTypeDAO().getFinAssetTypesByFinRef(finReference, "_TView"));

		// Extended Field Details for Assets
		financeDetail.setExtendedFieldRenderList(getExtendedAssetDetails(finReference,
				financeDetail.getFinAssetTypesList()));

		// VAS Recording Details
		if (ImplementationConstants.ALLOW_VAS) {
			financeDetail.getFinScheduleData().setVasRecordingList(getVasRecordings(finReference, "_TView"));
		}

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(
					finReference, FinanceConstants.MODULE_NAME, "_TView"));
		} else {
			financeDetail.setFinanceCollaterals(getFinCollateralService()
					.getFinCollateralsByRef(finReference, "_TView"));
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for Fetching Extended Field Details for the Asset Types
	 * 
	 * @param finReference
	 * @param assetTypes
	 * @return
	 */
	private List<ExtendedFieldRender> getExtendedAssetDetails(String finReference, List<FinAssetTypes> assetTypes) {

		List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
		if (assetTypes == null || assetTypes.isEmpty()) {
			return extendedFieldRenderList;
		}

		List<String> renderedAssetTypes = new ArrayList<>();

		// Extended FieldDetails
		for (FinAssetTypes assetType : assetTypes) {

			// If Duplicate asset types with multiple record , to avoid multiple DB callings
			if (renderedAssetTypes.contains(assetType.getAssetType())) {
				continue;
			}

			StringBuilder tableName = new StringBuilder();
			tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
			tableName.append("_");
			tableName.append(assetType.getAssetType());
			tableName.append("_ED");

			renderedAssetTypes.add(assetType.getAssetType());

			List<Map<String, Object>> renderMapList = getExtendedFieldRenderDAO().getExtendedFieldMap(finReference,
					tableName.toString(), "_View");
			for (int i = 0; i < renderMapList.size(); i++) {

				ExtendedFieldRender aExetendedFieldRender = new ExtendedFieldRender();
				aExetendedFieldRender.setTypeCode(assetType.getAssetType());
				aExetendedFieldRender.setTypeCodeDesc(assetType.getAssetType());

				Map<String, Object> extFieldMap = renderMapList.get(i);

				aExetendedFieldRender.setReference((String) extFieldMap.get("Reference"));
				extFieldMap.remove("Reference");
				aExetendedFieldRender.setSeqNo(Integer.valueOf(extFieldMap.get("SeqNo").toString()));
				extFieldMap.remove("SeqNo");
				aExetendedFieldRender.setVersion(Integer.valueOf(extFieldMap.get("Version").toString()));
				extFieldMap.remove("Version");
				aExetendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
				extFieldMap.remove("LastMntOn");
				aExetendedFieldRender.setLastMntBy(Long.valueOf(extFieldMap.get("LastMntBy").toString()));
				extFieldMap.remove("LastMntBy");
				aExetendedFieldRender.setRecordStatus(StringUtils.equals(
						String.valueOf(extFieldMap.get("RecordStatus")), "null") ? "" : String.valueOf(extFieldMap
						.get("RecordStatus")));
				extFieldMap.remove("RecordStatus");
				aExetendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")),
						"null") ? "" : String.valueOf(extFieldMap.get("RoleCode")));
				extFieldMap.remove("RoleCode");
				aExetendedFieldRender.setNextRoleCode(StringUtils.equals(
						String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? "" : String.valueOf(extFieldMap
						.get("NextRoleCode")));
				extFieldMap.remove("NextRoleCode");
				aExetendedFieldRender
						.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? "" : String
								.valueOf(extFieldMap.get("TaskId")));
				extFieldMap.remove("TaskId");
				aExetendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")),
						"null") ? "" : String.valueOf(extFieldMap.get("NextTaskId")));
				extFieldMap.remove("NextTaskId");
				aExetendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")),
						"null") ? "" : String.valueOf(extFieldMap.get("RecordType")));
				extFieldMap.remove("RecordType");
				aExetendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
				extFieldMap.remove("WorkflowId");

				aExetendedFieldRender.setMapValues(extFieldMap);
				extendedFieldRenderList.add(aExetendedFieldRender);
			}
		}
		return extendedFieldRenderList;
	}

	/**
	 * Method for Fetching Extended Field Details for the Asset Types
	 * 
	 * @param finReference
	 * @param assetTypes
	 * @return
	 */
	private List<VASRecording> getVasRecordings(String finReference, String tableType) {

		List<VASRecording> vasRecordingsList = new ArrayList<VASRecording>();
		vasRecordingsList = getVasRecordingDAO().getVASRecordingsByLinkRef(finReference, tableType);
		if (vasRecordingsList == null || vasRecordingsList.isEmpty()) {
			return vasRecordingsList;
		}

		List<String> renderedVasProducts = new ArrayList<>();

		// Extended FieldDetails
		for (VASRecording recording : vasRecordingsList) {

			// If Duplicate asset types with multiple record , to avoid multiple DB callings
			if (renderedVasProducts.contains(recording.getProductCode())) {
				continue;
			}

			StringBuilder tableName = new StringBuilder();
			tableName.append(VASConsatnts.MODULE_NAME);
			tableName.append("_");
			tableName.append(recording.getProductCode());
			tableName.append("_ED");

			renderedVasProducts.add(recording.getProductCode());

			Map<String, Object> extFieldMap = getExtendedFieldRenderDAO().getExtendedField(recording.getVasReference(),
					tableName.toString(), "_View");

			ExtendedFieldRender aExetendedFieldRender = new ExtendedFieldRender();
			aExetendedFieldRender.setTypeCode(recording.getProductCode());
			aExetendedFieldRender.setTypeCodeDesc(recording.getProductDesc());

			aExetendedFieldRender.setReference((String) extFieldMap.get("Reference"));
			extFieldMap.remove("Reference");
			aExetendedFieldRender.setSeqNo(Integer.valueOf(extFieldMap.get("SeqNo").toString()));
			extFieldMap.remove("SeqNo");
			aExetendedFieldRender.setVersion(Integer.valueOf(extFieldMap.get("Version").toString()));
			extFieldMap.remove("Version");
			aExetendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			aExetendedFieldRender.setLastMntBy(Long.valueOf(extFieldMap.get("LastMntBy").toString()));
			extFieldMap.remove("LastMntBy");
			aExetendedFieldRender.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")),
					"null") ? "" : String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			aExetendedFieldRender
					.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? "" : String
							.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			aExetendedFieldRender.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")),
					"null") ? "" : String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			aExetendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			aExetendedFieldRender.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")),
					"null") ? "" : String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			aExetendedFieldRender.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")),
					"null") ? "" : String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			aExetendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
			extFieldMap.remove("WorkflowId");

			aExetendedFieldRender.setMapValues(extFieldMap);
			recording.setExtendedFieldRender(aExetendedFieldRender);
		}
		return vasRecordingsList;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailsServicing method.
	 * 
	 * @param finReference
	 *            (String)
	 * @param eventCodeRef
	 *            (String)
	 * @param procEdtEvent
	 *            (String)
	 * @param userrole
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getServicingFinance(String finReference, String eventCodeRef, String procEdtEvent,
			String userrole) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", false);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		financeDetail.getFinScheduleData().setFinServiceInstructions(
				getFinServiceInstructionDAO().getFinServiceInstructions(finReference, "_Temp", procEdtEvent));

		//Rolledover Finance Details
		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ROLLOVER)) {
			RolledoverFinanceHeader header = getRolledoverFinanceDAO()
					.getRolledoverFinanceHeader(finReference, "_Temp");
			if (header != null) {
				header.setRolledoverFinanceDetails(getRolledoverFinanceDAO().getRolledoverDetailList(finReference,
						"_View"));
			}
			financeDetail.setRolledoverFinanceHeader(header);
		}

		// Plan EMI Holiday Details
		if (financeMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData.setPlanEMIHmonths(getFinPlanEmiHolidayDAO().getPlanEMIHMonthsByRef(finReference, ""));
			} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				scheduleData.setPlanEMIHDates(getFinPlanEmiHolidayDAO().getPlanEMIHDatesByRef(finReference, ""));
			}
		}

		//Finance Customer Details			
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
					financeMain.getCustID(), true, "_View"));
		}

		//Finance Reference Details List
		financeDetail = getFinanceReferenceDetails(financeDetail, userrole, "DDE", eventCodeRef, procEdtEvent, true);

		//Finance Document Details
		List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)) {
			//Finance Guaranteer Details			
			financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_View"));
			//Finance Joint Account Details
			financeDetail.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference,
					"_View"));
		}

		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ADDDISB)
				|| StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_CANCELDISB)) {
			// Advance Payment Details
			financeDetail.setAdvancePaymentsList(getFinAdvancePaymentsService().getFinAdvancePaymentsById(finReference,
					"_View"));
		}

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(financeMain.getPromotionCode(),
					eventCodeRef, "_AView", false, FinanceConstants.MODULEID_PROMOTION));
		} else {
			financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(financeMain.getFinType(),
					eventCodeRef, "_AView", false, FinanceConstants.MODULEID_FINTYPE));
		}

		// Finance Fee Details
		scheduleData.getFinFeeDetailList().addAll((getFinFeeDetailService().getFinFeeDetailById(finReference, false, "_TView", eventCodeRef)));

		/*// Finance Receipt Details
		scheduleData.setFinReceiptDetails(getFinFeeDetailService().getFinReceiptDetais(finReference));
		List<Long> feeIds = new ArrayList<Long>();
		for (FinFeeDetail finFeeDetail : scheduleData.getFinFeeDetailList()) {
			feeIds.add(finFeeDetail.getFeeID());
		}
		
		if (!feeIds.isEmpty()) {
			scheduleData.setFinFeeReceipts(getFinFeeDetailService().getFinFeeReceiptsById(feeIds, "_TView"));
		}*/

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(
					finReference, FinanceConstants.MODULE_NAME, "_View"));
		} else {
			financeDetail.setFinanceCollaterals(getFinCollateralService()
					.getFinCollateralsByRef(finReference, "_TView"));
		}

		logger.debug("Leaving");

		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailsWIF method.
	 * 
	 * @param finReference
	 *            (String)
	 * @param reqCustDetail
	 *            (String)
	 * @param procEdtEvent
	 *            (boolean)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getWIFFinance(String finReference, boolean reqCustDetail, String procEdtEvent) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", true);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Fee Details
		scheduleData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, true, "_View"));

		if (reqCustDetail && scheduleData.getFinanceMain() != null) {

			if (StringUtils
					.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {

				long custId = scheduleData.getFinanceMain().getCustID();
				if (custId != 0) {
					financeDetail.setCustomer(getCustomerDAO().getWIFCustomerByID(custId, null, "_AView"));

					if (financeDetail.getCustomer() == null) {
						Customer customer = getCustomerDAO().getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						financeDetail.setCustomer(wifcustomer);
					}
				} else {
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					PFSParameter parameter = SysParamUtil.getSystemParameterObject(PennantConstants.LOCAL_CCY);
					wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());
					parameter = SysParamUtil.getSystemParameterObject("APP_DFT_NATION");
					wifcustomer.setCustNationality(parameter.getSysParmValue().trim());
					wifcustomer.setLovDescCustNationalityName(parameter.getSysParmDescription());
					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					financeDetail.setCustomer(wifcustomer);
				}

				financeDetail.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = scheduleData.getFinanceType().getFinType();

				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(finReference,
						financeMain.getFinCcy(), financeMain.getFinAmount(), financeMain.isNewRecord(), finType, null,
						procEdtEvent));

				financeDetail = getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, null,
						PennantConstants.PFF_CUSTCTG_INDIV, procEdtEvent);
			} else {
				IndicativeTermDetail termDetail = getIndicativeTermDetailDAO().getIndicateTermByRef(finReference,
						"_View", true);
				if (termDetail == null) {
					termDetail = new IndicativeTermDetail();
					termDetail.setCustId(financeMain.getCustID());
					termDetail.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
					termDetail.setNewRecord(true);
					termDetail.setWorkflowId(0);
				}
				financeDetail.setIndicativeTermDetail(termDetail);
			}

		} else if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
				FinanceConstants.FIN_DIVISION_RETAIL)) {

			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference, "_View");
			if (propCustomer != null) {
				financeMain.setCustID(propCustomer.getCustId());
				financeMain.setLovDescCustCIF(propCustomer.getCustCIF());
				financeMain.setLovDescCustShrtName(propCustomer.getCustShrtName());
				financeMain.setLovDescCustCtgCode(propCustomer.getCustCtgCode());
				financeMain.setFinBranch(propCustomer.getCustDftBranch());
			} else {
				financeMain.setCustID(0);
				financeMain.setLovDescCustCIF("");
				financeMain.setLovDescCustShrtName("");
				financeMain.setLovDescCustCtgCode("");
				financeMain.setFinBranch("");
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getFinanceDetailById(String finReference, boolean isWIF, String eventCodeRef,
			boolean reqCustDetail, String procEdtEvent, String userRole) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", isWIF);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Service Instructions
		financeDetail.getFinScheduleData().setFinServiceInstructions(
				getFinServiceInstructionDAO().getFinServiceInstructions(finReference, "_Temp", procEdtEvent));

		//Rolledover Finance Details
		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ROLLOVER)) {
			RolledoverFinanceHeader header = getRolledoverFinanceDAO()
					.getRolledoverFinanceHeader(finReference, "_Temp");

			if (header != null) {
				header.setRolledoverFinanceDetails(getRolledoverFinanceDAO().getRolledoverDetailList(finReference,
						"_View"));
			}
			financeDetail.setRolledoverFinanceHeader(header);
		}

		if (isWIF && reqCustDetail && scheduleData.getFinanceMain() != null) {

			if (StringUtils
					.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {

				long custId = scheduleData.getFinanceMain().getCustID();
				if (custId != 0) {
					financeDetail.setCustomer(getCustomerDAO().getWIFCustomerByID(custId, null, "_AView"));
					if (financeDetail.getCustomer() == null) {
						Customer customer = getCustomerDAO().getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						financeDetail.setCustomer(wifcustomer);
					}
				} else {
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					PFSParameter parameter = SysParamUtil.getSystemParameterObject(PennantConstants.LOCAL_CCY);
					wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());
					parameter = SysParamUtil.getSystemParameterObject("APP_DFT_NATION");
					wifcustomer.setCustNationality(parameter.getSysParmValue().trim());
					wifcustomer.setLovDescCustNationalityName(parameter.getSysParmDescription());
					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					financeDetail.setCustomer(wifcustomer);
				}
				financeDetail.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = scheduleData.getFinanceType().getFinType();

				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(finReference,
						financeMain.getFinCcy(), financeMain.getFinAmount(), financeMain.isNewRecord(), finType, null,
						procEdtEvent));

				financeDetail = getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, null,
						PennantConstants.PFF_CUSTCTG_INDIV, procEdtEvent);
			} else {
				IndicativeTermDetail termDetail = getIndicativeTermDetailDAO().getIndicateTermByRef(finReference,
						"_View", true);
				if (termDetail == null) {
					termDetail = new IndicativeTermDetail();
					termDetail.setCustId(financeMain.getCustID());
					termDetail.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
					termDetail.setNewRecord(true);
					termDetail.setWorkflowId(0);
				}
				financeDetail.setIndicativeTermDetail(termDetail);
			}

		} else if (isWIF
				&& StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
						FinanceConstants.FIN_DIVISION_RETAIL)) {
			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference, "_View");
			if (propCustomer != null) {
				financeMain.setCustID(propCustomer.getCustId());
				financeMain.setLovDescCustCIF(propCustomer.getCustCIF());
				financeMain.setLovDescCustShrtName(propCustomer.getCustShrtName());
				financeMain.setLovDescCustCtgCode(propCustomer.getCustCtgCode());
				financeMain.setFinBranch(propCustomer.getCustDftBranch());
			} else {
				financeMain.setCustID(0);
				financeMain.setLovDescCustCIF("");
				financeMain.setLovDescCustShrtName("");
				financeMain.setLovDescCustCtgCode("");
				financeMain.setFinBranch("");
			}
		}

		if (!isWIF) {

			//Finance Customer Details			
			if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(getCustomerDetailsService().getCustomerDetailsById(
						financeMain.getCustID(), true, "_View"));
			}

			// Deviation Details
			if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ORG)) {
				financeDetail.setFinanceDeviations(getDeviationDetailsService().getFinanceDeviations(finReference));
				financeDetail.setApprovedFinanceDeviations(getDeviationDetailsService().getApprovedFinanceDeviations(
						finReference));
			}

			// Mandate Details
			financeDetail.setMandate(getFinMandateService().getMnadateByID(financeMain.getMandateID()));
		}

		if (!isWIF && financeMain != null) {

			//Finance Reference Details List
			financeDetail = getFinanceReferenceDetails(financeDetail, userRole, "DDE", eventCodeRef, procEdtEvent, true);

			//Finance Document Details
			List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
					FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
			if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
				financeDetail.getDocumentDetailsList().addAll(documentList);
			} else {
				financeDetail.setDocumentDetailsList(documentList);
			}

			if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ORG)
					|| StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)) {
				//Finance Guaranteer Details			
				financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference,
						"_View"));

				//Finance Joint Account Details
				financeDetail.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(
						finReference, "_View"));
			}

			if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ORG)) {
				//Finance Contributor Details
				if (scheduleData.getFinanceType().isAllowRIAInvestment()) {
					financeDetail.setFinContributorHeader(getFinContributorHeaderDAO().getFinContributorHeaderById(
							finReference, "_View"));
					if (financeDetail.getFinContributorHeader() != null) {
						financeDetail.getFinContributorHeader().setContributorDetailList(
								getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_View"));
					}
				}
				// finance Contract Asset Details
				if (FinanceConstants.PRODUCT_ISTISNA.equals(financeDetail.getFinScheduleData().getFinanceType()
						.getFinCategory())) {
					getContractorAssetDetailService().setContractorAssetDetails(financeDetail, "_View");
				} else {
					financeDetail.setContractorAssetDetails(null);
				}

				// Etihad Credit Bureau Details
				financeDetail.setEtihadCreditBureauDetail(getEtihadCreditBureauDetailService()
						.getEtihadCreditBureauDetailById(finReference, "_View"));

				// Bundled Products Details
				if (scheduleData.getFinanceType() != null
						&& StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
								FinanceConstants.FIN_DIVISION_RETAIL)) {
					financeDetail.setBundledProductsDetail(getBundledProductsDetailService()
							.getBundledProductsDetailById(finReference, "_View"));
				}

				// Agreement Field Details
				if (scheduleData.getFinanceType() != null
						&& StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
								FinanceConstants.FIN_DIVISION_RETAIL)
						&& (StringUtils.equals(scheduleData.getFinanceType().getFinCategory(),
								FinanceConstants.PRODUCT_IJARAH) || StringUtils.equals(scheduleData.getFinanceType()
								.getFinCategory(), FinanceConstants.PRODUCT_FWIJARAH))) {
					financeDetail.setAgreementFieldDetails(getAgreementFieldsDetailService()
							.getAgreementFieldDetailsById(finReference, "_View"));
				}

				// Advance Payment Details
				financeDetail.setAdvancePaymentsList(getFinAdvancePaymentsService().getFinAdvancePaymentsById(
						finReference, "_View"));

				// Covenant Type Details
				financeDetail.setCovenantTypeList(getFinCovenantTypeService().getFinCovenantTypeById(finReference,
						"_View",false));

				// Asset Evaluation Details
				financeDetail.setFinAssetEvaluation(getFinAssetEvaluationService().getFinAssetEvaluationById(
						finReference, "_View"));

				//FinAssetType
				financeDetail
						.setFinAssetTypesList(getFinAssetTypeDAO().getFinAssetTypesByFinRef(finReference, "_Temp"));

				// Extended Field Details for Assets
				financeDetail.setExtendedFieldRenderList(getExtendedAssetDetails(finReference,
						financeDetail.getFinAssetTypesList()));

				// Collateral Details
				if (ImplementationConstants.COLLATERAL_INTERNAL) {
					financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO()
							.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_View"));
				} else {
					financeDetail.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference,
							"_View"));
				}
			}
		}

		// Finance Fee Details
		financeDetail.getFinScheduleData().setFinFeeDetailList(
				getFinFeeDetailService().getFinFeeDetailById(finReference, isWIF, "_View"));

		logger.debug("Leaving");

		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getWIFFinanceDetailById(String finReference, String procEdtEvent) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", true);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Service Instructions
		financeDetail.getFinScheduleData().setFinServiceInstructions(
				getFinServiceInstructionDAO().getFinServiceInstructions(finReference, "_Temp", procEdtEvent));

		// Finance Fee Details
		financeDetail.getFinScheduleData().setFinFeeDetailList(
				getFinFeeDetailService().getFinFeeDetailById(finReference, true, "_View"));

		if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference, "_View");
			if (propCustomer != null) {
				financeMain.setCustID(propCustomer.getCustId());
				financeMain.setLovDescCustCIF(propCustomer.getCustCIF());
				financeMain.setLovDescCustShrtName(propCustomer.getCustShrtName());
				financeMain.setLovDescCustCtgCode(propCustomer.getCustCtgCode());
				financeMain.setFinBranch(propCustomer.getCustDftBranch());
			} else {
				financeMain.setCustID(0);
				financeMain.setLovDescCustCIF("");
				financeMain.setLovDescCustShrtName("");
				financeMain.setLovDescCustCtgCode("");
				financeMain.setFinBranch("");
			}
		}

		logger.debug("Leaving");
		return financeDetail;
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, String type) {
		return getFinanceMainDAO().getFinanceMainById(finReference, type, false);
	}

	/**
	 * Method for Fetching List of Fee Charge Details depends on Event Code
	 * 
	 * @param finType
	 * @param startDate
	 * @param isWIF
	 * @return
	 */
	@Override
	public List<Rule> getFeeRuleDetails(FinanceType finType, Date startDate, boolean isWIF) {
		logger.debug("Entering");

		//Finance Accounting Fee Charge Details
		String eventCode = PennantApplicationUtil.getEventCode(startDate);
		Long accSetId = getFinTypeAccountingDAO().getAccountSetID(finType.getFinType(), eventCode,
				FinanceConstants.MODULEID_FINTYPE);

		//Fetch Stage Accounting AccountingSetId List 
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(finType.getFinType(),
				FinanceConstants.FINSER_EVENT_ORG, null, "_ACView"));
		if (accSetId != Long.MIN_VALUE) {
			accSetIdList.add(Long.valueOf(accSetId));
		}

		//Finance Fee Charge Details
		List<Rule> feeChargeList = new ArrayList<Rule>();
		if (!accSetIdList.isEmpty()) {
			feeChargeList = getTransactionEntryDAO().getListFeeChargeRules(
					accSetIdList,
					eventCode.startsWith(AccountEventConstants.ACCEVENT_ADDDBS) ? AccountEventConstants.ACCEVENT_ADDDBS
							: eventCode, "", 0);
		}

		logger.debug("Leaving");
		return feeChargeList;
	}

	/**
	 * Method for Fetch Ins insurance Details Object for Early Settlement Refund
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FeeRule getInsFee(String finReference) {
		return getFinFeeChargesDAO().getInsFee(finReference, "");
	}

	/**
	 * Method for Fetching List of Fee Rules From Approved Finance
	 */
	@Override
	public List<FeeRule> getApprovedFeeRules(String finReference, String finEvent, boolean isWIF) {
		logger.debug("Entering");
		List<FeeRule> feeRuleList = getFinFeeChargesDAO().getFeeChargesByFinRef(finReference, finEvent, isWIF, "");
		logger.debug("Leaving");
		return feeRuleList;
	}

	@Override
	public List<ContractorAssetDetail> getContractorAssetDetailList(String finReference) {
		logger.debug("Entering");
		List<ContractorAssetDetail> assetDetails = getContractorAssetDetailService().getContractorAssetDetailList(
				finReference, "_AView");
		logger.debug("Leaving");
		return assetDetails;
	}

	/**
	 * getApprovedFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getApprovedFinanceDetailById(String finReference, boolean isWIF) {
		return getFinSchdDetailById(finReference, "_AView", isWIF);

	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 * */
	@Override
	public FinanceDetail getFinanceOrgDetails(FinanceMain financeMain, String type) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);
		String finReference = scheduleData.getFinanceMain().getFinReference();

		//Finance Type Details
		FinanceType financeType = getFinanceTypeDAO().getOrgFinanceTypeByID(scheduleData.getFinanceMain().getFinType(),
				"_ORGView");
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			// Fetching Promotion Details
			Promotion promotion = this.promotionDAO.getPromotionById(financeMain.getPromotionCode(), "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}
		scheduleData.setFinanceType(financeType);

		//Step Policy Details List
		if (scheduleData.getFinanceMain().isStepFinance()) {
			scheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference,
					type, false));
		}

		//Overdraft Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(
					finReference, "_Temp", false));
		}

		//Finance Flag Details
		financeDetail.setFinFlagsDetails(getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference,
				FinanceConstants.MODULE_NAME, "_Temp"));

		//Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));

		//Finance Disbursement Details
		scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
				type, false));

		//Finance Repayments Instruction Details
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		String tableType = "";
		if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {
			tableType = "_Temp";
		}

		scheduleData.setFinServiceInstructions(getFinServiceInstructionDAO().getFinServiceInstructions(finReference,
				tableType, financeDetail.getModuleDefiner()));

		//Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		//Fetch Secondary account Details
		if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
			scheduleData.getFinanceMain().setSecondaryAccount(
					getSecondaryAccountDAO().getSecondaryAccountsByFinRef(
							scheduleData.getFinanceMain().getFinReference(), type));
		}

		// Plan EMI Holiday Details
		int sdSize = scheduleData.getFinanceScheduleDetails().size();
		if (financeMain.isPlanEMIHAlw() && sdSize > 0) {
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData.setPlanEMIHmonths(getFinPlanEmiHolidayDAO()
						.getPlanEMIHMonthsByRef(finReference, tableType));
			} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				scheduleData.setPlanEMIHDates(getFinPlanEmiHolidayDAO().getPlanEMIHDatesByRef(finReference, tableType));
			}
		}

		financeDetail.setFinScheduleData(scheduleData);
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 * */
	@Override
	public FinanceDetail getFinSchdDetailById(String finReference, String type, boolean isWIF) {
		logger.debug("Entering");

		//Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, isWIF));

		if (scheduleData.getFinanceMain() != null) {

			//Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getOrgFinanceTypeByID(
					scheduleData.getFinanceMain().getFinType(), "_ORGView");
			if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO.getPromotionById(scheduleData.getFinanceMain()
						.getPromotionCode(), "_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			scheduleData.setFinanceType(financeType);

			//Step Policy Details List
			if (scheduleData.getFinanceMain().isStepFinance()) {
				scheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference,
						isWIF ? "_View" : "_TView", isWIF));
			}

			//Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,
					type, isWIF));

			//Finance Disbursement Details
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
					isWIF ? "_View" : type, isWIF));

			//Finance Repayments Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF));
			
 			// Fee Details
			List<FinFeeDetail> finOriginationFeeList = getFinFeeDetailDAO().getFinScheduleFees(finReference, false, "_View");
			scheduleData.setFinFeeDetailList(finOriginationFeeList);
			
			// Finance Fee Schedule Details
			if (finOriginationFeeList != null && !finOriginationFeeList.isEmpty()) {
				List<Long> feeIDList = new ArrayList<>();
				for (FinFeeDetail feeDetail : finOriginationFeeList) {
					feeIDList.add(feeDetail.getFeeID());
					feeDetail.setRcdVisible(false);
				}

				if (!feeIDList.isEmpty()) {
					List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(
							feeIDList, false, "");

					if (feeScheduleList != null && !feeScheduleList.isEmpty()) {
						HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
						for (int i = 0; i < feeScheduleList.size(); i++) {
							FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

							List<FinFeeScheduleDetail> schList = new ArrayList<>();
							if (schFeeMap.containsKey(schdFee.getFeeID())) {
								schList = schFeeMap.get(schdFee.getFeeID());
								schFeeMap.remove(schdFee.getFeeID());
							}
							schList.add(schdFee);
							schFeeMap.put(schdFee.getFeeID(), schList);

						}

						for (int i = 0; i < finOriginationFeeList.size(); i++) {
							FinFeeDetail feeDetail = finOriginationFeeList.get(i);
							if (schFeeMap.containsKey(feeDetail.getFeeID())) {
								feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
							}
						}
					}
				}
			}

			//Finance Overdue Penalty Rate Details
			if (!isWIF) {

				//Finance Flag Details
				financeDetail.setFinFlagsDetails(getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference,
						FinanceConstants.MODULE_NAME, type));

			}

			//insurance Details
			scheduleData.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, type, isWIF));

			// FinSchFrqInsurance Details
			if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {
				List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO().getFinSchFrqInsuranceFinRef(
						finReference, isWIF, "_View");
				HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();

				for (int i = 0; i < finSchFrqInsurances.size(); i++) {
					FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

					List<FinSchFrqInsurance> schList = new ArrayList<>();
					if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
						schList = schInsMap.get(finSchFrqInsurance.getInsId());
						schInsMap.remove(finSchFrqInsurance.getInsId());
					}
					schList.add(finSchFrqInsurance);
					schInsMap.put(finSchFrqInsurance.getInsId(), schList);

				}

				for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
					FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
					if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)
							&& schInsMap.containsKey(finInsurance.getInsId())) {
						finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
					}
				}
				schInsMap = null;
			}

			if (!isWIF) {

				String tableType = "";
				if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {
					tableType = "_Temp";
				}

				//Overdue Penalty Rates
				scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference,
						StringUtils.equals(tableType, "") ? type : tableType));

				//Overdraft Schedule Detail
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, scheduleData.getFinanceMain()
						.getProductCategory())) {
					scheduleData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO()
							.getOverdraftScheduleDetails(finReference, tableType, isWIF));
				}

				//Fetch Secondary account Details
				if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
					scheduleData.getFinanceMain().setSecondaryAccount(
							getSecondaryAccountDAO().getSecondaryAccountsByFinRef(
									scheduleData.getFinanceMain().getFinReference(), type));
				}
			}

		}
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String nextRoleCode,
			String screenCode, String eventCode, String procEdtEvent, boolean extFieldsReq) {
		logger.debug("Entering");

		List<Long> accSetIdList = new ArrayList<Long>();
		boolean isCustExist = true;
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String ctgType = "";
		if (financeMain.getCustID() <= 0) {
			isCustExist = false;
		}
		if(financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null){
			ctgType = financeDetail.getCustomerDetails().getCustomer().getCustCtgCode();
		}

		List<FinanceReferenceDetail> aggrementList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> eligibilityList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> retScoringGroupList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> corpScoringGroupList = new ArrayList<FinanceReferenceDetail>(1);
		List<FinanceReferenceDetail> checkListdetails = new ArrayList<FinanceReferenceDetail>(1);

		List<FinanceReferenceDetail> finRefDetails = getFinanceReferenceDetailDAO().getFinanceProcessEditorDetails(
				financeType.getFinType(),
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent, "_FINVIEW");

		if (finRefDetails != null && !finRefDetails.isEmpty()) {
			for (FinanceReferenceDetail finrefDetail : finRefDetails) {
				if ((!finrefDetail.isIsActive()) || StringUtils.isEmpty(finrefDetail.getLovDescRefDesc())) {
					continue;
				}
				if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_CHECKLIST) {
					if (StringUtils.trimToEmpty(finrefDetail.getShowInStage()).contains((nextRoleCode + ","))) {
						checkListdetails.add(finrefDetail);
						continue;
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_AGREEMENT) {
					if (StringUtils.trimToEmpty(finrefDetail.getMandInputInStage()).contains((nextRoleCode + ","))) {
						aggrementList.add(finrefDetail);
						continue;
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_ELIGIBILITY) {
					if (StringUtils.isNotEmpty(finrefDetail.getLovDescRuleReturnType())
							&& StringUtils.trimToEmpty(finrefDetail.getAllowInputInStage()).contains(
									(nextRoleCode + ","))) {
						eligibilityList.add(finrefDetail);
						continue;
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_RTLSCORE) {
					if (StringUtils.trimToEmpty(finrefDetail.getMandInputInStage()).contains((nextRoleCode + ","))) {
						retScoringGroupList.add(finrefDetail);
						continue;
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_CORPSCORE) {
					if (StringUtils.trimToEmpty(finrefDetail.getMandInputInStage()).contains((nextRoleCode + ","))) {
						corpScoringGroupList.add(finrefDetail);
						continue;
					}
				} else if (finrefDetail.getFinRefType() == FinanceConstants.PROCEDT_STAGEACC) {
					accSetIdList.add(finrefDetail.getFinRefId());
					continue;
				}
			}
		}

		//Finance Agreement Details	
		financeDetail.setAggrementList(aggrementList);

		if (isCustExist) {
			if (financeMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {

				//Eligibility Details
				financeDetail.setElgRuleList(getEligibilityDetailService().fetchEligibilityDetails(financeMain,
						eligibilityList));

				//Scoring Details
				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)) {
					getScoringDetailService().fetchFinScoringDetails(financeDetail, retScoringGroupList, ctgType);
				} else {
					getScoringDetailService().fetchFinScoringDetails(financeDetail, corpScoringGroupList, ctgType);
				}
			}

			//Checklist Details
			getCheckListDetailService().fetchFinCheckListDetails(financeDetail, checkListdetails);

			//Finance Stage Accounting Posting Details 
			//=======================================
			List<TransactionEntry> stageEntries = new ArrayList<>();
			for (int i = 0; i < accSetIdList.size(); i++) {
				stageEntries.addAll(AccountingConfigCache.getTransactionEntry(accSetIdList.get(i)));
			}
			financeDetail.setStageTransactionEntries(stageEntries);
		}

		// Accounting Set Details
		if (StringUtils.isBlank(eventCode)) {

			if (StringUtils.equalsIgnoreCase(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
				eventCode = AccountEventConstants.ACCEVENT_CMTDISB;
			} else {
				eventCode = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
			}
		}

		//Finance Commitment Accounting Posting Details
		//=======================================
		if (PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			if (financeType.isFinCommitmentReq() && StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {

				long accountingSetId = getAccountingSetDAO().getAccountingSetId(
						AccountEventConstants.ACCEVENT_CMTDISB, AccountEventConstants.ACCEVENT_CMTDISB);//TODO : ACCOUNTINGSET

				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(AccountingConfigCache.getTransactionEntry(accountingSetId));
				}
			}
		}

		//TODO: Need to Modify Based on Product Level for Finance Origination Extended fields
		if (financeMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {

			// Fetching Finance Asset Additional Fields
			//=======================================
			if (!"QDE".equals(screenCode) && extFieldsReq) {

				/*
				 * ExtendedFieldHeader fieldHeader = null; List<ExtendedFieldDetail> extendedFieldDetails = null; String
				 * assetCode = financeMain.getFinPurpose();
				 * 
				 * fieldHeader =
				 * getExtendedFieldHeaderDAO().getExtendedFieldHeaderByModuleName(FinanceConstants.MODULE_NAME,
				 * assetCode, "_AView");
				 * 
				 * if (fieldHeader != null) { extendedFieldDetails =
				 * getExtendedFieldDetailDAO().getExtendedFieldDetailBySubModule(assetCode, "_AView");
				 * fieldHeader.setExtendedFieldDetails(extendedFieldDetails);
				 * financeDetail.setExtendedFieldHeader(fieldHeader);
				 * 
				 * finReference = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinReference());
				 * 
				 * if (extendedFieldDetails != null && !extendedFieldDetails.isEmpty() &&
				 * StringUtils.isNotEmpty(finReference)) { String tableName = ""; if
				 * (PennantStaticListUtil.getModuleName().containsKey(FinanceConstants.MODULE_NAME)) { tableName =
				 * PennantStaticListUtil.getModuleName().get(FinanceConstants.MODULE_NAME).get(assetCode);
				 * HashMap<String, Object> map = (HashMap<String, Object>)
				 * getExtendedFieldDetailDAO().retrive(tableName, finReference, "_Temp"); if (map != null) {
				 * financeDetail.getLovDescExtendedFieldValues().putAll(map); } } } }
				 */
			}
		}
		logger.debug("Leaving");
		return financeDetail;
	}

	/**
	 * Method for testing Finance Reference is Already Exist or not
	 */
	@Override
	public boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF) {
		logger.debug("Entering");
		if (isWIF) {
			tableType = "";
		}
		logger.debug("Leaving");
		return getFinanceMainDAO().isFinReferenceExists(financeReference, tableType, isWIF);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws DatatypeConfigurationException 
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate", isWIF);
		if (!isWIF) {
			aAuditHeader = processLimitSaveOrUpdate(aAuditHeader,true);
		}
		if (!aAuditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return aAuditHeader;
		}
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		
		//process to send FIN-one request and create or update the cust data.
		/*if (!isWIF) {
			processFinOneCheck(aAuditHeader);
		}

		if (aAuditHeader.getAuditDetail().getErrorDetails() != null
				&& !aAuditHeader.getAuditDetail().getErrorDetails().isEmpty()) {
			return aAuditHeader;
		}*/
		/*
		 * Cloner cloner = new Cloner(); AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		 */
		AuditHeader auditHeader = null;
		try {
			auditHeader = aAuditHeader.getNewCopyInstance();
		} catch (Exception e) {
			logger.error("Error Occured {}", e);
		}
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		Date curBDay = DateUtility.getAppDate();

		TableType tableType = TableType.MAIN_TAB;
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		//Accounting (Stage/Posting) Execution Process
		//=======================================
		if (!isWIF) {

			//Finance Stage Accounting Process
			//=======================================
			if (financeDetail.getStageAccountingList() != null && financeDetail.getStageAccountingList().size() > 0) {

				List<ReturnDataSet> list = new ArrayList<ReturnDataSet>();
				auditHeader = executeStageAccounting(auditHeader, list);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
				list = null;
			}

			//Accounting Execution Process on Maintenance
			//=======================================
			if (tableType == TableType.MAIN_TAB && financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {

				auditHeader = executeAccountingProcess(auditHeader, curBDay);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
			}
		}

		if (isWIF) {

			//Customer Basic Details Maintenance
			//=======================================
			WIFCustomer customer = financeDetail.getCustomer();
			if (customer != null) {
				long custId = customer.getCustID();
				if (customer.isNewRecord()) {
					custId = getCustomerDAO().saveWIFCustomer(customer);
				} else {
					getCustomerDAO().updateWIFCustomer(customer);
					getCustomerIncomeDAO().deleteByCustomer(customer.getCustID(), "", true);
				}

				if (customer.getCustomerIncomeList() != null && !customer.getCustomerIncomeList().isEmpty()) {
					for (CustomerIncome income : customer.getCustomerIncomeList()) {
						income.setCustID(custId);
					}
					getCustomerIncomeDAO().saveBatch(customer.getCustomerIncomeList(), "", true);
				}
				financeMain.setCustID(custId);
			}

			//Indicative Term Sheet Details Maintenance
			//=======================================
			IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
			if (termDetail != null) {
				termDetail.setFinReference(finReference);
				if (termDetail.isNewRecord()) {
					getIndicativeTermDetailDAO().save(termDetail, tableType.getSuffix(), true);
				} else {
					getIndicativeTermDetailDAO().update(termDetail, tableType.getSuffix(), true);
				}
			}
		} else {
			// set Customer Details Audit
			if (financeDetail.getCustomerDetails() != null && StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
				auditDetails.addAll(getCustomerDetailsService().saveOrUpdate(financeDetail, ""));
			}
		}

		// Finance Main Details Save And Update
		//=======================================
		// Update Task_log and Task_Owners tables
		if (!isWIF) {
			updateTaskLog(financeMain, true);
		}

		//Mandate Should be processed first for changes since the mandate id will be updated in the finance main.
		getFinMandateService().saveOrUpdate(financeDetail, auditHeader, tableType.getSuffix());

		if (financeMain.isNew()) {
			getFinanceMainDAO().save(financeMain, tableType, isWIF);

			//Save Finance Premium Details
			String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
			if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
				//FIXME : DataSet Removal to be worked on if it requires in future
			}

			//Overdraft Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& !financeDetail.getFinScheduleData().getOverdraftScheduleDetails().isEmpty()) {

				for (int i = 0; i < financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size(); i++) {
					financeDetail.getFinScheduleData().getOverdraftScheduleDetails().get(i)
							.setFinReference(finReference);
				}
				getOverdraftScheduleDetailDAO().saveList(
						financeDetail.getFinScheduleData().getOverdraftScheduleDetails(), tableType.getSuffix());
			}

			// Save Rolledover Finance Details
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().saveHeader(financeDetail.getRolledoverFinanceHeader(), tableType.getSuffix());

				// Rolledover Details
				getRolledoverFinanceDAO()
						.saveDetailList(financeDetail.getRolledoverFinanceHeader().getRolledoverFinanceDetails(),
								tableType.getSuffix());
			}
			
		} else {
			getFinanceMainDAO().update(financeMain, tableType, isWIF);

			//Update Finance Premium Details
			String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
			if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
				//FIXME : DataSet Removal to be worked on if it requires in future
			}

			//Overdraft Details
			if (financeMain.isLovDescIsSchdGenerated()
					&& StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& !financeDetail.getFinScheduleData().getOverdraftScheduleDetails().isEmpty()) {

				// Existing Data deletion
				getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "_Temp", isWIF);

				// Save New list of records
				for (int i = 0; i < financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size(); i++) {
					financeDetail.getFinScheduleData().getOverdraftScheduleDetails().get(i)
							.setFinReference(finReference);
				}
				getOverdraftScheduleDetailDAO().saveList(
						financeDetail.getFinScheduleData().getOverdraftScheduleDetails(), tableType.getSuffix());
			}

			// Update Rolled over Finance Details
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().updateHeader(financeDetail.getRolledoverFinanceHeader(),
						tableType.getSuffix());

				// Rolledover Details
				getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), tableType.getSuffix());
				getRolledoverFinanceDAO()
						.saveDetailList(financeDetail.getRolledoverFinanceHeader().getRolledoverFinanceDetails(),
								tableType.getSuffix());
			}
			
		}
		
		// Save or Update FInance Tax Details
		FinanceTaxDetail taxDetail = financeDetail.getTaxDetail();
		if ( taxDetail != null) {
			FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(taxDetail.getFinReference(), "_View");
			if (tempTaxDetail != null) {
				getFinanceTaxDetailDAO().delete(taxDetail, tableType);
			}
			if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(taxDetail.getApplicableFor())
					|| PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(taxDetail.getApplicableFor())
					|| PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(taxDetail.getApplicableFor())) {
				taxDetail.setFinReference(finReference);
				taxDetail.setTaskId(financeMain.getTaskId());
				taxDetail.setNextTaskId(financeMain.getNextTaskId());
				taxDetail.setRoleCode(financeMain.getRoleCode());
				taxDetail.setNextRoleCode(financeMain.getNextRoleCode());
				taxDetail.setRecordStatus(financeMain.getRecordStatus());
				taxDetail.setWorkflowId(financeMain.getWorkflowId());
				getFinanceTaxDetailDAO().save(taxDetail, tableType);
			}
		}

		// Save Contributor Header Details
		//=======================================
		if (financeDetail.getFinContributorHeader() != null) {

			FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
			contributorHeader.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
			if (contributorHeader.isNewRecord()) {
				getFinContributorHeaderDAO().save(contributorHeader, tableType.getSuffix());
			} else {
				getFinContributorHeaderDAO().update(contributorHeader, tableType.getSuffix());
			}

			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeDetail
					.getFinContributorHeader().getBefImage(), financeDetail.getFinContributorHeader()));

			if (contributorHeader.getContributorDetailList() != null
					&& contributorHeader.getContributorDetailList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
				details = processingContributorList(details, tableType.getSuffix(), contributorHeader.getFinReference());
				auditDetails.addAll(details);
			}
		}

		// Save schedule details
		//=======================================
		if (!financeDetail.isNewRecord()) {

			if (!isWIF && tableType == TableType.MAIN_TAB
					&& financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				//Fetch Existing data before Modification

				FinScheduleData oldFinSchdData = null;
				if (financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {
					oldFinSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "", -1);
					oldFinSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				}

				//Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				entryDetail.setEventAction(financeDetail.getAccountingEventCode());
				entryDetail.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				//Save Schedule Details For Future Modifications
				if (financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {
					listSave(oldFinSchdData, "_Log", false, logKey);
				}
			}

			listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), tableType.getSuffix(),
					isWIF);
			getFinServiceInstructionDAO().deleteList(financeDetail.getFinScheduleData().getFinReference(),
					financeDetail.getModuleDefiner(), "_Temp");
			listSave(financeDetail.getFinScheduleData(), tableType.getSuffix(), isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
					tableType.getSuffix());
			//Secondary account details
			saveSecondaryAccountList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
					tableType.getSuffix());

			// Plan EMI Holiday Details Deletion, if exists on Old image
			//=======================================
			FinanceMain befFinMain = financeDetail.getFinScheduleData().getFinanceMain().getBefImage();
			if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(finReference, tableType.getSuffix());
				} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHDates(finReference, tableType.getSuffix());
				}
			}

		} else {
			listSave(financeDetail.getFinScheduleData(), tableType.getSuffix(), isWIF, 0);
			saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
					tableType.getSuffix());

			//Secondary account details
			saveSecondaryAccountList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
					tableType.getSuffix());

		}

		// Plan EMI Holiday Details
		//=======================================
		if (financeMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (financeDetail.getFinScheduleData().getPlanEMIHmonths() != null
						&& !financeDetail.getFinScheduleData().getPlanEMIHmonths().isEmpty()) {

					List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
					for (int i = 0; i < financeDetail.getFinScheduleData().getPlanEMIHmonths().size(); i++) {
						int planEMIHMonth = financeDetail.getFinScheduleData().getPlanEMIHmonths().get(i);
						FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
						emiHoliday.setFinReference(finReference);
						emiHoliday.setPlanEMIHMonth(planEMIHMonth);
						holidayList.add(emiHoliday);
					}

					getFinPlanEmiHolidayDAO().savePlanEMIHMonths(holidayList, tableType.getSuffix());
				}
			} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				if (financeDetail.getFinScheduleData().getPlanEMIHDates() != null
						&& !financeDetail.getFinScheduleData().getPlanEMIHDates().isEmpty()) {

					List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
					for (int i = 0; i < financeDetail.getFinScheduleData().getPlanEMIHDates().size(); i++) {
						Date planEMIHDate = financeDetail.getFinScheduleData().getPlanEMIHDates().get(i);
						FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
						emiHoliday.setFinReference(finReference);
						emiHoliday.setPlanEMIHDate(planEMIHDate);
						holidayList.add(emiHoliday);
					}

					getFinPlanEmiHolidayDAO().savePlanEMIHDates(holidayList, tableType.getSuffix());
				}
			}
		}

		// Save Finance Step Policy Details
		//=======================================
		if (isWIF || financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			getFinanceStepDetailDAO().deleteList(finReference, isWIF, tableType.getSuffix());
			saveStepDetailList(financeDetail.getFinScheduleData(), isWIF, tableType.getSuffix());
		}

		if (!isWIF) {

			//Finance Eligibility Rule Details
			//=======================================
			auditDetails.addAll(getEligibilityDetailService().saveOrUpdate(financeDetail));

			// Finance Scoring Module Details List Saving 
			//=======================================
			auditDetails.addAll(getScoringDetailService().saveOrUpdate(financeDetail));
		}

		// Save asset details
		//=======================================
		if (!isWIF) {

			String auditTranType = auditHeader.getAuditTranType();

			// Save Document Details
			//=======================================
			if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, tableType.getSuffix(), financeDetail
						.getFinScheduleData().getFinanceMain(), financeDetail.getModuleDefiner());
				auditDetails.addAll(details);
			}

			// set Finance Check List audit details to auditDetails
			//=======================================
			if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().saveOrUpdate(financeDetail, tableType.getSuffix()));
			}

			// set contract Details Audit
			//=======================================
			if (financeDetail.getContractorAssetDetails() != null
					&& !financeDetail.getContractorAssetDetails().isEmpty()) {
				auditDetails.addAll(getContractorAssetDetailService().saveOrUpdate(finReference,
						financeDetail.getContractorAssetDetails(), tableType.getSuffix(), auditTranType));
			}

			// set Guaranteer Details Audit
			//=======================================
			if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
				auditDetails.addAll(getGuarantorDetailService().saveOrUpdate(financeDetail.getGurantorsDetailList(),
						tableType.getSuffix(), auditTranType));
			}

			// set JountAccount Details Audit
			//=======================================
			if (financeDetail.getJountAccountDetailList() != null
					&& !financeDetail.getJountAccountDetailList().isEmpty()) {
				auditDetails.addAll(getJointAccountDetailService().saveOrUpdate(
						financeDetail.getJountAccountDetailList(), tableType.getSuffix(), auditTranType));
			}

			// set Finance Collateral Details Audit
			//=======================================
			if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
				auditDetails.addAll(getFinCollateralService().saveOrUpdate(financeDetail.getFinanceCollaterals(),
						tableType.getSuffix(), auditTranType));
			}

			//Deviation details
			getDeviationDetailsService().processDevaitions(finReference, financeDetail.getFinanceDeviations(),
					auditHeader);

			//Dedup Details
			//=======================================
			auditDetails.addAll(saveDedupDetails(financeDetail));

			//Additional Field Details Save / Update
			//=======================================
			doSaveAddlFieldDetails(financeDetail, tableType.getSuffix());

			// Etihad Credit Bureau Details
			//=======================================
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				auditDetails.add(getEtihadCreditBureauDetailService().saveOrUpdate(
						financeDetail.getEtihadCreditBureauDetail(), tableType.getSuffix(), auditTranType));
			}

			// Bundled Products Details
			//=======================================
			if (financeDetail.getBundledProductsDetail() != null) {
				auditDetails.add(getBundledProductsDetailService().saveOrUpdate(
						financeDetail.getBundledProductsDetail(), tableType.getSuffix(), auditTranType));
			}
			// Agreement Field  Details
			//=======================================
			if (financeDetail.getAgreementFieldDetails() != null) {
				auditDetails.add(getAgreementFieldsDetailService().saveOrUpdate(
						financeDetail.getAgreementFieldDetails(), tableType.getSuffix(), auditTranType));
			}

			// Advance Payment Details
			//=======================================
			//Payment Order Issue Details
			//=======================================
			//Quick disbursement
			if (financeMain.isQuickDisb()) {
				if(!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) ||!financeDetail.isStp()){
					auditDetails.addAll(getFinAdvancePaymentsService().processQuickDisbursment(financeDetail,
							tableType.getSuffix(), auditTranType));	
				} else {
					auditDetails.addAll(getFinAdvancePaymentsService().processAPIQuickDisbursment(financeDetail,
							tableType.getSuffix(), auditTranType));	
				}
				
			} else {
				if (financeDetail.getAdvancePaymentsList() != null && !financeDetail.getAdvancePaymentsList().isEmpty()) {
					auditDetails.addAll(getFinAdvancePaymentsService().saveOrUpdate(
							financeDetail.getAdvancePaymentsList(), tableType.getSuffix(), auditTranType));
				}
			}

			// Covenant Type Details
			//=======================================
			if (financeDetail.getCovenantTypeList() != null && !financeDetail.getCovenantTypeList().isEmpty()) {
				auditDetails.addAll(getFinCovenantTypeService().saveOrUpdate(financeDetail.getCovenantTypeList(),
						tableType.getSuffix(), auditTranType));
			}

			// set Finance Collateral Details Audit
			//=======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, tableType.getSuffix(), financeDetail
						.getFinScheduleData().getFinanceMain());
				auditDetails.addAll(details);
			}
			//FinAssetTypes Audit
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				details = processingFinAssetTypesList(details, tableType.getSuffix(), financeDetail
						.getFinScheduleData().getFinanceMain());
				auditDetails.addAll(details);
			}

			// AssetType Extended field Details
			if (financeDetail.getExtendedFieldRenderList() != null
					&& financeDetail.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
				details = processingExtendedFieldDetailList(details, finReference,
						AssetConstants.EXTENDEDFIELDS_MODULE, tableType.getSuffix());
				auditDetails.addAll(details);
			}

			//Vas Recording Details
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null && !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				details = processingVasRecordngList(details, tableType.getSuffix());
				auditDetails.addAll(details);
			}

			//Vas Recording Extended Field Details
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null && !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
				details = processingExtendedFieldDetailList(details, finReference, VASConsatnts.MODULE_NAME,
						tableType.getSuffix());
				auditDetails.addAll(details);
			}

			// Flag Details
			//=======================================
			if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				details = processingFinFlagDetailList(details, tableType.getSuffix());
				auditDetails.addAll(details);
			}

			// Asset Evaluation Details
			//=======================================
			if (financeDetail.getFinAssetEvaluation() != null) {
				auditDetails.add(getFinAssetEvaluationService().saveOrUpdate(financeDetail.getFinAssetEvaluation(),
						tableType.getSuffix(), auditTranType));
			}

		}

		// Finance Fee Details
		//=======================================
		if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null
				&& !financeDetail.getFinScheduleData().getFinFeeDetailList().isEmpty()) {
			auditDetails.addAll(getFinFeeDetailService().saveOrUpdate(
					financeDetail.getFinScheduleData().getFinFeeDetailActualList(), tableType.getSuffix(),
					auditHeader.getAuditTranType(), isWIF));
		}

		// Finance Fee Receipts
		//=======================================
		if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null
				&& !financeDetail.getFinScheduleData().getFinFeeReceipts().isEmpty()) {
			for(FinFeeReceipt finFeeReceipt : financeDetail.getFinScheduleData().getFinFeeReceipts()) {
				for(FinFeeDetail finFeeDetail : financeDetail.getFinScheduleData().getFinFeeDetailActualList()) {
					if(finFeeReceipt.getFeeTypeId() == finFeeDetail.getFeeTypeID()) {
						finFeeReceipt.setFeeID(finFeeDetail.getFeeID());
						break;
					}
				}
			}
			
			auditDetails.addAll(getFinFeeDetailService().saveOrUpdateFinFeeReceipts(
					financeDetail.getFinScheduleData().getFinFeeReceipts(), tableType.getSuffix(),
					auditHeader.getAuditTranType()));
		}
		
		// set Finance Insurance Details Audit
		//=======================================
		if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
				&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
			details = processFinInsuranceDetails(details, tableType.getSuffix(), financeDetail, isWIF);
			auditDetails.addAll(details);
		}
		
		if (!isWIF) {
			 processLimitSaveOrUpdate(aAuditHeader,false);
		}

		if (!isWIF) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					financeMain.getBefImage(), financeMain));

			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		
		//Reset Finance Detail Object for Service Task Verifications
		//=======================================
		auditHeader.getAuditDetail().setModelData(financeDetail);

		logger.debug("Leaving");
		return auditHeader;
		
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Extended FieldDetails
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingExtendedFieldDetailList(List<AuditDetail> auditDetails, String finReference,
			String moduleName, String type) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetails.get(i).getModelData();

			// Table Name identification
			StringBuilder tableName = new StringBuilder();
			tableName.append(moduleName);
			tableName.append("_");
			tableName.append(extendedFieldRender.getTypeCode());
			tableName.append("_ED");

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				extendedFieldRender.setRoleCode("");
				extendedFieldRender.setNextRoleCode("");
				extendedFieldRender.setTaskId("");
				extendedFieldRender.setNextTaskId("");
			}

			//Table Name addition for Audit
			if (StringUtils.equals(AssetConstants.EXTENDEDFIELDS_MODULE, moduleName)) {
				extendedFieldRender.setReference(finReference);
			}
			extendedFieldRender.setTableName(tableName.toString());
			extendedFieldRender.setWorkflowId(0);

			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (extendedFieldRender.isNewRecord()) {
				saveRecord = true;
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (extendedFieldRender.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = extendedFieldRender.getRecordType();
				recordStatus = extendedFieldRender.getRecordStatus();
				extendedFieldRender.setRecordType("");
				extendedFieldRender.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}

			// Add Common Fields
			HashMap<String, Object> mapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			if (saveRecord || updateRecord) {
				if (saveRecord) {
					mapValues.put("Reference", extendedFieldRender.getReference());
					mapValues.put("SeqNo", extendedFieldRender.getSeqNo());
				}

				mapValues.put("Version", extendedFieldRender.getVersion());
				mapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
				mapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
				mapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
				mapValues.put("RoleCode", extendedFieldRender.getRoleCode());
				mapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
				mapValues.put("TaskId", extendedFieldRender.getTaskId());
				mapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
				mapValues.put("RecordType", extendedFieldRender.getRecordType());
				mapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			}

			if (saveRecord) {
				getExtendedFieldRenderDAO().save(extendedFieldRender.getMapValues(), type, tableName.toString());
			}

			if (updateRecord) {
				getExtendedFieldRenderDAO().update(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						extendedFieldRender.getMapValues(), type, tableName.toString());
			}

			if (deleteRecord) {
				getExtendedFieldRenderDAO().delete(extendedFieldRender.getReference(), extendedFieldRender.getSeqNo(),
						type, tableName.toString());
			}
			if (approveRec) {
				extendedFieldRender.setRecordType(rcdType);
				extendedFieldRender.setRecordStatus(recordStatus);
			}

			// Setting Extended field is to identify record related to Extended fields
			auditDetails.get(i).setExtended(true);
			auditDetails.get(i).setModelData(extendedFieldRender);
		}
		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingFinFlagDetailList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
				finFlagsDetail.setWorkflowId(0);
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getFinFlagDetailsDAO().save(finFlagsDetail, type);
			}

			if (updateRecord) {
				getFinFlagDetailsDAO().update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				getFinFlagDetailsDAO().delete(finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),
						finFlagsDetail.getModuleName(), type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finFlagsDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingVasRecordngList(List<AuditDetail> auditDetails, String type) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			VASRecording recording = (VASRecording) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				recording.setRoleCode("");
				recording.setNextRoleCode("");
				recording.setTaskId("");
				recording.setNextTaskId("");
				recording.setWorkflowId(0);
			}

			if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (recording.isNewRecord()) {
				saveRecord = true;
				if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					recording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					recording.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					recording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (recording.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = recording.getRecordType();
				recordStatus = recording.getRecordStatus();
				recording.setRecordType("");
				recording.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				getVasRecordingDAO().save(recording, type);
			}

			if (updateRecord) {
				getVasRecordingDAO().update(recording, type);
			}

			if (deleteRecord) {
				getVasRecordingDAO().delete(recording, type);
			}

			if (approveRec) {
				recording.setRecordType(rcdType);
				recording.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(recording);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	private List<AuditDetail> saveDedupDetails(FinanceDetail financeDetail) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// Save Black List Customer Data
		//=======================================

		long lastmntby = financeMain.getLastMntBy();
		String roleCode = financeMain.getRoleCode();
		String recordSts = financeMain.getRecordStatus();

		if (financeDetail.getFinBlacklistCustomer() != null && !financeDetail.getFinBlacklistCustomer().isEmpty()) {

			List<FinBlacklistCustomer> insertList = new ArrayList<FinBlacklistCustomer>();
			List<FinBlacklistCustomer> updateList = new ArrayList<FinBlacklistCustomer>();

			FinBlacklistCustomer blCustomer = new FinBlacklistCustomer();
			String[] blFields = PennantJavaUtil.getFieldDetails(blCustomer, blCustomer.getExcludeFields());

			for (int i = 0; i < financeDetail.getFinBlacklistCustomer().size(); i++) {

				blCustomer = financeDetail.getFinBlacklistCustomer().get(i);
				blCustomer.setLastMntBy(lastmntby);
				blCustomer.setRoleCode(roleCode);
				blCustomer.setRecordStatus(recordSts);
				if (!blCustomer.isNewBlacklistRecord()) {
					updateList.add(blCustomer);
				} else {
					insertList.add(blCustomer);
				}

				auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i + 1, blFields[0], blFields[1], null,
						blCustomer));
			}

			if (!insertList.isEmpty()) {
				getBlacklistCustomerDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getBlacklistCustomerDAO().updateList(updateList);
			}

			blFields = null;
			blCustomer = null;
			insertList = null;
			updateList = null;
		}

		// Save Finance DeDup List Data
		//=======================================
		if (financeDetail.getFinDedupDetails() != null && !financeDetail.getFinDedupDetails().isEmpty()) {

			List<FinanceDedup> insertList = new ArrayList<FinanceDedup>();
			List<FinanceDedup> updateList = new ArrayList<FinanceDedup>();

			FinanceDedup dedup = new FinanceDedup();
			String[] dedupFields = PennantJavaUtil.getFieldDetails(dedup, dedup.getExcludeFields());
			dedup = null;
			for (int i = 0; i < financeDetail.getFinDedupDetails().size(); i++) {

				dedup = financeDetail.getFinDedupDetails().get(i);
				dedup.setLastMntBy(lastmntby);
				dedup.setRoleCode(roleCode);
				dedup.setRecordStatus(recordSts);
				if (!dedup.isNewRecord()) {
					updateList.add(dedup);
				} else {
					insertList.add(dedup);
				}

				auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i + 1, dedupFields[0], dedupFields[1], null,
						dedup));
			}
			if (!insertList.isEmpty()) {
				getFinanceDedupeDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getFinanceDedupeDAO().updateList(updateList);
			}

			insertList = null;
			updateList = null;
			dedupFields = null;
			dedup = null;
		}

		// Save Dedup PoliceCase Data
		//=======================================
		if (financeDetail.getDedupPoliceCaseDetails() != null && !financeDetail.getDedupPoliceCaseDetails().isEmpty()) {
			List<PoliceCase> insertList = new ArrayList<PoliceCase>();
			List<PoliceCase> updateList = new ArrayList<PoliceCase>();

			PoliceCase policeCase = new PoliceCase();
			String[] pcFields = PennantJavaUtil.getFieldDetails(policeCase, policeCase.getExcludeFields());
			policeCase = null;

			for (int i = 0; i < financeDetail.getDedupPoliceCaseDetails().size(); i++) {

				policeCase = financeDetail.getDedupPoliceCaseDetails().get(i);
				policeCase.setLastMntBy(lastmntby);
				policeCase.setRoleCode(roleCode);
				policeCase.setRecordStatus(recordSts);

				if (!policeCase.isNewPolicecaseRecord()) {
					updateList.add(policeCase);
				} else {
					insertList.add(policeCase);
				}

				auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i + 1, pcFields[0], pcFields[1], null,
						policeCase));
			}

			if (!insertList.isEmpty()) {
				getPoliceCaseDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getPoliceCaseDAO().updatePoliceCaseList(updateList);
			}

			pcFields = null;
			policeCase = null;
			insertList = null;
			updateList = null;
		}

		// Save Customer Dedup Data
		//=======================================

		if (financeDetail.getCustomerDedupList() != null && !financeDetail.getCustomerDedupList().isEmpty()) {

			List<CustomerDedup> insertList = new ArrayList<CustomerDedup>();
			List<CustomerDedup> updateList = new ArrayList<CustomerDedup>();

			CustomerDedup deDupCustomer = new CustomerDedup();
			String[] blFields = PennantJavaUtil.getFieldDetails(deDupCustomer, deDupCustomer.getExcludeFields());

			for (int i = 0; i < financeDetail.getCustomerDedupList().size(); i++) {

				deDupCustomer = financeDetail.getCustomerDedupList().get(i);
				deDupCustomer.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				deDupCustomer.setLastMntBy(lastmntby);
				deDupCustomer.setRoleCode(roleCode);
				deDupCustomer.setRecordStatus(recordSts);
				if (!deDupCustomer.isNewCustDedupRecord()) {
					updateList.add(deDupCustomer);
				} else {
					insertList.add(deDupCustomer);
				}

				auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i + 1, blFields[0], blFields[1], null,
						deDupCustomer));
			}

			if (!insertList.isEmpty()) {
				getCustomerDedupDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getCustomerDedupDAO().updateList(updateList);
			}

			deDupCustomer = null;
			insertList = null;
			updateList = null;
		}
		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinanceMain by using FinanceMainDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader, boolean isWIF) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceType()
				.getFinCategory());

		//Finance SubChild List And Reference Details List Deletion
		listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "", isWIF);
		getFinServiceInstructionDAO().deleteList(financeDetail.getFinScheduleData().getFinReference(),
				financeDetail.getModuleDefiner(), "");

		if (!isWIF) {
			//Additional Field Details Deletion
			doDeleteAddlFieldDetails(financeDetail, "");
		}

		//Indicative Term Sheet Details deletion on WIF FinanceMain Deletion 
		if (isWIF && financeDetail.getIndicativeTermDetail() != null) {
			getIndicativeTermDetailDAO().delete(financeDetail.getIndicativeTermDetail(), "", true);
		}

		if (!isWIF) {
			auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", auditHeader.getAuditTranType()));
			auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "", auditHeader.getAuditTranType()));
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				auditDetails.add(getEtihadCreditBureauDetailService().delete(
						financeDetail.getEtihadCreditBureauDetail(), "", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getBundledProductsDetail() != null) {
				auditDetails.add(getBundledProductsDetailService().delete(financeDetail.getBundledProductsDetail(), "",
						auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAgreementFieldDetails() != null) {
				auditDetails.add(getAgreementFieldsDetailService().delete(financeDetail.getAgreementFieldDetails(), "",
						auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAdvancePaymentsList() != null) {
				auditDetails.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(), "",
						auditHeader.getAuditTranType()));
			}
			if (financeDetail.getCovenantTypeList() != null) {
				auditDetails.addAll(getFinCovenantTypeService().delete(financeDetail.getCovenantTypeList(), "",
						auditHeader.getAuditTranType()));
			}

			//Collateral assignment Details
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				auditDetails.addAll(details);
				getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "");
			}

			// FinAssetTypes details
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "");
				auditDetails.addAll(details);
			}

			// AssetType Extended field Details
			if (financeDetail.getExtendedFieldRenderList() != null
					&& financeDetail.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

				List<String> tableNames = new ArrayList<>();
				for (int i = 0; i < details.size(); i++) {
					ExtendedFieldRender asset = (ExtendedFieldRender) details.get(i).getModelData();
					details.get(i).setExtended(true);
					if (tableNames.contains(asset.getTypeCode())) {
						continue;
					}
					tableNames.add(asset.getTypeCode());

					// Table Name identification
					StringBuilder tableName = new StringBuilder();
					tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
					tableName.append("_");
					tableName.append(asset.getTypeCode());
					tableName.append("_ED");

					// Records Deletion from Table
					getExtendedFieldRenderDAO().deleteList(financeMain.getFinReference(), tableName.toString(), "");
				}
				auditDetails.addAll(details);
			}

			// Deleting Finance Insurance Details
			if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
					&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "");
				//delete from finschedule temp
				for (int i = 0; i < financeDetail.getFinScheduleData().getFinInsuranceList().size(); i++) {
					FinInsurances insurance = financeDetail.getFinScheduleData().getFinInsuranceList().get(i);
					getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "");
				}
				auditDetails.addAll(details);
			}

			if (financeDetail.getFinAssetEvaluation() != null) {
				auditDetails.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(), "",
						auditHeader.getAuditTranType()));
			}
		}

		if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
			auditDetails.addAll(getFinFeeDetailService().delete(
					financeDetail.getFinScheduleData().getFinFeeDetailActualList(), "", auditHeader.getAuditTranType(),
					isWIF));
		}

		//Delete Finance Premium Details 
		if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
			//FIXME : DataSet Removal to be worked on if it requires in future
		}

		//Finance Deletion
		getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, isWIF, true);

		//Step Details Deletion
		getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "");

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws JaxenException
	 * @throws DatatypeConfigurationException 
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException, JaxenException {
		logger.debug("Entering");

		String tranType = "";
		String roleCode = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF);
		if (!isWIF) {
			aAuditHeader = processLimitApprove(aAuditHeader,true);
		}
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		
		Date curBDay = DateUtility.getAppDate();
		//gCDCustomerService.processGcdCustomer(financeDetail, "insert"); // inserting gcdcustomer.
		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			financeMain.setFinApprovedDate(curBDay);

			if (financeMain.getFinContractDate() == null) {
				financeMain.setFinContractDate(financeMain.getFinStartDate());
			}

			if (financeMain.getFeeChargeAmt() == null) {
				financeMain.setFeeChargeAmt(BigDecimal.ZERO);
			}

			if (financeMain.getInsuranceAmt() == null) {
				financeMain.setInsuranceAmt(BigDecimal.ZERO);
			}
		}
		//To maintain record Maintained status for furthur process
		String recordMainStatus = StringUtils.trimToEmpty(financeMain.getRcdMaintainSts());

		if (!isWIF) {
			auditHeader = executeAccountingProcess(auditHeader, curBDay);
		}

		//Validation Checking for All Finance Detail data
		//=======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// send ConfirmReservation Request to ACP Interface and save log details
		//======================================================================
		if (!ImplementationConstants.LIMIT_INTERNAL) {
			if (!isWIF) {
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)
						|| StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(
								FinanceConstants.FINSER_EVENT_ROLLOVER)) {
					getLimitCheckDetails().doProcessLimits(financeMain, FinanceConstants.CONFIRM);
				}

			}
		}

		// Rolled Over Details Accounting Process
		//=======================================
		if (financeDetail.getRolledoverFinanceHeader() != null) {

			// Actual Customer payment Amounts Process
			List<RolledoverFinanceDetail> rolledoverFinanceDetails = financeDetail.getRolledoverFinanceHeader()
					.getRolledoverFinanceDetails();
			for (RolledoverFinanceDetail detail : rolledoverFinanceDetails) {

				if (detail.getCustPayment().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				try {
					doRolledOverFinancePostings(detail.getFinReference(), detail.getCustPayment(), financeDetail
							.getRolledoverFinanceHeader().getPaymentAccount());
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.debug("Exception: ", e);
					ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
					errorDetails.add(new ErrorDetails("RolledOver", PennantConstants.ERR_UNDEF, "E", e.getMessage(),
							new String[] {}, new String[] {}));
					auditHeader.setErrorList(errorDetails);
				} catch (InterfaceException e) {
					logger.debug("Exception: ", e);
					ArrayList<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();
					errorDetails.add(new ErrorDetails(e.getErrorCode(), PennantConstants.ERR_UNDEF, "E", e
							.getErrorMessage(), new String[] {}, new String[] {}));
					auditHeader.setErrorList(errorDetails);
				}
			}
		}

		//Validation Checking for All Finance Detail data
		//=======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		//Re-Prepare of Finance Overdue Details with Existing Data
		//=======================================
		if (StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(FinanceConstants.FINSER_EVENT_CHGFRQ)) {

			List<FinanceScheduleDetail> schdList = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			for (int i = 1; i < schdList.size(); i++) {

				FinanceScheduleDetail curSchd = schdList.get(i);
				if (!(curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(
						BigDecimal.ZERO) > 0))) {
					continue;
				}

				if (curSchd.isSchPftPaid() || curSchd.isSchPriPaid()) {
					continue;
				}

				if (curSchd.getSchDate().compareTo(curBDay) > 0) {
					continue;
				}

				FinRepayQueue finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setBranch(financeMain.getFinBranch());
				finRepayQueue.setFinType(financeMain.getFinType());
				finRepayQueue.setCustomerID(financeMain.getCustID());
				finRepayQueue.setRpyDate(curSchd.getSchDate());
				finRepayQueue.setFinPriority(9999);
				finRepayQueue.setFinRpyFor("S");
				finRepayQueue.setSchdPft(curSchd.getProfitSchd());
				finRepayQueue.setSchdPri(curSchd.getPrincipalSchd());
				finRepayQueue.setSchdPftPaid(curSchd.getSchdPftPaid());
				finRepayQueue.setSchdPriPaid(curSchd.getSchdPriPaid());
				finRepayQueue.setSchdPftBal(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				finRepayQueue.setSchdPriBal(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				finRepayQueue.setSchdIsPftPaid(false);
				finRepayQueue.setSchdIsPriPaid(false);

				try {
					getRecoveryPostingsUtil().overDueDetailPreparation(finRepayQueue, financeMain.getProfitDaysBasis(),
							curBDay, false, false);
				} catch (IllegalAccessException e) {
					logger.error("Exception: ", e);
				} catch (InvocationTargetException e) {
					logger.error("Exception: ", e);
				}
			}

			//Recalculate Status of Finance using Overdue
			String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
			financeMain.setFinStatus(curFinStatus);

			//Suspense Process Check after Overdue Details Recalculation
			suspenseCheckProcess(financeMain, FinanceConstants.FINSER_EVENT_CHGFRQ, curBDay,
					financeMain.getFinStatus(), 0);

		}

		//Bundled Product TODO: Need to check why it was calling
		BundledProductsDetail bundledProductTemp = null;
		if (ImplementationConstants.ALLOW_BUNDLEDPRODUCT) {
			bundledProductTemp = getBundledProductsDetailService().getBundledProductsDetailById(
					financeMain.getFinReference(), "_Temp");
		}

		// Fetch Next Payment Details from Finance for Salaried Postings Verification
		FinanceScheduleDetail orgNextSchd = null;
		if (ImplementationConstants.ALLOW_FIN_SALARY_PAYMENT && StringUtils.isNotEmpty(financeMain.getRcdMaintainSts())) {
			orgNextSchd = getFinanceScheduleDetailDAO().getNextSchPayment(financeMain.getFinReference(), curBDay);
		}
		String recordType = financeMain.getRecordType();
		
		if (!isWIF) {
			processLimitApprove(aAuditHeader,false);
		}

		if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, isWIF, true);
			listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "", isWIF);
			getFinServiceInstructionDAO().deleteList(financeDetail.getFinScheduleData().getFinReference(),
					financeDetail.getModuleDefiner(), "");

			//Delete Finance Premium Details 
			if (StringUtils.equals(FinanceConstants.PRODUCT_SUKUK, productCode)) {
				//FIXME : DataSet Removal to be worked on if it requires in future
			}

			//Delete overdraft Details 
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
				getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "", isWIF);
			}

			//Finance Flag Details
			if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "");
				auditDetails.addAll(details);
			}

			//Step Details Deletion
			//=======================================
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "");

			if (!isWIF && (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) || (financeMain.isQuickDisb()|| !financeDetail.isStp()))) {
				//Additional Field Details Deletion
				//=======================================
				doDeleteAddlFieldDetails(financeDetail, "");
				auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", tranType));
				auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "",
						auditHeader.getAuditTranType()));
				auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "",
						auditHeader.getAuditTranType())));
			}

			// Delete Rolledover Finance Details
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().deleteHeader(financeMain.getFinReference(), "");

				// Rolledover Details
				getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), "");
			}

		} else {
			roleCode = financeMain.getRoleCode();
			financeMain.setRcdMaintainSts("");
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);
			
			// Resetting Maturity Terms & Summary details rendering in case of Reduce maturity cases
			if(!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())){
				int size = financeDetail.getFinScheduleData().getFinanceScheduleDetails().size();
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = financeDetail.getFinScheduleData().getFinanceScheduleDetails().get(i);
					if(curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0){
						financeMain.setMaturityDate(curSchd.getSchDate());
						break;
					}else if(curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && 
							curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0){
						financeDetail.getFinScheduleData().getFinanceScheduleDetails().remove(i);
					}
				}
			}

			if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				// Mandate Should be processed first for changes since the mandate id will be updated in the finance
				// main.
				getFinMandateService().doApprove(financeDetail, auditHeader, "");

				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				if (financeDetail.getFinScheduleData().getFinanceType() != null
						&& financeDetail.getFinScheduleData().getFinanceType().isAllowDownpayPgm()
						&& StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
					financeMain.setLinkedFinRef(financeMain.getFinReference() + "_DP");
				}
				getFinanceMainDAO().save(financeMain, TableType.MAIN_TAB, isWIF);

				// Setting BPI Paid amount to Schedule details
				// =======================================
				FinanceRepayments repayment = null;
				if (financeMain.isAlwBPI()
						&& StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, financeMain.getBpiTreatment())) {
					for (int i = 0; i < financeDetail.getFinScheduleData().getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail curSchd = financeDetail.getFinScheduleData().getFinanceScheduleDetails()
								.get(i);
						if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
							curSchd.setSchdPftPaid(financeMain.getBpiAmount());
							if (curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) == 0) {
								curSchd.setSchPftPaid(true);
								long linkedTranId = financeDetail.getFinScheduleData().getDisbursementDetails().get(0)
										.getLinkedTranId();
								repayment = prepareBpiRepayData(financeMain, curSchd.getSchDate(), linkedTranId,
										financeMain.getBpiAmount());
							}
							break;
						}
						if (curSchd.getInstNumber() > 1) {
							break;
						}
					}
				}

				// Schedule Details
				// =======================================
				listSave(financeDetail.getFinScheduleData(), "", isWIF, 0);

				// BPI Repayment details saving
				if (repayment != null) {
					getFinanceRepaymentsDAO().save(repayment, "");
				}

				// Save Finance Step Policy Details
				// =======================================
				saveStepDetailList(financeDetail.getFinScheduleData(), isWIF, "");

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF, "");

				// Secondary Account Details
				// ===========================================
				saveSecondaryAccountList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
						"");

				// Save FInance Tax Details
				FinanceTaxDetail financeTaxDetail = financeDetail.getTaxDetail();
				if (financeTaxDetail != null) {
					FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeTaxDetail.getFinReference(), "_AView");
					if (tempTaxDetail != null) {
						getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.MAIN_TAB);
					}
					if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(financeTaxDetail.getApplicableFor())
							|| PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(financeTaxDetail.getApplicableFor())
							|| PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(financeTaxDetail.getApplicableFor())) {
						financeTaxDetail.setRecordType(" ");
						financeTaxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						financeTaxDetail.setTaskId("");
						financeTaxDetail.setNextTaskId("");
						financeTaxDetail.setRoleCode("");
						financeTaxDetail.setNextRoleCode("");
						financeTaxDetail.setWorkflowId(0);
						getFinanceTaxDetailDAO().save(financeTaxDetail, TableType.MAIN_TAB);
					}
				}

				// Indicative Term Sheet Details Maintenance
				// =======================================
				IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
				if (termDetail != null) {
					termDetail.setFinReference(financeMain.getFinReference());
					getIndicativeTermDetailDAO().save(termDetail, "", true);
					getIndicativeTermDetailDAO().delete(termDetail, "_Temp", true);
				}

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_SUKUK, productCode)) {
					// FIXME : DataSet Removal to be worked on if it requires in future
				}

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& !financeDetail.getFinScheduleData().getOverdraftScheduleDetails().isEmpty()) {

					// Save New list of records
					for (int i = 0; i < financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size(); i++) {
						financeDetail.getFinScheduleData().getOverdraftScheduleDetails().get(i)
								.setFinReference(financeMain.getFinReference());
					}
					getOverdraftScheduleDetailDAO()
							.saveList(financeDetail.getFinScheduleData().getOverdraftScheduleDetails(), "");
				}

				/*
				 * // Save Finance Flag details if(financeDetail.getFinFlagsDetails() != null &&
				 * !financeDetail.getFinFlagsDetails().isEmpty()) {
				 * getFinFlagDetailsDAO().savefinFlagList(financeDetail.getFinFlagsDetails(), ""); }
				 */

				// Save Contributor Header Details
				// =======================================
				if (financeDetail.getFinContributorHeader() != null) {

					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
					getFinContributorHeaderDAO().save(contributorHeader, "");
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
							financeDetail.getFinContributorHeader().getBefImage(),
							financeDetail.getFinContributorHeader()));

					// Save Contributor Header Details
					// =======================================
					if (contributorHeader.getContributorDetailList() != null
							&& contributorHeader.getContributorDetailList().size() > 0) {
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
						details = processingContributorList(details, "", contributorHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}

				// Save Rolledover Finance Details
				// =======================================
				if (financeDetail.getRolledoverFinanceHeader() != null) {
					getRolledoverFinanceDAO().saveHeader(financeDetail.getRolledoverFinanceHeader(), "");

					// Rolledover Details
					getRolledoverFinanceDAO().saveDetailList(
							financeDetail.getRolledoverFinanceHeader().getRolledoverFinanceDetails(), "");
				}

				// Plan EMI Holiday Details
				// =======================================
				if (financeMain.isPlanEMIHAlw()) {
					if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
						if (financeDetail.getFinScheduleData().getPlanEMIHmonths() != null
								&& !financeDetail.getFinScheduleData().getPlanEMIHmonths().isEmpty()) {

							List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
							for (int i = 0; i < financeDetail.getFinScheduleData().getPlanEMIHmonths().size(); i++) {
								int planEMIHMonth = financeDetail.getFinScheduleData().getPlanEMIHmonths().get(i);
								FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
								emiHoliday.setFinReference(financeMain.getFinReference());
								emiHoliday.setPlanEMIHMonth(planEMIHMonth);
								holidayList.add(emiHoliday);
							}

							getFinPlanEmiHolidayDAO().savePlanEMIHMonths(holidayList, "");
						}
					} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(),
							FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
						if (financeDetail.getFinScheduleData().getPlanEMIHDates() != null
								&& !financeDetail.getFinScheduleData().getPlanEMIHDates().isEmpty()) {

							List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
							for (int i = 0; i < financeDetail.getFinScheduleData().getPlanEMIHDates().size(); i++) {
								Date planEMIHDate = financeDetail.getFinScheduleData().getPlanEMIHDates().get(i);
								FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
								emiHoliday.setFinReference(financeMain.getFinReference());
								emiHoliday.setPlanEMIHDate(planEMIHDate);
								holidayList.add(emiHoliday);
							}

							getFinPlanEmiHolidayDAO().savePlanEMIHDates(holidayList, "");
						}
					}
				}
				// Vas Recording Details
				if (financeDetail.getFinScheduleData().getVasRecordingList() != null
						&& !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
					details = processingVasRecordngList(details, "");
					auditDetails.addAll(details);

					// Vas Recording Extended Field Details
					List<AuditDetail> exdDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
					exdDetails = processingExtendedFieldDetailList(exdDetails, null, VASConsatnts.MODULE_NAME, "");
					auditDetails.addAll(exdDetails);
				}

			} else {

				tranType = PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");

				if (!isWIF) {

					// Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if (financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {
						oldFinSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(), "",
								-1);
						oldFinSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());
					}

					// Overdraft Movements are set for every Overdraft Schedule Built,and limit change is calculated by
					// sub old fin asset and current Fin asset
					// =======================================
					if (StringUtils.equals(financeDetail.getModuleDefiner(),
							FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)
							&& financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {

						BigDecimal limitChange = financeMain.getFinAssetValue()
								.subtract(oldFinSchdData.getFinanceMain().getFinAssetValue());

						OverdraftMovements odMovements = new OverdraftMovements();
						odMovements.setFinReference(financeMain.getFinReference());
						odMovements.setDroplineDate(financeMain.getFirstDroplineDate());
						odMovements.setDroplineFrq(financeMain.getDroplineFrq());
						odMovements.setTenor(financeMain.getNumberOfTerms());
						odMovements.setODExpiryDate(financeMain.getMaturityDate());
						odMovements.setODLimit(financeMain.getFinAssetValue());
						odMovements.setLimitChange(limitChange);
						odMovements.setValueDate(Calendar.getInstance().getTime());

						// Saving the OverdraftMovements in table
						getOverdraftScheduleDetailDAO().saveOverdraftMovement(odMovements);

					}

					// Create log entry for Action for Schedule Modification
					// =======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
					entryDetail.setEventAction(StringUtils.isBlank(financeDetail.getAccountingEventCode())
							? AccountEventConstants.ACCEVENT_ADDDBSN : financeDetail.getAccountingEventCode());
					entryDetail
							.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated());
					entryDetail.setPostDate(curBDay);
					entryDetail.setReversalCompleted(false);
					long logKey = getFinLogEntryDetailDAO().save(entryDetail);

					// Save Schedule Details For Future Modifications
					if (financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {
						listSave(oldFinSchdData, "_Log", isWIF, logKey);
					}
				}

				// Save Finance Main after Saving the oldFinSchdData
				// =======================================
				getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, isWIF);

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& !financeDetail.getFinScheduleData().getOverdraftScheduleDetails().isEmpty()) {
					getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "", isWIF);

					// Save New list of records
					for (int i = 0; i < financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size(); i++) {
						financeDetail.getFinScheduleData().getOverdraftScheduleDetails().get(i)
								.setFinReference(financeMain.getFinReference());
					}
					getOverdraftScheduleDetailDAO()
							.saveList(financeDetail.getFinScheduleData().getOverdraftScheduleDetails(), "");
				}

				// ScheduleDetails delete and save
				// =======================================
				listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "", isWIF);
				getFinServiceInstructionDAO().deleteList(financeMain.getFinReference(),
						financeDetail.getModuleDefiner(), "_Temp");
				listSave(financeDetail.getFinScheduleData(), "", isWIF, 0);

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF, "");

				// Secondary Account Details
				// =======================================
				saveSecondaryAccountList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
						"");

				// Update Finance Premium Details
				// =======================================
				if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
					// FIXME : DataSet Removal to be worked on if it requires in future
				}

			}

			// set Customer Details Audit
			if (financeDetail.getCustomerDetails() != null
					&& StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
				auditDetails.addAll(getCustomerDetailsService().saveOrUpdate(financeDetail, ""));
			}

			if (!financeDetail.isExtSource() && !isWIF) {

				// Contractor Asset Details
				// =======================================
				List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
				if (contractorAssetDetails != null && !contractorAssetDetails.isEmpty()) {
					auditDetails
							.addAll(getContractorAssetDetailService().doApprove(contractorAssetDetails, "", tranType));
				}

				// Save Document Details
				// =======================================
				if (financeDetail.getDocumentDetailsList() != null
						&& financeDetail.getDocumentDetailsList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, "",
							financeDetail.getFinScheduleData().getFinanceMain(), financeDetail.getModuleDefiner());
					auditDetails.addAll(details);
					listDocDeletion(financeDetail, "_Temp");
				}

				// set Check list details Audit
				// =======================================
				if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
					auditDetails.addAll(getCheckListDetailService().doApprove(financeDetail, ""));
				}

				// set the Audit Details & Save / Update Guarantor Details
				// =======================================
				if (financeDetail.getGurantorsDetailList() != null
						&& !financeDetail.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(getGuarantorDetailService().doApprove(financeDetail.getGurantorsDetailList(),
							"", tranType, financeMain.getFinSourceID()));
				}

				// set the Audit Details & Save / Update JountAccount Details
				// =======================================
				if (financeDetail.getJountAccountDetailList() != null
						&& !financeDetail.getJountAccountDetailList().isEmpty()) {
					auditDetails.addAll(getJointAccountDetailService().doApprove(
							financeDetail.getJountAccountDetailList(), "", tranType, financeMain.getFinSourceID()));
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
					auditDetails.addAll(getFinCollateralService().doApprove(financeDetail.getFinanceCollaterals(), "",
							tranType, financeMain.getFinSourceID()));
				}

				// Finance Eligibility Rule Details
				// =======================================
				auditDetails.addAll(getEligibilityDetailService().saveOrUpdate(financeDetail));

				// Finance Scoring Module Details List Saving
				// =======================================
				auditDetails.addAll(getScoringDetailService().saveOrUpdate(financeDetail));

				// Dedup Details
				// =======================================
				auditDetails.addAll(saveDedupDetails(financeDetail));

				// Additional Field Details Save / Update
				// =======================================
				doSaveAddlFieldDetails(financeDetail, "");

				// Etihad Credit Bureau Details
				// =======================================
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					getEtihadCreditBureauDetailService().doApprove(financeDetail.getEtihadCreditBureauDetail(), "",
							tranType);
				}

				// Bundled Products Details
				// =======================================
				if (bundledProductTemp != null) {
					getBundledProductsDetailService().doApprove(bundledProductTemp, "", tranType);
				}
				// Agreement Field Details
				// =======================================
				if (financeDetail.getAgreementFieldDetails() != null) {
					getAgreementFieldsDetailService().doApprove(financeDetail.getAgreementFieldDetails(), "", tranType);
				}

				// Asset Evaluation Details
				// =======================================
				if (financeDetail.getFinAssetEvaluation() != null) {
					getFinAssetEvaluationService().doApprove(financeDetail.getFinAssetEvaluation(), "", tranType);
				}

				// Advance Payment Details
				// =======================================
				if (financeDetail.getAdvancePaymentsList() != null) {
					if (StringUtils.trimToEmpty(recordMainStatus).equals(FinanceConstants.FINSER_EVENT_CANCELDISB)) {
						getFinAdvancePaymentsService().doCancel(financeDetail);
					} else {
						getFinAdvancePaymentsService().doApprove(financeDetail.getAdvancePaymentsList(), "", tranType);
					}

					getFinAdvancePaymentsService().processDisbursments(financeDetail);
				}

				// Covenant Type Details
				// =======================================
				if (financeDetail.getCovenantTypeList() != null) {
					getFinCovenantTypeService().doApprove(financeDetail.getCovenantTypeList(), "", tranType);
				}

				// Collateral Assignments Details
				// =======================================
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					details = processingCollateralAssignmentList(details, "",
							financeDetail.getFinScheduleData().getFinanceMain());
					auditDetails.addAll(details);
				}

				// FinAssetTypes
				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					details = processingFinAssetTypesList(details, "",
							financeDetail.getFinScheduleData().getFinanceMain());
					auditDetails.addAll(details);
				}

				// AssetType Extended field Details
				if (financeDetail.getExtendedFieldRenderList() != null
						&& financeDetail.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
					details = processingExtendedFieldDetailList(details, financeMain.getFinReference(),
							AssetConstants.EXTENDEDFIELDS_MODULE, "");
					auditDetails.addAll(details);
				}

				// Fin Flag Details
				if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
					details = processingFinFlagDetailList(details, "");
					auditDetails.addAll(details);
				}

			}

			// Finance Insurance Details
			// =======================================
			if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
					&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				details = processFinInsuranceDetails(details, "", financeDetail, isWIF);
				auditDetails.addAll(details);
			}

			// Finance Fee Details
			if (!financeDetail.isExtSource()) {
				if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
					getFinFeeDetailService().doApprove(financeDetail.getFinScheduleData().getFinFeeDetailActualList(),
							"", tranType, isWIF);
				}
			}

			// Finance Fee Receipt Details
			if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
				if (financeDetail.getFinScheduleData().getFinFeeReceipts() == null
						|| financeDetail.getFinScheduleData().getFinFeeReceipts().isEmpty()) {
					getFinFeeDetailService().createExcessAmount(financeMain.getFinReference(), null);
				} else {
					getFinFeeDetailService().doApproveFinFeeReceipts(
							financeDetail.getFinScheduleData().getFinFeeReceipts(), "", tranType, financeMain.getFinReference());
				}
			}
		}

		// Save New Finance Object created with DownPayment Support program
		//=======================================
		if (financeDetail.getFinScheduleData().getFinanceType() != null
				&& financeDetail.getFinScheduleData().getFinanceType().isAllowDownpayPgm()
				&& StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
			downpayFinApprove(aAuditHeader);
		}

		// Save Finance Schedule Snapshot
		//===============================
		//TODO commented below line which is leading to column miss match exception 
		//getFinanceMainDAO().saveFinanceSnapshot(financeMain); 

		// Update Task_log and Task_Owners tables
		//=======================================
		financeMain.setRoleCode(roleCode);
		updateTaskLog(financeMain, false);

		// Send Handling instruction to ICCS interface
		doHandlingInstructionProcess(financeDetail);

		// Save Salaried Posting Details
		if (ImplementationConstants.ALLOW_FIN_SALARY_PAYMENT && StringUtils.isNotEmpty(financeMain.getRcdMaintainSts())) {
			saveFinSalPayment(financeDetail.getFinScheduleData(), orgNextSchd, false);
		}
		if (!isWIF) {
			getFinStageAccountingLogDAO()
					.update(financeMain.getFinReference(), financeDetail.getModuleDefiner(), false);
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

		if (!financeDetail.isExtSource()) {

			// Delete Rolledover Finance Details
			//=======================================
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().deleteHeader(financeMain.getFinReference(), "_Temp");

				// Rolledover Details
				getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), "_Temp");
			}

			// Save FInance Tax Details
			if (financeDetail.getTaxDetail() != null) {
				FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeDetail.getTaxDetail().getFinReference(), "_TView");
				if (tempTaxDetail != null) {
					getFinanceTaxDetailDAO().delete(financeDetail.getTaxDetail(), TableType.TEMP_TAB);
				}
			}
			
			// ScheduleDetails delete
			//=======================================
			if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) || (financeMain.isQuickDisb()|| !financeDetail.isStp())) {
				listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "_Temp", isWIF);
			}

			// Plan EMI Holiday Details Deletion, if exists on Old image
			//=======================================
			FinanceMain befFinMain = financeDetail.getFinScheduleData().getFinanceMain().getBefImage();
			if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(financeMain.getFinReference(), "_Temp");
				} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHDates(financeMain.getFinReference(), "_Temp");
				}
			}

			if (!isWIF && (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) || (financeMain.isQuickDisb()|| !financeDetail.isStp()))) {
				//Additional Field Details Deletion in _Temp Table
				//=======================================
				doDeleteAddlFieldDetails(financeDetail, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(getCheckListDetailService().delete(financeDetail, "_Temp",
						auditHeader.getAuditTranType()));
				auditDetailList.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_Temp",
						auditHeader.getAuditTranType())));
				auditDetailList.addAll(getContractorAssetDetailService().delete(
						financeDetail.getContractorAssetDetails(), "_Temp", auditHeader.getAuditTranType()));
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					auditDetailList.add(getEtihadCreditBureauDetailService().delete(
							financeDetail.getEtihadCreditBureauDetail(), "_Temp", auditHeader.getAuditTranType()));
				}
				if (bundledProductTemp != null) {
					auditDetailList.add(getBundledProductsDetailService().delete(bundledProductTemp, "_Temp",
							auditHeader.getAuditTranType()));
				}
				if (financeDetail.getAdvancePaymentsList() != null) {
					auditDetailList.addAll(getFinAdvancePaymentsService().delete(
							financeDetail.getAdvancePaymentsList(), "_Temp", auditHeader.getAuditTranType()));
				}
				if (financeDetail.getCovenantTypeList() != null) {
					auditDetailList.addAll(getFinCovenantTypeService().delete(financeDetail.getCovenantTypeList(),
							"_Temp", auditHeader.getAuditTranType()));
				}
				if (financeDetail.getFinAssetEvaluation() != null) {
					auditDetailList.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(),
							"_Temp", auditHeader.getAuditTranType()));
				}

				//Collateral assignment Details
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					auditDetailList.addAll(details);
					getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
				}

				// FinAssetTypes details
				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
					auditDetailList.addAll(details);
				}

				// AssetType Extended field Details
				if (financeDetail.getExtendedFieldRenderList() != null
						&& financeDetail.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

					List<String> tableNames = new ArrayList<>();
					for (int i = 0; i < details.size(); i++) {
						ExtendedFieldRender asset = (ExtendedFieldRender) details.get(i).getModelData();

						// Table Name identification
						StringBuilder tableName = new StringBuilder();
						tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
						tableName.append("_");
						tableName.append(asset.getTypeCode());
						tableName.append("_ED");

						details.get(i).setExtended(true);
						asset.setReference(financeMain.getFinReference());
						asset.setTableName(tableName.toString());
						asset.setWorkflowId(0);

						if (tableNames.contains(asset.getTypeCode())) {
							continue;
						}
						tableNames.add(asset.getTypeCode());

						getExtendedFieldRenderDAO().deleteList(financeMain.getFinReference(), tableName.toString(),
								"_Temp");

					}
					auditDetailList.addAll(details);
				}

				// Vas Recording Details details
				if (financeDetail.getFinScheduleData().getVasRecordingList() != null && !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
					getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
					auditDetailList.addAll(details);

					// Vas Recording Extended field Details
					List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");

					List<String> tableNames = new ArrayList<>();
					for (int i = 0; i < vasExtDetails.size(); i++) {
						ExtendedFieldRender vas = (ExtendedFieldRender) vasExtDetails.get(i).getModelData();
						vasExtDetails.get(i).setExtended(true);
						if (tableNames.contains(vas.getTypeCode())) {
							continue;
						}
						tableNames.add(vas.getTypeCode());

						// Table Name identification
						StringBuilder tableName = new StringBuilder();
						tableName.append(VASConsatnts.MODULE_NAME);
						tableName.append("_");
						tableName.append(vas.getTypeCode());
						tableName.append("_ED");

						// Records Deletion from Table
						getExtendedFieldRenderDAO().deleteList(vas.getReference(), tableName.toString(), "_Temp");
					}
					auditDetailList.addAll(vasExtDetails);
				}

				// Deleting Finance Insurance Details
				if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
						&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
					getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "_Temp");
					//delete from finschedule temp
					for (int i = 0; i < financeDetail.getFinScheduleData().getFinInsuranceList().size(); i++) {
						FinInsurances insurance = financeDetail.getFinScheduleData().getFinInsuranceList().get(i);
						getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "_Temp");
					}
					auditDetailList.addAll(details);
				}

				if (financeDetail.getFinanceCollaterals() != null) {
					auditDetailList.addAll(getFinCollateralService().delete(financeDetail.getFinanceCollaterals(),
							"_Temp", auditHeader.getAuditTranType()));
				}
			}

			if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) || (financeMain.isQuickDisb()|| !financeDetail.isStp())) {

				//Fin Fee Details Deletion
				if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
					auditDetailList.addAll(getFinFeeDetailService().delete(
							financeDetail.getFinScheduleData().getFinFeeDetailActualList(), "_Temp",
							auditHeader.getAuditTranType(), isWIF));
				}

				//Fin Fee Receipt Details Deletion
				if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null) {
					auditDetailList.addAll(getFinFeeDetailService().deleteFinFeeReceipts(
							financeDetail.getFinScheduleData().getFinFeeReceipts(), "_Temp", auditHeader.getAuditTranType()));
				}
				
				//Step Details Deletion
				//=======================================
				getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

				//Delete Finance Premium Details
				//=======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_SUKUK, productCode)) {
					//FIXME : DataSet Removal to be worked on if it requires in future
				}

				//Delete Finance Overdraft Details
				//=======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
					getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "_Temp", isWIF);
				}

				//Finance Flag Details
				if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
					getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME,
							"_Temp");
					auditDetailList.addAll(details);
				}

				// Finance Main Details
				//=======================================
				getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, isWIF, true);
			}
			
			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from Temp table
			if (!isWIF) {
				getAuditHeaderDAO().addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);

		// Adding audit as Insert/Update/deleted into main table
		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Save GCDCustomer'/////
		//processgcdCustomer(financeDetail, "insert");
		
		
		logger.debug("Leaving");

		return auditHeader;
	}
	
	
	private AuditHeader processFinOneCheck(AuditHeader auditHeader) {
		logger.debug("Entering");

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		AuditDetail auditDetail = auditHeader.getAuditDetail();
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		String[] errorParm = new String[2];
		errorParm[0] = "Loan";
		if (customerDetails.getCustomer().getCustCoreBank() != null) {
			// call the finone procedure to update a customer in Finone 
			getgCDCustomerService().processGcdCustomer(customerDetails, PennantConstants.CUSTOMER_DEDUP_UPDATE);
			if (StringUtils.equals(customerDetails.getGcdCustomer().getStatusFromFinnOne(), PennantConstants.CUSTOMER_DEDUP_REJECTED)) {
				errorParm[1]=customerDetails.getGcdCustomer().getRejectionReason();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99014", errorParm, null), auditHeader.getUsrLanguage()));
				auditDetail.setErrorDetails(
						ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));
				auditHeader.setAuditDetail(auditDetail);
				auditHeader.setErrorList(auditDetail.getErrorDetails());
				return auditHeader;
			}

		} else {
			// call the finone procedure to create a customer in Finone 
			getgCDCustomerService().processGcdCustomer(customerDetails, PennantConstants.CUSTOMER_DEDUP_INSERT);
			if (StringUtils.equals(customerDetails.getGcdCustomer().getStatusFromFinnOne(), PennantConstants.CUSTOMER_DEDUP_REJECTED)) {
				errorParm[1]=customerDetails.getGcdCustomer().getRejectionReason();
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "99014", errorParm, null), auditHeader.getUsrLanguage()));
				auditDetail.setErrorDetails(
						ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), auditHeader.getUsrLanguage()));
				auditHeader.setAuditDetail(auditDetail);
				auditHeader.setErrorList(auditDetail.getErrorDetails());
				return auditHeader;
			}
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for process Finance Maintenance and sending handling instruction request to ICCS interface
	 * 
	 * @param financeDetail
	 * @throws InterfaceException
	 * 
	 */
	private void doHandlingInstructionProcess(FinanceDetail financeDetail) throws InterfaceException {
		logger.debug("Entering");

		HandlingInstruction handlingInstruction = new HandlingInstruction();
		String narration = "";

		if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_POSTPONEMENT)) {//Postponement
			handlingInstruction.setMaintenanceCode(FinanceConstants.INSTCODE_POSTPONEMNT);
			Date installmentDate = financeDetail.getFinScheduleData().getFinanceMain().getEventFromDate();
			handlingInstruction.setInstallmentDate(installmentDate);
			narration = "Postponement";
		} else if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGFRQ)) {
			handlingInstruction.setMaintenanceCode(FinanceConstants.INSTCODE_AMENDINSTLMNT); // Modify installment date
			narration = "Amend Installment date";
		} else if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RATECHG)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGRPY)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDDISB)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGPFT)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RESCHD)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGGRCEND)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDTERM)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RMVTERM)) {
			handlingInstruction.setMaintenanceCode(FinanceConstants.INSTCODE_RESCHDPAY);
			narration = "Re-Schedule";
		}

		if (!StringUtils.isBlank(handlingInstruction.getMaintenanceCode())) {
			handlingInstruction.setFinanceRef(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			handlingInstruction.setRemarks(narration);

			// Send Handling instruction to ICCS interface
			getHandlingInstructionService().sendFinanceMaintenanceRequest(handlingInstruction);
		}

		logger.debug("Leaving");
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doPreApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException {
		logger.debug("Entering");

		String tranType = "";
		String roleCode = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Date curBDay = DateUtility.getAppDate();

		//Execute Accounting Details Process
		//=======================================
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = financeDetail.getFinScheduleData().getFinanceType().getFinCategory();
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			financeMain.setFinApprovedDate(curBDay);

			if (financeMain.getFinContractDate() == null) {
				financeMain.setFinContractDate(financeMain.getFinStartDate());
			}

			if (financeMain.getFeeChargeAmt() == null) {
				financeMain.setFeeChargeAmt(BigDecimal.ZERO);
			}
			if (financeMain.getInsuranceAmt() == null) {
				financeMain.setInsuranceAmt(BigDecimal.ZERO);
			}
		}

		//Validation Checking for All Finance Detail data
		//=======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		String preApprovalTableType = PennantConstants.PREAPPROVAL_TABLE_TYPE;

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, isWIF, true);
			listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "", isWIF);

			//Delete Finance Premium Details 
			if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
				//FIXME : DataSet Removal to be worked on if it requires in future
			}

			//Step Details Deletion
			//=======================================
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

			if (!isWIF) {
				//Additional Field Details Deletion
				//=======================================
				doDeleteAddlFieldDetails(financeDetail, "");
				auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", tranType));
				auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "",
						auditHeader.getAuditTranType()));
				auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "",
						auditHeader.getAuditTranType())));
			}

		} else {
			roleCode = financeMain.getRoleCode();
			financeMain.setRcdMaintainSts("");
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setWorkflowId(0);

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				if (financeDetail.getFinScheduleData().getFinanceType() != null
						&& financeDetail.getFinScheduleData().getFinanceType().isAllowDownpayPgm()
						&& StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
					financeMain.setLinkedFinRef(financeMain.getFinReference() + "_DP");
				}
				getFinanceMainDAO().save(financeMain, TableType.PRE_APPR_TAB, isWIF);

				//Schedule Details
				//=======================================
				listSave(financeDetail.getFinScheduleData(), preApprovalTableType, isWIF, 0);

				// Save Finance Step Policy Details
				//=======================================
				saveStepDetailList(financeDetail.getFinScheduleData(), isWIF, preApprovalTableType);

				//Fee Charge Details
				//=======================================
				saveFeeChargeList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
						preApprovalTableType);

				//Secondary Account Details
				//=======================================
				saveSecondaryAccountList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
						preApprovalTableType);

				//Save Finance Premium Details
				//=======================================
				if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
					//FIXME : DataSet Removal to be worked on if it requires in future
				}

				// Save Contributor Header Details
				//=======================================
				if (financeDetail.getFinContributorHeader() != null) {

					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
					getFinContributorHeaderDAO().save(contributorHeader, preApprovalTableType);
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
							financeDetail.getFinContributorHeader().getBefImage(), financeDetail
									.getFinContributorHeader()));

					// Save Contributor Header Details
					//=======================================
					if (contributorHeader.getContributorDetailList() != null
							&& contributorHeader.getContributorDetailList().size() > 0) {
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
						details = processingContributorList(details, preApprovalTableType,
								contributorHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}

			} else {

				tranType = PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, isWIF);

				if (!isWIF) {

					//Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if (financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {
						oldFinSchdData = getFinSchDataByFinRef(financeDetail.getFinScheduleData().getFinReference(),
								"", -1);
						oldFinSchdData.setFinReference(financeDetail.getFinScheduleData().getFinReference());
					}

					//Create log entry for Action for Schedule Modification
					//=======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
					entryDetail.setEventAction(financeDetail.getAccountingEventCode());
					entryDetail.setSchdlRecal(financeDetail.getFinScheduleData().getFinanceMain()
							.isScheduleRegenerated());
					entryDetail.setPostDate(curBDay);
					entryDetail.setReversalCompleted(false);
					long logKey = getFinLogEntryDetailDAO().save(entryDetail);

					//Save Schedule Details For Future Modifications
					if (financeDetail.getFinScheduleData().getFinanceMain().isScheduleRegenerated()) {
						listSave(oldFinSchdData, "_Log", isWIF, logKey);
					}
				}

				// ScheduleDetails delete and save
				//=======================================
				listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "", isWIF);
				listSave(financeDetail.getFinScheduleData(), "", isWIF, 0);

				//Fee Charge Details
				//=======================================

				//Secondary Account Details
				//=======================================
				saveSecondaryAccountList(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), isWIF,
						"");

				//Update Finance Premium Details
				//=======================================
				if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
					//FIXME : DataSet Removal to be worked on if it requires in future
				}

			}

			if (!financeDetail.isExtSource() && !isWIF) {

				// Asset Details Process
				//=======================================

				// Save Document Details
				//=======================================
				if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, preApprovalTableType, financeDetail
							.getFinScheduleData().getFinanceMain(), financeDetail.getModuleDefiner());
					auditDetails.addAll(details);
					listDocDeletion(financeDetail, "_Temp");
				}

				// set Check list details Audit
				//=======================================
				if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
					auditDetails.addAll(getCheckListDetailService().doApprove(financeDetail, preApprovalTableType));
				}

				// set the Audit Details & Save / Update Guarantor Details
				//=======================================
				if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(getGuarantorDetailService().doApprove(financeDetail.getGurantorsDetailList(),
							preApprovalTableType, tranType, ""));
				}

				// set the Audit Details & Save / Update JountAccount Details 
				//=======================================
				if (financeDetail.getJountAccountDetailList() != null
						&& !financeDetail.getJountAccountDetailList().isEmpty()) {
					auditDetails.addAll(getJointAccountDetailService().doApprove(
							financeDetail.getJountAccountDetailList(), preApprovalTableType, tranType, ""));
				}

				// set Finance Collateral Details Audit
				//=======================================
				if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
					auditDetails.addAll(getFinCollateralService().doApprove(financeDetail.getFinanceCollaterals(),
							preApprovalTableType, tranType, ""));
				}

				// set Finance Insurance Details Audit
				//=======================================
				if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
						&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
					details = processFinInsuranceDetails(details, preApprovalTableType, financeDetail, isWIF);
					auditDetails.addAll(details);
				}

				//Finance Eligibility Rule Details
				//=======================================
				String finReference = financeMain.getFinReference();
				auditDetails.addAll(getEligibilityDetailService().saveOrUpdate(financeDetail));
				//move to pre-approval table
				List<FinanceEligibilityDetail> list = getEligibilityDetailService().getFinElgDetailList(finReference);
				if (list != null && !list.isEmpty()) {
					getEligibilityDetailService().saveList(list, preApprovalTableType);
					getEligibilityDetailService().deleteByFinRef(finReference);
				}

				// Finance Scoring Module Details List Saving 
				//=======================================
				auditDetails.addAll(getScoringDetailService().saveOrUpdate(financeDetail));
				//move to pre-approval table
				List<FinanceScoreHeader> hearlist = getScoringDetailService().getFinScoreHeaderList(finReference, "");
				if (hearlist != null && !hearlist.isEmpty()) {
					List<Long> headerIds = new ArrayList<Long>();
					for (FinanceScoreHeader header : hearlist) {
						headerIds.add(header.getHeaderId());
						getScoringDetailService().saveHeader(header, preApprovalTableType);
					}
					List<FinanceScoreDetail> detailslist = getScoringDetailService().getFinScoreDetailList(headerIds,
							"");
					getScoringDetailService().saveDetailList(detailslist, preApprovalTableType);
					//delete from maintable

					getScoringDetailService().deleteDetailList(headerIds, "");
					getScoringDetailService().deleteHeaderList(finReference, "");

				}

				//Dedup Details
				//=======================================
				auditDetails.addAll(saveDedupDetails(financeDetail));
				moveDedupsPreApproval(finReference, preApprovalTableType);

				// Etihad Credit Bureau Details
				//=======================================
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					getEtihadCreditBureauDetailService().doApprove(financeDetail.getEtihadCreditBureauDetail(),
							preApprovalTableType, tranType);
				}

				// Bundled Products Details
				//=======================================
				if (financeDetail.getBundledProductsDetail() != null) {
					getBundledProductsDetailService().doApprove(financeDetail.getBundledProductsDetail(),
							preApprovalTableType, tranType);
				}

				// Advance Payment Details
				//=======================================
				if (financeDetail.getAdvancePaymentsList() != null) {
					getFinAdvancePaymentsService().doApprove(financeDetail.getAdvancePaymentsList(),
							preApprovalTableType, tranType);
				}

				// Covenant Type  Details
				//=======================================
				if (financeDetail.getCovenantTypeList() != null) {
					getFinCovenantTypeService().doApprove(financeDetail.getCovenantTypeList(), preApprovalTableType,
							tranType);
				}

				// set Finance Collateral Details Audit
				//=======================================
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					details = processingCollateralAssignmentList(details, preApprovalTableType, financeDetail
							.getFinScheduleData().getFinanceMain());
					auditDetails.addAll(details);
				}

				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					details = processingFinAssetTypesList(details, preApprovalTableType, financeDetail
							.getFinScheduleData().getFinanceMain());
					auditDetails.addAll(details);
				}

				// Asset Evaluation Details
				//=======================================
				if (financeDetail.getFinAssetEvaluation() != null) {
					getFinAssetEvaluationService().doApprove(financeDetail.getFinAssetEvaluation(),
							preApprovalTableType, tranType);
				}
			}
			// Finance Fee Details
			if (!financeDetail.isExtSource()) {
				if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
					getFinFeeDetailService().doApprove(financeDetail.getFinScheduleData().getFinFeeDetailActualList(),
							preApprovalTableType, tranType, isWIF);
				}
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

		if (!financeDetail.isExtSource()) {

			// ScheduleDetails delete
			//=======================================
			listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "_Temp", isWIF);

			if (!isWIF) {
				//Additional Field Details Deletion in _Temp Table
				//=======================================
				doDeleteAddlFieldDetails(financeDetail, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(getCheckListDetailService().delete(financeDetail, "_Temp",
						auditHeader.getAuditTranType()));
				auditDetailList.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_Temp",
						auditHeader.getAuditTranType())));
				auditDetailList.addAll(getContractorAssetDetailService().delete(
						financeDetail.getContractorAssetDetails(), "_Temp", auditHeader.getAuditTranType()));
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					auditDetails.add(getEtihadCreditBureauDetailService().delete(
							financeDetail.getEtihadCreditBureauDetail(), "_Temp", auditHeader.getAuditTranType()));
				}
				if (financeDetail.getBundledProductsDetail() != null) {
					auditDetails.add(getBundledProductsDetailService().delete(financeDetail.getBundledProductsDetail(),
							"_Temp", auditHeader.getAuditTranType()));
				}
				if (financeDetail.getAdvancePaymentsList() != null) {
					auditDetails.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(),
							"_Temp", auditHeader.getAuditTranType()));
				}
				if (financeDetail.getCovenantTypeList() != null) {
					auditDetails.addAll(getFinCovenantTypeService().delete(financeDetail.getCovenantTypeList(),
							"_Temp", auditHeader.getAuditTranType()));
				}

				//Collateral assignment Details
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					auditDetails.addAll(details);
					getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
				}

				// FinAssetTypes details
				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
					auditDetails.addAll(details);
				}

				// AssetType Extended field Details
				if (financeDetail.getExtendedFieldRenderList() != null
						&& financeDetail.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

					List<String> tableNames = new ArrayList<>();
					for (int i = 0; i < details.size(); i++) {
						ExtendedFieldRender asset = (ExtendedFieldRender) details.get(i).getModelData();
						details.get(i).setExtended(true);
						if (tableNames.contains(asset.getTypeCode())) {
							continue;
						}
						tableNames.add(asset.getTypeCode());

						// Table Name identification
						StringBuilder tableName = new StringBuilder();
						tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
						tableName.append("_");
						tableName.append(asset.getTypeCode());
						tableName.append("_ED");

						// Records Deletion from Table
						getExtendedFieldRenderDAO().deleteList(financeMain.getFinReference(), tableName.toString(),
								"_Temp");
					}
					auditDetails.addAll(details);
				}

				// Vas Recording Details details
				if (financeDetail.getFinScheduleData().getVasRecordingList() != null && !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
					getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
					auditDetails.addAll(details);

					// Vas Recording Extended field Details
					List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");

					List<String> tableNames = new ArrayList<>();
					for (int i = 0; i < vasExtDetails.size(); i++) {
						ExtendedFieldRender vas = (ExtendedFieldRender) vasExtDetails.get(i).getModelData();
						vasExtDetails.get(i).setExtended(true);
						if (tableNames.contains(vas.getTypeCode())) {
							continue;
						}
						tableNames.add(vas.getTypeCode());

						// Table Name identification
						StringBuilder tableName = new StringBuilder();
						tableName.append(VASConsatnts.MODULE_NAME);
						tableName.append("_");
						tableName.append(vas.getTypeCode());
						tableName.append("_ED");

						// Records Deletion from Table
						getExtendedFieldRenderDAO().deleteList(vas.getReference(), tableName.toString(), "_Temp");
					}
					auditDetails.addAll(vasExtDetails);
				}

				// Deleting Finance Insurance Details
				if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
						&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
					getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "_Temp");

					//delete from finschedule temp
					for (int i = 0; i < financeDetail.getFinScheduleData().getFinInsuranceList().size(); i++) {
						FinInsurances insurance = financeDetail.getFinScheduleData().getFinInsuranceList().get(i);
						getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "_Temp");
					}
					auditDetails.addAll(details);
				}

				if (financeDetail.getFinAssetEvaluation() != null) {
					auditDetails.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(),
							"_Temp", auditHeader.getAuditTranType()));
				}
			}

			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				auditDetails.addAll(getFinFeeDetailService().delete(
						financeDetail.getFinScheduleData().getFinFeeDetailActualList(), "_Temp",
						auditHeader.getAuditTranType(), isWIF));
			}

			//Step Details Deletion
			//=======================================
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

			//Delete Finance Premium Details
			//=======================================
			if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
				//FIXME : DataSet Removal to be worked on if it requires in future
			}

			// Finance Main Details
			//=======================================
			getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, isWIF, true);

			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from Temp table
			if (!isWIF) {
				getAuditHeaderDAO().addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));

		// Adding audit as Insert/Update/deleted into main table
		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		//Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Save New Finance Object created with DownPayment Support program
		//=======================================
		if (financeDetail.getFinScheduleData().getFinanceType() != null
				&& financeDetail.getFinScheduleData().getFinanceType().isAllowDownpayPgm()) {
			downpayFinApprove(aAuditHeader);
		}

		// Save Finance Schedule Snapshot
		//===============================
		//TODO commented below line which is leading to column miss match exception 
		//getFinanceMainDAO().saveFinanceSnapshot(financeMain); 

		// Update Task_log and Task_Owners tables
		//=======================================
		financeMain.setRoleCode(roleCode);
		updateTaskLog(financeMain, false);

		//TODO: confirm limits is required for pre approve
		logger.debug("Leaving");
		return auditHeader;

	}

	/**
	 * @param finReference
	 * @param type
	 */
	private void moveDedupsPreApproval(String finReference, String type) {
		logger.debug(" Entering ");
		getBlacklistCustomerDAO().moveData(finReference, type);
		getFinanceDedupeDAO().moveData(finReference, type);
		getPoliceCaseDAO().moveData(finReference, type);
		getCustomerDedupDAO().moveData(finReference, type);
		logger.debug(" Leaving ");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws JaxenException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader doReject(AuditHeader auditHeader, boolean isWIF) throws InterfaceException, JaxenException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "doReject", isWIF);
		if (!auditHeader.isNextProcess()) {
			logger.debug("Leaving");
			return auditHeader;
		}
		String tranType = PennantConstants.TRAN_DEL;

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = StringUtils.trimToEmpty(financeDetail.getFinScheduleData().getFinanceType()
				.getFinCategory());

		// Cancel All Transactions done by Finance Reference
		//=======================================
		cancelStageAccounting(financeMain.getFinReference(), financeDetail.getModuleDefiner());
		
		getPostingsPreparationUtil().postReveralsByFinreference(financeMain.getFinReference());

		// Save Finance Details Data on Reject Tables
		//=======================================
		if (!isWIF && StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			getFinanceMainDAO().saveRejectFinanceDetails(financeMain);

			//Save Finance Rejected Details Data in FinanceRejectDetail Tables
			getFinanceRejectDetailDAO().saveFinanceRejectedDetailsLog(financeMain);
		}

		//OverDraft Schedule Details Deletion
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
				&& financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
			getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "_Temp", isWIF);
		}

		// Finance Details deletion
		//=======================================
		listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "_Temp", isWIF);
		getFinServiceInstructionDAO().deleteList(financeDetail.getFinScheduleData().getFinReference(),
				financeDetail.getModuleDefiner(), "_Temp");

		//Document Details 
		//=======================================
		getDocumentDetailsDAO().deleteList(financeDetail.getDocumentDetailsList(), "_Temp");

		// Fee charges deletion
		getFinFeeChargesDAO().deleteChargesBatch(financeMain.getFinReference(), financeDetail.getModuleDefiner(),
				false, "_Temp");

		// Fee Schedule deletion
		//getFinFeeScheduleDetailDAO().deleteFeeScheduleBatch(financeMain.getFinReference(), isWIF, "_Temp");

		// Secondary Account deletion
		getSecondaryAccountDAO().delete(financeMain.getFinReference(), "_Temp");

		// Checklist Details delete
		//=======================================
		getCheckListDetailService().delete(financeDetail, "_Temp", tranType);

		if (!isWIF) {
			//Additional Field Details Deletion
			//=======================================
			doDeleteAddlFieldDetails(financeDetail, "_Temp");
		}

		//Indicative Term Sheet Details Maintenance
		//=======================================
		IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
		if (termDetail != null) {
			termDetail.setFinReference(financeMain.getFinReference());
			getIndicativeTermDetailDAO().delete(termDetail, "_Temp", true);
		}

		//Delete Finance Premium Details 
		if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
			//FIXME : DataSet Removal to be worked on if it requires in future
		}

		// Delete Rolledover Finance Details
		//=======================================
		if (financeDetail.getRolledoverFinanceHeader() != null) {
			getRolledoverFinanceDAO().deleteHeader(financeMain.getFinReference(), "_Temp");

			// Rolledover Details
			getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), "_Temp");
		}

		// Plan EMI Holiday Details Deletion, if exists on Old image
		//=======================================
		FinanceMain befFinMain = financeDetail.getFinScheduleData().getFinanceMain().getBefImage();
		if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(financeMain.getFinReference(), "_Temp");
			} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				getFinPlanEmiHolidayDAO().deletePlanEMIHDates(financeMain.getFinReference(), "_Temp");
			}
		}
		
		// Delete Tax Details
		if (financeDetail.getTaxDetail() != null) {
			FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(financeDetail.getTaxDetail().getFinReference(), "_TView");
			if (tempTaxDetail != null) {
				getFinanceTaxDetailDAO().delete(financeDetail.getTaxDetail(), TableType.TEMP_TAB);
			}
		}

		// Finance Main Details Deletion
		//=======================================
		getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, isWIF, false);

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], financeMain
				.getBefImage(), financeMain));

		//Step Details
		//=======================================
		getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

		// Asset deletion
		if (!isWIF) {

			//Finance Eligibility Rule Details
			//=======================================
			List<FinanceEligibilityDetail> elgList = getEligibilityDetailService().getFinElgDetailList(
					financeMain.getFinReference());
			FinanceEligibilityDetail eligibilityDetail = new FinanceEligibilityDetail();
			String[] elgFields = PennantJavaUtil.getFieldDetails(eligibilityDetail,
					eligibilityDetail.getExcludeFields());
			for (int i = 0; i < elgList.size(); i++) {
				elgList.get(i).setLastMntBy(financeMain.getLastMntBy());
				elgList.get(i).setLastMntOn(financeMain.getLastMntOn());
				elgList.get(i).setRoleCode(financeMain.getRoleCode());
				elgList.get(i).setRecordStatus(financeMain.getRecordStatus());
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, elgFields[0], elgFields[1],
						null, elgList.get(i)));
			}
			getEligibilityDetailService().deleteByFinRef(financeMain.getFinReference());

			// Finance Scoring Module Details List Saving 
			//=======================================
			List<Object> scoreObjectList = getScoringDetailService().getFinScoreDetailList(
					financeMain.getFinReference());

			//Finance Score Headers
			if (scoreObjectList != null) {
				List<FinanceScoreHeader> headerList = (List<FinanceScoreHeader>) scoreObjectList.get(0);
				FinanceScoreHeader tempHeader = new FinanceScoreHeader();
				String[] headerFields = PennantJavaUtil.getFieldDetails(tempHeader, tempHeader.getExcludeFields());
				tempHeader = null;
				List<Long> headerIdList = new ArrayList<Long>();
				for (int i = 0; i < headerList.size(); i++) {
					headerIdList.add(headerList.get(i).getHeaderId());
					headerList.get(i).setLastMntBy(financeMain.getLastMntBy());
					headerList.get(i).setRoleCode(financeMain.getRoleCode());
					headerList.get(i).setRecordStatus(financeMain.getRecordStatus());
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, headerFields[0],
							headerFields[1], null, headerList.get(i)));
				}

				HashMap<Long, List<FinanceScoreDetail>> scoreMap = (HashMap<Long, List<FinanceScoreDetail>>) scoreObjectList
						.get(1);
				List<FinanceScoreDetail> detailList = new ArrayList<FinanceScoreDetail>();
				for (int i = 0; i < headerList.size(); i++) {
					detailList.addAll((Collection<FinanceScoreDetail>) scoreMap.get(headerIdList.get(i)));
				}
				FinanceScoreDetail tempDetail = new FinanceScoreDetail();
				String[] detailfields = PennantJavaUtil.getFieldDetails(tempDetail, tempDetail.getExcludeFields());
				tempDetail = null;

				for (int i = 0; i < detailList.size(); i++) {
					detailList.get(i).setLastMntBy(financeMain.getLastMntBy());
					detailList.get(i).setRoleCode(financeMain.getRoleCode());
					detailList.get(i).setRecordStatus(financeMain.getRecordStatus());
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, detailfields[0],
							detailfields[1], null, detailList.get(i)));
				}

				//Deletion of Scoring Details
				getScoringDetailService().deleteHeaderList(financeMain.getFinReference(), "");
				getScoringDetailService().deleteDetailList(headerIdList, "");
			}

			// Delete Black List Customer Data
			//=======================================
			List<FinBlacklistCustomer> blackListData = getBlacklistCustomerDAO().fetchFinBlackList(
					financeMain.getFinReference());
			FinBlacklistCustomer blData = new FinBlacklistCustomer();
			String[] blFields = PennantJavaUtil.getFieldDetails(blData, blData.getExcludeFields());
			for (int i = 0; i < blackListData.size(); i++) {
				blackListData.get(i).setLastMntBy(financeMain.getLastMntBy());
				blackListData.get(i).setRoleCode(financeMain.getRoleCode());
				blackListData.get(i).setRecordStatus(financeMain.getRecordStatus());
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, blFields[0], blFields[1], null,
						blackListData.get(i)));
			}
			getBlacklistCustomerDAO().deleteList(financeMain.getFinReference());

			// Delete Finance DeDup List Data
			//=======================================
			getFinanceDedupeDAO().deleteList(financeMain.getFinReference());

			// Delete Dedup PoliceCase Data
			//=======================================
			List<PoliceCase> policeCaseData = getPoliceCaseDAO().fetchFinPoliceCase(financeMain.getFinReference());
			PoliceCase policeCaselistData = new PoliceCase();
			String[] pcFields = PennantJavaUtil.getFieldDetails(policeCaselistData,
					policeCaselistData.getExcludeFields());
			for (int i = 0; i < policeCaseData.size(); i++) {
				policeCaseData.get(i).setLastMntBy(financeMain.getLastMntBy());
				policeCaseData.get(i).setRoleCode(financeMain.getRoleCode());
				policeCaseData.get(i).setRecordStatus(financeMain.getRecordStatus());
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, pcFields[0], pcFields[1], null,
						policeCaseData.get(i)));
			}

			getPoliceCaseDAO().deleteList(financeMain.getFinReference());

			// Cancel commodity inventory details
			doCheckCommodityInventory(financeDetail);

			auditDetails.addAll(jointGuarantorDeletion(financeDetail, "_Temp", auditHeader.getAuditTranType()));
			auditDetails.addAll(getCheckListDetailService().delete(financeDetail, "_Temp",
					auditHeader.getAuditTranType()));
			auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "_Temp",
					auditHeader.getAuditTranType())));
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				auditDetails.add(getEtihadCreditBureauDetailService().delete(
						financeDetail.getEtihadCreditBureauDetail(), "_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getBundledProductsDetail() != null) {
				auditDetails.add(getBundledProductsDetailService().delete(financeDetail.getBundledProductsDetail(),
						"_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAgreementFieldDetails() != null) {
				auditDetails.add(getAgreementFieldsDetailService().delete(financeDetail.getAgreementFieldDetails(),
						"_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAdvancePaymentsList() != null) {
				auditDetails.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(),
						"_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getCovenantTypeList() != null) {
				auditDetails.addAll(getFinCovenantTypeService().delete(financeDetail.getCovenantTypeList(), "_Temp",
						auditHeader.getAuditTranType()));
			}
			if (financeDetail.getFinAssetEvaluation() != null) {
				auditDetails.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(), "_Temp",
						auditHeader.getAuditTranType()));
			}

			//Collateral assignment Details
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				auditDetails.addAll(details);
				getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
			}

			// FinAssetTypes details
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
				auditDetails.addAll(details);
			}

			// AssetType Extended field Details
			if (financeDetail.getExtendedFieldRenderList() != null
					&& financeDetail.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

				List<String> tableNames = new ArrayList<>();
				for (int i = 0; i < details.size(); i++) {
					ExtendedFieldRender asset = (ExtendedFieldRender) details.get(i).getModelData();
					details.get(i).setExtended(true);
					if (tableNames.contains(asset.getTypeCode())) {
						continue;
					}
					tableNames.add(asset.getTypeCode());

					// Table Name identification
					StringBuilder tableName = new StringBuilder();
					tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
					tableName.append("_");
					tableName.append(asset.getTypeCode());
					tableName.append("_ED");

					// Records Deletion from Table
					getExtendedFieldRenderDAO()
							.deleteList(financeMain.getFinReference(), tableName.toString(), "_Temp");
				}
				auditDetails.addAll(details);
			}

			// Vas Recording Details details
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null && !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
				auditDetails.addAll(details);

				// Vas Recording Extended field Details
				List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");

				List<String> tableNames = new ArrayList<>();
				for (int i = 0; i < vasExtDetails.size(); i++) {
					ExtendedFieldRender vas = (ExtendedFieldRender) vasExtDetails.get(i).getModelData();
					vasExtDetails.get(i).setExtended(true);
					if (tableNames.contains(vas.getTypeCode())) {
						continue;
					}
					tableNames.add(vas.getTypeCode());

					// Table Name identification
					StringBuilder tableName = new StringBuilder();
					tableName.append(VASConsatnts.MODULE_NAME);
					tableName.append("_");
					tableName.append(vas.getTypeCode());
					tableName.append("_ED");

					// Records Deletion from Table
					getExtendedFieldRenderDAO().deleteList(vas.getReference(), tableName.toString(), "_Temp");
				}
				auditDetails.addAll(vasExtDetails);
			}

			// Deleting Finance Insurance Details
			if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
					&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "_Temp");
				//delete from finschedule temp
				for (int i = 0; i < financeDetail.getFinScheduleData().getFinInsuranceList().size(); i++) {
					FinInsurances insurance = financeDetail.getFinScheduleData().getFinInsuranceList().get(i);
					getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "_Temp");
				}
				auditDetails.addAll(details);
			}

			if (financeDetail.getFinanceCollaterals() != null) {
				auditDetails.addAll(getFinCollateralService().delete(financeDetail.getFinanceCollaterals(), "_Temp",
						auditHeader.getAuditTranType()));
			}

			//Finance Flag Details
			if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "_Temp");
				auditDetails.addAll(details);
			}

		}

		if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
			auditDetails.addAll(getFinFeeDetailService().delete(
					financeDetail.getFinScheduleData().getFinFeeDetailActualList(), "_Temp",
					auditHeader.getAuditTranType(), isWIF));
		}

		if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null) {
			auditDetails.addAll(getFinFeeDetailService().deleteFinFeeReceipts(
					financeDetail.getFinScheduleData().getFinFeeReceipts(), "_Temp",
					auditHeader.getAuditTranType()));
		}
		
		auditHeader.setAuditDetails(auditDetails);

		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		//Reset Finance Detail Object for Service Task Verifications
		//=======================================
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Update Task_log and Task_Owners tables
		//=======================================
		updateTaskLog(financeMain, false);

		// Send Cancel DDA Registration request to interface
		//==================================================
		getDdaControllerService().cancelDDARegistration(financeMain.getFinReference());

		getFinMandateService().doRejct(financeDetail, auditHeader);

		// Send Collateral DeMark request to interface
		//==================================================
		/**
		 * Check whether Collateral Marked or not if Marked then send DeMark collateral request to interface
		 * 
		 */
		if (financeDetail.getFinanceCollaterals() != null) {
			getCollateralMarkProcess().deMarkCollateral(financeDetail.getFinanceCollaterals());
		}

		// send Cancel Reservation Request to ACP Interface and save log details
		//======================================================================
		//Maker limit unblock should happen in the origination only
		if (StringUtils.isEmpty(financeMain.getRcdMaintainSts())) {
			if (ImplementationConstants.LIMIT_INTERNAL) {
				getLimitManagement().processLoanLimitOrgination(financeDetail, false, LimitConstants.UNBLOCK,false);
			} else {
				getLimitCheckDetails().doProcessLimits(financeMain, FinanceConstants.CANCEL_RESERVE);
			}
		} else if (StringUtils.equals(financeMain.getRcdMaintainSts(), FinanceConstants.FINSER_EVENT_ADDDISB)) {
			if (ImplementationConstants.LIMIT_INTERNAL) {
				getLimitManagement().processLoanDisbursments(financeDetail, false, LimitConstants.CANCIL,false);
			}
		}

		logger.debug("Leaving");
		return auditHeader;
	}

	private void doCheckCommodityInventory(FinanceDetail financeDetail) {
		//Cancel Finance Commodity Inventory details
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean isWIF) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF);

		String auditTranType = auditHeader.getAuditTranType();
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String usrLanguage = financeMain.getUserDetails().getUsrLanguage();

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		if (!isWIF) {
			auditHeader = getAuditDetails(auditHeader, method);
		}

		// Finance Insurance  Details
		//=======================================
		if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
				&& financeDetail.getFinScheduleData().getFinInsuranceList().size() > 0) {
			financeDetail.getAuditDetailMap().put("FinInsuranceDetails",
					setInsuranceDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(financeDetail.getAuditDetailMap().get("FinInsuranceDetails"));
		}
		// Finance vas recording
		if (financeDetail.getFinScheduleData().getVasRecordingList() != null
				&& !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
			financeDetail.getAuditDetailMap().put("VasRecordings", setVasAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(financeDetail.getAuditDetailMap().get("VasRecordings"));

			financeDetail.getAuditDetailMap().put("VasExtendedDetails", setVasExtendedAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(financeDetail.getAuditDetailMap().get("VasExtendedDetails"));
		}
		
		if (!isWIF && !financeDetail.isExtSource()) {

			String rcdType = financeMain.getRecordType();
			if (!financeDetail.isLovDescIsQDE() && rcdType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				//Customer Details Validation
				//=======================================
				if (financeDetail.getCustomerDetails() != null) {
					financeDetail.getCustomerDetails().setUserDetails(financeDetail.getUserDetails());
					auditDetails.addAll(getCustomerDetailsService().validate(financeDetail.getCustomerDetails(),
							financeMain.getWorkflowId(), method, usrLanguage));
				}

				// Contractor Asset Details
				//=======================================
				List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
				if (contractorAssetDetails != null && !contractorAssetDetails.isEmpty()) {
					auditDetails.addAll(getContractorAssetDetailService().validate(financeDetail, method, usrLanguage));
				}
			}

			// Finance Check List Details
			//=======================================
			List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(getCheckListDetailService().validate(
						financeDetail.getAuditDetailMap().get("checkListDetails"), method, usrLanguage));
			}

			// Guaranteer Details Validation
			//=======================================
			List<GuarantorDetail> gurantorsDetailList = financeDetail.getGurantorsDetailList();
			if (gurantorsDetailList != null && !gurantorsDetailList.isEmpty()) {
				auditDetails.addAll(getGuarantorDetailService().validate(gurantorsDetailList,
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

			// Joint Account Details Validation
			//=======================================
			List<JointAccountDetail> jountAccountDetailList = financeDetail.getJountAccountDetailList();
			if (jountAccountDetailList != null && !jountAccountDetailList.isEmpty()) {
				auditDetails.addAll(getJointAccountDetailService().validate(jountAccountDetailList,
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}
			
			FinanceTaxDetail taxDetail = financeDetail.getTaxDetail();
			
			if (taxDetail != null) {
				if (!financeDetail.isActionSave()) {
					long custId = taxDetail.getTaxCustId();
					String taxNumber = taxDetail.getTaxNumber();
					boolean idExist = false;
					
					if (custId != 0) {
						//GST Number Validation
						if(StringUtils.isNotBlank(taxNumber)) {
							getFinanceTaxDetailService().gstNumbeValidation(auditDetails.get(0), taxDetail);
						}
					
						if (PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(taxDetail.getApplicableFor())) {
							for (JointAccountDetail jointAccountDetail : jountAccountDetailList) {
								if (jointAccountDetail.getCustID() == custId && !(StringUtils
										.equals(PennantConstants.RECORD_TYPE_DEL, jointAccountDetail.getRecordType())
										|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, jointAccountDetail.getRecordType()))) {
									idExist = true;
									break;
								}
							}
							String[] errParm = new String[1];
							String[] valueParm = new String[1];
							valueParm[0] = taxDetail.getCustCIF();
							errParm[0] = valueParm[0];
							
							if (!idExist) {	//if Co-Applicant is not available
								auditDetails.get(0).setErrorDetail(ErrorUtil.getErrorDetail(
										new ErrorDetails(PennantConstants.KEY_FIELD, "65021", errParm, valueParm), usrLanguage));
							}
						} else if (PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(taxDetail.getApplicableFor())) {
							for (GuarantorDetail guarantorDetail : gurantorsDetailList) {
								if (guarantorDetail.getCustID() == custId && !(StringUtils
										.equals(PennantConstants.RECORD_TYPE_DEL, guarantorDetail.getRecordType())
										|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, guarantorDetail.getRecordType()))) {
									idExist = true;
									break;
								}
							}

							String[] errParm = new String[1];
							String[] valueParm = new String[1];
							valueParm[0] = taxDetail.getCustCIF();
							errParm[0] = valueParm[0];
							
							if (!idExist) {	//if Guarantor is not available
								auditDetails.get(0).setErrorDetail(ErrorUtil.getErrorDetail(
										new ErrorDetails(PennantConstants.KEY_FIELD, "65022", errParm, valueParm), usrLanguage));
							}
						} 
					}
				}
			}

			// set Finance Collateral Details Audit
			//=======================================
			if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
				auditDetails.addAll(getFinCollateralService().validate(financeDetail.getFinanceCollaterals(),
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

			//Etihad Credit Bureau details
			//=======================================
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				financeDetail.getEtihadCreditBureauDetail().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getEtihadCreditBureauDetailService().validate(
						financeDetail.getEtihadCreditBureauDetail(), method, auditTranType, usrLanguage));
			}

			//Bundled Products details
			//=======================================
			if (financeDetail.getBundledProductsDetail() != null) {
				financeDetail.getBundledProductsDetail().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getBundledProductsDetailService().validate(financeDetail.getBundledProductsDetail(),
						method, auditTranType, usrLanguage));
			}
			//Agreement Field details
			//=======================================
			if (financeDetail.getAgreementFieldDetails() != null) {
				financeDetail.getAgreementFieldDetails().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getAgreementFieldsDetailService().validate(financeDetail.getAgreementFieldDetails(),
						method, auditTranType, usrLanguage));
			}

			//Advance Payment details
			//=======================================
			if (financeDetail.getAdvancePaymentsList() != null) {
				auditDetails.addAll(getFinAdvancePaymentsService().validate(financeDetail.getAdvancePaymentsList(),
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

			//Covenant Type details
			//=======================================
			if (financeDetail.getCovenantTypeList() != null) {
				auditDetails.addAll(getFinCovenantTypeService().validate(financeDetail.getCovenantTypeList(),
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

			//Collateral Assignments details
			//=======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {

				//Collateral Assignments Validation
				List<CollateralAssignment> assignments = financeDetail.getCollateralAssignmentList();
				if (assignments != null && !assignments.isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					details = getCollateralAssignmentValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}

			//FinAssetType Detail
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {

				// FinAssetType Validation
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				if (details != null && !details.isEmpty()) {
					details = getFinAssetTypesValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}
			//Extended field details Validation
			if (financeDetail.getExtendedFieldRenderList() != null
					&& !financeDetail.getExtendedFieldRenderList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

				if (details != null && !details.isEmpty()) {
					for (AuditDetail assetDetail : details) {
						ExtendedFieldRender aExetendedFieldRender = (ExtendedFieldRender) assetDetail.getModelData();
						StringBuilder tableName = new StringBuilder();
						tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
						tableName.append("_");
						tableName.append(aExetendedFieldRender.getTypeCode());
						tableName.append("_ED");
						assetDetail = getExtendedFieldDetailsValidation().validate(assetDetail, method, usrLanguage,
								tableName.toString());
						auditDetails.add(assetDetail);
					}
				}
			}

			//Asset Evaluation details
			//=======================================
			if (financeDetail.getFinAssetEvaluation() != null) {
				financeDetail.getFinAssetEvaluation().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getFinAssetEvaluationService().validate(financeDetail.getFinAssetEvaluation(), method,
						auditTranType, usrLanguage));
			}

			//Vas Recording Details
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null && !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				if (details != null && !details.isEmpty()) {
					details = getVasRecordingValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}

				//Extended field details Validation
				List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
				if (vasExtDetails != null && !vasExtDetails.isEmpty()) {
					for (AuditDetail assetDetail : vasExtDetails) {
						ExtendedFieldRender aExetendedFieldRender = (ExtendedFieldRender) assetDetail.getModelData();
						StringBuilder tableName = new StringBuilder();
						tableName.append(VASConsatnts.MODULE_NAME);
						tableName.append("_");
						tableName.append(aExetendedFieldRender.getTypeCode());
						tableName.append("_ED");
						assetDetail = getExtendedFieldDetailsValidation().validate(assetDetail, method, usrLanguage,
								tableName.toString());
						auditDetails.add(assetDetail);
					}
				}
			}

			// Finance Flag details Validation 
			List<FinFlagsDetail> finFlagsDetailList = financeDetail.getFinFlagsDetails();
			if (finFlagsDetailList != null && !finFlagsDetailList.isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				details = getFlagDetailValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}

		//Finance Fee details
		if (!financeDetail.isExtSource()) {
			if (financeDetail.getFinScheduleData().getFinFeeDetailList() != null) {
				auditDetails.addAll(getFinFeeDetailService().validate(
						financeDetail.getFinScheduleData().getFinFeeDetailActualList(), financeMain.getWorkflowId(),
						method, auditTranType, usrLanguage, isWIF));
			}
		}
		
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {
			// Finance Fee Receipts
			if (financeDetail.getFinScheduleData().getFinFeeReceipts() == null) {
				financeDetail.getFinScheduleData().setFinFeeReceipts(new ArrayList<FinFeeReceipt>());
			}
			auditDetails.addAll(getFinFeeDetailService().validateFinFeeReceipts(financeDetail,
					financeMain.getWorkflowId(), method, auditTranType, usrLanguage, auditDetails));
		}
		
		//Finance Insurance details
		//=======================================
		if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
				&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
			List<FinInsurances> finInsurance = financeDetail.getFinScheduleData().getFinInsuranceList();

			if (finInsurance != null && !finInsurance.isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				details = getFinInsuranceValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean isWIF) {
		logger.debug("Entering");

		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];

		// Checking , if Customer is in EOD process or not. if Yes, not allowed to do an action
		if (!StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, financeMain.getRecordType())) {
			int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

			// If Customer Exists in EOD Processing, Not allowed to Maintenance till completion
			if (eodProgressCount > 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
						"60203", errParm, valueParm), usrLanguage));
			}
		}

		if (auditDetail.getErrorDetails() == null || auditDetail.getErrorDetails().isEmpty()) {
			if (!isWIF && !method.equals(PennantConstants.method_doReject)
					&& !financeMain.getRecordStatus().contains(PennantConstants.RCD_STATUS_RESUBMITTED)
					&& !financeMain.getRecordStatus().contains(PennantConstants.RCD_STATUS_SAVED)
					&& !financeMain.getRecordStatus().equals(PennantConstants.RCD_STATUS_DECLINED)) {

				// Eligibility Details
				//=======================================
				getEligibilityDetailService().validate(financeDetail.getElgRuleList(), auditDetail, errParm, valueParm,
						usrLanguage);

				// Scoring Details
				//=======================================
				getScoringDetailService().validate(financeDetail, auditDetail, errParm, valueParm, usrLanguage);

				// Collateral Details
				if (financeDetail.getFinScheduleData().getFinanceType().isFinCollateralReq()
						&& ImplementationConstants.COLLATERAL_INTERNAL
						&& financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					BigDecimal totAssignAmt = BigDecimal.ZERO;
					for (CollateralAssignment collateralAssignment : financeDetail.getCollateralAssignmentList()) {
						totAssignAmt = totAssignAmt.add(collateralAssignment.getCollateralValue()
								.multiply(collateralAssignment.getAssignPerc())
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
					}
					if (totAssignAmt.compareTo(financeMain.getFinAmount()) < 0) {
						int foramtter = CurrencyUtil.getFormat(financeMain.getFinCcy());
						String[] errParmCollateral = new String[2];
						String[] valueParmCollateral = new String[2];
						valueParmCollateral[0] = String.valueOf(PennantApplicationUtil.amountFormate(totAssignAmt,
								foramtter));
						valueParmCollateral[1] = String.valueOf(PennantApplicationUtil.amountFormate(
								financeMain.getFinAmount(), foramtter));
						errParmCollateral[0] = PennantJavaUtil.getLabel("label_CollateralAmount") + ":"
								+ valueParmCollateral[0];
						errParmCollateral[1] = PennantJavaUtil.getLabel("label_FinanceAmount") + ":"
								+ valueParmCollateral[1];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "65012", errParmCollateral, valueParmCollateral),
								usrLanguage));
					}
				}
			}

			//Finance Insurance Validation
			//validate the Insurances against the finance Type having list of configured insurances
			List<String> mandPolicyList;
			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				mandPolicyList = getFinTypeInsuranceDAO().getFinTypeInsurances(financeMain.getPromotionCode(),
						FinanceConstants.MODULEID_PROMOTION);
			} else {
				mandPolicyList = getFinTypeInsuranceDAO().getFinTypeInsurances(financeMain.getFinType(),
						FinanceConstants.MODULEID_FINTYPE);
			}

			if (mandPolicyList != null && !mandPolicyList.isEmpty()) {

				boolean notValidEntry = false;
				String mandPolicy = "";
				for (int i = 0; i < mandPolicyList.size(); i++) {

					List<FinInsurances> finInsurances = financeDetail.getFinScheduleData().getFinInsuranceList();
					mandPolicy = mandPolicyList.get(i);
					if (finInsurances != null && !finInsurances.isEmpty()) {

						boolean isMandRcdFound = false;
						for (int j = 0; j < finInsurances.size(); j++) {
							FinInsurances finInsurance = finInsurances.get(j);
							if (StringUtils.equals(finInsurance.getPolicyCode(), mandPolicy)) {
								isMandRcdFound = true;
								break;
							}
						}

						if (!isMandRcdFound) {
							notValidEntry = true;
							break;
						}

					} else {
						notValidEntry = true;
						break;
					}
				}

				if (notValidEntry) {

					String[] errParmInsurance = new String[2];
					String[] valueParmInsurace = new String[2];
					valueParmInsurace[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
					valueParmInsurace[1] = mandPolicy;

					errParmInsurance[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParmInsurace[0];
					errParmInsurance[1] = PennantJavaUtil.getLabel("label_InsurancePolicy") + ":"
							+ valueParmInsurace[1];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
							"91132", errParmInsurance, valueParmInsurace), usrLanguage));
				}
			}
		}

		getFinMandateService().validateMandate(auditDetail, financeDetail);
		if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API) || (financeMain.isQuickDisb()|| !financeDetail.isStp())) {
			getFinMandateService().promptMandate(auditDetail, financeDetail);
		}

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(method) && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			auditDetail.setBefImage(getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", isWIF));
		}

		return auditDetail;
	}

	
	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Flag details
		if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
			auditDetailMap.put("FinFlagsDetail", setFinFlagAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsDetail"));
		}

		if (!financeDetail.isExtSource()) {

			//Finance Contribution Details
			//=======================================
			if (financeDetail.getFinContributorHeader() != null
					&& financeDetail.getFinContributorHeader().getContributorDetailList() != null
					&& financeDetail.getFinContributorHeader().getContributorDetailList().size() > 0) {
				auditDetailMap.put("Contributor",
						setContributorAuditData(financeDetail.getFinContributorHeader(), auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("Contributor"));
			}

			//Finance Document Details
			//=======================================
			if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
				auditDetailMap
						.put("DocumentDetails", setDocumentDetailsAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
			}

			//Finance Check List Details 
			//=======================================
			List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

			if (StringUtils.equals(method, "saveOrUpdate")) {
				if (financeCheckList != null && !financeCheckList.isEmpty()) {
					auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
							auditTranType, method));
				}
			} else {
				String tableType = "_Temp";
				if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType()
						.equals(PennantConstants.RECORD_TYPE_DEL)) {
					tableType = "";
				}

				String finReference = financeDetail.getFinScheduleData().getFinReference();
				financeCheckList = getCheckListDetailService().getCheckListByFinRef(finReference, tableType);
				financeDetail.setFinanceCheckList(financeCheckList);

				if (financeCheckList != null && !financeCheckList.isEmpty()) {
					auditDetails.addAll(getCheckListDetailService().getAuditDetail(auditDetailMap, financeDetail,
							auditTranType, method));
				}
			}
		}

		// Collateral Assignment Details
		//=======================================
		if (financeDetail.getCollateralAssignmentList() != null
				&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
			auditDetailMap.put("CollateralAssignments",
					setCollateralAssignmentAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CollateralAssignments"));
		}
		//FinAssetTypes Details
		if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
			auditDetailMap.put("FinAssetTypes", setFinAssetTypesAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinAssetTypes"));
		}

		// Asset Type Extended Field Details
		if (financeDetail.getExtendedFieldRenderList() != null && !financeDetail.getExtendedFieldRenderList().isEmpty()) {
			auditDetailMap
					.put("ExtendedFieldDetails", setExtendedFieldsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	/**
	 * Method For Preparing List of AuditDetails for Extended Field Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> setExtendedFieldsAuditData(FinanceDetail financeDetail, String auditTranType,
			String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		for (int i = 0; i < financeDetail.getExtendedFieldRenderList().size(); i++) {
			ExtendedFieldRender extendedFieldRender = financeDetail.getExtendedFieldRenderList().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(extendedFieldRender.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			extendedFieldRender.setReference(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (extendedFieldRender.isWorkflow()) {
					isRcdType = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				extendedFieldRender.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			// Audit Details Preparation
			HashMap<String, Object> auditMapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			auditMapValues.put("Reference", extendedFieldRender.getReference());
			auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
			auditMapValues.put("Version", extendedFieldRender.getVersion());
			auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
			auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
			auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
			auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
			auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
			auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
			auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
			auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
			auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			extendedFieldRender.setAuditMapValues(auditMapValues);

			String[] fields = PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender);
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], extendedFieldRender
					.getBefImage(), extendedFieldRender));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details for DocumentDetails
	 * 
	 * @param vasRecording
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setVasAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		VASRecording vasRecording = new VASRecording();
		String[] fields = PennantJavaUtil.getFieldDetails(vasRecording, vasRecording.getExcludeFields());
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < financeDetail.getFinScheduleData().getVasRecordingList().size(); i++) {
			VASRecording recording = financeDetail.getFinScheduleData().getVasRecordingList().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(recording.getRecordType()))) {
				continue;
			}

			recording.setPrimaryLinkRef(financeMain.getFinReference());
			recording.setWorkflowId(financeMain.getWorkflowId());
			boolean isRcdType = false;

			if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				recording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				recording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeMain.isWorkflow()) {
					isRcdType = true;
				}
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				recording.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				recording.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			recording.setRecordStatus(financeMain.getRecordStatus());
			recording.setUserDetails(financeMain.getUserDetails());
			recording.setLastMntOn(financeMain.getLastMntOn());
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], recording.getBefImage(),
					recording));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method For Preparing List of AuditDetails for Extended Field Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @return
	 */
	private List<AuditDetail> setVasExtendedAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		for (int i = 0; i < financeDetail.getFinScheduleData().getVasRecordingList().size(); i++) {

			VASRecording recording = financeDetail.getFinScheduleData().getVasRecordingList().get(i);
			ExtendedFieldRender extendedFieldRender = recording.getExtendedFieldRender();
			extendedFieldRender.setTypeCode(recording.getProductCode());
			extendedFieldRender.setTypeCodeDesc(recording.getProductDesc());

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(extendedFieldRender.getRecordType()))) {
				continue;
			}
			boolean isRcdType = false;

			extendedFieldRender.setReference(recording.getVasReference());
			extendedFieldRender.setWorkflowId(financeMain.getWorkflowId());
			if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeMain.isWorkflow()) {
					isRcdType = true;
				}
			} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				extendedFieldRender.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				extendedFieldRender.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| extendedFieldRender.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			recording.setRecordStatus(financeMain.getRecordStatus());
			recording.setUserDetails(financeMain.getUserDetails());
			recording.setLastMntOn(financeMain.getLastMntOn());

			// Audit Details Preparation
			HashMap<String, Object> auditMapValues = (HashMap<String, Object>) extendedFieldRender.getMapValues();
			auditMapValues.put("Reference", extendedFieldRender.getReference());
			auditMapValues.put("SeqNo", extendedFieldRender.getSeqNo());
			auditMapValues.put("Version", extendedFieldRender.getVersion());
			auditMapValues.put("LastMntOn", extendedFieldRender.getLastMntOn());
			auditMapValues.put("LastMntBy", extendedFieldRender.getLastMntBy());
			auditMapValues.put("RecordStatus", extendedFieldRender.getRecordStatus());
			auditMapValues.put("RoleCode", extendedFieldRender.getRoleCode());
			auditMapValues.put("NextRoleCode", extendedFieldRender.getNextRoleCode());
			auditMapValues.put("TaskId", extendedFieldRender.getTaskId());
			auditMapValues.put("NextTaskId", extendedFieldRender.getNextTaskId());
			auditMapValues.put("RecordType", extendedFieldRender.getRecordType());
			auditMapValues.put("WorkflowId", extendedFieldRender.getWorkflowId());
			extendedFieldRender.setAuditMapValues(auditMapValues);

			String[] fields = PennantJavaUtil.getExtendedFieldDetails(extendedFieldRender);
			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], extendedFieldRender
					.getBefImage(), extendedFieldRender));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List Finance Flag of Audit Details with detailed fields
	 * 
	 * @param financeDetail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinFlagAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinFlagsDetails().size(); i++) {

			FinFlagsDetail finFlagsDetail = financeDetail.getFinFlagsDetails().get(i);
			boolean isRcdType = false;

			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finFlagsDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFlagsDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			if (StringUtils.isNotEmpty(finFlagsDetail.getRecordType())) {
				String[] fields = PennantJavaUtil.getFieldDetails(new FinFlagsDetail(),
						finFlagsDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], finFlagsDetail
						.getBefImage(), finFlagsDetail));
			}
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Method for Rate changes for IJARAH Finances by Applying Actual rates
	 * 
	 * @param bulkRateChangeFinances
	 * @param fromDate
	 * @param toDate
	 * @param recalType
	 * @param rateChange
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws DatatypeConfigurationException 
	 * @throws AccountNotFoundException
	 */
	@Override
	public boolean bulkRateChangeFinances(List<BulkProcessDetails> bulkRateChangeFinances, String recalType,
			BigDecimal rateChange) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		//Bulk Rate Changes applied for fetched list
		for (BulkProcessDetails rateChangeFinance : bulkRateChangeFinances) {

			//Get Total Finance Details to particular Finance
			FinanceDetail financeDetail = getFinSchdDetailById(rateChangeFinance.getFinReference(), "_AView", true);

			//Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setEventFromDate(rateChangeFinance.getLovDescEventFromDate());
			financeMain.setEventToDate(rateChangeFinance.getLovDescEventToDate());
			financeMain.setRecalType(recalType);

			//Schedule Details Log Maintenance
			final List<FinanceScheduleDetail> oldScheduleDetails = financeDetail.getFinScheduleData()
					.getFinanceScheduleDetails();
			getFinanceScheduleDetailDAO().saveList(oldScheduleDetails, "_Log", false);
			
			//Schedule Re-calculation based on Applied parameters
			financeDetail.setFinScheduleData(ScheduleCalculator.changeRate(financeDetail.getFinScheduleData(), "", "",
					BigDecimal.ZERO, rateChange == null ? BigDecimal.ZERO : rateChange, true));

			//Record proceed through WorkFlow defined Process
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(),
					financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null,
					null, auditDetail, financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());

			//Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}

		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for Processing Bulk Finance for Deferment Process
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws DatatypeConfigurationException 
	 * 
	 * @throws AccountNotFoundException
	 */
	@Override
	public boolean bulkDefermentChanges(List<BulkDefermentChange> defermentChangeFinances, String recalType,
			boolean excludeDeferment, String addTermAfter, Date calFromDate, Date calToDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		//Bulk Deferment Changes applied for fetched list
		for (BulkDefermentChange defermentFinance : defermentChangeFinances) {

			//Get Total Finance Details to particular Finance
			FinanceDetail financeDetail = getFinSchdDetailById(defermentFinance.getFinReference(), "_AView", true);

			//Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setEventFromDate(defermentFinance.getEventFromDate());
			financeMain.setEventToDate(defermentFinance.getEventFromDate());
			financeMain.setRecalType(recalType);
			financeMain.setRecalFromDate(calFromDate);
			financeMain.setRecalToDate(calToDate);

			//Schedule Details Log Maintenance
			final List<FinanceScheduleDetail> oldScheduleDetails = befImage.getFinScheduleData()
					.getFinanceScheduleDetails();
			getFinanceScheduleDetailDAO().saveList(oldScheduleDetails, "_Log", false);

			//Record proceed through WorkFlow defined Process
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(),
					financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null,
					null, auditDetail, financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetails>>());

			//Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 */
	@Override
	public CustomerEligibilityCheck getCustEligibilityDetail(Customer customer, String productCode,
			String finReference, String finCcy, BigDecimal curFinRpyAmount, int months, BigDecimal custDSR,
			List<JointAccountDetail> jointAccountDetails) {
		logger.debug("Entering");

		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		if (customer != null) {

			// Eligibility object
			String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
			BeanUtils.copyProperties(customer, eligibilityCheck);

			if (customer.getCustDOB() != null) {
				int dobMonths = DateUtility.getMonthsBetween(customer.getCustDOB(), DateUtility.getAppDate());
				BigDecimal age = new BigDecimal((dobMonths / 12) + "." + (dobMonths % 12));
				eligibilityCheck.setCustAge(age);
				//Minor Age Calculation
				int minorAge = SysParamUtil.getValueAsInt("MINOR_AGE");
				if (age.compareTo(BigDecimal.valueOf(minorAge)) < 0) {
					eligibilityCheck.setCustIsMinor(true);
				} else {
					eligibilityCheck.setCustIsMinor(false);
				}

			}

			Currency finCurrency = null;
			// Customer Total Income & Expense Conversion
			if (StringUtils.isNotBlank(customer.getCustBaseCcy()) && !dftCcy.equals(customer.getCustBaseCcy())) {
				finCurrency = CurrencyUtil.getCurrencyObject(customer.getCustBaseCcy());
				eligibilityCheck.setCustTotalIncome(calculateExchangeRate(customer.getCustTotalIncome(), finCurrency));
				eligibilityCheck
						.setCustTotalExpense(calculateExchangeRate(customer.getCustTotalExpense(), finCurrency));
			}

			if (months > 0) {
				eligibilityCheck.setTenure(new BigDecimal((months / 12) + "." + (months % 12)));
			}

			Date curBussDate = DateUtility.getAppDate();
			eligibilityCheck.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate,
					customer.getCustBlackListDate()));

			eligibilityCheck.setCustCtgCode(customer.getCustCtgCode());
			eligibilityCheck.setReqProduct(productCode);

			//Currently
			if (curFinRpyAmount != null && curFinRpyAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (!StringUtils.equals(dftCcy, finCcy)) {
					if (finCurrency != null && finCurrency.getCcyCode().equals(finCcy)) {
						eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
					} else {
						finCurrency = CurrencyUtil.getCurrencyObject(finCcy);
						eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
					}
				} else {
					eligibilityCheck.setCurFinRepayAmt(curFinRpyAmount);
				}
			}

			//Finance Amount Calculations
			List<FinanceProfitDetail> financeProfitDetailsList = getCustomerDAO().getCustFinAmtDetails(
					customer.getCustID(), eligibilityCheck);

			BigDecimal custFinAmount = BigDecimal.ZERO;
			BigDecimal custODAmount = BigDecimal.ZERO;

			for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
				custFinAmount = custFinAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(),
						finCcy, financeProfitDetail.getTotalPriBal()));
				custODAmount = custODAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(),
						finCcy, financeProfitDetail.getODPrincipal()));
			}

			eligibilityCheck.setCustLiveFinAmount(custFinAmount);
			eligibilityCheck.setCustPastDueAmt(custODAmount);

			//set Customer Designation if customer status is Employed
			eligibilityCheck.setCustEmpSts(customer.getCustEmpSts());

			//Get Customer Repay Totals On Bank
			eligibilityCheck.setCustRepayBank(getCustomerDAO().getCustRepayBankTotal(customer.getCustID()));

			// Customer Current Processing Finance Amounts
			if (StringUtils.isNotEmpty(finReference)) {
				eligibilityCheck.setCustProcRepayBank(getCustomerDAO().getCustRepayProcBank(customer.getCustID(),
						finReference));
			}

			//Get Co-Applicants Repay Totals On Bank
			BigDecimal totalCoAppRepayBank = BigDecimal.ZERO;
			BigDecimal totalCoAppIncome = BigDecimal.ZERO;
			BigDecimal totalCoAppExpense = BigDecimal.ZERO;
			BigDecimal totalCoAppCurFinEMI = BigDecimal.ZERO;
			if (jointAccountDetails != null && !jointAccountDetails.isEmpty()) {
				for (JointAccountDetail accountDetail : jointAccountDetails) {
					FinanceExposure exposure = getCustomerDAO().getCoAppRepayBankTotal(accountDetail.getCustCIF());
					if (exposure == null) {
						continue;
					}
					totalCoAppRepayBank = totalCoAppRepayBank.add(PennantApplicationUtil.unFormateAmount(
							exposure.getCurrentExpoSure(),
							SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));

					totalCoAppIncome = totalCoAppIncome.add(CalculationUtil.getConvertedAmount(exposure.getFinCCY(),
							finCcy, exposure.getFinanceAmt()));
					totalCoAppExpense = totalCoAppExpense.add(CalculationUtil.getConvertedAmount(exposure.getFinCCY(),
							finCcy, exposure.getOverdueAmt()));
					BigDecimal curFinEMI = CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy,
							exposure.getCurrentExpoSureinBaseCCY());
					totalCoAppCurFinEMI = totalCoAppCurFinEMI.add(PennantApplicationUtil.unFormateAmount(curFinEMI,
							SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
				}
			}
			eligibilityCheck.setCoAppRepayBank(totalCoAppRepayBank);
			eligibilityCheck.setCoAppIncome(totalCoAppIncome);
			eligibilityCheck.setCoAppExpense(totalCoAppExpense);
			eligibilityCheck.setCoAppCurFinEMI(totalCoAppCurFinEMI);

			//Get Customer Repay Totals by Other Commitments
			eligibilityCheck.setCustRepayOther(BigDecimal.ZERO);//getCustomerDAO().getCustRepayOtherTotal(customer.getCustID())

			//Get Customer Worst Status From Finances
			eligibilityCheck.setCustWorstSts(getCustomerDAO().getCustWorstSts(customer.getCustID()));

			//FIXME to be Removed
			//DSR Calculation
			/*
			 * Rule rule = getRuleDAO().getRuleByID(RuleConstants.ELGRULE_DSRCAL, RuleConstants.MODULE_ELGRULE,
			 * RuleConstants.EVENT_ELGRULE, ""); if (rule != null) { Object dscr =
			 * getRuleExecutionUtil().executeRule(rule.getSQLRule(), eligibilityCheck.getDeclaredFieldValues(), finCcy,
			 * RuleReturnType.DECIMAL); eligibilityCheck.setDSCR(PennantApplicationUtil.getDSR(dscr)); }
			 */
		}
		logger.debug("Leaving");
		return eligibilityCheck;
	}

	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public CustomerEligibilityCheck getWIFCustEligibilityDetail(WIFCustomer customer, String finCcy)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		if (customer != null) {

			eligibilityCheck.setCustCtgCode(customer.getCustCtgCode());

			// Eligibility object
			org.apache.commons.beanutils.BeanUtils.copyProperties(eligibilityCheck, customer);
			int dobMonths = DateUtility.getMonthsBetween(customer.getCustDOB(), DateUtility.getAppDate());
			BigDecimal age = new BigDecimal((dobMonths / 12) + "." + (dobMonths % 12));
			eligibilityCheck.setCustAge(age);

			//Minor Age Calculation

			int minorAge = SysParamUtil.getValueAsInt("MINOR_AGE");
			if (age.compareTo(BigDecimal.valueOf(minorAge)) < 0) {

				eligibilityCheck.setCustIsMinor(true);
			} else {
				eligibilityCheck.setCustIsMinor(false);
			}

			Date curBussDate = DateUtility.getAppDate();
			eligibilityCheck.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate,
					customer.getCustBlackListDate()));

			if (customer.getExistCustID() != 0) {

				//Finance Amount Calculations
				List<FinanceProfitDetail> financeProfitDetailsList = getCustomerDAO().getCustFinAmtDetails(
						customer.getExistCustID(), eligibilityCheck);

				BigDecimal custFinAmount = BigDecimal.ZERO;
				BigDecimal custODAmount = BigDecimal.ZERO;

				for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
					custFinAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy,
							financeProfitDetail.getTotalPriBal().add(financeProfitDetail.getTotalPftBal())));
					custODAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy,
							financeProfitDetail.getODPrincipal().add(financeProfitDetail.getODProfit())));
				}

				eligibilityCheck.setCustLiveFinAmount(custFinAmount);
				eligibilityCheck.setCustPastDueAmt(custODAmount);

				//get Customer Designation if customer status is Employed
				eligibilityCheck.setCustEmpDesg(getCustomerDAO().getCustEmpDesg(customer.getExistCustID()));
				eligibilityCheck.setCustEmpSts(customer.getCustEmpSts());

				//Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustomerDAO().getCustWorstSts(customer.getExistCustID()));
				eligibilityCheck.setCustTotalIncome(customer.getTotalIncome());
				eligibilityCheck.setCustTotalExpense(customer.getTotalExpense());
				//Get Customer Repay Totals On Bank
				eligibilityCheck.setCustRepayBank(getCustomerDAO().getCustRepayBankTotal(customer.getCustID()));

			} else {

				//Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustStatusByMinDueDays());
			}

			//DSR Calculation
			Rule rule = getRuleDAO().getRuleByID(RuleConstants.ELGRULE_DSRCAL, RuleConstants.MODULE_ELGRULE,
					RuleConstants.EVENT_ELGRULE, "");
			if (rule != null) {
				Object dscr = getRuleExecutionUtil().executeRule(rule.getSQLRule(),
						eligibilityCheck.getDeclaredFieldValues(), finCcy, RuleReturnType.DECIMAL);
				eligibilityCheck.setDSCR(PennantApplicationUtil.getDSR(dscr));
			}
		}
		logger.debug("Leaving");
		return eligibilityCheck;
	}

	/**
	 * Method for Deletion of Contributor Details
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	private List<AuditDetail> listDeletion_FinContributor(FinanceDetail finDetail, String tableType,
			String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (finDetail.getFinContributorHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader(), "");
			FinContributorHeader contributorHeader = finDetail.getFinContributorHeader();
			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], contributorHeader.getBefImage(),
					contributorHeader));

			getFinContributorHeaderDAO().delete(contributorHeader.getFinReference(), tableType);

			String[] fields1 = PennantJavaUtil.getFieldDetails(new FinContributorDetail(), "");
			if (contributorHeader.getContributorDetailList() != null
					&& contributorHeader.getContributorDetailList().size() > 0) {

				for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {
					FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
					auditList.add(new AuditDetail(auditTranType, i + 1, fields1[0], fields1[1], contributorDetail
							.getBefImage(), contributorDetail));
				}
				getFinContributorDetailDAO().deleteByFinRef(
						contributorHeader.getContributorDetailList().get(0).getFinReference(), tableType);
			}
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type, long logKey) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));

		// Fee Details
		finSchData.setFinFeeDetailList(getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, false, "_View"));

		// Finance Fee Schedule Details
		if (finSchData.getFinFeeDetailList() != null && !finSchData.getFinFeeDetailList().isEmpty()) {

			List<Long> feeIDList = new ArrayList<>();
			for (int i = 0; i < finSchData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = finSchData.getFinFeeDetailList().get(i);

				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
					feeIDList.add(feeDetail.getFeeID());
				}
			}

			if (!feeIDList.isEmpty()) {
				List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(
						feeIDList, false, "");

				if (feeScheduleList != null && !feeScheduleList.isEmpty()) {

					HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
					for (int i = 0; i < feeScheduleList.size(); i++) {
						FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(schdFee.getFeeID())) {
							schList = schFeeMap.get(schdFee.getFeeID());
							schFeeMap.remove(schdFee.getFeeID());
						}
						schList.add(schdFee);
						schFeeMap.put(schdFee.getFeeID(), schList);

					}

					for (int i = 0; i < finSchData.getFinFeeDetailList().size(); i++) {
						FinFeeDetail feeDetail = finSchData.getFinFeeDetailList().get(i);
						if (schFeeMap.containsKey(feeDetail.getFeeID())) {
							feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
						}
					}
				}
			}
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finSchData.getFinanceMain().getProductCategory())) {
			String odType = "_Temp";
			if(StringUtils.isBlank(finSchData.getFinanceMain().getRecordType())){
				odType = "";
			}
			finSchData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(
					finReference, odType, false));
		}
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,
				false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		if (logKey == 0) {
			finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(),
					"_AView"));
			finSchData = getFinMaintainenceDetails(finSchData);
			finSchData.setAccrueValue(getAccrueAmount(finReference));
		}
		logger.debug("Leaving");
		return finSchData;
	}

	@Override
	public FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData) {
		logger.debug("Entering");
		String finReference = finSchData.getFinanceMain().getFinReference();
		finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference, false));
		finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finReference));
		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method for Get the Accrue Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public BigDecimal getAccrueAmount(String finReference) {
		return getProfitDetailsDAO().getAccrueAmount(finReference);
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	public FinScheduleData getFinSchDataById(String finReference, String type, boolean summaryRequired) {
		logger.debug("Entering");

		FinScheduleData finSchData = new FinScheduleData();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, type, false);
		if (financeMain == null) {
			return finSchData;
		}

		finSchData.setFinReference(financeMain.getFinReference());
		finSchData.setFinanceMain(financeMain);

		// Overdraft Schedule Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			finSchData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(
					finReference, "_Temp", false));
		}

		// Schedule details
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));

		// Disbursement Details
		finSchData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type,
				false));

		// Repay instructions
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		if (summaryRequired) {

			// Finance Type
			//finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByFinType(financeMain.getFinType()));
			//Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByFinType(financeMain.getFinType());
			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO.getPromotionById(financeMain.getPromotionCode(), "_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			finSchData.setFinanceType(financeType);

			// Suspense
			finSchData.setFinPftSuspended(false);
			FinanceSuspHead financeSuspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finReference, "");
			if (financeSuspHead != null && financeSuspHead.isFinIsInSusp()) {
				finSchData.setFinPftSuspended(true);
				finSchData.setFinSuspDate(financeSuspHead.getFinSuspDate());
			}

			//Finance Summary Details Preparation
			final Date curBussDate = DateUtility.getAppDate();
			FinanceSummary summary = new FinanceSummary();
			summary.setFinReference(financeMain.getFinReference());
			summary.setSchDate(curBussDate);

			if (financeMain.isAllowGrcPeriod() && curBussDate.compareTo(financeMain.getNextGrcPftDate()) <= 0) {
				summary.setNextSchDate(financeMain.getNextGrcPftDate());
			} else if (financeMain.getNextRepayDate().compareTo(financeMain.getNextRepayPftDate()) < 0) {
				summary.setNextSchDate(financeMain.getNextRepayDate());
			} else {
				summary.setNextSchDate(financeMain.getNextRepayPftDate());
			}

			//commented because we are fetching total fees from FinfeeDeatail table
/*			summary = getFinanceScheduleDetailDAO().getFinanceSummaryDetails(summary);
			summary = getFinFeeDetailDAO().getTotalFeeCharges(summary);*/
			summary.setFinCurODDays(getFinODDetailsDAO().getFinODDays(finReference, ""));
			finSchData.setFinanceSummary(summary);

			FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(finReference);
			if (finODDetails != null) {
				summary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
				summary.setFinODTotWaived(finODDetails.getTotWaived());
				summary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
				summary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
			}
		}

		logger.debug("Leaving");
		return finSchData;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 * **/
	@Override
	public FinScheduleData getFinSchDataForReceipt(String finReference, String type) {
		logger.debug("Entering");

		FinScheduleData scheduleData = new FinScheduleData();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, type, false);
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);

		// Schedule details
		scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(
					finReference, "", false));
		}
		
		// Disbursement Details
		scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
				type, false));

		// Repay instructions
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		// Fiannce Type
		scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), type));
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			// Fetching Promotion Details
			Promotion promotion = this.promotionDAO.getPromotionById(financeMain.getPromotionCode(), type);
			scheduleData.getFinanceType().setFInTypeFromPromotiion(promotion);
		}

		// Fee Details
		/*scheduleData.setFinFeeDetailList(getFinFeeDetailDAO().getFinScheduleFees(finReference, false, "_View"));
		
		// Finance Fee Schedule Details
		if (scheduleData.getFinFeeDetailList() != null && !scheduleData.getFinFeeDetailList().isEmpty()) {

			List<Long> feeIDList = new ArrayList<>();
			for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);
				feeDetail.setRcdVisible(false);
				feeIDList.add(feeDetail.getFeeID());
			}

			if(!feeIDList.isEmpty()){
				List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(feeIDList, false, "");

				if(feeScheduleList != null && !feeScheduleList.isEmpty()){
					HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();                        
					for (int i = 0; i < feeScheduleList.size(); i++) {
						FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(schdFee.getFeeID())) {
							schList = schFeeMap.get(schdFee.getFeeID());
							schFeeMap.remove(schdFee.getFeeID());
						}
						schList.add(schdFee);
						schFeeMap.put(schdFee.getFeeID(), schList);

					}

					for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
						FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);
						if (schFeeMap.containsKey(feeDetail.getFeeID())) {
							feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
						}
					}
				}
			}
		}*/
		
		scheduleData.setFinFeeDetailList(getFinFeeDetailDAO().getFinScheduleFees(finReference, false, "_View"));

		// Finance Fee Schedule Details
		if (scheduleData.getFinFeeDetailList() != null && !scheduleData.getFinFeeDetailList().isEmpty()) {

			List<Long> feeIDList = new ArrayList<>();
			for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);

				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
					feeIDList.add(feeDetail.getFeeID());
				}
			}

			if (!feeIDList.isEmpty()) {
				List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(
						feeIDList, false, "");

				if (feeScheduleList != null && !feeScheduleList.isEmpty()) {

					HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
					for (int i = 0; i < feeScheduleList.size(); i++) {
						FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(schdFee.getFeeID())) {
							schList = schFeeMap.get(schdFee.getFeeID());
							schFeeMap.remove(schdFee.getFeeID());
						}
						schList.add(schdFee);
						schFeeMap.put(schdFee.getFeeID(), schList);

					}

					for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) {
						FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i);
						if (schFeeMap.containsKey(feeDetail.getFeeID())) {
							feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
						}
					}
				}
			}
		}

		// Insurance Details
		if (ImplementationConstants.ALLOW_INSURANCE) {

			scheduleData.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, "_AView",
					false));

			// FinSchFrqInsurance Details
			if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {

				List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO().getFinSchFrqInsuranceFinRef(
						finReference, false, "_AView");

				if (finSchFrqInsurances != null && !finSchFrqInsurances.isEmpty()) {

					HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();
					for (int i = 0; i < finSchFrqInsurances.size(); i++) {
						FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

						List<FinSchFrqInsurance> schList = new ArrayList<>();
						if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
							schList = schInsMap.get(finSchFrqInsurance.getInsId());
							schInsMap.remove(finSchFrqInsurance.getInsId());
						}
						schList.add(finSchFrqInsurance);
						schInsMap.put(finSchFrqInsurance.getInsId(), schList);

					}

					for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
						FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
						if (schInsMap.containsKey(finInsurance.getInsId())) {
							finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
						}
					}
				}
			}
		}

		logger.debug("Leaving");
		return scheduleData;
	}

	/**
	 * Method for Fetching Profit Details for Particular Finance Reference
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		return getProfitDetailsDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * Method for Fetching Profit Details
	 */
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		return getFinanceMainDAO().getFinanceProfitDetails(finRef);
	}

	/**
	 * Method for getting Finance Contributor Header Details
	 */
	public FinContributorHeader getFinContributorHeaderById(String finReference) {
		logger.debug("Entering");
		FinContributorHeader header = getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_AView");
		if (header != null) {
			header.setContributorDetailList(getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference,
					"_AView"));
		}
		logger.debug("Leaving");
		return header;
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,
			String postingGroupBy) {
		return getPostingsDAO().getPostingsByFinRefAndEvent(finReference, finEvent, showZeroBal, postingGroupBy);
	}

	/**
	 * Method for fetching list of entries executed based on Linked Transaction ID
	 */
	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid) {
		return getPostingsDAO().getPostingsByLinkTransId(linkedTranid);
	}

	/**
	 * Method to CheckLimits
	 * 
	 * @param AuditHeader
	 * 
	 *            1. Check limit category exists or not for the account type, if not exists set limitValid = true other
	 *            wise goto next step. 2. Fetch customer limits from core banking. 3. If the limits not available set
	 *            the ErrMessage. 4. If available limit is less than finance amount, set warning message if the user
	 *            have the permission 'override Limits' otherwise set Error message.
	 * 
	 * */
	public AuditHeader doCheckLimits(AuditHeader auditHeader) {
		logger.debug("Entering");

		CustomerLimit custLimit = null;
		String[] errParm = new String[2];
		String[] valueParm = new String[2];

		List<CustomerLimit> list = null;

		FinanceDetail finDetails = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = finDetails.getFinScheduleData().getFinanceMain();
		AccountType accountType = getAccountTypeDAO().getAccountTypeById(
				finDetails.getFinScheduleData().getFinanceType().getFinAcType(), "");

		if (StringUtils.isNotBlank(accountType.getAcLmtCategory())) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(financeMain.getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			financeMain.setLimitValid(true);
			auditHeader.getAuditDetail().setModelData(finDetails);
			logger.debug("Leaving");
			return auditHeader;
		}

		try {
			list = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, e.getMessage(), null));
			logger.debug("Exception: ", e);
			logger.debug("Leaving");
			return auditHeader;
		}

		if (list == null || list.size() == 0) {

			valueParm[0] = accountType.getAcLmtCategory();
			errParm[0] = PennantJavaUtil.getLabel("label_LimtCategorys") + ":" + valueParm[0];

			valueParm[1] = financeMain.getLovDescCustCIF();
			errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID") + ":" + valueParm[1];

			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
					new ErrorDetails("Limit", "41002", errParm, valueParm), finDetails.getUserDetails()
							.getUsrLanguage()));
			logger.debug("Leaving");
			return auditHeader;

		} else {

			for (CustomerLimit customerLimit : list) {

				if (customerLimit.getLimitAmount() == null || customerLimit.getLimitAmount().equals(BigDecimal.ZERO)) {

					valueParm[0] = customerLimit.getLimitCategory();
					errParm[0] = PennantJavaUtil.getLabel("label_LimtCategory") + ":" + valueParm[0];
					valueParm[1] = financeMain.getLovDescCustCIF();
					errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID") + ":" + valueParm[1];

					auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit", "41002", errParm,
							valueParm), finDetails.getUserDetails().getUsrLanguage()));

					return auditHeader;

				} else {

					Currency fCurrency = CurrencyUtil.getCurrencyObject(financeMain.getFinCcy());
					BigDecimal finAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);

					Currency lCurrency = null;
					if (!StringUtils.trimToEmpty(customerLimit.getLimitCurrency()).equals(fCurrency.getCcyCode())) {
						lCurrency = CurrencyUtil.getCurrencyObject(customerLimit.getLimitCurrency());
					} else {
						lCurrency = fCurrency;
					}

					BigDecimal availAmount = calculateExchangeRate(customerLimit.getAvailAmount(), lCurrency);

					if (availAmount != null && availAmount.compareTo(finAmount) < 0) {
						int dftFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);

						valueParm[0] = PennantApplicationUtil.amountFormate(financeMain.getFinAmount(),
								lCurrency.getCcyEditField() == 0 ? dftFormatter : lCurrency.getCcyEditField());
						errParm[0] = valueParm[0];

						valueParm[1] = PennantApplicationUtil.amountFormate(customerLimit.getLimitAmount(),
								fCurrency.getCcyEditField() == 0 ? dftFormatter : fCurrency.getCcyEditField());
						errParm[1] = valueParm[1];

						String errorCode = "30532";
						if (finDetails.getFinScheduleData().getFinanceType().isOverrideLimit()) {
							errorCode = "65006";
						}

						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails("Limit", errorCode,
								errParm, valueParm), finDetails.getUserDetails().getUsrLanguage()));
						logger.debug("Leaving");
						return auditHeader;
					}
				}
			}
			financeMain.setLimitValid(true);
			auditHeader.getAuditDetail().setModelData(finDetails);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Checking Black List Abuser data Against Customer Included in Finance
	 * 
	 * @param financeDetail
	 * @return
	 */
	@Override
	public boolean checkExistCustIsBlackListed(long custId) {
		logger.debug("Entering");

		String custCRCPR = getCustomerDAO().getCustCRCPRById(custId, "");
		if (StringUtils.isNotBlank(custCRCPR)) {
			Date blackListDate = getCustomerDAO().getCustBlackListedDate(custCRCPR, "");
			if (blackListDate != null) {
				logger.debug("Leaving");
				return true;
			}
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method for Filling Amounts in USD & BHD Format to FinanceMain Object
	 * 
	 * @param financeMain
	 * @return
	 */
	@Override
	public FinanceMain fetchConvertedAmounts(FinanceMain financeMain, boolean calAllAmounts) {
		logger.debug("Entering");
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		if (calAllAmounts) {
			if (dftCcy.equals(financeMain.getFinCcy())) {
				financeMain.setAmountBD(PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter));
			} else {
				Currency fCurrency = CurrencyUtil.getCurrencyObject(financeMain.getFinCcy());
				BigDecimal actualAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);
				financeMain.setAmountBD(actualAmount);
			}
		}

		if ("USD".equals(financeMain.getFinCcy())) {
			financeMain.setAmountUSD(PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter));
		} else {
			Currency fCurrency = CurrencyUtil.getCurrencyObject("USD");
			BigDecimal actualAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);
			financeMain.setAmountUSD(actualAmount);
		}

		logger.debug("Leaving");
		return financeMain;
	}

	public List<ErrorDetails> getDiscrepancies(FinanceDetail financeDetail) {
		logger.debug("Entering");

		List<ErrorDetails> errorDetails = new ArrayList<ErrorDetails>();

		long oDDays = getFinODDetailsDAO().checkCustPastDue(
				financeDetail.getFinScheduleData().getFinanceMain().getCustID());
		int allowedDays = SysParamUtil.getValueAsInt("MAX_ALLOW_ODDAYS");
		if (oDDays > 0) {
			if (oDDays <= allowedDays) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60201",
						new String[] { String.valueOf(oDDays) }, null), ""));
			} else {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60202",
						new String[] { String.valueOf(oDDays) }, null), ""));
			}
		}

		//Check Limit Status of Customer
		CustomerLimit custLimit = null;
		List<CustomerLimit> customerLimitList = null;
		AccountType accountType = getAccountTypeDAO().getAccountTypeById(
				financeDetail.getFinScheduleData().getFinanceType().getFinAcType(), "");
		if (StringUtils.isNotBlank(accountType.getAcLmtCategory())) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			logger.debug("Leaving");
			return errorDetails;
		}

		try {
			customerLimitList = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			logger.debug("Leaving");
			return errorDetails;
		}

		if (customerLimitList != null && customerLimitList.size() > 0) {
			Date curBussDate = DateUtility.getAppDate();
			String limitType = "";

			BigDecimal finAmount = financeDetail.getFinScheduleData().getFinanceMain().getFinAmount();
			BigDecimal downpaybank = financeDetail.getFinScheduleData().getFinanceMain().getDownPayBank();
			BigDecimal downpaySuppl = financeDetail.getFinScheduleData().getFinanceMain().getDownPaySupl();
			finAmount = finAmount.subtract(downpaybank == null ? BigDecimal.ZERO : downpaybank).subtract(
					downpaySuppl == null ? BigDecimal.ZERO : downpaySuppl);
			BigDecimal calcFinAmount = BigDecimal.ZERO;
			for (CustomerLimit limit : customerLimitList) {
				if (StringUtils.isNotEmpty(limit.getCustCountry())) {
					limitType = " ( " + limit.getCustCountry() + " - " + limit.getCustCountryDesc() + " )";
				} else if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
					limitType = " ( " + limit.getCustGrpCode() + " - " + limit.getCustGrpDesc() + " ) ";
				} else {
					limitType = "Customer";
				}

				if (StringUtils.isEmpty(limit.getLimitCategory())) {
					if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60304",
								new String[] { limitType }, null), ""));
					} else if (StringUtils.isNotEmpty(limit.getCustCountry())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60307",
								new String[] { limitType }, null), ""));
					} else {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60301",
								null, null), ""));
					}
					continue;
				}

				if (limit.getLimitExpiry() != null && limit.getLimitExpiry().compareTo(curBussDate) < 0) {

					if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60305",
								new String[] { limitType, limit.getLimitCategoryDesc() }, null), ""));
					} else if (StringUtils.isNotEmpty(limit.getCustCountry())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60308",
								new String[] { limitType, limit.getLimitCategoryDesc() }, null), ""));
					} else {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "60302",
								new String[] { limit.getLimitCategoryDesc() }, null), ""));
					}
					logger.debug("Leaving");
				}

				if (StringUtils.isNotBlank(limit.getLimitCurrency())) {
					if (!StringUtils.trimToEmpty(limit.getLimitCurrency()).equals(
							financeDetail.getFinScheduleData().getFinanceMain().getFinCcy())) {
						Currency lCurrency = CurrencyUtil.getCurrencyObject(limit.getLimitCurrency());
						Currency fCurrency = CurrencyUtil.getCurrencyObject(financeDetail.getFinScheduleData()
								.getFinanceMain().getFinCcy());
						calcFinAmount = CalculationUtil.getConvertedAmount(fCurrency, lCurrency, finAmount);
					} else {
						calcFinAmount = finAmount;
					}
				}
				BigDecimal excessAmount = limit.getRiskAmount().add(calcFinAmount).subtract(limit.getLimitAmount());
				if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal excessPerc;

					int formatter = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain()
							.getFinCcy());
					;

					if (limit.getLimitAmount().compareTo(BigDecimal.ZERO) == 0) {
						excessPerc = new BigDecimal(100);
					} else {
						excessPerc = excessAmount.multiply(new BigDecimal(100)).divide(limit.getLimitAmount(), 2,
								RoundingMode.HALF_DOWN);
					}

					if (StringUtils.isNotEmpty(limit.getCustCountry())) {
						errorDetails.add(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "60309", new String[] {
										excessPerc.toString(),
										PennantApplicationUtil.amountFormate(excessAmount, formatter), limitType,
										limit.getLimitCategoryDesc() }, null), ""));
					} else if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
						errorDetails.add(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "60306", new String[] {
										excessPerc.toString(),
										PennantApplicationUtil.amountFormate(excessAmount, formatter), limitType,
										limit.getLimitCategoryDesc() }, null), ""));
					} else {
						errorDetails.add(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD, "60303", new String[] {
										excessPerc.toString(),
										PennantApplicationUtil.amountFormate(excessAmount, formatter),
										limit.getLimitCategoryDesc() }, null), ""));
					}
				}
			}
		}
		logger.debug("Leaving");
		return errorDetails;
	}

	/**
	 * Method for Checking exception List based upon Requirements
	 */
	public AuditHeader doCheckExceptions(AuditHeader auditHeader) {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		//Check for Exception 
		aFinanceMain.setException(false);

		//*** Case 1 : Amount Case Check Exception for 100K BHD ***
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		final BigDecimal finAmount = PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(),
				CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
		if (dftCcy.equals(aFinanceMain.getFinCcy())) {
			aFinanceMain.setAmount(finAmount);
		} else {
			//Covert Amount into BHD Format 
			Currency fCurrency = CurrencyUtil.getCurrencyObject(aFinanceMain.getFinCcy());
			aFinanceMain.setAmount(finAmount.multiply(fCurrency.getCcySpotRate()));
		}

		if (aFinanceMain.getAmount().compareTo(BigDecimal.valueOf(100000.000)) > 0) {
			aFinanceMain.setException(true);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		auditHeader.getAuditDetail().setModelData(aFinanceDetail);

		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Method for Calculating Exchange Rate for Fiannce Schedule Calculation
	 * 
	 * @param amount
	 * @param aCurrency
	 * @return
	 */
	private BigDecimal calculateExchangeRate(BigDecimal amount, Currency aCurrency) {
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		if (StringUtils.equals(dftCcy, aCurrency.getCcyCode())) {
			return amount;
		} else {
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}

			amount = amount.multiply(aCurrency.getCcySpotRate());
		}
		return amount;
	}

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingContributorList(List<AuditDetail> auditDetails, String type, String finReference) {
		logger.debug("Entering");

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinContributorDetail contributorDetail = (FinContributorDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				contributorDetail.setRoleCode("");
				contributorDetail.setNextRoleCode("");
				contributorDetail.setTaskId("");
				contributorDetail.setNextTaskId("");
			}

			contributorDetail.setWorkflowId(0);

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (contributorDetail.isNewRecord()) {
				saveRecord = true;
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (contributorDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = contributorDetail.getRecordType();
				recordStatus = contributorDetail.getRecordStatus();
				contributorDetail.setRecordType("");
				contributorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.isBlank(contributorDetail.getFinReference())) {
					contributorDetail.setFinReference(finReference);
				}
				finContributorDetailDAO.save(contributorDetail, type);
			}

			if (updateRecord) {
				finContributorDetailDAO.update(contributorDetail, type);
			}

			if (deleteRecord) {
				finContributorDetailDAO.delete(contributorDetail, type);
			}

			if (approveRec) {
				contributorDetail.setRecordType(rcdType);
				contributorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(contributorDetail);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	@Override
	public void updateCustCIF(long custID, String finReference) {
		getFinanceMainDAO().updateCustCIF(custID, finReference);

	}

	//Document Details List Maintainance
	public void listDocDeletion(FinanceDetail financeDetail, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(financeDetail.getDocumentDetailsList()),
				tableType);
	}

	@Override
	public List<DocumentDetails> getFinDocByFinRef(String finReference, String finEvent, String type) {
		return getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, finEvent,
				type);
	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, "");
	}

	// ******************************************************//
	// ***************** Additional Details *****************//
	// ******************************************************//

	/**
	 * Method for Save/ Update Additional Field Details
	 */
	@SuppressWarnings("deprecation")
	public void doSaveAddlFieldDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");

		if (financeDetail.getLovDescExtendedFieldValues() != null
				&& financeDetail.getLovDescExtendedFieldValues().size() > 0) {

			String tableName = "";
			if (PennantStaticListUtil.getModuleName().containsKey("Finance")) {
				tableName = PennantStaticListUtil.getModuleName().get("Finance")
						.get(financeDetail.getFinScheduleData().getFinanceMain().getFinPurpose());
			}

			if (!getExtendedFieldDetailDAO().isExist(tableName, financeDetail.getFinScheduleData().getFinReference(),
					tableType)) {
				getExtendedFieldDetailDAO().saveAdditional(financeDetail.getFinScheduleData().getFinReference(),
						financeDetail.getLovDescExtendedFieldValues(), tableType, tableName);
			} else {
				getExtendedFieldDetailDAO().updateAdditional(financeDetail.getLovDescExtendedFieldValues(),
						financeDetail.getFinScheduleData().getFinReference(), tableType, tableName);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Delete Additional Field Details
	 */
	@SuppressWarnings("deprecation")
	private void doDeleteAddlFieldDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug("Entering");

		if (financeDetail.getLovDescExtendedFieldValues() != null
				&& financeDetail.getLovDescExtendedFieldValues().size() > 0) {

			String tableName = "";
			if (PennantStaticListUtil.getModuleName().containsKey("Finance")) {
				tableName = PennantStaticListUtil.getModuleName().get("Finance")
						.get(financeDetail.getFinScheduleData().getFinanceMain().getFinPurpose());
			}

			getExtendedFieldDetailDAO().deleteAdditional(financeDetail.getFinScheduleData().getFinReference(),
					tableName, tableType);
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ************* Cust Related Finance Details ***********//
	// ******************************************************//

	@Override
	public FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType, String finType,
			String userRole, String procEdtEvent) {
		logger.debug("Entering");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		//Finance Commitment Accounting Posting Details
		if (PennantConstants.RECORD_TYPE_NEW
				.equals(financeDetail.getFinScheduleData().getFinanceMain().getRecordType())) {
			if (financeDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq()
					&& StringUtils
							.isNotBlank(financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef())) {

				long accountingSetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_CMTDISB,
						AccountEventConstants.ACCEVENT_CMTDISB);
				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(getTransactionEntryDAO().getListTransactionEntryById(
							accountingSetId, "_AEView", true));
				}
			}
		}

		//Finance Stage Accounting Posting Details 
		financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Set Eligibility Details to finaceDetail
		financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(
				financeMain.getFinReference(), financeMain.getFinCcy(), financeMain.getFinAmount(),
				financeMain.isNewRecord(), finType, userRole, procEdtEvent));

		// Set Scoring Details to finaceDetail
		getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, userRole, ctgType, procEdtEvent);

		//Reset Finance Document Details
		String preAppref = StringUtils.trimToEmpty(financeMain.getFinPreApprovedRef());
		if (financeMain.isNewRecord()
				&& ("".equals(preAppref) || preAppref.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL))) {
			financeDetail.setDocumentDetailsList(new ArrayList<DocumentDetails>(1));
		}

		//Set Check List Details to finaceDetail 
		getCheckListDetailService().setFinanceCheckListDetails(financeDetail, finType,
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent, userRole);

		logger.debug("Leaving");
		return financeDetail;
	}

	public FinanceMain getFinanceMainParms(final String finReference) {
		return getFinanceMainDAO().getFinanceMainParms(finReference);
	}

	@Override
	public List<String> getFinanceReferenceList() {
		return getFinanceMainDAO().getFinanceReferenceList();
	}

	@Override
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		return getFinanceMainDAO().getIjaraBulkRateFinList(fromDate, toDate);
	}

	@Override
	public List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate) {
		return getFinanceMainDAO().getBulkDefermentFinList(fromDate, toDate);
	}

	@Override
	public String getCustStatusByMinDueDays() {
		CustomerStatusCode customerStatusCode = getCustomerStatusCodeDAO().getCustStatusByMinDueDays("");
		if (customerStatusCode != null) {
			return customerStatusCode.getCustStsCode();
		}
		return "";
	}

	@Override
	public List<CustomerIncome> prepareIncomeDetails() {
		logger.debug("Entering");

		List<IncomeType> incomeTypeList = getIncomeTypeDAO().getIncomeTypeList();
		List<CustomerIncome> customerIncomes = new ArrayList<CustomerIncome>();
		for (IncomeType incomeType : incomeTypeList) {
			CustomerIncome income = new CustomerIncome();
			income.setIncomeExpense(incomeType.getIncomeExpense().trim());
			income.setCustIncomeType(incomeType.getIncomeTypeCode().trim());
			income.setJointCust(false);
			income.setMargin(BigDecimal.ZERO);
			income.setCategory(incomeType.getCategory().trim());
			income.setCustIncome(BigDecimal.ZERO);
			income.setVersion(1);
			income.setRecordType(PennantConstants.RCD_ADD);
			income.setWorkflowId(0);
			income.setLovDescCategoryName(incomeType.getLovDescCategoryName().trim());
			income.setLovDescCustIncomeTypeName(incomeType.getIncomeTypeDesc().trim());

			customerIncomes.add(income);
		}
		logger.debug("Leaving");
		return customerIncomes;
	}

	public void downpayFinApprove(AuditHeader aAuditHeader) {
		logger.debug("Entering");

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		financeDetail.setFinScheduleData(ScheduleCalculator.getDownPaySchd(financeDetail.getFinScheduleData()));
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		Date curBussDate = DateUtility.getAppDate();
		financeMain.setFinApprovedDate(curBussDate);

		//Profit Details Data Preparation
		//=======================================
		Date curBDay = DateUtility.getAppDate();

		FinanceProfitDetail pftDetail = 	getAccrualService().calProfitDetails(financeMain, financeDetail.getFinScheduleData()
				.getFinanceScheduleDetails(), null, curBDay);

		// Save/Update Finance Profit Details
		// Save/Update Finance Profit Details
		boolean isNew = false;

		if (StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isNew = true;
		}

		doSave_PftDetails(pftDetail, isNew);

		financeMain.setRecordType("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setWorkflowId(0);
		getFinanceMainDAO().save(financeMain, TableType.MAIN_TAB, false);

		//Schedule Details
		//=======================================
		listSave(financeDetail.getFinScheduleData(), "", false, 0);

		logger.debug("Leaving");
	}

	/**
	 * Method to update Task_log table
	 * 
	 * @param financeMain
	 */
	private void updateTaskLog(FinanceMain financeMain, boolean isSaveorUpdate) {
		logger.debug("Entering");
		List<QueueAssignment> queueAssignList = new ArrayList<QueueAssignment>();
		List<TaskOwners> taskOwnerList = new ArrayList<TaskOwners>();
		TaskOwners taskOwner = null;
		boolean addRecord = false;
		String roleCode = null;
		String userId = "";

		if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus()) && !financeMain.isNewRecord()) {
			TaskOwners owner = fetchTaskOwner(financeMain.getFinReference(), financeMain.getRoleCode());
			if (owner.getCurrentOwner() == 0) {
				owner.setCurrentOwner(financeMain.getLastMntBy());
				owner.setActualOwner(financeMain.getLastMntBy());
			}
			owner.setNewRecord(false);
			owner.setProcessed(true);
			taskOwnerList.add(owner);

			// Update resubmitting task owner in queue assignment
			queueAssignList = getResubmitQueueDetails(financeMain, owner.isProcessed());

			roleCode = financeMain.getNextRoleCode();
		} else {
			roleCode = financeMain.getRoleCode();
			userId = String.valueOf(financeMain.getLastMntBy());
		}

		List<TaskOwners> existingTaskOwners = getTaskOwnersDAO().getTaskOwnerList(financeMain.getFinReference(),
				roleCode);
		if (existingTaskOwners.size() == 0) {
			taskOwner = new TaskOwners();
			taskOwner.setReference(financeMain.getFinReference());
			taskOwner.setRoleCode(financeMain.getRoleCode());
			taskOwner.setActualOwner(financeMain.getLastMntBy());
			taskOwner.setCurrentOwner(financeMain.getLastMntBy());
			if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
				taskOwner.setProcessed(true);
			}
			taskOwner.setNewRecord(true);
			existingTaskOwners.add(taskOwner);
			addRecord = true;
		} else {
			for (int i = 0; i < existingTaskOwners.size(); i++) {
				taskOwner = existingTaskOwners.get(i);
				taskOwner.setNewRecord(false);

				if (PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())
						|| (taskOwner.getCurrentOwner() != 0 && PennantConstants.RCD_STATUS_RESUBMITTED
								.equals(financeMain.getRecordStatus()))) {
					taskOwner.setProcessed(false);
				} else {
					taskOwner.setProcessed(true);
				}

				if (taskOwner.getCurrentOwner() == 0 && taskOwner.getRoleCode().equals(financeMain.getRoleCode())) {
					taskOwner.setActualOwner(financeMain.getLastMntBy());
					taskOwner.setCurrentOwner(financeMain.getLastMntBy());
					addRecord = true;
				} else {
					if (StringUtils.isEmpty(userId)) {
						userId = userId.concat(taskOwner.getCurrentOwner() + ",");
						if (i == existingTaskOwners.size() - 1) {
							userId = userId.substring(0, userId.length() - 1);
						}
					}
				}
			}
		}

		if (financeMain.isNewRecord() || !PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())
				|| addRecord) {
			queueAssignList.addAll(addQueueAssignmentDetails(existingTaskOwners, financeMain, userId, roleCode,
					taskOwner.isProcessed()));
			taskOwnerList.addAll(existingTaskOwners);
		}

		String nextUsers = null;
		if (isSaveorUpdate && StringUtils.isNotEmpty(financeMain.getNextTaskId())) {
			addTaskQueueDetails(financeMain, queueAssignList, taskOwnerList);
			nextUsers = financeMain.getNextUserId();
		}

		if (financeMain.getLovDescNextUsersRolesMap() != null) {
			saveUserActivityDetails(financeMain);
			getTaskOwnersDAO().saveOrUpdateList(taskOwnerList);

			if (queueAssignList.size() > 0 && StringUtils.isNotBlank(financeMain.getLovDescAssignMthd())
					&& (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)|| (financeMain.isQuickDisb()))) {
				getQueueAssignmentDAO().saveOrUpdate(queueAssignList);
			}
		}

		if (isSaveorUpdate) {
			List<String> refList = new ArrayList<String>();
			refList.add(financeMain.getFinReference());
			getFinanceMainDAO().updateNextUserId(refList, "", nextUsers, true); // Update nextuserid value in finance table
		} else {
			getUserActivityLogDAO().updateFinStatus(financeMain.getFinReference(),
					PennantConstants.WORFLOW_MODULE_FINANCE);
		}
		logger.debug("Leaving");
	}

	private List<QueueAssignment> getResubmitQueueDetails(FinanceMain financeMain, boolean isProcessed) {
		// Update resubmitting task owner in queue assignment
		List<QueueAssignment> queueList = getQueueAssignmentDAO().getQueueAssignmentList(
				String.valueOf(financeMain.getLastMntBy()), PennantConstants.WORFLOW_MODULE_FINANCE,
				financeMain.getRoleCode());
		QueueAssignment queueAssignment = null;
		List<QueueAssignment> queueAssignmentList = new ArrayList<QueueAssignment>();
		if (queueList.isEmpty()) {
			queueAssignment = getNewQueueAssignment(financeMain.getRoleCode(), financeMain.getLastMntBy(), true);
			queueAssignment.setLastAssignedOn(financeMain.getLastMntOn());
			queueAssignment.setLovDescUserAction(financeMain.getRecordStatus());
			queueAssignment.setLovDescQAUserId(0);
			queueAssignment.setAssignedCount(isProcessed ? 0 : 1);
			queueAssignment.setProcessedCount(isProcessed ? 1 : 0);
			queueAssignmentList.add(queueAssignment);
			queueAssignment = null;
		} else {
			for (int i = 0; i < queueList.size(); i++) {
				queueAssignment = queueList.get(i);
				queueAssignment.setNewRecord(false);
				queueAssignment.setRecordProcessed(isProcessed);
				queueAssignment.setAssignedCount(1);
				queueAssignment.setProcessedCount(isProcessed ? 1 : 0);
				queueAssignmentList.add(queueAssignment);
				queueAssignment = null;
			}
		}
		return queueAssignmentList;
	}

	private List<QueueAssignment> addQueueAssignmentDetails(List<TaskOwners> dbTaskOwnersList, FinanceMain financeMain,
			String userId, String roleCode, boolean isProcessed) {
		List<QueueAssignment> queueAssignmentList = new ArrayList<QueueAssignment>(dbTaskOwnersList.size());
		QueueAssignment queueAssignment = null;
		List<QueueAssignment> queueList = null;
		if (StringUtils.isNotEmpty(userId)) {
			queueList = getQueueAssignmentDAO().getQueueAssignmentList(userId, PennantConstants.WORFLOW_MODULE_FINANCE,
					roleCode);
		}
		if (queueList == null || queueList.size() == 0) {
			queueAssignment = getNewQueueAssignment(financeMain.getRoleCode(), financeMain.getLastMntBy(), true);
			queueAssignment.setLastAssignedOn(financeMain.getLastMntOn());
			queueAssignment.setLovDescUserAction(financeMain.getRecordStatus());
			queueAssignment.setLovDescQAUserId(0);
			queueAssignment.setAssignedCount(isProcessed ? 0 : 1);
			queueAssignment.setProcessedCount(isProcessed ? 1 : 0);
			queueAssignmentList.add(queueAssignment);
		} else {
			for (TaskOwners owner : dbTaskOwnersList) {
				if (owner.getCurrentOwner() != 0) {
					for (int i = 0; i < queueList.size(); i++) {
						queueAssignment = queueList.get(i);
						queueAssignment.setNewRecord(false);
						queueAssignment.setRecordProcessed(isProcessed);
						if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())) {
							if (owner.getCurrentOwner() == queueAssignment.getUserId()
									&& owner.getRoleCode().equals(queueAssignment.getUserRoleCode())) {
								queueAssignment.setProcessedCount(-1);
								queueAssignment.setAssignedCount(1);
								queueAssignmentList.add(queueAssignment);
								break;
							}
						} else if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
							queueAssignment.setProcessedCount(1);
							queueAssignment.setAssignedCount(1);
							queueAssignmentList.add(queueAssignment);
							break;
						} else if (financeMain.isNewRecord()
								&& PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
							queueAssignment.setAssignedCount(1);
							queueAssignment.setProcessedCount(0);
							queueAssignmentList.add(queueAssignment);
							break;
						}
					}
				}
			}
		}
		return queueAssignmentList;
	}

	private void addTaskQueueDetails(FinanceMain financeMain, List<QueueAssignment> queueAssignList,
			List<TaskOwners> taskOwnerList) {
		logger.debug("Entering");
		String nextUsers = null;
		Map<String, String> nextUsersRolesMap = new HashMap<String, String>();
		nextUsersRolesMap.put(financeMain.getRoleCode(), String.valueOf(financeMain.getLastMntBy()));

		if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())) {
			String nextRoleCodes[] = null;
			if (financeMain.getNextRoleCode().contains(",")) {
				nextRoleCodes = financeMain.getNextRoleCode().split(",");
			} else {
				nextRoleCodes = new String[] { financeMain.getNextRoleCode() };
			}
			String baseRole = "";
			TaskOwners taskOwner = null;
			QueueAssignment queueAssignment = null;
			String excludeUsers = String.valueOf(financeMain.getLastMntBy());
			for (int i = 0; i < nextRoleCodes.length; i++) {
				long nextUserId = 0;

				// Check if any base role available for the role code
				baseRole = financeMain.getLovDescBaseRoleCodeMap().get(nextRoleCodes[i]);

				// If base role available, check for the BASE ROLE user in Task Owners table else for the ROLE user and assign
				if (StringUtils.isNotEmpty(baseRole)) {
					taskOwner = fetchTaskOwner(financeMain.getFinReference(), baseRole);
					nextUserId = taskOwner.getCurrentOwner();
					TaskOwners temp = fetchTaskOwner(financeMain.getFinReference(), nextRoleCodes[i]);
					if (null != temp && (temp.getCurrentOwner() == nextUserId || temp.getCurrentOwner() == 0)) {
						taskOwner.setNewRecord(false);
					} else {
						taskOwner.setNewRecord(true);
					}
					taskOwner.setProcessed(false);
					taskOwner.setRoleCode(nextRoleCodes[i]);
					taskOwnerList.add(taskOwner);

					if (taskOwner.getCurrentOwner() != 0) {
						queueAssignment = getNewQueueAssignment(taskOwner.getRoleCode(), taskOwner.getCurrentOwner(),
								false);
						queueAssignment.setRecordProcessed(false);
						queueAssignment.setAssignedCount(1);
						queueAssignment.setProcessedCount(0);
						queueAssignList.add(queueAssignment);
					}

				} else {
					taskOwner = fetchTaskOwner(financeMain.getFinReference(), nextRoleCodes[i]);
					if (null == taskOwner) {
						if (StringUtils.trimToEmpty(financeMain.getLovDescAssignMthd()).equalsIgnoreCase(
								PennantConstants.AUTO_ASSIGNMENT)) {

							queueAssignment = getQueueAssignmentDAO().getNewUserId(
									PennantConstants.WORFLOW_MODULE_FINANCE, nextRoleCodes[i], excludeUsers);
							queueAssignment.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
							queueAssignment.setUserRoleCode(nextRoleCodes[i]);
							queueAssignment.setNewRecord(false);
							queueAssignment.setRecordProcessed(false);
							queueAssignment.setAssignedCount(1);
							queueAssignment.setProcessedCount(0);
							queueAssignList.add(queueAssignment);

							nextUserId = queueAssignment.getUserId();
						}
						taskOwner = new TaskOwners();
						taskOwner.setReference(financeMain.getFinReference());
						taskOwner.setRoleCode(nextRoleCodes[i]);
						taskOwner.setActualOwner(nextUserId);
						taskOwner.setCurrentOwner(nextUserId);
						taskOwner.setProcessed(false);
						taskOwner.setNewRecord(true);
						taskOwnerList.add(taskOwner);
					} else {
						taskOwner.setProcessed(false);
						taskOwner.setNewRecord(false);
						taskOwnerList.add(taskOwner);

						if (taskOwner.getCurrentOwner() != 0
								&& !StringUtils.trimToEmpty(financeMain.getBefImage().getNextRoleCode()).contains(
										nextRoleCodes[i])) {
							queueAssignment = getNewQueueAssignment(taskOwner.getRoleCode(),
									taskOwner.getCurrentOwner(), false);
							queueAssignment.setRecordProcessed(false);
							queueAssignment.setAssignedCount(1);
							queueAssignment.setProcessedCount(0);
							queueAssignList.add(queueAssignment);
						}
						nextUserId = taskOwner.getCurrentOwner();
					}
				}

				excludeUsers = excludeUsers.concat("," + String.valueOf(nextUserId));

				if (!financeMain.getNextRoleCode().contains(financeMain.getRoleCode())) {
					nextUsersRolesMap.remove(financeMain.getRoleCode());
					nextUsersRolesMap.put(nextRoleCodes[i], String.valueOf(nextUserId));
				} else {
					nextUsers = String.valueOf(financeMain.getLastMntBy());
				}

				if (nextUserId != 0 && (!financeMain.getNextRoleCode().contains(financeMain.getRoleCode()))) {
					if (StringUtils.isBlank(nextUsers)) {
						nextUsers = String.valueOf(nextUserId);
					} else {
						nextUsers = nextUsers.concat("," + String.valueOf(nextUserId));
					}
				}
			}
		} else {
			if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())) {
				TaskOwners taskOwnerTemp = fetchTaskOwner(financeMain.getFinReference(), financeMain.getNextRoleCode());
				if (taskOwnerTemp != null) {
					financeMain.setNextUserId(String.valueOf(taskOwnerTemp.getCurrentOwner()));
				}
			}
			nextUsers = financeMain.getNextUserId() == null ? String.valueOf(financeMain.getLastMntBy()) : financeMain
					.getNextUserId();
		}

		financeMain.setLovDescNextUsersRolesMap(nextUsersRolesMap);
		financeMain.setNextUserId(nextUsers);
		logger.debug("Leaving");
	}

	private void saveUserActivityDetails(FinanceMain financeMain) {
		List<UserActivityLog> logList = new ArrayList<UserActivityLog>();
		for (Map.Entry<String, String> entry : financeMain.getLovDescNextUsersRolesMap().entrySet()) {
			UserActivityLog userActivityLog = new UserActivityLog();
			userActivityLog.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
			userActivityLog.setReference(financeMain.getFinReference());
			userActivityLog.setFromUser(financeMain.getLastMntBy());
			userActivityLog.setActivity(financeMain.getRecordStatus());
			userActivityLog.setRoleCode(financeMain.getRoleCode());
			if (Long.parseLong(entry.getValue()) != 0) {
				userActivityLog.setToUser(Long.parseLong(entry.getValue()));
			}
			userActivityLog.setNextRoleCode(entry.getKey());
			userActivityLog.setLogTime(financeMain.getLastMntOn());
			userActivityLog.setProcessed(false);
			if (StringUtils.isEmpty(financeMain.getNextTaskId())) {
				userActivityLog.setRoleCode(entry.getKey());
				userActivityLog.setToUser(Long.valueOf(0));
			}
			logList.add(userActivityLog);
		}
		getUserActivityLogDAO().saveList(logList); // Always Save / Insert
	}

	private QueueAssignment getNewQueueAssignment(String usrRoleCode, long usrID, boolean isNewRcd) {
		QueueAssignment queueAssignment = new QueueAssignment();
		queueAssignment.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
		queueAssignment.setUserRoleCode(usrRoleCode);
		queueAssignment.setUserId(usrID);
		queueAssignment.setNewRecord(isNewRcd);
		return queueAssignment;
	}

	private TaskOwners fetchTaskOwner(String finRef, String roleCode) {
		return getTaskOwnersDAO().getTaskOwner(finRef, roleCode);
	}

	@Override
	public List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId) {
		return getFinanceMainDAO().getFinanceDetailByCmtRef(cmtRef, custId);
	}

	@Override
	public BigDecimal getCustRepayBankTotal(long custId) {
		return getCustomerDAO().getCustRepayBankTotal(custId);
	}

	@Override
	public String getApprovedRepayMethod(String finReference, String type) {
		return getFinanceMainDAO().getApprovedRepayMethod(finReference, type);
	}

	@Override
	public FeeRule getFeeChargesByFinRefAndFeeCode(String finReference, String feeCode, String tableType) {
		return getFinFeeChargesDAO().getFeeChargesByFinRefAndFee(finReference, feeCode, tableType);
	}

	@Override
	public boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType) {
		return getFinFeeChargesDAO().updateFeeChargesByFinRefAndFee(feeRule, tableType);
	}

	@Override
	public List<FinanceSummary> getFinExposureByCustId(long custId) {
		return getFinanceMainDAO().getFinExposureByCustId(custId);
	}

	@Override
	public List<String> getRollOverLimitRefList() {
		return getFinanceMainDAO().getRollOverLimitRefList();
	}

	@Override
	public List<String> getRollOverFinTypeList(String limitRef) {
		return getFinanceMainDAO().getRollOverFinTypeList(limitRef);
	}

	@Override
	public List<Date> getRollOverNextDateList(String limitRef, String finType) {
		return getFinanceMainDAO().getRollOverDateList(limitRef, finType);
	}

	@Override
	public List<RolledoverFinanceDetail> getRolloverFinanceList(String limitRef, String finType, Date rolloverDate) {
		return getFinanceMainDAO().getFinanceList(limitRef, finType, rolloverDate);
	}

	@Override
	public BigDecimal getTotalRepayAmount(String finReference) {
		return getFinanceScheduleDetailDAO().getTotalRepayAmount(finReference);
	}

	// ******************************************************//
	// *************Queue Assignment Details*****************//
	// ******************************************************//

	@Override
	public String getUserRoleCodeByRefernce(long userId, String reference, List<String> userRoles) {
		logger.debug("Entering");
		String usrRoleCode = getTaskOwnersDAO().getUserRoleCodeByRefernce(userId, reference, userRoles);
		logger.debug("Leaving");
		return usrRoleCode;
	}

	@Override
	public void updateFinancePriority() {
		getFinanceMainDAO().updateFinancePriority();
	}

	/**
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceDetail getPreApprovalFinanceDetailsById(String finReference) {
		logger.debug(" Entering ");
		String type = PennantConstants.PREAPPROVAL_TABLE_TYPE;

		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));

		//Step Policy Details List
		if (scheduleData.getFinanceMain().isStepFinance()) {
			scheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference,
					type, false));
		}

		//Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type,
				false));

		//Finance Disbursement Details
		scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
				type, false));

		//Finance Repayments Instruction Details
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		//Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		//Fetch Finance Premium Details
		//financeDetail.setPremiumDetail(getFinancePremiumDetailDAO().getFinPremiumDetailsById(finReference, type));

		//scheduleData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finReference,FinanceConstants.FINSER_EVENT_PREAPPROVAL, false, type));

		//Finance Fee Schedule Details
		scheduleData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, false, type));

		//Finance Guaranteer Details			
		financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, type));

		//Finance Joint Account Details
		financeDetail
				.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference, type));

		// Asset Type Details
		financeDetail.setFinAssetTypesList(getFinAssetTypeDAO().getFinAssetTypesByFinRef(finReference, "_Temp"));

		// Extended Field Details for Assets
		financeDetail.setExtendedFieldRenderList(getExtendedAssetDetails(finReference,
				financeDetail.getFinAssetTypesList()));

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(
					finReference, FinanceConstants.MODULE_NAME, "_TView"));
		} else {
			financeDetail.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference, type));
		}

		//document details
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_PREAPPROVAL, type));

		logger.debug(" Leaving ");
		return financeDetail;
	}

	// ******************************************************//
	// ************ ROLLED OVER FINANCE DETAIL  *************//
	// ******************************************************//

	@SuppressWarnings("unchecked")
	private void doRolledOverFinancePostings(String finReference, BigDecimal custPayAmount, String accNumber)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		//Finance Details
		FinanceMain main = getFinanceMainDAO().getFinanceMainById(finReference, "", false);
		FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(main.getFinType(), "_AView");
		List<FinanceScheduleDetail> scheduleDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,
				"", false);
		FinanceProfitDetail financeProfitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);

		List<Object> returnList = new ArrayList<Object>();
		try {
			String actRepayAccountId = main.getRepayAccountId();
			main.setRepayAccountId(accNumber);

			//Check Finance is RIA Finance Type or Not
			BigDecimal totRpyPri = BigDecimal.ZERO;
			BigDecimal totRpyPft = BigDecimal.ZERO;
			BigDecimal totSchdFee = BigDecimal.ZERO;
			BigDecimal totSchdIns = BigDecimal.ZERO;
			BigDecimal totSchdSuplRent = BigDecimal.ZERO;
			BigDecimal totSchdIncrCost = BigDecimal.ZERO;

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			Map<String, BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
			FinRepayQueue finRepayQueue = null;
			Date curBDay = DateUtility.getAppDate();

			for (int i = 0; i < scheduleDetails.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(main.getFinReference());
				finRepayQueue.setRpyDate(scheduleDetails.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, main, scheduleDetails.get(i), custPayAmount);

				// Check Balance
				BigDecimal totalPay = finRepayQueue.getSchdInsPayNow().add(finRepayQueue.getSchdSuplRentPayNow())
						.add(finRepayQueue.getSchdIncrCostPayNow()).add(finRepayQueue.getSchdFeePayNow())
						.add(finRepayQueue.getSchdPftPayNow()).add(finRepayQueue.getSchdPriPayNow());

				custPayAmount = custPayAmount.subtract(totalPay);

				//Overdue Details preparation
				List<Object> odObjDetails = getRecoveryPostingsUtil().recoveryCalculation(finRepayQueue,
						main.getProfitDaysBasis(), curBDay, false, false);
				OverdueChargeRecovery recovery = (OverdueChargeRecovery) odObjDetails.get(1);

				finRepayQueue.setRefundAmount(BigDecimal.ZERO);
				finRepayQueue.setPenaltyPayNow(BigDecimal.ZERO);
				finRepayQueue.setWaivedAmount(BigDecimal.ZERO);
				finRepayQueue.setPenaltyBal(recovery.getPenalty());
				finRepayQueue.setChargeType(recovery.getPenaltyType());

				if (totalPay.compareTo(BigDecimal.ZERO) == 0 && recovery.getPenalty().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				//Total Repayments Calculation for Principal, Profit & Refunds
				totRpyPri = totRpyPri.add(finRepayQueue.getSchdPriPayNow());
				totRpyPft = totRpyPft.add(finRepayQueue.getSchdPftPayNow());

				//Fee Details
				totSchdFee = totSchdFee.add(finRepayQueue.getSchdFeePayNow());
				totSchdIns = totSchdIns.add(finRepayQueue.getSchdInsPayNow());
				totSchdSuplRent = totSchdSuplRent.add(finRepayQueue.getSchdSuplRentPayNow());
				totSchdIncrCost = totSchdIncrCost.add(finRepayQueue.getSchdIncrCostPayNow());

				finRepayQueues.add(finRepayQueue);

				//If Balance is Nill then Process Closed
				if (custPayAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}

			totalsMap.put("totRpyTot", totRpyPri.add(totRpyPft));
			totalsMap.put("totRpyPri", totRpyPri);
			totalsMap.put("totRpyPft", totRpyPft);
			totalsMap.put("totRefund", BigDecimal.ZERO);
			totalsMap.put("INSREFUND", BigDecimal.ZERO);

			//Fee Details
			totalsMap.put("insPay", totSchdIns);
			totalsMap.put("schFeePay", totSchdFee);
			totalsMap.put("suplRentPay", totSchdSuplRent);
			totalsMap.put("incrCostPay", totSchdIncrCost);

			//Repayment process 
			returnList = getRepayPostingUtil()
					.postingsScreenRepayProcess(main, scheduleDetails, financeProfitDetail, finRepayQueues, totalsMap,
							AccountEventConstants.ACCEVENT_REPAY, null, financeType.getFinDivision());

			if ((Boolean) returnList.get(0)) {
				returnList.add(finRepayQueues);
			}

			if (!(Boolean) returnList.get(0)) {
				String errParm = (String) returnList.get(1);
				throw new InterfaceException("9999", errParm);
			}

			long linkedTranId = (Long) returnList.get(1);
			boolean partialPay = (Boolean) returnList.get(2);
			AEAmountCodes aeAmountCodes = (AEAmountCodes) returnList.get(3);

			//Repayment Postings Details Process
			returnList = getRepayPostingUtil().UpdateScreenPaymentsProcess(main, scheduleDetails, financeProfitDetail,
					finRepayQueues, linkedTranId, partialPay, aeAmountCodes);

			//Save Finance Repay Header Details
			FinRepayHeader finRepayHeader = new FinRepayHeader();
			finRepayHeader.setFinReference(finReference);
			finRepayHeader.setValueDate(curBDay);
			finRepayHeader.setRepayAmount(totRpyPri.add(totRpyPft));
			finRepayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_ROLLOVER);
			finRepayHeader.setPriAmount(totRpyPri);
			finRepayHeader.setPftAmount(totRpyPft);
			finRepayHeader.setTotalIns(totSchdIns);
			finRepayHeader.setTotalSuplRent(totSchdSuplRent);
			finRepayHeader.setTotalIncrCost(totSchdIncrCost);
			finRepayHeader.setTotalSchdFee(totSchdFee);
			finRepayHeader.setTotalRefund(BigDecimal.ZERO);
			finRepayHeader.setTotalWaiver(BigDecimal.ZERO);
			finRepayHeader.setInsRefund(BigDecimal.ZERO);
			finRepayHeader.setRepayAccountId(actRepayAccountId);
			finRepayHeader.setEarlyPayEffMtd(CalculationConstants.EARLYPAY_NOEFCT);
			finRepayHeader.setEarlyPayDate(curBDay);
			finRepayHeader.setSchdRegenerated(false);
			finRepayHeader.setPayApportionment(PennantConstants.List_Select);
			finRepayHeader.setLinkedTranId(linkedTranId);
			getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, "");

			//Finance Main Updation
			//=======================================
			main = (FinanceMain) returnList.get(3);
			getFinanceMainDAO().updateRepaymentAmount(main.getFinReference(),
					main.getFinRepaymentAmount().add(custPayAmount));

			// ScheduleDetails delete and save
			//=======================================
			scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(4);
			getFinanceScheduleDetailDAO().deleteByFinReference(finReference, "", false, 0);
			getFinanceScheduleDetailDAO().saveList(scheduleDetails, "", false);

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			FinanceScheduleDetail curSchd, BigDecimal custPayAmount) {
		logger.debug("Entering");

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		finRepayQueue.setSchdPft(curSchd.getProfitSchd());
		finRepayQueue.setSchdPri(curSchd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		finRepayQueue.setSchdPriBal(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
		finRepayQueue.setSchdPriPaid(curSchd.getSchdPriPaid());
		finRepayQueue.setSchdPftPaid(curSchd.getSchdPftPaid());

		// Fee Details
		//	1. Schedule Fee Amount
		finRepayQueue.setSchdFee(curSchd.getFeeSchd());
		finRepayQueue.setSchdFeePaid(curSchd.getSchdFeePaid());

		//	2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(curSchd.getInsSchd());
		finRepayQueue.setSchdInsPaid(curSchd.getSchdInsPaid());

		//	3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(curSchd.getSuplRent());
		finRepayQueue.setSchdSuplRentPaid(curSchd.getSuplRentPaid());

		//	4. Schedule Fee Amount
		finRepayQueue.setSchdIncrCost(curSchd.getIncrCost());
		finRepayQueue.setSchdIncrCostPaid(curSchd.getIncrCostPaid());

		// Scheduled Fee Collection Process

		//	1. Insurance Fee Amount
		if (custPayAmount.compareTo(curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())) > 0) {
			finRepayQueue.setSchdInsPayNow(curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid()));
		} else {
			finRepayQueue.setSchdInsPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdInsPayNow());

		//	2. Supplementary Rent Amount
		if (custPayAmount.compareTo(curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid())) > 0) {
			finRepayQueue.setSchdSuplRentPayNow(curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid()));
		} else {
			finRepayQueue.setSchdSuplRentPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdSuplRentPayNow());

		//	3. Increased Cost Amount
		if (custPayAmount.compareTo(curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid())) > 0) {
			finRepayQueue.setSchdIncrCostPayNow(curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid()));
		} else {
			finRepayQueue.setSchdIncrCostPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdIncrCostPayNow());

		//	4. Scheduled Fee Amount
		if (custPayAmount.compareTo(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())) > 0) {
			finRepayQueue.setSchdFeePayNow(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()));
		} else {
			finRepayQueue.setSchdFeePayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdFeePayNow());

		//	5. Scheduled Profit Amount
		if (custPayAmount.compareTo(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())) > 0) {
			finRepayQueue.setSchdPftPayNow(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		} else {
			finRepayQueue.setSchdPftPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdPftPayNow());

		//	6. Scheduled Principal Amount
		if (custPayAmount.compareTo(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())) > 0) {
			finRepayQueue.setSchdPriPayNow(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
		} else {
			finRepayQueue.setSchdPriPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdPriPayNow());

		logger.debug("Leaving");
		return finRepayQueue;
	}

	private AuditHeader processLimitSaveOrUpdate(AuditHeader aAuditHeader,boolean validateOnly) {

		if (ImplementationConstants.LIMIT_INTERNAL) {

			FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			String nextrole = finmain.getNextRoleCode();
			String role = finmain.getRoleCode();
			String moduleType = StringUtils.trimToEmpty(finmain.getRcdMaintainSts());
			String prodCategory = StringUtils.trimToEmpty(finType.getProductCategory());

			//process block
			if (!financeDetail.isActionSave() && !StringUtils.equals(nextrole, role)) {
				// Checking for Limit check Authority i.e Is current Role contains limit check authority (or) Not
				List<FinanceReferenceDetail> limitCheckList = getLimitCheckDetails().doLimitChek(role,
						finmain.getFinType());
				if (limitCheckList == null || limitCheckList.isEmpty()) {
					return aAuditHeader;
				}

				boolean validateReserve = false;
				for (FinanceReferenceDetail finRefDetail : limitCheckList) {
					if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.PRECHECK)) {
						validateReserve = true;
						break;
					}
				}
				if (validateReserve) {

					if ("".equals(moduleType) || FinanceConstants.FINSER_EVENT_ORG.equals(moduleType)) {
						List<ErrorDetails> errorDetails = getLimitManagement().processLoanLimitOrgination(
								financeDetail, aAuditHeader.isOveride(), LimitConstants.BLOCK,validateOnly);
						if (!errorDetails.isEmpty()) {
							aAuditHeader.setErrorList(errorDetails);
						}
					} else if (moduleType.equals(FinanceConstants.FINSER_EVENT_ADDDISB)
							&& !prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
						if (finmain.getFinAssetValue().compareTo(finmain.getFinCurrAssetValue()) == 0) {
							List<ErrorDetails> errorDetails = getLimitManagement().processLoanDisbursments(
									financeDetail, aAuditHeader.isOveride(), LimitConstants.BLOCK,validateOnly);
							if (!errorDetails.isEmpty()) {
								aAuditHeader.setErrorList(errorDetails);
							}
						}

					}
				}
			}
		}
		aAuditHeader = nextProcess(aAuditHeader);
		return aAuditHeader;
	}

	private AuditHeader processLimitApprove(AuditHeader aAuditHeader,boolean validateOnly) {

		if (ImplementationConstants.LIMIT_INTERNAL) {

			FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			String moduleType = StringUtils.trimToEmpty(finmain.getRcdMaintainSts());
			String prodCategory = StringUtils.trimToEmpty(finType.getProductCategory());
			//Origination 
			if ("".equals(moduleType) || FinanceConstants.FINSER_EVENT_ORG.equals(moduleType)) {

				String transType = "";
				if (prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
					transType = LimitConstants.BLOCK;
				} else {
					transType = LimitConstants.APPROVE;
				}
				List<ErrorDetails> errorDetails = getLimitManagement().processLoanLimitOrgination(financeDetail,
						aAuditHeader.isOveride(), transType,validateOnly);
				if (!errorDetails.isEmpty()) {
					aAuditHeader.setErrorList(errorDetails);
				}
			} else {

				if (moduleType.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)) {
					List<ErrorDetails> errorDetails = getLimitManagement().processLimitIncrease(financeDetail,
							aAuditHeader.isOveride(),validateOnly);
					if (!errorDetails.isEmpty()) {
						aAuditHeader.setErrorList(errorDetails);
					}
				} else {

					String tranType = "";

					if (moduleType.equals(FinanceConstants.FINSER_EVENT_ADDDISB)) {
						tranType = LimitConstants.APPROVE;
					} else if (moduleType.equals(FinanceConstants.FINSER_EVENT_CANCELDISB)) {
						tranType = LimitConstants.UNBLOCK;
					}

					if (!StringUtils.isBlank(tranType)) {
						List<ErrorDetails> errorDetails = getLimitManagement().processLoanDisbursments(financeDetail,
								aAuditHeader.isOveride(), tranType,validateOnly);
						if (!errorDetails.isEmpty()) {
							aAuditHeader.setErrorList(errorDetails);
						}
					}
				}

			}
		}
		aAuditHeader = nextProcess(aAuditHeader);
		return aAuditHeader;
	}

	/**
	 * Method for Preparing Data for Finance Repay Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	private FinanceRepayments prepareBpiRepayData(FinanceMain finMain, Date bpiDate, long linkedTranId,
			BigDecimal bpiAmount) {
		logger.debug(Literal.ENTERING);

		FinanceRepayments repayment = new FinanceRepayments();
		Date curAppDate = DateUtility.getAppDate();

		repayment.setFinReference(finMain.getFinReference());
		repayment.setFinSchdDate(bpiDate);
		repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(bpiAmount);
		repayment.setFinPostDate(curAppDate);
		repayment.setFinValueDate(finMain.getFinStartDate());
		repayment.setFinBranch(finMain.getFinBranch());
		repayment.setFinType(finMain.getFinType());
		repayment.setFinCustID(finMain.getCustID());
		repayment.setFinSchdPftPaid(bpiAmount);
		repayment.setFinSchdPriPaid(BigDecimal.ZERO);
		repayment.setFinTotSchdPaid(bpiAmount);
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(BigDecimal.ZERO);
		repayment.setFinRefund(BigDecimal.ZERO);

		// Fee Details
		repayment.setSchdFeePaid(BigDecimal.ZERO);
		repayment.setSchdInsPaid(BigDecimal.ZERO);
		repayment.setSchdSuplRentPaid(BigDecimal.ZERO);
		repayment.setSchdIncrCostPaid(BigDecimal.ZERO);

		logger.debug(Literal.LEAVING);
		return repayment;
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursements(String finReferecne, String type, boolean isWIF) {
		return getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReferecne, type, isWIF);
	}

	public TATDetail getTATDetail(String reference, String rolecode) {
		return getTatDetailDAO().getTATDetail(reference, rolecode);
	}

	public void saveTATDetail(TATDetail tatDetail) {
		getTatDetailDAO().save(tatDetail);
	}

	public void updateTATDetail(TATDetail tatDetail) {
		getTatDetailDAO().update(tatDetail);
	}

	// ******************************************************//
	// ************ LPO Status Updation Details *************//
	// ******************************************************//

	/**
	 * Method for Approval process for LPO Approval Agreement
	 */
	@Override
	public void updateFinApprovalStatus(String finReference, String approvalStatus) {
		getFinanceMainDAO().updateApprovalStatus(finReference, approvalStatus);
	}

	@Override
	public String getNextRoleCodeByRef(String finReference, String type) {
		return getFinanceMainDAO().getNextRoleCodeByRef(finReference, type);

	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId, String type, boolean readAttachment) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, type, readAttachment);
	}

	@Override
	public List<DocumentDetails> getDocumentDetails(String finReference, String finProcEvent) {
		return getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				finProcEvent, "_View");
	}

	@Override
	public List<String> getUsersLoginList(List<String> nextRoleCodes) {
		return getFinanceMainDAO().getUsersLoginList(nextRoleCodes);
	}
	
	@Override
	public List<ReturnDataSet> prepareVasAccounting(AEEvent aeEvent, List<VASRecording> vasRecordings) {
		return processVasAccounting(aeEvent, vasRecordings, false);
	}

	@Override
	public FinanceMain getFinanceMainForBatch(String finReference) {
		return getFinanceMainDAO().getFinanceMainForBatch(finReference);
	}

	// ******************************************************//
	// *************** EOD PROCESS Details ******************//
	// ******************************************************//

	@Override
	public int getProgressCountByCust(long custID) {
		return getCustomerQueuingDAO().getProgressCountByCust(custID);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public ExtendedFieldHeaderDAO getExtendedFieldHeaderDAO() {
		return extendedFieldHeaderDAO;
	}

	public void setExtendedFieldHeaderDAO(ExtendedFieldHeaderDAO extendedFieldHeaderDAO) {
		this.extendedFieldHeaderDAO = extendedFieldHeaderDAO;
	}

	public ExtendedFieldDetailDAO getExtendedFieldDetailDAO() {
		return extendedFieldDetailDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
		return custLimitIntefaceService;
	}

	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
		this.custLimitIntefaceService = custLimitIntefaceService;
	}

	public FinanceWriteoffDAO getFinanceWriteoffDAO() {
		return financeWriteoffDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	public void setFinContributorHeaderDAO(FinContributorHeaderDAO finContributorHeaderDAO) {
		this.finContributorHeaderDAO = finContributorHeaderDAO;
	}

	public FinContributorHeaderDAO getFinContributorHeaderDAO() {
		return finContributorHeaderDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}

	public IncomeTypeDAO getIncomeTypeDAO() {
		return incomeTypeDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public IndicativeTermDetailDAO getIndicativeTermDetailDAO() {
		return indicativeTermDetailDAO;
	}

	public void setIndicativeTermDetailDAO(IndicativeTermDetailDAO indicativeTermDetailDAO) {
		this.indicativeTermDetailDAO = indicativeTermDetailDAO;
	}

	public QueueAssignmentDAO getQueueAssignmentDAO() {
		return queueAssignmentDAO;
	}

	public void setQueueAssignmentDAO(QueueAssignmentDAO queueAssignmentDAO) {
		this.queueAssignmentDAO = queueAssignmentDAO;
	}

	public UserActivityLogDAO getUserActivityLogDAO() {
		return userActivityLogDAO;
	}

	public void setUserActivityLogDAO(UserActivityLogDAO userActivityLogDAO) {
		this.userActivityLogDAO = userActivityLogDAO;
	}

	public TaskOwnersDAO getTaskOwnersDAO() {
		return taskOwnersDAO;
	}

	public void setTaskOwnersDAO(TaskOwnersDAO taskOwnersDAO) {
		this.taskOwnersDAO = taskOwnersDAO;
	}

	public LimitInterfaceDAO getLimitInterfaceDAO() {
		return limitInterfaceDAO;
	}

	public void setLimitInterfaceDAO(LimitInterfaceDAO limitInterfaceDAO) {
		this.limitInterfaceDAO = limitInterfaceDAO;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public FinanceRejectDetailDAO getFinanceRejectDetailDAO() {
		return financeRejectDetailDAO;
	}

	public void setFinanceRejectDetailDAO(FinanceRejectDetailDAO financeRejectDetailDAO) {
		this.financeRejectDetailDAO = financeRejectDetailDAO;
	}

	public RolledoverFinanceDAO getRolledoverFinanceDAO() {
		return rolledoverFinanceDAO;
	}

	public void setRolledoverFinanceDAO(RolledoverFinanceDAO rolledoverFinanceDAO) {
		this.rolledoverFinanceDAO = rolledoverFinanceDAO;
	}

	public PayOrderIssueHeaderDAO getPayOrderIssueHeaderDAO() {
		return payOrderIssueHeaderDAO;
	}

	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	public TATDetailDAO getTatDetailDAO() {
		return tatDetailDAO;
	}

	public void setTatDetailDAO(TATDetailDAO tatDetailDAO) {
		this.tatDetailDAO = tatDetailDAO;
	}

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

	public HandlingInstructionService getHandlingInstructionService() {
		return handlingInstructionService;
	}

	public void setHandlingInstructionService(HandlingInstructionService handlingInstructionService) {
		this.handlingInstructionService = handlingInstructionService;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	/*
	 * Method to get the schedule change module list from the ScheduleEffectModule table
	 */
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		return getFinanceMainDAO().getScheduleEffectModuleList(schdChangeReq);
	}

	public List<FinTypeFees> getFinTypeFees(String finType, String eventCode, boolean origination, int moduleId) {
		return getFinTypeFeesDAO().getFinTypeFeesList(finType, eventCode, "_AView", origination, moduleId);
	}
	
	@Override
	public BigDecimal getOutStandingBalFromFees(String finReference) {
		return getFinanceScheduleDetailDAO().getOutStandingBalFromFees(finReference);
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public OverdraftScheduleDetailDAO getOverdraftScheduleDetailDAO() {
		return overdraftScheduleDetailDAO;
	}

	public void setOverdraftScheduleDetailDAO(OverdraftScheduleDetailDAO overdraftScheduleDetailDAO) {
		this.overdraftScheduleDetailDAO = overdraftScheduleDetailDAO;
	}

	public FlagDetailValidation getFlagDetailValidation() {
		if (flagDetailValidation == null) {
			this.flagDetailValidation = new FlagDetailValidation(finFlagDetailsDAO);
		}
		return this.flagDetailValidation;
	}

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public FinTypeInsuranceDAO getFinTypeInsuranceDAO() {
		return finTypeInsuranceDAO;
	}

	public void setFinTypeInsuranceDAO(FinTypeInsuranceDAO finTypeInsuranceDAO) {
		this.finTypeInsuranceDAO = finTypeInsuranceDAO;
	}

	public ExtendedFieldRenderDAO getExtendedFieldRenderDAO() {
		return extendedFieldRenderDAO;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public ExtendedFieldDetailsValidation getExtendedFieldDetailsValidation() {
		if (extendedFieldDetailsValidation == null) {
			this.extendedFieldDetailsValidation = new ExtendedFieldDetailsValidation(extendedFieldRenderDAO);
		}
		return this.extendedFieldDetailsValidation;
	}

	public VASRecordingDAO getVasRecordingDAO() {
		return vasRecordingDAO;
	}

	public void setVasRecordingDAO(VASRecordingDAO vasRecordingDAO) {
		this.vasRecordingDAO = vasRecordingDAO;
	}

	public VasRecordingValidation getVasRecordingValidation() {
		if (vasRecordingValidation == null) {
			this.vasRecordingValidation = new VasRecordingValidation(vasRecordingDAO);
		}
		return this.vasRecordingValidation;
	}

	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}

	@Override
	public BigDecimal getFinAssetValue(String finReference) {
		return getFinanceMainDAO().getFinAssetValue(finReference);
	}

	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public GCDCustomerService getgCDCustomerService() {
		return gCDCustomerService;
	}

	public void setgCDCustomerService(GCDCustomerService gCDCustomerService) {
		this.gCDCustomerService = gCDCustomerService;
	}

	public FinanceTaxDetailService getFinanceTaxDetailService() {
		return financeTaxDetailService;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}
}
