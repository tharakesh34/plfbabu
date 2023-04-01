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
 * * FileName : AddDatedScheduleDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.financeservice.AddDatedScheduleService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class AddDatedScheduleDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 4583907397986780542L;
	private final static Logger logger = LogManager.getLogger(AddDatedScheduleDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AddDatedScheduleDialog; // autowired
	protected CurrencyBox repayAmount; // autowired
	protected Datebox fromDate; // autowired
	protected Combobox cbTillDate; // autowired
	protected Combobox cbAddTermAfter; // autowired
	protected Combobox cbReCalType; // autowired
	protected Row tillDateRow; // autowired
	protected Row addTermRow; // autowired

	private Date validateFromDate = null;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private AddDatedScheduleService addDatedScheduleService;

	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public AddDatedScheduleDialogCtrl() {
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
	public void onCreate$window_AddDatedScheduleDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AddDatedScheduleDialog);

		try {

			// READ OVERHANDED params !
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
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AddDatedScheduleDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");
		if (aFinScheduleData == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinScheduleData = new FinScheduleData();

			setFinScheduleData(aFinScheduleData);
		} else {
			setFinScheduleData(aFinScheduleData);
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(DialogType.MODAL);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AddDatedScheduleDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		fillSchDates(this.cbTillDate, aFinSchData);
		int formatter = CurrencyUtil.getFormat(aFinSchData.getFinanceMain().getFinCcy());
		if (getFinanceScheduleDetail() != null) {
			this.repayAmount.setValue(CurrencyUtil.parse(getFinanceScheduleDetail().getDisbAmount(), formatter));
			this.repayAmount.setScale(formatter);
			this.fromDate.setValue(getFinanceScheduleDetail().getSchDate());
		}
		if (aFinSchData.getFinanceMain() != null) {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
					PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDTERM,ADDLAST,");
		} else {
			fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), "");
		}
		if (StringUtils.equals(getFinScheduleData().getFinanceMain().getRecalType(),
				CalculationConstants.RPYCHG_TILLDATE)) {
			this.tillDateRow.setVisible(true);
		}
		fillComboBox(this.cbAddTermAfter, "", new ArrayList<>(), ",MATURITY,");

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	private void doWriteComponentsToBean(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.repayAmount.getValidateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.fromDate.getValue().compareTo(validateFromDate) <= 0 || this.fromDate.getValue()
					.compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) >= 0) {
				throw new WrongValueException(this.fromDate, Labels.getLabel("DATE_ALLOWED_RANGE", new String[] {
						Labels.getLabel("label_AddDatedScheduleDialog_FromDate.value"),
						DateUtil.formatToLongDate(validateFromDate),
						DateUtil.formatToLongDate(getFinScheduleData().getFinanceMain().getMaturityDate()) }));
			}
			getFinScheduleData().getFinanceMain().setEventFromDate(this.fromDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_AddDatedScheduleDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				getFinScheduleData().getFinanceMain()
						.setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.tillDateRow.isVisible()) {
			try {
				if (this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDatedScheduleDialog_TillDate.value") }));
				}
				if ((this.fromDate.getValue() != null && ((Date) this.cbTillDate.getSelectedItem().getValue())
						.compareTo(this.fromDate.getValue()) < 0)
						|| (((Date) this.cbTillDate.getSelectedItem().getValue())
								.compareTo(this.fromDate.getValue()) == 0)) {
					throw new WrongValueException(this.cbTillDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_AddDatedScheduleDialog_TillDate.value"),
											DateUtil.formatToLongDate((Date) this.fromDate.getValue()) }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			if (this.addTermRow.isVisible()) {
				if (isValidComboValue(this.cbAddTermAfter,
						Labels.getLabel("label_AddDatedScheduleDialog_AddTermAfter.value"))
						&& this.cbAddTermAfter.getSelectedIndex() != 0) {
					this.cbAddTermAfter.getSelectedItem().getValue().toString();
				}
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

		getFinScheduleData().getFinanceMain().setEventToDate(getFinScheduleData().getFinanceMain().getMaturityDate());

		// Schedule Recalculation With New Dated Schedule term
		getFinScheduleData().getFinanceMain().setRecalToDate(null);
		setFinScheduleData(addDatedScheduleService.getAddDatedSchedule(getFinScheduleData()));

		// Show Error Details in Schedule Maintenance
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
		if (this.repayAmount.isVisible()) {
			/*
			 * this.repayAmount.setConstraint(new AmountValidator(18,0,
			 * Labels.getLabel("label_AddDatedScheduleDialog_Amount.value"),false));
			 */
		}
		if (this.fromDate.isVisible()) {
			this.fromDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_AddDatedScheduleDialog_FromDate.value") }));
		}
		logger.debug("Leaving");
	}

	public void onClick$btnAddDatedSchedule(Event event) {
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
	 * The Click event is raised when the Close event is occurred. <br>
	 * 
	 * @param event
	 * 
	 */
	public void onClose(Event event) {
		doClose(false);
	}

	protected void doSave() {
		logger.debug("Entering");
		final FinScheduleData aFinScheduleData = new FinScheduleData();
		doSetValidation();
		doWriteComponentsToBean(aFinScheduleData);
		this.window_AddDatedScheduleDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.repayAmount.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		logger.debug("Leaving");
	}

	// Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			this.tillDateRow.setVisible(true);
			this.cbAddTermAfter.setSelectedIndex(0);
			this.addTermRow.setVisible(false);
		} else if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDTERM)
				|| this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADJMDT)
				|| this.cbReCalType.getSelectedItem().getValue().toString()
						.equals(CalculationConstants.RPYCHG_ADJTERMS)) {
			this.addTermRow.setVisible(true);
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
		} else {
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
			this.addTermRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/** To fill schedule dates */
	private void fillSchDates(Combobox dateCombobox, FinScheduleData financeDetail) {
		logger.debug("Entering");

		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		if (financeDetail.getFinanceScheduleDetails() != null) {
			boolean checkForLastValidDate = true;
			List<FinanceScheduleDetail> financeScheduleDetails = financeDetail.getFinanceScheduleDetails();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Check For Last Paid Date
				if (checkForLastValidDate) {
					validateFromDate = curSchd.getSchDate();
				}

				// Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}

				// Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}

				checkForLastValidDate = false;
				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()));
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
			}

			// murthy.y on 21/04/2020: Removed condition to allow back dated Form Date.
		}
		logger.debug("Leaving");
	}

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

	public AddDatedScheduleService getAddDatedScheduleService() {
		return addDatedScheduleService;
	}

	public void setAddDatedScheduleService(AddDatedScheduleService addDatedScheduleService) {
		this.addDatedScheduleService = addDatedScheduleService;
	}

}
