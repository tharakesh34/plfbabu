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

import java.io.Serializable;
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
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
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
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerIdentity/customerIdentityDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CustomerIdentityDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -2646507082404896955L;
	private final static Logger logger = Logger.getLogger(CustomerIdentityDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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

	protected Label 		recordStatus; 					// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	
	// not auto wired vars
	private CustomerIdentity customerIdentity; // overhanded per param
	private transient CustomerIdentityListCtrl customerIdentityListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient long  	oldVar_idCustID;
	private transient String  	oldVar_idType;
	private transient String  	oldVar_idIssuedBy;
	private transient String  	oldVar_idRef;
	private transient String  	oldVar_idIssueCountry;
	private transient Date  	oldVar_idIssuedOn;
	private transient Date  	oldVar_idExpiresOn;
	private transient String  	oldVar_idLocation;
	private transient String 	oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerIdentityDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 	// autowire
	protected Button btnEdit; 	// autowire
	protected Button btnDelete; // autowire
	protected Button btnSave; 	// autowire
	protected Button btnCancel; // autowire
	protected Button btnClose; 	// autowire
	protected Button btnHelp; 	// autowire
	protected Button btnNotes; 	// autowire

	protected Button btnSearchPRCustid; // autowire

	protected Button btnSearchIdType; // autowire
	protected Textbox lovDescIdTypeName;
	private transient String 		oldVar_lovDescIdTypeName;
	protected Button btnSearchIdIssueCountry; // autowire
	protected Textbox lovDescIdIssueCountryName;
	private transient String 		oldVar_lovDescIdIssueCountryName;

	// ServiceDAOs / Domain Classes
	private transient CustomerIdentityService customerIdentityService;
	private transient PagedListService pagedListService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;


	private boolean newRecord=false;
	private boolean newCustomer=false;
	protected JdbcSearchObject<Customer> newSearchObject ;
	Date appStartDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
	Date endDate=(Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_END_DATE");
	Date startDate = (Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");
	/**
	 * default constructor.<br>
	 */
	public CustomerIdentityDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerIdentity object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerIdentityDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);


		// READ OVERHANDED params !
		if (args.containsKey("customerIdentity")) {
			this.customerIdentity = (CustomerIdentity) args.get("customerIdentity");
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
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerIdentityDialog");
		}

		// READ OVERHANDED params !
		// we get the customerIdentityListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerIdentity here.
		if (args.containsKey("customerIdentityListCtrl")) {
			setCustomerIdentityListCtrl((CustomerIdentityListCtrl) args.get("customerIdentityListCtrl"));
		} else {
			setCustomerIdentityListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerIdentity());
		if (getCustomerIdentity().isNewRecord()) {
			onload();
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
		this.idIssuedOn.setFormat(PennantConstants.dateFormat);
		this.idExpiresOn.setFormat(PennantConstants.dateFormat);
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
		getUserWorkspace().alocateAuthorities("CustomerIdentityDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerIdentityDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug("Leaving");
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
	public void onClose$window_CustomerIdentityDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerIdentityDialog);
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
		} catch (final WrongValueException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


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
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("Data Changed(): false");
		}
		if (close) {
			closeDialog(this.window_CustomerIdentityDialog, "CustomerIdentity");
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerIdentity aCustomerIdentity) throws InterruptedException {
		logger.debug("Entering");
		// if aCustomerIdentity == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCustomerIdentity == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCustomerIdentity = getCustomerIdentityService().getNewCustomerIdentity();

			setCustomerIdentity(aCustomerIdentity);
		} else {
			setCustomerIdentity(aCustomerIdentity);
		}

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

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerIdentityDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_idCustID = this.idCustID.longValue();
		this.oldVar_idType = this.idType.getValue();
		this.oldVar_lovDescIdTypeName = this.lovDescIdTypeName.getValue();
		this.oldVar_idIssuedBy = this.idIssuedBy.getValue();
		this.oldVar_idRef = this.idRef.getValue();
		this.oldVar_idIssueCountry = this.idIssueCountry.getValue();
		this.oldVar_lovDescIdIssueCountryName = this.lovDescIdIssueCountryName.getValue();
		this.oldVar_idIssuedOn = this.idIssuedOn.getValue();
		this.oldVar_idExpiresOn = this.idExpiresOn.getValue();
		this.oldVar_idLocation = this.idLocation.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.idCustID.setValue(this.oldVar_idCustID);
		this.idType.setValue(this.oldVar_idType);
		this.lovDescIdTypeName.setValue(this.oldVar_lovDescIdTypeName);
		this.idIssuedBy.setValue(this.oldVar_idIssuedBy);
		this.idRef.setValue(this.oldVar_idRef);
		this.idIssueCountry.setValue(this.oldVar_idIssueCountry);
		this.lovDescIdIssueCountryName.setValue(this.oldVar_lovDescIdIssueCountryName);
		this.idIssuedOn.setValue(this.oldVar_idIssuedOn);
		this.idExpiresOn.setValue(this.oldVar_idExpiresOn);
		this.idLocation.setValue(this.oldVar_idLocation);
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
		// To clear the Error Messages
		doClearMessage();

		if (this.oldVar_idCustID != this.idCustID.longValue()) {
			return true;
		}
		if (this.oldVar_idType != this.idType.getValue()) {
			return true;
		}
		if (this.oldVar_idIssuedBy != this.idIssuedBy.getValue()) {
			return true;
		}
		if (this.oldVar_idRef != this.idRef.getValue()) {
			return true;
		}
		if (this.oldVar_idIssueCountry != this.idIssueCountry.getValue()) {
			return true;
		}
		String oldIdIssuedOn = "";
		String newIdIssuedOn ="";
		if (this.oldVar_idIssuedOn!=null){
			oldIdIssuedOn=DateUtility.formatDate(this.oldVar_idIssuedOn,PennantConstants.dateFormat);
		}
		if (this.idIssuedOn.getValue()!=null){
			newIdIssuedOn=DateUtility.formatDate(this.idIssuedOn.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldIdIssuedOn).equals(StringUtils.trimToEmpty(newIdIssuedOn))) {
			return true;
		}
		String oldIdExpiresOn = "";
		String newIdExpiresOn ="";
		if (this.oldVar_idExpiresOn!=null){
			oldIdExpiresOn=DateUtility.formatDate(this.oldVar_idExpiresOn,PennantConstants.dateFormat);
		}
		if (this.idExpiresOn.getValue()!=null){
			newIdExpiresOn=DateUtility.formatDate(this.idExpiresOn.getValue(),PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(oldIdExpiresOn).equals(StringUtils.trimToEmpty(newIdExpiresOn))) {
			return true;
		}
		if (this.oldVar_idLocation != this.idLocation.getValue()) {
			return true;
		}

		return false;
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
	private void doClearMessage() {
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

	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		final JdbcSearchObject<CustomerIdentity> soCustomerIdentity = getCustomerIdentityListCtrl().getSearchObj();
		getCustomerIdentityListCtrl().pagingCustomerIdentityList.setActivePage(0);
		getCustomerIdentityListCtrl().getPagedListWrapper().setSearchObject(soCustomerIdentity);
		if (getCustomerIdentityListCtrl().listBoxCustomerIdentity != null) {
			getCustomerIdentityListCtrl().listBoxCustomerIdentity.getListModel();
		}
		logger.debug("Leaving");
		
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerIdentity.getRecordType()).equals("")){
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
					closeDialog(this.window_CustomerIdentityDialog, "CustomerIdentity"); 
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerIdentity object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerIdentity() in the frontend.
		// we get it from the backend.
		final CustomerIdentity aCustomerIdentity = getCustomerIdentityService().getNewCustomerIdentity();
		aCustomerIdentity.setNewRecord(true);
		setCustomerIdentity(aCustomerIdentity);
		doClear(); // clear all commponents
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.custCIF.focus();
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aCustomerIdentity.getRecordType()).equals("")){
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
				closeDialog(this.window_CustomerIdentityDialog, "CustomerIdentity");
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

		aCustomerIdentity.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerIdentity.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerIdentity.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerIdentity.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerIdentity.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerIdentity);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCustomerIdentity))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							logger.debug("Leaving");
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

			aCustomerIdentity.setTaskId(taskId);
			aCustomerIdentity.setNextTaskId(nextTaskId);
			aCustomerIdentity.setRoleCode(getRole());
			aCustomerIdentity.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerIdentity, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerIdentity);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						deleteNotes(getNotes(), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++ Search Button Component Events+++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerIdentityDialog,
					auditHeader);
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
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
		logger.debug("Leaving");
	}	

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		Notes notes = new Notes();
		notes.setModuleName("CustomerIdentity");
		notes.setReference(getReference());
		notes.setVersion(getCustomerIdentity().getVersion());
		return notes;
	}
	/**
	 * Get the Reference value
	 */
	private String getReference(){
		return String.valueOf(getCustomerIdentity().getIdCustID())
		+PennantConstants.KEY_SEPERATOR+getCustomerIdentity().getIdType();
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

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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
