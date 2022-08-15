package com.pennant.webui.amtmasters.dealermapping;

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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennant.backend.service.dealermapping.DealerMappingService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.mmfl.cd.model.MerchantDetails;

public class DealerMappingDialogCtrl extends GFCBaseCtrl<DealerMapping> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DealerMappingDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DealerMappingDialog;

	protected ExtendedCombobox merchantName;
	protected ExtendedCombobox storeName;
	protected Textbox storeAddress;
	protected Textbox storeCity;
	protected Textbox storeId;
	protected Textbox dealerCode;
	protected Textbox posId;
	protected Checkbox active;

	private transient DealerMappingListCtrl dealerMappingListCtrl;
	private transient DealerMappingService dealerMappingService;
	private DealerMapping dealerMapping;

	public DealerMappingDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		// super.pageRightName = "DealerMappingDialog";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.dealerMapping.getId()));
		return referenceBuffer.toString();
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_DealerMappingDialog(Event event) {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_DealerMappingDialog);

		try {
			this.dealerMapping = (DealerMapping) arguments.get("dealerMapping");
			this.dealerMappingListCtrl = (DealerMappingListCtrl) arguments.get("dealerMappingListCtrl");

			if (this.dealerMapping == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			DealerMapping dealerMapping = new DealerMapping();
			BeanUtils.copyProperties(this.dealerMapping, dealerMapping);
			this.dealerMapping.setBefImage(dealerMapping);

			doLoadWorkFlow(this.dealerMapping.isWorkflow(), this.dealerMapping.getWorkflowId(),
					this.dealerMapping.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "DealerMappingDialog");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.dealerMapping);

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

		this.merchantName.setMandatoryStyle(true);
		this.merchantName.setModuleName("MerchantDetails");
		this.merchantName.setValueColumn("MerchantId");
		this.merchantName.setDescColumn("MerchantName");

		this.storeName.setMandatoryStyle(true);

		setStatusDetails();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DealerMappingDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DealerMappingDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DealerMappingDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DealerMappingDialog_btnSave"));
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
		doShowNotes(this.dealerMapping);
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$merchantName(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = merchantName.getObject();
		String merchant = null;
		if (dataObject instanceof String) {
			merchantName.setValue("", "");
		} else {
			MerchantDetails details = (MerchantDetails) dataObject;
			if (details == null) {
				merchantName.setValue("", "");
			}
			if (details != null) {
				this.merchantName.setErrorMessage("");
				merchant = this.merchantName.getValue();
			}
		}

		this.storeName.setValue("");
		this.storeName.setDescription("");

		if (merchant != null) {
			onfulfillMerchantName();
		}

		logger.debug(Literal.LEAVING);

	}

	public void onFulfill$storeName(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = storeName.getObject();
		if (dataObject instanceof String) {
			this.storeName.setValue("");
			this.storeName.setDescription("");
			this.storeAddress.setValue("");
			this.storeCity.setValue("");
			this.storeId.setValue("");
			this.dealerCode.setValue("");
			this.posId.setValue("");

		} else {
			MerchantDetails merchantDetails = (MerchantDetails) dataObject;
			if (merchantDetails != null) {
				this.storeName.setValue(merchantDetails.getStoreName());
				this.storeName.setDescription(String.valueOf(merchantDetails.getStoreId()));
				this.storeName.setErrorMessage("");

				storeCity.setText(merchantDetails.getStoreCity());
				storeAddress.setText(merchantDetails.getStoreAddressLine1());
				storeId.setText(String.valueOf(merchantDetails.getStoreId()));
				posId.setText(String.valueOf(merchantDetails.getPOSId()));
			}
		}

		logger.debug("Leaving");
	}

	private void onfulfillMerchantName() {
		this.storeName.setMandatoryStyle(true);
		this.storeName.setModuleName("Stores");
		this.storeName.setValueColumn("StoreName");
		this.storeName.setDescColumn("StoreId");

		Filter[] filters = new Filter[1];
		String merchantId = this.merchantName.getTextbox().getValue();
		filters[0] = Filter.in("MerchantId", Long.valueOf(merchantId.equals("") ? "0" : merchantId));
		this.storeName.setFilters(filters);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		dealerMappingListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.dealerMapping.getBefImage());
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
	public void doWriteBeanToComponents(DealerMapping dealerMapping) {
		logger.debug(Literal.ENTERING);

		if (dealerMapping.getMerchantId() == 0) {
			this.merchantName.setDescription("");
			this.merchantName.setValue("");
		} else {
			this.merchantName.setDescription(String.valueOf(dealerMapping.getMerchantName()));
			this.merchantName.setValue(String.valueOf(dealerMapping.getMerchantId()));

		}
		onfulfillMerchantName();
		this.storeName.setValue(dealerMapping.getStoreName());
		if (dealerMapping.getStoreId() > 0) {
			this.storeName.setDescription(String.valueOf(dealerMapping.getStoreId()));
		} else {
			this.storeName.setDescription("");
		}
		this.storeCity.setText(dealerMapping.getStoreCity());
		this.storeAddress.setText(dealerMapping.getStoreAddress());

		if (dealerMapping.getDealerCode() == 0) {
			this.dealerCode.setText("");
		} else {
			this.dealerCode.setText(String.valueOf(dealerMapping.getDealerCode()));
		}
		this.storeId.setText(String.valueOf(dealerMapping.getStoreId()));
		this.posId.setText(String.valueOf(dealerMapping.getPosId()));
		this.active.setChecked(dealerMapping.isActive());

		if (dealerMapping.isNewRecord() || (dealerMapping.getRecordType() != null ? dealerMapping.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.recordStatus.setValue(dealerMapping.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(DealerMapping aDealerMapping) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			aDealerMapping.setMerchantId(Long.valueOf(this.merchantName.getTextbox().getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDealerMapping.setStoreId(Long.valueOf(this.storeId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.dealerCode.getText() != null && !this.dealerCode.getText().equals("")) {
				aDealerMapping.setDealerCode(Long.valueOf(this.dealerCode.getText()));
			} else {
				aDealerMapping.setDealerCode(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.posId.getText() != null && !this.posId.getText().equals("")) {
				aDealerMapping.setPosId(Long.valueOf(this.posId.getText()));
			} else {
				aDealerMapping.setDealerCode(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aDealerMapping.setActive(this.active.isChecked());
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
	 * @param covenantType The entity that need to be render.
	 */
	public void doShowDialog(DealerMapping dealerMapping) {
		logger.debug(Literal.ENTERING);

		if (dealerMapping.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.merchantName.setFocus(true);
		} else {
			// this.description.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(dealerMapping.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(dealerMapping);

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
		logger.debug(Literal.ENTERING);

		if (!this.merchantName.isReadonly()) {
			this.merchantName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DealerMappingDialog_MerchantName.value"),
							PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
		}

		if (!this.storeName.isReadonly()) {
			this.storeName
					.setConstraint(new PTStringValidator(Labels.getLabel("label_DealerMappingDialog_StoreName.value"),
							PennantRegularExpressions.REGEX_ACC_HOLDER_NAME, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.merchantName.setConstraint("");
		this.storeName.setConstraint("");
		this.storeAddress.setConstraint("");
		this.storeCity.setConstraint("");
		this.storeId.setConstraint("");
		this.posId.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.ENTERING);

		final DealerMapping aDealerMapping = new DealerMapping();
		BeanUtils.copyProperties(this.dealerMapping, aDealerMapping);

		doDelete(String.valueOf(aDealerMapping.getMerchantId()), aDealerMapping);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (this.dealerMapping.isNewRecord()) {
			this.merchantName.setReadonly(false);
		} else {
			this.merchantName.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("DealerMappingDialog_MerchantName"), this.merchantName);
		readOnlyComponent(isReadOnly("DealerMappingDialog_StoreName"), this.storeName);
		readOnlyComponent(isReadOnly("DealerMappingDialog_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.dealerMapping.isNewRecord()) {
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

		readOnlyComponent(true, this.merchantName);
		readOnlyComponent(true, this.storeName);
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
		this.merchantName.setValue("");
		this.storeName.setValue("");
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final DealerMapping dealerMapping = new DealerMapping();
		BeanUtils.copyProperties(this.dealerMapping, dealerMapping);

		doSetValidation();
		doWriteComponentsToBean(dealerMapping);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(dealerMapping.getRecordType())) {
				dealerMapping.setVersion(dealerMapping.getVersion() + 1);
				if (dealerMapping.isNewRecord()) {
					dealerMapping.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					dealerMapping.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					dealerMapping.setNewRecord(true);
				}
			}
		} else {
			dealerMapping.setVersion(dealerMapping.getVersion() + 1);
			if (dealerMapping.isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(dealerMapping, tranType)) {
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
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(DealerMapping dealerMapping, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		dealerMapping.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		dealerMapping.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		dealerMapping.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			dealerMapping.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(dealerMapping.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, dealerMapping);
				}
				if (isNotesMandatory(taskId, dealerMapping)) {
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
			dealerMapping.setTaskId(taskId);
			dealerMapping.setNextTaskId(nextTaskId);
			dealerMapping.setRoleCode(getRole());
			dealerMapping.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(dealerMapping, tranType);
			String operationRefs = getServiceOperations(taskId, dealerMapping);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(dealerMapping, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(dealerMapping, tranType);
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
	 * 
	 */

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		DealerMapping aDealerMapping = (DealerMapping) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = dealerMappingService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = dealerMappingService.saveOrUpdate(auditHeader);
				}
			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = dealerMappingService.doApprove(auditHeader);

					if (aDealerMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}
				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = dealerMappingService.doReject(auditHeader);
					if (aDealerMapping.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}
				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DealerMappingDialog, auditHeader);
					return processCompleted;
				}
			}
			auditHeader = ErrorControl.showErrorDetails(this.window_DealerMappingDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;
				if (deleteNotes) {
					deleteNotes(getNotes(this.dealerMapping), true);
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

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(DealerMapping aStockCompany, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aStockCompany.getBefImage(), aStockCompany);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aStockCompany.getUserDetails(),
				getOverideMap());
	}

	public DealerMapping getDealerMapping() {
		return dealerMapping;
	}

	public void setDealerMapping(DealerMapping dealerMapping) {
		this.dealerMapping = dealerMapping;
	}

	public void setDealerMappingService(DealerMappingService dealerMappingService) {
		this.dealerMappingService = dealerMappingService;
	}

}
