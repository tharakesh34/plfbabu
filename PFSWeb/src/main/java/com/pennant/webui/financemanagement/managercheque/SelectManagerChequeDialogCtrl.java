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
 *																							*
 * FileName    		:  FinanceMainQDEDialogCtrl.java                                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-11-2011    														*
 *                                                                  						*
 * Modified Date    :  16-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.financemanagement.managercheque;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.search.Filter;
import com.pennant.webui.financemanagement.managercheque.ManagerChequeListCtrl.MCType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/FinanceManagement/ManagerCheque/SelectManagerChequeTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SelectManagerChequeDialogCtrl extends GFCBaseCtrl<FinanceDetail> {


	private static final long serialVersionUID = 8556168885363682933L;
	private static final Logger logger = Logger.getLogger(SelectManagerChequeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWiredd by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window       			window_SelectManagerChequeTypeDialog;
	protected ExtendedCombobox      finaceRef;                             
	protected Button       			btnProceed;
	protected Row 					row_finReference;

	protected Radiogroup radio_ManagerChequeType;
	protected Radio 	 financeManagerChq;
	protected Radio      nonFinanceManagerChq;
	
	protected ManagerCheque managerCheque;
	private FinanceMain financeMain;
	protected ManagerChequeListCtrl managerChequeListCtrl;
	private MCType mcType = null;

	private boolean managerChequeType = true;
	
	/**
	 * default constructor.<br>
	 */
	public SelectManagerChequeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_SelectManagerChequeTypeDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_SelectManagerChequeTypeDialog);
				
		this.managerCheque = (ManagerCheque) arguments.get("managerCheque");
		this.financeMain = (FinanceMain) arguments.get("financeMain");
		this.managerChequeListCtrl = (ManagerChequeListCtrl) arguments.get("managerChequeListCtrl");
		this.enqiryModule = (Boolean) arguments.get("enqiryModule");
		this.mcType = (MCType) arguments.get("mcType");
		
		doSetFieldProperties();
		showSelectFinanceTypeDialog();
		
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void onCheck$radio_ManagerChequeType(Event event){
		logger.debug("Entering");

		doClearMessage();
		this.finaceRef.setValue("");
		this.finaceRef.setDescription("");
		if(this.financeManagerChq.isChecked()){
			this.finaceRef.setMandatoryStyle(true);
			this.row_finReference.setVisible(true);
			this.managerChequeType = true;
		}else if(this.nonFinanceManagerChq.isChecked()){
			this.finaceRef.setMandatoryStyle(false);
			this.row_finReference.setVisible(false);
			this.managerChequeType = false;
		}
		
		logger.debug("Leaving" );
	}

	public void onFulfill$finaceRef(Event event){
		logger.debug("Entering" + event.toString());
		
		doClearMessage();
		Object dataObject = finaceRef.getObject();
		if (dataObject instanceof String) {
			this.finaceRef.setValue(dataObject.toString());
			this.finaceRef.setDescription("");
		} else {
			financeMain = (FinanceMain) dataObject;
			if (financeMain != null) {
				this.finaceRef.setValue(financeMain.getFinReference());
				this.finaceRef.setDescription(financeMain.getFinType());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * When user clicks on button "btnProceed" button
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnProceed(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		doSetValidation();
		this.window_SelectManagerChequeTypeDialog.onClose();

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("enqiryModule", enqiryModule);
		map.put("managerChequeType", managerChequeType);
		map.put("financeMain", financeMain);
		map.put("managerCheque", managerCheque);
		map.put("managerChequeListCtrl", this.managerChequeListCtrl);
		map.put("mcType", mcType);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/FinanceManagement/ManagerCheque/ManagerChequeDialog.zul",null,map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI Process++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //


	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		this.finaceRef.setTextBoxWidth(150);
		this.finaceRef.setMandatoryStyle(true);
		this.finaceRef.setModuleName("MGRCHQFinanceMain");
		this.finaceRef.setValueColumn("FinReference");
		this.finaceRef.setDescColumn("FinType");
		this.finaceRef.setValidateColumns(new String[] { "FinReference" });
 		Filter[] filters = new Filter[1];
 		filters[0]= new Filter("FinIsActive", 1, Filter.OP_EQUAL);
 		this.finaceRef.setFilters(filters);
 		
 		logger.debug("Leaving");
	}


	/**
	 * Opens the SelectFinanceTypeDialog window modal.
	 */
	private void showSelectFinanceTypeDialog() throws InterruptedException {
		logger.debug("Entering");
		try {
			// open the dialog in modal mode
			this.window_SelectManagerChequeTypeDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");

		this.finaceRef.setErrorMessage("");

		if(this.financeManagerChq.isChecked()){
			if(StringUtils.isEmpty(this.finaceRef.getValue())) {
				throw new WrongValueException(this.finaceRef, Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_ManagerChequeDialog_FinaceRef.value") }));
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		Clients.clearWrongValue(this.finaceRef);
		this.finaceRef.setConstraint("");
		this.finaceRef.setErrorMessage("");

		logger.debug("Leaving");
	}
	
}
