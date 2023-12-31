/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : CustomerRatingDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
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
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerRating/customerRatingDialog.zul file.
 */
public class CustomerRatingDialogCtrl extends GFCBaseCtrl<CustomerRating> {
	private static final long serialVersionUID = -6959194080451993569L;
	private static final Logger logger = LogManager.getLogger(CustomerRatingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerRatingDialog;// autowired

	protected Longbox custID; // autowired
	protected ExtendedCombobox custRatingType; // autowired
	protected ExtendedCombobox custRatingCode; // autowired
	protected ExtendedCombobox custRating; // autowired
	protected Textbox custCIF; // autowired
	protected Label custShrtName; // autowired

	// not auto wired vars
	private CustomerRating customerRating; // overhanded per param
	private transient CustomerRatingListCtrl customerRatingListCtrl; // overhanded per param

	private transient boolean validationOn;

	protected Button btnSearchPRCustid; // autowire

	// ServiceDAOs / Domain Classes
	private transient CustomerRatingService customerRatingService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerRating> customerRatings;
	private CustomerDialogCtrl customerDialogCtrl;
	private transient boolean isRatingTypeNumeric;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String sCustRatingType;

	/**
	 * default constructor.<br>
	 */
	public CustomerRatingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerRatingDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerRating object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_CustomerRatingDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerRatingDialog);

		if (arguments.containsKey("roleCode")) {
			role = (String) arguments.get("roleCode");
		}
		/* set components visible dependent of the users rights */
		doCheckRights();

		// READ OVERHANDED params !
		if (arguments.containsKey("customerRating")) {
			this.customerRating = (CustomerRating) arguments.get("customerRating");
			CustomerRating befImage = new CustomerRating();
			BeanUtils.copyProperties(this.customerRating, befImage);
			this.customerRating.setBefImage(befImage);
			setCustomerRating(this.customerRating);
		} else {
			setCustomerRating(null);
		}

		if (arguments.containsKey("moduleType")) {
			this.moduleType = (String) arguments.get("moduleType");
		}

		if (getCustomerRating().isNewRecord()) {
			setNewRecord(true);
		}

		if (arguments.containsKey("customerDialogCtrl")) {

			setCustomerDialogCtrl((CustomerDialogCtrl) arguments.get("customerDialogCtrl"));
			setNewCustomer(true);

			if (arguments.containsKey("newRecord")) {
				setNewRecord(true);
			} else {
				setNewRecord(false);
			}
			this.customerRating.setWorkflowId(0);
			getUserWorkspace().allocateRoleAuthorities(role, "CustomerRatingDialog");

		}

		doLoadWorkFlow(this.customerRating.isWorkflow(), this.customerRating.getWorkflowId(),
				this.customerRating.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerRatingDialog");
		}

		// READ OVERHANDED params !
		// we get the customerRatingListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete customerRating here.
		if (arguments.containsKey("customerRatingListCtrl")) {
			setCustomerRatingListCtrl((CustomerRatingListCtrl) arguments.get("customerRatingListCtrl"));
		} else {
			setCustomerRatingListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCustomerRating());

