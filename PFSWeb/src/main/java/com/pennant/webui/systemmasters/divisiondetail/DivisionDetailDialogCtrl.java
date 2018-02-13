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
 * FileName    		:  DivisionDetailDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-08-2013    														*
 *                                                                  						*
 * Modified Date    :  02-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-08-2013       Pennant	                 0.1                                            * 
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
package com.pennant.webui.systemmasters.divisiondetail;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.systemmasters.DivisionDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.ScreenCTL;
import com.pennant.search.Filter;

/**
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/DivisionDetail/divisionDetailDialog.zul file.
 */
public class DivisionDetailDialogCtrl extends GFCBaseCtrl<DivisionDetail> {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DivisionDetailDialogCtrl.class);
	
	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_DivisionDetailDialog; 
	protected Row 			row0; 
	protected Label 		label_DivisionCode;
	protected Hlayout 		hlayout_DivisionCode;
	protected Space 		space_DivisionCode; 
 
	protected Textbox 		divisionCode; 
	protected Label 		label_DivisionCodeDesc;
	protected Hlayout 		hlayout_DivisionCodeDesc;
	protected Space 		space_DivisionCodeDesc; 
 
	protected Textbox 		divisionCodeDesc; 
	protected Row 			row1; 
	protected Label 		label_Active;
	protected Hlayout 		hlayout_Active;
	protected Space 		space_Active; 
	protected Checkbox 		alwPromotion; 
	protected Checkbox 		active; 
	
	protected Row 			row_Suspremarks;
	protected Combobox		suspTrigger;
	protected Label			label_DivisionDetailDialog_DivisionSuspRemarks;
	protected Textbox		divisionSuspRemarks;
	
	protected Label			label_EntityCode;
	protected ExtendedCombobox		entityCode;

	protected Label 		recordType;	 
	protected Groupbox 		gb_statusDetails;
	private boolean 		enqModule=false;

	
	// not auto wired vars
	private DivisionDetail divisionDetail; // overhanded per param
	private transient DivisionDetailListCtrl divisionDetailListCtrl; // overhanded per param

	
	// ServiceDAOs / Domain Classes
	private transient DivisionDetailService divisionDetailService;

	/**
	 * default constructor.<br>
	 */
	public DivisionDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "DivisionDetailDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected DivisionDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DivisionDetailDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_DivisionDetailDialog);

		try {
			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}
			
			// READ OVERHANDED params !
			if (arguments.containsKey("divisionDetail")) {
				this.divisionDetail = (DivisionDetail) arguments.get("divisionDetail");
				DivisionDetail befImage =new DivisionDetail();
				BeanUtils.copyProperties(this.divisionDetail, befImage);
				this.divisionDetail.setBefImage(befImage);
				
				setDivisionDetail(this.divisionDetail);
			} else {
				setDivisionDetail(null);
			}
			doLoadWorkFlow(this.divisionDetail.isWorkflow(),this.divisionDetail.getWorkflowId(),this.divisionDetail.getNextTaskId());
	
			if (isWorkFlowEnabled() && !enqModule){
					this.userAction	= setListRecordStatus(this.userAction);
					getUserWorkspace().allocateRoleAuthorities(getRole(), "DivisionDetailDialog");
			}else{
				getUserWorkspace().allocateAuthorities(super.pageRightName);
			}
	
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the divisionDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete divisionDetail here.
			if (arguments.containsKey("divisionDetailListCtrl")) {
				setDivisionDetailListCtrl((DivisionDetailListCtrl) arguments.get("divisionDetailListCtrl"));
			} else {
				setDivisionDetailListCtrl(null);
			}
	
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDivisionDetail());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_DivisionDetailDialog.onClose();
		}
		
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		displayComponents(ScreenCTL.SCRN_GNEDT);
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		doSave();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doWriteBeanToComponents(this.divisionDetail.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		MessageUtil.showHelpWindow(event, window_DivisionDetailDialog);
		logger.debug("Leaving" +event.toString());
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
		logger.debug("Entering" +event.toString());
		try {
			
			ScreenCTL.displayNotes(getNotes("DivisionDetail",getDivisionDetail().getDivisionCode(),getDivisionDetail().getVersion()),this);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" +event.toString());
	
	}

	// GUI operations

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDivisionDetail
	 * @throws Exception
	 */
	public void doShowDialog(DivisionDetail aDivisionDetail) throws Exception {
		logger.debug("Entering");
		// set ReadOnly mode accordingly if the object is new or not.
		if (aDivisionDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.divisionCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.divisionCodeDesc.focus();
				if (StringUtils.isNotBlank(aDivisionDetail.getRecordType())) {
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
			doWriteBeanToComponents(aDivisionDetail);
			// set ReadOnly mode accordingly if the object is new or not.

			//displayComponents(ScreenCTL.getMode(enqModule,isWorkFlowEnabled(),aDivisionDetail.isNewRecord()));

			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_DivisionDetailDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}
	
	// 1 Enquiry
	// 2 New Record
	// 3 InitEdit
	// 4 EditMode
	// 5 WorkFlow Add
	// 6 WorkFlow Edit
	
	private void displayComponents(int mode){
		logger.debug("Entering");
		
		//doReadOnly(ScreenCTL.initButtons(mode, this.btnCtrl, this.btnNotes, isWorkFlowEnabled(),isFirstTask(), this.userAction,this.divisionCode,this.divisionCodeDesc));
		
		if (getDivisionDetail().isNewRecord()){
			  	setComponentAccessType("DivisionDetailDialog_DivisionCode", false, this.divisionCode, this.space_DivisionCode, this.label_DivisionCode, this.hlayout_DivisionCode,null);
			  	setComponentAccessType("DivisionDetailDialog_DivisionCodeDesc", false, this.divisionCodeDesc, this.space_DivisionCodeDesc, this.label_DivisionCodeDesc, this.hlayout_DivisionCodeDesc,null);
			}
		
		logger.debug("Leaving");
	} 
	
	
	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getDivisionDetail().isNewRecord()) {
			this.divisionCode.setReadonly(false);
			this.divisionCodeDesc.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.divisionCode.setReadonly(true);
			this.divisionCodeDesc.setReadonly(isReadOnly("DivisionDetailDialog_divisionCodeDesc"));
			this.btnCancel.setVisible(true);
		}

		//this.suspTrigger.setDisabled(false);//TODO:Right Name
		this.alwPromotion.setDisabled(isReadOnly("DivisionDetailDialog_Active"));
		this.suspTrigger.setDisabled(isReadOnly("DivisionDetailDialog_Active"));
		this.divisionSuspRemarks.setReadonly(isReadOnly("DivisionDetailDialog_Active"));
		this.entityCode.setReadonly(isReadOnly("DivisionDetailDialog_EntityCode"));
		this.alwPromotion.setDisabled(true);
		
        this.active.setDisabled(isReadOnly("DivisionDetailDialog_Active"));
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.divisionDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		logger.debug("Leaving ");
	}
	
	
	
	
	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		/*
		boolean tempReadOnly= readOnly;
		
		if(readOnly || (!readOnly && (PennantConstants.RECORD_TYPE_DEL.equals(divisionDetail.getRecordType())))) {
			tempReadOnly=true;
		}
		
		setComponentAccessType("DivisionDetailDialog_DivisionCode", true, this.divisionCode, this.space_DivisionCode, this.label_DivisionCode, this.hlayout_DivisionCode,null);		
  		setComponentAccessType("DivisionDetailDialog_DivisionCodeDesc", tempReadOnly, this.divisionCodeDesc, this.space_DivisionCodeDesc, this.label_DivisionCodeDesc, this.hlayout_DivisionCodeDesc,null);
		setRowInvisible(this.row0, this.hlayout_DivisionCode,this.hlayout_DivisionCodeDesc);
  		setComponentAccessType("DivisionDetailDialog_Active", tempReadOnly, this.active, this.space_Active, this.label_Active, this.hlayout_Active,null);
		setRowInvisible(this.row1, this.hlayout_Active,null);*/
		
		this.divisionCode.setReadonly(true);
		this.divisionCodeDesc.setReadonly(true);
		this.alwPromotion.setDisabled(true);
		this.suspTrigger.setDisabled(true);
		this.active.setDisabled(true);
		this.entityCode.setReadonly(true);
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
		logger.debug("Entering") ;
		
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnSave"));	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.divisionCode.setMaxlength(8);
		this.divisionCodeDesc.setMaxlength(50);
		this.row_Suspremarks.setVisible(false);
		
		this.entityCode.setModuleName("Entity");
		this.entityCode.setMandatoryStyle(true);
		this.entityCode.setValueColumn("EntityCode");
		this.entityCode.setDescColumn("EntityDesc");
		this.entityCode.setValidateColumns(new String[] { "EntityCode" });
		Filter[] fieldCode = new Filter[1] ;
		fieldCode[0]= new Filter("Active", 1, Filter.OP_EQUAL);
		this.entityCode.setFilters(fieldCode);
	
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aDivisionDetail
	 *            DivisionDetail
	 */
	public void doWriteBeanToComponents(DivisionDetail aDivisionDetail) {
		logger.debug("Entering") ;
		this.divisionCode.setValue(aDivisionDetail.getDivisionCode());
		this.divisionCodeDesc.setValue(aDivisionDetail.getDivisionCodeDesc());
		fillComboBox(this.suspTrigger, aDivisionDetail.getDivSuspTrigger(), PennantStaticListUtil.getSuspendedTriggers(), "");

		this.alwPromotion.setChecked(aDivisionDetail.isAlwPromotion());
		this.active.setChecked(aDivisionDetail.isActive());
		this.entityCode.setValue(aDivisionDetail.getEntityCode());
		this.recordStatus.setValue(aDivisionDetail.getRecordStatus());
		//this.recordType.setValue(aDivisionDetail.getRecordType());
		if(aDivisionDetail.isNew()){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		} else {
			if(StringUtils.equals(getComboboxValue(this.suspTrigger), PennantConstants.SUSP_TRIG_MAN)) {
				this.row_Suspremarks.setVisible(true);
			}
			if(this.row_Suspremarks.isVisible()) {
				this.divisionSuspRemarks.setValue(aDivisionDetail.getDivSuspRemarks());
			}
		}
		
		if (aDivisionDetail.isNewRecord()) {
			this.entityCode.setDescription("");
		} else {
			this.entityCode.setDescription(aDivisionDetail.getEntityDesc());
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aDivisionDetail
	 */
	public void doWriteComponentsToBean(DivisionDetail aDivisionDetail) {
		logger.debug("Entering") ;
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		//Division Code
		try {
		    aDivisionDetail.setDivisionCode(this.divisionCode.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Division Code Desc
		try {
		    aDivisionDetail.setDivisionCodeDesc(this.divisionCodeDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aDivisionDetail.setDivSuspTrigger(PennantConstants.List_Select.equals(
					getComboboxValue(this.suspTrigger))?"":getComboboxValue(this.suspTrigger));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Division Susp Remarks
		try {
			if(this.divisionSuspRemarks.isVisible()) {
				aDivisionDetail.setDivSuspRemarks(this.divisionSuspRemarks.getValue());
			} else {
				aDivisionDetail.setDivSuspRemarks("");
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Allow Promotions
		try {
			aDivisionDetail.setActive(this.alwPromotion.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		//Active
		try {
			aDivisionDetail.setActive(this.active.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//ENtity Code
		try {
			aDivisionDetail.setEntityCode(this.entityCode.getValidatedValue());
			aDivisionDetail.setEntityDesc(this.entityCode.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		doRemoveLOVValidation();
		
		if (!wve.isEmpty()) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		
		logger.debug("Leaving");
	}

	public void onSelect$suspTrigger(Event event) {
		logger.debug("Entering" + event.toString());
		if(StringUtils.equals(getComboboxValue(this.suspTrigger), PennantConstants.SUSP_TRIG_MAN)) {
			this.row_Suspremarks.setVisible(true);
		} else {
			this.row_Suspremarks.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		doClearMessage();
		//Division Code
		if (!this.divisionCode.isReadonly()){
			this.divisionCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DivisionDetailDialog_DivisionCode.value"),PennantRegularExpressions.REGEX_UPPERCASENAME,true));
		}
		//Division Code Desc
		if (!this.divisionCodeDesc.isReadonly()){
			this.divisionCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DivisionDetailDialog_DivisionCodeDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		
		if (!this.entityCode.isReadonly()) {
			this.entityCode.setConstraint(new PTStringValidator(Labels
					.getLabel("label_DivisionDetailDialog_EntityCode.value"),
					PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
	logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.divisionCode.setConstraint("");
		this.divisionCodeDesc.setConstraint("");
		this.entityCode.setConstraint("");
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
			this.divisionCode.setErrorMessage("");
			this.divisionCodeDesc.setErrorMessage("");
			this.suspTrigger.setErrorMessage("");
			this.entityCode.setErrorMessage("");
	logger.debug("Leaving");
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getDivisionDetailListCtrl().search();
	}

	/**
	 * Deletes a DivisionDetail object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final DivisionDetail aDivisionDetail = new DivisionDetail();
		BeanUtils.copyProperties(getDivisionDetail(), aDivisionDetail);
		String tranType=PennantConstants.TRAN_WF;
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_DivisionDetailDialog_DivisionCode.value")+" : "+aDivisionDetail.getDivisionCode();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aDivisionDetail.getRecordType())){
				aDivisionDetail.setVersion(aDivisionDetail.getVersion()+1);
				aDivisionDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				
				if (isWorkFlowEnabled()){
					aDivisionDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aDivisionDetail.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aDivisionDetail.getNextTaskId(), aDivisionDetail);
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aDivisionDetail, tranType)) {
					refreshList();
					closeDialog();
				}

			} catch (Exception e) {
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
		
		this.divisionCode.setValue("");
		this.divisionCodeDesc.setValue("");
		this.entityCode.setValue("");
		this.suspTrigger.setSelectedIndex(0);
		this.alwPromotion.setChecked(false);
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
		final DivisionDetail aDivisionDetail = new DivisionDetail();
		BeanUtils.copyProperties(getDivisionDetail(), aDivisionDetail);
		boolean isNew = false;
		
		if(isWorkFlowEnabled()){
			aDivisionDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aDivisionDetail.getNextTaskId(), aDivisionDetail);
		}
		
		// force validation, if on, than execute by component.getValue()
		if(!PennantConstants.RECORD_TYPE_DEL.equals(aDivisionDetail.getRecordType()) && isValidation()) {
			doSetValidation();
			// fill the DivisionDetail object with the components data
			doWriteComponentsToBean(aDivisionDetail);
		}
		// Write the additional validations as per below example
		// get the selected branch object from the listbox
		// Do data level validations here
		
		isNew = aDivisionDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aDivisionDetail.getRecordType())){
				aDivisionDetail.setVersion(aDivisionDetail.getVersion()+1);
				if(isNew){
					aDivisionDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aDivisionDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aDivisionDetail.setNewRecord(true);
				}
			}
		}else{
			aDivisionDetail.setVersion(aDivisionDetail.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}
		
		// save it to database
		try {

			if (doProcess(aDivisionDetail, tranType)) {
				// doWriteBeanToComponents(aDivisionDetail);
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
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
	
	private boolean doProcess(DivisionDetail aDivisionDetail,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		aDivisionDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aDivisionDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDivisionDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
		
		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (auditingReq) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			
			aDivisionDetail.setTaskId(getTaskId());
			aDivisionDetail.setNextTaskId(getNextTaskId());
			aDivisionDetail.setRoleCode(getRole());
			aDivisionDetail.setNextRoleCode(getNextRoleCode());
			
			if (StringUtils.isBlank(getOperationRefs())) {
					processCompleted = doSaveProcess(getAuditHeader(aDivisionDetail, tranType),null);
			} else {
				String[] list = getOperationRefs().split(";");
				AuditHeader auditHeader =  getAuditHeader(aDivisionDetail, PennantConstants.TRAN_WF);
				
				for (int i = 0; i < list.length; i++) {
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			processCompleted = doSaveProcess(getAuditHeader(aDivisionDetail, tranType), null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param  AuditHeader auditHeader
	 * @param method  (String)
	 * @return boolean
	 * 
	 */
	
	private boolean doSaveProcess(AuditHeader auditHeader, String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;
		
		DivisionDetail aDivisionDetail = (DivisionDetail) auditHeader.getAuditDetail().getModelData();
		
		try {
			
			while(retValue==PennantConstants.porcessOVERIDE){
				
				if (StringUtils.isBlank(method)){
					if (PennantConstants.TRAN_DEL.equals(auditHeader.getAuditTranType())){
						auditHeader = getDivisionDetailService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getDivisionDetailService().saveOrUpdate(auditHeader);	
					}
					
				}else{
					if (PennantConstants.method_doApprove.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getDivisionDetailService().doApprove(auditHeader);

						if(PennantConstants.RECORD_TYPE_DEL.equals(aDivisionDetail.getRecordType())){
							deleteNotes=true;
						}

					}else if (PennantConstants.method_doReject.equalsIgnoreCase(StringUtils.trimToEmpty(method))){
						auditHeader = getDivisionDetailService().doReject(auditHeader);
						if(PennantConstants.RECORD_TYPE_NEW.equals(aDivisionDetail.getRecordType())){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_DivisionDetailDialog, auditHeader);
						return processCompleted; 
					}
				}
				
				auditHeader =	ErrorControl.showErrorDetails(this.window_DivisionDetailDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				
				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes("DivisionDetail",aDivisionDetail.getDivisionCode(),aDivisionDetail.getVersion()),true);
					}
				}
				
				if (retValue==PennantConstants.porcessOVERIDE){
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

	private AuditHeader getAuditHeader(DivisionDetail aDivisionDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDivisionDetail.getBefImage(), aDivisionDetail);   
		return new AuditHeader(aDivisionDetail.getDivisionCode(),null,null,null,auditDetail,aDivisionDetail.getUserDetails(),getOverideMap());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DivisionDetail getDivisionDetail() {
		return this.divisionDetail;
	}

	public void setDivisionDetail(DivisionDetail divisionDetail) {
		this.divisionDetail = divisionDetail;
	}

	public void setDivisionDetailService(DivisionDetailService divisionDetailService) {
		this.divisionDetailService = divisionDetailService;
	}

	public DivisionDetailService getDivisionDetailService() {
		return this.divisionDetailService;
	}

	public void setDivisionDetailListCtrl(DivisionDetailListCtrl divisionDetailListCtrl) {
		this.divisionDetailListCtrl = divisionDetailListCtrl;
	}

	public DivisionDetailListCtrl getDivisionDetailListCtrl() {
		return this.divisionDetailListCtrl;
	}

}
