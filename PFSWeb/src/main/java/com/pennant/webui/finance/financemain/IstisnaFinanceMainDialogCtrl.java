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
 * FileName    		:  IstisnaFinanceMainDialogCtrl.java                                                   * 	  
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/IstisnaFinanceMainDialog.zul file.
 */
public class IstisnaFinanceMainDialogCtrl extends FinanceMainBaseCtrl {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = Logger.getLogger(IstisnaFinanceMainDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_IstisnaFinanceMainDialog; 				// autoWired

	/**
	 * default constructor.<br>
	 */
	public IstisnaFinanceMainDialogCtrl() {
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
	public void onCreate$window_IstisnaFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_IstisnaFinanceMainDialog);

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
		
		setMainWindow(window_IstisnaFinanceMainDialog);
		setProductCode("Istisna");
		
		/* set components visible dependent of the users rights */
		doCheckRights();
		
		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52+ "px");
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getFinanceDetail());
		Events.echoEvent("onPostWinCreation", this.self, null);
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_IstisnaFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		String recStatus = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		
		if(this.userAction.getSelectedItem() != null && !recStatus.equals(PennantConstants.RCD_STATUS_REJECTED) &&
				(this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED) ||
				this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_CANCELLED)) && StringUtils.isEmpty(moduleDefiner)){
		   doReject();
		}else{
		   doSave();
		}
		logger.debug("Leaving " + event.toString());
	}
	/**
	 * When  record is rejected . <br>
	 * 
	 */
	
	public void doReject() throws InterruptedException{
		logger.debug("Entering");
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMain", getFinanceDetail().getFinScheduleData().getFinanceMain());
		map.put("financeMainDialogCtrl", this);
		try{
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceReject.zul",
					window_IstisnaFinanceMainDialog, map);
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
		MessageUtil.showHelpWindow(event, window_IstisnaFinanceMainDialog);
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

	/**
	 * Method for Rest Total Contract Advance after Disbursements Added
	 * @param contractAdv
	 */
	public void setFinAmount(BigDecimal contractAdv, Date startDate){
		getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(contractAdv);
		this.finAmount.setValue(PennantAppUtil.formateAmount(contractAdv,
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
		
		if(contractAdv.compareTo(BigDecimal.ZERO) > 0){
			this.finStartDate.setDisabled(true);
			this.gracePeriodEndDate.setDisabled(true);
			this.finCcy.setReadonly(true);
		}else{
			this.finStartDate.setDisabled(false);
			this.gracePeriodEndDate.setDisabled(false);
			this.finCcy.setReadonly(false);
		}
		
		if(startDate == null){
			startDate = DateUtility.getAppDate();
		}
		this.finStartDate.setValue(startDate);
		setNetFinanceAmount(true);
	}
	
	public List<Object> doValidateFinDetail(){
		logger.debug("Entering");
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(StringUtils.isBlank(this.finCcy.getValue())){
				throw new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_EMPTY", 
						new String[]{Labels.getLabel("label_FinanceMainDialog_FinCcy.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.finStartDate.getValue() == null){
				throw new WrongValueException(this.gracePeriodEndDate_two, Labels.getLabel("FIELD_NO_EMPTY", 
						new String[]{Labels.getLabel("label_FinanceMainDialog_FinStartDate.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {//Must be greater than
			if (this.gracePeriodEndDate_two.getValue() == null && this.gracePeriodEndDate.getValue() == null) {
				throw new WrongValueException(this.gracePeriodEndDate_two,  Labels.getLabel("MAND_FIELD_COMPARE", 
						new String[]{Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
						Labels.getLabel("label_FinanceMainDialog_FinStartDate.value")}));
			}
			
			if(this.gracePeriodEndDate.getValue() != null && this.gracePeriodEndDate.getValue().compareTo(this.finStartDate.getValue()) <= 0){
				throw new WrongValueException(this.gracePeriodEndDate_two,  Labels.getLabel("MAND_FIELD_COMPARE", 
						new String[]{Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
						Labels.getLabel("label_FinanceMainDialog_FinStartDate.value")}));
			}
			
			if(this.gracePeriodEndDate.getValue() == null){
				if(this.gracePeriodEndDate_two.getValue() != null && 
						this.gracePeriodEndDate_two.getValue().compareTo(this.finStartDate.getValue()) <= 0){
					throw new WrongValueException(this.gracePeriodEndDate_two, Labels.getLabel("MAND_FIELD_COMPARE", 
							new String[]{Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
							Labels.getLabel("label_FinanceMainDialog_FinStartDate.value")}));
				}
			}
			if(this.gracePeriodEndDate.getValue() != null){
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, this.financeTypeDetailsTab);
		
		List<Object> list = new ArrayList<Object>();
		list.add(this.finCcy.getValue());
		list.add(this.finStartDate.getValue());
		list.add(this.gracePeriodEndDate_two.getValue());
		
		logger.debug("Leaving");
		return list;
	}

	/**
	 * When user leave grace period end date component
	 */
	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());

		if(this.graceTerms_Two.intValue() == 0 && 
				(this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null)){

			if(this.gracePeriodEndDate.getValue() != null){
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
			if(this.nextGrcPftDate.getValue() == null){
				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1,
						this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, 
						getFinanceDetail().getFinScheduleData().getFinanceType().getFddLockPeriod()).getNextFrequencyDate());
			}
			if(this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0){
				this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
						this.nextGrcPftDate_two.getValue(), this.gracePeriodEndDate_two.getValue(), false, true).getTerms());
			}else if(this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0){
				this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
						this.nextGrcPftDate_two.getValue(), this.gracePeriodEndDate_two.getValue(), true, true).getTerms());
			}

			this.graceTerms.setText("");
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onCheck$manualSchedule(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		super.onCheckmanualSchedule();
		logger.debug("Leaving " + event.toString());
	}
	
}