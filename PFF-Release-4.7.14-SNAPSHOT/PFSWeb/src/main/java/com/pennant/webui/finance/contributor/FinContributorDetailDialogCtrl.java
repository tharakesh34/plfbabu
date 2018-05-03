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
 * FileName    		:  FinContributorDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.contributor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.finance.financemain.ContributorDetailsDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/Finance/Contributor/FinContributorDetailDialog.zul file.
 */
public class FinContributorDetailDialogCtrl extends GFCBaseCtrl<FinContributorDetail> {
	private static final long serialVersionUID = -6959194080451993569L;
	private static final Logger logger = Logger.getLogger(FinContributorDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 		window_FinContributorDetailDialog;

	protected Longbox 		custID; 					
	protected Textbox 		contributorCIF; 			
	protected Label 		contributorName; 			
	protected CurrencyBox	contributorInvestment; 		
	protected Label 		investmentAcctBal;
	protected Textbox 		investmentAcc; 				
	protected Datebox 		investmentDate; 			
	protected Datebox 		recordDate; 				
	protected Decimalbox	contributionPerc; 			
	protected Decimalbox	mudaribPerc; 				
	protected Row 			row_mudaribPerc;
	
	
	protected Label 		label_FinContributorDetailDialog_CustID;
	protected Label 		label_FinContributorDetailDialog_ContributorInvestment;
	protected Label 		label_FinContributorDetailDialog_InvestmentAcc;
	protected Label 		label_FinContributorDetailDialog_InvestmentDate;
	protected Label 		label_FinContributorDetailDialog_RecordDate;
	protected Label 		label_FinContributorDetailDialog_MudaribPerc;
	protected Label 		label_FinContributorDetailDialog_RecordStatus;

	// not auto wired vars
	private FinContributorDetail finContributorDetail; 	// overhanded per param

	private transient boolean validationOn;
	
	protected Button btnSearchCustId; // autowire
	protected Button btnSearchInvestmentAcc; // autowire

	// ServiceDAOs / Domain Classes
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newContributor=false;
	private List<FinContributorDetail> contributorDetails;
	private ContributorDetailsDialogCtrl  contributorDetailsDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String finCcy="";
	private BigDecimal balInvestAmount= BigDecimal.ZERO;
	private BigDecimal finAmount = BigDecimal.ZERO;
	private  int formatter = 0;
	String roleCode = "";
	
	private FinanceDetail financeDetail;
	
	private AccountInterfaceService accountInterfaceService;
	Date appStartDate = DateUtility.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DFT_START_DATE);
	