		// Calling SelectCtrl For proper selection of Customer
		if (isNewRecord() && !isNewCustomer()) {
			onload();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 * 
	 * @throws InterruptedException
	 * @throws SuspendNotAllowedException
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custRatingType.setMaxlength(8);
		this.custRatingType.setMandatoryStyle(true);
		this.custRatingType.setTextBoxWidth(110);
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

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);

		} else {
			this.groupboxWf.setVisible(false);

		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerRatingDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerRatingDialog);
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
	 * @param event An event sent to the event handler of a component.
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
		doWriteBeanToComponents(this.customerRating.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerRating CustomerRating
	 */
	public void doWriteBeanToComponents(CustomerRating aCustomerRating) {
		logger.debug("Entering");

		if (aCustomerRating.getCustID() != Long.MIN_VALUE) {
			this.custID.setValue(aCustomerRating.getCustID());
		}

		this.custRatingType.setValue(aCustomerRating.getCustRatingType());
		doSetRatingCodeFilters(aCustomerRating.getCustRatingType());

		this.custRatingCode.setValue(aCustomerRating.getCustRatingCode(),
				StringUtils.trimToEmpty(aCustomerRating.getLovDesccustRatingCodeDesc()));
		this.custRating.setValue(aCustomerRating.getCustRating(),
				StringUtils.trimToEmpty(aCustomerRating.getLovDescCustRatingName()));

		this.isRatingTypeNumeric = aCustomerRating.isValueType();

		this.custCIF.setValue(
				aCustomerRating.getLovDescCustCIF() == null ? "" : aCustomerRating.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerRating.getLovDescCustShrtName() == null ? ""
				: aCustomerRating.getLovDescCustShrtName().trim());

		if (isNewRecord()) {
			this.custRatingType.setDescription("");
			this.custRatingType.setReadonly(false);

		} else {
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
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerRating.setLovDescCustRatingTypeName(this.custRatingType.getDescription());
			aCustomerRating.setCustRatingType(this.custRatingType.getValue());
			aCustomerRating.setValueType(this.isRatingTypeNumeric);
		} catch (WrongValueException we) {
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

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerRating
	 */
	public void doShowDialog(CustomerRating aCustomerRating) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custRatingCode.focus();
			if (isNewCustomer()) {
				doEdit();
			} else if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCustomerRating);

			doCheckEnquiry();
			this.btnCancel.setVisible(false);
			if (isNewCustomer()) {
				this.window_CustomerRatingDialog.setHeight("228px");
				this.window_CustomerRatingDialog.setWidth("800px");
				this.groupboxWf.setVisible(false);
				this.window_CustomerRatingDialog.doModal();
			} else {
				this.window_CustomerRatingDialog.setWidth("100%");
				this.window_CustomerRatingDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custID.setReadonly(true);
			this.custRatingType.setReadonly(true);
			this.custRatingCode.setReadonly(true);
			this.custRating.setReadonly(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);

		if (!this.custID.isReadonly()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CustomerRatingDialog_CustID.value"), null, true));
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

		this.custRatingType.setConstraint(new PTStringValidator(
				Labels.getLabel("label_CustomerRatingDialog_CustRatingType.value"), null, true, true));

		// this.custRatingCode.setConstraint(new
		// PTStringValidator(Labels.getLabel("label_CustomerRatingDialog_CustRatingCode.value"),null,true,true));

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
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custRating.setErrorMessage("");
		this.custRatingCode.setErrorMessage("");
		this.custRating.setErrorMessage("");
		this.custRatingType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getCustomerRatingListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a CustomerRating object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final CustomerRating aCustomerRating = new CustomerRating();
		BeanUtils.copyProperties(getCustomerRating(), aCustomerRating);

		final String keyReference = Labels.getLabel("label_CustomerRatingDialog_CustRatingType.value") + " : "
				+ aCustomerRating.getCustRatingType();

		doDelete(keyReference, aCustomerRating);

		logger.debug("Leaving");
	}

	protected void onDoDelete(final CustomerRating aCustomerRating) {
		String tranType = PennantConstants.TRAN_WF;
		if (StringUtils.isBlank(aCustomerRating.getRecordType())) {
			aCustomerRating.setVersion(aCustomerRating.getVersion() + 1);
			aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (getCustomerDialogCtrl() != null
					&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
				aCustomerRating.setNewRecord(true);
			}
			if (isWorkFlowEnabled()) {
				aCustomerRating.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}
		try {
			if (isNewCustomer()) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newCusomerProcess(aCustomerRating, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerRatingDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerRatings(this.customerRatings);
					// send the data back to customer
					closeDialog();
				}

			} else if (doProcess(aCustomerRating, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			showMessage(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (isNewRecord()) {

			if (isNewCustomer()) {
				this.btnCancel.setVisible(false);
				this.btnSearchPRCustid.setVisible(false);
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custRatingType.setReadonly(isReadOnly("CustomerRatingDialog_custRatingType"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custRatingType.setReadonly(true);
		}
		this.custCIF.setReadonly(true);
		this.custID.setReadonly(isReadOnly("CustomerRatingDialog_custID"));
		this.custRatingCode.setReadonly(isReadOnly("CustomerRatingDialog_custRatingCode"));
		this.custRating.setReadonly(isReadOnly("CustomerRatingDialog_custRating"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerRating.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {

			if (newCustomer) {
				if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newCustomer);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public boolean isReadOnly(String componentName) {
		boolean isCustomerWorkflow = false;
		if (getCustomerDialogCtrl() != null) {
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
		}
		if (isWorkFlowEnabled() || isCustomerWorkflow) {
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

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
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

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerRating object with the components data
		doWriteComponentsToBean(aCustomerRating);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerRating.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerRating.getRecordType())) {
				aCustomerRating.setVersion(aCustomerRating.getVersion() + 1);
				if (isNew) {
					aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerRating.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerRating.setVersion(1);
					aCustomerRating.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aCustomerRating.getRecordType())) {
					aCustomerRating.setVersion(aCustomerRating.getVersion() + 1);
					aCustomerRating.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerRating.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCustomerRating.setVersion(aCustomerRating.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {
			if (isNewCustomer()) {
				AuditHeader auditHeader = newCusomerProcess(aCustomerRating, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerRatingDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerRatings(this.customerRatings);
					// true;
					// send the data back to customer
					closeDialog();

				}

			} else if (doProcess(aCustomerRating, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCusomerProcess(CustomerRating aCustomerRating, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerRating, tranType);
		customerRatings = new ArrayList<CustomerRating>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerRating.getId());
		valueParm[1] = aCustomerRating.getCustRatingType();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustRatingType") + ":" + valueParm[1];

		if (getCustomerDialogCtrl().getRatingsList() != null && getCustomerDialogCtrl().getRatingsList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getRatingsList().size(); i++) {
				CustomerRating customerRating = getCustomerDialogCtrl().getRatingsList().get(i);

				if (customerRating.getCustRatingType().equals(aCustomerRating.getCustRatingType())) { // Both Current
																										// and Existing
																										// list rating
																										// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
							aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							customerRatings.add(aCustomerRating);
						} else if (aCustomerRating.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerRating.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							customerRatings.add(aCustomerRating);
						} else if (aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getRatingsList()
									.size(); j++) {
								CustomerRating rating = getCustomerDialogCtrl().getCustomerDetails().getRatingsList()
										.get(j);
								if (rating.getCustID() == aCustomerRating.getCustID()
										&& rating.getCustRatingType().equals(aCustomerRating.getCustRatingType())) {
									customerRatings.add(rating);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerRatings.add(customerRating);
						}
					}
				} else {
					customerRatings.add(customerRating);
				}
			}
		}
		if (!recordAdded) {
			customerRatings.add(aCustomerRating);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerRating (CustomerRating)
	 * 
	 * @param tranType        (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CustomerRating aCustomerRating, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerRating.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustomerRating.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerRating.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerRating.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerRating.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerRating);
				}

				if (isNotesMandatory(taskId, aCustomerRating)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");
				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCustomerRating.setTaskId(taskId);
			aCustomerRating.setNextTaskId(nextTaskId);
			aCustomerRating.setRoleCode(getRole());
			aCustomerRating.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerRating, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerRating);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerRating, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerRating, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerRating aCustomerRating = (CustomerRating) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getCustomerRatingService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getCustomerRatingService().saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getCustomerRatingService().doApprove(auditHeader);

					if (aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getCustomerRatingService().doReject(auditHeader);

					if (aCustomerRating.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_CustomerRatingDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_CustomerRatingDialog, auditHeader);
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$custRatingType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custRatingType.getObject();
		if (dataObject instanceof String) {
			this.custRatingType.setValue(dataObject.toString());
			this.custRatingType.setDescription("");

		} else {
			RatingType details = (RatingType) dataObject;
			if (details != null) {
				this.custRatingType.setValue(details.getRatingType());
				this.custRatingType.setDescription(details.getRatingTypeDesc());
				this.isRatingTypeNumeric = details.isValueType();
				doSetRatingCodeFilters(details.getRatingType());
			}
		}

		if (!StringUtils.trimToEmpty(sCustRatingType).equals(this.custRatingType.getValue())) {
			this.custRatingCode.setValue("");
		}
		sCustRatingType = this.custRatingType.getValue();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 */
	private void onload() {
		logger.debug("Entering");
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF().trim());
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.newSearchObject = newSearchObject;
		logger.debug("Leaving");
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerRating aCustomerRating, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerRating.getBefImage(), aCustomerRating);

		return new AuditHeader(getReference(), String.valueOf(aCustomerRating.getCustID()), null, null, auditDetail,
				aCustomerRating.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerRatingDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
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
	@Override
	protected String getReference() {
		return getCustomerRating().getCustID() + PennantConstants.KEY_SEPERATOR
				+ getCustomerRating().getCustRatingType();
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
		if (StringUtils.isNotBlank(sectorcode)) {
			Filter filters[] = new Filter[1];
			filters[0] = new Filter("RatingType", sectorcode, Filter.OP_EQUAL);
			this.custRatingCode.setFilters(filters);
			this.custRating.setFilters(filters);
		}
		this.custRatingCode.setValue("", "");
		this.custRating.setValue("", "");
	}
}
