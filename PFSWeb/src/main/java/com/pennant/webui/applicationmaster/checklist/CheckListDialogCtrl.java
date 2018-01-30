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
 * FileName    		:  CheckListDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.checklist;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.service.applicationmaster.CheckListService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.applicationmaster.checklist.model.CheckListDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.pagging.PagedListWrapper;

/**
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CheckList/checkListDialog.zul file.
 */
public class CheckListDialogCtrl extends GFCBaseCtrl<CheckList> {
	private static final long serialVersionUID = 3545862467364688600L;
	private static final Logger logger = Logger.getLogger(CheckListDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window 	window_CheckListDialog; 	
	protected Textbox 	checkListDesc; 				
	protected Intbox 	checkMinCount; 				
	protected Intbox 	checkMaxCount; 				
	protected Checkbox 	docRequired;
	protected Combobox	moduleName;
	protected Space		space_ModuleName;
	protected ExtendedCombobox 	checkRule;
	protected Label		label_CheckListDialog_CheckRule;
	/*protected Textbox 	lovDescCheckRule; 			
	protected Button 	btnSearchCheckRule; 		
*/	protected Checkbox 	active; 					
	protected Listbox   listbox_ChkListDetails;     
	protected Paging    pagingChkListDetailsList;   
	protected Grid 		grid_Basicdetails;	
	protected Button        btnNew_CheckListDetail; 

	// not auto wired variables
	private CheckList checkList; 							// overHanded per parameter
	private transient CheckListListCtrl checkListListCtrl; 	// overHanded per parameter

	private transient boolean validationOn;
	
	protected Listheader listheader_RecordStatus;
	protected Listheader listheader_RecordType;

	// Button controller for the CRUD buttons
	private transient boolean 	 isEditable=false;

	// ServiceDAOs / Domain Classes
	private transient CheckListService checkListService;
	private HashMap<String, ArrayList<ErrorDetail>> overideMap= new HashMap<String, ArrayList<ErrorDetail>>();
	private List<CheckListDetail> chekListDetailsList=new ArrayList<CheckListDetail>();
	private PagedListWrapper<CheckListDetail> chkListDetailPagedListWrapper;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public CheckListDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CheckListDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CheckList object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CheckListDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CheckListDialog);

