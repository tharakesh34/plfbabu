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
 * * FileName : ChangeFrequencyDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
 * Modified Date : 05-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.FrequencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.ChangeFrequencyService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/ChangeFrequencyDialog.zul file.
 */
public class ChangeFrequencyDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(ChangeFrequencyDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChangeFrequencyDialog;

	protected FrequencyBox repayFrq;
	protected Combobox cbFrqFromDate;
	protected Datebox grcPeriodEndDate;
	protected Datebox nextGrcRepayDate;
	protected Datebox nextRepayDate;
	protected Checkbox pftIntact;
	protected Combobox recalType;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	protected Row row_GrcPeriodEndDate;
	protected Row row_grcNextRepayDate;

	private FinScheduleData finScheduleData;
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;

	private transient ChangeFrequencyService changeFrequencyService;
	private boolean appDateValidationReq = false;
	private List<ValueLabel> recalTypes = new ArrayList<>();

	/**
	 * default constructor.<br>
	 */
	public ChangeFrequencyDialogCtrl() {
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
	public void onCreate$window_ChangeFrequencyDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangeFrequencyDialog);

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

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ChangeFrequencyDialog.onClose();
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
			this.window_ChangeFrequencyDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		this.grcPeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.repayFrq.setMandatoryStyle(true);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		this.recalTypes = PennantStaticListUtil.getRecalTypes();
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeFrq(Event event) {
		doSave();
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
	 * The Click event is raised when the Close event is occurred. <br>
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
	public void doSave() {
		doWriteComponentsToBean();
		this.window_ChangeFrequencyDialog.onClose();
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		FinanceMain fm = aFinSchData.getFinanceMain();

		this.repayFrq.setDisableFrqCode(true);
		this.repayFrq.setDisableFrqMonth(true);
		this.repayFrq.setDisableFrqDay(false);
		this.repayFrq.setAlwFrqDays(aFinSchData.getFinanceType().getFrequencyDays());
		this.repayFrq.setValue(fm.getRepayFrq());

		if (aFinSchData.getFinanceType().isFinPftUnChanged()) {
			this.pftIntact.setChecked(true);
			this.pftIntact.setDisabled(true);
		} else {
			this.pftIntact.setDisabled(false);
			this.pftIntact.setChecked(false);
		}

		fillSchFromDates(aFinSchData.getFinanceScheduleDetails());

		doSetRecalType(this.repayFrq.getValue(), this.cbFrqFromDate.getSelectedItem().getValue());

		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug(Literal.ENTERING);

		this.cbFrqFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		this.cbFrqFromDate.appendChild(comboitem);
		this.cbFrqFromDate.setSelectedItem(comboitem);

		if (financeScheduleDetails == null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		// Date grcEndDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
		Date curBussDate = SysParamUtil.getAppDate();
		FinanceScheduleDetail prvSchd = null;
		boolean isPrvShcdAdded = false;
		for (int i = 0; i < financeScheduleDetails.size(); i++) {

			FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
			if (i == 0) {
				prvSchd = curSchd;
			}

			// Not Allowing Grace Period Dates
			/*
			 * if(curSchd.getSchDate().compareTo(grcEndDate) <= 0){ if(curSchd.getSchDate().compareTo(grcEndDate) == 0){
			 * prvSchd = curSchd; } continue; }
			 */

			// Not allow Before Current Business Date
			if (appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
				prvSchd = curSchd;
				continue;
			}

			// Change Frequency is not allowed for the schedule which has the presentment
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
			// Not Review Date
			if (!curSchd.isRepayOnSchDate() && !getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()
					&& !curSchd.isPftOnSchDate()) {
				prvSchd = curSchd;
				continue;
			}

			// Only allowed if payment amount is greater than Zero
			if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0
					&& StringUtils.isEmpty(curSchd.getBpiOrHoliday())) {
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

			if (i == financeScheduleDetails.size() - 1) {
				continue;
			}

			if (prvSchd != null && !isPrvShcdAdded) {
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(prvSchd.getSchDate()) + " " + prvSchd.getSpecifier());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date fromDate = null;
		String frq = "";

		FinanceMain fm = getFinScheduleData().getFinanceMain();
		FinServiceInstruction fsi = new FinServiceInstruction();

		try {
			if (isValidComboValue(this.repayFrq.getFrqDayCombobox(), Labels.getLabel("label_FrqDay.value"))) {
				frq = this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue();
			}
			fsi.setRepayFrq(this.repayFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbFrqFromDate, Labels.getLabel("label_ChangeFrequencyDialog_FromDate.value"))) {
				fromDate = (Date) this.cbFrqFromDate.getSelectedItem().getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.row_GrcPeriodEndDate.isVisible()) {

			try {
				if (fromDate != null && fromDate.compareTo(fm.getGrcPeriodEndDate()) <= 0) {
					if (this.grcPeriodEndDate.getValue() == null) {
						throw new WrongValueException(this.grcPeriodEndDate,
								Labels.getLabel("FIELD_IS_MAND", new String[] {
										Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value") }));
					} else {
						if (this.grcPeriodEndDate.getValue().compareTo(fm.getFinStartDate()) < 0) {
							throw new WrongValueException(this.grcPeriodEndDate,
									Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL", new String[] {
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"),
											Labels.getLabel("label_ChangeFrequencyDialog_FinStartDate.value") }));
						} else if (this.grcPeriodEndDate.getValue().compareTo(fromDate) < 0) {
							throw new WrongValueException(this.grcPeriodEndDate, Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] {
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value"),
											DateUtil.formatToLongDate(fromDate) }));
						}
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			fsi.setNextGrcRepayDate(this.nextGrcRepayDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.nextGrcRepayDate.getValue() != null && this.grcPeriodEndDate.getValue() != null) {
				if (this.nextGrcRepayDate.getValue().compareTo(this.grcPeriodEndDate.getValue()) > 0) {
					throw new WrongValueException(this.nextGrcRepayDate,
							Labels.getLabel("DATE_ALLOWED_MAXDATE",
									new String[] {
											Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value"),
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value") }));
				} else if (this.nextGrcRepayDate.getValue().compareTo(fm.getFinStartDate()) <= 0) {
					throw new WrongValueException(this.nextGrcRepayDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] {
											Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value"),
											Labels.getLabel("label_ChangeFrequencyDialog_FinStartDate.value") }));
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
									new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),
											Labels.getLabel("label_ChangeFrequencyDialog_GrcPeriodEndDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.nextRepayDate.getValue() != null && fromDate != null) {
				if (this.nextRepayDate.getValue().compareTo(fromDate) <= 0) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE",
									new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),
											DateUtil.formatToShortDate(fromDate) }));
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
									new String[] { Labels.getLabel("label_ChangeFrequencyDialog_NextRepayDate.value"),
											Labels.getLabel("label_ChangeFrequencyDialog_NextGrcRepayDate.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fsi.setNextRepayDate(this.nextRepayDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.recalType,
					Labels.getLabel("label_ChangeFrequencyDialog_RecalculationType.value"))) {
				fsi.setRecalType(this.recalType.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fsi.setServiceReqNo(this.serviceReqNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			fsi.setRemarks(this.remarks.getValue());
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

		fsi.setPftIntact(this.pftIntact.isChecked());
		fsi.setFinID(fm.getFinID());
		fsi.setFinReference(fm.getFinReference());
		fsi.setFinEvent(FinServiceEvent.CHGFRQ);
		fsi.setFromDate(fromDate);
		fsi.setRepayFrq(frq);
		fsi.setGrcPeriodEndDate(this.grcPeriodEndDate.getValue());
		fsi.setNextGrcRepayDate(this.nextGrcRepayDate.getValue());
		fsi.setNextRepayDate(this.nextRepayDate.getValue());

		// call change frequency method to calculate new schedules
		setFinScheduleData(changeFrequencyService.doChangeFrequency(getFinScheduleData(), fsi));
		fsi.setPftChg(getFinScheduleData().getPftChg());
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(fsi);

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

		logger.debug("Leaving");
	}

	public void onSelectDay$repayFrq(Event event) {
		this.nextGrcRepayDate.setText("");
		this.nextRepayDate.setText("");
		this.grcPeriodEndDate.setText("");

		doSetRecalType(this.repayFrq.getValue(), this.cbFrqFromDate.getSelectedItem().getValue());
	}

	public void onSelect$cbFrqFromDate(Event event) {
		doSetRecalType(this.repayFrq.getValue(), this.cbFrqFromDate.getSelectedItem().getValue());
	}

	private void doSetRecalType(String frequency, Object frqFromDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = getFinScheduleData().getFinanceMain();
		String repayFrq = fm.getRepayFrq();

		String dftRecallType = CalculationConstants.RPYCHG_ADJMDT;

		if (fm.isStepFinance()) {
			doShowRecalType(dftRecallType, false);
			logger.debug(Literal.LEAVING);
			return;
		}

		if (!FrequencyCodeTypes.FRQ_MONTHLY.equals(FrequencyUtil.getFrequencyCode(repayFrq))) {
			doShowRecalType(dftRecallType, false);
			logger.debug(Literal.LEAVING);
			return;
		}

		if (frequency == null) {
			doShowRecalType(dftRecallType, false);
			logger.debug(Literal.LEAVING);
			return;
		}

		if (frqFromDate == null || PennantConstants.List_Select.equals(frqFromDate)) {
			doShowRecalType(dftRecallType, false);
			logger.debug(Literal.LEAVING);
			return;
		}

		Date frqChangeDate = (Date) frqFromDate;

		if (fm.isAllowGrcPeriod() && DateUtil.compare(frqChangeDate, fm.getGrcPeriodEndDate()) <= 0) {
			repayFrq = fm.getGrcPftFrq();
		}

		int repayFrqDay = FrequencyUtil.getIntFrequencyDay(repayFrq);
		int changeFrqDay = FrequencyUtil.getIntFrequencyDay(frequency);

		if (changeFrqDay > repayFrqDay) {
			doShowRecalType("", true);
		} else {
			doShowRecalType(dftRecallType, false);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowRecalType(String recalType, boolean allowChange) {
		fillComboBox(this.recalType, recalType, this.recalTypes);
		this.recalType.setDisabled(!allowChange);
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

	public void setChangeFrequencyService(ChangeFrequencyService changeFrequencyService) {
		this.changeFrequencyService = changeFrequencyService;
	}

}