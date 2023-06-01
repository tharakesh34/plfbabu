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
 * * FileName : JavaScriptBuilder.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-07-2013 * * Modified
 * Date : 23-05-2013 * * Description : JavaScript Builder * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-07-2013 Chaitanya Varma 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.backend.model.rulefactory.JSRuleReturnType;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.StringReplacement;
import com.pennant.pff.letter.CourierStatus;
import com.pennant.pff.receipt.ClosureType;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.web.util.MessageUtil;

public class JavaScriptBuilder extends Groupbox {
	private static final Logger logger = LogManager.getLogger(JavaScriptBuilder.class);

	private static final long serialVersionUID = 1L;

	protected Tree tree;
	protected Space space;
	protected Codemirror codemirror;
	protected Codemirror splCodemirror;
	protected Tabbox tabbox;
	protected Tabs tabs;
	protected Tab treeTab;
	protected Tab scriptTab;
	protected Tab specialRulescriptTab;
	protected Tabpanels tabPanels;
	protected Tabpanel treeTabpanel;
	protected Tabpanel scriptTabpanel;
	protected Tabpanel specialRulescriptTabpanel;

	private String actualBlock = "";
	private String sqlQuery = "";
	private String actualQuery = "";
	private String splQuery = "";
	private String fields = "";
	private String query = "";
	private String module = "";
	private String event = "";

	private int uniqueId = 0;
	private int logicCount = 0;
	private int mode = 1; // (1-SelectFromFields, 2-All)
	private int currentDesktopHeight;
	private int borderLayoutHeight = 0;
	private int noOfRowsVisible = 5;
	private int tabPanelboxHeight = 200;
	private int spaceCount = 0;
	public static final int borderlayoutMainNorth = 100;

	private RuleReturnType ruleType = RuleReturnType.BOOLEAN; // Default Boolean Type
	private RuleModule ruleModule;
	private ModuleMapping moduleMapping = null;
	private Set<String> fieldsSet = new HashSet<String>();

	private boolean editable = true;
	private boolean readOnly = true; // FIXME Temporary Solution for Saving

	private List<ValueLabel> operatorsList = PennantStaticListUtil.getOperators("JS"); // retrieve all the operators
	private List<ValueLabel> operandTypesList = PennantStaticListUtil.getOperandTypes("JS"); // retrieve all selection
																								// types
	private List<ValueLabel> logicalOperatorsList = PennantStaticListUtil.getLogicalOperators("JS");// retrieve values
	private List<GlobalVariable> globalVariableList = SysParamUtil.getGlobaVariableList();// retrieve values from
																							// table--GlobalVariable
	private List<RBFieldDetail> objectFieldList = null;// retrieve values
	private List<JSRuleReturnType> jsRuleReturnTypeList = null;

	private List<ValueLabel> closureTypeList = ClosureType.getTypes();
	private List<ValueLabel> courierStatusList = CourierStatus.getTypes();

	protected Groupbox groupbox;
	protected Toolbar toolbar;
	protected Button btnValidate = new Button("GENERATE");
	protected Button btnSimulation = new Button("SIMULATE");

	/**
	 * Enumerates the supported RuleTabs.
	 */
	public enum RuleTabs {
		DESIGN, SCRIPT, SPLSCRIPT
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Query object in a Map.
	 * 
	 * @param event
	 */
	public JavaScriptBuilder() {
		super();
		doSetProperties();
	}

	/**
	 * Set the default properties to the component
	 */
	private void doSetProperties() {
		logger.debug("Entering");
		this.uniqueId = 0;

		this.setHeight("100%");
		this.setStyle("overflow:auto;");

		this.groupbox = new Groupbox(); // Groupbox
		this.groupbox.setParent(this);

		this.tabbox = new Tabbox(); // Tabbox
		this.tabbox.setParent(this.groupbox);

		this.tabs = new Tabs(); // Tabs
		this.tabs.setParent(tabbox);

		this.treeTab = new Tab("Rule Design");
		this.treeTab.setParent(tabs);

		this.specialRulescriptTab = new Tab("Special Rule");
		this.specialRulescriptTab.setParent(tabs);

		this.scriptTab = new Tab("Rule Result");
		this.scriptTab.setParent(tabs);

		this.toolbar = new Toolbar(); // Toolbar
		this.toolbar.setStyle("padding: 0px 0px 0px;");
		this.toolbar.setParent(tabbox);
		this.toolbar.setAlign("end");
		this.toolbar.setSclass("toolbar-end");

		this.btnValidate.setParent(toolbar);
		this.btnValidate.addForward("onClick", this, "onClick$btnValidate");

		Space space = new Space();
		space.setSpacing("10px");
		space.setParent(this.toolbar);

		this.btnSimulation.setParent(this.toolbar);
		this.btnSimulation.addForward("onClick", this, "onClick$btnSimulation");

		this.tabPanels = new Tabpanels();
		this.tabPanels.setParent(this.tabbox);

		this.treeTabpanel = new Tabpanel();
		this.treeTabpanel.setParent(this.tabPanels);

		setBorderLayoutHeight();
		this.tabPanelboxHeight = this.borderLayoutHeight - (this.noOfRowsVisible * 20) - 185;

		this.tree = new Tree();
		this.tree.setZclass("z-dottree");
		this.tree.setSclass("QueryBuilderTree");
		this.tree.setHeight(this.tabPanelboxHeight + "px");
		this.tree.setStyle("overflow:auto;");
		this.tree.setParent(this.treeTabpanel);

		this.specialRulescriptTabpanel = new Tabpanel();
		this.specialRulescriptTabpanel.setParent(tabPanels);

		this.scriptTabpanel = new Tabpanel();
		this.scriptTabpanel.setParent(tabPanels);

		this.splCodemirror = new Codemirror();
		this.splCodemirror.setWidth("100%");
		this.splCodemirror.setReadonly(false);
		// this.splCodemirror.setConfig("lineNumbers:true");
		this.splCodemirror.setSyntax("js");
		this.splCodemirror.setHeight(tabPanelboxHeight + "px");
		this.splCodemirror.setStyle("overflow:auto;");
		this.splCodemirror.setParent(this.specialRulescriptTabpanel);

		this.codemirror = new Codemirror();
		this.codemirror.setWidth("100%");
		this.codemirror.setReadonly(true);
		// this.codemirror.setConfig("lineNumbers:true");
		this.codemirror.setSyntax("js");
		this.codemirror.setHeight(tabPanelboxHeight + "px");
		this.codemirror.setStyle("overflow:auto;");
		this.codemirror.setParent(this.scriptTabpanel);

		logger.debug("Leaving");
	}

