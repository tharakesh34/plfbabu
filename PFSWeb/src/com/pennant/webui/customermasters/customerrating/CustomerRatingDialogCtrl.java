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
 * FileName    		:  CustomerRatingDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerrating;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.service.customermasters.CustomerRatingService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerRating/customerRatingDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerRatingDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = -6959194080451993569L;
	private final static Logger logger = Logger.getLogger(CustomerRatingDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	 */
	protected Window 		window_CustomerRatingDialog;// autowired

	protected Longbox 		custID; 					// autowired
	protected ExtendedCombobox 		custRatingType; 			// autowired
	protected ExtendedCombobox 		custRatingCode; 			// autowired
	protected ExtendedCombobox 		custRating; 				// autowired
	protected Textbox 		custCIF;					// autowired
	protected Label 		custShrtName;				// autowired

	protected Label 		recordStatus; 				// autowired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;


	// not auto wired vars
	private CustomerRating customerRating; 								// overhanded per param
	private transient CustomerRatingListCtrl customerRatingListCtrl; 	// overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient long   oldVar_custID;
	private transient String oldVar_custRatingType;
	private transient String oldVar_custRatingCode;
	private transient String oldVar_custRating;
	private transient String oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerRatingDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 			// autowire
	protected Button btnEdit; 			// autowire
	protected Button btnDelete; 		// autowire
	protected Button btnSave; 			// autowire
	protected Button btnCancel; 		// autowire
	protected Button btnClose; 			// autowire
	protected Button btnHelp; 			// autowire
	protected Button btnNotes; 			// autowire
	protected Button btnSearchPRCustid; // autowire

	private transient String 		oldVar_lovDescCustRatingTypeName;


	// ServiceDAOs / Domain Classes
	private transient CustomerRatingService customerRatingService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord=false;
	private boolean newCustomer=false;
	private List<CustomerRating> customerRatings;
	private CustomerDialogCtrl customerDialogCtrl;
	private transient boolean isRatingTypeNumeric;
	protected JdbcSearchObject<Customer> newSearchObject ;
	private String moduleType="";
	private String sCustRatingType;
	private String role="";

	/**
	 * default constructor.<br>
	 */
	public CustomerRatingDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerRating object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */	
	public void onCreate$window_CustomerRatingDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());


		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix,
				true, this.btnNew,this.btnEdit, this.btnDelete, this.btnSave,
				this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		if(args.containsKey("roleCode")){
			role=(String) args.get("roleCode");
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED params !
		if (args.containsKey("customerRating")) {
			this.customerRating = (CustomerRating) args.get("customerRating");
			CustomerRating befImage =new CustomerRating();
			BeanUtils.copyProperties(this.customerRating, befImage);
			this.customerRating.setBefImage(befImage);
			setCustomerRating(this.customerRating);
		} else {
			setCustomerRating(null);
		}
		
		if (args.containsKey("moduleType")) {
			this.moduleType = (String) args.get("moduleType");
		}

		if(getCustomerRating().isNewRecord()){
			setNewRecord(true);
		}

		if(args.containsKey("customerDialogCtrl")){

			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerDialogCtrl"));
			setNewCustomer(true);

			if(args.containsKey("newRecord")){
				setNewRecord(true);
			}else{
				setNewRecord(false);
			}
			this.customerRating.setWorkflowId(0);
			getUserWorkspace().alocateRoleAuthorities(role, "CustomerRatingDialog");
	
		}

		doLoadWorkFlow(this.customerRating.isWorkflow(),this.customerRating.getWorkflowId(),
				this.customerRating.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerRatingDialog");
		}

		// READ OVERHANDED params !
		// we get the customerRatingListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerRating here.
		if (args.containsKey("customerRatingListCtrl")) {
			setCustomerRatingListCtrl((CustomerRatingListCtrl) args.get("customerRatingListCtrl"));
		} else {
			setCustomerRatingListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerRating());

		//Calling SelectCtrl For proper selection of Customer
		if(isNewRecord() & !isNewCustomer()){
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
		this.custRatingType.setMaxlength(8);
		this.custRatingType.setMandatoryStyle(true);
		this.custRatingType.getTextbox().setWidth("110px");
		this.custRatingType.setModuleName("RatingType");
		this.custRatingType.setValueColumn("RatingType");
		this.custRatingType.setDescColumn("RatingTypeDesc");
		this.custRatingType.setValidateColumns(new String[] { "RatingType" });
		
		this.custRatingCode.setTextBoxWidth(110);
		this.custRatingCode.setModuleName("RatingCode");
		this.custRatingCode.setValueColumn("RatingCode");
		this.custRatingCode.setDescColumn("RatingCodeDesc");
		this.custRatingCode.setValidateColumns(new String[] { "RatingCode" });
		
		this.custRating.setTextBoxWidth(110);
		this.custRating.setModuleName("RatingCode");
		this.custRating.setValueColumn("RatingCode");
		this.custRating.setDescColumn("RatingCodeDesc");
		this.custRating.setValidateColumns(new String[] { "RatingCode" });
	
		this.custRatingCode.setMaxlength(8);
		this.custRating.setMaxlength(8);

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
		getUserWorkspace().alocateAuthorities("CustomerRatingDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnSave"));
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
	public void onClose$window_CustomerRatingDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
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
		PTMessageUtils.showHelpWindow(event, window_CustomerRatingDialog);
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
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeWindow();
		}
		logger.debug("Leaving");		
	}

	/**
	 * Method for closing Customer Selection Window 
	 * @throws InterruptedException
	 */
	public void closeWindow() throws InterruptedException{
		logger.debug("Entering");

		if(isNewCustomer()){
			window_CustomerRatingDialog.onClose();	
		}else{
			closeDialog(this.window_CustomerRatingDialog, "CustomerRating");
		}


		logger.debug("Leaving");
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
	 * @param aCustomerRating
	 *            CustomerRating
	 */
	public void doWriteBeanToComponents(CustomerRating aCustomerRating) {
		logger.debug("Entering");

		if(aCustomerRating.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerRating.getCustID());	
		}

		this.custRatingType.setValue(aCustomerRating.getCustRatingType());
		doSetRatingCodeFilters(aCustomerRating.getCustRatingType());
		
		this.custRatingCode.setValue(aCustomerRating.getCustRatingCode(),StringUtils.trimToEmpty(aCustomerRating.getLovDesccustRatingCodeDesc()));
		this.custRating.setValue(aCustomerRating.getCustRating(),StringUtils.trimToEmpty(aCustomerRating.getLovDescCustRatingName()));
		
		this.isRatingTypeNumeric = aCustomerRating.isValueType();
		
		this.custCIF.setValue(aCustomerRating.getLovDescCustCIF()==null?"":aCustomerRating.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerRating.getLovDescCustShrtName()==null?"":aCustomerRating.getLovDescCustShrtName().trim());

		if (isNewRecord()){
			this.custRatingType.setDescription("");
			this.custRatingType.setReadonly(false);

		}else{
			this.custRatingType.setDescription(aCustomerRating.getLovDescCustRatingTypeName());
			this.custRatingType.setReadonly(true);
		}
		this.recordStatus.setValue(aCustomerRating.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerRating
	 */
	public void doWriteComponentsToBean(CustomerRating aCustomerRating) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerRating.setCustID(this.custID.getValue());
			aCustomerRating.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerRating.setLovDescCustRatingTypeName(this.custRatingType.getDescription());
			aCustomerRating.setCustRatingType(this.custRatingType.getValue());
			aCustomerRating.setValueType(this.isRatingTypeNumeric);
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerRating.setCustRatingCode(this.custRatingCode.getValidatedValue());
			aCustomerRating.setLovDesccustRatingCodeDesc(this.custRatingCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			
			aCustomerRating.setCustRating(this.custRating.getValidatedValue());
			aCustomerRating.setLovDescCustRatingName(this.custRating.getDescription());
		} catch (WrongValueException we) {
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

		aCustomerRating.setRecordStatus(this.recordStatus.getValue());
		setCustomerRating(aCustomerRating);
		logger.debug("Leaving");

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerRating
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerRating aCustomerRating) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custRatingCode.focus();
			if (isNewCustomer()){
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
			doWriteBeanToComponents(aCustomerRating);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			this.btnCancel.setVisible(false);
			this.btnDelete.setVisible(false);
			if(isNewCustomer()){
				this.window_CustomerRatingDialog.setHeight("228px");
				this.window_CustomerRatingDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerRatingDialog.doModal() ;
			}else{
				this.window_CustomerRatingDialog.setWidth("100%");
				this.window_CustomerRatingDialog.setHeight("100%");
				setDialog(this.window_CustomerRatingDialog);
			}

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
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_custRatingType = this.custRatingType.getValue();
		this.oldVar_lovDescCustRatingTypeName = this.custRatingType.getDescription();
		this.oldVar_custRatingCode = this.custRatingCode.getValue();
		this.oldVar_custRating = this.custRating.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.custID.setValue(this.oldVar_custID);
		this.custRatingType.setValue(this.oldVar_custRatingType);
		this.custRatingType.setDescription(this.oldVar_lovDescCustRatingTypeName);
		this.custRatingCode.setValue(this.oldVar_custRatingCode);
		this.custRating.setValue(this.oldVar_custRating);
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

		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (this.oldVar_custRatingType != this.custRatingType.getValue()) {
			return true;
		}
		if (this.oldVar_custRatingCode != this.custRatingCode.getValue()) {
			return true;
		}
		if (this.oldVar_custRating != this.custRating.getValue()) {
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

		if (!this.custID.isReadonly()){
			this.custCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_CustomerRatingDialog_CustID.value")}));
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
		this.custRating.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		this.custRatingType.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{ Labels.getLabel("label_CustomerRatingDialog_CustRatingType.value")}));

//		this.custRatingCode.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
//				new String[]{ Labels.getLabel("label_CustomerRatingDialog_CustRatingCode.value")}));

		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custRatingType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custRating.setErrorMessage("");
		this.custRatingCode.setErrorMessage("");
		this.custRating.setErrorMessage("");
		this.custRatingType.setErrorMessage("");
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		/*getCustomerRatingListCtrl().findSearchObject();
		if (getCustomerRatingListCtrl().listBoxCustomerRating != null) {
			getCustomerRatingListCtrl().listBoxCustomerRating.getListModel();
		}*/
		
		final JdbcSearchObject<CustomerRating> soAcademic = getCustomerRatingListCtrl().getSearchObj();
		getCustomerRatingListCtrl().pagingCustomerRatingList.setActivePage(0);
		getCustomerRatingListCtrl().getPagedListWrapper().setSearchObject(soAcademic);
		if (getCustomerRatingListCtrl().listBoxCustomerRating != null) {
			getCustomerRatingListCtrl().listBoxCustomerRating.getListModel();
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerRating object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CustomerRating aCustomerRating = new CustomerRating();
		BeanUtils.copyProperties(getCustomerRating(), aCustomerRating);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") 
		+ "\n\n --> " + aCustomerRating.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, 
				MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerRating.getRecordType()).equals("")){
				aCustomerRating.setVersion(aCustomerRating.getVersion()+1);
				aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if(getCustomerDialogCtrl() != null &&  getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()){
					aCustomerRating.setNewRecord(true);	
				}
				if (isWorkFlowEnabled()){
					aCustomerRating.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(isNewCustomer()){
					tranType=PennantConstants.TRAN_DEL;
					AuditHeader auditHeader =  newCusomerProcess(aCustomerRating,tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerRatingDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
						getCustomerDialogCtrl().doFillCustomerRatings(this.customerRatings);
						// send the data back to customer
						closeWindow();
					}	

				}else if(doProcess(aCustomerRating,tranType)){
					refreshList();
					closeWindow();
				}
			}catch (DataAccessException e){
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CustomerRating object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		// remember the old vars
		doStoreInitValues();

		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerRating() in the frontEnd.
		// we get it from the backEnd.
		final CustomerRating aCustomerRating = getCustomerRatingService().getNewCustomerRating();
		aCustomerRating.setNewRecord(true);
		setCustomerRating(aCustomerRating);
		doClear(); // clear all components
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
		if (isNewRecord()){

			if(isNewCustomer()){
				this.btnCancel.setVisible(false);	
				this.btnSearchPRCustid.setVisible(false);
			}else{
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custRatingType.setReadonly(isReadOnly("CustomerRatingDialog_custRatingType"));
		}else{
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custRatingType.setReadonly(true);
		}
		this.custCIF.setReadonly(true);
		this.custID.setReadonly(isReadOnly("CustomerRatingDialog_custID"));
		this.custRatingCode.setReadonly(isReadOnly("CustomerRatingDialog_custRatingCode"));
		this.custRating.setReadonly(isReadOnly("CustomerRatingDialog_custRating"));

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerRating.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{

			if(newCustomer){
				if("ENQ".equals(this.moduleType)){
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				}else if (isNewRecord()){
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				}else{
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			}else{
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName){
		boolean isCustomerWorkflow = false;
		if(getCustomerDialogCtrl() != null){
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.custCIF.setReadonly(true);
		this.custRatingType.setReadonly(true);
		this.custRatingCode.setReadonly(true);
		this.custRating.setReadonly(true);

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
		this.custShrtName.setValue("");
		this.custRatingType.setValue("");
		this.custRatingType.setDescription("");
		this.custRatingCode.setValue("");
		this.custRating.setValue("");
		logger.debug("Leaving");		
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerRating aCustomerRating = new CustomerRating();
		BeanUtils.copyProperties(getCustomerRating(), aCustomerRating);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerRating object with the components data
		doWriteComponentsToBean(aCustomerRating);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerRating.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerRating.getRecordType()).equals("")){
				aCustomerRating.setVersion(aCustomerRating.getVersion()+1);
				if(isNew){
					aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerRating.setNewRecord(true);
				}
			}
		}else{

			if(isNewCustomer()){
				if(isNewRecord()){
					aCustomerRating.setVersion(1);
					aCustomerRating.setRecordType(PennantConstants.RCD_ADD);
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}

				if(StringUtils.trimToEmpty(aCustomerRating.getRecordType()).equals("")){
					aCustomerRating.setVersion(aCustomerRating.getVersion()+1);
					aCustomerRating.setRecordType(PennantConstants.RCD_UPD);
				}

				if(aCustomerRating.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()){
					tranType =PennantConstants.TRAN_ADD;
				} else if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
					tranType =PennantConstants.TRAN_UPD;
				}

			}else{
				aCustomerRating.setVersion(aCustomerRating.getVersion()+1);
				if(isNew){
					tranType =PennantConstants.TRAN_ADD;
				}else{
					tranType =PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if(isNewCustomer()){
				AuditHeader auditHeader =  newCusomerProcess(aCustomerRating,tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerRatingDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue==PennantConstants.porcessCONTINUE || retValue==PennantConstants.porcessOVERIDE){
					getCustomerDialogCtrl().doFillCustomerRatings(this.customerRatings);
					//true;
					// send the data back to customer
					closeWindow();

				}

			}else if(doProcess(aCustomerRating,tranType)){
				refreshList();
				// Close the Existing Dialog
				closeWindow();
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}


	private AuditHeader newCusomerProcess(CustomerRating aCustomerRating,String tranType){
		boolean recordAdded=false;

		AuditHeader auditHeader= getAuditHeader(aCustomerRating, tranType);
		customerRatings = new ArrayList<CustomerRating>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerRating.getId());
		valueParm[1] = aCustomerRating.getCustRatingType();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustRatingType") + ":"+valueParm[1];

		if(getCustomerDialogCtrl().getRatingsList()!=null && getCustomerDialogCtrl().getRatingsList().size()>0){
			for (int i = 0; i < getCustomerDialogCtrl().getRatingsList().size(); i++) {
				CustomerRating customerRating = getCustomerDialogCtrl().getRatingsList().get(i);


				if(customerRating.getCustRatingType().equals(aCustomerRating.getCustRatingType())){ // Both Current and Existing list rating same

					if(isNewRecord()){
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetails(PennantConstants.KEY_FIELD,"41001",errParm,valueParm), 
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}


					if(tranType==PennantConstants.TRAN_DEL){
						if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)){
							aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded=true;
							customerRatings.add(aCustomerRating);
						}else if(aCustomerRating.getRecordType().equals(PennantConstants.RCD_ADD)){
							recordAdded=true;
						}else if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded=true;
							customerRatings.add(aCustomerRating);
						}else if(aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)){
							recordAdded=true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getRatingsList().size(); j++) {
								CustomerRating rating =  getCustomerDialogCtrl().getCustomerDetails().getRatingsList().get(j);
								if(rating.getCustID() == aCustomerRating.getCustID() && 
										rating.getCustRatingType().equals(aCustomerRating.getCustRatingType())){
									customerRatings.add(rating);
								}
							}
						}
					}else{
						if(tranType!=PennantConstants.TRAN_UPD){
							customerRatings.add(customerRating);
						}
					}
				}else{
					customerRatings.add(customerRating);
				}
			}
		}
		if(!recordAdded){
			customerRatings.add(aCustomerRating);
		}
		return auditHeader;
	} 

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerRating (CustomerRating)
	 * 
	 * @param tranType (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerRating aCustomerRating,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCustomerRating.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerRating.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerRating.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerRating.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerRating.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerRating);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCustomerRating))) {
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

			aCustomerRating.setTaskId(taskId);
			aCustomerRating.setNextTaskId(nextTaskId);
			aCustomerRating.setRoleCode(getRole());
			aCustomerRating.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerRating, tranType);

			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerRating);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerRating, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerRating, tranType);
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
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerRating  aCustomerRating= (CustomerRating) auditHeader.getAuditDetail()
		.getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerRatingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerRatingService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerRatingService().doApprove(auditHeader);

						if (aCustomerRating.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerRatingService().doReject(auditHeader);

						if (aCustomerRating.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerRatingDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(
						this.window_CustomerRatingDialog, auditHeader);
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

	public void onFulfill$custRatingType(Event event){
		logger.debug("Entering" + event.toString());

		Object dataObject = custRatingType.getObject();
		if (dataObject instanceof String){
			this.custRatingType.setValue(dataObject.toString());
			this.custRatingType.setDescription("");

		}else{
			RatingType details= (RatingType) dataObject;
			if (details != null) {
				this.custRatingType.setValue(details.getRatingType());
				this.custRatingType.setDescription(details.getRatingTypeDesc());
				this.isRatingTypeNumeric = details.isValueType();
				doSetRatingCodeFilters(details.getRatingType());
			}
		}

		if (!StringUtils.trimToEmpty(sCustRatingType).equals(this.custRatingType.getValue())){
			this.custRatingCode.setValue("");
		} 
		sCustRatingType= this.custRatingType.getValue();
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

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException, InterruptedException{
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();	
		map.put("DialogCtrl", this);
		map.put("filtertype","Extended");
		map.put("searchObject",this.newSearchObject);
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
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerRating aCustomerRating, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerRating.getBefImage(), aCustomerRating);

		return new AuditHeader(getReference(),String.valueOf(aCustomerRating.getCustID()), null,
				null, auditDetail, aCustomerRating.getUserDetails(), getOverideMap());
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
			ErrorControl.showErrorControl(this.window_CustomerRatingDialog, auditHeader);
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

		final HashMap<String, Serializable> map = new HashMap<String, Serializable>();
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
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerRating");
		notes.setReference(getReference());
		notes.setVersion(getCustomerRating().getVersion());
		logger.debug("Leaving");
		return notes;
	}
	/** 
	 * Get the Reference value
	 */
	private String getReference(){
		return getCustomerRating().getCustID()+PennantConstants.KEY_SEPERATOR
		+getCustomerRating().getCustRatingType();
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

	public CustomerRating getCustomerRating() {
		return this.customerRating;
	}
	public void setCustomerRating(CustomerRating customerRating) {
		this.customerRating = customerRating;
	}

	public void setCustomerRatingService(CustomerRatingService customerRatingService) {
		this.customerRatingService = customerRatingService;
	}
	public CustomerRatingService getCustomerRatingService() {
		return this.customerRatingService;
	}

	public void setCustomerRatingListCtrl(CustomerRatingListCtrl customerRatingListCtrl) {
		this.customerRatingListCtrl = customerRatingListCtrl;
	}
	public CustomerRatingListCtrl getCustomerRatingListCtrl() {
		return this.customerRatingListCtrl;
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

	public boolean isNewRecord() {
		return newRecord;
	}
	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public boolean isNewCustomer() {
		return newCustomer;
	}
	public void setNewCustomer(boolean newCustomer) {
		this.newCustomer = newCustomer;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}
	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	
	public void doSetRatingCodeFilters(String sectorcode) {
		if (!StringUtils.trimToEmpty(sectorcode).equals("")) {
			Filter filters[] = new Filter[1];
			filters[0] = new Filter("RatingType", sectorcode, Filter.OP_EQUAL);
			this.custRatingCode.setFilters(filters);
			this.custRating.setFilters(filters);
		} 
		this.custRatingCode.setValue("", "");
		this.custRating.setValue("", "");
	}
}