	/**
	 * default constructor.<br>
	 */
	public FinContributorDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinContributorDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected FinContributorDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */	
	public void onCreate$window_FinContributorDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinContributorDetailDialog);

		if (arguments.containsKey("finContributorDetail")) {
			this.finContributorDetail = (FinContributorDetail) arguments.get("finContributorDetail");
			FinContributorDetail befImage =new FinContributorDetail();
			BeanUtils.copyProperties(this.finContributorDetail, befImage);
			this.finContributorDetail.setBefImage(befImage);
			setFinContributorDetail(this.finContributorDetail);
		} else {
			setFinContributorDetail(null);
		}
		
		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}
		
		if (arguments.containsKey("finCcy")) {
			this.finCcy = (String) arguments.get("finCcy");
		}
		
		if (arguments.containsKey("finAmount")) {
			this.finAmount = (BigDecimal) arguments.get("finAmount");
		}
		
		if (arguments.containsKey("balInvestAmount")) {
			this.balInvestAmount = (BigDecimal) arguments.get("balInvestAmount");
		}
		if (arguments.containsKey("formatter")) {
			this.formatter = (Integer) arguments.get("formatter");
			newContributor=true;
		}
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
 		}

		if(getFinContributorDetail().isNewRecord()){
			setNewRecord(true);
		}

		if(arguments.containsKey("contributorDetailsDialogCtrl")){

			setContributorDetailsDialogCtrl((ContributorDetailsDialogCtrl) arguments.get("contributorDetailsDialogCtrl"));
			setNewContributor(true);

			if(arguments.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.finContributorDetail.setWorkflowId(0);
			if(arguments.containsKey("roleCode")){
				roleCode = (String) arguments.get("roleCode");
				getUserWorkspace().allocateRoleAuthorities(roleCode, "FinContributorDetailDialog");
			}
			formatter = CurrencyUtil.getFormat(this.contributorDetailsDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		}

		doLoadWorkFlow(this.finContributorDetail.isWorkflow(),this.finContributorDetail.getWorkflowId(),
				this.finContributorDetail.getNextTaskId());
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "FinContributorDetailDialog");
		}

		// set Field Properties
		doSetFieldProperties();
		if(this.contributorDetailsDialogCtrl!=null && 
				this.contributorDetailsDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory().equals(
						FinanceConstants.PRODUCT_MUSHARAKA)){
			dosetLabels();
		}
		doShowDialog(getFinContributorDetail());

		//Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() && !isNewContributor()) {
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 * @throws InterruptedException 
	 * @throws SuspendNotAllowedException 
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		
		this.contributorCIF.setMaxlength(6);
		this.mudaribPerc.setScale(formatter);
		
		this.contributorInvestment.setMandatory(true);
		this.contributorInvestment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.contributorInvestment.setScale(formatter);
		this.contributorInvestment.setTextBoxWidth(150);
		
		this.investmentAcc.setMaxlength(50);
		this.mudaribPerc.setMaxlength(6);
		this.mudaribPerc.setScale(2);
		this.mudaribPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.investmentDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.recordDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		readOnlyComponent(true,this.recordDate);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		
		if(this.contributorDetailsDialogCtrl!=null && 
				this.contributorDetailsDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory().equals(FinanceConstants.PRODUCT_MUSHARAKA)){
			this.row_mudaribPerc.setVisible(false);
		}
		
		logger.debug("Leaving");
	}
	
	public void dosetLabels(){
		logger.debug("Entering");
		label_FinContributorDetailDialog_CustID.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_CustID.value"));
		label_FinContributorDetailDialog_ContributorInvestment.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_ContributorInvestment.value"));
	    label_FinContributorDetailDialog_InvestmentAcc.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_InvestmentAcc.value"));
		label_FinContributorDetailDialog_InvestmentDate.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_InvestmentDate.value"));
		label_FinContributorDetailDialog_RecordDate.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_RecordDate.value"));
		label_FinContributorDetailDialog_MudaribPerc.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_MudaribPerc.value"));
		label_FinContributorDetailDialog_RecordStatus.setValue(Labels.getLabel("label_FinMusharakContributorDetailDialog_CustID.value"));
		logger.debug("Leaving");
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
		getUserWorkspace().allocateAuthorities("FinContributorDetailDialog", roleCode);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinContributorDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinContributorDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinContributorDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinContributorDetailDialog_btnSave"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
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
		MessageUtil.showHelpWindow(event, window_FinContributorDetailDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doWriteBeanToComponents(this.finContributorDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinContributorDetail
	 *            FinContributorDetail
	 */
	public void doWriteBeanToComponents(FinContributorDetail aFinContributorDetail) {
		logger.debug("Entering");

		if(aFinContributorDetail.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aFinContributorDetail.getCustID());	
		}
		
		this.contributorCIF.setValue(aFinContributorDetail.getLovDescContributorCIF());
		this.contributorName.setValue(aFinContributorDetail.getContributorName());
		this.contributorInvestment.setValue(PennantAppUtil.formateAmount(
				aFinContributorDetail.getContributorInvest(),formatter));
		this.contributionPerc.setValue(aFinContributorDetail.getTotalInvestPerc());
		
		this.investmentAcc.setValue(PennantApplicationUtil.formatAccountNumber(aFinContributorDetail.getInvestAccount()));
		this.investmentAcctBal.setValue(getAcBalance(aFinContributorDetail.getInvestAccount()));
		this.investmentDate.setValue(aFinContributorDetail.getInvestDate());
		if(isNewRecord()){
			this.recordDate.setValue(DateUtility.getAppDate());
		}else{
			this.recordDate.setValue(aFinContributorDetail.getRecordDate());
			this.mudaribPerc.setValue(aFinContributorDetail.getMudaribPerc());
			this.recordStatus.setValue(aFinContributorDetail.getRecordStatus());
			logger.debug("Leaving");
		}
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinContributorDetail
	 */
	public void doWriteComponentsToBean(FinContributorDetail aFinContributorDetail) {
		logger.debug("Entering");
		doSetLOVValidation();
		
		int formatter = CurrencyUtil.getFormat(this.contributorDetailsDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aFinContributorDetail.setLovDescContributorCIF(this.contributorCIF.getValue());
			aFinContributorDetail.setCustID(this.custID.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinContributorDetail.setContributorName(this.contributorName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			BigDecimal investAmt = PennantAppUtil.unFormateAmount(this.contributorInvestment.getActualValue(), formatter);
			 if(balInvestAmount.compareTo(BigDecimal.ZERO) > 0 && investAmt.compareTo(balInvestAmount) > 0){
				throw new WrongValueException(this.contributorInvestment, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
						new String[] { Labels.getLabel("label_FinContributorDetailDialog_ContributorInvestment.value"),
						PennantAppUtil.amountFormate(balInvestAmount, formatter)}));
			}	
			
			aFinContributorDetail.setContributorInvest(investAmt);
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinContributorDetail.setInvestAccount(PennantApplicationUtil.unFormatAccountNumber(this.investmentAcc.getValue()));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.investmentDate.getValue() == null){
				this.investmentDate.setValue(appStartDate);
			}
			aFinContributorDetail.setInvestDate(this.investmentDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinContributorDetail.setRecordDate(this.recordDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(finAmount.compareTo(BigDecimal.ZERO)>0){
			BigDecimal investAmt = PennantAppUtil.unFormateAmount(this.contributorInvestment.getActualValue(), formatter);
			aFinContributorDetail.setTotalInvestPerc((investAmt.divide(finAmount,2,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aFinContributorDetail.setMudaribPerc(this.mudaribPerc.getValue() == null? BigDecimal.ZERO :this.mudaribPerc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aFinContributorDetail.setRecordStatus(this.recordStatus.getValue());
		setFinContributorDetail(aFinContributorDetail);
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aFinContributorDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinContributorDetail aFinContributorDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.contributorCIF.focus();
		} else {
			this.contributorInvestment.focus();
			if (isNewContributor()){
				doEdit();
			}else  if (isWorkFlowEnabled()){
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
			doWriteBeanToComponents(aFinContributorDetail);

			if(isNewContributor()){
				this.window_FinContributorDetailDialog.setHeight("280px");
				this.window_FinContributorDetailDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_FinContributorDetailDialog.doModal() ;
			}else{
				this.window_FinContributorDetailDialog.setWidth("100%");
				this.window_FinContributorDetailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custID.isReadonly()){
			this.contributorCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_FinContributorDetailDialog_CustID.value"),null,true));
		}
		
		if (!this.btnSearchInvestmentAcc.isDisabled()){
			this.investmentAcc.setConstraint(new PTStringValidator(Labels.getLabel("label_FinContributorDetailDialog_InvestmentAcc.value"),null,true));
		}
		
		if (!this.investmentDate.isReadonly()){
			this.investmentDate.setConstraint(new PTDateValidator(Labels.getLabel("label_FinContributorDetailDialog_InvestmentDate.value"),false,startDate,appStartDate,true));
		}
		
		if (!this.mudaribPerc.isDisabled()){
			this.mudaribPerc.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinContributorDetailDialog_MudaribPerc.value"), 2));
		}
		
		if (!this.contributorInvestment.isReadonly()) {
			int format = CurrencyUtil.getFormat(this.contributorDetailsDialogCtrl.getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());;
			this.contributorInvestment.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_FinContributorDetailDialog_ContributorInvestment.value"), format, true, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		Clients.clearWrongValue(contributorInvestment);
		this.contributorCIF.setConstraint("");
		this.contributorInvestment.setConstraint("");
		this.investmentAcc.setConstraint("");
		this.investmentDate.setConstraint("");
		this.recordDate.setConstraint("");
		this.mudaribPerc.setConstraint("");
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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.contributorCIF.setErrorMessage("");
		this.contributorInvestment.setErrorMessage("");
		this.investmentAcc.setErrorMessage("");
		this.investmentDate.setErrorMessage("");
		this.recordDate.setErrorMessage("");
		this.mudaribPerc.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$contributorInvestment(Event event){
		logger.debug("Entering " + event.toString());
		contributionPercCalc();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Calculation of Contribution Percentage from Contributor
	 */
	private void contributionPercCalc(){
		this.contributorInvestment.setConstraint("");
		this.contributorInvestment.setErrorMessage("");
		
		this.contributionPerc.setValue(BigDecimal.ZERO);
		if(this.contributorInvestment.getActualValue().compareTo(BigDecimal.ZERO) > 0){
			if(finAmount.compareTo(BigDecimal.ZERO) > 0){
				BigDecimal contAmt = PennantApplicationUtil.unFormateAmount(this.contributorInvestment.getActualValue(), formatter);
				this.contributionPerc.setValue(contAmt.divide(finAmount, 2, RoundingMode.HALF_DOWN));
			}
		}	
	}

	// CRUD operations

	/**
	 * Deletes a FinContributorDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final FinContributorDetail aFinContributorDetail = new FinContributorDetail();
		BeanUtils.copyProperties(getFinContributorDetail(), aFinContributorDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_value",
				new String[] {Labels.getLabel("Contributor")})
		+ "\n\n --> " + this.contributorCIF.getText();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinContributorDetail.getRecordType())){
				aFinContributorDetail.setVersion(aFinContributorDetail.getVersion()+1);
				aFinContributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				aFinContributorDetail.setNewRecord(true);

				if (isWorkFlowEnabled()){
					aFinContributorDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(isNewContributor()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCusomerProcess(aFinContributorDetail,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_FinContributorDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getContributorDetailsDialogCtrl().doFillFinContributorDetails(this.contributorDetails,true);
						// send the data back to customer
						closeDialog();
					}	

				}
			}catch (DataAccessException e){
				logger.error("Exception: ", e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()){

			if(isNewContributor()){
				this.btnCancel.setVisible(false);	
			}
			this.btnSearchCustId.setVisible(true);
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchCustId.setVisible(false);
		}
		
		this.contributorCIF.setReadonly(true);
		this.custID.setReadonly(isReadOnly("FinContributorDetailDialog_custID"));
		this.btnSearchCustId.setDisabled(isReadOnly("FinContributorDetailDialog_custID"));
		this.contributorInvestment.setDisabled(isReadOnly("FinContributorDetailDialog_contributorInvestment"));
		this.investmentAcc.setReadonly(true);
		this.btnSearchInvestmentAcc.setDisabled(isReadOnly("FinContributorDetailDialog_investmentAcc"));
		this.investmentDate.setDisabled(isReadOnly("FinContributorDetailDialog_investmentDate"));
		this.recordDate.setDisabled(isReadOnly("FinContributorDetailDialog_recordDate"));
		this.mudaribPerc.setDisabled(isReadOnly("FinContributorDetailDialog_mudaribPerc"));
		
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.finContributorDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{

			if(newContributor){
				if("ENQ".equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newContributor);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewContributor()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custID.setReadonly(true);
		this.contributorCIF.setReadonly(true);
		this.contributorInvestment.setDisabled(true);
		this.investmentAcc.setReadonly(true);
		this.investmentDate.setDisabled(true);
		this.recordDate.setDisabled(true);
		this.mudaribPerc.setDisabled(true);

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
		
		this.custID.setValue(Long.MIN_VALUE);
		this.contributorCIF.setValue("");
		this.contributorName.setValue("");
		this.contributorInvestment.setValue("");
		this.investmentAcc.setValue("");
		this.investmentDate.setText("");
		this.recordDate.setText("");
		this.mudaribPerc.setValue("");
		logger.debug("Leaving");		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final FinContributorDetail aFinContributorDetail = new FinContributorDetail();
		BeanUtils.copyProperties(getFinContributorDetail(), aFinContributorDetail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the FinContributorDetail object with the components data
		doWriteComponentsToBean(aFinContributorDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aFinContributorDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinContributorDetail.getRecordType())){
				aFinContributorDetail.setVersion(aFinContributorDetail.getVersion()+1);
				if(isNew){
					aFinContributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aFinContributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinContributorDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewContributor()){
				if(isNewRecord()){
					aFinContributorDetail.setVersion(1);
					aFinContributorDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aFinContributorDetail.getRecordType())){
					aFinContributorDetail.setVersion(aFinContributorDetail.getVersion()+1);
					aFinContributorDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aFinContributorDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aFinContributorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aFinContributorDetail.setVersion(aFinContributorDetail.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isNewContributor()){
				AuditHeader auditHeader =  newCusomerProcess(aFinContributorDetail,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_FinContributorDetailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getContributorDetailsDialogCtrl().doFillFinContributorDetails(this.contributorDetails,true);
					//true;
					// send the data back to customer
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}


	private AuditHeader newCusomerProcess(FinContributorDetail aFinContributorDetail,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aFinContributorDetail, tranType);
		contributorDetails = new ArrayList<FinContributorDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aFinContributorDetail.getLovDescContributorCIF();
		valueParm[1] = aFinContributorDetail.getContributorName();

		errParm[0] = PennantJavaUtil.getLabel("label_ContributorCIF") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ContributorName") + ":"+valueParm[1];

		if(getContributorDetailsDialogCtrl().getContributorsList()!=null && getContributorDetailsDialogCtrl().getContributorsList().size()>0){
			for (int i = 0; i < getContributorDetailsDialogCtrl().getContributorsList().size(); i++) {
				FinContributorDetail finContributorDetail = getContributorDetailsDialogCtrl().getContributorsList().get(i);

				if(finContributorDetail.getCustID() == aFinContributorDetail.getCustID()){ // Both Current and Existing list rating same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(PennantConstants.TRAN_DEL.equals(tranType)){
						if(PennantConstants.RECORD_TYPE_UPD.equals(aFinContributorDetail.getRecordType())){
							aFinContributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							contributorDetails.add(aFinContributorDetail);
						}else if(PennantConstants.RCD_ADD.equals(aFinContributorDetail.getRecordType())){
							recordAdded=true;
						}else if(aFinContributorDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aFinContributorDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							contributorDetails.add(aFinContributorDetail);
						}else if(PennantConstants.RECORD_TYPE_CAN.equals(aFinContributorDetail.getRecordType())){
							recordAdded=true;
							for (int j = 0; j < getContributorDetailsDialogCtrl().getFinanceDetail().getFinContributorHeader().getContributorDetailList().size(); j++) {
								FinContributorDetail detail =  getContributorDetailsDialogCtrl().getFinanceDetail().getFinContributorHeader().getContributorDetailList().get(j);
								if(detail.getCustID() == aFinContributorDetail.getCustID()){
									contributorDetails.add(detail);
								}
							}
						}
					}else{
						if(!PennantConstants.TRAN_UPD.equals(tranType)){
							contributorDetails.add(finContributorDetail);
						}
					}
				}else{
					contributorDetails.add(finContributorDetail);
				}
			}
		}
		if(!recordAdded){
			contributorDetails.add(aFinContributorDetail);
		}
		return auditHeader;
	} 

	// Search Button Component Events
	
	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchCustId(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	
		map.put("DialogCtrl", this);
		map.put("searchObject",this.newSearchObject);
		
		List<Filter> filterList = new ArrayList<Filter>();
		filterList.add(new Filter("CustCoreBank", " ", Filter.OP_NOT_EQUAL));
		map.put("filtersList",filterList);
		
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",null,map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering"); 
		final Customer aCustomer = (Customer)nCustomer; 		
		this.custID.setValue(aCustomer.getCustID());
		this.contributorCIF.setValue(aCustomer.getCustCIF().trim());
		this.contributorName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}
	
	/**
	 * when clicks on button "btnSearchDisbAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchInvestmentAcc(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		this.contributorCIF.clearErrorMessage();
		if(StringUtils.isNotBlank(this.contributorCIF.getValue())) {
			Object dataObject;

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(finCcy);
			iAccount.setAcType("EACAJK");
			iAccount.setAcCustCIF(this.contributorCIF.getValue());
			iAccount.setDivision(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDivision());
			
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_FinContributorDetailDialog,
						"Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.investmentAcc.setValue(dataObject.toString());
					this.investmentAcctBal.setValue("");
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.investmentAcc.setValue(details.getAccountId());
						this.investmentAcctBal.setValue(getAcBalance(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error("Exception: ", e);
				MessageUtil.showError("Account Details not Found!!!");
			}
		}else {
			throw new WrongValueException(this.contributorCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinContributorDetailDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Fetching Account Balance
	 * @param acId
	 * @return
	 */
	private String getAcBalance(String acId){
		if (StringUtils.isNotBlank(acId)) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId), formatter);
		}else{
			return "";
		}
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(FinContributorDetail aFinContributorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aFinContributorDetail.getBefImage(), aFinContributorDetail);

		return new AuditHeader(getReference(),String.valueOf(aFinContributorDetail.getCustID()), null,
				null, auditDetail, aFinContributorDetail.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinContributorDetailDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
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
		doShowNotes(this.finContributorDetail);
	}

	/** 
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getFinContributorDetail().getCustID()+PennantConstants.KEY_SEPERATOR +
					getFinContributorDetail().getContributorName();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinContributorDetail getFinContributorDetail() {
		return this.finContributorDetail;
	}
	public void setFinContributorDetail(FinContributorDetail customerRating) {
		this.finContributorDetail = customerRating;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}
	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewContributor() {
		return newContributor;
	}
	public void setNewContributor(boolean newContributor) {
		this.newContributor = newContributor;
	}

	public ContributorDetailsDialogCtrl getContributorDetailsDialogCtrl() {
		return contributorDetailsDialogCtrl;
	}
	public void setContributorDetailsDialogCtrl(
			ContributorDetailsDialogCtrl contributorDetailsDialogCtrl) {
		this.contributorDetailsDialogCtrl = contributorDetailsDialogCtrl;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}
	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
 

}
