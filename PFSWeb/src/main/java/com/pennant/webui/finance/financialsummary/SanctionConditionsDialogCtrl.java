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
 * * FileName : CustomerPhoneNumberDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financialsummary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.service.finance.financialsummary.SanctionConditionsService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.webui.customermasters.customer.CustomerSelectCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class SanctionConditionsDialogCtrl extends GFCBaseCtrl<SanctionConditions> {
	private static final long serialVersionUID = -3093280086658721485L;
	private static final Logger logger = LogManager.getLogger(SanctionConditionsDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autowired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_sanctionConditionsDialog; // autowired

	protected Textbox sanctionCondition; // autowired
	protected Textbox remarks; // autowiredc
	protected Combobox status;

	private SanctionConditions sanctionConditions; // overhanded per param

	// per

	protected Button btnSearchPRCustid; // autowire

	// ServiceDAOs / Domain Classes
	private transient SanctionConditionsService sanctionConditionsService;
	private transient CustomerSelectCtrl customerSelectCtrl;

	private boolean newRecord = false;
	private boolean newSanctionConditons = false;

	private List<SanctionConditions> sanctionConditionList;
	private FinancialSummaryDialogCtrl financialSummaryDialogCtrl;
	protected JdbcSearchObject<Customer> newSearchObject;
	private String moduleType = "";
	private String userRole = "";
	private boolean isFinanceProcess = false;
	private boolean workflow = false;

	/**
	 * default constructor.<br>
	 */
	public SanctionConditionsDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "SanctionConditionsDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected CustomerPhoneNumber object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_sanctionConditionsDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_sanctionConditionsDialog);

		try {

			if (arguments.containsKey("sanctionConditions")) {
				this.sanctionConditions = (SanctionConditions) arguments.get("sanctionConditions");
				SanctionConditions befImage = new SanctionConditions();
				BeanUtils.copyProperties(this.sanctionConditions, befImage);
				this.sanctionConditions.setBefImage(befImage);
				setSanctionConditions(this.sanctionConditions);
			} else {
				getSanctionConditions();
			}

			if (arguments.containsKey("moduleType")) {
				this.moduleType = (String) arguments.get("moduleType");
			}

			if (getSanctionConditions().isNewRecord()) {
				setNewRecord(true);

			}

			if (arguments.containsKey("financialSummaryDialogCtrl")) {
				setFinancialSummaryDialogCtrl((FinancialSummaryDialogCtrl) arguments.get("financialSummaryDialogCtrl"));
				setNewSanctionConditons(true);

				if (arguments.containsKey("newRecord")) {
					setNewRecord(true);
				} else {
					setNewRecord(false);
				}
				this.sanctionConditions.setWorkflowId(0);
				if (arguments.containsKey("roleCode")) {
					userRole = arguments.get("roleCode").toString();
					getUserWorkspace().allocateRoleAuthorities(userRole, "SanctionConditionsDialog");
				}

			}
			if (arguments.containsKey("isFinanceProcess")) {
				isFinanceProcess = (Boolean) arguments.get("isFinanceProcess");
			}

			if (getFinancialSummaryDialogCtrl() != null && !isFinanceProcess) {
				workflow = this.sanctionConditions.isWorkflow();
			}

			doLoadWorkFlow(this.sanctionConditions.isWorkflow(), this.sanctionConditions.getWorkflowId(),
					this.sanctionConditions.getNextTaskId());
			/* set components visible dependent of the users rights */
			doCheckRights();

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "SanctionConditionsDialog");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getSanctionConditions());

		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_sanctionConditionsDialog.onClose();
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		this.sanctionCondition.setMaxlength(500);
		this.remarks.setMaxlength(40);

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities("CustomerPhoneNumberDialog", userRole);

		this.btnNew.setVisible(true);
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CustomerPhoneNumberDialog_btnEdit"));
		this.btnDelete.setVisible(true);
		this.btnSave.setVisible(true);
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
		MessageUtil.showHelpWindow(event, window_sanctionConditionsDialog);
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
		doWriteBeanToComponents(this.sanctionConditions.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCustomerPhoneNumber CustomerPhoneNumber
	 */
	public void doWriteBeanToComponents(SanctionConditions sanctionConditions) {
		logger.debug("Entering");

		if (isNewRecord()) {
			this.sanctionCondition.setValue("");
			this.remarks.setValue("");
			fillComboBox(this.status, "", PennantStaticListUtil.getSanctionStatusList(), "");
		} else {
			this.sanctionCondition.setValue(sanctionConditions.getSanctionCondition());
			this.remarks.setValue(sanctionConditions.getRemarks());
			fillComboBox(this.status, sanctionConditions.getStatus(), PennantStaticListUtil.getSanctionStatusList(),
					"");
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCustomerPhoneNumber
	 */
	public void doWriteComponentsToBean(SanctionConditions sanctionConditions) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			sanctionConditions.setSanctionCondition(this.sanctionCondition.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			sanctionConditions.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			sanctionConditions.setStatus(this.status.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		setSanctionConditions(sanctionConditions);
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aCustomerPhoneNumber
	 */
	public void doShowDialog(SanctionConditions sanctionConditions) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isNewSanctionConditons()) {
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
			doWriteBeanToComponents(sanctionConditions);

			doCheckEnquiry();
			if (isNewSanctionConditons()) {
				this.window_sanctionConditionsDialog.setHeight("50%");
				this.window_sanctionConditionsDialog.setWidth("60%");
				this.groupboxWf.setVisible(false);
				this.window_sanctionConditionsDialog.doModal();
			} else {
				this.window_sanctionConditionsDialog.setWidth("100%");
				this.window_sanctionConditionsDialog.setHeight("100%");
				setDialog(DialogType.MODAL);
			}

		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_sanctionConditionsDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	private void doCheckEnquiry() {
		if ("ENQ".equals(this.moduleType)) {
			this.sanctionCondition.setReadonly(true);
			this.remarks.setDisabled(true);
			this.status.setDisabled(true);
			this.btnSave.setVisible(false);
			this.btnDelete.setVisible(false);
		}
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	/*
	 * protected void refreshList() { getFinancialSummaryDialogCtrl().search(); }
	 */

	// CRUD operations

	protected void onDoDelete(final SanctionConditions asanctionConditions) {

		String tranType = PennantConstants.TRAN_WF;

		if (StringUtils.isBlank(asanctionConditions.getRecordType())) {
			asanctionConditions.setVersion(asanctionConditions.getVersion() + 1);
			asanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			if (!isFinanceProcess && getSanctionConditions() != null && getSanctionConditions().isWorkflow()) {
				asanctionConditions.setNewRecord(true);
			}
			if (isWorkFlowEnabled()) {
				asanctionConditions.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		} else if (StringUtils.equals(asanctionConditions.getRecordType(), PennantConstants.RCD_UPD)) {
			asanctionConditions.setNewRecord(true);
		}

		try {

			if (isNewSanctionConditons()) {
				tranType = PennantConstants.TRAN_DEL;
				AuditHeader auditHeader = newSanctionConditionsProcess(sanctionConditions, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_sanctionConditionsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinancialSummaryDialogCtrl().doFillSanctionConditionsDetails(this.sanctionConditionList);
					// true;
					// send the data back to customer
					closeDialog();
				}

			} else if (doProcess(asanctionConditions, tranType)) {
				/* refreshList(); */
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final SanctionConditions asanctionConditions = new SanctionConditions();
		BeanUtils.copyProperties(getSanctionConditions(), asanctionConditions);

		final String keyReference = Labels.getLabel("label_SanctionConditionsDialog_SanctionCondition.value") + " : "
				+ asanctionConditions.getSeqNo();

		doDelete(keyReference, asanctionConditions);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		this.sanctionCondition.setDisabled(isReadOnly("CustomerPhoneNumberDialog_phonePriority"));
		this.remarks.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneCustID"));
		this.status.setReadonly(isReadOnly("CustomerPhoneNumberDialog_phoneCustID"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.sanctionConditions.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (newSanctionConditons) {
				if ("ENQ".equals(this.moduleType)) {
					this.btnCtrl.setBtnStatus_New();
					this.btnSave.setVisible(false);
					btnCancel.setVisible(false);
				} else if (isNewRecord()) {
					this.btnCtrl.setBtnStatus_Edit();
					btnCancel.setVisible(false);
				} else {
					this.btnCtrl.setWFBtnStatus_Edit(newSanctionConditons);
				}
			} else {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/*
	 * public boolean isReadOnly(String componentName) { boolean isCustomerWorkflow = false; if
	 * (getFinancialSummaryDialogCtrl() != null) { isCustomerWorkflow = getris; } if (isWorkFlowEnabled() ||
	 * isCustomerWorkflow) { return getUserWorkspace().isReadOnly(componentName); } return false; }
	 */

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.sanctionCondition.setReadonly(true);
		this.remarks.setReadonly(true);
		this.status.setReadonly(true);

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
		this.sanctionCondition.setValue("");
		this.remarks.setValue("");
		this.status.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final SanctionConditions sanctionConditions = new SanctionConditions();
		BeanUtils.copyProperties(getSanctionConditions(), sanctionConditions);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		// fill the CustomerPhoneNumber object with the components data
		doWriteComponentsToBean(sanctionConditions);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = sanctionConditions.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(sanctionConditions.getRecordType())) {
				sanctionConditions.setVersion(sanctionConditions.getVersion() + 1);
				if (isNew) {
					sanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					sanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					sanctionConditions.setNewRecord(true);
				}
			}
		} else {

			if (isNewSanctionConditons()) {
				if (isNewRecord()) {
					sanctionConditions.setVersion(1);
					sanctionConditions.setRecordType(PennantConstants.RCD_ADD);
				} else {
					tranType = PennantConstants.TRAN_UPD;
					if (workflow && !isFinanceProcess && StringUtils.isBlank(sanctionConditions.getRecordType())) {
						sanctionConditions.setNewRecord(true);
					}
				}

				if (StringUtils.isBlank(sanctionConditions.getRecordType())) {
					sanctionConditions.setVersion(sanctionConditions.getVersion() + 1);
					sanctionConditions.setRecordType(PennantConstants.RCD_UPD);
				}

				if (sanctionConditions.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
					tranType = PennantConstants.TRAN_ADD;
				} else if (sanctionConditions.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					tranType = PennantConstants.TRAN_UPD;
				}

			} else {
				sanctionConditions.setVersion(sanctionConditions.getVersion() + 1);
				if (isNew) {
					tranType = PennantConstants.TRAN_ADD;
				} else {
					tranType = PennantConstants.TRAN_UPD;
				}
			}
		}

		// save it to database
		try {

			if (isNewSanctionConditons()) {
				AuditHeader auditHeader = newSanctionConditionsProcess(sanctionConditions, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_sanctionConditionsDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					getFinancialSummaryDialogCtrl().doFillSanctionConditionsDetails(this.sanctionConditionList);
					// send the data back to customer
					closeDialog();
				}
			} else if (doProcess(sanctionConditions, tranType)) {
				/* refreshList(); */
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
	private AuditHeader newSanctionConditionsProcess(SanctionConditions asanctionConditions, String tranType) {
		boolean recordAdded = false;

		AuditHeader auditHeader = getAuditHeader(sanctionConditions, tranType);
		sanctionConditionList = new ArrayList<SanctionConditions>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];

		if (getFinancialSummaryDialogCtrl().getSanctionConditionsDetailList() != null
				&& getFinancialSummaryDialogCtrl().getSanctionConditionsDetailList().size() > 0) {
			for (int i = 0; i < getFinancialSummaryDialogCtrl().getSanctionConditionsDetailList().size(); i++) {
				SanctionConditions sanctionConditions = getFinancialSummaryDialogCtrl()
						.getSanctionConditionsDetailList().get(i);

				if (asanctionConditions.getSeqNo() == sanctionConditions.getSeqNo()) {
					errParm[0] = Labels.getLabel("label_SanctionConditionsDialog_SanctionCondition.value");
					errParm[1] = String.valueOf(sanctionConditions.getSeqNo());
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}

					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (asanctionConditions.getRecordType().equals(PennantConstants.RCD_UPD)) {
							asanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							sanctionConditionList.add(asanctionConditions);
						} else if (asanctionConditions.getRecordType().equals(PennantConstants.RCD_ADD)) {
							recordAdded = true;
						} else if (asanctionConditions.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							asanctionConditions.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							sanctionConditionList.add(asanctionConditions);
						} else if (asanctionConditions.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
							recordAdded = true;
							for (int j = 0; j < getFinancialSummaryDialogCtrl().getSanctionConditionsDetailList()
									.size(); j++) {
								SanctionConditions sanctionConditionsDetails = getFinancialSummaryDialogCtrl()
										.getSanctionConditionsDetailList().get(j);
								if (sanctionConditionsDetails.getSanctionCondition() == asanctionConditions
										.getSanctionCondition()
										&& sanctionConditionsDetails.getSanctionCondition()
												.equals(asanctionConditions.getSanctionCondition())) {
									sanctionConditionList.add(sanctionConditionsDetails);
								}
							}
						}
					} else {
						if (!PennantConstants.TRAN_UPD.equals(tranType)) {
							sanctionConditionList.add(sanctionConditions);
						}
					}
				} else {
					sanctionConditionList.add(sanctionConditions);
				}
			}
		}

		if (!recordAdded) {
			sanctionConditionList.add(sanctionConditions);
		}
		return auditHeader;
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustomerPhoneNumber (CustomerPhoneNumber)
	 * 
	 * @param tranType             (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(SanctionConditions sanctionConditions, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		sanctionConditions.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		sanctionConditions.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		sanctionConditions.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			sanctionConditions.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(sanctionConditions.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, sanctionConditions);
				}

				if (isNotesMandatory(taskId, sanctionConditions)) {
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

			sanctionConditions.setTaskId(taskId);
			sanctionConditions.setNextTaskId(nextTaskId);
			sanctionConditions.setRoleCode(getRole());
			sanctionConditions.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(sanctionConditions, tranType);

			String operationRefs = getServiceOperations(taskId, sanctionConditions);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(sanctionConditions, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(sanctionConditions, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		SanctionConditions asanctionConditions = (SanctionConditions) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					deleteNotes = true;
				} else {
					auditHeader = getSanctionConditionsService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					if (asanctionConditions.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getSanctionConditionsService().doReject(auditHeader);
					if (asanctionConditions.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_sanctionConditionsDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_sanctionConditionsDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.sanctionConditions), true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustomerIdentity
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(SanctionConditions sanctionConditions, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, sanctionConditions.getBefImage(), sanctionConditions);

		return new AuditHeader(getReference(), String.valueOf(sanctionConditions.getId()), null, null, auditDetail,
				sanctionConditions.getUserDetails(), getOverideMap());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.sanctionConditions);
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

	public FinancialSummaryDialogCtrl getFinancialSummaryDialogCtrl() {
		return financialSummaryDialogCtrl;
	}

	public void setFinancialSummaryDialogCtrl(FinancialSummaryDialogCtrl financialSummaryDialogCtrl) {
		this.financialSummaryDialogCtrl = financialSummaryDialogCtrl;
	}

	public SanctionConditions getSanctionConditions() {
		return sanctionConditions;
	}

	public void setSanctionConditions(SanctionConditions sanctionConditions) {
		this.sanctionConditions = sanctionConditions;
	}

	public boolean isNewSanctionConditons() {
		return newSanctionConditons;
	}

	public void setNewSanctionConditons(boolean newSanctionConditons) {
		this.newSanctionConditons = newSanctionConditons;
	}

	public SanctionConditionsService getSanctionConditionsService() {
		return sanctionConditionsService;
	}

	public void setSanctionConditionsService(SanctionConditionsService sanctionConditionsService) {
		this.sanctionConditionsService = sanctionConditionsService;
	}

}
