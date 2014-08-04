package com.pennant.webui.finance.financemain;

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
 * FileName    		:  FinanceRejectCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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



import java.io.Serializable;

import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;

import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.rits.cloning.Cloner;



/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * com.pennant.webui.finance.financemain;
 * /WEB-INF/pages/Finance/FinanceMain/financeReject.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceRejectCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -210929672381582779L;
	private final static Logger logger = Logger.getLogger(FinanceRejectCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting auto wired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceRejectDialog; 		// autoWired

	protected Combobox 		financeRejectStatus; 				// autoWired
	protected Textbox 		remarks; 			// autoWired
	// Button controller for the CRUD buttons
	protected Button btnSave; 		// autoWired
	protected Button btnClose; 		// autoWired

	private transient FinanceMain financeMain; 
	private Object financeMainDialogCtrl = null;
	/**
	 * default constructor.<br>
	 */
	public FinanceRejectCtrl() {
		super();
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Reject object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceRejectDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("financeMain")) {
			this.financeMain=(FinanceMain)args.get("financeMain");
		}
		if (args.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl=(Object)args.get("financeMainDialogCtrl");
		}
		doShowDialog(getFinanceMain());		 
		logger.debug("Leaving" + event.toString());
	}
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
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
	public void doShowDialog(FinanceMain aFinanceMain) throws InterruptedException {
		setFinanceMain(aFinanceMain);
		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceMain);
			this.window_FinanceRejectDialog.setHeight(Integer.parseInt(getBorderLayoutHeight().substring(0,getBorderLayoutHeight().indexOf("px"))) - 150 + "px");
			this.window_FinanceRejectDialog.doModal();
		}catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}
	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param 
	 *            
	 */
	public void doWriteBeanToComponents(FinanceMain aFinanceMain) {
		logger.debug("Entering ");
		fillComboBox(this.financeRejectStatus,aFinanceMain.getFinStatus(), PennantAppUtil.getRejectCodes(),"");
		if(aFinanceMain!=null){
			this.remarks.setValue(aFinanceMain.getFinRemarks());
		}
		logger.debug("Leaving ");
	}
	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param 
	 */
	public void doWriteComponentsToBean(FinanceMain aFinanceMain) {
		logger.debug("Entering ");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try{
			if(getComboboxValue(this.financeRejectStatus).equals("#")){
				throw new WrongValueException(this.financeRejectStatus, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceRejectStatusDialog_FinanceRejectStatus.value") }));
			}
			else{
				aFinanceMain.setFinStatus(this.financeRejectStatus.getSelectedItem().getValue().toString());
			}}catch (WrongValueException we ) {
				wve.add(we);
			}
			try{ 
				aFinanceMain.setFinRemarks(this.remarks.getValue());

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
			setFinanceMain(aFinanceMain);
			logger.debug("Leaving ");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		if (this.remarks.isVisible()){
			this.remarks.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY"
					,new String[]{Labels.getLabel("label_FinanceRejectStatusDialog_FinanceRemarks.value")}));
		}
		logger.debug("Leaving ");
	}
	/**
	 * Removes the Validation by setting the  constraints to the empty.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		this.financeRejectStatus.setConstraint("");
		this.remarks.setConstraint("");
		logger.debug("Leaving ");
	}
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");
		FinanceMain aFinanceMain = new FinanceMain();
		Cloner cloner = new Cloner();
		aFinanceMain = cloner.deepClone(getFinanceMain());
		doSetValidation();
		doWriteComponentsToBean(aFinanceMain);
		try {
			getFinanceMainDialogCtrl().getClass().getMethod("updateFinanceMain", aFinanceMain.getClass()).invoke(financeMainDialogCtrl, getFinanceMain());
			getFinanceMainDialogCtrl().getClass().getMethod("doSave").invoke(getFinanceMainDialogCtrl());
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.window_FinanceRejectDialog.onClose();
		logger.debug("Leaving");
	}
	public void doClose(){
		logger.debug("Entering ");
		this.window_FinanceRejectDialog.onClose();
		logger.debug("Leaving");
	}
	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}
	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	
}
