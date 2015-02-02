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
 * FileName    		:  CorporateCustomerDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2011    														*
 *                                                                  						*
 * Modified Date    :  01-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.customermasters.corporatecustomerdetail;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CorporateCustomerDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTPhoneNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CorporateCustomerDetail/corporateCustomerDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CorporateCustomerDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -8302487308102065374L;
	private final static Logger logger = Logger.getLogger(CorporateCustomerDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	 window_CorporateCustomerDetailDialog;  // autowired

	protected Longbox 	 custId; 								// autowired
	protected Textbox 	 name; 									// autowired
	protected Textbox 	 phoneNumber; 							// autowired
	protected Textbox 	 phoneNumber1;							// autowired
	protected Textbox 	 emailId; 								// autowired
	protected Datebox 	 bussCommenceDate; 						// autowired
	protected Datebox 	 servCommenceDate; 						// autowired
	protected Datebox 	 bankRelationshipDate; 					// autowired
	protected Decimalbox paidUpCapital; 						// autowired
	protected Decimalbox authorizedCapital; 					// autowired
	protected Decimalbox reservesAndSurPlus; 					// autowired
	protected Decimalbox intangibleAssets; 						// autowired
	protected Decimalbox tangibleNetWorth; 						// autowired
	protected Decimalbox longTermLiabilities; 					// autowired
	protected Decimalbox capitalEmployed; 						// autowired
	protected Decimalbox investments; 							// autowired
	protected Decimalbox nonCurrentAssets; 						// autowired
	protected Decimalbox netWorkingCapital;	 					// autowired
	protected Decimalbox netSales; 								// autowired
	protected Decimalbox otherIncome; 							// autowired
	protected Decimalbox netProfitAfterTax; 					// autowired
	protected Decimalbox depreciation; 							// autowired
	protected Decimalbox cashAccurals; 							// autowired
	protected Decimalbox annualTurnover; 						// autowired
	protected Decimalbox returnOnCapitalEmp; 					// autowired
	protected Decimalbox currentAssets; 						// autowired
	protected Decimalbox currentLiabilities; 					// autowired
	protected Decimalbox currentBookValue; 						// autowired
	protected Decimalbox currentMarketValue; 					// autowired
	protected Decimalbox promotersShare; 						// autowired
	protected Decimalbox associatesShare; 						// autowired
	protected Decimalbox publicShare; 							// autowired
	protected Decimalbox finInstShare; 							// autowired
	protected Decimalbox others; 								// autowired
	protected Textbox 	 custCIF;								// autowired
	protected Label 	 custShrtName;							// autowired

	protected Label 	recordStatus; 								// autowired
	protected Radiogroup userAction;
	protected Groupbox 	groupboxWf;
	

	// not auto wired vars
	private CorporateCustomerDetail corporateCustomerDetail; // overhanded per param
	private transient CorporateCustomerDetailListCtrl corporateCustomerDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialize.
	private transient long   			oldVar_custId;
	private transient String  			oldVar_name;
	private transient String  			oldVar_phoneNumber;
	private transient String  			oldVar_phoneNumber1;
	private transient String  			oldVar_emailId;
	private transient Date  			oldVar_bussCommenceDate;
	private transient Date  			oldVar_servCommenceDate;
	private transient Date  			oldVar_bankRelationshipDate;
	private transient BigDecimal  		oldVar_paidUpCapital;
	private transient BigDecimal  		oldVar_authorizedCapital;
	private transient BigDecimal  		oldVar_reservesAndSurPlus;
	private transient BigDecimal  		oldVar_intangibleAssets;
	private transient BigDecimal  		oldVar_tangibleNetWorth;
	private transient BigDecimal  		oldVar_longTermLiabilities;
	private transient BigDecimal  		oldVar_capitalEmployed;
	private transient BigDecimal  		oldVar_investments;
	private transient BigDecimal  		oldVar_nonCurrentAssets;
	private transient BigDecimal  		oldVar_netWorkingCapital;
	private transient BigDecimal  		oldVar_netSales;
	private transient BigDecimal  		oldVar_otherIncome;
	private transient BigDecimal  		oldVar_netProfitAfterTax;
	private transient BigDecimal  		oldVar_depreciation;
	private transient BigDecimal  		oldVar_cashAccurals;
	private transient BigDecimal  		oldVar_annualTurnover;
	private transient BigDecimal  		oldVar_returnOnCapitalEmp;
	private transient BigDecimal  		oldVar_currentAssets;
	private transient BigDecimal  		oldVar_currentLiabilities;
	private transient BigDecimal  		oldVar_currentBookValue;
	private transient BigDecimal  		oldVar_currentMarketValue;
	private transient BigDecimal  		oldVar_promotersShare;
	private transient BigDecimal  		oldVar_associatesShare;
	private transient BigDecimal  		oldVar_publicShare;
	private transient BigDecimal  		oldVar_finInstShare;
	private transient BigDecimal  		oldVar_others;
	private transient String 			oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CorporateCustomerDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autowire
	protected Button btnEdit; 			// autowire
	protected Button btnDelete; 		// autowire
	protected Button btnSave; 			// autowire
	protected Button btnCancel; 		// autowire
	protected Button btnClose; 			// autowire
	protected Button btnHelp; 			// autowire
	protected Button btnNotes; 			// autowire

	// ServiceDAOs / Domain Classes
	private transient CorporateCustomerDetailService corporateCustomerDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	protected JdbcSearchObject<Customer> newSearchObject ;
    Date startDate = (Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");
	/**
	 * default constructor.<br>
	 */
	public CorporateCustomerDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CorporateCustomerDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CorporateCustomerDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("corporateCustomerDetail")) {
			this.corporateCustomerDetail = (CorporateCustomerDetail) args.get("corporateCustomerDetail");
			CorporateCustomerDetail befImage =new CorporateCustomerDetail();
			BeanUtils.copyProperties(this.corporateCustomerDetail, befImage);
			this.corporateCustomerDetail.setBefImage(befImage);
			setCorporateCustomerDetail(this.corporateCustomerDetail);
		} else {
			setCorporateCustomerDetail(null);
		}

		doLoadWorkFlow(this.corporateCustomerDetail.isWorkflow(),
				this.corporateCustomerDetail.getWorkflowId(),this.corporateCustomerDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CorporateCustomerDetailDialog");
		}

		// READ OVERHANDED params !
		// we get the corporateCustomerDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete corporateCustomerDetail here.
		if (args.containsKey("corporateCustomerDetailListCtrl")) {
			setCorporateCustomerDetailListCtrl((CorporateCustomerDetailListCtrl) args.get(
			"corporateCustomerDetailListCtrl"));
		} else {
			setCorporateCustomerDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCorporateCustomerDetail());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;

		//Empty sent any required attributes
		this.name.setMaxlength(20);
		this.phoneNumber.setMaxlength(20);
		this.phoneNumber1.setMaxlength(20);
		this.emailId.setMaxlength(100);
		this.bussCommenceDate.setFormat(PennantConstants.dateFormat);
		this.servCommenceDate.setFormat(PennantConstants.dateFormat);
		this.bankRelationshipDate.setFormat(PennantConstants.dateFormat);
		this.paidUpCapital.setMaxlength(18);
		this.paidUpCapital.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.paidUpCapital.setScale(0);
		this.authorizedCapital.setMaxlength(18);
		this.authorizedCapital.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.authorizedCapital.setScale(0);
		this.reservesAndSurPlus.setMaxlength(18);
		this.reservesAndSurPlus.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.reservesAndSurPlus.setScale(0);
		this.intangibleAssets.setMaxlength(18);
		this.intangibleAssets.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.intangibleAssets.setScale(0);
		this.tangibleNetWorth.setMaxlength(18);
		this.tangibleNetWorth.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.tangibleNetWorth.setScale(0);
		this.longTermLiabilities.setMaxlength(18);
		this.longTermLiabilities.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.longTermLiabilities.setScale(0);
		this.capitalEmployed.setMaxlength(18);
		this.capitalEmployed.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.capitalEmployed.setScale(0);
		this.investments.setMaxlength(18);
		this.investments.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.investments.setScale(0);
		this.nonCurrentAssets.setMaxlength(18);
		this.nonCurrentAssets.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.nonCurrentAssets.setScale(0);
		this.netWorkingCapital.setMaxlength(18);
		this.netWorkingCapital.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.netWorkingCapital.setScale(0);
		this.netSales.setMaxlength(18);
		this.netSales.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.netSales.setScale(0);
		this.otherIncome.setMaxlength(18);
		this.otherIncome.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.otherIncome.setScale(0);
		this.netProfitAfterTax.setMaxlength(18);
		this.netProfitAfterTax.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.netProfitAfterTax.setScale(0);
		this.depreciation.setMaxlength(18);
		this.depreciation.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.depreciation.setScale(0);
		this.cashAccurals.setMaxlength(18);
		this.cashAccurals.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.cashAccurals.setScale(0);
		this.annualTurnover.setMaxlength(18);
		this.annualTurnover.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.annualTurnover.setScale(0);
		this.returnOnCapitalEmp.setMaxlength(18);
		this.returnOnCapitalEmp.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.returnOnCapitalEmp.setScale(0);
		this.currentAssets.setMaxlength(18);
		this.currentAssets.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.currentAssets.setScale(0);
		this.currentLiabilities.setMaxlength(18);
		this.currentLiabilities.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.currentLiabilities.setScale(0);
		this.currentBookValue.setMaxlength(18);
		this.currentBookValue.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.currentBookValue.setScale(0);
		this.currentMarketValue.setMaxlength(18);
		this.currentMarketValue.setFormat(PennantApplicationUtil.getAmountFormate(0));
		this.currentMarketValue.setScale(0);
		this.promotersShare.setMaxlength(5);
		this.promotersShare.setFormat(PennantConstants.rateFormate2);
		this.promotersShare.setScale(2);
		this.associatesShare.setMaxlength(5);
		this.associatesShare.setFormat(PennantConstants.rateFormate2);
		this.associatesShare.setScale(2);
		this.publicShare.setMaxlength(5);
		this.publicShare.setFormat(PennantConstants.rateFormate2);
		this.publicShare.setScale(2);
		this.finInstShare.setMaxlength(5);
		this.finInstShare.setFormat(PennantConstants.rateFormate2);
		this.finInstShare.setScale(2);
		this.others.setMaxlength(5);
		this.others.setFormat(PennantConstants.rateFormate2);
		this.others.setScale(2);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
			this.groupboxWf.setVisible(false);
			
		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("CorporateCustomerDetailDialog");

		this.btnDelete.setVisible(false);
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
		"button_CorporateCustomerDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
		"button_CorporateCustomerDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
		"button_CorporateCustomerDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CorporateCustomerDetailDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" + event.toString());
		doEdit();
		// remember the old vars
		doStoreInitValues();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		PTMessageUtils.showHelpWindow(event, window_CorporateCustomerDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering" + event.toString());
		doNew();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" + event.toString());
		doCancel();
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

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++ GUI Process ++++++++++++++++++ //

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

		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_CorporateCustomerDetailDialog, "CorporateCustomerDetail");
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCorporateCustomerDetail
	 *            CorporateCustomerDetail
	 */
	public void doWriteBeanToComponents(CorporateCustomerDetail aCorporateCustomerDetail) {
		logger.debug("Entering") ;

		if(aCorporateCustomerDetail.getCustId()!=Long.MIN_VALUE){
			this.custId.setValue(aCorporateCustomerDetail.getCustId());	
		}
		this.name.setValue(aCorporateCustomerDetail.getName());
		this.phoneNumber.setValue(aCorporateCustomerDetail.getPhoneNumber());
		this.phoneNumber1.setValue(aCorporateCustomerDetail.getPhoneNumber1());
		this.emailId.setValue(aCorporateCustomerDetail.getEmailId());
		this.bussCommenceDate.setValue(aCorporateCustomerDetail.getBussCommenceDate());
		this.servCommenceDate.setValue(aCorporateCustomerDetail.getServCommenceDate());
		this.bankRelationshipDate.setValue(aCorporateCustomerDetail.getBankRelationshipDate());
		this.paidUpCapital.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getPaidUpCapital(),0));
		this.authorizedCapital.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getAuthorizedCapital(),0));
		this.reservesAndSurPlus.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getReservesAndSurPlus(),0));
		this.intangibleAssets.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getIntangibleAssets(),0));
		this.tangibleNetWorth.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getTangibleNetWorth(),0));
		this.longTermLiabilities.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getLongTermLiabilities(),0));
		this.capitalEmployed.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getCapitalEmployed(),0));
		this.investments.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getInvestments(),0));
		this.nonCurrentAssets.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getNonCurrentAssets(),0));
		this.netWorkingCapital.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getNetWorkingCapital(),0));
		this.netSales.setValue(PennantAppUtil.formateAmount(aCorporateCustomerDetail.getNetSales(),0));
		this.otherIncome.setValue(PennantAppUtil.formateAmount(aCorporateCustomerDetail.getOtherIncome(),0));
		this.netProfitAfterTax.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getNetProfitAfterTax(),0));
		this.depreciation.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getDepreciation(),0));
		this.cashAccurals.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getCashAccurals(),0));
		this.annualTurnover.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getAnnualTurnover(),0));
		this.returnOnCapitalEmp.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getReturnOnCapitalEmp(),0));
		this.currentAssets.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getCurrentAssets(),0));
		this.currentLiabilities.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getCurrentLiabilities(),0));
		this.currentBookValue.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getCurrentBookValue(),0));
		this.currentMarketValue.setValue(PennantAppUtil.formateAmount(
				aCorporateCustomerDetail.getCurrentMarketValue(),0));
		this.promotersShare.setValue(aCorporateCustomerDetail.getPromotersShare());
		this.associatesShare.setValue(aCorporateCustomerDetail.getAssociatesShare());
		this.publicShare.setValue(aCorporateCustomerDetail.getPublicShare());
		this.finInstShare.setValue(aCorporateCustomerDetail.getFinInstShare());
		this.others.setValue(aCorporateCustomerDetail.getOthers());
		this.custCIF.setValue(aCorporateCustomerDetail.getLovDescCustCIF()==null?"":
			aCorporateCustomerDetail.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCorporateCustomerDetail.getLovDescCustShrtName()==null?"":
			aCorporateCustomerDetail.getLovDescCustShrtName().trim());

		this.recordStatus.setValue(aCorporateCustomerDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCorporateCustomerDetail
	 */
	public void doWriteComponentsToBean(CorporateCustomerDetail aCorporateCustomerDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCorporateCustomerDetail.setLovDescCustCIF(this.custCIF.getValue());
			aCorporateCustomerDetail.setCustId(this.custId.longValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setName(this.name.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setPhoneNumber(this.phoneNumber.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setPhoneNumber1(this.phoneNumber1.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setEmailId(this.emailId.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setBussCommenceDate(new Timestamp(
					this.bussCommenceDate.getValue().getTime()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setServCommenceDate(new Timestamp(
					this.servCommenceDate.getValue().getTime()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.bankRelationshipDate.getValue() != null){
				aCorporateCustomerDetail.setBankRelationshipDate(new Timestamp(
						this.bankRelationshipDate.getValue().getTime()));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setPaidUpCapital(PennantAppUtil.unFormateAmount(
					this.paidUpCapital.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setAuthorizedCapital(PennantAppUtil.unFormateAmount(
					this.authorizedCapital.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setReservesAndSurPlus(PennantAppUtil.unFormateAmount(
					this.reservesAndSurPlus.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setIntangibleAssets(PennantAppUtil.unFormateAmount(
					this.intangibleAssets.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setTangibleNetWorth(PennantAppUtil.unFormateAmount(
					this.tangibleNetWorth.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setLongTermLiabilities(PennantAppUtil.unFormateAmount(
					this.longTermLiabilities.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setCapitalEmployed(PennantAppUtil.unFormateAmount(
					this.capitalEmployed.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setInvestments(PennantAppUtil.unFormateAmount(
					this.investments.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setNonCurrentAssets(PennantAppUtil.unFormateAmount(
					this.nonCurrentAssets.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setNetWorkingCapital(PennantAppUtil.unFormateAmount(
					this.netWorkingCapital.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setNetSales(PennantAppUtil.unFormateAmount(
					this.netSales.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setOtherIncome(PennantAppUtil.unFormateAmount(
					this.otherIncome.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setNetProfitAfterTax(PennantAppUtil.unFormateAmount(
					this.netProfitAfterTax.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setDepreciation(PennantAppUtil.unFormateAmount(
					this.depreciation.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setCashAccurals(PennantAppUtil.unFormateAmount(
					this.cashAccurals.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setAnnualTurnover(PennantAppUtil.unFormateAmount(
					this.annualTurnover.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setReturnOnCapitalEmp(PennantAppUtil.unFormateAmount(
					this.returnOnCapitalEmp.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setCurrentAssets(PennantAppUtil.unFormateAmount(
					this.currentAssets.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setCurrentLiabilities(PennantAppUtil.unFormateAmount(
					this.currentLiabilities.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setCurrentBookValue(PennantAppUtil.unFormateAmount(
					this.currentBookValue.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCorporateCustomerDetail.setCurrentMarketValue(PennantAppUtil.unFormateAmount(
					this.currentMarketValue.getValue(), 0));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		BigDecimal totalSharePercent = BigDecimal.ZERO;
		boolean percentageCheck = false;
		totalSharePercent = new BigDecimal(this.promotersShare.getValue()==null?"0":this.promotersShare.getValue().toString());
		totalSharePercent = totalSharePercent.add(new BigDecimal(this.associatesShare.getValue()==null?"0":this.associatesShare.getValue().toString()));
		totalSharePercent = totalSharePercent.add(new BigDecimal(this.publicShare.getValue()==null?"0":this.publicShare.getValue().toString()));
		totalSharePercent = totalSharePercent.add(new BigDecimal(this.finInstShare.getValue()==null?"0":this.finInstShare.getValue().toString()));
		totalSharePercent = totalSharePercent.add(new BigDecimal(this.others.getValue()==null?"0":this.others.getValue().toString()));
		if(totalSharePercent.doubleValue() != new Double(100)){
			percentageCheck = true;
		}

		try {
			if(percentageCheck){
				throw new WrongValueException(promotersShare, 
						Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
			}	
			aCorporateCustomerDetail.setPromotersShare(this.promotersShare.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(percentageCheck){
				throw new WrongValueException(associatesShare, 
						Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
			}	
			aCorporateCustomerDetail.setAssociatesShare(this.associatesShare.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(percentageCheck){
				throw new WrongValueException(publicShare, 
						Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
			}
			aCorporateCustomerDetail.setPublicShare(this.publicShare.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(percentageCheck){
				throw new WrongValueException(finInstShare, 
						Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
			}
			aCorporateCustomerDetail.setFinInstShare(this.finInstShare.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(percentageCheck){
				throw new WrongValueException(others, 
						Labels.getLabel("label_CorporateCustomerDetailDialog_TotalShare.value"));
			}
			aCorporateCustomerDetail.setOthers(this.others.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCorporateCustomerDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCorporateCustomerDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CorporateCustomerDetail aCorporateCustomerDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// set ReadOnly mode accordingly if the object is new or not.
		if (getCorporateCustomerDetail().isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.name.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCorporateCustomerDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CorporateCustomerDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_custId = this.custId.longValue();
		this.oldVar_name = this.name.getValue();
		this.oldVar_phoneNumber = this.phoneNumber.getValue();
		this.oldVar_phoneNumber1 = this.phoneNumber1.getValue();
		this.oldVar_emailId = this.emailId.getValue();
		this.oldVar_bussCommenceDate = PennantAppUtil.getTimestamp(this.bussCommenceDate.getValue());	
		this.oldVar_servCommenceDate = PennantAppUtil.getTimestamp(this.servCommenceDate.getValue());	
		this.oldVar_bankRelationshipDate = PennantAppUtil.getTimestamp(this.bankRelationshipDate.getValue());	
		this.oldVar_paidUpCapital = this.paidUpCapital.getValue();
		this.oldVar_authorizedCapital = this.authorizedCapital.getValue();
		this.oldVar_reservesAndSurPlus = this.reservesAndSurPlus.getValue();
		this.oldVar_intangibleAssets = this.intangibleAssets.getValue();
		this.oldVar_tangibleNetWorth = this.tangibleNetWorth.getValue();
		this.oldVar_longTermLiabilities = this.longTermLiabilities.getValue();
		this.oldVar_capitalEmployed = this.capitalEmployed.getValue();
		this.oldVar_investments = this.investments.getValue();
		this.oldVar_nonCurrentAssets = this.nonCurrentAssets.getValue();
		this.oldVar_netWorkingCapital = this.netWorkingCapital.getValue();
		this.oldVar_netSales = this.netSales.getValue();
		this.oldVar_otherIncome = this.otherIncome.getValue();
		this.oldVar_netProfitAfterTax = this.netProfitAfterTax.getValue();
		this.oldVar_depreciation = this.depreciation.getValue();
		this.oldVar_cashAccurals = this.cashAccurals.getValue();
		this.oldVar_annualTurnover = this.annualTurnover.getValue();
		this.oldVar_returnOnCapitalEmp = this.returnOnCapitalEmp.getValue();
		this.oldVar_currentAssets = this.currentAssets.getValue();
		this.oldVar_currentLiabilities = this.currentLiabilities.getValue();
		this.oldVar_currentBookValue = this.currentBookValue.getValue();
		this.oldVar_currentMarketValue = this.currentMarketValue.getValue();
		this.oldVar_promotersShare = this.promotersShare.getValue();
		this.oldVar_associatesShare = this.associatesShare.getValue();
		this.oldVar_publicShare = this.publicShare.getValue();
		this.oldVar_finInstShare = this.finInstShare.getValue();
		this.oldVar_others = this.others.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custId.setValue(this.oldVar_custId);
		this.name.setValue(this.oldVar_name);
		this.phoneNumber.setValue(this.oldVar_phoneNumber);
		this.phoneNumber1.setValue(this.oldVar_phoneNumber1);
		this.emailId.setValue(this.oldVar_emailId);
		this.bussCommenceDate.setValue(this.oldVar_bussCommenceDate);
		this.servCommenceDate.setValue(this.oldVar_servCommenceDate);
		this.bankRelationshipDate.setValue(this.oldVar_bankRelationshipDate);
		this.paidUpCapital.setValue(this.oldVar_paidUpCapital);
		this.authorizedCapital.setValue(this.oldVar_authorizedCapital);
		this.reservesAndSurPlus.setValue(this.oldVar_reservesAndSurPlus);
		this.intangibleAssets.setValue(this.oldVar_intangibleAssets);
		this.tangibleNetWorth.setValue(this.oldVar_tangibleNetWorth);
		this.longTermLiabilities.setValue(this.oldVar_longTermLiabilities);
		this.capitalEmployed.setValue(this.oldVar_capitalEmployed);
		this.investments.setValue(this.oldVar_investments);
		this.nonCurrentAssets.setValue(this.oldVar_nonCurrentAssets);
		this.netWorkingCapital.setValue(this.oldVar_netWorkingCapital);
		this.netSales.setValue(this.oldVar_netSales);
		this.otherIncome.setValue(this.oldVar_otherIncome);
		this.netProfitAfterTax.setValue(this.oldVar_netProfitAfterTax);
		this.depreciation.setValue(this.oldVar_depreciation);
		this.cashAccurals.setValue(this.oldVar_cashAccurals);
		this.annualTurnover.setValue(this.oldVar_annualTurnover);
		this.returnOnCapitalEmp.setValue(this.oldVar_returnOnCapitalEmp);
		this.currentAssets.setValue(this.oldVar_currentAssets);
		this.currentLiabilities.setValue(this.oldVar_currentLiabilities);
		this.currentBookValue.setValue(this.oldVar_currentBookValue);
		this.currentMarketValue.setValue(this.oldVar_currentMarketValue);
		this.promotersShare.setValue(this.oldVar_promotersShare);
		this.associatesShare.setValue(this.oldVar_associatesShare);
		this.publicShare.setValue(this.oldVar_publicShare);
		this.finInstShare.setValue(this.oldVar_finInstShare);
		this.others.setValue(this.oldVar_others);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {

		//To clear the Error Messages
		doClearMessage();

		if (this.oldVar_custId != this.custId.longValue()) {
			return true;
		}
		if (this.oldVar_name != this.name.getValue()) {
			return true;
		}
		if (this.oldVar_phoneNumber != this.phoneNumber.getValue()) {
			return true;
		}
		if (this.oldVar_phoneNumber1 != this.phoneNumber1.getValue()) {
			return true;
		}
		if (this.oldVar_emailId != this.emailId.getValue()) {
			return true;
		}
		String oldBussCommenceDate = "";
		String newBussCommenceDate = "";
		if (this.oldVar_bussCommenceDate != null) {
			oldBussCommenceDate = DateUtility.formatDate(this.oldVar_bussCommenceDate,PennantConstants.dateFormat);
		}
		if (this.bussCommenceDate.getValue() != null) {
			newBussCommenceDate = DateUtility.formatDate(this.bussCommenceDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldBussCommenceDate).equals(StringUtils.trimToEmpty(newBussCommenceDate))) {
			return true;
		}
		String oldServCommenceDate = "";
		String newServCommenceDate = "";
		if (this.oldVar_servCommenceDate != null) {
			oldServCommenceDate = DateUtility.formatDate(this.oldVar_servCommenceDate,PennantConstants.dateFormat);
		}
		if (this.servCommenceDate.getValue() != null) {
			newServCommenceDate = DateUtility.formatDate(this.servCommenceDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldServCommenceDate).equals(StringUtils.trimToEmpty(newServCommenceDate))) {
			return true;
		}
		String oldBankRelationshipDate = "";
		String newBankRelationshipDate = "";
		if (this.oldVar_bankRelationshipDate != null) {
			oldBankRelationshipDate = DateUtility.formatDate(this.oldVar_bankRelationshipDate,PennantConstants.dateFormat);
		}
		if (this.bankRelationshipDate.getValue() != null) {
			newBankRelationshipDate = DateUtility.formatDate(this.bankRelationshipDate.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldBankRelationshipDate).equals(StringUtils.trimToEmpty(newBankRelationshipDate))) {
			return true;
		}
		if (this.oldVar_paidUpCapital != this.paidUpCapital.getValue()) {
			return true;
		}
		if (this.oldVar_authorizedCapital != this.authorizedCapital.getValue()) {
			return true;
		}
		if (this.oldVar_reservesAndSurPlus != this.reservesAndSurPlus.getValue()) {
			return true;
		}
		if (this.oldVar_intangibleAssets != this.intangibleAssets.getValue()) {
			return true;
		}
		if (this.oldVar_tangibleNetWorth != this.tangibleNetWorth.getValue()) {
			return true;
		}
		if (this.oldVar_longTermLiabilities != this.longTermLiabilities.getValue()) {
			return true;
		}
		if (this.oldVar_capitalEmployed != this.capitalEmployed.getValue()) {
			return true;
		}
		if (this.oldVar_investments != this.investments.getValue()) {
			return true;
		}
		if (this.oldVar_nonCurrentAssets != this.nonCurrentAssets.getValue()) {
			return true;
		}
		if (this.oldVar_netWorkingCapital != this.netWorkingCapital.getValue()) {
			return true;
		}
		if (this.oldVar_netSales != this.netSales.getValue()) {
			return true;
		}
		if (this.oldVar_otherIncome != this.otherIncome.getValue()) {
			return true;
		}
		if (this.oldVar_netProfitAfterTax != this.netProfitAfterTax.getValue()) {
			return true;
		}
		if (this.oldVar_depreciation != this.depreciation.getValue()) {
			return true;
		}
		if (this.oldVar_cashAccurals != this.cashAccurals.getValue()) {
			return true;
		}
		if (this.oldVar_annualTurnover != this.annualTurnover.getValue()) {
			return true;
		}
		if (this.oldVar_returnOnCapitalEmp != this.returnOnCapitalEmp.getValue()) {
			return true;
		}
		if (this.oldVar_currentAssets != this.currentAssets.getValue()) {
			return true;
		}
		if (this.oldVar_currentLiabilities != this.currentLiabilities.getValue()) {
			return true;
		}
		if (this.oldVar_currentBookValue != this.currentBookValue.getValue()) {
			return true;
		}
		if (this.oldVar_currentMarketValue != this.currentMarketValue.getValue()) {
			return true;
		}
		if (this.oldVar_promotersShare != this.promotersShare.getValue()) {
			return true;
		}
		if (this.oldVar_associatesShare != this.associatesShare.getValue()) {
			return true;
		}
		if (this.oldVar_publicShare != this.publicShare.getValue()) {
			return true;
		}
		if (this.oldVar_finInstShare != this.finInstShare.getValue()) {
			return true;
		}
		if (this.oldVar_others != this.others.getValue()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custId.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_CustId.value"),null,true));
		}
		if (!this.name.isReadonly()){
			this.name.setConstraint(new PTStringValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_Name.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}	
		if (!this.phoneNumber.isReadonly()){
			this.phoneNumber.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_PhoneNumber.value"),true));
		}	
		if (!this.phoneNumber1.isReadonly()){
			if(!StringUtils.trimToEmpty(this.phoneNumber1.getValue()).equals("")){
				this.phoneNumber1.setConstraint(new PTPhoneNumberValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_PhoneNumber1.value"),true));
			}
		}	
		if (!this.emailId.isReadonly()){
			this.emailId.setConstraint(new PTEmailValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_EmailId.value"),true));
		}	
		if (!this.bussCommenceDate.isDisabled()){
			this.bussCommenceDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_BussCommenceDate.value"),true,startDate,DateUtility.getSystemDate(),false));
		}
		if (!this.servCommenceDate.isDisabled()){
			this.servCommenceDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_ServCommenceDate.value"),true, startDate,DateUtility.getSystemDate(),false));
		}
		if (!this.bankRelationshipDate.isDisabled()){
			if(this.bankRelationshipDate.getValue() != null){
				this.bankRelationshipDate.setConstraint(new PTDateValidator(Labels.getLabel("label_CorporateCustomerDetailDialog_BankRelationshipDate.value"),true,startDate,DateUtility.getSystemDate(),false));
			}
		}
		/*if (!this.paidUpCapital.isReadonly()){
			this.paidUpCapital.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_PaidUpCapital.value")));
		}	
		if (!this.authorizedCapital.isReadonly()){
			this.authorizedCapital.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_AuthorizedCapital.value")));
		}	
		if (!this.reservesAndSurPlus.isReadonly()){
			this.reservesAndSurPlus.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_ReservesAndSurPlus.value")));
		}	
		if (!this.intangibleAssets.isReadonly()){
			this.intangibleAssets.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_IntangibleAssets.value")));
		}	
		if (!this.tangibleNetWorth.isReadonly()){
			this.tangibleNetWorth.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_TangibleNetWorth.value")));
		}	
		if (!this.longTermLiabilities.isReadonly()){
			this.longTermLiabilities.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_LongTermLiabilities.value")));
		}	
		if (!this.capitalEmployed.isReadonly()){
			this.capitalEmployed.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_CapitalEmployed.value")));
		}	
		if (!this.investments.isReadonly()){
			this.investments.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_Investments.value")));
		}	
		if (!this.nonCurrentAssets.isReadonly()){
			this.nonCurrentAssets.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_NonCurrentAssets.value")));
		}	
		if (!this.netWorkingCapital.isReadonly()){
			this.netWorkingCapital.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_NetWorkingCapital.value")));
		}	
		if (!this.netSales.isReadonly()){
			this.netSales.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_NetSales.value")));
		}	
		if (!this.otherIncome.isReadonly()){
			this.otherIncome.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_OtherIncome.value")));
		}	
		if (!this.netProfitAfterTax.isReadonly()){
			this.netProfitAfterTax.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_NetProfitAfterTax.value")));
		}	
		if (!this.depreciation.isReadonly()){
			this.depreciation.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_Depreciation.value")));
		}	
		if (!this.cashAccurals.isReadonly()){
			this.cashAccurals.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_CashAccurals.value")));
		}	
		if (!this.annualTurnover.isReadonly()){
			this.annualTurnover.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_AnnualTurnover.value")));
		}	
		if (!this.returnOnCapitalEmp.isReadonly()){
			this.returnOnCapitalEmp.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_ReturnOnCapitalEmp.value")));
		}	
		if (!this.currentAssets.isReadonly()){
			this.currentAssets.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_CurrentAssets.value")));
		}	
		if (!this.currentLiabilities.isReadonly()){
			this.currentLiabilities.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_CurrentLiabilities.value")));
		}	
		if (!this.currentBookValue.isReadonly()){
			this.currentBookValue.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_CurrentBookValue.value")));
		}	
		if (!this.currentMarketValue.isReadonly()){
			this.currentMarketValue.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_CorporateCustomerDetailDialog_CurrentMarketValue.value")));
		}	
		 */
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.name.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.phoneNumber1.setConstraint("");
		this.emailId.setConstraint("");
		this.bussCommenceDate.setConstraint("");
		this.servCommenceDate.setConstraint("");
		this.bankRelationshipDate.setConstraint("");
		this.paidUpCapital.setConstraint("");
		this.authorizedCapital.setConstraint("");
		this.reservesAndSurPlus.setConstraint("");
		this.intangibleAssets.setConstraint("");
		this.tangibleNetWorth.setConstraint("");
		this.longTermLiabilities.setConstraint("");
		this.capitalEmployed.setConstraint("");
		this.investments.setConstraint("");
		this.nonCurrentAssets.setConstraint("");
		this.netWorkingCapital.setConstraint("");
		this.netSales.setConstraint("");
		this.otherIncome.setConstraint("");
		this.netProfitAfterTax.setConstraint("");
		this.depreciation.setConstraint("");
		this.cashAccurals.setConstraint("");
		this.annualTurnover.setConstraint("");
		this.returnOnCapitalEmp.setConstraint("");
		this.currentAssets.setConstraint("");
		this.currentLiabilities.setConstraint("");
		this.currentBookValue.setConstraint("");
		this.currentMarketValue.setConstraint("");
		this.promotersShare.setConstraint("");
		this.associatesShare.setConstraint("");
		this.publicShare.setConstraint("");
		this.finInstShare.setConstraint("");
		this.others.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.name.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.phoneNumber1.setErrorMessage("");
		this.emailId.setErrorMessage("");
		this.bussCommenceDate.setErrorMessage("");
		this.servCommenceDate.setErrorMessage("");
		this.bankRelationshipDate.setErrorMessage("");
		this.paidUpCapital.setErrorMessage("");
		this.authorizedCapital.setErrorMessage("");
		this.reservesAndSurPlus.setErrorMessage("");
		this.intangibleAssets.setErrorMessage("");
		this.tangibleNetWorth.setErrorMessage("");
		this.longTermLiabilities.setErrorMessage("");
		this.capitalEmployed.setErrorMessage("");
		this.investments.setErrorMessage("");
		this.nonCurrentAssets.setErrorMessage("");
		this.netWorkingCapital.setErrorMessage("");
		this.netSales.setErrorMessage("");
		this.otherIncome.setErrorMessage("");
		this.netProfitAfterTax.setErrorMessage("");
		this.depreciation.setErrorMessage("");
		this.cashAccurals.setErrorMessage("");
		this.annualTurnover.setErrorMessage("");
		this.returnOnCapitalEmp.setErrorMessage("");
		this.currentAssets.setErrorMessage("");
		this.currentLiabilities.setErrorMessage("");
		this.currentBookValue.setErrorMessage("");
		this.currentMarketValue.setErrorMessage("");
		this.promotersShare.setErrorMessage("");
		this.associatesShare.setErrorMessage("");
		this.publicShare.setErrorMessage("");
		this.finInstShare.setErrorMessage("");
		this.others.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CorporateCustomerDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CorporateCustomerDetail aCorporateCustomerDetail = new CorporateCustomerDetail();
		BeanUtils.copyProperties(getCorporateCustomerDetail(), aCorporateCustomerDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") +
		"\n\n --> " + aCorporateCustomerDetail.getLovDescCustCIF();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCorporateCustomerDetail.getRecordType()).equals("")){
				aCorporateCustomerDetail.setVersion(aCorporateCustomerDetail.getVersion()+1);
				aCorporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCorporateCustomerDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCorporateCustomerDetail,tranType)){
					refreshList();
					closeDialog(this.window_CorporateCustomerDetailDialog, "CorporateCustomerDetail"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CorporateCustomerDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		final CorporateCustomerDetail aCorporateCustomerDetail = getCorporateCustomerDetailService().
		getNewCorporateCustomerDetail();
		setCorporateCustomerDetail(aCorporateCustomerDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.name.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCorporateCustomerDetail().isNewRecord()){
			this.btnCancel.setVisible(false);	
		}else{
			this.btnCancel.setVisible(true);
		}

		this.custCIF.setReadonly(true);
		this.name.setReadonly(isReadOnly("CorporateCustomerDetailDialog_name"));
		this.phoneNumber.setReadonly(isReadOnly("CorporateCustomerDetailDialog_phoneNumber"));
		this.phoneNumber1.setReadonly(isReadOnly("CorporateCustomerDetailDialog_phoneNumber1"));
		this.emailId.setReadonly(isReadOnly("CorporateCustomerDetailDialog_emailId"));
		this.bussCommenceDate.setDisabled(isReadOnly("CorporateCustomerDetailDialog_bussCommenceDate"));
		this.servCommenceDate.setDisabled(isReadOnly("CorporateCustomerDetailDialog_servCommenceDate"));
		this.bankRelationshipDate.setDisabled(isReadOnly("CorporateCustomerDetailDialog_bankRelationshipDate"));
		this.paidUpCapital.setReadonly(isReadOnly("CorporateCustomerDetailDialog_paidUpCapital"));
		this.authorizedCapital.setReadonly(isReadOnly("CorporateCustomerDetailDialog_authorizedCapital"));
		this.reservesAndSurPlus.setReadonly(isReadOnly("CorporateCustomerDetailDialog_reservesAndSurPlus"));
		this.intangibleAssets.setReadonly(isReadOnly("CorporateCustomerDetailDialog_intangibleAssets"));
		this.tangibleNetWorth.setReadonly(isReadOnly("CorporateCustomerDetailDialog_tangibleNetWorth"));
		this.longTermLiabilities.setReadonly(isReadOnly("CorporateCustomerDetailDialog_longTermLiabilities"));
		this.capitalEmployed.setReadonly(isReadOnly("CorporateCustomerDetailDialog_capitalEmployed"));
		this.investments.setReadonly(isReadOnly("CorporateCustomerDetailDialog_investments"));
		this.nonCurrentAssets.setReadonly(isReadOnly("CorporateCustomerDetailDialog_nonCurrentAssets"));
		this.netWorkingCapital.setReadonly(isReadOnly("CorporateCustomerDetailDialog_netWorkingCapital"));
		this.netSales.setReadonly(isReadOnly("CorporateCustomerDetailDialog_netSales"));
		this.otherIncome.setReadonly(isReadOnly("CorporateCustomerDetailDialog_otherIncome"));
		this.netProfitAfterTax.setReadonly(isReadOnly("CorporateCustomerDetailDialog_netProfitAfterTax"));
		this.depreciation.setReadonly(isReadOnly("CorporateCustomerDetailDialog_depreciation"));
		this.cashAccurals.setReadonly(isReadOnly("CorporateCustomerDetailDialog_cashAccurals"));
		this.annualTurnover.setReadonly(isReadOnly("CorporateCustomerDetailDialog_annualTurnover"));
		this.returnOnCapitalEmp.setReadonly(isReadOnly("CorporateCustomerDetailDialog_returnOnCapitalEmp"));
		this.currentAssets.setReadonly(isReadOnly("CorporateCustomerDetailDialog_currentAssets"));
		this.currentLiabilities.setReadonly(isReadOnly("CorporateCustomerDetailDialog_currentLiabilities"));
		this.currentBookValue.setReadonly(isReadOnly("CorporateCustomerDetailDialog_currentBookValue"));
		this.currentMarketValue.setReadonly(isReadOnly("CorporateCustomerDetailDialog_currentMarketValue"));
		this.promotersShare.setReadonly(isReadOnly("CorporateCustomerDetailDialog_promotersShare"));
		this.associatesShare.setReadonly(isReadOnly("CorporateCustomerDetailDialog_associatesShare"));
		this.publicShare.setReadonly(isReadOnly("CorporateCustomerDetailDialog_publicShare"));
		this.finInstShare.setReadonly(isReadOnly("CorporateCustomerDetailDialog_finInstShare"));
		this.others.setReadonly(isReadOnly("CorporateCustomerDetailDialog_others"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.corporateCustomerDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.name.setReadonly(true);
		this.phoneNumber.setReadonly(true);
		this.phoneNumber1.setReadonly(true);
		this.emailId.setReadonly(true);
		this.bussCommenceDate.setDisabled(true);
		this.servCommenceDate.setDisabled(true);
		this.bankRelationshipDate.setDisabled(true);
		this.paidUpCapital.setReadonly(true);
		this.authorizedCapital.setReadonly(true);
		this.reservesAndSurPlus.setReadonly(true);
		this.intangibleAssets.setReadonly(true);
		this.tangibleNetWorth.setReadonly(true);
		this.longTermLiabilities.setReadonly(true);
		this.capitalEmployed.setReadonly(true);
		this.investments.setReadonly(true);
		this.nonCurrentAssets.setReadonly(true);
		this.netWorkingCapital.setReadonly(true);
		this.netSales.setReadonly(true);
		this.otherIncome.setReadonly(true);
		this.netProfitAfterTax.setReadonly(true);
		this.depreciation.setReadonly(true);
		this.cashAccurals.setReadonly(true);
		this.annualTurnover.setReadonly(true);
		this.returnOnCapitalEmp.setReadonly(true);
		this.currentAssets.setReadonly(true);
		this.currentLiabilities.setReadonly(true);
		this.currentBookValue.setReadonly(true);
		this.currentMarketValue.setReadonly(true);
		this.promotersShare.setReadonly(true);
		this.associatesShare.setReadonly(true);
		this.publicShare.setReadonly(true);
		this.finInstShare.setReadonly(true);
		this.others.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		// remove validation, if there are a save before
		this.custCIF.setValue("");
		this.name.setValue("");
		this.phoneNumber.setValue("");
		this.phoneNumber1.setValue("");
		this.emailId.setValue("");
		this.bussCommenceDate.setText("");
		this.servCommenceDate.setText("");
		this.bankRelationshipDate.setText("");
		this.paidUpCapital.setValue("");
		this.authorizedCapital.setValue("");
		this.reservesAndSurPlus.setValue("");
		this.intangibleAssets.setValue("");
		this.tangibleNetWorth.setValue("");
		this.longTermLiabilities.setValue("");
		this.capitalEmployed.setValue("");
		this.investments.setValue("");
		this.nonCurrentAssets.setValue("");
		this.netWorkingCapital.setValue("");
		this.netSales.setValue("");
		this.otherIncome.setValue("");
		this.netProfitAfterTax.setValue("");
		this.depreciation.setValue("");
		this.cashAccurals.setValue("");
		this.annualTurnover.setValue("");
		this.returnOnCapitalEmp.setValue("");
		this.currentAssets.setValue("");
		this.currentLiabilities.setValue("");
		this.currentBookValue.setValue("");
		this.currentMarketValue.setValue("");
		this.promotersShare.setValue("");
		this.associatesShare.setValue("");
		this.publicShare.setValue("");
		this.finInstShare.setValue("");
		this.others.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CorporateCustomerDetail aCorporateCustomerDetail = new CorporateCustomerDetail();
		BeanUtils.copyProperties(getCorporateCustomerDetail(), aCorporateCustomerDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CorporateCustomerDetail object with the components data
		doWriteComponentsToBean(aCorporateCustomerDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCorporateCustomerDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCorporateCustomerDetail.getRecordType()).equals("")){
				aCorporateCustomerDetail.setVersion(aCorporateCustomerDetail.getVersion()+1);
				if(isNew){
					aCorporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCorporateCustomerDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCorporateCustomerDetail.setNewRecord(true);
				}
			}
		}else{
			aCorporateCustomerDetail.setVersion(aCorporateCustomerDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if(doProcess(aCorporateCustomerDetail,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CorporateCustomerDetailDialog, "CorporateCustomerDetail");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCorporateCustomerDetail
	 *            (CorporateCustomerDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CorporateCustomerDetail aCorporateCustomerDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCorporateCustomerDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCorporateCustomerDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCorporateCustomerDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCorporateCustomerDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCorporateCustomerDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCorporateCustomerDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(
						taskId,aCorporateCustomerDetail))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCorporateCustomerDetail.setTaskId(taskId);
			aCorporateCustomerDetail.setNextTaskId(nextTaskId);
			aCorporateCustomerDetail.setRoleCode(getRole());
			aCorporateCustomerDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCorporateCustomerDetail, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCorporateCustomerDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCorporateCustomerDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCorporateCustomerDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");

		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CorporateCustomerDetail aCorporateCustomerDetail = (CorporateCustomerDetail) 
		auditHeader.getAuditDetail().getModelData();
		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCorporateCustomerDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCorporateCustomerDetailService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCorporateCustomerDetailService().doApprove(auditHeader);
						if(aCorporateCustomerDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doReject)){
						auditHeader = getCorporateCustomerDetailService().doReject(auditHeader);
						if(aCorporateCustomerDetail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, 
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(
								this.window_CorporateCustomerDetailDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CorporateCustomerDetailDialog,
						auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
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
		}
		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCorporateCustomerDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CorporateCustomerDetail aCorporateCustomerDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCorporateCustomerDetail.getBefImage(), 
				aCorporateCustomerDetail);   
		return new AuditHeader(String.valueOf(aCorporateCustomerDetail.getCustId())
				,String.valueOf(aCorporateCustomerDetail.getCustId()),null,
				null,auditDetail,aCorporateCustomerDetail.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CorporateCustomerDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

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
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CorporateCustomerDetail> soCorporateCustomerDetail = getCorporateCustomerDetailListCtrl().getSearchObj();
		getCorporateCustomerDetailListCtrl().pagingCorporateCustomerDetailList.setActivePage(0);
		getCorporateCustomerDetailListCtrl().getPagedListWrapper().setSearchObject(soCorporateCustomerDetail);
		if(getCorporateCustomerDetailListCtrl().listBoxCorporateCustomerDetail!=null){
			getCorporateCustomerDetailListCtrl().listBoxCorporateCustomerDetail.getListModel();
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CorporateCustomerDetail");
		notes.setReference(String.valueOf(getCorporateCustomerDetail().getCustId()));
		notes.setVersion(getCorporateCustomerDetail().getVersion());
		logger.debug("Leaving");
		return notes;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CorporateCustomerDetail getCorporateCustomerDetail() {
		return this.corporateCustomerDetail;
	}
	public void setCorporateCustomerDetail(CorporateCustomerDetail corporateCustomerDetail) {
		this.corporateCustomerDetail = corporateCustomerDetail;
	}

	public void setCorporateCustomerDetailService(
			CorporateCustomerDetailService corporateCustomerDetailService) {
		this.corporateCustomerDetailService = corporateCustomerDetailService;
	}
	public CorporateCustomerDetailService getCorporateCustomerDetailService() {
		return this.corporateCustomerDetailService;
	}

	public void setCorporateCustomerDetailListCtrl(
			CorporateCustomerDetailListCtrl corporateCustomerDetailListCtrl) {
		this.corporateCustomerDetailListCtrl = corporateCustomerDetailListCtrl;
	}
	public CorporateCustomerDetailListCtrl getCorporateCustomerDetailListCtrl() {
		return this.corporateCustomerDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

}
