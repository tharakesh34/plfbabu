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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.RestructureService;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.RestructureType;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RepayConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

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

	protected Listbox listBoxCharges;
	protected Listheader listheader_RestructureCharge_TdsAmount;
	protected Button btnRestructure;

	private LovFieldDetail lovFieldDetail = PennantAppUtil.getDefaultRestructure("RSTRS");

	private FinScheduleData finScheduleData;
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;
	private transient RestructureService restructureService;
	private RestructureType restructureType = new RestructureType();
	Date appDate = SysParamUtil.getAppDate();
	private Date fullyPaidDate = null;

	private RestructureDetail rstDetail;
	private boolean enquiry = false;

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

	public void onCreate$window_RestructureDialog(Event event) throws Exception {
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
	 * @throws Exception
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
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
		this.actRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.actRate.setScale(9);
		this.baseRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.baseRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.baseRate.setEffectiveRateVisible(true);
		this.grcMaxAmount.setDisabled(true);
		this.grcMaxAmount.setProperties(false, PennantConstants.defaultCCYDecPos);
		this.recalculationType.setDisabled(true);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);

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

		fillComboBox(this.restructuringType, "", PennantAppUtil.getRestructureType(), "");

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
			try {
				setEffectiveRate();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}

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
				List<RestructureCharge> chargeList = restructureService.getRestructureChargeList(aFinSchData, appDate);
				doFillCharges(chargeList);
				rstDetail.setChargeList(chargeList);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doRenderData(FinScheduleData scheduleData) {
		logger.debug(Literal.ENTERING);

		rstDetail = scheduleData.getRestructureDetail();
		if (rstDetail == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		fillComboBox(this.restructuringType, rstDetail.getRestructureType(), PennantAppUtil.getRestructureType(), "");
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
					comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate()));
					comboitem.setValue(curSchd.getSchDate());
					dateCombobox.appendChild(comboitem);

					if (restructureDate != null && DateUtility.compare(restructureDate, curSchd.getSchDate()) == 0) {
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
		if (maxValueDate != null && DateUtility.compare(maxValueDate, fullyPaidDate) > 0) {
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
				if (StringUtils.equals(charge.getAlocType(), RepayConstants.ALLOCATION_MANADV)
						|| StringUtils.equals(charge.getAlocType(), RepayConstants.ALLOCATION_BOUNCE)
						|| StringUtils.equals(charge.getAlocType(), RepayConstants.ALLOCATION_ODC)
						|| StringUtils.equals(charge.getAlocType(), RepayConstants.ALLOCATION_LPFT)) {
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
	 * @throws Exception
	 */
	public void onClick_Capitalized(ForwardEvent event) throws Exception {
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
											DateUtility.formatToShortDate(fullyPaidDate),
											DateUtility.formatToShortDate(appDate) }));
				}

				Date allowedRestrcutureDate = validateRestructureDate(getFinScheduleData());
				if (DateUtil.compare(this.restructureDateIn.getValue(), allowedRestrcutureDate) < 0) {
					throw new WrongValueException(this.restructureDateIn,
							Labels.getLabel("DATE_ALLOWED_RANGE_EQUAL",
									new String[] { Labels.getLabel("label_RestructureDialog_RestructureDate.value"),
											DateUtility.formatToShortDate(allowedRestrcutureDate),
											DateUtility.formatToShortDate(appDate) }));

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

		// Service details calling for Schedule calculation
		getFinScheduleData().setRestructureDetail(rstDetail);
		getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
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

	private void setEffectiveRate() throws InterruptedException {
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

}
