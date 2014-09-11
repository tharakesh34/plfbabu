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
 * FileName    		:  JavaScriptBuilder.java		                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-07-2013    														*
 *                                                                  						*
 * Modified Date    :  23-05-2013    														*
 *                                                                  						*
 * Description 		:  Query Builder                  		                          		*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-07-2013       Chaitanya Varma	   	      0.1       		                            * 
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

package com.pennant;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;

import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.StringReplacement;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedMultipleSearchListBox;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;



public class JavaScriptBuilder extends Groupbox {

	private static final long serialVersionUID = 1L;
	protected Tree tree; // autowired
	protected Space space; // autowired
	public Codemirror codemirror; // autowired

	public String actualBlock;
	public String sqlQuery = "";
	public String actualQuery = "";
	public String ruleType = "";
	private int uniqueId = 0;
	private RuleModule ruleModule;
	private String entityCode;
	private boolean editable = true;
	private String treeHeight = "400px";
	private String textBoxHeight = "125px";
	private int logicCount = 0;
	//private int count=0;
	private ModuleMapping moduleMapping=null;
	Map<String,Object> fieldObjectMap = new HashMap<String, Object>();


	private String query = "";
	private String resultModule  = "";

	private List<ValueLabel> 	 operatorsList			=	PennantStaticListUtil.getOperators("JS"); //retrieve all the operators
	private List<ValueLabel> 	 operandTypesList		=	PennantStaticListUtil.getOperandTypes("JS"); //retrieve all selection types
	private List<ValueLabel> 	 logicalOperatorsList 	= 	PennantStaticListUtil.getLogicalOperators("JS");// retrieve values
	private List<GlobalVariable> globalVariableList 	=  	SystemParameterDetails.getGlobaVariableList();// retrieve values from table--GlobalVariable
	private List<RBFieldDetail>  objectFieldList 		= 	null;// retrieve values
	private List<ValueLabel>  	 resultSelectionList    =   new ArrayList<ValueLabel>();

	private String module="";
	private boolean resultMultiSelection = false;
	private Map<String, Object> multiSelectValue = new HashMap<String, Object>();
	private String multiSelectModuleName;

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Query object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public JavaScriptBuilder() {
		tree = new Tree();
		this.appendChild(tree);
		tree.setZclass("z-dottree");
		tree.setHeight(treeHeight);
		codemirror = new Codemirror();
		codemirror.setWidth("100%");
		//textbox.setMultiline(true);
		codemirror.setReadonly(true);
		//textbox.setRows(10);
		codemirror.setConfig("lineNumbers:true");
		codemirror.setSyntax("js");
		//codemirror.setHeight(this.textBoxHeight);
		this.appendChild(codemirror);
		this.setHeight("400px");
		tree.setHeight("350px");
		codemirror.setVisible(false);
		createNewQuery();
	}

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * zul-file is called with a parameter for a selected Query object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public JavaScriptBuilder(int uUID) {
		this.uniqueId = uUID;
		tree = new Tree();
		this.appendChild(tree);
		tree.setZclass("z-dottree");
		tree.setHeight(treeHeight);
		codemirror = new Codemirror();
		codemirror.setWidth("100%");
		codemirror.setReadonly(true);
		codemirror.setHeight(this.textBoxHeight);
		codemirror.setSyntax("js");
		this.appendChild(codemirror);
		this.setHeight("1000px");
		tree.setHeight("240px");
		codemirror.setVisible(false);
		createNewQuery();
	}

	/**
	 * Create new query
	 */
	public void createNewQuery(){

	}

