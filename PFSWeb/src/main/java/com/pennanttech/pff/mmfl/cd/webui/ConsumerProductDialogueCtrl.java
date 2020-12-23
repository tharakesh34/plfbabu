package com.pennanttech.pff.mmfl.cd.webui;

import java.math.BigDecimal;
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
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.mmfl.cd.model.ConsumerProduct;
import com.pennanttech.pff.mmfl.cd.service.ConsumerProductService;

public class ConsumerProductDialogueCtrl extends GFCBaseCtrl<ConsumerProduct> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(ConsumerProductDialogueCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_consumerProductDialogue;

	protected Uppercasebox modelId;
	protected Textbox modelDescription;
	protected Textbox txtNames;
	protected Button btnNames;
	protected Textbox assetDescription;
	protected CurrencyBox minAmount;
	protected CurrencyBox maxAmount;
	protected Textbox modelStatus;
	protected Button btnchannels;
	protected Textbox txtchannel;
	protected Checkbox active;
	protected ConsumerProduct consumerProduct;

	private transient ConsumerProductListCtrl consumerProductListCtrl;
	private transient ConsumerProductService consumerProductService;

	public ConsumerProductDialogueCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ProductDialogue";
	}

	@Override
	protected String getReference() {
		StringBuilder referenceBuffer = new StringBuilder(String.valueOf(this.consumerProduct.getProductId()));
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
	public void onCreate$window_consumerProductDialogue(Event event) throws AppException {
		logger.debug(Literal.ENTERING);

		setPageComponents(window_consumerProductDialogue);

		try {
			this.consumerProduct = (ConsumerProduct) arguments.get("ConsumerProduct");
			this.consumerProductListCtrl = (ConsumerProductListCtrl) arguments.get("consumerProductListCtrl");

			if (this.consumerProduct == null) {
				throw new AppException(Labels.getLabel("error.unhandled"));
			}

			ConsumerProduct consumerProduct = new ConsumerProduct();
			BeanUtils.copyProperties(this.consumerProduct, consumerProduct);
			this.consumerProduct.setBefImage(consumerProduct);

			doLoadWorkFlow(this.consumerProduct.isWorkflow(), this.consumerProduct.getWorkflowId(),
					this.consumerProduct.getNextTaskId());

			if (isWorkFlowEnabled()) {
				if (!enqiryModule) {
					this.userAction = setListRecordStatus(this.userAction);
				}
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ProductDialogue");
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.consumerProduct);

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

		this.txtchannel.setMaxlength(20);
		this.modelId.setMaxlength(20);

		int formatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
		this.minAmount.setProperties(true, formatter);
		this.maxAmount.setProperties(true, formatter);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ProductDialogue_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ProductDialogue_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_ProductDialogue_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ProductDialogue_btnSave"));
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
		doShowNotes(this.consumerProduct);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		logger.debug(Literal.ENTERING);
		consumerProductListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.consumerProduct.getBefImage());
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
	public void doWriteBeanToComponents(ConsumerProduct consumerProduct) {
		logger.debug(Literal.ENTERING);

		this.modelId.setText(consumerProduct.getModelId());
		this.modelDescription.setText(consumerProduct.getModelDescription());
		this.txtNames.setText(consumerProduct.getManufacturerId());
		this.assetDescription.setText(consumerProduct.getAssetDescription());
		this.minAmount.setValue(PennantApplicationUtil.formateAmount(consumerProduct.getMinAmount(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.maxAmount.setValue(PennantApplicationUtil.formateAmount(consumerProduct.getMaxAmount(),
				CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		this.modelStatus.setText(consumerProduct.getModelStatus());
		this.active.setChecked(consumerProduct.isActive());
		if (consumerProduct.isNew() || (consumerProduct.getRecordType() != null ? consumerProduct.getRecordType() : "")
				.equals(PennantConstants.RECORD_TYPE_NEW)) {
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		this.txtchannel.setText(consumerProduct.getChannel());
		this.recordStatus.setValue(consumerProduct.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCovenantType
	 */
	public void doWriteComponentsToBean(ConsumerProduct consumerProduct) {
		logger.debug(Literal.ENTERING);

		List<WrongValueException> wve = new ArrayList<>();

		try {
			consumerProduct.setModelId(this.modelId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setModelDescription(this.modelDescription.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setManufacturerId(this.txtNames.getText());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setAssetDescription(this.assetDescription.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setMinAmount(PennantApplicationUtil.unFormateAmount(this.minAmount.getActualValue(),
					CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setMaxAmount(PennantApplicationUtil.unFormateAmount(this.maxAmount.getActualValue(),
					CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setModelStatus(this.modelStatus.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			consumerProduct.setChannel(this.txtchannel.getValue());
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
	public void doShowDialog(ConsumerProduct consumerProduct) {
		logger.debug(Literal.ENTERING);

		if (consumerProduct.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.modelId.setFocus(true);
		} else {
			this.modelDescription.setFocus(true);
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(consumerProduct.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		doWriteBeanToComponents(consumerProduct);

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

		BigDecimal minAmount = this.minAmount.getActualValue();
		BigDecimal maxAmount = this.maxAmount.getActualValue();

		if (!this.modelId.isReadonly()) {
			this.modelId.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductList_ModelId.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_CODE, true, 1, 20));
		}

		if (!this.modelDescription.isReadonly()) {
			this.modelDescription
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductList_ModelDescription.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true, 1, 20));
		}

		if (!this.txtNames.getText().equals("")) {
			if (!this.btnNames.isDisabled()) {
				this.txtNames.setConstraint(
						new PTStringValidator(Labels.getLabel("label_ProductList_ManufacturerName.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		}

		if (!this.assetDescription.isReadonly()) {
			this.assetDescription
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductList_AssetDescription.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true, 1, 20));
		}

		if (!this.minAmount.isReadonly()) {
			if (minAmount.compareTo(BigDecimal.ZERO) == 0 || minAmount.compareTo(new BigDecimal("0.00")) == 0) {
				throw new WrongValueException(this.minAmount,
						Labels.getLabel("label_CDProductDeatislDialogue_MinAmountAlert.value"));
			} else {
				if (minAmount.compareTo(maxAmount) == 1) {
					throw new WrongValueException(this.minAmount,
							Labels.getLabel("label_ConsumerProductDialogue_MinValueAlert.value"));
				}

			}
		}

		if (!this.maxAmount.isReadonly()) {
			if (maxAmount.compareTo(BigDecimal.ZERO) == 0 || maxAmount.compareTo(new BigDecimal("0.00")) == 0) {
				throw new WrongValueException(this.minAmount,
						Labels.getLabel("label_CDProductDeatislDialogue_MaxAmountAlert.value"));
			}
		}

		if (!this.modelStatus.isReadonly()) {
			this.modelStatus.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductList_ModelStatus.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true, 1, 20));
		}

		if (!this.btnchannels.isDisabled()) {
			this.txtchannel.setConstraint(new PTStringValidator(Labels.getLabel("label_ProductList_ChannelAlert.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);

		this.modelId.setConstraint("");
		this.modelDescription.setConstraint("");
		this.txtchannel.setConstraint("");
		this.txtNames.setConstraint("");
		this.assetDescription.setConstraint("");
		this.minAmount.setConstraint("");
		this.maxAmount.setConstraint("");
		this.modelStatus.setConstraint("");

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

		final ConsumerProduct consumerProduct = new ConsumerProduct();
		BeanUtils.copyProperties(this.consumerProduct, consumerProduct);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ consumerProduct.getModelId();
		if (MessageUtil.confirm(msg) != MessageUtil.YES) {
			return;
		}

		if (StringUtils.trimToEmpty(consumerProduct.getRecordType()).equals("")) {
			consumerProduct.setVersion(consumerProduct.getVersion() + 1);
			consumerProduct.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (isWorkFlowEnabled()) {
				consumerProduct.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				consumerProduct.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
				getWorkFlowDetails(userAction.getSelectedItem().getLabel(), consumerProduct.getNextTaskId(),
						consumerProduct);
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			if (doProcess(consumerProduct, tranType)) {
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
		logger.debug(Literal.ENTERING);

		if (this.consumerProduct.isNewRecord()) {
			this.modelId.setDisabled(false);
		} else {
			this.modelId.setDisabled(true);
		}

		readOnlyComponent(isReadOnly("ProductDialogue_Description"), this.modelDescription);
		readOnlyComponent(isReadOnly("ProductDialogue_Name"), this.btnNames);
		readOnlyComponent(isReadOnly("ProductDialogue_AssetDescription"), this.assetDescription);
		readOnlyComponent(isReadOnly("ProductDialogue_MinimumAmount"), this.minAmount);
		readOnlyComponent(isReadOnly("ProductDialogue_MaximumAmount"), this.maxAmount);
		readOnlyComponent(isReadOnly("ProductDialogue_ModelStatus"), this.modelStatus);
		readOnlyComponent(isReadOnly("ProductDialogue_Channel"), this.btnchannels);
		readOnlyComponent(isReadOnly("ProductDialogue_Active"), this.active);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.consumerProduct.isNewRecord()) {
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

		readOnlyComponent(true, this.modelId);
		readOnlyComponent(true, this.modelDescription);
		readOnlyComponent(true, this.btnNames);
		readOnlyComponent(true, this.assetDescription);
		readOnlyComponent(true, this.minAmount);
		readOnlyComponent(true, this.maxAmount);
		readOnlyComponent(true, this.modelStatus);
		readOnlyComponent(true, this.btnchannels);
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
		this.modelId.setValue("");
		this.modelDescription.setValue("");
		this.txtNames.setValue("");
		this.assetDescription.setValue("");
		this.minAmount.setValue("");
		this.maxAmount.setValue("");
		this.modelStatus.setValue("");
		this.txtchannel.setValue("");
		this.active.setChecked(false);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);
		final ConsumerProduct consumerProduct = new ConsumerProduct();
		BeanUtils.copyProperties(this.consumerProduct, consumerProduct);

		doSetValidation();
		doWriteComponentsToBean(consumerProduct);

		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(consumerProduct.getRecordType())) {
				consumerProduct.setVersion(consumerProduct.getVersion() + 1);
				if (consumerProduct.isNew()) {
					consumerProduct.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					consumerProduct.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					consumerProduct.setNewRecord(true);
				}
			}
		} else {
			consumerProduct.setVersion(consumerProduct.getVersion() + 1);
			if (consumerProduct.isNew()) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(consumerProduct, tranType)) {
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
	private boolean doProcess(ConsumerProduct consumenrProduct, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		consumenrProduct.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		consumenrProduct.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		consumenrProduct.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			consumenrProduct.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(consumenrProduct.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, consumenrProduct);
				}
				if (isNotesMandatory(taskId, consumenrProduct)) {
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
			consumenrProduct.setTaskId(taskId);
			consumenrProduct.setNextTaskId(nextTaskId);
			consumenrProduct.setRoleCode(getRole());
			consumenrProduct.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(consumenrProduct, tranType);
			String operationRefs = getServiceOperations(taskId, consumenrProduct);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(consumenrProduct, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(consumenrProduct, tranType);
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
		ConsumerProduct consumerProduct = (ConsumerProduct) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = consumerProductService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = consumerProductService.saveOrUpdate(auditHeader);
					}
				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = consumerProductService.doApprove(auditHeader);

						if (consumerProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = consumerProductService.doReject(auditHeader);
						if (consumerProduct.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_consumerProductDialogue, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_consumerProductDialogue, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.consumerProduct), true);
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

	private AuditHeader getAuditHeader(ConsumerProduct consumerProduct, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, consumerProduct.getBefImage(), consumerProduct);
		return new AuditHeader(getReference(), null, null, null, auditDetail, consumerProduct.getUserDetails(),
				getOverideMap());
	}

	public void setConsumerProductListCtrl(ConsumerProductListCtrl consumerProductListCtrl) {
		this.consumerProductListCtrl = consumerProductListCtrl;
	}

	public void setConsumerProductService(ConsumerProductService consumerProductService) {
		this.consumerProductService = consumerProductService;
	}

	public void onClick$btnNames(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_consumerProductDialogue, "Manufacturer",
				String.valueOf(this.txtNames.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.txtNames.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onClick$btnchannels(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_consumerProductDialogue, "ChannelTypes",
				String.valueOf(this.txtchannel.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.txtchannel.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

}
