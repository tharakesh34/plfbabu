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
 * FileName    		:  CustomerAdditionalDetailDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.customermasters.customeradditionaldetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerAdditionalDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.LongValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/CustomerMasters/CustomerAdditionalDetail/customerAdditionalDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */

public class CustomerAdditionalDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 9149147830087927324L;
	private final static Logger logger = Logger.getLogger(CustomerAdditionalDetailDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_CustomerAdditionalDetailDialog; // autoWired

	protected Longbox custID; 								// autoWired
	protected Textbox custAcademicLevel; 					// autoWired
	protected Textbox academicDecipline; 					// autoWired
	protected Longbox custRefCustID; 						// autoWired
	protected Textbox custRefStaffID; 						// autoWired
	protected Textbox custCIF;								// autoWired
	protected Label   custShrtName;							// autoWired

	protected Label recordStatus; 							// autoWired
	protected Radiogroup userAction;
	protected Groupbox groupboxWf;


	// not auto wired variables
	private CustomerAdditionalDetail customerAdditionalDetail; // overHanded per parameter
	private transient CustomerAdditionalDetailListCtrl customerAdditionalDetailListCtrl; // overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient long    oldVar_custID;
	private transient String  oldVar_custAcademicLevel;
	private transient String  oldVar_academicDecipline;
	private transient long    oldVar_custRefCustID;
	private transient String  oldVar_custRefStaffID;
	private transient String  oldVar_recordStatus;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CustomerAdditionalDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	protected Button btnNew; 	// autoWire
	protected Button btnEdit; 	// autoWire
	protected Button btnDelete; // autoWire
	protected Button btnSave; 	// autoWire
	protected Button btnCancel; // autoWire
	protected Button btnClose; 	// autoWire
	protected Button btnHelp; 	// autoWire
	protected Button btnNotes; 	// autoWire
	protected Button btnSearchPRCustid; // autoWire

	protected Button  btnSearchCustAcademicLevel; 		// autoWired
	protected Textbox lovDescCustAcademicLevelName;
	private transient String 	oldVar_lovDescCustAcademicLevelName;
	protected Button  btnSearchAcademicDecipline; 		// autoWired
	protected Textbox lovDescAcademicDeciplineName;
	private transient String  oldVar_lovDescAcademicDeciplineName;

	// ServiceDAOs / Domain Classes
	private transient CustomerAdditionalDetailService customerAdditionalDetailService;
	private transient PagedListService pagedListService;
	private boolean newRecord=false;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject ;

	/**
	 * default constructor.<br>
	 */
	public CustomerAdditionalDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerAdditionalDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerAdditionalDetailDialog(Event event)	throws Exception {
		logger.debug("Entering" + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);


		// READ OVERHANDED parameters !
		if (args.containsKey("customerAdditionalDetail")) {
			this.customerAdditionalDetail = (CustomerAdditionalDetail) args.get("customerAdditionalDetail");
			CustomerAdditionalDetail befImage =new CustomerAdditionalDetail();
			BeanUtils.copyProperties(this.customerAdditionalDetail, befImage);
			this.customerAdditionalDetail.setBefImage(befImage);
			setCustomerAdditionalDetail(this.customerAdditionalDetail);
		} else {
			setCustomerAdditionalDetail(null);
		}

		if(args.containsKey("customerAdditionalDetailDialogCtrl")){

			setCustomerDialogCtrl((CustomerDialogCtrl) args.get("customerAdditionalDetailDialogCtrl"));

			this.customerAdditionalDetail.setWorkflowId(0);
			if(args.containsKey("roleCode")){
				getUserWorkspace().alocateRoleAuthorities((String) args.get("roleCode"), "CustomerAdditionalDetailDialog");
			}
		}
		doLoadWorkFlow(this.customerAdditionalDetail.isWorkflow(), this.customerAdditionalDetail.getWorkflowId(),
				this.customerAdditionalDetail.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CustomerAdditionalDetailDialog");
		}


		// READ OVERHANDED parameters !
		// we get the customerAdditionalDetailListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerAdditionalDetail here.
		if (args.containsKey("customerAdditionalDetailListCtrl")) {
			setCustomerAdditionalDetailListCtrl((CustomerAdditionalDetailListCtrl) args.get("customerAdditionalDetailListCtrl"));
		} else {
			setCustomerAdditionalDetailListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerAdditionalDetail());

		//Calling SelectCtrl For proper selection of Customer
		if(getCustomerAdditionalDetail().isNew()){
			onLoad();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering ");
		//Empty sent any required attributes
		this.custAcademicLevel.setMaxlength(8);
		this.academicDecipline.setMaxlength(8);
		this.custRefStaffID.setMaxlength(8);

		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			
		}else{
			this.groupboxWf.setVisible(false);
			
		}
		this.btnSearchAcademicDecipline.setVisible(false);
		logger.debug("Leaving ");
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
		logger.debug("Entering ");
		getUserWorkspace().alocateAuthorities("CustomerAdditionalDetailDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerAdditionalDetailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerAdditionalDetailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerAdditionalDetailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerAdditionalDetailDialog_btnSave"));
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
	public void onClose$window_CustomerAdditionalDetailDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_CustomerAdditionalDetailDialog);
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
		logger.debug("Entering ");
		boolean close = true;

		if (isDataChanged()) {
			logger.debug("doClose isDataChanged(): true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

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
		if(close){
			closeDialog(this.window_CustomerAdditionalDetailDialog, "CustomerAdditionalDetail");
		}
		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerAdditionalDetail
	 *            CustomerAdditionalDetail
	 */
	public void doWriteBeanToComponents(CustomerAdditionalDetail aCustomerAdditionalDetail) {
		logger.debug("Entering ");

		if(aCustomerAdditionalDetail.getCustID()!=Long.MIN_VALUE){
			this.custID.setValue(aCustomerAdditionalDetail.getCustID());	
		}
		this.custAcademicLevel.setValue(aCustomerAdditionalDetail.getCustAcademicLevel());
		this.academicDecipline.setValue(aCustomerAdditionalDetail.getAcademicDecipline());
		this.custRefCustID.setValue(aCustomerAdditionalDetail.getCustRefCustID());
		this.custRefStaffID.setValue(aCustomerAdditionalDetail.getCustRefStaffID());
		this.custCIF.setValue(aCustomerAdditionalDetail.getLovDescCustCIF()==null?
				"":aCustomerAdditionalDetail.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerAdditionalDetail.getLovDescCustShrtName()==null?
				"":aCustomerAdditionalDetail.getLovDescCustShrtName().trim());

		if (aCustomerAdditionalDetail.isNewRecord()){
			this.lovDescCustAcademicLevelName.setValue("");
			this.lovDescAcademicDeciplineName.setValue("");
		}else{
			this.lovDescCustAcademicLevelName
			.setValue(aCustomerAdditionalDetail.getCustAcademicLevel()+ "-"
					+ aCustomerAdditionalDetail.getLovDescCustAcademicLevelName());
			this.lovDescAcademicDeciplineName
			.setValue(aCustomerAdditionalDetail.getAcademicDecipline()+ "-"
					+ aCustomerAdditionalDetail.getLovDescAcademicDeciplineName());
		}
		this.recordStatus.setValue(aCustomerAdditionalDetail.getRecordStatus());
		logger.debug("Leaving ");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerAdditionalDetail
	 */
	public void doWriteComponentsToBean(CustomerAdditionalDetail aCustomerAdditionalDetail) {
		logger.debug("Entering ");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerAdditionalDetail.setCustID(this.custID.getValue());
			aCustomerAdditionalDetail.setLovDescCustCIF(this.custCIF.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerAdditionalDetail.setLovDescCustAcademicLevelName(this.lovDescCustAcademicLevelName.getValue());
			aCustomerAdditionalDetail.setCustAcademicLevel(this.custAcademicLevel.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerAdditionalDetail.setLovDescAcademicDeciplineName(this.lovDescAcademicDeciplineName.getValue());
			aCustomerAdditionalDetail.setAcademicDecipline(this.academicDecipline.getValue());	
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerAdditionalDetail.setCustRefCustID(this.custRefCustID.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCustomerAdditionalDetail.setCustRefStaffID(this.custRefStaffID.getValue());
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

		aCustomerAdditionalDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving ");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerAdditionalDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(CustomerAdditionalDetail aCustomerAdditionalDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.btnSearchCustAcademicLevel.focus();
		} else {
			this.custRefCustID.focus();
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
			doWriteBeanToComponents(aCustomerAdditionalDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_CustomerAdditionalDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving ");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering ");
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_custAcademicLevel = this.custAcademicLevel.getValue();
		this.oldVar_lovDescCustAcademicLevelName = this.lovDescCustAcademicLevelName.getValue();
		this.oldVar_academicDecipline = this.academicDecipline.getValue();
		this.oldVar_lovDescAcademicDeciplineName = this.lovDescAcademicDeciplineName.getValue();
		this.oldVar_custRefCustID = this.custRefCustID.longValue();
		this.oldVar_custRefStaffID = this.custRefStaffID.getValue();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving ");
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering ");
		this.custID.setValue(this.oldVar_custID);
		this.custAcademicLevel.setValue(this.oldVar_custAcademicLevel);
		this.lovDescCustAcademicLevelName.setValue(this.oldVar_lovDescCustAcademicLevelName);
		this.academicDecipline.setValue(this.oldVar_academicDecipline);
		this.lovDescAcademicDeciplineName.setValue(this.oldVar_lovDescAcademicDeciplineName);
		this.custRefCustID.setValue(this.oldVar_custRefCustID);
		this.custRefStaffID.setValue(this.oldVar_custRefStaffID);
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving ");
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
		if (this.oldVar_custAcademicLevel != this.custAcademicLevel.getValue()) {
			return true;
		}
		if (this.oldVar_academicDecipline != this.academicDecipline.getValue()) {
			return true;
		}
		if (this.oldVar_custRefCustID != this.custRefCustID.longValue()) {
			return true;
		}
		if (this.oldVar_custRefStaffID != this.custRefStaffID.getValue()) {
			return true;
		}
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering ");
		setValidationOn(true);

		if (!this.btnSearchPRCustid.isVisible()){
			this.custCIF.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAdditionalDetailDialog_CustID.value"),null,true));
		}
		if (!this.custRefCustID.isReadonly()){
			this.custRefCustID.setConstraint(new LongValidator(19,Labels.getLabel(
			"label_CustomerAdditionalDetailDialog_CustRefCustID.value")));
		}
		if (!this.custRefStaffID.isReadonly()){
			this.custRefStaffID.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAdditionalDetailDialog_CustRefStaffID.value"),null,true));
		}
		logger.debug("Leaving ");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering ");
		setValidationOn(false);
		this.custCIF.setConstraint("");
		this.custRefCustID.setConstraint("");
		this.custRefStaffID.setConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustAcademicLevelName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAdditionalDetailDialog_CustAcademicLevel.value"),null,true));
		this.lovDescAcademicDeciplineName.setConstraint(new PTStringValidator(Labels.getLabel("label_CustomerAdditionalDetailDialog_AcademicDecipline.value"),null,true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCustAcademicLevelName.setConstraint("");
		this.lovDescAcademicDeciplineName.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custRefCustID.setErrorMessage("");
		this.custRefStaffID.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a CustomerAdditionalDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering ");

		final CustomerAdditionalDetail aCustomerAdditionalDetail = new CustomerAdditionalDetail();
		BeanUtils.copyProperties(getCustomerAdditionalDetail(), aCustomerAdditionalDetail);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel(
		"message.Question.Are_you_sure_to_delete_this_record")
		+ "\n\n --> " + aCustomerAdditionalDetail.getCustID();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO,Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCustomerAdditionalDetail.getRecordType()).equals("")){
				aCustomerAdditionalDetail.setVersion(aCustomerAdditionalDetail.getVersion()+1);
				aCustomerAdditionalDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCustomerAdditionalDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if(doProcess(aCustomerAdditionalDetail,tranType)){
					refreshList();
					closeDialog(this.window_CustomerAdditionalDetailDialog, "CustomerAdditionalDetail"); 
				}

			}catch (DataAccessException e){
				logger.debug("Leaving");
				showMessage(e);
			}
		}
		logger.debug("Leaving ");
	}

	/**
	 * Create a new CustomerAdditionalDetail object. <br>
	 */
	private void doNew() {
		logger.debug("Entering ");

		// remember the old variables
		doStoreInitValues();
		/** !!! DO NOT BREAK THE TIERS !!! */
		// we don't create a new CustomerAdditionalDetail() in the frontEnd.
		// we get it from the backEnd.
		final CustomerAdditionalDetail aCustomerAdditionalDetail = getCustomerAdditionalDetailService().getNewCustomerAdditionalDetail();
		aCustomerAdditionalDetail.setNewRecord(true);
		setCustomerAdditionalDetail(aCustomerAdditionalDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.btnSearchCustAcademicLevel.focus();
		logger.debug("Leaving ");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering ");
		if (getCustomerAdditionalDetail().isNewRecord()){
			this.btnCancel.setVisible(false);
			this.btnSearchPRCustid.setVisible(true);
		}else{
			this.btnSearchPRCustid.setVisible(false);
			this.custID.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.custCIF.setReadonly(true);
		this.btnSearchCustAcademicLevel.setDisabled(isReadOnly("CustomerAdditionalDetailDialog_custAcademicLevel"));
		this.btnSearchAcademicDecipline.setDisabled(isReadOnly("CustomerAdditionalDetailDialog_academicDecipline"));
		this.custRefCustID.setReadonly(isReadOnly("CustomerAdditionalDetailDialog_custRefCustID"));
		this.custRefStaffID.setReadonly(isReadOnly("CustomerAdditionalDetailDialog_custRefStaffID"));
		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerAdditionalDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving ");
	}

	public boolean isReadOnly(String componentName){
		if (isWorkFlowEnabled()){
			return getUserWorkspace().isReadOnly(componentName);
		}
		return false;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering ");

		this.custCIF.setReadonly(true);
		this.btnSearchCustAcademicLevel.setDisabled(true);
		this.btnSearchAcademicDecipline.setDisabled(true);
		this.custRefCustID.setReadonly(true);
		this.custRefStaffID.setReadonly(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering ");

		// remove validation, if there are a save before
		this.custCIF.setText("");
		this.custAcademicLevel.setValue("");
		this.lovDescCustAcademicLevelName.setValue("");
		this.academicDecipline.setValue("");
		this.lovDescAcademicDeciplineName.setValue("");
		this.custRefCustID.setText("");
		this.custRefStaffID.setValue("");
		logger.debug("Leaving ");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering ");

		final CustomerAdditionalDetail aCustomerAdditionalDetail = new CustomerAdditionalDetail();
		BeanUtils.copyProperties(getCustomerAdditionalDetail(), aCustomerAdditionalDetail);
		boolean isNew = false;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the CustomerAdditionalDetail object with the components data
		doWriteComponentsToBean(aCustomerAdditionalDetail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerAdditionalDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aCustomerAdditionalDetail.getRecordType()).equals("")){
				aCustomerAdditionalDetail.setVersion(aCustomerAdditionalDetail.getVersion()+1);
				if(isNew){
					aCustomerAdditionalDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCustomerAdditionalDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerAdditionalDetail.setNewRecord(true);
				}
			}
		}else{
			aCustomerAdditionalDetail.setVersion(aCustomerAdditionalDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCustomerAdditionalDetail, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_CustomerAdditionalDetailDialog, "CustomerAdditionalDetail");
			}

		} catch (final DataAccessException e) {
			logger.debug("Leaving");
			showMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerAdditionalDetail
	 *            (CustomerAdditionalDetail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerAdditionalDetail aCustomerAdditionalDetail, String tranType){
		logger.debug("Entering ");

		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCustomerAdditionalDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCustomerAdditionalDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerAdditionalDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCustomerAdditionalDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerAdditionalDetail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCustomerAdditionalDetail);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aCustomerAdditionalDetail))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
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

			aCustomerAdditionalDetail.setTaskId(taskId);
			aCustomerAdditionalDetail.setNextTaskId(nextTaskId);
			aCustomerAdditionalDetail.setRoleCode(getRole());
			aCustomerAdditionalDetail.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCustomerAdditionalDetail, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCustomerAdditionalDetail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCustomerAdditionalDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCustomerAdditionalDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("Leaving ");
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
		logger.debug("Entering ");

		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		CustomerAdditionalDetail aCustomerAdditionalDetail = (CustomerAdditionalDetail)
		auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCustomerAdditionalDetailService().delete(auditHeader);
						deleteNotes = true;
					}else{
						auditHeader = getCustomerAdditionalDetailService().saveOrUpdate(auditHeader);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerAdditionalDetailService().doApprove(auditHeader);

						if (aCustomerAdditionalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerAdditionalDetailService().doReject(auditHeader);

						if (aCustomerAdditionalDetail.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerAdditionalDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerAdditionalDetailDialog, auditHeader);
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
			logger.debug("Leaving");
			e.printStackTrace();
		}
		logger.debug("Leaving ");
		return processCompleted;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++ Search Button Component Events++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void onClick$btnSearchCustAcademicLevel(Event event){
		logger.debug("Entering" + event.toString());

		String sCustAcademicLevel= this.custAcademicLevel.getValue();
		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerAdditionalDetailDialog, "Academic");

		if (dataObject instanceof String){
			this.custAcademicLevel.setValue(dataObject.toString());
			this.lovDescCustAcademicLevelName.setValue("");
		}else{
			Academic details= (Academic) dataObject;
			if (details != null) {
				this.custAcademicLevel.setValue(details.getAcademicLevel());
				this.lovDescCustAcademicLevelName.setValue(details.getLovValue() + "-" + details.getAcademicDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustAcademicLevel).equals(this.custAcademicLevel.getValue())){
			this.academicDecipline.setValue("");
			this.lovDescAcademicDeciplineName.setValue("");
			this.btnSearchAcademicDecipline.setVisible(false);
		}

		if(!this.lovDescCustAcademicLevelName.getValue().equals("")){		   
			this.btnSearchAcademicDecipline.setVisible(true);		   
		}else{
			this.lovDescAcademicDeciplineName.setValue("");
			this.btnSearchAcademicDecipline.setVisible(false);	
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnSearchAcademicDecipline(Event event){
		logger.debug("Entering" + event.toString());

		Filter[] filters = new Filter[1] ;
		filters[0]= new Filter("academicLevel", this.custAcademicLevel.getValue(), Filter.OP_EQUAL); 

		Object dataObject = ExtendedSearchListBox.show(this.window_CustomerAdditionalDetailDialog, "Academic",filters);
		if (dataObject instanceof String){
			this.academicDecipline.setValue(dataObject.toString());
			this.lovDescAcademicDeciplineName.setValue("");
		}else{
			Academic details= (Academic) dataObject;
			if (details != null) {
				this.academicDecipline.setValue(details.getAcademicDecipline());
				this.lovDescAcademicDeciplineName.setValue(details.getLovValue()+ "-"	+ details.getAcademicDesc());
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
		onLoad();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onLoad() throws SuspendNotAllowedException, InterruptedException{
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
	 * @param aCustomerAdditionalDetail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerAdditionalDetail aCustomerAdditionalDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerAdditionalDetail.getBefImage(),aCustomerAdditionalDetail);
		return new AuditHeader(String.valueOf(aCustomerAdditionalDetail.getId())
				, String.valueOf(aCustomerAdditionalDetail.getCustID()),
				null, null, auditDetail, aCustomerAdditionalDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	private void showMessage(Exception e){
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerAdditionalDetailDialog, auditHeader);
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
		Notes notes = new Notes();
		notes.setModuleName("CustomerAdditionalDetail");
		notes.setReference(String.valueOf(getCustomerAdditionalDetail().getCustID()));
		notes.setVersion(getCustomerAdditionalDetail().getVersion());

		map.put("notes", notes);
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
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	// Method for refreshing the list after successful updation
	private void refreshList() {
		logger.debug("Entering ");
		final JdbcSearchObject<CustomerAdditionalDetail> soCustomerAdditionalDetail =
			getCustomerAdditionalDetailListCtrl().getSearchObj();
		getCustomerAdditionalDetailListCtrl().pagingCustomerAdditionalDetailList.setActivePage(0);
		getCustomerAdditionalDetailListCtrl().getPagedListWrapper().setSearchObject(soCustomerAdditionalDetail);
		if (getCustomerAdditionalDetailListCtrl().listBoxCustomerAdditionalDetail != null) {
			getCustomerAdditionalDetailListCtrl().listBoxCustomerAdditionalDetail.getListModel();
		}
		logger.debug("Leaving ");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("CustomerAdditionalDetail");
		notes.setReference(String.valueOf(getCustomerAdditionalDetail().getId()));
		notes.setVersion(getCustomerAdditionalDetail().getVersion());
		logger.debug("Leaving ");
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

	public CustomerAdditionalDetail getCustomerAdditionalDetail() {
		return this.customerAdditionalDetail;
	}
	public void setCustomerAdditionalDetail(
			CustomerAdditionalDetail customerAdditionalDetail) {
		this.customerAdditionalDetail = customerAdditionalDetail;
	}

	public void setCustomerAdditionalDetailService(
			CustomerAdditionalDetailService customerAdditionalDetailService) {
		this.customerAdditionalDetailService = customerAdditionalDetailService;
	}
	public CustomerAdditionalDetailService getCustomerAdditionalDetailService() {
		return this.customerAdditionalDetailService;
	}

	public void setCustomerAdditionalDetailListCtrl(CustomerAdditionalDetailListCtrl customerAdditionalDetailListCtrl) {
		this.customerAdditionalDetailListCtrl = customerAdditionalDetailListCtrl;
	}
	public CustomerAdditionalDetailListCtrl getCustomerAdditionalDetailListCtrl() {
		return this.customerAdditionalDetailListCtrl;
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

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}
	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	public boolean isNewRecord() {
		return newRecord;
	}
}
