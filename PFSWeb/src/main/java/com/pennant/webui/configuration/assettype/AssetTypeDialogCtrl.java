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
 * FileName    		:  AssetTypeDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-12-2016    														*
 *                                                                  						*
 * Modified Date    :  14-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.configuration.assettype;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.model.extendedfields.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.configuration.AssetTypeService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.solutionfactory.extendedfielddetail.ExtendedFieldDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/configuration/AssetType/assetTypeDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class AssetTypeDialogCtrl extends GFCBaseCtrl <AssetType> {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(AssetTypeDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ All the components that are defined here
	 * and have a corresponding component with the same 'id' in the zul-file are getting by our 'extends GFCBaseCtrl'
	 * GenericForwardComposer. ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_AssetTypeDialog; 
	protected Uppercasebox 	assetType; 
	protected Textbox 		assetDesc; 
	protected Textbox 		remarks; 
	protected Checkbox 		active; 
	protected Codemirror 	preValidation; 
	protected Codemirror 	postValidation; 
	protected Grid 			preValidationGrid;
	protected Grid 			postValidationGrid;
	protected Tab			extendedDetailsTab;
	protected Tab			preValidationTab;
	protected Tab			postValidationTab;
	protected Tabpanel		extendedFieldTabpanel;
	private transient AssetTypeListCtrl assetTypeListCtrl; 
	private transient AssetTypeService 	assetTypeService;
	private transient ExtendedFieldDialogCtrl extendedFieldDialogCtrl;
	protected Button 							btnCopyTo;

	private AssetType 	assetConfigurationType; 
	private boolean 	enqModule=false;
	protected Label preModuleDesc;
	protected Label preSubModuleDesc;
	protected Label postModuleDesc;
	protected Label postSubModuleDesc;
	protected Listbox prevalidationListbox;
	protected Listbox postValidationListbox;
	protected Button btnValidate;
	protected Button btnSimulate;
	protected Button button_pre_Simulate;
	protected Button button_post_Simulate;
	JSONArray variables = new JSONArray();
	private List<String> fieldNames = new ArrayList<String>();
	protected boolean 							alwCopyOption = false;
	protected boolean 							isCopyProcess = false;
	protected boolean 							preScriptValidated = false;
	protected boolean 							postScriptValidated = false;
	
	/**
	 * default constructor.<br>
	 */
	public AssetTypeDialogCtrl() {
		super();
	}


	@Override
	protected void doSetProperties() {
		super.pageRightName = "AssetTypeDialog";
	}


	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected AssetType object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_AssetTypeDialog(Event event) throws Exception {
		logger.debug("Entring" +event.toString());
		try {

			// Set the page level components.
			setPageComponents(window_AssetTypeDialog);

			// READ OVERHANDED params !
			if (arguments.containsKey("enqModule")) {
				enqModule=(Boolean) arguments.get("enqModule");
			}else{
				enqModule=false;
			}
			
			this.alwCopyOption = (Boolean) arguments.get("alwCopyOption");
			this.isCopyProcess = (Boolean) arguments.get("isCopyProcess");

			// READ OVERHANDED params !
			if (arguments.containsKey("assetConfigurationType")) {
				this.assetConfigurationType = (AssetType) arguments.get("assetConfigurationType");
				AssetType befImage =new AssetType();
				BeanUtils.copyProperties(this.assetConfigurationType, befImage);
				this.assetConfigurationType.setBefImage(befImage);

				setAssetType(this.assetConfigurationType);
			} else {
				setAssetType(null);
			}
			doLoadWorkFlow(this.assetConfigurationType.isWorkflow(),this.assetConfigurationType.getWorkflowId(),this.assetConfigurationType.getNextTaskId());

			if (isWorkFlowEnabled() && !enqModule){
				this.userAction	= setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "AssetTypeDialog");
			}else{
				getUserWorkspace().allocateAuthorities("AssetTypeDialog");
			}

			if (arguments.containsKey("assetTypeListCtrl")) {
				setAssetTypeListCtrl((AssetTypeListCtrl) arguments.get("assetTypeListCtrl"));
			} else {
				setAssetTypeListCtrl(null);
			}

			// set Field Properties
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(getAssetType());
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Method for setting label details on Header in Selecting Tab
	 * @param event
	 */
	public void onSelect$preValidationTab(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.preModuleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.preSubModuleDesc.setValue(this.assetType.getValue());
		this.prevalidationListbox.getItems().clear();
		renderScriptFields(prevalidationListbox);
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for setting label details on Header in Selecting Tab
	 * @param event
	 */
	public void onSelect$postValidationTab(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.postModuleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.postSubModuleDesc.setValue(this.assetType.getValue());
		this.postValidationListbox.getItems().clear();
		renderScriptFields(postValidationListbox);
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for rendering Field Details from Extended fields for Validations & Simulation
	 * @param listbox
	 */
	private void renderScriptFields(Listbox listbox){
		logger.debug("Entering");
		
		if (getExtendedFieldDialogCtrl() != null) {
			List<ExtendedFieldDetail>  extFieldList= getExtendedFieldDialogCtrl().getExtendedFieldDetailsList();
			if (extFieldList != null && !extFieldList.isEmpty()) {
				for (ExtendedFieldDetail details : extFieldList) {
					if (!StringUtils.equals(details.getRecordType(),PennantConstants.RECORD_TYPE_DEL) && 
							!StringUtils.equals(details.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
						Listitem item = new Listitem();
						Listcell lc = new Listcell(details.getFieldName());
						lc.setParent(item);
						lc = new Listcell(details.getFieldLabel());
						lc.setParent(item);
						listbox.appendChild(item);
					}
				}
			}
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPreValidate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event , false, false)) {
			preScriptValidated = true;
			//check if code mirror is empty or not 
			if(StringUtils.isNotEmpty(this.preValidation.getValue().trim())){
				if (MessageUtil.confirm("NO Errors Found! Proceed With Simulation?") == MessageUtil.YES) {
					// create a new window for input values
					createSimulationWindow(variables, this.preValidation.getValue());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPostValidate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event, true , false)) {
			postScriptValidated = true;
			//check if code mirror is empty or not 
			if(StringUtils.isNotEmpty(this.postValidation.getValue().trim())){
				if (MessageUtil.confirm("NO Errors Found! Proceed With Simulation?") == MessageUtil.YES) {
					// create a new window for input values
					createSimulationWindow(variables, this.postValidation.getValue());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * 
	 * @param event
	 */
	public void onChange$postValidation(Event event) {
		postScriptValidated = false;
	}
	/**
	 * 
	 * @param event
	 */
	public void onChange$preValidation(ForwardEvent event) {
		preScriptValidated = false;
	}

			
	/**
	 * CALL THE RESULT ZUL FILE
	 * 
	 * @param jsonArray
	 * @throws InterruptedException
	 */
	public void createSimulationWindow(JSONArray jsonArray, String scriptRule) throws InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("variables", jsonArray);
		map.put("scriptRule", scriptRule);
		
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralStructure/ScriptValidationResult.zul", null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS
	 * 
	 * @param event
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validate(ForwardEvent event, boolean isPostValidation, boolean bothValidations) throws InterruptedException {
		boolean noerrors = true;
		// object containing errors and variables
		Object[] data = (Object[]) event.getOrigin().getData();
		// array of errors
		if (data != null && data.length != 0) {
			JSONArray errors = (JSONArray) data[0];
			// array of variables
			variables = (JSONArray) data[1];

			// if no errors
			if (variables != null && errors.size() == 0) {
				// check for new declared variables
				for (int i = 0; i < variables.size(); i++) {
					JSONObject variable = (JSONObject) variables.get(i);
					if (!"errors".equals(variable.get("name"))) {
						if (!fieldNames.contains(variable.get("name"))) {
							// if new variables found throw error message
							noerrors = false;
							MessageUtil.showError("Unknown Variable :" + variable.get("name"));
							return noerrors;
						} else {
							noerrors = true;
						}
					}
				}
				if (noerrors) {
					return validateResult(isPostValidation, bothValidations);
				}

			} else {
				for (int i = 0; i < errors.size(); i++) {
					JSONObject error = (JSONObject) errors.get(i);
					if (error != null) {
						MessageUtil.showError("Error : At Line " + error.get("line") + ",Position "
								+ error.get("character") + "\n\n" + error.get("reason").toString());
						return false;
					}
				}
			}
		} else {
			return true;
		}
		return noerrors;
	}

	/**
	 * Method for Checking script has Error Details information or not.
	 * @param isPostValidation
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validateResult(boolean isPostValidation, boolean bothValidations) throws InterruptedException {

		if(!bothValidations){
			if(isPostValidation){
				if (!this.postValidation.getValue().contains("errors")) {
					MessageUtil.showError("Error Details not found ");
					return false;
				}
			}else{
				if (!this.preValidation.getValue().contains("errors")) {
					MessageUtil.showError("Error Details not found ");
					return false;
				}
			}
		}else{
			if (StringUtils.isNotEmpty(this.preValidation.getValue()) && !this.preValidation.getValue().contains("errors")) {
				MessageUtil.showError("Error Details not found in Pre Validations.");
				return false;
			}else if(StringUtils.isNotEmpty(this.postValidation.getValue()) && !this.postValidation.getValue().contains("errors")){
				MessageUtil.showError("Error Details not found in Post Validations.");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPreSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event , false , false)) {
			// create a new window for input values
			createSimulationWindow(variables, this.preValidation.getValue());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPostSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if (validate(event, true , false)) {
			// create a new window for input values
			createSimulationWindow(variables, this.postValidation.getValue());
		}
		logger.debug("Leaving" + event.toString());
	}
	

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}
	
	/**
	 * Method for On click action on Copy button to make Duplicate record with existing Data
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug("Entering");
		
		if (MessageUtil.confirm(Labels.getLabel("conf.closeWindowWithoutSave")) == MessageUtil.YES) {
			closeAssetWindow();
			Events.postEvent("onClick$button_AssetTypeList_NewAssetType",
					assetTypeListCtrl.window_AssetTypeList, this.assetConfigurationType);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Checking Actual Initiated Owner of the Record
	 * @return
	 */
	private boolean isMaintainable() {
		// If workflow enabled and not first task owner then cannot maintain. Else can maintain
		if (isWorkFlowEnabled()) {
			if (!StringUtils.equals(getRole(), getWorkFlow().firstTaskOwner())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws InterruptedException 
	 * @throws ParseException 
	 */
	public void onClick$btnCancel(Event event) throws ParseException, InterruptedException {
		doCancel();
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
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		// TODO: Open Comment If, save is working on ZK scripts for validation 
		/*boolean validationReq = true;
		if (this.userAction.getSelectedItem() != null){
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) ||
					this.userAction.getSelectedItem().getLabel().contains("Reject") ||
					this.userAction.getSelectedItem().getLabel().contains("Resubmit") ||
					this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				validationReq = false;
			}
		}*/
		
		// Pre Validation Checking for Validated or not
		if(StringUtils.isNotEmpty(this.preValidation.getValue().trim()) && !preScriptValidated){
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PreValidation") }));
			return;
		}
		
		// Post Validation Checking for Validated or not
		if(StringUtils.isNotEmpty(this.postValidation.getValue().trim()) && !postScriptValidated){
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PostValidation") }));
			return;
		}
		
		// Validation Details are correct and validated
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_AssetTypeDialog(Event event) throws Exception {
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
		doShowNotes(this.assetConfigurationType);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAssetType
	 *            The entity that need to be render.
	 */
	public void doShowDialog(AssetType aAssetType) throws InterruptedException {
		logger.debug("Entering");

		if (aAssetType.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.assetType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.assetDesc.focus();
				if (StringUtils.isNotBlank(aAssetType.getRecordType())) {
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

		try {

			// fill the components with the data
			doWriteBeanToComponents(aAssetType);
			
			int height = getContentAreaHeight() ;
			this.preValidationGrid.setHeight(height-150+"px");
			this.postValidationGrid.setHeight(height-150+"px");
			this.preValidation.setHeight(height-160+"px");
			this.postValidation.setHeight(height-160+"px");
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.assetConfigurationType.isNewRecord()) {
			this.assetType.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.active.setDisabled(true);
			this.btnCopyTo.setVisible(false);
		} else {
			this.assetType.setReadonly(true);
			this.btnCancel.setVisible(true);
			if(StringUtils.equals(getAssetType().getRecordType(), PennantConstants.RECORD_TYPE_NEW)){
				this.active.setDisabled(true);
			}else{				
				this.active.setDisabled(isReadOnly("AssetTypeDialog_Active"));
			}
			this.btnCopyTo.setVisible(isMaintainable() && alwCopyOption);
		}

		this.assetDesc.setReadonly(isReadOnly("AssetTypeDialog_AssetDescription"));
		this.remarks.setReadonly(isReadOnly("AssetTypeDialog_Remarks"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.assetConfigurationType.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug("Leaving ");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.assetType.setReadonly(true);
		this.assetDesc.setReadonly(true);
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
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering") ;

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_AssetTypeDialog_btnSave"));
		this.btnCancel.setVisible(false);


		logger.debug("Leaving") ;
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		//Empty sent any required attributes
		this.assetType.setMaxlength(8);
		this.assetDesc.setMaxlength(20);
		this.remarks.setMaxlength(1000);
		setStatusDetails();
		logger.debug("Leaving") ;
	}

	/**
	 * Method for setting Basic Details on Selecting Extended Details Tab
	 * @param event
	 */
	public void onSelect$extendedDetailsTab(Event event) {
		getExtendedFieldDialogCtrl().doSetBasicDetail(
				AssetConstants.EXTENDEDFIELDS_MODULE, this.assetType.getValue(), this.assetDesc.getValue());
	}


	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aAssetType
	 *            AssetType
	 */
	public void doWriteBeanToComponents(AssetType aAssetType) {
		logger.debug("Entering") ;
		
		if(aAssetType.isNewRecord()) {
			this.active.setChecked(true);
		}else{
			this.active.setChecked(aAssetType.isActive());
		}
	
		this.preValidation.setValue(aAssetType.getPreValidation());
		this.postValidation.setValue(aAssetType.getPostValidation());
		this.assetType.setValue(aAssetType.getAssetType());
		this.assetDesc.setValue(aAssetType.getAssetDesc());
		this.remarks.setValue(aAssetType.getRemarks());
		
		//Extended Field Details tab
		appendExtendedFieldsTab();
		
		// Default Values Setting for Script Validations
		postScriptValidated = true;
		preScriptValidated = true;

		this.recordStatus.setValue(aAssetType.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAssetType
	 */
	public void doWriteComponentsToBean(AssetType aAssetType) {
		logger.debug("Entering") ;
		doSetValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		//Asset Type
		try {
			aAssetType.setAssetType(this.assetType.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Asset Description
		try {
			aAssetType.setAssetDesc(this.assetDesc.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Remarks
		try {
			aAssetType.setRemarks(this.remarks.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Active
		try {
			aAssetType.setActive(this.active.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAssetType.setPreValidation(this.preValidation.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		try {
			aAssetType.setPostValidation(this.postValidation.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		//Extended Field Details 
		if (getExtendedFieldDialogCtrl() != null) {
			ExtendedFieldHeader extendedFieldHeader = getExtendedFieldDialogCtrl().doSave_ExtendedFields(extendedDetailsTab);
			extendedFieldHeader.setModuleName(AssetConstants.EXTENDEDFIELDS_MODULE);
			extendedFieldHeader.setSubModuleName(aAssetType.getAssetType());
			aAssetType.setExtendedFieldHeader(extendedFieldHeader);
		}

		doRemoveValidation();

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
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		//Asset Type
		if (!this.assetType.isReadonly()){
			this.assetType.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetTypeDialog_AssetType.value"),PennantRegularExpressions.REGEX_UPP_BOX_ALPHA,true));
		}
		//Asset Description
		if (!this.assetDesc.isReadonly()){
			this.assetDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetTypeDialog_AssetDescription.value"),null,true));
		}
		//Remarks
		if (!this.remarks.isReadonly()){
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_AssetTypeDialog_Remarks.value"),null,false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.assetType.setConstraint("");
		this.assetDesc.setConstraint("");
		this.remarks.setConstraint("");
		logger.debug("Leaving");
	}


	//Append ExtendedFieldsTab 
	private void appendExtendedFieldsTab() {
		try {
			ExtendedFieldHeader extendedFieldHeader = assetConfigurationType.getExtendedFieldHeader();
			extendedFieldHeader.setModuleName(AssetConstants.EXTENDEDFIELDS_MODULE);
			if (assetConfigurationType.isNew()) {
				extendedFieldHeader.setSubModuleName(assetConfigurationType.getAssetType());
				extendedFieldHeader.setNumberOfColumns("2");
			}
			Map<String, Object> map = new HashMap<>();
			map.put("extendedFieldHeader", extendedFieldHeader);
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("firstTaskRole", isFirstTask());
			if (assetConfigurationType.isNew()) {
				map.put("newRecord", assetConfigurationType.isNew());
			}
			map.put("moduleName", AssetConstants.EXTENDEDFIELDS_MODULE);

			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul", extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Remove Error Messages for Fields
	 */

	public void doClearMessage() {
		logger.debug("Entering");
		this.assetType.setErrorMessage("");
		this.assetDesc.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Refreshing List after Save/Delete a Record
	 */

	private void refreshList() {
		final JdbcSearchObject<AssetType> aAssetType = getAssetTypeListCtrl().getSearchObject();
		getAssetTypeListCtrl().pagingAssetTypeList.setActivePage(0);
		getAssetTypeListCtrl().getPagedListWrapper().setSearchObject(aAssetType);
		if (getAssetTypeListCtrl().listBoxAssetType != null) {
			getAssetTypeListCtrl().listBoxAssetType.getListModel();
		}
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * @throws InterruptedException 
	 * @throws ParseException 
	 * 
	 */
	private void doCancel() throws ParseException, InterruptedException {
		logger.debug("Entering");

		doWriteBeanToComponents(this.assetConfigurationType.getBefImage());
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
			closeAssetWindow();
		}

		logger.debug("Leaving");
	}
	
	
	public void closeAssetWindow() {
		
		if (getExtendedFieldDialogCtrl() != null) {
			getExtendedFieldDialogCtrl().closeDialog();
		}
		closeDialog();
	}

	/**
	 * Deletes a AssetType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		final AssetType aAssetType = new AssetType();
		BeanUtils.copyProperties(getAssetType(), aAssetType);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ aAssetType.getAssetType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.trimToEmpty(aAssetType.getRecordType()).equals("")) {
				aAssetType.setVersion(aAssetType.getVersion() + 1);
				aAssetType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aAssetType.setRecordStatus(userAction.getSelectedItem().getValue().toString());
					aAssetType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
					getWorkFlowDetails(userAction.getSelectedItem().getLabel(), aAssetType.getNextTaskId(), aAssetType);
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aAssetType, tranType)) {
					refreshList();
					closeAssetWindow();
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

		this.assetType.setValue("");
		this.assetDesc.setValue("");
		this.remarks.setValue("");
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

		final AssetType aAssetType = new AssetType();
		BeanUtils.copyProperties(getAssetType(), aAssetType);
		boolean isNew = false;

		doClearMessage();
		doSetValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aAssetType);
		// doStoreInitValues();
		isNew = aAssetType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aAssetType.getRecordType())) {
				aAssetType.setVersion(aAssetType.getVersion() + 1);
				if (isNew) {
					aAssetType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aAssetType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aAssetType.setNewRecord(true);
				}
			}
		} else {
			aAssetType.setVersion(aAssetType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aAssetType, tranType)) {
				refreshList();
				closeAssetWindow();
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

	private boolean doProcess(AssetType aAssetType,String tranType){
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aAssetType.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aAssetType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aAssetType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aAssetType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aAssetType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aAssetType);
				}

				if (isNotesMandatory(taskId, aAssetType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
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

			aAssetType.setTaskId(taskId);
			aAssetType.setNextTaskId(nextTaskId);
			aAssetType.setRoleCode(getRole());
			aAssetType.setNextRoleCode(nextRoleCode);

			// Set workflow values
			ExtendedFieldHeader extFldHeader = aAssetType.getExtendedFieldHeader();
			extFldHeader.setWorkflowId(aAssetType.getWorkflowId());
			extFldHeader.setRecordStatus(aAssetType.getRecordStatus());
			extFldHeader.setTaskId(aAssetType.getTaskId());
			extFldHeader.setNextTaskId(aAssetType.getNextTaskId());
			extFldHeader.setRoleCode(aAssetType.getRoleCode());
			extFldHeader.setNextRoleCode(aAssetType.getNextRoleCode());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aAssetType.getRecordType())) {
				if (StringUtils.trimToNull(extFldHeader.getRecordType()) == null) {
					extFldHeader.setRecordType(aAssetType.getRecordType());
					extFldHeader.setNewRecord(true);
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equals(aAssetType.getRecordType())) {
				extFldHeader.setRecordType(aAssetType.getRecordType());
				extFldHeader.setNewRecord(aAssetType.isNewRecord());
			}

			for (ExtendedFieldDetail ext : extFldHeader.getExtendedFieldDetails()) {
				ext.setWorkflowId(aAssetType.getWorkflowId());
				ext.setRecordStatus(aAssetType.getRecordStatus());
				ext.setTaskId(aAssetType.getTaskId());
				ext.setNextTaskId(aAssetType.getNextTaskId());
				ext.setRoleCode(aAssetType.getRoleCode());
				ext.setNextRoleCode(aAssetType.getNextRoleCode());
				if (PennantConstants.RECORD_TYPE_DEL.equals(aAssetType.getRecordType())) {
					if (StringUtils.trimToNull(ext.getRecordType()) == null) {
						ext.setRecordType(aAssetType.getRecordType());
						ext.setNewRecord(true);
					}
				}
			}

			auditHeader = getAuditHeader(aAssetType, tranType);
			String operationRefs = getServiceOperations(taskId, aAssetType);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aAssetType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aAssetType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		AssetType aAssetType = (AssetType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getAssetTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getAssetTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getAssetTypeService().doApprove(auditHeader);
						if (aAssetType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getAssetTypeService().doReject(auditHeader);
						if (aAssetType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {

						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_AssetTypeDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_AssetTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.assetConfigurationType), true);
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
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * @param aAuthorizedSignatoryRepository
	 * @param tranType
	 * @return
	 */

	private AuditHeader getAuditHeader(AssetType aAssetType, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aAssetType.getBefImage(), aAssetType);   
		return new AuditHeader(aAssetType.getAssetType(),null,null,null,auditDetail,aAssetType.getUserDetails(),getOverideMap());
	}


	@Override
	protected String getReference() {
		return String.valueOf(this.assetConfigurationType.getId());
	}
	public AssetType getAssetType() {
		return this.assetConfigurationType;
	}

	public void setAssetType(AssetType assetConfigurationType) {
		this.assetConfigurationType = assetConfigurationType;
	}

	public void setAssetTypeService(AssetTypeService assetTypeService) {
		this.assetTypeService = assetTypeService;
	}

	public AssetTypeService getAssetTypeService() {
		return this.assetTypeService;
	}

	public void setAssetTypeListCtrl(AssetTypeListCtrl assetTypeListCtrl) {
		this.assetTypeListCtrl = assetTypeListCtrl;
	}

	public AssetTypeListCtrl getAssetTypeListCtrl() {
		return this.assetTypeListCtrl;
	}

	public ExtendedFieldDialogCtrl getExtendedFieldDialogCtrl() {
		return extendedFieldDialogCtrl;
	}
	public void setExtendedFieldDialogCtrl(ExtendedFieldDialogCtrl extendedFieldDialogCtrl) {
		this.extendedFieldDialogCtrl = extendedFieldDialogCtrl;
	}
	public List<String> getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

}
