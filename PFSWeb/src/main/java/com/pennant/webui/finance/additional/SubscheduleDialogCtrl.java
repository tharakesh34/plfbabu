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
 * * FileName : SubScheduleDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.FrequencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SubscheduleDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -4863495235148249386L;
	private static final Logger logger = LogManager.getLogger(SubscheduleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SubScheduleDialog;
	protected Datebox firstDate;
	protected FrequencyBox termFrq;
	protected Intbox numOfTerms;
	protected Row firstDateRow;
	protected Row numOfTermsRow;
	protected Row frqRow;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public SubscheduleDialogCtrl() {
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
	public void onCreate$window_SubScheduleDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SubScheduleDialog);

		if (arguments.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
			setFinScheduleData(this.finScheduleData);
		} else {
			setFinScheduleData(null);
		}

		if (arguments.containsKey("financeScheduleDetail")) {
			this.setFinanceScheduleDetail((FinanceScheduleDetail) arguments.get("financeScheduleDetail"));
			setFinanceScheduleDetail(this.financeScheduleDetail);
		} else {
			setFinanceScheduleDetail(null);
		}

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (arguments.containsKey("financeMainDialogCtrl")) {
			setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
		} else {
			setScheduleDetailDialogCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_SubScheduleDialog.doModal();
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

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.numOfTerms.setMaxlength(3);
		this.firstDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.termFrq.setMandatoryStyle(true);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		this.termFrq.setValue("");
		logger.debug("Entering");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.numOfTerms.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Date newSchdDate = DateUtil
					.getDate(DateUtil.format(this.firstDate.getValue(), PennantConstants.dateFormat));
			if (newSchdDate.compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) <= 0) {
				throw new WrongValueException(this.firstDate, Labels.getLabel("DATE_ALLOWED_AFTER", new String[] {
						Labels.getLabel("label_SubScheduleDialog_firstDate.value"),
						DateUtil.formatToShortDate(getFinScheduleData().getFinanceMain().getMaturityDate()) }));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.termFrq.isValidComboValue()) {
				this.termFrq.getValue();
			}
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
		// Schedule Calculation Process
		setFinScheduleData(ScheduleCalculator.addSubSchedule(getFinScheduleData(), this.numOfTerms.getValue(),
				this.firstDate.getValue(), this.termFrq.getValue()));
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		// Show Error Details in Schedule Maintainance
		if (getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		} else {
			getFinScheduleData().setSchduleGenerated(true);
			if (getScheduleDetailDialogCtrl() != null) {
				getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.numOfTerms.isVisible()) {
			this.numOfTerms.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_SubScheduleDialog_Terms.value"), true, false));
		}
		this.firstDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_SubScheduleDialog_firstDate.value") }));
		logger.debug("Leaving");
	}

	/**
	 * when the "AddSubSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddSubSchedule(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (getFinanceScheduleDetail() != null) {
			if (isDataChanged()) {
				doSave();
			} else {
				MessageUtil.showError("No Data has been changed.");
			}
		} else {
			doSave();
		}
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

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.numOfTerms.clearErrorMessage();
		this.termFrq.setErrorMessage("");
		this.firstDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		doSetValidation();
		doWriteComponentsToBean();
		this.window_SubScheduleDialog.onClose();
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				if ((curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
						&& !curSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_MATURITY)) {
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
					comboitem.setValue(curSchd.getSchDate());
					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null
							&& curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}

			}
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

}
