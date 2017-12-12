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
 * FileName    		:  CustomerIdentityDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeridentity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerIdentityService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerIdentity/customerIdentityDialog.zul
 * file.
 */
public class CustomerIdentityDialogCtrl extends GFCBaseCtrl<CustomerIdentity> {
	private static final long serialVersionUID = -2646507082404896955L;
	private static final Logger logger = Logger.getLogger(CustomerIdentityDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window  		window_CustomerIdentityDialog; 	// autowired

	protected Longbox 		idCustID; 						// autowired
	protected Textbox 		idType; 						// autowired
	protected Textbox 		idIssuedBy; 					// autowired
	protected Textbox 		idRef; 							// autowired
	protected Textbox 		idIssueCountry; 				// autowired
	protected Datebox 		idIssuedOn; 					// autowired
	protected Datebox 		idExpiresOn; 					// autowired
	protected Textbox 		idLocation; 					// autowired
	protected Textbox 	 	custCIF;						// autoWired
	protected Label   	 	custShrtName;					// autoWired


	// not auto wired vars
	private CustomerIdentity customerIdentity; // overhanded per param
	private transient CustomerIdentityListCtrl customerIdentityListCtrl; // overhanded per param

	private transient boolean validationOn;
	
	protected Button btnSearchPRCustid; // autowire

	protected Button btnSearchIdType; // autowire
	protected Textbox lovDescIdTypeName;
	
	protected Button btnSearchIdIssueCountry; // autowire
	protected Textbox lovDescIdIssueCountryName;
	

	// ServiceDAOs / Domain Classes
	private transient CustomerIdentityService customerIdentityService;
	private transient PagedListService pagedListService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;


	private boolean newRecord=false;
	private boolean newCustomer=false;
	protected JdbcSearchObject<Customer> newSearchObject ;
	Date appStartDate = DateUtility.getAppDate();
	Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	
	/**
	 * default constructor.<br>
	 */
	public CustomerIdentityDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerIdentityDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerIdentity object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerIdentityDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerIdentityDialog);

