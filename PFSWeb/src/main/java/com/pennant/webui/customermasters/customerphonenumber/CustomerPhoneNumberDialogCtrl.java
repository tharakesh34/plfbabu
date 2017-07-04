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
 * FileName    		:  CustomerPhoneNumberDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.customermasters.customerphonenumber;

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
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.service.customermasters.CustomerPhoneNumberService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/CustomerMasters/CustomerPhoneNumber
 * /customerPhoneNumberDialog.zul file.
 */
public class CustomerPhoneNumberDialogCtrl extends GFCBaseCtrl<CustomerPhoneNumber> {
	private static final long serialVersionUID = -3093280086658721485L;
	private static final Logger logger = Logger.getLogger(CustomerPhoneNumberDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CustomerPhoneNumberDialog; // autowired

	protected Longbox phoneCustID; // autowired
	protected ExtendedCombobox phoneTypeCode; // autowired
	//protected Textbox phoneCountryCode; // autowired
	//protected Textbox phoneAreaCode; // autowired
	protected Textbox phoneNumber; // autowired
	protected Textbox custCIF; // autowired
	protected Label custShrtName; // autowired
	protected Combobox custPhonePriority; // autoWired
	protected Textbox mobileNumber; // autowired

	// not auto wired vars
	private CustomerPhoneNumber customerPhoneNumber; // overhanded per param
	private transient CustomerPhoneNumberListCtrl customerPhoneNumberListCtrl; // overhanded
																				// per
	private transient boolean validationOn;
	
	protected Button btnSearchPRCustid; // autowire

	

	// ServiceDAOs / Domain Classes
	private transient CustomerPhoneNumberService customerPhoneNumberService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newCustomer = false;
	private List<CustomerPhoneNumber> customerPhoneNumbers;
	private CustomerDialogCtrl customerDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	protected Row row_phoneNumber;
	private final List<ValueLabel> CustomerPriorityList = PennantStaticListUtil
			.getCustomerEmailPriority();
	private String regex;

	/**
	 * default constructor.<br>
	 */
	public CustomerPhoneNumberDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CustomerPhoneNumberDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CustomerPhoneNumber
	 * object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CustomerPhoneNumberDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CustomerPhoneNumberDialog);