	/**
	 * gives desktop height
	 * 
	 * @return currentDesktopHeight
	 */
	public static int getCurrentDesktopHeight() {
		Intbox currentDesktopHeight = (Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight");

		if (currentDesktopHeight.getValue() != null) {
			return currentDesktopHeight.getValue().intValue();
		}

		return 0;
	}

	/**
	 * set the border layout height
	 */
	public void setBorderLayoutHeight() {
		this.currentDesktopHeight = getCurrentDesktopHeight();
		this.borderLayoutHeight = this.currentDesktopHeight - borderlayoutMainNorth;
	}

	/**
	 * Create a new Condition
	 * 
	 * @param condition(String) to build new condition based on the dbQuery
	 * @param label(String)
	 * @return treeItem(TreeItem)
	 */
	private Treeitem createNewCondition(String condition, String label) {
		logger.debug("Entering");

		String[] buildCondition = null;
		String uUID = "cond" + this.getId() + this.uniqueId;
		this.uniqueId++;
		String excludeFields = null;
		boolean isMaintain = false;
		boolean elseCondition = false;
		int count = 0;
		Space space;

		if (StringUtils.isNotBlank(condition)) {
			buildCondition = condition.split("\\|");
			isMaintain = true;
			label = buildCondition[count];
			count++;
		}
		if (StringUtils.equalsIgnoreCase(label, "else")) {
			elseCondition = true;
		}

		// Tree item Preparation Starts here
		Treeitem treeItem = new Treeitem();
		treeItem.setId("treeItem" + this.getId() + this.uniqueId);

		Treerow treeRow = new Treerow();
		treeRow.setParent(treeItem);

		Treecell treeCell = new Treecell();
		treeCell.setParent(treeRow);
		treeCell.setId(uUID);

		// This will be used for set spacing for each item. This should be removed once we get fix from ZK on the Tree
		// formatting.
		space = new Space();
		space.setId("space" + treeCell.getId());
		space.setWidth("0px");
		space.setParent(treeCell);

		// Creating remove Button
		Button buttonRemove = new Button();
		buttonRemove.setId(uUID + "_RmvCond");
		buttonRemove.setParent(treeCell);
		buttonRemove.setImage("/images/icons/delete.png");
		buttonRemove.addForward("onClick", this, "onRemoveCondition", treeItem);

		// Remove Button Disabled in First IF condition and ELSE Condition
		if (uniqueId == 1 || StringUtils.equalsIgnoreCase(label, "ELSE")) {
			buttonRemove.setDisabled(true);
		}

		space = new Space();
		space.setWidth("5px");
		space.setParent(treeCell);

		// Creating logical operator (AND, OR)
		if ("AND".equalsIgnoreCase(label) || "OR".equalsIgnoreCase(label)) {
			Combobox comboboxLogicalOperator = new Combobox();
			comboboxLogicalOperator.setId(uUID + "_logicalOperator");
			comboboxLogicalOperator.setParent(treeCell);
			comboboxLogicalOperator.setWidth("70px");
			comboboxLogicalOperator.setReadonly(true);
			if (isMaintain) {
				label = label.replace("AND", "&&");
				label = label.replace("OR", "||");
			}
			fillComboBox(comboboxLogicalOperator, label, logicalOperatorsList, null);
		} else {
			// Creating Conditional statement (IF, ELSE IF, ELSE)
			Label labelCondition = new Label(label.toUpperCase());
			labelCondition.setId(uUID + "_statement");
			labelCondition.setParent(treeCell);
			labelCondition.setSclass("queryBuilder_Label");
		}

		if (!elseCondition) {
			// Left Operand Type creation
			space = new Space();
			space.setWidth("5px");
			space.setParent(treeCell);

			Combobox leftOperandType = new Combobox();
			leftOperandType.setId(uUID + "_leftOperandType");
			leftOperandType.setParent(treeCell);
			leftOperandType.setWidth("120px");
			leftOperandType.setReadonly(true);
			leftOperandType.addForward("onChange", this, "onChangeOperandType", null);

			String leftOperandTypeValue = null;

			if (getMode() == 1) { // for automatically gives the selected from fields list
				leftOperandTypeValue = RuleConstants.FIELDLIST;
				leftOperandType.setVisible(false);
			}

			if (isMaintain) {
				leftOperandTypeValue = buildCondition[count];
				count++;
			}

			fillComboBox(leftOperandType, leftOperandTypeValue, operandTypesList, excludeFields);

			// Creating Left Operand
			space = new Space();
			space.setId("space_" + leftOperandType.getId());
			space.setWidth("5px");
			space.setParent(treeCell);

			String leftOperandValue = null;
			String leftOperandCalculatedFields = " ";

			if (isMaintain) {
				leftOperandValue = buildCondition[count];
				count++;
				leftOperandCalculatedFields = buildCondition[count];
				count++;
			}

			Component leftOperand = createOperand(uUID + "_leftOperand", leftOperandType, leftOperandValue);
			leftOperand.setAttribute("calculatedFields", leftOperandCalculatedFields); // for Calculate Fields

			// Logical Operator creation
			Combobox comboLogicalOperator = new Combobox();
			comboLogicalOperator.setId(uUID + "_operator");
			comboLogicalOperator.setParent(treeCell);
			comboLogicalOperator.setWidth("120px");
			comboLogicalOperator.setAttribute("TreeCell", treeCell);
			comboLogicalOperator.setReadonly(true);
			comboLogicalOperator.addForward("onChange", this, "onChangeOperator", null);

			String logicalOperatorValue = null;
			excludeFields = "";

			// Prepare Exclude fields to the operators based on the Left Operand Value.
			if (isMaintain) {
				logicalOperatorValue = buildCondition[count];
				count++;
				if (leftOperand instanceof Combobox) {
					Combobox leftOperandCombo = (Combobox) leftOperand; // to reduce type cast
					leftOperandTypeValue = leftOperandType.getSelectedItem().getValue();

					if (StringUtils.equals(leftOperandTypeValue, RuleConstants.FIELDLIST)) {
						RBFieldDetail fielddetails = (RBFieldDetail) leftOperandCombo.getSelectedItem()
								.getAttribute("FieldDetails");
						excludeFields = getExcludeFieldsByOperands(leftOperandCombo, "operator", fielddetails);
					} else if (StringUtils.equals(leftOperandTypeValue, RuleConstants.GLOBALVAR)) {
						GlobalVariable globalVariable = (GlobalVariable) leftOperandCombo.getSelectedItem()
								.getAttribute("GlobalVariableDetails");
						excludeFields = getGlobalExcludeFieldsByOperands(leftOperandCombo, "operator", globalVariable);
					}
				}
			}

			fillComboBox(comboLogicalOperator, logicalOperatorValue, operatorsList, excludeFields);

			space = new Space();
			space.setId("space_" + comboLogicalOperator.getId());
			space.setWidth("5px");
			space.setParent(treeCell);

			// Right Operand Type creation
			Combobox rightOperandType = new Combobox();
			rightOperandType.setId(uUID + "_rightOperandType");
			rightOperandType.setParent(treeCell);
			rightOperandType.setWidth("120px");
			rightOperandType.setReadonly(true);
			rightOperandType.addForward("onChange", this, "onChangeOperandType", null);
			excludeFields = "";

			String rightOperandTypeValue = null;
			String rightOperandTypeCalcFields = " ";

			if (isMaintain) {
				rightOperandTypeValue = buildCondition[count];
				count++;
				excludeFields = getExcludeFieldsByOperator(comboLogicalOperator, leftOperand, leftOperandType);
			}

			fillComboBox(rightOperandType, rightOperandTypeValue, operandTypesList, excludeFields);

			space = new Space();
			space.setId("space_" + rightOperandType.getId());
			space.setWidth("5px");
			space.setParent(treeCell);

			// Creating Right Operand
			Component rightOperand = createOperand(uUID + "_rightOperand", rightOperandType,
					isMaintain ? buildCondition[count++] : null);
			if (isMaintain) {
				rightOperandTypeCalcFields = buildCondition[count];
				count++;
			}
			rightOperand.setAttribute("calculatedFields", rightOperandTypeCalcFields); // for Calculated Fields
		}

		// Creating AND for adding a new condition
		Label buttonAnd = new Label();
		buttonAnd.setValue("AND");
		buttonAnd.setId(uUID + "_btn_AND");
		buttonAnd.setSclass("button_Label");
		buttonAnd.addForward("onClick", this, "onClickLogicalOperator", treeItem);
		buttonAnd.setParent(treeCell);

		if (isMaintain) {
			buttonAnd.setVisible("1".equals(buildCondition[count]) ? true : false);
			count++;
		}

		space = new Space();
		space.setId("space_" + buttonAnd.getId());
		space.setWidth("5px");
		space.setParent(treeCell);

		/*
		 * if (isMaintain) { space.setVisible("1".equals(buildCondition[count - 1]) ? true : false); }
		 */

		// Creating ELSE Button for adding a new condition
		Label buttonElse = new Label();
		buttonElse.setTooltiptext("Add Else Condition");
		buttonElse.setValue("ELSE");
		buttonElse.setId(uUID + "_btn_ELSE");
		buttonElse.addForward("onClick", this, "onClickElse", treeItem);
		buttonElse.setSclass("button_Label");
		buttonElse.setParent(treeCell);

		if (isMaintain) {
			buttonElse.setVisible("1".equals(buildCondition[count]) ? true : false);
			count++;
		}

		space = new Space();
		space.setId("space_" + buttonElse.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		/*
		 * if (isMaintain) { space.setVisible("1".equals(buildCondition[count - 1]) ? true : false); }
		 */

		// Creating ELSE IF Button for adding a new condition
		Label buttonElseIf = new Label();
		buttonElseIf.setTooltiptext("Add Else IF Condition");
		buttonElseIf.setValue("ELSE IF");
		buttonElseIf.setId(uUID + "_btn_ELSEIF");
		buttonElseIf.setSclass("button_Label");
		buttonElseIf.addForward("onClick", this, "onClickElseIF", treeItem);
		buttonElseIf.setParent(treeCell);

		if (isMaintain) {
			buttonElseIf.setVisible("1".equals(buildCondition[count]) ? true : false);
			count++;
		}

		space = new Space();
		space.setId("space_" + buttonElseIf.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		/*
		 * if (isMaintain) { space.setVisible("1".equals(buildCondition[count - 1]) ? true : false); }
		 */

		// Creating NESTED IF Button for adding a new condition
		Label buttonNestedIf = new Label(); // Nested IF
		buttonNestedIf.setTooltiptext("Add Nested IF Condition");
		buttonNestedIf.setValue("NESTED IF");
		buttonNestedIf.setId(uUID + "_btn_NESTEDIF");
		buttonNestedIf.setSclass("button_Label");
		buttonNestedIf.addForward("onClick", this, "onClickNestedIf", treeItem);
		buttonNestedIf.setParent(treeCell);

		if (isMaintain) {
			buttonNestedIf.setVisible("1".equals(buildCondition[count]) ? true : false);
			count++;
		}

		space = new Space();
		space.setId("space_" + buttonNestedIf.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		/*
		 * if (isMaintain) { space.setVisible("1".equals(buildCondition[count - 1]) ? true : false); }
		 */

		// Creating CALCULATE Button for adding a new sub condition
		Label buttonCalculate = new Label(); // Calculation
		buttonCalculate.setTooltiptext("Click for Add Return Value");
		buttonCalculate.setValue("CALCULATE");
		buttonCalculate.setId(uUID + "_btn_CALCULATE");
		buttonCalculate.setParent(treeCell);
		buttonCalculate.setSclass("button_Label");

		if (isMaintain) {
			buttonCalculate.setVisible("1".equals(buildCondition[count]) ? true : false);
			count++;
		} else {
			if (this.ruleType == RuleReturnType.DECIMAL || this.ruleType == RuleReturnType.CALCSTRING) {
				buttonCalculate.setVisible(true);
			} else {
				buttonCalculate.setVisible(false);
			}
		}

		/*
		 * if (button_Calculate.isVisible()) { space.setVisible(true); } else { space.setVisible(false); }
		 */
		space = new Space();
		space.setId("space_" + buttonCalculate.getId());
		space.setWidth("5px");
		space.setParent(treeCell);

		/*
		 * if (isMaintain) { space.setVisible("1".equals(buildCondition[count - 1]) ? true : false); }
		 */

		// Result
		resultCreation(buildCondition, uUID, isMaintain, count, space, treeCell, buttonCalculate);

		logger.debug("Leaving");

		return treeItem;
	}

	/**
	 * @param buildCondition
	 * @param uUID
	 * @param isMaintain
	 * @param count
	 * @param space
	 * @param treeCell
	 * @param result
	 */
	private void resultCreation(String[] buildCondition, String uUID, boolean isMaintain, int count, Space space,
			Treecell treeCell, Label result) {

		if (this.ruleType == RuleReturnType.OBJECT) {
			customisedComponentCreation(buildCondition, uUID, isMaintain, count, treeCell);
		} else if (this.ruleType == RuleReturnType.DECIMAL || this.ruleType == RuleReturnType.STRING
				|| this.ruleType == RuleReturnType.CALCSTRING) {
			if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
				customisedComponentCreation(buildCondition, uUID, isMaintain, count, treeCell);
			} else {
				Textbox tbResult = new Textbox(); // Result TextBox
				tbResult.setId(uUID + "_RESULT");
				tbResult.setParent(treeCell);
				tbResult.setWidth("120px");
				result.addForward("onClick", this, "onClickCalculate", tbResult);

				String tbResultCalcFields = " ";

				if (isMaintain) {
					if (count >= buildCondition.length) {
						tbResult.setVisible(false);
					} else {
						tbResult.setValue(buildCondition[count]);
						count++;
						if (StringUtils.isEmpty(tbResult.getValue())) {
							tbResult.setVisible(false);
						}
						tbResultCalcFields = buildCondition[count];
					}
				}

				tbResult.setAttribute("calculatedFields", tbResultCalcFields);
			}
		} else if (this.ruleType == RuleReturnType.INTEGER) {
			if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
				customisedComponentCreation(buildCondition, uUID, isMaintain, count, treeCell);
			} else {
				Intbox ibResult = new Intbox(); // Result IntBox
				ibResult.setId(uUID + "_RESULT");
				ibResult.setParent(treeCell);
				ibResult.setWidth("120px");

				ibResult.setVisible(true);

				if (isMaintain) {
					if (count >= buildCondition.length) {
						ibResult.setVisible(false);
					} else {
						ibResult.setValue(Integer.valueOf(buildCondition[count]));
						count++;
					}
				}
			}
		} else if (this.ruleType == RuleReturnType.BOOLEAN) {
			if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
				customisedComponentCreation(buildCondition, uUID, isMaintain, count, treeCell);
			} else {
				String cbResultValue = null;
				List<ValueLabel> booleanList = new ArrayList<ValueLabel>();
				booleanList.add(new ValueLabel("1", "TRUE"));
				booleanList.add(new ValueLabel("0", "FALSE"));

				Combobox cbResult = new Combobox(); // Result ComboBox
				cbResult.setId(uUID + "_RESULT");
				cbResult.setParent(treeCell);
				cbResult.setReadonly(true);
				cbResult.setWidth("120px");

				if (isMaintain) {
					if (count >= buildCondition.length) {
						cbResult.setVisible(false);
					} else {
						cbResultValue = buildCondition[count];
						count++;
					}
				}

				fillComboBox(cbResult, cbResultValue, booleanList, "");
			}
		} else {
			space.setVisible(false);
		}
	}

	/**
	 * @param buildCondition
	 * @param uUID
	 * @param isMaintain
	 * @param count
	 * @param treeCell
	 */
	private void customisedComponentCreation(String[] buildCondition, String uUID, boolean isMaintain, int count,
			Treecell treeCell) {
		Space space;
		int loopCount = 0;
		for (JSRuleReturnType jsRuleReturnType : this.jsRuleReturnTypeList) {
			space = new Space();
			space.setParent(treeCell);
			space.setWidth("5px");

			if (StringUtils.equals(RuleConstants.COMPONENTTYPE_EXTENDEDCOMBOBOX, jsRuleReturnType.getComponentType())) {
				String resultExtendedComboValue = null;
				String resultExtendedComboCalcFields = " ";

				ExtendedCombobox extendedCombo = new ExtendedCombobox();
				extendedCombo.setId(uUID + "_RESULT" + loopCount);
				extendedCombo.setParent(treeCell);
				extendedCombo.getTextbox().setReadonly(true);

				if (isMaintain) {
					resultExtendedComboValue = buildCondition[count].replace("'", "");
					count++;
					resultExtendedComboCalcFields = buildCondition[count];
					count++;
				}

				extendedCombo.setValue(resultExtendedComboValue);
				extendedCombo.setAttribute("calculatedFields", resultExtendedComboCalcFields);
				extendedCombo.setValueColumn(jsRuleReturnType.getValueColumn());
				extendedCombo.setValidateColumns(jsRuleReturnType.getValidateColumns());
				extendedCombo.setButtonDisabled(StringUtils.isBlank(jsRuleReturnType.getModuleName()));

				if (jsRuleReturnType.isMultiSelection()) {
					extendedCombo.addForward("onFulfill", this, "onFulfillExtendedComobo", jsRuleReturnType);
				} else {
					extendedCombo.setModuleName(jsRuleReturnType.getModuleName());
				}
			} else if (StringUtils.equals(RuleConstants.COMPONENTTYPE_COMBOBOX, jsRuleReturnType.getComponentType())) {
				String resultComboboxValue = null;
				String resultComboboxCalcFields = " ";

				Combobox resultCombobox = new Combobox();
				resultCombobox.setId(uUID + "_RESULT" + loopCount);
				resultCombobox.setParent(treeCell);
				resultCombobox.setWidth("120px");
				resultCombobox.setReadonly(true);

				if (isMaintain) {
					resultComboboxValue = buildCondition[count];
					count++;
					resultComboboxCalcFields = buildCondition[count];
					count++;
					if (StringUtils.equals(resultComboboxValue, PennantConstants.List_Select)
							|| StringUtils.isBlank(resultComboboxValue.trim())) {
						resultCombobox.setVisible(false);
					}
				}

				resultCombobox.setAttribute("calculatedFields", resultComboboxCalcFields);
				fillComboBox(resultCombobox, resultComboboxValue, jsRuleReturnType.getListOfData(), "");

				// for deviation case we added this (old code: } else if
				// (StringUtils.equals(RuleConstants.COMPONENTTYPE_DECIMAL, jsRuleReturnType.getComponentType())) { )
			} else if (StringUtils.equals(RuleConstants.COMPONENTTYPE_DECIMAL, jsRuleReturnType.getComponentType())
					|| StringUtils.equals(RuleConstants.COMPONENTTYPE_INTEGER, jsRuleReturnType.getComponentType())) {
				Label buttonCalculate = new Label(); // Calculation
				buttonCalculate.setTooltiptext("Click for Add Return Value");
				buttonCalculate.setValue("CALCULATE");
				buttonCalculate.setId(uUID + "_btn_CALCULATE" + loopCount);
				buttonCalculate.setParent(treeCell);
				buttonCalculate.setSclass("button_Label");

				if (isMaintain) {
					buttonCalculate.setVisible("1".equals(buildCondition[count]) ? true : false);
					count++;
				}

				space = new Space();
				space.setParent(treeCell);
				space.setWidth("5px");

				Textbox tbResult = new Textbox(); // Result TextBox
				tbResult.setId(uUID + "_RESULT" + loopCount);
				tbResult.setParent(treeCell);
				tbResult.setWidth("120px");

				buttonCalculate.addForward("onClick", this, "onClickCalculate", tbResult);
				String resultTextboxCalcFields = " ";

				if (isMaintain) {
					if (count >= buildCondition.length) {
						tbResult.setVisible(false);
					} else {
						tbResult.setValue(buildCondition[count]);
						count++;
						if (StringUtils.isEmpty(tbResult.getValue())) {
							tbResult.setVisible(false);
						}
					}
					resultTextboxCalcFields = buildCondition[count];
					count++;
				}

				tbResult.setAttribute("calculatedFields", resultTextboxCalcFields);
			} else if (StringUtils.equals(RuleConstants.COMPONENTTYPE_INTEGER, jsRuleReturnType.getComponentType())) {
				Intbox intboxResult = new Intbox(); // Result TextBox
				intboxResult.setId(uUID + "_RESULT" + loopCount);
				intboxResult.setParent(treeCell);
				intboxResult.setWidth("120px");

				String resultIntboxCalcFields = " ";

				if (isMaintain) {
					if (count >= buildCondition.length) {
						intboxResult.setVisible(false);
					} else {
						intboxResult.setText(buildCondition[count]);
						count++;
						if (StringUtils.isEmpty(intboxResult.getText())) {
							intboxResult.setVisible(false);
						}
					}
					resultIntboxCalcFields = buildCondition[count];
				}
				intboxResult.setAttribute("calculatedFields", resultIntboxCalcFields);
			} else if (StringUtils.equals(RuleConstants.COMPONENTTYPE_PERCENTAGE,
					jsRuleReturnType.getComponentType())) {
				Decimalbox deciamlboxResult = new Decimalbox(); // Result TextBox
				deciamlboxResult.setId(uUID + "_RESULT" + loopCount);
				deciamlboxResult.setParent(treeCell);
				deciamlboxResult.setWidth("120px");

				String resultDecimalboxCalcFields = " ";

				if (isMaintain) {
					if (count >= buildCondition.length) {
						deciamlboxResult.setVisible(false);
					} else {
						deciamlboxResult.setText(buildCondition[count]);
						count++;
						if (StringUtils.isEmpty(deciamlboxResult.getText())) {
							deciamlboxResult.setVisible(false);
						}
					}
					resultDecimalboxCalcFields = buildCondition[count];
					count++;
				}
				deciamlboxResult.setAttribute("calculatedFields", resultDecimalboxCalcFields);
			} else if (StringUtils.equals(RuleConstants.COMPONENTTYPE_STRING, jsRuleReturnType.getComponentType())) {
				Textbox tbResult = new Textbox(); // Result TextBox
				tbResult.setId(uUID + "_RESULT" + loopCount);
				tbResult.setParent(treeCell);
				tbResult.setWidth("120px");

				String resultTextboxCalcFields = " ";
				if (isMaintain) {
					if (count >= buildCondition.length) {
						tbResult.setVisible(false);
					} else {
						tbResult.setValue(buildCondition[count]);
						count++;
						if (StringUtils.isEmpty(tbResult.getValue())) {
							tbResult.setVisible(false);
						}
					}
					resultTextboxCalcFields = buildCondition[count];
				}
				tbResult.setAttribute("calculatedFields", resultTextboxCalcFields);
			}

			loopCount++;
		}
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Removing the TreeItem
	 */
	public void onRemoveCondition(ForwardEvent event) {
		logger.debug("Entering");

		List<Treeitem> rmvTreeItems = new ArrayList<>();
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell treeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		Label label = (Label) treeItem.getFellowIfAny(treeCell.getId() + "_statement");

		if (label != null && StringUtils.equalsIgnoreCase(label.getValue(), "IF")) { // IF BLOCK
			Treeitem parentItem = (Treeitem) treeChildren.getParent();
			String parentuUID = parentItem.getTreerow().getChildren().get(0).getId();
			// NESTED IF
			parentItem.getFellowIfAny(parentuUID + "_btn_NESTEDIF").setVisible(true);
			parentItem.getFellowIfAny("space_" + parentuUID + "_btn_NESTEDIF").setVisible(true);
			// RESULT
			if (parentItem.getFellowIfAny(parentuUID + "_RESULT") != null) {
				parentItem.getFellowIfAny(parentuUID + "_RESULT").setVisible(true);
			}
			parentItem.getFellowIfAny(parentuUID + "_btn_CALCULATE").setVisible(false);

			if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
				for (int count = 0; count < this.jsRuleReturnTypeList.size(); count++) {
					parentItem.getFellowIfAny(parentuUID + "_RESULT" + count).setVisible(true);
					if (treeItem.getFellowIfAny(parentuUID + "_btn_CALCULATE" + count) != null) {
						treeItem.getFellowIfAny(parentuUID + "_btn_CALCULATE" + count).setVisible(true);
					}
				}
			} else if (this.ruleType == RuleReturnType.DECIMAL || this.ruleType == RuleReturnType.CALCSTRING) {
				parentItem.getFellowIfAny(parentuUID + "_btn_CALCULATE").setVisible(true);
			}

			List<Treeitem> list = parentItem.getTreechildren().getChildren();
			for (Treeitem ti : list) {
				Treecell trcell = (Treecell) ti.getChildren().get(0).getChildren().get(0);
				if (trcell.getFellowIfAny(trcell.getId() + "_logicalOperator") == null) {
					trcell.getFellowIfAny(trcell.getId() + "_statement");
					rmvTreeItems.add(ti);
				}
			}
		} else {
			rmvTreeItems.add(treeItem);
		}

		for (Treeitem ti : rmvTreeItems) {
			ti.detach();
		}

		logger.debug("Leaving");
	}

	/**
	 * On selecting to add a new Condition
	 */
	public void onClickElseIF(ForwardEvent event) {
		logger.debug("Entering");

		Treeitem treeItem = (Treeitem) event.getData();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		Treeitem newCondition = createNewCondition(null, "ELSE IF");

		if (treeItem.getNextSibling() != null) {
			treeChildren.insertBefore(newCondition, treeItem.getNextSibling());
		} else {
			treeChildren.appendChild(newCondition);
		}

		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();

		doSetButtonProperties(treeChildren, currentuUID, newuUID, "ELSEIF");

		Treechildren parentTreeChildren = (Treechildren) treeItem.getParent();
		Object parentTreeTreeItem = parentTreeChildren.getParent();

		if (parentTreeTreeItem instanceof Treeitem) {
			setTreeSpace(newCondition, (Treeitem) parentTreeTreeItem);
		} else {
			setTreeSpace(newCondition, null);
		}
		logger.debug("Leaving");
	}

	/**
	 * On selecting to add a new Condition
	 */
	public void onClickElse(ForwardEvent event) {
		logger.debug("Entering");

		Treeitem treeItem = (Treeitem) event.getData();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();

		Treeitem newCondition = createNewCondition(null, "ELSE");
		treeChildren.appendChild(newCondition);

		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();

		doSetButtonProperties(treeChildren, currentuUID, newuUID, "ELSE");

		setTreeSpace(newCondition, treeItem);

		logger.debug("Leaving");
	}

	/**
	 * On selecting to add a new sub Condition
	 * 
	 * @param event
	 */
	public void onClickNestedIf(ForwardEvent event) {
		logger.debug("Entering");

		Treechildren treeChildren;
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treeitem ifCondition = createNewCondition(null, "IF");

		if (treeItem.getTreechildren() != null) {
			treeChildren = treeItem.getTreechildren();
		} else {
			treeChildren = new Treechildren();
			treeChildren.setParent(treeItem);
		}

		if (treeItem.getNextSibling() != null) {
			treeChildren.insertBefore(ifCondition, treeItem.getNextSibling());
		} else {
			treeChildren.appendChild(ifCondition);
		}

		String currentuUID = currentTreeCell.getId();
		String newuUID = ifCondition.getTreerow().getChildren().get(0).getId();

		doSetButtonProperties(treeChildren, currentuUID, newuUID, "IF");
		setTreeSpace(ifCondition, treeItem);

		Treeitem elseCondition = setElseCondition(treeChildren, ifCondition);
		setTreeSpace(elseCondition, treeItem);

		logger.debug("Leaving");
	}

	/**
	 * On selecting to add a new sub Condition
	 * 
	 * @param event
	 */
	public void onClickLogicalOperator(ForwardEvent event) {
		logger.debug("Entering");

		Treechildren treeChildren;
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treeitem newCondition = createNewCondition(null, "AND");

		if (treeItem.getTreechildren() != null) {
			treeChildren = treeItem.getTreechildren();
			treeChildren.insertBefore(newCondition, treeChildren.getFirstChild());
		} else {
			treeChildren = new Treechildren();
			treeChildren.setParent(treeItem);
			treeChildren.appendChild(newCondition);
		}

		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();

		doSetButtonProperties(treeChildren, currentuUID, newuUID, "AND");
		setTreeSpace(newCondition, treeItem);

		logger.debug("Leaving");
	}

	/**
	 * set the Button properties
	 * 
	 * @param treeChildren
	 * @param currentuUID
	 * @param newuUID
	 * @param label
	 */
	public void doSetButtonProperties(Treechildren treeChildren, String currentuUID, String newuUID, String label) {
		logger.debug("Entering");

		if (StringUtils.equalsIgnoreCase(label, "ELSEIF")) { // ELSEIF
			// ELSE
			treeChildren.getFellowIfAny(newuUID + "_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_ELSE").setVisible(false);
		} else if (StringUtils.equalsIgnoreCase(label, "ELSE")) { // ELSE
			// ELSEIF
			treeChildren.getFellowIfAny(newuUID + "_btn_ELSEIF").setVisible(false);
			// treeChildren.getFellowIfAny(newuUID + "_leftOperand_space").setVisible(false);
			// ELSE
			treeChildren.getFellowIfAny(newuUID + "_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny(currentuUID + "_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_" + currentuUID + "_btn_ELSE").setVisible(false);
			// AND
			treeChildren.getFellowIfAny(newuUID + "_btn_AND").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_AND").setVisible(false);
		} else if (StringUtils.equalsIgnoreCase(label, "IF")) { // IF
			// NESTED IF
			treeChildren.getFellowIfAny(currentuUID + "_btn_NESTEDIF").setVisible(false);
			treeChildren.getFellowIfAny("space_" + currentuUID + "_btn_NESTEDIF").setVisible(false);
			// CALCULATE
			treeChildren.getFellowIfAny(currentuUID + "_btn_CALCULATE").setVisible(false);
			// if (this.ruleType == RuleReturnType.OBJECT) {
			if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
				for (int count = 0; count < this.jsRuleReturnTypeList.size(); count++) {
					treeChildren.getFellowIfAny(currentuUID + "_RESULT" + count).setVisible(false);
					if (treeChildren.getFellowIfAny(currentuUID + "_btn_CALCULATE" + count) != null) {
						treeChildren.getFellowIfAny(currentuUID + "_btn_CALCULATE" + count).setVisible(false);
					}
				}
			} else {
				treeChildren.getFellowIfAny(currentuUID + "_RESULT").setVisible(false);
			}
		} else if (StringUtils.equalsIgnoreCase(label, "AND")) { // AND
			// ELSE
			treeChildren.getFellowIfAny(newuUID + "_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_ELSE").setVisible(false);
			// ELSE IF
			treeChildren.getFellowIfAny(newuUID + "_btn_ELSEIF").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_ELSEIF").setVisible(false);
			// NESTED IF
			treeChildren.getFellowIfAny(newuUID + "_btn_NESTEDIF").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_NESTEDIF").setVisible(false);
			// CALCULATE
			treeChildren.getFellowIfAny(newuUID + "_btn_CALCULATE").setVisible(false);
			treeChildren.getFellowIfAny("space_" + newuUID + "_btn_CALCULATE").setVisible(false);
			// RESULT TextBox
			// treeChildren.getFellowIfAny(newuUID + "_RESULT").setVisible(false);

			// if (this.ruleType == RuleReturnType.OBJECT) {
			if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
				for (int count = 0; count < this.jsRuleReturnTypeList.size(); count++) {
					treeChildren.getFellowIfAny(newuUID + "_RESULT" + count).setVisible(false);
					if (treeChildren.getFellowIfAny(newuUID + "_btn_CALCULATE" + count) != null) {
						treeChildren.getFellowIfAny(newuUID + "_btn_CALCULATE" + count).setVisible(false);
					}
				}
			} else {
				treeChildren.getFellowIfAny(newuUID + "_RESULT").setVisible(false);
			}

		}

		logger.debug("Leaving");
	}

