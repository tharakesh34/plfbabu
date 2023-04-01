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
 * * FileName : WIApplyChangeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
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
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.financeservice.ChangeProfitService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/Additional/ChangeProfitDialog.zul file.
 */
public class ChangeProfitDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -686158342325561513L;
	private static final Logger logger = LogManager.getLogger(ChangeProfitDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChangeProfitDialog;
	protected Combobox cbProfitFromDate;
	protected Combobox cbProfitToDate;
	protected CurrencyBox wIAmount;
	protected Date actPftFromDate = null;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	// not auto wired vars
	private FinScheduleData finScheduleData = null; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail = null; // overhanded per param
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;

	private transient ChangeProfitService changeProfitService;

	/**
	 * default constructor.<br>
	 */
	public ChangeProfitDialogCtrl() {
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
	public void onCreate$window_ChangeProfitDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangeProfitDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				setFinScheduleData((FinScheduleData) arguments.get("finScheduleData"));
			}

			if (arguments.containsKey("financeScheduleDetail")) {
				setFinanceScheduleDetail((FinanceScheduleDetail) arguments.get("financeScheduleDetail"));
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
			this.window_ChangeProfitDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ChangeProfitDialog.onClose();
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

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ChangeProfitDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cbProfitFromDate.setReadonly(true);
		this.cbProfitToDate.setReadonly(true);
		this.wIAmount.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		this.wIAmount.setMandatory(true);
		this.wIAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.wIAmount.setScale(format);
		this.wIAmount.setTextBoxWidth(150);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");

	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeProfit(Event event) throws InterruptedException {
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

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		doSetValidation();
		doWriteComponentsToBean();
		this.window_ChangeProfitDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		fillSchFromDates(this.cbProfitFromDate, aFinSchData.getFinanceScheduleDetails());

		actPftFromDate = aFinSchData.getFinanceMain().getFinStartDate();

		if (getFinanceScheduleDetail() != null) {
			fillSchToDates(this.cbProfitToDate, aFinSchData.getFinanceScheduleDetails(),
					getFinanceScheduleDetail().getSchDate());
		} else {
			fillSchToDates(this.cbProfitToDate, aFinSchData.getFinanceScheduleDetails(),
					aFinSchData.getFinanceMain().getFinStartDate());
		}

		// check the values and set in respective fields.
		// If schedule detail is not null i.e. existing one
		if (getFinanceScheduleDetail() != null) {
			if (getFinanceScheduleDetail().getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE)) {
				this.wIAmount.setValue(CurrencyUtil.parse(getFinanceScheduleDetail().getPrincipalSchd(),
						CurrencyUtil.getFormat(aFinSchData.getFinanceMain().getFinCcy())));
			} else {
				this.wIAmount.setValue(CurrencyUtil.parse(getFinanceScheduleDetail().getRepayAmount(),
						CurrencyUtil.getFormat(aFinSchData.getFinanceMain().getFinCcy())));
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.cbProfitFromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// Not Allowed for Repayment
				/*
				 * if (!curSchd.isRepayOnSchDate() ) { continue; }
				 */

				// Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setAttribute("fromSpecifier", curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if (curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	public void fillSchToDates(Combobox dateCombobox, List<FinanceScheduleDetail> financeScheduleDetails,
			Date fillAfter) {
		logger.debug("Entering");
		if ("cbProfitToDate".equals(dateCombobox.getId())) {
			this.cbProfitToDate.getItems().clear();
		}

		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				/*
				 * //Not Allowed for Repayment if (!curSchd.isRepayOnSchDate() ) { continue; }
				 */

				// Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				// Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtil.formatToLongDate(curSchd.getSchDate()) + " " + curSchd.getSpecifier());
				comboitem.setAttribute("toSpecifier", curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if (curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) == 0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else if (curSchd.getSchDate().compareTo(fillAfter) >= 0) {
					dateCombobox.appendChild(comboitem);
				} else if (curSchd.getSchDate().compareTo(fillAfter) < 0) {
					actPftFromDate = curSchd.getSchDate();
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
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.wIAmount.getValidateValue();
			finServiceInstruction.setAmount(CurrencyUtil.unFormat(this.wIAmount.getValidateValue(),
					CurrencyUtil.getFormat(finMain.getFinCcy())));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbProfitFromDate, Labels.getLabel("label_ChangeProfitDialog_FromDate.value"))) {
				finMain.setEventFromDate((Date) this.cbProfitFromDate.getSelectedItem().getValue());
				finMain.setEventFromDate((Date) actPftFromDate);
				finServiceInstruction.setFromDate((Date) this.cbProfitFromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbProfitToDate, Labels.getLabel("label_ChangeProfitDialog_ToDate.value"))
					&& this.cbProfitFromDate.getSelectedIndex() != 0) {
				if (((Date) this.cbProfitToDate.getSelectedItem().getValue())
						.compareTo((Date) this.cbProfitFromDate.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(this.cbProfitToDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",
									new String[] { Labels.getLabel("label_ChangeProfitDialog_ToDate.value"),
											Labels.getLabel("label_ChangeProfitDialog_FromDate.value") }));
				} else {
					finMain.setEventToDate((Date) this.cbProfitToDate.getSelectedItem().getValue());
				}
				finServiceInstruction.setToDate((Date) this.cbProfitToDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		finServiceInstruction.setFinID(finMain.getFinID());
		finServiceInstruction.setFinReference(finMain.getFinReference());
		finServiceInstruction.setFinEvent(FinServiceEvent.CHGPFT);

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);
		// Service details calling for Schedule calculation
		setFinScheduleData(
				changeProfitService.getChangeProfitDetails(finScheduleData, finServiceInstruction.getAmount()));
		getFinScheduleData().getFinanceMain().resetRecalculationFields();

		// Show Error Details in Schedule Maintainance
		if (getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()) {
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		} else {
			getFinScheduleData().setSchduleGenerated(true);
			if (getScheduleDetailDialogCtrl() != null) {
				try {
					getScheduleDetailDialogCtrl().doFillScheduleList(getFinScheduleData());
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
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

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		this.wIAmount
				.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ChangeProfitDialog_ProfitAmount.value"),
						CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy()), true, false));
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.wIAmount.clearErrorMessage();
		this.cbProfitFromDate.clearErrorMessage();
		this.cbProfitToDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	public void onChange$cbProfitFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		this.cbProfitToDate.setDisabled(true);
		if (isValidComboValue(this.cbProfitFromDate, Labels.getLabel("label_ChangeProfitDialog_FromDate.value"))) {
			fillSchToDates(this.cbProfitToDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.cbProfitFromDate.getSelectedItem().getValue());
			this.cbProfitToDate.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
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

	public void setChangeProfitService(ChangeProfitService changeProfitService) {
		this.changeProfitService = changeProfitService;
	}

}
