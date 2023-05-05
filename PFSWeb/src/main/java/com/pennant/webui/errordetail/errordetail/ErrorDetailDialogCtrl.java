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
 * * FileName : ErrorDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2016 * *
 * Modified Date : 05-05-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.errordetail.errordetail;

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
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.errordetail.ErrorDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the /WEB-INF/pages/ErrorDetail/ErrorDetail/errorDetailDialog.zul file. <br>
 * ************************************************************<br>
 */
public class ErrorDetailDialogCtrl extends GFCBaseCtrl<ErrorDetail> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ErrorDetailDialogCtrl.class);

	/*
	 * ************************************************************************ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ************************************************************************
	 */
	protected Window window_ErrorDetailDialog;
	protected Row row0;
	protected Label label_ErrorCode;
	protected Hlayout hlayout_ErrorCode;
	protected Space space_ErrorCode;

	protected Textbox errorCode;
	protected Label label_ErrorLanguage;
	protected Hlayout hlayout_ErrorLanguage;
	protected Space space_ErrorLanguage;

	protected Textbox errorLanguage;
	protected Row row1;
	protected Label label_ErrorSeverity;
	protected Hlayout hlayout_ErrorSeverity;
	protected Space space_ErrorSeverity;

	protected Combobox errorSeverity;
	protected Label label_ErrorMessage;
	protected Hlayout hlayout_ErrorMessage;
	protected Space space_ErrorMessage;

	protected Textbox errorMessage;
	protected Row row2;
	protected Label label_ErrorExtendedMessage;
	protected Hlayout hlayout_ErrorExtendedMessage;
	protected Space space_ErrorExtendedMessage;

	protected Textbox errorExtendedMessage;

	private boolean enqModule = false;

	// not auto wired vars
	private ErrorDetail errorDetail; // overhanded per param
	private transient ErrorDetailListCtrl errorDetailListCtrl; // overhanded per param

	// ServiceDAOs / Domain Classes
	private transient ErrorDetailService errorDetailService;
	private transient PagedListService pagedListService;
	private List<ValueLabel> listErrorSeverity = PennantStaticListUtil.getSysParamType();

	/**
	 * default constructor.<br>
	 */
	public ErrorDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ErrorDetailDialog";
	}

	// ************************************************* //
	// *************** Component Events **************** //
	// ************************************************* //

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected ErrorDetail object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ErrorDetailDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ErrorDetailDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			/* set components visible dependent of the users rights */
			doCheckRights();
			// READ OVERHANDED params !
			if (arguments.containsKey("errorDetail")) {
				this.errorDetail = (ErrorDetail) arguments.get("errorDetail");
				ErrorDetail befImage = new ErrorDetail();
				BeanUtils.copyProperties(this.errorDetail, befImage);
				this.errorDetail.setBefImage(befImage);

				setErrorDetail(this.errorDetail);
			} else {
				setErrorDetail(null);
			}
			doLoadWorkFlow(this.errorDetail.isWorkflow(), this.errorDetail.getWorkflowId(),
					this.errorDetail.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "ErrorDetailDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			// READ OVERHANDED params !
			// we get the errorDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete errorDetail here.
			if (arguments.containsKey("errorDetailListCtrl")) {
				setErrorDetailListCtrl((ErrorDetailListCtrl) arguments.get("errorDetailListCtrl"));
			} else {
				setErrorDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getErrorDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	private void doEdit() {
		logger.debug("Entering");

		if (this.errorDetail.isNewRecord()) {
			this.errorCode.setReadonly(false);
			this.errorLanguage.setReadonly(false);
			this.errorExtendedMessage.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.errorCode.setReadonly(true);
			this.errorSeverity.setDisabled(true);
			this.errorLanguage.setReadonly(true);
			this.errorExtendedMessage.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.errorMessage.setReadonly(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.errorDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnDelete(Event event) {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSave(Event event) {
		logger.debug("Entering" + event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.errorDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_ErrorDetailDialog);
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
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.errorDetail);

	}

	// ****************************************************************+
	// ************************ GUI operations ************************+
	// ****************************************************************+

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aErrorDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(ErrorDetail aErrorDetail) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (errorDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.errorCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.errorSeverity.focus();
				if (StringUtils.isNotBlank(errorDetail.getRecordType())) {
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
		doWriteBeanToComponents(aErrorDetail);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.errorCode.setReadonly(true);
		this.errorLanguage.setReadonly(true);
		this.errorSeverity.setDisabled(true);
		this.errorMessage.setReadonly(true);
		this.errorExtendedMessage.setReadonly(true);

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

	// ****************************************************************+
	// ****************************++ helpers ************************++
	// ****************************************************************+

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);

		if (!enqModule) {
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ErrorDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ErrorDetailDialog_btnEdit"));
			this.btnDelete.setVisible(false);// getUserWorkspace().isAllowed("button_ErrorDetailDialog_btnDelete")
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ErrorDetailDialog_btnSave"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.errorCode.setMaxlength(10);
		this.errorLanguage.setMaxlength(2);
		this.errorMessage.setMaxlength(100);
		this.errorExtendedMessage.setMaxlength(300);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aErrorDetail ErrorDetail
	 */
	public void doWriteBeanToComponents(ErrorDetail aErrorDetail) {
		logger.debug("Entering");
		this.errorCode.setValue(aErrorDetail.getCode());
		this.errorLanguage.setValue(aErrorDetail.getLanguage());
		fillComboBox(this.errorSeverity, aErrorDetail.getSeverity(), listErrorSeverity, "");
		this.errorMessage.setValue(aErrorDetail.getMessage());
		this.errorExtendedMessage.setValue(aErrorDetail.getExtendedMessage());

		this.recordStatus.setValue(aErrorDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aErrorDetail
	 */
	public void doWriteComponentsToBean(ErrorDetail aErrorDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Error Code
		try {
			aErrorDetail.setCode(this.errorCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Error Language
		try {
			aErrorDetail.setLanguage(this.errorLanguage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Error Severity
		try {
			String strErrorSeverity = null;
			if (this.errorSeverity.getSelectedItem() != null) {
				strErrorSeverity = this.errorSeverity.getSelectedItem().getValue().toString();
			}
			if (strErrorSeverity != null && !PennantConstants.List_Select.equals(strErrorSeverity)) {
				aErrorDetail.setSeverity(strErrorSeverity);
			} else {
				aErrorDetail.setSeverity(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Error Message
		try {
			aErrorDetail.setMessage(this.errorMessage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Error Extended Message
		try {
			aErrorDetail.setExtendedMessage(this.errorExtendedMessage.getValue());
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
		aErrorDetail.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Error Code
		if (!this.errorCode.isReadonly()) {
			this.errorCode
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ErrorDetailDialog_ErrorCode.value"),
							PennantRegularExpressions.REGEX_NUMERIC, true));
		}
		// Error Language
		if (!this.errorLanguage.isReadonly()) {
			this.errorLanguage
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ErrorDetailDialog_ErrorLanguage.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		// Error Severity
		if (!this.errorSeverity.isDisabled()) {
			this.errorSeverity.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_ErrorDetailDialog_ErrorSeverity.value"), listErrorSeverity, true));
		}
		// Error Message
		if (!this.errorMessage.isReadonly()) {
			this.errorMessage.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ErrorDetailDialog_ErrorMessage.value"), null, true));
		}
		// Error Extended Message
		if (!this.errorExtendedMessage.isReadonly()) {
			this.errorExtendedMessage.setConstraint(
					new PTStringValidator(Labels.getLabel("label_ErrorDetailDialog_ErrorExtendedMessage.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.errorCode.setConstraint("");
		this.errorLanguage.setConstraint("");
		this.errorSeverity.setConstraint("");
		this.errorMessage.setConstraint("");
		this.errorExtendedMessage.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.errorCode.setErrorMessage("");
		this.errorLanguage.setErrorMessage("");
		this.errorSeverity.setErrorMessage("");
		this.errorMessage.setErrorMessage("");
		this.errorExtendedMessage.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */
	protected void refreshList() {
		getErrorDetailListCtrl().search();
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final ErrorDetail aErrorDetail = new ErrorDetail();
		BeanUtils.copyProperties(getErrorDetail(), aErrorDetail);

		doDelete(aErrorDetail.getCode(), aErrorDetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.errorCode.setValue("");
		this.errorLanguage.setValue("");
		this.errorSeverity.setSelectedIndex(0);
		this.errorMessage.setValue("");
		this.errorExtendedMessage.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table.
	 */
	public void doSave() {
		logger.debug("Entering");
		final ErrorDetail aErrorDetail = new ErrorDetail();
		BeanUtils.copyProperties(getErrorDetail(), aErrorDetail);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aErrorDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aErrorDetail.getNextTaskId(), aErrorDetail);
		}

		// *************************************************************
		// force validation, if on, than execute by component.getValue()
		// *************************************************************
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aErrorDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the ErrorDetail object with the components data
			doWriteComponentsToBean(aErrorDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aErrorDetail.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aErrorDetail.getRecordType())) {
				aErrorDetail.setVersion(aErrorDetail.getVersion() + 1);
				if (isNew) {
					aErrorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aErrorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aErrorDetail.setNewRecord(true);
				}
			}
		} else {
			aErrorDetail.setVersion(aErrorDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aErrorDetail, tranType)) {
				// doWriteBeanToComponents(aErrorDetail);
				refreshList();
				closeDialog();
			}

		} catch (final DataAccessException e) {
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
	protected boolean doProcess(ErrorDetail aErrorDetail, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		aErrorDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aErrorDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aErrorDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aErrorDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			aErrorDetail.setTaskId(getTaskId());
			aErrorDetail.setNextTaskId(getNextTaskId());
			aErrorDetail.setRoleCode(getRole());
			aErrorDetail.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aErrorDetail, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aErrorDetail, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aErrorDetail, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		ErrorDetail aErrorDetail = (ErrorDetail) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getErrorDetailService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getErrorDetailService().saveOrUpdate(auditHeader);
				}

			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getErrorDetailService().doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aErrorDetail.getRecordType())) {
						deleteNotes = true;
					}

				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getErrorDetailService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aErrorDetail.getRecordType())) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ErrorDetailDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ErrorDetailDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes("ErrorDetails", aErrorDetail.getCode(), aErrorDetail.getVersion()), true);
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

		logger.debug("return Value:" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// ******************************************************//
	// ***************** WorkFlow Components*****************//
	// ******************************************************//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(ErrorDetail aErrorDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aErrorDetail.getBefImage(), aErrorDetail);
		return new AuditHeader(aErrorDetail.getCode(), null, null, null, auditDetail, aErrorDetail.getUserDetails(),
				getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ErrorDetail getErrorDetail() {
		return this.errorDetail;
	}

	public void setErrorDetail(ErrorDetail errorDetail) {
		this.errorDetail = errorDetail;
	}

	public void setErrorDetailService(ErrorDetailService errorDetailService) {
		this.errorDetailService = errorDetailService;
	}

	public ErrorDetailService getErrorDetailService() {
		return this.errorDetailService;
	}

	public void setErrorDetailListCtrl(ErrorDetailListCtrl errorDetailListCtrl) {
		this.errorDetailListCtrl = errorDetailListCtrl;
	}

	public ErrorDetailListCtrl getErrorDetailListCtrl() {
		return this.errorDetailListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.errorDetail.getCode());
	}

}
