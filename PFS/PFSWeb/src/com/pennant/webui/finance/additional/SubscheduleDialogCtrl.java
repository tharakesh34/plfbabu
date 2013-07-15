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
 * FileName    		:  SubScheduleDialogCtrl.java                          	            * 	  
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.webui.finance.financemain.FinanceMainDialogCtrl;
import com.pennant.webui.finance.wiffinancemain.WIFFinanceMainDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

public class SubscheduleDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(SubscheduleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_SubScheduleDialog; 		// autowired
	protected Datebox 		firstDate; 	 					// autowired
	protected Textbox 		termFrq; 						// autoWired
	protected Combobox 		cbTermFrqCode; 					// autoWired
	protected Combobox 		cbTermFrqMth; 					// autoWired
	protected Combobox 		cbTermFrqDay; 					// autoWired
	protected Intbox 		numOfTerms; 					// autoWired
	protected Row 			firstDateRow; 					// autowired
	protected Row 			numOfTermsRow; 					// autowired
	protected Row 			frqRow; 						// autowired

	// not auto wired vars
	private FinScheduleData finScheduleData; 				// overhanded per param
	private FinanceScheduleDetail financeScheduleDetail; 	// overhanded per param
	private transient WIFFinanceMainDialogCtrl wIFFinanceMainDialogCtrl;
	private transient FinanceMainDialogCtrl financeMainDialogCtrl;
	
	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient boolean 		validationOn;
	
	/**
	 * default constructor.<br>
	 */
	public SubscheduleDialogCtrl() {
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
	public void onCreate$window_SubScheduleDialog(Event event) throws Exception {
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
		if (args.containsKey("wIFFinanceMainDialogCtrl")) {
			setwIFFinanceMainDialogCtrl((WIFFinanceMainDialogCtrl) args
					.get("wIFFinanceMainDialogCtrl"));
		} else {
			setwIFFinanceMainDialogCtrl(null);
		}
		
		if (args.containsKey("financeMainDialogCtrl")) {
			setFinanceMainDialogCtrl((FinanceMainDialogCtrl) args
					.get("financeMainDialogCtrl"));
		} else {
			setFinanceMainDialogCtrl(null);
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
			setDialog(this.window_SubScheduleDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	/**
	 * @return the financeScheduleDetail
	 */
	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	/**
	 * @param financeScheduleDetail the financeScheduleDetail to set
	 */
	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	/**
	 * @return the wIFFinanceMainDialogCtrl
	 */
	public WIFFinanceMainDialogCtrl getwIFFinanceMainDialogCtrl() {
		return wIFFinanceMainDialogCtrl;
	}

	/**
	 * @param wIFFinanceMainDialogCtrl the wIFFinanceMainDialogCtrl to set
	 */
	public void setwIFFinanceMainDialogCtrl(
			WIFFinanceMainDialogCtrl wIFFinanceMainDialogCtrl) {
		this.wIFFinanceMainDialogCtrl = wIFFinanceMainDialogCtrl;
	}
	
	/**
	 * @return the financeMainDialogCtrl
	 */
	public FinanceMainDialogCtrl getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	/**
	 * @param financeMainDialogCtrl the financeMainDialogCtrl to set
	 */
	public void setFinanceMainDialogCtrl(FinanceMainDialogCtrl financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	/**
	 * @return the validationOn
	 */
	public boolean isValidationOn() {
		return validationOn;
	}

	/**
	 * @param validationOn the validationOn to set
	 */
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.numOfTerms.setMaxlength(3);
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
		// Fill Depreciation Frequency Code, Month, Day codes
		clearField(this.cbTermFrqCode);
		fillFrqCode(this.cbTermFrqCode, "", isReadOnly("FinanceMainDialog_depreciationFrq"));
		clearField(this.cbTermFrqMth);
		fillFrqMth(this.cbTermFrqMth, "", isReadOnly("FinanceMainDialog_depreciationFrq"));
		clearField(this.cbTermFrqDay);
		fillFrqDay(this.cbTermFrqDay, "", isReadOnly("FinanceMainDialog_depreciationFrq"));

		logger.debug("Entering");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceMain
	 */
	public void doWriteComponentsToBean() {
		logger.debug("Entering");
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			this.numOfTerms.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try{
			Date newSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(this.firstDate.getValue(),  PennantConstants.dateFormat));
			if(newSchdDate.compareTo(getFinScheduleData().getFinanceMain().getMaturityDate()) <=0){
				throw new WrongValueException(this.firstDate, Labels.getLabel("DATE_ALLOWED_AFTER",
						new String[] { Labels.getLabel("label_SubScheduleDialog_firstDate.value"),
						DateUtility.formatUtilDate(getFinScheduleData().getFinanceMain().getMaturityDate(),  PennantConstants.dateFormat)}));
			}
			
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(isValidComboValue(this.cbTermFrqCode,Labels.getLabel("label_FrqCode.value"))){
				if(isValidComboValue(this.cbTermFrqMth,Labels.getLabel("label_FrqMth.value"))){
					if(isValidComboValue(this.cbTermFrqDay,Labels.getLabel("label_FrqDay.value"))){
						this.termFrq.getValue();
					}
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
		setFinScheduleData(ScheduleCalculator.addSubSchedule(getFinScheduleData(),this.numOfTerms.getValue(),
				this.firstDate.getValue(), this.termFrq.getValue()));
		getFinScheduleData().setSchduleGenerated(true);
		if(getFinanceMainDialogCtrl()!=null){
			this.financeMainDialogCtrl.doFillScheduleList(getFinScheduleData(), null);
		}else {
			this.wIFFinanceMainDialogCtrl.doFillScheduleList(getFinScheduleData());
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
			this.numOfTerms.setConstraint(new IntValidator(numOfTerms.getMaxlength(), Labels.getLabel("label_SubScheduleDialog_Terms.value"), false));
		}
		this.firstDate.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
				Labels.getLabel("label_SubScheduleDialog_firstDate.value")}));
		logger.debug("Leaving");
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


	/**
	 * when the "AddSubSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddSubSchedule(Event event) throws InterruptedException {
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
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when close event is occurred. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * */
	public void onClose(Event event)throws InterruptedException{
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
		
	}

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
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
			this.window_SubScheduleDialog.onClose();
		}
		logger.debug("Leaving");
	}

	private void doClearMessage() {
		logger.debug("Entering");
		setValidationOn(false);
		this.numOfTerms.clearErrorMessage();
		this.termFrq.clearErrorMessage();
		this.firstDate.clearErrorMessage();
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

		/*if (this.oldVar_termFrq != this.termFrq.getValue()) {
			return true;
		}
		if(this.firstDate.getValue() != null){
			if(DateUtility.compare(this.oldVar_firstDate, this.firstDate.getValue())!=0) {
				return true;
			}
		}
		if (this.oldVar_numOfTerms != this.numOfTerms.getValue()) {
			return true;
		}*/
		logger.debug("Leaving");
		return false;
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
	public void fillSchFromDates(Combobox dateCombobox,
			List<FinanceScheduleDetail> financeScheduleDetails) {
		logger.debug("Entering");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		dateCombobox.appendChild(comboitem);
		dateCombobox.setSelectedItem(comboitem);
		if (financeScheduleDetails != null) {
			for (int i = 0; i < financeScheduleDetails.size(); i++) {
				if (financeScheduleDetails.get(i).isRepayOnSchDate() &&
						!financeScheduleDetails.get(i).getSpecifier().equals(CalculationConstants.MATURITY)) {
					comboitem = new Comboitem();
					comboitem.setLabel(PennantAppUtil.formateDate(
							financeScheduleDetails.get(i).getSchDate(),
							PennantConstants.dateFormate));
					comboitem.setValue(financeScheduleDetails.get(i).getSchDate());
					dateCombobox.appendChild(comboitem);
					if (getFinanceScheduleDetail() != null
							&& financeScheduleDetails.get(i).getSchDate()
							.equals(getFinanceScheduleDetail().getSchDate())) {
						dateCombobox.setSelectedItem(comboitem);
					}
				}

			}
		}
		logger.debug("Leaving");
	}
	
	// Default Frequency Code comboBox change	
	public void onSelect$cbTermFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode 		=	getComboboxValue(this.cbTermFrqCode);

		onSelectFrqCode(frqCode,this.cbTermFrqCode,this.cbTermFrqMth,this.cbTermFrqDay,this.termFrq,isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));		
		logger.debug("Leaving"+event.toString());  
	}		
	public void onSelect$cbTermFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode 		= 	getComboboxValue(this.cbTermFrqCode);
		String frqMth 		=	getComboboxValue(this.cbTermFrqMth);

		onSelectFrqMth(frqCode,frqMth,this.cbTermFrqMth,this.cbTermFrqDay,this.termFrq,isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving"+event.toString());
	}
	public void onSelect$cbTermFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode 		= 	getComboboxValue(this.cbTermFrqCode);
		String frqMth		= 	getComboboxValue(this.cbTermFrqMth);
		String frqDay	 	= 	getComboboxValue(this.cbTermFrqDay);

		onSelectFrqDay(frqCode,frqMth,frqDay,this.termFrq);
		logger.debug("Leaving"+event.toString());
	}
	
	private String getComboboxValue(Combobox combobox){
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}
	
}
