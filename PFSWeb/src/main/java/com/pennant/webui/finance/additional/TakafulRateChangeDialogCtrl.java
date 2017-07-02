/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *											    											*
 * FileName    		:  WIRateChangeDialogCtrl.java                          	            * 	  
 *                                                                    			    		*
 * Author      		:  PENNANT TECHONOLOGIES              				    				*
 *                                                                  			    		*
 * Creation Date    :  05-10-2011    							    						*
 *                                                                  			    		*
 * Modified Date    :  05-10-2011    							    						*
 *                                                                  			    		*
 * Description 		:                                             			    			*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-10-2011       Pennant	                 0.1                                        	* 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.additional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

public class TakafulRateChangeDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -4578996988245614938L;
	private static final Logger logger = Logger.getLogger(TakafulRateChangeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_TakafulRateChangeDialog; 				
	protected Decimalbox 	rateChange; 									
	protected Combobox 		insurances;  										
	protected Combobox 		fromDate;  										
	protected Combobox 		toDate;  										
	protected Button 		btnAddTakafulRate;
	protected Uppercasebox  serviceReqNo;
	protected Textbox		remarks;
	// not auto wired vars
	private FinScheduleData finScheduleData; 					// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 		// overhanded per param
	private transient ScheduleDetailDialogCtrl financeMainDialogCtrl;

	private transient boolean validationOn;
	private String moduleDefiner = "";

	/**
	 * default constructor.<br>
	 */
	public TakafulRateChangeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceMain object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_TakafulRateChangeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_TakafulRateChangeDialog);

		try {
			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(this.finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setFinanceMainDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setFinanceMainDialogCtrl(null);
			}

			if (arguments.containsKey("moduleDefiner")) {
				moduleDefiner = (String) arguments.get("moduleDefiner");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
			this.window_TakafulRateChangeDialog.doModal();

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_TakafulRateChangeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws Exception
	 */
	private void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_TakafulRateChangeDialog.onClose();
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
		// Empty sent any required attributes
		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateChange.setScale(9);
		this.serviceReqNo.setMaxlength(20);
		this.remarks.setMaxlength(200);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 */
	private void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		fillSchInsurances(this.insurances, aFinSchData.getFinInsuranceList());
		fillSchFromDates(this.fromDate,	aFinSchData.getFinanceScheduleDetails());
		fillSchToDates(this.toDate,aFinSchData.getFinanceScheduleDetails(), aFinanceMain.getFinStartDate());
		logger.debug("Leaving");
	}

	private void fillSchInsurances(Combobox insuranceCombobox, List<FinInsurances> finInsuranceList) {
		logger.debug("Entering");

		this.fromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		insuranceCombobox.appendChild(comboitem);
		insuranceCombobox.setSelectedItem(comboitem);

		if (finInsuranceList != null && !finInsuranceList.isEmpty()) {
			for (int i = 0; i < finInsuranceList.size(); i++) {
				if (finInsuranceList.get(i).getPaymentMethod().equals(InsuranceConstants.PAYTYPE_SCH_FRQ)) {
					FinInsurances curInsurance = finInsuranceList.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(curInsurance.getInsuranceType() + "_" + curInsurance.getInsReference());
					comboitem.setValue(curInsurance.getInsuranceType());
					insuranceCombobox.appendChild(comboitem);
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	private void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");

		this.fromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// ALlow only Repayment Schedule Dates
				if (!curSchd.isRepayOnSchDate() || 
						curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0){
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	private void fillSchToDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillAfter) {
		logger.debug("Entering");
		this.toDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				// ALlow only Repayment Schedule Dates
				if (!curSchd.isRepayOnSchDate() || 
						curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate()) <= 0){
					continue;
				}

				if(curSchd.getSchDate().compareTo(fillAfter) < 0) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InterruptedException 
	 */
	private void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.rateChange.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue(
					this.fromDate,
					Labels.getLabel("label_TakafulRateChangeDialog_FromDate.value"))) {
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)this.fromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.toDate,
					Labels.getLabel("label_TakafulRateChangeDialog_ToDate.value"))
					&& this.fromDate.getSelectedIndex() != 0) {
				if (((Date) this.toDate.getSelectedItem().getValue())
						.compareTo((Date) this.fromDate.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(
							this.toDate,
							Labels.getLabel("DATE_ALLOWED_AFTER",new String[]{
									Labels.getLabel("label_TakafulRateChangeDialog_ToDate.value"),
									Labels.getLabel("label_TakafulRateChangeDialog_FromDate.value")}));
				} else {
					getFinScheduleData().getFinanceMain().setEventToDate((Date)this.toDate.getSelectedItem().getValue());
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
		for (int i = 0; i < getFinScheduleData().getInsuranceList().size(); i++) {
			List<FinSchFrqInsurance> schdList = getFinScheduleData().getFinInsuranceList().get(i).getFinSchFrqInsurances();
			if (schdList != null && !schdList.isEmpty()) {
				FinanceMain financeMain = getFinScheduleData().getFinanceMain();
				for (FinSchFrqInsurance curSchd : schdList) {
					if (curSchd.getInsSchDate().compareTo(financeMain.getEventFromDate()) < 0) {
						continue;
					}
					if (curSchd.getInsSchDate().compareTo(financeMain.getEventToDate()) > 0) {
						break;
					}

					if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_INSCHANGE)) {
						if (curSchd.getInsuranceType().equals(this.insurances.getSelectedItem().getValue())) {
							curSchd.setInsuranceRate(this.rateChange.getValue());
						}
					}
				}
			}

		}

		//Schedule Calculation Process
		if(StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_INSCHANGE)){
			setFinScheduleData(ScheduleCalculator.recalInsuranceSchedule(getFinScheduleData()));
		}
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		//Show Error Details in Schedule Maintenance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);

			if(getFinanceMainDialogCtrl() != null){
					getFinanceMainDialogCtrl().doFillSchInsDetails(getFinScheduleData().getFinInsuranceList());
			}
			this.window_TakafulRateChangeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.rateChange.isVisible()) {
			this.rateChange.setConstraint(new PTDecimalValidator(Labels.getLabel("label_TakafulRateChangeDialog_Rate.value"), 
					9, true, false, 0, 9999));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages
	 * 
	 * */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.rateChange.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddTakafulRate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(getFinanceScheduleDetail()!=null){
			if(isDataChanged()){
				doSave();
			}else{
				MessageUtil.showError("No Data has been changed.");
			}
		}else{
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *			  An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(false);
	}

	/**
	 * The Click event is raised when the Close event is occurred.
	 * 
	 * @param event
	 * 
	 * */
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

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public ScheduleDetailDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(ScheduleDetailDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

}
