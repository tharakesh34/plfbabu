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
 * FileName    		:  CustomerEMailDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customeremail;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.customermasters.CustomerEMailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.customermasters.customer.CustomerViewDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerEMail/customerEMailDialog.zul file.
 */
public class CustomerEMailDialogCtrl extends GFCBaseCtrl<CustomerEMail> {
	private static final long serialVersionUID = -7522534300621535097L;
	private static final Logger logger = Logger.getLogger(CustomerEMailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerEMailDialog; // autoWired

	protected Longbox custID; // autoWired
	protected ExtendedCombobox custEMailTypeCode; // autoWired
	protected Combobox custEMailPriority; // autoWired
	protected Textbox custEMail; // autoWired
	protected Textbox custCIF; // autoWired
	protected Label custShrtName; // autoWired

	// not auto wired variables
	private CustomerEMail customerEMail; // overHanded per parameter
	private transient CustomerEMailListCtrl customerEMailListCtrl; // overHanded
																	// per
																	// parameter

	private transient boolean validationOn;

	protected Button btnSearchPRCustid; // autoWired

	// ServiceDAOs / Domain Classes
	private transient CustomerEMailService customerEMailService;
	private transient PagedListService pagedListService;
	protected JdbcSearchObject<Customer> searchObj;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerEMail> customerEmails;
	private CustomerDialogCtrl customerDialogCtrl;
	private CustomerViewDialogCtrl customerViewDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	private boolean workflow = false;
	private final List<ValueLabel> CustomerPriorityList = PennantStaticListUtil
			.getCustomerEmailPriority();

	/**
	 * default constructor.<br>
	 */
	public CustomerEMailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerEMailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerEMail object
	 * in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerEMailDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerEMailDialog);

