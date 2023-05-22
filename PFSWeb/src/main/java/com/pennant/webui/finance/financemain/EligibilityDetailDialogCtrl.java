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
 * * FileName : EligibilityDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.backend.service.finance.EligibilityDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.webui.delegationdeviation.DeviationExecutionCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file.
 */
public class EligibilityDetailDialogCtrl extends GFCBaseCtrl<FinanceEligibilityDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(EligibilityDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_EligibilityDetailDialog; // autoWired
	protected Borderlayout borderlayoutEligibilityDetail; // autoWired

	// Finance Eligibility Details Tab
	protected Button btnElgRule; // autoWired
	protected Label label_ElgRuleSummaryVal; // autoWired
	protected Listbox listBoxFinElgRef; // autoWired

	List<FinanceEligibilityDetail> eligibilityRuleList = null;

	// External Fields usage for Individuals ----> Eligibility Details

	private transient boolean custisEligible = true;
	private boolean isWIF = false;

	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	private FinanceMain financeMain = null;

	private EligibilityDetailService eligibilityDetailService;
	private CurrencyService currencyService;
	private RuleService ruleService;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	protected Groupbox finBasicdetails;
	DeviationExecutionCtrl deviationExecutionCtrl;

	/**
	 * default constructor.<br>
	 */
	public EligibilityDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_EligibilityDetailDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_EligibilityDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
			setFinanceMain(getFinScheduleData().getFinanceMain());
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		if (arguments.containsKey("roleCode")) {
			// this.userRole = arguments.get("roleCode").toString();
		}

		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog() {
		logger.debug("Entering");
		try {
			// append finance basic details
			appendFinBasicDetails();

			eligibilityRuleList = getFinanceDetail().getElgRuleList();
			if (isWIF) {
				doCheckWIFFinEligibility(false);
			} else {

				deviationExecutionCtrl = (DeviationExecutionCtrl) getFinanceMainDialogCtrl().getClass()
						.getMethod("getDeviationExecutionCtrl").invoke(getFinanceMainDialogCtrl());

				// Set Eligibility based on deviations and rule result
				for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
					setStatusByDevaition(financeEligibilityDetail);
				}

				// Fill eligibility details
				doFillFinEligibilityDetails(eligibilityRuleList);

				// Set eligibility grtoup status
				setCustEligibilityGropuStatus();
			}

			try {
				getFinanceMainDialogCtrl().getClass().getMethod("setEligibilityDetailDialogCtrl", this.getClass())
						.invoke(financeMainDialogCtrl, this);
			} catch (InvocationTargetException e) {
				logger.error("Exception: ", e);
			}
			getBorderLayoutHeight();
			this.listBoxFinElgRef.setHeight(this.borderLayoutHeight - 210 - 52 + "px");
			this.window_EligibilityDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Executed Eligibility Details
	 * 
	 * @param eligibilityDetails
	 */
	public void doFillFinEligibilityDetails(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");

		this.listBoxFinElgRef.getItems().clear();
		String overridePerc = "";
		if (eligibilityDetails != null && !eligibilityDetails.isEmpty()) {
			for (FinanceEligibilityDetail detail : eligibilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				// Rule Source
				lc = new Listcell("");
				lc.setParent(item);

				// Rule Code
				lc = new Listcell(detail.getLovDescElgRuleCode());
				lc.setParent(item);

				// Rule Code Desc
				lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
				lc.setParent(item);

				// Can Override
				lc = new Listcell();
				Checkbox cbOverride = new Checkbox();
				cbOverride.setDisabled(true);

				if (detail.isCanOverride()) {
					cbOverride.setChecked(true);
					overridePerc = String.valueOf(detail.getOverridePerc());
				} else {
					cbOverride.setChecked(false);
					overridePerc = "";
				}
				lc.appendChild(cbOverride);
				lc.setParent(item);

				// Override Value
				lc = new Listcell(overridePerc);
				lc.setParent(item);

				// If Rule Not Executed
				// Bug Fix Change
				if (StringUtils.isEmpty(detail.getRuleResult())) {

					lc = new Listcell("");
					lc.setParent(item);

				} else {

					String labelCode = "";
					String StyleCode = "";

					if (detail.isEligible()) {
						if (detail.isEligibleWithDevaition()) {
							labelCode = Labels.getLabel("common.Eligible_Deviation");
						} else {
							labelCode = Labels.getLabel("common.Eligible");
						}
						StyleCode = "font-weight:bold;color:green;";

					} else {
						labelCode = Labels.getLabel("common.Ineligible");
						StyleCode = "font-weight:bold;color:red;";
					}

					if (RuleConstants.RETURNTYPE_DECIMAL.equals(detail.getRuleResultType())) {
						if (RuleConstants.ELGRULE_DSRCAL.equals(detail.getLovDescElgRuleCode())
								|| RuleConstants.ELGRULE_PDDSRCAL.equals(detail.getLovDescElgRuleCode())) {
							BigDecimal val = PennantApplicationUtil.getDSR(detail.getRuleResult());
							val = val.setScale(2, RoundingMode.HALF_DOWN);
							labelCode = String.valueOf(val) + "%";
						} else {
							labelCode = CurrencyUtil.format(new BigDecimal(detail.getRuleResult()),
									CurrencyUtil.getFormat(getFinanceMain().getFinCcy()));
						}

						StyleCode = "text-align:right;";
					}

					lc = new Listcell(labelCode);
					lc.setStyle(StyleCode);
					lc.setParent(item);

				}

				lc = new Listcell("");
				lc.setParent(item);

				lc = new Listcell("");
				lc.setParent(item);

				listBoxFinElgRef.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	private void setStatusByDevaition(FinanceEligibilityDetail finElgDet) {

		FinanceDeviations deviation = null;

		List<FinanceDeviations> list = deviationExecutionCtrl.getFinanceDeviations();
		if (list != null && !list.isEmpty()) {
			for (FinanceDeviations financeDeviations : list) {
				if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
					deviation = financeDeviations;
					break;
				}
			}
		}
		if (deviation == null) {
			List<FinanceDeviations> approvedList = getFinanceDetail().getApprovedFinanceDeviations();
			if (approvedList != null && !approvedList.isEmpty()) {

				for (FinanceDeviations financeDeviations : approvedList) {
					if (PennantConstants.RCD_STATUS_REJECTED
							.equals(StringUtils.trimToEmpty(financeDeviations.getApprovalStatus()))) {
						continue;
					}
					if (financeDeviations.getDeviationCode().equals(String.valueOf(finElgDet.getElgRuleCode()))) {
						deviation = financeDeviations;
						break;
					}
				}
			}
		}

		if (deviation != null) {
			if ("".equals(deviation.getDelegationRole())) {
				finElgDet.setEligible(false);
			} else {
				finElgDet.setEligible(true);
				finElgDet.setEligibleWithDevaition(true);
			}

		} else {
			finElgDet.setEligibleWithDevaition(false);
			String ruleResult = StringUtils.trimToEmpty(finElgDet.getRuleResult());
			if (StringUtils.isEmpty(ruleResult) || "0".equals(ruleResult) || "0.0".equals(ruleResult)
					|| "0.00".equals(ruleResult)) {
				finElgDet.setEligible(false);
			} else {
				finElgDet.setEligible(true);
			}
		}
	}

	/**
	 * Method for Executing Finance Eligibility Details List
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnElgRule(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug(Literal.ENTERING);

		if (isWIF) {
			doCheckWIFFinEligibility(true);
		} else {
			boolean custValidated = true;

			custValidated = (Boolean) getFinanceMainDialogCtrl().getClass().getMethod("doCustomerValidation")
					.invoke(getFinanceMainDialogCtrl());
			setFinanceDetail((FinanceDetail) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceDetail")
					.invoke(getFinanceMainDialogCtrl()));
			custValidated = (Boolean) getFinanceMainDialogCtrl().getClass().getMethod("doExtendedDetailsValidation")
					.invoke(getFinanceMainDialogCtrl());
			custValidated = (Boolean) getFinanceMainDialogCtrl().getClass().getMethod("doPSLDetailsValidation")
					.invoke(getFinanceMainDialogCtrl());

			if (custValidated) {
				doCheckFinEligibility(false);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Click event for eligibility
	 * 
	 * @param isSave
	 */
	public void doCheckFinEligibility(boolean isSave) {
		logger.debug(Literal.ENTERING);

		// Clear eligibility summary status.
		this.label_ElgRuleSummaryVal.setValue("");

		// Prepare the data for rules.
		FinanceDetail aFinanceDetail = null;

		try {
			Object object = getFinanceMainDialogCtrl().getClass().getMethod("prepareCustElgDetail", Boolean.class)
					.invoke(getFinanceMainDialogCtrl(), false);
			if (object != null) {
				aFinanceDetail = (FinanceDetail) object;
			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		// Move the sub rules to the top.
		List<String> subruleCodes = PennantStaticListUtil.getSubrules();
		List<FinanceEligibilityDetail> subrules = new ArrayList<>();
		List<FinanceEligibilityDetail> otherRules = new ArrayList<>();

		for (FinanceEligibilityDetail eligibility : eligibilityRuleList) {
			if (subruleCodes.contains(eligibility.getLovDescElgRuleCode())) {
				subrules.add(eligibility);
			} else {
				otherRules.add(eligibility);
			}
		}

		List<FinanceEligibilityDetail> eligibilityRules = new ArrayList<>(eligibilityRuleList.size());
		eligibilityRules.addAll(subrules);
		eligibilityRules.addAll(otherRules);

		List<FinanceDeviations> elgDeviations = new ArrayList<>();

		for (FinanceEligibilityDetail eligibility : eligibilityRules) {
			if (eligibility.isExecute()) {
				if (!isSave) {
					eligibility.setUserOverride(false);
				}

				FinanceDeviations deviation = doExecuteAndCheckDeviations(eligibility, aFinanceDetail);
				if (eligibility.isAllowDeviation() && deviation != null) {
					elgDeviations.add(deviation);
				}
			} else {
				// Consider the existing deviation.
				for (FinanceDeviations deviation : deviationExecutionCtrl.getFinanceDeviations()) {
					if (StringUtils.equals(deviation.getModule(), DeviationConstants.TY_ELIGIBILITY) && StringUtils
							.equals(deviation.getDeviationCode(), String.valueOf(eligibility.getElgRuleCode()))) {
						elgDeviations.add(deviation);

						break;
					}
				}
			}

			if (StringUtils.equals(RuleConstants.ELGRULE_DSRCAL, eligibility.getLovDescElgRuleCode())) {
				aFinanceDetail.getCustomerEligibilityCheck()
						.setDSCR(PennantApplicationUtil.getDSR(eligibility.getRuleResult()));
				getFinanceDetail().setCustomerEligibilityCheck(aFinanceDetail.getCustomerEligibilityCheck());
			} else if (StringUtils.equals(RuleConstants.ELGRULE_FOIR, eligibility.getLovDescElgRuleCode())) {
				if (eligibility.getRuleResult() == null) {
					aFinanceDetail.getCustomerEligibilityCheck().setFoir(BigDecimal.ZERO);
				} else {
					aFinanceDetail.getCustomerEligibilityCheck().setFoir(new BigDecimal(eligibility.getRuleResult()));
				}
				getFinanceDetail().setCustomerEligibilityCheck(aFinanceDetail.getCustomerEligibilityCheck());
			} else if (StringUtils.equals(RuleConstants.ELGRULE_LTV, eligibility.getLovDescElgRuleCode())) {
				if (eligibility.getRuleResult() == null) {
					aFinanceDetail.getCustomerEligibilityCheck().setLtv(BigDecimal.ZERO);
				} else {
					aFinanceDetail.getCustomerEligibilityCheck().setLtv(new BigDecimal(eligibility.getRuleResult()));
				}
				getFinanceDetail().setCustomerEligibilityCheck(aFinanceDetail.getCustomerEligibilityCheck());
			} else if (subruleCodes.contains(eligibility.getLovDescElgRuleCode())) {
				getFinanceDetail().getCustomerEligibilityCheck()
						.addExtendedField("RULE_" + eligibility.getLovDescElgRuleCode(), eligibility.getRuleResult());
			}
		}

		deviationExecutionCtrl.fillDeviationListbox(elgDeviations, getUserRole(), DeviationConstants.TY_ELIGIBILITY);

		// Set Eligibility based on deviations and rule result
		for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
			setStatusByDevaition(financeEligibilityDetail);
		}

		// Fill eligibility details
		doFillFinEligibilityDetails(eligibilityRuleList);

		// Set eligibility group status
		setCustEligibilityGropuStatus();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Checking Deviation on Execution Process
	 * 
	 * @param finElgDet
	 * @param aFinanceDetail
	 * @return
	 */
	public FinanceDeviations doExecuteAndCheckDeviations(FinanceEligibilityDetail finElgDet,
			FinanceDetail aFinanceDetail) {

		CustomerEligibilityCheck customerEligibilityCheck = aFinanceDetail.getCustomerEligibilityCheck();
		String finCcy = aFinanceDetail.getFinScheduleData().getFinanceMain().getFinCcy();

		BigDecimal finAmount = customerEligibilityCheck.getReqFinAmount();
		customerEligibilityCheck.setReqFinAmount(CalculationUtil.getConvertedAmount(finCcy, null, finAmount));
		customerEligibilityCheck.setDisbursedAmount(
				CalculationUtil.getConvertedAmount(finCcy, null, customerEligibilityCheck.getDisbursedAmount()));

		BigDecimal downpaybank = customerEligibilityCheck.getDownpayBank();
		customerEligibilityCheck.setDownpayBank(CalculationUtil.getConvertedAmount(finCcy, null, downpaybank));
		BigDecimal downpaySupl = customerEligibilityCheck.getDownpaySupl();
		customerEligibilityCheck.setDownpaySupl(CalculationUtil.getConvertedAmount(finCcy, null, downpaySupl));

		BigDecimal zero = BigDecimal.ZERO;
		BigDecimal finAssetValue = zero;
		BigDecimal totalAssgnedCollAmount = zero;

		if (customerEligibilityCheck.getCurrentAssetValue() != null) {
			finAssetValue = customerEligibilityCheck.getCurrentAssetValue();
			finAssetValue = PennantApplicationUtil.formateAmount(finAssetValue, PennantConstants.defaultCCYDecPos);
		}

		if (customerEligibilityCheck.getExtendedValue("Collaterals_Total_Assigned") != null) {
			totalAssgnedCollAmount = (BigDecimal) customerEligibilityCheck
					.getExtendedValue("Collaterals_Total_Assigned");
		}

		try {
			BigDecimal collPrec = finAssetValue.divide(totalAssgnedCollAmount, PennantConstants.defaultCCYDecPos,
					RoundingMode.HALF_UP);
			collPrec = collPrec.multiply(new BigDecimal(100));
			customerEligibilityCheck.addExtendedField("Coll_Assign_Percentage", collPrec);
		} catch (Exception e) {
			customerEligibilityCheck.addExtendedField("Coll_Assign_Percentage", 0);
		}

		FinanceDeviations deviation = deviationExecutionCtrl.checkEligibilityDeviations(finElgDet, aFinanceDetail);
		customerEligibilityCheck.setReqFinAmount(finAmount);

		return deviation;
	}

	/**
	 * updates the status of the customer to True/False
	 * 
	 * @param
	 * @return void
	 */
	public void setCustEligibilityGropuStatus() {
		logger.debug("Entering");
		setCustisEligible(getEligibilityDetailService().isCustEligible(eligibilityRuleList));
		if (isCustisEligible()) {
			label_ElgRuleSummaryVal.setValue(Labels.getLabel("common.Eligible"));
			label_ElgRuleSummaryVal.setStyle("font-weight:bold;color:green;");
		} else {
			label_ElgRuleSummaryVal.setValue(Labels.getLabel("common.Ineligible"));
			label_ElgRuleSummaryVal.setStyle("font-weight:bold;color:red;");
		}
		logger.debug("Leaving");
	}

	private String getUserRole() {
		try {
			return (String) getFinanceMainDialogCtrl().getClass().getMethod("getUserRole")
					.invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			logger.debug(e);
		}
		return "";
	}

	/**
	 * Method to capture event when eligible rule item double clicked
	 * 
	 * @param event
	 */
	public void onElgRuleItemChecked(ForwardEvent event) {
		logger.debug("Entering" + event.toString());
		Checkbox cb = (Checkbox) event.getOrigin().getTarget();
		FinanceEligibilityDetail financeEligibilityDetail = (FinanceEligibilityDetail) event.getData();

		financeEligibilityDetail.setUserOverride(cb.isChecked());
		financeEligibilityDetail.setEligible(getEligibilityDetailService()
				.getEligibilityStatus(financeEligibilityDetail, financeMain.getFinCcy(), financeMain.getFinAmount()));
		setCustEligibilityGropuStatus();
		Listcell elgLimitLabel = new Listcell();
		try {
			elgLimitLabel = (Listcell) cb.getParent().getPreviousSibling().getPreviousSibling();
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		if (cb.isChecked() && elgLimitLabel != null) {
			elgLimitLabel.setLabel(Labels.getLabel("common.Eligible"));
			elgLimitLabel.setStyle("font-weight:bold;color:green;");
		} else {
			elgLimitLabel.setLabel(Labels.getLabel("common.Ineligible"));
			elgLimitLabel.setStyle("font-weight:bold;color:red;");
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail doSave_EligibilityList(FinanceDetail aFinanceDetail)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering ");
		setFinanceDetail(aFinanceDetail);
		if (isWIF) {
			doCheckWIFFinEligibility(true);
		} else {
			doCheckFinEligibility(true);
		}
		getFinanceDetail().setElgRuleList(eligibilityRuleList);

		logger.debug("Leaving ");
		return getFinanceDetail();
	}

	/**
	 * Click event for eligibility
	 * 
	 * @param isSave
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void doCheckWIFFinEligibility(boolean isUserAction)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");
		this.label_ElgRuleSummaryVal.setValue("");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

		aFinanceDetail = (FinanceDetail) getFinanceMainDialogCtrl().getClass()
				.getMethod("dofillEligibilityData", Boolean.class).invoke(getFinanceMainDialogCtrl(), isUserAction);

		for (FinanceEligibilityDetail financeEligibilityDetail : eligibilityRuleList) {
			if (financeEligibilityDetail.isExecute()) {
				financeEligibilityDetail.setUserOverride(false);
				financeEligibilityDetail = getEligibilityDetailService().getElgResult(financeEligibilityDetail,
						aFinanceDetail);
			}

			if (RuleConstants.ELGRULE_DSRCAL.equals(financeEligibilityDetail.getLovDescElgRuleCode().trim())) {
				aFinanceDetail.getCustomerEligibilityCheck()
						.setDSCR(PennantApplicationUtil.getDSR(financeEligibilityDetail.getRuleResult()));
				getFinanceDetail().setCustomerEligibilityCheck(aFinanceDetail.getCustomerEligibilityCheck());
			}
		}
		aFinanceDetail = null;

		// Fill eligibility details
		doFillWIFFinEligibilityDetails(this.eligibilityRuleList);

		// Set eligibility group status
		setCustEligibilityGropuStatus();

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Executed Eligibility Details
	 * 
	 * @param eligibilityDetails
	 */
	public void doFillWIFFinEligibilityDetails(List<FinanceEligibilityDetail> eligibilityDetails) {
		logger.debug("Entering");

		this.listBoxFinElgRef.getItems().clear();
		String overridePerc = "";
		if (eligibilityDetails != null && !eligibilityDetails.isEmpty()) {
			for (FinanceEligibilityDetail detail : eligibilityDetails) {
				Listitem item = new Listitem();
				Listcell lc;

				// Rule Source
				lc = new Listcell("");
				lc.setParent(item);

				// Rule Code
				lc = new Listcell(detail.getLovDescElgRuleCode());
				lc.setParent(item);

				// Rule Code Desc
				lc = new Listcell(detail.getLovDescElgRuleCodeDesc());
				lc.setParent(item);

				// Can Override
				lc = new Listcell();
				Checkbox cbOverride = new Checkbox();
				cbOverride.setDisabled(true);

				if (detail.isCanOverride()) {
					cbOverride.setChecked(true);
					overridePerc = String.valueOf(detail.getOverridePerc());
				} else {
					cbOverride.setChecked(false);
					overridePerc = "";
				}
				lc.appendChild(cbOverride);
				lc.setParent(item);

				// Override Value
				lc = new Listcell(overridePerc);
				lc.setParent(item);

				// If Rule Not Executed
				if (StringUtils.isEmpty(detail.getRuleResult())) {
					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);
				} else {
					// If Decimal Result for Eligibility
					if (RuleConstants.RETURNTYPE_DECIMAL.equals(detail.getRuleResultType())) {
						// IF Error in Executing the Rule
						if ("E".equals(detail.getRuleResult())) {
							lc = new Listcell(Labels.getLabel("common.InSuffData"));
							lc.setStyle("font-weight:bold;color:red;");
							lc.setParent(item);

							lc = new Listcell("");
							lc.setParent(item);
							// IF DSR Calculation Rule
						} else if (RuleConstants.ELGRULE_DSRCAL.equals(detail.getLovDescElgRuleCode())
								|| RuleConstants.ELGRULE_PDDSRCAL.equals(detail.getLovDescElgRuleCode())) {

							String result = detail.getRuleResult() + "%";
							lc = new Listcell(result);
							lc.setParent(item);
							lc = new Listcell("");
							lc.setParent(item);

						} else {

							lc = new Listcell(CurrencyUtil.format(new BigDecimal(detail.getRuleResult()),
									CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
							lc.setStyle("text-align:right;");
							lc.setParent(item);

							lc = new Listcell(CurrencyUtil.format(detail.getOverrideResult(),
									CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
							lc.setStyle("text-align:right;");
							lc.setParent(item);
						}
					} else {
						if (detail.isEligible()) {
							lc = new Listcell(Labels.getLabel("common.Eligible"));
							lc.setStyle("font-weight:bold;color:green;");
						} else {
							lc = new Listcell(Labels.getLabel("common.Ineligible"));
							lc.setStyle("font-weight:bold;color:red;");
						}
						lc.setParent(item);

						lc = new Listcell("");
						lc.setParent(item);
					}
					if ((!detail.isEligible() && detail.isCanOverride()) || detail.isUserOverride()) {
						Checkbox cbElgOverride = new Checkbox();
						cbElgOverride.setChecked(detail.isUserOverride());
						if (detail.isExecute()) {
							cbElgOverride.setDisabled(false);
						} else {
							cbElgOverride.setDisabled(true);
						}
						item.setTooltiptext(Labels.getLabel("listitem_ElgRule_tooltiptext"));
						cbElgOverride.addForward("onCheck", window_EligibilityDetailDialog, "onElgRuleItemChecked",
								detail);
						lc = new Listcell("");
						cbElgOverride.setParent(lc);
						lc.setParent(item);
					} else {
						lc = new Listcell("");
						lc.setParent(item);
					}
				}
				listBoxFinElgRef.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails() {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentCtrl", this);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul", this.finBasicdetails,
					map);
		} catch (Exception e) {
			logger.debug(e);
		}
	}

	public void doSetLabels(ArrayList<Object> finHeaderList) {
		getFinBasicDetailsCtrl().doWriteBeanToComponents(finHeaderList);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceMain getFinanceMain() {
		return financeMain;
	}

	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public EligibilityDetailService getEligibilityDetailService() {
		return eligibilityDetailService;
	}

	public void setEligibilityDetailService(EligibilityDetailService eligibilityDetailService) {
		this.eligibilityDetailService = eligibilityDetailService;
	}

	public boolean isCustisEligible() {
		return custisEligible;
	}

	public void setCustisEligible(boolean custisEligible) {
		this.custisEligible = custisEligible;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

}