		try{
		/* set components visible dependent of the users rights */
		doCheckRights();

		if (arguments.containsKey("customerIdentity")) {
			this.customerIdentity = (CustomerIdentity) arguments.get("customerIdentity");
			CustomerIdentity befImage =new CustomerIdentity();
			BeanUtils.copyProperties(this.customerIdentity, befImage);
			this.customerIdentity.setBefImage(befImage);

			setCustomerIdentity(this.customerIdentity);
		} else {
			setCustomerIdentity(null);
		}

		if(getCustomerIdentity().isNewRecord()){
			setNewRecord(true);
		}


		doLoadWorkFlow(this.customerIdentity.isWorkflow(),this.customerIdentity.getWorkflowId(),this.customerIdentity.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerIdentityDialog");
		}

		// READ OVERHANDED params !
		// we get the customerIdentityListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerIdentity here.
		if (arguments.containsKey("customerIdentityListCtrl")) {
			setCustomerIdentityListCtrl((CustomerIdentityListCtrl) arguments.get("customerIdentityListCtrl"));
		} else {
			setCustomerIdentityListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerIdentity());
		if (getCustomerIdentity().isNewRecord()) {
			onload();
		}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerIdentityDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.idType.setMaxlength(8);
		this.idIssuedBy.setMaxlength(50);
		this.idRef.setMaxlength(50);
		this.idIssueCountry.setMaxlength(2);
		this.idIssuedOn.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.idExpiresOn.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.idLocation.setMaxlength(100);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
			this.groupboxWf.setVisible(false);
			
		}
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
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerIdentityDialog);
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
		doWriteBeanToComponents(this.customerIdentity.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerIdentity
	 *            CustomerIdentity
	 */
	public void doWriteBeanToComponents(CustomerIdentity aCustomerIdentity) {
		logger.debug("Entering");
		this.idCustID.setValue(aCustomerIdentity.getIdCustID());
		this.idType.setValue(aCustomerIdentity.getIdType());
		this.idIssuedBy.setValue(aCustomerIdentity.getIdIssuedBy());
		this.idRef.setValue(aCustomerIdentity.getIdRef());
		this.idIssueCountry.setValue(aCustomerIdentity.getIdIssueCountry());
		this.idIssuedOn.setValue(aCustomerIdentity.getIdIssuedOn());
		this.idExpiresOn.setValue(aCustomerIdentity.getIdExpiresOn());
		this.idLocation.setValue(aCustomerIdentity.getIdLocation());
		this.custCIF.setValue(aCustomerIdentity.getLovDescCustCIF()==null?"":aCustomerIdentity.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerIdentity.getLovDescCustShrtName()==null?"":aCustomerIdentity.getLovDescCustShrtName().trim());

		if (aCustomerIdentity.isNewRecord()){
			this.lovDescIdTypeName.setValue("");
			this.lovDescIdIssueCountryName.setValue("");
		}else{
			this.lovDescIdTypeName.setValue(aCustomerIdentity.getIdType()+"-"+aCustomerIdentity.getLovDescIdTypeName());
			this.lovDescIdIssueCountryName.setValue(aCustomerIdentity.getIdIssueCountry()+"-"+aCustomerIdentity.getLovDescIdIssueCountryName());
		}
		this.recordStatus.setValue(aCustomerIdentity.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerIdentity
	 */
	public void doWriteComponentsToBean(CustomerIdentity aCustomerIdentity) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerIdentity.setIdCustID(this.idCustID.longValue());
			aCustomerIdentity.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerIdentity.setLovDescIdTypeName(this.lovDescIdTypeName.getValue());
			aCustomerIdentity.setIdType(this.idType.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerIdentity.setIdIssuedBy(this.idIssuedBy.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerIdentity.setIdRef(this.idRef.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerIdentity.setLovDescIdIssueCountryName(this.lovDescIdIssueCountryName.getValue());
			aCustomerIdentity.setIdIssueCountry(this.idIssueCountry.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
				aCustomerIdentity.setIdIssuedOn(this.idIssuedOn.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
				aCustomerIdentity.setIdExpiresOn(this.idExpiresOn.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerIdentity.setIdLocation(this.idLocation.getValue());
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

		aCustomerIdentity.setRecordStatus(this.recordStatus.getValue());
		setCustomerIdentity(aCustomerIdentity);
		logger.debug("Leaving");		
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerIdentity
	 * @throws Exception
	 */
	public void doShowDialog(CustomerIdentity aCustomerIdentity) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCustomerIdentity.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.idIssuedBy.focus();
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
			doWriteBeanToComponents(aCustomerIdentity);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerIdentityDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.idCustID.isReadonly()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerIdentityDialog_IdCustID.value"),null,true));
		}
		if (!this.idIssuedBy.isReadonly()){
			this.idIssuedBy.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerIdentityDialog_IdIssuedBy.value"),null,true));
		}	
		if (!this.idRef.isReadonly()){
			this.idRef.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerIdentityDialog_IdRef.value"),null,true));
		}	
		if (!this.idIssuedOn.isDisabled()){
			this.idIssuedOn.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerIdentityDialog_IdIssuedOn.value"),true,startDate,appStartDate,true));
		}
		if (!this.idExpiresOn.isDisabled()){
			this.idExpiresOn.setConstraint(new PTDateValidator(Labels.getLabel("label_CustomerIdentityDialog_IdExpiresOn.value"),true,appStartDate,endDate,true));
		}
		if (!this.idLocation.isReadonly()){
			this.idLocation.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerIdentityDialog_IdLocation.value"),null,true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.idIssuedBy.setConstraint("");
		this.idRef.setConstraint("");
		this.idIssuedOn.setConstraint("");
		this.idExpiresOn.setConstraint("");
		this.idLocation.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescIdTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerIdentityDialog_IdType.value"),null,true));
		this.lovDescIdIssueCountryName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerIdentityDialog_IdIssueCountry.value"),null,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescIdTypeName.setConstraint("");
		this.lovDescIdIssueCountryName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.idIssuedBy.setErrorMessage("");
		this.idRef.setErrorMessage("");
		this.idIssuedOn.setErrorMessage("");
		this.idExpiresOn.setErrorMessage("");
		this.idLocation.setErrorMessage("");
		this.lovDescIdTypeName.setErrorMessage("");
		this.lovDescIdIssueCountryName.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerIdentityListCtrl().search();
		
	}
	
	// CRUD operations

	/**
	 * Deletes a CustomerIdentity object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CustomerIdentity aCustomerIdentity = new CustomerIdentity();
		BeanUtils.copyProperties(getCustomerIdentity(), aCustomerIdentity);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCustomerIdentity.getIdCustID();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerIdentity.getRecordType())){
				aCustomerIdentity.setVersion(aCustomerIdentity.getVersion()+1);
				aCustomerIdentity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCustomerIdentity.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCustomerIdentity,tranType)){
					refreshList();
					closeDialog(); 
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getCustomerIdentity().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.btnSearchPRCustid.setVisible(true);
		}else{
			this.idCustID.setReadonly(true);
			this.btnSearchIdType.setVisible(false);
			this.btnSearchPRCustid.setVisible(false);
			this.btnCancel.setVisible(true);
		}

		this.custCIF.setReadonly(true);
		this.idCustID.setReadonly(isReadOnly("CustomerIdentityDialog_idCustID"));
		this.btnSearchIdType.setDisabled(isReadOnly("CustomerIdentityDialog_idType"));
		this.idIssuedBy.setReadonly(isReadOnly("CustomerIdentityDialog_idIssuedBy"));
		this.idRef.setReadonly(isReadOnly("CustomerIdentityDialog_idRef"));
		this.btnSearchIdIssueCountry.setDisabled(isReadOnly("CustomerIdentityDialog_idIssueCountry"));
		this.idIssuedOn.setDisabled(isReadOnly("CustomerIdentityDialog_idIssuedOn"));
		this.idExpiresOn.setDisabled(isReadOnly("CustomerIdentityDialog_idExpiresOn"));
		this.idLocation.setReadonly(isReadOnly("CustomerIdentityDialog_idLocation"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerIdentity.isNewRecord()){
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
		this.btnSearchIdType.setDisabled(true);
		this.idIssuedBy.setReadonly(true);
		this.idRef.setReadonly(true);
		this.btnSearchIdIssueCountry.setDisabled(true);
		this.idIssuedOn.setDisabled(true);
		this.idExpiresOn.setDisabled(true);
		this.idLocation.setReadonly(true);

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
		this.custCIF.setText("");
		this.idType.setValue("");
		this.lovDescIdTypeName.setValue("");
		this.idIssuedBy.setValue("");
		this.idRef.setValue("");
		this.idIssueCountry.setValue("");
		this.lovDescIdIssueCountryName.setValue("");
		this.idIssuedOn.setText("");
		this.idExpiresOn.setText("");
		this.idLocation.setValue("");
		logger.debug("Leaving");		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {

		logger.debug("Entering");
		final CustomerIdentity aCustomerIdentity = new CustomerIdentity();
		BeanUtils.copyProperties(getCustomerIdentity(), aCustomerIdentity);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerIdentity object with the components data
		doWriteComponentsToBean(aCustomerIdentity);

		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aCustomerIdentity.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerIdentity.getRecordType())){
				aCustomerIdentity.setVersion(aCustomerIdentity.getVersion()+1);
				if(isNew){
					aCustomerIdentity.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerIdentity.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerIdentity.setNewRecord(true);
				}
			}
		}else{
			aCustomerIdentity.setVersion(aCustomerIdentity.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCustomerIdentity,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerIdentity
	 *            (CustomerIdentity)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerIdentity aCustomerIdentity,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCustomerIdentity.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aCustomerIdentity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerIdentity.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerIdentity.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerIdentity.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerIdentity);
				}

				if (isNotesMandatory(taskId, aCustomerIdentity)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode= getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCustomerIdentity.setTaskId(taskId);
			aCustomerIdentity.setNextTaskId(nextTaskId);
			aCustomerIdentity.setRoleCode(getRole());
			aCustomerIdentity.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerIdentity, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerIdentity);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerIdentity, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerIdentity, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
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
		CustomerIdentity aCustomerIdentity = (CustomerIdentity) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerIdentityService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerIdentityService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCustomerIdentityService().doApprove(auditHeader);
						if (aCustomerIdentity.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCustomerIdentityService().doReject(auditHeader);
						if (aCustomerIdentity.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerIdentityDialog, auditHeader);
						logger.debug("Leaving");
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_CustomerIdentityDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.customerIdentity), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Component Events

	public void onClick$btnSearchIdType(Event event){
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerIdentityDialog,"IdentityDetails");
		if (dataObject instanceof String){
			this.idType.setValue(dataObject.toString());
			this.lovDescIdTypeName.setValue("");
		}else{
			IdentityDetails details= (IdentityDetails) dataObject;
			if (details != null) {
				this.idType.setValue(details.getLovValue());
				this.lovDescIdTypeName.setValue(details.getLovValue()+"-"+details.getIdentityDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchIdIssueCountry(Event event){
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerIdentityDialog,"Country");
		if (dataObject instanceof String){
			this.idIssueCountry.setValue(dataObject.toString());
			this.lovDescIdIssueCountryName.setValue("");
		}else{
			Country details= (Country) dataObject;
			if (details != null) {
				this.idIssueCountry.setValue(details.getLovValue());
				this.lovDescIdIssueCountryName.setValue(details.getLovValue()+"-"+details.getCountryDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	// To load the customerSelect filter dialog
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("searchObject",this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",	null, map);
		logger.debug("Leaving");
	}

	//To set the customer id from search//////
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException{
		logger.debug("Entering");
		final Customer aCustomer = (Customer)nCustomer; 
		this.idCustID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerIdentity
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerIdentity aCustomerIdentity, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerIdentity.getBefImage(), aCustomerIdentity);

		return new AuditHeader(getReference(),String.valueOf(aCustomerIdentity.getIdCustID()), null,
				null, auditDetail, aCustomerIdentity.getUserDetails(), getOverideMap());
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
		doShowNotes(this.customerIdentity);
	}

	
	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return String.valueOf(getCustomerIdentity().getIdCustID())
		+PennantConstants.KEY_SEPERATOR+getCustomerIdentity().getIdType();
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

	public CustomerIdentity getCustomerIdentity() {
		return this.customerIdentity;
	}
	public void setCustomerIdentity(CustomerIdentity customerIdentity) {
		this.customerIdentity = customerIdentity;
	}

	public void setCustomerIdentityService(CustomerIdentityService customerIdentityService) {
		this.customerIdentityService = customerIdentityService;
	}
	public CustomerIdentityService getCustomerIdentityService() {
		return this.customerIdentityService;
	}

	public void setCustomerIdentityListCtrl(CustomerIdentityListCtrl customerIdentityListCtrl) {
		this.customerIdentityListCtrl = customerIdentityListCtrl;
	}
	public CustomerIdentityListCtrl getCustomerIdentityListCtrl() {
		return this.customerIdentityListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setCustomerSelectCtrl(CustomerSelectCtrl customerSelectctrl) {
		this.customerSelectCtrl = customerSelectctrl;
	}

	public CustomerSelectCtrl getCustomerSelectCtrl() {
		return customerSelectCtrl;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}

}
