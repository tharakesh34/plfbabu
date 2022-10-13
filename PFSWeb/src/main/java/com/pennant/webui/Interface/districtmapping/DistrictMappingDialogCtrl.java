package com.pennant.webui.Interface.districtmapping;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennant.backend.model.systemmasters.District;
import com.pennant.backend.service.cersai.DistrictMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Interface/DistrictMapping/districtMappingDialog.zul file. <br>
 */
public class DistrictMappingDialogCtrl extends GFCBaseCtrl<DistrictMapping> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DistrictMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DistrictMappingDialog;
	protected Space space_MappingType;
	protected Combobox mappingType;
	protected ExtendedCombobox district;
	protected Space space_MappingValue;
	protected Textbox mappingValue;
	private DistrictMapping districtMapping; // overhanded per param

	private transient DistrictMappingListCtrl districtMappingListCtrl; // overhanded per param
	private transient DistrictMappingService districtMappingService;

	private List<ValueLabel> listMappingType = PennantStaticListUtil.getMappingTypes();

	/**
	 * default constructor.<br>
	 */
	public DistrictMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DistrictMappingDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(this.districtMapping.getMappingType());
		referenceBuffer.append("/");
		referenceBuffer.append(this.districtMapping.getDistrict());
		referenceBuffer.append("/");
		referenceBuffer.append(this.districtMapping.getMappingValue());
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DistrictMappingDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_DistrictMappingDialog);

		try {
			// Get the required arguments.
			this.districtMapping = (DistrictMapping) arguments.get("districtMapping");
			this.districtMappingListCtrl = (DistrictMappingListCtrl) arguments.get("districtMappingListCtrl");

			if (this.districtMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			DistrictMapping dstrctMppng = new DistrictMapping();
			BeanUtils.copyProperties(this.districtMapping, dstrctMppng);
			this.districtMapping.setBefImage(dstrctMppng);

			// Render the page and display the data.
			doLoadWorkFlow(this.districtMapping.isWorkflow(), this.districtMapping.getWorkflowId(),
					this.districtMapping.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
			} else {
				getUserWorkspace().allocateAuthorities(this.pageRightName, null);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.districtMapping);
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

		this.district.setMandatoryStyle(true);
		this.district.setModuleName("District");
		this.district.setValueColumn("Code");
		this.district.setDescColumn("Name");
		this.district.setValidateColumns(new String[] { "Code" });
		this.mappingValue.setMaxlength(50);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DistrictMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DistrictMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DistrictMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DistrictMappingDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
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
		doShowNotes(this.districtMapping);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		districtMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.districtMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$district(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = district.getObject();
		if (dataObject instanceof String) {
			this.district.setValue(dataObject.toString());
			this.district.setDescription("");
			this.district.setAttribute("", null);
		} else {
			District dstrct = (District) dataObject;
			if (dstrct != null) {
				this.district.setValue(dstrct.getCode());
				this.district.setDescription(dstrct.getName());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param districtMapping
	 * 
	 */
	public void doWriteBeanToComponents(DistrictMapping aDistrictMapping) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mappingType, String.valueOf(aDistrictMapping.getMappingType()), listMappingType, "");
		this.district.setValue(aDistrictMapping.getDistrict());
		this.mappingValue.setValue(aDistrictMapping.getMappingValue());

		if (aDistrictMapping.isNewRecord()) {
			this.district.setDescription("");
		} else {
			this.district.setDescription(aDistrictMapping.getDistrictName());
		}

		this.recordStatus.setValue(aDistrictMapping.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDistrictMapping
	 */
	public void doWriteComponentsToBean(DistrictMapping aDistrictMapping) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Mapping Type
		try {
			String strMappingType = null;
			if (this.mappingType.getSelectedItem() != null) {
				strMappingType = this.mappingType.getSelectedItem().getValue().toString();
			}
			if (strMappingType != null && !PennantConstants.List_Select.equals(strMappingType)) {
				aDistrictMapping.setMappingType(strMappingType);

			} else {
				aDistrictMapping.setMappingType("0");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// District
		try {
			aDistrictMapping.setDistrict(this.district.getValidatedValue());
			aDistrictMapping.setDistrictName(this.district.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mapping Value
		try {
			aDistrictMapping.setMappingValue(this.mappingValue.getValue());
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
	 * @param districtMapping The entity that need to be render.
	 */
	public void doShowDialog(DistrictMapping districtMapping) {
		logger.debug(Literal.LEAVING);

		if (districtMapping.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.mappingType.focus();
		} else {
			this.mappingType.setReadonly(true);
			this.district.setReadonly(true);
			this.mappingValue.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(districtMapping.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.mappingValue.focus();
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

		doWriteBeanToComponents(districtMapping);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.mappingType.isReadonly()) {
			this.mappingType.setConstraint(new StaticListValidator(listMappingType,
					Labels.getLabel("label_DistrictMappingDialog_MappingType.value")));
		}
		if (!this.district.isReadonly()) {
			this.district
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DistrictMappingDialog_District.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.mappingValue.isReadonly()) {
			this.mappingValue.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DistrictMappingDialog_MappingValue.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.mappingType.setConstraint("");
		this.district.setConstraint("");
		this.mappingValue.setConstraint("");

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

	/**
	 * Deletes a DistrictMapping object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final DistrictMapping aDistrictMapping = new DistrictMapping();
		BeanUtils.copyProperties(this.districtMapping, aDistrictMapping);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aDistrictMapping.getMappingType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aDistrictMapping.getRecordType()).equals("")) {
				aDistrictMapping.setVersion(aDistrictMapping.getVersion() + 1);
				aDistrictMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aDistrictMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aDistrictMapping.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aDistrictMapping.getNextTaskId(),
							aDistrictMapping);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDistrictMapping, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.districtMapping.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.mappingType);
			readOnlyComponent(false, this.district);
			readOnlyComponent(false, this.mappingValue);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.mappingType);
			readOnlyComponent(true, this.district);
			readOnlyComponent(true, this.mappingValue);

		}
		readOnlyComponent(isReadOnly("DistrictMappingDialog_MappingValue"), this.mappingValue);
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.districtMapping.isNewRecord()) {
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

		readOnlyComponent(true, this.mappingType);
		readOnlyComponent(true, this.district);
		readOnlyComponent(true, this.mappingValue);

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
		logger.debug("Entering");
		this.mappingType.setSelectedIndex(0);
		this.district.setValue("");
		this.district.setDescription("");
		this.mappingValue.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final DistrictMapping aDistrictMapping = new DistrictMapping();
		BeanUtils.copyProperties(this.districtMapping, aDistrictMapping);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aDistrictMapping);

		isNew = aDistrictMapping.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDistrictMapping.getRecordType())) {
				aDistrictMapping.setVersion(aDistrictMapping.getVersion() + 1);
				if (isNew) {
					aDistrictMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDistrictMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDistrictMapping.setNewRecord(true);
				}
			}
		} else {
			aDistrictMapping.setVersion(aDistrictMapping.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aDistrictMapping, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(DistrictMapping aDistrictMapping, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aDistrictMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDistrictMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDistrictMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aDistrictMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aDistrictMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aDistrictMapping);
				}

				if (isNotesMandatory(taskId, aDistrictMapping)) {
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

			aDistrictMapping.setTaskId(taskId);
			aDistrictMapping.setNextTaskId(nextTaskId);
			aDistrictMapping.setRoleCode(getRole());
			aDistrictMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aDistrictMapping, tranType);
			String operationRefs = getServiceOperations(taskId, aDistrictMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aDistrictMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aDistrictMapping, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param AuditHeader auditHeader
	 * @param method      (String)
	 * @return boolean
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DistrictMapping aDistrictMapping = (DistrictMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = districtMappingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = districtMappingService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = districtMappingService.doApprove(auditHeader);

					if (aDistrictMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = districtMappingService.doReject(auditHeader);
					if (aDistrictMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DistrictMappingDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_DistrictMappingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.districtMapping), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(DistrictMapping aDistrictMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDistrictMapping.getBefImage(), aDistrictMapping);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aDistrictMapping.getUserDetails(),
				getOverideMap());
	}

	public void setDistrictMappingService(DistrictMappingService districtMappingService) {
		this.districtMappingService = districtMappingService;
	}

}
