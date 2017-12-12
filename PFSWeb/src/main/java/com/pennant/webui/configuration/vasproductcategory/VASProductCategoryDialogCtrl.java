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
 * FileName    		:  VASProductCategoryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  09-01-2017    														*
 *                                                                  						*
 * Modified Date    :  09-01-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 09-01-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.webui.configuration.vasproductcategory;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.service.vasproduct.VASProductCategoryService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/vasproduct/VASProductCategory/vASProductCategoryDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class VASProductCategoryDialogCtrl extends GFCBaseCtrl<VASProductCategory> {

	private static final long						serialVersionUID	= 1L;
	private static final Logger						logger				= Logger.getLogger(VASProductCategoryDialogCtrl.class);

	
	protected Window								window_VASProductCategoryDialog;
	protected Row									row0;
	protected Label									label_ProductCtg;
	protected Space									space_ProductCtg;

	protected Textbox								productCtg;
	protected Label									label_ProductCtgDesc;
	protected Space									space_ProductCtgDesc;

	protected Textbox								productCtgDesc;
	protected Checkbox 								active;

	protected Label									recordType;
	protected Groupbox								gb_statusDetails;
	private boolean									enqModule			= false;

	private VASProductCategory						vASProductCategory;
	private transient VASProductCategoryListCtrl	vASProductCategoryListCtrl;

	private transient VASProductCategoryService		vASProductCategoryService;

	/**
	 * default constructor.<br>
	 */
	public VASProductCategoryDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VASProductCategoryDialog";
	}


	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected VASProductCategory object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VASProductCategoryDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			setPageComponents(window_VASProductCategoryDialog);

			// get the params map that are overhanded by creation.
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("vASProductCategory")) {
				this.vASProductCategory = (VASProductCategory) arguments.get("vASProductCategory");
				VASProductCategory befImage = new VASProductCategory();
				BeanUtils.copyProperties(this.vASProductCategory, befImage);
				this.vASProductCategory.setBefImage(befImage);
				setVASProductCategory(this.vASProductCategory);
			} else {
				setVASProductCategory(null);
			}

			doLoadWorkFlow(this.vASProductCategory.isWorkflow(), this.vASProductCategory.getWorkflowId(),
					this.vASProductCategory.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "VASProductCategoryDialog");
			} else {
				if(!enqModule){
					getUserWorkspace().allocateAuthorities("VASProductCategoryDialog");
				}
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the vASProductCategoryListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete vASProductCategory here.
			if (arguments.containsKey("vASProductCategoryListCtrl")) {
				setVASProductCategoryListCtrl((VASProductCategoryListCtrl) arguments.get("vASProductCategoryListCtrl"));
			} else {
				setVASProductCategoryListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVASProductCategory());
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
		MessageUtil.showHelpWindow(event, window_VASProductCategoryDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doClose(this.btnSave.isVisible());
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_VASProductCategoryDialog(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
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
		logger.debug("Entering" + event.toString());
		doShowNotes(this.vASProductCategory);
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVASProductCategory
	 * @throws InterruptedException
	 */
	public void doShowDialog(VASProductCategory aVASProductCategory) throws InterruptedException {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (vASProductCategory.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.productCtg.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.productCtgDesc.focus();
				if (StringUtils.isNotBlank(aVASProductCategory.getRecordType())) {
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
		doWriteBeanToComponents(aVASProductCategory);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.productCtg);
		readOnlyComponent(true, this.productCtgDesc);
		readOnlyComponent(true, this.active);

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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		if(!enqiryModule){
			getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VASProductCategoryDialog_btnNew"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VASProductCategoryDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VASProductCategoryDialog_btnSave"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VASProductCategoryDialog_btnEdit"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.productCtg.setMaxlength(8);
		this.productCtgDesc.setMaxlength(50);

		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVASProductCategory
	 *            VASProductCategory
	 */
	public void doWriteBeanToComponents(VASProductCategory aVASProductCategory) {
		logger.debug("Entering");
		
		this.productCtg.setValue(aVASProductCategory.getProductCtg());
		this.productCtgDesc.setValue(aVASProductCategory.getProductCtgDesc());
		 this.active.setChecked(aVASProductCategory.isActive());

		this.recordStatus.setValue(aVASProductCategory.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVASProductCategory
	 */
	public void doWriteComponentsToBean(VASProductCategory aVASProductCategory) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Product Ctg
		try {
			aVASProductCategory.setProductCtg(this.productCtg.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Product Ctg Desc
		try {
			aVASProductCategory.setProductCtgDesc(this.productCtgDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Active
		try {
			aVASProductCategory.setActive(this.active.isChecked());
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
		//Product Ctg
		if (!this.productCtg.isReadonly()) {
			this.productCtg.setConstraint(new PTStringValidator(Labels.getLabel("label_VASProductCategoryDialog_ProductCtg.value"), 
					PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}
		//Product Ctg Desc
		if (!this.productCtgDesc.isReadonly()) {
			this.productCtgDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_VASProductCategoryDialog_ProductCtgDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.productCtg.setConstraint("");
		this.productCtgDesc.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		//Product Ctg
		//Product Ctg Desc
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */

	private void doRemoveLOVValidation() {
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList() {
		vASProductCategoryListCtrl.search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.vASProductCategory.getProductCtg());
	}
	
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.vASProductCategory.isNewRecord()) {
			this.productCtg.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.productCtg.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.productCtgDesc.setReadonly(isReadOnly("VASProductCategoryDialog_ProductCtgDesc"));
		int count = vASProductCategoryService.getVASProductCategoryByACtive(this.vASProductCategory.getProductCtg());
		
		if (count > 0) {
			this.active.setDisabled(true);
		} else if (vASProductCategory.isNew()) {
			this.active.setDisabled(true);
		} else {
			this.active.setDisabled(isReadOnly("VASProductCategoryDialog_Active"));
		}
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASProductCategory.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		if(count > 0){
			this.btnDelete.setVisible(false);
		}

		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.vASProductCategory.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering");
		boolean close = true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				doSave();
				close = false;
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog();
		}

		logger.debug("Leaving");
	}

	/**
	 * Deletes a VASProductCategory object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final VASProductCategory aVASProductCategory = new VASProductCategory();
		BeanUtils.copyProperties(getVASProductCategory(), aVASProductCategory);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aVASProductCategory.getProductCtg();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aVASProductCategory.getRecordType()).equals("")) {
				aVASProductCategory.setVersion(aVASProductCategory.getVersion() + 1);
				aVASProductCategory.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aVASProductCategory.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aVASProductCategory.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aVASProductCategory.getNextTaskId(),
							aVASProductCategory);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aVASProductCategory, tranType)) {
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

		this.productCtg.setValue("");
		this.productCtgDesc.setValue("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VASProductCategory aVASProductCategory = new VASProductCategory();
		BeanUtils.copyProperties(getVASProductCategory(), aVASProductCategory);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aVASProductCategory.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aVASProductCategory.getNextTaskId(),
					aVASProductCategory);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aVASProductCategory.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the VASProductCategory object with the components data
			doWriteComponentsToBean(aVASProductCategory);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVASProductCategory.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aVASProductCategory.getRecordType()).equals("")) {
				aVASProductCategory.setVersion(aVASProductCategory.getVersion() + 1);
				if (isNew) {
					aVASProductCategory.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASProductCategory.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASProductCategory.setNewRecord(true);
				}
			}
		} else {
			aVASProductCategory.setVersion(aVASProductCategory.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aVASProductCategory, tranType)) {
				refreshList();
				closeDialog();
			}

		} catch (DataAccessException e) {
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
	private boolean doProcess(VASProductCategory aVASProductCategory, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aVASProductCategory.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aVASProductCategory.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVASProductCategory.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}

				}
			}

			aVASProductCategory.setTaskId(getTaskId());
			aVASProductCategory.setNextTaskId(getNextTaskId());
			aVASProductCategory.setRoleCode(getRole());
			aVASProductCategory.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aVASProductCategory, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aVASProductCategory, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aVASProductCategory, tranType), null);
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

		VASProductCategory aVASProductCategory = (VASProductCategory) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getVASProductCategoryService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getVASProductCategoryService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getVASProductCategoryService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aVASProductCategory.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getVASProductCategoryService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aVASProductCategory.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VASProductCategoryDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_VASProductCategoryDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes("VASProductCategory", aVASProductCategory.getProductCtg(),aVASProductCategory.getVersion()), true);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ WorkFlow Components+++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(VASProductCategory aVASProductCategory, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASProductCategory.getBefImage(), aVASProductCategory);
		return new AuditHeader(aVASProductCategory.getProductCtg(), null, null, null, auditDetail,
				aVASProductCategory.getUserDetails(), getOverideMap());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public VASProductCategory getVASProductCategory() {
		return this.vASProductCategory;
	}
	public void setVASProductCategory(VASProductCategory vASProductCategory) {
		this.vASProductCategory = vASProductCategory;
	}

	public void setVASProductCategoryService(VASProductCategoryService vASProductCategoryService) {
		this.vASProductCategoryService = vASProductCategoryService;
	}
	public VASProductCategoryService getVASProductCategoryService() {
		return this.vASProductCategoryService;
	}

	public void setVASProductCategoryListCtrl(VASProductCategoryListCtrl vASProductCategoryListCtrl) {
		this.vASProductCategoryListCtrl = vASProductCategoryListCtrl;
	}
	public VASProductCategoryListCtrl getVASProductCategoryListCtrl() {
		return this.vASProductCategoryListCtrl;
	}

}
