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
 * 
 * FileName : NPAService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.NPAProvisionDetailDAO;
import com.pennant.backend.dao.applicationmaster.NPAProvisionHeaderDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleResult;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.service.extended.fields.ExtendedFieldService;

public class NPAService extends ServiceHelper {
	private static Logger logger = LogManager.getLogger(NPAService.class);

	private NPAProvisionHeaderDAO nPAProvisionHeaderDAO;
	private NPAProvisionDetailDAO nPAProvisionDetailDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private VASRecordingDAO vASRecordingDAO;
	private ExtendedFieldService extendedFieldServiceHook;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;

	/**
	 * Default constructor
	 */
	public NPAService() {
		super();
	}

	/**
	 * Processing NPA provision.
	 * 
	 * @param custEODEvent
	 * @return
	 */
	public void processProvisions(CustEODEvent custEODEvent) {
		String strCustId = custEODEvent.getCustIdAsString();
		logger.info("Provision Calculation started for the Customer ID {}", strCustId);

		Date eodDate = custEODEvent.getEodDate();
		Date monthEnd = DateUtil.getMonthEnd(eodDate);
		if (DateUtil.compare(eodDate, monthEnd) == 0) {
			return;
		}
		logger.info("Provision Calculation started...");

		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();
		Date valueDate = custEODEvent.getEodValueDate();

		String provisionBooks = custEODEvent.getProvisionBooks();

		boolean customerProvision = custEODEvent.isCustomerProvision();
		String custCtgCode = custEODEvent.getCustomer().getCustCtgCode();
		if (customerProvision) {
			Provision provision = getMaxProvisionAsset(finEODEvents, provisionBooks);

			if (provision == null) {
				logger.info("Provision not found for the customer.");
				return;
			}

			for (FinEODEvent finEODEvent : finEODEvents) {
				Provision provisionDet = new Provision();

				provisionDet.setProvisionRate(provision.getProvisionRate());
				provisionDet.setAssetCode(provision.getAssetCode());
				provisionDet.setAssetStageOrder(provision.getAssetStageOrder());
				provisionDet.setCollateralValue(provision.getCollateralValue());
				provisionDet.setNpa(provision.isNpa());
				provisionDet.setManualProvision(false);
				provisionDet.getProvisionAmounts().addAll(provision.getProvisionAmounts());

				if (provision.getRuleId() == null) {
					return;
				}
				provisionDet.setRuleId(provision.getRuleId());

				findProvision(finEODEvent, valueDate, provisionBooks, provisionDet);
			}
		} else {
			for (FinEODEvent finEODEvent : finEODEvents) {
				finEODEvent.getFinanceMain().setLovDescCustCtgCode(custCtgCode);
				findProvision(finEODEvent, valueDate, provisionBooks, null);
			}
		}

		logger.info("Provision Calculation completd.");
	}

	private FinEODEvent findProvision(FinEODEvent finEODEvent, Date valueDate, String provisionBooks,
			Provision provision) {
		long finID = finEODEvent.getFinProfitDetail().getFinID();

		FinanceMain fm = finEODEvent.getFinanceMain();
		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		Provision oldProvision = provisionDAO.getProvisionByFinId(finID, TableType.MAIN_TAB, false);
		if (oldProvision != null && oldProvision.isManualProvision()) {
			return finEODEvent;
		}

		if (pftDetail.isWriteoffLoan()) {
			return finEODEvent;
		}

		if (provision == null) {
			provision = getProvision(finEODEvent, provisionBooks);
		}

		if (provision == null) {
			return finEODEvent;
		}

		provision.setFinReference(pftDetail.getFinReference());
		provision.setFinBranch(pftDetail.getFinBranch());
		provision.setFinType(pftDetail.getFinType());
		provision.setCustID(pftDetail.getCustId());
		provision.setProvisionDate(valueDate);
		provision.setProduct(pftDetail.getFinCategory());
		provision.setOverDuePrincipal(pftDetail.getODPrincipal());
		provision.setOverDueProfit(pftDetail.getODProfit());
		provision.setFutureRpyPri(pftDetail.getFutureRpyPri());
		provision.setCustCtgCode(fm.getLovDescCustCtgCode());
		provision.setUnDisbursedAmount(fm.getFinAssetValue().subtract(fm.getFinCurrAssetValue()));
		Date lastFullypaid = pftDetail.getFullPaidDate();

		if (lastFullypaid == null) {
			lastFullypaid = pftDetail.getFinStartDate();
		}

		provision.setLastFullyPaidDate(lastFullypaid);
		provision.setDueFromDate(pftDetail.getPrvODDate());
		provision.setDueDays(pftDetail.getCurODDays());

		provision.setOutStandPrincipal(pftDetail.getTotalPriBal());
		provision.setOutStandProfit(pftDetail.getTotalPftBal());

		List<FinanceScheduleDetail> schdules = finEODEvent.getFinanceScheduleDetails();
		calculateProvisionAmount(valueDate, schdules, provision);

		provision.setProfitAccruedAndNotDue(pftDetail.getAmzTillLBD());
		// Executing accounting process
		setAssetMovement(provision, oldProvision, valueDate);

		provision.setCurrBucket(pftDetail.getDueBucket());
		provision.setDpd(pftDetail.getCurODDays());
		provision.setLastMntBy(1000);
		provision.setLastMntOn(new Timestamp(System.currentTimeMillis()));

		finEODEvent.getFinProfitDetail().setProvision(true);
		finEODEvent.getProvisions().add(provision);

		return finEODEvent;
	}

