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
 * * FileName : FinTypePartnerBankDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 24-04-2017 * *
 * Modified Date : 24-04-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-04-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/FinTypePartnerBank/finTypePartnerBankDialog.zul
 * file. <br>
 */
public class FinTypePartnerBankDialogCtrl extends GFCBaseCtrl<FinTypePartnerBank> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(FinTypePartnerBankDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinTypePartnerBankDialog;
	protected Textbox finType;
	protected Combobox purpose;
	protected Combobox paymentMode;
	protected ExtendedCombobox partnerBankID;
	private FinTypePartnerBank finTypePartnerBank; // overhanded per param
	protected Row row_Van;
	protected Label label_VanApplicable;
	protected Checkbox vanApplicable;

	private transient FinTypePartnerBankListCtrl finTypePartnerBankListCtrl; // overhanded per param

	private String userRole = "";
	private Label label_finTypeDesc;
	private String finDivision = null;

	List<ValueLabel> purposeList = PennantStaticListUtil.getPurposeList();
	List<ValueLabel> paymentModesList = PennantStaticListUtil.getPaymentTypesWithIST();
	private List<FinTypePartnerBank> finTypePartnerBankList;
	protected boolean consumerDurable = false;

	/**
	 * default constructor.<br>
	 */
	public FinTypePartnerBankDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinTypePartnerBankDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.finTypePartnerBank.getID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinTypePartnerBankDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinTypePartnerBankDialog);

		if (arguments.containsKey("fintypepartnerbank")) {
			this.finTypePartnerBank = (FinTypePartnerBank) arguments.get("fintypepartnerbank");
			FinTypePartnerBank befImage = new FinTypePartnerBank();
			BeanUtils.copyProperties(this.finTypePartnerBank, befImage);
			this.finTypePartnerBank.setBefImage(befImage);
			setFinTypePartnerBank(this.finTypePartnerBank);
		} else {
			setFinTypePartnerBank(null);
		}

		if (arguments.containsKey("fintypepartnerbankListCtrl")) {
			setFinTypePartnerBankListCtrl((FinTypePartnerBankListCtrl) arguments.get("fintypepartnerbankListCtrl"));
		} else {
			setFinTypePartnerBankListCtrl(null);
		}

		if (arguments.containsKey("role")) {
			userRole = arguments.get("role").toString();
			getUserWorkspace().allocateRoleAuthorities(arguments.get("role").toString(), "FinTypePartnerBankDialog");
		}

		if (arguments.containsKey("consumerDurable")) {
			this.consumerDurable = (Boolean) arguments.get("consumerDurable");
		}

		this.finTypePartnerBank.setWorkflowId(0);
		doLoadWorkFlow(this.finTypePartnerBank.isWorkflow(), this.finTypePartnerBank.getWorkflowId(),
				this.finTypePartnerBank.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), super.pageRightName);
		}

		if (arguments.containsKey("finDivision")) {
			this.finDivision = (String) arguments.get("finDivision");
		}

		doCheckRights();
		doSetFieldProperties();
		doShowDialog(this.finTypePartnerBank);

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.partnerBankID.setModuleName("PartnerBankModes");
		this.partnerBankID.setValueColumn("PartnerBankCode");
		this.partnerBankID.setDescColumn("PartnerBankName");
		this.partnerBankID.setValidateColumns(new String[] { "PartnerBankCode" });
		this.partnerBankID.setMandatoryStyle(true);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities("FinTypePartnerBankDialog", userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doSave();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);

		doEdit();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);

		MessageUtil.showHelpWindow(event, super.window);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		doDelete();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);

		doCancel();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);

		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);

		doShowNotes(this.finTypePartnerBank);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.finTypePartnerBank.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$partnerBankID(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Object dataObject = partnerBankID.getObject();

		if (dataObject instanceof String) {
			this.partnerBankID.setValue(dataObject.toString());
			this.partnerBankID.setDescription("");
		} else {
			PartnerBankModes partnerBankModes = (PartnerBankModes) dataObject;
			if (partnerBankModes != null) {
				this.partnerBankID.setAttribute("PartnerBankId", partnerBankModes.getPartnerBankId());
				this.partnerBankID.setValue(partnerBankModes.getPartnerBankCode(),
						partnerBankModes.getPartnerBankName());
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param finTypePartnerBank
	 * 
	 */
	public void doWriteBeanToComponents(FinTypePartnerBank aFinTypePartnerBank) {
		logger.debug(Literal.ENTERING);

		this.finType.setValue(aFinTypePartnerBank.getFinType());
		this.label_finTypeDesc.setValue(aFinTypePartnerBank.getFinTypeDesc());

		fillComboBox(this.purpose, aFinTypePartnerBank.getPurpose(), purposeList, "");
		if (!consumerDurable) {
			fillComboBox(this.paymentMode, aFinTypePartnerBank.getPaymentMode(), paymentModesList, ",RTRNGDS,");
		} else {
			fillComboBox(this.paymentMode, aFinTypePartnerBank.getPaymentMode(), paymentModesList, "");
		}

		setPartnerBankProperties();

		if (!aFinTypePartnerBank.isNewRecord()) {

			this.partnerBankID.setValue(StringUtils.trimToEmpty(aFinTypePartnerBank.getPartnerBankCode()),
					StringUtils.trimToEmpty(aFinTypePartnerBank.getPartnerBankName()));
			this.partnerBankID.setAttribute("PartnerBankId", aFinTypePartnerBank.getPartnerBankID());

		}
		this.vanApplicable.setChecked(aFinTypePartnerBank.isVanApplicable());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinTypePartnerBank
	 */
	public void doWriteComponentsToBean(FinTypePartnerBank aFinTypePartnerBank) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		doSetLOVValidation();

		// Finance Type
		try {
			aFinTypePartnerBank.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Purpose
		try {
			String purposeValue = getComboboxValue(this.purpose);
			if (PennantConstants.List_Select.equals(purposeValue)) {
				throw new WrongValueException(this.purpose, Labels.getLabel("Label_RuleDialog_select_list"));
			}
			aFinTypePartnerBank.setPurpose(purposeValue);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Payment Mode
		try {
			String paymentModeValue = getComboboxValue(this.paymentMode);
			if (PennantConstants.List_Select.equals(paymentModeValue)) {
				throw new WrongValueException(this.paymentMode, Labels.getLabel("Label_RuleDialog_select_list"));
			}
			aFinTypePartnerBank.setPaymentMode(getComboboxValue(this.paymentMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Partner Bank ID
		try {

			aFinTypePartnerBank.setPartnerBankName((this.partnerBankID.getDescription()));
			aFinTypePartnerBank.setPartnerBankCode(this.partnerBankID.getValue());
			this.partnerBankID.getValidatedValue();
			Object object = this.partnerBankID.getAttribute("PartnerBankId");

			if (object != null) {
				aFinTypePartnerBank.setPartnerBankID(Long.parseLong(object.toString()));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinTypePartnerBank.setVanApplicable(this.vanApplicable.isChecked());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param finTypePartnerBank The entity that need to be render.
	 */
	public void doShowDialog(FinTypePartnerBank finTypePartnerBank) {
		logger.debug(Literal.LEAVING);

		if (finTypePartnerBank.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finType.focus();
		} else {
			this.finType.focus();
			doEdit();
			btnCancel.setVisible(false);
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinTypePartnerBankDialog_btnDelete"));
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(finTypePartnerBank);

		if (!finTypePartnerBank.isNewRecord()) {
			this.btnSave.setVisible(false);
		}
		this.window_FinTypePartnerBankDialog.doModal();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);
		boolean isMandValidate = true;

		this.partnerBankID.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinTypePartnerBankDialog_PartnerBankID.value"), null,
						isMandValidate ? this.partnerBankID.isMandatory() : false, true));

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.finType.setConstraint("");
		this.purpose.setConstraint("");
		this.paymentMode.setConstraint("");
		this.partnerBankID.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	protected boolean doCustomDelete(final FinTypePartnerBank aFinTypePartnerBank, String tranType) {
		tranType = PennantConstants.TRAN_DEL;
		AuditHeader auditHeader = newFinTypePartnerBankEntryProcess(aFinTypePartnerBank, tranType);
		auditHeader = ErrorControl.showErrorDetails(this.window_FinTypePartnerBankDialog, auditHeader);
		int retValue = auditHeader.getProcessStatus();
		if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
			getFinTypePartnerBankListCtrl().doFillFinTypePartnerBanks(this.finTypePartnerBankList);
			return true;
		}
		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final FinTypePartnerBank aFinTypePartnerBank = new FinTypePartnerBank();
		BeanUtils.copyProperties(this.finTypePartnerBank, aFinTypePartnerBank);

		doDelete(aFinTypePartnerBank.getPartnerBankCode(), aFinTypePartnerBank);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.finType);

		if (this.finTypePartnerBank.isNewRecord()) {
			readOnlyComponent(isReadOnly("FinTypePartnerBankDialog_Purpose"), this.purpose);
			readOnlyComponent(isReadOnly("FinTypePartnerBankDialog_PaymentMode"), this.paymentMode);
			readOnlyComponent(isReadOnly("FinTypePartnerBankDialog_PartnerBankID"), this.partnerBankID);
		} else {
			readOnlyComponent(true, this.purpose);
			readOnlyComponent(true, this.paymentMode);
			readOnlyComponent(true, this.partnerBankID);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.finTypePartnerBank.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	public boolean isReadOnly(String componentName) {
		return getUserWorkspace().isReadOnly(componentName);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.finType);
		readOnlyComponent(true, this.purpose);
		readOnlyComponent(true, this.paymentMode);
		readOnlyComponent(true, this.partnerBankID);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the Rule Return Type
	 * 
	 * @param event
	 */
	public void onChange$purpose(Event event) {
		logger.debug("Entering" + event.toString());

		String purposeValue = this.purpose.getSelectedItem().getValue();

		if (StringUtils.equals(purposeValue, AccountConstants.PARTNERSBANK_DISB)) {
			this.paymentModesList = PennantStaticListUtil.getPaymentTypesWithIST();
		} else {
			this.paymentModesList = PennantStaticListUtil.getAllPaymentTypes();
		}

		if (!consumerDurable) {
			fillComboBox(this.paymentMode, "", paymentModesList, ",RTRNGDS,");
		} else {
			fillComboBox(this.paymentMode, "", paymentModesList, "");
		}
		setPartnerBankProperties();

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the Rule Return Type
	 * 
	 * @param event
	 */
	public void onChange$paymentMode(Event event) {
		logger.debug("Entering" + event.toString());

		setPartnerBankProperties();

		logger.debug("Leaving" + event.toString());
	}

	private void setPartnerBankProperties() {
		logger.debug("Entering");

		String purposeValue = getComboboxValue(this.purpose);
		String paymentModeValue = getComboboxValue(this.paymentMode);

		Filter[] filters = null;

		if (StringUtils.isNotEmpty(finDivision)) {
			filters = new Filter[3];
			filters[0] = new Filter("Purpose", purposeValue, Filter.OP_EQUAL);
			filters[1] = new Filter("PaymentMode", paymentModeValue, Filter.OP_EQUAL);
			filters[2] = new Filter("DIVISIONCODE", finDivision, Filter.OP_EQUAL);
		} else {
			filters = new Filter[2];
			filters[0] = new Filter("Purpose", purposeValue, Filter.OP_EQUAL);
			filters[1] = new Filter("PaymentMode", paymentModeValue, Filter.OP_EQUAL);
		}

		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");
		this.partnerBankID.setFilters(filters);

		if (StringUtils.equals(AccountConstants.PARTNERSBANK_RECEIPTS, this.purpose.getSelectedItem().getValue())) {
			this.row_Van.setVisible(SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED));
		} else {
			this.vanApplicable.setChecked(false);
			this.row_Van.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");

		this.finType.setValue("");
		this.purpose.setValue("");
		this.paymentMode.setValue("");
		this.partnerBankID.setValue("");
		this.partnerBankID.setDescription("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");

		final FinTypePartnerBank aFinTypePartnerBank = new FinTypePartnerBank();
		BeanUtils.copyProperties(this.finTypePartnerBank, aFinTypePartnerBank);
		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		doWriteComponentsToBean(aFinTypePartnerBank);

		isNew = aFinTypePartnerBank.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinTypePartnerBank.getRecordType())) {
				aFinTypePartnerBank.setVersion(aFinTypePartnerBank.getVersion() + 1);
				if (isNew) {
					aFinTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinTypePartnerBank.setNewRecord(true);
				}
			}
		} else {
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
				aFinTypePartnerBank.setVersion(1);
				aFinTypePartnerBank.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}

			if (StringUtils.isBlank(aFinTypePartnerBank.getRecordType())) {
				aFinTypePartnerBank.setVersion(aFinTypePartnerBank.getVersion() + 1);
				aFinTypePartnerBank.setRecordType(PennantConstants.RCD_UPD);
			}

			if (aFinTypePartnerBank.getRecordType().equals(PennantConstants.RCD_ADD) && isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aFinTypePartnerBank.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			AuditHeader auditHeader = newFinTypePartnerBankEntryProcess(aFinTypePartnerBank, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_FinTypePartnerBankDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				getFinTypePartnerBankListCtrl().doFillFinTypePartnerBanks(this.finTypePartnerBankList);
				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
		}

		logger.debug("Leaving");
	}

	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinTypePartnerBankDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
	}

	private AuditHeader newFinTypePartnerBankEntryProcess(FinTypePartnerBank aFinTypePartnerBank, String tranType) {
		logger.debug("Entering");

		boolean recordAdded = false;
		AuditHeader auditHeader = getAuditHeader(aFinTypePartnerBank, tranType);
		finTypePartnerBankList = new ArrayList<FinTypePartnerBank>();
		String[] valueParm = new String[3];
		String[] errParm = new String[3];
		valueParm[0] = String.valueOf(aFinTypePartnerBank.getPartnerBankCode());
		valueParm[1] = aFinTypePartnerBank.getPaymentMode();
		valueParm[2] = aFinTypePartnerBank.getPurpose();
		errParm[0] = PennantJavaUtil.getLabel("label_FinTypePartnerBankDialog_PartnerBankID.value") + ":"
				+ valueParm[0];
		errParm[1] = PennantJavaUtil.getLabel("label_FinTypePartnerBankDialog_PaymentMode.value") + ":" + valueParm[1];
		errParm[2] = PennantJavaUtil.getLabel("label_FinTypePartnerBankDialog_Purpose.value") + ":" + valueParm[2];

		List<FinTypePartnerBank> existingList = getFinTypePartnerBankListCtrl().getFinTypePartnerBankList();
		if (CollectionUtils.isNotEmpty(existingList)) {
			for (int i = 0; i < existingList.size(); i++) {
				FinTypePartnerBank oldFinTypePartnerBank = existingList.get(i);

				if (SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)
						&& StringUtils.equals(AccountConstants.PARTNERSBANK_RECEIPTS,
								oldFinTypePartnerBank.getPurpose())
						&& StringUtils.equals(AccountConstants.PARTNERSBANK_RECEIPTS,
								aFinTypePartnerBank.getPurpose())) {
					// Both Current and Existing list rating same
					if ((oldFinTypePartnerBank.isVanApplicable() && aFinTypePartnerBank.isVanApplicable())) {
						if (aFinTypePartnerBank.isNewRecord()) {
							valueParm[0] = String.valueOf(aFinTypePartnerBank.isVanApplicable());
							errParm[0] = PennantJavaUtil.getLabel("label_FinTypePartnerBankDialog_VanApplicable.value")
									+ ":" + valueParm[0];
							auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
									new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
									getUserWorkspace().getUserLanguage()));
							return auditHeader;
						}
					}
				}

				if (StringUtils.equals(oldFinTypePartnerBank.getPaymentMode(), aFinTypePartnerBank.getPaymentMode())
						&& oldFinTypePartnerBank.getPartnerBankID() == aFinTypePartnerBank.getPartnerBankID()
						&& StringUtils.equals(oldFinTypePartnerBank.getPurpose(), aFinTypePartnerBank.getPurpose())) {
					// Both Current and Existing list rating same

					if (aFinTypePartnerBank.isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41008", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(aFinTypePartnerBank.getRecordType())) {
							aFinTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							finTypePartnerBankList.add(aFinTypePartnerBank);
						} else if (PennantConstants.RCD_ADD.equals(aFinTypePartnerBank.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(aFinTypePartnerBank.getRecordType())) {
							aFinTypePartnerBank.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							finTypePartnerBankList.add(aFinTypePartnerBank);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(aFinTypePartnerBank.getRecordType())) {
							recordAdded = true;
							List<FinTypePartnerBank> savedList = getFinTypePartnerBankListCtrl()
									.getFinTypePartnerBankList();
							for (int j = 0; j < savedList.size(); j++) {
								FinTypePartnerBank partBankType = savedList.get(j);
								if (partBankType.getFinType().equals(aFinTypePartnerBank.getFinType())) {
									finTypePartnerBankList.add(partBankType);
								}
							}
						} else if (PennantConstants.RECORD_TYPE_DEL.equals(aFinTypePartnerBank.getRecordType())) {
							aFinTypePartnerBank.setNewRecord(true);
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							finTypePartnerBankList.add(oldFinTypePartnerBank);
						}
					}
				} else {
					finTypePartnerBankList.add(oldFinTypePartnerBank);
				}
			}
		}
		if (!recordAdded) {
			finTypePartnerBankList.add(aFinTypePartnerBank);
		}

		logger.debug("Leaving");

		return auditHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(FinTypePartnerBank aFinTypePartnerBank, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinTypePartnerBank.getBefImage(), aFinTypePartnerBank);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aFinTypePartnerBank.getUserDetails(),
				getOverideMap());
	}

	public FinTypePartnerBankListCtrl getFinTypePartnerBankListCtrl() {
		return finTypePartnerBankListCtrl;
	}

	public void setFinTypePartnerBankListCtrl(FinTypePartnerBankListCtrl finTypePartnerBankListCtrl) {
		this.finTypePartnerBankListCtrl = finTypePartnerBankListCtrl;
	}

	public FinTypePartnerBank getFinTypePartnerBank() {
		return finTypePartnerBank;
	}

	public void setFinTypePartnerBank(FinTypePartnerBank finTypePartnerBank) {
		this.finTypePartnerBank = finTypePartnerBank;
	}
}