		try {
			// READ OVERHANDED parameters !
			if (arguments.containsKey("checkList")) {
				this.checkList = (CheckList) arguments.get("checkList");
				CheckList befImage = new CheckList();
				BeanUtils.copyProperties(this.checkList, befImage);
				this.checkList.setBefImage(befImage);

				setCheckList(this.checkList);
			} else {
				setCheckList(null);
			}

			// READ OVERHANDED parameters !
			// we get the checkListListWindow controller. So we have access to it and can synchronize the shown data
			// when we do insert, edit or delete checkList here.
			if (arguments.containsKey("checkListListCtrl")) {
				setCheckListListCtrl((CheckListListCtrl) arguments.get("checkListListCtrl"));
			} else {
				setCheckListListCtrl(null);
			}

			doLoadWorkFlow(this.checkList.isWorkflow(), this.checkList.getWorkflowId(), this.checkList.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "CheckListDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			setChkListDetailPagedListWrapper();
			getChkListDetailPagedListWrapper();

			// Set the DialogController Height for listBox
			getBorderLayoutHeight();
			grid_Basicdetails.getRows().getVisibleItemCount();
			int dialogHeight = grid_Basicdetails.getRows().getVisibleItemCount() * 20 + 100 + 35;
			int listboxHeight = borderLayoutHeight - dialogHeight;
			listbox_ChkListDetails.setHeight(listboxHeight + "px");
			listRows = Math.round(listboxHeight / 24) - 1;

			if (isWorkFlowEnabled()) {
				this.listheader_RecordStatus.setVisible(true);
				this.listheader_RecordType.setVisible(true);
			} else {
				this.listheader_RecordStatus.setVisible(false);
				this.listheader_RecordType.setVisible(true);
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getCheckList());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_CheckListDialog.onClose();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.checkListDesc.setMaxlength(100);
		this.checkMinCount.setMaxlength(2);
		this.checkMaxCount.setMaxlength(2);
		this.checkRule.setMaxlength(8);
		this.checkRule.setModuleName("Rule");
		this.checkRule.setValueColumn("RuleCode");
		this.checkRule.setDescColumn("RuleCodeDesc");
		this.checkRule.setValidateColumns(new String[]{"RuleCode"});
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("RuleModule", RuleConstants.MODULE_CLRULE, Filter.OP_EQUAL);
		filters[1] = new Filter("RuleEvent", RuleConstants.EVENT_CLRULE, Filter.OP_EQUAL);
		this.checkRule.setFilters(filters);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving") ;
	}

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

		getUserWorkspace().allocateAuthorities(super.pageRightName, getRole());

		this.btnNew_CheckListDetail.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnNew_CheckListDetail"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		btnNew_CheckListDetail.setVisible(true);
		isEditable=true;
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		MessageUtil.showHelpWindow(event, window_CheckListDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		isEditable=false;
		btnNew_CheckListDetail.setVisible(false);
		doWriteBeanToComponents(this.checkList.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCheckList
	 *            CheckList
	 */
	public void doWriteBeanToComponents(CheckList aCheckList) {
		logger.debug("Entering") ;
		this.checkListDesc.setValue(aCheckList.getCheckListDesc());
		this.checkMinCount.setValue(aCheckList.getCheckMinCount());
		this.checkMaxCount.setValue(aCheckList.getCheckMaxCount());
		this.active.setChecked(aCheckList.isActive());
		this.recordStatus.setValue(aCheckList.getRecordStatus());
		if(this.checkList.isNewRecord()){
		fillComboBox(this.moduleName, PennantConstants.WORFLOW_MODULE_FINANCE,
				PennantStaticListUtil.getWorkFlowModules(), "");
		}else{
			fillComboBox(this.moduleName, aCheckList.getModuleName(),
					PennantStaticListUtil.getWorkFlowModules(), "");
		}
		if(aCheckList.isNew() || StringUtils.trimToEmpty(aCheckList.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
		String modulename = this.moduleName.getSelectedItem().getValue().toString();
		doModuleSelection(modulename);
		this.checkRule.setValue(aCheckList.getCheckRule());
		this.checkRule.setDescription(StringUtils.isBlank(aCheckList.getCheckRule())? "" :aCheckList.getLovDescCheckRuleName());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aCheckList
	 */
	public void doWriteComponentsToBean(CheckList aCheckList) {
		logger.debug("Entering") ;
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			aCheckList.setCheckListDesc(this.checkListDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.checkMinCount.getValue()==null){
				throw new WrongValueException(this.checkMinCount,Labels.getLabel("FIELD_IS_MAND"
						,new String[]{Labels.getLabel("label_CheckListDialog_CheckMinCount.value")})); 
			}
			aCheckList.setCheckMinCount(this.checkMinCount.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {

			if(this.listbox_ChkListDetails.getItemCount()==0){
				throw new WrongValueException(this.btnNew_CheckListDetail,Labels.getLabel("FIELD_IS_MAND", new String[]{Labels.getLabel("label_FinanceCheckListList.title")}));
			} else if(this.listbox_ChkListDetails.getItemCount() < this.checkMaxCount.intValue()){
				throw new WrongValueException(this.btnNew_CheckListDetail,Labels.getLabel("label_CheckListDialog_Checklist_Madatory"));
			}
			
			if (this.docRequired.isChecked() && this.listbox_ChkListDetails.getVisibleItemCount() != 0) {
				aCheckList.setCheckMaxCount(this.listbox_ChkListDetails.getVisibleItemCount());
			}else{
				aCheckList.setCheckMaxCount(this.checkMaxCount.intValue());
			}
			
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if ("#".equals(getComboboxValue(this.moduleName))) {
				throw new WrongValueException(this.moduleName, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_CheckListDialog_ModuleName.value") }));
			}
			aCheckList.setModuleName(getComboboxValue(this.moduleName));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aCheckList.setCheckRule(this.checkRule.getValidatedValue());
			aCheckList.setLovDescCheckRuleName(this.checkRule.getDescription());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckList.setActive(this.active.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if((!this.checkMaxCount.isReadonly()) && this.checkMaxCount.intValue()<this.checkMinCount.intValue()){
				throw new WrongValueException(this.checkMaxCount,Labels.getLabel("FIELD_IS_EQUAL_OR_GREATER"
						,new String[]{Labels.getLabel("label_CheckListDialog_CheckMaxCount.value")
								,Labels.getLabel("label_CheckListDialog_CheckMinCount.value")}));	
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size()>0) {
			WrongValueException [] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		aCheckList.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aCheckList
	 * @throws Exception
	 */
	public void doShowDialog(CheckList aCheckList) throws Exception {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCheckList.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.checkListDesc.focus();
		} else {
			this.checkListDesc.focus();
			if (isWorkFlowEnabled()){
				if (StringUtils.isNotBlank(aCheckList.getRecordType())) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnNew_CheckListDetail.setVisible(false);
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCheckList);

			doFillCheckListDetailsList(getCheckList().getChkListList());
			
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e){
			logger.error("Exception: ", e);
			this.window_CheckListDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug("Leaving") ;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.checkListDesc.isReadonly()){
			this.checkListDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CheckListDialog_CheckListDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.checkMinCount.isReadonly() ){
			this.checkMinCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_CheckListDialog_CheckMinCount.value"),false,false,0,this.listbox_ChkListDetails.getItemCount()));
		}	
		/*if (!this.checkMaxCount.isReadonly()){
			this.checkMaxCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_CheckListDialog_CheckMaxCount.value"),true, false,1,2));
		}*/
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.checkListDesc.setConstraint("");
		this.checkMinCount.setConstraint("");
		this.checkMaxCount.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Set Validations for LOV Fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		if (!this.checkRule.isReadonly()){
			this.checkRule.setConstraint(new PTStringValidator(Labels.getLabel("label_CheckListDialog_CheckRule.value"), null, false,true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.checkRule.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.checkListDesc.setErrorMessage("");
		this.checkMinCount.setErrorMessage("");
		this.checkMaxCount.setErrorMessage("");
		this.moduleName.setErrorMessage("");
		this.checkRule.setErrorMessage("");
		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Deletes a CheckList object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		final CheckList aCheckList = new CheckList();
		BeanUtils.copyProperties(getCheckList(), aCheckList);
		String tranType=PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
				Labels.getLabel("label_CheckListDialog_CheckListDesc.value")+" : "+aCheckList.getCheckListDesc();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aCheckList.getRecordType())){
				aCheckList.setVersion(aCheckList.getVersion()+1);
				aCheckList.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aCheckList.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}
			try {
				if(doProcess(aCheckList,tranType)){
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
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (getCheckList().isNewRecord()){
			this.btnCancel.setVisible(false);
		}else{
			this.btnCancel.setVisible(true);
		}

		this.checkListDesc.setReadonly(isReadOnly("CheckListDialog_checkListDesc"));
		this.checkMinCount.setReadonly(isReadOnly("CheckListDialog_checkMinCount"));
		this.moduleName.setDisabled(isReadOnly("CheckListDialog_moduleName"));
		this.checkRule.setReadonly(isReadOnly("CheckListDialog_checkRule"));
		this.active.setDisabled(isReadOnly("CheckListDialog_active"));
		if (docRequired.isChecked()) {
			this.checkMaxCount.setReadonly(true);
		}else{
			this.checkMaxCount.setReadonly(isReadOnly("CheckListDialog_checkMaxCount"));
		}

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.checkList.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
			//btnCancel.setVisible(true);
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * When user clicks on select "Selection Item based on moduleName" 
	 * 
	 * @param event
	 */
	public void onSelect$moduleName(Event event) throws SuspendNotAllowedException, InterruptedException {

		logger.debug("Entering " + event.toString());
		String modulename = this.moduleName.getSelectedItem().getValue().toString();
		doModuleSelection(modulename);
		logger.debug("Leaving " + event.toString());

	}

	private void doModuleSelection(String modulename) {

		if ((PennantConstants.WORFLOW_MODULE_FACILITY.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_COLLATERAL.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_VAS.equals(modulename))
				|| (PennantConstants.WORFLOW_MODULE_COMMITMENT.equals(modulename))) {
			this.checkRule.setVisible(false);
			this.checkRule.setValue("");
			this.label_CheckListDialog_CheckRule.setVisible(false);

		} else {
			this.checkRule.setVisible(true);
			this.checkRule.setValue("");
			this.label_CheckListDialog_CheckRule.setVisible(true);
		}

	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.checkListDesc.setReadonly(true);
		this.checkMinCount.setReadonly(true);
		this.checkMaxCount.setReadonly(true);
		this.moduleName.setDisabled(true);
		this.checkRule.setReadonly(true);
		this.active.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		this.checkListDesc.setValue("");
		this.checkMinCount.setText("");
		this.checkMaxCount.setText("");
		this.moduleName.setValue("");
		this.checkRule.setValue("");
		this.checkRule.setDescription("");
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
		final CheckList aCheckList = new CheckList();
		BeanUtils.copyProperties(getCheckList(), aCheckList);
		boolean isNew = false;

		// force validation, if on, than execute by component.getValue()
		doSetValidation();
		// fill the CheckList object with the components data
		doWriteComponentsToBean(aCheckList);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aCheckList.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCheckList.getRecordType())){
				aCheckList.setVersion(aCheckList.getVersion()+1);
				if(isNew){
					aCheckList.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aCheckList.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCheckList.setNewRecord(true);
				}
			}
		}else{
			aCheckList.setVersion(aCheckList.getVersion()+1);
			if(isNew){
				tranType =PennantConstants.TRAN_ADD;
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {

			if(doProcess(aCheckList,tranType)){
				doWriteBeanToComponents(aCheckList);
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
	 * @param aCheckList
	 *            (CheckList)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CheckList aCheckList,String tranType){
		logger.debug("Entering");
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";

		aCheckList.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aCheckList.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCheckList.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCheckList.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCheckList.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCheckList);
				}

				if (isNotesMandatory(taskId, aCheckList)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (StringUtils.isNotBlank(nextTaskId)) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aCheckList.setTaskId(taskId);
			aCheckList.setNextTaskId(nextTaskId);
			aCheckList.setRoleCode(getRole());
			aCheckList.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCheckList, tranType);
			String operationRefs = getServiceOperations(taskId, aCheckList);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aCheckList, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aCheckList, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		CheckList aCheckList = (CheckList) auditHeader.getAuditDetail().getModelData();

		try {
			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.isBlank(method)){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getCheckListService().delete(auditHeader);
						deleteNotes=true;
					}else{
						auditHeader = getCheckListService().saveOrUpdate(auditHeader);	
					}
				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getCheckListService().doApprove(auditHeader);

						if(aCheckList.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}
					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getCheckListService().doReject(auditHeader);
						if(aCheckList.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}
					}else{
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CheckListDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CheckListDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(this.checkList),true);
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
	 * @param aCheckList
	 * @param tranType
	 * @return
	 */
	private AuditHeader getAuditHeader(CheckList aCheckList, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aCheckList.getBefImage(), aCheckList);   
		return new AuditHeader(String.valueOf(aCheckList.getCheckListId()),null,null,null,auditDetail,aCheckList.getUserDetails(),getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e
	 *            (Exception)
	 */
	@SuppressWarnings("unused")
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CheckListDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}
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
		doShowNotes(this.checkList);
	}
	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getCheckListListCtrl().search();
	}

	/**
	 *  when clicks on  "btnNew_DetailsOfExpense" 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNew_CheckListDetail(Event event) 
	throws Exception{
		logger.debug("Entering " + event.toString());
		CheckListDetail checkListDetail = new CheckListDetail();
		checkListDetail.setNewRecord(true);
		checkListDetail.setDocRequired(this.docRequired.isChecked());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("checkListDialogCtrl", this);
		map.put("checkList",getCheckList());
		map.put("checkListDetail", checkListDetail);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDetailDialog.zul",null,map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheckListDetailItemDoubleClicked(Event event)throws Exception{
		logger.debug("Entering " + event.toString());

		// get the selected invoiceHeader object
		final Listitem item=this.listbox_ChkListDetails.getSelectedItem();

		if(item!=null){	
			final CheckListDetail checkListDetail=(CheckListDetail)item.getAttribute("data");	

			if (checkListDetail.getRecordType() !=null && 
					(checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL) ||
							(checkListDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)))) {
				MessageUtil.showError(Labels.getLabel("RECORD_NO_MAINTAIN"));
			}else {
				checkListDetail.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("checkListDialogCtrl", this);
				map.put("checkList",getCheckList());
				map.put("checkListDetail", checkListDetail);
				map.put("roleCode", getRole());
				map.put("isEditable", isEditable);
				map.put("isNewRecord", getCheckList().isNew());

				try {
					Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDetailDialog.zul",null,map);

				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving " + event.toString());	
	}

	/**
	 * This method fills expense details list 
	 * @param expenseDetails
	 */
	@SuppressWarnings("unchecked")
	public void doFillCheckListDetailsList(List<CheckListDetail> checkListDetailList){
		logger.debug("Entering ");
		Comparator<Object> comp = new BeanComparator("ansDesc");
		Collections.sort(checkListDetailList,comp);
		//FIXME should checked better to remove the paging 
		this.pagingChkListDetailsList.setPageSize(100);
		this.setChekListDetailsList(checkListDetailList);
		getCheckList().setChkListList(checkListDetailList);
		getChkListDetailPagedListWrapper().initList(checkListDetailList, this.listbox_ChkListDetails, pagingChkListDetailsList);
		this.listbox_ChkListDetails.setItemRenderer(new CheckListDetailListModelItemRenderer());
		if(this.listbox_ChkListDetails.getVisibleItemCount()!=0){
			Clients.clearWrongValue(this.btnNew_CheckListDetail);
		}
		if (!getCheckList().isNew()) {
			if (checkListDetailList != null && checkListDetailList.size() > 0) {
				for (CheckListDetail checkListDetail : checkListDetailList) {
					if (checkListDetail.isDocRequired()) {
						this.docRequired.setChecked(true);
						this.docRequired.setDisabled(true);
						this.checkMaxCount.setReadonly(true);
						break;
					}
					
				}
			}else{
				this.docRequired.setDisabled(false);
			}
		}
		
		if (this.listbox_ChkListDetails.getVisibleItemCount() == 0) {
			this.docRequired.setDisabled(false);
		}else{
			this.docRequired.setDisabled(true);
		}

		logger.debug("Leaving ");

	}
	
	/**
	 * On Document Required Checked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$docRequired(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (docRequired.isChecked()) {
			this.checkMaxCount.setValue(0);
			this.checkMaxCount.setReadonly(true);

		}else{
			this.checkMaxCount.setReadonly(false);
		}
		logger.debug("Leaving " + event.toString());
	}
	
	@Override
	protected String getReference() {
		return String.valueOf(this.checkList.getCheckListId());
	}
	

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}
	public boolean isValidationOn() {
		return this.validationOn;
	}

	public CheckList getCheckList() {
		return this.checkList;
	}
	public void setCheckList(CheckList checkList) {
		this.checkList = checkList;
	}

	public void setCheckListService(CheckListService checkListService) {
		this.checkListService = checkListService;
	}
	public CheckListService getCheckListService() {
		return this.checkListService;
	}

	public void setCheckListListCtrl(CheckListListCtrl checkListListCtrl) {
		this.checkListListCtrl = checkListListCtrl;
	}
	public CheckListListCtrl getCheckListListCtrl() {
		return this.checkListListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	@SuppressWarnings("unchecked")
	public void setChkListDetailPagedListWrapper() {
		if(this.chkListDetailPagedListWrapper == null){
			this.chkListDetailPagedListWrapper = (PagedListWrapper<CheckListDetail>) SpringUtil.getBean("pagedListWrapper");
		}
	}

	public PagedListWrapper<CheckListDetail> getChkListDetailPagedListWrapper() {
		return chkListDetailPagedListWrapper;
	}

	public void setChekListDetailsList(List<CheckListDetail> chekListDetailsList) {
		this.chekListDetailsList = chekListDetailsList;
	}

	public List<CheckListDetail> getChekListDetailsList() {
		return chekListDetailsList;
	}
}
