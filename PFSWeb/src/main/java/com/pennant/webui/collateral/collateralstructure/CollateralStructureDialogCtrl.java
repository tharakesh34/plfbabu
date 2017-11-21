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
 * FileName    		:  CollateralStructureDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  29-11-2016    														*
 *                                                                  						*
 * Modified Date    :  29-11-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 29-11-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.webui.collateral.collateralstructure;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
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
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.extendedfields.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.collateral.CollateralStructureService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.solutionfactory.extendedfielddetail.ExtendedFieldDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MessageUtil;

/**
 * ************************************************************<br>
 * This is the controller class for the
 * /WEB-INF/pages/collateral/CollateralstructureDialog.zul file. <br>
 * ************************************************************<br>
 */
public class CollateralStructureDialogCtrl extends GFCBaseCtrl<CollateralStructure> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CollateralStructureDialogCtrl.class);

	protected Window window_CollateralStructureDialog;
	protected Textbox collateralType;
	protected Textbox collateralDesc;
	protected Combobox ltvType;
	protected Decimalbox ltvPercentage;
	protected Space space_LtvPercentage;
	protected Checkbox marketableSecurities;
	protected Checkbox active;
	protected Checkbox preValidationReq;
	protected Checkbox postValidationReq;
	protected Checkbox collateralLocReq;
	protected Checkbox collateralValuatorReq;
	protected Textbox remarks;

	protected Label moduleDesc;
	protected Label subModuleDesc;

	protected Label preModuleDesc;
	protected Label preSubModuleDesc;
	protected Button btnPreValidate;
	protected Button btnPreSimulate;

	protected Label postModuleDesc;
	protected Label postSubModuleDesc;
	protected Button btnPostValidate;
	protected Button btnPostSimulate;

	protected Checkbox allowLtvWaiver;
	protected Space space_MaxLtvWaiver;
	protected Decimalbox maxLtvWaiver;
	protected JavaScriptBuilder javaScriptSqlRule;

	protected Codemirror postValidation;
	protected Codemirror preValidation;
	protected Listbox prevalidationListbox;
	protected Grid preValidationGrid;
	protected Grid postValidationGrid;
	protected Listbox postValidationListbox;

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab basicDetailsTab;
	protected Tab extendedDetailsTab;
	protected Tab ltvRuleTab;
	protected Tab preValidationTab;
	protected Tab postValidationTab;
	protected Tabpanel extendedFieldTabpanel;
	protected Button btnCopyTo;

	private CollateralStructure collateralStructure;
	private transient CollateralStructureListCtrl collateralStructureListCtrl;
	private transient CollateralStructureService collateralStructureService;
	private transient ExtendedFieldDialogCtrl extendedFieldDialogCtrl;
	private JSONArray variables = new JSONArray();
	private List<String> fieldNames = new ArrayList<String>();
	protected boolean alwCopyOption = false;
	protected boolean isCopyProcess = false;
	protected boolean preScriptValidated = false;
	protected boolean postScriptValidated = false;

	public CollateralStructureDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "CollateralStructureDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that
	 * the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onCreate$window_CollateralStructureDialog(Event event)
			throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_CollateralStructureDialog);

		try {
			// Get the required arguments.
			this.collateralStructure = (CollateralStructure) arguments.get("collateralStructure");
			this.collateralStructureListCtrl = (CollateralStructureListCtrl) arguments.get("collateralStructureListCtrl");
			this.alwCopyOption = (Boolean) arguments.get("alwCopyOption");
			this.isCopyProcess = (Boolean) arguments.get("isCopyProcess");

			if (this.collateralStructure == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			CollateralStructure collateralStructure = new CollateralStructure();
			BeanUtils.copyProperties(this.collateralStructure,collateralStructure);
			this.collateralStructure.setBefImage(collateralStructure);

			// Render the page and display the data.
			doLoadWorkFlow(this.collateralStructure.isWorkflow(),
					this.collateralStructure.getWorkflowId(),this.collateralStructure.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(),this.pageRightName);
			}
			
			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.collateralStructure);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for append Extended field Details tab
	 */
	private void appendExtendedFieldsTab() {
		try {
			ExtendedFieldHeader extendedFieldHeader = collateralStructure.getExtendedFieldHeader();
			if (extendedFieldHeader == null) {
				extendedFieldHeader = new ExtendedFieldHeader();
				extendedFieldHeader.setNewRecord(true);
				collateralStructure.setExtendedFieldHeader(extendedFieldHeader);
			}
			extendedFieldHeader.setModuleName(CollateralConstants.MODULE_NAME);
			if (collateralStructure.isNew()) {
				extendedFieldHeader.setSubModuleName(collateralStructure.getCollateralType());
				extendedFieldHeader.setNumberOfColumns("2");
			}
			Map<String, Object> map = new HashMap<>();
			map.put("extendedFieldHeader", extendedFieldHeader);
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("firstTaskRole", StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole()));
			map.put("newRecord", collateralStructure.isNew());
			map.put("moduleName", CollateralConstants.MODULE_NAME);

			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul",
							extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");

		this.collateralType.setMaxlength(8);
		this.collateralDesc.setMaxlength(20);
		this.ltvPercentage.setMaxlength(6);
		this.ltvPercentage.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.ltvPercentage.setScale(2);
		this.remarks.setMaxlength(1000);
		setStatusDetails();

		logger.debug("Leaving");
	}

	/**
	 * Method for setting Basic Details on Selecting Extended Details Tab
	 * 
	 * @param event
	 */
	public void onSelect$extendedDetailsTab(Event event) {
		getExtendedFieldDialogCtrl().doSetBasicDetail(CollateralConstants.MODULE_NAME,
				this.collateralType.getValue(), this.collateralDesc.getValue());
	}

	/**
	 * Method for setting Basic Details on Selecting LTV Rule Details Tab
	 * 
	 * @param event
	 */
	public void onSelect$ltvRuleTab(Event event) {
		this.moduleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.subModuleDesc.setValue(this.collateralType.getValue());
		if (StringUtils.isNotEmpty(this.collateralDesc.getValue())) {
			this.subModuleDesc.setValue(this.collateralType.getValue() + " - "
					+ this.collateralDesc.getValue());
		}
	}

	/**
	 * Method for setting label details on Header in Selecting Tab
	 * @param event
	 */
	public void onSelect$preValidationTab(Event event) {
		logger.debug("Entering" + event.toString());
		
		this.preModuleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.preSubModuleDesc.setValue(this.collateralType.getValue());
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
		this.postSubModuleDesc.setValue(this.collateralType.getValue());
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
		Clients.clearWrongValue(this.preValidation);
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
		Clients.clearWrongValue(this.postValidation);
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
		Clients.clearWrongValue(this.postValidation);
	}
	/**
	 * 
	 * @param event
	 */
	public void onChange$preValidation(ForwardEvent event) {
		preScriptValidated = false;
		Clients.clearWrongValue(this.preValidation);
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
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_CollateralStructureDialog_btnNew"));
		this.btnDelete.setVisible(false);
		/*getUserWorkspace().isAllowed("button_CollateralStructureDialog_btnDelete")*/
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_CollateralStructureDialog_btnSave"));
		this.btnCancel.setVisible(false);

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
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
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
		}
		
		if(validationReq){
			if ((StringUtils.isNotBlank(this.preValidation.getValue()) || 
					StringUtils.isNotBlank(this.postValidation.getValue()))  && 
					validate(event, false, true)) {
				doSave();
			}else if(StringUtils.isBlank(this.preValidation.getValue()) ||
					StringUtils.isBlank(this.postValidation.getValue())){
				doSave();
			}
		}else{
			doSave();
		}
		*/ 
		// Pre Validation Checking for Validated or not
		if(this.preValidationReq.isChecked() && 
				StringUtils.isNotEmpty(this.preValidation.getValue().trim()) && !preScriptValidated){
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PreValidation") }));
			return;
		}

		// Post Validation Checking for Validated or not
		if(this.postValidationReq.isChecked() && 
				StringUtils.isNotEmpty(this.postValidation.getValue().trim()) && !postScriptValidated){
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PostValidation") }));
			return;
		}

		// Validation Details are correct and validated
		doSave();

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
			closeDialog();
			Events.postEvent("onClick$button_CollateralStructureList_NewCollateralStructure",
					collateralStructureListCtrl.window_CollateralStructureList, this.collateralStructure);
		}
		logger.debug("Leaving");
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
	 * The framework calls this event handler when user clicks the delete
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel
	 * button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
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
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");

		doWriteBeanToComponents(this.collateralStructure.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(CollateralStructure collateralStructure) {
		logger.debug("Entering");

		// Basic Details
		if (this.collateralStructure.isNewRecord()) {
			this.active.setChecked(true);
			String dftLtvType = CollateralConstants.FIXED_LTV;
			if(isCopyProcess){
				dftLtvType = collateralStructure.getLtvType();
			}
			fillComboBox(this.ltvType, dftLtvType, PennantStaticListUtil.getListLtvTypes(), "");
		} else {
			this.active.setChecked(collateralStructure.isActive());
			fillComboBox(this.ltvType, collateralStructure.getLtvType(), PennantStaticListUtil.getListLtvTypes(), "");
		}

		this.preValidation.setValue(collateralStructure.getPreValidation());
		this.postValidation.setValue(collateralStructure.getPostValidation());
		this.preValidationTab.setDisabled(!collateralStructure.isPreValidationReq());
		this.postValidationTab.setDisabled(!collateralStructure.isPostValidationReq());
		this.preValidationReq.setChecked(collateralStructure.isPreValidationReq());
		this.postValidationReq.setChecked(collateralStructure.isPostValidationReq());

		this.collateralType.setValue(collateralStructure.getCollateralType());
		this.collateralDesc.setValue(collateralStructure.getCollateralDesc());
		this.marketableSecurities.setChecked(collateralStructure.isMarketableSecurities());
		this.collateralLocReq.setChecked(collateralStructure.isCollateralLocReq());
		this.collateralValuatorReq.setChecked(collateralStructure.isCollateralValuatorReq());
		this.remarks.setValue(collateralStructure.getRemarks());
		setLtvType(getComboboxValue(this.ltvType), false);
		this.ltvPercentage.setValue(collateralStructure.getLtvPercentage());

		// LTVRule tab
		this.moduleDesc.setValue(CollateralConstants.MODULE_NAME);
		this.subModuleDesc.setValue(collateralStructure.getCollateralType());
		this.allowLtvWaiver.setChecked(collateralStructure.isAllowLtvWaiver());
		this.maxLtvWaiver.setValue(collateralStructure.getMaxLtvWaiver());

		this.javaScriptSqlRule.setModule(RuleConstants.MODULE_LTVRULE);
		this.javaScriptSqlRule.setEvent(RuleConstants.EVENT_LTVRULE);
		this.javaScriptSqlRule.setMode(RuleConstants.RULEMODE_SELECTFIELDLIST);
		this.javaScriptSqlRule.setRuleType(RuleReturnType.DECIMAL);
		if (collateralStructure.isNewRecord() && !isCopyProcess) {
			this.javaScriptSqlRule.setActualBlock("");
			this.javaScriptSqlRule.setEditable(true);
		} else {
			this.javaScriptSqlRule.setSqlQuery(collateralStructure.getSQLRule());
			this.javaScriptSqlRule.setFields(collateralStructure.getFields());
			this.javaScriptSqlRule.setActualBlock(collateralStructure.getActualBlock());
			if (StringUtils.isNotBlank(collateralStructure.getActualBlock())) {
				this.javaScriptSqlRule.buildQuery(collateralStructure.getActualBlock());
			}
		}
		
		// Default Values Setting for Script Validations
		postScriptValidated = true;
		preScriptValidated = true;
		
		// Extended Field Details tab
		appendExtendedFieldsTab();
		
		this.recordStatus.setValue(collateralStructure.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 * @throws Exception
	 */
	public void doWriteComponentsToBean(CollateralStructure collateralStructure, boolean validationReq) throws Exception {
		logger.debug("Entering");
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// BasicDeatils
		try {
			collateralStructure.setCollateralType(this.collateralType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			collateralStructure.setCollateralDesc(this.collateralDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			collateralStructure.setLtvType(getComboboxValue(this.ltvType));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			collateralStructure.setLtvPercentage(this.ltvPercentage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		collateralStructure.setMarketableSecurities(this.marketableSecurities.isChecked());
		collateralStructure.setActive(this.active.isChecked());
		collateralStructure.setPreValidationReq(this.preValidationReq.isChecked());
		collateralStructure.setPostValidationReq(this.postValidationReq.isChecked());
		collateralStructure.setCollateralLocReq(this.collateralLocReq.isChecked());
		collateralStructure.setCollateralValuatorReq(this.collateralValuatorReq.isChecked());
		try {
			collateralStructure.setRemarks(this.remarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, basicDetailsTab);
		
		// Extended Field Details
		if (getExtendedFieldDialogCtrl() != null) {
			ExtendedFieldHeader extendedFieldHeader = getExtendedFieldDialogCtrl().doSave_ExtendedFields(extendedDetailsTab);
			extendedFieldHeader.setModuleName(CollateralConstants.MODULE_NAME);
			extendedFieldHeader.setSubModuleName(collateralStructure.getCollateralType());
			extendedFieldHeader.setTabHeading(collateralStructure.getCollateralDesc());
			collateralStructure.setExtendedFieldHeader(extendedFieldHeader);
		}

		// LTV Rules
		if (StringUtils.equals(getComboboxValue(this.ltvType), CollateralConstants.VARIABLE_LTV)) {
			collateralStructure.setAllowLtvWaiver(this.allowLtvWaiver.isChecked());
			try {
				collateralStructure.setMaxLtvWaiver(this.maxLtvWaiver.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			if (!this.javaScriptSqlRule.isReadOnly()) {
				try {
					collateralStructure.setSQLRule(this.javaScriptSqlRule.getSqlQuery());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					collateralStructure.setActualBlock(this.javaScriptSqlRule.getActualBlock());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					collateralStructure.setFields(this.javaScriptSqlRule.getFields());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
			
		} else {
			collateralStructure.setAllowLtvWaiver(false);
			collateralStructure.setMaxLtvWaiver(BigDecimal.ZERO);
			collateralStructure.setSQLRule("");
			collateralStructure.setActualBlock("");
			collateralStructure.setFields("");
		}
		showErrorDetails(wve, ltvRuleTab);

		// Pre Valiadtion
		if (this.preValidationReq.isChecked()) {
			try {
				if (validationReq && StringUtils.trimToNull(this.preValidation.getValue()) == null) {
					throw new WrongValueException(preValidation, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CollateralStructureDialog_PreValidation.value") }));
				}
				collateralStructure.setPreValidation(this.preValidation.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			collateralStructure.setPreValidation("");
		}

		showErrorDetails(wve, preValidationTab);

		// Post Validation
		if (this.postValidationReq.isChecked()) {
			try {
				if (validationReq && StringUtils.trimToNull(this.postValidation.getValue()) == null) {
					throw new WrongValueException(postValidation, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CollateralStructureDialog_PostValidation.value") }));
				}
				collateralStructure.setPostValidation(this.postValidation.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			collateralStructure.setPostValidation("");
		}

		showErrorDetails(wve, postValidationTab);

		logger.debug("Leaving");
	}

	// For Tab Wise validations
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		doRemoveValidation();
		doRemoveLOVValidation();
		if (wve.size() > 0) {
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic
	 *            The entity that need to be render.
	 */
	public void doShowDialog(CollateralStructure collateralStructure) {
		logger.debug("Entering");

		// set ReadOnly mode accordingly if the object is new or not.
		if (collateralStructure.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.collateralType.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.collateralDesc.focus();
				if (StringUtils.isNotBlank(collateralStructure.getRecordType())) {
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

		this.btnDelete.setVisible(false);
		// fill the components with the data
		doWriteBeanToComponents(collateralStructure);
		
		int height = getContentAreaHeight() ;
		this.preValidationGrid.setHeight(height-150+"px");
		this.postValidationGrid.setHeight(height-150+"px");
		this.preValidation.setHeight(height-160+"px");
		this.postValidation.setHeight(height-160+"px");
		setDialog(DialogType.EMBEDDED);

		logger.debug("Leaving");

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");

		// Basic Details tab
		if (!this.collateralType.isReadonly()) {
			this.collateralType.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralStructureDialog_CollateralType.value"),
							PennantRegularExpressions.REGEX_UPP_BOX_ALPHA, true));
		}
		if (!this.collateralDesc.isReadonly()) {
			this.collateralDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralStructureDialog_CollateralDesc.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.ltvPercentage.isDisabled()) {
			if (StringUtils.equals(CollateralConstants.FIXED_LTV, getComboboxValue(this.ltvType))) {
				this.ltvPercentage.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralStructureDialog_LtvPercentage.value"),
								2, true, false, 100));
			}
		}

		// LTV Rules tab
		if (!this.ltvType.isDisabled()) {
			this.ltvType.setConstraint(new StaticListValidator(PennantStaticListUtil.getListLtvTypes(),
							Labels.getLabel("label_CollateralStructureDialog_LtvType.value")));
		}

		if (!this.remarks.isReadonly()) {
			this.remarks.setConstraint(new PTStringValidator(Labels.getLabel("label_CollateralStructureDialog_Remarks.value"),
					PennantRegularExpressions.REGEX_DESCRIPTION, false));
		}

		if (this.allowLtvWaiver.isChecked()) {
			this.maxLtvWaiver.setConstraint(new PTDecimalValidator(Labels.getLabel("label_CollateralStructureDialog_MaxLtvWaiver.value"),
							2, true, false, 100));
		}

		logger.debug("Leaving");
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		this.collateralType.setConstraint("");
		this.collateralDesc.setConstraint("");
		this.ltvType.setConstraint("");
		this.ltvPercentage.setConstraint("");
		this.remarks.setConstraint("");
		Clients.clearWrongValue(this.preValidation);
		Clients.clearWrongValue(this.postValidation);
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
	 * Clears validation error messages from all the fields of the dialog
	 * controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug("Entering");
		this.collateralType.setErrorMessage("");
		this.collateralDesc.setErrorMessage("");
		this.ltvType.setErrorMessage("");
		this.ltvPercentage.setErrorMessage("");
		this.remarks.setErrorMessage("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Changing LTV Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$ltvType(Event event) throws Exception {
		logger.debug("Entering");
		setLtvType(getComboboxValue(this.ltvType), true);
		logger.debug("Leaving");
	}

	private void setLtvType(String ltvType, boolean isAction) {
		this.ltvPercentage.setConstraint("");
		this.ltvPercentage.setErrorMessage("");
		this.ltvPercentage.setDisabled(true);
		this.ltvPercentage.setValue(BigDecimal.ZERO);
		this.space_LtvPercentage.setSclass("");
		this.ltvRuleTab.setDisabled(true);
		if (CollateralConstants.FIXED_LTV.equals(ltvType)) {
			this.ltvPercentage.setDisabled(isReadOnly("CollateralStructureDialog_LtvPercentage"));
			this.space_LtvPercentage.setSclass(PennantConstants.mandateSclass);
			this.allowLtvWaiver.setDisabled(true);
			this.maxLtvWaiver.setDisabled(true);
		} else if (CollateralConstants.VARIABLE_LTV.equals(ltvType)) {
			this.ltvRuleTab.setDisabled(false);
			this.allowLtvWaiver.setDisabled(isReadOnly("CollateralStructureDialog_AllowLtvWaiver"));
			if(isAction){
				this.maxLtvWaiver.setDisabled(true);
				this.maxLtvWaiver.setValue(BigDecimal.ZERO);
				this.javaScriptSqlRule.setEditable(true);
			}
		}
	}

	/**
	 * Method for Checking Waiver allowed for LTV
	 * 
	 * @param event
	 */
	public void onCheck$allowLtvWaiver(Event event) {
		logger.debug("Entering");
		setLTVWaiver();
		logger.debug("Leaving");
	}

	private void setLTVWaiver() {
		if (this.allowLtvWaiver.isChecked()) {
			this.maxLtvWaiver.setDisabled(isReadOnly("CollateralStructureDialog_MaxLtvWaiver"));
			this.space_MaxLtvWaiver.setSclass(PennantConstants.mandateSclass);
		} else {
			this.maxLtvWaiver.setDisabled(true);
			this.space_MaxLtvWaiver.setSclass(PennantConstants.mandateSclass);
			this.maxLtvWaiver.setValue(BigDecimal.ZERO);
		}
	}

	/**
	 * Method for Checking postValidationReq ot not
	 * 
	 * @param event
	 */
	public void onCheck$postValidationReq(Event event) {
		logger.debug("Entering");
		this.postValidationTab.setDisabled(!postValidationReq.isChecked());
		logger.debug("Leaving");
	}

	/**
	 * Method for Checking preValidationReq ot not
	 * 
	 * @param event
	 */
	public void onCheck$preValidationReq(Event event) {
		logger.debug("Entering");
		this.preValidationTab.setDisabled(!preValidationReq.isChecked());
		logger.debug("Leaving");
	}

	// *************************************************************
	// ********************** CRUD operations **********************
	// *************************************************************

	/**
	 * Deletes a Academic entity from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() {
		logger.debug("Entering");

		final CollateralStructure collateralStructure = new CollateralStructure();
		BeanUtils.copyProperties(this.collateralStructure, collateralStructure);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels
				.getLabel("message.Question.Are_you_sure_to_delete_this_record")
				+ "\n\n --> "
				+ Labels.getLabel("label_AcademicDialog_AcademicLevel.value")
				+ " : " + collateralStructure.getCollateralType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(collateralStructure.getRecordType())) {
				collateralStructure.setVersion(collateralStructure.getVersion() + 1);
				collateralStructure.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					collateralStructure.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(collateralStructure, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving");
	}

	@Override
	public void closeDialog() {
		super.closeDialog();

		if (getExtendedFieldDialogCtrl() != null) {
			getExtendedFieldDialogCtrl().closeDialog();
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		if (this.collateralStructure.isNewRecord()) {
			this.collateralType.setReadonly(false);
			this.btnCancel.setVisible(false);
			this.active.setDisabled(true);
			this.btnCopyTo.setVisible(false);
		} else {
			this.collateralType.setReadonly(true);
			this.btnCancel.setVisible(true);
			if (StringUtils.equals(collateralStructure.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				this.active.setDisabled(true);
			} else {
				this.active.setDisabled(isReadOnly("CollateralStructureDialog_Active"));
			}
			this.btnCopyTo.setVisible(isMaintainable() && alwCopyOption);
		}

		this.collateralDesc.setReadonly(isReadOnly("CollateralStructureDialog_CollateralDesc"));
		this.ltvType.setDisabled(isReadOnly("CollateralStructureDialog_LtvType"));
		this.ltvPercentage.setDisabled(isReadOnly("CollateralStructureDialog_LtvPercentage"));
		this.marketableSecurities.setDisabled(isReadOnly("CollateralStructureDialog_MarketableSecurities"));
		this.preValidationReq.setDisabled(isReadOnly("CollateralStructureDialog_PreValidationReq"));
		this.postValidationReq.setDisabled(isReadOnly("CollateralStructureDialog_PostValidationReq"));
		this.collateralLocReq.setDisabled(isReadOnly("CollateralStructureDialog_CollateralLocReq"));
		this.collateralValuatorReq.setDisabled(isReadOnly("CollateralStructureDialog_CollateralValuatorReq"));
		this.remarks.setReadonly(isReadOnly("CollateralStructureDialog_Remarks"));
		this.allowLtvWaiver.setDisabled(isReadOnly("CollateralStructureDialog_AllowLtvWaiver"));
		this.maxLtvWaiver.setDisabled(isReadOnly("CollateralStructureDialog_MaxLtvWaiver"));
		this.javaScriptSqlRule.setTreeTabVisible(!isReadOnly("CollateralStructureDialog_JavaScriptSqlRule"));
		this.preValidation.setReadonly(isReadOnly("CollateralStructureDialog_PreValidation"));
		this.postValidation.setReadonly(isReadOnly("CollateralStructureDialog_PostValidation"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.collateralStructure.isNewRecord()) {
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
		this.collateralType.setReadonly(true);
		this.collateralDesc.setReadonly(true);
		this.ltvType.setDisabled(true);
		this.ltvPercentage.setDisabled(true);
		this.marketableSecurities.setDisabled(true);
		this.active.setDisabled(true);
		this.preValidationReq.setDisabled(true);
		this.postValidationReq.setDisabled(true);
		this.collateralLocReq.setDisabled(true);
		this.collateralValuatorReq.setDisabled(true);
		this.remarks.setReadonly(true);
		this.allowLtvWaiver.setDisabled(true);
		this.maxLtvWaiver.setDisabled(true);

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
	 * Method for Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		final CollateralStructure aCollateralStructure = new CollateralStructure();
		BeanUtils.copyProperties(this.collateralStructure, aCollateralStructure);
		boolean isNew = false;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		boolean validationReq = true;
		if (this.userAction.getSelectedItem() != null){
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) ||
					this.userAction.getSelectedItem().getLabel().contains("Reject") ||
					this.userAction.getSelectedItem().getLabel().contains("Resubmit") ||
					this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				validationReq = false;
			}
		}
		
		// Validation Not required Cases excluding
		if(validationReq){
			doClearMessage();
			doSetValidation();
		}
		
		// fill the Academic object with the components data
		doWriteComponentsToBean(aCollateralStructure, validationReq);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = aCollateralStructure.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aCollateralStructure.getRecordType())) {
				aCollateralStructure.setVersion(aCollateralStructure.getVersion() + 1);
				if (isNew) {
					aCollateralStructure.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aCollateralStructure.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aCollateralStructure.setNewRecord(true);
				}
			}
		} else {
			aCollateralStructure.setVersion(aCollateralStructure.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aCollateralStructure, tranType)) {
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
	 * @param aAcademic
	 *            (Academic)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doProcess(CollateralStructure aCollateralStructure, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aCollateralStructure.setLastMntBy(getUserWorkspace().getLoggedInUser().getLoginUsrID());
		aCollateralStructure.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aCollateralStructure.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aCollateralStructure.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aCollateralStructure.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aCollateralStructure);
				}

				if (isNotesMandatory(taskId, aCollateralStructure)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}
			if (!StringUtils.isBlank(nextTaskId)) {
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

			aCollateralStructure.setTaskId(taskId);
			aCollateralStructure.setNextTaskId(nextTaskId);
			aCollateralStructure.setRoleCode(getRole());
			aCollateralStructure.setNextRoleCode(nextRoleCode);

			// Set workflow values
			ExtendedFieldHeader extFldHeader = aCollateralStructure.getExtendedFieldHeader();
			extFldHeader.setWorkflowId(aCollateralStructure.getWorkflowId());
			extFldHeader.setRecordStatus(aCollateralStructure.getRecordStatus());
			extFldHeader.setTaskId(aCollateralStructure.getTaskId());
			extFldHeader.setNextTaskId(aCollateralStructure.getNextTaskId());
			extFldHeader.setRoleCode(aCollateralStructure.getRoleCode());
			extFldHeader.setNextRoleCode(aCollateralStructure.getNextRoleCode());
			if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralStructure.getRecordType())) {
				if (StringUtils.trimToNull(extFldHeader.getRecordType()) == null) {
					extFldHeader.setRecordType(aCollateralStructure.getRecordType());
					extFldHeader.setNewRecord(true);
				}
			} else if (PennantConstants.RECORD_TYPE_UPD.equals(aCollateralStructure.getRecordType())) {
				extFldHeader.setRecordType(aCollateralStructure.getRecordType());
				extFldHeader.setNewRecord(aCollateralStructure.isNewRecord());
			}
		
			for (ExtendedFieldDetail ext : extFldHeader.getExtendedFieldDetails()) {
				ext.setWorkflowId(aCollateralStructure.getWorkflowId());
				ext.setRecordStatus(aCollateralStructure.getRecordStatus());
				ext.setTaskId(aCollateralStructure.getTaskId());
				ext.setNextTaskId(aCollateralStructure.getNextTaskId());
				ext.setRoleCode(aCollateralStructure.getRoleCode());
				ext.setNextRoleCode(aCollateralStructure.getNextRoleCode());
				if (PennantConstants.RECORD_TYPE_DEL.equals(aCollateralStructure.getRecordType())) {
					if (StringUtils.trimToNull(ext.getRecordType()) == null) {
						ext.setRecordType(aCollateralStructure.getRecordType());
						ext.setNewRecord(true);
					}
				}
			}

			auditHeader = getAuditHeader(aCollateralStructure, tranType);
			String operationRefs = getServiceOperations(taskId, aCollateralStructure);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aCollateralStructure, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aCollateralStructure, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
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
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		CollateralStructure aCollateralStructure = (CollateralStructure) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = collateralStructureService.delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = collateralStructureService.saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = collateralStructureService.doApprove(auditHeader);

						if (aCollateralStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = collateralStructureService.doReject(auditHeader);

						if (aCollateralStructure.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"),null));
						retValue = ErrorControl.showErrorControl(this.window_CollateralStructureDialog,auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_CollateralStructureDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.collateralStructure), true);
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
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(CollateralStructure aCollateralStructure, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1,aCollateralStructure.getBefImage(), aCollateralStructure);
		return new AuditHeader(getReference(), null, null, null, auditDetail,aCollateralStructure.getUserDetails(), getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.collateralStructure);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		collateralStructureListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.collateralStructure.getCollateralType());
	}

	public void setCollateralStructureService(
			CollateralStructureService collateralStructureService) {
		this.collateralStructureService = collateralStructureService;
	}

	public void setExtendedFieldDialogCtrl(
			ExtendedFieldDialogCtrl extendedFieldDialogCtrl) {
		this.extendedFieldDialogCtrl = extendedFieldDialogCtrl;
	}

	public ExtendedFieldDialogCtrl getExtendedFieldDialogCtrl() {
		return this.extendedFieldDialogCtrl;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}
	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}
	
}
