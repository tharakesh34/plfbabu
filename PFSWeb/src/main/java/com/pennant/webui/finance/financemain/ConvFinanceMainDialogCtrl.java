/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright hvaer, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  ConvFinanceMainDialogCtrl.java                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file.
 */
public class ConvFinanceMainDialogCtrl extends FinanceMainBaseCtrl {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(ConvFinanceMainDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_ConvFinanceMainDialog; 					// autoWired

	/**
	 * default constructor.<br>
	 */
	public ConvFinanceMainDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.doSetProperties();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ConvFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ConvFinanceMainDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			setFinanceDetail((FinanceDetail) arguments.get("financeDetail"));
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(getFinanceDetail());
			old_NextRoleCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getNextRoleCode();
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (arguments.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) arguments.get("financeMainListCtrl"));
		} 
		
		if (arguments.containsKey("financeSelectCtrl")) {
			setFinanceSelectCtrl((FinanceSelectCtrl) arguments.get("financeSelectCtrl"));
		} 

		if (arguments.containsKey("tabbox")) {
			listWindowTab = (Tab) arguments.get("tabbox");
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.containsKey("eventCode")) {
			eventCode = (String) arguments.get("eventCode");
		}
		
		if (arguments.containsKey("menuItemRightName")) {
			menuItemRightName = (String) arguments.get("menuItemRightName");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain);

		if (isWorkFlowEnabled()) {
			String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
			if(recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)){
				this.userAction = setRejectRecordStatus(this.userAction);
			}else {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateMenuRoleAuthorities(getRole(), super.pageRightName, menuItemRightName);	
			}
		}else{
			this.south.setHeight("0px");
		}

		setMainWindow(window_ConvFinanceMainDialog);
		setProductCode("Conv");
		
		/* set components visible dependent of the users rights */
		doCheckRights();


		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52+ "px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceDetail());
					
		Events.echoEvent("onPostWinCreation", this.self, null);
		logger.debug("Leaving " + event.toString());
	}
	
	@Override
	protected void doShowDialog(FinanceDetail financeDetail) throws InterruptedException {
		super.doShowDialog(financeDetail);
	}
	
	@Override
	protected void doEdit() {
		super.doEdit();
	}
	
	@Override
	protected void doReadOnly() {
		super.doReadOnly();
	}
	
	@Override
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException,
	InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		super.doWriteBeanToComponents(aFinanceDetail, onLoadProcess);
	}
	
	@Override
	public void doSave() throws Exception {
		super.doSave();
		
	}
	
	
	@Override
	protected void doSetValidation() {
		super.doSetValidation();
		
	}
	
	@Override
	protected void doSetLOVValidation() {
		super.doSetLOVValidation();
		
	}
	
	@Override
	protected void doRemoveLOVValidation() {
		super.doRemoveLOVValidation();
		
	}
	
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_ConvFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		String prevRecordStatus = getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus();
		String recordStatus = userAction.getSelectedItem().getValue();

		if (!PennantConstants.RCD_STATUS_REJECTED.equals(prevRecordStatus)
				&& (PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus))
				&& StringUtils.isEmpty(moduleDefiner)) {
			boolean allow = DisbursementInstCtrl.allowReject(getFinanceDetail().getAdvancePaymentsList());
			if (!allow) {
				MessageUtil.showMessage(Labels.getLabel("label_Finance_QuickDisb_Cancelled"));
				return;
			} 
		} 
		
		int  capturereaonse = 0;
		
		if (!PennantConstants.RCD_STATUS_REJECTED.equals(prevRecordStatus)
				&& (PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus))
				&& StringUtils.isEmpty(moduleDefiner)) {
			capturereaonse=2;//reject
		}else if (StringUtils.equalsIgnoreCase("HOLD", recordStatus))  {
			capturereaonse=1;//hold
		}
		
		
		if (capturereaonse!=0) {
			doFillReasons(capturereaonse);
		} else {
			doSave();
		}
		
		logger.debug(Literal.LEAVING);
	}


	public void doFillReasons(int reason) throws InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("reason", reason);
		try{
			Executions.createComponents("/WEB-INF/pages/ReasonDetail/ReasonDetails.zul", window_ConvFinanceMainDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		MessageUtil.showHelpWindow(event, window_ConvFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error("Exception: ", e);
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onCheck$manualSchedule(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		super.onCheckmanualSchedule();
		logger.debug("Leaving " + event.toString());
	}
	
}