	/**
	 * onChanging Operand Types
	 * 
	 * @param event
	 */
	public void onChangeOperandType(ForwardEvent event) {
		logger.debug("Entering");

		Combobox operandType = (Combobox) event.getOrigin().getTarget();
		Treecell treeCell = (Treecell) operandType.getParent();
		String uUID = treeCell.getId();

		if (treeCell.getFellowIfAny(treeCell.getId() + "_button") != null) {
			treeCell.getFellowIfAny(treeCell.getId() + "_button").detach();
		}

		if (operandType.getId().endsWith("_rightOperandType")) {
			uUID = uUID + "_rightOperand";
		} else if (operandType.getId().endsWith("_leftOperandType")) {
			if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
				if (treeCell.getFellowIfAny(uUID + "_rightOperandType") != null) {
					Combobox rightOperandType = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
					Combobox operator = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");

					if (operator.getSelectedIndex() > 0) {
						operator.setSelectedIndex(0);
					}

					if (rightOperandType.getSelectedIndex() > 0) {
						rightOperandType.setSelectedIndex(0);
					}
				}

				if (treeCell.getFellowIfAny(treeCell.getId() + "_rightOperand") != null) {
					Component rightOperand = treeCell.getFellowIfAny(treeCell.getId() + "_rightOperand");
					if (rightOperand instanceof Combobox) {
						if (((Combobox) rightOperand).getSelectedIndex() > 0) {
							((Combobox) rightOperand).setSelectedIndex(0);
						}
					} else if (rightOperand instanceof Longbox) {
						((Longbox) rightOperand).setValue((long) 0);
					} else if (rightOperand instanceof Textbox) {
						if (((Textbox) rightOperand).isReadonly()) {
							((Textbox) rightOperand).setReadonly(false);
						}
						((Textbox) rightOperand).setConstraint("");
						((Textbox) rightOperand).setValue("");
					}
				}

				Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");
				String comboBoxValue = comboBox.getSelectedItem().getValue().toString();
				String excludeFields = getExcludeFieldsByOperandType(operandType, "operator");

				fillComboBox(comboBox, comboBoxValue, operatorsList, excludeFields);

				excludeFields = "";
				excludeFields = getExcludeFieldsByOperandType(operandType, "operandType");
				comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
				comboBoxValue = comboBox.getSelectedItem().getValue().toString();

				fillComboBox(comboBox, comboBoxValue, operandTypesList, excludeFields);
			}
			uUID = uUID + "_leftOperand";
		}
		if (treeCell.getFellowIfAny(uUID) != null) {
			treeCell.getFellowIfAny(uUID).detach();

			if (treeCell.getFellowIfAny(uUID + "_space") != null) {
				treeCell.getFellowIfAny(uUID + "_space").detach();
			}

			if (treeCell.getFellowIfAny(uUID + "_calculate") != null) {
				treeCell.getFellowIfAny(uUID + "_calculate").detach();
			}
		}
		createOperand(uUID, operandType, null);

