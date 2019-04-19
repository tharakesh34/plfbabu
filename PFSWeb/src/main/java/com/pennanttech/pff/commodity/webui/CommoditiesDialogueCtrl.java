package com.pennanttech.pff.commodity.webui;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.SpringBeanUtil;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Search;
import com.pennanttech.pennapps.jdbc.search.SearchProcessor;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.commodity.model.CommodityType;
import com.pennanttech.pff.commodity.service.CommoditiesService;

public class CommoditiesDialogueCtrl extends GFCBaseCtrl<Commodity> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CommodityTypeDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CommoditiesDialogue;

	protected ExtendedCombobox type;
	protected Uppercasebox code;
	protected Textbox description;
	protected CurrencyBox currentValue;
	protected Uppercasebox hsnCode;
	protected Checkbox active;
	protected Commodity commodities;

	private transient CommoditiesListCtrl commoditiesListCtrl;
	private transient CommoditiesService commoditiesService;

	private CommodityType commodityTypeObject;

	/**
	 * default constructor.<br>
	 */
	public CommoditiesDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CommoditiesDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.commodities.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_CommoditiesDialogue(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_CommoditiesDialogue);

		try {
			this.commodities = (Commodity) arguments.get("commodity");
			this.commoditiesListCtrl = (CommoditiesListCtrl) arguments.get("commoditiesListCtrl");

			if (this.commodities == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			Commodity acommodities = new Commodity();
			BeanUtils.copyProperties(this.commodities, acommodities);
			this.commodities.setBefImage(acommodities);

			doLoadWorkFlow(this.commodities.isWorkflow(), this.commodities.getWorkflowId(),
					this.commodities.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CommoditiesDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.commodities);
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
		setStatusDetails();
		this.type.setMandatoryStyle(true);
		this.type.setModuleName("CommodityType");
		this.type.setValueColumn("Id");
		this.type.setDescColumn("Description");
		this.type.setValueType(DataType.LONG);
		this.type.setValidateColumns(new String[] { "Id", "Code", "Description" });
		logger.debug(Literal.LEAVING);

		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());

		this.currentValue.setProperties(false, formatter);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CommoditiesDialog_btnSave"));
		this.btnCancel.setVisible(false);

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
		doShowNotes(this.commodities);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		commoditiesListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.commodities);
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param covenantType
	 * 
	 */
	public void doWriteBeanToComponents(Commodity acommodityType) {
		logger.debug(Literal.ENTERING);

		this.type.setValue(acommodityType.getCommodityTypeCode());
		this.type.setDescription(acommodityType.getDescription());
		CommodityType commodity = new CommodityType();
		commodity.setCode(acommodityType.getCode());
		commodity.setDescription(acommodityType.getDescription());
		commodity.setId(acommodityType.getCommodityType());
		this.type.setObject(commodity);
		onChangeCommodityType();

		this.code.setText(acommodityType.getCode());
		this.hsnCode.setText(acommodityType.getHSNCode());
		this.currentValue.setValue(PennantApplicationUtil.formateAmount(acommodityType.getCurrentValue(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));

		this.description.setText(acommodityType.getDescription());
		this.active.setChecked(acommodityType.isActive());

		this.recordStatus.setValue(acommodityType.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(Commodity acommodities) {
		logger.debug(Literal.LEAVING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			this.type.getValidatedValue();
			Object obj = this.type.getObject();
			if (obj != null) {
				acommodities.setCommodityType(((CommodityType) obj).getId());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodities.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodities.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodities.setCurrentValue(PennantApplicationUtil.unFormateAmount(this.currentValue.getActualValue(),
					CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodities.setHSNCode(this.hsnCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			acommodities.setActive(this.active.isChecked());
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param covenantType
	 *            The entity that need to be render.
	 */
	public void doShowDialog(Commodity commodities) {
		logger.debug(Literal.LEAVING);

		doWriteBeanToComponents(commodities);

		if (commodities.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.type.setFocus(true);
		} else {
			this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(commodities.getRecordType())) {
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

		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.type.getButton().isDisabled()) {
			this.type.setConstraint(
					new PTStringValidator(Labels.getLabel("label_StockCompanyDialogue_CompanyCode.value"), null, true));
		}

		if (!this.code.isReadonly()) {
			this.code.setConstraint(
					new PTStringValidator(Labels.getLabel("label_CommoditiesDialogue_CommodityCode.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
		}

		if (!this.code.isReadonly()) {
			if (this.code.getValue().length() > 8) {
				throw new WrongValueException(this.code,
						Labels.getLabel("label_CommoditiesDialogue_CommodityTypeAlert.value"));
			}
		}

		if (!this.currentValue.isReadonly()) {
			this.currentValue.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_CommoditiesDialogue_CurrentValue.value"), 2, false, false));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.type.setConstraint("");
		this.description.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

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
	 * Deletes a CovenantType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.LEAVING);

		final Commodity acommodities = new Commodity();
		BeanUtils.copyProperties(this.commodities, acommodities);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ acommodities.getCommodityType();
		if (MessageUtil.confirm(msg) != MessageUtil.YES) {
			return;
		}

		if (StringUtils.trimToEmpty(acommodities.getRecordType()).equals("")) {
			acommodities.setVersion(acommodities.getVersion() + 1);
			acommodities.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (isWorkFlowEnabled()) {
				acommodities.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				acommodities.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), acommodities.getNextTaskId(), acommodities);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (doProcess(acommodities, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (DataAccessException e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.commodities.isNewRecord()) {
			this.type.setButtonDisabled(false);
			this.code.setDisabled(false);
		} else {
			this.type.setButtonDisabled(true);
			this.code.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("Commodities_Description"), this.description);
		readOnlyComponent(isReadOnly("Commodities_CurrentValue"), this.currentValue);
		readOnlyComponent(isReadOnly("Commodities_HSNCode"), this.hsnCode);
		readOnlyComponent(isReadOnly("Commodities_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.commodities.isNewRecord()) {
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

		readOnlyComponent(true, this.type);
		readOnlyComponent(true, this.description);
		readOnlyComponent(true, this.active);

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
		this.type.setValue("");
		this.description.setValue("");
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final Commodity acommodities = new Commodity();
		BeanUtils.copyProperties(this.commodities, acommodities);

		doSetValidation();
		doWriteComponentsToBean(acommodities);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(acommodities.getRecordType())) {
				acommodities.setVersion(acommodities.getVersion() + 1);
				if (acommodities.isNew()) {
					acommodities.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					acommodities.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					acommodities.setNewRecord(true);
				}
			}
		} else {
			acommodities.setVersion(acommodities.getVersion() + 1);
			if (acommodities.isNew()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(acommodities, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
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
	private boolean doProcess(Commodity acommodityType, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		acommodityType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		acommodityType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		acommodityType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			acommodityType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(acommodityType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, acommodityType);
				}

				if (isNotesMandatory(taskId, acommodityType)) {
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

			acommodityType.setTaskId(taskId);
			acommodityType.setNextTaskId(nextTaskId);
			acommodityType.setRoleCode(getRole());
			acommodityType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(acommodityType, tranType);
			String operationRefs = getServiceOperations(taskId, acommodityType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(acommodityType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(acommodityType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		Commodity aCovenantType = (Commodity) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = commoditiesService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = commoditiesService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = commoditiesService.doApprove(auditHeader);

						if (aCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = commoditiesService.doReject(auditHeader);
						if (aCovenantType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CommoditiesDialogue, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CommoditiesDialogue, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.commodities), true);
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

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(CommodityType aStockCompany, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStockCompany.getBefImage(), aStockCompany);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aStockCompany.getUserDetails(),
				getOverideMap());
	}

	public void setCommoditiesService(CommoditiesService commoditiesService) {
		this.commoditiesService = commoditiesService;
	}

	public void onFulfill$type(Event event) {
		onChangeCommodityType();
	}

	public void onChangeCommodityType() {
		if (StringUtils.isBlank(this.type.getValue())) {
			clearOnChangeCommodityData();
			return;
		}

		commodityTypeObject = (CommodityType) this.type.getObject();
		if (commodityTypeObject == null) {
			return;
		}

		Search search = new Search(CommodityType.class);
		search.addFilterEqual("Id", commodityTypeObject.getId());

		SearchProcessor searchProcessor = (SearchProcessor) SpringBeanUtil.getBean("searchProcessor");
		commodityTypeObject = (CommodityType) searchProcessor.getResults(search).get(0);

		clearOnChangeCommodityData();

		commodityTypeDetails(commodityTypeObject);
	}

	public void clearOnChangeCommodityData() {
		this.type.setValue("");
		this.description.setText("");
		this.active.setChecked(false);
	}

	public void commodityTypeDetails(CommodityType commodities) {
		this.type.setValue(commodities.getCode());
		this.type.setDescription(commodities.getDescription());
		this.description.setText(commodities.getDescription());
		this.active.setChecked(commodities.isActive());
	}

}