	private void calculateProvisionAmount(Date valueDate, List<FinanceScheduleDetail> schdules, Provision provision) {
		Date monthStart = DateUtil.getMonthStart(valueDate);
		Date monthEnd = DateUtil.getMonthEnd(valueDate);

		BigDecimal provisionedAmt = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schdules) {
			if (schd.getSchDate().compareTo(monthStart) >= 0 && schd.getSchDate().compareTo(monthEnd) <= 0) {
				provisionedAmt = schd.getClosingBalance();
				break;
			}
		}

		BigDecimal principalDue = BigDecimal.ZERO;
		BigDecimal profitAccruedAndDue = BigDecimal.ZERO;

		for (FinanceScheduleDetail schd : schdules) {
			if (schd.getSchDate().compareTo(valueDate) <= 0) {
				principalDue = principalDue.add(schd.getPrincipalSchd());
				principalDue = principalDue.subtract(schd.getSchdPriPaid());

				profitAccruedAndDue = profitAccruedAndDue.add(schd.getProfitSchd());
				profitAccruedAndDue = profitAccruedAndDue.subtract(schd.getSchdPftPaid());
				profitAccruedAndDue = profitAccruedAndDue.subtract(schd.getSchdPftWaiver());

			}
		}
		provisionedAmt = provisionedAmt.add(principalDue).add(profitAccruedAndDue);
		provision.setClosingBalance(provisionedAmt);
		provision.setProfitAccruedAndDue(profitAccruedAndDue);
		Rule provRule = ruleDAO.getRuleByID(provision.getRuleId(), "");

		Map<String, Object> dataMap = new HashMap<>();

		dataMap.put("custCtgCode", provision.getCustCtgCode());
		dataMap.put("DueBucket", provision.getCurrBucket());
		dataMap.put("ODDays", provision.getDueDays());
		dataMap.put("Product", provision.getProduct());
		dataMap.put("reqFinType", provision.getFinType());

		dataMap.put("SecuredLoan", provision.isSecured());
		dataMap.put("AssetStage", provision.getAssetStageOrder());
		dataMap.put("InsuranceAmount", provision.getInsuranceAmount());

		dataMap.put("OutstandingAmount", provision.getClosingBalance());
		dataMap.put("FuturePrincipal", provision.getFutureRpyPri());
		dataMap.put("OverduePrincipal", provision.getOverDuePrincipal());
		dataMap.put("OverdueInterest", provision.getOverDueProfit());
		dataMap.put("UndisbursedAmount", provision.getUnDisbursedAmount()); // FIX ME

		getPropertyType(provision, dataMap);

		Object result = RuleExecutionUtil.executeRule(provRule.getSQLRule(), dataMap, provision.getFinCcy(),
				RuleReturnType.OBJECT);
		RuleResult ruleResult = (RuleResult) result;
		Object provisionAmount = ruleResult.getProvAmount();
		Object percentage = ruleResult.getProvPercentage();

		BigDecimal provisionRate = new BigDecimal(percentage != null ? percentage.toString() : "0");
		provision.setProvisionRate(provisionRate);
		BigDecimal provisionAmt = new BigDecimal(provisionAmount != null ? provisionAmount.toString() : "0");
		provision.setProvisionedAmt(provisionAmt);

