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
 * FileName    		:  FeeTierDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  16-09-2011    														*
 *                                                                  						*
 * Modified Date    :  16-09-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 16-09-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.rulefactory.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.JavaScriptBuilder;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Fee/RuleResult/feeTierDialog.zul file.
 */
public class RuleResultDialogCtrl extends GFCBaseCtrl<JavaScriptBuilder> {
	private static final long serialVersionUID = -2393925908398735705L;
	private static final Logger logger = Logger.getLogger(RuleResultDialogCtrl.class);

	protected Window window_RuleResultDialog;
	public Codemirror formula; // FIXME change the modifier into protected

	protected Listbox listboxFieldCodes;
	protected Listbox listboxFeeOperators;

	protected Tabbox tabbox_AmtCodes;
	protected Tabbox tabbox_optValues;

	protected Column amtCodeColumn;
	protected Column coremirrorColumn;
	protected Column optValuesColumn;

	protected Button ruleResult_btnValidate;

	// ServiceDAOs / Domain Classes
	private List<RBFieldDetail> objectFieldList;
	private RuleReturnType returnType = null;

	String returnValue = "";
	Textbox calculateBox = null;
	String validateMode = "";

	/**
	 * default constructor.<br>
	 */
	public RuleResultDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected RuleResult object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_RuleResultDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RuleResultDialog);

		// READ OVERHANDED parameters !
		// we get the feeTierListWindow controller. So we have access to it and can synchronize the shown data when we
		// do insert, edit or delete RuleResult here.
		if (arguments.containsKey("objectFieldList")) {
			objectFieldList = (List<RBFieldDetail>) arguments.get("objectFieldList");
		}

		if (arguments.containsKey("returnType")) {
			returnType = (RuleReturnType) arguments.get("returnType");
		}

		if (arguments.containsKey("CalculateBox")) {
			calculateBox = (Textbox) arguments.get("CalculateBox");
			returnValue = calculateBox.getValue();
		}

		if (arguments.containsKey("Mode")) {
			validateMode = (String) arguments.get("Mode");
		}

		getBorderLayoutHeight();
		int groupboxHeight = borderLayoutHeight - 150;
		tabbox_AmtCodes.setHeight(groupboxHeight + "px");
		tabbox_optValues.setHeight(groupboxHeight + "px");
		listboxFieldCodes.setHeight((groupboxHeight - 35) + "px");
		listboxFeeOperators.setHeight((groupboxHeight - 35) + "px");
		formula.setHeight(groupboxHeight + "px");

		// ++ create the searchObject and initialize sorting ++//
		Listitem item;
		Listcell cell;
		if (objectFieldList != null) {
			String fieldType = null;
			for (RBFieldDetail rbFieldDetail : objectFieldList) {
				fieldType = rbFieldDetail.getRbFldType();
				boolean flag = false;

				if (this.returnType == RuleReturnType.DECIMAL || this.returnType == RuleReturnType.INTEGER) {
					if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.DECIMAL))
							|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.BIGINT))
							|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NUMERIC))) {
						flag = true;
					}
				} else {
					flag = true;
				}

				if (flag) {
					item = new Listitem();
					cell = new Listcell(rbFieldDetail.getRbFldName());
					cell.setStyle("text-align:left;");
					cell.setParent(item);
					cell = new Listcell(rbFieldDetail.getRbFldDesc());
					cell.setStyle("text-align:left;");
					cell.setParent(item);
					item.setParent(listboxFieldCodes);
				}
			}
		}

		List<ValueLabel> listRuleOperators = PennantStaticListUtil.getRuleOperator();

		if (listRuleOperators != null) {
			for (int i = 0; i < listRuleOperators.size(); i++) {
				item = new Listitem();
				cell = new Listcell(listRuleOperators.get(i).getValue());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				cell = new Listcell(listRuleOperators.get(i).getLabel());
				cell.setStyle("text-align:left;");
				cell.setParent(item);
				item.setParent(listboxFeeOperators);
			}
		}

		doEdit();

		try {
			// fill the components with the data
			this.formula.setFocus(true);
			this.formula.setValue(returnValue);
			this.window_RuleResultDialog.doModal();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_RuleResultDialog.onClose();
		}

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

		MessageUtil.showHelpWindow(event, window_RuleResultDialog);

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

	// CRUD operations

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Enterring");

		// btnCancel.setVisible(false);
		this.formula.setReadonly(false);

		this.amtCodeColumn.setWidth("45%");
		this.coremirrorColumn.setWidth("35%");
		this.optValuesColumn.setWidth("20%");

		logger.debug("Leaving");

	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Enterring");

		// Clients.evalJavaScript(javaScript)
		if (this.formula.getValue() == null || StringUtils.isBlank(this.formula.getValue())) {
			if (!this.formula.isReadonly()) {
				throw new WrongValueException(formula, "Please enter code");
			}
		}
		try {
			calculateBox.setValue(this.formula.getValue());
		} catch (WrongValueException e) {
			this.window_RuleResultDialog.onClose();
			throw e;
		}
		this.window_RuleResultDialog.onClose();
		logger.debug("Leaving");
	}

	/**
	 * Method for creating Simulated window with Existing Fields
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void createSimulationWindow(List<ValueLabel> fieldsList) throws InterruptedException {
		logger.debug("Entering");

		final HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("fieldsList", fieldsList);
		map.put("ruleResultDialogCtrl", this);
		map.put("returnType", this.returnType);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultView.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void onClick$btnSave(Event event) {
		calculateBox.setValue(formula.getValue());
	}

	/**
	 * This Method/Event is called from the java script function Validate/Simulate. It will open a new window to execute
	 * the rule
	 * 
	 * @param event
	 */
	public void onUser$ruleResult_btnValidate(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if (StringUtils.isBlank(this.formula.getValue())) {
			MessageUtil.showError(Labels.getLabel("FIELD_NO_EMPTY", new String[] { Labels.getLabel("label_Formula") }));
		} else {
			while (event.getData() == null) {
				event = ((ForwardEvent) event).getOrigin();
			}

			Object[] data = (Object[]) event.getData();

			// Check clicking button is for Validation or Simulation
			boolean isSimulated = (Boolean) data[0];
			JSONArray errors = (JSONArray) data[1];
			JSONArray codeVariables = (JSONArray) data[2];
			boolean isSaveRecord = (Boolean) data[3];

			String values = "";
			String fieldValue="";
			List<ValueLabel> fieldList = new ArrayList<>();
			HashMap<String, String>fieldMap = new HashMap<>();
			for (RBFieldDetail rbFieldDetail : objectFieldList) {
				fieldMap.put(rbFieldDetail.getRbFldName(), rbFieldDetail.getRbFldDesc());
			}

			for (int i = 0; i < codeVariables.size(); i++) {
				JSONObject jsonObject = (JSONObject) codeVariables.get(i);
				if (!"Result".equals(jsonObject.get("name")) && !(jsonObject.get("name").toString()).startsWith(RuleConstants.RULEFIELD_CCY)) {
					fieldValue = (String) jsonObject.get("name");
					if(fieldMap.containsKey(fieldValue)){
						fieldList.add(new ValueLabel(fieldValue, fieldMap.get(fieldValue)));
						if(StringUtils.isNotEmpty(values)){
							values = values + ",";
						}
						values = values + fieldValue;
					}else{
						MessageUtil.showError(Labels.getLabel("FIELD_NOT_AVAILBLE", new String[] { fieldValue }));
						return;
					}
				}
			}

			if (isSimulated) {
				createSimulationWindow(fieldList);
			} else {
				if (errors.size() != 0) {
					if (MessageUtil.confirm(errors.size()
							+ PennantJavaUtil.getLabel("message_ErrorCount_CodeMirror")) == MessageUtil.YES) {
						Events.postEvent("onUser$errors", window_RuleResultDialog, errors);
					}
				} else {
					if (isSaveRecord) {
						doSave();
						if (StringUtils.isBlank(values)) {
							values = " ";
						}
						calculateBox.setAttribute("calculatedFields", values);
					} else {
						MessageUtil.showMessage(PennantJavaUtil.getLabel("message_NoError_CodeMirror"));
					}
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for showing Error Details
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onUser$errors(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		if (StringUtils.isEmpty(formula.getValue())) {
			MessageUtil.showError(Labels.getLabel("Code_NotEmpty"));
		} else {
			JSONArray message = (JSONArray) event.getData();
			for (int i = 0; i < message.size(); i++) {
				JSONObject jsonObject = (JSONObject) message.get(i);
				if (jsonObject != null) {
					String errorMsg = "Error : Line-" + jsonObject.get("line") + ",Character-"
							+ jsonObject.get("character") + "\n\n" + (String) jsonObject.get("reason");

					if (message.size() - 1 != i + 1) {
						errorMsg = errorMsg + "\n\n" + PennantJavaUtil.getLabel("message_ErrorProcess_Conformation");

						if (MessageUtil.confirm(errorMsg) == MessageUtil.NO) {
							break;
						}
					} else {
						MessageUtil.showError(errorMsg);

						break;
					}
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setAmountCodesList(List<RBFieldDetail> objectFieldList) {
		this.objectFieldList = objectFieldList;
	}

	public List<RBFieldDetail> getObjectFieldList() {
		return objectFieldList;
	}
}
