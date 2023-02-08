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
 * * FileName : IRRFeeTypeDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-06-2017 * * Modified
 * Date : 21-06-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-06-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.irrfeetype;

import java.math.RoundingMode;
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
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.service.applicationmaster.IRRCodeService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.applicationmaster.irrcode.IRRCodeDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.receipt.constants.Allocation;

/**
 * This is the controller class for the /WEB-INF/pages/applicationmaster/IRRFeeType/iRRFeeTypeDialog.zul file. <br>
 */
public class IRRFeeTypeDialogCtrl extends GFCBaseCtrl<IRRFeeType> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(IRRFeeTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_IRRFeeTypeDialog;
	// protected ExtendedCombobox iRRID;
	protected ExtendedCombobox feeTypeID;
	protected Decimalbox feePercentage;
	private IRRFeeType iRRFeeType; // overhanded per param

	private transient IRRCodeDialogCtrl irrCodeDialogCtrl; // overhanded per
															// param
	private List<IRRFeeType> irrFeeTypesList;
	private boolean newRecord;
	private IRRCodeService irrCodeService;
	private String userRole = "";
	private boolean isCompReadonly = false;

	/**
	 * default constructor.<br>
	 */
	public IRRFeeTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "IRRFeeTypeDialog";
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.iRRFeeType.getIRRID());
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_IRRFeeTypeDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_IRRFeeTypeDialog);

		try {
			// Get the required arguments.
			this.iRRFeeType = (IRRFeeType) arguments.get("irrfeetype");
			this.irrCodeDialogCtrl = (IRRCodeDialogCtrl) arguments.get("iRRCodeDialogCtrl");
			setNewRecord((boolean) arguments.get("newRecord"));

			if (this.iRRFeeType == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			if (arguments.containsKey("roleCode")) {
				userRole = arguments.get("roleCode").toString();
				getUserWorkspace().allocateRoleAuthorities(userRole, super.pageRightName);
			}

			if (arguments.containsKey("isCompReadonly")) {
				this.isCompReadonly = (boolean) arguments.get("isCompReadonly");
			}

			// Store the before image.
			IRRFeeType iRRFeeType = new IRRFeeType();
			BeanUtils.copyProperties(this.iRRFeeType, iRRFeeType);
			this.iRRFeeType.setBefImage(iRRFeeType);

			// Render the page and display the data.
			doLoadWorkFlow(this.iRRFeeType.isWorkflow(), this.iRRFeeType.getWorkflowId(),
					this.iRRFeeType.getNextTaskId());

			/*
			 * if (isWorkFlowEnabled()) { if (!enqiryModule) { this.userAction = setListRecordStatus(this.userAction); }
			 * getUserWorkspace().allocateAuthorities(this.pageRightName, getRole()); } else {
			 * getUserWorkspace().allocateAuthorities(this.pageRightName, null); }
			 */

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.iRRFeeType);
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

		this.feeTypeID.setMandatoryStyle(true);
		this.feeTypeID.setModuleName("FeeType");
		this.feeTypeID.setValueColumn("FeeTypeCode");
		this.feeTypeID.setDescColumn("FeeTypeDesc");
		this.feeTypeID.setValidateColumns(new String[] { "FeeTypeCode" });

		ArrayList<String> list = new ArrayList<>();
		list.add(Allocation.BOUNCE);
		list.add(Allocation.ODC);
		list.add(Allocation.LPFT);

		Filter[] filters = new Filter[1];
		filters[0] = Filter.notIn("FeeTypeCode", list);
		feeTypeID.setFilters(filters);

		this.feePercentage.setMaxlength(6);
		this.feePercentage.setFormat(PennantApplicationUtil.getAmountFormate(PennantConstants.defaultCCYDecPos));
		this.feePercentage.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.feePercentage.setScale(PennantConstants.defaultCCYDecPos);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(super.pageRightName, userRole);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_IRRFeeTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_IRRFeeTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_IRRFeeTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_IRRFeeTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
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
		doShowNotes(this.iRRFeeType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cancel the actual operation and Resets to the original status
	 */
	private void doCancel() {
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.iRRFeeType.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		logger.debug("Entering");
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_IRRFeeTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param iRRFeeType
	 * 
	 */
	public void doWriteBeanToComponents(IRRFeeType aIRRFeeType) {
		logger.debug(Literal.ENTERING);

		this.feeTypeID.setValue(aIRRFeeType.getFeeTypeCode());
		this.feePercentage.setValue(aIRRFeeType.getFeePercentage());

		if (aIRRFeeType.isNewRecord()) {
			this.feeTypeID.setDescription("");
		} else {
			this.feeTypeID.setDescription(aIRRFeeType.getFeeTypeCode());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aIRRFeeType
	 */
	public void doWriteComponentsToBean(IRRFeeType aIRRFeeType) {
		logger.debug(Literal.LEAVING);

		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// Fee Type I D
		try {
			this.feeTypeID.getValidatedValue();
			Object obj = this.feeTypeID.getAttribute("FeeTypeID");
			if (obj != null) {
				aIRRFeeType.setIRRID(Long.valueOf(String.valueOf(obj)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fee Percentage
		try {
			aIRRFeeType.setFeePercentage(this.feePercentage.getValue());
			// aIRRFeeType.setFeePercentage(new
			// BigDecimal(PennantApplicationUtil.formatRate(this.feePercentage.getValue().doubleValue(),2)));
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
		setiRRFeeType(aIRRFeeType);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * onChanging Branch
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$feeTypeID(Event event) throws InterruptedException {
		logger.debug("Entering");

		Object dataObject = feeTypeID.getObject();

		if (dataObject instanceof String) {
			this.feeTypeID.setValue("");
			this.feeTypeID.setDescription("");
		} else {
			FeeType irrFeeType = (FeeType) dataObject;
			if (irrFeeType != null) {
				iRRFeeType.setFeeTypeID(Long.valueOf(irrFeeType.getFeeTypeID()));
				iRRFeeType.setFeeTypeCode(irrFeeType.getFeeTypeCode());
				iRRFeeType.setFeeTypeDesc(irrFeeType.getFeeTypeDesc());
			}
		}
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param iRRFeeType The entity that need to be render.
	 */
	public void doShowDialog(IRRFeeType iRRFeeType) {
		logger.debug(Literal.LEAVING);

		if (isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.feeTypeID.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		if (enqiryModule) {
			this.btnCtrl.setBtnStatus_Enquiry();
			this.btnNotes.setVisible(false);
		}

		doWriteBeanToComponents(iRRFeeType);
		this.window_IRRFeeTypeDialog.doModal();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.LEAVING);

		if (!this.feeTypeID.isReadonly()) {
			this.feeTypeID
					.setConstraint(new PTStringValidator(Labels.getLabel("label_IRRFeeTypeDialog_FeeTypeID.value"),
							PennantRegularExpressions.REGEX_NAME, true));
		}
		if (!this.feePercentage.isReadonly()) {
			this.feePercentage
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_IRRFeeTypeDialog_FeePercentage.value"),
							PennantConstants.defaultCCYDecPos, true, false, 0, 100));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.LEAVING);

		this.feeTypeID.setConstraint("");
		this.feePercentage.setConstraint("");

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

	protected boolean doCustomDelete(final IRRFeeType aIRRFeeType, String tranType) {
		if (aIRRFeeType.isNewRecord()) {
			tranType = PennantConstants.TRAN_DEL;
			AuditHeader auditHeader = newIRRFeeTypeDetailProcess(aIRRFeeType, tranType);
			auditHeader = ErrorControl.showErrorDetails(this.window_IRRFeeTypeDialog, auditHeader);
			int retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
				if (getIrrCodeDialogCtrl() != null) {
					getIrrCodeDialogCtrl().doFillIRRFeeTypeDetails(this.irrFeeTypesList);
				}
				return true;
			}
		}

		return false;
	}

	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final IRRFeeType aIRRFeeType = new IRRFeeType();
		BeanUtils.copyProperties(getiRRFeeType(), aIRRFeeType);

		doDelete(aIRRFeeType.getFeeTypeCode(), aIRRFeeType);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug(Literal.LEAVING);

		if (isNewRecord()) {
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.feeTypeID);
		} else {
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.feeTypeID);
		}
		readOnlyComponent(isReadOnly("IRRFeeTypeDialog_FeePercentage"), this.feePercentage);
		readOnlyComponent(isCompReadonly, this.feePercentage);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.iRRFeeType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			if (isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(true);
			}
		}

		this.btnSave.setVisible(!isCompReadonly);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.LEAVING);

		readOnlyComponent(true, this.feeTypeID);
		readOnlyComponent(true, this.feePercentage);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
			this.userAction.setSelectedIndex(0);

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		this.feeTypeID.setValue("");
		this.feeTypeID.setDescription("");
		this.feePercentage.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final IRRFeeType aIRRFeeType = new IRRFeeType();
		BeanUtils.copyProperties(this.iRRFeeType, aIRRFeeType);
		boolean isNew = false;

		doSetValidation();
		doWriteComponentsToBean(aIRRFeeType);

		isNew = aIRRFeeType.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aIRRFeeType.getRecordType())) {
				aIRRFeeType.setVersion(aIRRFeeType.getVersion() + 1);
				aIRRFeeType.setNewRecord(true);
				if (isNew) {
					aIRRFeeType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aIRRFeeType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aIRRFeeType.setNewRecord(true);
				}
			}
		} else {
			if (isNewRecord()) {
				aIRRFeeType.setVersion(1);
				aIRRFeeType.setRecordType(PennantConstants.RCD_ADD);
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
			if (StringUtils.isBlank(aIRRFeeType.getRecordType())) {
				aIRRFeeType.setVersion(aIRRFeeType.getVersion() + 1);
				aIRRFeeType.setRecordType(PennantConstants.RCD_UPD);
				aIRRFeeType.setNewRecord(true);
			}
			if (aIRRFeeType.getRecordType().equals(PennantConstants.RCD_ADD) && isNewRecord()) {
				tranType = PennantConstants.TRAN_ADD;
			} else if (aIRRFeeType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (aIRRFeeType.isNewRecord()) {
				AuditHeader auditHeader = newIRRFeeTypeDetailProcess(aIRRFeeType, tranType);
				auditHeader = ErrorControl.showErrorDetails(this.window_IRRFeeTypeDialog, auditHeader);
				int retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE || retValue == PennantConstants.porcessOVERIDE) {
					if (getIrrCodeDialogCtrl() != null) {
						getIrrCodeDialogCtrl().doFillIRRFeeTypeDetails(this.irrFeeTypesList);
					}
					closeDialog();
				}
			}
		} catch (final DataAccessException e) {
			logger.error("Exception: ", e);
			showMessage(e);
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
	private AuditHeader newIRRFeeTypeDetailProcess(IRRFeeType aIRRFeeType, String tranType) {

		boolean recordAdded = false;
		boolean duplicateRecord = false;

		AuditHeader auditHeader = getAuditHeader(aIRRFeeType, tranType);
		this.irrFeeTypesList = new ArrayList<IRRFeeType>();

		String[] valueParm = new String[2];
		String[] errParm = new String[2];
		valueParm[0] = aIRRFeeType.getFeeTypeCode();
		errParm[0] = PennantJavaUtil.getLabel("label_IRRFeeType") + ": " + valueParm[0];

		List<IRRFeeType> feeTypesList = null;
		if (getIrrCodeDialogCtrl() != null) {
			feeTypesList = getIrrCodeDialogCtrl().getIrrFeeTypesList();
		}
		if (feeTypesList != null && !feeTypesList.isEmpty()) {
			for (IRRFeeType details : feeTypesList) {
				if (aIRRFeeType.getFeeTypeID() == details.getFeeTypeID()) {
					duplicateRecord = true;
				}
				if (duplicateRecord) {
					if (isNewRecord()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm),
								getUserWorkspace().getUserLanguage()));
						return auditHeader;
					}
					if (PennantConstants.TRAN_DEL.equals(tranType)) {
						if (PennantConstants.RECORD_TYPE_UPD.equals(details.getRecordType())) {
							details.setRecordType(PennantConstants.RECORD_TYPE_DEL);
							recordAdded = true;
							this.irrFeeTypesList.add(details);
						} else if (PennantConstants.RCD_ADD.equals(details.getRecordType())) {
							recordAdded = true;
						} else if (PennantConstants.RECORD_TYPE_NEW.equals(details.getRecordType())) {
							details.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							recordAdded = true;
							this.irrFeeTypesList.add(details);
						} else if (PennantConstants.RECORD_TYPE_CAN.equals(details.getRecordType())) {
							recordAdded = true;
						}
					}
				} else {
					this.irrFeeTypesList.add(details);
				}
				duplicateRecord = false;
			}
		}
		if (PennantConstants.TRAN_UPD.equals(tranType)) {
			this.irrFeeTypesList.add(aIRRFeeType);
			recordAdded = true;
		}
		if (!recordAdded) {
			this.irrFeeTypesList.add(aIRRFeeType);
		}
		return auditHeader;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(IRRFeeType aIRRFeeType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aIRRFeeType.getBefImage(), aIRRFeeType);
		return new AuditHeader(getReference(), null, null, null, auditDetail, aIRRFeeType.getUserDetails(),
				getOverideMap());
	}

	public IRRCodeDialogCtrl getIrrCodeDialogCtrl() {
		return irrCodeDialogCtrl;
	}

	public void setIrrCodeDialogCtrl(IRRCodeDialogCtrl irrCodeDialogCtrl) {
		this.irrCodeDialogCtrl = irrCodeDialogCtrl;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public IRRFeeType getiRRFeeType() {
		return iRRFeeType;
	}

	public void setiRRFeeType(IRRFeeType iRRFeeType) {
		this.iRRFeeType = iRRFeeType;
	}

	public IRRCodeService getIrrCodeService() {
		return irrCodeService;
	}

	public void setIrrCodeService(IRRCodeService irrCodeService) {
		this.irrCodeService = irrCodeService;
	}

}