		List<ProvisionAmount> provisionAmountsList = provision.getProvisionAmounts();
		List<ProvisionAmount> insuranceProvAmountList = new ArrayList<>();
		for (ProvisionAmount provisonAmt : provisionAmountsList) {
			Rule rule = ruleDAO.getRuleByID(provisonAmt.getRuleId(), "");

			Object ruleResultObj = RuleExecutionUtil.executeRule(rule.getSQLRule(), dataMap, provision.getFinCcy(),
					RuleReturnType.OBJECT);
			RuleResult ruleResult1 = (RuleResult) ruleResultObj;

			Object provPercentage = ruleResult1.getProvPercentage() != null ? ruleResult1.getProvPercentage() : "0";
			provisonAmt.setProvisionPer(new BigDecimal(provPercentage.toString()));
			Object provAmount = ruleResult1.getProvAmount() != null ? ruleResult1.getProvAmount() : "0";
			provisonAmt.setProvisionAmtCal(new BigDecimal(provAmount.toString()));

			Object vasProvAmount = ruleResult1.getVasProvAmount();
			if (provision.isInsuranceComponent() && vasProvAmount != null) {
				ProvisionAmount pa = new ProvisionAmount();
				pa.setAssetCode(provisonAmt.getAssetCode());
				pa.setProvisionType(provisonAmt.getProvisionType() + "_INS");

				Object vasProvPerc = ruleResult1.getVasProvPercentage();
				Object vasProvPercentage = vasProvPerc != null ? vasProvPerc : "0";

				pa.setProvisionPer(new BigDecimal(vasProvPercentage.toString()));
				pa.setProvisionAmtCal(new BigDecimal(vasProvAmount.toString()));
				insuranceProvAmountList.add(pa);
			}

		}
		provisionAmountsList.addAll(insuranceProvAmountList);
	}

	private void getPropertyType(Provision provision, Map<String, Object> dataMap) {
		if (!ImplementationConstants.ALLOW_ED_FIELDS_IN_NPA || extendedFieldServiceHook == null
				|| extendedFieldRenderDAO == null) {
			if (!dataMap.containsKey("PropertyType")) {
				dataMap.put("PropertyType", provision.getPropertyType());
			}
			return;
		}

		List<CollateralAssignment> colAssRef;
		String moduleName = FinanceConstants.MODULE_NAME;
		String reference = provision.getFinReference();

		colAssRef = collateralAssignmentDAO.getCollateralAssignmentByFinRef(reference, moduleName, "_AView");
		colAssRef.addAll(collateralAssignmentDAO.getCollateralAssignmentByFinRef(reference, moduleName, "_CTView"));

		for (CollateralAssignment ca : colAssRef) {
			String tableName = CollateralConstants.MODULE_NAME + "_" + ca.getCollateralType() + "_ED";
			String colRef = ca.getCollateralRef();
			extendedFieldServiceHook.setExtendedFields(dataMap, CollateralConstants.MODULE_NAME, tableName, colRef);
		}
	}

	private Provision getProvision(FinEODEvent finEODEvent, String provisionBooks) {
		FinanceProfitDetail pftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain financeMain = finEODEvent.getFinanceMain();
		String finType = financeMain.getFinType();
		int dpdDays = pftDetail.getCurODDays();
		NPAProvisionDetail provisionDetail = null;
		Provision provision = null;
		BigDecimal insuranceAmount = BigDecimal.ZERO;
		boolean isInsuranceComponent = false;
		boolean isSecured = this.collateralAssignmentDAO.isSecuredLoan(finEODEvent.getFinanceMain().getFinReference(),
				TableType.MAIN_TAB);

		List<VASRecording> vASRecordingsList = vASRecordingDAO.getVASRecordingsByLinkRef(financeMain.getFinReference(),
				"");

		if (CollectionUtils.isNotEmpty(vASRecordingsList)) {

			for (VASRecording vasRecording : vASRecordingsList) {
				insuranceAmount = insuranceAmount.add(vasRecording.getFee());
			}
			isInsuranceComponent = true;
		}

		List<NPAProvisionHeader> provisionHeaders = nPAProvisionHeaderDAO.getNPAProvisionsListByFintype(finType,
				TableType.AVIEW);

		// If Provision details are not available against the loan type.
		if (CollectionUtils.isEmpty(provisionHeaders)) {
			return provision;
		}

		long provisionBookHeaderCount = provisionHeaders.stream()
				.filter(header -> StringUtils.equals(header.getNpaTemplateCode(), provisionBooks)).count();

		// If Provision Header not available for provision Book.
		if (provisionBookHeaderCount == 0) {
			return provision;
		}

		for (NPAProvisionHeader provisionHeader : provisionHeaders) {

			List<NPAProvisionDetail> provisionDetails = this.nPAProvisionDetailDAO
					.getNPAProvisionDetailList(provisionHeader.getId(), TableType.AVIEW);

			List<NPAProvisionDetail> activeProvisionDetails = provisionDetails.stream()
					.filter(provDtl -> provDtl.isActive()).collect(Collectors.toList());

			// If there are no active provision details available.
			if (CollectionUtils.isEmpty(activeProvisionDetails)) {
				continue;
			}

			for (int i = 0; i < activeProvisionDetails.size() - 1; i++) {

				if (dpdDays == activeProvisionDetails.get(i + 1).getDPDdays()) {
					provisionDetail = activeProvisionDetails.get(i + 1);
					break;

				}

				if (dpdDays == activeProvisionDetails.get(i).getDPDdays()) {
					provisionDetail = activeProvisionDetails.get(i);
					break;

				}

				if (i == 0 && dpdDays < activeProvisionDetails.get(i).getDPDdays()) {
					provisionDetail = activeProvisionDetails.get(i);
					break;

				}

				if (dpdDays > activeProvisionDetails.get(i).getDPDdays()
						&& dpdDays < activeProvisionDetails.get(i + 1).getDPDdays()) {
					provisionDetail = activeProvisionDetails.get(i + 1);
					break;
				}
			}

			if (provisionDetail == null) {

				if (StringUtils.equals(provisionHeader.getNpaTemplateCode(), provisionBooks)) {
					return null;
				} else
					continue;
			}

			if (provision == null) {
				provision = new Provision();
			}

			setProvisionAmounts(provisionHeader, provision, provisionDetail);

			if (StringUtils.equals(provisionHeader.getNpaTemplateCode(), provisionBooks)) {
				provision.setAssetCode(provisionDetail.getAssetCode());
				provision.setAssetStageOrder(provisionDetail.getAssetStageOrder());
				provision.setNpa(provisionDetail.isNPAActive());
				provision.setManualProvision(false);

				if (provisionDetail.getRuleId() == null) {
					return null;
				}

				provision.setRuleId(provisionDetail.getRuleId());
				provision.setNpaTemplateId(provisionHeader.getNpaTemplateId());

				if (isSecured) {
					provision = setCollateralValue(finEODEvent, provision);
					provision.setSecured(true);
				}

				if (isInsuranceComponent) {
					provision.setInsuranceAmount(insuranceAmount);
					provision.setInsuranceComponent(isInsuranceComponent);
				}

			}
		}

		return provision;
	}

	private void setProvisionAmounts(NPAProvisionHeader provisionHeader, Provision provision,
			NPAProvisionDetail provisionDetail) {
		ProvisionAmount pa = null;
		pa = new ProvisionAmount();
		pa.setAssetCode(provisionDetail.getAssetCode());
		pa.setRuleId(provisionDetail.getRuleId());
		pa.setProvisionType(provisionHeader.getNpaTemplateCode());
		provision.getProvisionAmounts().add(pa);
	}

	private void setAssetMovement(Provision provision, Provision oldProvision, Date eodValueDate) {
		// Getting Old Provision details
		if (oldProvision == null) {
			provision.setAssetFwdMov(provision.isNpa());
		} else {
			if (!oldProvision.isNpa() && provision.isNpa()) {
				provision.setAssetFwdMov(true);
			} else if (oldProvision.isNpa() && !provision.isNpa()) {
				provision.setAssetBkwMov(true);
			}
		}
	}

	private Provision setCollateralValue(FinEODEvent finEODEvent, Provision provision) {
		provision.setCollateralValue(
				this.collateralAssignmentDAO.getCollateralValue(finEODEvent.getFinanceMain().getFinReference()));
		return provision;
	}

	/**
	 * Checking the If NPA repay Heirarchy Required or not against the loan type
	 * 
	 * @param finReference
	 * @return
	 */
	public boolean isNAPRepayHierarchyReq(long finID) {
		logger.debug(Literal.ENTERING);

		Provision provision = provisionDAO.getProvisionByFinId(finID, TableType.AVIEW, false);
		if (provision == null) {
			return false;
		}

		NPAProvisionHeader provisionHeader = nPAProvisionHeaderDAO.getNPAProvisionByFintype(provision.getFinType(),
				provision.getNpaTemplateId(), TableType.MAIN_TAB);

		// If Provision details are not available against the loan type.
		if (provisionHeader == null) {
			return false;
		}

		List<NPAProvisionDetail> provisionDetails = this.nPAProvisionDetailDAO
				.getNPAProvisionDetailList(provisionHeader.getId(), TableType.AVIEW);

		// If Provision Percentages are not available against the loan type.
		if (CollectionUtils.isEmpty(provisionDetails)) {
			return false;
		}

		NPAProvisionDetail npaProvisionDetail = null;
		for (NPAProvisionDetail detail : provisionDetails) {
			if (StringUtils.equals(detail.getAssetCode(), provision.getAssetCode())) {
				npaProvisionDetail = detail;
				break;
			}
		}

		if (npaProvisionDetail == null) {
			return false;
		}

		if (PennantConstants.YES.equals(npaProvisionDetail.getNPARepayApprtnmnt())) {
			return true;
		}

		logger.debug(Literal.LEAVING);
		return false;
	}

	private Provision getMaxProvisionAsset(List<FinEODEvent> finEODEvents, String provisionBooks) {
		Provision maxProvision = new Provision();

		for (FinEODEvent finEODEvent : finEODEvents) {
			Provision provision = getProvision(finEODEvent, provisionBooks);

			if (provision == null) {
				continue;
			}

			if (provision.getAssetStageOrder() > maxProvision.getAssetStageOrder()) {
				maxProvision.setAssetCode(provision.getAssetCode());
				maxProvision.setAssetStageOrder(provision.getAssetStageOrder());
				maxProvision.setNpa(provision.isNpa());
				maxProvision.setProvisionRate(provision.getProvisionRate());
				maxProvision.setCollateralValue(provision.getCollateralValue());
				maxProvision.getProvisionAmounts().clear();
				maxProvision.getProvisionAmounts().addAll(provision.getProvisionAmounts());
			} else if (provision.getAssetStageOrder() == maxProvision.getAssetStageOrder()) {
				if (provision.isNpa() && !maxProvision.isNpa()) {
					maxProvision.setAssetCode(provision.getAssetCode());
					maxProvision.setAssetStageOrder(provision.getAssetStageOrder());
					maxProvision.setProvisionRate(provision.getProvisionRate());
					maxProvision.setNpa(provision.isNpa());
					maxProvision.setCollateralValue(provision.getCollateralValue());
					maxProvision.getProvisionAmounts().clear();
					maxProvision.getProvisionAmounts().addAll(provision.getProvisionAmounts());
				}
			}
		}

		if (StringUtils.trimToNull(maxProvision.getAssetCode()) == null) {
			return null;
		}
		return maxProvision;
	}

	public void processAccounting(CustEODEvent custEODEvent) {
		List<FinEODEvent> finEODEvents = custEODEvent.getFinEODEvents();

		Date eodDate = custEODEvent.getEodDate();
		Date monthEnd = DateUtil.getMonthEnd(eodDate);
		if (DateUtil.compare(eodDate, monthEnd) != 0) {
			return;
		}

		for (FinEODEvent finEODEvent : finEODEvents) {
			List<Provision> provisions = finEODEvent.getProvisions();

			if (CollectionUtils.isEmpty(provisions)) {
				continue;
			}

			for (Provision provision : provisions) {

				// Process Provision Accounting.
				finEODEvent = processProvAccounting(finEODEvent, custEODEvent, provision);

				// Process Provision Accounting Reversal.
				finEODEvent = processProvAccountingReversal(finEODEvent, custEODEvent, provision);

				// Process NPAChange Accounting.
				finEODEvent = processNPAChgAccounting(finEODEvent, custEODEvent, provision);
			}
		}
	}

	private FinEODEvent processProvAccounting(FinEODEvent finEODEvent, CustEODEvent custEODEvent, Provision provision) {

		String eventCode = AccountingEvent.PROVSN;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		Long accountingID = getAccountingID(main, eventCode);
		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return finEODEvent;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, finPftDetail, finEODEvent.getFinanceScheduleDetails(),
				eventCode, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		EventProperties eventProperties = main.getEventProperties();
		if (eventProperties.isParameterLoaded()) {
			aeEvent.setValueDate(eventProperties.getPostDate());
		} else {
			aeEvent.setValueDate(SysParamUtil.getPostDate());
		}

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		postAccountingEOD(aeEvent);

		provision.setLinkedTranId(aeEvent.getLinkedTranId());
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		return finEODEvent;
	}

	private FinEODEvent processProvAccountingReversal(FinEODEvent finEODEvent, CustEODEvent custEODEvent,
			Provision provision) {

		String eventCode = AccountingEvent.PRVSN_MN;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		Long accountingID = getAccountingID(main, eventCode);
		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return finEODEvent;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, finPftDetail, finEODEvent.getFinanceScheduleDetails(),
				eventCode, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());

		aeEvent.setValueDate(custEODEvent.getProvisionEffectiveDate());
		if (aeEvent.getValueDate() == null) {
			aeEvent.setValueDate(custEODEvent.getEodValueDate());
		}

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		postAccountingEOD(aeEvent);

		provision.setLinkedTranId(aeEvent.getLinkedTranId());
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		return finEODEvent;
	}

	private FinEODEvent processNPAChgAccounting(FinEODEvent finEODEvent, CustEODEvent custEODEvent,
			Provision provision) {

		if (!provision.isAssetFwdMov()) {
			return finEODEvent;
		}

		AEEvent aeEvent = executeNPAAccounting(finEODEvent, custEODEvent, provision);

		if (aeEvent == null) {
			return finEODEvent;
		}

		provision.setChgLinkedTranId(aeEvent.getLinkedTranId());
		postAccountingEOD(aeEvent);
		finEODEvent.getReturnDataSet().addAll(aeEvent.getReturnDataSet());

		return finEODEvent;
	}

	private AEEvent executeNPAAccounting(FinEODEvent finEODEvent, CustEODEvent custEODEvent, Provision provision) {

		String eventCode = AccountingEvent.PROVCHG;
		FinanceProfitDetail finPftDetail = finEODEvent.getFinProfitDetail();
		FinanceMain main = finEODEvent.getFinanceMain();

		Long accountingID = getAccountingID(main, eventCode);
		if (accountingID == null || accountingID == Long.MIN_VALUE) {
			logger.debug(" Leaving. Accounting Not Found");
			return null;
		}

		AEEvent aeEvent = AEAmounts.procCalAEAmounts(main, finPftDetail, finEODEvent.getFinanceScheduleDetails(),
				eventCode, custEODEvent.getEodValueDate(), custEODEvent.getEodValueDate());
		// Old
		// -- PFTAMZ(amz) - TDSCHDPFT(pftS)
		// --TDSCHDPFTBAL(PftSB)
		// --TDSCHDPFTBAL(pftSB)+TOTALPRIBAL(priAB)
		// --TOTALPRIBAL(priAB)-TDSCHDPRI(priS)

		// New
		// TDSCHDPFTBAL(pftSB)+TDSCHDPRIBAL(PriSB) //1 Leg
		// TDSCHDPFTBAL(PftSB) //2 Leg
		// TOTALPRISCHD(pri) - TDSCHDPRI(priS)//3 Leg

		AEAmountCodes aeAmountCodes = aeEvent.getAeAmountCodes();
		aeAmountCodes.setProvAmt(provision.getProvisionedAmt());

		aeEvent.setDataMap(aeAmountCodes.getDeclaredFieldValues());
		aeEvent.getAcSetIDList().add(accountingID);
		aeEvent.setCustAppDate(custEODEvent.getCustomer().getCustAppDate());
		return aeEvent;
	}

	// Getters and setters
	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public void setnPAProvisionHeaderDAO(NPAProvisionHeaderDAO nPAProvisionHeaderDAO) {
		this.nPAProvisionHeaderDAO = nPAProvisionHeaderDAO;
	}

	public void setnPAProvisionDetailDAO(NPAProvisionDetailDAO nPAProvisionDetailDAO) {
		this.nPAProvisionDetailDAO = nPAProvisionDetailDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public ExtendedFieldService getExtendedFieldServiceHook() {
		return extendedFieldServiceHook;
	}

	@Autowired(required = false)
	public void setExtendedFieldServiceHook(ExtendedFieldService extendedFieldServiceHook) {
		this.extendedFieldServiceHook = extendedFieldServiceHook;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}
}
