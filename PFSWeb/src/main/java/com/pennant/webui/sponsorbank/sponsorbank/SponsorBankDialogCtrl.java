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
 * FileName    		:  SponsorBankDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-03-2017    														*
 *                                                                  						*
 * Modified Date    :  09-03-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-03-2017       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.sponsorbank.sponsorbank;

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
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.sponsorbank.SponsorBank;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.sponsorbank.SponsorBankService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennant.webui.util.MultiLineMessageBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/sponsorbank/SponsorBank/sponsorBankDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class SponsorBankDialogCtrl extends GFCBaseCtrl<SponsorBank> {

	private static final long				serialVersionUID	= 1L;
	private final static Logger				logger				= Logger.getLogger(SponsorBankDialogCtrl.class);

	protected Window						window_SponsorBankDialog;

	protected Textbox				sponsorBankCode;
	protected Textbox						sponsorBankName;
	protected ExtendedCombobox			bankCode;
	protected ExtendedCombobox				bankBranchCode;
	protected Textbox						branchMICRCode;
	protected Textbox						branchIFSCCode;
	protected Textbox						branchCity;
	protected Textbox						utilityCode;
	protected Textbox						accountNo;
	protected Combobox						accountType;
	protected Checkbox						active;

	private SponsorBank						sponsorBank;															
	private transient SponsorBankListCtrl	sponsorBankListCtrl;													

	private transient SponsorBankService	sponsorBankService;
	private transient BankDetailService		bankDetailService;
	protected int							accNoLength;

	/**
	 * default constructor.<br>
	 */
	public SponsorBankDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SponsorBankDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_SponsorBankDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());

		// Set the page level components.
		setPageComponents(window_SponsorBankDialog);

		try {
			// Get the required arguments.
			this.sponsorBank = (SponsorBank) arguments.get("sponsorBank");
			this.sponsorBankListCtrl = (SponsorBankListCtrl) arguments.get("sponsorBankListCtrl");

			if (this.sponsorBank == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			SponsorBank sponsorBank = new SponsorBank();
			BeanUtils.copyProperties(this.sponsorBank, sponsorBank);
			this.sponsorBank.setBefImage(sponsorBank);

			// Render the page and display the data.
			doLoadWorkFlow(this.sponsorBank.isWorkflow(), this.sponsorBank.getWorkflowId(),
					this.sponsorBank.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.sponsorBank);
		} catch (Exception e) {
			logger.error("Exception:", e);
			closeDialog();
			MessageUtil.showError(e.toString());
		}

		logger.debug("Leaving");

	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.sponsorBankCode.setMaxlength(8);
		this.sponsorBankName.setMaxlength(50);
		
		this.bankCode.setModuleName("BankDetail");
		this.bankCode.setMandatoryStyle(true);
		this.bankCode.setValueColumn("BankCode");
		this.bankCode.setDescColumn("BankName");
		this.bankCode.setDisplayStyle(2);
		this.bankCode.setValidateColumns(new String[] { "bankCode" });

		this.bankBranchCode.setModuleName("BankBranch");
		this.bankBranchCode.setMandatoryStyle(true);
		this.bankBranchCode.setDisplayStyle(2);
		this.bankBranchCode.setValueColumn("BranchCode");
		this.bankBranchCode.setDescColumn("BranchDesc");
		this.bankBranchCode.setValidateColumns(new String[] { "BranchCode" });

		this.branchMICRCode.setMaxlength(20);
		this.branchMICRCode.setReadonly(true);
		this.branchIFSCCode.setMaxlength(20);
		this.branchIFSCCode.setReadonly(true);
		this.branchCity.setMaxlength(50);
		this.branchCity.setReadonly(true);
		this.utilityCode.setMaxlength(8);
		this.accountNo.setMaxlength(50);
		
		if(StringUtils.isNotBlank(this.sponsorBank.getBankCode())){
			accNoLength = getBankDetailService().getAccNoLengthByCode(this.sponsorBank.getBankCode());
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

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_SponsorBankDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_SponsorBankDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_SponsorBankDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_SponsorBankDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");

	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.sponsorBank.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aSponsorBank
	 * 
	 */
	public void doWriteBeanToComponents(SponsorBank aSponsorBank) {
		logger.debug("Entering");
		this.sponsorBankCode.setValue(aSponsorBank.getSponsorBankCode());
		this.sponsorBankName.setValue(aSponsorBank.getSponsorBankName());
		this.bankCode.setValue(aSponsorBank.getBankCode());
		this.bankBranchCode.setValue(aSponsorBank.getBankBranchCode());

		this.branchMICRCode.setValue(aSponsorBank.getBranchMICRCode());
		this.branchIFSCCode.setValue(aSponsorBank.getBranchIFSCCode());
		this.branchCity.setValue(aSponsorBank.getBranchCity());
		this.utilityCode.setValue(aSponsorBank.getUtilityCode());
		this.accountNo.setValue(aSponsorBank.getAccountNo());
		this.active.setChecked(aSponsorBank.isActive());
		fillComboBox(this.accountType, aSponsorBank.getAccountType(), PennantStaticListUtil.getAccountType(), "");

		if (aSponsorBank.isNewRecord()) {
			this.bankCode.setDescription("");
			this.bankBranchCode.setDescription("");
		} else {
			this.bankCode.setDescription(aSponsorBank.getBankCodeName());
			this.bankBranchCode.setDescription(aSponsorBank.getBankBranchCodeName());
		}
		this.recordStatus.setValue(aSponsorBank.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aSponsorBank
	 */
	public void doWriteComponentsToBean(SponsorBank aSponsorBank) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Sponsor Bank Code
		try {
			aSponsorBank.setSponsorBankCode(this.sponsorBankCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Sponsor Bank Name
		try {
			aSponsorBank.setSponsorBankName(this.sponsorBankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Code
		try {
			aSponsorBank.setBankCode(this.bankCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Branch Code
		try {
			aSponsorBank.setBankBranchCode(this.bankBranchCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch MICR Code
		try {
			aSponsorBank.setBranchMICRCode(this.branchMICRCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch IFSC Code
		try {
			aSponsorBank.setBranchIFSCCode(this.branchIFSCCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch City
		try {
			aSponsorBank.setBranchCity(this.branchCity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Utility Code
		try {
			aSponsorBank.setUtilityCode(this.utilityCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Account No
		try {
			aSponsorBank.setAccountNo(this.accountNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Account Type
		try {
			aSponsorBank.setAccountType(this.accountType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Active
		try {
			aSponsorBank.setActive(this.active.isChecked());
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

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aSponsorBank
	 *            The entity that need to be render.
	 */
	public void doShowDialog(SponsorBank aSponsorBank) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (sponsorBank.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.sponsorBankCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.sponsorBankName.focus();
				if (StringUtils.isNotBlank(sponsorBank.getRecordType())) {
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
		}

		// fill the components with the data
		doWriteBeanToComponents(sponsorBank);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Sponsor Bank Code
		if (!this.sponsorBankCode.isReadonly()) {
			this.sponsorBankCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SponsorBankDialog_SponsorBankCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Sponsor Bank Name
		if (!this.sponsorBankName.isReadonly()) {
			this.sponsorBankName.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SponsorBankDialog_SponsorBankName.value"), PennantRegularExpressions.REGEX_NAME,
					true));
		}
		//Bank Code
		if (!this.bankCode.isReadonly()) {
			this.bankCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_SponsorBankDialog_BankCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Bank Branch Code
		if (!this.bankBranchCode.isReadonly()) {
			this.bankBranchCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SponsorBankDialog_BankBranchCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Branch MICR Code
		if (!this.branchMICRCode.isReadonly()) {
			this.branchMICRCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SponsorBankDialog_BranchMICRCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					false));
		}
		//Branch IFSC Code
		if (!this.branchIFSCCode.isReadonly()) {
			this.branchIFSCCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SponsorBankDialog_BranchIFSCCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					false));
		}
		//Branch City
		if (!this.branchCity.isReadonly()) {
			this.branchCity
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SponsorBankDialog_BranchCity.value"),
							null, false));
		}
		//Utility Code
		if (!this.utilityCode.isReadonly()) {
			this.utilityCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_SponsorBankDialog_UtilityCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		//Account No
		if (!this.accountNo.isReadonly()) {
			this.accountNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_SponsorBankDialog_AccountNo.value"), PennantRegularExpressions.REGEX_ACCOUNTNUMBER,
					true,accNoLength,accNoLength));
		}
		//Account Type
		if (!this.accountType.isDisabled()) {
			this.accountType.setConstraint(new StaticListValidator(PennantStaticListUtil.getAccountType(),
					Labels.getLabel("label_SponsorBankDialog_AccountType.value")));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.sponsorBankCode.setConstraint("");
		this.sponsorBankName.setConstraint("");
		this.bankCode.setConstraint("");
		this.bankBranchCode.setConstraint("");
		this.branchMICRCode.setConstraint("");
		this.branchIFSCCode.setConstraint("");
		this.branchCity.setConstraint("");
		this.utilityCode.setConstraint("");
		this.accountNo.setConstraint("");
		this.accountType.setConstraint("");
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
		this.sponsorBankCode.setErrorMessage("");
		this.sponsorBankName.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.bankBranchCode.setErrorMessage("");
		this.branchMICRCode.setErrorMessage("");
		this.branchIFSCCode.setErrorMessage("");
		this.branchCity.setErrorMessage("");
		this.utilityCode.setErrorMessage("");
		this.accountNo.setErrorMessage("");
		this.accountType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a Academic entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");
		final SponsorBank aSponsorBank = new SponsorBank();
		BeanUtils.copyProperties(this.sponsorBank, aSponsorBank);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_SponsorBankDialog_SponsorBankCode.value") + " : "
				+ aSponsorBank.getSponsorBankCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aSponsorBank.getRecordType()).equals("")) {
				aSponsorBank.setVersion(aSponsorBank.getVersion() + 1);
				aSponsorBank.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aSponsorBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aSponsorBank.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aSponsorBank.getNextTaskId(),
							aSponsorBank);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aSponsorBank, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_SponsorBankDialog, e);
			}

		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$bankCode(Event event) {
		logger.debug("Entering");
		Object dataObject = bankCode.getObject();
		if (dataObject instanceof String) {
			this.bankCode.setValue("");
			this.bankCode.setDescription("");
		} else {
			BankDetail details = (BankDetail) dataObject;
			if (details != null) {
				this.bankCode.setValue(String.valueOf(details.getBankCode()));
				this.bankCode.setDescription(details.getBankName());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$bankBranchCode(Event event) {
		logger.debug("Entering");
		Object dataObject = bankBranchCode.getObject();
		if (dataObject instanceof String) {
			this.bankBranchCode.setValue("");
			this.bankBranchCode.setDescription("");
			this.branchCity.setValue("");
			this.branchMICRCode.setValue("");
			this.branchIFSCCode.setValue("");
		} else {
			BankBranch details = (BankBranch) dataObject;
			if (details != null) {
				this.bankBranchCode.setValue(String.valueOf(details.getBranchCode()));
				this.bankBranchCode.setDescription(details.getBranchDesc());
				this.branchCity.setValue(details.getCity());
				this.branchMICRCode.setValue(details.getMICR());
				this.branchIFSCCode.setValue(details.getIFSC());
				if(StringUtils.isNotBlank(details.getBankCode())){
					accNoLength = getBankDetailService().getAccNoLengthByCode(details.getBankCode());
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.sponsorBank.isNewRecord()) {
			this.sponsorBankCode.setReadonly(false);
			this.sponsorBankName.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.sponsorBankCode.setReadonly(true);
			this.sponsorBankName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.bankCode.setReadonly(isReadOnly("SponsorBankDialog_BankCode"));
		this.bankBranchCode.setReadonly(isReadOnly("SponsorBankDialog_BankBranchCode"));
		this.branchMICRCode.setReadonly(true);//"SponsorBankDialog_BranchMICRCode"
		this.branchIFSCCode.setReadonly(true);//"SponsorBankDialog_BranchIFSCCode"
		this.branchCity.setReadonly(true);//"SponsorBankDialog_BranchCity"
		this.utilityCode.setReadonly(isReadOnly("SponsorBankDialog_UtilityCode"));
		this.accountNo.setReadonly(isReadOnly("SponsorBankDialog_AccountNo"));
		this.accountType.setDisabled(isReadOnly("SponsorBankDialog_AccountType"));
		this.active.setDisabled(isReadOnly("SponsorBankDialog_Active"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.sponsorBank.isNewRecord()) {
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

		logger.debug("Entering");
		this.sponsorBankCode.setReadonly(true);
		this.sponsorBankName.setReadonly(true);
		this.bankCode.setReadonly(true);
		this.bankBranchCode.setReadonly(true);
		this.branchMICRCode.setReadonly(true);
		this.branchIFSCCode.setReadonly(true);
		this.branchCity.setReadonly(true);
		this.utilityCode.setReadonly(true);
		this.accountNo.setReadonly(true);
		this.accountType.setDisabled(true);
		this.active.setDisabled(true);

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

		this.sponsorBankCode.setValue("");
		this.sponsorBankName.setValue("");
		this.bankCode.setValue("");
		this.bankCode.setDescription("");
		this.bankBranchCode.setValue("");
		this.bankBranchCode.setDescription("");
		this.branchMICRCode.setValue("");
		this.branchIFSCCode.setValue("");
		this.branchCity.setValue("");
		this.utilityCode.setValue("");
		this.accountNo.setValue("");
		this.accountType.setSelectedIndex(0);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() {
		logger.debug("Entering");

		final SponsorBank aSponsorBank = new SponsorBank();
		BeanUtils.copyProperties(this.sponsorBank, aSponsorBank);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the Academic object with the components data
		doWriteComponentsToBean(aSponsorBank);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aSponsorBank.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aSponsorBank.getRecordType())) {
				aSponsorBank.setVersion(aSponsorBank.getVersion() + 1);
				if (isNew) {
					aSponsorBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aSponsorBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aSponsorBank.setNewRecord(true);
				}
			}
		} else {
			aSponsorBank.setVersion(aSponsorBank.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aSponsorBank, tranType)) {
				//doWriteBeanToComponents(aSponsorBank);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_SponsorBankDialog, e);
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

	private boolean doProcess(SponsorBank aSponsorBank, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aSponsorBank.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aSponsorBank.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aSponsorBank.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aSponsorBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aSponsorBank.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aSponsorBank);
				}

				if (isNotesMandatory(taskId, aSponsorBank)) {

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

			aSponsorBank.setTaskId(taskId);
			aSponsorBank.setNextTaskId(nextTaskId);
			aSponsorBank.setRoleCode(getRole());
			aSponsorBank.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aSponsorBank, tranType);
			String operationRefs = getServiceOperations(taskId, aSponsorBank);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aSponsorBank, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aSponsorBank, tranType);
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
		boolean deleteNotes = false;

		SponsorBank aSponsorBank = (SponsorBank) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = sponsorBankService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = sponsorBankService.saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = sponsorBankService.doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aSponsorBank.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = sponsorBankService.doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aSponsorBank.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_SponsorBankDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_SponsorBankDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.sponsorBank), true);
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
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(SponsorBank aSponsorBank, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aSponsorBank.getBefImage(), aSponsorBank);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aSponsorBank.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.sponsorBank);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		sponsorBankListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.sponsorBank.getSponsorBankCode());
	}

	public void setSponsorBankService(SponsorBankService sponsorBankService) {
		this.sponsorBankService = sponsorBankService;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
