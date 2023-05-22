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
 * * FileName : ExtFieldConfigDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-11-2016 * *
 * Modified Date : 29-11-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-11-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.solutionfactory.extendedfielddetail;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.LengthConstants;
import com.pennant.backend.model.FinServicingEvent;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.service.staticparms.ExtFieldConfigService;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

public class ExtFieldConfigDialogCtrl extends GFCBaseCtrl<ExtendedFieldHeader> implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ExtFieldConfigDialogCtrl.class);

	protected Window window_ExtFieldConfigDialog;
	protected Combobox module;
	protected Combobox subModule;
	protected ExtendedCombobox product;
	protected Checkbox preValidationReq;
	protected Checkbox postValidationReq;
	protected Space space_subModule;
	protected Label label_SubModule;
	protected Textbox tabHeading;

	protected Label label_Event;
	protected Combobox finEvent;
	protected Space space_event;

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
	protected Tab preValidationTab;
	protected Tab postValidationTab;
	protected Tabpanel extendedFieldTabpanel;

	private ExtendedFieldHeader extendedFieldHeader;
	private transient ExtFieldConfigListCtrl configListCtrl;
	private transient ExtFieldConfigService extFieldConfigService;
	private transient ExtendedFieldDialogCtrl extendedFieldDialogCtrl;
	private JSONArray variables = new JSONArray();
	private List<String> fieldNames = new ArrayList<String>();
	protected boolean preScriptValidated = false;
	protected boolean postScriptValidated = false;

	private List<ValueLabel> configList = PennantStaticListUtil.getConfigTypes();
	private List<ValueLabel> custCtgList = PennantAppUtil.getcustCtgCodeList();
	private List<FinServicingEvent> events = PennantStaticListUtil.getFinEvents(true);
	private List<ValueLabel> verificatinList = PennantStaticListUtil.getVerificatinTypes();
	private List<ValueLabel> orgnizationList = PennantStaticListUtil.getOrganizationTypes();

	private String subModuleVal;

	public ExtFieldConfigDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "ExtendedFieldConfigDialog";
	}

	/**
	 * 
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ExtFieldConfigDialog(Event event) {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_ExtFieldConfigDialog);

		try {
			// Get the required arguments.
			this.extendedFieldHeader = (ExtendedFieldHeader) arguments.get("ExtendedFieldHeader");
			this.configListCtrl = (ExtFieldConfigListCtrl) arguments.get("ConfigListCtrl");

			if (this.extendedFieldHeader == null) {
				throw new Exception(Labels.getLabel("error.unhandled"));
			}

			// Store the before image.
			ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
			BeanUtils.copyProperties(this.extendedFieldHeader, extendedFieldHeader);
			this.extendedFieldHeader.setBefImage(extendedFieldHeader);

			// Render the page and display the data.
			doLoadWorkFlow(this.extendedFieldHeader.isWorkflow(), this.extendedFieldHeader.getWorkflowId(),
					this.extendedFieldHeader.getNextTaskId());

			if (isWorkFlowEnabled() && !enqiryModule) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), this.pageRightName);
			}

			doSetFieldProperties();
			doCheckRights();
			doShowDialog(this.extendedFieldHeader);
		} catch (Exception e) {
			closeDialog();
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for append Extended field Details tab
	 */
	private void appendExtendedFieldsTab() {
		try {
			if (this.extendedFieldHeader == null) {
				extendedFieldHeader = new ExtendedFieldHeader();
				extendedFieldHeader.setNewRecord(true);
			}
			extendedFieldHeader.setModuleName(this.module.getSelectedItem().getValue());
			if (extendedFieldHeader.isNewRecord()) {
				extendedFieldHeader.setSubModuleName(extendedFieldHeader.getSubModuleName());
				extendedFieldHeader.setNumberOfColumns("2");
			}
			Map<String, Object> map = new HashMap<>();
			map.put("extendedFieldHeader", extendedFieldHeader);
			map.put("roleCode", getRole());
			map.put("dialogCtrl", this);
			map.put("firstTaskRole", StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole()));
			map.put("newRecord", extendedFieldHeader.isNewRecord());
			map.put("moduleName", this.module.getSelectedItem().getValue());

			Executions.createComponents("/WEB-INF/pages/SolutionFactory/ExtendedFieldDetail/ExtendedFieldDialog.zul",
					extendedFieldTabpanel, map);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		fillComboBox(module, "", configList, "");
		fillComboBox(subModule, "", custCtgList, "");

		// Finance Type
		this.product.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.product.setMandatoryStyle(true);
		this.product.setModuleName("Product");
		this.product.setValueColumn("ProductCode");
		this.product.setDescColumn("ProductDesc");
		this.product.setValidateColumns(new String[] { "ProductCode" });

		this.tabHeading.setMaxlength(20);

		setStatusDetails();

		logger.debug(Literal.LEAVING);
	}

	public void onChange$module(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		String moduleVal = this.module.getSelectedItem().getValue().toString();
		this.tabHeading.setValue("");
		visibleComponent(moduleVal, "");
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void visibleComponent(String moduleVal, String subModuleVal) {
		if (moduleVal.equals(ExtendedFieldConstants.MODULE_CUSTOMER)) {
			this.subModule.setVisible(true);
			this.space_subModule.setVisible(true);
			this.label_SubModule.setVisible(true);
			this.product.setVisible(false);
			this.product.setValue(null);
			this.label_Event.setVisible(false);
			this.space_event.setVisible(false);
			this.finEvent.setVisible(false);
			fillComboBox(subModule, subModuleVal, custCtgList, "");
		} else if (moduleVal.equals(ExtendedFieldConstants.MODULE_LOAN)) {
			this.subModule.setVisible(false);
			this.space_subModule.setVisible(false);
			this.label_Event.setVisible(true);
			this.space_event.setVisible(true);
			this.finEvent.setVisible(true);
			this.label_SubModule.setVisible(true);
			this.product.setVisible(true);
			this.product.setValue(subModuleVal);
			List<String> excludeValue = new ArrayList<>();

			excludeValue.add(FinServiceEvent.SUSPHEAD);
			excludeValue.add(FinServiceEvent.COVENANTS);
			excludeValue.add(FinServiceEvent.CHANGETDS);
			excludeValue.add(FinServiceEvent.PROVISION);
			excludeValue.add(FinServiceEvent.CANCELFIN);
			excludeValue.add(FinServiceEvent.CANCELDISB);
			excludeValue.add(FinServiceEvent.NOCISSUANCE);
			excludeValue.add(FinServiceEvent.OVERDRAFTSCHD);
			excludeValue.add(FinServiceEvent.CHGSCHDMETHOD);

			fillComboBox(finEvent, extendedFieldHeader.getEvent(), PennantStaticListUtil.getValueLabels(events),
					excludeValue);
		} else if (moduleVal.equals(ExtendedFieldConstants.MODULE_VERIFICATION)) {
			this.subModule.setVisible(true);
			this.space_subModule.setVisible(true);
			this.label_SubModule.setVisible(true);
			this.product.setVisible(false);
			this.product.setValue(null);
			this.label_Event.setVisible(false);
			this.space_event.setVisible(false);
			this.finEvent.setVisible(false);
			fillComboBox(subModule, subModuleVal, verificatinList, "");
		} else if (moduleVal.equals(ExtendedFieldConstants.MODULE_ORGANIZATION)) {
			this.subModule.setVisible(true);
			this.space_subModule.setVisible(true);
			this.label_SubModule.setVisible(true);
			this.product.setVisible(false);
			this.product.setValue(null);
			this.label_Event.setVisible(false);
			this.space_event.setVisible(false);
			this.finEvent.setVisible(false);
			fillComboBox(subModule, subModuleVal, orgnizationList, "");
		} else {
			this.subModule.setVisible(false);
			this.space_subModule.setVisible(false);
			this.label_SubModule.setVisible(false);
			this.product.setVisible(false);
			this.product.setValue(null);
			fillComboBox(subModule, "", custCtgList, "");
		}
	}

	/**
	 * Method for setting Basic Details on Selecting Extended Details Tab
	 * 
	 * @param event
	 */
	public void onSelect$extendedDetailsTab(Event event) {
		subModuleVal = this.subModule.getSelectedItem().getLabel();
		if (subModuleVal.equals(Labels.getLabel("Combo.Select"))) {
			subModuleVal = this.product.getValue();
		}
		getExtendedFieldDialogCtrl().doSetBasicDetail(this.module.getSelectedItem().getValue(), subModuleVal, null);
	}

	/**
	 * Method for setting Basic Details on Selecting LTV Rule Details Tab
	 * 
	 * @param event
	 */
	public void onSelect$ltvRuleTab(Event event) {
		subModuleVal = this.subModule.getSelectedItem().getLabel();
		if (subModuleVal.equals(Labels.getLabel("Combo.Select"))) {
			subModuleVal = this.product.getValue();
		}
		this.moduleDesc.setValue(this.module.getSelectedItem().getValue());
		this.subModuleDesc.setValue(this.subModule.getSelectedItem().getValue());
		if (StringUtils.isNotEmpty(this.subModule.getSelectedItem().getValue())) {
			this.subModuleDesc.setValue(this.module.getSelectedItem().getValue() + " - " + subModuleVal);
		}
	}

	/**
	 * Method for setting label details on Header in Selecting Tab
	 * 
	 * @param event
	 */
	public void onSelect$preValidationTab(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		subModuleVal = this.subModule.getSelectedItem().getLabel();
		if (subModuleVal.equals(Labels.getLabel("Combo.Select"))) {
			subModuleVal = this.product.getValue();
		}

		this.preModuleDesc.setValue(this.module.getSelectedItem().getValue());
		this.preSubModuleDesc.setValue(subModuleVal);
		this.prevalidationListbox.getItems().clear();
		renderScriptFields(prevalidationListbox);

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for setting label details on Header in Selecting Tab
	 * 
	 * @param event
	 */
	public void onSelect$postValidationTab(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		subModuleVal = this.subModule.getSelectedItem().getLabel();
		if (subModuleVal.equals(Labels.getLabel("Combo.Select"))) {
			subModuleVal = this.product.getValue();
		}

		this.postModuleDesc.setValue(this.module.getSelectedItem().getValue());
		this.postSubModuleDesc.setValue(subModuleVal);
		this.postValidationListbox.getItems().clear();
		renderScriptFields(postValidationListbox);

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for rendering Field Details from Extended fields for Validations & Simulation
	 * 
	 * @param listbox
	 */
	private void renderScriptFields(Listbox listbox) {
		logger.debug(Literal.ENTERING);

		if (getExtendedFieldDialogCtrl() != null) {
			List<ExtendedFieldDetail> extFieldList = getExtendedFieldDialogCtrl().getExtendedFieldDetailsList();
			if (extFieldList != null && !extFieldList.isEmpty()) {
				for (ExtendedFieldDetail details : extFieldList) {
					if (!StringUtils.equals(details.getRecordType(), PennantConstants.RECORD_TYPE_DEL)
							&& !StringUtils.equals(details.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
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

		logger.debug(Literal.LEAVING);
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPreValidate(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.preValidation);
		if (validate(event, false, false)) {
			preScriptValidated = true;
			if (StringUtils.isNotEmpty(this.preValidation.getValue().trim())) {
				MessageUtil.confirm("NO Errors Found! Proceed With Simulation?", evnt -> {
					if (Messagebox.ON_YES.equals(evnt.getName())) {
						createSimulationWindow(variables, this.preValidation.getValue());
					}
				});
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS AND CONFIRM EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPostValidate(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.postValidation);
		if (validate(event, true, false)) {
			postScriptValidated = true;
			if (StringUtils.isNotEmpty(this.postValidation.getValue().trim())) {
				MessageUtil.confirm("NO Errors Found! Proceed With Simulation?", evnt -> {
					if (Messagebox.ON_YES.equals(evnt.getName())) {
						createSimulationWindow(variables, this.postValidation.getValue());
					}
				});
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
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
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("variables", jsonArray);
		map.put("scriptRule", scriptRule);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Collateral/CollateralStructure/ScriptValidationResult.zul",
					null, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND RETURNS THE ERRORS
	 * 
	 * @param event
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validate(ForwardEvent event, boolean isPostValidation, boolean bothValidations)
			throws InterruptedException {
		boolean noerrors = true;
		// object containing errors and variables
		Object[] data = (Object[]) event.getOrigin().getData();
		// array of errors
		if (data != null && data.length != 0) {
			JSONArray errors = (JSONArray) data[0];
			// array of variables
			variables = (JSONArray) data[1];

			if (!isPostValidation) {
				return noerrors;
			}
			// if no errors
			if (variables != null && errors.size() == 0) {
				// check for new declared variables
				for (int i = 0; i < variables.size(); i++) {
					JSONObject variable = (JSONObject) variables.get(i);
					if (variable.get("name").equals("cd")) {
						continue;
					}
					if (isPostValidation) {
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
					} else {
						if (!"defaults".equals(variable.get("name"))) {
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
	 * 
	 * @param isPostValidation
	 * @return
	 * @throws InterruptedException
	 */
	private boolean validateResult(boolean isPostValidation, boolean bothValidations) throws InterruptedException {

		if (!bothValidations) {
			if (isPostValidation) {
				if (!this.postValidation.getValue().contains("errors")) {
					MessageUtil.showError("Error Details not found ");
					return false;
				}
			} else {
				if (!this.preValidation.getValue().contains("defaults")) {
					MessageUtil.showError("Error Details not found ");
					return false;
				}
			}
		} else {
			if (StringUtils.isNotEmpty(this.preValidation.getValue())
					&& !this.preValidation.getValue().contains("defaults")) {
				MessageUtil.showError("Error Details not found in Pre Validations.");
				return false;
			} else if (StringUtils.isNotEmpty(this.postValidation.getValue())
					&& !this.postValidation.getValue().contains("errors")) {
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
		logger.debug(Literal.ENTERING + event.toString());
		if (validate(event, false, false)) {
			// create a new window for input values
			createSimulationWindow(variables, this.preValidation.getValue());
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * VALIDATES THE SCRIPT CODE AND EXECUTE
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$btnPostSimulate(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		if (validate(event, true, false)) {
			// create a new window for input values
			createSimulationWindow(variables, this.postValidation.getValue());
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set Visible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);

		getUserWorkspace().allocateAuthorities(this.pageRightName, getRole());
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldConfigDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldConfigDialog_btnEdit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_ExtendedFieldConfigDialog_btnSave"));
		this.btnDelete.setVisible(false);
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the save button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnSave(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		// Pre Validation Checking for Validated or not
		if (this.preValidationReq.isChecked() && StringUtils.isNotEmpty(this.preValidation.getValue().trim())
				&& !preScriptValidated) {
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PreValidation") }));
			return;
		}

		// Post Validation Checking for Validated or not
		if (this.postValidationReq.isChecked() && StringUtils.isNotEmpty(this.postValidation.getValue().trim())
				&& !postScriptValidated) {
			MessageUtil.showError(Labels.getLabel("label_PrePostValidation_ValidationCheck",
					new String[] { Labels.getLabel("Tab_PostValidation") }));
			return;
		}

		// Validation Details are correct and validated
		doSave();

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The framework calls this event handler when user clicks the edit button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnEdit(Event event) {
		doEdit();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnHelp(Event event) {
		MessageUtil.showHelpWindow(event, super.window);
	}

	/**
	 * The framework calls this event handler when user clicks the delete button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnDelete(Event event) {
		doDelete();
	}

	/**
	 * The framework calls this event handler when user clicks the cancel button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnCancel(Event event) {
		doCancel();
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
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
		logger.debug(Literal.ENTERING);

		doWriteBeanToComponents(this.extendedFieldHeader.getBefImage());
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnCancel.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param academic
	 * 
	 */
	public void doWriteBeanToComponents(ExtendedFieldHeader extendedFieldHeader) {
		logger.debug(Literal.ENTERING);

		// Basic Details
		this.preValidation.setValue(extendedFieldHeader.getPreValidation());
		this.postValidation.setValue(extendedFieldHeader.getPostValidation());
		this.preValidationTab.setDisabled(!extendedFieldHeader.isPreValidationReq());
		this.postValidationTab.setDisabled(!extendedFieldHeader.isPostValidationReq());
		this.preValidationReq.setChecked(extendedFieldHeader.isPreValidationReq());
		this.postValidationReq.setChecked(extendedFieldHeader.isPostValidationReq());
		this.tabHeading.setValue(extendedFieldHeader.getTabHeading());

		if (extendedFieldHeader.isNewRecord()) {
			visibleComponent(ExtendedFieldConstants.MODULE_CUSTOMER, "");
			fillComboBox(module, ExtendedFieldConstants.MODULE_CUSTOMER, configList, "");
		} else {
			visibleComponent(extendedFieldHeader.getModuleName(), extendedFieldHeader.getSubModuleName());
			fillComboBox(module, extendedFieldHeader.getModuleName(), configList, "");
		}

		// Default Values Setting for Script Validations
		postScriptValidated = true;
		preScriptValidated = true;

		// Extended Field Details tab
		appendExtendedFieldsTab();

		this.recordStatus.setValue(extendedFieldHeader.getRecordStatus());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aAcademic
	 */
	public void doWriteComponentsToBean(ExtendedFieldHeader extendedFieldHeader, boolean validationReq) {
		logger.debug(Literal.ENTERING);
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		// BasicDeatils
		try {
			extendedFieldHeader.setModuleName(this.module.getSelectedItem().getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.subModule.isVisible()) {
				extendedFieldHeader.setSubModuleName(this.subModule.getSelectedItem().getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.product.isVisible()) {
			try {
				if (StringUtils.trimToNull(this.product.getValue()) == null) {
					throw new WrongValueException(this.product, Labels.getLabel("CHECK_NO_EMPTY",
							new String[] { Labels.getLabel("label_SelectFinanceTypeDialog_FinType.value") }));
				}
				extendedFieldHeader.setSubModuleName(this.product.getValue());
			} catch (WrongValueException e) {
				wve.add(e);
			}
		}

		try {

			if (!this.finEvent.isDisabled() && this.finEvent.isVisible() && (this.finEvent.getSelectedItem() == null
					|| this.finEvent.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select))) {
				throw new WrongValueException(this.finEvent, Labels.getLabel("FIELD_IS_MAND",
						new String[] { Labels.getLabel("label_FinanceWorkFlowDialog_FinEvent.value") }));
			}

			if (StringUtils.equals(extendedFieldHeader.getModuleName(), ExtendedFieldConstants.MODULE_LOAN)) {
				extendedFieldHeader.setEvent(getComboboxValue(this.finEvent));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			extendedFieldHeader.setTabHeading(this.tabHeading.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		extendedFieldHeader.setPreValidationReq(this.preValidationReq.isChecked());
		extendedFieldHeader.setPostValidationReq(this.postValidationReq.isChecked());

		showErrorDetails(wve, basicDetailsTab);

		// Extended Field Details
		if (getExtendedFieldDialogCtrl() != null) {
			ExtendedFieldHeader extendedFieldHeader1 = getExtendedFieldDialogCtrl()
					.doSave_ExtendedFields(extendedDetailsTab);
			extendedFieldHeader.setNumberOfColumns(extendedFieldHeader1.getNumberOfColumns());
			extendedFieldHeader.setExtendedFieldDetails(extendedFieldHeader1.getExtendedFieldDetails());
		}

		// Pre Valiadtion
		if (this.preValidationReq.isChecked()) {
			try {
				if (validationReq && StringUtils.trimToNull(this.preValidation.getValue()) == null) {
					throw new WrongValueException(preValidation, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CollateralStructureDialog_PreValidation.value") }));
				}
				extendedFieldHeader.setPreValidation(this.preValidation.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			extendedFieldHeader.setPreValidation("");
		}

		showErrorDetails(wve, preValidationTab);

		// Post Validation
		if (this.postValidationReq.isChecked()) {
			try {
				if (validationReq && StringUtils.trimToNull(this.postValidation.getValue()) == null) {
					throw new WrongValueException(postValidation, Labels.getLabel("FIELD_IS_MAND",
							new String[] { Labels.getLabel("label_CollateralStructureDialog_PostValidation.value") }));
				}
				extendedFieldHeader.setPostValidation(this.postValidation.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			extendedFieldHeader.setPostValidation("");
		}

		showErrorDetails(wve, postValidationTab);

		logger.debug(Literal.LEAVING);
	}

	// For Tab Wise validations
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);
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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page.
	 * 
	 * @param aAcademic The entity that need to be render.
	 */
	public void doShowDialog(ExtendedFieldHeader extendedFieldHeader) {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (extendedFieldHeader.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
			this.module.focus();
		} else {
			if (isWorkFlowEnabled()) {
				this.subModule.focus();
				if (StringUtils.isNotBlank(extendedFieldHeader.getRecordType())) {
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
		doWriteBeanToComponents(extendedFieldHeader);

		int height = getContentAreaHeight();
		this.preValidationGrid.setHeight(height - 150 + "px");
		this.postValidationGrid.setHeight(height - 150 + "px");
		this.preValidation.setHeight(height - 160 + "px");
		this.postValidation.setHeight(height - 160 + "px");
		setDialog(DialogType.EMBEDDED);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);

		String module = this.module.getSelectedItem().getValue();

		if (!this.module.isDisabled()) {
			this.module.setConstraint(
					new PTListValidator<ValueLabel>(Labels.getLabel("label_ExtendedFieldConfig_Module.value"), configList, true));
		}

		if (!this.subModule.isDisabled() && this.subModule.isVisible()) {
			if (ExtendedFieldConstants.MODULE_VERIFICATION.equals(module)) {
				this.subModule.setConstraint(new PTListValidator<ValueLabel>(
						Labels.getLabel("label_ExtendedFieldConfig_SubModule.value"), verificatinList, true));
			} else if (ExtendedFieldConstants.MODULE_CUSTOMER.equals(module)) {
				this.subModule.setConstraint(new PTListValidator<ValueLabel>(
						Labels.getLabel("label_ExtendedFieldConfig_SubModule.value"), custCtgList, true));
			}
		}

		if (!this.tabHeading.isDisabled()) {
			this.tabHeading
					.setConstraint(new PTStringValidator(Labels.getLabel("label_ExtendedFieldConfig_TabHeading.value"),
							PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}

		if (!this.finEvent.isDisabled() && this.finEvent.isVisible()) {
			this.finEvent.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceWorkFlowDialog_FinEvent.value"), null, true));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Remove the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		this.module.setConstraint("");
		this.tabHeading.setConstraint("");
		this.subModule.setConstraint("");
		this.product.setConstraint("");
		Clients.clearWrongValue(this.preValidation);
		Clients.clearWrongValue(this.postValidation);
		logger.debug(Literal.LEAVING);
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
	 * Clears validation error messages from all the fields of the dialog controller.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		this.module.setErrorMessage("");
		this.tabHeading.setErrorMessage("");
		this.subModule.setErrorMessage("");
		this.product.setErrorMessage("");
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Checking postValidationReq ot not
	 * 
	 * @param event
	 */
	public void onCheck$postValidationReq(Event event) {
		logger.debug(Literal.ENTERING);
		this.postValidationTab.setDisabled(!postValidationReq.isChecked());
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Checking preValidationReq ot not
	 * 
	 * @param event
	 */
	public void onCheck$preValidationReq(Event event) {
		logger.debug(Literal.ENTERING);
		this.preValidationTab.setDisabled(!preValidationReq.isChecked());
		logger.debug(Literal.LEAVING);
	}

	private void doDelete() {
		logger.debug(Literal.ENTERING);

		final ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(this.extendedFieldHeader, extendedFieldHeader);

		String keyReference = Labels.getLabel("label_AcademicDialog_AcademicLevel.value") + " : "
				+ extendedFieldHeader.getSubModuleName();

		doDelete(keyReference, extendedFieldHeader);

		logger.debug(Literal.LEAVING);
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
		logger.debug(Literal.ENTERING);

		if (this.extendedFieldHeader.isNewRecord()) {
			this.module.setDisabled(false);
			this.subModule.setDisabled(false);
			this.product.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.module.setDisabled(true);
			this.subModule.setDisabled(true);
			this.finEvent.setDisabled(true);
			this.product.setReadonly(true);
			this.btnCancel.setVisible(true);
		}

		this.tabHeading.setReadonly(isReadOnly("ExtendedFieldConfigDialog_tabHeading"));
		this.preValidationReq.setDisabled(isReadOnly("ExtendedFieldConfigDialog_preValidationReq"));
		this.postValidationReq.setDisabled(isReadOnly("ExtendedFieldConfigDialog_postValidationReq"));
		this.preValidation.setReadonly(isReadOnly("ExtendedFieldConfigDialog_preValidation"));
		this.postValidation.setReadonly(isReadOnly("ExtendedFieldConfigDialog_postValidation"));

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
			if (this.extendedFieldHeader.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug(Literal.ENTERING);
		this.module.setReadonly(true);
		this.tabHeading.setReadonly(true);
		this.subModule.setReadonly(true);
		this.product.setReadonly(true);
		this.preValidationReq.setDisabled(true);
		this.postValidationReq.setDisabled(true);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}
		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Saves the components to table.
	 */
	public void doSave() {
		logger.debug(Literal.ENTERING);

		final ExtendedFieldHeader extendedFieldHeader = new ExtendedFieldHeader();
		BeanUtils.copyProperties(this.extendedFieldHeader, extendedFieldHeader);
		boolean isNew = false;

		// ************************************************************
		// force validation, if on, than execute by component.getValue()
		// ************************************************************
		boolean validationReq = true;
		if (this.userAction.getSelectedItem() != null) {
			if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				validationReq = false;
			}
		}

		// Validation Not required Cases excluding
		if (validationReq) {
			doClearMessage();
			doSetValidation();
		}

		// fill the Academic object with the components data
		doWriteComponentsToBean(extendedFieldHeader, validationReq);

		// Write the additional validations as per below example
		// get the selected branch object from the list box
		// Do data level validations here

		isNew = extendedFieldHeader.isNewRecord();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(extendedFieldHeader.getRecordType())) {
				extendedFieldHeader.setVersion(extendedFieldHeader.getVersion() + 1);
				if (isNew) {
					extendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					extendedFieldHeader.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					extendedFieldHeader.setNewRecord(true);
				}
			}
		} else {
			extendedFieldHeader.setVersion(extendedFieldHeader.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(extendedFieldHeader, tranType)) {
				refreshList();
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aAcademic (Academic)
	 * 
	 * @param tranType  (String)
	 * 
	 * @return boolean
	 * 
	 */
	protected boolean doProcess(ExtendedFieldHeader extendedFieldHeader, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		extendedFieldHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		extendedFieldHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		extendedFieldHeader.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			extendedFieldHeader.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(extendedFieldHeader.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, extendedFieldHeader);
				}

				if (isNotesMandatory(taskId, extendedFieldHeader)) {
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

			extendedFieldHeader.setTaskId(taskId);
			extendedFieldHeader.setNextTaskId(nextTaskId);
			extendedFieldHeader.setRoleCode(getRole());
			extendedFieldHeader.setNextRoleCode(nextRoleCode);
			for (ExtendedFieldDetail ext : extendedFieldHeader.getExtendedFieldDetails()) {
				ext.setWorkflowId(extendedFieldHeader.getWorkflowId());
				ext.setRecordStatus(extendedFieldHeader.getRecordStatus());
				ext.setTaskId(extendedFieldHeader.getTaskId());
				ext.setNextTaskId(extendedFieldHeader.getNextTaskId());
				ext.setRoleCode(extendedFieldHeader.getRoleCode());
				ext.setNextRoleCode(extendedFieldHeader.getNextRoleCode());
				ext.setLastMntBy(extendedFieldHeader.getLastMntBy());
				if (PennantConstants.RECORD_TYPE_DEL.equals(extendedFieldHeader.getRecordType())) {
					if (StringUtils.trimToNull(ext.getRecordType()) == null) {
						ext.setRecordType(extendedFieldHeader.getRecordType());
						ext.setNewRecord(true);
					}
				}
			}

			auditHeader = getAuditHeader(extendedFieldHeader, tranType);
			String operationRefs = getServiceOperations(taskId, extendedFieldHeader);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(extendedFieldHeader, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(extendedFieldHeader, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader (AuditHeader)
	 * 
	 * @param method      (String)
	 * 
	 * @return boolean
	 * 
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		ExtendedFieldHeader extendedFieldHeader = (ExtendedFieldHeader) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;

		while (retValue == PennantConstants.porcessOVERIDE) {
			if (StringUtils.isBlank(method)) {
				if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
					auditHeader = getExtFieldConfigService().delete(auditHeader);
					deleteNotes = true;
				} else {
					auditHeader = getExtFieldConfigService().saveOrUpdate(auditHeader);
				}

			} else {
				if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
					auditHeader = getExtFieldConfigService().doApprove(auditHeader);

					if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
						deleteNotes = true;
					}

				} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
					auditHeader = getExtFieldConfigService().doReject(auditHeader);

					if (extendedFieldHeader.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
						deleteNotes = true;
					}

				} else {
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_ExtFieldConfigDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_ExtFieldConfigDialog, auditHeader);
			retValue = auditHeader.getProcessStatus();

			if (retValue == PennantConstants.porcessCONTINUE) {
				processCompleted = true;

				if (deleteNotes) {
					deleteNotes(getNotes(this.extendedFieldHeader), true);
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
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aAcademic
	 * @param tranType
	 * @return AuditHeader
	 */
	private AuditHeader getAuditHeader(ExtendedFieldHeader extendedFieldHeader, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, extendedFieldHeader.getBefImage(), extendedFieldHeader);
		return new AuditHeader(getReference(), null, null, null, auditDetail, extendedFieldHeader.getUserDetails(),
				getOverideMap());
	}

	/**
	 * The framework calls this event handler when user clicks the notes button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.extendedFieldHeader);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	protected void refreshList() {
		configListCtrl.search();
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.extendedFieldHeader.getSubModuleName());
	}

	/**
	 * @return the extFieldConfigService
	 */
	public ExtFieldConfigService getExtFieldConfigService() {
		return extFieldConfigService;
	}

	/**
	 * @param extFieldConfigService the extFieldConfigService to set
	 */
	public void setExtFieldConfigService(ExtFieldConfigService extFieldConfigService) {
		this.extFieldConfigService = extFieldConfigService;
	}

	public void setExtendedFieldDialogCtrl(ExtendedFieldDialogCtrl extendedFieldDialogCtrl) {
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
