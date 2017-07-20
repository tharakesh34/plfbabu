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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  IjarahBulkRateChangeCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.enquiry.model.BulkRateChangeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;

/**
 * This is the controller class for the 
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul
 */
public class IjarahBulkRateChangeCtrl extends GFCBaseListCtrl<BulkProcessDetails> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(IjarahBulkRateChangeCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	 
		protected Window window_IjaraBulkRateChange; 		// autowired
		protected Borderlayout borderlayout_BulkRate;
		protected Listbox listBoxIjarahFinance;				// autowired
		protected Grid grid_BulkRateChange;					// autowired
		
		protected Decimalbox rateChange; 					// autowired
		protected Datebox fromDate;  						// autowired
		protected Datebox toDate;  							// autowired
		protected Datebox tillDate; 	 					// autowired
		protected Combobox cbReCalType; 					// autowired
		protected Label label_IjaraBulkRateChange_TillDate; // autowired
		protected Hbox hbox_TillDate;						// autowired
		
		protected Button btnPreview;
		protected Button btnProceed;
		
		private FinanceDetailService financeDetailService;
		private List<BulkProcessDetails> rateChangeFinances;
		
	
	/**
	 * default constructor.<br>
	 */
	public IjarahBulkRateChangeCtrl() {
		super();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_IjaraBulkRateChange(Event event) throws Exception {
		logger.debug("Entering"+event.toString());	
		
		this.borderlayout_BulkRate.setHeight(getBorderLayoutHeight());
		this.listBoxIjarahFinance.setHeight(getListBoxHeight(grid_BulkRateChange.getRows().getVisibleItemCount()));
		
		doRemoveValidation();
		doClearMessage();
		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,");
		
		this.fromDate.setValue(null);
		this.toDate.setValue(null);
		this.cbReCalType.setSelectedIndex(0);
		this.rateChange.setText("");		
		
		this.fromDate.setDisabled(false);
		this.toDate.setDisabled(false);
		this.rateChange.setDisabled(false);
		this.hbox_TillDate.setVisible(false);
		this.cbReCalType.setDisabled(false);

		doSetFieldProperties();
		this.listBoxIjarahFinance.getItems().clear();
		this.btnPreview.setDisabled(false);
		this.btnProceed.setDisabled(true);
		
		logger.debug("Leaving" + event.toString());
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
		this.tillDate.setFormat(PennantConstants.dateFormat);
		this.fromDate.setFormat(PennantConstants.dateFormat);
		this.toDate.setFormat(PennantConstants.dateFormat);
		logger.debug("Leaving");
	}


	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_IjaraBulkRateChange);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		Events.postEvent("onCreate", this.window_IjaraBulkRateChange, event);
		this.window_IjaraBulkRateChange.invalidate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for List Preview for Condition List
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnPreview(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		throwValidation();		
		
		Date fromDate = DateUtility.getDBDate(DateUtility.formatDate(
				this.fromDate.getValue(), PennantConstants.DBDateFormat));
		
		Date toDate = DateUtility.getDBDate(DateUtility.formatDate(
				this.toDate.getValue(), PennantConstants.DBDateFormat));
		
		setRateChangeFinances(getFinanceDetailService().getIjaraBulkRateFinList(fromDate, toDate));
		getPagedListWrapper().initList(getRateChangeFinances(), this.listBoxIjarahFinance, new Paging());
		this.listBoxIjarahFinance.setItemRenderer(new BulkRateChangeListModelItemRenderer());
		
		doReadOnly();
		this.btnPreview.setDisabled(true);
		this.btnProceed.setDisabled(false);
		logger.debug("Leaving" +event.toString());
	}
	
	public void throwValidation(){
		logger.debug("Entering");
		
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			this.fromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.toDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.rateChange.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try{
			isValidComboValue(this.cbReCalType, Labels.getLabel("label_IjaraBulkRateChange_RecalType.value"));
		}catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(this.hbox_TillDate.isVisible()){
			try {
				if(this.tillDate.getValue().before(this.fromDate.getValue()) || this.tillDate.getValue().after(this.toDate.getValue())){
					throw new WrongValueException(this.tillDate, Labels.getLabel("DATE_RANGE", new String[]{
						Labels.getLabel("label_IjaraBulkRateChange_TillDate.value"),
						DateUtility.formatToLongDate(this.fromDate.getValue()), 
						DateUtility.formatToLongDate(this.toDate.getValue()) }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		if (wve.size() > 0) {
			doRemoveValidation();
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		//Clear Error Messages
		doClearMessage();
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for List Preview for Condition List
	 * @param event
	 * @throws InterruptedException
	 * @throws AccountNotFoundException 
	 * @throws WrongValueException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnProceed(Event event) throws InterruptedException, WrongValueException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering" +event.toString());
		throwValidation();		
		
		this.btnProceed.setDisabled(true);
		
		//Processing Fetch Finance Details for IJARAH Bulk Rate Changes
		boolean success = getFinanceDetailService().bulkRateChangeFinances(getRateChangeFinances(),
				this.cbReCalType.getSelectedItem().getValue().toString(), this.rateChange.getValue());
		
		//Need to check Process failure case
		if(success){
			MessageUtil.showMessage(
					"Bulk Rate Application Process Succeed for Finance Count " + getRateChangeFinances().size());
		}
		
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if(!this.fromDate.isDisabled()){
			this.fromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_FromDate.value"),true));
		}

		if(!this.toDate.isDisabled()){
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_ToDate.value"),true));
		}

		if(!this.rateChange.isDisabled()){
			this.rateChange.setConstraint(new PTStringValidator(Labels.getLabel("label_IjaraBulkRateChange_Rate.value"),null,true));
		}

		if(this.hbox_TillDate.isVisible()){
			if(!this.tillDate.isDisabled()){
				this.tillDate.setConstraint(new PTDateValidator(Labels.getLabel("label_IjaraBulkRateChange_TillDate.value"),true));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.rateChange.setDisabled(true);
		this.cbReCalType.setDisabled(true);
		this.tillDate.setDisabled(true);
		this.fromDate.setDisabled(true);
		this.toDate.setDisabled(true);
		logger.debug("Leaving");
	}
	
	/**
	 * Method to remove constraints
	 * 
	 * */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.rateChange.setConstraint("");
		this.tillDate.setConstraint("");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.cbReCalType.setConstraint("");
		logger.debug("Leaving");
	}
	/**
	 * Method to clear error messages
	 * 
	 * */
	private void doClearMessage() {
		logger.debug("Entering");
		this.rateChange.clearErrorMessage();
		this.tillDate.clearErrorMessage();
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		logger.debug("Leaving");
	}

	// Getters & Setters

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setRateChangeFinances(List<BulkProcessDetails> rateChangeFinances) {
		this.rateChangeFinances = rateChangeFinances;
	}
	public List<BulkProcessDetails> getRateChangeFinances() {
		return rateChangeFinances;
	}

}