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
 * * FileName : AuthorizationLimitDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-04-2018 * *
 * Modified Date : 06-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-04-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.authorization.authorizationlimit;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.authorization.AuthorizationLimit;
import com.pennant.backend.model.authorization.AuthorizationLimitDetail;
import com.pennant.backend.service.authorization.AuthorizationLimitService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Authorization/AuthorizationLimit/authorizationLimitDialog.zul
 * file. <br>
 */
public class AuthorizationLimitDialogCtrl extends GFCBaseCtrl<AuthorizationLimit> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AuthorizationLimitDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_AuthorizationLimitDialog;

	protected Row userRow;
	protected ExtendedCombobox userId;
	protected Textbox userName;

	protected Row roleRow;
	protected ExtendedCombobox roleId;
	protected Textbox roleName;

	protected CurrencyBox limitAmount;
	protected Space space_StartDate;
	protected Datebox startDate;
	protected Space space_ExpiryDate;
	protected Datebox expiryDate;
	protected Row holdRow;
	protected Space space_HoldStartDate;
	protected Datebox holdStartDate;
	protected Space space_HoldExpiryDate;
	protected Datebox holdExpiryDate;
	protected Space space_Active;
	protected Checkbox active;
	private AuthorizationLimit authorizationLimit; // overhanded per param

	private transient AuthorizationLimitListCtrl authorizationLimitListCtrl; // overhanded per param
	private transient AuthorizationLimitService authorizationLimitService;

	private String hold;
	private Listbox listBoxCodeLimit;
	private Button btnAddDetails;
	private int listCount = 0;
	private Caption caption_LimitDetails;
	List<AuthorizationLimitDetail> deletedLimitList = new ArrayList<AuthorizationLimitDetail>();

	/**
	 * default constructor.<br>
	 */
	public AuthorizationLimitDialogCtrl() {
		super();
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.authorizationLimit.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AuthorizationLimitDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AuthorizationLimitDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AuthorizationLimitDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AuthorizationLimitDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.userId.setValue("");
		this.userId.setDescription("");
		this.roleId.setValue("");
		this.roleId.setDescription("");
		this.limitAmount.setValue("");
		this.startDate.setText("");
		this.expiryDate.setText("");
		this.holdStartDate.setText("");
		this.holdExpiryDate.setText("");
		this.active.setChecked(false);

		logger.debug("Leaving");
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

		final AuthorizationLimit aAuthorizationLimit = new AuthorizationLimit();
		BeanUtils.copyProperties(this.authorizationLimit, aAuthorizationLimit);

		String record = "User ID : " + aAuthorizationLimit.getUsrLogin();
		if (!userRow.isVisible()) {
			record = "Role Code : " + aAuthorizationLimit.getRoleCd();
		}

		doDelete(record, aAuthorizationLimit);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (authorizationLimit.getLimitType() == 1) {
			userRow.setVisible(true);
			roleRow.setVisible(false);
		} else {
			userRow.setVisible(false);
			roleRow.setVisible(true);
		}
		if (this.authorizationLimit.isNewRecord()) {
			this.btnCancel.setVisible(false);
			if (userRow.isVisible()) {
				readOnlyComponent(false, this.userId);
			}
			if (roleId.isVisible()) {
				readOnlyComponent(false, this.roleId);
			}
		} else {
			this.btnCancel.setVisible(true);
			if (userRow.isVisible()) {
				readOnlyComponent(true, this.userId);
				userId.setMandatoryStyle(false);
			}

			if (roleRow.isVisible()) {
				readOnlyComponent(false, this.roleId);
				roleId.setMandatoryStyle(false);
			}
		}

		if (StringUtils.equals("Y", hold)) {
			readOnlyComponent(true, this.startDate);
			readOnlyComponent(true, this.expiryDate);

			holdRow.setVisible(true);
			readOnlyComponent(isReadOnly("AuthorizationLimitDialog_HoldStartDate"), this.holdStartDate);
			readOnlyComponent(isReadOnly("AuthorizationLimitDialog_HoldExpiryDate"), this.holdExpiryDate);

			readOnlyComponent(true, this.active);
			readOnlyComponent(true, this.limitAmount);
			this.btnDelete.setVisible(false);
		} else {
			holdRow.setVisible(false);
			if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, authorizationLimit.getRecordType())) {
				readOnlyComponent(true, this.limitAmount);
				readOnlyComponent(true, this.startDate);
				readOnlyComponent(true, this.expiryDate);
				readOnlyComponent(true, this.active);
				readOnlyComponent(true, this.limitAmount);
			} else {
				readOnlyComponent(isReadOnly("AuthorizationLimitDialog_StartDate"), this.startDate);
				readOnlyComponent(isReadOnly("AuthorizationLimitDialog_ExpiryDate"), this.expiryDate);
				readOnlyComponent(isReadOnly("AuthorizationLimitDialog_Active"), this.active);
				readOnlyComponent(isReadOnly("AuthorizationLimitDialog_LimitAmount"), this.limitAmount);
			}
		}

		if (this.startDate.isReadonly()) {
			space_StartDate.setSclass("");
		}

		if (this.expiryDate.isReadonly()) {
			space_ExpiryDate.setSclass("");
		}

		if (limitAmount.isReadonly()) {
			limitAmount.setMandatory(false);
			this.btnAddDetails.setVisible(false);
		} else {
			this.btnAddDetails.setVisible(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.authorizationLimit.isNewRecord()) {
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
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAuthorizedSignatoryRepository (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType                       (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(AuthorizationLimit aAuthorizationLimit, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAuthorizationLimit.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aAuthorizationLimit.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAuthorizationLimit.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAuthorizationLimit.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAuthorizationLimit.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAuthorizationLimit);
				}

				if (isNotesMandatory(taskId, aAuthorizationLimit)) {
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

			aAuthorizationLimit.setTaskId(taskId);
			aAuthorizationLimit.setNextTaskId(nextTaskId);
			aAuthorizationLimit.setRoleCode(getRole());
			aAuthorizationLimit.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aAuthorizationLimit, tranType);
			String operationRefs = getServiceOperations(taskId, aAuthorizationLimit);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAuthorizationLimit, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAuthorizationLimit, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.userId);
		readOnlyComponent(true, this.roleId);
		readOnlyComponent(true, this.limitAmount);
		space_StartDate.setStyle("");
		readOnlyComponent(true, this.startDate);
		space_ExpiryDate.setStyle("");
		readOnlyComponent(true, this.expiryDate);
		space_HoldStartDate.setStyle("");
		readOnlyComponent(true, this.holdStartDate);
		space_HoldExpiryDate.setStyle("");
		readOnlyComponent(true, this.holdExpiryDate);
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
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.userId.setConstraint("");
		this.roleId.setConstraint("");
		this.limitAmount.setConstraint("");
		this.startDate.setConstraint("");
		this.expiryDate.setConstraint("");
		this.holdStartDate.setConstraint("");
		this.holdExpiryDate.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Saves the components to table. <br>
	 */
	public void doSave() {
		logger.debug("Entering");
		final AuthorizationLimit aAuthorizationLimit = new AuthorizationLimit();
		BeanUtils.copyProperties(this.authorizationLimit, aAuthorizationLimit);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aAuthorizationLimit);

		isNew = aAuthorizationLimit.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAuthorizationLimit.getRecordType())) {
				aAuthorizationLimit.setVersion(aAuthorizationLimit.getVersion() + 1);
				if (isNew) {
					aAuthorizationLimit.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAuthorizationLimit.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAuthorizationLimit.setNewRecord(true);
				}
			}
		} else {
			aAuthorizationLimit.setVersion(aAuthorizationLimit.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		try {
			if (doProcess(aAuthorizationLimit, tranType)) {
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
		AuthorizationLimit aAuthorizationLimit = (AuthorizationLimit) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		boolean limitHold = false;

		if (StringUtils.equals("Y", hold)) {
			limitHold = true;
		}

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = authorizationLimitService.delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = authorizationLimitService.saveOrUpdate(auditHeader, limitHold);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = authorizationLimitService.doApprove(auditHeader, limitHold);

					if (aAuthorizationLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = authorizationLimitService.doReject(auditHeader);
					if (aAuthorizationLimit.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_AuthorizationLimitDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_AuthorizationLimitDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.authorizationLimit), true);
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
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		if (authorizationLimit.getLimitType() == 1) {
			this.userId.setMandatoryStyle(true);
			this.userId.setModuleName("SecurityUsers");
			this.userId.setValueColumn("UsrLogin");
			this.userId.setDescColumn("UsrFName");
			this.userId.setValidateColumns(new String[] { "usrLogin" });
		} else {
			this.roleId.setMandatoryStyle(true);
			this.roleId.setModuleName("SecurityRole");
			this.roleId.setValueColumn("RoleCd");
			this.roleId.setDescColumn("RoleDesc");
			this.roleId.setValidateColumns(new String[] { "RoleCd" });
		}

		/*
		 * if(StringUtils.equalsIgnoreCase("FIN", authorizationLimit.getModule())){
		 * limitCode.setLabel(Labels.getLabel("label_AuthorizationLimitDialog_Product.value")); }else{
		 * limitCode.setLabel(Labels.getLabel("label_AuthorizationLimitDialog_ColType.value")); }
		 */
		this.limitAmount.setProperties(true, CurrencyUtil.getFormat(""));
		this.startDate.setFormat(PennantConstants.dateFormat);
		this.expiryDate.setFormat(PennantConstants.dateFormat);
		this.holdStartDate.setFormat(PennantConstants.dateFormat);
		this.holdExpiryDate.setFormat(PennantConstants.dateFormat);

		if (StringUtils.equalsIgnoreCase("FIN", authorizationLimit.getModule())) {
			caption_LimitDetails.setLabel(Labels.getLabel("label_AuthorizationLimitDialog_Product.value"));
		} else {
			caption_LimitDetails.setLabel(Labels.getLabel("label_AuthorizationLimitDialog_ColType.value"));
		}
		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		logger.debug(Literal.LEAVING);

		logger.debug(Literal.LEAVING);
	}

	@Override
	protected void doSetProperties() {

		super.pageRightName = "AuthorizationLimitDialog";
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		/*
		 * if (!this.userId.isReadonly()){ this.userId.setConstraint(new
		 * PTStringValidator(Labels.getLabel("label_AuthorizationLimitDialog_UserID.value"),PennantRegularExpressions.
		 * REGEX_NAME,true)); }
		 */
		if (!this.userId.isReadonly() && userRow.isVisible()) {
			this.userId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AuthorizationLimitDialog_UserId.value"), null, true, true));
		}

		if (!this.roleId.isReadonly() && roleRow.isVisible()) {
			this.roleId.setConstraint(new PTStringValidator(
					Labels.getLabel("label_AuthorizationLimitDialog_RoleId.value"), null, true, true));
		}

		if (!this.limitAmount.isReadonly()) {
			this.limitAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_AuthorizationLimitDialog_LimitAmount.value"), 0, true, false, 0));
		}

		if (!this.expiryDate.isReadonly()) {

			Date date = DateUtil.getDatePart(SysParamUtil.getAppDate());
			if (startDate.getValue() == null || startDate.getValue().before(date)) {
				date = DateUtil.getDatePart(SysParamUtil.getAppDate());
			} else {
				date = startDate.getValue();
			}
			this.expiryDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_AuthorizationLimitDialog_ExpiryDate.value"), true, date, null, false));
		}

		if (!this.startDate.isReadonly()) {
			this.startDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_AuthorizationLimitDialog_StartDate.value"), true,
							DateUtil.getDatePart(SysParamUtil.getAppDate()), null, true));
		}

		if (holdRow.isVisible()) {
			if (this.holdStartDate.isVisible() && !this.holdStartDate.isReadonly()) {
				// this.holdStartDate.setConstraint(new
				// PTDateValidator(Labels.getLabel("label_AuthorizationLimitDialog_HoldStartDate.value"),true,false));
			}
			if (this.holdExpiryDate.isVisible() && !this.holdExpiryDate.isReadonly()) {
				this.holdExpiryDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_AuthorizationLimitDialog_HoldExpiryDate.value"),
								true, holdStartDate.getValue(), expiryDate.getValue(), true));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param authorizationLimit The entity that need to be render.
	 */
	public void doShowDialog(AuthorizationLimit authorizationLimit) {
		logger.debug(Literal.LEAVING);

		if (authorizationLimit.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {

			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(authorizationLimit.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				// setFocus
				this.limitAmount.focus();
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

		doWriteBeanToComponents(authorizationLimit);
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param authorizationLimit
	 * 
	 */
	public void doWriteBeanToComponents(AuthorizationLimit aAuthorizationLimit) {
		logger.debug(Literal.ENTERING);

		if (aAuthorizationLimit.isNewRecord()) {
			this.userId.setDescription("");
			this.roleId.setDescription("");
		} else {
			this.userId.setValue(aAuthorizationLimit.getUsrLogin());
			this.userId.setDescription(aAuthorizationLimit.getUsrLogin());
			userName.setValue(PennantApplicationUtil.getFullName(aAuthorizationLimit.getUsrFName(),
					aAuthorizationLimit.getUsrMName(), aAuthorizationLimit.getUsrLName()));

			this.roleId.setValue(aAuthorizationLimit.getRoleCd());
			this.roleId.setDescription(aAuthorizationLimit.getRoleName());
			this.roleName.setValue(aAuthorizationLimit.getRoleName());
		}

		this.limitAmount.setValue(
				PennantApplicationUtil.formateAmount(aAuthorizationLimit.getLimitAmount(), CurrencyUtil.getFormat("")));

		this.startDate.setValue(aAuthorizationLimit.getStartDate());
		if (startDate.getValue() != null && !this.startDate.isReadonly()) {
			if (startDate.getValue().before(DateUtil.getDatePart(DateUtil.getSysDate()))) {
				readOnlyComponent(true, this.startDate);
				space_StartDate.setSclass("");
			}
		}

		this.expiryDate.setValue(aAuthorizationLimit.getExpiryDate());
		this.holdStartDate.setValue(aAuthorizationLimit.getHoldStartDate());
		this.holdExpiryDate.setValue(aAuthorizationLimit.getHoldExpiryDate());
		this.active.setChecked(aAuthorizationLimit.isActive());
		refreshListBox(aAuthorizationLimit.getAuthorizationLimitDetails());

		this.recordStatus.setValue(aAuthorizationLimit.getRecordStatus());

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAuthorizationLimit
	 */
	public void doWriteComponentsToBean(AuthorizationLimit aAuthorizationLimit) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		boolean validAmount = false;
		if (aAuthorizationLimit.getLimitType() == 1) {
			// User I D
			try {
				this.userId.getValue();
				SecurityUser user = (SecurityUser) this.userId.getObject();

				if (user != null) {
					aAuthorizationLimit.setUserID(user.getUsrID());
					aAuthorizationLimit.setUsrLogin(user.getUsrLogin());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

		} else {
			// Role Id
			try {
				this.roleId.getValidatedValue();
				SecurityRole role = (SecurityRole) this.roleId.getObject();
				if (role != null) {
					this.roleName.setValue(role.getRoleDesc());
					aAuthorizationLimit.setRoleId(role.getRoleID());
					aAuthorizationLimit.setRoleCd(role.getRoleCd());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// Limit Amount
		try {
			aAuthorizationLimit.setLimitAmount(PennantApplicationUtil
					.unFormateAmount(this.limitAmount.getValidateValue(), CurrencyUtil.getFormat("")));
			validAmount = true;
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Start Date
		try {
			aAuthorizationLimit.setStartDate(this.startDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Expiry Date
		try {
			aAuthorizationLimit.setExpiryDate(this.expiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Hold Start Date
		try {
			aAuthorizationLimit.setHoldStartDate(this.holdStartDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Hold Expiry Date
		try {
			aAuthorizationLimit.setHoldExpiryDate(this.holdExpiryDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Active
		try {
			aAuthorizationLimit.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// List Data Preparation.

		List<Component> components = listBoxCodeLimit.getChildren();

		if (components.size() >= 1) {
			List<AuthorizationLimitDetail> limitDetails = new ArrayList<AuthorizationLimitDetail>();

			for (Component component : components) {
				boolean valid = true;
				if (component instanceof Listitem) {
					Listitem listitem = (Listitem) component;
					// Validation

					ExtendedCombobox combobox = (ExtendedCombobox) listitem.getFirstChild().getFirstChild();
					CurrencyBox codeLimitAmount = (CurrencyBox) listitem.getFirstChild().getNextSibling()
							.getFirstChild();
					AuthorizationLimitDetail detail = (AuthorizationLimitDetail) listitem.getAttribute("data");

					try {
						if (StringUtils.trimToNull(combobox.getValue()) == null) {
							throw new WrongValueException(combobox, Labels.getLabel("FIELD_IS_MAND",
									new String[] { Labels.getLabel("listheader_AuthLimitCode") }));
						}
						detail.setCode(combobox.getValue());

					} catch (WrongValueException we) {
						wve.add(we);
						valid = false;
					}

					try {
						String[] parameters = new String[3];
						parameters[0] = Labels.getLabel("listheader_AuthLimitAmount");
						parameters[1] = "0";

						BigDecimal amount = PennantApplicationUtil.unFormateAmount(codeLimitAmount.getValidateValue(),
								CurrencyUtil.getFormat(""));

						if (amount.compareTo(BigDecimal.ZERO) <= 0) {
							throw new WrongValueException(codeLimitAmount,
									Labels.getLabel("NUMBER_MINVALUE", parameters));

						}

						if (validAmount) {
							if (aAuthorizationLimit.getLimitAmount().compareTo(amount) < 0) {
								parameters[1] = PennantApplicationUtil.formatAmount(this.limitAmount.getActualValue(),
										CurrencyUtil.getFormat(""));
								throw new WrongValueException(codeLimitAmount,
										Labels.getLabel("NUMBER_MAXVALUE_EQ", parameters));
							}
						}
						detail.setLimitAmount(amount);

					} catch (WrongValueException we) {
						wve.add(we);
						valid = false;
					}

					if (valid) {
						limitDetails.add(detail);
					}
				}
			}

			aAuthorizationLimit.setAuthorizationLimitDetails(limitDetails);
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
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(AuthorizationLimit aAuthorizationLimit, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAuthorizationLimit.getBefImage(), aAuthorizationLimit);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aAuthorizationLimit.getUserDetails(),
				getOverideMap());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.authorizationLimit.getId());
	}

	public void onAuthorizationLimitDetailItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);

		// New

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
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		logger.debug(Literal.ENTERING);
		doShowNotes(this.authorizationLimit);
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
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_AuthorizationLimitDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_AuthorizationLimitDialog);

		try {
			// Get the required arguments.
			this.authorizationLimit = (AuthorizationLimit) arguments.get("authorizationLimit");
			this.authorizationLimitListCtrl = (AuthorizationLimitListCtrl) arguments.get("authorizationLimitListCtrl");

			if (this.authorizationLimit == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}
			this.hold = (String) arguments.get("hold");

			// Store the before image.
			AuthorizationLimit authorizationLimit = new AuthorizationLimit();
			BeanUtils.copyProperties(this.authorizationLimit, authorizationLimit);
			this.authorizationLimit.setBefImage(authorizationLimit);

			// Render the page and display the data.
			doLoadWorkFlow(this.authorizationLimit.isWorkflow(), this.authorizationLimit.getWorkflowId(),
					this.authorizationLimit.getNextTaskId());

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
			doShowDialog(this.authorizationLimit);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$limitAmount(Event event) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$roleId(Event event) {
		logger.debug(Literal.ENTERING);
		SecurityRole role = (SecurityRole) this.roleId.getObject();
		if (role != null) {
			this.roleName.setValue(role.getRoleDesc());
		}

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$userId(Event event) {
		logger.debug(Literal.ENTERING);

		SecurityUser user = (SecurityUser) this.userId.getObject();
		if (user != null) {
			this.userName.setValue(
					PennantApplicationUtil.getFullName(user.getUsrFName(), user.getUsrMName(), user.getUsrLName()));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		logger.debug(Literal.ENTERING);
		authorizationLimitListCtrl.search();
		logger.debug(Literal.LEAVING);
	}

	private void refreshListBox(List<AuthorizationLimitDetail> limitDetails) {

		if (CollectionUtils.isEmpty(limitDetails)) {
			return;
		}

		for (AuthorizationLimitDetail detail : limitDetails) {

			AuthorizationLimitDetail limitDetail = new AuthorizationLimitDetail();
			BeanUtils.copyProperties(detail, limitDetail);
			detail.setBefImage(limitDetail);
			addListIteam(limitDetail);
		}
	}

	public void onClick$btnAddDetails(Event event) {
		logger.debug(Literal.ENTERING);

		AuthorizationLimitDetail detail = new AuthorizationLimitDetail();
		detail.setNewRecord(true);
		detail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
		addListIteam(detail);

		logger.debug(Literal.LEAVING);
	}

	public void addListIteam(AuthorizationLimitDetail detail) {
		Listitem item = new Listitem();
		Listcell lc = new Listcell();
		ExtendedCombobox combobox = new ExtendedCombobox();
		combobox.setId("code" + listCount);
		if (detail.isNewRecord()) {
			combobox.setMandatoryStyle(true);
			combobox.setReadonly(false);
		} else {
			combobox.setMandatoryStyle(false);
			combobox.setReadonly(true);
		}

		combobox.addForward("onFulfill", self, "onChangeCode");

		combobox.setValue(detail.getCode());
		combobox.setDescription(detail.getCodeName());

		if (StringUtils.equalsIgnoreCase("FIN", authorizationLimit.getModule())) {
			combobox.setModuleName("Product");
			combobox.setValueColumn("ProductCode");
			combobox.setDescColumn("ProductDesc");
			combobox.setValidateColumns(new String[] { "ProductCode" });
		} else {
			combobox.setModuleName("CollateralStructure");
			combobox.setValueColumn("CollateralType");
			combobox.setDescColumn("CollateralDesc");
			combobox.setValidateColumns(new String[] { "CollateralType" });
		}

		lc.appendChild(combobox);
		item.appendChild(lc);

		/*
		 * Listcell lc1 = new Listcell(); Label label= new Label(detail.getCodeName()); lc1.appendChild(label);
		 * item.appendChild(lc1);
		 */
		Listcell lc2 = new Listcell();
		CurrencyBox codeLimitAmount = new CurrencyBox();
		codeLimitAmount.setId("code" + listCount);
		codeLimitAmount.setProperties(true, CurrencyUtil.getFormat(""));

		codeLimitAmount
				.setValue(PennantApplicationUtil.formateAmount(detail.getLimitAmount(), CurrencyUtil.getFormat("")));

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())
				|| StringUtils.equals("Y", hold)) {
			readOnlyComponent(true, codeLimitAmount);
		} else {
			readOnlyComponent(limitAmount.isReadonly(), codeLimitAmount);
		}

		if (codeLimitAmount.isReadonly()) {
			codeLimitAmount.setMandatory(false);
		}
		codeLimitAmount.addForward("onFulfill", self, "onChangeLimitAmount");

		lc2.appendChild(codeLimitAmount);
		item.appendChild(lc2);

		Listcell lc3 = new Listcell();
		Label operationLabel = new Label(detail.getRecordType());
		lc3.appendChild(operationLabel);
		item.appendChild(lc3);

		Listcell lc4 = new Listcell();
		Button delete = new Button();

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())) {
			delete.setLabel(Labels.getLabel("btnCancel.label"));
		} else {
			delete.setLabel(Labels.getLabel("btnDelete.label"));
		}

		lc4.appendChild(delete);
		delete.addForward("onClick", self, "onClick_Delete");
		item.appendChild(lc4);
		delete.setVisible(true);
		delete.setDisabled(codeLimitAmount.isReadonly());

		item.setAttribute("data", detail);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAuthorizationLimitDetailItemDoubleClicked");

		listBoxCodeLimit.appendChild(item);
		setKeyPhaseFilters();
		this.listCount++;

	}

	public void onChangeCode(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		ExtendedCombobox combobox = (ExtendedCombobox) event.getOrigin().getTarget();
		Listitem listitem = (Listitem) combobox.getParent().getParent();

		AuthorizationLimitDetail detail = (AuthorizationLimitDetail) listitem.getAttribute("data");
		combobox.getValidatedValue();
		detail.setCode(combobox.getValue());
		listitem.setAttribute("data", detail);
		setKeyPhaseFilters();

		logger.debug(Literal.LEAVING);
	}

	private void setKeyPhaseFilters() {

		List<Component> components = listBoxCodeLimit.getChildren();
		List<String> valueList = new ArrayList<String>();

		if (components.size() >= 1) {
			for (Component component1 : components) {
				if (component1 instanceof Listitem) {
					ExtendedCombobox combobox = (ExtendedCombobox) component1.getFirstChild().getFirstChild();
					if (StringUtils.trimToNull(combobox.getValue()) != null) {
						valueList.add(combobox.getValue());
					}
				}
			}
		}

		if (CollectionUtils.isEmpty(valueList)) {
			return;
		}
		List<String> finalList = new ArrayList<String>();

		for (Component component1 : components) {
			if (component1 instanceof Listitem) {
				ExtendedCombobox combobox = (ExtendedCombobox) component1.getFirstChild().getFirstChild();

				for (String string : valueList) {
					if (!StringUtils.equals(string, combobox.getValue())) {
						finalList.add(string);
					}
				}

				if (!CollectionUtils.isEmpty(finalList)) {
					Filter[] filters = new Filter[1];
					if (StringUtils.equalsIgnoreCase("FIN", authorizationLimit.getModule())) {
						filters[0] = new Filter("ProductCode", finalList, Filter.OP_NOT_IN);
					} else {
						filters[0] = new Filter("CollateralType", finalList, Filter.OP_NOT_IN);
					}
					combobox.setFilters(filters);
				}

				finalList = new ArrayList<String>();
			}
		}
	}

	public void onChnageCollateralType(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		logger.debug(Literal.LEAVING);
	}

	public void onChangeLimitAmount(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		CurrencyBox currencyBox = (CurrencyBox) event.getOrigin().getTarget();
		Listitem listitem = (Listitem) currencyBox.getParent().getParent();

		AuthorizationLimitDetail detail = (AuthorizationLimitDetail) listitem.getAttribute("data");

		if (StringUtils.isBlank(detail.getRecordType())) {
			detail.setVersion(detail.getVersion() + 1);
			detail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			detail.setNewRecord(true);
		}

		detail.setLimitAmount(
				PennantApplicationUtil.unFormateAmount(currencyBox.getValidateValue(), CurrencyUtil.getFormat("")));
		// Update the Record Type for any value change

		listitem.setAttribute("data", detail);
		logger.debug(Literal.LEAVING);
	}

	public void onClick_Delete(ForwardEvent event) {
		logger.debug(Literal.ENTERING);

		Listitem listitem = (Listitem) event.getOrigin().getTarget().getParent().getParent();
		AuthorizationLimitDetail detail = (AuthorizationLimitDetail) listitem.getAttribute("data");
		if (StringUtils.trimToEmpty(detail.getRecordType()).equals("")) {
			detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			detail.setVersion(detail.getVersion() + 1);
			detail.setNewRecord(true);
		} else {

			if (detail.isNewRecord()) {
				if (StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, detail.getRecordType())) {
					listBoxCodeLimit.removeChild(listitem);
					logger.debug(Literal.LEAVING);
					return;
				} else if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())) {
					detail.setRecordType(null);
				} else {
					detail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				}

			} else {
				detail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
			}
		}

		refreshListIteam(listitem);
		listitem.setAttribute("data", detail);

		logger.debug(Literal.LEAVING);
	}

	public void refreshListIteam(Listitem listitem) {
		AuthorizationLimitDetail detail = (AuthorizationLimitDetail) listitem.getAttribute("data");
		CurrencyBox codeLimitAmount = (CurrencyBox) listitem.getFirstChild().getNextSibling().getFirstChild();

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())) {
			readOnlyComponent(true, codeLimitAmount);
		} else {
			readOnlyComponent(isReadOnly("AuthorizationLimitDialog_LimitAmount"), codeLimitAmount);
		}

		if (codeLimitAmount.isReadonly()) {
			codeLimitAmount.setMandatory(false);
		} else {
			codeLimitAmount.setMandatory(true);
		}

		Button delete = (Button) codeLimitAmount.getParent().getNextSibling().getNextSibling().getFirstChild();

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, detail.getRecordType())) {
			delete.setLabel(Labels.getLabel("btnCancel.label"));
		} else {
			delete.setLabel(Labels.getLabel("btnDelete.label"));
		}

		Label operationLabel = (Label) codeLimitAmount.getParent().getNextSibling().getFirstChild();
		operationLabel.setValue(detail.getRecordType());
	}

	// label_Cancel

	public void setAuthorizationLimitService(AuthorizationLimitService authorizationLimitService) {
		this.authorizationLimitService = authorizationLimitService;
	}

}
