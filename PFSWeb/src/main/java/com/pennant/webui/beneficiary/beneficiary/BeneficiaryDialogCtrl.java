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
 * FileName    		:  BeneficiaryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-12-2016    														*
 *                                                                  						*
 * Modified Date    :  01-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-12-2016       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.beneficiary.beneficiary;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.beneficiary.BeneficiaryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTEmailValidator;
import com.pennant.util.Constraint.PTMobileNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/com.pennant.beneficiary/Beneficiary/beneficiaryDialog.zul file.
 */
public class BeneficiaryDialogCtrl extends GFCBaseCtrl<Beneficiary> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BeneficiaryDialogCtrl.class);

	protected Window window_BeneficiaryDialog;
	protected ExtendedCombobox custID;
	protected ExtendedCombobox bankBranchID;
	protected Textbox accNumber;
	protected Textbox accHolderName;
	//protected Textbox phoneCountryCode;
	//protected Textbox phoneAreaCode;
	protected Textbox phoneNumber;
	protected Textbox email;
	protected Textbox bank;
	protected Textbox city;
	protected Textbox branch;
	protected int	accNoLength;
	private Beneficiary beneficiary;

	private transient BeneficiaryListCtrl beneficiaryListCtrl;
	private transient BeneficiaryService beneficiaryService;
	private transient BankDetailService	bankDetailService;

	private Checkbox beneficiaryActive;
	private Checkbox defaultBeneficiary;
	
	/**
	 * default constructor.<br>
	 */
	public BeneficiaryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "BeneficiaryDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.beneficiary.getBeneficiaryId());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_BeneficiaryDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_BeneficiaryDialog);

		try {
			// Get the required arguments.
			this.beneficiary = (Beneficiary) arguments.get("beneficiary");
			this.beneficiaryListCtrl = (BeneficiaryListCtrl) arguments.get("beneficiaryListCtrl");

			if (this.beneficiary == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Beneficiary beneficiary = new Beneficiary();
			BeanUtils.copyProperties(this.beneficiary, beneficiary);
			this.beneficiary.setBefImage(beneficiary);
			
			// Render the page and display the data.
			doLoadWorkFlow(this.beneficiary.isWorkflow(), this.beneficiary.getWorkflowId(),
					this.beneficiary.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.beneficiary);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.custID.setModuleName("Customer");
		this.custID.setMandatoryStyle(true);
		this.custID.setValueColumn("CustCIF");
		this.custID.setDescColumn("CustShrtName");
		this.custID.setDisplayStyle(2);
		this.custID.setValidateColumns(new String[] { "CustCIF" });

		this.bankBranchID.setModuleName("BankBranch");
		this.bankBranchID.setMandatoryStyle(true);
		this.bankBranchID.setValueColumn("IFSC");
		this.bankBranchID.setDescColumn("");
		this.bankBranchID.setDisplayStyle(2);
		this.bankBranchID.setValidateColumns(new String[] { "IFSC" });

		this.accNumber.setMaxlength(50);
		this.accHolderName.setMaxlength(50);
		this.phoneNumber.setMaxlength(10);
		this.phoneNumber.setWidth("180px");
		this.email.setMaxlength(50);
		
		if(StringUtils.isNotBlank(this.beneficiary.getBankCode())){
			accNoLength = getBankDetailService().getAccNoLengthByCode(this.beneficiary.getBankCode());
		}
		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_BeneficiaryDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_BeneficiaryDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_BeneficiaryDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_BeneficiaryDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.beneficiary);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		beneficiaryListCtrl.search();
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.beneficiary.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	public void onFulfill$bankBranchID(Event event) {
		logger.debug("Entering");

		Object dataObject = bankBranchID.getObject();

		if (dataObject instanceof String) {
			this.bankBranchID.setValue(dataObject.toString());
			this.bank.setValue("");
			this.city.setValue("");
			this.branch.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;

			if (details != null) {
				this.bankBranchID.setAttribute("bankBranchID", details.getBankBranchID());
				this.bank.setValue(details.getBankName());
				this.city.setValue(details.getCity());
				this.branch.setValue(details.getBranchDesc());
				this.bankBranchID.setValue(details.getIFSC());
				if(StringUtils.isNotBlank(details.getBankCode())){
					accNoLength = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				}
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$custID(Event event) {
		logger.debug("Entering");

		Object dataObject = custID.getObject();

		if (dataObject instanceof String) {
			this.custID.setValue(dataObject.toString());
		} else {
			Customer details = (Customer) dataObject;
			if (details != null) {
				this.custID.setAttribute("custID", details.getCustID());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param beneficiary
	 * 
	 */
	public void doWriteBeanToComponents(Beneficiary beneficiary) {
		logger.debug("Entering");

		if (beneficiary.getCustID() != Long.MIN_VALUE && beneficiary.getCustID() != 0) {
			this.custID.setAttribute("custID", beneficiary.getCustID());
			this.custID.setValue(beneficiary.getCustCIF(), beneficiary.getCustShrtName());
		}

		if (beneficiary.getBankBranchID() != Long.MIN_VALUE && beneficiary.getBankBranchID() != 0) {
			this.bankBranchID.setAttribute("bankBranchID", beneficiary.getBankBranchID());
			this.bankBranchID.setValue(StringUtils.trimToEmpty(beneficiary.getiFSC()));
		}
		this.city.setValue(StringUtils.trimToEmpty(beneficiary.getCity()));
		this.bank.setValue(StringUtils.trimToEmpty(beneficiary.getBankName()));
		this.branch.setValue(beneficiary.getBranchDesc());
		this.accNumber.setValue(beneficiary.getAccNumber());
		this.accHolderName.setValue(beneficiary.getAccHolderName());
		this.phoneNumber.setValue(beneficiary.getPhoneNumber());
		this.email.setValue(beneficiary.getEmail());
		this.recordStatus.setValue(beneficiary.getRecordStatus());
	
		this.beneficiaryActive.setChecked(beneficiary.isBeneficiaryActive());
		this.defaultBeneficiary.setChecked(beneficiary.isDefaultBeneficiary());
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBeneficiary
	 */
	public void doWriteComponentsToBean(Beneficiary aBeneficiary) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Cust ID
		try {
			this.custID.getValidatedValue();
			String custid = String.valueOf(this.custID.getAttribute("custID"));
			aBeneficiary.setCustID(Long.valueOf((custid)));
			aBeneficiary.setCustCIF(this.custID.getValue()); // Customer CIF
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Bank Branch ID
		try {
			this.bankBranchID.getValidatedValue();
			Object obj = this.bankBranchID.getAttribute("bankBranchID");
			
			aBeneficiary.setiFSC(this.bankBranchID.getValue());
			if (obj != null) {
				aBeneficiary.setBankBranchID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Acc Number
		try {
			aBeneficiary.setAccNumber(this.accNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Acc Holder Name
		try {
			aBeneficiary.setAccHolderName(this.accHolderName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Phone Country Code
		try {
			aBeneficiary.setPhoneNumber(this.phoneNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Email
		try {
			aBeneficiary.setEmail(this.email.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Beneficiary Active
		try {
			aBeneficiary.setBeneficiaryActive(this.beneficiaryActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Beneficiary Default
		try {
			aBeneficiary.setDefaultBeneficiary(this.defaultBeneficiary.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aBeneficiary.setRecordStatus(this.recordStatus.getValue());

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param beneficiary
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Beneficiary beneficiary) {
		logger.debug("Entering");

		if (beneficiary.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.custID.focus();
		} else {
			this.custID.setReadonly(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(beneficiary.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
			readOnlyComponent(true, this.bankBranchID);
			readOnlyComponent(true, this.accNumber);
			readOnlyComponent(true, this.accHolderName);
			readOnlyComponent(true, this.phoneNumber);
			readOnlyComponent(true, this.email);
		}

		doWriteBeanToComponents(beneficiary);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Cust ID
		if (!this.custID.isReadonly()) {
			this.custID.setConstraint(new PTStringValidator(Labels.getLabel("label_BeneficiaryDialog_CustID.value"),
					null, true));
		}
		// Bank Branch ID
		if (!this.bankBranchID.isReadonly()) {
			this.bankBranchID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BeneficiaryDialog_BankBranchID.value"), null, true));
		}
		// Acc Number
		if (!this.accNumber.isReadonly()) {
			this.accNumber.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BeneficiaryDialog_AccNumber.value"), PennantRegularExpressions.REGEX_ACCOUNTNUMBER,
					true));
		}
		// Acc Holder Name
		if (!this.accHolderName.isReadonly()) {
			this.accHolderName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_BeneficiaryDialog_AccHolderName.value"), PennantRegularExpressions.REGEX_ACC_HOLDER_NAME,
					true));
		}

		// Phone Number
		if (!this.phoneNumber.isReadonly()) {
			this.phoneNumber.setConstraint(new PTMobileNumberValidator(Labels
					.getLabel("label_BeneficiaryDialog_PhoneNumber.value"), false));
		}
		// Email
		if (!this.email.isReadonly()) {
			this.email.setConstraint(new PTEmailValidator(Labels.getLabel("label_BeneficiaryDialog_Email.value"), false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.custID.setConstraint("");
		this.bankBranchID.setConstraint("");
		this.accNumber.setConstraint("");
		this.accHolderName.setConstraint("");
		this.phoneNumber.setConstraint("");
		this.email.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.custID.setErrorMessage("");
		this.bankBranchID.setErrorMessage("");
		this.accNumber.setErrorMessage("");
		this.accHolderName.setErrorMessage("");
		this.phoneNumber.setErrorMessage("");
		this.email.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a Beneficiary object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");
		final Beneficiary aBeneficiary = new Beneficiary();
		BeanUtils.copyProperties(this.beneficiary, aBeneficiary);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aBeneficiary.getBeneficiaryId();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aBeneficiary.getRecordType()).equals("")) {
				aBeneficiary.setVersion(aBeneficiary.getVersion() + 1);
				aBeneficiary.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aBeneficiary.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aBeneficiary.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aBeneficiary.getNextTaskId(),
							aBeneficiary);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aBeneficiary, tranType)) {
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
		if (this.beneficiary.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.custID);
		} else {
			readOnlyComponent(true, this.custID);
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("BeneficiaryDialog_BankBranchID"), this.bankBranchID);
		readOnlyComponent(isReadOnly("BeneficiaryDialog_AccNumber"), this.accNumber);
		readOnlyComponent(isReadOnly("BeneficiaryDialog_AccHolderName"), this.accHolderName);
		readOnlyComponent(isReadOnly("BeneficiaryDialog_PhoneNumber"), this.phoneNumber);
		readOnlyComponent(isReadOnly("BeneficiaryDialog_Email"), this.email);
		readOnlyComponent(isReadOnly("BeneficiaryDialog_Active"), this.beneficiaryActive);
		readOnlyComponent(isReadOnly("BeneficiaryDialog_DefaultBeneficiary"), this.defaultBeneficiary);
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.beneficiary.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		readOnlyComponent(true, this.custID);
		readOnlyComponent(true, this.bankBranchID);
		readOnlyComponent(true, this.accNumber);
		readOnlyComponent(true, this.accHolderName);
		readOnlyComponent(true, this.phoneNumber);
		readOnlyComponent(true, this.email);
		readOnlyComponent(true, this.beneficiaryActive);
		readOnlyComponent(true, this.defaultBeneficiary);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
	}

	/**
	 * Clear's the components values.
	 */
	public void doClear() {
		this.custID.setValue("");
		this.custID.setDescription("");
		this.bankBranchID.setValue("");
		this.bankBranchID.setDescription("");
		this.accNumber.setValue("");
		this.accHolderName.setValue("");
		this.phoneNumber.setValue("");
		this.email.setValue("");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final Beneficiary aBeneficiary = new Beneficiary();
		BeanUtils.copyProperties(this.beneficiary, aBeneficiary);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aBeneficiary);

		isNew = aBeneficiary.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aBeneficiary.getRecordType())) {
				aBeneficiary.setVersion(aBeneficiary.getVersion() + 1);
				if (isNew) {
					aBeneficiary.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aBeneficiary.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aBeneficiary.setNewRecord(true);
				}
			}
		} else {
			aBeneficiary.setVersion(aBeneficiary.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aBeneficiary, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(Beneficiary aBeneficiary, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aBeneficiary.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aBeneficiary.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aBeneficiary.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aBeneficiary.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aBeneficiary.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aBeneficiary);
				}

				if (isNotesMandatory(taskId, aBeneficiary)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aBeneficiary.setTaskId(taskId);
			aBeneficiary.setNextTaskId(nextTaskId);
			aBeneficiary.setRoleCode(getRole());
			aBeneficiary.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aBeneficiary, tranType);
			String operationRefs = getServiceOperations(taskId, aBeneficiary);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aBeneficiary, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aBeneficiary, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader
	 *            auditHeader
	 * @param method
	 *            (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Beneficiary aBeneficiary = (Beneficiary) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = beneficiaryService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = beneficiaryService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = beneficiaryService.doApprove(auditHeader);

						if (aBeneficiary.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = beneficiaryService.doReject(auditHeader);
						if (aBeneficiary.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_BeneficiaryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_BeneficiaryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.beneficiary), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(Beneficiary aBeneficiary, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aBeneficiary.getBefImage(), aBeneficiary);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aBeneficiary.getUserDetails(),
				getOverideMap());
	}

	public void setBeneficiaryService(BeneficiaryService beneficiaryService) {
		this.beneficiaryService = beneficiaryService;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}
}
