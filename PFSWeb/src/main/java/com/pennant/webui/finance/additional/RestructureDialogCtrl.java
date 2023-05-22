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
 * * FileName : RestructureDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2021 * *
 * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-03-2021 Pennant 0.1 * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.RestructureType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/RestructureDialog.zul file.
 */
public class RestructureDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(RestructureDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RestructureDialog;

	protected Combobox restructuringType;
	protected Combobox restructuringReason;
	protected Combobox restructureDate;
	protected Datebox restructureDateIn;
	protected Intbox numberOfEMIHoliday;
	protected Intbox numberOfPriHoliday;
	protected Intbox numberOfEMITerms;
	protected Intbox totNoOfRestructuring;
	protected Row rateReviewRow;
	protected Decimalbox actRate;
	protected RateBox baseRate;
	protected CurrencyBox grcMaxAmount;
	protected Combobox recalculationType;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	// Step Details
	protected Groupbox gb_RestructureStep;
	protected Listbox listBoxRestructureSteps;
	protected Button btnNew_RestructureStep;

	protected Combobox calcOfSteps;
	protected Combobox stepsAppliedFor;
	protected ExtendedCombobox stepPolicy;
	protected Combobox stepType;
	protected Space space_stepType;

	protected Checkbox alwManualSteps;
	protected Intbox noOfSteps;
	protected Intbox grcSteps;

	protected Listbox listBoxCharges;
	protected Listheader listheader_RestructureCharge_TdsAmount;
	protected Button btnRestructure;

	private LovFieldDetail lovFieldDetail = PennantAppUtil.getDefaultRestructure("RSTRS");
	protected Label label_RestructureDialog_numberOfSteps;
	protected Hbox hbox_numberOfSteps;
	protected Space space_noOfSteps;

	private FinScheduleData finScheduleData;
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;
	private StepDetailDialogCtrl stepDetailDialogCtrl;

	private transient RestructureService restructureService;
	private RestructureType restructureType = new RestructureType();
	Date appDate = SysParamUtil.getAppDate();
	private Date fullyPaidDate = null;
	public List<FinanceStepPolicyDetail> finStepPolicyList = null;

	private RestructureDetail rstDetail;
	private boolean enquiry = false;
	private transient StepPolicyService stepPolicyService;
	private FinanceDetail fd = null;
	@Autowired
	private FinFeeDetailService finFeeDetailService;

	/**
	 * default constructor.<br>
	 */
	public RestructureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	public void onCreate$window_RestructureDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(this.window_RestructureDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setFinanceMainDialogCtrl(null);
			}

			if (arguments.containsKey("financeDetail")) {
				fd = (FinanceDetail) arguments.get("financeDetail");
			}

			if (arguments.containsKey("enquiry")) {
				enquiry = (Boolean) arguments.get("financeMainDialogCtrl");
			}

			doSetFieldProperties();
			doShowDialog(getFinScheduleData());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RestructureDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		try {
			// fill the components with the data
			if (enquiry) {
				doRenderData(aFinScheduleData);
				doReadOnly();
			} else {
				doWriteBeanToComponents(aFinScheduleData);
			}
			setDialog(DialogType.MODAL);

		} catch (UiException e) {
			logger.error(Literal.EXCEPTION, e);
			this.window_RestructureDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.restructuringType.setReadonly(true);
		this.restructuringReason.setReadonly(true);
		this.restructureDate.setReadonly(true);
		this.restructureDateIn.setFormat(PennantConstants.dateFormat);
		this.numberOfEMIHoliday.setReadonly(true);
		this.numberOfEMIHoliday.setMaxlength(2);
		this.numberOfPriHoliday.setReadonly(true);
		this.numberOfPriHoliday.setMaxlength(2);
		this.numberOfEMITerms.setMaxlength(2);
		this.numberOfEMITerms.setReadonly(true);
		this.totNoOfRestructuring.setReadonly(true);
		this.actRate.setMaxlength(13);
		this.actRate.setFormat(PennantConstants.rateFormate9);
		this.actRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.actRate.setScale(9);
		this.baseRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.baseRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.baseRate.setEffectiveRateVisible(true);
		this.grcMaxAmount.setDisabled(true);
		this.grcMaxAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.recalculationType.setDisabled(true);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);

		FinanceType financeType = getFinScheduleData().getFinanceType();

		// Step Finance Field Properties
		this.noOfSteps.setMaxlength(2);
		this.noOfSteps.setStyle("text-align:right;");
		this.grcSteps.setMaxlength(2);
		this.grcSteps.setStyle("text-align:right;");
		this.stepType.setReadonly(true);
		this.stepsAppliedFor.setDisabled(true);

		this.stepPolicy.setProperties("StepPolicyHeader", "PolicyCode", "PolicyDesc", true, 8);
		String[] alwdStepPolices = StringUtils.trimToEmpty(financeType.getAlwdStepPolicies()).split(",");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
		this.stepPolicy.setFilters(filter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		if (!this.numberOfEMIHoliday.isReadonly()) {
			this.numberOfEMIHoliday.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_NumberOfEMIHoliday.value"), 0, true,
							false, 0, restructureType.getMaxEmiHoliday()));
		}

		if (!this.numberOfPriHoliday.isReadonly()) {
			this.numberOfPriHoliday.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_NumberOfPriHoliday.value"), 0, true,
							false, 0, restructureType.getMaxPriHoliday()));
		}

		if (!this.numberOfEMITerms.isReadonly()) {
			this.numberOfEMITerms.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_NumberOfEMITerms.value"), 0, true,
							false, 0, restructureType.getMaxEmiTerm()));
		}

		this.totNoOfRestructuring.setConstraint(
				new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_TotNoOfRestructuring.value"), 0, true,
						false, 1, restructureType.getMaxTotTerm()));

		if (!this.grcMaxAmount.isReadonly()) {
			this.grcMaxAmount
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_RestructureDialog_GrcMaxAmount.value"),
							PennantConstants.defaultCCYDecPos, false, false));
		}

		if (ImplementationConstants.RESTRUCTURE_DATE_ALW_EDIT) {
			this.restructureDateIn
					.setConstraint(new PTDateValidator(Labels.getLabel("label_RestructureDialog_RestructureDate.value"),
							false, fullyPaidDate, true, true));
		}

		if (ImplementationConstants.RESTRUCTURE_RATE_CHG_ALW) {
			if (this.actRate.isVisible()) {
				this.actRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_RestructureDialog_ActRate.value"), 9, false, false, 0, 9999));
			}
			if (!this.baseRate.isMarginReadonly()) {
				this.baseRate.setMarginConstraint(new PTDecimalValidator(
						Labels.getLabel("label_RestructureDialog_MarginRate.value"), 9, false, true, -9999, 9999));
			}
			if (!this.baseRate.isBaseReadonly()) {
				this.baseRate.setBaseConstraint(new PTStringValidator(
						Labels.getLabel("label_RestructureDialog_BaseRate.value"), null, false, true));
			}
		}
	}

	/**
	 * When the "Restructure" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnRestructure(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		boolean isDue = restructureService.checkLoanDues(rstDetail.getChargeList());
		if (!isDue) {
			MessageUtil.showError(Labels.getLabel("label_Restructure_LoanDues_Validation"));
			return;
		}

		if (gb_RestructureStep.isVisible()) {
			doValidateStepDetails();
		}

		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doClose(false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean();
		doClose(false);
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.restructuringType.setErrorMessage("");
		this.restructuringReason.setErrorMessage("");
		this.restructureDate.setErrorMessage("");
		this.restructureDateIn.setErrorMessage("");
		this.numberOfEMIHoliday.setErrorMessage("");
		this.numberOfPriHoliday.setErrorMessage("");
		this.numberOfEMITerms.setErrorMessage("");
		this.totNoOfRestructuring.setErrorMessage("");
		this.baseRate.setBaseErrorMessage("");
		this.baseRate.setSpecialErrorMessage("");
		this.baseRate.setMarginErrorMessage("");
		this.actRate.clearErrorMessage();
		this.grcMaxAmount.setErrorMessage("");
		this.recalculationType.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.restructuringType.setConstraint("");
		this.restructuringReason.setConstraint("");
		this.restructureDate.setConstraint("");
		this.restructureDateIn.setConstraint("");
		this.numberOfEMIHoliday.setConstraint("");
		this.numberOfPriHoliday.setConstraint("");
		this.numberOfEMITerms.setConstraint("");
		this.totNoOfRestructuring.setConstraint("");
		this.baseRate.setBaseConstraint("");
		this.baseRate.setSpecialConstraint("");
		this.baseRate.setMarginConstraint("");
		this.actRate.setConstraint("");
		this.grcMaxAmount.setConstraint("");
		this.recalculationType.setConstraint("");
		this.serviceReqNo.setConstraint("");
		this.remarks.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug(Literal.ENTERING);
		FinanceMain fm = aFinSchData.getFinanceMain();

		fillComboBox(this.restructuringType, "", PennantAppUtil.getRestructureType(fm.isStepFinance()), "");

		String recalType = CalculationConstants.RST_RECAL_ADDTERM_RECALEMI;
		if (getFinScheduleData().getRestructureDetail() != null
				&& (StringUtils.equals(getFinScheduleData().getRestructureDetail().getRecalculationType(),
						CalculationConstants.RST_RECAL_ADJUSTTENURE))) {
			recalType = CalculationConstants.RST_RECAL_ADJUSTTENURE;
		}
		fillComboBox(this.recalculationType, recalType, PennantStaticListUtil.getRecalTypeList(), "");

		String defaultRestructure = "";
		if (aFinSchData.getRestructureDetail() != null) {
			defaultRestructure = StringUtils.trimToEmpty(aFinSchData.getRestructureDetail().getRestructureReason());
		} else {
			String fldCode = StringUtils.trimToEmpty(lovFieldDetail.getFieldCodeValue());
			if (defaultRestructure.isEmpty() && !fldCode.isEmpty()) {
				defaultRestructure = fldCode;
			}
		}

		fillComboBox(this.restructuringReason, defaultRestructure, lovFieldDetail.getValueLabelList(), "");

		fillSchDates(this.restructureDate, aFinSchData, fm.getFinStartDate(), null);

		// Restructure Date Allowed as Input Field/selection based on Parameters
		if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {

			this.restructureDate.setDisabled(true);
			this.restructureDate.setVisible(false);

			this.restructureDateIn.setVisible(true);
			this.restructureDateIn.setValue(appDate);

			// Finding Min Allowed Date for Restructure Date
			findMinAllowedRestructureDate(aFinSchData);

			if (ImplementationConstants.RESTRUCTURE_DATE_ALW_EDIT) {
				this.restructureDateIn.setDisabled(false);
			} else {
				this.restructureDateIn.setDisabled(true);
			}
		} else {
			this.restructureDateIn.setDisabled(true);
			this.restructureDateIn.setVisible(false);
		}

		// Rate Change Allowed
		if (ImplementationConstants.RESTRUCTURE_RATE_CHG_ALW) {
			this.rateReviewRow.setVisible(true);
			setEffectiveRate();
		}

		showStepDetails(aFinSchData);

		// Restructure initiation
		rstDetail = new RestructureDetail();
		rstDetail.setNewRecord(true);

		// Fetch All charges and rendering for user selection
		if (ImplementationConstants.RESTRUCTURE_ALW_CHARGES) {
			this.listBoxCharges.setVisible(true);

			if (ImplementationConstants.ALLOW_TDS_ON_FEE) {
				this.listheader_RestructureCharge_TdsAmount.setVisible(true);
			}

			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				String branchCode = getUserWorkspace().getLoggedInUser().getBranchCode();
				List<FinFeeDetail> convertToFinanceFees = finFeeDetailService.convertToFinanceFees(fd, branchCode);
				convertToFinanceFees.stream()
						.forEach(f1 -> f1.setFeeScheduleMethod(CalculationConstants.REMFEE_PART_OF_SALE_PRICE));
				aFinSchData.setFinFeeDetailList(convertToFinanceFees);

				List<RestructureCharge> chargeList = restructureService.getRestructureChargeList(aFinSchData, appDate);
				doFillCharges(chargeList);
				rstDetail.setChargeList(chargeList);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void showStepDetails(FinScheduleData aFinSchData) {
		FinanceMain fm = aFinSchData.getFinanceMain();
		if (fm.isStepFinance()) {
			this.gb_RestructureStep.setVisible(true);
			fillComboBox(this.calcOfSteps, fm.getCalcOfSteps(), PennantStaticListUtil.getCalcOfStepsList(), "");
			fillComboBox(this.stepsAppliedFor, fm.getStepsAppliedFor(), PennantStaticListUtil.getStepsAppliedFor(), "");
			fillComboBox(this.stepType, fm.getStepType(), PennantStaticListUtil.getStepType(), "");
			this.stepPolicy.setValue(fm.getStepPolicy(), fm.getLovDescStepPolicyName());
			this.alwManualSteps.setChecked(fm.isAlwManualSteps());
			this.noOfSteps.setValue(fm.getNoOfSteps());
			this.grcSteps.setValue(fm.getNoOfGrcSteps());
			doFillStepDetails(finScheduleData.getStepPolicyDetails());
			this.stepPolicy.setReadonly(true);
			this.calcOfSteps.setDisabled(true);
			this.stepsAppliedFor.setReadonly(true);

			if (PennantConstants.STEPPING_CALC_PERC.equals(fm.getCalcOfSteps())) {
				this.alwManualSteps.setDisabled(false);
				this.stepPolicy.setReadonly(false);
			}

			this.stepType.setDisabled(true);
			this.btnNew_RestructureStep.setVisible(true);
		} else {
			if (!StringUtils.equals("#", getComboboxValue(this.restructuringType))) {
				List<RestructureType> restructureTypeList = PennantAppUtil
						.getRestructureType(Long.valueOf(getComboboxValue(this.restructuringType)));
				restructureType = restructureTypeList.get(0);

				if (StringUtils.equals("Scenario9", restructureType.getRstTypeCode())
						|| StringUtils.equals("Scenario10", restructureType.getRstTypeCode())
						|| StringUtils.equals("Scenario11", restructureType.getRstTypeCode())) {
					this.gb_RestructureStep.setVisible(true);
					setStepDetailsForNonStepLoan(aFinSchData);
				}
			} else {
				this.gb_RestructureStep.setVisible(false);
			}
		}
	}

	private void setStepDetailsForNonStepLoan(FinScheduleData finScheduleData) {
		FinanceType finType = finScheduleData.getFinanceType();
		FinanceMain finMain = finScheduleData.getFinanceMain();

		String calOfSteps = finMain.isStepFinance() ? finMain.getCalcOfSteps() : finType.getCalcOfSteps();
		if (!finType.isAlwManualSteps() && StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_PERC)) {
			fillComboBox(this.calcOfSteps, finType.getCalcOfSteps(), PennantStaticListUtil.getCalcOfStepsList(), "");
			this.calcOfSteps.setDisabled(true);
			fillComboBox(this.stepsAppliedFor, finType.getStepsAppliedFor(), PennantStaticListUtil.getStepsAppliedFor(),
					"");
			this.stepsAppliedFor.setDisabled(true);
			this.alwManualSteps.setChecked(finType.isAlwManualSteps());
			onCheckAlwManualSteps(finType.isAlwManualSteps());
			this.stepPolicy.setValue(finType.getDftStepPolicy());
			this.stepPolicy.setDescription(finType.getLovDescDftStepPolicyName());
			this.stepType.setValue(finType.getDftStepPolicyType());
			fillComboBox(this.stepType, finType.getDftStepPolicyType(), PennantStaticListUtil.getStepType(), "");
			this.stepType.setDisabled(true);
			setStepPolicyDetails(finScheduleData);
		}

		if (StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_PERC) && finType.isAlwManualSteps()) {

			fillComboBox(this.calcOfSteps, finType.getCalcOfSteps(), PennantStaticListUtil.getCalcOfStepsList(), "");
			this.calcOfSteps.setDisabled(true);
			fillComboBox(this.stepsAppliedFor, finType.getStepsAppliedFor(), PennantStaticListUtil.getStepsAppliedFor(),
					"");
			this.stepsAppliedFor.setDisabled(true);
			this.alwManualSteps.setChecked(finType.isAlwManualSteps());
			onCheckAlwManualSteps(finType.isAlwManualSteps());
			this.stepPolicy.setValue(finType.getDftStepPolicy());
			this.stepPolicy.setDescription(finType.getLovDescDftStepPolicyName());
			this.stepType.setValue(finType.getDftStepPolicyType());
			fillComboBox(this.stepType, finType.getDftStepPolicyType(), PennantStaticListUtil.getStepType(), "");
			this.stepType.setDisabled(true);
			setStepPolicyDetails(finScheduleData);

		}

		if (StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_PERC) && finType.isAlwManualSteps()
				&& finMain.isAlwManualSteps()) {
			onCheckAlwManualSteps(false);
		}

		if (StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
			fillComboBox(this.calcOfSteps, finType.getCalcOfSteps(), PennantStaticListUtil.getCalcOfStepsList(), "");
			fillComboBox(this.stepsAppliedFor, finType.getStepsAppliedFor(), PennantStaticListUtil.getStepsAppliedFor(),
					"");
			if (StringUtils.equals(this.calcOfSteps.getSelectedItem().getValue(), PennantConstants.STEPPING_CALC_AMT)) {
				this.stepPolicy.setReadonly(true);
				this.stepType.setDisabled(true);
				this.alwManualSteps.setChecked(true);
				this.alwManualSteps.setDisabled(true);
				onCheckAlwManualSteps(false);
			} else {
				this.stepPolicy.setReadonly(false);
				this.stepType.setDisabled(false);
			}
			this.noOfSteps.setVisible(true);
			setStepPolicyDetails(finScheduleData);
		}
	}

	public void onChange$calcOfSteps(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onChangeCalofSteps();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChangeCalofSteps() {
		String calcOfSteps = getComboboxValue(this.calcOfSteps);
		if (CollectionUtils.isNotEmpty(finStepPolicyList)) {
			this.finStepPolicyList.clear();
		}

		if (calcOfSteps.equals(PennantConstants.STEPPING_CALC_AMT)) {
			this.stepPolicy.setReadonly(true);
			this.stepType.setDisabled(true);
			this.btnNew_RestructureStep.setVisible(true);
		} else if (calcOfSteps.equals(PennantConstants.STEPPING_CALC_PERC)) {
			if (this.alwManualSteps.isChecked()) {
				this.stepPolicy.setReadonly(true);
			} else {
				this.stepPolicy.setReadonly(false);
			}
			this.stepType.setDisabled(false);
			if (StringUtils.isEmpty(this.stepPolicy.getValue())) {
				this.stepPolicy.setProperties("StepPolicyHeader", "PolicyCode", "PolicyDesc", true, 8);
				this.stepPolicy.setFilters(null);
			}
			this.btnNew_RestructureStep.setVisible(false);
		}
		this.finScheduleData.getFinanceMain().setCalcOfSteps(calcOfSteps);
		this.finScheduleData.getFinanceType().setCalcOfSteps(calcOfSteps);
	}

	public void onFulfill$stepPolicy(Event event) {
		logger.debug("Entering " + event.toString());

		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");
		this.stepType.setConstraint("");
		this.stepPolicy.clearErrorMessage();
		this.noOfSteps.setErrorMessage("");
		this.stepType.setErrorMessage("");

		Object dataObject = stepPolicy.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.stepPolicy.setValue(dataObject.toString());
				this.stepPolicy.setDescription("");
			}
			fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
			getFinScheduleData().getStepPolicyDetails().clear();
			doFillStepDetails(getFinScheduleData().getStepPolicyDetails());
		} else {
			StepPolicyHeader detail = (StepPolicyHeader) dataObject;
			if (detail != null) {
				this.stepPolicy.setValue(detail.getPolicyCode(), detail.getPolicyDesc());
				fillComboBox(this.stepType, detail.getStepType(), PennantStaticListUtil.getStepType(), "");
				// Fetch Step Policy Details List
				List<StepPolicyDetail> policyList = getStepPolicyService()
						.getStepPolicyDetailsById(this.stepPolicy.getValue());
				this.noOfSteps.setValue(policyList.size());
				getFinScheduleData().resetStepPolicyDetails(policyList);
				List<FinanceStepPolicyDetail> policyDetails = getFinScheduleData().getStepPolicyDetails();
				for (FinanceStepPolicyDetail financeStepPolicyDetail : policyDetails) {
					financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				}
				doFillStepDetails(policyDetails);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnNew_RestructureStep(Event event) throws Exception {
		logger.debug("Entering");

		int stepValue = this.noOfSteps.intValue();
		String calcOfsteps = getComboboxValue(this.calcOfSteps);
		if (stepValue == 0) {
			throw new WrongValueException(this.noOfSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_RepaySteps.value") }));
		}
		if (stepValue < 0) {
			throw new WrongValueException(this.noOfSteps, Labels.getLabel("NUMBER_NOT_NEGATIVE",
					new String[] { Labels.getLabel("label_FinanceMainDialog_RepaySteps.value") }));
		}

		if (calcOfsteps.equals("#")) {
			throw new WrongValueException(this.calcOfSteps, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_CalcOfSteps.value") }));
		}

		FinanceStepPolicyDetail financeStepPolicyDetail = new FinanceStepPolicyDetail();
		financeStepPolicyDetail.setNewRecord(true);
		financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
		openFinStepPolicyDetailDialog(financeStepPolicyDetail, true);
		logger.debug("Leaving");
	}

	public void openFinStepPolicyDetailDialog(FinanceStepPolicyDetail finStepPolicy, boolean isNewRecord)
			throws InterruptedException {
		try {

			this.noOfSteps.setErrorMessage("");
			this.grcSteps.setErrorMessage("");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeStepPolicyDetail", finStepPolicy);
			map.put("restructureDialogCtrl", this);
			map.put("newRecord", isNewRecord);
			// map.put("roleCode", roleCode);
			map.put("finStepPoliciesList", finScheduleData.getStepPolicyDetails());
			map.put("alwDeletion", this.alwManualSteps.isChecked());
			map.put("alwManualStep", this.alwManualSteps.isChecked());
			map.put("ccyFormatter", 2);
			FinanceDetail fd = new FinanceDetail();
			fd.setFinScheduleData(getFinScheduleData());
			if (!getFinScheduleData().getFinanceMain().isStepFinance()) {
				Map<String, Object> noOfInstAndAmt = restructureService.getNoOfInstAndAmt(getFinScheduleData(),
						this.restructureDateIn.getValue());
				map.put("Amount", noOfInstAndAmt.get("Amount"));
				map.put("NoOfInstallments", noOfInstAndAmt.get("NoOfInstallments"));
				map.put("IsNonStepLoan", true);
			}
			map.put("financeDetail", fd);
			map.put("enquiryModule", false);
			map.put("fromRestructure", true);
			map.put("rpySteps", this.noOfSteps.intValue());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinStepPolicyDetailDialog.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	public void onCheck$alwManualSteps(Event event) {
		logger.debug("Entering : " + event.toString());
		onCheckAlwManualSteps(true);
		logger.debug("Leaving : " + event.toString());
	}

	private void onCheckAlwManualSteps(boolean isAction) {
		FinScheduleData schData = getFinScheduleData();
		FinanceType financeType = schData.getFinanceType();
		FinanceMain financeMain = schData.getFinanceMain();

		String calOfSteps = "";
		calOfSteps = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");

		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");

		if (this.alwManualSteps.isChecked()) {
			this.label_RestructureDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.space_noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setSclass(PennantConstants.mandateSclass);
			this.noOfSteps.setValue(0);
			this.stepType.setVisible(true);
			this.stepType.setSclass(PennantConstants.mandateSclass);
			this.btnNew_RestructureStep.setVisible(true);
			this.stepPolicy.setValue("", "");
			this.stepPolicy.setReadonly(true);
			this.space_stepType.setSclass(PennantConstants.mandateSclass);
			this.stepType.setDisabled(false);
			/*
			 * if (isAction) { List<StepPolicyDetail> policyList = new ArrayList<StepPolicyDetail>(); if
			 * (StringUtils.isNotEmpty(this.stepPolicy.getValue())) { policyList =
			 * getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue()); }
			 * schData.resetStepPolicyDetails(policyList); doFillStepDetails(schData.getStepPolicyDetails());
			 * fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), ""); }
			 * else { fillComboBox(this.stepType, schData.getFinanceMain().getStepType(),
			 * PennantStaticListUtil.getStepType(), ""); }
			 */
			if (StringUtils.equals(calOfSteps, PennantConstants.STEPPING_CALC_AMT)) {
				if (getComboboxValue(this.calcOfSteps).equals(PennantConstants.STEPPING_CALC_AMT)) {
					fillComboBox(this.stepType, PennantConstants.List_Select, PennantStaticListUtil.getStepType(), "");
					this.stepType.setDisabled(true);
				}
			}
			this.stepsAppliedFor.setDisabled(true);
			this.calcOfSteps.setDisabled(false);
		} else {
			this.stepPolicy.setValue(schData.getFinanceMain().getStepPolicy());
			this.stepPolicy.setDescription(schData.getFinanceMain().getLovDescStepPolicyName());
			fillComboBox(this.stepType, schData.getFinanceMain().getStepType(), PennantStaticListUtil.getStepType(),
					"");
			this.stepPolicy.setMandatoryStyle(true);
			this.label_RestructureDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.space_noOfSteps.setSclass("");
			if (getComboboxValue(this.calcOfSteps).equals(PennantConstants.STEPPING_CALC_AMT)) {
				this.stepPolicy.setValue("", "");
				this.stepPolicy.setReadonly(true);
			} else {
				this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
			}
			this.stepType.setReadonly(isReadOnly("FinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			this.stepsAppliedFor.setDisabled(true);
			this.calcOfSteps.setDisabled(true);
			if (isReadOnly("FinanceMainDialog_alwManualSteps")) {
				this.alwManualSteps.setVisible(false);
			}
		}

		// Filling Step Policy Details List
		if (isAction || financeMain.isStepFinance()) {
			if (financeMain.isStepFinance() && this.alwManualSteps.isChecked()) {
				fillComboBox(this.stepType, FinanceConstants.STEPTYPE_EMI, PennantStaticListUtil.getStepType(), "");
			}
			List<StepPolicyDetail> policyList = new ArrayList<>();

			if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
				policyList = stepPolicyService.getStepPolicyDetailsById(this.stepPolicy.getValue());
			}

			List<FinanceStepPolicyDetail> tfspd = schData.getStepPolicyDetails();

			if (CollectionUtils.isNotEmpty(policyList)) {
				schData.resetStepPolicyDetails(policyList);
			}

			schData.getStepPolicyDetails().forEach(l1 -> {
				l1.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
				for (FinanceStepPolicyDetail fspd : tfspd) {
					if (l1.getStepNo() == fspd.getStepNo()) {
						l1.setInstallments(fspd.getInstallments());
					}
				}
			});

			doFillStepDetails(schData.getStepPolicyDetails());
		}
	}

	private void setStepPolicyDetails(FinScheduleData finScheduleData) {
		List<StepPolicyDetail> policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
		finScheduleData.resetStepPolicyDetails(policyList);
		finScheduleData.getStepPolicyDetails();
		List<FinanceStepPolicyDetail> stepPolicyDetails = finScheduleData.getStepPolicyDetails();
		for (FinanceStepPolicyDetail financeStepPolicyDetail : stepPolicyDetails) {
			financeStepPolicyDetail.setStepSpecifier(PennantConstants.STEP_SPECIFIER_REG_EMI);
		}
		doFillStepDetails(finScheduleData.getStepPolicyDetails());
	}

	public void doFillStepDetails(List<FinanceStepPolicyDetail> finStepPolicyDetails) {

		logger.debug("Entering ");

		Listitem listItem = null;
		Listcell lc = null;

		BigDecimal tenorPerc = BigDecimal.ZERO;
		int totInstallments = 0;
		BigDecimal avgRateMargin = BigDecimal.ZERO;
		BigDecimal avgAplliedRate = BigDecimal.ZERO;
		BigDecimal avgEmiPerc = BigDecimal.ZERO;

		this.listBoxRestructureSteps.getItems().clear();
		setFinStepPoliciesList(finStepPolicyDetails);
		if (CollectionUtils.isNotEmpty(finStepPolicyDetails)) {
			Comparator<Object> comp = new BeanComparator<Object>("stepNo");
			Collections.sort(finStepPolicyDetails, comp);
			BigDecimal firstStepAmount = BigDecimal.ZERO;

			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {

				/*
				 * if (!StringUtils.equals(PennantConstants.STEP_SPECIFIER_REG_EMI,
				 * financeStepPolicyDetail.getStepSpecifier())) { continue; }
				 */

				if (financeStepPolicyDetail.getStepNo() == 1) {
					firstStepAmount = PennantApplicationUtil.formateAmount(financeStepPolicyDetail.getSteppedEMI(),
							PennantConstants.defaultCCYDecPos);
				}

				listItem = new Listitem();

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getStepNo()));
				lc.setParent(listItem);

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.formatRate(financeStepPolicyDetail.getTenorSplitPerc().doubleValue(),
						2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(String.valueOf(financeStepPolicyDetail.getInstallments()));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(
						PennantApplicationUtil.formatRate(financeStepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				BigDecimal appliedRate = BigDecimal.ZERO;

				lc = new Listcell();
				appliedRate = financeStepPolicyDetail.getRateMargin();
				if (getFinScheduleData().getFinanceMain().getRepayProfitRate() != null) {
					appliedRate = appliedRate.add(getFinScheduleData().getFinanceMain().getRepayProfitRate());
				}
				lc.setLabel(
						PennantApplicationUtil.formatRate(financeStepPolicyDetail.getRateMargin().doubleValue(), 9));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(
						PennantApplicationUtil.formatRate(financeStepPolicyDetail.getEmiSplitPerc().doubleValue(), 2));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				lc.setLabel(PennantApplicationUtil.amountFormate(financeStepPolicyDetail.getSteppedEMI(),
						PennantConstants.defaultCCYDecPos));
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				lc = new Listcell();
				if (financeStepPolicyDetail.getStepNo() == 1) {
					lc.setLabel("100%");
				} else if (firstStepAmount.compareTo(BigDecimal.ZERO) > 0) {
					lc.setLabel(PennantApplicationUtil
							.formateAmount(financeStepPolicyDetail.getSteppedEMI(), PennantConstants.defaultCCYDecPos)
							.multiply(new BigDecimal(100)).divide(firstStepAmount, 0, RoundingMode.HALF_DOWN).toString()
							.concat("%"));
				}
				lc.setParent(listItem);
				lc.setStyle("text-align:right;");

				tenorPerc = tenorPerc.add(financeStepPolicyDetail.getTenorSplitPerc());
				totInstallments = totInstallments + financeStepPolicyDetail.getInstallments();
				avgRateMargin = avgRateMargin.add(financeStepPolicyDetail.getRateMargin());
				avgAplliedRate = avgAplliedRate.add(appliedRate);
				avgEmiPerc = avgEmiPerc.add(financeStepPolicyDetail.getEmiSplitPerc());

				listItem.setParent(this.listBoxRestructureSteps);
				listItem.setAttribute("data", financeStepPolicyDetail);
				ComponentsCtrl.applyForward(listItem, "onDoubleClick=onFinStepPolicyItemDoubleClicked");
			}

		}

		if (CollectionUtils.isNotEmpty(finStepPolicyDetails)) {
			String stepType = "";
			if (this.stepType.getSelectedItem() != null) {
				stepType = this.stepType.getSelectedItem().getValue();
			}
			for (FinanceStepPolicyDetail financeStepPolicyDetail : finStepPolicyDetails) {
				addFooter(finStepPolicyDetails.size(), tenorPerc, totInstallments, avgRateMargin, avgAplliedRate,
						avgEmiPerc, stepType);
				break;
			}
		}

		logger.debug("Leaving ");

	}

	private void addFooter(int size, BigDecimal tenorPerc, int totInstallments, BigDecimal avgRateMargin,
			BigDecimal avgAplliedRate, BigDecimal avgEmiPerc, String stepType) {

		Listitem listItem = new Listitem();
		listItem.setStyle("background-color: #C0EBDF;");

		Listcell lc = new Listcell(Labels.getLabel("label_StepDetailsFooter"));
		lc.setStyle("text-align:left;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(PennantApplicationUtil.formatRate(tenorPerc.doubleValue(), 2) + "%");
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		lc = new Listcell(String.valueOf(totInstallments));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		avgRateMargin = avgRateMargin.divide(new BigDecimal(size), 9, RoundingMode.HALF_DOWN);
		lc = new Listcell(PennantApplicationUtil.formatRate(avgRateMargin.doubleValue(), 9));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(listItem);

		avgAplliedRate = avgAplliedRate.divide(new BigDecimal(size), 9, RoundingMode.HALF_DOWN);
		lc = new Listcell(PennantApplicationUtil.formatRate(avgAplliedRate.doubleValue(), 9));
		lc.setParent(listItem);
		lc.setStyle("text-align:right;font-weight:bold;");

		if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)) {
			avgEmiPerc = avgEmiPerc.divide(new BigDecimal(size), 2, RoundingMode.HALF_DOWN);
		}
		lc = new Listcell(PennantApplicationUtil.formatRate(avgEmiPerc.doubleValue(), 2) + "%");
		lc.setParent(listItem);
		lc.setStyle("text-align:right;font-weight:bold;");

		lc = new Listcell("");
		lc.setStyle("text-align:right;");
		lc.setParent(listItem);

		lc = new Listcell("");
		lc.setStyle("text-align:right;");
		lc.setParent(listItem);

		this.listBoxRestructureSteps.appendChild(listItem);
	}

	public void setFinStepPoliciesList(List<FinanceStepPolicyDetail> finStepPoliciesList) {

		this.finStepPolicyList = new ArrayList<>();
		List<FinanceStepPolicyDetail> spdList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(finStepPoliciesList)) {
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();

			for (FinanceStepPolicyDetail spd : finStepPoliciesList) {
				rpySpdList.add(spd);
			}
			Collections.sort(rpySpdList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
					: step1.getStepNo() < step2.getStepNo() ? -1 : 0);
			spdList.addAll(rpySpdList);
			if (CollectionUtils.isNotEmpty(finStepPolicyList)) {
				this.finStepPolicyList.clear();
			}
		}
		this.finStepPolicyList.addAll(spdList);
	}

	private void doRenderData(FinScheduleData scheduleData) {
		logger.debug(Literal.ENTERING);

		rstDetail = scheduleData.getRestructureDetail();
		if (rstDetail == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		fillComboBox(this.restructuringType, rstDetail.getRestructureType(),
				PennantAppUtil.getRestructureType(scheduleData.getFinanceMain().isStepFinance()), "");
		fillComboBox(this.recalculationType, rstDetail.getRecalculationType(), PennantStaticListUtil.getRecalTypeList(),
				"");

		String defaultRestructure = StringUtils.trimToNull(rstDetail.getRestructureReason());
		String fldCode = StringUtils.trimToNull(lovFieldDetail.getLovValue());
		if (defaultRestructure == null && fldCode != null) {
			defaultRestructure = lovFieldDetail.getFieldCodeValue();
		}

		fillComboBox(this.restructuringReason, defaultRestructure, lovFieldDetail.getValueLabelList(), "");

		fillSchDates(this.restructureDate, scheduleData, scheduleData.getFinanceMain().getFinStartDate(),
				rstDetail.getRestructureDate());

		// Restructure Date Allowed as Input Field/selection based on Parameters
		if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {

			this.restructureDate.setDisabled(true);
			this.restructureDate.setVisible(false);

			this.restructureDateIn.setVisible(true);
			this.restructureDateIn.setValue(rstDetail.getRestructureDate());
			this.restructureDateIn.setDisabled(true);
		} else {
			this.restructureDateIn.setDisabled(true);
			this.restructureDateIn.setVisible(false);
		}

		// Rate Change Allowed
		if (ImplementationConstants.RESTRUCTURE_RATE_CHG_ALW) {
			this.rateReviewRow.setVisible(true);
		}

		this.actRate.setValue(rstDetail.getRepayProfitRate());
		this.baseRate.setBaseValue(rstDetail.getBaseRate());
		this.baseRate.setSpecialValue(rstDetail.getSplRate());
		this.baseRate.setMarginValue(rstDetail.getMargin());

		this.numberOfEMIHoliday.setValue(rstDetail.getEmiHldPeriod());
		this.numberOfPriHoliday.setValue(rstDetail.getPriHldPeriod());
		this.numberOfEMITerms.setValue(rstDetail.getEmiPeriods());
		this.totNoOfRestructuring.setValue(rstDetail.getTotNoOfRestructure());

		this.grcMaxAmount.setValue(
				PennantApplicationUtil.unFormateAmount(rstDetail.getGrcMaxAmount(), PennantConstants.defaultCCYDecPos));
		this.serviceReqNo.setValue(rstDetail.getServiceRequestNo());
		this.remarks.setValue(rstDetail.getRemark());

		// Fetch All charges and rendering for user selection
		if (ImplementationConstants.RESTRUCTURE_ALW_CHARGES) {
			this.listBoxCharges.setVisible(true);
			doFillCharges(rstDetail.getChargeList());
		}

		logger.debug(Literal.LEAVING);
	}

	private void doReadOnly() {
		this.restructuringType.setDisabled(true);
		this.restructuringReason.setDisabled(true);
		this.restructureDate.setDisabled(true);
		this.restructureDateIn.setDisabled(true);
		this.numberOfEMIHoliday.setReadonly(true);
		this.numberOfPriHoliday.setReadonly(true);
		this.numberOfEMITerms.setReadonly(true);
		this.totNoOfRestructuring.setReadonly(true);
		this.actRate.setDisabled(true);
		this.baseRate.getBaseComp().setReadonly(true);
		this.baseRate.getSpecialComp().setReadonly(true);
		this.baseRate.getMarginComp().setDisabled(true);
		this.grcMaxAmount.setDisabled(true);
		this.recalculationType.setDisabled(true);
		this.serviceReqNo.setReadonly(true);
		this.remarks.setReadonly(true);
		this.btnRestructure.setVisible(false);
	}

	public void onChange$numberOfEMIHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeTenurePeriod();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$numberOfEMITerms(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeTenurePeriod();
		logger.debug(Literal.LEAVING);
	}

	public void onChange$numberOfPriHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeTenurePeriod();
		logger.debug(Literal.LEAVING);
	}

	private void onChangeTenurePeriod() {
		int totValue = this.numberOfEMIHoliday.intValue() + this.numberOfEMITerms.intValue()
				+ this.numberOfPriHoliday.intValue();
		this.totNoOfRestructuring.setValue(totValue);
	}

	/** To fill schedule dates */
	private void fillSchDates(Combobox dateCombobox, FinScheduleData aFinSchData, Date fillAfter,
			Date restructureDate) {
		logger.debug(Literal.ENTERING);

		dateCombobox.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (aFinSchData.getFinanceScheduleDetails() != null) {
			List<FinanceScheduleDetail> financeScheduleDetails = aFinSchData.getFinanceScheduleDetails();

			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Not Allowed for Repayment
				if (!(curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
					continue;
				}
				// Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}
				// Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// When Add terms done with zero installments and trying with TILLDATE option
				// Date after closing balance is Zero and not a maturity date should not allow
				if (curSchd.getSchDate().compareTo(aFinSchData.getFinanceMain().getMaturityDate()) != 0
						&& curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				if (FinanceConstants.FLAG_HOLIDAY.equals(curSchd.getBpiOrHoliday())) {
					continue;
				}

				if (fillAfter.compareTo(curSchd.getSchDate()) < 0) {
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
					comboitem.setValue(curSchd.getSchDate());
					dateCombobox.appendChild(comboitem);

					if (restructureDate != null && DateUtil.compare(restructureDate, curSchd.getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void findMinAllowedRestructureDate(FinScheduleData aFinSchData) {
		logger.debug(Literal.ENTERING);

		if (aFinSchData.getFinanceScheduleDetails() != null) {
			List<FinanceScheduleDetail> financeScheduleDetails = aFinSchData.getFinanceScheduleDetails();

			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if (curSchd.getSchDate().compareTo(appDate) <= 0) {
					if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
						this.baseRate.setBaseValue(curSchd.getBaseRate());
						this.baseRate.setSpecialValue(curSchd.getSplRate());
						this.baseRate.setMarginValue(curSchd.getMrgRate());
					} else {
						this.actRate.setValue(curSchd.getActRate());
					}
				}

				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					fullyPaidDate = curSchd.getSchDate();
					continue;
				}

				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					fullyPaidDate = curSchd.getSchDate();
					continue;
				}

				if (curSchd.isDisbOnSchDate() || curSchd.getPresentmentId() > 0) {
					fullyPaidDate = curSchd.getSchDate();
					continue;
				}
			}
		}

		// Checking Manual Advise Last max Value Date before Application/Restructuring Date
		FinanceMain fm = aFinSchData.getFinanceMain();
		Date maxValueDate = restructureService.getMaxValueDateOfRcv(fm.getFinID());
		if (maxValueDate != null && DateUtil.compare(maxValueDate, fullyPaidDate) > 0) {
			fullyPaidDate = maxValueDate;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Selecting Restructuring Type
	 * 
	 * @param event
	 */
	public void onChange$restructuringType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (this.restructuringType.getSelectedIndex() <= 0) {
			logger.debug(Literal.LEAVING + event.toString());
			return;
		}

		List<RestructureType> restructureTypeList = PennantAppUtil
				.getRestructureType(Long.valueOf(getComboboxValue(this.restructuringType)));
		restructureType = restructureTypeList.get(0);

		this.numberOfEMIHoliday.setValue(0);
		this.numberOfEMITerms.setValue(0);
		this.numberOfPriHoliday.setValue(0);
		this.totNoOfRestructuring.setValue(0);
		this.grcMaxAmount.setValue(BigDecimal.ZERO);

		if (restructureType.getMaxEmiHoliday() > 0) {
			this.numberOfEMIHoliday.setReadonly(false);
		} else {
			this.numberOfEMIHoliday.setReadonly(true);
		}

		if (restructureType.getMaxPriHoliday() > 0) {
			this.numberOfPriHoliday.setReadonly(false);
			this.grcMaxAmount.setReadonly(false);
		} else {
			this.numberOfPriHoliday.setReadonly(true);
			this.grcMaxAmount.setReadonly(true);
		}

		if (restructureType.getMaxEmiTerm() > 0) {
			this.numberOfEMITerms.setReadonly(false);
		} else {
			this.numberOfEMITerms.setReadonly(true);
		}

		String recalType = CalculationConstants.RST_RECAL_ADDTERM_RECALEMI;
		if (StringUtils.equals("Scenario7", restructureType.getRstTypeCode())
				|| StringUtils.equals("Scenario8", restructureType.getRstTypeCode())) {
			recalType = CalculationConstants.RST_RECAL_ADJUSTTENURE;
			this.numberOfEMITerms.setReadonly(false);
		}
		fillComboBox(this.recalculationType, recalType, PennantStaticListUtil.getRecalTypeList(), "");

		if (StringUtils.equals("Scenario9", restructureType.getRstTypeCode())
				|| StringUtils.equals("Scenario10", restructureType.getRstTypeCode())
				|| StringUtils.equals("Scenario11", restructureType.getRstTypeCode())) {
			showStepDetails(finScheduleData);
		} else {
			this.gb_RestructureStep.setVisible(false);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$restructureDate(Event event) {
		logger.debug("Entering" + event.toString());

		// Fetch All charges and rendering for user selection
		if (!ImplementationConstants.RESTRUCTURE_ALW_CHARGES || this.restructureDate.getSelectedIndex() <= 0) {
			logger.debug("Leaving" + event.toString());
			return;
		}

		List<RestructureCharge> chargeList = restructureService.getRestructureChargeList(getFinScheduleData(),
				(Date) this.restructureDate.getSelectedItem().getValue());
		doFillCharges(chargeList);
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$restructureDateIn(Event event) {
		logger.debug("Entering" + event.toString());

		// Fetch All charges and rendering for user selection
		if (!ImplementationConstants.RESTRUCTURE_ALW_CHARGES) {
			logger.debug("Leaving" + event.toString());
			return;
		}

		if (this.restructureDateIn.getValue() == null) {
			logger.debug("Leaving" + event.toString());
			return;
		}

		FinanceMain fm = finScheduleData.getFinanceMain();
		if (DateUtil.compare(this.restructureDateIn.getValue(), fm.getFinStartDate()) < 0) {
			throw new WrongValueException(this.restructureDateIn,
					"Restructure Date should be greater than Loan Start Date");
		}

		List<RestructureCharge> chargeList = restructureService.getRestructureChargeList(getFinScheduleData(),
				this.restructureDateIn.getValue());
		rstDetail.setChargeList(chargeList);
		doFillCharges(chargeList);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for rendering restructuring Charges
	 * 
	 * @param chargeList
	 */
	private void doFillCharges(List<RestructureCharge> chargeList) {
		logger.debug(Literal.ENTERING);

		this.listBoxCharges.getItems().clear();
		int ccyDecPos = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());

		if (CollectionUtils.isNotEmpty(chargeList)) {

			BigDecimal totalCpz = BigDecimal.ZERO;
			for (RestructureCharge charge : chargeList) {
				Listitem item = new Listitem();

				Checkbox cb = new Checkbox();
				cb.setAttribute("data", charge);
				cb.setChecked(charge.isCapitalized());
				cb.addForward("onClick", self, "onClick_Capitalized");
				if (StringUtils.equals(charge.getAlocType(), Allocation.MANADV)
						|| StringUtils.equals(charge.getAlocType(), Allocation.BOUNCE)
						|| StringUtils.equals(charge.getAlocType(), Allocation.ODC)
						|| StringUtils.equals(charge.getAlocType(), Allocation.LPFT)) {
					cb.setDisabled(false);
				} else {
					cb.setDisabled(true);
				}
				Listcell lc = new Listcell();
				lc.appendChild(cb);
				lc.setParent(item);

				lc = new Listcell(charge.getAlocType() + " - " + charge.getAlocTypeDesc());
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getActualAmount(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getTdsAmount(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(charge.getTaxType());
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getCgst(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getSgst(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getUgst(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getIgst(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getCess(), ccyDecPos));
				lc.setParent(item);

				lc = new Listcell(PennantApplicationUtil.amountFormate(charge.getTotalAmount(), ccyDecPos));
				lc.setParent(item);

				this.listBoxCharges.appendChild(item);

				if (charge.isCapitalized()) {
					totalCpz = totalCpz.add(charge.getTotalAmount());
				}
			}

			Listitem item = new Listitem();
			item.setStyle("background-color: #C0EBDF;");

			Listcell lc = new Listcell();
			lc.setParent(item);

			lc = new Listcell(Labels.getLabel("lable_RestructureDialog_TotalCpzAmount"));
			lc.setStyle("text-align:left;font-weight:bold;");
			lc.setSpan(9);
			lc.setParent(item);

			lc = new Listcell(PennantApplicationUtil.amountFormate(totalCpz, ccyDecPos));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);

			this.listBoxCharges.appendChild(item);

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for selecting charges for capitalization calculation amount
	 * 
	 * @param event
	 */
	public void onClick_Capitalized(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Checkbox checkBox = (Checkbox) event.getOrigin().getTarget();
		RestructureCharge actCharge = (RestructureCharge) checkBox.getAttribute("data");

		if (rstDetail != null && rstDetail.getChargeList() != null) {
			for (RestructureCharge rsChrg : rstDetail.getChargeList()) {
				if (rsChrg.getChargeSeq() != actCharge.getChargeSeq()) {
					continue;
				}
				rsChrg.setCapitalized(checkBox.isChecked());
				break;
			}
		}

		setRstDetail(rstDetail);
		doFillCharges(rstDetail.getChargeList());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();
		FinServiceInstruction fsi = new FinServiceInstruction();
		List<FinanceScheduleDetail> schedules = getFinScheduleData().getFinanceScheduleDetails();

		try {
			if (isValidComboValue(this.restructuringType,
					Labels.getLabel("label_RestructureDialog_RestructuringType.value"))) {
				fsi.setRestructuringType(this.restructuringType.getSelectedItem().getValue());
				rstDetail.setRestructureType(getComboboxValue(this.restructuringType));
				rstDetail.setRstTypeCode(this.restructureType.getRstTypeCode());
				rstDetail.setRstTypeDesc(this.restructureType.getRstTypeDesc());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.restructuringReason,
					Labels.getLabel("label_RestructureDialog_RestructuringReason.value"))) {
				rstDetail.setRestructureReason(getComboboxValue(this.restructuringReason));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				try {
					rstDetail.setRestructureDate(this.restructureDateIn.getValue());
				} catch (WrongValueException we) {
					throw new WrongValueException(this.restructureDateIn,
							Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
									new String[] { Labels.getLabel("label_RestructureDialog_RestructureDate.value"),
											DateUtil.formatToShortDate(fullyPaidDate),
											DateUtil.formatToShortDate(appDate) }));
				}

			} else {
				if (isValidComboValue(this.restructureDate,
						Labels.getLabel("label_RestructureDialog_RestructureDate.value"))) {
					rstDetail.setRestructureDate((Date) this.restructureDate.getSelectedItem().getValue());
				}
			}
			fsi.setFromDate(rstDetail.getRestructureDate());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.recalculationType, Labels.getLabel("label_RestructureDialog_SchdMthd.value"))) {

				fsi.setRecalType(getComboboxValue(this.recalculationType));
				rstDetail.setRecalculationType(getComboboxValue(this.recalculationType));

				if (CalculationConstants.RST_RECAL_ADJUSTTENURE.equals(fsi.getRecalType())) {
					rstDetail.setTenorChange(true);
					rstDetail.setEmiRecal(false);
				} else if (CalculationConstants.RST_RECAL_RECALEMI.equals(fsi.getRecalType())) {
					rstDetail.setTenorChange(false);
					rstDetail.setEmiRecal(true);
				} else {
					rstDetail.setTenorChange(true);
					rstDetail.setEmiRecal(true);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.numberOfEMIHoliday.intValue() >= 0) {
				rstDetail.setEmiHldPeriod(this.numberOfEMIHoliday.intValue());
			}
			if (this.numberOfEMITerms.intValue() >= 0) {
				rstDetail.setEmiPeriods(this.numberOfEMITerms.intValue());
			}

			// validate Tenor for Step Loans
			if (StringUtils.equals("Scenario9", restructureType.getRstTypeCode())
					|| StringUtils.equals("Scenario10", restructureType.getRstTypeCode())
					|| StringUtils.equals("Scenario11", restructureType.getRstTypeCode())) {
				int noOfEMITerms = getFinScheduleData().getFinanceMain().getNumberOfTerms() + rstDetail.getEmiPeriods();
				int noOfStepTerms = 0;

				for (FinanceStepPolicyDetail stepPolicyDetail : finStepPolicyList) {
					noOfStepTerms = noOfStepTerms + stepPolicyDetail.getInstallments();
				}

				if (noOfEMITerms != noOfStepTerms) {
					throw new WrongValueException(this.numberOfEMITerms,
							Labels.getLabel("label_RestructureDialog_NumberOfSteps.value"));
				}
			}

			if (this.numberOfPriHoliday.intValue() >= 0) {
				rstDetail.setPriHldPeriod(this.numberOfPriHoliday.intValue());
			}
			if (this.totNoOfRestructuring.intValue() <= 0) {
				throw new WrongValueException(this.totNoOfRestructuring, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_RestructureDialog_NumberOftTerms.value"), " 0 " }));
			}
			fsi.setTerms(this.totNoOfRestructuring.intValue());
			rstDetail.setTotNoOfRestructure(this.totNoOfRestructuring.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (ImplementationConstants.RESTRUCTURE_RATE_CHG_ALW) {
			try {
				fsi.setBaseRate(StringUtils.trimToNull(this.baseRate.getBaseValue()));
				rstDetail.setBaseRate(StringUtils.trimToNull(this.baseRate.getBaseValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				fsi.setSplRate(StringUtils.trimToNull(this.baseRate.getSpecialValue()));
				rstDetail.setSplRate(StringUtils.trimToNull(this.baseRate.getSpecialValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				fsi.setMargin(this.baseRate.getMarginValue());
				rstDetail.setMargin(this.baseRate.getMarginValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.actRate.getValue() != null && !this.actRate.isReadonly()) {
					if (this.actRate.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(this.actRate, Labels.getLabel("NUMBER_NOT_NEGATIVE",
								new String[] { Labels.getLabel("label_RateChangeDialog_Rate.value") }));
					}
					fsi.setActualRate(this.actRate.getValue());
					rstDetail.setRepayProfitRate(this.actRate.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// IF Single Rate required based on Origination Selection please comment below try-catch block
			try {
				if ((this.rateReviewRow.isVisible() && this.actRate.getValue() != null
						&& this.actRate.getValue().compareTo(BigDecimal.ZERO) > 0)
						&& (StringUtils.isNotEmpty(this.baseRate.getBaseValue()))) {
					throw new WrongValueException(this.actRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_RateChangeDialog_BaseRate.value"),
											Labels.getLabel("label_RateChangeDialog_Rate.value") }));
				}
				if ((this.rateReviewRow.isVisible() && this.actRate.getValue() == null)
						&& (StringUtils.isEmpty(this.baseRate.getBaseValue()))) {
					throw new WrongValueException(this.actRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_RateChangeDialog_BaseRate.value"),
											Labels.getLabel("label_RateChangeDialog_Rate.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// BaseRate margin validation
			try {
				if (StringUtils.trimToNull(this.baseRate.getBaseValue()) == null
						&& this.baseRate.getMarginValue() != null
						&& this.baseRate.getMarginValue().compareTo(BigDecimal.ZERO) != 0) {
					throw new WrongValueException(baseRate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
							new String[] { Labels.getLabel("label_RateChangeDialog_MarginRate.value") }));

				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			rstDetail.setGrcMaxAmount(PennantApplicationUtil.formateAmount(this.grcMaxAmount.getActualValue(),
					PennantConstants.defaultCCYDecPos));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fsi.setServiceReqNo(this.serviceReqNo.getValue());
			rstDetail.setServiceRequestNo(this.serviceReqNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fsi.setRemarks(this.remarks.getValue());
			rstDetail.setRemark(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		// If Future Restructure date is not available based on selection
		if (restructureDate == null) {
			MessageUtil.showError("No installment pending which is greater than current app date.");
			return;
		}

		// Restructure cannot be done if presentment already sent for future date
		int rstHolidayPeriod = rstDetail.getEmiHldPeriod() + rstDetail.getPriHldPeriod();
		int remainingRepayPeriods = 0;
		Date restructureDate = null;

		for (int iFsd = 0; iFsd < schedules.size() - 1; iFsd++) {
			FinanceScheduleDetail fsd = schedules.get(iFsd);
			if (fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) < 0) {
				rstDetail.setLastBilledDate(fsd.getSchDate());
				rstDetail.setLastBilledInstNo(fsd.getInstNumber());
				continue;
			}
			if (fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) >= 0 && fsd.isFrqDate()
					&& (fsd.isRepayOnSchDate() || fsd.isPftOnSchDate())) {
				if (restructureDate == null) {
					restructureDate = fsd.getSchDate();
				}
				remainingRepayPeriods = remainingRepayPeriods + 1;
			}
		}

		// When Tenor Intact required then after restructured period atleast one installment should be available for
		// keeping EMI intact and recalculate
		if (!rstDetail.isTenorChange()) {
			if (rstHolidayPeriod >= remainingRepayPeriods) {
				MessageUtil.showError(
						"Total of EMI Holidays + Principal Holidays must be less than future repayment periods for EMI intact schedules");
				return;
			}
		}

		// For Step Detail that has overdue & future installments , no of installments cannot be less than total
		// no of overdue installments
		if (finScheduleData.getFinanceMain().isStepFinance()) {
			for (FinanceStepPolicyDetail spd : finStepPolicyList) {
				int instCount = 0;
				if (spd.getStepEnd() != null && spd.getStepEnd().compareTo(rstDetail.getRestructureDate()) > 0) {
					for (FinanceScheduleDetail fsd : schedules) {
						if (fsd.getSchDate().compareTo(spd.getStepStart()) >= 0
								&& fsd.getSchDate().compareTo(rstDetail.getRestructureDate()) < 0) {
							instCount++;
						}
					}
					if (spd.getInstallments() < instCount) {
						MessageUtil.showError("No of Installments cannot be less than " + instCount + " for Step No "
								+ spd.getStepNo());
						return;
					}
				}
			}
		}

		// check if noofSteps & step details are equal
		if (CollectionUtils.isNotEmpty(finStepPolicyList) && this.noOfSteps.intValue() != finStepPolicyList.size()) {
			MessageUtil.showError("Step Details should be equal to Repay steps " + this.noOfSteps.intValue());
			return;
		}

		// check last step should not have amount & remaining steps should have amount
		if (CollectionUtils.isNotEmpty(finStepPolicyList) && this.noOfSteps.intValue() != 0) {
			List<FinanceStepPolicyDetail> stpDtls = finStepPolicyList;
			for (int i = 0; i < stpDtls.size(); i++) {
				if (i != this.noOfSteps.intValue() - 1) {
					if (stpDtls.get(i).getSteppedEMI() != null
							&& stpDtls.get(i).getSteppedEMI().compareTo(BigDecimal.ZERO) == 0) {
						MessageUtil.showError("EMI Amount cannot be 0 for Step No: " + stpDtls.get(i).getStepNo());
						return;
					}
				}
			}
		}

		// Service details calling for Schedule calculation
		getFinScheduleData().setRestructureDetail(rstDetail);
		getFinScheduleData().setStepPolicyDetails(finStepPolicyList);
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		finMain.setDevFinCalReq(false);

		if (StringUtils.equals("Scenario9", restructureType.getRstTypeCode())
				|| StringUtils.equals("Scenario10", restructureType.getRstTypeCode())
				|| StringUtils.equals("Scenario11", restructureType.getRstTypeCode()) && !finMain.isStepFinance()) {
			finMain.setStepFinance(true);
			finMain.setStepPolicy(this.stepPolicy.getValue());
			finMain.setStepType(this.stepType.getSelectedItem().getValue().toString());
			finMain.setAlwManualSteps(this.alwManualSteps.isChecked());
			finMain.setStepsAppliedFor(this.stepsAppliedFor.getSelectedItem().getValue().toString());
			finMain.setCalcOfSteps(this.calcOfSteps.getSelectedItem().getValue().toString());
		}

		if (finMain.isStepFinance()) {
			finMain.setRecalType(CalculationConstants.RPYCHG_ADDRECAL);
			finMain.setNoOfSteps(this.noOfSteps.intValue());
		}

		setFinScheduleData(restructureService.doRestructure(getFinScheduleData(), fsi));

		// Show Error Details in Schedule Maintenance
		if (getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		} else {
			getFinScheduleData().setSchduleGenerated(true);
			if (getFinanceMainDialogCtrl() != null) {
				try {
					getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public List<ErrorDetail> doValidateStepDetails() {
		logger.debug("Entering");

		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinScheduleData().getFinanceType();
		String stepAppliedOn = "";
		stepAppliedOn = financeMain.getStepsAppliedFor() != null ? financeMain.getStepsAppliedFor()
				: financeType.getStepsAppliedFor();
		int totalTerms = financeMain.getNumberOfTerms();

		String specifier = "";
		if (PennantConstants.STEPPING_APPLIED_EMI.equals(stepAppliedOn)
				|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepAppliedOn)) {
			specifier = PennantConstants.STEP_SPECIFIER_REG_EMI;
		} else if (PennantConstants.STEPPING_APPLIED_GRC.equals(stepAppliedOn)
				|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepAppliedOn)) {
			specifier = PennantConstants.STEP_SPECIFIER_GRACE;
		}

		boolean isAlwManualSteps = this.alwManualSteps.isChecked();
		this.noOfSteps.setErrorMessage("");
		int noOfSteps = 0;
		int stepCount = 0;
		financeMain.setRpyStps(false);
		financeMain.setGrcStps(false);
		String stepType = this.stepType.getSelectedItem().getValue();
		String calculatedOn = "";
		int repaySteps = 0;
		int grcSteps = 0;
		calculatedOn = financeMain.isStepFinance() ? financeMain.getCalcOfSteps() : financeType.getCalcOfSteps();
		List<ErrorDetail> errorList = new ArrayList<ErrorDetail>();

		if (CollectionUtils.isNotEmpty(finStepPolicyList)) {

			List<FinanceStepPolicyDetail> graceSpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> rpySpdList = new ArrayList<>();
			List<FinanceStepPolicyDetail> spdList = new ArrayList<>();
			int rpyTerms = 0;
			int grcTerms = 0;

			for (FinanceStepPolicyDetail spd : finStepPolicyList) {
				if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
					repaySteps = repaySteps + 1;
					rpyTerms = rpyTerms + spd.getInstallments();
					rpySpdList.add(spd);
					financeMain.setRpyStps(true);
				} else if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_GRACE)) {
					grcSteps = grcSteps + 1;
					graceSpdList.add(spd);
					grcTerms = grcTerms + spd.getInstallments();
					financeMain.setGrcStps(true);
				}
			}

			String label = "";
			if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(specifier)) {
				spdList = rpySpdList;
				noOfSteps = this.noOfSteps.intValue();
				label = Labels.getLabel("label_FinanceMainDialog_RepaySteps.value");
				stepCount = repaySteps;
			} else {
				spdList = graceSpdList;
				noOfSteps = this.grcSteps.intValue();
				label = Labels.getLabel("label_FinanceMainDialog_GrcSteps.value");
				stepCount = grcSteps;
			}

			if (CollectionUtils.isNotEmpty(graceSpdList)) {
				BigDecimal totSteppedEMIInst = BigDecimal.ZERO;
				BigDecimal totSteppedEMI = BigDecimal.ZERO;
				for (FinanceStepPolicyDetail spd : graceSpdList) {
					BigDecimal inst = BigDecimal.valueOf(spd.getInstallments());
					totSteppedEMIInst = inst.multiply(spd.getSteppedEMI());
					totSteppedEMI = totSteppedEMI.add(totSteppedEMIInst);
					if ((totSteppedEMI.compareTo(financeMain.getFinCurrAssetValue()) > 0)) {
						errorList.add(new ErrorDetail(PennantConstants.KEY_FIELD, "STP0011", null, null));
						return errorList;
					}
				}
			}

			if (isAlwManualSteps) {

				if ((StringUtils.equals(stepAppliedOn, PennantConstants.STEPPING_APPLIED_BOTH)
						&& (CollectionUtils.isEmpty(rpySpdList) && CollectionUtils.isEmpty(graceSpdList)))
						|| (StringUtils.equals(stepAppliedOn, PennantConstants.STEPPING_APPLIED_EMI)
								&& CollectionUtils.isEmpty(rpySpdList))
						|| (StringUtils.equals(stepAppliedOn, PennantConstants.STEPPING_APPLIED_GRC)
								&& CollectionUtils.isEmpty(graceSpdList))) {
					errorList.add(new ErrorDetail("30541", PennantConstants.KEY_SEPERATOR, new String[] {}));
					return errorList;
				}
			}

			if (isAlwManualSteps && stepCount != noOfSteps) {
				errorList.add(new ErrorDetail("STP005", PennantConstants.KEY_SEPERATOR, new String[] { label, label }));
				return errorList;
			}

			if (CollectionUtils.isNotEmpty(spdList)) {

				if (isAlwManualSteps && StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_REG_EMI)
						&& totalTerms != rpyTerms) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TotStepInstallments", new String[] { "Repay" }),
									Labels.getLabel("label_TotalTerms") }));
					return errorList;
				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE) && totalTerms != grcTerms) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TotStepInstallments", new String[] { "Grace" }),
									Labels.getLabel("label_GrcTotalTerms") }));
					return errorList;
				}

				int sumInstallments = 0;
				BigDecimal sumTenurePerc = BigDecimal.ZERO;

				BigDecimal calTotTenorSplit = BigDecimal.ZERO;
				BigDecimal calTotEmiStepPercent = BigDecimal.ZERO;
				boolean hadZeroInstStep = false;
				BigDecimal tenurePerc = BigDecimal.ZERO;

				for (int i = 0; i < spdList.size(); i++) {
					FinanceStepPolicyDetail stepPolicy = spdList.get(i);

					if (stepPolicy.getInstallments() > 0 && isAlwManualSteps) {
						tenurePerc = (new BigDecimal(stepPolicy.getInstallments()).multiply(new BigDecimal(100)))
								.divide(new BigDecimal(totalTerms), 2, RoundingMode.HALF_DOWN);
						stepPolicy.setTenorSplitPerc(tenurePerc);
						sumTenurePerc = sumTenurePerc.add(tenurePerc);
						sumInstallments = sumInstallments + stepPolicy.getInstallments();
						if (i == (spdList.size() - 1) && sumInstallments == totalTerms) {
							if (sumTenurePerc.compareTo(new BigDecimal(100)) != 0) {
								stepPolicy.setTenorSplitPerc(stepPolicy.getTenorSplitPerc().add(new BigDecimal(100))
										.subtract(sumTenurePerc));
							}
						}

					} else if (stepPolicy.getTenorSplitPerc().compareTo(BigDecimal.ZERO) > 0) {

						BigDecimal terms = stepPolicy.getTenorSplitPerc().multiply(new BigDecimal(totalTerms))
								.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
						sumInstallments = sumInstallments + Integer.parseInt(terms.toString());
						stepPolicy.setInstallments(Integer.parseInt(terms.toString()));
						if (i == (spdList.size() - 1)) {
							if (sumInstallments != totalTerms) {
								stepPolicy.setInstallments(stepPolicy.getInstallments() + totalTerms - sumInstallments);
							}
						}
						sumTenurePerc = sumTenurePerc.add(stepPolicy.getTenorSplitPerc());
					}

					if (stepPolicy.getInstallments() == 0) {
						hadZeroInstStep = true;
					}

					calTotTenorSplit = calTotTenorSplit.add(stepPolicy.getTenorSplitPerc());

					calTotEmiStepPercent = calTotEmiStepPercent.add(stepPolicy.getEmiSplitPerc());

					// Setting Bean Property Field Details
					if (StringUtils.isBlank(stepPolicy.getRecordType())) {
						stepPolicy.setVersion(stepPolicy.getVersion() + 1);
						stepPolicy.setRecordType(PennantConstants.RCD_ADD);
						stepPolicy.setNewRecord(true);
					}
					stepPolicy.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					stepPolicy.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					stepPolicy.setUserDetails(getUserWorkspace().getLoggedInUser());

				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_REG_EMI)) {
					doFillStepDetails(finStepPolicyList);
				} else if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE)) {
					// doFillStepDetaisForGrace(finStepPolicyList);
				}

				// If Any Step Policy have Zero installments while on
				// Calculation
				if (hadZeroInstStep) {
					errorList.add(new ErrorDetail("30569", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_MinInstallment"), " 1 " }));
					return errorList;
				}

				// Tenor Percentage Validation for Repay Step Policy Details
				if (calTotTenorSplit.compareTo(new BigDecimal(100)) != 0) {
					errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
							new String[] { Labels.getLabel("label_TenorSplitPerc"), "100.00 %" }));
					return errorList;
				}

				// Average EMI Percentage/ Total Percentage based on Step Type
				// Validation for Step Policy Details
				if (StringUtils.equals(calculatedOn, PennantConstants.STEPPING_CALC_PERC)) {
					if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_EMI)) {
						BigDecimal emiStepPercAvg = calTotEmiStepPercent
								.divide(new BigDecimal(finStepPolicyList.size()), 0, RoundingMode.HALF_DOWN);
						if (emiStepPercAvg.compareTo(new BigDecimal(100)) != 0) {
							errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("label_AvgEMISplitPerc"), "100.00 %" }));
							return errorList;
						}
					} else if (StringUtils.equals(stepType, FinanceConstants.STEPTYPE_PRIBAL)) {
						if (calTotEmiStepPercent.compareTo(new BigDecimal(100)) != 0) {
							errorList.add(new ErrorDetail("30540", PennantConstants.KEY_SEPERATOR, new String[] {
									Labels.getLabel("label_OutStandingPrincipalSplitPerc"), "100.00 %" }));
							return errorList;
						}
					}
				}

				if (StringUtils.equals(specifier, PennantConstants.STEP_SPECIFIER_GRACE)) {
					errorList.add(new ErrorDetail("STP001", PennantConstants.KEY_SEPERATOR, null));
				}

				if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(specifier)
						&& StringUtils.equals(calculatedOn, PennantConstants.STEPPING_CALC_AMT)) {

					for (FinanceStepPolicyDetail financeStepPolicyDetail : spdList) {
						if (financeStepPolicyDetail.getStepNo() > repaySteps) {
							errorList.add(new ErrorDetail("90405", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
											+ String.valueOf(financeStepPolicyDetail.getStepNo()) }));
							return errorList;
						} else if (financeStepPolicyDetail.getStepNo() < repaySteps
								&& financeStepPolicyDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) <= 0) {
							errorList.add(new ErrorDetail("91121", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value"),
											String.valueOf(BigDecimal.ZERO) }));
							return errorList;
						} else if (financeStepPolicyDetail.getStepNo() == repaySteps
								&& financeStepPolicyDetail.getSteppedEMI().compareTo(BigDecimal.ZERO) > 0) {
							errorList.add(new ErrorDetail("90277", PennantConstants.KEY_SEPERATOR,
									new String[] {
											"Last " + Labels.getLabel("label_FinStepPolicyDialog_SteppedEMI.value"),
											String.valueOf(BigDecimal.ZERO) }));
							return errorList;
						}
					}
				}

				if (PennantConstants.STEP_SPECIFIER_GRACE.equals(specifier)) {
					for (FinanceStepPolicyDetail spd : spdList) {
						if (spd.getStepNo() > grcSteps) {
							errorList.add(new ErrorDetail("90405", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
											+ String.valueOf(spd.getStepNo()) }));
							return errorList;
						} else if (spd.getStepNo() < grcSteps && spd.isAutoCal()) {
							errorList.add(new ErrorDetail("STP004", PennantConstants.KEY_SEPERATOR,
									new String[] { Labels.getLabel("listheader_StepFinanceGrace_StepNo.label") + " "
											+ String.valueOf(spd.getStepNo()) }));
						}
					}
				}

			}
		} else {
			if (isAlwManualSteps) {
				errorList.add(new ErrorDetail("30541", PennantConstants.KEY_SEPERATOR, new String[] {}));
			}
		}
		logger.debug("Leaving");
		return errorList;
	}

	/**
	 * Double Click on Step Policy Item
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFinStepPolicyItemDoubleClicked(Event event) throws InterruptedException {
		logger.debug("Entering");

		// get the selected Academic object
		final Listitem item = this.listBoxRestructureSteps.getSelectedItem();

		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceStepPolicyDetail aFinStepPolicy = (FinanceStepPolicyDetail) item.getAttribute("data");
			if (validateStepPolicyDetail(aFinStepPolicy)) {
				MessageUtil.showError("Not able to Maintain");
				return;
			}

			openFinStepPolicyDetailDialog(aFinStepPolicy, false);
		}
		logger.debug("Leaving");
	}

	private Boolean validateStepPolicyDetail(FinanceStepPolicyDetail aFinStepPolicy) {
		List<FinanceScheduleDetail> schedules = getFinScheduleData().getFinanceScheduleDetails();
		int idxStart = 0;
		Date stepEndDate = null;

		if (aFinStepPolicy.isNewRecord()) {
			return false;
		}

		for (int i = 0; i < aFinStepPolicy.getStepNo(); i++) {
			if (i > aFinStepPolicy.getStepNo()) {
				break;
			}
			int instCount = 0;
			for (int iFsd = idxStart; iFsd < schedules.size(); iFsd++) {
				FinanceScheduleDetail fsd = schedules.get(iFsd);
				String specifier = fsd.getSpecifier();
				if (fsd.isRepayOnSchDate() && fsd.isFrqDate()) {
					instCount = instCount + 1;
				} else if (iFsd != 0 && PennantConstants.STEP_SPECIFIER_GRACE.equals(aFinStepPolicy.getStepSpecifier())
						&& !(FinanceConstants.FLAG_BPI.equals(fsd.getBpiOrHoliday()))
						&& (CalculationConstants.SCH_SPECIFIER_GRACE.equals(specifier)
								|| CalculationConstants.SCH_SPECIFIER_GRACE_END.equals(specifier))
						&& !fsd.isDisbOnSchDate() && fsd.isFrqDate()) {
					instCount = instCount + 1;
				}

				if (aFinStepPolicy.getInstallments() == instCount) {
					stepEndDate = fsd.getSchDate();
					/* aFinStepPolicy.setValidAmountForRestructuring(fsd.getRepayAmount()); */
					idxStart = iFsd + 1;
					break;
				}
			}
		}

		if (stepEndDate.compareTo(this.restructureDateIn.getValue()) < 0) {
			return true;
		}
		return false;
	}

	private Date validateRestructureDate(FinScheduleData finScheduleData) {
		Date allowedRestrcutureDate = null;
		if (finScheduleData.getFinanceScheduleDetails() != null) {
			List<FinanceScheduleDetail> financeScheduleDetails = finScheduleData.getFinanceScheduleDetails();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				FinanceScheduleDetail prvSchd = null;
				if (i != 0) {
					prvSchd = financeScheduleDetails.get(i - 1);
				}

				if (prvSchd != null && prvSchd.getSchDate().compareTo(appDate) < 0
						&& curSchd.getSchDate().compareTo(appDate) >= 0) {
					allowedRestrcutureDate = prvSchd.getSchDate();
					continue;
				}
			}
		}
		return allowedRestrcutureDate;
	}

	public void onFulfill$baseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.baseRate.getBaseComp());
		Clients.clearWrongValue(this.baseRate.getSpecialComp());
		this.baseRate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;

		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = baseRate.getBaseObject();
			if (dataObject instanceof String) {
				this.baseRate.setBaseValue(dataObject.toString());
				this.baseRate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.baseRate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.baseRate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.baseRate.getSpecialValue(),
							this.baseRate.getMarginValue() == null ? BigDecimal.ZERO : this.baseRate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.baseRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.baseRate.setBaseValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = baseRate.getSpecialObject();
			if (dataObject instanceof String) {
				this.baseRate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.baseRate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.baseRate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.baseRate.getSpecialValue(),
							this.baseRate.getMarginValue() == null ? BigDecimal.ZERO : this.baseRate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.baseRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.baseRate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}

	private void setEffectiveRate() {
		if (StringUtils.isBlank(this.baseRate.getBaseValue())) {
			this.baseRate.setEffRateText(PennantApplicationUtil.formatRate(
					(this.baseRate.getMarginValue() == null ? BigDecimal.ZERO : this.baseRate.getMarginValue())
							.doubleValue(),
					2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.baseRate.getBaseValue(),
				getFinScheduleData().getFinanceMain().getFinCcy(), this.baseRate.getSpecialValue(),
				this.baseRate.getMarginValue() == null ? BigDecimal.ZERO : this.baseRate.getMarginValue(),
				getFinScheduleData().getFinanceMain().getRpyMinRate(),
				getFinScheduleData().getFinanceMain().getRpyMaxRate());
		if (rateDetail.getErrorDetails() == null) {
			this.baseRate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.baseRate.setSpecialValue("");
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setRestructureService(RestructureService restructureService) {
		this.restructureService = restructureService;
	}

	public RestructureType getRestructureType() {
		return restructureType;
	}

	public void setRestructureType(RestructureType restructureType) {
		this.restructureType = restructureType;
	}

	public RestructureDetail getRstDetail() {
		return rstDetail;
	}

	public void setRstDetail(RestructureDetail rstDetail) {
		this.rstDetail = rstDetail;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public List<FinanceStepPolicyDetail> getFinStepPolicyList() {
		return finStepPolicyList;
	}

	public void setFinStepPolicyList(List<FinanceStepPolicyDetail> finStepPolicyList) {
		this.finStepPolicyList = finStepPolicyList;
	}

	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return stepDetailDialogCtrl;
	}

	@Autowired
	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}

}
