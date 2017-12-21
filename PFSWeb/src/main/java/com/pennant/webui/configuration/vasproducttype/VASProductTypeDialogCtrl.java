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
 * FileName    		:  VASProductTypeDialogCtrl.java                                                   * 	  
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
package com.pennant.webui.configuration.vasproducttype;

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

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennant.backend.service.vasproducttype.VASProductTypeService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/VASProductType/VASProductType/vASProductTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class VASProductTypeDialogCtrl extends GFCBaseCtrl<VASProductType> {

	private static final long					serialVersionUID	= 1L;
	private static final Logger					logger				= Logger.getLogger(VASProductTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window							window_VASProductTypeDialog;
	protected Row								row0;
	protected Label								label_ProductType;
	protected Space								space_ProductType;

	protected Textbox							productType;
	protected Label								label_ProductTypeDesc;
	protected Space								space_ProductTypeDesc;

	protected Textbox							productTypeDesc;
	protected Row								row1;
	protected Label								label_ProductCtg;
	protected Space								space_ProductCtg;

	protected ExtendedCombobox					productCtg;
	protected Checkbox							active;

	protected Label								recordType;
	protected Groupbox							gb_statusDetails;
	private boolean								enqModule			= false;

	private VASProductType						vASProductType;
	private transient VASProductTypeListCtrl	vASProductTypeListCtrl;

	private transient VASProductTypeService		vASProductTypeService;

	/**
	 * default constructor.<br>
	 */
	public VASProductTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "VASProductTypeDialog";
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected VASProductType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_VASProductTypeDialog(Event event) throws Exception {
		logger.debug("Entring" + event.toString());
		try {
			setPageComponents(window_VASProductTypeDialog);
			if (arguments.containsKey("enqModule")) {
				enqModule = (Boolean) arguments.get("enqModule");
			} else {
				enqModule = false;
			}

			// READ OVERHANDED params !
			if (arguments.containsKey("vASProductType")) {
				this.vASProductType = (VASProductType) arguments.get("vASProductType");
				VASProductType befImage = new VASProductType();
				BeanUtils.copyProperties(this.vASProductType, befImage);
				this.vASProductType.setBefImage(befImage);

				setVASProductType(this.vASProductType);
			} else {
				setVASProductType(null);
			}
			doLoadWorkFlow(this.vASProductType.isWorkflow(), this.vASProductType.getWorkflowId(),
					this.vASProductType.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "VASProductTypeDialog");
			} else {
				getUserWorkspace().allocateAuthorities("VASProductTypeDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the vASProductTypeListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete vASProductType here.
			if (arguments.containsKey("vASProductTypeListCtrl")) {
				setVASProductTypeListCtrl((VASProductTypeListCtrl) arguments.get("vASProductTypeListCtrl"));
			} else {
				setVASProductTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getVASProductType());
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
		doCancel();
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		MessageUtil.showHelpWindow(event, window_VASProductTypeDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_VASProductTypeDialog(Event event) throws Exception {
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
		doShowNotes(this.vASProductType);

	}

	public void onFulfill$productCtg(Event event) {
		logger.debug("Entering");

		Object dataObject = productCtg.getObject();

		if (dataObject instanceof String) {
			this.productCtg.setValue(dataObject.toString());
			
		} else {
			VASProductCategory details = (VASProductCategory) dataObject;

			if (details != null) {
				this.productCtg.setAttribute("productCtg", details.getProductCtg());
				this.productCtg.setValue(details.getProductCtg());
				this.productCtg.setDescription(details.getProductCtgDesc());
			}
		}

		logger.debug("Leaving");
	
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param aVASProductType
	 * @throws InterruptedException
	 */
	public void doShowDialog(VASProductType aVASProductType) throws InterruptedException {
		// set ReadOnly mode accordingly if the object is new or not.
		if (vASProductType.isNew()) {
			this.btnCtrl.setInitNew();
			this.active.setDisabled(true);
			doEdit();
			// setFocus
			this.productType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				if (StringUtils.isNotBlank(aVASProductType.getRecordType())) {
					this.productCtg.focus();
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
		doWriteBeanToComponents(aVASProductType);
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");

	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");

		readOnlyComponent(true, this.productType);
		readOnlyComponent(true, this.productTypeDesc);
		readOnlyComponent(true, this.productCtg);
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
		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_VASProductTypeDialog_btnNew"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_VASProductTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_VASProductTypeDialog_btnSave"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_VASProductTypeDialog_btnEdit"));

		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Empty sent any required attributes
		this.productType.setMaxlength(8);
		this.productTypeDesc.setMaxlength(50);
		
		this.productCtg.setModuleName("VASProductCategory");
		this.productCtg.setMandatoryStyle(true);
		this.productCtg.setValueColumn("productCtg");
		this.productCtg.setDescColumn("ProductCtgDesc");
		this.productCtg.setDisplayStyle(2);
		this.productCtg.setValidateColumns(new String[] { "productCtg", "ProductCtgDesc" });
		Filter[] filtersProductCtg = new Filter[1] ;
		filtersProductCtg[0]= new Filter("Active", 1, Filter.OP_EQUAL);
		this.productCtg.setFilters(filtersProductCtg);

		setStatusDetails();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aVASProductType
	 *            VASProductType
	 */
	public void doWriteBeanToComponents(VASProductType aVASProductType) {
		logger.debug("Entering");
		this.productType.setValue(aVASProductType.getProductType());
		this.productTypeDesc.setValue(aVASProductType.getProductTypeDesc());

		this.productCtg.setAttribute("productCtg", aVASProductType.getProductCtg());
		this.productCtg.setValue(aVASProductType.getProductCtg(),aVASProductType.getProductCtgDesc());
		this.active.setChecked(aVASProductType.isActive());
		
		this.recordStatus.setValue(aVASProductType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aVASProductType
	 */
	public void doWriteComponentsToBean(VASProductType aVASProductType) {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Product Type
		try {
			aVASProductType.setProductType(this.productType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Product Type Desc
		try {
			aVASProductType.setProductTypeDesc(this.productTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Product Ctg
		try {
			this.productCtg.getValidatedValue();
			String productCtg = String.valueOf(this.productCtg.getAttribute("productCtg"));
			aVASProductType.setProductCtg(productCtg);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Active
		try {
			aVASProductType.setActive(this.active.isChecked());
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
		//Product Type
		if (!this.productType.isReadonly()) {
			this.productType.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VASProductTypeDialog_ProductType.value"), PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE,
					true));
		}
		//Product Type Desc
		if (!this.productTypeDesc.isReadonly()) {
			this.productTypeDesc.setConstraint(new PTStringValidator(Labels
					.getLabel("label_VASProductTypeDialog_ProductTypeDesc.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		//Product Ctg
		if (!this.productCtg.isReadonly()) {
			this.productCtg.setConstraint(new PTStringValidator(Labels.getLabel("label_VASProductTypeDialog_ProductCtg.value"),
					null, true,true));
			
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.productType.setConstraint("");
		this.productTypeDesc.setConstraint("");
		this.productCtg.setConstraint("");
		logger.debug("Leaving");
	}
	
	/**
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.productType.setErrorMessage("");
		this.productTypeDesc.setErrorMessage("");
		this.productCtg.setErrorMessage("");
		logger.debug("Leaving");
	}

	
	/**
	 * Set Validations for LOV Fields
	 */

	private void doSetLOVValidation() {
		//Product Type
		//Product Type Desc
		//Product Ctg
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
		vASProductTypeListCtrl.search();
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.vASProductType.getProductType());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.vASProductType.isNewRecord()) {
			this.productType.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.productType.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.productTypeDesc.setReadonly(isReadOnly("VASProductTypeDialog_ProductTypeDesc"));
		this.productCtg.setReadonly(isReadOnly("VASProductTypeDialog_ProductCtg"));
		int count = vASProductTypeService.getVASProductTypeByActive(this.vASProductType.getProductType(), "");
		
		if (count > 0) {
			this.active.setDisabled(true);
		} else if (vASProductType.isNew()) {
			this.active.setDisabled(true);
		} else {
			this.active.setDisabled(isReadOnly("VASProductTypeDialog_Active"));
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.vASProductType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}
		if (count > 0) {
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

		doWriteBeanToComponents(this.vASProductType.getBefImage());
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
	 * Deletes a VASProductType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final VASProductType aVASProductType = new VASProductType();
		BeanUtils.copyProperties(getVASProductType(), aVASProductType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aVASProductType.getProductType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aVASProductType.getRecordType()).equals("")) {
				aVASProductType.setVersion(aVASProductType.getVersion() + 1);
				aVASProductType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aVASProductType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aVASProductType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aVASProductType.getNextTaskId(),
							aVASProductType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aVASProductType, tranType)) {
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

		this.productType.setValue("");
		this.productTypeDesc.setValue("");
		this.productCtg.setValue("");
		this.productCtg.setDescription("");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		final VASProductType aVASProductType = new VASProductType();
		BeanUtils.copyProperties(getVASProductType(), aVASProductType);
		boolean isNew = false;

		if (isWorkFlowEnabled()) {
			aVASProductType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aVASProductType.getNextTaskId(),
					aVASProductType);
		}

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if (!PennantConstants.RECORD_TYPE_DEL.equals(aVASProductType.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the VASProductType object with the components data
			doWriteComponentsToBean(aVASProductType);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aVASProductType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aVASProductType.getRecordType()).equals("")) {
				aVASProductType.setVersion(aVASProductType.getVersion() + 1);
				if (isNew) {
					aVASProductType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aVASProductType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aVASProductType.setNewRecord(true);
				}
			}
		} else {
			aVASProductType.setVersion(aVASProductType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if (doProcess(aVASProductType, tranType)) {
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

	private boolean doProcess(VASProductType aVASProductType, String tranType) {
		logger.debug("Entering");
		boolean processCompleted = false;
		aVASProductType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aVASProductType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aVASProductType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			aVASProductType.setTaskId(getTaskId());
			aVASProductType.setNextTaskId(getNextTaskId());
			aVASProductType.setRoleCode(getRole());
			aVASProductType.setNextRoleCode(getNextRoleCode());

			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
				processCompleted = doSaveProcess(getAuditHeader(aVASProductType, tranType), null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader = getAuditHeader(aVASProductType, PennantConstants.TRAN_WF);

				for (int i = 0; i < list.length; i++) {
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			processCompleted = doSaveProcess(getAuditHeader(aVASProductType, tranType), null);
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

		VASProductType aVASProductType = (VASProductType) auditHeader.getAuditDetail().getModelData();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())) {
						auditHeader = getVASProductTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getVASProductTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getVASProductTypeService().doApprove(auditHeader);

						if (PennantConstants.RECORD_TYPE_DEL.equals(aVASProductType.getRecordType())) {
							deleteNotes = true;
						}

					} else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))) {
						auditHeader = getVASProductTypeService().doReject(auditHeader);
						if (PennantConstants.RECORD_TYPE_NEW.equals(aVASProductType.getRecordType())) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_VASProductTypeDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_VASProductTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(
								getNotes("VASProductType", aVASProductType.getProductType(),
										aVASProductType.getVersion()), true);
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

	private AuditHeader getAuditHeader(VASProductType aVASProductType, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aVASProductType.getBefImage(), aVASProductType);
		return new AuditHeader(getReference(), null, null, null, auditDetail,
				aVASProductType.getUserDetails(), getOverideMap());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public VASProductType getVASProductType() {
		return this.vASProductType;
	}

	public void setVASProductType(VASProductType vASProductType) {
		this.vASProductType = vASProductType;
	}

	public void setVASProductTypeService(VASProductTypeService vASProductTypeService) {
		this.vASProductTypeService = vASProductTypeService;
	}

	public VASProductTypeService getVASProductTypeService() {
		return this.vASProductTypeService;
	}

	public void setVASProductTypeListCtrl(VASProductTypeListCtrl vASProductTypeListCtrl) {
		this.vASProductTypeListCtrl = vASProductTypeListCtrl;
	}

	public VASProductTypeListCtrl getVASProductTypeListCtrl() {
		return this.vASProductTypeListCtrl;
	}

}
