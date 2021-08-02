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
package com.pennant.webui.systemmasters.panmapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennant.backend.service.systemmasters.CustTypePANMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/SystemMaster/PANMapping/CustTypePANMappingDialog.zul file. <br>
 * ************************************************************<br>
 */
public class CustTypePANMappingDialogCtrl extends GFCBaseCtrl<CustTypePANMapping> {
	private static final long serialVersionUID = -6945930303723518608L;
	private static final Logger logger = LogManager.getLogger(CustTypePANMappingDialogCtrl.class);

	protected Window window_PANMappingDialog;
	protected Combobox custCategory;
	protected ExtendedCombobox custType;
	protected Uppercasebox panLetter;
	protected Checkbox active;

	private CustTypePANMapping custTypePANMapping;

	private transient CustTypePANMappingListCtrl custTypePANMappingListCtrl;

	private transient boolean validationOn;

	private transient CustTypePANMappingService custTypePANMappingService;

	private List<ValueLabel> CustCtgType = PennantAppUtil.getcustCtgCodeList();

	/**
	 * default constructor.<br>
	 */
	public CustTypePANMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "PANMappingDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_PANMappingDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_PANMappingDialog);

		try {
			// Get the required arguments.
			this.custTypePANMapping = (CustTypePANMapping) arguments.get("custTypePANMapping");
			this.custTypePANMappingListCtrl = (CustTypePANMappingListCtrl) arguments.get("panMappingListCtrl");

			if (this.custTypePANMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			CustTypePANMapping custTypePANMapping = new CustTypePANMapping();
			BeanUtils.copyProperties(this.custTypePANMapping, custTypePANMapping);
			this.custTypePANMapping.setBefImage(custTypePANMapping);

			// Render the page and display the data.
			doLoadWorkFlow(this.custTypePANMapping.isWorkflow(), this.custTypePANMapping.getWorkflowId(),
					this.custTypePANMapping.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.custTypePANMapping);
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

		this.custType.setMandatoryStyle(true);
		this.custType.setModuleName("CustomerType");
		this.custType.setValueColumn("CustTypeCode");
		this.custType.setDescColumn("CustTypeDesc");
		this.custType.setValidateColumns(new String[] { "CustTypeCode" });
		readOnlyComponent(true, this.custType);

		this.panLetter.setMaxlength(1);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_PANMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_PANMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_PANMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_PANMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		doSave();
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
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
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.custTypePANMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$custType(Event event) throws WrongValueException, Exception {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = custType.getObject();

		if (dataObject instanceof String) {

			this.custType.setValue(dataObject.toString());
		} else {

			CustomerType details = (CustomerType) dataObject;

			if (details != null) {

			} else {
				this.custType.setValue("");
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$custCategory(Event event) {

		String str = this.custCategory.getSelectedItem().getValue().toString();
		readOnlyComponent(false, this.custType);
		this.custType.setValue("");
		this.custType.setDescription("");
		onChangeCustCategory(str);

	}

	private void onChangeCustCategory(String str) {

		Filter filter[] = null;
		if (!StringUtils.equals(str, PennantConstants.List_Select)) {
			filter = new Filter[1];
			filter[0] = new Filter("CUSTTYPECTG", str, Filter.OP_EQUAL);
			this.custType.setFilters(filter);
		} else {
			this.custType.setFilters(filter);
		}
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param custTypePANMapping
	 * 
	 */
	public void doWriteBeanToComponents(CustTypePANMapping custTypePANMapping) {
		logger.debug(Literal.ENTERING);

		// Customer Category
		fillComboBox(this.custCategory, custTypePANMapping.getCustCategory(), CustCtgType, "");

		// Customer Type
		this.custType.setValue(custTypePANMapping.getCustType(), custTypePANMapping.getCustTypeDesc());

		// PAN 4th Letter
		this.panLetter.setValue(custTypePANMapping.getPanLetter());

		// Active
		if (custTypePANMapping.isNewRecord()) {
			this.active.setChecked(true);
		} else {
			this.active.setChecked(custTypePANMapping.isActive());
		}
		onChangeCustCategory(custTypePANMapping.getCustCategory());

		this.recordStatus.setValue(custTypePANMapping.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param custTypePANMapping
	 */
	public void doWriteComponentsToBean(CustTypePANMapping custTypePANMapping) {
		logger.debug(Literal.ENTERING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			custTypePANMapping.setCustCategory(getComboboxValue(this.custCategory));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			custTypePANMapping.setCustType(this.custType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			custTypePANMapping.setPanLetter(this.panLetter.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			custTypePANMapping.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		custTypePANMapping.setRecordStatus(this.recordStatus.getValue());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param custTypePANMapping The entity that need to be render.
	 */
	public void doShowDialog(CustTypePANMapping custTypePANMapping) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (custTypePANMapping.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.custType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.custType.focus();
				if (StringUtils.isNotBlank(custTypePANMapping.getRecordType())) {
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
		doWriteBeanToComponents(custTypePANMapping);

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(true);

		if (!this.custCategory.isDisabled()) {
			this.custCategory.setConstraint(
					new StaticListValidator(CustCtgType, Labels.getLabel("label_PANMappingDialog_CustCategory.value")));

		}

		if (!this.custType.isReadonly()) {
			this.custType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_PANMappingDialog_CustType.value"), null, true, true));
		}

		if (!this.panLetter.isReadonly()) {
			this.panLetter
					.setConstraint(new PTStringValidator(Labels.getLabel("label_PANMappingDialog_panLetter.value"),
							PennantRegularExpressions.REGEX_ALPHA, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		setValidationOn(false);
		this.custCategory.setConstraint("");
		this.custType.setConstraint("");
		this.panLetter.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
	}

	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);

		this.custCategory.setErrorMessage("");
		this.custType.setErrorMessage("");
		this.panLetter.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final CustTypePANMapping entity = new CustTypePANMapping();
		BeanUtils.copyProperties(this.custTypePANMapping, entity);

		doDelete(Labels.getLabel("label_PANMappingDialog_panLetter.value") + " : " + custTypePANMapping.getPanLetter(),
				custTypePANMapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.custTypePANMapping.isNewRecord()) {
			this.custType.setReadonly(true);
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.custType.setReadonly(isReadOnly("PANMappingDialog_custType"));
		}
		this.custCategory.setDisabled(isReadOnly("PANMappingDialog_custCategory"));
		this.panLetter.setReadonly(isReadOnly("PANMappingDialog_panLetter"));
		this.active.setDisabled(isReadOnly("PANMappingDialog_active"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.custTypePANMapping.isNewRecord()) {
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
		logger.debug(Literal.ENTERING);
		this.custCategory.setReadonly(true);
		this.custType.setReadonly(true);
		this.panLetter.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
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

		this.custCategory.setValue("");
		this.custType.setValue("");
		this.panLetter.setValue("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final CustTypePANMapping aCustTypePANMapping = new CustTypePANMapping();
		BeanUtils.copyProperties(this.custTypePANMapping, aCustTypePANMapping);
		boolean isNew;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		doSetValidation();
		// fill the CustTypePANMapping object with the components data
		doWriteComponentsToBean(aCustTypePANMapping);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCustTypePANMapping.isNewRecord();
		String tranType;

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCustTypePANMapping.getRecordType())) {
				aCustTypePANMapping.setVersion(aCustTypePANMapping.getVersion() + 1);
				if (isNew) {
					aCustTypePANMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCustTypePANMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCustTypePANMapping.setNewRecord(true);
				}
			}
		} else {
			aCustTypePANMapping.setVersion(aCustTypePANMapping.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aCustTypePANMapping, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aCustTypePANMapping (CustTypePANMapping)
	 * 
	 * @param tranType            (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(CustTypePANMapping aCustTypePANMapping, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader;
		String nextRoleCode = "";

		aCustTypePANMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCustTypePANMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCustTypePANMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId;
			aCustTypePANMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCustTypePANMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCustTypePANMapping);
				}

				if (isNotesMandatory(taskId, aCustTypePANMapping)) {
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

			aCustTypePANMapping.setTaskId(taskId);
			aCustTypePANMapping.setNextTaskId(nextTaskId);
			aCustTypePANMapping.setRoleCode(getRole());
			aCustTypePANMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aCustTypePANMapping, tranType);
			String operationRefs = getServiceOperations(taskId, aCustTypePANMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCustTypePANMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCustTypePANMapping, tranType);
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
		AuditHeader aAuditHeader = auditHeader;
		CustTypePANMapping aCustTypePANMapping = (CustTypePANMapping) aAuditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (aAuditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						aAuditHeader = getCustTypePANMappingService().delete(aAuditHeader);
						deleteNotes = true;
					} else {
						aAuditHeader = getCustTypePANMappingService().saveOrUpdate(aAuditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						aAuditHeader = getCustTypePANMappingService().doApprove(aAuditHeader);

						if (aCustTypePANMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						aAuditHeader = getCustTypePANMappingService().doReject(aAuditHeader);

						if (aCustTypePANMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						aAuditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_PANMappingDialog, aAuditHeader);
						return processCompleted;
					}
				}

				aAuditHeader = ErrorControl.showErrorDetails(this.window_PANMappingDialog, aAuditHeader);
				retValue = aAuditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.custTypePANMapping), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					aAuditHeader.setOveride(true);
					aAuditHeader.setErrorMessage(null);
					aAuditHeader.setInfoMessage(null);
					aAuditHeader.setOverideMessage(null);
				}
			}

			setOverideMap(aAuditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aCustTypePANMapping
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CustTypePANMapping aCustTypePANMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCustTypePANMapping.getBefImage(), aCustTypePANMapping);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aCustTypePANMapping.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.custTypePANMapping);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		custTypePANMappingListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.custTypePANMapping.getMappingID());
	}

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CustTypePANMappingListCtrl getCustTypePANMappingListCtrl() {
		return custTypePANMappingListCtrl;
	}

	public void setCustTypePANMappingListCtrl(CustTypePANMappingListCtrl custTypePANMappingListCtrl) {
		this.custTypePANMappingListCtrl = custTypePANMappingListCtrl;
	}

	public CustTypePANMappingService getCustTypePANMappingService() {
		return custTypePANMappingService;
	}

	public void setCustTypePANMappingService(CustTypePANMappingService custTypePANMappingService) {
		this.custTypePANMappingService = custTypePANMappingService;
	}

}