		try {

			if (arguments.containsKey("customerEMail")) {
				this.customerEMail = (CustomerEMail) arguments
						.get("customerEMail");
				CustomerEMail befImage = new CustomerEMail();
				BeanUtils.copyProperties(this.customerEMail, befImage);
				this.customerEMail.setBefImage(befImage);
				setCustomerEMail(this.customerEMail);
			} else {
				setCustomerEMail(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getCustomerEMail().isNewRecord()) {
				setNewRecord(true);
			}

			if (arguments.containsKey("customerDialogCtrl")) {
				setCustomerDialogCtrl((CustomerDialogCtrl) arguments
						.get("customerDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerEMail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole,
							"CustomerEMailDialog");
				}
			}
			if (arguments.containsKey("customerViewDialogCtrl")) {
				setCustomerViewDialogCtrl((CustomerViewDialogCtrl) arguments.get("customerViewDialogCtrl"));
				setNewCustomer(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.customerEMail.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerEMailDialog");
				}
			}
			
			if (getCustomerDialogCtrl() != null) {
				workflow = getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow();
			}
			
			doLoadWorkFlow(this.customerEMail.isWorkflow(),
					this.customerEMail.getWorkflowId(),
					this.customerEMail.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"CustomerEMailDialog");
			}

			// READ OVERHANDED parameters !
			// we get the customerEMailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete customerEMail here.
			if (arguments.containsKey("customerEMailListCtrl")) {
				setCustomerEMailListCtrl((CustomerEMailListCtrl) arguments
						.get("customerEMailListCtrl"));
			} else {
				setCustomerEMailListCtrl(null);
			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomerEMail());

			// Calling SelectCtrl For proper selection of Customer
			if (isNewRecord() && !isNewCustomer()) {
				onload();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerEMailDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.custEMailTypeCode.setMaxlength(8);
		this.custEMailTypeCode.setMandatoryStyle(true);
		this.custEMailTypeCode.setTextBoxWidth(116);
		this.custEMailTypeCode.setModuleName("EMailType");
		this.custEMailTypeCode.setValueColumn("EmailTypeCode");
		this.custEMailTypeCode.setDescColumn("EmailTypeDesc");
		this.custEMailTypeCode
				.setValidateColumns(new String[] { "EmailTypeCode" });
		this.custEMail.setMaxlength(100);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
			this.south.setHeight("0px");
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
		getUserWorkspace().allocateAuthorities("CustomerEMailDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerEMailDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerEMailDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerEMailDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_CustomerEMailDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerEMailDialog);
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
		doWriteBeanToComponents(this.customerEMail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerEMail
	 *            CustomerEMail
	 */
	public void doWriteBeanToComponents(CustomerEMail aCustomerEMail) {
		logger.debug("Entering");

		if (aCustomerEMail.getCustID() != Long.MIN_VALUE) {
			this.custID.setValue(aCustomerEMail.getCustID());
		}
		this.custEMailTypeCode.setValue(aCustomerEMail.getCustEMailTypeCode());
		fillComboBox(this.custEMailPriority,
				String.valueOf(aCustomerEMail.getCustEMailPriority()),
				CustomerPriorityList, "");
		this.custEMail.setValue(aCustomerEMail.getCustEMail());
		this.custCIF.setValue(aCustomerEMail.getLovDescCustCIF() == null ? ""
				: aCustomerEMail.getLovDescCustCIF().trim());
		this.custShrtName
				.setValue(aCustomerEMail.getLovDescCustShrtName() == null ? ""
						: aCustomerEMail.getLovDescCustShrtName().trim());

		if (isNewRecord()) {
			this.custEMailTypeCode.setDescription("");
		} else {
			this.custEMailTypeCode.setDescription(aCustomerEMail
					.getLovDescCustEMailTypeCode());
		}
		this.recordStatus.setValue(aCustomerEMail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerEMail
	 */
	public void doWriteComponentsToBean(CustomerEMail aCustomerEMail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerEMail.setLovDescCustCIF(this.custCIF.getValue());
			aCustomerEMail.setCustID(this.custID.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerEMail.setLovDescCustEMailTypeCode(this.custEMailTypeCode
					.getDescription());
			aCustomerEMail.setCustEMailTypeCode(this.custEMailTypeCode
					.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
			
		try {
			if ("#".equals(getComboboxValue(this.custEMailPriority))) {
				aCustomerEMail.setCustEMailPriority(0);
			} else {
				aCustomerEMail.setCustEMailPriority(Integer.valueOf(getComboboxValue(this.custEMailPriority)));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCustomerEMail.setCustEMail(this.custEMail.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCustomerEMail.setRecordStatus(this.recordStatus.getValue());
		setCustomerEMail(aCustomerEMail);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerEMail
	 * @throws Exception
	 */
	public void doShowDialog(CustomerEMail aCustomerEMail) throws Exception {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custEMailPriority.focus();
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
			doWriteBeanToComponents(aCustomerEMail);

			doCheckEnquiry();
			if (isNewCustomer()) {
				this.window_CustomerEMailDialog.setHeight("35%");
				this.window_CustomerEMailDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerEMailDialog.doModal();
			} else {
				this.window_CustomerEMailDialog.setWidth("100%");
				this.window_CustomerEMailDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerEMailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if (PennantConstants.MODULETYPE_ENQ.equals(this.moduleType)) {
			this.custEMailTypeCode.setReadonly(true);
			this.custEMailPriority.setDisabled(true);
			this.custEMail.setReadonly(true);
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

		if (this.btnSearchPRCustid.isVisible()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels
					.getLabel("label_CustomerEMailDialog_CustID.value"), null,
					true));
		}
		
		if (!this.custEMailPriority.isDisabled()) {
			this.custEMailPriority.setConstraint(new PTStringValidator(Labels
					.getLabel("label_CustomerEMailDialog_CustEMailPriority.value"), null, true));
		}
				 
		if (!this.custEMail.isReadonly()) {
			this.custEMail.setConstraint(new PTEmailValidator(Labels
					.getLabel("label_CustomerEMailDialog_CustEMail.value"),
					true));
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
		this.custEMailPriority.setConstraint("");
		this.custEMail.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.custEMailTypeCode.setConstraint(new PTStringValidator(Labels
				.getLabel("label_CustomerEMailDialog_CustEMailTypeCode.value"),
				null, true, true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.custEMailTypeCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.custEMailPriority.setErrorMessage("");
		this.custEMail.setErrorMessage("");
		this.custEMailTypeCode.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {

		getCustomerEMailListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a CustomerEMail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerEMail aCustomerEMail = new CustomerEMail();
		BeanUtils.copyProperties(getCustomerEMail(), aCustomerEMail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_CustomerEMailDialog_CustEMailTypeCode.value")
				+ " : " + aCustomerEMail.getCustEMailTypeCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerEMail.getRecordType())) {
				aCustomerEMail.setVersion(aCustomerEMail.getVersion() + 1);
				aCustomerEMail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (!isFinanceProcess
						&& getCustomerDialogCtrl() != null
						&& getCustomerDialogCtrl().getCustomerDetails()
								.getCustomer().isWorkflow()) {
					aCustomerEMail.setNewRecord(true);
				}
				if (isWorkFlowEnabled()) {
					aCustomerEMail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {

				if (isNewCustomer()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newCustomerEmailProcess(
							aCustomerEMail, tranType);
					auditHeader = ErrorControl.showErrorDetails(
							this.window_CustomerEMailDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE
							|| retValue == PennantConstants.porcessOVERIDE) {
						getCustomerDialogCtrl().doFillCustomerEmailDetails(
								this.customerEmails);
						// send the data back to customer
						closeDialog();
					}
				} else if (doProcess(aCustomerEMail, tranType)) {
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

		if (isNewRecord()) {
			if (isNewCustomer()) {
				this.btnCancel.setVisible(false);
				this.btnSearchPRCustid.setVisible(false);
			} else {
				this.btnSearchPRCustid.setVisible(true);
			}
			this.custEMailTypeCode
					.setReadonly(isReadOnly("CustomerEMailDialog_custEMailTypeCode"));
			this.custEMailTypeCode
					.setMandatoryStyle(!isReadOnly("CustomerEMailDialog_custEMailTypeCode"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.custEMailTypeCode.setReadonly(true);
		}
		this.custCIF.setReadonly(true);
		this.custID.setReadonly(isReadOnly("CustomerEMailDialog_custID"));
		this.custEMailPriority
				.setDisabled(isReadOnly("CustomerEMailDialog_custEMailPriority"));
		this.custEMail.setReadonly(isReadOnly("CustomerEMailDialog_custEMail"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerEMail.isNewRecord()) {
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
			isCustomerWorkflow = getCustomerDialogCtrl().getCustomerDetails()
					.getCustomer().isWorkflow();
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
		this.custEMailTypeCode.setReadonly(true);
		this.custEMailPriority.setDisabled(true);
		this.custEMail.setReadonly(true);

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
		this.custEMailTypeCode.setValue("");
		this.custEMailTypeCode.setDescription("");
		this.custEMailPriority.setText("");
		this.custEMail.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final CustomerEMail aCustomerEMail = new CustomerEMail();
		BeanUtils.copyProperties(getCustomerEMail(), aCustomerEMail);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerEMail object with the components data
		doWriteComponentsToBean(aCustomerEMail);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerEMail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerEMail.getRecordType())) {
				aCustomerEMail.setVersion(aCustomerEMail.getVersion() + 1);
				if (isNew) {
					aCustomerEMail
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerEMail
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerEMail.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerEMail.setVersion(1);
					aCustomerEMail.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if(workflow) {
						aCustomerEMail.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(aCustomerEMail.getRecordType())) {
					aCustomerEMail.setVersion(aCustomerEMail.getVersion() + 1);
					aCustomerEMail.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerEMail.getRecordType().equals(
						PennantConstants.RCD_ADD)
						&& isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerEMail.getRecordType().equals(
						PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCustomerEMail.setVersion(aCustomerEMail.getVersion() + 1);
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
				AuditHeader auditHeader = newCustomerEmailProcess(
						aCustomerEMail, tranType);
				auditHeader = ErrorControl.showErrorDetails(
						this.window_CustomerEMailDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE
						|| retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerEmailDetails(
							this.customerEmails);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aCustomerEMail, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private AuditHeader newCustomerEmailProcess(CustomerEMail aCustomerEMail,
			String tranType) {
		logger.debug("Entering");
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerEMail, tranType);
		customerEmails = new ArrayList<CustomerEMail>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = aCustomerEMail.getLovDescCustCIF();
		valueParm[1] = aCustomerEMail.getCustEMailTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":"
				+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_CustEMailTypeCode") + ":"
				+ valueParm[1];

		if (getCustomerDialogCtrl().getCustomerEmailDetailList() != null
				&& getCustomerDialogCtrl().getCustomerEmailDetailList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl()
					.getCustomerEmailDetailList().size(); i++) {
				CustomerEMail customerEMail = getCustomerDialogCtrl()
						.getCustomerEmailDetailList().get(i);
				
				if (isNewRecord()) {

					if (customerEMail.getCustEMailPriority() == aCustomerEMail.getCustEMailPriority()) {
						valueParm[1]=this.custEMailPriority.getSelectedItem().getLabel();
						errParm[1] = PennantJavaUtil.getLabel("label_CustEMailPriority") + ":"+valueParm[1];
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail(
								PennantConstants.KEY_FIELD, "30702", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}
				}

				if (aCustomerEMail.getCustEMailTypeCode().equals(
						customerEMail.getCustEMailTypeCode())) { // Both Current
																	// and
																	// Existing
																	// list
																	// rating
																	// same

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD,
										"41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerEMail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_UPD)) {
							aCustomerEMail
									.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							customerEmails.add(aCustomerEMail);
						} else if (aCustomerEMail.getRecordType().equals(
								PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerEMail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerEMail
									.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							customerEmails.add(aCustomerEMail);
						} else if (aCustomerEMail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl()
									.getCustomerDetails()
									.getCustomerEMailList().size(); j++) {
								CustomerEMail email = getCustomerDialogCtrl()
										.getCustomerDetails()
										.getCustomerEMailList().get(j);
								if (email.getCustID() == aCustomerEMail
										.getCustID()
										&& email.getCustEMailTypeCode()
												.equals(aCustomerEMail
														.getCustEMailTypeCode())) {
									customerEmails.add(email);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerEmails.add(customerEMail);
						}
					}
				} else {
					customerEmails.add(customerEMail);
				}
			}
		}

		if (!recordAdded) {
			customerEmails.add(aCustomerEMail);
		}
		logger.debug("Leaving");
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerEMail
	 *            (CustomerEMail)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerEMail aCustomerEMail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerEMail.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getUserId());
		aCustomerEMail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerEMail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerEMail.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerEMail
						.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerEMail);
				}

				if (isNotesMandatory(taskId, aCustomerEMail)) {
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

			aCustomerEMail.setTaskId(taskId);
			aCustomerEMail.setNextTaskId(nextTaskId);
			aCustomerEMail.setRoleCode(getRole());
			aCustomerEMail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerEMail, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerEMail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerEMail,
							PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerEMail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CustomerEMail aCustomerEMail = (CustomerEMail) auditHeader
				.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerEMailService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerEMailService().saveOrUpdate(
								auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(
							PennantConstants.method_doApprove)) {
						auditHeader = getCustomerEMailService().doApprove(
								auditHeader);
						if (aCustomerEMail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerEMailService().doReject(
								auditHeader);
						if (aCustomerEMail.getRecordType().equals(
								PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(
								PennantConstants.ERR_9999, Labels
										.getLabel("InvalidWorkFlowMethod"),
								null));
						retValue = ErrorControl.showErrorControl(
								this.window_CustomerEMailDialog, auditHeader);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(
						this.window_CustomerEMailDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.customerEMail), true);
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
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	// Search Button Component Events

	public void onFulfill$custEMailTypeCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = custEMailTypeCode.getObject();
		if (dataObject instanceof String) {
			this.custEMailTypeCode.setValue(dataObject.toString());
			this.custEMailTypeCode.setDescription("");
		} else {
			EMailType details = (EMailType) dataObject;
			if (details != null) {
				this.custEMailTypeCode.setValue(details.getEmailTypeCode());
				this.custEMailTypeCode.setDescription(details
						.getEmailTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Calling list Of existed Customers
	 * 
	 * @param event
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchPRCustid(Event event)
			throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		onload();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void onload() throws SuspendNotAllowedException,
			InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents(
				"/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul",
				null, map);
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer,
			JdbcSearchObject<Customer> newSearchObject)
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
	 * @param aCustomerEMail
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustomerEMail aCustomerEMail,
			String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aCustomerEMail.getBefImage(), aCustomerEMail);
		return new AuditHeader(getReference(), String.valueOf(aCustomerEMail
				.getCustID()), null, null, auditDetail,
				aCustomerEMail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(
					PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerEMailDialog,
					auditHeader);
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
		doShowNotes(this.customerEMail);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerEMail().getCustID() + PennantConstants.KEY_SEPERATOR
				+ getCustomerEMail().getCustEMailTypeCode();
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

	public CustomerEMail getCustomerEMail() {
		return this.customerEMail;
	}

	public void setCustomerEMail(CustomerEMail customerEMail) {
		this.customerEMail = customerEMail;
	}

	public void setCustomerEMailService(
			CustomerEMailService customerEMailService) {
		this.customerEMailService = customerEMailService;
	}

	public CustomerEMailService getCustomerEMailService() {
		return this.customerEMailService;
	}

	public void setCustomerEMailListCtrl(
			CustomerEMailListCtrl customerEMailListCtrl) {
		this.customerEMailListCtrl = customerEMailListCtrl;
	}

	public CustomerEMailListCtrl getCustomerEMailListCtrl() {
		return this.customerEMailListCtrl;
	}

	public void setCustomerEmails(List<CustomerEMail> customerEmails) {
		this.customerEmails = customerEmails;
	}

	public List<CustomerEMail> getCustomerEmails() {
		return customerEmails;
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

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public CustomerViewDialogCtrl getCustomerViewDialogCtrl() {
		return customerViewDialogCtrl;
	}

	public void setCustomerViewDialogCtrl(CustomerViewDialogCtrl customerViewDialogCtrl) {
		this.customerViewDialogCtrl = customerViewDialogCtrl;
	}

}
