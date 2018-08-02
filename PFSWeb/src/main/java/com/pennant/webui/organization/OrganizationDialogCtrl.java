package com.pennant.webui.organization;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.dataengine.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.organization.OrganizationType;
import com.pennanttech.pff.organization.model.Organization;
import com.pennanttech.pff.organization.service.OrganizationService;

public class OrganizationDialogCtrl extends GFCBaseCtrl<Organization> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(OrganizationDialogCtrl.class);

	protected Window window_OrganizationDialog;
	protected Tab organizationDetails;
	protected ExtendedCombobox cif;
	protected Textbox name;
	protected Uppercasebox code;
	protected Datebox dateOfInc;

	protected Tabpanel observationsFieldTabPanel;
	private String module = "";
	private Organization organization;
	private transient OrganizationListCtrl organizationListCtrl;
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	
	@Autowired
	private OrganizationService organizationService;

	public OrganizationDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "OrganizationSchoolDialog";
	}

	public void onCreate$window_OrganizationDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_OrganizationDialog);

		try {
			// Get the required arguments.
			this.organization = (Organization) arguments.get("organization");

			if (arguments.get("organizationListCtrl") != null) {
				this.organizationListCtrl = (OrganizationListCtrl) arguments.get("organizationListCtrl");
			}

			if (arguments.get("enqiryModule") != null) {
				enqiryModule = (boolean) arguments.get("enqiryModule");
			}
			
			if (arguments.get("module") != null) {
				module = (String) arguments.get("module");
			}

			if (this.organization == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			Organization organization = new Organization();
			BeanUtils.copyProperties(this.organization, organization);
			this.organization.setBefImage(organization);

			// Render the page and display the data.
			doLoadWorkFlow(this.organization.isWorkflow(), this.organization.getWorkflowId(),
					this.organization.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.organization);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.cif.setMaxlength(8);
		this.cif.setMandatoryStyle(true);
		this.cif.setModuleName("Customer");
		this.cif.setValueColumn("CustCIF");
		this.cif.setDescColumn("CustShrtName");
		this.cif.setValidateColumns(new String[] { "CustCIF" });
		
		this.code.setMaxlength(15);
		this.name.setMaxlength(50);
		this.dateOfInc.setFormat(DateFormat.SHORT_DATE.getPattern());
		
		logger.debug(Literal.LEAVING);
	}

	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_OrganizationSchoolList_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_OrganizationSchoolDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_OrganizationSchoolDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_OrganizationSchoolDialog_btnSave"));

		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void doShowDialog(Organization organization) {
		logger.debug(Literal.ENTERING);

		if (organization.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(organization.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
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
			this.south.setVisible(false);
		}

		doWriteBeanToComponents(organization);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.organization.isNewRecord()) {
			this.btnCancel.setVisible(false);
		} else {
			this.btnCancel.setVisible(true);
			this.cif.setReadonly(true);
			this.name.setReadonly(true);
			this.code.setReadonly(true);
			}

		/*readOnlyComponent(isReadOnly("OrganizationSchoolDialog_CIF"), this.cif);
		readOnlyComponent(isReadOnly("OrganizationSchoolDialog_Name"), this.name);
		readOnlyComponent(isReadOnly("OrganizationSchoolDialog_Code"), this.dateOfInc);*/
		readOnlyComponent(isReadOnly("OrganizationSchoolDialog_DateOfIncorporation"), this.dateOfInc);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.organization.isNewRecord()) {
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

	public void doReadOnly() {
		logger.debug(Literal.ENTERING);

		this.cif.setReadonly(true);
		this.name.setReadonly(true);
		this.dateOfInc.setReadonly(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.recordStatus.setValue("");
			if (!enqiryModule) {
				this.userAction.setSelectedIndex(0);
			}

		}
		logger.debug(Literal.LEAVING);
	}

	public void doWriteBeanToComponents(Organization org) {
		logger.debug(Literal.ENTERING);

		if (!org.isNewRecord()) {
			this.cif.setValue(StringUtils.trimToEmpty(org.getCif()),StringUtils.trimToEmpty(org.getCustShrtName()));
			if (org.getCif() != null) {
				this.cif.setAttribute("CustId", org.getCustId());
			} else {
				this.cif.setAttribute("CustId", null);
			}
		}

		this.name.setValue(org.getName());
		this.code.setValue(org.getCode());
		this.dateOfInc.setValue(org.getDate_Incorporation());
		this.recordStatus.setValue(org.getRecordStatus());

		// Organization details
		appendOrganizationFieldDetails(org);

		logger.debug(Literal.LEAVING);
	}

	private void appendOrganizationFieldDetails(Organization org) {
		logger.debug(Literal.ENTERING);
		try {
			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = extendedFieldCtrl
					.getExtendedFieldHeader(ExtendedFieldConstants.MODULE_ORGANIZATION, module);

			if (extendedFieldHeader == null) {
				return;
			}
			// Extended Field Details
			StringBuilder tableName = new StringBuilder();
			tableName.append(extendedFieldHeader.getModuleName());
			tableName.append("_");
			tableName.append(extendedFieldHeader.getSubModuleName());
			tableName.append("_ED");

			List<ExtendedFieldDetail> detailsList = extendedFieldHeader.getExtendedFieldDetails();
			int fieldSize = 0;
			if (detailsList != null && !detailsList.isEmpty()) {
				fieldSize = detailsList.size();
				if (fieldSize != 0) {
					fieldSize = fieldSize / 2;
					fieldSize = fieldSize + 1;
				}
			}

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(String.valueOf(org.getOrganizationId()), tableName.toString(), "_View");
			extendedFieldCtrl.setTabpanel(observationsFieldTabPanel);
			extendedFieldCtrl.setTab(this.organizationDetails);
			org.setExtendedFieldHeader(extendedFieldHeader);
			org.setExtendedFieldRender(extendedFieldRender);

			if (org.getBefImage() != null) {
				org.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				org.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}
			extendedFieldCtrl.setCcyFormat(2);
			extendedFieldCtrl.setReadOnly(isReadOnly("OrganizationSchoolDialog_OrganizationSchoolExtFields"));
			extendedFieldCtrl.setWindow(this.window_OrganizationDialog);
			extendedFieldCtrl.render();
			this.organizationDetails
					.setLabel(Labels.getLabel("label_OrganizationDialog_OrganizationDetails.value"));
			this.observationsFieldTabPanel.setHeight((fieldSize * 37) + "px");
		} catch (Exception e) {
			closeDialog();
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}
	
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		if (!this.cif.isReadonly()) {
			this.cif.setConstraint(new PTStringValidator(
					Labels.getLabel("label_OrganizationDialog_CIF.value"), null, true, true));
		}
		if (!this.name.isReadonly()) {
			this.name.setConstraint(
					new PTStringValidator(Labels.getLabel("label_OrganizationDialog_Name.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		}
		
		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_OrganizationDialog_Code.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHANUM, true));
		}
		
		if (this.dateOfInc.isVisible() && !this.dateOfInc.isDisabled()) {
		this.dateOfInc.setConstraint(
					new PTDateValidator(Labels.getLabel("label_OrganizationDialog_DateOfInc.value"), true, null,
							DateUtil.getDatePart(DateUtility.getAppDate()), true));
	}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.cif.setConstraint("");
		this.name.setConstraint("");
		this.dateOfInc.setConstraint("");
		this.code.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	
	public void doWriteComponentsToBean(Organization org) {
		logger.debug(Literal.ENTERING);

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			org.setCif(this.cif.getValue());
			org.setCustShrtName(this.cif.getDescription());
			this.cif.getValidatedValue();
			Object object = this.cif.getAttribute("CustId");
			if (object != null) {
				org.setCustId(Long.parseLong(object.toString()));
			} else {
				org.setCustId(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			org.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (OrganizationType.SCHOOL.getValue().equalsIgnoreCase(module)) {
				org.setType(OrganizationType.SCHOOL.getKey());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			org.setName(this.name.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			org.setDate_Incorporation(this.dateOfInc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			org.setCreatedBy(getUserWorkspace().getLoggedInUser().getUserId());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			org.setCreatedOn(DateUtility.getAppDate());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		
		if (!wve.isEmpty()) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		// Extended Field validations
		if (org.getExtendedFieldHeader() != null) {
			try {
				org.setExtendedFieldRender(extendedFieldCtrl.save(true));
			} catch (ParseException e) {
				logger.debug(Literal.EXCEPTION);
			}
		}

		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING);
		doSave();
		logger.debug(Literal.LEAVING);

	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(Literal.ENTERING);
		doEdit();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		logger.debug(Literal.ENTERING);
		MessageUtil.showHelpWindow(event, super.window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		doDelete();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(Literal.ENTERING);
		doCancel();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.organization);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		organizationListCtrl.search();
		logger.debug(Literal.LEAVING);
	}
	
	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.organization.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$cif(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = cif.getObject();
		if (dataObject instanceof String || dataObject == null) {
			this.cif.setValue("");
			this.cif.setDescription("");
			this.cif.setAttribute("CustId", null);
		} else {
			Customer details = (Customer) dataObject;
				this.cif.setAttribute("CustId", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}
	
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Organization org = new Organization();
		BeanUtils.copyProperties(this.organization, org);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(org);

		isNew = org.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(org.getRecordType())) {
				org.setVersion(org.getVersion() + 1);
				if (isNew) {
					org.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					org.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					org.setNewRecord(true);
				}
			}
		} else {
			org.setVersion(org.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(org, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}
	
	private boolean doProcess(Organization organization, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		organization.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		organization.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		organization.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			organization.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(organization.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, organization);
				}

				if (isNotesMandatory(taskId, organization)) {
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

			organization.setTaskId(taskId);
			organization.setNextTaskId(nextTaskId);
			organization.setRoleCode(getRole());
			organization.setNextRoleCode(nextRoleCode);

			// Extended Field details
			if (organization.getExtendedFieldRender() != null) {
				int seqNo = 0;
				ExtendedFieldRender details = organization.getExtendedFieldRender();
				details.setReference(String.valueOf(organization.getOrganizationId()));
				details.setSeqNo(++seqNo);
				details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(organization.getRecordStatus());
				details.setRecordType(organization.getRecordType());
				details.setVersion(organization.getVersion());
				details.setWorkflowId(organization.getWorkflowId());
				details.setTaskId(taskId);
				details.setNextTaskId(nextTaskId);
				details.setRoleCode(getRole());
				details.setNextRoleCode(nextRoleCode);
				details.setNewRecord(organization.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(organization.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(organization.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			auditHeader = getAuditHeader(organization, tranType);
			String operationRefs = getServiceOperations(taskId, organization);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(organization, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(organization, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private AuditHeader getAuditHeader(Organization organization, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, organization.getBefImage(), organization);
		return new AuditHeader(getReference(), null, null, null, auditDetail, organization.getUserDetails(),
				getOverideMap());
	}
	
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Organization organization = (Organization) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = organizationService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = organizationService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = organizationService.doApprove(auditHeader);

						if (organization.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = organizationService.doReject(auditHeader);
						if (organization.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_OrganizationDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_OrganizationDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.organization), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}
	
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final Organization entity = new Organization();
		BeanUtils.copyProperties(this.organization, entity);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ entity.getId();
		if (MessageUtil.YES == MessageUtil.confirm(msg)) {
			if (("").equals(StringUtils.trimToEmpty(entity.getRecordType()))) {
				entity.setVersion(entity.getVersion() + 1);
				entity.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					entity.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					entity.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), entity.getNextTaskId(), entity);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(entity, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.organization.getId());
	}
}
