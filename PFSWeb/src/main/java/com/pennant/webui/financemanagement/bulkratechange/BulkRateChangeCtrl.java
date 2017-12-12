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
 * FileName    		:  BulkRateChangeCtrl.java                          		            * 	  
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
package com.pennant.webui.financemanagement.bulkratechange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class BulkRateChangeCtrl extends GFCBaseCtrl<BulkRateChangeHeader> {
	private static final long serialVersionUID = -4578996988245614938L;
	private static final Logger logger = Logger.getLogger(BulkRateChangeCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RateChangeDialog; 					
	protected Textbox reference;  								
	protected Datebox fromDate;  								
	protected Datebox toDate;  								
	protected Combobox reCalType; 							
	protected Decimalbox rateChange; 							

	private transient BulkRateChangeDialogCtrl bulkRateChangeDialogCtrl;
	private transient BulkRateChangeHeader bulkRateChangeHeader;

	private transient boolean validationOn;
	final List<ValueLabel>	      recalTypes  = PennantStaticListUtil.getSchCalCodes();

	//private boolean 		isApplyRateChangeWin = false;

		/**
		 * default constructor.<br>
		 */
		public BulkRateChangeCtrl() {
			super();
		}

		// Component Events
	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected BulkRateChangeHeader object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RateChangeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RateChangeDialog);

		try {

			if (arguments.containsKey("bulkRateChangeHeader")) {
				setBulkRateChangeHeader((BulkRateChangeHeader) arguments.get("bulkRateChangeHeader"));
			} else {
				setBulkRateChangeHeader(null);
			}

			if (arguments.containsKey("bulkRateChangeDialogCtrl")) {
				setBulkRateChangeDialogCtrl((BulkRateChangeDialogCtrl) arguments.get("bulkRateChangeDialogCtrl"));
			} else {
				setBulkRateChangeDialogCtrl(null);
			}

			/*if (args.containsKey("isApplyRateChangeWin")) {
				isApplyRateChangeWin = (Boolean) args.get("isApplyRateChangeWin");
			} else {
				isApplyRateChangeWin = false;
			}*/

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getBulkRateChangeHeader());
			this.window_RateChangeDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RateChangeDialog.onClose();
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aBulkRateChangeHeader
	 * @throws Exception
	 */
	public void doShowDialog(BulkRateChangeHeader aBulkRateChangeHeader) throws Exception {
		logger.debug("Entering");
		try {
			this.rateChange.focus();
			// fill the components with the data
			doWriteBeanToComponents(aBulkRateChangeHeader);

			//Components DiaplayMode
			doReadOnly();

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_RateChangeDialog.onClose();
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
		this.fromDate.setFormat(PennantConstants.dateFormat);		
		this.rateChange.setMaxlength(13);
		this.rateChange.setFormat(PennantConstants.rateFormate9);
		this.rateChange.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rateChange.setScale(9);
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		if (getBulkRateChangeHeader().isNewRecord()) {
			this.reference.setReadonly(false);
		} else {
			this.reference.setReadonly(true);
		}

		this.fromDate.setReadonly(true);
		this.toDate.setReadonly(true);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aBulkRateChangeHeader
	 *            BulkRateChangeHeader
	 */
	public void doWriteBeanToComponents(BulkRateChangeHeader aBulkRateChangeHeader) {
		logger.debug("Entering");

		if(StringUtils.isBlank(getBulkRateChangeHeader().getReCalType())) {
			fillComboBox(this.reCalType, "TILLMDT", recalTypes, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,STEPPOS,");
		} else {
			fillComboBox(this.reCalType, getBulkRateChangeHeader().getReCalType(), recalTypes, ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,STEPPOS,");
		}
		this.reference.setValue(aBulkRateChangeHeader.getBulkRateChangeRef());
		this.fromDate.setValue(getBulkRateChangeHeader().getFromDate());
		this.rateChange.setValue(aBulkRateChangeHeader.getRateChange());

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBulkRateChangeHeader
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean() throws InterruptedException {
		logger.debug("Entering");

		//doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Bulk Rate Reference
		try {
			getBulkRateChangeHeader().setBulkRateChangeRef(this.reference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//From Date
		try {
			getBulkRateChangeHeader().setFromDate(this.fromDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//To Date
		try {
			getBulkRateChangeHeader().setToDate(this.toDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Rate Change
		try {
			getBulkRateChangeHeader().setRateChange(this.rateChange.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Recalculation Type
		try {
			String strReCalType = null;
			if (this.reCalType.getSelectedItem() != null) {
				strReCalType = this.reCalType.getSelectedItem().getValue().toString();
			}
			if (strReCalType != null && !PennantConstants.List_Select.equals(strReCalType)) {
				getBulkRateChangeHeader().setReCalType(strReCalType);
				getBulkRateChangeHeader().setLovDescReCalType(PennantStaticListUtil.getlabelDesc(strReCalType, recalTypes));
			} else {
				getBulkRateChangeHeader().setReCalType(null);
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		this.window_RateChangeDialog.onClose();

		logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		setValidationOn(true);


		if (!this.fromDate.isReadonly()) {
			this.fromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_FromDate.value"), true));
		}

		if (!this.toDate.isReadonly()) {
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_ToDate.value"), true));
		}
		if (!this.rateChange.isReadonly()) {
			this.rateChange.setConstraint(new PTDecimalValidator(Labels.getLabel("label_RateChangeDialog_Rate.value"), 	9, true, true, 0, 9999)); //TODO
		}

		if (!this.reCalType.isDisabled()){
			this.reCalType.setConstraint(new StaticListValidator(recalTypes, Labels.getLabel("label_RateChangeDialog_RecalType.value")));  
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Removes the Validation by setting the  constraints to the empty.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");

		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.rateChange.setConstraint("");
		this.reCalType.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages
	 * 
	 * */
	protected void doClearMessage() {
		logger.debug("Entering");

		setValidationOn(false);
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.rateChange.clearErrorMessage();
		this.reCalType.clearErrorMessage();

		logger.debug("Leaving");
	}

	/**
	 * when the "Apply" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddRateChange(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if (getBulkRateChangeHeader() != null) {
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

	/**
	 * Saves the components. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");

		doSetValidation();
		doWriteComponentsToBean();
		getBulkRateChangeDialogCtrl().fillRateChangeDetails(getBulkRateChangeHeader());
		try {
			getBulkRateChangeDialogCtrl().calculateNewRateAndNewProfit(getBulkRateChangeHeader(), getBulkRateChangeHeader().getBulkRateChangeDetailsList(), null);
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug("Leaving");
	}


	//	getter / setter

	public boolean isValidationOn() {
		return validationOn;
	}
	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public BulkRateChangeDialogCtrl getBulkRateChangeDialogCtrl() {
		return bulkRateChangeDialogCtrl;
	}

	public void setBulkRateChangeDialogCtrl(BulkRateChangeDialogCtrl bulkRateChangeDialogCtrl) {
		this.bulkRateChangeDialogCtrl = bulkRateChangeDialogCtrl;
	}

	public BulkRateChangeHeader getBulkRateChangeHeader() {
		return bulkRateChangeHeader;
	}

	public void setBulkRateChangeHeader(BulkRateChangeHeader bulkRateChangeHeader) {
		this.bulkRateChangeHeader = bulkRateChangeHeader;
	}
}
