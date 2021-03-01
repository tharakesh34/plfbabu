package com.pennant.webui.ocrmaster.ocrheader;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.service.systemmasters.OCRHeaderService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class OCRHeaderDialogCtrl extends GFCBaseCtrl<OCRHeader> {
	private static final Logger logger = LogManager.getLogger(OCRHeaderDialogCtrl.class);
	private static final long serialVersionUID = -210929672381582779L;
	protected Window window_OCRDialog;

	protected Uppercasebox ocrID;
	protected Textbox ocrDescription;
	protected Intbox customerPortion;
	protected Combobox ocrType;
	protected Checkbox active; 
	protected Label label_OCRDialog_OCRType;
	protected Groupbox ocrSteps;
	protected Button btnNew_OCRSteps;
	protected Listbox listBoxOCRStepsDetail;

	// ServiceDAOs / Domain Classes
	private OCRHeader ocrHeader;
	private transient OCRHeaderService ocrHeaderService;
	private transient OCRHeaderListCtrl ocrHeaderListCtrl; 
	private transient boolean validationOn;

	private final List<ValueLabel> ocrApplicableList = PennantStaticListUtil.getOCRApplicableList();
	private List<OCRDetail> ocrDetailList = new ArrayList<>();
	private String moduleType = "";

	/**
	 * default constructor.<br>
	 */
	public OCRHeaderDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "OCRHeaderDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected City object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_OCRDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_OCRDialog);

		try {

			if (arguments.containsKey("ocrHeader")) {
				this.ocrHeader = (OCRHeader) arguments.get("ocrHeader");
				OCRHeader befImage = new OCRHeader();
				BeanUtils.copyProperties(this.ocrHeader, befImage);
				this.ocrHeader.setBefImage(befImage);

				setOCRHeader(this.ocrHeader);
			} else {
				setOCRHeader(null);
			}

			doLoadWorkFlow(this.ocrHeader.isWorkflow(), this.ocrHeader.getWorkflowId(), this.ocrHeader.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "OCRHeaderDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			if (arguments.containsKey("ocrHeaderListCtrl")) {
				setOCRHeaderListCtrl((OCRHeaderListCtrl) arguments.get("ocrHeaderListCtrl"));
			} else {
				setOCRHeaderListCtrl(null);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getOCRHeader());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_OCRDialog.onClose();
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.ocrID.setMaxlength(20);
		this.ocrDescription.setMaxlength(100);
		this.customerPortion.setMaxlength(2);
		this.ocrType.setMaxlength(30);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_OCRHeaderDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_OCRHeaderDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_OCRHeaderDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OCRHeaderDialog_btnSave"));
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doEdit();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		MessageUtil.showHelpWindow(event, window_OCRDialog);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doDelete();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCancel();
		logger.debug(Literal.LEAVING + event.toString());
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
		logger.debug(Literal.ENTERING);
		doWriteBeanToComponents(this.ocrHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aOCRHeader
	 * 
	 */
	public void doWriteBeanToComponents(OCRHeader aOCRHeader) {
		logger.debug(Literal.ENTERING);
		this.ocrID.setValue(aOCRHeader.getOcrID());
		this.ocrDescription.setValue(aOCRHeader.getOcrDescription());
		this.customerPortion.setValue(aOCRHeader.getCustomerPortion());
		fillComboBox(this.ocrType, aOCRHeader.getOcrType(), ocrApplicableList, "");
		if (aOCRHeader.getOcrType() == null) {
			this.ocrType.setSelectedIndex(1);
		}
		//this.splitApplicable.setChecked(aOCRHeader.isSplitApplicable());
		this.recordStatus.setValue(aOCRHeader.getRecordStatus());
		this.active.setChecked(aOCRHeader.isActive());
		if (StringUtils.equals(PennantConstants.SEGMENTED_VALUE, aOCRHeader.getOcrType())) {
			this.ocrSteps.setVisible(true);
			this.listBoxOCRStepsDetail.setVisible(true);
			this.ocrType.setDisabled(true);
		}
		doFillOCRDetails(aOCRHeader.getOcrDetailList());
		if (aOCRHeader.isNew()) {
			this.active.setChecked(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aOCRHeader
	 */
	public void doWriteComponentsToBean(OCRHeader aOCRHeader) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//OCR  ID
		try {
			aOCRHeader.setOcrID(this.ocrID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Description
		try {
			aOCRHeader.setOcrDescription(this.ocrDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// CustomerPortion
		try {
			aOCRHeader.setCustomerPortion(this.customerPortion.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Applicable on
		try {
			String strOCRType = null;
			if (this.ocrType.getSelectedItem() != null) {
				strOCRType = this.ocrType.getSelectedItem().getValue().toString();
			}
			if (strOCRType != null && !PennantConstants.List_Select.equals(strOCRType)) {
				aOCRHeader.setOcrType(strOCRType);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		/*
		 * //Split Applicable try { //aOCRHeader.setSplitApplicable(this.splitApplicable.isChecked()); } catch
		 * (WrongValueException we) { wve.add(we); }
		 */
		//Active
		try {
			aOCRHeader.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		doRemoveValidation();

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aOCRHeader.setRecordStatus(this.recordStatus.getValue());
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aOCRHeader
	 * 
	 * @throws Exception
	 */
	public void doShowDialog(OCRHeader aOCRHeader) throws Exception {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (aOCRHeader.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ocrID.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aOCRHeader.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aOCRHeader);

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_OCRDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);

		if (!this.ocrID.isReadonly()) {
			this.ocrID.setConstraint(new PTStringValidator(Labels.getLabel("label_OCRDialog_OCRID.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.ocrDescription.isReadonly()) {
			this.ocrDescription
					.setConstraint(new PTStringValidator(Labels.getLabel("label_OCRDialog_OCRDescription.value"),
							PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.customerPortion.isReadonly()) {
			this.customerPortion
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_OCRDialog_CustomerPortion.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0, 100));
		}
		//Applicable On
		if (!this.ocrType.isDisabled() && this.label_OCRDialog_OCRType.isVisible()) {
			this.ocrType.setConstraint(new StaticListValidator(ocrApplicableList,
					Labels.getLabel("label_OCRDialog_OCRApplicableOn.value")));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		this.ocrID.setConstraint("");
		this.ocrDescription.setConstraint("");
		this.customerPortion.setConstraint("");
		this.ocrType.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Enterring");
		this.ocrID.setErrorMessage("");
		this.ocrDescription.setErrorMessage("");
		this.customerPortion.setErrorMessage("");
		this.ocrType.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getOCRHeaderListCtrl().search();
	}

	// CRUD operations

	/**
	 * Deletes a OCRHeader object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final OCRHeader aOCRHeader = new OCRHeader();
		BeanUtils.copyProperties(getOCRHeader(), aOCRHeader);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ Labels.getLabel("label_OCRDialog_OCRID.value") + " : " + aOCRHeader.getOcrID();

		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aOCRHeader.getRecordType())) {
				aOCRHeader.setVersion(aOCRHeader.getVersion() + 1);
				aOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aOCRHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aOCRHeader.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aOCRHeader.getNextTaskId(), aOCRHeader);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aOCRHeader, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.ocrHeader.isNewRecord()) {
			this.ocrID.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.ocrID.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.ocrDescription.setReadonly(isReadOnly("OCRHeaderDialog_OCRDescription"));
		this.customerPortion.setReadonly(isReadOnly("OCRHeaderDialog_CustomerPortion"));
		this.ocrType.setDisabled(isReadOnly("OCRHeaderDialog_OCRApplicableOn"));
		this.active.setDisabled(isReadOnly("OCRHeaderDialog_Active"));
		this.btnNew_OCRSteps.setVisible(!isReadOnly("button_OCRHeaderDialog_btnNew_OCRSteps")); 

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.ocrHeader.isNewRecord()) {
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
		this.ocrID.setReadonly(true);
		this.ocrDescription.setReadonly(true);
		this.customerPortion.setReadonly(true);
		this.ocrType.setDisabled(true);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);
		// remove validation, if there are a save before
		this.ocrID.setValue("");
		this.ocrDescription.setValue("");
		this.ocrType.setValue("");
		//this.splitApplicable.setChecked(false);
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final OCRHeader aOCRHeader = new OCRHeader();
		BeanUtils.copyProperties(this.ocrHeader, aOCRHeader);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the OCRHeader object with the components data
		doWriteComponentsToBean(aOCRHeader);

		// Write the additional validations as per below example
		// get the selected step object from the listBox
		// Do data level validations here
		aOCRHeader.setOcrDetailList(getOcrDetailList());
		isNew = aOCRHeader.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aOCRHeader.getRecordType())) {
				aOCRHeader.setVersion(aOCRHeader.getVersion() + 1);
				if (isNew) {
					aOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aOCRHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aOCRHeader.setNewRecord(true);
				}
			}
		} else {
			aOCRHeader.setVersion(aOCRHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aOCRHeader, tranType)) {
				refreshList();
				// Close the Existing Dialog
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
	 * @param aOCRHeader
	 *            (OCRHeader)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(OCRHeader aOCRHeader, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aOCRHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aOCRHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aOCRHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aOCRHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aOCRHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aOCRHeader);
				}

				if (isNotesMandatory(taskId, aOCRHeader)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isNotBlank(nextTaskId)) {
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

			aOCRHeader.setTaskId(taskId);
			aOCRHeader.setNextTaskId(nextTaskId);
			aOCRHeader.setRoleCode(getRole());
			aOCRHeader.setNextRoleCode(nextRoleCode);

			// OCR details
			if (aOCRHeader.getOcrDetailList() != null && !aOCRHeader.getOcrDetailList().isEmpty()) {
				for (OCRDetail details : aOCRHeader.getOcrDetailList()) {
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setUserDetails(getUserWorkspace().getLoggedInUser());
					details.setWorkflowId(aOCRHeader.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aOCRHeader.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aOCRHeader.getRecordType());
							details.setNewRecord(true);
						}
					}
					if (PennantConstants.RECORD_TYPE_UPD.equals(details.getRecordType())) {
						details.setNewRecord(aOCRHeader.isNew());
					}
				}
			}

			auditHeader = getAuditHeader(aOCRHeader, tranType);

			String operationRefs = getServiceOperations(taskId, aOCRHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aOCRHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
				}
			}
		} else {
			auditHeader = getAuditHeader(aOCRHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		OCRHeader aOCRHeader = (OCRHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = ocrHeaderService.delete(auditHeader);

						deleteNotes = true;
					} else {
						auditHeader = ocrHeaderService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = ocrHeaderService.doApprove(auditHeader);

						if (aOCRHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getOcrHeaderService().doReject(auditHeader);
						if (aOCRHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_OCRDialog, auditHeader);
						logger.debug(Literal.LEAVING);
						return processCompleted;
					}
				}

				retValue = ErrorControl.showErrorControl(this.window_OCRDialog, auditHeader);

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.ocrHeader), true);
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 * 
	 * @param aOCRHeader
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(OCRHeader aOCRHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aOCRHeader.getBefImage(), aOCRHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aOCRHeader.getUserDetails(),
				getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 *
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug(Literal.ENTERING);
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_OCRDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug(Literal.LEAVING);
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
		doShowNotes(this.ocrHeader);
	}

	/*
	 * Method for Split Applicable
	 */

	public void onCheck$splitApplicable(Event event) {
		logger.debug(Literal.ENTERING);

		/*
		 * if (this.splitApplicable.isChecked()) { this.ocrSteps.setVisible(true); } else {
		 */
		if (checkOCRStepsDeleted()) {
			MessageUtil.showError(Labels.getLabel("label_OCRDetailDialog_OCRStepsDeletionAlert.value"));
			//this.splitApplicable.setChecked(true);
			return;
		}
		this.ocrSteps.setVisible(false);
		//}
		logger.debug(Literal.LEAVING);
	}

	private boolean checkOCRStepsDeleted() {
		if (!CollectionUtils.isEmpty(getOcrDetailList())) {
			for (OCRDetail details : getOcrDetailList()) {
				if (!PennantConstants.RECORD_TYPE_DEL.equalsIgnoreCase(details.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(details.getRecordType())) {
					return true;
				}
			}
		}
		return false;
	}

	// ********************************************************************//
	// *** New Button & Double Click Events for OCR Steps List **//
	// ********************************************************************//

	public void onClick$btnNew_OCRSteps(Event event) {
		logger.debug(Literal.ENTERING);

		String recordStatus = userAction.getSelectedItem().getValue();

		if (!StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_CANCELLED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_REJECTED)
				&& !StringUtils.equals(recordStatus, PennantConstants.RCD_STATUS_RESUBMITTED)) {
			doSetValidation();
		}

		doWriteComponentsToBean(getOCRHeader());
		final OCRHeader aOCRHeader = new OCRHeader();
		BeanUtils.copyProperties(this.ocrHeader, aOCRHeader);
		final OCRDetail ocrDetail = new OCRDetail();
		ocrDetail.setNewRecord(true);
		ocrDetail.setWorkflowId(0);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ocrDetail", ocrDetail);
		map.put("ocrHeaderDialogCtrl", this);
		map.put("roleCode", getRole());
		map.put("newRecord", true);
		map.put("ocrHeader", aOCRHeader);
		try {
			Executions.createComponents("/WEB-INF/pages/OCR/OCRDetailDialog.zul", window_OCRDialog, map);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public boolean getCurrentOcrExist(OCRDetail ocrDetail) {
		boolean isCurrentOcr = false;
		for (OCRDetail ocrDetail1 : ocrDetailList) {
			if (ocrDetail1.getStepSequence() != ocrDetail1.getStepSequence()) {
				isCurrentOcr = true;
				return isCurrentOcr;
			}
		}
		return isCurrentOcr;
	}

	// OCR details rendering
	public void doFillOCRDetails(List<OCRDetail> list) {
		logger.debug(Literal.ENTERING);
		int totalCustContribution = 0;
		int totalFinContribution = 0;
		this.listBoxOCRStepsDetail.getItems().clear();
		setOcrDetailList(list);
		if (list != null && !list.isEmpty()) {
			Collections.sort(list);
			for (OCRDetail details : list) {
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(String.valueOf(details.getStepSequence()));
				lc.setParent(item);

				if (!PennantConstants.RCD_DEL.equalsIgnoreCase(details.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(details.getRecordType())) {
					totalCustContribution += details.getCustomerContribution();
					totalFinContribution += details.getFinancerContribution();
				}

				String custContribution = "--";
				if (details.getCustomerContribution() > 0) {
					custContribution = String.valueOf(details.getCustomerContribution()).concat("%");
				}
				lc = new Listcell(custContribution);
				lc.setParent(item);

				String finContribution = "--";
				if (details.getFinancerContribution() > 0) {
					finContribution = String.valueOf(details.getFinancerContribution()).concat("%");
				}
				lc = new Listcell(finContribution);
				lc.setParent(item);

				lc = new Listcell(PennantJavaUtil.getLabel(details.getRecordStatus()));
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(details.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", details);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onOCRDetailItemDoubleClicked");
				this.listBoxOCRStepsDetail.appendChild(item);
			}
			//group total
			if (listBoxOCRStepsDetail != null && listBoxOCRStepsDetail.getItems().size() > 0) {
				Listitem item = new Listitem();
				Listcell lc = new Listcell(Labels.getLabel("listheader_AdvancePayments_GrandTotal.label"));
				lc.setStyle("font-weight:bold");
				lc.setParent(item);

				lc = new Listcell(String.valueOf(totalCustContribution).concat("%"));
				lc.setParent(item);

				lc = new Listcell(String.valueOf(totalFinContribution).concat("%"));
				lc.setParent(item);

				lc = new Listcell();
				lc.setParent(item);

				lc = new Listcell();
				lc.setParent(item);
				this.listBoxOCRStepsDetail.appendChild(item);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	// Double click OCR Deatils list
	public void onOCRDetailItemDoubleClicked(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		final OCRHeader aOCRHeader = new OCRHeader();
		BeanUtils.copyProperties(this.ocrHeader, aOCRHeader);
		final Listitem item = this.listBoxOCRStepsDetail.getSelectedItem();
		if (item != null) {
			final OCRDetail ocrDetail = (OCRDetail) item.getAttribute("data");

			if (!enqiryModule && isDeleteRecord(ocrDetail)) {
				MessageUtil.showMessage(Labels.getLabel("common_NoMaintainance"));
				return;
			} else {
				ocrDetail.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("ocrDetail", ocrDetail);
				map.put("ocrHeaderDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("newRecord", false);
				map.put("ocrHeader", aOCRHeader);
				if (PennantConstants.MODULETYPE_ENQ.equals(moduleType)) {
					map.put("enqModule", true);
				}
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/OCR/OCRDetailDialog.zul", window_OCRDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelect$ocrType(Event event) {
		logger.debug(Literal.ENTERING);

		String ocr = ocrType.getSelectedItem().getValue();
		if (PennantConstants.SEGMENTED_VALUE.equals(ocr)) {
			this.ocrSteps.setVisible(true);
			this.listBoxOCRStepsDetail.setVisible(true);
		} else {
			this.ocrSteps.setVisible(false);
			this.listBoxOCRStepsDetail.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void validateOCRSteps(List<OCRDetail> list) {

	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getOCRHeader().getOcrID();
	}

	public static boolean isDeleteRecord(OCRDetail ocrDetail) {
		if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, ocrDetail.getRecordType())
				|| StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, ocrDetail.getRecordType())) {
			return true;
		}
		return false;
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

	public OCRHeader getOCRHeader() {
		return this.ocrHeader;
	}

	public void setOCRHeader(OCRHeader ocrHeader) {
		this.ocrHeader = ocrHeader;
	}

	public void setOcrHeaderService(OCRHeaderService ocrHeaderService) {
		this.ocrHeaderService = ocrHeaderService;
	}

	public OCRHeaderService getOcrHeaderService() {
		return this.ocrHeaderService;
	}

	public void setOCRHeaderListCtrl(OCRHeaderListCtrl ocrHeaderListCtrl) {
		this.ocrHeaderListCtrl = ocrHeaderListCtrl;
	}

	public OCRHeaderListCtrl getOCRHeaderListCtrl() {
		return this.ocrHeaderListCtrl;
	}

	public List<OCRDetail> getOcrDetailList() {
		return ocrDetailList;
	}

	public void setOcrDetailList(List<OCRDetail> ocrDetailList) {
		this.ocrDetailList = ocrDetailList;
	}

}
