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
 * FileName    		:  WIAddRmvTermsDialogCtrl.java                          	            * 	  
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

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

public class AddRmvTermsDialogCtrl extends GFCBaseCtrl<FinScheduleData> {
	private static final long				serialVersionUID	= 2623911832045017662L;
	private static final Logger				logger				= Logger.getLogger(AddRmvTermsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window						window_AddRmvTermsDialog;
	protected Intbox						terms;
	protected Button						btnAddRmvTerms;
	protected Combobox						cbFromDate;
	protected Combobox						cbReCalType;
	protected Combobox						cbRecalFromDate;
	protected Row							recalFromDateRow;
	protected Row							numOfTermsRow;
	protected Row							recalTypeRow;
	protected Row							fromDateRow;
	protected Uppercasebox					serviceReqNo;
	protected Textbox						remarks;

	// not auto wired vars
	private FinScheduleData					finScheduleData;														// overhanded per param
	private FinanceScheduleDetail			financeScheduleDetail;													// overhanded per param
	private ScheduleDetailDialogCtrl		scheduleDetailDialogCtrl;

	private transient boolean				validationOn;
	private boolean							addTerms;

	private transient AddTermsService		addTermsService;
	private transient RemoveTermsService	rmvTermsService;
	private boolean appDateValidationReq = false;

	/**
	 * default constructor.<br>
	 */
	public AddRmvTermsDialogCtrl() {
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
	 * @throws Exception
	 */
	public void onCreate$window_AddRmvTermsDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_AddRmvTermsDialog);

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
			
			if (arguments.containsKey("appDateValidationReq")) {
				this.appDateValidationReq = (boolean) arguments.get("appDateValidationReq");
			}

			// READ OVERHANDED params !
			// we get the WIFFinanceMainDialogCtrl controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete WIFFinanceMain here.
			if (arguments.containsKey("financeMainDialogCtrl")) {
				setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) arguments.get("financeMainDialogCtrl"));
			} else {
				setScheduleDetailDialogCtrl(null);
			}

			if (arguments.containsKey("addTerms")) {
				this.setAddTerms((Boolean) arguments.get("addTerms"));
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinScheduleData());

			if (addTerms) {
				this.window_AddRmvTermsDialog.setTitle(Labels.getLabel("window_AddTermsDialog.title"));
			} else {
				this.window_AddRmvTermsDialog.setTitle(Labels.getLabel("window_RmvTermsDialog.title"));
			}
			setDialog(DialogType.MODAL);
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_AddRmvTermsDialog.onClose();
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinScheduleData);

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_AddRmvTermsDialog.onClose();
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
		this.terms.setMaxlength(3);
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
	public void doWriteBeanToComponents(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		this.terms.setValue(0);
		if (isAddTerms()) {
			this.fromDateRow.setVisible(false);
			this.btnAddRmvTerms.setLabel(Labels.getLabel("btnAddTerms.label"));
			this.btnAddRmvTerms.setTooltiptext(Labels.getLabel("btnAddTerms.tooltiptext"));
			this.recalFromDateRow.setVisible(false);
			this.numOfTermsRow.setVisible(true);
		} else {
			this.numOfTermsRow.setVisible(false);
			this.fromDateRow.setVisible(true);
			fillSchFromDates(this.cbFromDate, aFinSchData.getFinanceScheduleDetails());
			this.btnAddRmvTerms.setLabel(Labels.getLabel("btnRmvTerms.label"));
			this.btnAddRmvTerms.setTooltiptext(Labels.getLabel("btnRmvTerms.tooltiptext"));
			this.recalTypeRow.setVisible(true);

			String excldValues = ",CURPRD,TILLDATE,ADDTERM,ADDRECAL,STEPPOS,";
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(),
					PennantStaticListUtil.getSchCalCodes(), excldValues);
			
			changeRecalType();
			fillSchRecalFromDates(cbRecalFromDate, aFinSchData.getFinanceScheduleDetails(), null);
		}
		
		logger.debug("Entering");
	}
	
	public void fillSchRecalFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails, Date fillBefore) {
		logger.debug("Entering");
		
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				if (curSchd.isRepayOnSchDate()  
						&& ((curSchd.getProfitSchd().compareTo(curSchd.getSchdPftPaid()) >= 0 && 
						curSchd.isRepayOnSchDate() && !curSchd.isSchPftPaid()) ||
								(curSchd.getPrincipalSchd().compareTo(curSchd.getSchdPriPaid()) >= 0 && 
								curSchd.isRepayOnSchDate() && !curSchd.isSchPriPaid()))) {
					
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
					comboitem.setAttribute("toSpecifier",curSchd.getSpecifier());
					comboitem.setValue(curSchd.getSchDate());
					if(fillBefore != null && curSchd.getSchDate().compareTo(fillBefore) < 0) {
						if(i != financeScheduleDetails.size()-1){
							dateCombobox.appendChild(comboitem);
							if(getFinanceScheduleDetail() != null && curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
								dateCombobox.setSelectedItem(comboitem);
							}
						}
					} else{
						if(i != financeScheduleDetails.size()-1 && fillBefore == null){
							dateCombobox.appendChild(comboitem);
							if(getFinanceScheduleDetail() != null && curSchd.getSchDate().compareTo(getFinanceScheduleDetail().getSchDate())==0) {
								dateCombobox.setSelectedItem(comboitem);
							}
						}
					}
				}
			}
		}
		logger.debug("Leaving");
	}
	
	public void onChange$cbFromDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbFromDate.getSelectedIndex() > 0){
			this.cbRecalFromDate.getItems().clear();
			fillSchRecalFromDates(cbRecalFromDate, getFinScheduleData().getFinanceScheduleDetails(), 
					(Date)this.cbFromDate.getSelectedItem().getValue());	
		}
		logger.debug("Leaving" + event.toString());
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
		FinServiceInstruction finServiceInstruction = new FinServiceInstruction();
		int count = 0;
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		if (this.numOfTermsRow.isVisible()) {
			try {
				this.terms.getValue();
			} catch (WrongValueException we) {
				wve.add(we);
			}
			finServiceInstruction.setTerms(this.terms.getValue());
		}
		try {
			if (this.fromDateRow.isVisible()) {
				if (isValidComboValue(this.cbFromDate, Labels.getLabel("label_AddRmvTermsDialog_FromDate.value"))) {
					getFinScheduleData().getFinanceMain().setEventFromDate(
							(Date) this.cbFromDate.getSelectedItem().getValue());
					List<FinanceScheduleDetail> sd = getFinScheduleData().getFinanceScheduleDetails();

					for (int i = 0; i < sd.size(); i++) {
						if (getFinScheduleData().getFinanceMain().getEventFromDate().compareTo(sd.get(i).getSchDate()) == 0) {
							count = count + (sd.size() - 1) - i;
						}
					}
				}
				Date fromDate= (Date) this.cbFromDate.getSelectedItem().getValue();
				finServiceInstruction.setFromDate(fromDate);
				getFinScheduleData().getFinanceMain().setRecalFromDate(fromDate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.recalTypeRow.isVisible()) {
				if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_AddRmvTermsDialog_RecalType.value"))
						&& this.cbReCalType.getSelectedIndex() != 0) {
					getFinScheduleData().getFinanceMain().setRecalType(
							this.cbReCalType.getSelectedItem().getValue().toString());

				}

				finServiceInstruction.setRecalType(getComboboxValue(this.cbReCalType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.recalFromDateRow.isVisible()) {
				if (isValidComboValue(this.cbRecalFromDate,
						Labels.getLabel("label_AddRmvTermsDialog_RecalFromDate.value"))
						&& this.cbRecalFromDate.getSelectedIndex() != 0) {
					this.cbRecalFromDate.getSelectedItem().getValue().toString();
				}
				
				Date recalFromDate= (Date) this.cbRecalFromDate.getSelectedItem().getValue();
				getFinScheduleData().getFinanceMain().setRecalFromDate(recalFromDate);
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

		finServiceInstruction.setFinReference(getFinScheduleData().getFinanceMain().getFinReference());
		if(isAddTerms()){
			finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_ADDTERM);
		}else{
			finServiceInstruction.setFinEvent(FinanceConstants.FINSER_EVENT_RMVTERM);
		}
		finServiceInstruction.setServiceReqNo(this.serviceReqNo.getValue());
		finServiceInstruction.setRemarks(this.remarks.getValue());

		getFinScheduleData().getErrorDetails().clear();

		getFinScheduleData().setFinServiceInstruction(finServiceInstruction);
		// call change frequency method to calculate new schedules(Service details calling for Schedule calculation)
		if (isAddTerms()) {
			getFinScheduleData().getFinanceMain().setEventFromDate(
					getFinScheduleData().getFinanceMain().getFinStartDate());
			setFinScheduleData(addTermsService.getAddTermsDetails(getFinScheduleData(), finServiceInstruction));
		} else {
			setFinScheduleData(rmvTermsService.getRmvTermsDetails(getFinScheduleData()));
		}
		getFinScheduleData().getFinanceMain().resetRecalculationFields();
		//Show Error Details in Schedule Maintenance
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
		if (this.terms.isVisible()) {
			this.terms.setConstraint(new PTNumberValidator(Labels.getLabel("label_AddRmvTermsDialog_Terms.value"),
					true, false));
		}
		if (this.fromDateRow.isVisible()) {
			this.cbFromDate.setConstraint("NO EMPTY:"
					+ Labels.getLabel("FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_AddRmvTermsDialog_FromDate.value") }));
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "AddRmvTerms" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddRmvTerms(Event event) throws InterruptedException {
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
	 * @param event
	 *            An event sent to the event handler of a component.
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

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.terms.clearErrorMessage();
		this.cbFromDate.clearErrorMessage();
		this.serviceReqNo.setErrorMessage("");
		this.remarks.setErrorMessage("");
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

		this.window_AddRmvTermsDialog.onClose();
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
		boolean termsExist = false;
		Date curBussDate = DateUtility.getAppDate();
		Date grcEndDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();

		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				
				//In Remove Terms the Disbursement Dates Need to be shown
				if(curSchd.isDisbOnSchDate() && !isAddTerms()){
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}
			
				if (DateUtility.compare(curSchd.getSchDate(), grcEndDate) <= 0) {
					continue;
				}
				
				// Not allow Before Current Business Date
				if(appDateValidationReq && curSchd.getSchDate().compareTo(curBussDate) <= 0) {
					continue;
				}

				//Not Allowed for Repayment
				if (!(curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(
						BigDecimal.ZERO) > 0))) {
					continue;
				}

				//Profit Paid (Partial/Full) or Principal Paid (Partial/Full)
				if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0 || curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0) {
					dateCombobox.getItems().clear();
					comboitem = new Comboitem();
					comboitem.setValue("#");
					comboitem.setLabel(Labels.getLabel("Combo.Select"));
					dateCombobox.appendChild(comboitem);
					dateCombobox.setSelectedItem(comboitem);
					continue;
				}

				boolean addToCombo = true;
				if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) == 0) {
					if (!termsExist) {
						addToCombo = false;
					}
				} else {
					termsExist = true;
				}

				if (addToCombo) {
					comboitem = new Comboitem();
					comboitem.setLabel(DateUtility.formatToLongDate(curSchd.getSchDate())+" "+curSchd.getSpecifier());
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

	//Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		changeRecalType();
		logger.debug("Leaving" + event.toString());
	}
	
	private void changeRecalType(){
		if (this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.recalFromDateRow.setVisible(true);
		} else {
			if(this.cbRecalFromDate.getItems() != null && this.cbRecalFromDate.getItems().size() > 0){
				this.cbRecalFromDate.setSelectedIndex(0);
			}
			this.recalFromDateRow.setVisible(false);
		}
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

	public boolean isAddTerms() {
		return addTerms;
	}

	public void setAddTerms(boolean addTerms) {
		this.addTerms = addTerms;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public void setAddTermsService(AddTermsService addTermsService) {
		this.addTermsService = addTermsService;
	}

	public void setRmvTermsService(RemoveTermsService rmvTermsService) {
		this.rmvTermsService = rmvTermsService;
	}
}
