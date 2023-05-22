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
 * * FileName : DeviationParamDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-06-2015 * *
 * Modified Date : 22-06-2015 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-06-2015 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.solutionfactory.deviationparam;

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
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.solutionfactory.DeviationParamService;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/DeviationParam/deviationParamDialog.zul file.
 */
public class DeviationParamDialogCtrl extends GFCBaseCtrl<DeviationParam> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DeviationParamDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DeviationParamDialog;
	protected Row row0;
	protected Label label_Code;
	protected Hlayout hlayout_Code;
	protected Space space_Code;

	protected Textbox code;
	protected Label label_Description;
	protected Hlayout hlayout_Description;
	protected Space space_Description;

	protected Textbox description;
	protected Row row1;
	protected Label label_Type;
	protected Hlayout hlayout_Type;
	protected Space space_Type;

	protected Textbox type;
	protected Label label_Formula;
	protected Hlayout hlayout_Formula;
	protected Space space_Formula;

	protected Textbox formula;
	protected Row row2;
	protected Label label_DataType;
	protected Hlayout hlayout_DataType;
	protected Space space_DataType;

	protected Combobox dataType;

	protected Label recordType;
	private boolean enqModule = false;

	// not auto wired vars
	private DeviationParam deviationParam; // overhanded per param
	private transient DeviationParamListCtrl deviationParamListCtrl; // overhanded
	// per
	// param

	// ServiceDAOs / Domain Classes
	private transient DeviationParamService deviationParamService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public DeviationParamDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DeviationParamDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected DeviationParam object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_DeviationParamDialog(Event event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DeviationParamDialog);

		try {

			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("deviationParam")) {
				this.deviationParam = (DeviationParam) arguments.get("deviationParam");
				DeviationParam befImage = new DeviationParam();
				BeanUtils.copyProperties(this.deviationParam, befImage);
				this.deviationParam.setBefImage(befImage);

				setDeviationParam(this.deviationParam);
			} else {
				setDeviationParam(null);
			}
			doLoadWorkFlow(this.deviationParam.isWorkflow(), this.deviationParam.getWorkflowId(),
					this.deviationParam.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "DeviationParamDialog");
			} else {
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the deviationParamListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete deviationParam here.
			if (arguments.containsKey("deviationParamListCtrl")) {
				setDeviationParamListCtrl((DeviationParamListCtrl) arguments.get("deviationParamListCtrl"));
			} else {
				setDeviationParamListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDeviationParam());
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
		logger.debug("Entering" + event.toString());
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doDelete();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
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
		logger.debug("Entering" + event.toString());
		doWriteBeanToComponents(this.deviationParam.getBefImage());
		displayComponents(ScreenCTL.SCRN_GNINT);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_DeviationParamDialog);
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
		logger.debug("Entering" + event.toString());
		try {

			ScreenCTL.displayNotes(
					getNotes("DeviationParam", getDeviationParam().getCode(), getDeviationParam().getVersion()), this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());

	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aDeviationParam
	 * @throws InterruptedException
	 */
	public void doShowDialog(DeviationParam aDeviationParam) throws InterruptedException {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doWriteBeanToComponents(aDeviationParam);
			// set ReadOnly mode accordingly if the object is new or not.

			displayComponents(ScreenCTL.getMode(enqModule, isWorkFlowEnabled(), aDeviationParam.isNewRecord()));

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit

	private void displayComponents(int mode) {
		logger.debug("Entering");

		doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(), isFirstTask(),
				this.userAction, this.code, this.code));

		if (getDeviationParam().isNewRecord()) {
			setComponentAccessType("DeviationParamDialog_Code", false, this.code, this.space_Code, this.label_Code,
					this.hlayout_Code, null);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly(boolean readOnly) {
		logger.debug("Entering");

		boolean tempReadOnly = readOnly;
		if (readOnly) {
			tempReadOnly = true;
		} else if (PennantConstants.RECORD_TYPE_DEL.equals(this.deviationParam.getRecordType())) {
			tempReadOnly = true;
		}
		setComponentAccessType("DeviationParamDialog_Code", true, this.code, this.space_Code, this.label_Code,
				this.hlayout_Code, null);
		setComponentAccessType("DeviationParamDialog_Description", tempReadOnly, this.description,
				this.space_Description, this.label_Description, this.hlayout_Description, null);
		setRowInvisible(this.row0, this.hlayout_Code, this.hlayout_Description);
		setComponentAccessType("DeviationParamDialog_Type", tempReadOnly, this.type, this.space_Type, this.label_Type,
				this.hlayout_Type, null);
		setComponentAccessType("DeviationParamDialog_Formula", tempReadOnly, this.formula, this.space_Formula,
				this.label_Formula, this.hlayout_Formula, null);
		setRowInvisible(this.row1, this.hlayout_Type, this.hlayout_Formula);
		setComponentAccessType("DeviationParamDialog_DataType", tempReadOnly, this.dataType, this.space_DataType,
				this.label_DataType, this.hlayout_DataType, null);
		setRowInvisible(this.row2, this.hlayout_DataType, null);
		logger.debug("Leaving");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if (!enqModule) {
			getUserWorkspace().allocateAuthorities(super.pageRightName);
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DeviationParamDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DeviationParamDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DeviationParamDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DeviationParamDialog_btnSave"));
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.code.setMaxlength(20);
		this.description.setMaxlength(50);
		this.type.setMaxlength(20);
		this.formula.setMaxlength(500);

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
	 * @param aDeviationParam DeviationParam
	 */
	public void doWriteBeanToComponents(DeviationParam aDeviationParam) {
		logger.debug("Entering");
		this.code.setValue(aDeviationParam.getCode());
		this.description.setValue(aDeviationParam.getDescription());
		this.type.setValue(DeviationConstants.TY_PRODUCT);
		this.formula.setValue(aDeviationParam.getFormula());
		fillComboBox(this.dataType, aDeviationParam.getDataType(), PennantStaticListUtil.getDeviationDataTypes(), "");
		this.recordStatus.setValue(aDeviationParam.getRecordStatus());
		this.recordType.setValue(PennantJavaUtil.getLabel(aDeviationParam.getRecordType()));
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDeviationParam
	 */
	public void doWriteComponentsToBean(DeviationParam aDeviationParam) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Code
		try {
			aDeviationParam.setCode(this.code.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Description
		try {
			aDeviationParam.setDescription(this.description.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Type
		try {
			aDeviationParam.setType(this.type.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Formula
		try {
			aDeviationParam.setFormula(this.formula.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Data Type
		try {
			this.dataType.getValue();
			aDeviationParam.setDataType(this.dataType.getSelectedItem().getValue().toString());
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

		logger.debug("Leaving");
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		// Code
		if (!this.code.isReadonly()) {
			this.code.setConstraint(new PTStringValidator(Labels.getLabel("label_DeviationParamDialog_Code.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		// Description
		if (!this.description.isReadonly()) {
			this.description.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DeviationParamDialog_Description.value"), null, true));
		}
		// Type
		if (!this.type.isReadonly()) {
			this.type.setConstraint(new PTStringValidator(Labels.getLabel("label_DeviationParamDialog_Type.value"),
					PennantRegularExpressions.REGEX_NAME, true));
		}
		// Formula
		if (!this.formula.isReadonly()) {
			this.formula.setConstraint(
					new PTStringValidator(Labels.getLabel("label_DeviationParamDialog_Formula.value"), null, true));
		}
		// Data Type
		if (!this.dataType.isDisabled()) {
			this.dataType
					.setConstraint(new PTListValidator<ValueLabel>(Labels.getLabel("label_DeviationParamDialog_DataType.value"),
							PennantStaticListUtil.getDeviationDataTypes(), true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.code.setConstraint("");
		this.description.setConstraint("");
		this.type.setConstraint("");
		this.formula.setConstraint("");
		this.dataType.setConstraint("");
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
		this.code.setErrorMessage("");
		this.description.setErrorMessage("");
		this.type.setErrorMessage("");
		this.formula.setErrorMessage("");
		this.dataType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		getDeviationParamListCtrl().search();
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final DeviationParam aDeviationParam = new DeviationParam();
		BeanUtils.copyProperties(getDeviationParam(), aDeviationParam);

		doDelete(aDeviationParam.getCode(), aDeviationParam);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.code.setValue("");
		this.description.setValue("");
		this.type.setValue("");
		this.formula.setValue("");
		this.dataType.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final DeviationParam aDeviationParam = new DeviationParam();
		BeanUtils.copyProperties(getDeviationParam(), aDeviationParam);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aDeviationParam.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aDeviationParam.getNextTaskId(),
					aDeviationParam);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aDeviationParam.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the DeviationParam object with the components data
			doWriteComponentsToBean(aDeviationParam);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aDeviationParam.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDeviationParam.getRecordType())) {
				aDeviationParam.setVersion(aDeviationParam.getVersion() + 1);
				if (isNew) {
					aDeviationParam.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aDeviationParam.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDeviationParam.setNewRecord(true);
				}
			}
		} else {
			aDeviationParam.setVersion(aDeviationParam.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aDeviationParam, tranType)) {
				// doWriteBeanToComponents(aDeviationParam);
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

	protected boolean doProcess(DeviationParam aDeviationParam, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aDeviationParam.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDeviationParam.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDeviationParam.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aDeviationParam.setTaskId(getTaskId());
			aDeviationParam.setNextTaskId(getNextTaskId());
			aDeviationParam.setRoleCode(getRole());
			aDeviationParam.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(getAuditHeader(aDeviationParam, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aDeviationParam, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aDeviationParam, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
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
		boolean deleteNotes = false;

		DeviationParam aDeviationParam = (DeviationParam) auditHeader.getAuditDetail().getModelData();

		while (retValue == PennantConstants.porcessOVERIDE) {

			if (StringUtils.isBlank(method)) {
				if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
					auditHeader = getDeviationParamService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getDeviationParamService().saveOrUpdate(auditHeader);
				}

			} else {
				if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getDeviationParamService().doApprove(auditHeader);

					if (PennantConstants.RECORD_TYPE_DEL.equals(aDeviationParam.getRecordType())) {
						deleteNotes = true;
					}

				} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
					auditHeader = getDeviationParamService().doReject(auditHeader);
					if (PennantConstants.RECORD_TYPE_NEW.equals(aDeviationParam.getRecordType())) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_DeviationParamDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_DeviationParamDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes("DeviationParam", aDeviationParam.getCode(), aDeviationParam.getVersion()),
							true);
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
		logger.debug("Leaving");
		return processCompleted;
	}

	// WorkFlow Components

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(DeviationParam aDeviationParam, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDeviationParam.getBefImage(), aDeviationParam);
		return new AuditHeader(aDeviationParam.getCode(), null, null, null, auditDetail,
				aDeviationParam.getUserDetails(), getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DeviationParam getDeviationParam() {
		return this.deviationParam;
	}

	public void setDeviationParam(DeviationParam deviationParam) {
		this.deviationParam = deviationParam;
	}

	public void setDeviationParamService(DeviationParamService deviationParamService) {
		this.deviationParamService = deviationParamService;
	}

	public DeviationParamService getDeviationParamService() {
		return this.deviationParamService;
	}

	public void setDeviationParamListCtrl(DeviationParamListCtrl deviationParamListCtrl) {
		this.deviationParamListCtrl = deviationParamListCtrl;
	}

	public DeviationParamListCtrl getDeviationParamListCtrl() {
		return this.deviationParamListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

}
