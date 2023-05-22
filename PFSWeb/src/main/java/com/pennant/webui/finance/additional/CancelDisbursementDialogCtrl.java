/**
 * 
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
 * * FileName : CancelDisbursementDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-10-2011 * *
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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.CancelDisbursementService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class CancelDisbursementDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 4583907397986780542L;
	private static final Logger logger = LogManager.getLogger(CancelDisbursementDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CancelDisbursementDialog;
	protected CurrencyBox disbAmount;
	protected Combobox fromDate;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	// private Date lastPaidDate = null;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; // overhanded per param
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private AccountsService accountsService;
	private transient CancelDisbursementService cancelDisbursementService;
	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public CancelDisbursementDialogCtrl() {
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
	public void onCreate$window_CancelDisbursementDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CancelDisbursementDialog);

		try {
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
			this.window_CancelDisbursementDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CancelDisbursementDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		// Empty sent any required attributes
		this.disbAmount.setMandatory(false);
		this.disbAmount.setReadonly(false);
		this.disbAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.disbAmount.setScale(formatter);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.fromDate.setReadonly(true);
		this.disbAmount.setDisabled(true);
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
		try {

			doReadOnly();
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CancelDisbursementDialog.onClose();
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
		if (aFinSchData.getDisbursementDetails() != null && !aFinSchData.getDisbursementDetails().isEmpty()) {
			fillSchFromDates(this.fromDate, aFinSchData);
		}

		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox, FinScheduleData scheduleData) {
		logger.debug("Entering");

		this.fromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		List<FinanceScheduleDetail> finScheduleDetailsList = scheduleData.getFinanceScheduleDetails();

		List<FinanceScheduleDetail> finScheduleDetails = finScheduleDetailsList;
		for (int i = 0; i < finScheduleDetails.size(); i++) {
			FinanceScheduleDetail curSchd = finScheduleDetails.get(i);
			if (curSchd.isDisbOnSchDate()) {

				if (curSchd.getSchDate().compareTo(SysParamUtil.getAppDate()) >= 0) {

					if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
							|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {
						continue;

					}

					// Don't Allow Start date Disbursement cancellation
					if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
						// continue;
					}

					// Adding through Disbursement details
					List<FinanceDisbursement> disbList = scheduleData.getDisbursementDetails();
					if (!disbList.isEmpty()) {

						for (int j = 0; j < disbList.size(); j++) {
							FinanceDisbursement curDisb = disbList.get(j);
							if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
								continue;
							}
							if (DateUtil.compare(curDisb.getDisbDate(), curSchd.getSchDate()) >= 0) {
								comboitem = new Comboitem();
								comboitem.setLabel(
										DateUtil.format(curDisb.getDisbDate(), DateFormat.SHORT_DATE.getPattern())
												+ " , " + curDisb.getDisbSeq());
								comboitem.setValue(curDisb.getDisbDate());
								comboitem.setAttribute("Seq", curDisb.getDisbSeq());
								dateCombobox.appendChild(comboitem);
							}
						}

						// After Disbursements Completion Close the loop
						break;
					}
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
	private void doWriteComponentsToBean(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		FinanceMain finMain = getFinScheduleData().getFinanceMain();
		int formatter = CurrencyUtil.getFormat(finMain.getFinCcy());

		try {
			this.disbAmount.getValidateValue();
			finServiceInstruction.setAmount(CurrencyUtil.unFormat(this.disbAmount.getValidateValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.fromDate.getSelectedIndex() <= 0) {
				throw new WrongValueException(this.fromDate, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CancelDisbursementDialog_FromDate.value") }));
			}
			finServiceInstruction.setFromDate((Date) this.fromDate.getSelectedItem().getValue());
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

		finServiceInstruction.setFinID(finMain.getFinID());
		finServiceInstruction.setFinReference(finMain.getFinReference());
		finServiceInstruction.setFinEvent(FinServiceEvent.CANCELDISB);

		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		// Disbursement Details Correction
		List<FinanceDisbursement> list = getFinScheduleData().getDisbursementDetails();
		int selectedSeq = Integer.parseInt(String.valueOf(this.fromDate.getSelectedItem().getAttribute("Seq")));
		for (int i = 0; i < list.size(); i++) {
			FinanceDisbursement disbursement = list.get(i);
			if (disbursement.getDisbDate().compareTo(finServiceInstruction.getFromDate()) == 0
					&& disbursement.getDisbSeq() == selectedSeq) {
				disbursement.setDisbStatus(FinanceConstants.DISB_STATUS_CANCEL);
				break;
			}
		}

		// Schedule Data disbursement Amount Correction
		getFinScheduleData().setDisbursementDetails(list);
		Date eventFromDate = null;
		for (int i = 0; i < getFinScheduleData().getFinanceScheduleDetails().size(); i++) {
			FinanceScheduleDetail curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			if (curSchd.getSchDate().compareTo(finServiceInstruction.getFromDate()) == 0) {
				if (curSchd.isDisbOnSchDate()) {
					curSchd.setDisbAmount(curSchd.getDisbAmount().subtract(finServiceInstruction.getAmount()));
					if (curSchd.getDisbAmount().compareTo(BigDecimal.ZERO) == 0) {
						curSchd.setDisbOnSchDate(false);
					} else {
						curSchd.setDisbOnSchDate(true);
					}
					eventFromDate = getFinScheduleData().getFinanceScheduleDetails().get(i).getSchDate();
				}

				if (!curSchd.isDisbOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isPftOnSchDate()
						&& !curSchd.isRvwOnSchDate() && !curSchd.isCpzOnSchDate()) {
					eventFromDate = getFinScheduleData().getFinanceScheduleDetails().get(i + 1).getSchDate();
					getFinScheduleData().getFinanceScheduleDetails().remove(i);
					i--;
				}
				break;
			}
		}

		finMain.setEventFromDate(eventFromDate);
		finMain.setEventToDate(finMain.getMaturityDate());
		finMain.setRecalFromDate(finServiceInstruction.getFromDate());
		finMain.setRecalToDate(finMain.getMaturityDate());

		// Service details calling for Schedule calculation
		setFinScheduleData(cancelDisbursementService.getCancelDisbDetails(getFinScheduleData()));
		finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);

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
		logger.debug("Leaving");
	}

	public void onClick$btnCancelDisbursement(Event event) {
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

	protected void doSave() {
		logger.debug("Entering");
		final FinScheduleData aFinScheduleData = new FinScheduleData();
		doSetValidation();
		doWriteComponentsToBean(aFinScheduleData);
		this.window_CancelDisbursementDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.disbAmount.setErrorMessage("");
		this.fromDate.setErrorMessage("");
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error message
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.disbAmount.setConstraint("");
		this.fromDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method to set the amount on based of disbDate
	 * 
	 * @param event
	 */
	public void onChange$fromDate(Event event) {
		logger.debug("Entering" + event.toString());

		if (getFinScheduleData().getDisbursementDetails() != null
				&& getFinScheduleData().getDisbursementDetails().size() > 0) {
			for (FinanceDisbursement finDisbursement : getFinScheduleData().getDisbursementDetails()) {
				int sequence = (Integer) this.fromDate.getSelectedItem().getAttribute("Seq");
				if (finDisbursement.getDisbDate().compareTo((Date) this.fromDate.getSelectedItem().getValue()) == 0
						&& sequence == finDisbursement.getDisbSeq()) {
					this.disbAmount.setValue(CurrencyUtil.parse(finDisbursement.getDisbAmount(),
							CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())));
					break;
				}
			}
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

	public boolean isValidationOn() {
		return validationOn;
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public void setCancelDisbursementService(CancelDisbursementService cancelDisbursementService) {
		this.cancelDisbursementService = cancelDisbursementService;
	}

}
