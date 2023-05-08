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
 * * FileName : QueryBuilder.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-07-2013 * * Modified Date :
 * 23-05-2013 * * Description : Query Builder * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-07-2013 Chaitanya Varma 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.QBFieldDetail;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.applicationmaster.QueryModule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.StringReplacement;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.web.util.MessageUtil;

@SuppressWarnings("rawtypes")
public class QueryBuilder extends Groupbox {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LogManager.getLogger(QueryBuilder.class);

	protected Tree tree; // autowired
	protected Space space; // autowired
	protected Textbox textbox; // autowired

	private String actualBlock;
	private String sqlQuery = "";
	private int uniqueId = 0;
	private QueryModule queryModule;
	// private String entityCode;
	private int listRows;
	private boolean editable = true;
	private String treeHeight = "200px";
	private String textBoxHeight = "125px";
	private String listBoxHeight = "0px";
	public String queryValue = null;
	private ModuleMapping moduleMapping = null;
	private ListModelList listModelList;

	private List<ValueLabel> operatorsList = PennantStaticListUtil.getOperators(""); // retrieve all the operators
	private List<ValueLabel> operandTypesList = PennantStaticListUtil.getOperandTypes(""); // retrieve all selection
																							// types
	private List<ValueLabel> logicalOperatorsList = PennantStaticListUtil.getLogicalOperators("");// retrieve values
	private List<GlobalVariable> globalVariableList = null;// retrieve values from table--GlobalVariable
	private List<QBFieldDetail> objectFieldList = null;// retrieve values
	private List<Query> subQueriesList = null;// retrieve values

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected Query object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public QueryBuilder() {
		tree = new Tree();
		this.appendChild(tree);
		tree.setZclass("z-dottree");
		tree.setSclass("QueryBuilderTree");
		tree.setHeight(treeHeight);

		textbox = new Textbox();
		textbox.setWidth("100%");
		textbox.setMultiline(true);
		textbox.setReadonly(true);
		textbox.setRows(10);
		textbox.setHeight(this.textBoxHeight);
		this.appendChild(textbox);
		createNewQuery();
	}

	/**
	 * Create new query
	 */
	public void createNewQuery() {
	}

