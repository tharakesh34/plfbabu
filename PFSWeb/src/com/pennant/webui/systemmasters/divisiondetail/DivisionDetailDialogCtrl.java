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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

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
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hlayout;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.systemmasters.DivisionDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.ScreenCTL;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/SystemMaster/DivisionDetail/divisionDetailDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class DivisionDetailDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(DivisionDetailDialogCtrl.class);
	
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting  by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
 
	protected Checkbox 		active; 

	protected Label 		recordStatus; 
	protected Label 		recordType;	 
	protected Radiogroup 	userAction;
	protected Groupbox 		gb_statusDetails;
	protected Groupbox 		groupboxWf;
	protected South 		south;
	private boolean 		enqModule=false;

	
	// not auto wired vars
	private DivisionDetail divisionDetail; // overhanded per param
	private transient DivisionDetailListCtrl divisionDetailListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_DivisionCode;
	private transient String  		oldVar_DivisionCodeDesc;
	private transient boolean  		oldVar_Active;
	private transient String oldVar_recordStatus;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_DivisionDetailDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button btnNew; 
	protected Button btnEdit; 
	protected Button btnDelete; 
	protected Button btnSave; 
	protected Button btnCancel; 
	protected Button btnClose; 
	protected Button btnHelp; 
	protected Button btnNotes; 
	
	
	// ServiceDAOs / Domain Classes
	private transient DivisionDetailService divisionDetailService;
	private transient PagedListService pagedListService;

	/**
	 * default constructor.<br>
	 */
	public DivisionDetailDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected DivisionDetail object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_DivisionDetailDialog(Event event) throws Exception {
		logger.debug("Entring" +event.toString());
		try {

			// get the params map that are overhanded by creation.
			final Map<String, Object> args = getCreationArgsMap(event);
			// READ OVERHANDED params !
			if (args.containsKey("enqModule")) {
				enqModule=(Boolean) args.get("enqModule");
			}else{
				enqModule=false;
			}
			
			// READ OVERHANDED params !
			if (args.containsKey("divisionDetail")) {
				this.divisionDetail = (DivisionDetail) args.get("divisionDetail");
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
					getUserWorkspace().alocateRoleAuthorities(getRole(), "DivisionDetailDialog");
			}else{
				getUserWorkspace().alocateAuthorities("DivisionDetailDialog");
			}
	
			/* set components visible dependent of the users rights */
			doCheckRights();

			// READ OVERHANDED params !
			// we get the divisionDetailListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete divisionDetail here.
			if (args.containsKey("divisionDetailListCtrl")) {
				setDivisionDetailListCtrl((DivisionDetailListCtrl) args.get("divisionDetailListCtrl"));
			} else {
				setDivisionDetailListCtrl(null);
			}
	
			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getDivisionDetail());
		} catch (Exception e) {
			createException(window_DivisionDetailDialog, e);
			logger.error(e);
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
		doStoreInitValues();
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
		doResetInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_DivisionDetailDialog);
		logger.debug("Leaving" +event.toString());
	}

		/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_DivisionDetailDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doClose();
		logger.debug("Leaving" +event.toString());
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

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	
	}


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aDivisionDetail
	 * @throws InterruptedException
	 */
	public void doShowDialog(DivisionDetail aDivisionDetail) throws InterruptedException {
		logger.debug("Entering") ;
		
		// if aDivisionDetail == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aDivisionDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aDivisionDetail = getDivisionDetailService().getNewDivisionDetail();
			
			setDivisionDetail(aDivisionDetail);
		} else {
			setDivisionDetail(aDivisionDetail);
		}
		// set ReadOnly mode accordingly if the object is new or not.
		if (aDivisionDetail.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.divisionCode.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.divisionCodeDesc.focus();
				if (!StringUtils.trimToEmpty(aDivisionDetail.getRecordType()).equals("")) {
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

			doStoreInitValues();
		
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_DivisionDetailDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		getUserWorkspace().alocateAuthorities("DivisionDetailDialog");
		if(!enqModule){
			this.btnNew.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnNew"));
			this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnEdit"));
			this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnDelete"));
			this.btnSave.setVisible(getUserWorkspace().isAllowed("button_DivisionDetailDialog_btnSave"));	
		}

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

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
	
		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}
	
	
	/**
	 * Stores the initialinitial values to member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_DivisionCode = this.divisionCode.getValue();
		this.oldVar_DivisionCodeDesc = this.divisionCodeDesc.getValue();
		this.oldVar_Active = this.active.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.divisionCode.setValue(this.oldVar_DivisionCode);
		this.divisionCodeDesc.setValue(this.oldVar_DivisionCodeDesc);
		this.active.setChecked(this.oldVar_Active);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		
		if(isWorkFlowEnabled() & !enqModule){	
			this.userAction.setSelectedIndex(0);	
		}
		logger.debug("Leaving");
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
		this.active.setChecked(aDivisionDetail.isActive());
		this.recordStatus.setValue(aDivisionDetail.getRecordStatus());
		//this.recordType.setValue(aDivisionDetail.getRecordType());
		if(aDivisionDetail.isNew()){
			this.active.setChecked(true);
			this.active.setDisabled(true);
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
		//Active
		try {
			aDivisionDetail.setActive(this.active.isChecked());
		}catch (WrongValueException we ) {
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

	
	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		logger.debug("Entering");
		//To clear the Error Messages
		doClearMessage();
		
		if (!StringUtils.trimToEmpty(this.oldVar_DivisionCode).equals(StringUtils.trimToEmpty(this.divisionCode.getValue()))) {
			return true;
		}
		
		if (!StringUtils.trimToEmpty(this.oldVar_DivisionCodeDesc).equals(StringUtils.trimToEmpty(this.divisionCodeDesc.getValue()))) {
			return true;
		}
		if (this.oldVar_Active != this.active.isChecked()) {
			return true;
		}
		logger.debug("Leaving"); 
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Division Code
		if (!this.divisionCode.isReadonly()){
			this.divisionCode.setConstraint(new PTStringValidator(Labels.getLabel("label_DivisionDetailDialog_DivisionCode.value"),PennantRegularExpressions.REGEX_NAME,true));
		}
		//Division Code Desc
		if (!this.divisionCodeDesc.isReadonly()){
			this.divisionCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_DivisionDetailDialog_DivisionCodeDesc.value"),PennantRegularExpressions.REGEX_NAME,true));
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

	private void doClearMessage() {
		logger.debug("Entering");
			this.divisionCode.setErrorMessage("");
			this.divisionCodeDesc.setErrorMessage("");
	logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList(){
		final JdbcSearchObject<DivisionDetail> soDivisionDetail = getDivisionDetailListCtrl().getSearchObj();
		getDivisionDetailListCtrl().pagingDivisionDetailList.setActivePage(0);
		getDivisionDetailListCtrl().getPagedListWrapper().setSearchObject(soDivisionDetail);
		if(getDivisionDetailListCtrl().listBoxDivisionDetail!=null){
			getDivisionDetailListCtrl().listBoxDivisionDetail.getListModel();
		}
	} 


	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		boolean close=true;
		if (!enqModule && isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}
		
		if(close){
			closeDialog(this.window_DivisionDetailDialog, "DivisionDetail");	
		}

		logger.debug("Leaving") ;
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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aDivisionDetail.getDivisionCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();
		
		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aDivisionDetail.getRecordType()).equals("")){
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
				if(doProcess(aDivisionDetail,tranType)){
					refreshList();
					closeDialog(this.window_DivisionDetailDialog, "DivisionDetail"); 
				}

			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showErrorMessage(this.window_DivisionDetailDialog,e);
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
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aDivisionDetail.getRecordType()).equals("")){
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
			
			if(doProcess(aDivisionDetail,tranType)){
				//doWriteBeanToComponents(aDivisionDetail);
				refreshList();
				closeDialog(this.window_DivisionDetailDialog, "DivisionDetail");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_DivisionDetailDialog,e);
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
		aDivisionDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aDivisionDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aDivisionDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if (isWorkFlowEnabled()) {

			if (!"Save".equals(userAction.getSelectedItem().getLabel())) {
				if (PennantConstants.WF_Audit_Notes.equals(getAuditingReq())) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			aDivisionDetail.setTaskId(getTaskId());
			aDivisionDetail.setNextTaskId(getNextTaskId());
			aDivisionDetail.setRoleCode(getRole());
			aDivisionDetail.setNextRoleCode(getNextRoleCode());
			
			if (StringUtils.trimToEmpty(getOperationRefs()).equals("")) {
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
				
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
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
			logger.error(e);
			e.printStackTrace();
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

	private AuditHeader getAuditHeader(DivisionDetail aDivisionDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aDivisionDetail.getBefImage(), aDivisionDetail);   
		return new AuditHeader(aDivisionDetail.getDivisionCode(),null,null,null,auditDetail,aDivisionDetail.getUserDetails(),getOverideMap());
	}
	
	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}

	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

}