	/**
	 * Create a new Condition
	 * @param condition(String) to build new condition based on the dbQuery
	 * @return treeItem(TreeItem)
	 */
	private Treeitem createNewCondition(String condition,String label) {
		int i =0;
		String [] str = null;
		String uUID= "cond"+this.getId()+uniqueId++;
		boolean buildCondition = false;	
		Space space;

		if(!StringUtils.trimToEmpty(condition).equals("")){
			if(condition.contains(" ||") && !condition.contains("else")){
				condition=condition.replace(" ||", "OR");
			}
			str = condition.split("\\|");
			buildCondition = true;
		}	

		Treeitem treeItem = new Treeitem();
		Treerow treeRow = new Treerow();
		treeRow.setParent(treeItem);
		Treecell treeCell = new Treecell();
		treeCell.setParent(treeRow);

		treeCell.setId(uUID);

		//Creating remove button
		Button removeCondition = new Button();
		removeCondition.setImage("/images/icons/delete.png");
		removeCondition.addForward("onClick", this,
				"onRemoveCondition", treeItem);
		removeCondition.setId(uUID+"_RmvCond");
		removeCondition.setParent(treeCell);

		space = new Space();
		space.setWidth("5px");
		space.setParent(treeCell);
		String statement ="";
		if(label.equals("AND") || (buildCondition && str[i].trim().equals("&&"))||(buildCondition && str[i].trim().equals("OR"))){
			Combobox logicalOperator = new Combobox();
			logicalOperator.setWidth("70px");
			logicalOperator.setParent(treeCell);
			logicalOperator.setId(uUID+"_logicalOperator");
			logicalOperator.setReadonly(true);
			fillComboBox(logicalOperator, buildCondition?str[i++].replace("OR","||"):null, logicalOperatorsList, null);
		}else{
			//Creating remove button
			Label label_condition = new Label(buildCondition?str[i++]:label);
			label_condition.setSclass("queryBuilder_Label");
			label_condition.setId(uUID+"_statement");
			label_condition.setParent(treeCell);
			statement = label_condition.getValue();
		}

		space = new Space();
		space.setWidth("5px");
		space.setParent(treeCell);

		//Creating logical operators
		if((uniqueId == 1 || uniqueId == 1001)){
			removeCondition.setDisabled(true);
		}else{
		}

		//Leftoperand creation
		Combobox leftOperandType = new Combobox();
		leftOperandType.setWidth("120px");
		leftOperandType.setParent(treeCell);
		leftOperandType.setId(uUID+"_leftOperandType");
		leftOperandType.setReadonly(true);
		String excludeFields =  ","+PennantConstants.DBVALUE+","+PennantConstants.CALCVALUE+","+PennantConstants.STATICTEXT+","+PennantConstants.GLOBALVAR+",";
		fillComboBox(leftOperandType, buildCondition?str[i++]:null,operandTypesList,excludeFields);
		leftOperandType.addForward("onChange", this,
				"onChangeOperandType", null);

		space = new Space();
		space.setId("space_"+leftOperandType.getId());
		space.setWidth("5px");
		space.setParent(treeCell);

		createOperand(uUID+"_leftOperand", leftOperandType, buildCondition?str[i++]:null);

		//Operators creation
		Combobox operator = new Combobox();
		operator.setParent(treeCell);
		operator.setId(uUID+"_operator");
		operator.setWidth("120px");
		operator.setAttribute("TreeCell", treeCell);
		excludeFields="";
		fillComboBox(operator, buildCondition?str[i++]:null,operatorsList,excludeFields);
		operator.addForward("onChange", this, "onChangeOperator",null);

		space = new Space();
		space.setId("space_"+operator.getId());
		space.setWidth("5px");
		space.setParent(treeCell);

		//Rightoperand creation
		Combobox rightOperandType = new Combobox();
		rightOperandType.setParent(treeCell);
		rightOperandType.setId(uUID+"_rightOperandType");
		rightOperandType.setWidth("120px");
		excludeFields="";
		if(condition!=null){
			if(condition.contains("DBVALUE")){
				excludeFields="";
			}else{
				excludeFields = "," + PennantConstants.DBVALUE + ",";
			}
		}
		else{
			excludeFields = "," + PennantConstants.DBVALUE + "," ;
		}
		fillComboBox(rightOperandType, buildCondition?str[i++]:null,operandTypesList,excludeFields);
		rightOperandType.addForward("onChange", this,
				"onChangeOperandType", null );

		space = new Space();
		space.setId("space_"+rightOperandType.getId());
		space.setWidth("5px");
		space.setParent(treeCell);

		createOperand(uUID+"_rightOperand", rightOperandType,  buildCondition?str[i++]:null);

		//Creating button for adding a new condition																			//AND		
		Label logicalOperator = new Label();
		logicalOperator.setValue("AND");
		logicalOperator.setId(uUID+"_btn_AND");
		logicalOperator.setSclass("button_Label");
		logicalOperator.addForward("onClick", this,
				"onClickLogicalOperator", treeItem);
		logicalOperator.setParent(treeCell);
		if(buildCondition ){
			logicalOperator.setVisible(str[i++].equals("1")?true:false);
		}

		space = new Space();
		space.setId("space_"+logicalOperator.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		if(buildCondition ){
			space.setVisible(str[i-1].equals("1")?true:false);
		}

		//Creating button for adding a new condition																	         //ELSE
		Label addElseButton = new Label();
		addElseButton.setTooltiptext("Add Else Condition");
		addElseButton.setValue("ELSE");
		addElseButton.setId(uUID+"_btn_ELSE");
		addElseButton.addForward("onClick", this,
				"onClickElse", treeItem);
		addElseButton.setSclass("button_Label");
		addElseButton.setParent(treeCell);
		if(buildCondition ){
			addElseButton.setVisible(str[i++].equals("1")?true:false);
		}

		space = new Space();
		space.setId("space_"+addElseButton.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		if(buildCondition ){
			space.setVisible(str[i-1].equals("1")?true:false);
		}
		//Creating button for adding a new condition
		Label addElseIfButton = new Label();																					 //ELSE IF
		addElseIfButton.setTooltiptext("Add Else IF Condition");
		addElseIfButton.setValue("ELSE IF");
		addElseIfButton.setId(uUID+"_btn_ELSEIF");
		addElseIfButton.setSclass("button_Label");
		addElseIfButton.addForward("onClick", this,
				"onClickElseIF", treeItem);
		addElseIfButton.setParent(treeCell);
		if(buildCondition ){
			addElseIfButton.setVisible(str[i++].equals("1")?true:false);
		}


		space = new Space();
		space.setId("space_"+addElseIfButton.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		if(buildCondition ){
			space.setVisible(str[i-1].equals("1")?true:false);
		}

		//Creating button for adding a new condition
		Label addNestedIfButton = new Label();																					//Nested IF
		addNestedIfButton.setTooltiptext("Add Nested IF Condition");
		addNestedIfButton.setValue("NESTED IF");
		addNestedIfButton.setId(uUID+"_btn_NESTEDIF");
		addNestedIfButton.setSclass("button_Label");
		addNestedIfButton.addForward("onClick", this,
				"onClickNestedIf", treeItem);
		addNestedIfButton.setParent(treeCell);
		if(buildCondition ){
			addNestedIfButton.setVisible(str[i++].equals("1")?true:false);
		}


		space = new Space();
		space.setId("space_"+addNestedIfButton.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		if(buildCondition ){
			space.setVisible(str[i-1].equals("1")?true:false);
		}

		//Creating button for adding a new sub condition
		Label result = new Label();																								 //Calculation 
		result.setTooltiptext("Click for Add Return Value");
		result.setValue("CALCULATE");
		result.setId(uUID+"_btn_CALCULATE");
		if(resultSelectionList.size() <= 0){
			result.setSclass("button_Label");
		}else{
			result.setSclass("button_Label_Disable");
		}
		result.setParent(treeCell);
		if(buildCondition ){
			result.setVisible(str[i++].equals("1")?true:false);
		}

		space = new Space();
		space.setId("space_"+result.getId());
		space.setWidth("5px");
		space.setParent(treeCell);
		if(buildCondition ){
			space.setVisible(str[i-1].equals("1")?true:false);
		}
		//Creating button for adding a new sub condition
		if( resultSelectionList.size() <= 0){
			Textbox tb_result = new Textbox();
			tb_result.setId(uUID+"_RESULT");
			tb_result.setParent(treeCell);
			tb_result.setWidth("120px");
			tb_result.setSclass("javaScriptBuilderfont");
			if(resultSelectionList!=null ){
				if(resultSelectionList.size()>0){
					tb_result.setReadonly(true);
				}
			}
			if(buildCondition ){
				if(i>=str.length){
					tb_result.setVisible(false);
				}else{
					tb_result.setValue(buildCondition?str[i++]:"");
				}
			}
			result.addForward("onClick", this, "onClickCalculate", tb_result);
		}else {
			if(isMultiSelectionResult()){
				//Creating result button
				Hbox hbox = new Hbox();
				//hbox.setParent(treeCell);
				Button resultBtn = new Button();
				resultBtn.setWidth("28px");
				resultBtn.setHeight("20px");
				resultBtn.setImage("/images/icons/Right_Arrow.png");
				resultBtn.addForward("onClick", this,"onButtonClick", treeItem);
				resultBtn.setId(uUID+"_mailType");
				resultBtn.setDisabled(StringUtils.trimToEmpty(getMultiSelectModuleName()).equals(""));
				resultBtn.setParent(treeCell);
				space = new Space();
				space.setWidth("5px");
				space.setParent(treeCell);
				Textbox textbox = new Textbox();
				textbox.setId(uUID+"_mailType");
				textbox.setWidth("120px");
				textbox.setSclass("javaScriptBuilderfont");
				textbox.setReadonly(true);
				textbox.setValue(str == null || str[str.length-1] .equals("0")  || str[str.length-1] .equals("1") ? "" : str[str.length-1]);
				textbox.setTooltiptext(str == null || str[str.length-1].equals("0") || str[str.length-1]  .equals("1" )? "" : str[str.length-1]);
				textbox.setParent(treeCell);
				if(StringUtils.trimToEmpty(label).equals("AND")){
					resultBtn.setVisible(false);
				}
				if(buildCondition ){
					if(i>=str.length){
						hbox.setVisible(false);
					}
				}
				setMultiSelectValue(str == null ? null : str[str.length-1]);
			}else{
				Combobox mailType = new Combobox();
				mailType.setSclass("javaScriptBuilderComboitemfont");
				mailType.setParent(treeCell);
				mailType.setId(uUID+"_mailType");
				mailType.setWidth("120px");
				//excludeFields =  ","+PennantConstants.DBVALUE+",";
				Comboitem comboitem;
				comboitem = new Comboitem();
				comboitem.setValue("#");
				comboitem.setLabel(Labels.getLabel("Combo.Select"));
				mailType.appendChild(comboitem);
				mailType.setSelectedItem(comboitem);
				for (int j = 0; j < resultSelectionList.size(); j++) {
					ValueLabel  templateCode =  resultSelectionList.get(j);
					comboitem = new Comboitem();
					comboitem.setLabel(templateCode.getLabel());
					comboitem.setValue(templateCode.getValue());
					comboitem.setAttribute("MailTemplateDetails",templateCode.getValue());
					mailType.appendChild(comboitem);
					if(str != null && str[str.length-1].equals(templateCode.getValue())){
						mailType.setSelectedIndex(mailType.getItemCount()-1);
					}
					//comboitem=null;
				}
				if(buildCondition ){
					if(i>=str.length){
						mailType.setVisible(false);
					}
				}	
				//mailType.addForward("onChange", this,"onChangeOperandType", null );
			}
		}
		if(!StringUtils.trimToEmpty(condition).equals("") && statement.equals("else") ){
			treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand").setVisible(false);
			treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand_space").setVisible(false);
			treeCell.getFellowIfAny(treeCell.getId()+"_leftOperandType").setVisible(false);
			treeCell.getFellowIfAny("space_"+leftOperandType.getId()).setVisible(false);
			treeCell.getFellowIfAny(treeCell.getId()+"_operator").setVisible(false);
			treeCell.getFellowIfAny("space_"+operator.getId()).setVisible(false);
			treeCell.getFellowIfAny(treeCell.getId()+"_rightOperandType").setVisible(false);
			treeCell.getFellowIfAny("space_"+rightOperandType.getId()).setVisible(false);
			treeCell.getFellowIfAny(treeCell.getId()+"_rightOperand").setVisible(false);
			treeCell.getFellowIfAny(uUID+"_rightOperand_space").setVisible(false);

		}

		if(buildCondition==true){
			Combobox comboBox = (Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_operator");
			Combobox comboBoxOperandType = (Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_rightOperandType");
			String excludefields = getExcludeFieldsByOperandType(leftOperandType, "operator");
			fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludefields);
			Component leftOperand=treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
			if(leftOperand instanceof Combobox){
				if(((Combobox) leftOperand).getSelectedIndex()!=0 && leftOperandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)){
					RBFieldDetail fielddetails=(RBFieldDetail) ((Combobox) leftOperand).getSelectedItem().getAttribute("FieldDetails");
					String excludefields1 = getExcludeFieldsByOperands((Combobox) leftOperand, "operator",fielddetails);
					fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludefields1);

				}else if(((Combobox) leftOperand).getSelectedIndex()!=0 && leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)){
					GlobalVariable globalVariable=(GlobalVariable) ((Combobox) leftOperand).getSelectedItem().getAttribute("GlobalVariableDetails");
					String excludefields1 = getGlobalExcludeFieldsByOperands((Combobox) leftOperand, "operator",globalVariable);
					fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludefields1);

				}
			}
			String excludefields2=getExcludeFieldsByOperator(operator,leftOperand,leftOperandType);
			fillComboBox(comboBoxOperandType, comboBoxOperandType.getSelectedItem().getValue().toString(), operandTypesList, excludefields2);
		}

		return treeItem;
	}



	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * ON removing a condition
	 */
	public void onRemoveCondition(ForwardEvent event){
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell treeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		if(treeChildren.getFellowIfAny(treeCell.getId() + "_statement")!= null){
			if(((Label)treeChildren.getFellowIfAny(treeCell.getId() + "_statement")).getValue().equals("ELSE")){
				Treeitem firstTreeItem = (Treeitem) treeChildren.getChildren().get(0);
				Treecell firstTreeCell = (Treecell) firstTreeItem.getTreerow().getFirstChild();
				String currentuUID = firstTreeCell.getId();
				treeChildren.getFellowIfAny(currentuUID+"_btn_ELSE").setVisible(true);
				treeChildren.getFellowIfAny("space_"+currentuUID+"_btn_ELSE").setVisible(true);
			}else if(((Label)treeChildren.getFellowIfAny(treeCell.getId() + "_statement")).getValue().equals("IF")){
				Treeitem parentItem = (Treeitem) treeCell.getParent().getParent().getParent().getParent();
				String currentuUID = parentItem.getTreerow().getChildren().get(0).getId();
				System.out.println(parentItem.getFellowIfAny(currentuUID+"_btn_NESTEDIF").isVisible());
				parentItem.getFellowIfAny(currentuUID+"_btn_NESTEDIF").setVisible(true);
				parentItem.getFellowIfAny("space_" + currentuUID+"_btn_NESTEDIF").setVisible(true);
				treeChildren.detach();
			}
		}	
		treeItem.detach();
	}

	/**
	 * On selecting to add a new Condition
	 */
	public void onClickElseIF(ForwardEvent event){
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		Treeitem newCondition = createNewCondition(null, "ELSE IF");

		if(treeItem.getNextSibling() != null ){
			treeChildren.insertBefore(newCondition, treeItem.getNextSibling());
		}else{
			treeChildren.appendChild(newCondition);
		}
		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();  

		doSetButtonProperties(treeChildren,currentuUID, newuUID,"ELSEIF");
	}

	/**
	 * On selecting to add a new Condition
	 */
	public void onClickElse(ForwardEvent event){
		Treeitem treeItem = (Treeitem) event.getData();
		Treechildren treeChildren = (Treechildren) treeItem.getParent();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();

		Treeitem newCondition = createNewCondition(null, "ELSE");

		/*if(treeItem.getNextSibling() != null ){
			treeChildren.insertBefore(newCondition, treeItem.getNextSibling());
		}else{*/
		treeChildren.appendChild(newCondition);
		//}
		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();  

		doSetButtonProperties(treeChildren,currentuUID, newuUID,"ELSE");
	}

	/**
	 * On selecting to add a new sub Condition
	 * @param event
	 */
	public void onClickNestedIf (ForwardEvent event){
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treechildren treeChildren;
		if(treeItem.getTreechildren() != null){
			treeChildren = treeItem.getTreechildren();
		}else{
			treeChildren = new Treechildren();
			treeChildren.setParent(treeItem);
		}
		Treeitem newCondition = createNewCondition(null,"IF");
		if(treeItem.getNextSibling() != null ){	
			treeChildren.insertBefore(newCondition, treeItem.getNextSibling());
		}else{
			treeChildren.appendChild(newCondition);
		}
		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();  

		doSetButtonProperties(treeChildren,currentuUID, newuUID,"IF");

	}

	/**
	 * On selecting to add a new sub Condition
	 * @param event
	 */
	public void onClickLogicalOperator (ForwardEvent event){
		Treeitem treeItem = (Treeitem) event.getData();
		Treecell currentTreeCell = (Treecell) event.getOrigin().getTarget().getParent();
		Treechildren treeChildren;
		Treeitem ifTreeItem = null;
		Treeitem newCondition =  createNewCondition(null,"AND");
		if(treeItem.getTreechildren() != null){
			treeChildren = treeItem.getTreechildren();
			ifTreeItem = (Treeitem) treeChildren.getAttribute("IFCondition");
		}else{
			treeChildren = new Treechildren();
			treeChildren.setParent(treeItem);
		}
		if(ifTreeItem == null){
			treeChildren.appendChild(newCondition);
		}else{
			treeChildren.insertBefore(newCondition, ifTreeItem);
		}
		String currentuUID = currentTreeCell.getId();
		String newuUID = newCondition.getTreerow().getChildren().get(0).getId();  

		doSetButtonProperties(treeChildren,currentuUID, newuUID,"AND");
	}

	/**
	 * 
	 * @param treeChildren
	 * @param currentuUID
	 * @param newuUID
	 * @param label
	 */
	public void doSetButtonProperties(Treechildren treeChildren, String currentuUID, String newuUID, String label){
		if(label.equals("ELSEIF")){
			treeChildren.getFellowIfAny(newuUID+"_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_ELSE").setVisible(false);
		}else if(label.equals("ELSE")){
			treeChildren.getFellowIfAny(newuUID+"_btn_ELSEIF").setVisible(false);
			treeChildren.getFellowIfAny(newuUID + "_leftOperand_space").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_btn_AND").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_AND").setVisible(false);

			treeChildren.getFellowIfAny(currentuUID+"_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_"+currentuUID+"_btn_ELSE").setVisible(false);

			treeChildren.getFellowIfAny(newuUID+"_leftOperand").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_leftOperand_space").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_leftOperandType").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_leftOperandType").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_operator").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_operator").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_rightOperandType").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_rightOperandType").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_rightOperand").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_rightOperand_space").setVisible(false);
		}else if(label.equals("IF")){
			treeChildren.getFellowIfAny(currentuUID+"_btn_NESTEDIF").setVisible(false);
			treeChildren.getFellowIfAny("space_"+currentuUID+"_btn_NESTEDIF").setVisible(false);
		}else if(label.equals("AND")){
			treeChildren.getFellowIfAny(newuUID+"_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_ELSE").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_btn_ELSEIF").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_ELSEIF").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_btn_NESTEDIF").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_NESTEDIF").setVisible(false);
			treeChildren.getFellowIfAny(newuUID+"_btn_CALCULATE").setVisible(false);
			treeChildren.getFellowIfAny("space_"+newuUID+"_btn_CALCULATE").setVisible(false);
			if(treeChildren.getFellowIfAny(newuUID+"_RESULT")!=null){
				treeChildren.getFellowIfAny(newuUID+"_RESULT").setVisible(false);
			}
			if(treeChildren.getFellowIfAny(newuUID+"_mailType")!=null){
				treeChildren.getFellowIfAny(newuUID+"_mailType").setVisible(false);
			}
		}
	}

	/**
	 * onChanging Operand Types
	 * @param event
	 */
	public void onChangeOperandType(ForwardEvent event){
		Combobox operandType = (Combobox) event.getOrigin().getTarget();
		Treecell treeCell = (Treecell) operandType.getParent();
		String uUID = treeCell.getId();

		if(treeCell.getFellowIfAny(treeCell.getId()+"_button") != null ){
			treeCell.getFellowIfAny(treeCell.getId()+"_button").detach();
		}

		if(operandType.getId().endsWith("_rightOperandType")){
			uUID = uUID+"_rightOperand";
		}else if(operandType.getId().endsWith("_leftOperandType")){
			if(treeCell.getFellowIfAny(uUID+"_operator") != null ){
				if(treeCell.getFellowIfAny(uUID+"_rightOperandType")!=null){
					Combobox rightOperandType=(Combobox) treeCell.getFellowIfAny(uUID+"_rightOperandType");
					Combobox operator=(Combobox)treeCell.getFellowIfAny(uUID+"_operator");
					if(operator.getSelectedIndex()>0){
						((Combobox) operator).setSelectedIndex(0);
					}
					if(rightOperandType.getSelectedIndex()>0){
						((Combobox) rightOperandType).setSelectedIndex(0);
					}
				}
				if(treeCell.getFellowIfAny(treeCell.getId()+"_rightOperand")!=null){
					Component rightOperand= treeCell.getFellowIfAny(treeCell.getId()+"_rightOperand");
					if(rightOperand instanceof Combobox){
						if(((Combobox) rightOperand).getSelectedIndex()>0){
							((Combobox) rightOperand).setSelectedIndex(0);
						}
					}else if(rightOperand instanceof Longbox){
						((Longbox) rightOperand).setValue((long) 0);
					}else if(rightOperand instanceof Textbox){
						if( ((Textbox) rightOperand).isReadonly()){
							((Textbox) rightOperand).setReadonly(false);
						}
						((Textbox) rightOperand).setConstraint("");
						((Textbox) rightOperand).setValue("");
					}
				}
				Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_operator");
				String excludeFields = getExcludeFieldsByOperandType(operandType, "operator");
				fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludeFields);
				excludeFields = "";
				excludeFields = getExcludeFieldsByOperandType(operandType, "operandType");
				comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_rightOperandType");
				fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operandTypesList, excludeFields);

				//Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_operator");
				//	String excludeFields = getExcludeFieldsByOperandType(operandType);
				//fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludeFields);
			}
			uUID = uUID+"_leftOperand";
		}
		if(treeCell.getFellowIfAny(uUID) != null ){
			treeCell.getFellowIfAny(uUID).detach();
			if(treeCell.getFellowIfAny(uUID+"_space") != null ){
				treeCell.getFellowIfAny(uUID+"_space").detach();
			}
			if(treeCell.getFellowIfAny(uUID+"_calculate") != null){
				treeCell.getFellowIfAny(uUID+"_calculate").detach();
			}
		}
		createOperand(uUID, operandType, null);

	}

	/**
	 * Get Exclude Fields by Operand Type
	 * @param operandType
	 * @return
	 */
	public String getExcludeFieldsByOperandType(Combobox operandType,String type){
		String excludeFields = "";
		if(operandType.getSelectedIndex() == 0 ){
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)){
			if(type.equals("operator")){
				excludeFields = ", > , >= , < , <= , IN , NOT IN ,";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.CALCVALUE +  ","+PennantConstants.STATICTEXT+","+PennantConstants.DBVALUE+"," ;
			}
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)){
			if(type.equals("operator")){
				excludeFields = ", IN , NOT IN ,";
			}
			if(type.equals("operandType")){
				excludeFields = "," +PennantConstants.DBVALUE+",";
			}
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)){
			if(type.equals("operandType")){
				excludeFields = ","+PennantConstants.DBVALUE+",";
			}else if(type.equals("operator")){
				excludeFields = ", IN , NOT IN ,";
			}

		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.CALCVALUE)){
			if(type.equals("operator")){
				excludeFields = ", LIKE , NOT LIKE , IN , NOT IN ,";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.STATICTEXT + "," +PennantConstants.DBVALUE+",";
			}
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.FUNCTION)){
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.SUBQUERY)){
		}
		return excludeFields;
	}

	/**
	 * onChanging Operand 
	 * @param event
	 */
	@SuppressWarnings("unused")
	public void onChangeOperand(ForwardEvent event){
		Combobox operandType = (Combobox) event.getOrigin().getData();
		Component operand = (Component) event.getOrigin().getTarget();
		onChangingOperand(operand);
	}
	public void onChangingOperand(Component operand){
		Treecell treeCell = (Treecell) operand.getParent();
		if(treeCell.getFellowIfAny(treeCell.getId()+"_button") != null ){
			treeCell.getFellowIfAny(treeCell.getId()+"_button").detach();
		}
		if(operand instanceof Combobox) {
			if(operand.getId().endsWith("_leftOperand")){
				Combobox operandtype=(Combobox)operand.getAttribute("OperandType");
				Combobox operator=(Combobox)treeCell.getFellowIfAny(treeCell.getId()+"_operator");
				if(operator.getSelectedIndex()>0){
					((Combobox) operator).setSelectedIndex(0);
				}
				if(treeCell.getFellowIfAny(treeCell.getId()+"_rightOperandType")!=null && treeCell.getFellowIfAny(treeCell.getId()+"_rightOperand")!=null){
					Combobox rightOperandType=(Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_rightOperandType");
					Component rightOperand=(Component) treeCell.getFellowIfAny(treeCell.getId()+"_rightOperand");
					rightOperandType.setSelectedIndex(0);
					if(rightOperand instanceof Combobox){
						if(((Combobox) rightOperand).getSelectedIndex()>0){
							((Combobox) rightOperand).setSelectedIndex(0);
						}
					}else if(rightOperand instanceof Longbox){
						((Longbox) rightOperand).setValue((long) 0);
					}else if(rightOperand instanceof Textbox){
						if( ((Textbox) rightOperand).isReadonly()){
							((Textbox) rightOperand).setReadonly(false);
						}
						((Textbox) rightOperand).setConstraint("");
						((Textbox) rightOperand).setValue("");
					}
				}
				if(operandtype.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)&& ((Combobox) operand).getSelectedIndex()!=0){
					RBFieldDetail fielddetails=(RBFieldDetail) ((Combobox) operand).getSelectedItem().getAttribute("FieldDetails");
					String uUID = treeCell.getId();
					if(treeCell.getFellowIfAny(uUID+"_operator") != null ) { 
						Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_operator");
						String excludeFields = getExcludeFieldsByOperands((Combobox) operand, "operator",fielddetails);
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludeFields);
						excludeFields = "";
						excludeFields = getExcludeFieldsByOperands((Combobox) operand, "operandType",fielddetails);
						comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_rightOperandType");
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operandTypesList, excludeFields);
					}
				}
				else if(operandtype.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR) && 
						((Combobox) operand).getSelectedIndex()!=0){
					GlobalVariable globalVariable=(GlobalVariable) ((Combobox) operand).getSelectedItem().getAttribute("GlobalVariableDetails");
					String uUID = treeCell.getId();
					if(treeCell.getFellowIfAny(uUID+"_operator") != null ) { 
						Combobox comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_operator");
						String excludeFields = getGlobalExcludeFieldsByOperands((Combobox) operand, "operator",globalVariable);
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operatorsList, excludeFields);
						excludeFields = "";
						excludeFields = getGlobalExcludeFieldsByOperands((Combobox) operand, "operandType",globalVariable);
						comboBox = (Combobox) treeCell.getFellowIfAny(uUID+"_rightOperandType");
						fillComboBox(comboBox, comboBox.getSelectedItem().getValue().toString(), operandTypesList, excludeFields);
					}
				}
			}else if(operand.getId().endsWith("_rightOperand")){
			}
		}
		else if(operand instanceof Textbox){
		}
		else if(operand instanceof Longbox){
			//this. selectedValue=((Longbox) operand).getValue();
		}

	}
	public String getExcludeFieldsByOperands(Combobox operand, String type,RBFieldDetail fielddetails){
		String excludeFields = "";
		String fieldType = fielddetails.getRbFldType();
		Comboitem fieldlist=operand.getSelectedItem();
		RBFieldDetail fieldDetails=(RBFieldDetail)fieldlist.getAttribute("FieldDetails");

		if((fieldType.equalsIgnoreCase(PennantConstants.DECIMAL))||(fieldType.equalsIgnoreCase(PennantConstants.BIGINT))||
				(fieldType.equalsIgnoreCase(PennantConstants.NUMERIC))){
			if(type.equals("operator")){
				excludeFields = ", LIKE , NOT LIKE , ";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.STATICTEXT +",";
			}
		}
		else if((fieldType.equalsIgnoreCase(PennantConstants.NVARCHAR))||
				(fieldType.equalsIgnoreCase(PennantConstants.NCHAR))||
				(fieldType.equalsIgnoreCase(PennantConstants.VARCHAR))){
			if(type.equals("operator")){
				excludeFields = ", > , >= , < , <= , ";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.CALCVALUE +",";
			}
		}
		else if((fieldType.equalsIgnoreCase(PennantConstants.DATETIME))||
				(fieldType.equalsIgnoreCase(PennantConstants.SMALLDATETIME))){
			if(type.equals("operandType")){
				excludeFields =  ","+ PennantConstants.CALCVALUE+","+ PennantConstants.STATICTEXT +",";
			}
		}
		if(!StringUtils.trimToEmpty(fieldDetails.getModuleCode()).equals("")){
			if(type.equals("operator")){
				excludeFields = ", > , >= , < , <= ,";
			}
		}else{
			if(type.equals("operandType")){
				excludeFields =  ","+PennantConstants.DBVALUE+",";
			}
		}
		return excludeFields;
	}

	public String getGlobalExcludeFieldsByOperands(Combobox operand, String type,GlobalVariable globalVariable){
		String excludeFields = "";
		String fieldType = globalVariable.getVarType();
		if((fieldType.equalsIgnoreCase(PennantConstants.DECIMAL))||(fieldType.equalsIgnoreCase(PennantConstants.BIGINT))||
				(fieldType.equalsIgnoreCase(PennantConstants.NUMERIC))){
			if(type.equals("operator")){
				excludeFields = ", LIKE , NOT LIKE ,";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.STATICTEXT +"," + PennantConstants.SUBQUERY + ","+PennantConstants.DBVALUE+",";
			}
		}
		else if((fieldType.equalsIgnoreCase(PennantConstants.NVARCHAR))||
				(fieldType.equalsIgnoreCase(PennantConstants.NCHAR))||
				(fieldType.equalsIgnoreCase(PennantConstants.VARCHAR))){
			if(type.equals("operator")){
				excludeFields = ", > , >= , < , <= ,";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.CALCVALUE +"," + PennantConstants.SUBQUERY + ","+PennantConstants.DBVALUE+",";
			}
		}
		else if((fieldType.equalsIgnoreCase(PennantConstants.DATETIME))||
				(fieldType.equalsIgnoreCase(PennantConstants.SMALLDATETIME))){
			if(type.equals("operator")){
				excludeFields = "";
			}else if(type.equals("operandType")){
				excludeFields = "," + PennantConstants.SUBQUERY + ","+ PennantConstants.CALCVALUE+","+
						PennantConstants.STATICTEXT + ","+PennantConstants.DBVALUE+",";
			}
		}
		Comboitem fieldlist=operand.getSelectedItem();
		GlobalVariable fieldDetails=(GlobalVariable)fieldlist.getAttribute("GlobalVariableDetails");
		return excludeFields;
	}
	/**
	 * onChanging Operator 
	 * @param event
	 */
	public void onChangeOperator(ForwardEvent event){
		Combobox operator = (Combobox) event.getOrigin().getTarget();
		onChangingOperator(operator);
	}

	public void onChangingOperator(Combobox operator){
		Treecell treeCell = (Treecell) operator.getParent();
		String uUID = treeCell.getId();
		if(treeCell.getFellowIfAny(treeCell.getId()+"_button") != null ){
			treeCell.getFellowIfAny(treeCell.getId()+"_button").detach();
		}

		if(treeCell.getFellowIfAny(uUID+"_operator") != null ){
			Combobox rightOperandType=(Combobox) treeCell.getFellowIfAny(uUID+"_rightOperandType");
			Combobox leftOperandType=(Combobox) treeCell.getFellowIfAny(uUID+"_leftOperandType");
			Component rightOperand=(Component) treeCell.getFellowIfAny(uUID+"_rightOperand");
			Component leftOperand=(Component) treeCell.getFellowIfAny(uUID+"_leftOperand");
			rightOperandType.setSelectedIndex(0);
			if(rightOperand instanceof Textbox){
				if(((Textbox) rightOperand).isReadonly()){
					((Textbox) rightOperand).setReadonly(false);
					((Textbox) rightOperand).setConstraint("");
					((Textbox) rightOperand).setValue("");
				}
				if(rightOperandType.getSelectedItem().getValue().equals(PennantConstants.DBVALUE)){
					((Textbox) rightOperand).setReadonly(false);
					((Textbox) rightOperand).setConstraint("");
					((Textbox) rightOperand).setValue("");
					((Textbox) rightOperand).setReadonly(true);
				} else {
					((Textbox) rightOperand).setConstraint("");
					((Textbox) rightOperand).setValue("");
				}
			}
			else if(rightOperand instanceof Combobox){
				((Combobox) rightOperand).setConstraint("");
				((Combobox) rightOperand).setValue("");
			}
			else if(rightOperand instanceof Longbox){
				((Longbox) rightOperand).setConstraint("");
				((Longbox) rightOperand).setValue((long) 0);
			}

			String	excludeFields = getExcludeFieldsByOperator((Combobox) operator,leftOperand,leftOperandType) ;
			fillComboBox(rightOperandType, rightOperandType.getSelectedItem().getValue().toString(), operandTypesList, excludeFields);
		}
	}

	/**
	 * Get the Excludefields for rightOperandType
	 * @param operator(Combobox),leftOperand(Component)
	 * @return excludeFields(String)
	 */
	public String getExcludeFieldsByOperator(Combobox operator,Component leftOperand,Combobox leftOperandType){
		String excludeFields = "";
		String selectedOperator = operator.getSelectedItem().getLabel();


		try{
			if(operator.getSelectedIndex() == 0 ){
			}else if((selectedOperator.equals(Labels.getLabel("EQUALS_LABEL")))||
					(selectedOperator.equals(Labels.getLabel("NOTEQUAL_LABEL")))||
					(selectedOperator.equals(Labels.getLabel("NOTLIKE_LABEL")))||
					(selectedOperator.equals(Labels.getLabel("LIKE_LABEL")))){
				if(leftOperand instanceof Combobox){
					String dataType = ((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase();
					if(leftOperandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)){
						if(((Combobox) leftOperand).getSelectedIndex()!=0){
							RBFieldDetail fielddetails=(RBFieldDetail) ((Combobox) leftOperand).getSelectedItem().getAttribute("FieldDetails");
							if(!StringUtils.trimToEmpty(fielddetails.getModuleCode()).equals("")){
								if(fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.NCHAR) ||
										fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.NVARCHAR) ||
										fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.VARCHAR)){
									excludeFields = "," + PennantConstants.SUBQUERY + ","+PennantConstants.CALCVALUE + ",";
								}else if(fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.INT) ||
										fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.BIGINT) ||
										fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.NUMERIC) ||
										fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.DECIMAL)){
									excludeFields = "," + PennantConstants.SUBQUERY + ","+PennantConstants.STATICTEXT + ",";
								}else if(fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.DATETIME) ||
										fielddetails.getRbFldType().toLowerCase().equals(PennantConstants.SMALLDATETIME)){
									excludeFields = "," + PennantConstants.SUBQUERY + ","+PennantConstants.CALCVALUE + ",";
								}
							}
							else{
								if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.NCHAR))||
										(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.NVARCHAR))||
										(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.VARCHAR))){
									excludeFields =  ","+ PennantConstants.SUBQUERY + ","+PennantConstants.CALCVALUE + ","+PennantConstants.DBVALUE+",";
								}else if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.DATETIME))||
										(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.SMALLDATETIME))){
									excludeFields = ","+PennantConstants.STATICTEXT + ","+PennantConstants.CALCVALUE + "," +PennantConstants.DBVALUE+",";
								}else if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.NUMERIC))||
										(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.BIGINT))||
										(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.DECIMAL))||
										(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.INT))){
									excludeFields = ","+ PennantConstants.SUBQUERY  + ","+PennantConstants.STATICTEXT + ","+PennantConstants.DBVALUE+",";
								}
							}

						}
					}else if(leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)){
						excludeFields=","+PennantConstants.DBVALUE+","+ PennantConstants.SUBQUERY  + ",";
						if(((Combobox) leftOperand).getSelectedIndex()!=0){
							//GlobalVariable globalVariable=(GlobalVariable) ((Combobox) leftOperand).getSelectedItem().getAttribute("GlobalVariableDetails");
							if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.NCHAR))||
									(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.NVARCHAR))||
									(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.VARCHAR))){
								excludeFields = ","+ PennantConstants.SUBQUERY + ","+PennantConstants.CALCVALUE + ","+PennantConstants.DBVALUE+",";
							}else if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.DATETIME))||
									(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.SMALLDATETIME))){
								excludeFields = ","+PennantConstants.STATICTEXT + ","+PennantConstants.CALCVALUE + "," +PennantConstants.SUBQUERY+","+PennantConstants.DBVALUE+",";
							}else if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.NUMERIC))||
									(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.BIGINT))||
									(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.DECIMAL))||
									(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.INT))){
								excludeFields =","+ PennantConstants.SUBQUERY  + ","+PennantConstants.STATICTEXT + ","+PennantConstants.DBVALUE+",";
							}

						}
					}
				}else if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)){
					excludeFields =","+PennantConstants.CALCVALUE+","+PennantConstants.STATICTEXT+","+PennantConstants.DBVALUE+",";

				}else if (leftOperandType.getSelectedItem().getValue().equals(PennantConstants.CALCVALUE)){
					excludeFields = "," +PennantConstants.CALCVALUE + ","	+PennantConstants.STATICTEXT+","+PennantConstants.DBVALUE+",";

				}
			}else if((selectedOperator.equals(Labels.getLabel("GREATER_LABEL")))||
					(selectedOperator.equals(Labels.getLabel("GREATEREQUAL_LABEL")))||
					(selectedOperator.equals(Labels.getLabel("LESS_LABEL")))||
					(selectedOperator.equals(Labels.getLabel("LESSEQUAL_LABEL")))){
				if(leftOperand instanceof Combobox){
					if(((Combobox) leftOperand).getSelectedIndex()!=0){
						if((((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.DATETIME))||
								(((Combobox) leftOperand).getSelectedItem().getTooltiptext().toLowerCase().contains(PennantConstants.SMALLDATETIME))){
							excludeFields =  ","+PennantConstants.STATICTEXT + ","+PennantConstants.CALCVALUE + "," +PennantConstants.DBVALUE+",";
						}else {
							excludeFields= ","+PennantConstants.STATICTEXT + ","+PennantConstants.DBVALUE+"," ;
						}
					} 
				}else {
					excludeFields= ","+PennantConstants.STATICTEXT + "," +PennantConstants.DBVALUE+",";
				}
			}

		}catch(Exception e){
			System.out.println(e);
		}
		return excludeFields;
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++  GUI OPERATIONS  ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Creating a new Operand based on Operand Type
	 */
	private void createOperand(String uUID, Combobox operandType,String value) {
		space = new Space();
		space.setId(uUID+"_space");
		space.setWidth("5px");

		if(operandType.getSelectedIndex() == 0 ){
			Combobox operand = new Combobox();
			operandType.getParent().insertBefore(space,operandType.getNextSibling().getNextSibling() );
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setReadonly(true);
			operand.setSclass("javaScriptBuilderComboitemfont");
			operand.setId(uUID);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)){
			Textbox operand = new Textbox();
			operand.setId(uUID);
			operand.setFocus(true);
			operand.setValue(value);
			operandType.getParent().insertBefore(space,operandType.getNextSibling().getNextSibling() );
			operandType.getParent().insertBefore(operand, space);
			operand.setSclass("javaScriptBuilderfont");
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)){				//GLOBALVAR			
			Combobox operand = new Combobox();
			operand.setId(uUID);
			operand.setSclass("javaScriptBuilderComboitemfont");
			String operandtype=operandType.getSelectedItem().getValue();
			if(value!=null){
				if((value.equals(" "))||(value.equals(""))){
					operand.setVisible(false);
				}
			}
			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);

			if(operand.getId().endsWith("_leftOperand")){
				for (int i = 0; i < globalVariableList.size(); i++) {
					GlobalVariable globalVariable = (GlobalVariable) globalVariableList
							.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(globalVariable.getVarName());
					comboitem.setValue(globalVariable.getVarName());
					comboitem.setTooltiptext("Data Type : "+globalVariable.getVarType().toUpperCase());
					comboitem.setAttribute("GlobalVariableDetails",globalVariable);
					comboitem.setAttribute("OperandType",operandtype);
					operand.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getVarName()))) {
						operand.setSelectedItem(comboitem);
					}
					//comboitem=null;
				}
			}
			else if(operand.getId().endsWith("_rightOperand")){
				Treecell treeCell=(Treecell) operandType.getParent();
				Combobox leftOperandType=(Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_leftOperandType");
				if(value!=null){
					if(value.equals(" ")){
						operand.setVisible(false);
					}
				}
				if(leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)){
					for (int i = 0; i < globalVariableList.size(); i++) {
						GlobalVariable globalVariable = (GlobalVariable) globalVariableList
								.get(i);
						comboitem = new Comboitem();
						comboitem.setLabel(globalVariable.getVarName());
						comboitem.setValue(globalVariable.getVarName());
						comboitem.setTooltiptext("Data Type : "+globalVariable.getVarType().toUpperCase());
						comboitem.setAttribute("GlobalVariableDetails",globalVariable);
						comboitem.setAttribute("OperandType",operandtype);
						operand.appendChild(comboitem);
						if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getVarName()))) {
							operand.setSelectedItem(comboitem);
						}
						//comboitem=null;
					}
				}
				if(treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand") instanceof Combobox){
					Combobox leftOperand=(Combobox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
					if(globalVariableList!=null && globalVariableList .size()>0){
						for (int i = 0; i < globalVariableList.size(); i++) {
							GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
							comboitem = new Comboitem();
							if(leftOperand.getSelectedIndex()>0){
								if(leftOperand.getSelectedItem().getTooltiptext().contains(globalVariable.getVarType().toUpperCase())){
									comboitem.setLabel(globalVariable.getVarName()); 
									comboitem.setValue(globalVariable.getVarName());
									comboitem.setTooltiptext("Data Type : "+globalVariable.getVarType().toUpperCase());
									comboitem.setAttribute("GlobalVariableDetails",globalVariable);
									comboitem.setAttribute("OperandType",operandtype);
									operand.appendChild(comboitem);
									operand.setAttribute("GlobalVariableDetails", globalVariable);
									if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getVarName()))) {
										operand.setSelectedItem(comboitem);
									}
								}
							}
						}
					}
				}else{
					//Textbox leftOperand=(Textbox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
					String charDataTypes="varchar,nvarchar,nchar,char";
					if(globalVariableList!=null && globalVariableList .size()>0){
						for (int i = 0; i < globalVariableList.size(); i++) {
							GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
							comboitem = new Comboitem();
							if(charDataTypes.contains(globalVariable.getVarType())){
								comboitem.setLabel(globalVariable.getVarName());
								comboitem.setValue(globalVariable.getVarName());
								comboitem.setTooltiptext("Data Type : "+globalVariable.getVarType().toUpperCase());
								comboitem.setAttribute("GlobalVariableDetails",globalVariable);
								comboitem.setAttribute("OperandType",operandtype);
								operand.appendChild(comboitem);
								operand.setAttribute("GlobalVariableDetails", globalVariable);
								if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(globalVariable.getVarName()))) {
									operand.setSelectedItem(comboitem);
								}
							}
						}
					}
				}
			}
			operand.setWidth("190px");
			operandType.getParent().insertBefore(space,operandType.getNextSibling().getNextSibling() );
			operandType.getParent().insertBefore(operand, space);
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.FIELDLIST)){

			if(this.objectFieldList != null && !this.objectFieldList.isEmpty()){
				this.objectFieldList.clear();
			}
			this.objectFieldList = PennantAppUtil.getRBFieldDetails(getModule());
			Combobox operand = new Combobox();
			operand.setId(uUID);
			operand.setSclass("javaScriptBuilderComboitemfont");
			Comboitem comboitem;
			comboitem = new Comboitem();
			comboitem.setValue("#");
			comboitem.setLabel(Labels.getLabel("Combo.Select"));
			operand.appendChild(comboitem);
			operand.setSelectedItem(comboitem);

			if(operand.getId().endsWith("_leftOperand")){
				for (int i = 0; i < this.objectFieldList.size(); i++) {
					RBFieldDetail fieldDetails = (RBFieldDetail) this.objectFieldList.get(i);
					if(this.fieldObjectMap.size() == 0 ){
						this.fieldObjectMap.put(fieldDetails.getRbFldName(), fieldDetails);
					}
					comboitem = new Comboitem();
					comboitem.setLabel(fieldDetails.getRbFldName() + "("
							+ fieldDetails.getRbFldLen() + ")");
					comboitem.setValue(fieldDetails.getRbFldName());
					comboitem.setTooltiptext("Data Type :"+fieldDetails.getRbFldType().toUpperCase());
					comboitem.setAttribute("FieldDetails",fieldDetails);
					comboitem.setAttribute("OperandType",operandType);
					operand.appendChild(comboitem);
					if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
						operand.setSelectedItem(comboitem);
					}
				}
			}
			else if(operand.getId().endsWith("_rightOperand")){
				Treecell treeCell=(Treecell) operandType.getParent();
				Combobox leftOperandType=(Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_leftOperandType");
				if(value!=null){
					if(value.equals(" ")){
						operand.setVisible(false);
					}
				}
				if(leftOperandType.getSelectedItem().getValue().equals(PennantConstants.GLOBALVAR)){
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = (RBFieldDetail ) this.objectFieldList.get(i);
						if(this.fieldObjectMap.size() == 0 ){
							this.fieldObjectMap.put(fieldDetails.getRbFldName(), fieldDetails);
						}
						comboitem = new Comboitem();
						comboitem.setLabel(fieldDetails.getRbFldName() + "("
								+ fieldDetails.getRbFldLen() + ")");
						comboitem.setValue(fieldDetails.getRbFldName());
						comboitem.setTooltiptext("Data Type :"+fieldDetails.getRbFldType().toUpperCase());
						comboitem.setAttribute("FieldDetails",fieldDetails);
						//comboitem.setAttribute("OperandType",operandType);
						operand.appendChild(comboitem);
						if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
							operand.setSelectedItem(comboitem);
						}
					}
				}else if(leftOperandType.getSelectedItem().getValue().equals(PennantConstants.CALCVALUE)){
					String numericDataTypes="int,bigint,numeric,decimal";
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = (RBFieldDetail ) this.objectFieldList.get(i);
						if(this.fieldObjectMap.size() == 0 ){
							this.fieldObjectMap.put(fieldDetails.getRbFldName(), fieldDetails);
						}
						if(numericDataTypes.contains(fieldDetails.getRbFldType())){
							comboitem = new Comboitem();
							comboitem.setLabel(fieldDetails.getRbFldName() + "("
									+ fieldDetails.getRbFldLen() + ")");
							comboitem.setValue(fieldDetails.getRbFldName());
							comboitem.setTooltiptext("Data Type :"+fieldDetails.getRbFldType().toUpperCase());
							comboitem.setAttribute("FieldDetails",fieldDetails);
							//comboitem.setAttribute("OperandType",operandType);
							operand.appendChild(comboitem);
							if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
								operand.setSelectedItem(comboitem);
							}
						}
					}
				}
				if(treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand") instanceof Combobox){
					Combobox leftOperand=(Combobox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = (RBFieldDetail ) this.objectFieldList.get(i);
						if(this.fieldObjectMap.size() == 0 ){
							this.fieldObjectMap.put(fieldDetails.getRbFldName(), fieldDetails);
						}
						comboitem = new Comboitem();
						if(leftOperand.getSelectedIndex()!=0){
							if(leftOperand.getSelectedItem().getTooltiptext().contains(fieldDetails.getRbFldType().toUpperCase())){
								comboitem.setLabel(fieldDetails.getRbFldName() + "("
										+ fieldDetails.getRbFldLen() + ")");
								comboitem.setValue(fieldDetails.getRbFldName());
								comboitem.setTooltiptext("Data Type :"+fieldDetails.getRbFldType().toUpperCase());
								comboitem.setAttribute("FieldDetails",fieldDetails);
								//comboitem.setAttribute("OperandType",operandType);
								operand.appendChild(comboitem);
								if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
									operand.setSelectedItem(comboitem);
								}
							}
						}
					}
				}else{
					//Textbox leftOperand=(Textbox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
					String charDataTypes="varchar,nvarchar,nchar,char";
					for (int i = 0; i < this.objectFieldList.size(); i++) {
						RBFieldDetail fieldDetails = (RBFieldDetail) this.objectFieldList.get(i);
						if(this.fieldObjectMap.size() == 0 ){
							this.fieldObjectMap.put(fieldDetails.getRbFldName(), fieldDetails);
						}
						comboitem = new Comboitem();
						if(charDataTypes.contains(fieldDetails.getRbFldType())){
							comboitem.setLabel(fieldDetails.getRbFldName() + "("
									+ fieldDetails.getRbFldLen() + ")");
							comboitem.setValue(fieldDetails.getRbFldName());
							comboitem.setTooltiptext("Data Type :"+fieldDetails.getRbFldType().toUpperCase());
							comboitem.setAttribute("FieldDetails",fieldDetails);
							//comboitem.setAttribute("OperandType",operandType);
							operand.appendChild(comboitem);
							if (StringUtils.trimToEmpty(value).equals(StringUtils.trimToEmpty(fieldDetails.getRbFldName()))) {
								operand.setSelectedItem(comboitem);
							}
						}
					}
				}
			}
			operand.setWidth("190px");
			operandType.getParent().insertBefore(space,operandType.getNextSibling().getNextSibling() );
			operandType.getParent().insertBefore(operand, space);
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.CALCVALUE)){
			Label calculate = new Label();																								 //Calculation 
			calculate.setTooltiptext("Click to add a formula");
			calculate.setValue("CALCULATE");
			calculate.setId(uUID+"_calculate");
			calculate.setSclass("button_Label");
			Textbox operand = new Textbox();
			operandType.getParent().insertBefore(space,operandType.getNextSibling().getNextSibling());
			operandType.getParent().insertBefore(calculate,space );
			operandType.getParent().insertBefore(operand, space);
			operand.setFocus(true);
			operand.setId(uUID);
			operand.setSclass("javaScriptBuilderfont");
			operand.setAttribute("OperandType", operandType);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			operand.setWidth("120px");
			operand.setValue(StringUtils.trimToEmpty(value));
			calculate.addForward("onClick", this, "onClickCalculate", operand);
		}else if(operandType.getSelectedItem().getValue().equals(PennantConstants.DBVALUE)){				//DBVALUE
			Textbox operand = new Textbox();
			operand.setId(uUID);
			operand.setSclass("javaScriptBuilderfont");
			if(value!=null){
				if((value.equals(" "))||(value.equals(""))){
					operand.setVisible(false);
				}
			}
			if(value!=null && value.contains("(")){
				operand.setValue(value.replace("(","").replace(")",""));
			}else{
				operand.setValue(value);
			}
			operand.setReadonly(true);
			operandType.getParent().insertBefore(space,operandType.getNextSibling().getNextSibling() );		
			Space space1=new Space();
			space1.setWidth("5px");
			Treecell treeCell=(Treecell) operandType.getParent();
			Button  btnSearch = new Button();
			btnSearch.setLabel("Search");
			btnSearch.setId(treeCell.getId()+"_button");
			if(value!=null){
				btnSearch.setDisabled(true);
			}
			operandType.getParent().insertBefore(btnSearch, space);
			operandType.setFocus(true);
			operand.setAttribute("OperandType", operandType);
			operandType.getParent().insertBefore(operand, btnSearch ) ;
			operandType.getParent().insertBefore(space1, btnSearch);
			operand.setAttribute("OperandType", operandType);
			btnSearch.setAttribute("OperandType", operandType);
			btnSearch.setAttribute("Operand", operand);
			operand.addForward("onChange", this, "onChangeOperand", operandType);
			btnSearch.addForward("onClick",this,"onSearchButtonClick",operand);

		}  
	}
	/**
	 * onClicking SearchButton 
	 * @param event
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 */
	public void onSearchButtonClick(ForwardEvent event) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{
		Component button = (Component) event.getOrigin().getTarget();
		Textbox lovText=(Textbox)button.getAttribute("Operand");
		Combobox operandType=(Combobox)button.getAttribute("OperandType");
		Treecell treeCell=(Treecell) operandType.getParent();
		Combobox leftOperand=(Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_leftOperand");
		RBFieldDetail fielddetails=(RBFieldDetail) ((Combobox) leftOperand).getSelectedItem().getAttribute("FieldDetails");
		Combobox operator=(Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_operator");
		String moduleCode = fielddetails.getModuleCode();


		if((operator.getSelectedItem().getLabel().equals(Labels.getLabel("IN_LABEL")))||
				(operator.getSelectedItem().getLabel().equals(Labels.getLabel("NOTIN_LABEL")))){
			HashMap<String,Object> selectedValues=new HashMap<String,Object>();
			Object dataObject=ExtendedMultipleSearchListBox.show(this, moduleCode, selectedValues);
			if (dataObject instanceof String){
				lovText.setValue(dataObject.toString());

			}else{
				HashMap<String,Object> details= (HashMap<String,Object>) dataObject;
				if (details != null) {
					String multivalues=details.keySet().toString();
					lovText.setValue(multivalues.replace("[","").replace("]", " "));
				}
			}
		}else {
			Object dataObject = ExtendedSearchListBox.show(this,moduleCode);
			if (dataObject instanceof String){
				lovText.setValue(dataObject.toString());
			}else{
				ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap(fielddetails.getModuleCode());
				String fieldValue= "";
				String fieldString =  moduleMapping.getLovFields()[0];
				String fieldMethod= "get" +fieldString.substring(0,1).toUpperCase()+fieldString.substring(1);
				if(dataObject!=null){
					if (dataObject.getClass().getMethod(fieldMethod,null).getReturnType().equals(String.class)) {
						fieldValue  = (String) dataObject.getClass().getMethod(fieldMethod,null).invoke(dataObject,null);
					}else{
						fieldValue=dataObject.getClass().getMethod(fieldMethod,null).invoke(dataObject,null).toString();
					}
					if(!StringUtils.trimToEmpty(fieldValue).equals("")){
						lovText.setValue(fieldValue);
					}
				}
			}
		}
	}

	/**
	 * Get Query from the tree
	 * @param treeChildren
	 * @return
	 */
	public String getQuery(Treechildren treeChildren,Boolean validation){
		List<Component> treeItems = treeChildren.getChildren();
		String uUID = "";
		String value="";
		Treecell treeCell;
		Component component;
		Treeitem treeItem;
		
			for(int i=0; i<treeItems.size(); i++){
				treeItem = (Treeitem) treeItems.get(i);
				treeCell = (Treecell) treeItem.getTreerow().getChildren().get(0);
				uUID = treeCell.getId();

				component =treeCell.getFellowIfAny(uUID+"_RmvCond"); 
				if(!((Button)component).isDisabled()){
					actualBlock += "~";
				}
				component = treeCell.getFellowIfAny(uUID+"_statement");
				if(component != null ){
					value = getOperandValue(component,validation);
					this.query += value;
					actualBlock += "(" +"|";
					actualBlock += value +"|";
				}else{
					component = treeCell.getFellowIfAny(uUID+"_logicalOperator");
					if(component != null){
						if(!((Button)treeCell.getFellowIfAny(uUID+"_RmvCond")).isDisabled()){
							this.query  = this.query.substring(0,this.query.length()-3);
							logicCount++;
						}
						actualBlock += "(" +"|";
						value = getOperandValue(component,validation);
						this.query += " " + value;
						actualBlock += value +"|";
					}else{
						actualBlock += "(" +"|";

					}
				}
				Label label = (Label)treeCell.getFellowIfAny(uUID+"_statement"); 
				if( label == null || !label.getValue().equalsIgnoreCase("ELSE")){
					this.query += "( ";
				}	
				label = null;
				component = treeCell.getFellowIfAny(uUID+"_leftOperandType");
				if(component.isVisible()){
					value = getOperandValue(component,validation);
				}else{
					value = "";
				}
				actualBlock += value +"|";

				component = treeCell.getFellowIfAny(uUID+"_leftOperand");
				if(component.isVisible()){
					Combobox leftOperandType = (Combobox)treeCell.getFellowIfAny(treeCell.getId()+"_leftOperandType");
					if(leftOperandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)){
						value="'"+getOperandValue(component,validation)+"'";
					}else{
						value = getOperandValue(component,validation);
					}

				}else{
					value = "";
				}
				this.query += value;
				actualBlock += value.replace("'", "") +"|";

				component = treeCell.getFellowIfAny(uUID+"_operator");
				if(component.isVisible()){
					value = getOperandValue(component,validation);
				}else{
					value = "";
				}	
				this.query += value;
				actualBlock += value +"|";

				if((((Combobox) treeCell.getFellowIfAny(uUID+"_rightOperandType")).isVisible())||
						(((Component) treeCell.getFellowIfAny(uUID+"_rightOperand")).isVisible())){


					component = treeCell.getFellowIfAny(uUID+"_rightOperandType");
					value = getOperandValue(component,validation);
					actualBlock += value +"|";

					component = treeCell.getFellowIfAny(uUID+"_rightOperand");

					Combobox Operator= (Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_operator");
					Combobox rightOperandType= (Combobox) treeCell.getFellowIfAny(treeCell.getId()+"_rightOperandType");
					if(rightOperandType.getSelectedItem().getValue().equals(PennantConstants.STATICTEXT)|| rightOperandType.getSelectedItem().getValue().equals(PennantConstants.DBVALUE)){
						value="'"+getOperandValue(component,validation)+"'";
					}else{
						value = getOperandValue(component,validation);
					}


					query += value + " " ;

					if(value != null)
					{ 
						actualBlock += value.replace("'", "") +"|";
					}else{
						actualBlock += "|";
					}
				}
				else{
					actualBlock += value+"|";

					component = treeCell.getFellowIfAny(uUID+"_rightOperandType");
					actualBlock += " " +"|";

					component = treeCell.getFellowIfAny(uUID+"_rightOperand");
					actualBlock += " " +"|";
				}

				component =treeCell.getFellowIfAny(uUID+"_btn_AND"); 
				if(((Label)component).isVisible()){
					actualBlock += "1|";
				}else{
					actualBlock += "0|";
				}
				component =treeCell.getFellowIfAny(uUID+"_btn_ELSE"); 
				if(((Label)component).isVisible()){
					actualBlock += "1|";
				}else{
					actualBlock += "0|";
				}
				component =treeCell.getFellowIfAny(uUID+"_btn_ELSEIF"); 
				if(((Label)component).isVisible()){
					actualBlock += "1|";
				}else{
					actualBlock += "0|";
				}

				component =treeCell.getFellowIfAny(uUID+"_btn_NESTEDIF"); 
				if(((Label)component).isVisible()){
					actualBlock += "1|";
				}else{
					actualBlock += "0|";
				}

				component =treeCell.getFellowIfAny(uUID+"_btn_CALCULATE"); 
				if(((Label)component).isVisible()){
					actualBlock += "1|";
				}else{
					actualBlock += "0|";
				}
				component = treeCell.getFellowIfAny(uUID+"_RESULT");
				if(component != null){
					if(component.isVisible()){
						value = getOperandValue(component,validation) ;
						actualBlock += value +"|";
					}else{
						value="";
					}
				}else if(component == null){
					component = treeCell.getFellowIfAny(uUID+"_mailType");
					if(component != null){
						if(component.isVisible()){
							value = getOperandValue(component,validation) ;
							actualBlock += value +"|";
						}else{
							value="";
						}
					}
				}
				label = (Label)treeCell.getFellowIfAny(uUID+"_statement"); 
				if( label!= null && label.getValue().equalsIgnoreCase("ELSE")){
					this.query += "{\n";
				}else{
					this.query += "){\n";
				}
				label = null;

				if(treeItem.getTreechildren() != null){
					getQuery(treeItem.getTreechildren(),validation);
				}
				else{
					if(logicCount > 0 && treeItem.getTreechildren()==null){
						this.query  = this.query.substring(0,this.query.length()-4);
						while(logicCount > 0){
							this.query += ")";
							logicCount--;
						}
						this.query += "){\n";
					}
				}
				actualBlock += "~)|";
				Component labelIF =treeCell.getFellowIfAny(uUID+"_statement");


				if(!StringUtils.trimToEmpty(value).equals("") ){
					this.query += "Result = " + "'" + value +"'" + " ; return ; \n } \n" ;
				}
				if(labelIF!=null){
					if( StringUtils.trimToEmpty(value).equals("") && (((Label) labelIF).getValue().equalsIgnoreCase("IF") || ((Label) labelIF).getValue().equalsIgnoreCase("ELSE IF"))){
						this.query += "  \n } \n" ;
					}
				}
			}
		if(!validation && StringUtils.trimToEmpty(this.query).equals("(  ){\n  \n }")){
			this.query = "";
			this.actualBlock = "";
		}

		this.query += " ";
		return this.query;
	}

	/**
	 * Get Value from the components
	 * @param component
	 * @return
	 */
	public String getOperandValue(Component component,Boolean validation){
		if(component instanceof Combobox){
			if(((Combobox) component).isVisible()) {
				if(validation){
					if( ((Combobox)component).getSelectedIndex() <= 0){
						throw new WrongValueException(component, Labels.getLabel("const_NO_SELECT"));
					}

					return ((Combobox)component).getSelectedItem().getValue();
				}else if(!validation &&  ((Combobox) component).getSelectedIndex()>0 ){
					return ((Combobox)component).getSelectedItem().getValue();
				}else if(!validation &&  ((Combobox) component).getSelectedIndex()<=0 ){
					return "";
				}
			}
			else{
				((Combobox) component).setConstraint("");
				return ((Combobox)component).getSelectedItem().getValue().toString();
			}
		}else if(component instanceof Textbox){
			if(((Textbox) component).isVisible()){
				if(validation && !component.getId().contains("_mailType")){
					((Textbox)component).setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
				}else if(!validation && !StringUtils.trimToEmpty(((Textbox) component).getValue()).equals("")){
					return ((Textbox)component).getValue().toString().trim() ;
				}else if(!validation && !StringUtils.trimToEmpty(((Textbox) component).getValue()).equals("")){
					return "";
				}
				return ((Textbox)component).getValue().toString().trim() ;

			}
			else{
				((Textbox) component).setConstraint("");
				((Textbox) component).setValue("");
				return ((Textbox)component).getValue().toString() ;
			}
		}else if(component instanceof Longbox){
			if(((Longbox) component).isVisible()){
				if(validation){
					((Longbox)component).setConstraint("NO EMPTY:" + Labels.getLabel("const_NO_EMPTY"));
					return ((Longbox)component).getValue().toString();
				}else if(!validation && ((Longbox) component).getValue()!=0){
					return  ((Longbox)component).getValue().toString();
				}else if(!validation && !StringUtils.trimToEmpty(((Textbox) component).getValue()).equals("")){
					return "";
				}
			}
			else{
				((Longbox) component).setConstraint("");
				((Longbox) component).setValue((long) 0);
				return ((Longbox)component).getValue().toString();
			}
		}else if(component instanceof Label){
			Combobox leftOperandType = (Combobox) component.getNextSibling().getNextSibling();
			if(validation) {
			   return ((Label)component).getValue().toString().toLowerCase();
			} else if((!validation && leftOperandType.getSelectedIndex()>0) || ((Label)component).getValue().toString().equalsIgnoreCase("ELSE")){
				return ((Label)component).getValue().toString().toLowerCase();
			} else if(!validation && leftOperandType.getSelectedIndex()<=0){
				return "";
			}
		}else if(component instanceof Button){
			Textbox textbox = (Textbox)component.getNextSibling().getNextSibling();
			/*if(StringUtils.trimToEmpty(textbox.getValue()).equals("")){
				throw new WrongValueException(component, Labels.getLabel("const_NO_SELECT"));
			}*/
			if(validation){
				return textbox == null ? "" : textbox.getValue().toString();
			}else if(!validation && !StringUtils.trimToEmpty(textbox.getValue()).equals("")){
				return textbox.getValue().toString().trim() ;
			}else if(!validation && StringUtils.trimToEmpty(textbox.getValue()).equals("")){
				return "";
			}
			
			
		}
		return null;
	}



	/**
	 * Build treee from the actual Block
	 * @param actualBlock
	 */
	public void buildQuery(String actualBlock){
		tree.getChildren().clear();
		String [] str = actualBlock.split("\\~");
		List<Treechildren> treeChildrens = new ArrayList<Treechildren>();
		List<Treeitem> treeItems = new ArrayList<Treeitem>();
		Treechildren mainTreeChildren = new Treechildren();
		treeChildrens.add(mainTreeChildren);
		mainTreeChildren.setParent(tree);
		for (String string : str) {
			if(!StringUtils.trimToEmpty(string).equals("")){
				if(string.startsWith("(")){
					string = StringUtils.substring(string, 2);
					Treeitem treeItem = createNewCondition(string,"IF");
					if(treeItems.size() != 0 ){
						if(treeItems.get(treeItems.size()-1).getTreechildren() == null){
							Treechildren treechildren = new Treechildren();
							treeChildrens.add(treechildren);
							treechildren.setParent(treeItems.get(treeItems.size()-1));
						}	
					}
					treeItems.add(treeItem);
					treeItem.setParent(treeChildrens.get(treeChildrens.size()-1));
				}else{
					Treeitem treeItem = treeItems.get(treeItems.size()-1);

					if(treeChildrens.size() > 1 && treeItem.getTreechildren() != null){
						treeChildrens.remove(treeChildrens.size()-1);
					}
					treeItems.remove(treeItems.size()-1);
				}
			}
		} 
		tree.setVisible(true);
		


	}



	/**
	 * Method to fill the combobox with given list of values
	 * 
	 * @param combobox
	 * @param value
	 * @param list 
	 */
	public void fillComboBoxByValueLabels(Combobox combobox, String value, List<ValueLabel> list) {
		fillComboBox(combobox, value, list,"");
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
		combobox.setSclass("javaScriptBuilderComboitemfont");
		Comboitem comboitem = new Comboitem();
		comboitem.setValue("#");
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);	
		for (ValueLabel valueLabel : list) {
			if(!StringUtils.trimToEmpty(excludeFields). contains(","+valueLabel.getValue()+",")){
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
	public boolean listDataValidation(Component component,String fieldParm){
		boolean error = true;
		Combobox comp = (Combobox) component;
		for(int i = 0;i<comp.getItemCount();i++){
			if(comp.getValue().equals(comp.getItemAtIndex(i).getLabel().toString())){
				comp.setSelectedIndex(i);
				error = false;
				break;
			}
		}
		return error;
	}


	public void setEntityCode(String entityCode){
		this.entityCode = entityCode;
		this.globalVariableList = SystemParameterDetails.getGlobaVariableList();
	}

	/**
	 * Validate Query
	 * @throws InterruptedException
	 */
	public void validateQuery() throws InterruptedException{
		if(this.editable){
			sqlQuery="";
			actualBlock = "";
			codemirror.setValue("");
			this.query = "";
			logicCount = 0;
			sqlQuery = getQuery(tree.getTreechildren(),true);
		}
		this.codemirror.setValue(sqlQuery);

	}

	/**
	 * Simulate
	 * @throws InterruptedException
	 */
	public void simulateQuery(Window window_NotificationsDialog) throws InterruptedException{
		if(this.editable) {
			getSqlQuery(true);
		}

		if(!StringUtils.trimToEmpty(sqlQuery).equals("")){
			if(objectFieldList!=null && objectFieldList.size()>0){
				for (int i = 0; i < objectFieldList.size(); i++) {

					RBFieldDetail fldDetails = (RBFieldDetail) objectFieldList.get(i);
					fieldObjectMap.put(fldDetails.getRbFldName(), fldDetails);
				}
			}
			final HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("fieldObjectMap", this.fieldObjectMap);
			map.put("javaScriptBuilder", this);
			map.put("varList", globalVariableList);


			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/JavaScriptBuilder/RuleResultSimulation.zul", 
					window_NotificationsDialog,map);
		}else {
			PTMessageUtils.showErrorMessage("Please Build the Rule before Simulation");
		}
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

	public void setActualBlock(String actualBlock) {
		this.actualBlock = actualBlock;
	}

	public String getSqlQuery(boolean validation) {
		sqlQuery="";
		actualBlock = "";
		query = "";
		logicCount = 0;
		codemirror.setValue("");
		sqlQuery = getQuery(tree.getTreechildren(),validation);
		actualQuery=StringReplacement.getReplacedQuery(sqlQuery,globalVariableList,null);
		this.codemirror.setValue(actualQuery);
		//this.codemirror.setValue(sqlQuery);
		System.out.println(this.actualBlock);
		return this.sqlQuery;
	}

	public void setSqlQuery(String sqlQuery) {
		this.codemirror.setValue(sqlQuery);
		this.sqlQuery = sqlQuery;
	}

	public void getResults(){

	}



	public void setEditable(boolean editable) {
		if(editable){
			tree.setVisible(true);
			if(StringUtils.trimToEmpty(actualBlock).equals("") ){
				tree.getChildren().clear();
				this.uniqueId = 0;
				Treechildren treeChildren = new Treechildren();
				treeChildren.appendChild(createNewCondition(null,"IF"));
				tree.appendChild(treeChildren);

			}
		}else{
			tree.setVisible(false);
			if(this.objectFieldList == null){
				this.objectFieldList = PennantAppUtil.getRBFieldDetails(module);
			}
		}
		this.editable = editable;
	}
	public void setEditable(boolean editable, int uniqueID) {
		if(editable){
			tree.setVisible(true);
			if(StringUtils.trimToEmpty(actualBlock).equals("") ){
				tree.getChildren().clear();
				this.uniqueId = uniqueID;
				Treechildren treeChildren = new Treechildren();
				treeChildren.appendChild(createNewCondition(null,"IF"));
				tree.appendChild(treeChildren);

			}
		}else{
			tree.setVisible(false);
			if(this.objectFieldList == null){
				this.objectFieldList = PennantAppUtil.getRBFieldDetails(module);
			}
		}
		this.editable = editable;
	}

	public void onClickCalculate(ForwardEvent event){
		final HashMap<String, Object> map = new HashMap<String, Object>();
		Textbox calculate = (Textbox) event.getData();
		if(this.objectFieldList == null){
			this.objectFieldList = PennantAppUtil.getRBFieldDetails(module);
		}
		map.put("objectFieldList", this.objectFieldList);
		map.put("Formula", calculate.getValue());
		map.put("CalculateBox", calculate);
		Executions.createComponents("/WEB-INF/pages/ApplicationMaster/JavaScriptBuilder/NotificationsRuleResultDialog.zul",
				this,map);

	}

	/**
	 * Method for Simulation of builded code
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSimulation(Event event) throws Exception{
		if(!StringUtils.trimToEmpty(this.sqlQuery).equals("")){
			final HashMap<String, Object> map = new HashMap<String, Object>();
			//	map.put("fieldObjectMap", fieldObjectMap);
			map.put("ruleDialogCtrl", this);
			map.put("varList", globalVariableList);

			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultSimulation.zul", 
					this,map);
		}else {
			PTMessageUtils.showErrorMessage("Please Build the Rule before Simulation");
		}
	}

	public int getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(int uniqueId) {
		this.uniqueId = uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = Integer.parseInt(uniqueId);
	}
	public ModuleMapping getModuleMapping() {
		return moduleMapping;
	}

	public void setModuleMapping(ModuleMapping moduleMapping) {
		this.moduleMapping = moduleMapping;
	}

	public List<ValueLabel> getResultSelectionList() {
		return resultSelectionList;
	}

	public void setResultSelectionList(List<ValueLabel> resultSelectionList) {
		this.resultSelectionList = resultSelectionList;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
		tree.getChildren().clear();
		this.multiSelectValue.clear();
		this.uniqueId = 0;
		Treechildren treeChildren = new Treechildren();
		treeChildren.appendChild(createNewCondition(null,"IF"));
		tree.appendChild(treeChildren);
	}

	/**
	 *  This Method is called when division button is clicked
	 * @param event
	 */
	public void onButtonClick(ForwardEvent event) {
		Button button = (Button)event.getOrigin().getTarget();
		Clients.clearWrongValue(button);
		Textbox textbox = (Textbox)button.getNextSibling().getNextSibling();
		setMultiSelectValue(textbox.getValue());
		Object dataObject = ExtendedMultipleSearchListBox.show(this, getMultiSelectModuleName() , getMultiSelectValue());
		if (dataObject instanceof String){
			textbox.setValue(dataObject.toString());
		}else{
			HashMap<String,Object> details= (HashMap<String,Object>) dataObject;
			if (details != null) {
				String multivalues=details.keySet().toString();
				textbox.setValue(multivalues.replace("[","").replace("]","").replace(" ", ""));
			}
		}
		textbox.setTooltiptext(textbox.getValue());
	}


	public boolean isMultiSelectionResult() {
		return resultMultiSelection;
	}

	public void setMultiSelectionResult(boolean multiSelectionResult) {
		this.resultMultiSelection = multiSelectionResult;
	}

	public Map<String, Object> getMultiSelectValue() {
		return multiSelectValue;
	}

	public void setMultiSelectValue(String value) {
		this.multiSelectValue.clear();	
		if(!StringUtils.trimToEmpty(value).equals("")){
			String vals[] = value.split(",");
			for(String val : vals){
				this.multiSelectValue.put(val, new Object());
			}
		}
	}

	public String getMultiSelectModuleName() {
		return multiSelectModuleName;
	}

	public void setMultiSelectModuleName(String multiSelectModuleName) {
		this.multiSelectModuleName = multiSelectModuleName;
	}

}
