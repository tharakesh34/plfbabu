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
 * FileName    		:  AddDatedScheduleDialogCtrl.java                          	            * 	  
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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
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
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class AddDatedScheduleDialogCtrl  extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 4583907397986780542L;
	private final static Logger logger = Logger.getLogger(AddDatedScheduleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AddDatedScheduleDialog; 	// autowired
	protected CurrencyBox 	repayAmount; 					// autowired
	protected Datebox 		fromDate; 						// autowired
	protected Combobox 		cbTillDate; 					// autowired
	protected Combobox 		cbAddTermAfter; 				// autowired
	protected Combobox 		cbReCalType; 					// autowired
	protected Row 			tillDateRow;					// autowired
	protected Row 			addTermRow;						// autowired

	private Date validateFromDate = null;

	// not auto wired vars
	private FinScheduleData finScheduleData; 				// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 	// overhanded per param
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient BigDecimal 	oldVar_disbAmount;
	private transient String 		oldVar_reCalType;
	private transient int 			oldVar_tillDate;
	private transient Date 			oldVar_fromDate;
	private transient int  			oldVar_addTermAfter;

	private transient boolean 		validationOn;
	
	static final List<ValueLabel>	      recalTypes	              = PennantStaticListUtil.getSchCalCodes();
	static final List<ValueLabel>	      addTermCodes	              = PennantStaticListUtil.getAddTermCodes();
	
	/**
	 * default constructor.<br>
	 */
	public AddDatedScheduleDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected FinanceMain object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AddDatedScheduleDialog(Event event) throws Exception {
		logger.debug(event.toString());

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) args.get("finScheduleData");
			setFinScheduleData(this.finScheduleData);
		} else {
			setFinScheduleData(null);
		}
		
		if (args.containsKey("financeScheduleDetail")) {
			this.setFinanceScheduleDetail((FinanceScheduleDetail) args.get("financeScheduleDetail"));
			setFinanceScheduleDetail(this.financeScheduleDetail);
		} else {
			setFinanceScheduleDetail(null);
		}
		

		// READ OVERHANDED params !
		// we get the WIFFinanceMainDialogCtrl controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete WIFFinanceMain here.
		if (args.containsKey("financeMainDialogCtrl")) {
			setScheduleDetailDialogCtrl((ScheduleDetailDialogCtrl) args.get("financeMainDialogCtrl"));
		} 
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinScheduleData());
		this.window_AddDatedScheduleDialog.doModal();
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.fromDate.setFormat(PennantConstants.dateFormat);
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_disbAmount = this.repayAmount.getValue();
		this.oldVar_fromDate = this.fromDate.getValue();
		this.oldVar_reCalType = this.cbReCalType.getValue();
		this.oldVar_tillDate = this.cbTillDate.getSelectedIndex();
		this.oldVar_addTermAfter = this.cbAddTermAfter.getSelectedIndex();
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinanceScheduleDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinScheduleData aFinScheduleData) throws InterruptedException {
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
			doStoreInitValues();
			setDialog(this.window_AddDatedScheduleDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
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
		fillSchDates(this.cbTillDate,aFinSchData);
		if (getFinanceScheduleDetail() != null) {
			this.repayAmount.setValue(PennantAppUtil.formateAmount(
					getFinanceScheduleDetail().getDisbAmount(),
					aFinSchData.getFinanceMain().getLovDescFinFormatter()));
			this.fromDate.setValue(getFinanceScheduleDetail().getSchDate());
			//Check if schedule header is null or not and set the recal type fields.
		}
		if(aFinSchData.getFinanceMain() != null ) {
			fillComboBox(this.cbReCalType, aFinSchData.getFinanceMain().getRecalType(), recalTypes, ",CURPRD,ADDTERM,ADDLAST,");
		}else {
			fillComboBox(this.cbReCalType, "", recalTypes, "");
		}
		if(getFinScheduleData().getFinanceMain().getRecalType().equals(CalculationConstants.RPYCHG_TILLDATE)) {
			this.tillDateRow.setVisible(true);
		}
		fillComboBox(this.cbAddTermAfter, "", addTermCodes, ",LAST REPAY,");

		logger.debug("Leaving");	
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterruptedException 
	 * @throws WrongValueException 
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinScheduleData) throws WrongValueException, InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.repayAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.fromDate.getValue().compareTo(validateFromDate) <= 0 ||
					this.fromDate.getValue().compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) >= 0){
				throw new WrongValueException(this.fromDate, Labels.getLabel("DATE_RANGE", new String[]{
					Labels.getLabel("label_AddDatedScheduleDialog_FromDate.value"), 
					PennantAppUtil.formateDate(validateFromDate, PennantConstants.dateFormate), 
					PennantAppUtil.formateDate(getFinScheduleData().getFinanceMain().getMaturityDate(), PennantConstants.dateFormate) }));
			}
			getFinScheduleData().getFinanceMain().setEventFromDate(this.fromDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if (isValidComboValue(this.cbReCalType, Labels.getLabel("label_AddDatedScheduleDialog_RecalType.value"))
					&& this.cbReCalType.getSelectedIndex() != 0) {
				getFinScheduleData().getFinanceMain().setRecalType(this.cbReCalType.getSelectedItem().getValue().toString());
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(this.tillDateRow.isVisible()) {
			try {
				if(this.cbTillDate.getSelectedIndex() == 0) {
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_AddDatedScheduleDialog_TillDate.value") }));
				}
				if((this.fromDate.getValue() != null && 
						((Date) this.cbTillDate.getSelectedItem().getValue()).compareTo(this.fromDate.getValue()) < 0) ||
						(((Date) this.cbTillDate.getSelectedItem().getValue()).compareTo(this.fromDate.getValue()) == 0)){
					throw new WrongValueException(this.cbTillDate, Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[]{ Labels.getLabel("label_AddDatedScheduleDialog_TillDate.value"),
						PennantAppUtil.formateDate((Date)this.fromDate.getValue(), PennantConstants.dateFormate)}));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try{
			if(this.addTermRow.isVisible()) {
				if (isValidComboValue(this.cbAddTermAfter,Labels.getLabel("label_AddDatedScheduleDialog_AddTermAfter.value"))
						&& this.cbAddTermAfter.getSelectedIndex() != 0) {
					this.cbAddTermAfter.getSelectedItem().getValue().toString();
				}
			}
		}catch (WrongValueException we) {
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
		
		/*if(this.tillDateRow.isVisible() && this.cbTillDate.getSelectedIndex() > 0) {
			getFinScheduleData().getFinanceMain().setRecalToDate((Date)this.cbTillDate.getSelectedItem().getValue());
			getFinScheduleData().getFinanceMain().setEventToDate((Date)this.cbTillDate.getSelectedItem().getValue());
			setFinScheduleData(ScheduleCalculator.addDatedSchedule(getFinScheduleData(), PennantAppUtil.unFormateAmount(this.repayAmount.getValue(),
							getFinScheduleData().getFinanceMain().getLovDescFinFormatter()), null));
		}else if(this.addTermRow.isVisible() && this.cbAddTermAfter.getSelectedIndex() > 0) {
			getFinScheduleData().getFinanceMain().setRecalToDate(null);
			setFinScheduleData(ScheduleCalculator.addDatedSchedule(getFinScheduleData(), PennantAppUtil.unFormateAmount(
							this.repayAmount.getValue(),getFinScheduleData().getFinanceMain().getLovDescFinFormatter()),
							this.cbAddTermAfter.getSelectedItem().getValue().toString()));
		}else {*/
		//}
		
		//Schedule Recalculation With New Dated Schedule term 
		getFinScheduleData().getFinanceMain().setRecalToDate(null);
		setFinScheduleData(ScheduleCalculator.addDatedSchedule(getFinScheduleData()));
		
		//Show Error Details in Schedule Maintenance
		if(getFinScheduleData().getErrorDetails() != null && !getFinScheduleData().getErrorDetails().isEmpty()){
			PTMessageUtils.showErrorMessage(getFinScheduleData().getErrorDetails().get(0));
			getFinScheduleData().getErrorDetails().clear();
		}else{
			getFinScheduleData().setSchduleGenerated(true);
			if(getScheduleDetailDialogCtrl()!=null){
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
			/*this.repayAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_AddDatedScheduleDialog_Amount.value"),false));*/
		}
		if (this.fromDate.isVisible()) {
			this.fromDate.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_AddDatedScheduleDialog_FromDate.value")}));
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws WrongValueException 
	 */
	public void onClick$btnAddDatedSchedule(Event event) throws InterruptedException, WrongValueException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		if(getFinanceScheduleDetail()!=null){
			if(isDataChanged()){
				doSave();
			}else{
				PTMessageUtils.showErrorMessage("No Data has been changed.");
			}
		}else{
			doSave();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws WrongValueException 
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, WrongValueException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when close event is occurred. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws WrongValueException 
	 * */
	public void onClose(Event event)throws InterruptedException, WrongValueException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());

	}

	private void doClose() throws InterruptedException, WrongValueException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		boolean close = true;
		doClearMessage();
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels
			.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			this.window_AddDatedScheduleDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws WrongValueException 
	 */
	public void doSave() throws InterruptedException, WrongValueException, IllegalAccessException, InvocationTargetException {
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
	private void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.repayAmount.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");

		if (this.oldVar_disbAmount != this.repayAmount.getValue()) {
			return true;
		}
		if (this.oldVar_reCalType != this.cbReCalType
				.getValue()) {
			return true;
		}
		if (this.oldVar_fromDate != this.fromDate.getValue()) {
			return true;
		}
		if (this.oldVar_addTermAfter != this.cbAddTermAfter.getSelectedIndex()) {
			return true;
		}
		if ( tillDateRow.isVisible() && this.oldVar_tillDate != this.cbTillDate.getSelectedIndex()) {
			return true;
		}
		logger.debug("Leaving");
		return false;
	}
	
	//Enable till date field if the selected recalculation type is TIIDATE
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_TILLDATE)){
			this.tillDateRow.setVisible(true);
			this.cbAddTermAfter.setSelectedIndex(0);
			this.addTermRow.setVisible(false);
		}else if(this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDTERM) ||
				this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADJMDT) ||
				this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADJTERMS)) {
			this.addTermRow.setVisible(true);
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
		}else {
			this.cbTillDate.setSelectedIndex(0);
			this.tillDateRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
			this.addTermRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/** To fill schedule dates */
	public void fillSchDates(Combobox dateCombobox, FinScheduleData financeDetail) {
		logger.debug("Entering");
		
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		
		Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
		if (financeDetail.getFinanceScheduleDetails() != null) {
			boolean checkForLastValidDate = true;
			List<FinanceScheduleDetail> financeScheduleDetails = financeDetail.getFinanceScheduleDetails();
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				
				FinanceScheduleDetail curSchd = financeScheduleDetails.get(i);
				
				//Check For Last Paid Date
				if(checkForLastValidDate){
					validateFromDate = curSchd.getSchDate();
				}
				
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

				//Profit repayment on frequency is TRUE
				if (getFinScheduleData().getFinanceMain().isFinRepayPftOnFrq()) {
					if (curSchd.getSchDate().before(getFinScheduleData().getFinanceMain().getLastRepayPftDate())) {
						continue;
					}
				}
				
				checkForLastValidDate = false;
				comboitem = new Comboitem();
				comboitem.setLabel(PennantAppUtil.formateDate(curSchd.getSchDate(), PennantConstants.dateFormate));
				comboitem.setValue(curSchd.getSchDate());
				dateCombobox.appendChild(comboitem);
			}
			
			if(validateFromDate.compareTo(curBussDate) < 0){
				validateFromDate = curBussDate;
			}
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

}