		try {

			if (arguments.containsKey("customerPhoneNumber")) {
				this.customerPhoneNumber = (CustomerPhoneNumber) arguments.get("customerPhoneNumber");
				CustomerPhoneNumber befImage = new CustomerPhoneNumber();
				BeanUtils.copyProperties(this.customerPhoneNumber, befImage);
				this.customerPhoneNumber.setBefImage(befImage);
				setCustomerPhoneNumber(this.customerPhoneNumber);
			} else {
				setCustomerPhoneNumber(null);
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getCustomerPhoneNumber().isNewRecord()) {
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
				this.customerPhoneNumber.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "CustomerPhoneNumberDialog");
				}

			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}
			doLoadWorkFlow(this.customerPhoneNumber.isWorkflow(), this.customerPhoneNumber.getWorkflowId(),
					this.customerPhoneNumber.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CustomerPhoneNumberDialog");
			}

			// READ OVERHANDED params !
			// we get the customerPhoneNumberListWindow controller. So we have
			// access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete customerPhoneNumber here.
			if (arguments.containsKey("customerPhoneNumberListCtrl")) {
				setCustomerPhoneNumberListCtrl((CustomerPhoneNumberListCtrl) arguments.get("customerPhoneNumberListCtrl"));
			} else {
				setCustomerPhoneNumberListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCustomerPhoneNumber());

			// Calling SelectCtrl For proper selection of Customer
			if (isNewRecord() && !isNewCustomer()) {
				onload();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CustomerPhoneNumberDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.phoneTypeCode.setMaxlength(8);
		this.phoneTypeCode.setMandatoryStyle(true);
		this.phoneTypeCode.setTextBoxWidth(121);
		this.phoneTypeCode.setModuleName("PhoneType");
		this.phoneTypeCode.setValueColumn("PhoneTypeCode");
		this.phoneTypeCode.setDescColumn("PhoneTypeDesc");
		this.phoneTypeCode.setValidateColumns(new String[] { "PhoneTypeCode" });

		this.phoneNumber.setMaxlength(11);
		
		this.mobileNumber.setMaxlength(LengthConstants.LEN_MOBILE);

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
		getUserWorkspace().allocateAuthorities("CustomerPhoneNumberDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnSave"));
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
		MessageUtil.showHelpWindow(event, window_CustomerPhoneNumberDialog);
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
		doWriteBeanToComponents(this.customerPhoneNumber.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 *            CustomerPhoneNumber
	 */
	public void doWriteBeanToComponents(CustomerPhoneNumber aCustomerPhoneNumber) {
		logger.debug("Entering");

		if (aCustomerPhoneNumber.getPhoneCustID() != Long.MIN_VALUE) {
			this.phoneCustID.setValue(aCustomerPhoneNumber.getPhoneCustID());
		}

		this.phoneTypeCode.setValue(aCustomerPhoneNumber.getPhoneTypeCode());
		this.phoneNumber.setValue(aCustomerPhoneNumber.getPhoneNumber());

		this.custCIF.setValue(aCustomerPhoneNumber.getLovDescCustCIF() == null ? ""
				: aCustomerPhoneNumber.getLovDescCustCIF().trim());
		this.custShrtName.setValue(aCustomerPhoneNumber.getLovDescCustShrtName() == null ? ""
				: aCustomerPhoneNumber.getLovDescCustShrtName().trim());
		fillComboBox(this.custPhonePriority, String.valueOf(aCustomerPhoneNumber.getPhoneTypePriority()),
				CustomerPriorityList, "");
		regex = aCustomerPhoneNumber.getPhoneRegex();
		dosetFieldLength(regex);

		if (isNewRecord()) {
			this.phoneTypeCode.setDescription("");
		} else {
			this.phoneTypeCode.setDescription(aCustomerPhoneNumber.getLovDescPhoneTypeCodeName());
		}

		this.recordStatus.setValue(aCustomerPhoneNumber.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 */
	public void doWriteComponentsToBean(CustomerPhoneNumber aCustomerPhoneNumber) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCustomerPhoneNumber.setPhoneCustID(this.phoneCustID.longValue());
			aCustomerPhoneNumber.setLovDescCustCIF(this.custCIF.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomerPhoneNumber.setLovDescPhoneTypeCodeName(this.phoneTypeCode.getDescription());
			aCustomerPhoneNumber.setPhoneTypeCode(this.phoneTypeCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.row_phoneNumber.isVisible()){
			aCustomerPhoneNumber.setPhoneNumber(this.phoneNumber.getValue());
			}else{
			aCustomerPhoneNumber.setPhoneNumber(this.mobileNumber.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aCustomerPhoneNumber.setPhoneRegex(regex);
		try {
			if ("#".equals(getComboboxValue(this.custPhonePriority))) {
				throw new WrongValueException(this.custPhonePriority, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CustomerPhoneNumberDialog_CustPhonePriority.value") }));
			} else {
				aCustomerPhoneNumber.setPhoneTypePriority(Integer.parseInt(this.custPhonePriority.getSelectedItem()
						.getValue().toString()));
			}
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

		aCustomerPhoneNumber.setRecordStatus(this.recordStatus.getValue());
		setCustomerPhoneNumber(aCustomerPhoneNumber);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCustomerPhoneNumber
	 * @throws Exception
	 */
	public void doShowDialog(CustomerPhoneNumber aCustomerPhoneNumber) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custCIF.focus();
		} else {
			this.custPhonePriority.focus();
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
			doWriteBeanToComponents(aCustomerPhoneNumber);

			doCheckEnquiry();
			if (isNewCustomer()) {
				this.window_CustomerPhoneNumberDialog.setHeight("30%");
				this.window_CustomerPhoneNumberDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_CustomerPhoneNumberDialog.doModal();
			} else {
				this.window_CustomerPhoneNumberDialog.setWidth("100%");
				this.window_CustomerPhoneNumberDialog.setHeight("100%");
				setDialog(DialogType.EMBEDDED);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_CustomerPhoneNumberDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType)) {
			this.phoneNumber.setReadonly(true);
			this.custPhonePriority.setDisabled(true);
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
		doClearMessage();

		if (!this.phoneCustID.isReadonly()) {
			this.custCIF.setConstraint(new PTStringValidator(Labels
					.getLabel("label_CustomerPhoneNumberDialog_PhoneCustID.value"), null, true));
		}
		if (this.row_phoneNumber.isVisible() && !this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(new PTMobileNumberValidator(Labels
					.getLabel("label_CustomerPhoneNumberDialog_PhoneNumber.value"),true,regex,this.phoneNumber.getMaxlength()));
		}
		
		if (!this.custPhonePriority.isDisabled()) {
			this.custPhonePriority.setConstraint(new PTStringValidator(Labels
					.getLabel("label_CustomerPhoneNumberDialog_CustPhonePriority.value"), null, true));
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
		this.phoneNumber.setConstraint("");
		this.custPhonePriority.setConstraint("");
		this.mobileNumber.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		this.phoneTypeCode.setConstraint(new PTStringValidator(Labels
				.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value"), null, true, true));
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.phoneTypeCode.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custCIF.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.phoneTypeCode.setErrorMessage("");
		this.custPhonePriority.setErrorMessage("");
		this.mobileNumber.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCustomerPhoneNumberListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a CustomerPhoneNumber object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");

		final CustomerPhoneNumber aCustomerPhoneNumber = new CustomerPhoneNumber();
		BeanUtils.copyProperties(getCustomerPhoneNumber(), aCustomerPhoneNumber);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_CustomerPhoneNumberDialog_PhoneTypeCode.value") + " : "
				+ aCustomerPhoneNumber.getPhoneTypeCode();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCustomerPhoneNumber.getRecordType())) {
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
				aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (!isFinanceProcess && getCustomerDialogCtrl() != null
						&& getCustomerDialogCtrl().getCustomerDetails().getCustomer().isWorkflow()) {
					aCustomerPhoneNumber.setNewRecord(true);
				}
				if (isWorkFlowEnabled()) {
					aCustomerPhoneNumber.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}else if(StringUtils.equals(aCustomerPhoneNumber.getRecordType(), PennantConstants.RCD_UPD)){
				aCustomerPhoneNumber.setNewRecord(true);	
			}

			try {

				if (isNewCustomer()) {
					tranType = PennantConstants.TRAN_DEL;
					AuditHeader auditHeader = newCustomerPhoneProcess(aCustomerPhoneNumber, tranType);
					auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
					int retValue = auditHeader.getProcessStatus();
					if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
						getCustomerDialogCtrl().doFillCustomerPhoneNumberDetails(this.customerPhoneNumbers);
						// true;
						// send the data back to customer
						closeDialog();
					}

				} else if (doProcess(aCustomerPhoneNumber, tranType)) {
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
			this.phoneTypeCode.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneTypeCode"));
		} else {
			this.btnCancel.setVisible(true);
			this.btnSearchPRCustid.setVisible(false);
			this.phoneTypeCode.setReadonly(true);
		}
		this.custPhonePriority.setDisabled(isReadOnly("CustomerPhoneNumberDialog_phonePriority"));
		this.phoneCustID.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneCustID"));
		this.custCIF.setReadonly(true);
		this.phoneNumber.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneNumber"));
		this.mobileNumber.setReadonly(isReadOnly("CustomerPhoneNumberDialog_mobileNumber"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.customerPhoneNumber.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newCustomer) {
				if ("ENQ".equals(this.moduleType)) {
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
		this.phoneCustID.setReadonly(true);
		this.custCIF.setReadonly(true);
		this.phoneTypeCode.setReadonly(true);
		this.phoneNumber.setReadonly(true);
		this.custPhonePriority.setDisabled(true);

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
		this.phoneTypeCode.setValue("");
		this.phoneTypeCode.setDescription("");
		this.phoneNumber.setValue("");
		this.custPhonePriority.setText("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final CustomerPhoneNumber aCustomerPhoneNumber = new CustomerPhoneNumber();
		BeanUtils.copyProperties(getCustomerPhoneNumber(), aCustomerPhoneNumber);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CustomerPhoneNumber object with the components data
		doWriteComponentsToBean(aCustomerPhoneNumber);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCustomerPhoneNumber.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustomerPhoneNumber.getRecordType())) {
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
				if (isNew) {
					aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustomerPhoneNumber.setNewRecord(true);
				}
			}
		} else {

			if (isNewCustomer()) {
				if (isNewRecord()) {
					aCustomerPhoneNumber.setVersion(1);
					aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}

				if (StringUtils.isBlank(aCustomerPhoneNumber.getRecordType())) {
					aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
					aCustomerPhoneNumber.setRecordType(PennantConstants.RCD_UPD);
				}

				if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				aCustomerPhoneNumber.setVersion(aCustomerPhoneNumber.getVersion() + 1);
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
				AuditHeader auditHeader = newCustomerPhoneProcess(aCustomerPhoneNumber, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getCustomerDialogCtrl().doFillCustomerPhoneNumberDetails(this.customerPhoneNumbers);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(aCustomerPhoneNumber, tranType)) {
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
	 * Method for Creating list of Details
	 */
	private AuditHeader newCustomerPhoneProcess(CustomerPhoneNumber aCustomerPhoneNumber, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(aCustomerPhoneNumber, tranType);
		customerPhoneNumbers = new ArrayList<CustomerPhoneNumber>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		valueParm[0] = String.valueOf(aCustomerPhoneNumber.getLovDescCustCIF());
		valueParm[1] = aCustomerPhoneNumber.getPhoneTypeCode();

		errParm[0] = PennantJavaUtil.getLabel("label_CustID") + ":" + valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_PhoneTypeCode") + ":" + valueParm[1];

		if (getCustomerDialogCtrl().getCustomerPhoneNumberDetailList() != null
				&& getCustomerDialogCtrl().getCustomerPhoneNumberDetailList().size() > 0) {
			for (int i = 0; i < getCustomerDialogCtrl().getCustomerPhoneNumberDetailList().size(); i++) {
				CustomerPhoneNumber customerPhoneNumber = getCustomerDialogCtrl().getCustomerPhoneNumberDetailList()
						.get(i);

				if (!PennantConstants.TRAN_DEL.equals(tranType)) {
					if (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, customerPhoneNumber.getRecordType()) &&
							!StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, customerPhoneNumber.getRecordType()) &&
							aCustomerPhoneNumber.getPhoneTypePriority() == Integer.parseInt(PennantConstants.EMAILPRIORITY_VeryHigh) && 
							customerPhoneNumber.getPhoneTypePriority() == aCustomerPhoneNumber.getPhoneTypePriority()) {
						
						valueParm[1]=this.custPhonePriority.getSelectedItem().getLabel();
						errParm[1] = PennantJavaUtil.getLabel("label_PhoneTypePriority") + ":"+valueParm[1];
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "90287", errParm, valueParm), getUserWorkspace().getUserLanguage()));
						
						return auditHeader;
					}
				}

				if (customerPhoneNumber.getPhoneTypeCode().equals(aCustomerPhoneNumber.getPhoneTypeCode())) { 

					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetails(
								PennantConstants.KEY_FIELD, "41001", errParm, valueParm), getUserWorkspace()
								.getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RCD_UPD)) {
							aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							customerPhoneNumbers.add(aCustomerPhoneNumber);
						} else if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							aCustomerPhoneNumber.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							customerPhoneNumbers.add(aCustomerPhoneNumber);
						} else if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getCustomerDialogCtrl().getCustomerDetails().getCustomerPhoneNumList()
									.size(); j++) {
								CustomerPhoneNumber phoneNumber = getCustomerDialogCtrl().getCustomerDetails()
										.getCustomerPhoneNumList().get(j);
								if (phoneNumber.getPhoneCustID() == aCustomerPhoneNumber.getPhoneCustID()
										&& phoneNumber.getPhoneTypeCode().equals(
												aCustomerPhoneNumber.getPhoneTypeCode())) {
									customerPhoneNumbers.add(phoneNumber);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							customerPhoneNumbers.add(customerPhoneNumber);
						}
					}
				} else {
					customerPhoneNumbers.add(customerPhoneNumber);
				}
			}
		}

		if (!recordAdded) {
			customerPhoneNumbers.add(aCustomerPhoneNumber);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerPhoneNumber
	 *            (CustomerPhoneNumber)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CustomerPhoneNumber aCustomerPhoneNumber, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCustomerPhoneNumber.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aCustomerPhoneNumber.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustomerPhoneNumber.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCustomerPhoneNumber.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustomerPhoneNumber.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustomerPhoneNumber);
				}

				if (isNotesMandatory(taskId, aCustomerPhoneNumber)) {
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

			aCustomerPhoneNumber.setTaskId(taskId);
			aCustomerPhoneNumber.setNextTaskId(nextTaskId);
			aCustomerPhoneNumber.setRoleCode(getRole());
			aCustomerPhoneNumber.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustomerPhoneNumber, tranType);

			String operationRefs = getServiceOperations(taskId, aCustomerPhoneNumber);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustomerPhoneNumber, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustomerPhoneNumber, tranType);
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
		CustomerPhoneNumber aCustomerPhoneNumber = (CustomerPhoneNumber) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getCustomerPhoneNumberService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getCustomerPhoneNumberService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getCustomerPhoneNumberService().doApprove(auditHeader);
						if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getCustomerPhoneNumberService().doReject(auditHeader);
						if (aCustomerPhoneNumber.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CustomerPhoneNumberDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CustomerPhoneNumberDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.customerPhoneNumber), true);
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

	public void onFulfill$phoneTypeCode(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = phoneTypeCode.getObject();
		if (dataObject instanceof String) {
			this.phoneTypeCode.setValue(dataObject.toString());
			this.phoneTypeCode.setDescription("");
		} else {
			PhoneType details = (PhoneType) dataObject;
			if (details != null) {
				this.phoneTypeCode.setValue(details.getPhoneTypeCode());
				this.phoneTypeCode.setDescription(details.getPhoneTypeDesc());
				regex = details.getPhoneTypeRegex();
				this.phoneTypeCode.setAttribute("Regex", regex);
				dosetFieldLength(regex);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	private void dosetFieldLength(String regex) {
		logger.debug("Entering");
		if(regex!=null){			
			String length=regex.substring(6, 8);
			int mobilelength=Integer.parseInt(length);
			this.phoneNumber.setMaxlength(mobilelength);
		}
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

	// To load the customer filter dialog
	private void onload() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.newSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	// To set the customer id from Customer filter
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		final Customer aCustomer = (Customer) nCustomer;
		this.phoneCustID.setValue(aCustomer.getCustID());
		this.custCIF.setValue(aCustomer.getCustCIF());
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
	private AuditHeader getAuditHeader(CustomerPhoneNumber aCustomerPhoneNumber, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustomerPhoneNumber.getBefImage(), aCustomerPhoneNumber);

		return new AuditHeader(getReference(), String.valueOf(aCustomerPhoneNumber.getPhoneCustID()), null, null,
				auditDetail, aCustomerPhoneNumber.getUserDetails(), getOverideMap());
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
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_CustomerPhoneNumberDialog, auditHeader);
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
		doShowNotes(this.customerPhoneNumber);
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getCustomerPhoneNumber().getPhoneCustID() + PennantConstants.KEY_SEPERATOR
				+ getCustomerPhoneNumber().getPhoneTypeCode();
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

	public CustomerPhoneNumber getCustomerPhoneNumber() {
		return this.customerPhoneNumber;
	}

	public void setCustomerPhoneNumber(CustomerPhoneNumber customerPhoneNumber) {
		this.customerPhoneNumber = customerPhoneNumber;
	}

	public void setCustomerPhoneNumberService(CustomerPhoneNumberService customerPhoneNumberService) {
		this.customerPhoneNumberService = customerPhoneNumberService;
	}

	public CustomerPhoneNumberService getCustomerPhoneNumberService() {
		return this.customerPhoneNumberService;
	}

	public void setCustomerPhoneNumberListCtrl(CustomerPhoneNumberListCtrl customerPhoneNumberListCtrl) {
		this.customerPhoneNumberListCtrl = customerPhoneNumberListCtrl;
	}

	public CustomerPhoneNumberListCtrl getCustomerPhoneNumberListCtrl() {
		return this.customerPhoneNumberListCtrl;
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

	public void setCustomerPhoneNumbers(List<CustomerPhoneNumber> customerPhoneNumbers) {
		this.customerPhoneNumbers = customerPhoneNumbers;
	}

	public List<CustomerPhoneNumber> getCustomerPhoneNumbers() {
		return customerPhoneNumbers;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

}
