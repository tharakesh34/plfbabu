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
 * * FileName : ReScheduleDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * * Modified
 * Date : 05-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.ConvFinanceMainDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/ReScheduleDialog.zul file.
 */
public class ReScheduleDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(ReScheduleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ReScheduleDialog;
	protected FrequencyBox repayFrq;
	protected FrequencyBox grcPftFrq;
	protected FrequencyBox grcCpzFrq;
	protected FrequencyBox grcRvwFrq;
	protected FrequencyBox repayPftFrq;
	protected FrequencyBox repayRvwFrq;
	protected FrequencyBox repayCpzFrq;
	protected Combobox cbFrqFromDate;
	protected Combobox cbSchdMthd;
	protected Datebox grcPeriodEndDate;
	protected Datebox nextGrcRepayDate;
	protected Datebox nextRepayDate;
	protected Intbox numberOfTerms;
	protected Decimalbox repayPftRate;
	protected Checkbox pftIntact;
	protected RateBox rate;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	protected Row row_GrcPeriodEndDate;
	protected Row row_grcNextRepayDate;
	protected Row row_Rate;
	protected Row row_PftIntact;
	protected Row row_GrcFrq;
	protected Row row_GrcRvwFrq;

	protected Row row_GrcRate;
	protected Decimalbox grcPftRate;
	protected RateBox grcRate;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;
	private transient ReScheduleService reScheduleService;
	private boolean appDateValidationReq = false;
	private transient StepDetailDialogCtrl stepDetailDialogCtrl;
	private transient ConvFinanceMainDialogCtrl fmDialogCtrl;

	/**
	 * default constructor.<br>
	 */
	public ReScheduleDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ReScheduleDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ReScheduleDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq = (boolean) arguments.get("appDateValidationReq");
			}

			// we get the FinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIF/FinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setFinanceMainDialogCtrl(null);
			}

			if (arguments.containsKey("stepDetailDialogCtrl")) {
				setStepDetailDialogCtrl((StepDetailDialogCtrl) arguments.get("stepDetailDialogCtrl"));
			}

			if (arguments.containsKey("financeMainBaseCtrl")
					&& arguments.get("financeMainBaseCtrl") instanceof ConvFinanceMainDialogCtrl) {
				fmDialogCtrl = ((ConvFinanceMainDialogCtrl) arguments.get("financeMainBaseCtrl"));
				setStepDetailDialogCtrl(fmDialogCtrl.getStepDetailDialogCtrl());
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ReScheduleDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ReScheduleDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.repayFrq.setMandatoryStyle(true);
		this.grcPftFrq.setMandatoryStyle(true);
		this.grcCpzFrq.setMandatoryStyle(true);
		this.grcRvwFrq.setMandatoryStyle(true);
		this.repayPftFrq.setMandatoryStyle(true);
		this.repayRvwFrq.setMandatoryStyle(true);
		this.repayCpzFrq.setMandatoryStyle(true);
		this.rate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.rate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.rate.setEffectiveRateVisible(true);
		this.numberOfTerms.setMaxlength(PennantConstants.NUMBER_OF_TERMS_LENGTH);
		this.repayPftRate.setMaxlength(13);
		this.repayPftRate.setFormat(PennantConstants.rateFormate9);
		this.repayPftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayPftRate.setScale(9);
		this.grcPeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);

		this.row_GrcPeriodEndDate.setVisible(false);
		this.row_GrcFrq.setVisible(false);
		this.row_GrcRvwFrq.setVisible(false);
		this.row_GrcRate.setVisible(false);

		this.grcRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.grcRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.grcRate.setEffectiveRateVisible(true);

		this.grcPftRate.setMaxlength(13);
		this.grcPftRate.setFormat(PennantConstants.rateFormate9);
		this.grcPftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.grcPftRate.setScale(9);
		logger.debug("Leaving");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		if (!this.repayPftRate.isDisabled()) {
			this.repayPftRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value"), 9, false, false, 9999));
		}
		if (!this.rate.isMarginReadonly()) {
			this.rate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ReScheduleDialog_MarginRate.value"), 9, false, true, -9999, 9999));
		}
		if (!this.rate.getBaseComp().isReadonly()) {
			this.rate.setBaseConstraint(
					new PTStringValidator(Labels.getLabel("label_ReScheduleDialog_BaseRate.value"), null, false, true));
		}

		if (!this.grcPftRate.isDisabled()) {
			this.grcPftRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ReScheduleDialog_GrcPftRate.value"), 9, false, false, 9999));
		}
		if (!this.grcRate.isMarginReadonly()) {
			this.grcRate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_ReScheduleDialog_MarginRate.value"), 9, false, true, -9999, 9999));
		}
		if (!this.grcRate.getBaseComp().isReadonly()) {
			this.grcRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_ReScheduleDialog_GrcBaseRate.value"), null, false, true));
		}

	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReSchd(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * The Click event is raised when the Close event is occurred.
	 * 
	 * @param event
	 * 
	 */
	public void onClose(Event event) {
		doClose(false);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		doClearMessage();
		doSetValidation();
		boolean isValid = doWriteComponentsToBean();
		if (isValid) {
			this.window_ReScheduleDialog.onClose();
		}
		logger.debug("Leaving");
	}

	public void doClearMessage() {
		this.cbFrqFromDate.setConstraint("");
		this.grcPeriodEndDate.setConstraint("");
		this.nextGrcRepayDate.setConstraint("");
		this.nextRepayDate.setConstraint("");
		this.repayPftRate.setConstraint("");
		this.rate.setBaseConstraint("");
		this.rate.setSpecialConstraint("");
		this.rate.setMarginConstraint("");
		this.serviceReqNo.setConstraint("");
		this.remarks.setConstraint("");
		this.grcPftRate.setConstraint("");
		this.grcRate.setBaseConstraint("");
		this.grcRate.setSpecialConstraint("");
		this.grcRate.setMarginConstraint("");

		this.cbFrqFromDate.setErrorMessage("");
		this.grcPeriodEndDate.setErrorMessage("");
		this.nextGrcRepayDate.setErrorMessage("");
		this.nextRepayDate.setErrorMessage("");
		this.repayPftRate.setErrorMessage("");
		this.rate.setBaseErrorMessage("");
		this.rate.setSpecialErrorMessage("");
		this.rate.setMarginErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");

		this.grcPftRate.setErrorMessage("");
		this.grcRate.setBaseErrorMessage("");
		this.grcRate.setSpecialErrorMessage("");
		this.grcRate.setMarginErrorMessage("");

	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		fillComboBox(this.cbSchdMthd, aFinanceMain.getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(),
				",GRCNDPAY,PFTCAP,");
		this.cbSchdMthd.setDisabled(true);

		// Ticket No:130061-->System should allow ROI change in Add Rate change / Base rate Change module
		// only where as system is allowing change ROI in reschedulings module.
		// ROI should be kept restricted. Disable the ROI field from re-schedulement screen.
		// this.rate.setReadonly(true);
		// this.repayPftRate.setReadonly(true);

		if (ImplementationConstants.ALW_RATE_CHANGE) {
			this.row_Rate.setVisible(false);
		}

		this.repayFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		this.grcPftFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.grcPftFrq.setValue(aFinanceMain.getGrcPftFrq());
		this.grcCpzFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.grcCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
		this.grcRvwFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.grcRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
		this.repayPftFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		this.repayRvwFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
		this.repayCpzFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
		fillSchFromDates(aFinSchData.getFinanceScheduleDetails());
		if (aFinSchData.getFinanceType().isFinPftUnChanged()) {
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		} else {
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}

		// Frequencies based on Conditions
		if (!aFinanceMain.isAllowGrcPftRvw()) {
			this.grcRvwFrq.setDisabled(true);
		}
		if (!aFinanceMain.isAllowGrcCpz()) {
			this.grcCpzFrq.setDisabled(true);
		}
		if (!aFinanceMain.isAllowRepayRvw()) {
			this.repayRvwFrq.setDisabled(true);
		}
		if (!aFinanceMain.isAllowRepayCpz()) {
			this.repayCpzFrq.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbFrqFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		this.cbFrqFromDate.appendChild(comboitem);
		this.cbFrqFromDate.setSelectedItem(comboitem);

		if (financeScheduleDetails != null) {
			Date curBussDate = SysParamUtil.getAppDate();
			FinanceScheduleDetail prvSchd = null;
			boolean isPrvShcdAdded = false;
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if (i == 0) {
					prvSchd = curSchd;
				} else if (!curSchd.isFrqDate()) {
					continue;
				}

				// Not allow Before Current Business Date
				if (appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					prvSchd = curSchd;
					continue;
				}

				// Only allowed if payment amount is greater than Zero
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
					prvSchd = curSchd;
					continue;
				}

				// Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
						|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);

					prvSchd = curSchd;
					continue;
				}

				// If Presentment Exists, should not consider for recalculation
				if (curSchd.getPresentmentId() > 0) {
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);

					prvSchd = curSchd;
					isPrvShcdAdded = false;
					continue;
				}

				if (i == financeScheduleDetails.size() - 1) {
					continue;
				}

				if (prvSchd != null && !isPrvShcdAdded) {
					comboitem = new Comboitem();
					comboitem.setLabel(
							DateUtil.formatToLongDate(prvSchd.getSchDate()) + " " + prvSchd.getSpecifier());
					comboitem.setValue(prvSchd.getSchDate());
					comboitem.setAttribute("fromSpecifier", prvSchd.getSpecifier());
					this.cbFrqFromDate.appendChild(comboitem);
					isPrvShcdAdded = true;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());
				this.cbFrqFromDate.appendChild(comboitem);

				if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0) {
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public boolean doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date fromDate = null;
		String frq = "";
		boolean isValid = true;

		FinanceMain fm = getFinScheduleData().getFinanceMain();

		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		boolean frqValid = true;

		try {
			if (this.repayFrq.isValidComboValue()) {
				frq = this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue();
			}
			finServiceInstruction.setRepayFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (this.row_GrcFrq.isVisible() && this.grcPftFrq.isValidComboValue()) {
				frq = this.grcPftFrq.getValue() == null ? "" : this.grcPftFrq.getValue();
			}
			finServiceInstruction.setGrcPftFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (this.row_GrcFrq.isVisible() && fm.isAllowGrcCpz() && this.grcCpzFrq.isValidComboValue()) {
				frq = this.grcCpzFrq.getValue() == null ? "" : this.grcCpzFrq.getValue();
			}
			finServiceInstruction.setGrcCpzFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (this.row_GrcRvwFrq.isVisible() && fm.isAllowGrcPftRvw() && this.grcRvwFrq.isValidComboValue()) {
				frq = this.grcRvwFrq.getValue() == null ? "" : this.grcRvwFrq.getValue();
			}
			finServiceInstruction.setGrcRvwFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (this.repayPftFrq.isValidComboValue()) {
				frq = this.repayPftFrq.getValue() == null ? "" : this.repayPftFrq.getValue();
			}
			finServiceInstruction.setRepayPftFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (fm.isAllowRepayRvw() && this.repayRvwFrq.isValidComboValue()) {
				frq = this.repayRvwFrq.getValue() == null ? "" : this.repayRvwFrq.getValue();
			}
			finServiceInstruction.setRepayRvwFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			frq = "";
			if (fm.isAllowRepayCpz() && this.repayCpzFrq.isValidComboValue()) {
				frq = this.repayCpzFrq.getValue() == null ? "" : this.repayCpzFrq.getValue();
			}
			finServiceInstruction.setRepayCpzFrq(frq);
		} catch (WrongValueException we) {
			wve.add(we);
			frqValid = false;
		}
		try {
			if (isValidComboValue(this.cbFrqFromDate, Labels.getLabel("label_ReScheduleDialog_FromDate.value"))) {
				fromDate = (Date) this.cbFrqFromDate.getSelectedItem().getValue();
				finServiceInstruction.setFromDate((Date) this.cbFrqFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbSchdMthd, Labels.getLabel("label_ReScheduleDialog_SchdMthd.value"))
					&& this.cbSchdMthd.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.cbSchdMthd));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.row_GrcPeriodEndDate.isVisible()) {
			try {
				if (fromDate != null && fromDate.compareTo(fm.getGrcPeriodEndDate()) <= 0) {
					if (this.grcPeriodEndDate.getValue() == null) {
						throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("FIELD_IS_MAND",
								new String[] { Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value") }));
					} else {
						if (this.grcPeriodEndDate.getValue().compareTo(fm.getFinStartDate()) < 0) {
							throw new WrongValueException(this.grcPeriodEndDate,
									Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
											new String[] {
													Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value"),
													Labels.getLabel("label_ReScheduleDialog_FinStartDate.value") }));
						} else if (this.grcPeriodEndDate.getValue().compareTo(fromDate) <= 0) {
							throw new WrongValueException(this.grcPeriodEndDate,
									Labels.getLabel("DATE_ALLOWED_MINDATE",
											new String[] {
													Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value"),
													DateUtil.formatToLongDate(fromDate) }));
						}
					}
				}
				finServiceInstruction.setGrcPeriodEndDate(this.grcPeriodEndDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			finServiceInstruction.setNextGrcRepayDate(this.nextGrcRepayDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.nextGrcRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null) {
				if (this.nextGrcRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) > 0) {
					throw new WrongValueException(this.nextGrcRepayDate,
							Labels.getLabel("DATE_ALLOWED_MAXDATE",
									new String[] { Labels.getLabel("label_ReScheduleDialog_NextGrcRepayDate.value"),
											Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value") }));
				} else if (this.nextGrcRepayDate.getValue().compareTo(fm.getFinStartDate()) <= 0) {
					throw new WrongValueException(this.nextGrcRepayDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] { Labels.getLabel("label_ReScheduleDialog_NextGrcRepayDate.value"),
											Labels.getLabel("label_ReScheduleDialog_FinStartDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.nextRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null) {
				if (this.nextRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) <= 0) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] { Labels.getLabel("label_ReScheduleDialog_NextRepayDate.value"),
											Labels.getLabel("label_ReScheduleDialog_GrcPeriodEndDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		Date curBusinessDate = SysParamUtil.getAppDate();
		try {
			if (this.nextRepayDate.getValue() != null && curBusinessDate != null) {
				if (this.nextRepayDate.getValue().compareTo(curBusinessDate) <= 0) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] { Labels.getLabel("label_ReScheduleDialog_NextRepayDate.value"),
											DateUtil.formatToShortDate(curBusinessDate) }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.nextRepayDate.getValue() != null && this.nextGrcRepayDate.getValue() != null) {
				if (this.nextRepayDate.getValue().compareTo(this.nextGrcRepayDate.getValue()) <= 0) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] { Labels.getLabel("label_ReScheduleDialog_NextRepayDate.value"),
											Labels.getLabel("label_ReScheduleDialog_NextGrcRepayDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setNextRepayDate(this.nextRepayDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.numberOfTerms.getValue() == null) {
				throw new WrongValueException(this.numberOfTerms, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_ReScheduleDialog_NumberOftTerms.value") }));
			}
			if (this.numberOfTerms.intValue() <= 0) {
				throw new WrongValueException(this.numberOfTerms, Labels.getLabel("NUMBER_MINVALUE",
						new String[] { Labels.getLabel("label_ReScheduleDialog_NumberOftTerms.value"), " 0 " }));
			}
			if (frqValid) {
				Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
				int termsAllowed = FrequencyUtil
						.getTerms(this.repayFrq.getValue(), fm.getNextRepayPftDate(), appEndDate, true, false)
						.getTerms();
				if (this.numberOfTerms.intValue() > termsAllowed) {
					throw new WrongValueException(this.numberOfTerms,
							Labels.getLabel("NUMBER_MAXVALUE",
									new String[] { Labels.getLabel("label_ReScheduleDialog_NumberOftTerms.value"),
											String.valueOf(termsAllowed) }));
				}
			}
			finServiceInstruction.setTerms(this.numberOfTerms.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setActualRate(
					ImplementationConstants.ALW_RATE_CHANGE ? fm.getRepayProfitRate() : this.repayPftRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setBaseRate(ImplementationConstants.ALW_RATE_CHANGE ? fm.getRepayBaseRate()
					: StringUtils.trimToNull(this.rate.getBaseValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setSplRate(ImplementationConstants.ALW_RATE_CHANGE ? fm.getRepaySpecialRate()
					: StringUtils.trimToNull(this.rate.getSpecialValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.trimToNull(this.rate.getBaseValue()) == null && this.rate.getMarginValue() != null
					&& this.rate.getMarginValue().compareTo(BigDecimal.ZERO) != 0) {
				throw new WrongValueException(rate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_ReScheduleDialog_MarginRate.value") }));

			}
			finServiceInstruction.setMargin(
					ImplementationConstants.ALW_RATE_CHANGE ? fm.getRepayMargin() : this.rate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Grace Rates
		try {
			finServiceInstruction.setGrcPftRate(this.grcPftRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setGraceBaseRate(StringUtils.trimToNull(this.grcRate.getBaseValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setGraceSpecialRate(StringUtils.trimToNull(this.grcRate.getSpecialValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.trimToNull(this.grcRate.getBaseValue()) == null && this.grcRate.getMarginValue() != null
					&& this.grcRate.getMarginValue().compareTo(BigDecimal.ZERO) != 0) {
				throw new WrongValueException(grcRate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_ReScheduleDialog_MarginRate.value") }));

			}
			finServiceInstruction.setGrcMargin(this.grcRate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// IF Single Rate required based on Origination Selection please comment below try-catch block
		/*
		 * try { if (this.row_Rate.isVisible() && !this.rate.isBaseReadonly() && ((this.repayPftRate.getValue() != null
		 * && this.repayPftRate.getValue().compareTo(BigDecimal.ZERO) > 0) &&
		 * (StringUtils.isNotEmpty(this.rate.getBaseValue())))) { throw new WrongValueException(this.repayPftRate,
		 * Labels.getLabel("EITHER_OR", new String[] { Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),
		 * Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value") })); } } catch (WrongValueException we) {
		 * wve.add(we); }
		 */
		try {
			if (this.row_GrcRate.isVisible() && ((this.grcPftRate.getValue() != null
					&& this.grcPftRate.getValue().compareTo(BigDecimal.ZERO) > 0)
					&& (StringUtils.isNotEmpty(this.grcRate.getBaseValue())))) {
				throw new WrongValueException(this.grcPftRate,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_ReScheduleDialog_GrcBaseRate.value"),
										Labels.getLabel("label_ReScheduleDialog_GrcPftRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_GrcRate.isVisible()
					&& (this.grcPftRate.getValue() == null && (StringUtils.isEmpty(this.grcRate.getBaseValue())))) {
				throw new WrongValueException(this.grcPftRate,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_ReScheduleDialog_GrcBaseRate.value"),
										Labels.getLabel("label_ReScheduleDialog_GrcPftRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_Rate.isVisible() && ((this.repayPftRate.getValue() != null
					&& this.repayPftRate.getValue().compareTo(BigDecimal.ZERO) > 0)
					&& (StringUtils.isNotEmpty(this.rate.getBaseValue())))) {
				throw new WrongValueException(this.repayPftRate,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),
										Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		/*
		 * try { if (this.row_Rate.isVisible() && !this.rate.isBaseReadonly() && ((this.repayPftRate.getValue() != null
		 * && this.repayPftRate.getValue().compareTo(BigDecimal.ZERO) > 0) &&
		 * (StringUtils.isNotEmpty(this.rate.getBaseValue())))) { throw new WrongValueException(this.repayPftRate,
		 * Labels.getLabel("EITHER_OR", new String[] { Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),
		 * Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value") })); } } catch (WrongValueException we) {
		 * wve.add(we); }
		 */
		try {
			if (this.row_Rate.isVisible()
					&& (this.repayPftRate.getValue() == null && (StringUtils.isEmpty(this.rate.getBaseValue())))) {
				throw new WrongValueException(this.repayPftRate,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_ReScheduleDialog_BaseRate.value"),
										Labels.getLabel("label_ReScheduleDialog_RepayPftRate.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			finServiceInstruction.setServiceReqNo(this.serviceReqNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			finServiceInstruction.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		finServiceInstruction.setPftIntact(this.pftIntact.isChecked());
		finServiceInstruction.setFinID(fm.getFinID());
		finServiceInstruction.setFinReference(fm.getFinReference());
		finServiceInstruction.setFinEvent(FinServiceEvent.RESCHD);

		if (fm.isStepFinance() && StringUtils.equals(PennantConstants.STEPPING_CALC_AMT, fm.getCalcOfSteps())) {
			isValid = validateStepDetails(finServiceInstruction);
			if (isValid && stepDetailDialogCtrl != null) {
				getFinScheduleData().setStepPolicyDetails(stepDetailDialogCtrl.getFinStepPoliciesList());
				stepDetailDialogCtrl.setDataChanged(false);
			}
		}

		if (isValid) {
			// Service details calling for Schedule calculation
			fm.setDevFinCalReq(false);
			setFinScheduleData(reScheduleService.doReSchedule(getFinScheduleData(), finServiceInstruction));
			finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());
			fm.resetRecalculationFields();
			getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

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
						logger.error("Exception: ", e);
					}
				}
			}
		}

		logger.debug("Leaving");
		return isValid;
	}

	private boolean validateStepDetails(FinServiceInstruction fsi) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (fmDialogCtrl != null && fmDialogCtrl.getStepDetailDialogCtrl() != null) {
			setStepDetailDialogCtrl(fmDialogCtrl.getStepDetailDialogCtrl());
			String stepAppliedOn = "";
			FinanceMain fm = getFinScheduleData().getFinanceMain();
			List<FinanceStepPolicyDetail> spdList = getFinScheduleData().getStepPolicyDetails();
			boolean grcStpFound = false;
			boolean rpyStpFound = false;
			Date grcStpStart = null;
			Date rpyStpStart = null;
			stepAppliedOn = fm.getStepsAppliedFor();
			List<FinanceScheduleDetail> fsdList = getFinScheduleData().getFinanceScheduleDetails();
			ArrayList<ErrorDetail> errorList = new ArrayList<ErrorDetail>(1);
			int curRpyTerms = 0;
			int grcStpTerms = 0;
			int grcTermsBeforeFrmDate = 0;
			int revisedGrcTerms = 0;
			int totRpyTerms = 0;
			int grcTerms = 0;
			stepDetailDialogCtrl.doWriteComponentsToBean(getFinScheduleData(), null, "Validate");

			if (CollectionUtils.isNotEmpty(spdList)) {
				getFinScheduleData().setStepPolicyDetails(spdList, true);
				spdList = getFinScheduleData().getStepPolicyDetails();
				for (FinanceStepPolicyDetail spd : spdList) {
					if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_GRACE)
							&& !grcStpFound) {
						grcStpStart = spd.getStepStart();
						grcStpFound = true;
					} else if (StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)
							&& !rpyStpFound) {
						rpyStpStart = spd.getStepStart();
						rpyStpFound = true;
					}

					if (rpyStpFound) {
						break;
					}
				}
			}
			if (this.row_GrcPeriodEndDate.isVisible() || fsi.getFromDate().compareTo(fm.getGrcPeriodEndDate()) == 0) {
				curRpyTerms = fsi.getTerms();
				totRpyTerms = fsi.getTerms();
				if (this.row_GrcPeriodEndDate.isVisible()) {

					int prvGrcStpTerms = 0;
					for (FinanceScheduleDetail fsd : fsdList) {

						if (fsd.isFrqDate() && DateUtil.compare(fsd.getSchDate(), fm.getGrcPeriodEndDate()) <= 0) {
							grcTerms = grcTerms + 1;
						}

						if (fsd.getSchDate().compareTo(fsi.getFromDate()) <= 0 && fsd.isFrqDate()
								&& !fsd.isDisbOnSchDate()) {
							grcTermsBeforeFrmDate = grcTermsBeforeFrmDate + 1;
						}

						if (grcStpStart != null) {
							if (fsd.getSchDate().compareTo(grcStpStart) >= 0
									&& fsd.getSchDate().compareTo(fsi.getFromDate()) <= 0
									&& !(StringUtils.equals(fsd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI))
									&& (StringUtils.equals(fsd.getSpecifier(), CalculationConstants.SCH_SPECIFIER_GRACE)
											|| StringUtils.equals(fsd.getSpecifier(),
													CalculationConstants.SCH_SPECIFIER_GRACE_END))
									&& !fsd.isDisbOnSchDate() && fsd.isFrqDate()) {
								prvGrcStpTerms = prvGrcStpTerms + 1;
							}
						}
					}

					int newGrcTerms = 0;
					Date nextGrcRpyDate = null;
					nextGrcRpyDate = fsi.getNextGrcRepayDate();
					if (nextGrcRpyDate != null) {
						if (FrequencyUtil.isFrqDate(fsi.getGrcPftFrq(), fsi.getGrcPeriodEndDate())) {
							newGrcTerms = FrequencyUtil.getTerms(fsi.getGrcPftFrq(), nextGrcRpyDate,
									fsi.getGrcPeriodEndDate(), true, false).getTerms();
						} else {
							newGrcTerms = FrequencyUtil
									.getTerms(fsi.getGrcPftFrq(), nextGrcRpyDate, fsi.getGrcPeriodEndDate(), true, true)
									.getTerms();
						}
					} else {
						String frequency = fsi.getRepayFrq();
						String frqCode = frequency.substring(0, 1);
						boolean isMonthlyFrq = StringUtils.equals(frqCode, FrequencyCodeTypes.FRQ_MONTHLY) ? true
								: false;
						// In ReScheduleService EventFromDate calculating based n Repay frequency.
						Date eventFromDate = FrequencyUtil
								.getNextDate(fsi.getRepayFrq(), 1, fsi.getFromDate(), "A", false, isMonthlyFrq ? 30 : 0)
								.getNextFrequencyDate();
						if (FrequencyUtil.isFrqDate(fsi.getGrcPftFrq(), fsi.getGrcPeriodEndDate())) {
							newGrcTerms = FrequencyUtil
									.getTerms(fsi.getRepayFrq(), eventFromDate, fsi.getGrcPeriodEndDate(), true, false)
									.getTerms();
						} else {
							newGrcTerms = FrequencyUtil
									.getTerms(fsi.getRepayFrq(), eventFromDate, fsi.getGrcPeriodEndDate(), true, true)
									.getTerms();
						}
					}

					grcStpTerms = prvGrcStpTerms + newGrcTerms;
					revisedGrcTerms = grcTermsBeforeFrmDate + newGrcTerms;
					if (grcTerms > revisedGrcTerms) {
						errorList.add(new ErrorDetail("STP010", PennantConstants.KEY_SEPERATOR,
								new String[] { "grace", "grace" }));
						int redGrcTerms = grcTerms - revisedGrcTerms;
						int elgRpyTerms = fm.getNumberOfTerms() + redGrcTerms;
						if (totRpyTerms > elgRpyTerms) {
							errorList.add(new ErrorDetail("STP011", PennantConstants.KEY_SEPERATOR,
									new String[] { String.valueOf(totRpyTerms), String.valueOf(redGrcTerms) }));
						}
					}

				}
			} else {
				curRpyTerms = fsi.getTerms();
				int prvRpyStpTerms = 0;
				int prvRpyTerms = 0;
				for (FinanceScheduleDetail fsd : fsdList) {

					if (fsd.getSchDate().compareTo(fsi.getFromDate()) <= 0 && fsd.isRepayOnSchDate() && fsd.isFrqDate()
							&& (StringUtils.equals(fsd.getSpecifier(), CalculationConstants.SCH_SPECIFIER_REPAY))) {
						prvRpyTerms = prvRpyTerms + 1;
					}

					if (rpyStpStart != null) {
						if (fsd.getSchDate().compareTo(rpyStpStart) >= 0
								&& fsd.getSchDate().compareTo(fsi.getFromDate()) <= 0 && fsd.isRepayOnSchDate()
								&& fsd.isFrqDate()
								&& (StringUtils.equals(fsd.getSpecifier(), CalculationConstants.SCH_SPECIFIER_REPAY))) {
							prvRpyStpTerms = prvRpyStpTerms + 1;
						}
					}
				}
				curRpyTerms = curRpyTerms + prvRpyStpTerms;
				totRpyTerms = prvRpyTerms + curRpyTerms;
			}
			if (PennantConstants.STEPPING_APPLIED_EMI.equals(stepAppliedOn)
					|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepAppliedOn)) {
				errorList.addAll(stepDetailDialogCtrl.doValidateStepDetails(fm, curRpyTerms,
						PennantConstants.STEP_SPECIFIER_REG_EMI, stepAppliedOn));
			}

			if (this.row_GrcPeriodEndDate.isVisible() && (PennantConstants.STEPPING_APPLIED_GRC.equals(stepAppliedOn)
					|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepAppliedOn))) {
				errorList.addAll(stepDetailDialogCtrl.doValidateStepDetails(fm, grcStpTerms,
						PennantConstants.STEP_SPECIFIER_GRACE, stepAppliedOn));
			}

			if (fm.getNumberOfTerms() > totRpyTerms) {
				errorList.add(new ErrorDetail("STP010", PennantConstants.KEY_SEPERATOR,
						new String[] { "normal EMI", "normal EMI" }));
			}

			int retValue = PennantConstants.porcessOVERIDE;
			AuditHeader auditHeader = getAuditHeader(getFinScheduleData().getFinanceMain(), "");
			// Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
			auditHeader = ErrorControl.showErrorDetails(window, auditHeader);

			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				logger.debug(Literal.LEAVING);
				return true;
			} else if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
				logger.debug(Literal.LEAVING);
				return true;
			}
			setOverideMap(auditHeader.getOverideMap());
			logger.debug(Literal.LEAVING);
			return false;
		}

		return true;
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceMain financeMain, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, financeMain.getBefImage(), financeMain);
		return new AuditHeader(financeMain.getFinReference(), null, null, null, auditDetail,
				financeMain.getUserDetails(), getOverideMap());
	}

	public void onFulfill$rate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.rate.getBaseComp());
		Clients.clearWrongValue(this.rate.getSpecialComp());
		this.rate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = rate.getBaseObject();
			if (dataObject instanceof String) {
				this.rate.setBaseValue(dataObject.toString());
				this.rate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.rate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.rate.getSpecialValue(),
							this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.rate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.rate.setBaseValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = rate.getSpecialObject();
			if (dataObject instanceof String) {
				this.rate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.rate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.rate.getSpecialValue(),
							this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.rate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.rate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}

	private void setEffectiveRate() throws InterruptedException {
		if (StringUtils.isBlank(this.rate.getBaseValue())) {
			this.rate.setEffRateText(PennantApplicationUtil.formatRate(
					(this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue()).doubleValue(),
					2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.rate.getBaseValue(),
				getFinScheduleData().getFinanceMain().getFinCcy(), this.rate.getSpecialValue(),
				this.rate.getMarginValue() == null ? BigDecimal.ZERO : this.rate.getMarginValue(),
				getFinScheduleData().getFinanceMain().getRpyMinRate(),
				getFinScheduleData().getFinanceMain().getRpyMaxRate());
		if (rateDetail.getErrorDetails() == null) {
			this.rate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.rate.setSpecialValue("");
		}
	}

	public void onFulfill$grcRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.grcRate.getBaseComp());
		Clients.clearWrongValue(this.grcRate.getSpecialComp());
		this.rate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = grcRate.getBaseObject();
			if (dataObject instanceof String) {
				this.grcRate.setBaseValue(dataObject.toString());
				this.grcRate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.grcRate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.grcRate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.grcRate.getSpecialValue(),
							this.grcRate.getMarginValue() == null ? BigDecimal.ZERO : this.grcRate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.grcRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.grcRate.setBaseValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = grcRate.getSpecialObject();
			if (dataObject instanceof String) {
				this.grcRate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.grcRate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.grcRate.getBaseValue(),
							getFinScheduleData().getFinanceMain().getFinCcy(), this.grcRate.getSpecialValue(),
							this.grcRate.getMarginValue() == null ? BigDecimal.ZERO : this.grcRate.getMarginValue(),
							getFinScheduleData().getFinanceMain().getRpyMinRate(),
							getFinScheduleData().getFinanceMain().getRpyMaxRate());
					if (rateDetail.getErrorDetails() == null) {
						this.grcRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.grcRate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setGraceEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}

	private void setGraceEffectiveRate() throws InterruptedException {
		if (StringUtils.isBlank(this.grcRate.getBaseValue())) {
			this.grcRate.setEffRateText(PennantApplicationUtil.formatRate(
					(this.grcRate.getMarginValue() == null ? BigDecimal.ZERO : this.grcRate.getMarginValue())
							.doubleValue(),
					2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.grcRate.getBaseValue(),
				getFinScheduleData().getFinanceMain().getFinCcy(), this.grcRate.getSpecialValue(),
				this.grcRate.getMarginValue() == null ? BigDecimal.ZERO : this.grcRate.getMarginValue(),
				getFinScheduleData().getFinanceMain().getRpyMinRate(),
				getFinScheduleData().getFinanceMain().getRpyMaxRate());
		if (rateDetail.getErrorDetails() == null) {
			this.grcRate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.grcRate.setSpecialValue("");
		}
	}

	public void onChange$cbFrqFromDate(Event event) {
		logger.debug("Entering" + event.toString());

		this.row_GrcPeriodEndDate.setVisible(false);
		this.row_GrcFrq.setVisible(false);
		this.row_GrcRvwFrq.setVisible(false);
		this.row_grcNextRepayDate.setVisible(true);
		this.row_GrcRate.setVisible(false);

		this.cbSchdMthd.setDisabled(true);
		fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(),
				PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");

		if (this.cbFrqFromDate.getSelectedIndex() != 0) {
			Date fromDate = (Date) this.cbFrqFromDate.getSelectedItem().getValue();

			List<FinanceScheduleDetail> financeScheduleDetails = getFinScheduleData().getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {

					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

					if (curSchd.isRepayOnSchDate() || curSchd.isRvwOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)
							|| fromDate.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
						if (fromDate.compareTo(curSchd.getSchDate()) == 0) {
							if (fromDate.compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) < 0) {
								this.row_GrcPeriodEndDate.setVisible(true);
								this.row_GrcFrq.setVisible(true);
								this.row_GrcRvwFrq.setVisible(true);
								this.row_GrcRate.setVisible(true);
								fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
										PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
							} else {
								fillComboBox(this.cbSchdMthd, getFinScheduleData().getFinanceMain().getScheduleMethod(),
										PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");
							}
							break;
						}
					}
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectMonth$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		logger.debug("Leaving" + event.toString());
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

	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}

}