		logger.debug("Leaving");
	}

	/**
	 * Get Exclude Fields by Operand Type
	 * 
	 * @param operandType
	 * @return
	 */
	public String getExcludeFieldsByOperandType(Combobox operandType, String type) {
		logger.debug("Entering");

		String excludeFields = "";
		String operandTypeValue = operandType.getSelectedItem().getValue();

		if (operandType.getSelectedIndex() == 0) {
			// Do nothing
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.STATICTEXT)) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = ", > , >= , < , <= , IN , NOT IN ,";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.CALCVALUE + " , " + RuleConstants.STATICTEXT + " , "
						+ RuleConstants.DBVALUE + " , ";
			}
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.GLOBALVAR)) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = " , IN , NOT IN , ";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.DBVALUE + " , ";
			}
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.FIELDLIST)) {
			if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.DBVALUE + " , ";
			} else if (StringUtils.equals(type, "operator")) {
				excludeFields = " , IN , NOT IN , ";
			}
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.CALCVALUE)) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = " , LIKE , NOT LIKE , IN , NOT IN , ";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.STATICTEXT + " , " + RuleConstants.DBVALUE + " , ";
			}
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.FUNCTION)) {
			// Do nothing
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.SUBQUERY)) {
			// Do nothing
		}

		logger.debug("Leaving");

		return excludeFields;
	}

	/**
	 * onChanging Operand
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	public void onChangeOperand(ForwardEvent event) {
		logger.debug("Entering");

		Combobox operandType = (Combobox) event.getOrigin().getData();
		Component operand = event.getOrigin().getTarget();
		onChangingOperand(operand);

		logger.debug("Leaving");
	}

	public void onChangingOperand(Component operand) {
		logger.debug("Entering");

		Treecell treeCell = (Treecell) operand.getParent();

		if (treeCell.getFellowIfAny(treeCell.getId() + "_button") != null) {
			treeCell.getFellowIfAny(treeCell.getId() + "_button").detach();
		}

		if (operand instanceof Combobox) {
			if (operand.getId().endsWith("_leftOperand")) {
				Combobox operandtype = (Combobox) operand.getAttribute("OperandType");
				String operandTypeValue = operandtype.getSelectedItem().getValue();
				Combobox operator = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_operator");

				if (operator.getSelectedIndex() > 0) {
					operator.setSelectedIndex(0);
				}

				if (treeCell.getFellowIfAny(treeCell.getId() + "_rightOperandType") != null
						&& treeCell.getFellowIfAny(treeCell.getId() + "_rightOperand") != null) {
					Combobox rightOperandType = (Combobox) treeCell
							.getFellowIfAny(treeCell.getId() + "_rightOperandType");
					Component rightOperand = treeCell.getFellowIfAny(treeCell.getId() + "_rightOperand");
					rightOperandType.setSelectedIndex(0);

					if (rightOperand instanceof Combobox) {
						if (((Combobox) rightOperand).getSelectedIndex() > 0) {
							((Combobox) rightOperand).setSelectedIndex(0);
						}
					} else if (rightOperand instanceof Longbox) {
						((Longbox) rightOperand).setValue((long) 0);
					} else if (rightOperand instanceof Textbox) {
						if (((Textbox) rightOperand).isReadonly()) {
							((Textbox) rightOperand).setReadonly(false);
						}
						((Textbox) rightOperand).setConstraint("");
						((Textbox) rightOperand).setValue("");
					}
				}

				if (StringUtils.equals(operandTypeValue, RuleConstants.FIELDLIST)
						&& ((Combobox) operand).getSelectedIndex() != 0) {
					RBFieldDetail fielddetails = (RBFieldDetail) ((Combobox) operand).getSelectedItem()
							.getAttribute("FieldDetails");
					String uUID = treeCell.getId();

					if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
						Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");
						String comboBoxValue = comboBox.getSelectedItem().getValue();
						String excludeFields = getExcludeFieldsByOperands((Combobox) operand, "operator", fielddetails);

						fillComboBox(comboBox, comboBoxValue, operatorsList, excludeFields);

						excludeFields = "";
						excludeFields = getExcludeFieldsByOperands((Combobox) operand, "operandType", fielddetails);
						comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
						comboBoxValue = comboBox.getSelectedItem().getValue();

						if (StringUtils.equals(((Combobox) operand).getSelectedItem().getValue(), "fm_ClosureType")) {
							excludeFields = " , " + RuleConstants.CALCVALUE + " , " + RuleConstants.STATICTEXT + " , "
									+ RuleConstants.DBVALUE + " , ";
						}

						fillComboBox(comboBox, comboBoxValue, operandTypesList, excludeFields);
					}
				} else if (StringUtils.equals(operandTypeValue, RuleConstants.GLOBALVAR)
						&& ((Combobox) operand).getSelectedIndex() != 0) {
					GlobalVariable globalVariable = (GlobalVariable) ((Combobox) operand).getSelectedItem()
							.getAttribute("GlobalVariableDetails");
					String uUID = treeCell.getId();

					if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
						Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");
						String comboBoxValue = comboBox.getSelectedItem().getValue();
						String excludeFields = getGlobalExcludeFieldsByOperands((Combobox) operand, "operator",
								globalVariable);

						fillComboBox(comboBox, comboBoxValue, operatorsList, excludeFields);

						excludeFields = "";
						excludeFields = getGlobalExcludeFieldsByOperands((Combobox) operand, "operandType",
								globalVariable);
						comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
						comboBoxValue = comboBox.getSelectedItem().getValue();

						fillComboBox(comboBox, comboBoxValue, operandTypesList, excludeFields);
					}
				}
			} else if (operand.getId().endsWith("_rightOperand")) {
				// Do nothing
			}
		} else if (operand instanceof Textbox) {
			// Do nothing
		} else if (operand instanceof Longbox) {
			// Do nothing
		}

		logger.debug("Leaving");
	}

	public String getExcludeFieldsByOperands(Combobox operand, String type, RBFieldDetail fielddetails) {
		logger.debug("Entering");

		String excludeFields = "";
		String fieldType = fielddetails.getRbFldType();
		Comboitem fieldlist = operand.getSelectedItem();
		RBFieldDetail fieldDetails = (RBFieldDetail) fieldlist.getAttribute("FieldDetails");

		if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.DECIMAL))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.BIGINT))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NUMERIC))) {
			if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.STATICTEXT + " , ";
			}
		} else if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NVARCHAR))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NCHAR))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.VARCHAR))) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = " , > , >= , < , <= , ";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.CALCVALUE + " , ";
			}
		} else if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.DATETIME))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.SMALLDATETIME))) {
			if (StringUtils.equals(type, "operandType")) {
				excludeFields = " , " + RuleConstants.CALCVALUE + " , ";
			}
		}

		if (StringUtils.isBlank(fieldDetails.getModuleCode())) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields += " , IN , NOT IN , ";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = excludeFields + " , " + RuleConstants.DBVALUE + " ,";
			}
		}

		logger.debug("Leaving");

		return excludeFields;
	}

	public String getGlobalExcludeFieldsByOperands(Combobox operand, String type, GlobalVariable globalVariable) {
		logger.debug("Entering");

		String excludeFields = "";
		String fieldType = globalVariable.getType();

		if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.DECIMAL))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.BIGINT))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NUMERIC))) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = ", LIKE , NOT LIKE ,";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = "," + RuleConstants.STATICTEXT + "," + RuleConstants.SUBQUERY + ","
						+ RuleConstants.DBVALUE + ",";
			}
		} else if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NVARCHAR))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.NCHAR))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.VARCHAR))) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = ", > , >= , < , <= ,";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = "," + RuleConstants.CALCVALUE + "," + RuleConstants.SUBQUERY + ","
						+ RuleConstants.DBVALUE + ",";
			}
		} else if ((StringUtils.equalsIgnoreCase(fieldType, PennantConstants.DATETIME))
				|| (StringUtils.equalsIgnoreCase(fieldType, PennantConstants.SMALLDATETIME))) {
			if (StringUtils.equals(type, "operator")) {
				excludeFields = "";
			} else if (StringUtils.equals(type, "operandType")) {
				excludeFields = "," + RuleConstants.SUBQUERY + "," + RuleConstants.CALCVALUE + ","
						+ RuleConstants.STATICTEXT + "," + RuleConstants.DBVALUE + ",";
			}
		}

		logger.debug("Leaving");

		return excludeFields;
	}

	/**
	 * onChanging Operator
	 * 
	 * @param event
	 */
	public void onChangeOperator(ForwardEvent event) {
		logger.debug("Entering");

		Combobox operator = (Combobox) event.getOrigin().getTarget();
		onChangingOperator(operator);

		logger.debug("Leaving");
	}

	public void onChangingOperator(Combobox operator) {
		logger.debug("Entering");

		Treecell treeCell = (Treecell) operator.getParent();
		String uUID = treeCell.getId();

		if (treeCell.getFellowIfAny(uUID + "_button") != null) {
			treeCell.getFellowIfAny(uUID + "_button").detach();
		}

		if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
			Combobox rightOperandType = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
			Combobox leftOperandType = (Combobox) treeCell.getFellowIfAny(uUID + "_leftOperandType");
			Component rightOperand = treeCell.getFellowIfAny(uUID + "_rightOperand");
			Component leftOperand = treeCell.getFellowIfAny(uUID + "_leftOperand");

			rightOperandType.setSelectedIndex(0);
			String rightOperandTypeValue = rightOperandType.getSelectedItem().getValue();

			if (rightOperand instanceof Combobox) {
				((Combobox) rightOperand).setConstraint("");
				((Combobox) rightOperand).setValue("");
			} else if (rightOperand instanceof Textbox) {
				((Textbox) rightOperand).setConstraint("");
				((Textbox) rightOperand).setValue("");

				if (((Textbox) rightOperand).isReadonly()) {
					((Textbox) rightOperand).setReadonly(false);
				}

				if (StringUtils.equals(rightOperandTypeValue, RuleConstants.DBVALUE)) {
					((Textbox) rightOperand).setReadonly(true);
				}
			} else if (rightOperand instanceof Longbox) {
				((Longbox) rightOperand).setConstraint("");
				((Longbox) rightOperand).setValue((long) 0);
			}

			String excludeFields = getExcludeFieldsByOperator(operator, leftOperand, leftOperandType);
			fillComboBox(rightOperandType, rightOperandTypeValue, operandTypesList, excludeFields);
		}

		logger.debug("Leaving");
	}

	/**
	 * Get the Excludefields for rightOperandType
	 * 
	 * @param operator(Combobox),leftOperand(Component)
	 * @return excludeFields(String)
	 */
	public String getExcludeFieldsByOperator(Combobox operator, Component leftOperand, Combobox leftOperandType) {
		logger.debug("Entering");

		String excludeFields = "";
		String selectedOperator = operator.getSelectedItem().getLabel();

		try {
			String leftOperandTooltip = "";
			if (operator.getSelectedIndex() == 0) {
				// Do nothing
			} else if (StringUtils.equals(selectedOperator, Labels.getLabel("EQUALS_LABEL"))
					|| StringUtils.equals(selectedOperator, Labels.getLabel("NOTEQUAL_LABEL"))) {
				String leftOperandTypeValue = leftOperandType.getSelectedItem().getValue();

				if (leftOperand instanceof Combobox) {
					if (StringUtils.equals(leftOperandTypeValue, RuleConstants.FIELDLIST)) {
						if (((Combobox) leftOperand).getSelectedIndex() != 0) {
							leftOperandTooltip = ((Combobox) leftOperand).getSelectedItem().getTooltiptext()
									.toLowerCase();
							RBFieldDetail fielddetails = (RBFieldDetail) ((Combobox) leftOperand).getSelectedItem()
									.getAttribute("FieldDetails");

							String rbFldType = fielddetails.getRbFldType().toLowerCase();
							excludeFields = " , " + RuleConstants.SUBQUERY;

							if (StringUtils.equals(rbFldType, PennantConstants.NCHAR)
									|| StringUtils.equals(rbFldType, PennantConstants.NVARCHAR)
									|| StringUtils.equals(rbFldType, PennantConstants.VARCHAR)
									|| StringUtils.equals(rbFldType, PennantConstants.DATETIME)
									|| StringUtils.equals(rbFldType, PennantConstants.SMALLDATETIME)) {
								excludeFields += " , " + RuleConstants.CALCVALUE + " , ";
							} else if (StringUtils.equals(rbFldType, PennantConstants.INT)
									|| StringUtils.equals(rbFldType, PennantConstants.BIGINT)
									|| StringUtils.equals(rbFldType, PennantConstants.NUMERIC)
									|| StringUtils.equals(rbFldType, PennantConstants.DECIMAL)) {
								excludeFields += " , " + RuleConstants.STATICTEXT + " , ";
							}

							if (StringUtils.isBlank(fielddetails.getModuleCode())) {
								excludeFields += " , " + RuleConstants.DBVALUE + " , ";
							}
						}
					} else if (StringUtils.equals(leftOperandTypeValue, RuleConstants.GLOBALVAR)) {
						excludeFields = " , " + RuleConstants.DBVALUE + " , " + RuleConstants.SUBQUERY + " , ";
						if (((Combobox) leftOperand).getSelectedIndex() != 0) {
							// GlobalVariable globalVariable=(GlobalVariable) ((Combobox)
							// leftOperand).getSelectedItem().getAttribute("GlobalVariableDetails");
							leftOperandTooltip = ((Combobox) leftOperand).getSelectedItem().getTooltiptext()
									.toLowerCase();

							if (StringUtils.contains(leftOperandTooltip, PennantConstants.NCHAR)
									|| StringUtils.contains(leftOperandTooltip, PennantConstants.NVARCHAR)
									|| StringUtils.contains(leftOperandTooltip, PennantConstants.VARCHAR)) {
								excludeFields += " , " + RuleConstants.CALCVALUE + " , ";
							} else if (StringUtils.contains(leftOperandTooltip, PennantConstants.DATETIME)
									|| StringUtils.contains(leftOperandTooltip, PennantConstants.SMALLDATETIME)) {
								excludeFields += " , " + RuleConstants.STATICTEXT + " , " + RuleConstants.CALCVALUE
										+ " , ";
							} else if (StringUtils.contains(leftOperandTooltip, PennantConstants.NUMERIC)
									|| StringUtils.contains(leftOperandTooltip, PennantConstants.BIGINT)
									|| StringUtils.contains(leftOperandTooltip, PennantConstants.DECIMAL)
									|| StringUtils.contains(leftOperandTooltip, PennantConstants.INT)) {
								excludeFields += " , " + RuleConstants.STATICTEXT + " , ";
							}
						}
					}
				} else if (StringUtils.equals(leftOperandTypeValue, RuleConstants.STATICTEXT)
						|| StringUtils.equals(leftOperandTypeValue, RuleConstants.CALCVALUE)) {
					excludeFields = " , " + RuleConstants.CALCVALUE + " , " + RuleConstants.STATICTEXT + " , "
							+ RuleConstants.DBVALUE + " , ";
				}
			} else if (StringUtils.equals(selectedOperator, Labels.getLabel("GREATER_LABEL"))
					|| StringUtils.equals(selectedOperator, Labels.getLabel("GREATEREQUAL_LABEL"))
					|| StringUtils.equals(selectedOperator, Labels.getLabel("LESS_LABEL"))
					|| StringUtils.equals(selectedOperator, Labels.getLabel("LESSEQUAL_LABEL"))) {
				excludeFields = " , " + RuleConstants.STATICTEXT + " , " + RuleConstants.DBVALUE + " , ";
				if (leftOperand instanceof Combobox) {
					if (((Combobox) leftOperand).getSelectedIndex() != 0) {
						leftOperandTooltip = ((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase();

						if (StringUtils.contains(leftOperandTooltip, PennantConstants.DATETIME)
								|| StringUtils.contains(leftOperandTooltip, PennantConstants.SMALLDATETIME)) {
							excludeFields += " , " + RuleConstants.CALCVALUE + " , ";
						}
					}
				}
				if (StringUtils.equals(RuleConstants.MODULE_DUEDATERULE, module)) {
					excludeFields = RuleConstants.DBVALUE;
				}
			} else if (StringUtils.equals(selectedOperator, Labels.getLabel("IN_LABEL"))
					|| StringUtils.equals(selectedOperator, Labels.getLabel("NOTIN_LABEL"))) {
				excludeFields = " , " + RuleConstants.FIELDLIST + " , ";

				String leftOperandTypeValue = leftOperandType.getSelectedItem().getValue();

				if (leftOperand instanceof Combobox) {
					if (StringUtils.equals(leftOperandTypeValue, RuleConstants.FIELDLIST)) {
						if (((Combobox) leftOperand).getSelectedIndex() != 0) {
							leftOperandTooltip = ((Combobox) leftOperand).getSelectedItem().getTooltiptext()
									.toLowerCase();
							RBFieldDetail fielddetails = (RBFieldDetail) ((Combobox) leftOperand).getSelectedItem()
									.getAttribute("FieldDetails");

							String rbFldType = fielddetails.getRbFldType().toLowerCase();

							if (StringUtils.equals(rbFldType, PennantConstants.NCHAR)
									|| StringUtils.equals(rbFldType, PennantConstants.NVARCHAR)
									|| StringUtils.equals(rbFldType, PennantConstants.VARCHAR)
									|| StringUtils.equals(rbFldType, PennantConstants.DATETIME)
									|| StringUtils.equals(rbFldType, PennantConstants.SMALLDATETIME)) {
								excludeFields += " , " + RuleConstants.CALCVALUE + " , ";
							} else if (StringUtils.equals(rbFldType, PennantConstants.INT)
									|| StringUtils.equals(rbFldType, PennantConstants.BIGINT)
									|| StringUtils.equals(rbFldType, PennantConstants.NUMERIC)
									|| StringUtils.equals(rbFldType, PennantConstants.DECIMAL)) {
								excludeFields += " , " + RuleConstants.STATICTEXT + " , ";
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");

		return excludeFields;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ GUI OPERATIONS ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Creating a new Operand based on Operand Type
	 */
	private Component createOperand(String uUID, Combobox operandType, String value) {
		logger.debug("Entering");

		space = new Space();
		space.setId(uUID + "_space");
		space.setWidth("5px");
		String operandTypeValue = operandType.getSelectedItem().getValue();

		if (operandType.getSelectedIndex() == 0) {
			Combobox operand = new Combobox();
			operand.setId(uUID);
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setReadonly(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
			return operand;
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.STATICTEXT)) {
			Textbox operand = new Textbox();
			operand.setId(uUID);
			operand.setFocus(true);
			operand.setValue(value);
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
			return operand;
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.GLOBALVAR)) { // GLOBALVAR
			Combobox operand = new Combobox();
			operand.setId(uUID);

			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);

			if (operand.getId().endsWith("_leftOperand")) {
				for (int i = 0; i < globalVariableList.size(); i++) {
					GlobalVariable globalVariable = globalVariableList.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(globalVariable.getName());
					comboitem.setValue(globalVariable.getName());
					comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
					comboitem.setAttribute("GlobalVariableDetails", globalVariable);
					comboitem.setAttribute("OperandType", operandTypeValue);
					operand.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getName()))) {
						operand.setSelectedItem(comboitem);
					}
				}
			} else if (operand.getId().endsWith("_rightOperand")) {
				Treecell treeCell = (Treecell) operandType.getParent();
				Combobox leftOperandType = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperandType");

				if (RuleConstants.GLOBALVAR.equals(leftOperandType.getSelectedItem().getValue())) {
					for (int i = 0; i < globalVariableList.size(); i++) {
						GlobalVariable globalVariable = globalVariableList.get(i);
						comboitem = new Comboitem();
						comboitem.setLabel(globalVariable.getName());
						comboitem.setValue(globalVariable.getName());
						comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
						comboitem.setAttribute("GlobalVariableDetails", globalVariable);
						comboitem.setAttribute("OperandType", operandTypeValue);
						operand.appendChild(comboitem);
						if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getName()))) {
							operand.setSelectedItem(comboitem);
						}
					}
				}

				if (treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand") instanceof Combobox) {
					Combobox leftOperand = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
					if (globalVariableList != null && globalVariableList.size() > 0) {
						for (int i = 0; i < globalVariableList.size(); i++) {
							GlobalVariable globalVariable = globalVariableList.get(i);
							comboitem = new Comboitem();

							if (leftOperand.getSelectedIndex() > 0) {
								if (StringUtils.containsIgnoreCase(leftOperand.getSelectedItem().getTooltiptext(),
										globalVariable.getType())) {
									comboitem.setLabel(globalVariable.getName());
									comboitem.setValue(globalVariable.getName());
									comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
									comboitem.setAttribute("GlobalVariableDetails", globalVariable);
									comboitem.setAttribute("OperandType", operandTypeValue);
									operand.appendChild(comboitem);
									operand.setAttribute("GlobalVariableDetails", globalVariable);
									if (StringUtils.trimToEmpty(value)
											.equals(StringUtils.trimToEmpty(globalVariable.getName()))) {
										operand.setSelectedItem(comboitem);
									}
								}
							}
						}
					}
				} else {
					String charDataTypes = "varchar,nvarchar,nchar,char";

					if (globalVariableList != null && globalVariableList.size() > 0) {
						for (int i = 0; i < globalVariableList.size(); i++) {
							GlobalVariable globalVariable = globalVariableList.get(i);
							comboitem = new Comboitem();

							if (charDataTypes.contains(globalVariable.getType())) {
								comboitem.setLabel(globalVariable.getName());
								comboitem.setLabel(globalVariable.getName());
								comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
								comboitem.setAttribute("GlobalVariableDetails", globalVariable);
								comboitem.setAttribute("OperandType", operandTypeValue);
								operand.appendChild(comboitem);
								operand.setAttribute("GlobalVariableDetails", globalVariable);
								if (StringUtils.trimToEmpty(value)
										.equals(StringUtils.trimToEmpty(globalVariable.getName()))) {
									operand.setSelectedItem(comboitem);
								}
							}
						}
					}
				}
			}

			operand.setWidth("190px");
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setReadonly(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			return operand;
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.FIELDLIST)) {
			if (objectFieldList == null || objectFieldList.isEmpty()) {
				this.objectFieldList = PennantAppUtil.getExtendedFieldForRules(getModule(),
						PennantAppUtil.getRBFieldDetails(getModule(), getEvent()));
			}

			Combobox operand = new Combobox();
			operand.setId(uUID);
			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);

			if (operand.getId().endsWith("_leftOperand")) {
				for (int i = 0; i < this.objectFieldList.size(); i++) {

					RBFieldDetail fieldDetails = this.objectFieldList.get(i);

					comboitem = new Comboitem();
					comboitem.setLabel(fieldDetails.getRbFldName() + "  -  " + fieldDetails.getRbFldDesc());
					comboitem.setValue(fieldDetails.getRbFldName());
					comboitem.setTooltiptext("Data Type :" + fieldDetails.getRbFldType().toUpperCase());
					comboitem.setAttribute("FieldDetails", fieldDetails);
					comboitem.setAttribute("OperandType", operandType);
					operand.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
						operand.setSelectedItem(comboitem);
					}
				}
			} else if (operand.getId().endsWith("_rightOperand")) {
				Treecell treeCell = (Treecell) operandType.getParent();
				Combobox leftOperandType = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperandType");
				String leftOperandTypeValue = leftOperandType.getSelectedItem().getValue();

				if (value != null) {
					if (" ".equals(value)) {
						operand.setVisible(false);
					}
				}

				if (StringUtils.equals(leftOperandTypeValue, RuleConstants.GLOBALVAR)) {
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = this.objectFieldList.get(i);

						comboitem = new Comboitem();
						comboitem.setLabel(fieldDetails.getRbFldName() + "  -  " + fieldDetails.getRbFldDesc());
						comboitem.setValue(fieldDetails.getRbFldName());
						comboitem.setTooltiptext("Data Type :" + fieldDetails.getRbFldType().toUpperCase());
						comboitem.setAttribute("FieldDetails", fieldDetails);
						// comboitem.setAttribute("OperandType",operandType);
						operand.appendChild(comboitem);
						if (StringUtils.trimToEmpty(value)
								.equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
							operand.setSelectedItem(comboitem);
						}
					}
				} else if (StringUtils.equals(leftOperandTypeValue, RuleConstants.CALCVALUE)) {
					String numericDataTypes = "int,bigint,numeric,decimal";
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = this.objectFieldList.get(i);

						if (numericDataTypes.contains(fieldDetails.getRbFldType())) {
							comboitem = new Comboitem();
							comboitem.setLabel(fieldDetails.getRbFldName() + "  -  " + fieldDetails.getRbFldDesc());
							comboitem.setValue(fieldDetails.getRbFldName());
							comboitem.setTooltiptext("Data Type :" + fieldDetails.getRbFldType().toUpperCase());
							comboitem.setAttribute("FieldDetails", fieldDetails);
							// comboitem.setAttribute("OperandType",operandType);
							operand.appendChild(comboitem);
							if (StringUtils.trimToEmpty(value)
									.equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
								operand.setSelectedItem(comboitem);
							}
						}
					}
				}

				if (treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand") instanceof Combobox) {
					Combobox leftOperand = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
					String selectedItem = leftOperand.getSelectedItem().getValue();

					if (StringUtils.equals(selectedItem, "fm_ClosureType")) {
						for (int i = 0; i < this.closureTypeList.size(); i++) {
							ValueLabel closureDetails = this.closureTypeList.get(i);
							String closureDetailsValue = closureDetails.getValue();

							comboitem = new Comboitem();

							if (leftOperand.getSelectedIndex() != 0) {
								comboitem.setLabel(closureDetails.getLabel());
								comboitem.setValue(closureDetails.getValue());
								operand.appendChild(comboitem);

								if (StringUtils.trimToEmpty(value).equals(closureDetailsValue)) {
									operand.setSelectedItem(comboitem);
								}
							}
						}
					} else if (StringUtils.equals(selectedItem, "PrvLetterCourierDeliveryStatus")) {
						for (int i = 0; i < this.courierStatusList.size(); i++) {
							ValueLabel courierStatus = this.courierStatusList.get(i);
							String courierStatusVaue = courierStatus.getValue();

							comboitem = new Comboitem();

							if (leftOperand.getSelectedIndex() != 0) {
								comboitem.setLabel(courierStatus.getLabel());
								comboitem.setValue(courierStatus.getValue());
								operand.appendChild(comboitem);

								if (StringUtils.trimToEmpty(value).equals(courierStatusVaue)) {
									operand.setSelectedItem(comboitem);
								}
							}
						}
					} else {
						for (int i = 0; i < this.objectFieldList.size(); i++) {
							RBFieldDetail fieldDetails = this.objectFieldList.get(i);

							comboitem = new Comboitem();

							if (leftOperand.getSelectedIndex() != 0) {

								if (leftOperand.getSelectedItem().getTooltiptext()
										.contains(fieldDetails.getRbFldType().toUpperCase())) {
									comboitem.setLabel(
											fieldDetails.getRbFldName() + "  -  " + fieldDetails.getRbFldDesc());
									comboitem.setValue(fieldDetails.getRbFldName());
									comboitem.setTooltiptext("Data Type :" + fieldDetails.getRbFldType().toUpperCase());
									comboitem.setAttribute("FieldDetails", fieldDetails);
									operand.appendChild(comboitem);
									if (StringUtils.trimToEmpty(value)
											.equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
										operand.setSelectedItem(comboitem);
									}
								}
							}
						}
					}
				} else {
					String charDataTypes = "varchar,nvarchar,nchar,char";
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = this.objectFieldList.get(i);

						comboitem = new Comboitem();
						if (charDataTypes.contains(fieldDetails.getRbFldType())) {
							comboitem.setLabel(fieldDetails.getRbFldName() + "  -  " + fieldDetails.getRbFldDesc());
							comboitem.setValue(fieldDetails.getRbFldName());
							comboitem.setTooltiptext("Data Type :" + fieldDetails.getRbFldType().toUpperCase());
							comboitem.setAttribute("FieldDetails", fieldDetails);
							// comboitem.setAttribute("OperandType",operandType);
							operand.appendChild(comboitem);
							if (StringUtils.trimToEmpty(value)
									.equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
								operand.setSelectedItem(comboitem);
							}
						}
					}
				}
			}
			operand.setReadonly(true);
			operand.setWidth("190px");
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
			return operand;
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.CALCVALUE)) {// Calculation
			Label calculate = new Label();
			calculate.setTooltiptext("Click to add a formula");
			calculate.setValue("CALCULATE");
			calculate.setId(uUID + "_calculate");
			calculate.setSclass("button_Label");

			Textbox operand = new Textbox();
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(calculate, space);
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setId(uUID);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
			operand.setValue(StringUtils.trimToEmpty(value));
			calculate.addForward("onClick", this, "onClickCalculate", operand);
			return operand;
		} else if (StringUtils.equals(operandTypeValue, RuleConstants.DBVALUE)) { // DBVALUE
			Textbox operand = new Textbox();
			operand.setId(uUID);
			operand.setReadonly(true);

			if (value != null) {
				if ((" ".equals(value)) || ("".equals(value))) {
					operand.setVisible(false);
				}
			}

			if (value != null && value.contains("(")) {
				operand.setValue(value.replace("(", "").replace(")", ""));
			} else {
				operand.setValue(value);
			}

			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			Space space1 = new Space();
			space1.setWidth("5px");
			Treecell treeCell = (Treecell) operandType.getParent();
			Button btnSearch = new Button();
			btnSearch.setLabel("Search");
			btnSearch.setId(treeCell.getId() + "_button");

			operandType.getParent().insertBefore(btnSearch, space);
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operandType.getParent().insertBefore(operand, btnSearch);
			operandType.getParent().insertBefore(space1, btnSearch);
			operand.setAttribute("OperandType", operandType);
			btnSearch.setAttribute("OperandType", operandType);
			btnSearch.setAttribute("Operand", operand);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			btnSearch.addForward("onClick", this, "onSearchButtonClick", operand);
			return operand;
		}

		logger.debug("Leaving");
		return null;
	}

	/**
	 * onClicking SearchButton
	 * 
	 * @param event
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 */
	public void onSearchButtonClick(ForwardEvent event) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		logger.debug("Entering");

		Component button = event.getOrigin().getTarget();
		Textbox lovText = (Textbox) button.getAttribute("Operand");
		Combobox operandType = (Combobox) button.getAttribute("OperandType");
		Treecell treeCell = (Treecell) operandType.getParent();
		Combobox leftOperand = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
		RBFieldDetail fielddetails = (RBFieldDetail) leftOperand.getSelectedItem().getAttribute("FieldDetails");

		if (fielddetails == null) {
			return;
		}

		Combobox operator = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_operator");
		String moduleCode = fielddetails.getModuleCode();
		String operatorLabel = operator.getSelectedItem().getLabel();

		if (StringUtils.equals(operatorLabel, Labels.getLabel("IN_LABEL"))
				|| StringUtils.equals(operatorLabel, Labels.getLabel("NOTIN_LABEL"))) {
			Map<String, Object> selectedValues = setSelectedValuesMap(lovText.getValue());

			Object dataObject = ExtendedMultipleSearchListBox.show(this, moduleCode, selectedValues);

			if (dataObject instanceof String) {
				lovText.setValue(dataObject.toString());
			} else {
				@SuppressWarnings("unchecked")
				Map<String, Object> details = (Map<String, Object>) dataObject;

				if (details != null) {
					String multivalues = details.keySet().toString();
					lovText.setValue(multivalues.replace("[", "").replace("]", " "));
				}
			}
		} else {
			Object dataObject = ExtendedSearchListBox.show(this, moduleCode);

			if (dataObject instanceof String) {
				lovText.setValue(dataObject.toString());
			} else {
				ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(fielddetails.getModuleCode());
				String fieldValue = "";
				String fieldString = moduleMapping.getLovFields()[0];
				String fieldMethod = "get" + fieldString.substring(0, 1).toUpperCase() + fieldString.substring(1);

				if (dataObject != null) {
					if (dataObject.getClass().getMethod(fieldMethod).getReturnType().equals(String.class)) {
						fieldValue = (String) dataObject.getClass().getMethod(fieldMethod).invoke(dataObject);
					} else {
						fieldValue = dataObject.getClass().getMethod(fieldMethod).invoke(dataObject).toString();
					}

					if (StringUtils.isNotBlank(fieldValue)) {
						lovText.setValue(fieldValue);
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	private Map<String, Object> setSelectedValuesMap(String code) {
		logger.debug("Entering");

		Map<String, Object> setValues = new HashMap<String, Object>();

		if (StringUtils.isNotBlank(code)) {
			for (String value : code.split(",")) {
				setValues.put(value.trim(), value.trim());
			}
		}

		logger.debug("Leaving");

		return setValues;
	}

	/**
	 * Get Query from the tree
	 * 
	 * @param treeChildren
	 * @return
	 */
	public String getQuery(Treechildren treeChildren) {
		logger.debug("Entering");

		Treecell treeCell;
		Treeitem treeItem;
		String uUID = "";
		if (treeChildren != null) {
			List<Component> treeItems = treeChildren.getChildren();
			List<String> objectResultList = null;
			List<String> queryResultList = null;

			for (int i = 0; i < treeItems.size(); i++) {
				treeItem = (Treeitem) treeItems.get(i);
				treeCell = (Treecell) treeItem.getTreerow().getChildren().get(0);
				uUID = treeCell.getId();
				boolean elseCondition = false;
				boolean flag = true;

				// Remove Button
				Button buttonRemove = (Button) treeCell.getFellowIfAny(uUID + "_RmvCond");
				if (!buttonRemove.isDisabled()) {
					actualBlock += "~";
				}

				Component labelStatement = treeCell.getFellowIfAny(uUID + "_statement");
				String statementValue = "";
				if (labelStatement != null) {
					// IF/ELSE IF/ELSE
					statementValue = getOperandValue(labelStatement);

					if (StringUtils.equalsIgnoreCase(statementValue, "ELSE")) {
						actualBlock += "~";
						elseCondition = true;
					} else {
						for (int j = this.spaceCount; j > 0; j--) {
							this.query += "\t\t";
						}
					}

					actualBlock += "(" + "|";
					actualBlock += statementValue + "|";
				} else {
					// AND/OR
					Combobox comboLogicalOperator = (Combobox) treeCell.getFellowIfAny(uUID + "_logicalOperator");
					this.query = this.query.substring(0, this.query.length() - 4); // For removing the opening braces
					logicCount++;
					this.spaceCount--;
					statementValue = getOperandValue(comboLogicalOperator);
					actualBlock += "(" + "|";
					actualBlock += comboLogicalOperator.getSelectedItem().getLabel() + "|";
					flag = false;
				}

				this.query += " " + statementValue;

				if (!elseCondition) {
					this.query += " (";

					// Left Operand Type
					Component comboLeftOperandType = treeCell.getFellowIfAny(uUID + "_leftOperandType");
					String leftOperandTypeValue = getOperandValue(comboLeftOperandType);

					actualBlock += leftOperandTypeValue + "|";

					// Left Operand
					Component leftOperand = treeCell.getFellowIfAny(uUID + "_leftOperand");
					String leftOperandValue = getOperandValue(leftOperand);
					actualBlock += leftOperandValue + "|";

					if (StringUtils.isNotBlank(leftOperandValue)
							&& StringUtils.equalsIgnoreCase(leftOperandTypeValue, RuleConstants.FIELDLIST)) {
						fieldsSet.add(leftOperandValue);
					}

					// Left Operand Calculation Fields
					String leftOperandCalculatedFields = " ";
					if (StringUtils.equals(RuleConstants.STATICTEXT, leftOperandTypeValue)) {
						leftOperandValue = "'" + leftOperandValue + "'";
					} else if (StringUtils.equals(RuleConstants.CALCVALUE, leftOperandTypeValue)) {
						if (leftOperand.getAttribute("calculatedFields") != null) {
							leftOperandCalculatedFields = (String) leftOperand.getAttribute("calculatedFields");
						}
					}
					actualBlock += leftOperandCalculatedFields + "|";

					for (String calculateField : leftOperandCalculatedFields.split(",")) {
						fieldsSet.add(calculateField);
					}

					// Operator
					Component comboLogicalOperator = treeCell.getFellowIfAny(uUID + "_operator");
					String logicalOperatorValue = getOperandValue(comboLogicalOperator);
					actualBlock += logicalOperatorValue + "|";

					// Right Operand Type
					Component rightOperandType = treeCell.getFellowIfAny(uUID + "_rightOperandType");
					String rightOperandTypeValue = getOperandValue(rightOperandType);
					actualBlock += rightOperandTypeValue + "|";

					// Right Operand
					Component rightOperand = treeCell.getFellowIfAny(uUID + "_rightOperand");
					String rightOperandValue = getOperandValue(rightOperand);
					actualBlock += rightOperandValue + "|";

					if (StringUtils.equals(rightOperandTypeValue, RuleConstants.FIELDLIST)) {
						fieldsSet.add(rightOperandValue);
					}

					// IN Operator/ NOT IN Operator
					if (StringUtils.equals(logicalOperatorValue, Labels.getLabel("IN_LABEL"))
							|| StringUtils.equals(logicalOperatorValue, Labels.getLabel("NOTIN_LABEL"))) {
						String[] values = rightOperandValue.split(",");

						for (int j = 0; j < values.length; j++) {
							if (j > 0) {
								this.query += " || ";
							} else {
								this.query += " ( ";
							}
							// TODO Numeric values to be developed
							this.query += leftOperandValue
									+ (logicalOperatorValue.equals(Labels.getLabel("IN_LABEL")) ? " == " : " != ") + "'"
									+ values[j].trim() + "'";
						}
						this.query += " ) ";
					} else {
						if (StringUtils.equals(rightOperandTypeValue, RuleConstants.STATICTEXT)
								|| StringUtils.equals(rightOperandTypeValue, RuleConstants.DBVALUE)
								|| StringUtils.equals("fm_ClosureType", leftOperandValue)
								|| StringUtils.equals("PrvLetterCourierDeliveryStatus", leftOperandValue)) {
							rightOperandValue = "'" + rightOperandValue + "'";
						}

						this.query += leftOperandValue + " ";
						this.query += logicalOperatorValue + " ";
						this.query += rightOperandValue;
					}

					// Right Operand Calculation Fields
					String rightOperandCalculatedFields = " ";
					if (rightOperand.getAttribute("calculatedFields") != null) {
						rightOperandCalculatedFields = (String) rightOperand.getAttribute("calculatedFields");
					}
					actualBlock += rightOperandCalculatedFields + "|";

					for (String calculateField : rightOperandCalculatedFields.split(",")) {
						fieldsSet.add(calculateField);
					}
				}

				// AND Button
				Label buttonAnd = (Label) treeCell.getFellowIfAny(uUID + "_btn_AND");
				if (buttonAnd.isVisible()) {
					actualBlock += "1|";
				} else {
					actualBlock += "0|";
				}

				// ELSE Button
				Label buttonElse = (Label) treeCell.getFellowIfAny(uUID + "_btn_ELSE");
				if (buttonElse.isVisible()) {
					actualBlock += "1|";
				} else {
					actualBlock += "0|";
				}

				// ELSE IF Button
				Label buttonElseIf = (Label) treeCell.getFellowIfAny(uUID + "_btn_ELSEIF");
				if (buttonElseIf.isVisible()) {
					actualBlock += "1|";
				} else {
					actualBlock += "0|";
				}

				// NESTED IF Button
				Label buttonNestedIf = (Label) treeCell.getFellowIfAny(uUID + "_btn_NESTEDIF");
				if (buttonNestedIf.isVisible()) {
					actualBlock += "1|";
				} else {
					actualBlock += "0|";
				}

				// CALCULATE Button
				Label buttonCalculate = (Label) treeCell.getFellowIfAny(uUID + "_btn_CALCULATE");
				if (buttonCalculate.isVisible()) {
					actualBlock += "1|";
				} else {
					actualBlock += "0|";
				}

				// RESULT
				Component resultComponent = treeCell.getFellowIfAny(uUID + "_RESULT");
				String resultValue = null;
				objectResultList = new ArrayList<String>();

				if (resultComponent != null && resultComponent.isVisible()) {
					// Result Calculation Fields
					String resultCalculatedFields = " ";
					resultValue = getOperandValue(resultComponent);

					if (resultComponent.getAttribute("calculatedFields") != null) {
						resultCalculatedFields = (String) resultComponent.getAttribute("calculatedFields");
					}

					actualBlock += resultValue + "|";
					actualBlock += resultCalculatedFields + "|";

					for (String calculateField : resultCalculatedFields.split(",")) {
						fieldsSet.add(calculateField);
					}
					// } else if (this.ruleType == RuleReturnType.OBJECT) {
				} else if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
					for (int count = 0; count < this.jsRuleReturnTypeList.size(); count++) {
						String componentType = this.jsRuleReturnTypeList.get(count).getComponentType();

						// for deviation case we added this (old code: if (StringUtils.equalsIgnoreCase(componentType,
						// "decimal")) { )
						if (StringUtils.equalsIgnoreCase(componentType, "decimal")
								|| StringUtils.equalsIgnoreCase(componentType, "integer")) {
							Component calculateButton = treeCell.getFellowIfAny(uUID + "_btn_CALCULATE" + count);
							if (calculateButton != null) {
								if (calculateButton.isVisible()) {
									actualBlock += "1" + "|";
								} else {
									actualBlock += "0" + "|";
								}
							}
						}

						Component objectResultComponent = treeCell.getFellowIfAny(uUID + "_RESULT" + count);
						if (objectResultComponent != null) {
							// Object Result Calculation Fields
							String resultCalculatedFields = " ";

							String objectResultValue = "";

							if (objectResultComponent.isVisible()) {
								objectResultValue = getOperandValue(objectResultComponent);
							}

							objectResultList.add(objectResultValue);

							if (objectResultComponent.getAttribute("calculatedFields") != null) {
								resultCalculatedFields = (String) objectResultComponent
										.getAttribute("calculatedFields");
							}

							actualBlock += objectResultValue + "|";
							actualBlock += resultCalculatedFields + "|";

							for (String calculateField : resultCalculatedFields.split(",")) {
								fieldsSet.add(calculateField);
							}
						}

					}
				}

				if (elseCondition) {
					this.query += " {\n";
				} else {
					this.query += ") {\n";
				}

				this.spaceCount++;
				if (treeItem.getTreechildren() != null && treeItem.getTreechildren().getChildren().size() > 0) {
					getQuery(treeItem.getTreechildren()); // Children Tree begins from here
				} else {
					// Using the logicCount we are closing the braces
					if (logicCount > 0 && treeItem.getTreechildren() == null) {
						this.query = this.query.substring(0, this.query.length() - 4);
						while (logicCount > 0) {
							this.query += ")";
							logicCount--;
						}
						this.query += ") {\n";
						flag = false;
					}
				}

				actualBlock += "~)|";

				if (resultComponent != null && resultComponent.isVisible()) {
					for (int j = this.spaceCount; j > 0; j--) {
						this.query += "\t\t";
					}

					if (this.ruleType == RuleReturnType.STRING) {
						this.query += "Result = '" + resultValue + "' ; return; ";
					} else {
						this.query += "Result = " + resultValue + " ; return; ";
					}

					this.spaceCount--;
					this.query += " \n ";

					for (int j = this.spaceCount; j > 0; j--) {
						query += "\t\t";
					}

					this.query += "}";
					// } else if (this.ruleType == RuleReturnType.OBJECT) {
				} else if (this.jsRuleReturnTypeList != null && !this.jsRuleReturnTypeList.isEmpty()) {
					queryResultList = new ArrayList<String>();

					for (int count = 0; count < this.jsRuleReturnTypeList.size(); count++) {
						Component objectResultComponent = treeCell.getFellowIfAny(uUID + "_RESULT" + count);

						if (objectResultComponent != null && objectResultComponent.isVisible()) {
							JSRuleReturnType jsRuleReturnType = jsRuleReturnTypeList.get(count);
							String resultLabel = jsRuleReturnType.getResultLabel();

							// if (StringUtils.isNotBlank(resultLabel)) {
							queryResultList.add(resultLabel + objectResultList.get(count) + ";");
							// } else {
							// queryResultList.add(objectResultList.get(count));
							// }

						}
					}

					if (!queryResultList.isEmpty()) {
						for (int j = this.spaceCount; j > 0; j--) {
							this.query += "\t\t";
						}

						this.query += "Result = ";

						for (String string : queryResultList) {
							this.query += string;
						}

						this.query += " return; ";
					}

					if (flag) {
						this.spaceCount--;
						this.query += "\n";

						for (int j = this.spaceCount; j > 0; j--) {
							this.query += "\t\t";
						}

						this.query += "}";
					}
				} else if (labelStatement != null) {
					this.spaceCount--;
					this.query += " \n ";

					for (int j = this.spaceCount; j > 0; j--) {
						this.query += "\t\t";
					}

					this.query += "}";
				}

			}
		}

		logger.debug("Leaving");

		return this.query;
	}

	/**
	 * Get Value from the components
	 * 
	 * @param component
	 * @return
	 */
	public String getOperandValue(Component component) {
		logger.debug("Entering");

		String operandValue = "";

		if (component instanceof Combobox) {
			Combobox combobox = (Combobox) component;
			combobox.setErrorMessage("");
			combobox.setConstraint("");

			if (combobox.isVisible()) {
				if (combobox.getSelectedIndex() <= 0) {
					this.treeTab.setSelected(true);
					throw new WrongValueException(component, Labels.getLabel("const_NO_SELECT"));
				}
			}
			operandValue = combobox.getSelectedItem().getValue();
		} else if (component instanceof ExtendedCombobox) {
			ExtendedCombobox extendedCombobox = (ExtendedCombobox) component;
			extendedCombobox.setErrorMessage("");
			extendedCombobox.setConstraint("");

			if (extendedCombobox.isVisible()) {
				extendedCombobox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
			}

			operandValue = "'" + extendedCombobox.getValue().trim() + "'";
		} else if (component instanceof Textbox) {
			Textbox textbox = (Textbox) component;
			textbox.setErrorMessage("");
			textbox.setConstraint("");

			if (textbox.isVisible()) {
				textbox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
			}

			operandValue = textbox.getValue().trim();
		} else if (component instanceof Longbox) {
			Longbox longbox = (Longbox) component;
			longbox.setErrorMessage("");
			longbox.setConstraint("");

			if (longbox.isVisible()) {
				longbox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
			} else {
				longbox.setValue((long) 0);
			}
			operandValue = longbox.getValue().toString();
		} else if (component instanceof Intbox) {
			Intbox intbox = (Intbox) component;
			intbox.setErrorMessage("");
			intbox.setConstraint("");

			if (intbox.isVisible()) {
				intbox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
			}

			operandValue = intbox.getValue().toString();
		} else if (component instanceof Decimalbox) {
			Decimalbox decimalbox = (Decimalbox) component;
			decimalbox.setErrorMessage("");
			decimalbox.setConstraint("");

			if (decimalbox.isVisible()) {
				decimalbox.setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
			}

			operandValue = decimalbox.getValue().toString();
		} else if (component instanceof Label) {
			operandValue = ((Label) component).getValue().toLowerCase();
		}

		logger.debug("Leaving");

		return operandValue;
	}

	/**
	 * Build treee from the actual Block
	 * 
	 * @param actualBlock
	 */
	public void buildQuery(String actualBlock) {
		logger.debug("Entering");

		List<Treechildren> treeChildrens = new ArrayList<Treechildren>();
		List<Treeitem> treeItems = new ArrayList<Treeitem>();
		Treeitem parentTreeItem = null;
		Treeitem treeItem = null;

		if (StringUtils.isEmpty(actualBlock)) {
			return;
		}

		String[] actualBlockArray = actualBlock.split("\\~");

		tree.getChildren().clear();

		Treechildren mainTreeChildren = new Treechildren();
		treeChildrens.add(mainTreeChildren);
		mainTreeChildren.setParent(tree);

		for (String string : actualBlockArray) {
			if (string.startsWith("(")) {
				string = StringUtils.substring(string, 2);
				treeItem = createNewCondition(string, "");

				if (treeItems.size() != 0) {
					parentTreeItem = treeItems.get(treeItems.size() - 1);
					if (treeItems.get(treeItems.size() - 1).getTreechildren() == null) {
						Treechildren treechildren = new Treechildren();
						treeChildrens.add(treechildren);
						treechildren.setParent(parentTreeItem);
					}
				}
				treeItems.add(treeItem);
				treeItem.setParent(treeChildrens.get(treeChildrens.size() - 1));
				setTreeSpace(treeItem, parentTreeItem);
				parentTreeItem = null;
			} else {
				treeItem = treeItems.get(treeItems.size() - 1);
				if (treeChildrens.size() > 1 && treeItem.getTreechildren() != null) {
					treeChildrens.remove(treeChildrens.size() - 1);
				}
				treeItems.remove(treeItems.size() - 1);
			}
		}
		tree.setVisible(true);

		logger.debug("Leaving");
	}

	private void setTreeSpace(Treeitem treeItem, Treeitem parentTreeItem) {
		logger.debug("Entering");
		int size = 0;
		Treecell treeCell = (Treecell) treeItem.getTreerow().getChildren().get(0);
		String uUID = treeCell.getId();
		Space space = (Space) treeItem.getFellowIfAny("space" + uUID);

		if (parentTreeItem != null) {
			Treecell parentTreeCell = (Treecell) parentTreeItem.getTreerow().getChildren().get(0);
			String parentuUID = parentTreeCell.getId();

			Space parentSpace = (Space) parentTreeCell.getFellowIfAny("space" + parentuUID);
			size = Integer.parseInt(parentSpace.getWidth().substring(0, parentSpace.getWidth().length() - 2));
			size = size + 10;
		}
		space.setWidth(size + "px");
		space.getWidth();

		logger.debug("Leaving");

	}

	/**
	 * Method to fill the combobox with given list of values except excludeFields
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 * @param excludeFields
	 */
	public void fillComboBox(Combobox combobox, String value, List<ValueLabel> list, String excludeFields) {
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);

		for (ValueLabel valueLabel : list) {
			if (!StringUtils.trimToEmpty(excludeFields).contains(", " + valueLabel.getValue() + " ,")) {
				comboitem = new Comboitem();
				comboitem.setValue(valueLabel.getValue());
				comboitem.setLabel(valueLabel.getLabel());
				combobox.appendChild(comboitem);

				if (StringUtils.trimToEmpty(value).equals(StringUtils.trim(valueLabel.getValue()))) {
					combobox.setSelectedItem(comboitem);
				}
			}
		}
	}

	/**
	 * Test the exact value from the listbox or the combobox are selected
	 */
	public boolean listDataValidation(Component component, String fieldParm) {
		boolean error = true;
		Combobox comp = (Combobox) component;
		for (int i = 0; i < comp.getItemCount(); i++) {
			if (comp.getValue().equals(comp.getItemAtIndex(i).getLabel())) {
				comp.setSelectedIndex(i);
				error = false;
				break;
			}
		}
		return error;
	}

	/**
	 * Validate the Query
	 */
	public void validateQuery() {
		logger.debug("Entering");

		if (this.editable) {
			sqlQuery = "";
			actualBlock = "";
			codemirror.setValue("");
			this.query = "";
			logicCount = 0;
			this.spaceCount = 0;
			sqlQuery = getQuery(tree.getTreechildren());
		}
		this.codemirror.setValue(this.splCodemirror.getValue() + sqlQuery);
		this.splCodemirror.setValue(this.splCodemirror.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Simulate the Query
	 */
	public void simulateQuery() {
		logger.debug("Entering");

		if (this.editable) {
			getSqlQuery();
		}

		if (StringUtils.isNotBlank(sqlQuery)) {
			Map<String, Object> fieldObjectMap = new HashMap<String, Object>();
			final Map<String, Object> map = new HashMap<String, Object>();

			if (objectFieldList == null || objectFieldList.isEmpty()) {
				this.objectFieldList = PennantAppUtil.getRBFieldDetails(getModule(), getEvent());
			}

			for (RBFieldDetail fldDetails : objectFieldList) {
				for (String field : this.fields.split(",")) {
					if (StringUtils.equals(field, fldDetails.getRbFldName())) {
						fieldObjectMap.put(fldDetails.getRbFldName(), fldDetails);
						break;
					}
				}
			}

			map.put("fieldObjectMap", fieldObjectMap);
			map.put("javaScriptBuilder", this);
			map.put("splCodemirror", splCodemirror.getValue());
			map.put("varList", globalVariableList);

			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultSimulation.zul", this, map);
		} else {
			MessageUtil.showError("Please Build the Rule before Simulation");
		}

		logger.debug("Leaving");
	}

	public void setEditable(boolean editable) {
		logger.debug("Entering");

		if (editable) {
			this.tree.setVisible(true);
			this.objectFieldList = null;
			this.uniqueId = 0;
			this.actualQuery = "";
			this.codemirror.setValue(this.actualQuery);

			if ((this.ruleType == RuleReturnType.OBJECT)
					&& (this.jsRuleReturnTypeList == null || this.jsRuleReturnTypeList.isEmpty())) {
				return;
			}

			if (StringUtils.isBlank(actualBlock)) {
				tree.getChildren().clear();
				Treechildren treeChildren = new Treechildren();

				Treeitem ifCondition = createNewCondition(null, "IF");
				treeChildren.appendChild(ifCondition);
				setTreeSpace(ifCondition, null);

				Treeitem elseCondition = setElseCondition(treeChildren, ifCondition);
				setTreeSpace(elseCondition, null);

				tree.appendChild(treeChildren);
			}
		} else {
			tree.setVisible(false);
			if (objectFieldList == null || objectFieldList.isEmpty()) {
				this.objectFieldList = PennantAppUtil.getRBFieldDetails(getModule(), getEvent());
			}
		}

		this.editable = editable;

		logger.debug("Leaving");
	}

	/**
	 * Set the else block.
	 * 
	 * @param treeChildren
	 * @param ifCondition
	 */
	private Treeitem setElseCondition(Treechildren treeChildren, Treeitem ifCondition) {
		Treeitem elseCondition = createNewCondition(null, "ELSE");
		treeChildren.appendChild(elseCondition);

		String ifUUID = ifCondition.getTreerow().getChildren().get(0).getId();
		String elseUUID = elseCondition.getTreerow().getChildren().get(0).getId();

		doSetButtonProperties(treeChildren, ifUUID, elseUUID, "ELSE");
		return elseCondition;
	}

	/**
	 * Calculate the value
	 * 
	 * @param event
	 */
	public void onClickCalculate(ForwardEvent event) {
		logger.debug("Entering");

		final Map<String, Object> map = new HashMap<String, Object>();
		Textbox calculate = (Textbox) event.getData();

		if (objectFieldList == null || objectFieldList.isEmpty()) {
			this.objectFieldList = PennantAppUtil.getRBFieldDetails(getModule(), getEvent());
		}

		map.put("objectFieldList", this.objectFieldList);
		map.put("Formula", calculate.getValue());
		map.put("returnType", this.getRuleType());
		map.put("CalculateBox", calculate);

		Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultDialog.zul", this, map);

		logger.debug("Leaving");
	}

	/**
	 * Method for Simulation of builded code
	 * 
	 * @param event
	 */
	public void onClick$btnSimulation(ForwardEvent event) {
		logger.debug("Entering");

		simulateQuery();
		setSelectedTab(RuleConstants.TAB_SCRIPT);

		logger.debug("Leaving");
	}

	/**
	 * Method for Simulation of builded code
	 * 
	 * @param event
	 */
	public void onClick$btnValidate(ForwardEvent event) {
		logger.debug("Entering");

		getSqlQuery();
		setSelectedTab(RuleConstants.TAB_SCRIPT);

		logger.debug("Leaving");
	}

	/**
	 * This Method is called when division button is clicked
	 * 
	 * @param event
	 */
	public void onFulfillExtendedComobo(ForwardEvent event) {
		logger.debug("Entering");

		ExtendedCombobox extendedCombo = (ExtendedCombobox) event.getOrigin().getTarget();
		Clients.clearWrongValue(extendedCombo);
		JSRuleReturnType jsRuleReturnType = (JSRuleReturnType) event.getData();

		String extendedComboValue = extendedCombo.getValue();
		Object dataObject = null;
		Map<String, Object> selectedValuesMap = null;

		if (StringUtils.isNotBlank(extendedComboValue)) {
			selectedValuesMap = setSelectedValuesMap(extendedComboValue);
		} else {
			selectedValuesMap = new HashMap<String, Object>();
		}

		if (jsRuleReturnType.isMultiSelection()) {
			dataObject = ExtendedMultipleSearchListBox.show(this, jsRuleReturnType.getModuleName(), selectedValuesMap);
		} else {
			extendedCombo.setModuleName(jsRuleReturnType.getModuleName());
			dataObject = extendedCombo.getObject();
		}

		if (dataObject == null) {
			return;
		} else if (dataObject instanceof String) {
			extendedComboValue = dataObject.toString();
		} else {
			@SuppressWarnings("unchecked")
			Map<String, Object> details = (Map<String, Object>) dataObject;
			extendedComboValue = details.keySet().toString();

			if (jsRuleReturnType.isMultiSelection()) {
				extendedComboValue = extendedComboValue.replace("[", "").replace("]", "").replace(" ", "");
			}
		}

		extendedCombo.setValue(extendedComboValue);
		extendedCombo.setTooltiptext(extendedComboValue);

		logger.debug("Leaving");
	}

	// ================================================================================
	// ============================== Getters and Setters =============================
	// ================================================================================

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public String getSqlQuery() {
		logger.debug("Entering");

		sqlQuery = "";
		actualBlock = "";
		this.query = "";
		logicCount = 0;
		this.spaceCount = 0;
		fields = "";
		fieldsSet = new HashSet<String>();
		codemirror.setValue("");
		sqlQuery = getQuery(tree.getTreechildren());

		for (String field : fieldsSet) {
			if (StringUtils.isNotBlank(fields)) {
				fields += "," + field;
			} else {
				fields = field;
			}
		}

		actualQuery = StringReplacement.getReplacedQuery(sqlQuery, globalVariableList, null);
		this.codemirror.setValue(this.splCodemirror.getValue() + actualQuery);
		this.splCodemirror.setValue(this.splCodemirror.getValue());

		splQuery = this.splCodemirror.getValue();

		actualQuery = this.splCodemirror.getValue() + actualQuery;
		logger.debug("Leaving");

		return this.sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.codemirror.setValue(sqlQuery);
		this.sqlQuery = sqlQuery;
	}

	public String getActualQuery() {
		return actualQuery;
	}

	public RuleModule getRuleModule() {
		return ruleModule;
	}

	public void setRuleModule(RuleModule ruleModule) {
		this.ruleModule = ruleModule;
	}

	public String getActualBlock() {
		return actualBlock;
	}

	public void setActualQuery(String actualQuery) {
		this.actualQuery = actualQuery;
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public ModuleMapping getModuleMapping() {
		return moduleMapping;
	}

	public void setModuleMapping(ModuleMapping moduleMapping) {
		this.moduleMapping = moduleMapping;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}

	public void setTreeTabVisible(boolean visible) {
		this.treeTabpanel.setVisible(visible);
		this.treeTab.setVisible(visible);

		this.btnSimulation.setVisible(visible);
		this.btnValidate.setVisible(visible);
		this.toolbar.setVisible(visible);
		setReadOnly(!visible);

		if (!visible) {
			this.scriptTab.setSelected(true);
		}
	}

	public boolean isTreeTabVisible() {
		return this.treeTabpanel.isVisible();
	}

	public void setSelectedTab(String tab) {
		if (StringUtils.equals(tab, RuleTabs.DESIGN.name())) {
			treeTab.setSelected(true);
		} else {
			scriptTab.setSelected(true);
		}
	}

	public boolean isEditable() {
		return editable;
	}

	public String getEvent() {
		return this.event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public int getNoOfRowsVisible() {
		return noOfRowsVisible;
	}

	public void setNoOfRowsVisible(int noOfRowsVisible) {
		this.noOfRowsVisible = noOfRowsVisible;

		this.setHeight("100%");
		this.tabPanelboxHeight = borderLayoutHeight - (noOfRowsVisible * 20) - 125;
		this.codemirror.setHeight(tabPanelboxHeight + "px");
		this.tree.setHeight(tabPanelboxHeight + "px");
	}

	public RuleReturnType getRuleType() {
		return ruleType;
	}

	public void setRuleType(RuleReturnType ruleType) {
		this.ruleType = ruleType;
	}

	public List<JSRuleReturnType> getJsRuleReturnTypeList() {
		return jsRuleReturnTypeList;
	}

	public void setJsRuleReturnTypeList(List<JSRuleReturnType> jsRuleReturnTypeList) {
		this.jsRuleReturnTypeList = jsRuleReturnTypeList;
	}

	public String getSplQuery() {
		return splQuery;
	}

	public void setSplQuery(String splQuery) {
		this.splCodemirror.setValue(splQuery);
		this.splQuery = splQuery;
	}

	// public void setEntityCode(String entityCode){
	// this.entityCode = entityCode;
	// this.globalVariableList = SysParamUtil.getGlobaVariableList();
	// }
}