	/**
	 * Create a new Condition
	 * 
	 * @param condition (String) to build new condition based on the dbQuery
	 * @return treeItem(TreeItem)
	 */
	private Treeitem createNewCondition(String condition) {
		int i = 0;
		String[] str = null;
		String uUID = "cond" + uniqueId++;
		boolean buildCondition = false;
		Space space;
		if (StringUtils.isNotBlank(condition)) {
			str = condition.split("\\|");
			buildCondition = true;
		}

		Treeitem treeItem = new Treeitem();
		Treerow treeRow = new Treerow();
		treeRow.setParent(treeItem);
		Treecell treeCell = new Treecell();
		treeCell.setParent(treeRow);

		treeCell.setId(uUID);

		// Creating remove button
		Button removeCondition = new Button();
		removeCondition.setImage("/images/icons/delete.png");
		removeCondition.addForward("onClick", this, "onRemoveCondition", treeItem);
		removeCondition.setId(uUID + "_RmvCond");
		removeCondition.setParent(treeCell);

		space = new Space();
		space.setWidth("2px");
		space.setParent(treeCell);

		// Creating logical operators
		if (uniqueId == 1) {
			removeCondition.setDisabled(true);
		} else {
			Combobox logicalOperator = new Combobox();
			logicalOperator.setWidth("70px");
			logicalOperator.setParent(treeCell);
			logicalOperator.setId(uUID + "_logicalOperator");
			logicalOperator.setReadonly(true);
			fillComboBox(logicalOperator, buildCondition ? str[i++] : null, logicalOperatorsList, null);

			space = new Space();
			space.setWidth("2px");
			space.setParent(treeCell);
		}

		// Leftoperand creation
		Combobox leftOperandType = new Combobox();
		leftOperandType.setParent(treeCell);
		leftOperandType.setId(uUID + "_leftOperandType");
		leftOperandType.setReadonly(true);
		String excludeFields = "," + PennantConstants.SUBQUERY + "," + PennantConstants.DBVALUE + ","
				+ PennantConstants.CALCVALUE + ",";
		fillComboBox(leftOperandType, buildCondition ? str[i++] : null, operandTypesList, excludeFields);
		leftOperandType.addForward("onChange", this, "onChangeOperandType", null);

		space = new Space();
		space.setWidth("2px");
		space.setParent(treeCell);

		createOperand(uUID + "_leftOperand", leftOperandType, buildCondition ? str[i++] : null);

		// Operators creation
		Combobox operator = new Combobox();
		operator.setParent(treeCell);
		operator.setId(uUID + "_operator");
		excludeFields = "";
		operator.setAttribute("TreeCell", treeCell);
		fillComboBoxByValueLabels(operator, buildCondition ? str[i++] : null, operatorsList, excludeFields);
		operator.addForward("onChange", this, "onChangeOperator", null);

		// Rightoperand creation
		Combobox rightOperandType = new Combobox();
		rightOperandType.setParent(treeCell);
		rightOperandType.setId(uUID + "_rightOperandType");
		if (condition != null) {
			if ((condition.contains("DBVALUE")) || (condition.contains("SUBQUERY"))) {
				// excludeFields="";
			} else {
				excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ",";
			}
		} else {
			excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ",";
		}
		fillComboBoxByValueLabels(rightOperandType, buildCondition ? str[i++] : null, operandTypesList, excludeFields);
		rightOperandType.addForward("onChange", this, "onChangeOperandType", null);
		if (condition != null) {
			if (condition.contains("| |")) {
				rightOperandType.setVisible(false);
			}
		}
		space = new Space();
		space.setWidth("2px");
		space.setParent(treeCell);

		createOperand(uUID + "_rightOperand", rightOperandType, buildCondition ? str[i++] : null);

		// Creating button for adding a new condition
		Button addCondition = new Button();
		addCondition.setImage("/images/icons/add.png");
		addCondition.addForward("onClick", this, "onAddCondition", treeItem);
		addCondition.setParent(treeCell);

		space = new Space();
		space.setWidth("2px");
		space.setParent(treeCell);

		// Creating button for adding a new sub condition
		Button addSubCondition = new Button();
		addSubCondition.setImage("/images/icons/extadd.png");
		addSubCondition.addForward("onClick", this, "onAddSubCondition", treeItem);
		addSubCondition.setParent(treeCell);

		if (buildCondition) {
			Combobox comboBox = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_operator");
			Combobox comboBoxOperandType = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_rightOperandType");
			String excludefields = getExcludeFieldsByOperandType(leftOperandType, "operator");
			fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludefields);
			Component leftOperand = treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
			if (leftOperand instanceof Combobox) {
				if (((Combobox) leftOperand).getSelectedIndex() != 0
						&& leftOperandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)) {
					QBFieldDetail fielddetails = (QBFieldDetail) ((Combobox) leftOperand).getSelectedItem()
							.getAttribute("FieldDetails");
					String excludefields1 = getExcludeFieldsByOperands((Combobox) leftOperand, "operator",
							fielddetails);
					fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList,
							excludefields1);

				} else if (((Combobox) leftOperand).getSelectedIndex() != 0
						&& leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)) {
					GlobalVariable globalVariable = (GlobalVariable) ((Combobox) leftOperand).getSelectedItem()
							.getAttribute("GlobalVariableDetails");
					String excludefields1 = getGlobalExcludeFieldsByOperands((Combobox) leftOperand, "operator",
							globalVariable);
					fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList,
							excludefields1);

				}
			}
			String excludefields2 = getExcludeFieldsByRightOperandType(operator, leftOperand, leftOperandType);
			fillComboBox(comboBoxOperandType, comboBoxOperandType.getSelectedItem().getValue().toString(),
					operandTypesList, excludefields2);
		}
		return treeItem;
	}

	// Component Events

	/**
	 * ON removing a condition
	 */
	public void onRemoveCondition(Event event) {
		Treeitem treeItem = (Treeitem) event.getData();
		treeItem.detach();
	}

	/**
	 * On selecting to add a new Condition
	 */
	public void onAddCondition(Event event) {
		Treeitem treeItem = (Treeitem) event.getData();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		Treeitem newCondition = createNewCondition(null);
		if (treeItem.getNextSibling() != null) {
			treeChildren.insertBefore(newCondition, treeItem.getNextSibling());
		} else {
			treeChildren.appendChild(newCondition);
		}
	}

	/**
	 * On selecting to add a new sub Condition
	 * 
	 * @param event
	 */
	public void onAddSubCondition(Event event) {
		Treeitem treeItem = (Treeitem) event.getData();
		Treechildren treeChildren;
		if (treeItem.getTreechildren() != null) {
			treeChildren = treeItem.getTreechildren();
		} else {
			treeChildren = new Treechildren();
			treeChildren.setParent(treeItem);
		}
		treeChildren.appendChild(createNewCondition(null));

	}

	/**
	 * onChanging Operand Types
	 * 
	 * @param event
	 */
	public void onChangeOperandType(ForwardEvent event) {
		Combobox operandType = (Combobox) event.getOrigin().getTarget();
		onChangingOperandType(operandType);

	}

	public void onChangingOperandType(Combobox operandType) {
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
						((Combobox) operator).setSelectedIndex(0);
					}
					if (rightOperandType.getSelectedIndex() > 0) {
						((Combobox) rightOperandType).setSelectedIndex(0);
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
				String excludeFields = getExcludeFieldsByOperandType(operandType, "operator");
				fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludeFields);
				excludeFields = "";
				excludeFields = getExcludeFieldsByOperandType(operandType, "operandType");
				comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
				fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operandTypesList,
						excludeFields);

			}
			uUID = uUID + "_leftOperand";
		}
		if (treeCell.getFellowIfAny(uUID) != null) {
			treeCell.getFellowIfAny(uUID).detach();
			treeCell.getFellowIfAny(uUID + "_space").detach();
		}
		createOperand(uUID, operandType, null);
	}

	public String getExcludeFieldsByOperandType(Combobox operandType, String type) {
		String excludeFields = "";
		if (operandType.getSelectedIndex() == 0) {
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)) {
			if ("operator".equals(type)) {
				excludeFields = ", > , >= , < , <= ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.SUBQUERY + "," + PennantConstants.STATICTEXT + ",";
			}
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)) {
			if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ",";
			}
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)) {
			if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ","
						+ PennantConstants.CALCVALUE + ",";
			}
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.CALCVALUE)) {
			if ("operator".equals(type)) {
				excludeFields = ", LIKE , NOT LIKE ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.STATICTEXT + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.SUBQUERY + ",";
			}
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.FUNCTION)) {
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.SUBQUERY)) {
		}
		return excludeFields;
	}

	/**
	 * onChanging Operand
	 * 
	 * @param event
	 */
	@SuppressWarnings("unused")
	public void onChangeOperand(ForwardEvent event) {
		Combobox operandType = (Combobox) event.getOrigin().getData();
		Component operand = (Component) event.getOrigin().getTarget();
		onChangingOperand(operand);
	}

	public void onChangingOperand(Component operand) {
		Treecell treeCell = (Treecell) operand.getParent();
		if (treeCell.getFellowIfAny(treeCell.getId() + "_button") != null) {
			treeCell.getFellowIfAny(treeCell.getId() + "_button").detach();
		}
		if (operand instanceof Combobox) {
			if (operand.getId().endsWith("_leftOperand")) {
				Combobox operandtype = (Combobox) operand.getAttribute("OperandType");
				Combobox operator = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_operator");
				if (operator.getSelectedIndex() > 0) {
					((Combobox) operator).setSelectedIndex(0);
				}
				if (treeCell.getFellowIfAny(treeCell.getId() + "_rightOperandType") != null
						&& treeCell.getFellowIfAny(treeCell.getId() + "_rightOperand") != null) {
					Combobox rightOperandType = (Combobox) treeCell
							.getFellowIfAny(treeCell.getId() + "_rightOperandType");
					Component rightOperand = (Component) treeCell.getFellowIfAny(treeCell.getId() + "_rightOperand");
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
				if (operandtype.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)
						&& ((Combobox) operand).getSelectedIndex() != 0) {
					QBFieldDetail fielddetails = (QBFieldDetail) ((Combobox) operand).getSelectedItem()
							.getAttribute("FieldDetails");
					String uUID = treeCell.getId();
					if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
						Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");
						String excludeFields = getExcludeFieldsByOperands((Combobox) operand, "operator", fielddetails);
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList,
								excludeFields);
						excludeFields = "";
						excludeFields = getExcludeFieldsByOperands((Combobox) operand, "operandType", fielddetails);
						comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operandTypesList,
								excludeFields);
					}
				} else if (operandtype.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)
						&& ((Combobox) operand).getSelectedIndex() != 0) {
					GlobalVariable globalVariable = (GlobalVariable) ((Combobox) operand).getSelectedItem()
							.getAttribute("GlobalVariableDetails");
					String uUID = treeCell.getId();
					if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
						Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");
						String excludeFields = getGlobalExcludeFieldsByOperands((Combobox) operand, "operator",
								globalVariable);
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList,
								excludeFields);
						excludeFields = "";
						excludeFields = getGlobalExcludeFieldsByOperands((Combobox) operand, "operandType",
								globalVariable);
						comboBox = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operandTypesList,
								excludeFields);
					}
				}
			} else if (operand.getId().endsWith("_rightOperand")) {
			}
		} else if (operand instanceof Textbox) {
		} else if (operand instanceof Longbox) {
		}

	}

	public String getGlobalExcludeFieldsByOperands(Combobox operand, String type, GlobalVariable globalVariable) {
		String excludeFields = "";
		String fieldType = globalVariable.getType();
		if ((fieldType.equals(PennantConstants.DECIMAL)) || (fieldType.equals(PennantConstants.BIGINT))
				|| (fieldType.equals(PennantConstants.NUMERIC))) {
			if ("operator".equals(type)) {
				excludeFields = ", LIKE , NOT LIKE ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.STATICTEXT + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.SUBQUERY + ",";
			}
		} else if ((fieldType.equalsIgnoreCase(PennantConstants.NVARCHAR))
				|| (fieldType.equalsIgnoreCase(PennantConstants.NCHAR))
				|| (fieldType.equalsIgnoreCase(PennantConstants.VARCHAR))) {
			if ("operator".equals(type)) {
				excludeFields = ", > , >= , < , <= ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.SUBQUERY + ",";
			}
		} else if ((fieldType.equalsIgnoreCase(PennantConstants.DATETIME))
				|| (fieldType.equalsIgnoreCase(PennantConstants.SMALLDATETIME))) {
			if ("operator".equals(type)) {
				excludeFields = "";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ","
						+ PennantConstants.CALCVALUE + "," + PennantConstants.STATICTEXT + ",";
			}
		}
		return excludeFields;
	}

	public String getExcludeFieldsByOperands(Combobox operand, String type, QBFieldDetail fielddetails) {
		String excludeFields = "";
		String fieldType = fielddetails.getQbFldType();
		if ((fieldType.equals(PennantConstants.DECIMAL)) || (fieldType.equals(PennantConstants.BIGINT))
				|| (fieldType.equals(PennantConstants.NUMERIC))) {
			if ("operator".equals(type)) {
				excludeFields = ", LIKE , NOT LIKE ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.STATICTEXT + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.SUBQUERY + ",";
			}
		} else if ((fieldType.equalsIgnoreCase(PennantConstants.NVARCHAR))
				|| (fieldType.equalsIgnoreCase(PennantConstants.NCHAR))
				|| (fieldType.equalsIgnoreCase(PennantConstants.VARCHAR))
				|| (fieldType.equalsIgnoreCase(PennantConstants.CHAR))) {
			if ("operator".equals(type)) {
				excludeFields = ", > , >= , < , <= ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.SUBQUERY + ",";
			}
		} else if ((fieldType.equalsIgnoreCase(PennantConstants.DATETIME))
				|| (fieldType.equalsIgnoreCase(PennantConstants.SMALLDATETIME))) {
			if ("operator".equals(type)) {
				excludeFields = "";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ","
						+ PennantConstants.CALCVALUE + "," + PennantConstants.STATICTEXT + ",";
			}
		}
		Comboitem fieldlist = operand.getSelectedItem();
		QBFieldDetail fieldDetails = (QBFieldDetail) fieldlist.getAttribute("FieldDetails");
		if (StringUtils.isNotBlank(fieldDetails.getModuleCode())) {
			if ("operator".equals(type)) {
				excludeFields = ", > , >= , < , <= ,";
			} else if ("operandType".equals(type)) {
				excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.SUBQUERY + ",";
			}
		}

		return excludeFields;
	}

	/**
	 * onChanging Operator
	 * 
	 * @param event
	 */
	public void onChangeOperator(ForwardEvent event) {
		Combobox operator = (Combobox) event.getOrigin().getTarget();
		onChangingOperator(operator);
	}

	public void onChangingOperator(Combobox operator) {
		Treecell treeCell = (Treecell) operator.getParent();
		String uUID = treeCell.getId();
		if (treeCell.getFellowIfAny(treeCell.getId() + "_button") != null) {
			treeCell.getFellowIfAny(treeCell.getId() + "_button").detach();
		}

		String selectedOperator = operator.getSelectedItem().getLabel();
		if (treeCell.getFellowIfAny(uUID + "_operator") != null) {
			Combobox rightOperandType = (Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType");
			Combobox leftOperandType = (Combobox) treeCell.getFellowIfAny(uUID + "_leftOperandType");
			Component rightOperand = (Component) treeCell.getFellowIfAny(uUID + "_rightOperand");
			Component leftOperand = (Component) treeCell.getFellowIfAny(uUID + "_leftOperand");
			rightOperandType.setSelectedIndex(0);
			if (rightOperand instanceof Textbox) {
				if (((Textbox) rightOperand).isReadonly()) {
					((Textbox) rightOperand).setReadonly(false);
					((Textbox) rightOperand).setConstraint("");
					((Textbox) rightOperand).setValue("");
				}
				if (rightOperandType.getSelectedItem().getValue().equals(PennantConstants.DBVALUE)) {
					((Textbox) rightOperand).setReadonly(false);
					((Textbox) rightOperand).setConstraint("");
					((Textbox) rightOperand).setValue("");
					((Textbox) rightOperand).setReadonly(true);
				} else {
					((Textbox) rightOperand).setConstraint("");
					((Textbox) rightOperand).setValue("");
				}
			} else if (rightOperand instanceof Combobox) {
				((Combobox) rightOperand).setConstraint("");
				((Combobox) rightOperand).setValue("");
			} else if (rightOperand instanceof Longbox) {
				((Longbox) rightOperand).setConstraint("");
				((Longbox) rightOperand).setValue((long) 0);
			}
			if ((selectedOperator.equals(Labels.getLabel("ISNULL_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("ISNOTNULL_LABEL")))) {
				rightOperandType.setVisible(false);
				if (rightOperand instanceof Textbox) {

					((Textbox) rightOperand).setVisible(false);
				} else if (rightOperand instanceof Combobox) {
					((Combobox) rightOperand).setVisible(false);
				} else if (rightOperand instanceof Longbox) {
					((Longbox) rightOperand).setVisible(false);
				}
				if (treeCell.getFellowIfAny(treeCell.getId() + "_button") != null) {
					treeCell.getFellowIfAny(treeCell.getId() + "_button").detach();
				}
			} else {
				rightOperandType.setVisible(true);
				if (rightOperand instanceof Textbox) {
					((Textbox) rightOperand).setVisible(true);
				} else if (rightOperand instanceof Combobox) {
					((Combobox) rightOperand).setVisible(true);
				} else if (rightOperand instanceof Longbox) {
					((Longbox) rightOperand).setVisible(true);
				}
				if (treeCell.getFellowIfAny(treeCell.getId() + "_button") != null) {
					treeCell.getFellowIfAny(treeCell.getId() + "_button").detach();
				}
			}
			if ((selectedOperator.equals(Labels.getLabel("EXISTS_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("NOTEXISTS_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("IN_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("NOTIN_LABEL")))) {
				String excludeFields = getExcludeFieldsByRightOperandType((Combobox) operator, leftOperand,
						leftOperandType);
				fillComboBox(rightOperandType, rightOperandType.getSelectedItem().getValue().toString(),
						operandTypesList, excludeFields);
			} else {
				String excludeFields = getExcludeFieldsByRightOperandType((Combobox) operator, leftOperand,
						leftOperandType);
				fillComboBox(rightOperandType, rightOperandType.getSelectedItem().getValue().toString(),
						operandTypesList, excludeFields);
			}
		}
	}

	/**
	 * Get the Excludefields for rightOperandType
	 * 
	 * @param operator (Combobox),leftOperand(Component)
	 * @return excludeFields(String)
	 */
	public String getExcludeFieldsByRightOperandType(Combobox operator, Component leftOperand,
			Combobox leftOperandType) {
		String excludeFields = "";
		String selectedOperator = operator.getSelectedItem().getLabel();
		try {
			if (operator.getSelectedIndex() == 0) {
			} else if ((selectedOperator.equals(Labels.getLabel("EXISTS_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("NOTEXISTS_LABEL")))) {
				excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
						+ PennantConstants.STATICTEXT + "," + PennantConstants.GLOBALVAR + ","
						+ PennantConstants.FIELDLIST + ",";
			} else if ((selectedOperator.equals(Labels.getLabel("IN_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("NOTIN_LABEL")))) {
				if (leftOperand instanceof Combobox) {
					if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)) {
						QBFieldDetail fielddetails = (QBFieldDetail) ((Combobox) leftOperand).getSelectedItem()
								.getAttribute("FieldDetails");
						if (fielddetails != null) {
							String fieldType = fielddetails.getQbFldType();
							if (StringUtils.isNotBlank(fielddetails.getModuleCode())) {
								excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.GLOBALVAR
										+ "," + PennantConstants.FIELDLIST + ",";
							} else {
								excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.GLOBALVAR
										+ "," + PennantConstants.FIELDLIST + "," + PennantConstants.DBVALUE + ",";
							}
							if ((fieldType.equals(PennantConstants.DECIMAL))
									|| (fieldType.equals(PennantConstants.BIGINT))
									|| (fieldType.equals(PennantConstants.NUMERIC))
									|| (fieldType.equalsIgnoreCase(PennantConstants.DATETIME))
									|| (fieldType.equalsIgnoreCase(PennantConstants.SMALLDATETIME))) {
								excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.GLOBALVAR
										+ "," + PennantConstants.FIELDLIST + "," + PennantConstants.DBVALUE + ","
										+ PennantConstants.STATICTEXT + ",";
							}
						}
					} else if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)) {
						excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
								+ PennantConstants.GLOBALVAR + "," + PennantConstants.FIELDLIST + ",";
						GlobalVariable globalVariable = (GlobalVariable) ((Combobox) leftOperand).getSelectedItem()
								.getAttribute("GlobalVariableDetails");
						if (globalVariable != null) {
							String fieldType = globalVariable.getType();
							if ((fieldType.equals(PennantConstants.DECIMAL))
									|| (fieldType.equals(PennantConstants.BIGINT))
									|| (fieldType.equals(PennantConstants.NUMERIC))
									|| (fieldType.equalsIgnoreCase(PennantConstants.DATETIME))
									|| (fieldType.equalsIgnoreCase(PennantConstants.SMALLDATETIME))) {
								excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.GLOBALVAR
										+ "," + PennantConstants.FIELDLIST + "," + PennantConstants.DBVALUE + ","
										+ PennantConstants.STATICTEXT + ",";
							}
						}
					}
				} else {
					excludeFields = "," + PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
							+ PennantConstants.GLOBALVAR + "," + PennantConstants.FIELDLIST + ","
							+ PennantConstants.STATICTEXT + ",";
				}
			} else if ((selectedOperator.equals(Labels.getLabel("EQUALS_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("NOTEQUAL_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("NOTLIKE_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("LIKE_LABEL")))) {
				if (leftOperand instanceof Combobox) {
					if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)) {
						if (((Combobox) leftOperand).getSelectedIndex() != 0) {
							QBFieldDetail fielddetails = (QBFieldDetail) ((Combobox) leftOperand).getSelectedItem()
									.getAttribute("FieldDetails");
							if (fielddetails != null) {
								if (StringUtils.isNotBlank(fielddetails.getModuleCode())) {
									excludeFields = "," + PennantConstants.SUBQUERY + "," + PennantConstants.CALCVALUE
											+ ",";
								} else {
									if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.NCHAR))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.NVARCHAR))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.VARCHAR))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.CHAR))) {
										excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY
												+ "," + PennantConstants.CALCVALUE + ",";
									} else if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext()
											.toLowerCase().contains(PennantConstants.DATETIME))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.SMALLDATETIME))) {
										excludeFields = "," + PennantConstants.STATICTEXT + ","
												+ PennantConstants.CALCVALUE + "," + PennantConstants.DBVALUE + ","
												+ PennantConstants.SUBQUERY + ",";
									} else if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext()
											.toLowerCase().contains(PennantConstants.NUMERIC))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.BIGINT))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.DECIMAL))
											|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext()
													.toLowerCase().contains(PennantConstants.INT))) {
										excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY
												+ "," + PennantConstants.STATICTEXT + ",";
									}
								}
							}
						}
					} else if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)) {
						excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ",";
						if (((Combobox) leftOperand).getSelectedIndex() != 0) {
							// GlobalVariable globalVariable=(GlobalVariable) ((Combobox)
							// leftOperand).getSelectedItem().getAttribute("GlobalVariableDetails");
							if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
									.contains(PennantConstants.NCHAR))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.NVARCHAR))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.VARCHAR))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.CHAR))) {
								excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ","
										+ PennantConstants.CALCVALUE + ",";
							} else if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
									.contains(PennantConstants.DATETIME))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.SMALLDATETIME))) {
								excludeFields = "," + PennantConstants.STATICTEXT + "," + PennantConstants.CALCVALUE
										+ "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ",";
							} else if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
									.contains(PennantConstants.NUMERIC))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.BIGINT))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.DECIMAL))
									|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
											.contains(PennantConstants.INT))) {
								excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ","
										+ PennantConstants.STATICTEXT + ",";
							}

						}
					}
				} else {
					excludeFields = "," + PennantConstants.SUBQUERY + "," + PennantConstants.DBVALUE + "," + ","
							+ PennantConstants.CALCVALUE + "," + PennantConstants.STATICTEXT + ",";
				}
			} else if ((selectedOperator.equals(Labels.getLabel("GREATER_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("GREATEREQUAL_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("LESS_LABEL")))
					|| (selectedOperator.equals(Labels.getLabel("LESSEQUAL_LABEL")))) {
				if (leftOperand instanceof Combobox) {
					if (((Combobox) leftOperand).getSelectedIndex() != 0) {
						if ((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
								.contains(PennantConstants.DATETIME))
								|| (((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase()
										.contains(PennantConstants.SMALLDATETIME))) {
							excludeFields = "," + PennantConstants.DBVALUE + "," + PennantConstants.SUBQUERY + ","
									+ PennantConstants.STATICTEXT + "," + PennantConstants.CALCVALUE + ",";
						} else {
							excludeFields = "," + PennantConstants.SUBQUERY + "," + PennantConstants.DBVALUE + ","
									+ PennantConstants.STATICTEXT + ",";
						}
					}
				} else {
					excludeFields = "," + PennantConstants.SUBQUERY + "," + PennantConstants.DBVALUE + ","
							+ PennantConstants.STATICTEXT + ",";
				}
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
		return excludeFields;
	}

	// GUI OPERATIONS

	/**
	 * Creating a new Operand based on Operand Type
	 */
	private void createOperand(String uUID, Combobox operandType, String value) {
		space = new Space();
		space.setId(uUID + "_space");
		space.setWidth("5px");
		if (operandType.getSelectedIndex() == 0) {
			Combobox operand = new Combobox();
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setReadonly(true);
			operand.setId(uUID);
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
			operand.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)) { // STATICTEXT
			Textbox operand = new Textbox();
			operand.setMaxlength(50);
			operand.setId(uUID);
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
			operand.setFocus(true);
			if (value != null && value.contains("(")) {
				operand.setValue(value.replace("(", "").replace(")", ""));
			} else {
				operand.setValue(value);
			}

			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)) { // GLOBALVAR
			Combobox operand = new Combobox();
			operand.setId(uUID);
			String operandtype = operandType.getSelectedItem().getValue();
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);

			if (operand.getId().endsWith("_leftOperand")) {
				for (int i = 0; i < globalVariableList.size(); i++) {
					GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(globalVariable.getName());
					comboitem.setValue(globalVariable.getName());
					comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
					comboitem.setAttribute("GlobalVariableDetails", globalVariable);
					comboitem.setAttribute("OperandType", operandtype);
					operand.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getName()))) {
						operand.setSelectedItem(comboitem);
					}
					// comboitem=null;
				}
			} else if (operand.getId().endsWith("_rightOperand")) {
				Treecell treeCell = (Treecell) operandType.getParent();
				Combobox leftOperandType = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperandType");
				if (value != null) {
					if ((" ").equals(value)) {
						operand.setVisible(false);
					}
				}
				if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)) {
					for (int i = 0; i < globalVariableList.size(); i++) {
						GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
						comboitem = new Comboitem();
						comboitem.setLabel(globalVariable.getName());
						comboitem.setValue(globalVariable.getName());
						comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
						comboitem.setAttribute("GlobalVariableDetails", globalVariable);
						comboitem.setAttribute("OperandType", operandtype);
						operand.appendChild(comboitem);
						if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getName()))) {
							operand.setSelectedItem(comboitem);
						}
						// comboitem=null;
					}
				}
				if (treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand") instanceof Combobox) {
					Combobox leftOperand = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
					if (globalVariableList != null && globalVariableList.size() > 0) {
						for (int i = 0; i < globalVariableList.size(); i++) {
							GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
							comboitem = new Comboitem();
							if (leftOperand.getSelectedIndex() > 0) {
								if (leftOperand.getSelectedItem().getTooltiptext()
										.contains(globalVariable.getType().toUpperCase())) {
									comboitem.setLabel(globalVariable.getName());
									comboitem.setValue(globalVariable.getName());
									comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
									comboitem.setAttribute("GlobalVariableDetails", globalVariable);
									comboitem.setAttribute("OperandType", operandtype);
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
					// Textbox leftOperand=(Textbox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
					String charDataTypes = "varchar,nvarchar,nchar,char";
					if (globalVariableList != null && globalVariableList.size() > 0) {
						for (int i = 0; i < globalVariableList.size(); i++) {
							GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
							comboitem = new Comboitem();
							if (charDataTypes.contains(globalVariable.getType())) {
								comboitem.setLabel(globalVariable.getName());
								comboitem.setValue(globalVariable.getName());
								comboitem.setTooltiptext("Data Type : " + globalVariable.getType().toUpperCase());
								comboitem.setAttribute("GlobalVariableDetails", globalVariable);
								comboitem.setAttribute("OperandType", operandtype);
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
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)) { // FIELDLIST
			String operandtype = operandType.getSelectedItem().getValue();
			if (objectFieldList != null) {
				this.objectFieldList.clear();
			}
			this.objectFieldList = PennantAppUtil.getQBFieldDetails(queryModule.getQueryModuleCode());
			Combobox operand = new Combobox();
			operand.setId(uUID);
			if (value != null) {
				if (("").equals(value)) {
					operand.setVisible(false);
				}
			}
			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);
			if (operand.getId().endsWith("_leftOperand")) {
				if (objectFieldList != null && objectFieldList.size() > 0) {
					for (int i = 0; i < objectFieldList.size(); i++) {
						QBFieldDetail fieldDetails = (QBFieldDetail) objectFieldList.get(i);
						comboitem = new Comboitem();
						comboitem.setLabel(fieldDetails.getQbFldName() + "(" + fieldDetails.getQbFldLen() + ")");
						comboitem.setValue(fieldDetails.getQbFldName());

						comboitem.setTooltiptext("Data Type : " + fieldDetails.getQbFldType().toUpperCase());
						comboitem.setAttribute("FieldDetails", fieldDetails);
						comboitem.setAttribute("OperandType", operandtype);
						operand.appendChild(comboitem);
						operand.setAttribute("FieldDetails", fieldDetails);
						if (StringUtils.trimToEmpty(value)
								.equals(StringUtils.trimToEmpty(fieldDetails.getQbFldName()))) {
							operand.setSelectedItem(comboitem);
						}
					}
				}

			} else if (operand.getId().endsWith("_rightOperand")) {
				Treecell treeCell = (Treecell) operandType.getParent();
				if (value != null) {
					if ((" ").equals(value)) {
						operand.setVisible(false);
					}
				}
				if (treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand") instanceof Combobox) {
					Combobox leftOperand = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
					if (objectFieldList != null && objectFieldList.size() > 0) {
						for (int i = 0; i < objectFieldList.size(); i++) {
							QBFieldDetail fieldDetails = (QBFieldDetail) objectFieldList.get(i);
							comboitem = new Comboitem();
							if (leftOperand.getSelectedIndex() > 0) {
								if (leftOperand.getSelectedItem().getTooltiptext()
										.contains(fieldDetails.getQbFldType().toUpperCase())) {
									comboitem.setLabel(
											fieldDetails.getQbFldName() + "(" + fieldDetails.getQbFldLen() + ")");
									comboitem.setValue(fieldDetails.getQbFldName());
									comboitem
											.setTooltiptext("Data Type : " + fieldDetails.getQbFldType().toUpperCase());
									comboitem.setAttribute("FieldDetails", fieldDetails);
									comboitem.setAttribute("OperandType", operandtype);
									operand.appendChild(comboitem);
									operand.setAttribute("FieldDetails", fieldDetails);
									if (StringUtils.trimToEmpty(value)
											.equals(StringUtils.trimToEmpty(fieldDetails.getQbFldName()))) {
										operand.setSelectedItem(comboitem);
									}
								}
							}
						}
					}
				} else {
					// Textbox leftOperand=(Textbox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
					String charDataTypes = "varchar,nvarchar,nchar,char";
					if (objectFieldList != null && objectFieldList.size() > 0) {
						for (int i = 0; i < objectFieldList.size(); i++) {
							QBFieldDetail fieldDetails = (QBFieldDetail) objectFieldList.get(i);
							comboitem = new Comboitem();
							if (charDataTypes.contains(fieldDetails.getQbFldType())) {
								comboitem
										.setLabel(fieldDetails.getQbFldName() + "(" + fieldDetails.getQbFldLen() + ")");
								comboitem.setValue(fieldDetails.getQbFldName());
								comboitem.setTooltiptext("Data Type : " + fieldDetails.getQbFldType().toUpperCase());
								comboitem.setAttribute("FieldDetails", fieldDetails);
								comboitem.setAttribute("OperandType", operandtype);
								operand.appendChild(comboitem);
								operand.setAttribute("FieldDetails", fieldDetails);
								if (StringUtils.trimToEmpty(value)
										.equals(StringUtils.trimToEmpty(fieldDetails.getQbFldName()))) {
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
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.CALCVALUE)) { // CALCVALUE
			Longbox operand = new Longbox();
			operand.setMaxlength(20);
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setValue(value != null ? Long.parseLong(value) : 0);
			operand.setId(uUID);
			operand.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.FUNCTION)) { // FUNCTION
			Longbox operand = new Longbox();
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setId(uUID);
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.SUBQUERY)) { // SUBQUERY
			Combobox operand = new Combobox();
			operand.setId(uUID);
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);
			for (int i = 0; i < subQueriesList.size(); i++) {
				Query query = (Query) subQueriesList.get(i);
				comboitem = new Comboitem();
				comboitem.setLabel("${" + query.getQueryCode() + "}");
				comboitem.setValue("${" + query.getQueryCode() + "}");

				comboitem.setTooltiptext(query.getSqlQuery());
				operand.appendChild(comboitem);
				if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty("${" + query.getQueryCode() + "}"))) {
					operand.setSelectedItem(comboitem);
				}
			}
			operand.setWidth("190px");
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(operand, space);
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		} else if (operandType.getSelectedItem().getValue().equals(PennantConstants.DBVALUE)) { // DBVALUE
			Textbox operand = new Textbox();
			operand.setId(uUID);
			if (value != null) {
				if (StringUtils.isWhitespace(value)) {
					operand.setVisible(false);
				}
			}
			if (value != null && value.contains("(")) {
				operand.setValue(value.replace("(", "").replace(")", ""));
			} else {
				operand.setValue(value);
			}
			operand.setReadonly(true);
			operandType.getParent().insertBefore(space, operandType.getNextSibling().getNextSibling());
			Space space1 = new Space();
			space1.setWidth("5px");
			Treecell treeCell = (Treecell) operandType.getParent();
			Button btnSearch = new Button();
			btnSearch.setLabel("Search");
			btnSearch.setId(treeCell.getId() + "_button");
			if (value != null) {
				btnSearch.setDisabled(true);
			}
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
		}
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
	public void onSearchButtonClick(ForwardEvent event) throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		Component button = (Component) event.getOrigin().getTarget();
		Textbox lovText = (Textbox) button.getAttribute("Operand");
		Combobox operandType = (Combobox) button.getAttribute("OperandType");
		Treecell treeCell = (Treecell) operandType.getParent();
		Combobox leftOperand = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_leftOperand");
		QBFieldDetail fielddetails = (QBFieldDetail) ((Combobox) leftOperand).getSelectedItem()
				.getAttribute("FieldDetails");
		Combobox operator = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_operator");
		String moduleCode = fielddetails.getModuleCode();
		// Filter[] filters = new Filter[1] ;
		// filters[0]= new Filter("EntityCode", entityCode, Filter.OP_EQUAL);

		if (StringUtils.isNotBlank(lovText.getValue())) {
			lovText.setConstraint("");
			lovText.setValue("");
		}

		if ((operator.getSelectedItem().getLabel().equals(Labels.getLabel("IN_LABEL")))
				|| (operator.getSelectedItem().getLabel().equals(Labels.getLabel("NOTIN_LABEL")))) {
			Map<String, Object> selectedValues = new HashMap<String, Object>();
			if (StringUtils.isNotBlank(lovText.getValue())) {
				selectedValues.put(lovText.getValue().trim().replace(",", ""),
						lovText.getValue().trim().replace(",", ""));
			}
			Object dataObject = ExtendedMultipleSearchListBox.show(this, moduleCode, selectedValues);
			if (dataObject instanceof String) {
				lovText.setValue(dataObject.toString());

			} else {
				@SuppressWarnings("unchecked")
				Map<String, Object> details = (Map<String, Object>) dataObject;
				if (details != null) {
					String multivalues = details.keySet().toString();
					lovText.setValue(multivalues.replace("[", "").replace("]", ""));
				}
			}
		} else {
			Object dataObject = ExtendedSearchListBox.show(this, moduleCode);
			if (dataObject instanceof String) {
				lovText.setValue(dataObject.toString());
			} else {
				String fieldValue = "";
				String fieldString = ModuleUtil.getLovFields(fielddetails.getModuleCode())[0];
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
	}

	/**
	 * Get Query from the tree
	 * 
	 * @param treeChildren
	 * @return
	 */
	public String getQuery(Treechildren treeChildren) {
		List<Component> treeItems = treeChildren.getChildren();
		String uUID = "";
		String value = "";
		String query = "";
		Treecell treeCell;
		Component component;
		Treeitem treeItem;
		for (int i = 0; i < treeItems.size(); i++) {
			treeItem = (Treeitem) treeItems.get(i);
			treeCell = (Treecell) treeItem.getTreerow().getChildren().get(0);
			uUID = treeCell.getId();
			component = treeCell.getFellowIfAny(uUID + "_logicalOperator");
			if (component != null) {
				actualBlock += "~(" + "|";
				value = getOperandValue(component);
				query += value + " ";
				actualBlock += value + "|";
			} else {
				actualBlock += "(" + "|";
			}
			query += "( ";
			component = treeCell.getFellowIfAny(uUID + "_leftOperandType");
			value = getOperandValue(component);
			actualBlock += value + "|";

			component = treeCell.getFellowIfAny(uUID + "_leftOperand");
			Combobox operator = (Combobox) treeCell.getFellowIfAny(uUID + "_operator");
			if ((((Combobox) operator).getSelectedItem().getLabel().equals(Labels.getLabel("EXISTS_LABEL")))
					|| (((Combobox) operator).getSelectedItem().getLabel()
							.equals(Labels.getLabel("NOTEXISTS_LABEL")))) {
				value = "";
			} else {
				value = getOperandValue(component);
			}

			query += value + " ";
			actualBlock += value.replace("'", "") + "|";

			component = treeCell.getFellowIfAny(uUID + "_operator");
			value = getOperandValue(component);
			query += value + " ";
			if ((((Combobox) treeCell.getFellowIfAny(uUID + "_rightOperandType")).isVisible())
					|| (((Component) treeCell.getFellowIfAny(uUID + "_rightOperand")).isVisible())) {
				actualBlock += value + "|";

				component = treeCell.getFellowIfAny(uUID + "_rightOperandType");
				value = getOperandValue(component);
				actualBlock += value + "|";

				component = treeCell.getFellowIfAny(uUID + "_rightOperand");

				Combobox aOperator = (Combobox) treeCell.getFellowIfAny(treeCell.getId() + "_operator");
				if ((aOperator.getSelectedItem().getLabel().equals(Labels.getLabel("IN_LABEL")))
						|| (aOperator.getSelectedItem().getLabel().equals(Labels.getLabel("NOTIN_LABEL")))) {
					value = getRightOperandValue(component);
				} else {
					value = getOperandValue(component);
				}
				query += value + " ";

				actualBlock += value.replace("'", "") + "|";
			} else {
				actualBlock += value + "|";

				component = treeCell.getFellowIfAny(uUID + "_rightOperandType");
				actualBlock += " " + "|";

				component = treeCell.getFellowIfAny(uUID + "_rightOperand");
				actualBlock += " " + "|";
			}
			if (treeItem.getTreechildren() != null) {
				query += getQuery(treeItem.getTreechildren());
			}
			query += ") ";
			actualBlock += "~)|";
		}
		query += " ";
		this.queryValue = query;
		return query;
	}

	/**
	 * Get Value from the components
	 * 
	 * @param component
	 * @return
	 */
	public String getOperandValue(Component component) {
		if (component instanceof Combobox) {
			if (((Combobox) component).isVisible()) {
				if (((Combobox) component).getSelectedIndex() <= 0) {
					throw new WrongValueException(component, Labels.getLabel("const_NO_SELECT"));
				}
				return ((Combobox) component).getSelectedItem().getValue().toString();
			} else {
				((Combobox) component).setConstraint("");
				return ((Combobox) component).getSelectedItem().getValue().toString();
			}

		} else if (component instanceof Textbox) {
			if (((Textbox) component).isVisible()) {
				((Textbox) component).setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
				return "'" + ((Textbox) component).getValue().trim() + "'";
			} else {
				((Textbox) component).setConstraint("");
				((Textbox) component).setValue("");

				return ((Textbox) component).getValue();
			}
		} else if (component instanceof Longbox) {
			if (((Longbox) component).isVisible()) {
				((Longbox) component).setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
				return ((Longbox) component).getValue().toString();

			}
		} else if (component instanceof Longbox) {
			if (((Longbox) component).isVisible()) {
				((Longbox) component).setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
				return ((Longbox) component).getValue().toString();
			} else {
				((Longbox) component).setConstraint("");
				((Longbox) component).setValue((long) 0);
				return ((Longbox) component).getValue().toString();
			}
		}
		return null;
	}

	/**
	 * Get Value for the right operand
	 * 
	 * @param component
	 * @return
	 */
	/**
	 * Build treee from the actual Block
	 * 
	 * @param actualBlock
	 */
	public String getRightOperandValue(Component component) {
		if (component instanceof Combobox) {
			if (((Combobox) component).isVisible()) {
				if (((Combobox) component).getSelectedIndex() <= 0) {
					throw new WrongValueException(component, Labels.getLabel("const_NO_SELECT"));
				}
				return ((Combobox) component).getSelectedItem().getValue();
			} else {
				((Combobox) component).setConstraint("");
				return ((Combobox) component).getSelectedItem().getValue().toString();
			}
		} else if (component instanceof Textbox) {
			if (((Textbox) component).getValue().contains(",")) {
				if (!((Textbox) component).isReadonly()) {
					String staticvalue = ((Textbox) component).getValue();
					String[] values = staticvalue.split(",");
					String newValue = StringUtils.join(values, "'");
					return "(" + "'" + newValue.replace("'", "','") + "'" + ")";
				} else {
					String staticvalue = ((Textbox) component).getValue().trim();
					String[] values = staticvalue.split(",");
					String newValue = StringUtils.join(values, "'");

					return "(" + "'" + newValue.trim().replace("' ", "','") + "'" + ")";
				}

			} else {
				return "(" + "'" + ((Textbox) component).getValue().trim() + "'" + ")";

			}
		}
		return null;
	}

	public void buildQuery(String actualBlock) {
		if (StringUtils.isBlank(actualBlock)) {

		} else {
			String[] str = actualBlock.split("\\~");
			List<Treechildren> treeChildrens = new ArrayList<Treechildren>();
			List<Treeitem> treeItems = new ArrayList<Treeitem>();
			Treechildren mainTreeChildren = new Treechildren();
			mainTreeChildren.setParent(tree);
			treeChildrens.add(mainTreeChildren);
			for (String string : str) {
				if (string.startsWith("(")) {
					string = StringUtils.substring(string, 2);
					Treeitem treeItem = createNewCondition(string);
					if (treeItems.size() != 0) {
						if (treeItems.get(treeItems.size() - 1).getTreechildren() == null) {
							Treechildren treechildren = new Treechildren();
							treeChildrens.add(treechildren);
							treechildren.setParent(treeItems.get(treeItems.size() - 1));
						}
					}
					treeItems.add(treeItem);
					treeItem.setParent(treeChildrens.get(treeChildrens.size() - 1));
				} else {
					Treeitem treeItem = treeItems.get(treeItems.size() - 1);

					if (treeChildrens.size() > 1 && treeItem.getTreechildren() != null) {
						treeChildrens.remove(treeChildrens.size() - 1);
					}
					treeItems.remove(treeItems.size() - 1);
				}
			}
		}

	}

	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list
	 */
	public void fillComboBoxByValueLabels(Combobox combobox, String value, List<ValueLabel> list,
			String excludeFields) {
		fillComboBox(combobox, value, list, excludeFields);
	}

	/**
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
			if (!StringUtils.trimToEmpty(excludeFields).contains("," + valueLabel.getValue() + ",")) {
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

	public void setEntityCode(String entityCode) {
		// this.entityCode = entityCode;
		this.globalVariableList = SysParamUtil.getGlobaVariableList();
		this.subQueriesList = PennantAppUtil.getSubqueries();
	}

	public boolean validateQuery() throws InterruptedException {
		if (this.editable) {
			sqlQuery = "";
			actualBlock = "";
			textbox.setValue("");
			sqlQuery = getQuery(tree.getTreechildren());
		}
		String actualQuery = StringReplacement.getReplacedQuery(sqlQuery, globalVariableList, subQueriesList);
		this.queryValue = actualQuery;
		JdbcSearchObject searchObject = new JdbcSearchObject();
		searchObject.addTabelName(queryModule.getTableName());
		searchObject.addWhereClause(actualQuery);
		try {
			searchObject.addFields(PennantAppUtil.getQueryModuleCustomColumns(queryModule.getResultColumns()));
			// PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			// pagedListService.getCount(searchObject);
			// this.sqlQuery=queryValue;
			this.textbox.setValue(actualQuery);
			return true;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			MessageUtil.showError(e.getLocalizedMessage());
			return false;
		}
	}

	public void simulateQuery() throws InterruptedException {
		if (!this.editable) {
			String queryTemp = this.sqlQuery;
			validateQuery();
			createResultWindow();
			this.sqlQuery = queryTemp;
		} else {
			validateQuery();
			createResultWindow();
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private void createResultWindow() {
		Window window = new Window();
		window.setTitle("Query Results");
		window.setStyle("border:1px solid #C5C5C5;");
		window.setClosable(true);
		window.setParent(this);
		Listbox listBox = new Listbox();
		listBox.setWidth("100%");
		listBox.setParent(window);
		listBox.setFixedLayout(true);
		// listBox.setVflex(true);
		listBox.setSpan(true);
		// listBox.setStyle("border:1px solid #C5C5C5;");
		Listhead listHead = new Listhead();
		listHead.setParent(listBox);
		String[] str = queryModule.getDisplayColumns().split(",");
		window.setHeight("80%");
		window.setWidth("95%");
		Listheader listHeader;
		for (String string : str) {
			if (string.contains(":A") && !string.contains(":AC")) {
				listHeader = new Listheader(string.substring(0, string.indexOf(":")));
			} else if (string.contains(":")) {
				listHeader = new Listheader(string.substring(0, string.indexOf(":")));
			} else {
				listHeader = new Listheader(string);
			}
			listHeader.setParent(listHead);
			listHeader.setHflex("min");
		}
		Paging paging = new Paging();
		paging.setParent(window);
		paging.setPageSize(listRows);
		paging.setHeight("35px");
		if (("0px").equals(listBoxHeight)) {
			listBox.setHeight(getListRows() * 25 + "px");
		} else {
			listBox.setHeight(listBoxHeight);
		}
		window.setHeight(listBox.getHeight() + 140 + "px");
		paging.setDetailed(true);
		JdbcSearchObject searchObject = new JdbcSearchObject();
		searchObject.addTabelName(queryModule.getTableName());
		searchObject.addWhereClause(this.textbox.getValue());
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		try {
			searchObject.addFields(
					PennantAppUtil.getQueryModuleCustomColumns(queryModule.getResultColumns().toLowerCase()));
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		List<?> list = pagedListService.getBySearchObject(searchObject);
		setListModelList(new ListModelList(list));
		listBox.setModel(getListModelList());
		listBox.setItemRenderer(new ListBoxItemRenderer());
		window.doModal();
	}

	public QueryModule getQueryModule() {
		return queryModule;
	}

	public void setQueryModule(QueryModule queryModule) {
		this.queryModule = queryModule;
	}

	public String getActualBlock() {
		return actualBlock;
	}

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public String getSqlQuery() {
		return sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.textbox.setValue(sqlQuery);
		this.sqlQuery = sqlQuery;
		System.out.println(this.textbox.getValue());
	}

	public void getResults() {

	}

	public class ListBoxItemRenderer implements ListitemRenderer {

		public ListBoxItemRenderer() {
		    super();
		}

		String[] str = queryModule.getResultColumns().split(",");

		@Override
		public void render(Listitem item, Object obj, int arg2) throws Exception {
			LinkedHashMap map = (LinkedHashMap) obj;
			Listcell listCell;
			for (int i = 0; i < str.length; i++) {
				int index = str[i].indexOf(':');
				if (index > 0) {
					String data = map.get(str[i].substring(0, index).toLowerCase()) == null ? "0"
							: map.get(str[i].substring(0, index).toLowerCase()).toString();
					String type = str[i].substring(index + 1);
					// A == Amount Field
					// D == Date Field
					// AC == Account Number
					// CD == Date with Century
					if ("A".equalsIgnoreCase(type)) {
						listCell = new Listcell(CurrencyUtil.format(new BigDecimal(data), 2));
						listCell.setParent(item);
						listCell.setStyle("text-align:right;");
					} else if ("D".equalsIgnoreCase(type)) {
						if (StringUtils.isBlank(data) || "0".equals(data)) {
							listCell = new Listcell("-- NO VALUE --");
							listCell.setParent(item);
						} else {
							listCell = new Listcell(data);
							listCell.setParent(item);
						}
					} else if ("AC".equalsIgnoreCase(type)) {
						listCell = new Listcell(data);
						listCell.setParent(item);
					} else if ("CD".equalsIgnoreCase(type)) {
						if (StringUtils.isBlank(data) || "0".equals(data)) {
							listCell = new Listcell("-- NO VALUE --");
							listCell.setParent(item);
						} else {
							listCell = new Listcell(DateUtil.formatToLongDate(DateUtil.parse(data, "yyMMdd")));
							listCell.setParent(item);
						}
					} else {
						listCell = new Listcell(data);
						listCell.setParent(item);
					}
				} else {
					listCell = new Listcell(map.get(str[i].trim().toLowerCase()).toString());
					listCell.setParent(item);
				}

			}
		}
	}

	public int getListRows() {
		return listRows;
	}

	public void setListRows(int listRows) {
		this.listRows = listRows;
	}

	public String getTreeHeight() {
		return treeHeight;
	}

	public void setTreeHeight(String treeHeight) {
		this.treeHeight = treeHeight;
		this.tree.setHeight(treeHeight);
	}

	public String getTextBoxHeight() {
		return textBoxHeight;
	}

	public void setTextBoxHeight(String textBoxHeight) {
		this.textbox.setHeight(textBoxHeight);
		this.textBoxHeight = textBoxHeight;
	}

	public String getListBoxHeight() {
		return listBoxHeight;
	}

	public void setListBoxHeight(String listBoxHeight) {
		this.listBoxHeight = listBoxHeight;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		if (editable) {
			tree.setVisible(true);
			if (StringUtils.isBlank(actualBlock)) {
				tree.getChildren().clear();
				this.uniqueId = 0;
				Treechildren treeChildren = new Treechildren();
				treeChildren.appendChild(createNewCondition(null));
				tree.appendChild(treeChildren);

			}
		} else {
			tree.setVisible(false);
		}
		this.editable = editable;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ModuleMapping getModuleMapping() {
		return moduleMapping;
	}

	public void setModuleMapping(ModuleMapping moduleMapping) {
		this.moduleMapping = moduleMapping;
	}

	public ListModelList getListModelList() {
		return listModelList;
	}

	public void setListModelList(ListModelList listModelList) {
		this.listModelList = listModelList;
	}
}
