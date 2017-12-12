/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  VesselDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-05-2015    														*
 *                                                                  						*
 * Modified Date    :  12-05-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-05-2015       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.vesseldetails;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.applicationmaster.VesselDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.applicationmaster.VesselDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the
 * /WEB-INF/pages/LMTMasters/VesselDetail/vesselDetailDialog.zul file.
 */
public class VesselDetailDialogCtrl extends GFCBaseCtrl<VesselDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger
			.getLogger(VesselDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting by our 'extends
	 * GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_VesselDetailDialog;
	protected Row row0;

	protected Textbox vesselTypeID;
	protected Space space_VesselTypeID;

	protected ExtendedCombobox vesselType;

	protected Space space_VesselSubType;
	protected Textbox vesselSubType;

	protected Row row1;
	protected Space space_IsActive;
	protected Checkbox active;
	protected Label recordType;

	// not auto wired vars
	private VesselDetail vesselDetail; // overhanded per param
	private transient VesselDetailListCtrl vesselDetailListCtrl; // overhanded
																	// per param

	// ServiceDAOs / Domain Classes
	private transient VesselDetailService vesselDetailService;

	/**
	 * default constructor.<br>
	 */
	public VesselDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VesselDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected VesselDetail object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VesselDetailDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_VesselDetailDialog);

		try {
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			if (arguments.containsKey("vesselDetail")) {
				this.vesselDetail = (VesselDetail) arguments
						.get("vesselDetail");
				VesselDetail befImage = new VesselDetail();
				BeanUtils.copyProperties(this.vesselDetail, befImage);
				this.vesselDetail.setBefImage(befImage);

				setVesselDetail(this.vesselDetail);
			} else {
				setVesselDetail(null);
			}
			doLoadWorkFlow(this.vesselDetail.isWorkflow(),
					this.vesselDetail.getWorkflowId(),
					this.vesselDetail.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),
						"VesselDialog");
			}

			// READ OVERHANDED params !
			// we get the vesselDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit
			// or
			// delete vesselDetail here.
			if (arguments.containsKey("vesselDetailListCtrl")) {
				setVesselDetailListCtrl((VesselDetailListCtrl) arguments
						.get("vesselDetailListCtrl"));
			} else {
				setVesselDetailListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVesselDetail());
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
		doEdit();
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
		doCancel();
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
		MessageUtil.showHelpWindow(event, window_VesselDetailDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event
	 *            (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.vesselDetail);
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aVesselDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(VesselDetail aVesselDetail)
			throws InterruptedException {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aVesselDetail.isNew()) {
			this.vesselTypeID.setVisible(true);
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.vesselTypeID.focus();
		} else {
			this.vesselTypeID.setReadonly(true);
			this.vesselType.focus();
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aVesselDetail.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		try {

			// fill the components with the data
			doWriteBeanToComponents(aVesselDetail);

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.vesselType.setReadonly(true);
		this.vesselSubType.setReadonly(true);
		this.active.setDisabled(true);

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

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getVesselDetail().isNewRecord()) {
			this.vesselTypeID.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.vesselTypeID.setReadonly(true);
			this.btnCancel.setVisible(true);
		}
		this.vesselType.setReadonly(isReadOnly("VesselDialog_vesselType"));
		this.vesselSubType
				.setReadonly(isReadOnly("VesselDialog_vesselSubType"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vesselDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			// btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}

	// Helpers

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnNew.setVisible(getUserWorkspace().isAllowed(
				"button_VesselDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed(
				"button_VesselDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed(
				"button_VesselDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed(
				"button_VesselDialog_btnSave"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Empty sent any required attributes
		this.vesselSubType.setMaxlength(100);
		this.vesselTypeID.setMaxlength(8);
		this.vesselType.setInputAllowed(false);
		this.vesselType.setDisplayStyle(3);
		this.vesselType.setMandatoryStyle(true);
		this.vesselType.setModuleName("VesselType");
		this.vesselType.setValueColumn("FieldCodeValue");
		this.vesselType.setDescColumn("ValueDesc");
		this.vesselType.setValidateColumns(new String[] { "FieldCodeValue" });
		this.vesselType.setTextBoxWidth(180);
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Get the Reference value
	 */
	@Override
	protected String getReference() {
		return getVesselDetail().getVesselTypeID();
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering ");
		doWriteBeanToComponents(this.vesselDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving ");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVesselDetail
	 *            VesselDetail
	 */
	public void doWriteBeanToComponents(VesselDetail aVesselDetail) {
		logger.debug("Entering");
		this.vesselType.setValue(aVesselDetail.getVesselType());
		this.vesselType.setDescription(aVesselDetail.getVesselTypeName());
		this.vesselTypeID.setValue(aVesselDetail.getVesselTypeID());
		this.vesselSubType.setValue(aVesselDetail.getVesselSubType());
		this.active.setChecked(aVesselDetail.isActive());
		this.recordStatus.setValue(aVesselDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVesselDetail
	 */
	public void doWriteComponentsToBean(VesselDetail aVesselDetail) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		// Vessel Type Id
		try {
			aVesselDetail.setVesselTypeID(this.vesselTypeID.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Vessel Type
		try {
			aVesselDetail.setVesselType(this.vesselType.getValue());
			aVesselDetail.setVesselTypeName(this.vesselType.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Vessel Sub Type
		try {
			aVesselDetail.setVesselSubType(this.vesselSubType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Is Active
		try {
			aVesselDetail.setActive(this.active.isChecked());
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
		// Vessel TypeID
		if (!this.vesselTypeID.isReadonly()) {
			this.vesselTypeID.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VesselDetailDialog_VesselTypeID.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		// Vessel Sub Type
		if (!this.vesselSubType.isReadonly()) {
			this.vesselSubType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VesselDetailDialog_VesselSubType.value"),
					PennantRegularExpressions.REGEX_ALPHANUM_SPACE, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.vesselTypeID.setConstraint("");
		this.vesselSubType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		// Vessel Type
		this.vesselType.setConstraint(new PTStringValidator(Labels
				.getLabel("label_VesselDetailDialog_VesselType.value"), null,
				true, true));
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
		this.vesselType.setConstraint("");
	}

	/**
	 * Remove Error Messages for Fields
	 */

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.vesselTypeID.setErrorMessage("");
		this.vesselType.setErrorMessage("");
		this.vesselSubType.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getVesselDetailListCtrl().search();
	}

	/**
	 * Deletes a VesselDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final VesselDetail aVesselDetail = new VesselDetail();
		BeanUtils.copyProperties(getVesselDetail(), aVesselDetail);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_VesselDetailDialog_VesselTypeID.value")
				+ " : " + aVesselDetail.getVesselTypeID();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aVesselDetail.getRecordType())) {
				aVesselDetail.setVersion(aVesselDetail.getVersion() + 1);
				aVesselDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aVesselDetail.setRecordStatus(userAction.getSelectedItem()
							.getValue().toString());
					aVesselDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
							aVesselDetail.getNextTaskId(), aVesselDetail);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aVesselDetail, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		this.vesselTypeID.setValue("");
		this.vesselType.setValue("");
		this.vesselType.setDescription("");
		this.vesselSubType.setValue("");
		this.active.setChecked(false);
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VesselDetail aVesselDetail = new VesselDetail();
		BeanUtils.copyProperties(getVesselDetail(), aVesselDetail);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aVesselDetail.setRecordStatus(userAction.getSelectedItem()
					.getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(),
					aVesselDetail.getNextTaskId(), aVesselDetail);
		}

		// force validation, if on, than execute by component.getValue()
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aVesselDetail
				.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the VesselDetail object with the components data
			doWriteComponentsToBean(aVesselDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVesselDetail.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aVesselDetail.getRecordType())) {
				aVesselDetail.setVersion(aVesselDetail.getVersion() + 1);
				if (isNew) {
					aVesselDetail
							.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVesselDetail
							.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVesselDetail.setNewRecord(true);
				}
			}
		} else {
			aVesselDetail.setVersion(aVesselDetail.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aVesselDetail, tranType)) {
				// doWriteBeanToComponents(aVesselDetail);
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
	 * @param aAuthorizedSignatoryRepository
	 *            (AuthorizedSignatoryRepository)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */

	private boolean doProcess(VesselDetail aVesselDetail, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aVesselDetail.setLastMntBy(getUserWorkspace().getLoggedInUser()
				.getLoginUsrID());
		aVesselDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVesselDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aVesselDetail.setTaskId(getTaskId());
			aVesselDetail.setNextTaskId(getNextTaskId());
			aVesselDetail.setRoleCode(getRole());
			aVesselDetail.setNextRoleCode(getNextRoleCode());

			if (StringUtils.isBlank(getOperationRefs())) {
				processCompleted = doSaveProcess(
						getAuditHeader(aVesselDetail, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aVesselDetail,
						PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(
					getAuditHeader(aVesselDetail, tranType), null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
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
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		VesselDetail aVesselDetail = (VesselDetail) auditHeader
				.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader
							.getAuditTranType())) {
						auditHeader = getVesselDetailService().delete(
								auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getVesselDetailService().saveOrUpdate(
								auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove
							.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getVesselDetailService().doApprove(
								auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL
								.equals(aVesselDetail.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject
							.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getVesselDetailService().doReject(
								auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW
								.equals(aVesselDetail.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						// auditHeader.setErrorDetails(new
						// ErrorDetails(PennantConstants.ERR_9999,
						// Labels.getLabel("InvalidWorkFlowMethod"),
						// null,PennantConstants.ERR_SEV_ERROR));
						retValue = ErrorControl.showErrorControl(
								this.window_VesselDetailDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(
						this.window_VesselDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						// deleteNotes(getNotes("VesselDetail",aVesselDetail.getVesselTypeID(),aVesselDetail.getVersion()),true);
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

	private AuditHeader getAuditHeader(VesselDetail aVesselDetail,
			String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,
				aVesselDetail.getBefImage(), aVesselDetail);
		return new AuditHeader(String.valueOf(aVesselDetail.getVesselTypeID()),
				null, null, null, auditDetail, aVesselDetail.getUserDetails(),
				getOverideMap());
	}

	public void onFulfill$vesselType(Event event) {
		logger.debug("Entering" + event.toString());

		Object dataObject = vesselType.getObject();
		if (dataObject instanceof String) {
			/* this.vesselType.setValue(String.valueOf(new Long(0))); */
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.vesselType.setValue(String.valueOf(details
						.getFieldCodeId()));
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public VesselDetail getVesselDetail() {
		return this.vesselDetail;
	}

	public void setVesselDetail(VesselDetail vesselDetail) {
		this.vesselDetail = vesselDetail;
	}

	public void setVesselDetailService(VesselDetailService vesselDetailService) {
		this.vesselDetailService = vesselDetailService;
	}

	public VesselDetailService getVesselDetailService() {
		return this.vesselDetailService;
	}

	public void setVesselDetailListCtrl(
			VesselDetailListCtrl vesselDetailListCtrl) {
		this.vesselDetailListCtrl = vesselDetailListCtrl;
	}

	public VesselDetailListCtrl getVesselDetailListCtrl() {
		return this.vesselDetailListCtrl;
	}

}
