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

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.ChangeScheduleMethodService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/ReScheduleDialog.zul file.
 */
public class ChangeScheduleMethodDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = 454600127282110738L;
	private static final Logger logger = LogManager.getLogger(ChangeScheduleMethodDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ChangeScheduleMethodDialog;
	protected Combobox cbFrqFromDate;
	protected Combobox cbSchdMthd;
	protected Combobox oldSchdMthd;
	protected Uppercasebox serviceReqNo;
	protected Textbox remarks;

	// not auto wired vars
	private FinScheduleData finScheduleData; // overhanded per param
	private ScheduleDetailDialogCtrl financeMainDialogCtrl;
	private transient ChangeScheduleMethodService changeScheduleMethodService;

	private boolean appDateValidationReq = true;

	/**
	 * default constructor.<br>
	 */
	public ChangeScheduleMethodDialogCtrl() {
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
	public void onCreate$window_ChangeScheduleMethodDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ChangeScheduleMethodDialog);

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
			this.window_ChangeScheduleMethodDialog.onClose();
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
			this.window_ChangeScheduleMethodDialog.onClose();
		} catch (Exception e) {
			throw e;
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		logger.debug("Leaving");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {

	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSchd_Chng(Event event) throws InterruptedException {
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
		doWriteComponentsToBean();
		this.window_ChangeScheduleMethodDialog.onClose();
		logger.debug("Leaving");
	}

	public void doClearMessage() {
		this.cbFrqFromDate.setConstraint("");
		this.cbFrqFromDate.setErrorMessage("");

	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain FinanceMain
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		fillComboBox(this.oldSchdMthd, aFinanceMain.getScheduleMethod(), PennantStaticListUtil.getScheduleMethods(),
				"");
		fillComboBox(this.cbSchdMthd, "", PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,NO_PAY,PFTCAP,");
		fillSchFromDates(aFinSchData.getFinanceScheduleDetails());

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
			Date grcEndDate = finScheduleData.getFinanceMain().getGrcPeriodEndDate();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if (curSchd.getSchDate().compareTo(grcEndDate) <= 0) {
					continue;
				}

				// Not allow Before Current Business Date
				if (appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					continue;
				}

				// Only allowed if payment amount is greater than Zero
				if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) <= 0) {
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
					continue;
				}

				// If Presentment Exists, should not consider for recalculation
				if (curSchd.getPresentmentId() > 0) {
					this.cbFrqFromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					this.cbFrqFromDate.appendChild(comboitem);
					continue;
				}

				if (i == financeScheduleDetails.size() - 1) {
					continue;
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
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		Date fromDate = null;

		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();

		try {
			if (isValidComboValue(this.cbFrqFromDate, Labels.getLabel("label_ReScheduleDialog_FromDate.value"))) {
				fromDate = (Date) this.cbFrqFromDate.getSelectedItem().getValue();
				finServiceInstruction.setFromDate(fromDate);
				financeMain.setEventFromDate(fromDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(this.cbSchdMthd, Labels.getLabel("label_ReScheduleDialog_SchdMthd.value"))
					&& this.cbSchdMthd.getSelectedIndex() != 0) {
				finServiceInstruction.setSchdMethod(getComboboxValue(this.cbSchdMthd));
				financeMain.setRecalSchdMethod(getComboboxValue(this.cbSchdMthd));
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

		// finServiceInstruction.setPftIntact(this.pftIntact.isChecked());
		finServiceInstruction.setFinID(financeMain.getFinID());
		finServiceInstruction.setFinReference(financeMain.getFinReference());
		finServiceInstruction.setFinEvent(FinServiceEvent.CHGSCHDMETHOD);

		// Service details calling for Schedule calculation
		getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
		setFinScheduleData(
				changeScheduleMethodService.doChangeScheduleMethod(getFinScheduleData(), finServiceInstruction));
		finServiceInstruction.setPftChg(getFinScheduleData().getPftChg());
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
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

		logger.debug("Leaving");
	}

	public void onChange$cbFrqFromDate(Event event) {
		logger.debug("Entering" + event.toString());

		// fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getScheduleMethod(),
		// PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");

		if (this.cbFrqFromDate.getSelectedIndex() != 0) {
			Date fromDate = (Date) this.cbFrqFromDate.getSelectedItem().getValue();

			List<FinanceScheduleDetail> financeScheduleDetails = getFinScheduleData().getFinanceScheduleDetails();
			if (financeScheduleDetails != null) {
				for (int i = 0; i < financeScheduleDetails.size(); i++) {

					FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

					if (curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)
							|| fromDate.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0) {
						if (fromDate.compareTo(curSchd.getSchDate()) == 0) {
							if (fromDate.compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) < 0) {
								// fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
								// PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
							} else {
								// fillComboBox(this.cbSchdMthd,getFinScheduleData().getFinanceMain().getScheduleMethod(),
								// PennantStaticListUtil.getScheduleMethods(), ",GRCNDPAY,PFTCAP,");
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
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectMonth$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
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

	public ChangeScheduleMethodService getChangeScheduleMethodService() {
		return changeScheduleMethodService;
	}

	public void setChangeScheduleMethodService(ChangeScheduleMethodService changeScheduleMethodService) {
		this.changeScheduleMethodService = changeScheduleMethodService;
	}

}
