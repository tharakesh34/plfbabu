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
 * FileName    		:  PartnerBankDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.partnerbank.partnerbank;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.service.applicationmaster.BankDetailService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
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
 * This is the controller class for the /WEB-INF/pages/partnerbank/PartnerBank/partnerBankDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class PartnerBankDialogCtrl extends GFCBaseCtrl<PartnerBank> {

	private static final long				serialVersionUID	= 1L;
	private final static Logger				logger				= Logger.getLogger(PartnerBankDialogCtrl.class);

	protected Window						window_PartnerBankDialog;

	protected Textbox						partnerBankCode;
	protected Textbox						partnerBankName;
	protected ExtendedCombobox				bankCode;
	protected ExtendedCombobox				bankBranchCode;
	protected Textbox						branchMICRCode;
	protected Textbox						branchIFSCCode;
	protected Textbox						branchCity;
	protected Textbox						utilityCode;
	protected Textbox						accountNo;
	protected Combobox						usage;
	protected Checkbox						active;
	protected ExtendedCombobox				acType;
	protected Checkbox						reqFileDownload;
	protected Checkbox						disbDownload;
	protected Intbox						inFavourLength;
	protected Combobox						accountCategory;
	
	private PartnerBank						partnerBank;															
	private transient PartnerBankListCtrl	partnerBankListCtrl;													

	private transient PartnerBankService	partnerBankService;
	private transient BankDetailService		bankDetailService;
	protected int							accNoLength;

	/**
	 * default constructor.<br>
	 */
	public PartnerBankDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PartnerBankDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PartnerBankDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());

		// Set the page level components.
		setPageComponents(window_PartnerBankDialog);

		try {
			// Get the required arguments.
			this.partnerBank = (PartnerBank) arguments.get("partnerBank");
			this.partnerBankListCtrl = (PartnerBankListCtrl) arguments.get("partnerBankListCtrl");

			if (this.partnerBank == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			PartnerBank partnerBank = new PartnerBank();
			BeanUtils.copyProperties(this.partnerBank, partnerBank);
			this.partnerBank.setBefImage(partnerBank);

			// Render the page and display the data.
			doLoadWorkFlow(this.partnerBank.isWorkflow(), this.partnerBank.getWorkflowId(),
					this.partnerBank.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.partnerBank);
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
		this.partnerBankCode.setMaxlength(8);
		this.partnerBankName.setMaxlength(50);
		
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
		this.inFavourLength.setMaxlength(2);
		if(StringUtils.isNotBlank(this.partnerBank.getBankCode())){
			accNoLength = getBankDetailService().getAccNoLengthByCode(this.partnerBank.getBankCode());
		}
		
		this.acType.setModuleName("AccountType");
		this.acType.setMandatoryStyle(true);
		this.acType.setValueColumn("AcType");
		this.acType.setDescColumn("AcTypeDesc");
		this.acType.setDisplayStyle(2);
		this.acType.setValidateColumns(new String[] { "AcType" });

		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PartnerBankDialog_btnSave"));
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

		doWriteBeanToComponents(this.partnerBank.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aPartnerBank
	 * 
	 */
	public void doWriteBeanToComponents(PartnerBank aPartnerBank) {
		logger.debug("Entering");
		this.partnerBankCode.setValue(aPartnerBank.getPartnerBankCode());
		this.partnerBankName.setValue(aPartnerBank.getPartnerBankName());
		this.bankCode.setValue(aPartnerBank.getBankCode());
		this.bankBranchCode.setValue(aPartnerBank.getBankBranchCode());

		this.branchMICRCode.setValue(aPartnerBank.getBranchMICRCode());
		this.branchIFSCCode.setValue(aPartnerBank.getBranchIFSCCode());
		this.branchCity.setValue(aPartnerBank.getBranchCity());
		this.utilityCode.setValue(aPartnerBank.getUtilityCode());
		this.accountNo.setValue(aPartnerBank.getAccountNo());
		this.acType.setValue(aPartnerBank.getAcType());
		this.reqFileDownload.setChecked(aPartnerBank.isAlwFileDownload());
		this.disbDownload.setChecked(aPartnerBank.isDisbDownload());
		this.inFavourLength.setValue(aPartnerBank.getInFavourLength());
		this.active.setChecked(aPartnerBank.isActive());
		
		fillComboBox(this.usage, aPartnerBank.getUsage(), PennantStaticListUtil.getAccountType(), "");
		fillComboBox(this.accountCategory, aPartnerBank.getAccountCategory(), PennantStaticListUtil.getBankAccountType(), "");

		if (aPartnerBank.isNewRecord()) {
			this.bankCode.setDescription("");
			this.bankBranchCode.setDescription("");
			this.acType.setDescription("");
		} else {
			this.bankCode.setDescription(aPartnerBank.getBankCodeName());
			this.bankBranchCode.setDescription(aPartnerBank.getBankBranchCodeName());
			this.acType.setDescription(aPartnerBank.getAcTypeName());
		}
		this.recordStatus.setValue(aPartnerBank.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aPartnerBank
	 */
	public void doWriteComponentsToBean(PartnerBank aPartnerBank) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Partner Bank Code
		try {
			aPartnerBank.setPartnerBankCode(this.partnerBankCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Partner Bank Name
		try {
			aPartnerBank.setPartnerBankName(this.partnerBankName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Code
		try {
			aPartnerBank.setBankCode(this.bankCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Bank Branch Code
		try {
			aPartnerBank.setBankBranchCode(this.bankBranchCode.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch MICR Code
		try {
			aPartnerBank.setBranchMICRCode(this.branchMICRCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch IFSC Code
		try {
			aPartnerBank.setBranchIFSCCode(this.branchIFSCCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Branch City
		try {
			aPartnerBank.setBranchCity(this.branchCity.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Utility Code
		try {
			aPartnerBank.setUtilityCode(this.utilityCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Account No
		try {
			aPartnerBank.setAccountNo(this.accountNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//
		try {
			aPartnerBank.setAcType(this.acType.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Account Type
		try {
			aPartnerBank.setUsage(this.usage.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Account Category
		try {
			aPartnerBank.setAccountCategory(this.accountCategory.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Required File Download
		try {
			aPartnerBank.setAlwFileDownload(this.reqFileDownload.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//Required File Download
		try {
			aPartnerBank.setDisbDownload(this.disbDownload.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//In Favor Length
		try {
			aPartnerBank.setInFavourLength(this.inFavourLength.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Active
		try {
			aPartnerBank.setActive(this.active.isChecked());
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
	 * @param aPartnerBank
	 *            The entity that need to be render.
	 */
	public void doShowDialog(PartnerBank aPartnerBank) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (partnerBank.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.partnerBankCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.partnerBankName.focus();
				if (StringUtils.isNotBlank(partnerBank.getRecordType())) {
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
		doWriteBeanToComponents(partnerBank);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Partner Bank Code
		if (!this.partnerBankCode.isReadonly()) {
			this.partnerBankCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_PartnerBankCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		
		//Bank Code
		if (!this.bankCode.isReadonly()) {
			this.bankCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_PartnerBankDialog_BankCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Bank Branch Code
		if (!this.bankBranchCode.isReadonly()) {
			this.bankBranchCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_BankBranchCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					true));
		}
		//Branch MICR Code
		if (!this.branchMICRCode.isReadonly()) {
			this.branchMICRCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_BranchMICRCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					false));
		}
		//Branch IFSC Code
		if (!this.branchIFSCCode.isReadonly()) {
			this.branchIFSCCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_BranchIFSCCode.value"), PennantRegularExpressions.REGEX_ALPHANUM,
					false));
		}
		//Branch City
		if (!this.branchCity.isReadonly()) {
			this.branchCity
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PartnerBankDialog_BranchCity.value"),
							null, false));
		}
		
		//Account No
		if (!this.accountNo.isReadonly()) {
			this.accountNo.setConstraint(new PTStringValidator(Labels
					.getLabel("label_PartnerBankDialog_AccountNo.value"), PennantRegularExpressions.REGEX_ACCOUNTNUMBER,
					true,accNoLength,accNoLength));
		}
		//Account Type
		if (!this.usage.isDisabled()) {
			this.usage.setConstraint(new StaticListValidator(PennantStaticListUtil.getAccountType(),
					Labels.getLabel("label_PartnerBankDialog_AccountType.value")));
		}
		
		//Account Type Code
		if (!this.acType.isReadonly()) {
			this.acType.setConstraint(new PTStringValidator(Labels.getLabel("label_PartnerBankDialog_GLCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.partnerBankCode.setConstraint("");
		this.partnerBankName.setConstraint("");
		this.bankCode.setConstraint("");
		this.bankBranchCode.setConstraint("");
		this.branchMICRCode.setConstraint("");
		this.branchIFSCCode.setConstraint("");
		this.branchCity.setConstraint("");
		this.utilityCode.setConstraint("");
		this.accountNo.setConstraint("");
		this.usage.setConstraint("");
		this.accountCategory.setConstraint("");
		this.acType.setConstraint("");
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
		this.partnerBankCode.setErrorMessage("");
		this.partnerBankName.setErrorMessage("");
		this.bankCode.setErrorMessage("");
		this.bankBranchCode.setErrorMessage("");
		this.branchMICRCode.setErrorMessage("");
		this.branchIFSCCode.setErrorMessage("");
		this.branchCity.setErrorMessage("");
		this.utilityCode.setErrorMessage("");
		this.accountNo.setErrorMessage("");
		this.acType.setErrorMessage("");
		this.usage.setErrorMessage("");
		this.accountCategory.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Deletes a Academic entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");
		final PartnerBank aPartnerBank = new PartnerBank();
		BeanUtils.copyProperties(this.partnerBank, aPartnerBank);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_PartnerBankDialog_PartnerBankCode.value") + " : "
				+ aPartnerBank.getPartnerBankCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES | MultiLineMessageBox.NO,
				Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aPartnerBank.getRecordType()).equals("")) {
				aPartnerBank.setVersion(aPartnerBank.getVersion() + 1);
				aPartnerBank.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aPartnerBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aPartnerBank.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aPartnerBank.getNextTaskId(),
							aPartnerBank);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aPartnerBank, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_PartnerBankDialog, e);
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
	
	public void onFulfill$glCode(Event event) {
		logger.debug("Entering");
		Object dataObject = acType.getObject();
		if (dataObject instanceof String) {
			this.acType.setValue("");
			this.acType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.acType.setValue(String.valueOf(details.getAcType()));
				this.acType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.partnerBank.isNewRecord()) {
			this.partnerBankCode.setReadonly(false);
			this.partnerBankName.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.partnerBankCode.setReadonly(true);
			this.partnerBankName.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.bankCode.setReadonly(isReadOnly("PartnerBankDialog_BankCode"));
		this.bankBranchCode.setReadonly(isReadOnly("PartnerBankDialog_BankBranchCode"));
		this.branchMICRCode.setReadonly(true);//"PartnerBankDialog_BranchMICRCode"
		this.branchIFSCCode.setReadonly(true);//"PartnerBankDialog_BranchIFSCCode"
		this.branchCity.setReadonly(true);//"PartnerBankDialog_BranchCity"
		this.utilityCode.setReadonly(isReadOnly("PartnerBankDialog_UtilityCode"));
		this.accountNo.setReadonly(isReadOnly("PartnerBankDialog_AccountNo"));
		this.usage.setDisabled(isReadOnly("PartnerBankDialog_AccountType"));
		this.acType.setReadonly(isReadOnly("PartnerBankDialog_AccType"));
		this.reqFileDownload.setDisabled(isReadOnly("PartnerBankDialog_AlwFileDownload"));
		this.disbDownload.setDisabled(isReadOnly("PartnerBankDialog_DisbDownload"));
		this.accountCategory.setDisabled(isReadOnly("PartnerBankDialog_AccountCategory"));
		this.inFavourLength.setDisabled(isReadOnly("PartnerBankDialog_InFavourLength"));
		this.active.setDisabled(isReadOnly("PartnerBankDialog_Active"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.partnerBank.isNewRecord()) {
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
		this.partnerBankCode.setReadonly(true);
		this.partnerBankName.setReadonly(true);
		this.bankCode.setReadonly(true);
		this.bankBranchCode.setReadonly(true);
		this.branchMICRCode.setReadonly(true);
		this.branchIFSCCode.setReadonly(true);
		this.branchCity.setReadonly(true);
		this.utilityCode.setReadonly(true);
		this.accountNo.setReadonly(true);
		this.usage.setDisabled(true);
		this.acType.setReadonly(true);
		this.reqFileDownload.setDisabled(true);
		this.disbDownload.setDisabled(true);
		this.accountCategory.setDisabled(true);
		this.inFavourLength.setReadonly(true);
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

		this.partnerBankCode.setValue("");
		this.partnerBankName.setValue("");
		this.bankCode.setValue("");
		this.bankCode.setDescription("");
		this.bankBranchCode.setValue("");
		this.bankBranchCode.setDescription("");
		this.branchMICRCode.setValue("");
		this.branchIFSCCode.setValue("");
		this.branchCity.setValue("");
		this.utilityCode.setValue("");
		this.accountNo.setValue("");
		this.acType.setValue("");
		this.usage.setSelectedIndex(0);
		this.accountCategory.setSelectedIndex(0);

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() {
		logger.debug("Entering");

		final PartnerBank aPartnerBank = new PartnerBank();
		BeanUtils.copyProperties(this.partnerBank, aPartnerBank);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the Academic object with the components data
		doWriteComponentsToBean(aPartnerBank);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aPartnerBank.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aPartnerBank.getRecordType())) {
				aPartnerBank.setVersion(aPartnerBank.getVersion() + 1);
				if (isNew) {
					aPartnerBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aPartnerBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aPartnerBank.setNewRecord(true);
				}
			}
		} else {
			aPartnerBank.setVersion(aPartnerBank.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aPartnerBank, tranType)) {
				//doWriteBeanToComponents(aPartnerBank);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_PartnerBankDialog, e);
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

	private boolean doProcess(PartnerBank aPartnerBank, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aPartnerBank.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aPartnerBank.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aPartnerBank.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aPartnerBank.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aPartnerBank.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aPartnerBank);
				}

				if (isNotesMandatory(taskId, aPartnerBank)) {

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

			aPartnerBank.setTaskId(taskId);
			aPartnerBank.setNextTaskId(nextTaskId);
			aPartnerBank.setRoleCode(getRole());
			aPartnerBank.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aPartnerBank, tranType);
			String operationRefs = getServiceOperations(taskId, aPartnerBank);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aPartnerBank, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aPartnerBank, tranType);
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

		PartnerBank aPartnerBank = (PartnerBank) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = partnerBankService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = partnerBankService.saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = partnerBankService.doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aPartnerBank.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = partnerBankService.doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aPartnerBank.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PartnerBankDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_PartnerBankDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.partnerBank), true);
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

	private AuditHeader getAuditHeader(PartnerBank aPartnerBank, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aPartnerBank.getBefImage(), aPartnerBank);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aPartnerBank.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.partnerBank);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		partnerBankListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.partnerBank.getPartnerBankCode());
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public BankDetailService getBankDetailService() {
		return bankDetailService;
	}

	public void setBankDetailService(BankDetailService bankDetailService) {
		this.bankDetailService = bankDetailService;
	}

}
