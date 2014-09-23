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
 * FileName    		:  FinanceWriteoffDialogCtrl.java                           
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
package com.pennant.webui.financemanagement.payments;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.South;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.MailUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.FinanceWriteoffHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceWriteoffService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.FinanceSelectCtrl;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * WEB-INF/pages/FinanceManagement/Payments/FinanceWriteoffDialog.zul <br/>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceWriteoffDialogCtrl extends GFCBaseListCtrl<FinanceMain> {

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger.getLogger(FinanceWriteoffDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 			window_FinWriteoffDialog;
	protected Borderlayout		borderlayoutFinWriteoffDialog;

	//Summary Details
	protected Textbox	 	finReference;
	protected Textbox	 	finType;
	protected Textbox	 	finBranch;
	protected Textbox	 	finCcy;
	protected Textbox	 	custID;
	protected Datebox	 	finStartDate;;
	protected Datebox	 	maturityDate;
	protected Datebox	 	writeoffDate;

	protected Decimalbox	label_FinWriteoffDialog_WOPriAmt;
	protected Decimalbox	label_FinWriteoffDialog_WOPftAmt;
	protected Decimalbox	label_FinWriteoffDialog_ODPriAmt;
	protected Decimalbox	label_FinWriteoffDialog_ODPftAmt;
	protected Decimalbox	label_FinWriteoffDialog_UnPaidPriAmt;
	protected Decimalbox	label_FinWriteoffDialog_UnPaidPftAmt;
	protected Decimalbox	label_FinWriteoffDialog_OutStandPrincipal;
	protected Decimalbox	label_FinWriteoffDialog_OutStandProfit;
	protected Decimalbox	label_FinWriteoffDialog_ProvisionAmt;
	protected Decimalbox	label_FinWriteoffDialog_PenaltyAmt;

	protected Decimalbox	writeoffPriAmt;
	protected Decimalbox	writeoffPftAmt;
	protected Decimalbox	adjAmount;
	protected Textbox		remarks;
	
	protected transient Date 		oldVar_writeoffDate;
	protected transient BigDecimal 	oldVar_writeoffPriAmt;
	protected transient BigDecimal 	oldVar_writeoffPftAmt;
	protected transient BigDecimal 	oldVar_adjAmount;
	protected transient String 		oldVar_remarks;

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected South 		south;

	protected Button 		btnWriteoffCal;
	protected Button 		btnWriteoffReCal;
	protected Button 		btnWriteoffPay;
	protected Button 		btnNotes;

	protected Listbox 		listBoxSchedule;
	protected Tab 			finWriteoffTab;
	protected Tab 			finScheduleTab;

	private transient FinanceSelectCtrl financeSelectCtrl = null;
	private FinanceMain financeMain;
	private FinanceWriteoffHeader financeWriteoffHeader;
	private FinanceWriteoffHeader effectFinScheduleData;
	private FinanceWriteoff financeWriteoff;
	private FinanceWriteoffService financeWriteoffService;
	
	private int ccyFormat = 0;
	private String menuItemRightName = null;
	private boolean notes_Entered = false;
	private String moduleDefiner = "";
	private MailUtil mailUtil;
	
	/**
	 * default constructor.<br>
	 */
	public FinanceWriteoffDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinWriteoffDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		try{ 

			// get the parameters map that are over handed by creation.
			final Map<String, Object> args = getCreationArgsMap(event);

			// READ OVERHANDED parameters !
			if (args.containsKey("financeWriteoffHeader")) {
				setFinanceWriteoffHeader((FinanceWriteoffHeader) args.get("financeWriteoffHeader"));
				FinanceMain befImage = new FinanceMain();
				financeMain = getFinanceWriteoffHeader().getFinanceMain();
				financeWriteoff = getFinanceWriteoffHeader().getFinanceWriteoff();

				Cloner cloner = new Cloner();
				befImage = cloner.deepClone(financeMain);
				getFinanceWriteoffHeader().getFinanceMain().setBefImage(befImage);

			}

			if (args.containsKey("moduleDefiner")) {
				moduleDefiner = (String) args.get("moduleDefiner");
			}

			if (args.containsKey("menuItemRightName")) {
				menuItemRightName = (String) args.get("menuItemRightName");
			}

			if (args.containsKey("financeSelectCtrl")) {
				setFinanceSelectCtrl((FinanceSelectCtrl) args.get("financeSelectCtrl"));
			} 

			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				String recStatus = StringUtils.trimToEmpty(financeMain.getRecordStatus());
				if(recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)){
					this.userAction = setRejectRecordStatus(this.userAction);
				}else {
					this.userAction = setListRecordStatus(this.userAction);
					getUserWorkspace().alocateMenuRoleAuthorities(getRole(), "FinWriteoffDialog", menuItemRightName);	
				}
			}else{
				this.south.setHeight("0px");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();

			doEdit();
			if (!financeMain.isNewRecord()) {
				this.btnNotes.setVisible(true);
				this.btnWriteoffCal.setDisabled(true);
			}else{
				this.btnWriteoffReCal.setDisabled(true);
				this.btnWriteoffPay.setDisabled(true);
			}

			doWriteBeanToComponents();

			doStoreInitValues();
			setDialog(this.window_FinWriteoffDialog);
		} catch (Exception e) {
			logger.error(e.getMessage());
			this.window_FinWriteoffDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("FinWriteoffDialog",getRole(), menuItemRightName);

		this.btnWriteoffCal.setVisible(getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffCal"));
		this.btnWriteoffReCal.setVisible(getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setVisible(getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		this.btnWriteoffCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffCal"));
		this.btnWriteoffReCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes

		ccyFormat = financeMain.getLovDescFinFormatter();

		this.finStartDate.setFormat(PennantConstants.dateFormate);
		this.maturityDate.setFormat(PennantConstants.dateFormate);	
		this.writeoffDate.setFormat(PennantConstants.dateFormat);	

		this.writeoffPriAmt.setMaxlength(18);
		this.writeoffPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.writeoffPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffPriAmt.setScale(ccyFormat);

		this.writeoffPftAmt.setMaxlength(18);
		this.writeoffPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.writeoffPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.writeoffPftAmt.setScale(ccyFormat);

		this.adjAmount.setMaxlength(18);
		this.adjAmount.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.adjAmount.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.adjAmount.setScale(ccyFormat);

		this.label_FinWriteoffDialog_WOPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_WOPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOPriAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_WOPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_WOPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_WOPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_WOPftAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_ODPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ODPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_ODPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_ODPriAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_ODPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ODPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_ODPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_ODPftAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_UnPaidPriAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidPriAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_UnPaidPriAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidPriAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_UnPaidPftAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_UnPaidPftAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_UnPaidPftAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_UnPaidPftAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_OutStandPrincipal.setMaxlength(18);
		this.label_FinWriteoffDialog_OutStandPrincipal.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_OutStandPrincipal.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_OutStandPrincipal.setScale(ccyFormat);

		this.label_FinWriteoffDialog_OutStandProfit.setMaxlength(18);
		this.label_FinWriteoffDialog_OutStandProfit.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_OutStandProfit.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_OutStandProfit.setScale(ccyFormat);

		this.label_FinWriteoffDialog_ProvisionAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_ProvisionAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_ProvisionAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_ProvisionAmt.setScale(ccyFormat);

		this.label_FinWriteoffDialog_PenaltyAmt.setMaxlength(18);
		this.label_FinWriteoffDialog_PenaltyAmt.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormat));
		this.label_FinWriteoffDialog_PenaltyAmt.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.label_FinWriteoffDialog_PenaltyAmt.setScale(ccyFormat);

		this.remarks.setMaxlength(200);

		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		this.writeoffDate.setDisabled(isReadOnly("FinWriteoffDialog_writeoffDate"));
		this.writeoffPriAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPriAmt"));
		this.writeoffPftAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPftAmt"));
		this.adjAmount.setDisabled(isReadOnly("FinWriteoffDialog_adjAmount"));
		this.remarks.setReadonly(isReadOnly("FinWriteoffDialog_remarks"));

		logger.debug("Leaving");
	}

	/**
	 * Method to fill finance data.
	 * 
	 * @param isChgRpy
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void doWriteBeanToComponents() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		this.finReference.setValue(financeMain.getFinReference());
		this.finType.setValue(financeMain.getLovDescFinTypeName());
		this.finBranch.setValue(financeMain.getFinBranch() + "-"+ financeMain.getLovDescFinBranchName());
		this.finCcy.setValue(financeMain.getFinCcy() + "-"+ financeMain.getLovDescFinCcyName());
		this.custID.setValue(financeMain.getLovDescCustCIF() + "-"+ financeMain.getLovDescCustShrtName());
		this.finStartDate.setValue(financeMain.getFinStartDate());
		this.maturityDate.setValue(financeMain.getMaturityDate());

		this.label_FinWriteoffDialog_WOPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWrittenoffPri(), ccyFormat));
		this.label_FinWriteoffDialog_WOPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWrittenoffPft(), ccyFormat));
		this.label_FinWriteoffDialog_ODPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getCurODPri(), ccyFormat));
		this.label_FinWriteoffDialog_ODPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getCurODPft(), ccyFormat));
		this.label_FinWriteoffDialog_UnPaidPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnPaidSchdPri(), ccyFormat));
		this.label_FinWriteoffDialog_UnPaidPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnPaidSchdPft(), ccyFormat));
		this.label_FinWriteoffDialog_OutStandPrincipal.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnPaidSchdPri(), ccyFormat));
		this.label_FinWriteoffDialog_OutStandProfit.setValue(PennantAppUtil.formateAmount(financeWriteoff.getUnPaidSchdPft(), ccyFormat));
		this.label_FinWriteoffDialog_PenaltyAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getPenaltyAmount(), ccyFormat));
		this.label_FinWriteoffDialog_ProvisionAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getProvisionedAmount(), ccyFormat));

		this.writeoffPriAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffPrincipal(), ccyFormat));
		this.writeoffPftAmt.setValue(PennantAppUtil.formateAmount(financeWriteoff.getWriteoffProfit(), ccyFormat));
		this.adjAmount.setValue(PennantAppUtil.formateAmount(financeWriteoff.getAdjAmount(), ccyFormat));
		this.remarks.setValue(financeWriteoff.getRemarks());
		this.writeoffDate.setValue(financeWriteoff.getWriteoffDate());

		if(financeWriteoff.getWriteoffDate() == null){
			Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
			this.writeoffDate.setValue(curBDay);
		}
		
		if(!financeMain.isNewRecord()){
			this.finScheduleTab.setVisible(true);

			Cloner cloner = new Cloner();
			effectFinScheduleData = cloner.deepClone(financeWriteoffHeader);
			doFillScheduleList(effectFinScheduleData);
			
		}
		this.recordStatus.setValue(financeMain.getRecordStatus());

		logger.debug("Leaving");
	}
	
	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_writeoffDate = this.writeoffDate.getValue();
		this.oldVar_writeoffPriAmt = this.writeoffPriAmt.getValue();
		this.oldVar_writeoffPftAmt = this.writeoffPftAmt.getValue();
		this.oldVar_adjAmount = this.adjAmount.getValue();
		this.oldVar_remarks = this.remarks.getValue();
		logger.debug("Leaving");
	}
	
	private boolean isDataChanged(){
		
		if(this.oldVar_writeoffDate != this.writeoffDate.getValue()){
			return true;
		}
		if(this.oldVar_writeoffPriAmt != this.writeoffPriAmt.getValue()){
			return true;
		}
		if(this.oldVar_writeoffPftAmt != this.writeoffPftAmt.getValue()){
			return true;
		}
		if(this.oldVar_adjAmount != this.adjAmount.getValue()){
			return true;
		}
		if(this.oldVar_remarks != this.remarks.getValue()){
			return true;
		}
		return false;
	}
	
	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Close_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				closeDialog(this.window_FinWriteoffDialog, "FinWriteoffDialog");
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
			closeDialog(this.window_FinWriteoffDialog, "FinWriteoffDialog");
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calculate Write-off Effect Schedule
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnWriteoffCal(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if(!isValidated()){
			return;
		}

		this.writeoffPriAmt.setDisabled(true);
		this.writeoffPftAmt.setDisabled(true);
		this.adjAmount.setDisabled(true);

		this.btnWriteoffCal.setDisabled(true);
		this.btnWriteoffReCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffReCal"));
		this.btnWriteoffPay.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffPay"));

		this.finScheduleTab.setVisible(true);
		this.listBoxSchedule.getItems().clear();
		
		//Reset only Schedule Details Data
		Cloner cloner = new Cloner();
		FinanceWriteoffHeader schdData = cloner.deepClone(financeWriteoffHeader);
		schdData.setScheduleDetails(getFinanceWriteoffService().getFinScheduleDetails(financeMain.getFinReference()));

		calScheduleWriteOffDetails(schdData);
		doStoreInitValues();

		logger.debug("Leaving" + event.toString());
	}

	private List<FinanceScheduleDetail> calScheduleWriteOffDetails(FinanceWriteoffHeader financeWriteoffHeader) throws IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");

		//Copy Total Finance Schedule Data for Calculation without Effecting the Original Schedule Data
		Cloner cloner = new Cloner();
		effectFinScheduleData = cloner.deepClone(financeWriteoffHeader);

		BigDecimal woPriAmt = PennantAppUtil.unFormateAmount(this.writeoffPriAmt.getValue(), ccyFormat);
		BigDecimal woPftAmt = PennantAppUtil.unFormateAmount(this.writeoffPftAmt.getValue(), ccyFormat);

		List<FinanceScheduleDetail> effectedFinSchDetails = effectFinScheduleData.getScheduleDetails();

		if(effectedFinSchDetails != null && effectedFinSchDetails.size() > 0){
			for (int i = 0; i < effectedFinSchDetails.size(); i++) {

				FinanceScheduleDetail curSchdl = effectedFinSchDetails.get(i);

				//Reset Write-off Principal Amount
				if(woPriAmt.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal schPriBal = curSchdl.getPrincipalSchd().subtract(curSchdl.getSchdPriPaid()).add(
							curSchdl.getDefPrincipal()).subtract(curSchdl.getDefSchdPriPaid()).subtract(curSchdl.getWriteoffPrincipal());
					if(schPriBal.compareTo(BigDecimal.ZERO) > 0){
						if(woPriAmt.compareTo(schPriBal) >= 0){
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(schPriBal));
							woPriAmt = woPriAmt.subtract(schPriBal);
						}else{
							curSchdl.setWriteoffPrincipal(curSchdl.getWriteoffPrincipal().add(woPriAmt));
							woPriAmt = BigDecimal.ZERO;
						}
					}
				}

				//Reset Write-off Profit Amount
				if(woPftAmt.compareTo(BigDecimal.ZERO) > 0){
					BigDecimal schPftBal = curSchdl.getProfitSchd().subtract(curSchdl.getSchdPftPaid()).add(
							curSchdl.getDefProfit()).subtract(curSchdl.getDefSchdPftPaid()).subtract(curSchdl.getWriteoffProfit());
					if(schPftBal.compareTo(BigDecimal.ZERO) > 0){
						if(woPftAmt.compareTo(schPftBal) >= 0){
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(schPftBal));
							woPftAmt = woPftAmt.subtract(schPftBal);
						}else{
							curSchdl.setWriteoffProfit(curSchdl.getWriteoffProfit().add(woPftAmt));
							woPftAmt = BigDecimal.ZERO;
						}
					}
				}
			}
		}

		doFillScheduleList(effectFinScheduleData);

		logger.debug("Leaving");
		return effectedFinSchDetails;
	}
	

	public List<FinanceScheduleDetail> sortSchdDetails(
			List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {
				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					if (detail1.getSchDate().after(detail2.getSchDate())) {
						return 1;
					}
					return 0;
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * @param aFinScheduleData (FinScheduleData) 
	 *  
	 */
	public void doFillScheduleList(FinanceWriteoffHeader finScheduleData) {
		logger.debug("Entering");

		FinanceScheduleDetail prvSchDetail = null;
		
		//Reset To Finance Schedule Data Object For rendering purpose
		FinScheduleData aFinScheduleData = new FinScheduleData();
		aFinScheduleData.setFinanceMain(finScheduleData.getFinanceMain());
		aFinScheduleData.setFinanceScheduleDetails(finScheduleData.getScheduleDetails());
		aFinScheduleData.setDisbursementDetails(finScheduleData.getDisbursementDetails());
		aFinScheduleData.setFinanceType(finScheduleData.getFinanceType());

		FinScheduleListItemRenderer finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if(aFinScheduleData != null && sdSize > 0) {

			// Find Out Fee charge Details on Schedule
			Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
			if(aFinScheduleData.getFeeRules() != null && aFinScheduleData.getFeeRules().size() > 0){
				feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();

				for (FeeRule fee : aFinScheduleData.getFeeRules()) {
					if(feeChargesMap.containsKey(fee.getSchDate())){
						ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}else{
						ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
						feeChargeList.add(fee);
						feeChargesMap.put(fee.getSchDate(), feeChargeList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
			if(aFinScheduleData.getRepayDetails() != null && aFinScheduleData.getRepayDetails().size() > 0){
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : aFinScheduleData.getRepayDetails()) {
					if(rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())){
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}else{
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
			if(aFinScheduleData.getPenaltyDetails() != null && aFinScheduleData.getPenaltyDetails().size() > 0){
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				for (OverdueChargeRecovery penaltyDetail : aFinScheduleData.getPenaltyDetails()) {
					if(penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())){
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap.get(penaltyDetail.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}else{
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}

			//Clear all the listitems in listbox
			for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
				boolean showRate = false;
				FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
				if(i==0){
					prvSchDetail =aScheduleDetail;
					showRate = true;
				}else {
					prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i-1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinScheduleData);
				if(aFinScheduleData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", aFinScheduleData.getDefermentMap().get(aScheduleDetail.getSchDate()));
				}else {
					map.put("defermentDetail", null);
				}

				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("paymentDetailsMap", rpyDetailsMap);
				map.put("penaltyDetailsMap", penaltyDetailsMap);
				map.put("window", this.window_FinWriteoffDialog);
				finRender.render(map, prvSchDetail, false, true, true, feeChargesMap, showRate);

				if(i == sdSize - 1){						
					finRender.render(map, prvSchDetail, true, true, true, feeChargesMap, showRate);					
					break;
				}
			}
		}
		logger.debug("Leaving");
	}


	/**
	 * Method for Calculate Write-off Effect Schedule
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnWriteoffReCal(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.writeoffPriAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPriAmt"));
		this.writeoffPftAmt.setDisabled(isReadOnly("FinWriteoffDialog_writeoffPftAmt"));
		this.adjAmount.setDisabled(isReadOnly("FinWriteoffDialog_adjAmount"));

		this.btnWriteoffCal.setDisabled(!getUserWorkspace().isAllowed("button_FinWriteoffDialog_btnWriteoffCal"));
		this.btnWriteoffReCal.setDisabled(true);
		this.btnWriteoffPay.setDisabled(true);

		this.listBoxSchedule.getItems().clear();
		this.finScheduleTab.setVisible(false);
		this.finWriteoffTab.setSelected(true);
		logger.debug("Leaving" + event.toString());
	}
	
	private  boolean isValidated() throws InterruptedException{
		logger.debug("Entering");
		
		if((this.writeoffPriAmt.getValue() == null || this.writeoffPriAmt.getValue().compareTo(BigDecimal.ZERO) <= 0) && 
				(this.writeoffPftAmt.getValue() == null || this.writeoffPftAmt.getValue().compareTo(BigDecimal.ZERO) <= 0)){

			PTMessageUtils.showErrorMessage("Write-off Amount must be Entered.");
			return false;
		}

		BigDecimal woPriAmt = PennantAppUtil.unFormateAmount(this.writeoffPriAmt.getValue(), ccyFormat);
		BigDecimal woPftAmt = PennantAppUtil.unFormateAmount(this.writeoffPftAmt.getValue(), ccyFormat);

		if(woPriAmt.compareTo(financeWriteoff.getUnPaidSchdPri()) > 0 || woPftAmt.compareTo(financeWriteoff.getUnPaidSchdPft()) > 0){
			PTMessageUtils.showErrorMessage("Entered Write-off Amount Should be less than Unpaid Balances.");
			return false;
		}
		
		logger.debug("Leaving");
		return true;
	}
	
	private FinanceWriteoff doWriteComponentsToBean(){
		logger.debug("Entering");
		
		int finFormatter  = financeMain.getLovDescFinFormatter();
		FinanceWriteoff writeoff = getFinanceWriteoff();
		
		writeoff.setFinReference(this.finReference.getValue());
		writeoff.setWrittenoffPri(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_WOPriAmt.getValue(), finFormatter));
		writeoff.setWrittenoffPft(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_WOPftAmt.getValue(), finFormatter));
		writeoff.setCurODPri(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ODPriAmt.getValue(), finFormatter));
		writeoff.setCurODPft(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ODPftAmt.getValue(), finFormatter));
		writeoff.setUnPaidSchdPri(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_UnPaidPriAmt.getValue(), finFormatter));
		writeoff.setUnPaidSchdPft(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_UnPaidPftAmt.getValue(), finFormatter));
		writeoff.setPenaltyAmount(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_PenaltyAmt.getValue(), finFormatter));
		writeoff.setProvisionedAmount(PennantApplicationUtil.unFormateAmount(this.label_FinWriteoffDialog_ProvisionAmt.getValue(), finFormatter));
		
		writeoff.setWriteoffDate(this.writeoffDate.getValue());
		writeoff.setWriteoffPrincipal(PennantApplicationUtil.unFormateAmount(this.writeoffPriAmt.getValue(), finFormatter));
		writeoff.setWriteoffProfit(PennantApplicationUtil.unFormateAmount(this.writeoffPftAmt.getValue(), finFormatter));
		writeoff.setAdjAmount(PennantApplicationUtil.unFormateAmount(this.adjAmount.getValue(), finFormatter));
		writeoff.setRemarks(this.remarks.getValue());
		
		logger.debug("Leaving");
		return writeoff;
	}

	/**
	 * Method for event of Changing Repayment Amount 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnWriteoffPay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		boolean isDataChanged = false;
		if(this.oldVar_writeoffPriAmt != this.writeoffPriAmt.getValue()){
			isDataChanged = true;
		}
		if(this.oldVar_writeoffPftAmt != this.writeoffPftAmt.getValue()){
			isDataChanged = true;
		}
		
		if(isDataChanged){
			PTMessageUtils.showErrorMessage("Amounts Changed. Must need to Recalculate Schedule.");
			return;
		}

		FinanceWriteoffHeader aFinanceWriteoffHeader = new FinanceWriteoffHeader();
		Cloner cloner = new Cloner();
		aFinanceWriteoffHeader = cloner.deepClone(getFinanceWriteoffHeader());

		aFinanceWriteoffHeader.setScheduleDetails(effectFinScheduleData.getScheduleDetails());
		FinanceMain aFinanceMain = aFinanceWriteoffHeader.getFinanceMain();
		
		//Prepare Validation & Calling
		if(!isValidated()){
			return;
		}
		
		//loading Write Off Object Entered Data into Bean
		aFinanceWriteoffHeader.setFinanceWriteoff(doWriteComponentsToBean());

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aFinanceMain.setNewRecord(true);
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			tranType = PennantConstants.TRAN_UPD;
		}

		// save it to database
		try {
			aFinanceMain.setRcdMaintainSts(PennantConstants.WRITEOFF);
			aFinanceWriteoffHeader.setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceWriteoffHeader, tranType)) {

				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				//Customer Notification for Role Identification
				if(StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("")){
					aFinanceMain.setNextRoleCode("");
				}
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),aFinanceMain.getNextRoleCode(), 
						aFinanceMain.getFinReference(), " Finance ", aFinanceMain.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);

				//Mail Alert Notification for User
				if(!StringUtils.trimToEmpty(aFinanceMain.getNextTaskId()).equals("") && 
						!StringUtils.trimToEmpty(aFinanceMain.getNextRoleCode()).equals(aFinanceMain.getRoleCode())){
					getMailUtil().sendMail("FIN", aFinanceWriteoffHeader,this);
				}

				closeDialog(this.window_FinWriteoffDialog, "FinWriteoffDialog");
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_FinWriteoffDialog, e);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private String getServiceTasks(String taskId, FinanceMain financeMain,
			String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getWorkFlow().getOperationRefs(taskId,financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getWorkFlow().getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getWorkFlow().firstTask.owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + ",";
					}
					nextRoleCode += getWorkFlow().getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws InterruptedException 
	 */
	private boolean doProcess(FinanceWriteoffHeader aFinanceWriteoffHeader, String tranType) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceWriteoffHeader.getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());

		afinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());
		aFinanceWriteoffHeader.setFinanceMain(afinanceMain);

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, afinanceMain))) {
				try {
					if (!isNotes_Entered()) {
						PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			auditHeader = getAuditHeader(aFinanceWriteoffHeader, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {

					/*FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					FinanceMain financeMain = tFinanceDetail.getFinScheduleData().getFinanceMain();

					List<Long> templateIDList = getManualPaymentService().getMailTemplatesByFinType(financeMain.getFinType(), financeMain.getRoleCode());
					for (Long templateId : templateIDList) {
						getMailUtil().sendMail(templateId, PennantConstants.TEMPLATE_FOR_CN, financeMain);
					}*/

				} else {
					FinanceWriteoffHeader tFinanceWriteoffHeader=  (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceWriteoffHeader.getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceWriteoffHeader);
					processCompleted = doSaveProcess(auditHeader, method);

				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceWriteoffHeader tFinanceWriteoffHeader =  (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceWriteoffHeader.getFinanceMain(),finishedTasks);

			}

			FinanceWriteoffHeader tFinanceWriteoffHeader =  (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getWorkFlow().getNextTaskIds(taskId,tFinanceWriteoffHeader.getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceWriteoffHeader.getFinanceMain());
					auditHeader.getAuditDetail().setModelData(tFinanceWriteoffHeader);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

			auditHeader = getAuditHeader(aFinanceWriteoffHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);

		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceWriteoffHeader aFinanceWriteoffHeader = (FinanceWriteoffHeader) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = aFinanceWriteoffHeader.getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					auditHeader = getFinanceWriteoffService().saveOrUpdate(auditHeader);

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceWriteoffService().doApprove(auditHeader);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceWriteoffService().doReject(auditHeader);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinWriteoffDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinWriteoffDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());

		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getErrorMsg());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinanceWriteoffHeader header, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,null , header);
		return new AuditHeader(header.getFinReference(), null, null, null, 
				auditDetail, header.getFinanceMain().getUserDetails(), getOverideMap());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		this.btnNotes.setSclass("");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for retrieving Notes Details
	 */
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(moduleDefiner);
		notes.setReference(financeMain.getFinReference());
		notes.setVersion(financeMain.getVersion());
		logger.debug("Leaving ");
		return notes;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj();
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public FinanceMain getFinanceMain() {
		return financeMain;
	}
	public void setFinanceMain(FinanceMain financeMain) {
		this.financeMain = financeMain;
	}

	public FinanceWriteoffHeader getFinanceWriteoffHeader() {
		return financeWriteoffHeader;
	}
	public void setFinanceWriteoffHeader(FinanceWriteoffHeader financeWriteoffHeader) {
		this.financeWriteoffHeader = financeWriteoffHeader;
	}

	public FinanceWriteoff getFinanceWriteoff() {
		return financeWriteoff;
	}
	public void setFinanceWriteoff(FinanceWriteoff financeWriteoff) {
		this.financeWriteoff = financeWriteoff;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}
	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public FinanceWriteoffService getFinanceWriteoffService() {
		return financeWriteoffService;
	}

	public void setFinanceWriteoffService(FinanceWriteoffService financeWriteoffService) {
		this.financeWriteoffService = financeWriteoffService;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}
}