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
 * FileName    		:  BulkDefermentChangeCtrl.java                           
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
package com.pennant.webui.financemanagement.bulkdeferment;

import java.lang.reflect.InvocationTargetException;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.webui.finance.enquiry.model.BulkDefermentChangeListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the 
 * WEB-INF/pages/FinanceManagement/SchdlRepayment/SchdlRepaymentDialog.zul
 */
public class BulkDefermentChangeCtrl extends GFCBaseListCtrl<BulkDefermentChange> {
	private static final long serialVersionUID = 966281186831332116L;
	private static final Logger logger = Logger.getLogger(BulkDefermentChangeCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_BulkDefferment; 			// autowired
	protected Borderlayout borderlayout_BulkDeferment;	// autowired
	protected Listbox listBoxDefermentFinance;			// autowired
	protected Grid grid_BulkDeferment;					// autowired

	protected Datebox fromDate;  						// autowired
	protected Datebox toDate;  							// autowired
	protected Combobox cbReCalType; 					// autowired
	protected Datebox calFromDate; 	 					// autowired
	protected Datebox calToDate; 	 					// autowired
	protected Checkbox exDefDate; 						// autowired
	protected Combobox cbAddTermAfter; 					// autowired

	protected Row recalFromDateRow;						// autowired
	protected Row excDefDateRow;						// autowired
	protected Row addTermRow;							// autowired

	protected Button btnPreview;
	protected Button btnProceed;

	private FinanceDetailService financeDetailService;
	private List<BulkDefermentChange> defermentChangeFinances;

	/**
	 * default constructor.<br>
	 */
	public BulkDefermentChangeCtrl() {
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
	public void onCreate$window_BulkDefferment(Event event) throws Exception {
		logger.debug("Entering"+event.toString());	

		this.borderlayout_BulkDeferment.setHeight(getBorderLayoutHeight());
		this.listBoxDefermentFinance.setHeight(getListBoxHeight(grid_BulkDeferment.getRows().getVisibleItemCount()));

		doRemoveValidation();
		doClearMessage();
		fillComboBox(this.cbReCalType, "", PennantStaticListUtil.getSchCalCodes(), ",CURPRD,ADDLAST,STEPPOS,");

		this.fromDate.setValue(null);
		this.toDate.setValue(null);
		this.cbReCalType.setSelectedIndex(0);
		this.calFromDate.setValue(null);
		this.calToDate.setValue(null);
		this.cbAddTermAfter.setSelectedIndex(0);
		this.exDefDate.setChecked(false);
		this.calFromDate.setValue(null);
		this.calToDate.setValue(null);

		this.fromDate.setDisabled(false);
		this.toDate.setDisabled(false);
		this.cbReCalType.setDisabled(false);
		this.recalFromDateRow.setVisible(false);
		this.excDefDateRow.setVisible(false);
		this.addTermRow.setVisible(false);
		this.calFromDate.setDisabled(false);
		this.calToDate.setDisabled(false);
		this.exDefDate.setDisabled(false);

		doSetFieldProperties();
		this.listBoxDefermentFinance.getItems().clear();

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
		this.fromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.toDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.calFromDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.calToDate.setFormat(DateFormat.SHORT_DATE.getPattern());
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
		MessageUtil.showHelpWindow(event, window_BulkDefferment);
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
		Events.postEvent("onCreate", this.window_BulkDefferment, event);
		this.window_BulkDefferment.invalidate();
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

		setDefermentChangeFinances(getFinanceDetailService().getBulkDefermentFinList(fromDate, toDate));
		getPagedListWrapper().initList(getDefermentChangeFinances(), this.listBoxDefermentFinance, new Paging());
		this.listBoxDefermentFinance.setItemRenderer(new BulkDefermentChangeListModelItemRenderer());
		
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

		try{
			isValidComboValue(this.cbReCalType, Labels.getLabel("label_BulkDefferment_RecalType.value"));
		}catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.calFromDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}


		try {
			this.calToDate.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(this.cbReCalType.getSelectedItem() != null && 
					this.cbReCalType.getSelectedItem().getValue().toString().equals(CalculationConstants.RPYCHG_ADDTERM)){
				isValidComboValue(this.cbAddTermAfter, Labels.getLabel("label_BulkDefferment_AddTermAfter.value"));
			}
		}catch (WrongValueException we) {
			wve.add(we);
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

		String addTermAfter = "";
		if(this.cbAddTermAfter.getSelectedIndex() > 0){
			addTermAfter = this.cbAddTermAfter.getSelectedItem().getValue().toString();
		}

		//Processing Fetch Finance Details for Bulk Deferment Changes
		setDefermentChangeFinances(new ArrayList<BulkDefermentChange>());
		boolean success = getFinanceDetailService().bulkDefermentChanges(getDefermentChangeFinances(), 
				this.cbReCalType.getSelectedItem().getValue().toString(), this.exDefDate.isChecked(),
				addTermAfter, this.calFromDate.getValue(), this.calToDate.getValue());

		//Need to check Process failure case
		if(success){
			MessageUtil.showMessage("Bulk Deferment Application Process Succeed for Finance Count "
					+ getDefermentChangeFinances().size());
		}

		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		if(!this.fromDate.isDisabled()){
			this.fromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_FromDate.value"),true));
		}

		if(!this.toDate.isDisabled()){
			this.toDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_ToDate.value"),true));
		}

		if(this.recalFromDateRow.isVisible()) {

			if(!this.calFromDate.isDisabled()){
				this.calFromDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_CalFromDate.value"),true));
			}

			if(!this.calToDate.isDisabled()){
				this.calToDate.setConstraint(new PTDateValidator(Labels.getLabel("label_BulkDefferment_CalToDate.value"),true));
			}

		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.cbReCalType.setDisabled(true);
		this.fromDate.setDisabled(true);
		this.toDate.setDisabled(true);
		this.calFromDate.setDisabled(true);
		this.calToDate.setDisabled(true);
		this.exDefDate.setDisabled(true);
		this.cbAddTermAfter.setDisabled(true);		
		logger.debug("Leaving");
	}

	/**
	 * Method to Remove Constraints
	 * 
	 * */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.fromDate.setConstraint("");
		this.toDate.setConstraint("");
		this.cbReCalType.setConstraint("");
		this.calFromDate.setConstraint("");
		this.calToDate.setConstraint("");
		this.cbAddTermAfter.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages
	 * 
	 * */
	private void doClearMessage() {
		logger.debug("Entering");
		this.fromDate.clearErrorMessage();
		this.toDate.clearErrorMessage();
		this.cbReCalType.clearErrorMessage();
		this.calFromDate.clearErrorMessage();
		this.calToDate.clearErrorMessage();
		this.cbAddTermAfter.clearErrorMessage();
		logger.debug("Leaving");
	}

	/**
	 * when user changes recalculation type 
	 * @param event
	 * 
	 */
	public void onChange$cbReCalType(Event event) {
		logger.debug("Entering" + event.toString());
		String recalType = this.cbReCalType.getSelectedItem().getValue().toString();
		if(recalType.equals(CalculationConstants.RPYCHG_TILLDATE) || recalType.equals(CalculationConstants.RPYCHG_TILLMDT)) {
			this.recalFromDateRow.setVisible(true);
			this.excDefDateRow.setVisible(true);
			this.addTermRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
			if(recalType.equals(CalculationConstants.RPYCHG_TILLMDT)){
				this.calToDate.setDisabled(true);
			}else {
				this.calToDate.setDisabled(false);
			}
		}else if(recalType.equals(CalculationConstants.RPYCHG_ADDTERM)) {
			this.addTermRow.setVisible(true);
			this.cbAddTermAfter.setSelectedIndex(0);
			this.recalFromDateRow.setVisible(false);
			this.excDefDateRow.setVisible(false);
			this.exDefDate.setChecked(false);
		}else {
			this.recalFromDateRow.setVisible(false);
			this.excDefDateRow.setVisible(false);
			this.exDefDate.setChecked(false);
			this.addTermRow.setVisible(false);
			this.cbAddTermAfter.setSelectedIndex(0);
		}
		logger.debug("Leaving" + event.toString());
	}

	// Getters & Setters

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public List<BulkDefermentChange> getDefermentChangeFinances() {
		return defermentChangeFinances;
	}
	public void setDefermentChangeFinances(
			List<BulkDefermentChange> defermentChangeFinances) {
		this.defermentChangeFinances = defermentChangeFinances;
	}

}