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

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.applicationmaster.CheckListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.applicationmaster.checklist.model.CheckListDetailListModelItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/ApplicationMaster/CheckList/checkListDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class CheckListDialogCtrl extends GFCBaseListCtrl<CheckList> implements Serializable {

	private static final long serialVersionUID = 3545862467364688600L;
	private final static Logger logger = Logger.getLogger(CheckListDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 	window_CheckListDialog; 	// autoWired
	protected Textbox 	checkListDesc; 				// autoWired
	protected Intbox 	checkMinCount; 				// autoWired
	protected Intbox 	checkMaxCount; 				// autoWired
	protected Textbox 	checkRule; 					// autoWired
	protected Textbox 	lovDescCheckRule; 			// autoWired
	protected Button 	btnSearchCheckRule; 		// autoWired
	protected Checkbox 	active; 					// autoWired
	protected Listbox   listbox_ChkListDetails;     // autoWired
	protected Paging    pagingChkListDetailsList;   // autoWired

	protected Label 	recordStatus; 			    // autoWired
	protected Button    btnNew; 		            // autoWired
	protected Button    btnEdit; 		            // autoWired
	protected Button    btnDelete;              	// autoWired
	protected Button    btnSave; 		            // autoWired
	protected Button    btnCancel;              	// autoWired
	protected Button    btnClose; 		            // autoWired
	protected Button    btnHelp; 		            // autoWired
	protected Button    btnNotes; 		            // autoWired
	protected Grid 		grid_Basicdetails;	    	// autoWired

	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	
	protected Button        btnNew_CheckListDetail; // autoWired

	// not auto wired variables
	private CheckList checkList; 							// overHanded per parameter
	private CheckList prvCheckList; 						// overHanded per parameter
	private transient CheckListListCtrl checkListListCtrl; 	// overHanded per parameter

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String  	 oldVar_checkListDesc;
	private transient int  		 oldVar_checkMinCount;
	private transient int  		 oldVar_checkMaxCount;
	private transient String  	 oldVar_checkRule;
	private transient boolean  	 oldVar_active;
	private transient String 	 oldVar_recordStatus;
	private List<CheckListDetail> oldVar_CheckListDetailList;

	private transient boolean validationOn;
	private boolean notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_CheckListDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	// ServiceDAOs / Domain Classes
	private transient CheckListService checkListService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();
	private List<CheckListDetail> chekListDetailsList=new ArrayList<CheckListDetail>();
	private PagedListWrapper<CheckListDetail> chkListDetailPagedListWrapper;
	int listRows;

	/**
	 * default constructor.<br>
	 */
	public CheckListDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected CheckList object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_CheckListDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		setChkListDetailPagedListWrapper();
		getChkListDetailPagedListWrapper();
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the parameters map that are overHanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("checkList")) {
			this.checkList = (CheckList) args.get("checkList");
			CheckList befImage =new CheckList();
			BeanUtils.copyProperties(this.checkList, befImage);
			this.checkList.setBefImage(befImage);

			setCheckList(this.checkList);
		} else {
			setCheckList(null);
		}
		//Set the DialogController Height for listBox
		getBorderLayoutHeight();
		grid_Basicdetails.getRows().getVisibleItemCount();
		int dialogHeight =  grid_Basicdetails.getRows().getVisibleItemCount()* 20 + 100 +35; 
		int listboxHeight = borderLayoutHeight-dialogHeight;
		listbox_ChkListDetails.setHeight(listboxHeight+"px");
		listRows = Math.round(listboxHeight/ 24)-1;

		doLoadWorkFlow(this.checkList.isWorkflow(),this.checkList.getWorkflowId(),this.checkList.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "CheckListDialog");
		}


		// READ OVERHANDED parameters !
		// we get the checkListListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete checkList here.
		if (args.containsKey("checkListListCtrl")) {
			setCheckListListCtrl((CheckListListCtrl) args.get("checkListListCtrl"));
		} else {
			setCheckListListCtrl(null);
		}

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getCheckList());
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.checkListDesc.setMaxlength(100);
		this.checkMinCount.setMaxlength(10);
		this.checkMaxCount.setMaxlength(10);
		this.checkRule.setMaxlength(8);

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

		getUserWorkspace().alocateAuthorities("CheckListDialog");

		this.btnNew_CheckListDetail.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnNew_CheckListDetail"));
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CheckListDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_CheckListDialog(Event event) throws Exception {
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
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
		PTMessageUtils.showHelpWindow(event, window_CheckListDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
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
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
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
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, 
					MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

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
			closeDialog(this.window_CheckListDialog, "CheckList");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
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
		this.checkRule.setValue(aCheckList.getCheckRule());
		this.lovDescCheckRule.setValue(StringUtils.trimToEmpty(aCheckList.getCheckRule()).equals("")? "" : aCheckList.getCheckRule()+"-"+aCheckList.getLovDescCheckRuleName());
		this.active.setChecked(aCheckList.isActive());
		this.recordStatus.setValue(aCheckList.getRecordStatus());
		
		if(aCheckList.isNew() || StringUtils.trimToEmpty(aCheckList.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
			this.active.setChecked(true);
			this.active.setDisabled(true);
		}
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
			aCheckList.setCheckMinCount(this.checkMinCount.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckList.setCheckMaxCount(this.checkMaxCount.intValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckList.setCheckRule(this.checkRule.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aCheckList.setActive(this.active.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			if(this.checkMaxCount.intValue()<this.checkMinCount.intValue()){
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
	 * @throws InterruptedException
	 */
	public void doShowDialog(CheckList aCheckList) throws InterruptedException {
		logger.debug("Entering") ;

		// if aCheckList == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aCheckList == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aCheckList = getCheckListService().getNewCheckList();

			setCheckList(aCheckList);
		} else {
			setCheckList(aCheckList);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aCheckList.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.checkListDesc.focus();
		} else {
			this.checkListDesc.focus();
			if (isWorkFlowEnabled()){
				if (!StringUtils.trimToEmpty(aCheckList.getRecordType()).equals("")) {
					this.btnNotes.setVisible(true);
				}
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aCheckList);

			// stores the initial data for comparing if they are changed
			// during user action.
		
			doFillCheckListDetailsList(getCheckList().getChkListList());
			doStoreInitValues();
			setDialog(this.window_CheckListDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_checkListDesc = this.checkListDesc.getValue();
		this.oldVar_checkMinCount = this.checkMinCount.intValue();	
		this.oldVar_checkMaxCount = this.checkMaxCount.intValue();	
		this.oldVar_checkRule = this.checkRule.getValue();	
		this.oldVar_active = this.active.isChecked();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		this.oldVar_CheckListDetailList=this.chekListDetailsList;
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the initial values from member variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.checkListDesc.setValue(this.oldVar_checkListDesc);
		this.checkMinCount.setValue(this.oldVar_checkMinCount);
		this.checkMaxCount.setValue(this.oldVar_checkMaxCount);
		this.checkRule.setValue(this.oldVar_checkRule);
		this.active.setChecked(this.oldVar_active);
		this.recordStatus.setValue(this.oldVar_recordStatus);
		this.chekListDetailsList=this.oldVar_CheckListDetailList;

		if(isWorkFlowEnabled()){
			this.userAction.setSelectedIndex(0);	
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
		if (this.oldVar_checkListDesc != this.checkListDesc.getValue()) {
			return true;
		}
		if (this.oldVar_checkMinCount != this.checkMinCount.intValue()) {
			return  true;
		}
		if (this.oldVar_checkMaxCount != this.checkMaxCount.intValue()) {
			return  true;
		}
		if (this.oldVar_checkRule != this.checkRule.getValue()) {
			return  true;
		}
		if (this.oldVar_active != this.active.isChecked()) {
			return true;
		}
		if (this.oldVar_CheckListDetailList != this.chekListDetailsList) {
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
		setValidationOn(true);
		if (!this.checkListDesc.isReadonly()){
			this.checkListDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CheckListDialog_CheckListDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.checkMinCount.isReadonly()){
			this.checkMinCount.setConstraint(new IntValidator(5, Labels.getLabel("label_CheckListDialog_CheckMinCount.value"), false));

		}	
		if (!this.checkMaxCount.isReadonly()){
			this.checkMaxCount.setConstraint(new IntValidator(5, Labels.getLabel("label_CheckListDialog_CheckMaxCount.value"), false));
		}	
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
		if (!this.btnSearchCheckRule.isDisabled()){
			this.lovDescCheckRule.setConstraint(new PTStringValidator(Labels.getLabel("label_CheckListDialog_CheckRule.value"), null, true));
		}	
		logger.debug("Leaving");
	}

	/**
	 * Remove Validations for LOV Fields
	 */
	private void doRemoveLOVValidation() {
		logger.debug("Entering");
		this.lovDescCheckRule.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.checkListDesc.setErrorMessage("");
		this.checkMinCount.setErrorMessage("");
		this.checkMaxCount.setErrorMessage("");
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

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
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + aCheckList.getCheckListId();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aCheckList.getRecordType()).equals("")){
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
					closeDialog(this.window_CheckListDialog, "CheckList"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new CheckList object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		final CheckList aCheckList = getCheckListService().getNewCheckList();
		setCheckList(aCheckList);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// remember the old variables
		doStoreInitValues();

		// setFocus
		this.checkListDesc.focus();
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
		this.checkMaxCount.setReadonly(isReadOnly("CheckListDialog_checkMaxCount"));
		this.checkRule.setReadonly(isReadOnly("CheckListDialog_checkRule"));
		this.btnSearchCheckRule.setDisabled(isReadOnly("CheckListDialog_checkRule"));
		this.active.setDisabled(isReadOnly("CheckListDialog_active"));

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
			btnCancel.setVisible(true);
		}
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.checkListDesc.setReadonly(true);
		this.checkMinCount.setReadonly(true);
		this.checkMaxCount.setReadonly(true);
		this.checkRule.setReadonly(true);
		this.btnSearchCheckRule.setDisabled(true);
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
		this.checkRule.setValue("");
		this.lovDescCheckRule.setValue("");
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

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
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
			if (StringUtils.trimToEmpty(aCheckList.getRecordType()).equals("")){
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
				closeDialog(this.window_CheckListDialog, "CheckList");
			}

		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
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

		aCheckList.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aCheckList.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCheckList.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aCheckList.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCheckList.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aCheckList);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aCheckList))) {
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
			if (!StringUtils.trimToEmpty(nextTaskId).equals("")) {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aCheckList.setTaskId(taskId);
			aCheckList.setNextTaskId(nextTaskId);
			aCheckList.setRoleCode(getRole());
			aCheckList.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aCheckList, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aCheckList);

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

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
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
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_CheckListDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_CheckListDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
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
	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_CheckListDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
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
		logger.debug("Entering");
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

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

	// Get the notes entered for rejected reason
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("CheckList");
		notes.setReference(String.valueOf(getCheckList().getCheckListId()));
		notes.setVersion(getCheckList().getVersion());
		return notes;
	}

	// Method for refreshing the list after successful update
	private void refreshList(){
		final JdbcSearchObject<CheckList> soCheckList = getCheckListListCtrl().getSearchObj();
		getCheckListListCtrl().pagingCheckListList.setActivePage(0);
		getCheckListListCtrl().getPagedListWrapper().setSearchObject(soCheckList);
		if(getCheckListListCtrl().listBoxCheckList!=null){
			getCheckListListCtrl().listBoxCheckList.getListModel();
		}
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
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("checkListDialogCtrl", this);
		map.put("checkList",getCheckList());
		map.put("checkListDetail", checkListDetail);
		map.put("roleCode", getRole());
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDetailDialog.zul",null,map);

		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
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
				PTMessageUtils.showErrorMessage(Labels.getLabel("RECORD_NO_MAINTAIN"));
			}else {
				checkListDetail.setNewRecord(false);
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("checkListDialogCtrl", this);
				map.put("checkList",getCheckList());
				map.put("checkListDetail", checkListDetail);
				map.put("roleCode", getRole());

				try {
					Executions.createComponents("/WEB-INF/pages/ApplicationMaster/CheckList/CheckListDetailDialog.zul",null,map);

				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
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
		this.pagingChkListDetailsList.setPageSize(listRows);
		this.setChekListDetailsList(checkListDetailList);
		getCheckList().setChkListList(checkListDetailList);
		getChkListDetailPagedListWrapper().initList(checkListDetailList, this.listbox_ChkListDetails, pagingChkListDetailsList);
		this.listbox_ChkListDetails.setItemRenderer(new CheckListDetailListModelItemRenderer());
		logger.debug("Leaving ");

	}
	
	
	/**
	 * To get the AccountingSet LOV List From RMTAERules Table Records are filtered by EventCode where EventCode=INCPFT
	 */
	public void onClick$btnSearchCheckRule(Event event) {
		logger.debug("Entering" + event.toString());
		
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("RuleModule", "CLRULE", Filter.OP_EQUAL);
		filters[1] = new Filter("RuleEvent", "", Filter.OP_EQUAL);
		
		Object dataObject = ExtendedSearchListBox.show(this.window_CheckListDialog, "Rule", filters);
		if (dataObject instanceof String) {
			this.checkRule.setValue("");
			this.lovDescCheckRule.setValue("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.checkRule.setValue(String.valueOf(details.getRuleCode()));
				this.lovDescCheckRule.setValue(details.getRuleCode() + "-" + details.getRuleCodeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

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

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public CheckList getPrvCheckList() {
		return prvCheckList;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	@SuppressWarnings("unchecked")
	public void setChkListDetailPagedListWrapper() {
		if(this.chkListDetailPagedListWrapper == null){
			this.chkListDetailPagedListWrapper = (PagedListWrapper<CheckListDetail>) SpringUtil.getBean("pagedListWrapper");;
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
