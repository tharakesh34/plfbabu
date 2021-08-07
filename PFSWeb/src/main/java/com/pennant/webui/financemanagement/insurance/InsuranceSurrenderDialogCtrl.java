package com.pennant.webui.financemanagement.insurance;

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
 * * FileName : InsuranceSurrenderDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : * * Modified
 * Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-09-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.configuration.VasCustomer;
import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.insurance.InsuranceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.AccountingEvent;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/insuranceDetails/insuranceDetailsDialog.zul
 * file. <br>
 */
public class InsuranceSurrenderDialogCtrl extends GFCBaseCtrl<VASRecording> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(InsuranceSurrenderDialogCtrl.class);

	protected Window window_InsuranceSurrenderDialog;

	protected Label finReference;
	protected Label insuranceReference;
	protected Label custCif;
	protected Label loanType;
	protected Label policyNumber;
	protected Label flpDays;

	protected Tab basicDetailTab;
	protected Tab tabPostingDetails;
	protected Tabpanel tabPanelPostingDetails;

	protected Combobox activity;
	protected Checkbox cancellationAfterFLP;
	protected Intbox dayPassedFLPdays;
	protected CurrencyBox premiumAmount;
	protected CurrencyBox cancelAmount;
	protected Textbox serviceReqNumber;
	protected Datebox flpCalculatedOn;
	protected Textbox reason;
	protected Textbox remarks;
	private VASRecording vASRecording;
	private Label label_CancellationAfterFLP;
	private long accountsetId;

	private transient InsuranceRebookingListCtrl insuranceRebookingListCtrl;
	private transient VASRecordingService vASRecordingService;
	private transient InsuranceDetailService insuranceDetailService;
	private transient AccountingSetDAO accountingSetDAO;

	/**
	 * default constructor.<br>
	 */
	public InsuranceSurrenderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "InsuranceSurrenderDialog";
	}

	@Override
	protected String getReference() {
		StringBuffer referenceBuffer = new StringBuffer(String.valueOf(this.vASRecording.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_InsuranceSurrenderDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_InsuranceSurrenderDialog);

		try {
			this.vASRecording = (VASRecording) arguments.get("vASRecording");
			this.insuranceRebookingListCtrl = (InsuranceRebookingListCtrl) arguments.get("listCtrl");
			if (this.vASRecording == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			// Store the before image.
			VASRecording avASRecording = new VASRecording();
			BeanUtils.copyProperties(this.vASRecording, avASRecording);
			this.vASRecording.setBefImage(avASRecording);

			// Render the page and display the data.
			doLoadWorkFlow(this.vASRecording.isWorkflow(), this.vASRecording.getWorkflowId(),
					this.vASRecording.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.vASRecording);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.cancelAmount.setProperties(true, getCcyFormat());
		this.premiumAmount.setProperties(false, getCcyFormat());
		this.premiumAmount.setTextBoxWidth(180);
		this.premiumAmount.setDisabled(true);
		this.cancellationAfterFLP.setVisible(false);
		this.label_CancellationAfterFLP.setVisible(false);
		this.remarks.setMaxlength(500);
		this.flpCalculatedOn.setFormat(PennantConstants.dateFormat);
		this.serviceReqNumber.setMaxlength(50);
		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(false);
		this.btnEdit.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_InsuranceSurrenderDialog_btnSave"));
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
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
	 * @throws Exception
	 */
	public void onClick$btnDelete(Event event) throws Exception {
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
		doShowNotes(this.vASRecording);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		insuranceRebookingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.vASRecording.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param InsuranceSurrender
	 * 
	 */
	public void doWriteBeanToComponents(VASRecording vasRecording) {
		logger.debug(Literal.ENTERING);

		this.finReference.setValue(vasRecording.getPrimaryLinkRef());
		this.insuranceReference.setValue(vasRecording.getVasReference());
		this.flpDays.setValue(vasRecording.getFlpDays() + "");
		this.loanType.setValue(vasRecording.getFinType());
		this.premiumAmount.setValue(PennantApplicationUtil.formateAmount(vasRecording.getFee(), getCcyFormat()));

		VASConfiguration configuration = vasRecording.getVasConfiguration();
		if (configuration.getFreeLockPeriod() > 0) {
			InsuranceDetails insuranceDetails = getInsuranceDetailService()
					.getInsurenceDetailsByRef(vasRecording.getVasReference(), "_View");
			if (insuranceDetails != null) {

				this.policyNumber.setValue(insuranceDetails.getPolicyNumber());
				if (configuration.getFlpCalculatedOn().equals(FinanceConstants.FLPCALCULATED_TYPE_ON_ISSUANCEDATE)) {
					this.flpCalculatedOn.setValue(insuranceDetails.getIssuanceDate());
				} else if (vasRecording.getVasConfiguration().getFlpCalculatedOn()
						.equals(FinanceConstants.FLPCALCULATED_TYPE_ON_VASAPPROVALDATE)) {
					this.flpCalculatedOn.setValue(vasRecording.getValueDate());
				}
				if (this.flpCalculatedOn.getValue() != null) {
					int differentDays = DateUtility.getDaysBetween(DateUtility.getAppDate(),
							this.flpCalculatedOn.getValue());
					differentDays = differentDays - configuration.getFreeLockPeriod();
					if (differentDays < 0) {
						this.dayPassedFLPdays.setValue(0);
					} else {
						this.dayPassedFLPdays.setValue(differentDays);
					}
				}
			}
		} else {
			this.dayPassedFLPdays.setValue(0);
		}

		VasCustomer customer = getvASRecordingService().getVasCustomerDetails(vasRecording.getPrimaryLinkRef(),
				vasRecording.getPostingAgainst());
		if (customer != null) {
			String custCifName = "";
			custCifName = custCifName.concat(customer.getCustCIF());
			if (StringUtils.trimToNull(customer.getCustShrtName()) != null) {
				custCifName = custCifName.concat(" - ").concat(customer.getCustShrtName());
			}
			this.custCif.setValue(custCifName);
		}
		fillComboBox(this.activity, vasRecording.getVasStatus(), PennantStaticListUtil.getActivity(), "");
		setCancelFLP(vasRecording.getVasStatus());
		this.serviceReqNumber.setValue(vasRecording.getServiceReqNumber());
		this.cancellationAfterFLP.setChecked(vasRecording.isCancelAfterFLP());
		this.cancelAmount.setValue(PennantApplicationUtil.formateAmount(vasRecording.getCancelAmt(), getCcyFormat()));

		this.reason.setValue(vasRecording.getReason());
		this.remarks.setValue(vasRecording.getRemarks());
		this.recordStatus.setValue(vasRecording.getRecordStatus());

		// Accounting Details Tab Addition
		if (!enqiryModule && !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			appendAccountingDetailTab(vasRecording, true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param details
	 */
	public void doWriteComponentsToBean(VASRecording details) {
		logger.debug(Literal.LEAVING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			details.setVasStatus(this.activity.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			details.setCancelAmt(
					PennantApplicationUtil.unFormateAmount(this.cancelAmount.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			details.setReason(this.reason.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			details.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			details.setCancelAfterFLP(this.cancellationAfterFLP.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			details.setServiceReqNumber(this.serviceReqNumber.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.cancelAmount.getActualValue().compareTo(this.premiumAmount.getActualValue()) > 0) {
				throw new WrongValueException(this.cancelAmount.getCcyTextBox(),
						Labels.getLabel("NUMBER_MAXVALUE_EQ",
								new String[] { Labels.getLabel("label_InsuranceSurrenderDialog_Amount.value"),
										Labels.getLabel("label_InsuranceSurrenderDialog_PremiumAmount.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		details.setInsuranceCancel(true);

		doRemoveValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		details.setRecordStatus(this.recordStatus.getValue());
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param vasRecording The entity that need to be render.
	 */
	public void doShowDialog(VASRecording vasRecording) {
		logger.debug(Literal.ENTERING);
		if (vasRecording.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(vasRecording.getRecordType())) {
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
		}
		doWriteBeanToComponents(vasRecording);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	private void appendAccountingDetailTab(VASRecording vasRecording, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (!onLoadProcess) {
			this.tabPostingDetails.setVisible(true);
			this.tabPanelPostingDetails.setVisible(true);
			this.tabPanelPostingDetails.setHeight(getListBoxHeight(7));
			accountsetId = getAccountingSetDAO().getAccountingSetId(AccountingEvent.CANINS, AccountingEvent.CANINS);
			final Map<String, Object> map = new HashMap<>();
			map.put("insuranceDetails", vasRecording);
			map.put("acSetID", accountsetId);
			map.put("enqModule", enqiryModule);
			map.put("dialogCtrl", this);
			map.put("isNotFinanceProcess", true);
			map.put("postAccReq", false);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					this.tabPanelPostingDetails, map);
			this.tabPostingDetails.setVisible(true);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(
					new PTStringValidator(Labels.getLabel("label_InsuranceReconciliationDialog_Remarks.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}
		if (!this.activity.isDisabled()) {
			this.activity.setConstraint(new StaticListValidator(PennantStaticListUtil.getActivity(),
					Labels.getLabel("label_InsuranceSurrenderDialog_Activity.value")));
		}
		if (!this.cancelAmount.isDisabled()) {
			this.cancelAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_InsuranceSurrenderDialog_Amount.value"), getCcyFormat(), true, false));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.remarks.setConstraint("");
		Clients.clearWrongValue(activity);
		this.activity.setConstraint("");
		this.cancelAmount.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.remarks.setErrorMessage("");
		this.activity.setErrorMessage("");
		this.cancelAmount.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void onChange$activity(Event event) {
		setCancelFLP(this.activity.getSelectedItem().getValue());
	}

	private void setCancelFLP(String activity) {
		if (VASConsatnts.STATUS_CANCEL.equals(activity)) {
			this.cancellationAfterFLP.setVisible(true);
			this.label_CancellationAfterFLP.setVisible(true);
		} else {
			this.cancellationAfterFLP.setVisible(false);
			this.label_CancellationAfterFLP.setVisible(false);
		}
	}

	private void doDelete() throws Exception {
		logger.debug(Literal.ENTERING);

		final VASRecording aVasRecording = new VASRecording();
		BeanUtils.copyProperties(this.vASRecording, aVasRecording);

		doDelete(aVasRecording.getPrimaryLinkRef(), aVasRecording);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);
		if (this.vASRecording.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
		}
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_Remarks"), this.remarks);
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_Reason"), this.activity);
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_CancelAmount"), this.cancelAmount);
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_CancelAmount"), this.serviceReqNumber);
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_CancelAmount"), this.flpCalculatedOn);
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_Reason"), this.reason);
		readOnlyComponent(isReadOnly("InsuranceSurrenderDialog_CancelAfterFLP"), this.reason);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASRecording.isNewRecord()) {
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

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);
		readOnlyComponent(true, this.remarks);
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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		this.remarks.setValue("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug(Literal.ENTERING);

		final VASRecording aVASRecording = new VASRecording();
		BeanUtils.copyProperties(this.vASRecording, aVASRecording);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aVASRecording);

		// POP UP Message for cancellation.
		if (VASConsatnts.STATUS_NORMAL.equals(this.vASRecording.getBefImage().getVasStatus())
				&& VASConsatnts.STATUS_CANCEL.equals(aVASRecording.getVasStatus())) {
			if (this.dayPassedFLPdays.intValue() > 0) {
				String msg = Labels.getLabel("message.Question.Cancel_Insurance");
				if (MessageUtil.confirm(msg) == MessageUtil.NO) {
					return;
				}
			}
		}

		isNew = aVASRecording.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVASRecording.getRecordType())) {
				aVASRecording.setVersion(aVASRecording.getVersion() + 1);
				if (isNew) {
					aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASRecording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASRecording.setNewRecord(true);
				}
			}
		} else {
			aVASRecording.setVersion(aVASRecording.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		try {
			if (doProcess(aVASRecording, tranType)) {
				// List Detail Refreshment
				refreshList();

				// Confirmation message
				String msg = PennantApplicationUtil.getSavingStatus(aVASRecording.getRoleCode(),
						aVASRecording.getNextRoleCode(), aVASRecording.getVasReference(), " Vas ",
						aVASRecording.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}
		} catch (final DataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	protected boolean doProcess(VASRecording detail, String tranType) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		detail.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginLogId());
		detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		detail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			detail.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(detail.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, detail);
				}

				if (isNotesMandatory(taskId, detail)) {
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
			detail.setTaskId(taskId);
			detail.setNextTaskId(nextTaskId);
			detail.setRoleCode(getRole());
			detail.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(detail, tranType);
			String operationRefs = getServiceOperations(taskId, detail);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(detail, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(detail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		VASRecording aVASRecording = (VASRecording) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getvASRecordingService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getvASRecordingService().saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getvASRecordingService().doApprove(auditHeader);
						if (aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getvASRecordingService().doReject(auditHeader);
						if (aVASRecording.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_InsuranceSurrenderDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_InsuranceSurrenderDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.vASRecording), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (AppException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private int getCcyFormat() {
		return CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(VASRecording aVASRecording, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASRecording.getBefImage(), aVASRecording);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aVASRecording.getUserDetails(),
				getOverideMap());
	}

	public VASRecordingService getvASRecordingService() {
		return vASRecordingService;
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public InsuranceDetailService getInsuranceDetailService() {
		return insuranceDetailService;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

}
