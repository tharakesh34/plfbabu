/**
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
 * FileName    		:  ContractorAssetDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-09-2013    														*
 *                                                                  						*
 * Modified Date    :  27-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-09-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.contractor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.service.finance.contractor.ContractorAssetDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.DisbursementDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/FinanceDetails/ContractorAssetDetail/contractorAssetDetailDialog.zul file.
 */
public class ContractorAssetDetailDialogCtrl extends GFCBaseCtrl<ContractorAssetDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ContractorAssetDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ContractorAssetDetailDialog; 

	protected Longbox	    custID;
	protected Textbox 		custIDName;
	protected Label	        labelCustIDName;
	protected Button	   	btnSearchcustID;
	protected Textbox 		finReference; 
	protected Textbox 		contractorName; 
	protected Decimalbox 	dftRetentionPerc; 
	protected Datebox	 	retentionTillDate; 
	protected Space	 		space_RetentionTillDate; 
	protected Textbox 		assetDesc; 
	protected CurrencyBox	assetValue; 

	private boolean 		enqModule=false;

	// not auto wired vars
	private ContractorAssetDetail contractorAssetDetail; // overhanded per param
	private transient DisbursementDetailDialogCtrl disbursementDetailDialogCtrl; // overhanded per param
	private boolean newRecord=false;
	private boolean newContractor=false;
	private int ccyFormatter = 0;
	private Date startDate = null;
	private Date grcEndDate = null;

	
	// ServiceDAOs / Domain Classes
	private transient ContractorAssetDetailService contractorAssetDetailService;
	private List<ContractorAssetDetail> contractorAssetDetails;
	
	/**
	 * default constructor.<br>
	 */
	public ContractorAssetDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ContractorAssetDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected ContractorAssetDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_ContractorAssetDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ContractorAssetDetailDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("contractorAssetDetail")) {
				this.contractorAssetDetail = (ContractorAssetDetail) arguments.get("contractorAssetDetail");
				ContractorAssetDetail befImage = new ContractorAssetDetail();
				BeanUtils.copyProperties(this.contractorAssetDetail, befImage);
				this.contractorAssetDetail.setBefImage(befImage);

				setContractorAssetDetail(this.contractorAssetDetail);
			} else {
				setContractorAssetDetail(null);
			}

			if(getContractorAssetDetail().isNewRecord()){
				setNewRecord(true);
			}

			if(arguments.containsKey("disbursementDetailDialogCtrl")){
				setDisbursementDetailDialogCtrl((DisbursementDetailDialogCtrl)arguments.get("disbursementDetailDialogCtrl"));
			}
			
			if (arguments.containsKey("startDate")) {
				this.startDate = (Date) arguments.get("startDate");
			} 
			if (arguments.containsKey("grcEndDate")) {
				this.grcEndDate = (Date) arguments.get("grcEndDate");
			} 

			if(arguments.containsKey("financeMainDialogCtrl")){
				ccyFormatter = CurrencyUtil.getFormat(getDisbursementDetailDialogCtrl().getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

				setNewContractor(true);
				if(arguments.containsKey("newRecord")){
					setNewRecord(true);
				}else{
					setNewRecord(false);
				}

				this.contractorAssetDetail.setWorkflowId(0);
				if(arguments.containsKey("roleCode")){
					setRole((String) arguments.get("roleCode"));
					getUserWorkspace().allocateRoleAuthorities((String) arguments.get("roleCode"), "ContractorAssetDetailDialog");
				}
			}else{
				ccyFormatter = 2;
			}

			doLoadWorkFlow(this.contractorAssetDetail.isWorkflow(),this.contractorAssetDetail.getWorkflowId(),this.contractorAssetDetail.getNextTaskId());

			if (isWorkFlowEnabled()){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ContractorAssetDetailDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getContractorAssetDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_ContractorAssetDetailDialog.onClose();
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doWriteBeanToComponents(this.contractorAssetDetail.getBefImage());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_ContractorAssetDetailDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *			  An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	public void onChange$custIDName(Event event) {
		logger.debug("Entering" + event.toString());

		this.custIDName.clearErrorMessage();

		Customer details = (Customer)PennantAppUtil.getCustomerObject(this.custIDName.getValue(), null);

		if(details == null) {
			this.custID.setValue(Long.valueOf(0));
			this.labelCustIDName.setValue("");
			//throw new WrongValueException( this.custIDName, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CommitmentDialog_custID.value") }));
		} else {
			this.custID.setValue(Long.valueOf(details.getCustID()));
			this.custIDName.setValue(String.valueOf(details.getCustCIF()));
			this.labelCustIDName.setValue(details.getCustShrtName());
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$dftRetentionPerc(Event event) {
		logger.debug("Entering" + event.toString());
		this.dftRetentionPerc.clearErrorMessage();
		this.retentionTillDate.setDisabled(true);
		this.space_RetentionTillDate.setSclass("");
		if(this.dftRetentionPerc.getValue() != null  && this.dftRetentionPerc.getValue().compareTo(BigDecimal.ZERO) > 0){
			this.retentionTillDate.setDisabled(isReadOnly("ContractorAssetDetailDialog_RetentionTillDate"));
			this.space_RetentionTillDate.setSclass(PennantConstants.mandateSclass);
		}
		if(this.dftRetentionPerc.getValue() == null || this.dftRetentionPerc.getValue().compareTo(BigDecimal.ZERO) == 0){
			this.retentionTillDate.setText("");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onClick$btnSearchcustID(Event event) {

		Object dataObject = ExtendedSearchListBox.show(this.window_ContractorAssetDetailDialog, "Customer");
		if (dataObject instanceof String) {
			this.custID.setText("");
			this.custIDName.setValue("");
			this.labelCustIDName.setValue("");
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setValue(Long.valueOf(details.getCustID()));
				this.custIDName.setValue(String.valueOf(details.getCustCIF()));
				this.labelCustIDName.setValue(details.getCustShrtName());
			}
		}
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
		logger.debug("Entering" +event.toString());
		try {			
			ScreenCTL.displayNotes(getNotes("ContractorAssetDetail",getContractorAssetDetail().
					getFinReference(),getContractorAssetDetail().getVersion()),this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" +event.toString());

	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aContractorAssetDetail
	 * @throws Exception
	 */
	public void doShowDialog(ContractorAssetDetail aContractorAssetDetail) throws Exception {
		logger.debug("Entering");

		try {
			
			// set ReadOnly mode accordingly if the object is new or not.
			if (isNewRecord()) {
				this.btnCtrl.setInitNew();
				doEdit();
				// setFocus
				this.contractorName.focus();
			} else {
				if (isNewContractor()) {
					doEdit();
				} else if (isWorkFlowEnabled()) {
					this.btnNotes.setVisible(true);
					doEdit();
				} else {
					this.btnCtrl.setBtnStatus_Enquiry();
					doReadOnly();
					btnCancel.setVisible(false);
				}
			}

			// fill the components with the data
			doWriteBeanToComponents(aContractorAssetDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			getBorderLayoutHeight();
			this.window_ContractorAssetDetailDialog.setHeight(this.borderLayoutHeight-200+"px");
			this.window_ContractorAssetDetailDialog.setWidth("70%");
			this.window_ContractorAssetDetailDialog.doModal();
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_ContractorAssetDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {
			if (isNewContractor()) {
				this.btnCancel.setVisible(false);
			}
			this.contractorName.setReadonly(isReadOnly("ContractorAssetDetailDialog_ContractorName"));
		} else {
			this.custIDName.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.contractorName.setReadonly(true);
		}
		
		this.custID.setReadonly(true);
		this.finReference.setReadonly(isReadOnly("ContractorAssetDetailDialog_FinReference"));
		this.custIDName.setReadonly(isReadOnly("ContractorAssetDetailDialog_custID"));
		this.btnSearchcustID.setVisible(!isReadOnly("ContractorAssetDetailDialog_custID"));
		this.btnSearchcustID.setDisabled(isReadOnly("ContractorAssetDetailDialog_custID"));
		this.assetDesc.setReadonly(isReadOnly("ContractorAssetDetailDialog_AssetDesc"));
		this.assetValue.setDisabled(isReadOnly("ContractorAssetDetailDialog_AssetValue"));
		this.dftRetentionPerc.setDisabled(isReadOnly("ContractorAssetDetailDialog_DftRetentionPerc"));
		this.retentionTillDate.setDisabled(true);
		if(this.dftRetentionPerc.getValue() != null  && this.dftRetentionPerc.getValue().compareTo(BigDecimal.ZERO) > 0){
			this.retentionTillDate.setDisabled(isReadOnly("ContractorAssetDetailDialog_RetentionTillDate"));
			this.space_RetentionTillDate.setSclass(PennantConstants.mandateSclass);
		}
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.contractorAssetDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if(newContractor){
				if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newContractor);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled() || isNewContractor()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}


	public void doReadOnly() {
		logger.debug("Entering");
		this.custID.setReadonly(true);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		
		this.finReference.setReadonly(true);
		this.contractorName.setReadonly(true);
		this.dftRetentionPerc.setDisabled(true);
		this.retentionTillDate.setDisabled(true);
		this.custIDName.setReadonly(true);
		this.btnSearchcustID.setVisible(false);
		this.btnSearchcustID.setDisabled(true);
		this.assetDesc.setReadonly(true);
		this.assetValue.setDisabled(true);
		
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	// Helpers

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
		if(!enqModule){
			getUserWorkspace().allocateAuthorities("ContractorAssetDetailDialog", getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ContractorAssetDetailDialog_btnSave"));
			this.btnCancel.setVisible(false);
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.finReference.setMaxlength(50);
		this.contractorName.setMaxlength(50);
		this.assetValue.setMandatory(true);
		this.assetValue.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.assetValue.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.assetValue.setScale(ccyFormatter);
		this.assetDesc.setMaxlength(50);
		this.dftRetentionPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.retentionTillDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		if (isWorkFlowEnabled()){
			if(enqModule){
				groupboxWf.setVisible(false);
			}
		}else{
			groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aContractorAssetDetail
	 *            ContractorAssetDetail
	 */
	public void doWriteBeanToComponents(ContractorAssetDetail aContractorAssetDetail) {
		logger.debug("Entering") ;
		this.custID.setValue(aContractorAssetDetail.getCustID());
		this.custIDName.setValue(aContractorAssetDetail.getLovDescCustCIF());
		this.contractorName.setValue(aContractorAssetDetail.getContractorName());
		this.labelCustIDName.setValue(aContractorAssetDetail.getLovDescCustShrtName());
		this.finReference.setValue(aContractorAssetDetail.getFinReference());
		this.finReference.setValue(aContractorAssetDetail.getFinReference());
		this.assetDesc.setValue(aContractorAssetDetail.getAssetDesc());
		this.assetValue.setValue(PennantApplicationUtil.formateAmount(aContractorAssetDetail.getAssetValue(), ccyFormatter));
		this.dftRetentionPerc.setValue(aContractorAssetDetail.getDftRetentionPerc());
		this.retentionTillDate.setValue(aContractorAssetDetail.getRetentionTillDate());

		this.recordStatus.setValue(aContractorAssetDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aContractorAssetDetail
	 */
	public void doWriteComponentsToBean(ContractorAssetDetail aContractorAssetDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Fin Reference
		try {
			aContractorAssetDetail.setFinReference(this.finReference.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aContractorAssetDetail.setContractorName(this.contractorName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		try {
			aContractorAssetDetail.setLovDescCustCIF(this.custIDName.getValue());
			aContractorAssetDetail.setLovDescCustShrtName(this.labelCustIDName.getValue());
			aContractorAssetDetail.setCustID(this.custID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Asset Desc
		try {
			aContractorAssetDetail.setAssetDesc(this.assetDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Default Retention percentage
		try {
			aContractorAssetDetail.setDftRetentionPerc(this.dftRetentionPerc.getValue() == null ? BigDecimal.ZERO : this.dftRetentionPerc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Retention Till Date
		try {
			aContractorAssetDetail.setRetentionTillDate(this.retentionTillDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Asset Value
		try {
			if(this.assetValue.getValidateValue()!=null){
				aContractorAssetDetail.setAssetValue(PennantApplicationUtil.unFormateAmount(this.assetValue.getActualValue(), ccyFormatter));
			}
			if(aContractorAssetDetail.getAssetValue().compareTo(aContractorAssetDetail.getTotClaimAmt()) < 0){
				throw new WrongValueException(this.assetValue, "Asset Value cannot be Less than the claim amount of " + 
						PennantApplicationUtil.formateAmount(aContractorAssetDetail.getTotClaimAmt(), ccyFormatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		if(aContractorAssetDetail.getAssetValue().compareTo(BigDecimal.ZERO) != 0){
			BigDecimal amount = (aContractorAssetDetail.getTotClaimAmt().divide(aContractorAssetDetail.getAssetValue(), 
					2, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100)) ;

			aContractorAssetDetail.setLovDescClaimPercent(PennantApplicationUtil.unFormateAmount(amount, 2));
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		//Contractor Name
		if (!this.contractorName.isReadonly()){
			this.contractorName.setConstraint(new PTStringValidator(Labels.getLabel("label_ContractorAssetDetailDialog_ContractorName.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		
		//Asset Value
		if (!this.assetValue.isReadonly()){
			this.assetValue.setConstraint(new PTDecimalValidator(Labels.getLabel("label_ContractorAssetDetailDialog_AssetValue.value"), ccyFormatter, true, false, 0));
		}
		
		//Asset Desc
		if (!this.assetDesc.isReadonly()){
			this.assetDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_ContractorAssetDetailDialog_AssetDesc.value"),PennantRegularExpressions.REGEX_ALPHANUM_SPACE,true));
		}
		
		//Default Retention Percentage
		BigDecimal retentionPerc = this.dftRetentionPerc.getValue() == null ? BigDecimal.ZERO : this.dftRetentionPerc.getValue();
		if (!this.dftRetentionPerc.isDisabled()){
			this.dftRetentionPerc.setConstraint(new PTDecimalValidator(Labels.getLabel(
					"label_ContractorAssetDetailDialog_DftRetentionPerc.value"), 2, true, false, 99));
		}
		
		//Retention Till Date
		if (!this.retentionTillDate.isDisabled() && retentionPerc.compareTo(BigDecimal.ZERO) > 0){
			this.retentionTillDate.setConstraint(new PTDateValidator(Labels.getLabel("label_ContractorAssetDetailDialog_RetentionTillDate.value"),
					true, startDate, grcEndDate,true));
		}
	
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.finReference.setConstraint("");
		this.assetDesc.setConstraint("");
		this.assetValue.setConstraint("");
		this.contractorName.setConstraint("");
		this.dftRetentionPerc.setConstraint("");
		this.retentionTillDate.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		if (this.btnSearchcustID.isVisible() && !this.btnSearchcustID.isDisabled()) {
			this.custIDName.setConstraint(new PTStringValidator(Labels.getLabel("label_ContractorAssetDetailDialog_custID.value"),null,false));
		}
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveLOVValidation() {
		this.custIDName.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custIDName.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.assetDesc.setErrorMessage("");
		this.assetValue.setErrorMessage("");
		this.contractorName.setErrorMessage("");
		this.dftRetentionPerc.setErrorMessage("");
		this.retentionTillDate.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a ContractorAssetDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final ContractorAssetDetail aContractorAssetDetail = new ContractorAssetDetail();
		BeanUtils.copyProperties(getContractorAssetDetail(), aContractorAssetDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " +
				Labels.getLabel("label_ContractorAssetDetailDialog_ContractorName.value")+" : "+aContractorAssetDetail.getContractorName();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if(getDisbursementDetailDialogCtrl() != null){
				List<FinanceDisbursement> list = getDisbursementDetailDialogCtrl().getDisbursementDetails();
				if(list != null && !list.isEmpty()){
					for (FinanceDisbursement disbursement : list) {
						if(disbursement.getContractorId() == aContractorAssetDetail.getContractorId() && 
								!StringUtils.trimToEmpty(disbursement.getRecordType()).equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)){
							MessageUtil.showError(
									"Not Allowed to Delete This Record. Disbursement Details Exist on this Contractor.");
							return;
						}
					}
				}
			}

			if (StringUtils.isBlank(aContractorAssetDetail.getRecordType())){
				aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion()+1);
				aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aContractorAssetDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aContractorAssetDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aContractorAssetDetail.getNextTaskId(), aContractorAssetDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(isNewContractor()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newContractor(aContractorAssetDetail, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_ContractorAssetDetailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getDisbursementDetailDialogCtrl().doFillContractorDetails(this.contractorAssetDetails);
						closeDialog();
					}	
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}


	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.finReference.setValue("");
		this.assetDesc.setValue("");
		this.assetValue.setValue("");
		this.contractorName.setValue("");
		this.dftRetentionPerc.setValue(BigDecimal.ZERO);
		this.retentionTillDate.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final ContractorAssetDetail aContractorAssetDetail = new ContractorAssetDetail();
		BeanUtils.copyProperties(getContractorAssetDetail(), aContractorAssetDetail);
		boolean isNew = false;

		if(isWorkFlowEnabled()){
			aContractorAssetDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aContractorAssetDetail.getNextTaskId(), aContractorAssetDetail);
		}

		// force validation, if on, than execute by component.getValue()
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aContractorAssetDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the ContractorAssetDetail object with the components data
			doWriteComponentsToBean(aContractorAssetDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aContractorAssetDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aContractorAssetDetail.getRecordType())){
				aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion()+1);
				if(isNew){
					aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aContractorAssetDetail.setNewRecord(true);
				}
			}
		}else{

			if(isNewContractor()){
				if(isNewRecord()){
					aContractorAssetDetail.setVersion(1);
					aContractorAssetDetail.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType = PennantConstants.TRAN_UPD;
				}

				if(StringUtils.isBlank(aContractorAssetDetail.getRecordType())){
					aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion()+1);
					aContractorAssetDetail.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aContractorAssetDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}
			}else{
				aContractorAssetDetail.setVersion(aContractorAssetDetail.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}			
		}

		// save it to database
		try {

			AuditHeader auditHeader =  newContractor(aContractorAssetDetail, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_ContractorAssetDetailDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
				getDisbursementDetailDialogCtrl().doFillContractorDetails(this.contractorAssetDetails);
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}


	private AuditHeader newContractor(ContractorAssetDetail aContractorAssetDetail, String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aContractorAssetDetail, tranType);
		this.contractorAssetDetails = new ArrayList<ContractorAssetDetail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aContractorAssetDetail.getContractorName();
		valueParm[1] = String.valueOf(aContractorAssetDetail.getFinReference());

		errParm[0] = PennantJavaUtil.getLabel("label_ContractorAssetDetailDialog_Contractor.value") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_ContractorAssetDetailDialog_FinReference.value") + ":"+valueParm[1];

		if(getDisbursementDetailDialogCtrl().getContractorAssetDetails() !=null && getDisbursementDetailDialogCtrl().getContractorAssetDetails().size()>0){
			for (int i = 0; i < getDisbursementDetailDialogCtrl().getContractorAssetDetails().size(); i++) {
				ContractorAssetDetail loanDetail = getDisbursementDetailDialogCtrl().getContractorAssetDetails().get(i);

				if(aContractorAssetDetail.getContractorName().equals(loanDetail.getContractorName())){ // Both Current and Existing list same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD,
								"41001",errParm,valueParm), getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if(PennantConstants.TRAN_DEL.equals(tranType)){
						if(PennantConstants.RECORD_TYPE_UPD.equals(aContractorAssetDetail.getRecordType())){
							aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							contractorAssetDetails.add(aContractorAssetDetail);
						}else if(PennantConstants.RCD_ADD.equals(aContractorAssetDetail.getRecordType())){
							recordAdded=true;
						}else if(PennantConstants.RECORD_TYPE_NEW.equals(aContractorAssetDetail.getRecordType())){
							aContractorAssetDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							contractorAssetDetails.add(aContractorAssetDetail);
						}else if(PennantConstants.RECORD_TYPE_CAN.equals(aContractorAssetDetail.getRecordType())){
							recordAdded=true;
							//No Such Case
						}
					}else{
						if(!PennantConstants.TRAN_UPD.equals(tranType)){
							contractorAssetDetails.add(loanDetail);
						}
					}
				}else{
					contractorAssetDetails.add(loanDetail);
				}
			}
		}

		if(!recordAdded){
			contractorAssetDetails.add(aContractorAssetDetail);
		}
		return auditHeader;
	} 

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ContractorAssetDetail aContractorAssetDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aContractorAssetDetail.getBefImage(), aContractorAssetDetail);   
		return new AuditHeader(aContractorAssetDetail.getFinReference(),null,null,null,auditDetail,aContractorAssetDetail.getUserDetails(),getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ContractorAssetDetail getContractorAssetDetail() {
		return this.contractorAssetDetail;
	}
	public void setContractorAssetDetail(ContractorAssetDetail contractorAssetDetail) {
		this.contractorAssetDetail = contractorAssetDetail;
	}

	public void setContractorAssetDetailService(ContractorAssetDetailService contractorAssetDetailService) {
		this.contractorAssetDetailService = contractorAssetDetailService;
	}
	public ContractorAssetDetailService getContractorAssetDetailService() {
		return this.contractorAssetDetailService;
	}

	public void setDisbursementDetailDialogCtrl(DisbursementDetailDialogCtrl disbursementDetailDialogCtrl) {
		this.disbursementDetailDialogCtrl = disbursementDetailDialogCtrl;
	}
	public DisbursementDetailDialogCtrl getDisbursementDetailDialogCtrl() {
		return this.disbursementDetailDialogCtrl;
	}
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public boolean isNewContractor() {
		return newContractor;
	}

	public void setNewContractor(boolean newContractor) {
		this.newContractor = newContractor;
	}

	public List<ContractorAssetDetail> getContractorAssetDetails() {
		return contractorAssetDetails;
	}
	public void setContractorAssetDetails(
			List<ContractorAssetDetail> contractorAssetDetails) {
		this.contractorAssetDetails = contractorAssetDetails;
	}

}
