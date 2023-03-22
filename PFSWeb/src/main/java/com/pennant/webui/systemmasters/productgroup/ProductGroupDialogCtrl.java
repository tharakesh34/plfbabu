package com.pennant.webui.systemmasters.productgroup;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennant.backend.service.systemmasters.ProductGroupService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class ProductGroupDialogCtrl extends GFCBaseCtrl<ProductGroup> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(ProductGroupDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ProductGroupDialog;
	protected Button btnModelId;
	protected Textbox modelId;
	protected ExtendedCombobox productCategoryId;
	protected Button btnchannels;
	protected Textbox txtchannel;
	protected Checkbox active;

	private ProductGroup productGroup;
	private transient ProductGroupListCtrl productGroupListCtrl;
	private transient ProductGroupService productGroupService;

	/**
	 * default constructor.<br>
	 */
	public ProductGroupDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProductGroupDialog";
	}

	public void onCreate$window_ProductGroupDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ProductGroupDialog);

		try {
			// Get the required arguments.
			this.productGroup = (ProductGroup) arguments.get("productGroup");
			this.productGroupListCtrl = (ProductGroupListCtrl) arguments.get("productGroupListCtrl");

			if (this.productGroup == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ProductGroup productGroup = new ProductGroup();
			BeanUtils.copyProperties(this.productGroup, productGroup);
			this.productGroup.setBefImage(productGroup);

			// Render the page and display the data.
			doLoadWorkFlow(this.productGroup.isWorkflow(), this.productGroup.getWorkflowId(),
					this.productGroup.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ProductGroupDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.productGroup);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param builderCompany The entity that need to be render.
	 */
	public void doShowDialog(ProductGroup productGroup) {
		logger.debug(Literal.LEAVING);

		if (productGroup.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.modelId.focus();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(productGroup.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.modelId.focus();
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(productGroup);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProductGroupDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProductGroupDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProductGroupDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProductGroupDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

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

	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		this.txtchannel.setMaxlength(20);

		this.productCategoryId.setMandatoryStyle(true);
		this.productCategoryId.setModuleName("ProductCategory");

		this.modelId.setMaxlength(8);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$productCategoryId(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = productCategoryId.getObject();
		if (dataObject instanceof String) {
			this.productCategoryId.setObject(null);
			this.productCategoryId.setValue("", "");
		} else {
			if (dataObject instanceof LovFieldDetail) {
				LovFieldDetail lovFieldDetail = (LovFieldDetail) dataObject;
				this.productCategoryId.setObject(lovFieldDetail);
				this.productCategoryId.setValue(String.valueOf(lovFieldDetail.getFieldCodeId()),
						lovFieldDetail.getValueDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$btnchannels(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_ProductGroupDialog, "ChannelTypes",
				String.valueOf(this.txtchannel.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.txtchannel.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onClick$btnModelId(Event event) {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_ProductGroupDialog, "ConsumerProduct",
				String.valueOf(this.modelId.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.modelId.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

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
	 * Writes the bean data to the components.<br>
	 * 
	 * @param builderGroup
	 * 
	 */
	public void doWriteBeanToComponents(ProductGroup aProductGroup) {
		logger.debug(Literal.ENTERING);

		this.modelId.setValue(aProductGroup.getModelId());
		this.txtchannel.setValue(aProductGroup.getChannel());
		this.active.setChecked(aProductGroup.isActive());
		if (productGroup.isNewRecord() || (productGroup.getRecordType() != null ? productGroup.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		if (aProductGroup.getProductCategoryId() != 0) {
			LovFieldDetail lovFieldDetail = new LovFieldDetail();
			lovFieldDetail.setFieldCodeId(Long.valueOf(aProductGroup.getProductCategoryId()));
			this.productCategoryId.setObject(lovFieldDetail);
			this.productCategoryId.setValue(String.valueOf(lovFieldDetail.getFieldCodeId()),
					lovFieldDetail.getValueDesc());
		}
		this.recordStatus.setValue(aProductGroup.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aBuilderGroup
	 */
	public void doWriteComponentsToBean(ProductGroup aProductGroup) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			aProductGroup.setModelId(this.modelId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Chaneel
		try {
			aProductGroup.setChannel(this.txtchannel.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// dealer category
		try {
			this.productCategoryId.getValidatedValue();
			aProductGroup.setProductCategoryId(Integer.parseInt(this.productCategoryId.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// cative
		try {
			aProductGroup.setActive(this.active.isChecked());
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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.btnModelId.isDisabled()) {
			this.modelId
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductGroupDialog_modelCode.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.productCategoryId.isReadonly()) {
			this.productCategoryId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_ProductGroupDialog_productCategory.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.modelId.setConstraint("");

		this.productCategoryId.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		// id
		// Name
		// Segmentation

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
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final ProductGroup aProductGroup = new ProductGroup();
		BeanUtils.copyProperties(this.productGroup, aProductGroup);

		doDelete(String.valueOf(aProductGroup.getModelId()), aProductGroup);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		productGroupListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (this.productGroup.isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.btnModelId);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.btnModelId);
		}

		readOnlyComponent(isReadOnly("ProductGroupDialog_ProductCategoryId"), this.productCategoryId);
		readOnlyComponent(isReadOnly("ProductGroupDialog_Channel"), this.btnchannels);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.productGroup.isNewRecord()) {
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

		readOnlyComponent(true, this.modelId);
		readOnlyComponent(true, this.btnchannels);

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
		this.modelId.setValue("");
		this.productCategoryId.setValue("");
		this.txtchannel.setValue("");
		// this.channel.setDescription("");

		this.active.setValue("");

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final ProductGroup aProductGroup = new ProductGroup();
		BeanUtils.copyProperties(this.productGroup, aProductGroup);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aProductGroup);

		isNew = aProductGroup.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aProductGroup.getRecordType())) {
				aProductGroup.setVersion(aProductGroup.getVersion() + 1);
				if (isNew) {
					aProductGroup.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aProductGroup.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aProductGroup.setNewRecord(true);
				}
			}
		} else {
			aProductGroup.setVersion(aProductGroup.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aProductGroup, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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
	protected boolean doProcess(ProductGroup aProductGroup, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aProductGroup.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aProductGroup.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aProductGroup.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aProductGroup.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aProductGroup.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aProductGroup);
				}

				if (isNotesMandatory(taskId, aProductGroup)) {
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

			aProductGroup.setTaskId(taskId);
			aProductGroup.setNextTaskId(nextTaskId);
			aProductGroup.setRoleCode(getRole());
			aProductGroup.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aProductGroup, tranType);
			String operationRefs = getServiceOperations(taskId, aProductGroup);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aProductGroup, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aProductGroup, tranType);
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
		ProductGroup aProductGroup = (ProductGroup) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = productGroupService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = productGroupService.saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = productGroupService.doApprove(auditHeader);

					if (aProductGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = productGroupService.doReject(auditHeader);
					if (aProductGroup.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ProductGroupDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ProductGroupDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.productGroup), true);
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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.productGroup);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ProductGroup aProductGroup, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aProductGroup.getBefImage(), aProductGroup);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aProductGroup.getUserDetails(),
				getOverideMap());
	}

	public ProductGroupService getProductGroupService() {
		return productGroupService;
	}

	public void setProductGroupService(ProductGroupService productGroupService) {
		this.productGroupService = productGroupService;
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.productGroup.getProductGroupId()));
		return referenceBuffer.toString();
	}

}
