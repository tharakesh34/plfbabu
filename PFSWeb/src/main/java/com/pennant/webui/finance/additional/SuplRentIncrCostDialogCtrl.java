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
 * FileName    		:  SuplRentIncrCostDialogCtrl.java                          	            * 	  
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

public class SuplRentIncrCostDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long serialVersionUID = -4578996988245614938L;
	private static final Logger logger = Logger.getLogger(SuplRentIncrCostDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_SuplRentIncrCostDialog; 			
	protected Combobox fromDate;  					
	protected Combobox toDate;  					
	protected Decimalbox suplRent; 								
	protected Decimalbox incrCost; 								

	// not auto wired vars
	private FinScheduleData finScheduleData; 					// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 		// overhanded per param
	private transient ScheduleDetailDialogCtrl financeMainDialogCtrl;
	
	Calendar calender=Calendar.getInstance();
	private transient boolean validationOn;

	/**
	 * default constructor.<br>
	 */
	public SuplRentIncrCostDialogCtrl() {
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
	public void onCreate$window_SuplRentIncrCostDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SuplRentIncrCostDialog);

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

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());
			this.window_SuplRentIncrCostDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_SuplRentIncrCostDialog.onClose();
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
	public void doShowDialog(FinScheduleData aFinScheduleData) throws Exception {
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_SuplRentIncrCostDialog.onClose();
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
		this.suplRent.setMaxlength(13);
		this.suplRent.setFormat(PennantConstants.rateFormate9);
		this.suplRent.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.suplRent.setScale(9);
		this.incrCost.setMaxlength(13);
		this.incrCost.setFormat(PennantConstants.rateFormate9);
		this.incrCost.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.incrCost.setScale(9);
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.fromDate.setReadonly(true);
		this.toDate.setReadonly(true);
		this.suplRent.setReadonly(true);
		this.incrCost.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            FinanceMain
	 * @throws InterruptedException 
	 */
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) throws InterruptedException {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinSchData.getFinanceMain();
		
		this.suplRent.setVisible(true);
		this.incrCost.setVisible(true);
		fillSchFromDates(this.fromDate, aFinSchData.getFinanceScheduleDetails());
		this.toDate.setVisible(true);
		if(getFinanceScheduleDetail() != null ) {
			fillSchToDates(this.toDate,	aFinSchData.getFinanceScheduleDetails(), getFinanceScheduleDetail().getSchDate() );
		}else {
			fillSchToDates(this.toDate, aFinSchData.getFinanceScheduleDetails(), aFinanceMain.getFinStartDate() );
		}
		
		logger.debug("Leaving");
	}

	/** To fill schedule dates */
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		this.fromDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		
		boolean includedPrvSchTerm  = false;
		
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					this.fromDate.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					includedPrvSchTerm = false;
					continue;
				}

				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}
				
				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getGrcPeriodEndDate())) {
					continue;
				}
				
				if((i-1 > 0) && !includedPrvSchTerm){
					
					FinanceScheduleDetail prvSchd = financeScheduleDetails.get(i-1);
					
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtility.formatToLongDate(prvSchd.getSchDate())+" "+prvSchd.getSpecifier());
					comboitem.setValue(prvSchd.getSchDate());
					comboitem.setAttribute("fromSpecifier",prvSchd.getSpecifier());
					dateCombobox.appendChild(comboitem);
					
					includedPrvSchTerm = true;
				}
				
				if(i==0){
					includedPrvSchTerm = true;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("fromSpecifier",curSchd.getSpecifier());

				if (getFinanceScheduleDetail() != null) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else { 
					dateCombobox.appendChild(comboitem);
				}
			}
		}
		logger.debug("Leaving");
	}

	/** To fill schedule dates in todate combo */
	public void fillSchToDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillAfter) {
		logger.debug("Entering");
		this.toDate.getItems().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {

				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);

				//Profit Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Principal Paid (Partial/Full)
				if (curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					continue;
				}

				//Schedule Date Passed last repay date
				if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayDate())) {
					continue;
				}

				comboitem = new Comboitem();
				comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
				comboitem.setValue(curSchd.getSchDate());
				comboitem.setAttribute("toSpecifier",curSchd.getSpecifier());
				if (getFinanceScheduleDetail() != null &&  curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate()) >= 0) {
					dateCombobox.appendChild(comboitem);
					if(curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
						dateCombobox.setSelectedItem(comboitem);
					}
				} else if(curSchd.getSchDate().compareTo(fillAfter) > 0) {
					dateCombobox.appendChild(comboitem);
				}
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
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		int format=CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());

		try {
			this.incrCost.getValue();
			getFinScheduleData().getFinanceMain().setCurIncrCost(PennantAppUtil.unFormateAmount(this.incrCost.getValue(), 
					format));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.suplRent.getValue();
			getFinScheduleData().getFinanceMain().setCurSuplRent(PennantAppUtil.unFormateAmount(this.suplRent.getValue(), 
					format));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isValidComboValue( this.fromDate, Labels.getLabel("label_SuplRentIncrCostDialog_FromDate.value"))) {
				getFinScheduleData().getFinanceMain().setEventFromDate((Date)this.fromDate.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.toDate, Labels.getLabel("label_SuplRentIncrCostDialog_ToDate.value"))
					&& this.fromDate.getSelectedIndex() != 0) {
				if (((Date) this.toDate.getSelectedItem().getValue())
						.compareTo((Date) this.fromDate.getSelectedItem().getValue()) < 0) {
					throw new WrongValueException(
							this.toDate, Labels.getLabel("DATE_ALLOWED_AFTER",new String[]{
											Labels.getLabel("label_SuplRentIncrCostDialog_ToDate.value"),
											Labels.getLabel("label_SuplRentIncrCostDialog_FromDate.value")}));
				} else {
					getFinScheduleData().getFinanceMain().setEventToDate((Date)this.toDate.getSelectedItem().getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if (wve.size() > 0) {
			doClearMessage();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		//Schedule Calculation Process
		setFinScheduleData(ScheduleCalculator.calSuplRentIncrCost(getFinScheduleData()));
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		//Show Error Details in Schedule Maintainance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			MessageUtil.showError(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);

			if(getFinanceMainDialogCtrl()!=null){
				getFinanceMainDialogCtrl().doFillScheduleList(getFinScheduleData());
			}
			this.window_SuplRentIncrCostDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (this.suplRent.isVisible()) {
			this.suplRent.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SuplRentIncrCostDialog_SuplRent.value"), 
					9, false, false, 0, 9999));
		}
		if (this.incrCost.isVisible()) {
			this.incrCost.setConstraint(new PTDecimalValidator(Labels.getLabel("label_SuplRentIncrCostDialog_IncrCost.value"), 
					9, false, false, 0, 9999));
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
		this.incrCost.clearErrorMessage();
		this.suplRent.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		logger.debug("Leaving");
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSuplRentIncrCost(Event event) throws InterruptedException {
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
	
	/** To fill todates based on selected from date*/
	public void onChange$fromDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.suplRent.setText("");
		this.incrCost.setText("");
		
		if (isValidComboValue(this.fromDate,Labels.getLabel("label_SuplRentIncrCostDialog_FromDate.value"))) {
			this.toDate.getItems().clear();
			String frSpecifier = this.fromDate.getSelectedItem().getAttribute("fromSpecifier").toString();
			fillSchToDates(this.toDate, getFinScheduleData().getFinanceScheduleDetails(),
					(Date) this.fromDate.getSelectedItem().getValue());
			if((frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_REPAY)) ||
					(frSpecifier.equals(CalculationConstants.SCH_SPECIFIER_GRACE_END))) {
				if(getFinScheduleData().getFinanceMain().getRpyAdvBaseRate()!=null) {
					this.suplRent.setValue(getFinScheduleData().getFinanceMain().getSupplementRent());
					this.incrCost.setValue(getFinScheduleData().getFinanceMain().getIncreasedCost());
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
