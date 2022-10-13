package com.pennant.webui.Interface.provincemapping;

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
import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.service.cersai.ProvinceMappingService;
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
 * This is the controller class for the /WEB-INF/pages/interface/ProvinceMapping/provinceMappingDialog.zul file. <br>
 */
public class ProvinceMappingDialogCtrl extends GFCBaseCtrl<ProvinceMapping> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ProvinceMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProvinceMappingDialog;
	protected Space space_MappingType;
	protected Combobox mappingType;
	protected ExtendedCombobox province;
	protected Space space_MappingValue;
	protected Textbox mappingValue;
	private ProvinceMapping provinceMapping; // overhanded per param

	private transient ProvinceMappingListCtrl provinceMappingListCtrl; // overhanded per param
	private transient ProvinceMappingService provinceMappingService;

	private List<ValueLabel> listMappingType = PennantStaticListUtil.getMappingTypes();

	/**
	 * default constructor.<br>
	 */
	public ProvinceMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProvinceMappingDialog";
	}

	@Override
	protected String getReference() {
		return getProvinceMapping().getMappingType() + PennantConstants.KEY_SEPERATOR
				+ getProvinceMapping().getMappingValue() + PennantConstants.KEY_SEPERATOR
				+ getProvinceMapping().getProvince();

	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_ProvinceMappingDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ProvinceMappingDialog);

		try {
			// Get the required arguments.
			this.provinceMapping = (ProvinceMapping) arguments.get("provinceMapping");
			this.provinceMappingListCtrl = (ProvinceMappingListCtrl) arguments.get("provinceMappingListCtrl");

			if (this.provinceMapping == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ProvinceMapping provinceMapping = new ProvinceMapping();
			BeanUtils.copyProperties(this.provinceMapping, provinceMapping);
			this.provinceMapping.setBefImage(provinceMapping);

			// Render the page and display the data.
			doLoadWorkFlow(this.provinceMapping.isWorkflow(), this.provinceMapping.getWorkflowId(),
					this.provinceMapping.getNextTaskId());

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
			doShowDialog(this.provinceMapping);
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

		this.province.setMandatoryStyle(true);
		this.province.setModuleName("Province");
		this.province.setValueColumn("CPProvince");
		this.province.setDescColumn("CPProvinceName");
		this.province.setValidateColumns(new String[] { "CPProvince" });
		this.mappingValue.setMaxlength(50);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProvinceMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProvinceMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProvinceMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProvinceMappingDialog_btnSave"));
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
		doShowNotes(this.provinceMapping);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		provinceMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.provinceMapping.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$province(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = province.getObject();
		if (dataObject instanceof String) {
			this.province.setValue(dataObject.toString());
			this.province.setDescription("");
			this.province.setAttribute("", null);
		} else {
			Province province = (Province) dataObject;
			if (province != null) {
				this.province.setValue(province.getCPProvince());
				this.province.setDescription(province.getCPProvinceName());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param provinceMapping
	 * 
	 */
	public void doWriteBeanToComponents(ProvinceMapping aProvinceMapping) {
		logger.debug(Literal.ENTERING);

		fillComboBox(this.mappingType, String.valueOf(aProvinceMapping.getMappingType()), listMappingType, "");
		this.province.setValue(aProvinceMapping.getProvince());
		this.mappingValue.setValue(aProvinceMapping.getMappingValue());

		if (aProvinceMapping.isNewRecord()) {
			this.province.setDescription("");
		} else {
			this.province.setDescription(aProvinceMapping.getProvinceName());
		}

		this.recordStatus.setValue(aProvinceMapping.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aProvinceMapping
	 */
	public void doWriteComponentsToBean(ProvinceMapping aProvinceMapping) {
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
				aProvinceMapping.setMappingType(strMappingType);

			} else {
				aProvinceMapping.setMappingType("0");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Province
		try {

			aProvinceMapping.setProvince(this.province.getValidatedValue());
			aProvinceMapping.setProvinceName(this.province.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Mapping Value
		try {
			aProvinceMapping.setMappingValue(this.mappingValue.getValue());
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
	 * @param provinceMapping The entity that need to be render.
	 */
	public void doShowDialog(ProvinceMapping provinceMapping) {
		logger.debug(Literal.LEAVING);

		if (provinceMapping.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.mappingType.focus();
		} else {
			this.mappingType.setReadonly(true);
			this.province.setReadonly(true);
			this.mappingValue.setReadonly(true);

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(provinceMapping.getRecordType())) {
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

		doWriteBeanToComponents(provinceMapping);
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
					Labels.getLabel("label_ProvinceMappingDialog_MappingType.value")));
		}
		if (!this.province.isReadonly()) {
			this.province
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProvinceMappingDialog_Province.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		if (!this.mappingValue.isReadonly()) {
			this.mappingValue.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ProvinceMappingDialog_MappingValue.value"),
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
		this.province.setConstraint("");
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
	 * Deletes a ProvinceMapping object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final ProvinceMapping aProvinceMapping = new ProvinceMapping();
		BeanUtils.copyProperties(this.provinceMapping, aProvinceMapping);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aProvinceMapping.getMappingType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aProvinceMapping.getRecordType()).equals("")) {
				aProvinceMapping.setVersion(aProvinceMapping.getVersion() + 1);
				aProvinceMapping.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aProvinceMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aProvinceMapping.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aProvinceMapping.getNextTaskId(),
							aProvinceMapping);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aProvinceMapping, tranType)) {
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

		if (this.provinceMapping.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.mappingType);
			readOnlyComponent(false, this.province);
			readOnlyComponent(false, this.mappingValue);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.mappingType);
			readOnlyComponent(true, this.province);
			readOnlyComponent(true, this.mappingValue);

		}

		this.mappingValue.setReadonly(isReadOnly("ProvinceMappingDialog_MappingValue"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.provinceMapping.isNewRecord()) {
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
		readOnlyComponent(true, this.province);
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
		this.province.setValue("");
		this.province.setDescription("");
		this.mappingValue.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final ProvinceMapping aProvinceMapping = new ProvinceMapping();
		BeanUtils.copyProperties(this.provinceMapping, aProvinceMapping);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aProvinceMapping);

		isNew = aProvinceMapping.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProvinceMapping.getRecordType())) {
				aProvinceMapping.setVersion(aProvinceMapping.getVersion() + 1);
				if (isNew) {
					aProvinceMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProvinceMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProvinceMapping.setNewRecord(true);
				}
			}
		} else {
			aProvinceMapping.setVersion(aProvinceMapping.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aProvinceMapping, tranType)) {
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
	protected boolean doProcess(ProvinceMapping aProvinceMapping, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProvinceMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProvinceMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProvinceMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProvinceMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProvinceMapping.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProvinceMapping);
				}

				if (isNotesMandatory(taskId, aProvinceMapping)) {
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

			aProvinceMapping.setTaskId(taskId);
			aProvinceMapping.setNextTaskId(nextTaskId);
			aProvinceMapping.setRoleCode(getRole());
			aProvinceMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProvinceMapping, tranType);
			String operationRefs = getServiceOperations(taskId, aProvinceMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProvinceMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProvinceMapping, tranType);
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
		ProvinceMapping aProvinceMapping = (ProvinceMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = provinceMappingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = provinceMappingService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = provinceMappingService.doApprove(auditHeader);

					if (aProvinceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = provinceMappingService.doReject(auditHeader);
					if (aProvinceMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ProvinceMappingDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ProvinceMappingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.provinceMapping), true);
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

	private AuditHeader getAuditHeader(ProvinceMapping aProvinceMapping, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProvinceMapping.getBefImage(), aProvinceMapping);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProvinceMapping.getUserDetails(),
				getOverideMap());
	}

	public void setProvinceMappingService(ProvinceMappingService provinceMappingService) {
		this.provinceMappingService = provinceMappingService;
	}

	public ProvinceMapping getProvinceMapping() {
		return provinceMapping;
	}

	public void setProvinceMapping(ProvinceMapping provinceMapping) {
		this.provinceMapping = provinceMapping;
	}

}
