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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  RuleDialogCtrl.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  03-06-2011    
 *                                                                  
 * Modified Date    :  03-06-2011    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-06-2011       Pennant	                 0.1                                         * 
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.LabelElement;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.CorpScoreGroupDetail;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.rulefactory.RuleModule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.PercentageValidator;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/RuleFactorry/Rule/ruleDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class RuleDialogCtrl extends GFCBaseListCtrl<Rule> implements Serializable {

	private static final long serialVersionUID = 966281186831332116L;
	private final static Logger logger = Logger.getLogger(RuleDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RuleDialog; 			// autowired

	//Tree Components
	protected Textbox 		ruleCode; 					// autowired
	protected Textbox 		ruleCodeDesc;				// autowired
	protected Combobox 		ruleModule;					// autowired
	protected Textbox 		ruleEvent;					// autowired
	protected Textbox 		ruleType;					// autowired
	protected Combobox 		waiverDecider;				// autowired
	protected Checkbox 		waiver;						// autowired
	protected Checkbox 		addFeeCharges;				// autowired
	protected Decimalbox 	waiverPercentage;			// autowired
	protected Intbox 		seqOrder;					// autowired
	protected Combobox 		returnType;					// autowired
	protected Longbox 		groupId;					// autowired
	protected Textbox 		lovDescGroupName;			// autowired
	protected Label 		label_seqOrder;				// autowired
	protected Label 		label_RuleTitle;			// autowired
	protected Grid			grid_basicDetail;			// autowired
	
	protected Label 		label_RuleDialog_GroupId;	// autowired
	protected Label 		label_RuleDialog_AddFeeCharges;	// autowired
	protected Hbox 			hbox_AddFeeCharges;			// autowired
	protected Hbox 			hbox_groupId;				// autowired
	
	protected Tree 			tree;						// autowired
	protected Treechildren 	treechildren;				// autowired
	protected Treeitem 		treeitem;					// autowired
	protected Treerow 		treerow;					// autowired
	protected Space 		space;						// autowired
	protected Codemirror	sqlRule;					// autowired
	protected Combobox 		combo;						// autowired
	protected Tabpanel 		tabPanel_tree;
	protected Tabpanel 		tabPanel_RuleResult;
	protected Tab 			tab_ruleDesign;
	protected Tab			tab_textRule;
	protected Row			row_waiverDecider;
	protected Row			row_waiver;
	protected Row			row_feeCharge;
	protected Space			space_waiver;
	
	//Tree Buttons 
	protected Button removeButton;											// autowired
	protected Button addElseIfButton;										// autowired
	protected Button addWithInIfButton;										// autowired
	protected Button addNestedIfButton;										// autowired
	protected Button addElseButton;											// autowired
	protected Button returnButton;											// autowired
	protected Button calValueButton;										// autowired
	protected Button btnSearchGroupId;										// autowired

	protected Label 	  recordStatus; 									// autowired
	protected Radiogroup  userAction;										// autowired
	protected Groupbox 	  groupboxWf;										// autowired

	// not auto wired vars
	private Rule rule; // overhanded per param
	private transient RuleListCtrl ruleListCtrl; // overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last initialization.
	private transient String oldVar_ruleCode;
	private transient String oldVar_ruleCodeDesc;
	private transient String oldVar_ruleModule;
	private transient String oldVar_sQLRule;
	private transient String oldVar_recordStatus;
	private transient String oldVar_ruleModuleTemp;
	private transient String oldVar_waiverDecider;
	private transient boolean oldVar_waiver;
	private transient BigDecimal oldVar_waiverPercentage;
	private transient String oldVar_returnType;

	private transient boolean validationOn;
	private boolean notes_Entered = false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_RuleDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	protected Button btnNew; 		// autowire
	protected Button btnEdit; 		// autowire
	protected Button btnDelete; 	// autowire
	protected Button btnSave; 		// autowire
	protected Button btnCancel; 	// autowire
	protected Button btnClose; 		// autowire
	protected Button btnHelp; 		// autowire
	protected Button btnNotes; 		// autowire
	protected Button btnReadValues;	// autowired
	protected Button btnSimulation; // autowire

	//For Main If Condition and for Criteria condition
	private int itemCount = 1;
	private int qryBraketCount = 1;
	private String strSqlRule = "";
	private String strWhereSqlRule = "(";
	private int fldComboSelAtmpts = 1;
	private String actualBlob = "";
	private boolean isItemSibling = false;
	private int parentCount = 1;
	private String fieldType = null;
	private int fieldLength = 0;
	private int mapCount = 1;
	
	//For Sub If Condition Only
	private String returnValue = "";
	private boolean returnCond = false;
	private boolean andCond = false;
	private boolean elsCond = false;
	private boolean isSubIfCond = false;
	
	//For Changes in comboBox's depend on Selection Types in Any ComboBox 
	private Map<String,String> fieldTypeMap = new HashMap<String, String>();
	private Map<String,String> selectionTypeMap = new HashMap<String, String>();
	private Map<String,String> dbTableMap = new HashMap<String, String>();
	private Map<String,Object> fieldObjectMap = new HashMap<String, Object>();
	
	private List<String> sqlRuleValueList = new ArrayList<String>();// list of tree values
	private List<Component> itemList = new ArrayList<Component>();// list of items(treeItem) in tree
	private List<BMTRBFldDetails> objectFieldList = new ArrayList<BMTRBFldDetails>();// retrieve values
	private List<BMTRBFldCriterias> operatorsList = new ArrayList<BMTRBFldCriterias>();// retrieve Operator values
	private List<GlobalVariable> globalVariableList = new ArrayList<GlobalVariable>();// retrieve values from table--GlobalVariable	
	private List<RuleModule> ruleModuleList = new ArrayList<RuleModule>(); // retrieve ruleModuleList
	private List<ValueLabel> moduleList=new ArrayList<ValueLabel>(); //retrieve moduleList

	// ServiceDAOs / Domain Classes
	private transient RuleService ruleService;
	private transient PagedListService pagedListService;
	private String ruleModuleName;	
	private String ruleModuleID;	

	/**
	 * default constructor.<br>
	 */
	public RuleDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RuleDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());	

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		if(args.containsKey("ruleModuleName")){
			ruleModuleName = (String) args.get("ruleModuleName");
		}
		
		if(args.containsKey("ruleModule")){
			ruleModuleID = (String) args.get("ruleModule");
		}
		
		// READ OVERHANDED params !
		if (args.containsKey("rule")) {
			this.rule = (Rule) args.get("rule");
			Rule befImage = new Rule();
			BeanUtils.copyProperties(this.rule, befImage);
			this.rule.setBefImage(befImage);
			setRule(this.rule);
		} else {
			setRule(null);
		}
		
		//Getting List of modules
		ruleModuleList = getRuleService().getRuleModules(getRule().getRuleModule());
		setRuleModuleCombo();
		setWaiverDeciderCombo();
		setReturnTypeCombo();

		doLoadWorkFlow(this.rule.isWorkflow(), this.rule.getWorkflowId(), this.rule.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(),"RuleDialog");
		}

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, 
				true, this.btnNew, this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel,
				this.btnClose, this.btnNotes);
		
		// READ OVERHANDED params !
		// we get the ruleListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete rule here.
		if (args.containsKey("ruleListCtrl")) {
			setRuleListCtrl((RuleListCtrl) args.get("ruleListCtrl"));
		} else {
			setRuleListCtrl(null);
		}
		
		getBorderLayoutHeight();
		int tabPanelboxHeight = borderLayoutHeight-(this.grid_basicDetail.getRows().getVisibleItemCount() * 20)-180;
		this.tree.setHeight(tabPanelboxHeight+"px");
		this.tree.setZclass("z-dottree");
		this.sqlRule.setHeight(tabPanelboxHeight+"px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(getRule());
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");		
		this.ruleCode.setMaxlength(8);
		this.ruleCodeDesc.setMaxlength(50);
		this.waiverPercentage.setMaxlength(5);
		this.waiverPercentage.setFormat(PennantConstants.rateFormate2);
		this.waiverPercentage.setScale(2);
		this.seqOrder.setMaxlength(4);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
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
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RuleDialog",getRole());

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"New"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Edit"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Save"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Delete"));
		this.btnCancel.setVisible(false);
		
		logger.debug("Leaving");
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
	public void onClose$window_RuleDialog(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doClose();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnSave(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		doSave();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering" +event.toString());
		doEdit();
		//remembering old variables
		doStoreInitValues();
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
		PTMessageUtils.showHelpWindow(event, window_RuleDialog);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering"+event.toString());
		doNew();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnDelete(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		doDelete();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering" +event.toString());
		doCancel();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onClick$btnClose(Event event) throws Exception {
		logger.debug("Entering"+event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e);
			throw e;
		}
		logger.debug("Leaving" + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * @throws Exception 
	 * 
	 */
	private void doClose() throws Exception {
		logger.debug("Entering");
		
		boolean close = true;
		if (isDataChanged()) {
			logger.debug("Data Changed(): True");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("Data Changed(): false");
		}
		if(close){
			closeDialog(window_RuleDialog, "RuleDialog");
		}
		logger.debug("Leaving");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original isItemSibling.<br>
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aRule (Rule)
	 */
	public void doWriteBeanToComponents(Rule aRule) {
		logger.debug("Entering");
		this.ruleCode.setValue(aRule.getRuleCode());
		this.ruleCodeDesc.setValue(aRule.getRuleCodeDesc());

		if(!StringUtils.trimToEmpty(aRule.getRuleModule()).equals("")){
			for (int i = 0; i < ruleModuleList.size(); i++) {
				if(ruleModuleList.get(i).getRbmModule().equals(aRule.getRuleModule()) && 
						ruleModuleList.get(i).getRbmEvent().trim().equals(StringUtils.trimToEmpty(aRule.getRuleEvent()))){
					ruleModule.setSelectedIndex(i+1);		
					this.ruleType.setValue(ruleModuleList.get(i).getRbmFldType());
					break;
				}
			}
		}		
		this.ruleEvent.setValue(aRule.getRuleEvent());
		this.sqlRule.setValue(aRule.getSQLRule());
		actualBlob = aRule.getActualBlock();
		if(!StringUtils.trimToEmpty(aRule.getWaiverDecider()).equals("")){
			List<ValueLabel> waiverDeciderList = PennantStaticListUtil.getWaiverDecider();
			for (int i = 0; i < waiverDeciderList.size(); i++) {
				if(waiverDeciderList.get(i).getValue().equals(aRule.getWaiverDecider())){
					waiverDecider.setSelectedIndex(i+1);		
					break;
				}
			}
		}	
		
		waiverSelection();
		this.waiver.setChecked(aRule.isWaiver());
		this.waiverPercentage.setValue(aRule.getWaiverPerc());
		this.addFeeCharges.setChecked(aRule.isAddFeeCharges());
		this.seqOrder.setValue(aRule.getSeqOrder());
		this.groupId.setValue(aRule.getGroupId());
		if(!(this.groupId.getValue() == 0 || this.groupId.getValue() == Long.MIN_VALUE)){
			this.lovDescGroupName.setValue(aRule.getGroupId() +" - "+aRule.getLovDescGroupName());
		}
		onWaiverChecked();
		
		if(!StringUtils.trimToEmpty(aRule.getReturnType()).equals("")){
			List<ValueLabel> returnTypeList = PennantStaticListUtil.getRuleReturnType();
			for (int i = 0; i < returnTypeList.size(); i++) {
				if(returnTypeList.get(i).getValue().equals(aRule.getReturnType())){
					returnType.setSelectedIndex(i+1);		
					break;
				}
			}
		}
		
		this.recordStatus.setValue(aRule.getRecordStatus());
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aRule
	 */
	public void doWriteComponentsToBean(Rule aRule) {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		
		try {
			if(this.ruleModule.getSelectedIndex() == 0){
				this.sqlRule.setValue("");
				this.tab_ruleDesign.setSelected(true);
				throw new WrongValueException(ruleModule, Labels.getLabel("Label_RuleDialog_select_list"));
			}
			if (StringUtils.trimToEmpty(this.sqlRule.getValue()).equals("")){
				throw new WrongValueException(sqlRule, Labels.getLabel("Label_RuleDialog_SQLRule"));
			}
			aRule.setRuleModule(this.ruleModule.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(!this.ruleCode.isReadonly()) {
				JdbcSearchObject<BMTRBFldDetails> searchObj = new JdbcSearchObject<BMTRBFldDetails>(BMTRBFldDetails.class,getListRows());
				searchObj.addTabelName("BMTRBFldDetails");
				searchObj.addFilter(new Filter("RBFldName", this.ruleCode.getValue(), Filter.OP_EQUAL));
				if(this.pagedListService.getSRBySearchObject(searchObj).getTotalCount() > 0){
					throw new WrongValueException(this.ruleCode, Labels.getLabel("label_RuleCodeExcept"));
				}
			}
			aRule.setRuleCode(this.ruleCode.getValue());
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setRuleCodeDesc(this.ruleCodeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setAddFeeCharges(this.addFeeCharges.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setSeqOrder(this.seqOrder.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setLovDescGroupName(this.lovDescGroupName.getValue());
			aRule.setGroupId(this.groupId.longValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.returnType.getSelectedIndex() == 0 || 
					this.returnType.getSelectedItem().getValue().toString().equals("#")){
				throw new WrongValueException(returnType,
						Labels.getLabel("Label_RuleDialog_select_list"));
			}
			aRule.setReturnType(this.returnType.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aRule.setRuleEvent(this.ruleEvent.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.ruleModule.getSelectedIndex() != 0 && 
					this.ruleModule.getSelectedItem().getValue().toString().equals("FEES")){
				if(this.waiverDecider.getSelectedIndex() == 0){
					throw new WrongValueException(waiverDecider,
							Labels.getLabel("Label_RuleDialog_select_list"));
				}
			}
			aRule.setWaiverDecider(this.waiverDecider.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setWaiver(this.waiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setWaiverPerc(this.waiverPercentage.getValue() == null? BigDecimal.ZERO : this.waiverPercentage.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setSQLRule(this.sqlRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aRule.setActualBlock(actualBlob);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		doRemoveValidation();
		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		aRule.setRecordStatus(this.recordStatus.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param aRule
	 * @throws InterruptedException
	 */
	public void doShowDialog(Rule aRule) throws InterruptedException {
		logger.debug("Entering");
		
		// if aRule == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (aRule == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontEnd.
			// We GET it from the backEnd.
			aRule = getRuleService().getNewRule();
			setRule(aRule);
		} else {
			setRule(aRule);
		}

		// set ReadOnly mode accordingly if the object is new or not.
		if (aRule.isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.ruleCode.focus();
		} else {
			this.ruleCodeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aRule);
			
			//Title Renaming related to module
			String title = "window_RuleDialog";
			if(!"".equals(ruleModuleID)){
				title = title + "_"+ruleModuleID;
			}
			this.label_RuleTitle.setValue(Labels.getLabel(title+".title"));
			
			if("SCORES".equals(this.ruleModuleID)){
				this.label_seqOrder.setValue(Labels.getLabel("label_RuleDialog_metricSeqOrder.value"));
			}

			// building tree based on condition
			if (aRule.getActualBlock() != null && aRule.getActualBlock() != "") {
				String str = aRule.getActualBlock();
				StringTokenizer st = new StringTokenizer(str, "|");
				while (st.hasMoreTokens()) {
					sqlRuleValueList.add(st.nextToken());
				}
			}
			
			// getting values from table---GlobalVariable
			JdbcSearchObject<GlobalVariable> searchObj2 = new JdbcSearchObject<GlobalVariable>(GlobalVariable.class);			
			globalVariableList = this.pagedListService.getBySearchObject(searchObj2);	
			
			//getting List of Operators depend on FieldType and SelectionType
			operatorsList = getRuleService().getOperatorsList();
				
			//Build tree Structure with already existing data
			if(aRule.isNewRecord()){
				if (!StringUtils.trimToEmpty(aRule.getRuleModule()).equals("")) {
					if (!(aRule.getRuleModule().equalsIgnoreCase("FEES") || aRule.getRuleModule().equalsIgnoreCase("SCORES"))) {
						Events.sendEvent("onChange$ruleModule",this.window_RuleDialog, "");	
						this.ruleModule.setDisabled(true);
					}else{
						//If case , Only For AIB Requirement
						if(aRule.getRuleModule().equalsIgnoreCase("SCORES")){
							this.ruleModule.setSelectedIndex(2);
							Events.sendEvent("onChange$ruleModule",this.window_RuleDialog, "");	
							readOnlyComponent(true, this.ruleModule);

						}else{
							this.ruleModule.setSelectedIndex(0);
						}
					}
				}
				this.row_waiver.setVisible(false);
				this.row_waiverDecider.setVisible(false);				
			}else{
				String module= this.ruleModule.getSelectedItem().getValue().toString();	
				String event= this.ruleEvent.getValue().trim();
				this.btnSimulation.setVisible(false);
				
				if(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Maintain") &&
						getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Save") 
						&& !getRule().getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
					
					objectFieldList = getRuleService().getFieldList(module,event);
					buildingTree(sqlRuleValueList,aRule);// designing tree structure	
					this.btnSimulation.setVisible(true);
				}
				
				if (!aRule.getRuleModule().equalsIgnoreCase("FEES")) {
					this.row_waiver.setVisible(false);
					this.row_waiverDecider.setVisible(false);				
				}else{
					this.row_waiverDecider.setVisible(true);	
					if(this.waiverDecider.getSelectedItem().getValue().toString().equals("F")){
						this.row_waiver.setVisible(true);
					}
				}
			}
			isItemSibling = false;

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_RuleDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}


	/**
	 * Build Tree using List of component values or for new tree
	 */
	public void buildingTree(List<String> ruleValues, Rule aRule) throws Exception {
		logger.debug("Entering");
		Component component1 = (Component) tree;
		itemList.add(component1);
		treechildren = new Treechildren();
		treeitem = new Treeitem();
		// creating tree
		buildTreeCell(ruleValues, aRule,false,true,false,false);
		logger.debug("Leaving");
	}
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in member vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		this.oldVar_ruleCode = this.ruleCode.getValue();
		this.oldVar_ruleCodeDesc = this.ruleCodeDesc.getValue();
		this.oldVar_ruleModule = this.ruleModule.getSelectedItem().getValue().toString();
		this.oldVar_ruleModuleTemp = this.ruleModule.getSelectedItem().getValue().toString();
		this.oldVar_waiver = this.waiver.isChecked();
		this.oldVar_waiverDecider = this.waiverDecider.getSelectedItem().getValue().toString();
		this.oldVar_waiverPercentage = this.waiverPercentage.getValue();
		this.oldVar_sQLRule = this.sqlRule.getValue();
		this.oldVar_returnType = this.returnType.getSelectedItem().getValue().toString();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from member vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		this.ruleCode.setValue(this.oldVar_ruleCode);
		this.ruleCodeDesc.setValue(this.oldVar_ruleCodeDesc);
		for (int i = 0; i < ruleModule.getItemCount(); i++) {
			if (ruleModule.getItemAtIndex(i).getValue().equals(this.oldVar_ruleModule)) {
				this.ruleModule.setSelectedIndex(i);
				break;
			}
		}
		this.sqlRule.setValue(this.oldVar_sQLRule);
		for (int i = 0; i < waiverDecider.getItemCount(); i++) {
			if (waiverDecider.getItemAtIndex(i).getValue().equals(this.oldVar_waiverDecider)) {
				this.waiverDecider.setSelectedIndex(i);
				break;
			}
		}
		this.waiver.setChecked(this.oldVar_waiver);
		this.waiverPercentage.setValue(this.oldVar_waiverPercentage);

		for (int i = 0; i < returnType.getItemCount(); i++) {
			if (returnType.getItemAtIndex(i).getValue().equals(this.oldVar_returnType)) {
				this.returnType.setSelectedIndex(i);
				break;
			}
		}
		
		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		this.recordStatus.setValue(this.oldVar_recordStatus);
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged() {
		
		doRemoveValidation();
		doClearMessage();

		if (this.oldVar_ruleModule != this.ruleModule.getSelectedItem().getValue().toString()) {
			return true;
		}
		if (this.oldVar_ruleCodeDesc != this.ruleCodeDesc.getValue()) {
			return true;
		}
		if (this.oldVar_sQLRule != this.sqlRule.getValue()) {
			return true;
		}
		if (this.oldVar_waiverDecider != this.waiverDecider.getSelectedItem().getValue().toString()) {
			return true;
		}
		if (this.oldVar_waiver != this.waiver.isChecked()) {
			return true;
		}
		if (this.oldVar_waiverPercentage != this.waiverPercentage.getValue()) {
			return true;
		}
		if (this.oldVar_returnType != this.returnType.getSelectedItem().getValue().toString()) {
			return true;
		}
		return false;
	}

	/**
	 * Remove Error Messages for Fields
	 */
	private void doClearMessage() {
		logger.debug("Entering");
		this.ruleCode.setErrorMessage("");
		this.ruleCodeDesc.setErrorMessage("");
		this.waiverPercentage.setErrorMessage("");
		this.returnType.setErrorMessage("");
		logger.debug("Leaving");
	}
	
	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		if (!this.ruleCode.isReadonly()){
			this.ruleCode.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleDialog_ruleCode.value"),PennantRegularExpressions.REGEX_ALPHANUM, true));
		}
		if (!this.ruleCodeDesc.isReadonly()){
			this.ruleCodeDesc.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleDialog_ruleCodeDesc.value"), 
					PennantRegularExpressions.REGEX_DESCRIPTION, true));
		}
		if (!this.waiverPercentage.isDisabled()){
			if(this.waiver.isChecked()){
				this.waiverPercentage.setConstraint(new PercentageValidator(5,2,
						Labels.getLabel("label_RuleDialog_waiver.value")));
			}
		}
		if (this.hbox_groupId.isVisible() && !this.groupId.isReadonly()){
			this.lovDescGroupName.setConstraint(new PTStringValidator(Labels.getLabel("label_RuleDialog_GroupId.value"), null, true));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		this.ruleCode.setConstraint("");
		this.ruleModule.setConstraint("");
		this.ruleCodeDesc.setConstraint("");
		this.waiverPercentage.setConstraint("");
		this.returnType.setConstraint("");
		logger.debug("Leaving");
	}

	/**
	 * Method for Build TreeItem as Sibling to calling Component
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onAddButtonClicked(Event event) throws Exception {
		logger.debug("Entering");
		Component component = (Component) event.getData();
		treeitem = new Treeitem();
		Rule aRule = getRule();
		buildTreeCell(sqlRuleValueList, aRule,false,true,false,true);
		addButtonLogic(component);
		logger.debug("Leaving");
	}

	/**
	 * Logic for creating sibling treeItem
	 * 
	 * @param cmp
	 */
	public void addButtonLogic(Component cmp){
		logger.debug("Entering");
		treeitem.appendChild(treerow);
		
		//logic for disable the Else button , because it enable only once
		Treecell treecell = (Treecell)treerow.getChildren().get(0);
		Label label = (Label) treecell.getFirstChild().getNextSibling().getNextSibling();
		if(label.getValue().equals("else")){
			Treecell trcell = (Treecell)((Treerow)((Treeitem)((Treechildren)cmp.getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getChildren().get(0);
			if(trcell.getLastChild() instanceof Space){
				Button btn = (Button)trcell.getLastChild().getPreviousSibling();
				btn.setDisabled(true);
			}else if(trcell.getLastChild() instanceof Textbox){
				Button btn = (Button)trcell.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
				btn.setDisabled(true);
			}
		}
		Component parent;
		if(!isItemSibling){
			parent = cmp.getParent();
		}else{
			parent = (Component) itemList.get(itemList.size()-2);
		}
		if (cmp.getNextSibling() != null) {
			parent.insertBefore(treeitem, cmp.getNextSibling());
		} else {
			if((parent instanceof Tree) && parentCount==1){
				if(parent.getChildren().get(0) instanceof Treechildren){
					((Component) parent.getChildren().get(0)).appendChild(treeitem);
				}else{
					parent.appendChild(treeitem);
					parentCount++;
				}
			}else if(!isItemSibling){
				parent.appendChild(treeitem);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for adding child treeItem calling Component
	 * 
	 * @param event
	 * @throws Exception 
	 */
	public void onAddSubButtonClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		Component cmp = (Component) event.getData();
		treeitem = new Treeitem();		
		Rule aRule = getRule();
		buildTreeCell(sqlRuleValueList,aRule,true,false,false,false);
		addSubButtonLogic(cmp);
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Logic for creating Child treeItem 
	 * 
	 * @param cmp
	 */
	public void addSubButtonLogic(Component cmp) {
		logger.debug("Entering");

		treeitem.appendChild(treerow);
		int found = 0;
		List<Component> treeChild = cmp.getChildren();
		for (int i = 0; i < treeChild.size(); i++) {
			if (treeChild.get(i) instanceof Treechildren) {
				treechildren = (Treechildren) treeChild.get(i);
				List<Component> child = treechildren.getChildren();
				if (child.get(child.size()-1) instanceof Treeitem) {
					found = 1;
					List<Component> childList  = ((Treeitem)child.get(child.size()-1)).getChildren();

					if(childList.size()>1){
						Treeitem lastComp = (Treeitem)child.get(child.size()-1);
						treechildren.insertBefore(treeitem, lastComp);
					}else{
						treechildren.appendChild(treeitem);
					}
				}
			}
		}
		if (found == 0) {
			treechildren = new Treechildren();
			treechildren.appendChild(treeitem);
			cmp.appendChild(treechildren);
		}
		logger.debug("Leaving");
	}

	/**
	 * action on clicking remove button, if child exist add that into previous sibling
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onRemoveButtonClicked(Event event) {
		logger.debug("Entering" +event.toString());
		List<Object> list = (List<Object>) event.getData();
		boolean elsCon = (Boolean) list.get(0);
		Component childComp = (Component) list.get(1);
		
		//Enable Else button after removing the Else Condition Item
		if(elsCon){
			Treecell treecell = (Treecell)((Treerow)((Treeitem)childComp.getParent().getFirstChild()).getChildren().get(0)).getChildren().get(0);
			Button elsButton = null;
			if(treecell.getLastChild() instanceof Textbox){
				elsButton = (Button) treecell.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
				elsButton.setDisabled(false);
			}else if(treecell.getLastChild() instanceof Space){
				elsButton = (Button) treecell.getLastChild().getPreviousSibling();
				elsButton.setDisabled(false);
			}
		}
		
		boolean subIfCond = false;
		List<Component> child = childComp.getParent().getChildren();
		if (child.size() > 1) {
			if(child.size() == 2){
				if(childComp.getNextSibling() != null){
					
					Treerow row = (Treerow)((Treeitem)childComp.getNextSibling()).getChildren().get(0);
					if(row.getChildren().size() != 0){

						Treecell treecell = (Treecell)row.getChildren().get(0);
						if(treecell.getFirstChild().getNextSibling().getNextSibling() instanceof Label){
							Label label = (Label) treecell.getFirstChild().getNextSibling().getNextSibling();
							if(label.getValue().equals("else")){
								childComp.getNextSibling().detach();
								addReturnCondition(childComp);
							}else{
								//Check for Else condition Exist or not
								Treecell tc = (Treecell)((Treerow)((Treeitem)childComp.getParent().getLastChild()).getChildren().get(0)).getChildren().get(0);
								Label lbl = (Label) tc.getFirstChild().getNextSibling().getNextSibling();
								boolean elsCondition = false;
								if(lbl.getValue().equals("else")){
									elsCondition = true;
								}
								childComp.detach();
								//Changing label and UnDisable the Else Button if Else condition not exist
								if(label.getValue().equals("else if")){
									if(!elsCondition){
										if(treecell.getLastChild() instanceof Space){
											Button btn = (Button)treecell.getLastChild().getPreviousSibling();
											btn.setDisabled(false);
										}else if(treecell.getLastChild() instanceof Textbox){
											Button btn = (Button)treecell.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
											btn.setDisabled(false);
										}
									}
									label.setValue("if");
									label.setStyle("font-weight:bold;margin-right:30px;margin-left:20px;");
								}
							}
						}else {
							childComp.detach();
						}
					}else{
						childComp.detach();
					}
				}else{
					childComp.detach();
				}
			}else{
				if(childComp.getPreviousSibling() == null){
					Treerow treerow = (Treerow)((Treeitem)childComp.getParent().getLastChild()).getChildren().get(0);
					if(treerow.getChildren().size() != 0){
						//Check for Else condition Exist or not
						Treecell trcell = (Treecell)treerow.getChildren().get(0);
						boolean elsCondition = false;
						if(trcell.getFirstChild().getNextSibling().getNextSibling() instanceof Label){
							Label lbl = (Label) trcell.getFirstChild().getNextSibling().getNextSibling();
							if(lbl.getValue().equals("else")){
								elsCondition = true;
							}

							Treerow row = (Treerow)((Treeitem)childComp.getNextSibling()).getChildren().get(0);
							if(row.getChildren().size() != 0){
								//Changing label and UnDisable the Else Button if Else condition not exist
								Treecell tc = (Treecell)row.getChildren().get(0);
								Label label = (Label) tc.getFirstChild().getNextSibling().getNextSibling();
								if(label.getValue().equals("else if")){
									if(!elsCondition){
										if(tc.getLastChild() instanceof Space){
											Button btn = (Button)tc.getLastChild().getPreviousSibling();
											btn.setDisabled(false);
										}else if(tc.getLastChild() instanceof Textbox){
											Button btn = (Button)tc.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
											btn.setDisabled(false);
										}
									}
									label.setValue("if");
									label.setStyle("font-weight:bold;margin-right:30px;margin-left:20px;");
								}
							}
						}
					}
					childComp.detach();
				}else{
					childComp.detach();
				}
			}
		} else {
			Treeitem item = (Treeitem)childComp.getParent().getParent();
			List<Component> childList = item.getChildren();
			for (int i = 0; i <childList.size(); i++) {
				if(childList.get(i) instanceof Treerow){
					if(childList.get(i).getChildren().size()>0){
						subIfCond = true;
					}
				}
			}
			if(!subIfCond){
				addReturnCondition(childComp);
			}else{
				childComp.getParent().detach();
			}
		}
		logger.debug("Leaving" +event.toString());
	}
	
	private void addReturnCondition(Component childComp){
		
		//Add return Condition
		Treecell tc = (Treecell)((Treerow)(childComp.getParent().getParent().getParent().getParent()).getChildren().get(0)).getChildren().get(0);
		
		Button btn  = (Button) tc.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
		btn.setDisabled(false);
		returnButton = new Button();
		returnButton.setLabel("R");
		returnButton.setStyle("font-weight:bold;dir:reverse;");
		returnButton.setTooltiptext("Click for Add Return Value");
		returnButton.setImage("/images/icons/Right_Arrow.png");
		Textbox textbox = getSelectText();
		tc.appendChild(returnButton);
		tc.appendChild(textbox);
		returnButton.addForward("onClick",window_RuleDialog,"onReturnButtonClicked",textbox);
		
		if(childComp.getParent().getParent().getParent().getChildren().size()>1){
			childComp.getParent().getParent().detach();
		}else{
			childComp.getParent().getParent().getParent().detach();
		}
	}
	
	/**
	 * Method for Adding SubIf Condition
	 * @param event
	 * @throws Exception
	 */
	public void onAddSubIfButtonClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		Component cmp = null;
		@SuppressWarnings("unchecked")
		List<Object> list = (List<Object>) event.getData();
		if(list.get(0) instanceof Button){
			Button btn  = (Button) list.get(0);
			btn.setDisabled(true);
		}
		cmp = (Treeitem) list.get(1);
		
		treeitem = new Treeitem();		
		Rule aRule = getRule();
		buildTreeCell(sqlRuleValueList,aRule,false,true,false,false);
		addSubIfButtonLogic(cmp);
		logger.debug("Leaving" +event.toString());
	}
	
	public void addSubIfButtonLogic(Component cmp) {
		logger.debug("Entering");
		
		//Remove return Condition
		Treecell treecell = (Treecell)((Treerow)cmp.getChildren().get(0)).getChildren().get(0);
		if(treecell.getLastChild() instanceof Textbox){
			treecell.removeChild(treecell.getLastChild());
			treecell.removeChild(treecell.getLastChild());
			((Button)treecell.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling()).setDisabled(true);
		}

		treeitem.appendChild(treerow);
		List<Component> treeChild = cmp.getChildren();
		if(treeChild.size() >1){
			for (int i = 0; i < treeChild.size(); i++) {
				if (treeChild.get(i) instanceof Treechildren) {
					treechildren = (Treechildren) treeChild.get(i);
					List<Component> child = treechildren.getChildren();
					if(child.size()>0) {
						if (child.get(child.size()-1) instanceof Treeitem) {
							List<Component> subChild = child.get(child.size()-1).getChildren();
							boolean childExist = false;
							for (int j = 0; j < subChild.size(); j++) {
								if (subChild.get(j) instanceof Treechildren) {
									((Treechildren)subChild.get(j)).appendChild(treeitem);
									childExist =true;
									break;
								}
							}
							if(!childExist){
								Treeitem  ti= new Treeitem();
								ti.setHeight("1px");
								Treechildren treechild = new Treechildren();
								Treerow tr = new Treerow();
								treechild.appendChild(treeitem);
								ti.appendChild(tr);
								ti.appendChild(treechild);
								treechildren.appendChild(ti);
								treechildren.setParent(cmp);
							}
						}
					}
				}
			}
			
			//Check for Else condition Exist or not
			Treecell tc = (Treecell)treerow.getChildren().get(0);
			Label label = (Label) tc.getFirstChild().getNextSibling().getNextSibling();
			if(label.getValue().equals("else")){
				Treecell tcell = (Treecell)((Treerow)((Treeitem)treeitem.getParent().getFirstChild()).getChildren().get(0)).getChildren().get(0);
				if(tcell.getLastChild() instanceof Space){
					Button btn = (Button)tcell.getLastChild().getPreviousSibling();
					btn.setDisabled(true);
				}else if(tcell.getLastChild() instanceof Textbox){
					Button btn = (Button)tcell.getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling();
					btn.setDisabled(true);
				}
			}			
		}else{
			treechildren = new Treechildren();
			Treeitem  ti= new Treeitem();
			ti.setHeight("1px");
			Treechildren tch = new Treechildren();
			Treerow tr = new Treerow();
			tch.appendChild(treeitem);
			ti.appendChild(tr);
			ti.appendChild(tch);
			treechildren.appendChild(ti);
			cmp.appendChild(treechildren);
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Method for Build TreeItem as Sibling to calling Component
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onElseButtonClicked(Event event) throws Exception {
		logger.debug("Entering");
		List<Object> list = (List<Object>) event.getData();
		Component component = null;
		if(list.get(0) instanceof Button){
			Button btn  = (Button) list.get(0);
			btn.setDisabled(true);
		}
		component = (Treeitem) list.get(1);
		
		treeitem = new Treeitem();
		Rule aRule = getRule();
		buildTreeCell(sqlRuleValueList, aRule,false,true,true,false);
		elseButtonLogic(component);
		logger.debug("Leaving");
	}

	/**
	 * Logic for creating sibling treeItem
	 * 
	 * @param cmp
	 */
	public void elseButtonLogic(Component cmp){
		logger.debug("Entering");
		treeitem.appendChild(treerow);
		Component parent;
		if(!isItemSibling){
			parent = cmp.getParent();
		}else{
			parent = (Component) itemList.get(itemList.size()-2);
		}
		if((parent instanceof Tree) && parentCount==1){
			if(parent.getChildren().get(0) instanceof Treechildren){
				((Component) parent.getChildren().get(0)).appendChild(treeitem);
			}else{
				parent.appendChild(treeitem);
				parentCount++;
			}
		}else if(!isItemSibling){
			parent.appendChild(treeitem);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method of Event for Return button	
	 * @param event
	 * @throws Exception
	 */
	public void onReturnButtonClicked(Event event) throws Exception{
		Textbox comp  = (Textbox) event.getData();
		comp.setErrorMessage("");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("returnValue", comp.getValue());
		map.put("textbox", comp);
		map.put("rbModule", ruleModule.getSelectedItem().getValue().toString());
		map.put("rbEvent", ruleEvent.getValue());
		
		Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultDialog.zul",
				window_RuleDialog,map);
	}
	
	/**
	 * Method for calling an Event for both Calculated and for DB selection Values
	 * @param event
	 * @throws Exception
	 */
	public void onValueButtonClicked(Event event) throws Exception{
		Button comp  = (Button) event.getData();
		Textbox textbox = ((Textbox) comp.getNextSibling());
		textbox.setErrorMessage("");
		
		Combobox selectionCombo = (Combobox) comp.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
		String selectionType = selectionCombo.getSelectedItem().getValue().toString();
		if(selectionType.equals("calvalue")){
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("returnValue",textbox.getValue());
			map.put("textbox", textbox);
			map.put("rbModule", ruleModule.getSelectedItem().getValue().toString());
			map.put("rbEvent", ruleEvent.getValue());
			
			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultDialog.zul",
					window_RuleDialog,map);
		}else if(selectionType.equals("dbvalue")){
			Combobox operatorCombo = (Combobox) comp.getPreviousSibling().getPreviousSibling();
			if (operatorCombo.getSelectedItem() == null) {
				throw new WrongValueException(operatorCombo, "Please select Operator ");
			}
			String oprLabel = operatorCombo.getSelectedItem().getLabel().toString();
			Combobox fieldCombo = (Combobox) selectionCombo.getPreviousSibling().getPreviousSibling();
			String fieldName = fieldCombo.getSelectedItem().getValue().toString();
			
			//For Calling Extended ListBox From DB
			if(oprLabel.equalsIgnoreCase("Equal") || oprLabel.equalsIgnoreCase("Not Equal")){
				Object dataObject = ExtendedSearchListBox.show(this.window_RuleDialog, dbTableMap.get(fieldName));
				if (dataObject == null) {
					//do Nothing
				}else if (dataObject instanceof String) {
					textbox.setValue("");
				} else {
					String textValue = dataObject.getClass().getMethod("getLovValue").invoke( dataObject).toString();
					if (textValue != null) {
						textbox.setValue(textValue);
					}
				}
			}else if(oprLabel.equalsIgnoreCase("IN") || oprLabel.equalsIgnoreCase("Not IN")){
				//Calling MultiSelection ListBox From DB
				String selectedValues= (String) MultiSelectionSearchListBox.show(this.window_RuleDialog, dbTableMap.get(fieldName),textbox.getValue(),new Filter[]{});
				if (selectedValues!= null) {
					textbox.setValue(selectedValues);
					textbox.setTooltiptext(selectedValues);
				}else{
					//do Nothing
				}
			}
		}
	}
	
	/**
	 * 	common code for generating treeCell
	 * 
	 * @param ruleValues
	 * @param aRule
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public void buildTreeCell(List<String> ruleValues, Rule aRule,boolean withInIFCond, 
			boolean nestedIF,boolean elseCond,boolean elsIfCond) throws Exception {
		logger.debug("Entering");
		
		// Add treeItem to Component itemList
		Component component = (Component) treeitem;
		treeitem.setWidth("100%");
		itemList.add(component);

		// create Buttons and Add Forward Events to buttons
		removeButton = new Button();
		removeButton.setTooltiptext("remove Rule Condition");
		removeButton.setImage("/images/icons/delete.png");
		removeButton.setStyle("padding:0px");
		
		addElseIfButton = new Button();
		addElseIfButton.setTooltiptext("Add Else IF Condition");
		addElseIfButton.setImage("/images/icons/elseif16.png");
		addElseIfButton.setStyle("padding:0px");
		
		addWithInIfButton = new Button();
		addWithInIfButton.setTooltiptext("Add Condition WithIn IF");
		addWithInIfButton.setImage("/images/icons/andblue.png");
		addWithInIfButton.setStyle("padding:0px");
		
		addNestedIfButton = new Button();
		addNestedIfButton.setTooltiptext("Add Nested IF Condition");
		addNestedIfButton.setImage("/images/icons/Nestedif16.png");
		addNestedIfButton.setStyle("padding:0px");
		
		addElseButton = new Button();
		addElseButton.setTooltiptext("Add Else Condition");
		addElseButton.setImage("/images/icons/else16.png");
		addElseButton.setStyle("padding:0px");

		List<Object> list = new ArrayList<Object>();
		list.add(elseCond);
		list.add(treeitem);
		removeButton.addForward("onClick", window_RuleDialog, "onRemoveButtonClicked", list);

		// create treeCell with in item of tree
		treerow = new Treerow();
		Treecell treecell = new Treecell();
		
		// front space
		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		treecell.appendChild(removeButton);

		// Condition for not generate Criteria comboBox at first time creation
		// of treeItem
		if (itemCount != 1 && !nestedIF) {
			space = new Space();
			space.setWidth("5px");
			treecell.appendChild(space);
			combo = getComboCondition();
			combo.setReadonly(true);
			if (ruleValues.size() > 0 && !aRule.isNew()) {

				if ("AND".equalsIgnoreCase(ruleValues.get(0).toString())
						|| "OR".equalsIgnoreCase(ruleValues.get(0).toString())
						|| "--select--".equalsIgnoreCase(ruleValues.get(0).toString())) {
					for (int i = 0; i < combo.getItemCount(); i++) {
						if (combo.getItemAtIndex(i).getLabel().equals(ruleValues.get(0))) {
							combo.setSelectedIndex(i);
							ruleValues.remove(0);
							break;
						}
					}
				}
			}
			treecell.appendChild(combo);
		} else {
			if(nestedIF){
				Label label = null;
				if(elseCond){
					label = new Label("else");
					label.setStyle("font-weight:bold;margin-right:15px;margin-left:18px;");
				}else if(elsIfCond){
					label = new Label("else if");
					label.setStyle("font-weight:bold;margin-right:1px;margin-left:18px;");
				}else{
					label = new Label("if");
					label.setStyle("font-weight:bold;margin-right:30px;margin-left:20px;");
				}
				treecell.appendChild(label);
			}else{
				space = new Space();
				space.setWidth("5px");
				treecell.appendChild(space);
				
				space = new Space();
				space.setWidth("5px");
				treecell.appendChild(space);
			}
		}

		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		combo = getFields();// first comboBox for Fields
		combo.setReadonly(true);
		combo.setTooltiptext(combo.getValue());
		if(elseCond){
			combo.setDisabled(true);
		}
		Textbox textbox = null ;

		String field = "";
		// Set field to ComboBox depend on fieldList and getting rule
		if (ruleValues.size() > 0 && !aRule.isNew()) {		

			ruleValues.remove(0);	
			for (int i = 0; i < combo.getItemCount(); i++) {
				if(elseCond){
					ruleValues.remove(0);	
					break;
				}else if (combo.getItemAtIndex(i).getValue().equals(ruleValues.get(0).trim())) {
					combo.setSelectedIndex(i);
					for (int j = 0; j < objectFieldList.size(); j++) {
						BMTRBFldDetails fldDetails = objectFieldList.get(j);
						String str = combo.getSelectedItem().getValue().toString();
						if (fldDetails.getRbFldName().equals(str)) {
							setFieldType(fldDetails.getRbFldType());
							setFieldLength(fldDetails.getRbFldLen());
							field = fldDetails.getRbSTFlds();
							break;
						}
					}
					ruleValues.remove(0);
					break;
				}
			}
		}
		treecell.appendChild(combo);

		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		String selectType ="";
		// Set fieldType to ComboBox depend on staticList of types and getting rule
		Combobox comb = getSelectionTypeItem(field, new Combobox());// selection type comboBox
		comb.setReadonly(true);
		if(elseCond){
			comb.setDisabled(true);
		}
		if (ruleValues.size() > 0 && !aRule.isNew()) {
			if(elseCond){
				ruleValues.remove(0);	
			}
			for (int i = 0; i < comb.getItemCount(); i++) {
				if (comb.getItemAtIndex(i).getValue().equals(ruleValues.get(0))) {
					comb.setSelectedIndex(i);
					ruleValues.remove(0);
					selectType = comb.getSelectedItem().getValue().toString();
					break;
				}
			}
		}
		treecell.appendChild(comb);
		
		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		
		// Set Logical operators for rule
		combo = getCriteria(getFieldType(),selectType,new Combobox());// condition comboBox
		combo.setReadonly(true);
		if(elseCond){
			combo.setDisabled(true);
		}
		if (ruleValues.size() > 0 && !aRule.isNew()) {
			if(elseCond){
				ruleValues.remove(0);	
			}
			for (int i = 0; i < combo.getItemCount(); i++) {
				if (combo.getItemAtIndex(i).getValue().equals(ruleValues.get(0))) {
					combo.setSelectedIndex(i);
					ruleValues.remove(0);
					break;
				}
			}
		}
		treecell.appendChild(combo);

		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		
		calValueButton = new Button();
		calValueButton.setImage("/images/icons/Right_Arrow.png");
		calValueButton.setStyle("padding:0px;");
		calValueButton.setTooltiptext("Click Button for Add Value");
		calValueButton.setDisabled(true);
		calValueButton.addForward("onClick",window_RuleDialog,"onValueButtonClicked",calValueButton);
		treecell.appendChild(calValueButton);
		
		if(elseCond){
			textbox = getSelectText();
			textbox.setReadonly(true);
			treecell.appendChild(textbox);
		}
		
		// conditions for selecting comboBox depend on field selection type
		if (ruleValues.size() > 0 && !aRule.isNew()) {
			if(elseCond){
				ruleValues.remove(0);	
				if(!(treecell.getLastChild() instanceof Textbox)){
					textbox = getSelectText();
					textbox.setReadonly(true);
					treecell.appendChild(textbox);
				}
			}else{
				String selectionType = (String) comb.getSelectedItem().getValue();
				if (selectionType.equals("static")) {// depend on type of field
					if (getFieldType().equalsIgnoreCase("nvarchar")) {
						textbox = getSelectText();
						textbox.setMaxlength(getFieldLength());
						textbox.setValue(ruleValues.get(0).toString());
						treecell.appendChild(textbox);
					} else if (getFieldType().equalsIgnoreCase("nchar")) {
						if(getFieldLength() == 1){
							Combobox booleanCombo = getBooleanVariables();
							for (int i = 0; i < booleanCombo.getItemCount(); i++) {
								if (booleanCombo.getItemAtIndex(i).getValue().equals(ruleValues.get(0))) {
									booleanCombo.setSelectedIndex(i);
									break;
								}
							}
							treecell.appendChild(booleanCombo);
						}else{
							textbox = getSelectText();
							textbox.setMaxlength(getFieldLength());
							textbox.setValue(ruleValues.get(0).toString());
							treecell.appendChild(textbox);
						}
					} else if (getFieldType().equalsIgnoreCase("bigint")) {
						Intbox intbox = getIntBox();
						intbox.setMaxlength(getFieldLength());
						intbox.setValue(Integer.valueOf(ruleValues.get(0).toString()));
						treecell.appendChild(intbox);
					} else if (getFieldType().equalsIgnoreCase("decimal")) {
						Decimalbox decimalbox = getDecimalbox();
						decimalbox.setMaxlength(getFieldLength());
						decimalbox.setValue(BigDecimal.valueOf(Double.valueOf(ruleValues.get(0).toString())));
						treecell.appendChild(decimalbox);
					} else if (getFieldType().equalsIgnoreCase("smalldatetime")) {
						Datebox datebox = getDateBox();
						datebox.setValue(DateUtility.getDBDate(ruleValues.get(0).toString()));
						treecell.appendChild(datebox);
					}
					ruleValues.remove(0);
					fldComboSelAtmpts++;
				} else if (selectionType.equals("calvalue")) {// Entered field Type

					textbox = getSelectText();
					textbox.setValue(ruleValues.get(0).toString());
					treecell.appendChild(textbox);
					calValueButton.setDisabled(false);
					ruleValues.remove(0);
					fldComboSelAtmpts++;
				} else if (selectionType.equals("dbvalue")) {// Entered field Type

					textbox = getSelectText();
					textbox.setValue(ruleValues.get(0).toString());
					treecell.appendChild(textbox);
					calValueButton.setDisabled(false);
					ruleValues.remove(0);
					fldComboSelAtmpts++;
				}else if (comb.getSelectedItem().getValue().equals("global")) {// Global variable field type
					combo = getGlobalVariables();
					for (int i = 0; i < combo.getItemCount(); i++) {
						if (combo.getItemAtIndex(i).getValue().equals(ruleValues.get(0))) {
							combo.setSelectedIndex(i);
							ruleValues.remove(0);
							fldComboSelAtmpts++;
							break;
						}
					}
					treecell.appendChild(combo);
				}
			}
		}

		treecell.appendChild(addElseIfButton);
		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		treecell.appendChild(addWithInIfButton);

		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		treecell.appendChild(addNestedIfButton);
		
		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		treecell.appendChild(addElseButton);

		//return Condition
		space = new Space();
		space.setWidth("5px");
		treecell.appendChild(space);
		if(withInIFCond){
			addElseIfButton.setVisible(false);
			addWithInIfButton.setVisible(false);
			addNestedIfButton.setVisible(false);
			addElseButton.setVisible(false);
		}
		if(elseCond){
			addElseIfButton.setDisabled(true);
			addWithInIfButton.setDisabled(true);
			addElseButton.setDisabled(true);
		}
		if(elsIfCond){
			addElseButton.setDisabled(true);
		}
		if(!withInIFCond){
			returnButton = new Button();
			returnButton.setLabel("R");
			returnButton.setStyle("font-weight:bold;dir:reverse;padding:0px;");
			returnButton.setTooltiptext("Click for Add Return Value");
			returnButton.setImage("/images/icons/Right_Arrow.png");
			treecell.appendChild(returnButton);
			textbox = getSelectText();
			if (ruleValues.size() > 0 && !aRule.isNew()) {
				if(!(ruleValues.get(0).equals("AND") || ruleValues.get(0).equals("OR") || 
						ruleValues.get(0).equals(")") || ruleValues.get(0).equals("(") || 
						ruleValues.get(0).equals("{") || ruleValues.get(0).equals("}"))){
					textbox.setValue(ruleValues.get(0).toString());
					ruleValues.remove(0);
				}
			}
			returnButton.addForward("onClick",window_RuleDialog,"onReturnButtonClicked",textbox);
			treecell.appendChild(textbox);
		}

		treecell.setStyle("display:inline-block;_display:inline;padding:0px;");// Alignment
		treerow.appendChild(treecell);

		// Add forward Events for creating new item(RuleCondition)
		addElseIfButton.addForward("onClick", window_RuleDialog, "onAddButtonClicked", treeitem);
		addWithInIfButton.addForward("onClick", window_RuleDialog, "onAddSubButtonClicked", treeitem);
		list= new ArrayList<Object>();
		list.add(addNestedIfButton);
		list.add(treeitem);
		addNestedIfButton.addForward("onClick", window_RuleDialog, "onAddSubIfButtonClicked",list);
		list = new ArrayList<Object>();
		list.add(addElseButton);
		list.add(treeitem);
		addElseButton.addForward("onClick", window_RuleDialog, "onElseButtonClicked", list);

		// Setting for generating tree item for new rule when creating window
		if (itemCount == 1) {
			removeButton.setDisabled(true);
			treeitem.setHeight("24px");
			treeitem.appendChild(treerow);
			treeitem.setStyle("padding:1px");
			treechildren.appendChild(treeitem);
			tree.appendChild(treechildren);
			tree.setSizedByContent(true);
			isItemSibling = false;
		}

		// condition structure of tree for editing logic
 		if (isSubIfCond) {
			if (itemList.size() > 1) {
				component = (Component) itemList.get(itemList.size() - 2);
			} else {
				component = (Component) itemList.get(itemList.size() -1);
			}
			if(component instanceof Tree){
				addButtonLogic(component);
			}else{
				if(ruleValues.get(1).trim().equals("AND") || ruleValues.get(1).trim().equals("{")){
					addSubButtonLogic(component);
				}else{
					addSubIfButtonLogic(component);
				}
			}
		} else if (qryBraketCount > 1) {
			if (itemList.size() > 1) {
				component = (Component) itemList.get(itemList.size() - 2);
			} else {
				component = (Component) itemList.get(itemList.size() - 1);
			}
			addSubButtonLogic(component);
		}else if (isItemSibling) {
			if (itemList.size() > 1) {
				component = (Component) itemList.get(itemList.size() - 1);
			} else {
				component = (Component) itemList.get(itemList.size());
			}
			addButtonLogic(component);
		}

		itemCount++;
		// Continue the process of building if size is not zero
		if (ruleValues.size() > 0 && !aRule.isNew()) {
			if (ruleValues.get(0).trim().equals(")") || ruleValues.get(0).trim().equals("}")) {
				int size = ruleValues.size();
				for (int i = 0; i < size; i++) {
					if (ruleValues.get(0).trim().equals(")") || ruleValues.get(0).trim().equals("}")) {
						ruleValues.remove(0);
						itemList.remove(itemList.size() - 1);
						qryBraketCount--;
					} else {
						break;
					}
				}
				// Condition for add sibling item
				if (ruleValues.size() != 0) {
					treeitem = new Treeitem();
					qryBraketCount++;
					if(ruleValues.get(0).trim().equals("{")){
						isItemSibling = false;
						isSubIfCond = true;
						buildTreeCell(sqlRuleValueList, aRule,false,true,false,false);
						isSubIfCond = false;
					}else{
						isItemSibling = true;
						isSubIfCond = false;
						if(ruleValues.get(0).trim().equals("(")){
							isSubIfCond = true;
							if(ruleValues.get(1).trim().equals("~")){
								buildTreeCell(sqlRuleValueList, aRule,false,true,true,true);
							}else{
								buildTreeCell(sqlRuleValueList, aRule,false,true,false,true);
							}
							isSubIfCond = false;
						}else{
							buildTreeCell(sqlRuleValueList, aRule,true,false,false,true);
						}
						isItemSibling = false;
					}
				}
			} else if (ruleValues.get(0).trim().equals("(")) {// Condition for adding
				// child item
				ruleValues.remove(0);
				qryBraketCount++;
				if (ruleValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = true;
					isSubIfCond = false;
					buildTreeCell(sqlRuleValueList, aRule,false,true,false,true);
					isItemSibling = false;
				}
			} else if (ruleValues.get(0).trim().equals("{")) {// Condition for adding
				// child item
				if (ruleValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = false;
					isSubIfCond = true;
					qryBraketCount++;
					buildTreeCell(sqlRuleValueList, aRule,false,true,false,false);
					isSubIfCond = false;
				}
			}else {
				if (ruleValues.size() != 0) {
					treeitem = new Treeitem();
					isItemSibling = true;
					isSubIfCond = false;
					qryBraketCount++;
					buildTreeCell(sqlRuleValueList, aRule,true,false,false,true);
					isItemSibling = false;
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * build a rule for values and generate in textBox
	 */
	public void onClick$btnReadValues(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		if(this.ruleModule.getSelectedIndex() == 0){
			this.sqlRule.setValue("");
			this.tab_ruleDesign.setSelected(true);
			throw new WrongValueException(ruleModule, Labels.getLabel("Label_RuleDialog_select_list"));
		}
		readButtonClicked();//calling read button method for generating rule
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Method for building SQLrule 
	 * @throws Exception
	 */
	public void readButtonClicked() throws Exception {
		logger.debug("Entering");
		sqlRule.setValue("");
		strSqlRule = strSqlRule +"\n";
		actualBlob = "(" + "|";
		strWhereSqlRule = "if(";
		doBuildRule(tree);// generate Rule
		sqlRule.setValue(strWhereSqlRule);
		this.tab_textRule.setSelected(true);
		logger.debug("Leaving");
	}

	/**
	 * Common Method for Building Tree and for Retrieving data from Components
	 * 
	 * @param selectionComp
	 * 
	 * @throws Exception
	 */
	public void doBuildRule(Component selectionComp) throws Exception {
		logger.debug("Entering");
		
		if (selectionComp.getChildren().size() == 0 && selectionComp instanceof Treerow){
			if(elsCond){
				strWhereSqlRule = strWhereSqlRule + " \n\t if( ";
			}else{
				strWhereSqlRule = strWhereSqlRule + "){ \n\t if( ";
			}
			actualBlob = actualBlob + "{" + "|";
		}else if (selectionComp.getChildren() != null) {
			int comboCount = 1;
			boolean inCondition = false;		
			
			for (int i = 0; i < selectionComp.getChildren().size(); i++) {
				Component childComp = (Component) selectionComp.getChildren().get(i);
				if (childComp instanceof Combobox) {					
					boolean validate = false;
					Component combo = (Combobox) childComp;
					((Combobox) combo).setErrorMessage("");
					String  value = ((Combobox) combo).getValue().toString();					
					if (value == null || value.equals("") || value.equals("--select--")) {
						if(!((Combobox)combo).isDisabled()){
							validate = false;
						}else{
							validate = true;
						}
					} else {
						for (int i1 = 0; i1 < ((Combobox) combo).getChildren().size(); i1++) {
							if (value.equals(((LabelElement) ((Combobox) combo).getChildren().get(i1)).getLabel())) {
								validate = true;
								break;
							}
						}
					}
					if (!validate) {
						tab_ruleDesign.setSelected(true);
						throw new WrongValueException(combo, Labels.getLabel("STATIC_INVALID", new String[]{""}));
					}

					String comboValue ="~";
					String comboLabel = "";
					if(((Combobox)combo).isDisabled()){
						elsCond = true;
					}else {
						comboValue = ((Combobox) combo).getSelectedItem().getValue().toString();
						comboLabel = ((Combobox) combo).getSelectedItem().getLabel().toString();
						elsCond = false;
					}
					// retrieve values from comboBox(s)
					if ("AND".equalsIgnoreCase(comboLabel)
							|| "OR".equalsIgnoreCase(comboLabel)
									|| "--select--".equalsIgnoreCase(comboLabel)) {

						andCond = true;
						strWhereSqlRule = strWhereSqlRule+ comboValue ;
						actualBlob = actualBlob+ comboLabel + "|" + "("+ "|";
					} else if (comboCount == 2) {
						actualBlob = actualBlob+ comboValue + "|";
						comboCount++;
					} else if (comboCount == 4) {	

						// than string format
						if(!elsCond){
							strWhereSqlRule = strWhereSqlRule+ comboValue+")" ;		
						}

						actualBlob = actualBlob+ comboValue + "|";
						inCondition = false;
						comboCount++;
					} else {
						if (comboCount == 1) {
							if(elsCond){
								strWhereSqlRule = strWhereSqlRule.substring(0, strWhereSqlRule.length()-4)+" {";
							}else{
								for (int j = 0; j < objectFieldList.size(); j++) {
									BMTRBFldDetails fldDetails = objectFieldList.get(j);								
									if (fldDetails.getRbFldName().equals(comboValue)) {
										setFieldType(fldDetails.getRbFldType());// To Get the type of selecting field
										setFieldLength(fldDetails.getRbFldLen());
										break;
									}
								}
							}
						}
						if (comboCount == 3) {
							if (StringUtils.strip(comboValue).equalsIgnoreCase("IN")
									|| StringUtils.strip(comboValue).equalsIgnoreCase("NOT IN")) {

								inCondition = true;
							}
						}
						if(!elsCond){
							if(!inCondition){
								if (comboCount == 1) {
									strWhereSqlRule = strWhereSqlRule+ "("+ comboValue;
								}else if (comboCount == 3) {
									strWhereSqlRule = strWhereSqlRule+ comboValue;
								}
							}
						}

						actualBlob = actualBlob+ comboValue + "|";
						comboCount++;
					}

				}else if (childComp instanceof Textbox) {
					// Component related to textBox
					
					Textbox textbox = (Textbox) childComp;
					textbox.setErrorMessage("");
					if (textbox.getValue()== null
							|| textbox.getValue().equals("")) {
						if(!textbox.isReadonly()){
							tab_ruleDesign.setSelected(true);
							throw new WrongValueException(textbox,Labels.getLabel("Label_RuleDialog_EnterValue"));
						}
					}
					
					 if (inCondition == true) {// Check condition for IN criteria
						 
						Combobox oprtComb =  (Combobox) childComp.getPreviousSibling().getPreviousSibling().getPreviousSibling();
						String oprtval = oprtComb.getSelectedItem().getValue().toString();
						
						Combobox fldComb = (Combobox) oprtComb.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
						 String fldval = fldComb.getSelectedItem().getValue().toString();
						 
						 String str = textbox.getValue().toString();
						 StringTokenizer st = new StringTokenizer(str, ",");
						 strWhereSqlRule = strWhereSqlRule.substring(0, strWhereSqlRule.length()-fldval.length()-1)+"(";
							while (st.hasMoreTokens()) {
								if (getFieldType().equalsIgnoreCase("nvarchar")) {
									if(oprtval.trim().equals("IN")){
										strWhereSqlRule = strWhereSqlRule +fldval+" == " + "'" + st.nextToken() + "'" + " || ";
									}else if(oprtval.trim().equals("NOT IN")){
										strWhereSqlRule = strWhereSqlRule +fldval+" != " +"'" + st.nextToken() + "'" + " || ";
									}
								} 
							}
						strWhereSqlRule = strWhereSqlRule.substring(0, strWhereSqlRule.length()-4)+")";
						inCondition = false;
					}else if(comboCount>4){
						returnValue = textbox.getValue().toString();
						returnCond = true;
						andCond = false;
					}else {
						// condition for string field type
						if (getFieldType().equalsIgnoreCase("nvarchar")) {
							if(!elsCond){
								strWhereSqlRule = strWhereSqlRule + " " + "'"+ textbox.getValue().toString() + "' )";
							}
						} else {// condition for Non-String field type
							if(!elsCond){
								strWhereSqlRule = strWhereSqlRule + " "+ textbox.getValue().toString()+")";
							}
						}
					}
					 String textValue =textbox.getValue().toString();
					 if(textValue.equals("")){
						 textValue = "~";
					 }
					actualBlob = actualBlob +textValue+ "|";
					comboCount++;
				} else if (childComp instanceof Datebox) {
					// Component related to dateBox
					
					Datebox datebox = (Datebox) childComp;
					datebox.setErrorMessage("");
					if (datebox.getValue() == null || datebox.getValue().equals("")) {
						tab_ruleDesign.setSelected(true);
						throw new WrongValueException(datebox, Labels.getLabel("Label_RuleDialog_select_date"));
					}
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
					String date = formatter.format(datebox.getValue());
					strWhereSqlRule = strWhereSqlRule + " " + "'" + date + "' )";
					actualBlob = actualBlob + date + "|";
					comboCount++;
				} else if (childComp instanceof Decimalbox) {
					// Component related to decimalBox
					
					Decimalbox decimalbox = (Decimalbox) childComp;
					decimalbox.setErrorMessage("");
					if (decimalbox.getValue() == null) {
						tab_ruleDesign.setSelected(true);
						throw new WrongValueException(decimalbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
					}
					strWhereSqlRule = strWhereSqlRule + " "+ decimalbox.getValue().toString()+" )";
					actualBlob = actualBlob + decimalbox.getValue().toString()+ "|";
					comboCount++;
				} else if (childComp instanceof Intbox) {
					// Component related to intBox
					
					Intbox intbox = (Intbox) childComp;
					intbox.setErrorMessage("");
					if (intbox.intValue() == 0) {
						tab_ruleDesign.setSelected(true);
						throw new WrongValueException(intbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
					}
					strWhereSqlRule = strWhereSqlRule + " "+ intbox.getValue().toString()+" )";
					actualBlob = actualBlob + intbox.getValue().toString()+ "|";
					comboCount++;
				} else if (childComp instanceof Longbox) {
					// Component related to longBox
					
					Longbox longbox = (Longbox) childComp;
					longbox.setErrorMessage("");
					if (longbox.longValue() == 0) {
						tab_ruleDesign.setSelected(true);
						throw new WrongValueException(longbox, Labels.getLabel("Label_RuleDialog_EnterValue"));
					}
					strWhereSqlRule = strWhereSqlRule + " "+ longbox.getValue().toString()+" )";
					actualBlob = actualBlob + longbox.getValue().toString()+ "|";
					comboCount++;
				} else {
					// repeating the logic for getting values from components
					doBuildRule((Component) childComp);
				}
			}
		}
		
		// Generating Structure for build Condition
		 if (selectionComp instanceof Treeitem) {
			 if(returnCond){
				 if(!returnValue.equals("")){
					 
					 if(selectionComp.getNextSibling() != null){
						 if(!andCond){
							 actualBlob = actualBlob + ") |";
							 andCond = false;
							 strWhereSqlRule = strWhereSqlRule +"){\n\tResult ="+ returnValue.trim() +";\n\treturn;\n}";
							 strWhereSqlRule = strWhereSqlRule + "else if(";
							 returnValue = "";
							 returnCond =false;
							 actualBlob = actualBlob + "( |";
						 }
					 }else{
						 if(elsCond){
							 strWhereSqlRule = strWhereSqlRule + "\n\tResult ="+ returnValue.trim() +";\n\treturn;\n}";
							 elsCond = false;
						 }else{
							 strWhereSqlRule = strWhereSqlRule + "){\n\tResult ="+ returnValue.trim() +";\n\treturn;\n}";
						 }
						 returnValue = "";
						 returnCond =false;
						 actualBlob = actualBlob +") |";
						 andCond = false;
					 }
				 }else{
					 strWhereSqlRule = strWhereSqlRule +  " \n}";
				 }
				
			 }else if(selectionComp.getNextSibling() != null && !andCond){
				 strWhereSqlRule = strWhereSqlRule + "else if(";
				 actualBlob = actualBlob + ") | ( |";
				 
			 }else{
				 if(((Treerow)selectionComp.getChildren().get(0)).getChildren().size() == 0){
					 strWhereSqlRule = strWhereSqlRule + "\n}";	
				 }else{
					 if(selectionComp.getNextSibling() != null){
						 if(((Treerow)selectionComp.getNextSibling().getChildren().get(0)).getChildren().size() == 0 && !andCond){
							 actualBlob = actualBlob +"}" + "|";
						 }
					 }else{
						 actualBlob = actualBlob +"}" + "|";
					 }
				 }
			 }
			 if(andCond){
				 actualBlob = actualBlob +") |";
				 andCond = false;
			 }
		 }
		logger.debug("Leaving");
	}
	
	/**
	 * Method for handling ComboBox selection
	 */
	public void onOperatorSelected(Event event) {
		logger.debug("Entering" +event.toString());
		Component component = (Component) event.getData();
		Combobox selectionCombo = (Combobox) component.getPreviousSibling().getPreviousSibling();
		String selectiontype = selectionCombo.getSelectedItem().getValue().toString();
		if(selectiontype.equalsIgnoreCase("dbvalue")){
			Textbox textComp = (Textbox) component.getNextSibling().getNextSibling().getNextSibling();
			textComp.setValue("");
		}else if(selectiontype.equalsIgnoreCase("static")){
			String oprLabel = ((Combobox)component).getSelectedItem().getValue().toString();
			if(oprLabel.equalsIgnoreCase("IN")
					|| oprLabel.equalsIgnoreCase("NOT IN")){
				Textbox staticText = (Textbox) component.getNextSibling().getNextSibling().getNextSibling();
				staticText.setMaxlength(100);
			}
		}
		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Method for handling ComboBox selection
	 */
	public void onFieldComboSelected(Event event) {
		logger.debug("Entering" +event.toString());
		
		Combobox component = (Combobox) event.getData();
		Combobox selctcombo = (Combobox) component.getNextSibling().getNextSibling();
		selctcombo.getItems().clear();
		selctcombo.setValue("");
		Combobox optrCombo = (Combobox) selctcombo.getNextSibling().getNextSibling();
		optrCombo.getItems().clear();
		optrCombo.setValue("");
		Button valueButton = (Button) optrCombo.getNextSibling().getNextSibling();
		valueButton.setDisabled(true);
		if(selectionTypeMap.containsKey(component.getSelectedItem().getValue().toString())){
			String str = selectionTypeMap.get(component.getSelectedItem().getValue().toString());
			getSelectionTypeItem(str,selctcombo);
		}
		if (fldComboSelAtmpts > 1) {
			Component selectCmp = null;
			if(selctcombo.getParent().getLastChild() instanceof Textbox){
				selectCmp = selctcombo.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling()
								.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
			}else if(component.getParent().getLastChild() instanceof Space){
				selectCmp = selctcombo.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling()
								.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
			}
			if ((selectCmp instanceof Textbox)
					|| (selectCmp instanceof Combobox)
					|| (selectCmp instanceof Datebox)
					|| (selectCmp instanceof Decimalbox)
					|| (selectCmp instanceof Longbox)
					|| (selectCmp instanceof Intbox)) {
				selctcombo.getParent().removeChild(selectCmp);
				fldComboSelAtmpts = 1;
			}
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Method for handling ComboBox selection
	 */
	public void onComboSelected(Event event) {
		logger.debug("Entering" +event.toString());
		Component component = (Component) event.getData();
		internalComboFill(component);
		logger.debug("Leaving" +event.toString());
	}

	//Method for Enable or disable Components Depend on selection type
	private void internalComboFill(Component component){
		logger.debug("Entering");
		Component posCmp = null ;
		if(component.getParent().getLastChild() instanceof Textbox){
			posCmp = component.getParent().getLastChild()
						.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling()
						.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
		}else if(component.getParent().getLastChild() instanceof Space){
			posCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling()
							.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
		}
		if(posCmp.getPreviousSibling() instanceof Button){
			((Button)posCmp.getPreviousSibling()).setDisabled(true);
		}else if(posCmp.getPreviousSibling().getPreviousSibling() instanceof Button){
			((Button)posCmp.getPreviousSibling().getPreviousSibling()).setDisabled(true);
		}

		// If field type is already selected then remove the exited component
		// related to field type
		if (fldComboSelAtmpts > 1) {
			Component selectCmp = null;
			if(component.getParent().getLastChild() instanceof Textbox){
				selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling()
								.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
			}else if(component.getParent().getLastChild() instanceof Space){
				selectCmp = component.getParent().getLastChild().getPreviousSibling().getPreviousSibling().getPreviousSibling()
								.getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling().getPreviousSibling();
			}
			if ((selectCmp instanceof Textbox)
					|| (selectCmp instanceof Combobox)
					|| (selectCmp instanceof Datebox)
					|| (selectCmp instanceof Decimalbox)
					|| (selectCmp instanceof Longbox)
					|| (selectCmp instanceof Intbox)) {
				component.getParent().removeChild(selectCmp);
				fldComboSelAtmpts = 1;
			}
		}
		Component preComp = component.getPreviousSibling().getPreviousSibling();
		if (((Combobox) preComp).getSelectedItem() == null || 
				((Combobox) preComp).getSelectedItem().getValue().equals("#")) {
			((Combobox)component).setValue("");
			tab_ruleDesign.setSelected(true);
			throw new WrongValueException(preComp,
					"please select fieldname before selection type");
		} else {
			// Set the field type of selected field
			for (int j = 0; j < objectFieldList.size(); j++) {
				BMTRBFldDetails fldDetails = objectFieldList.get(j);
				String str = ((Combobox) preComp).getSelectedItem().getValue().toString();
				if (fldDetails.getRbFldName().equals(str)) {
					setFieldType(fldDetails.getRbFldType());
					setFieldLength(fldDetails.getRbFldLen());
				}
			}
		}
		
		String selectionType = ((Combobox) component).getSelectedItem().getValue().toString();
		
		if(selectionType.equalsIgnoreCase("static")){
			String fieldType = fieldTypeMap.get(((Combobox) preComp).getSelectedItem().getValue().toString());
			if (fieldType.equalsIgnoreCase("nvarchar")) {
				Textbox textbox = getSelectText();
				textbox.setMaxlength(getFieldLength());
				component.getParent().insertBefore(textbox, posCmp);
			}else if (fieldType.equalsIgnoreCase("nchar")) {
				if(getFieldLength() == 1){
					component.getParent().insertBefore(getBooleanVariables(), posCmp);
				}else{
					Textbox textbox = getSelectText();
					textbox.setMaxlength(getFieldLength());
					component.getParent().insertBefore(textbox, posCmp);
				}
			}else if (fieldType.equalsIgnoreCase("bigint")) {
				Intbox intbox  = getIntBox();
				intbox.setMaxlength(getFieldLength());
				component.getParent().insertBefore(intbox, posCmp);
			}else if (fieldType.equalsIgnoreCase("decimal")) {
				Decimalbox decimalbox = getDecimalbox();
				decimalbox.setMaxlength(getFieldLength());
				component.getParent().insertBefore(decimalbox, posCmp);
			}else if (fieldType.equalsIgnoreCase("smalldatetime")) {
				component.getParent().insertBefore(getDateBox(), posCmp);
			}
		}else if(selectionType.equalsIgnoreCase("global")){
			
			component.getParent().insertBefore(getGlobalVariables(), posCmp);
		}else if(selectionType.equalsIgnoreCase("dbvalue")){
			
			Textbox textbox = getSelectText();
			((Button)posCmp.getPreviousSibling()).setDisabled(false);
			component.getParent().insertBefore(textbox, posCmp);
		}else if(selectionType.equalsIgnoreCase("calvalue")){
			
			Textbox textbox = getSelectText();
			((Button)posCmp.getPreviousSibling()).setDisabled(false);
			component.getParent().insertBefore(textbox, posCmp);
		}
		
		Combobox optrCombo = (Combobox) component.getNextSibling().getNextSibling();
		optrCombo.getItems().clear();
		optrCombo.setValue("");
		getCriteria(fieldType,selectionType, optrCombo);
		
		fldComboSelAtmpts++;
		logger.debug("Leaving");
	}

	/**
	 * Methods for returning field component types
	 * @return
	 */
	private Decimalbox getDecimalbox() {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setStyle("padding-left: 5px;padding-right: 5px;");
		decimalbox.setWidth("100px");
		return decimalbox;	
	}
	private Intbox getIntBox() {
		Intbox intbox = new Intbox(0);
		intbox.setStyle("padding-left: 5px;padding-right: 5px;");
		intbox.setWidth("100px");
		return intbox;	
	}

	private Datebox getDateBox() {
		Datebox date = new Datebox();
		date.setStyle("padding-left: 5px;padding-right: 5px;");
		date.setWidth("100px");
		date.setFormat(PennantConstants.DBDateTimeFormat);
		return date;	
	}

	/**
	 * This method for setting the rule module ComboBox items 
	 */
	private void setRuleModuleCombo(){	
		logger.debug("Entering");
		Comboitem item;
		item = new Comboitem();
		item.setLabel("--select--");
		item.setValue("");
		moduleList.add(new ValueLabel("","--select--"));
		this.ruleModule.appendChild(item);
		this.ruleModule.setSelectedItem(item);

		for (int i = 0; i < ruleModuleList.size(); i++) {			
			item = new Comboitem();
			item.setLabel(ruleModuleList.get(i).getRbmFldName());
			item.setValue(ruleModuleList.get(i).getRbmModule());
			this.ruleModule.appendChild(item);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * This method for setting the rule module ComboBox items 
	 */
	private void setWaiverDeciderCombo(){	
		logger.debug("Entering");
		Comboitem item;
		item = new Comboitem();
		item.setLabel("--select--");
		item.setValue("");
		this.waiverDecider.appendChild(item);
		this.waiverDecider.setSelectedItem(item);
		List<ValueLabel> waiverDeciderList = PennantStaticListUtil.getWaiverDecider();

		for (int i = 0; i < waiverDeciderList.size(); i++) {			
			item = new Comboitem();
			item.setLabel(waiverDeciderList.get(i).getLabel());
			item.setValue(waiverDeciderList.get(i).getValue());
			this.waiverDecider.appendChild(item);
		}
		logger.debug("Leaving");
	}
	
	public void onChange$waiverDecider(Event event){
		logger.debug("Entering" + event.toString());
		waiverSelection();
		logger.debug("Leaving" + event.toString());		
	}
	
	private void waiverSelection(){
		if(this.waiverDecider.getSelectedItem().getValue().toString().equals("F")){
			this.row_waiver.setVisible(true);
		}else{
			this.row_waiver.setVisible(false);
			this.waiver.setChecked(false);
			onWaiverChecked();
		}
	}
	
	private void setReturnTypeCombo(){	
		logger.debug("Entering");
		Comboitem item;
		item = new Comboitem();
		item.setLabel("--select--");
		item.setValue(PennantConstants.List_Select);
		this.returnType.appendChild(item);
		this.returnType.setSelectedItem(item);
		List<ValueLabel> returnTypeList = PennantStaticListUtil.getRuleReturnType();

		for (int i = 0; i < returnTypeList.size(); i++) {			
			item = new Comboitem();
			item.setLabel(returnTypeList.get(i).getLabel());
			item.setValue(returnTypeList.get(i).getValue());
			this.returnType.appendChild(item);
			
			if("SCORES".equals(this.ruleModuleID) && "I".equals(returnTypeList.get(i).getValue())){
				this.returnType.setSelectedItem(item);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for creating list of Logical Operators
	 */
	private Combobox getCriteria(String fieldType,String selectionType, Combobox combobox) {
		logger.debug("Entering");
		combobox.setWidth("100px");
		Comboitem item;
		String[] operatorLabels = {};
		String[] operatorValues = {};
		if((fieldType != null && !fieldType.equals("")) && (selectionType != null && !selectionType.equals(""))){
			
			for (int i = 0; i < operatorsList.size(); i++) {
				BMTRBFldCriterias criterias = operatorsList.get(i);
				if(criterias.getRbFldType().equalsIgnoreCase(fieldType) && criterias.getRbSTFld().equalsIgnoreCase(selectionType)){
					operatorLabels  = criterias.getRbFldCriteriaNames().split(",");
					operatorValues  = criterias.getRbFldCriteriaValues().split(",");
				}
			}
		}
		for (int i = 0; i < operatorLabels.length; i++) {
			item = new Comboitem();
			item.setLabel(operatorLabels[i]);
			item.setValue(operatorValues[i]);
			combobox.appendChild(item);
		}
		combobox.addForward("onChange", window_RuleDialog, "onOperatorSelected", combobox);
		logger.debug("Leaving");
		return combobox;
	}
	/**
	 * Method for generating List of Fields
	 * @return
	 */
	private Combobox getFields() {
		logger.debug("Entering");
		
		Combobox comboBox = new Combobox();
		Comboitem item;
		item = new Comboitem();
		item.setLabel("--select--");
		item.setValue("#");
		comboBox.appendChild(item);
		fieldObjectMap = new HashMap<String, Object>();
		for (int i = 0; i < objectFieldList.size(); i++) {
			
			BMTRBFldDetails fldDetails = (BMTRBFldDetails) objectFieldList.get(i);
			fieldObjectMap.put(fldDetails.getRbFldName(), fldDetails);
			item = new Comboitem();
			if(mapCount  == 1){
				fieldTypeMap.put(fldDetails.getRbFldName(), fldDetails.getRbFldType());
				selectionTypeMap.put(fldDetails.getRbFldName(), fldDetails.getRbSTFlds());
				dbTableMap.put(fldDetails.getRbFldName(), fldDetails.getRbFldTableName());
			}
			if(fldDetails.isRbForBldFlds()){
				item.setLabel(fldDetails.getRbFldName()+" - "+fldDetails.getRbFldDesc());
				item.setValue(fldDetails.getRbFldName());
				item.setTooltiptext("Description :"+fldDetails.getRbFldDesc()+" \n\n Data Type :"+fldDetails.getRbFldType());
				comboBox.appendChild(item);
			}
		}
		if(fieldTypeMap.size() > 0 && selectionTypeMap.size() > 0){
			mapCount++;
		}
		comboBox.addForward("onChange", window_RuleDialog, "onFieldComboSelected", comboBox);
		comboBox.setWidth("100px");
		logger.debug("Leaving");
		return comboBox;
	}
	
	/**
	 * Method for generating list of selection field Type
	 */
	private Combobox getSelectionTypeItem(String selectionTypes,Combobox combo) {
		logger.debug("Entering");
		Comboitem item;
		String[] selectionTypeArray = {};
		if(selectionTypes != null && !selectionTypes.equals("")){
			selectionTypeArray = selectionTypes.split(",");
		}
		for (int i = 0; i < selectionTypeArray.length; i++) {
			item = new Comboitem();
			item.setLabel(selectionTypeArray[i]);
			item.setValue(selectionTypeArray[i]);
			combo.appendChild(item);
		}
		combo.addForward("onChange", window_RuleDialog, "onComboSelected", combo);
		combo.setWidth("100px");
		logger.debug("Leaving");
		return combo;
	}
	/**
	 * Method for creating list of Criteria Conditions 
	 */
	private Combobox getComboCondition() {
		logger.debug("Entering");
		Combobox comboBox = new Combobox();
		Comboitem item;
		for (int i = 0; i < BuilderUtilListbox.getLogicModelLabel().getSize(); i++) {
			item = new Comboitem();
			item.setLabel(BuilderUtilListbox.getLogicModelLabel().getElementAt(i).toString());
			item.setValue(BuilderUtilListbox.getLogicModelValue().getElementAt(i).toString());
			comboBox.appendChild(item);
		}
		comboBox.setWidth("50px");
		logger.debug("Leaving");
		return comboBox;
	}

	/**
	 * Method for generating new textBox
	 */
	private Textbox getSelectText() {
		Textbox text = new Textbox();
		text.setWidth("98px");
		text.setStyle("padding-left: 5px;padding-right: 5px;");
		return text;
	}

	/**
	 * Method for generating Global Variable List
	 */
	private Combobox getGlobalVariables() {
		logger.debug("Entering");
		Combobox comboBox = new Combobox();
		comboBox.setStyle("padding-left: 5px;padding-right: 5px;");
		Comboitem item;

		for (int i = 0; i < globalVariableList.size(); i++) {
			GlobalVariable globalVariable = (GlobalVariable) globalVariableList.get(i);
			item = new Comboitem();
			item.setLabel(globalVariable.getVarName());
			item.setValue(globalVariable.getVarName());
			comboBox.appendChild(item);
		}
		comboBox.setWidth("100px");
		comboBox.setReadonly(true);
		logger.debug("Leaving");
		return comboBox;
	}
	
	private Combobox getBooleanVariables() {
		logger.debug("Entering");
		Combobox comboBox = new Combobox();
		comboBox.setStyle("padding-left: 5px;padding-right: 5px;");
		Comboitem item;

		for (int i = 0; i < BuilderUtilListbox.getBooleanOperators().getSize(); i++) {
			item = new Comboitem();
			item.setLabel(BuilderUtilListbox.getBooleanOperators().getElementAt(i).toString());
			item.setValue(BuilderUtilListbox.getBooleanOperators().getElementAt(i).toString());
			comboBox.appendChild(item);
		}
		comboBox.setWidth("100px");
		comboBox.setReadonly(true);
		logger.debug("Leaving");
		return comboBox;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	/**
	 * Deletes a Rule object from database.<br>
	 * @throws Exception 
	 */
	private void doDelete() throws Exception {
		logger.debug("Entering");
		final Rule aRule = new Rule();
		BeanUtils.copyProperties(getRule(), aRule);
		String tranType = PennantConstants.TRAN_WF;

		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
									+ "\n\n --> " + aRule.getRuleCode();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(aRule.getRecordType()).equals("")) {
				aRule.setVersion(aRule.getVersion() + 1);
				aRule.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aRule.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				if (doProcess(aRule, tranType)) {
					refreshList();
					closeDialog(window_RuleDialog, "RuleDialog");
				}
			} catch (DataAccessException e) {
				logger.error(e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new Rule object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old vars
		doStoreInitValues();
		
		// we don't create a new Rule() in the frontEnd.
		// we get it from the backEnd.
		final Rule aRule = getRuleService().getNewRule();
		aRule.setNewRecord(true);
		setRule(aRule);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();
		// setFocus
		this.ruleCode.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		if (getRule().isNewRecord()) {
			this.ruleCode.setReadonly(false);
			this.btnCancel.setVisible(false);
			readOnlyComponent(false, this.ruleModule);
			this.tab_ruleDesign.setVisible(true);
			this.btnReadValues.setVisible(true);
			this.waiverDecider.setDisabled(isReadOnly("RuleDialog_waiverDecider"));
		} else {
			this.ruleCode.setReadonly(true);
			this.btnCancel.setVisible(true);
			readOnlyComponent(true, this.ruleModule);
			
			if(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Maintain") &&
					getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Save")){
				this.btnReadValues.setVisible(true);
				this.tab_ruleDesign.setVisible(true);
			}else{
				this.btnReadValues.setVisible(false);
				this.tab_ruleDesign.setVisible(false);
			}
			
			if(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Deletion") && 
					getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Delete")){
				this.btnDelete.setVisible(true);
			}else{
				this.btnDelete.setVisible(false);
			}
			if(getRule().getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
				this.tab_ruleDesign.setVisible(false);
				this.btnReadValues.setVisible(false);
				if(!getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Maintain")){
					this.btnSave.setVisible(true);
					if(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Deletion")){
						this.btnDelete.setVisible(false);
					}
				}else{
					this.btnSave.setVisible(false);
					this.btnDelete.setVisible(false);
					if(getUserWorkspace().isAllowed("button_RuleDialog_btn"+this.ruleModuleName+"Deletion")){
						this.btnSave.setVisible(true);
					}
				}
			}
		}
		
		//readOnlyComponent(true, this.ruleModule);
		//this.ruleModule.setReadonly(true);
		//this.ruleCodeDesc.setReadonly(isReadOnly("RuleDialog_ruleCodeDesc"));
		readOnlyComponent(isReadOnly("RuleDialog_ruleCodeDesc"),this.ruleCodeDesc);
		this.sqlRule.setReadonly(true);
		this.waiverDecider.setDisabled(true);
		this.waiver.setDisabled(isReadOnly("RuleDialog_waiver"));
		this.waiverPercentage.setDisabled(isReadOnly("RuleDialog_waiverPercentage"));
		this.addFeeCharges.setDisabled(isReadOnly("RuleDialog_addFeeCharges"));
		this.seqOrder.setReadonly(isReadOnly("RuleDialog_seqOrder"));
		readOnlyComponent(isReadOnly("RuleDialog_returnType"), this.returnType);
		//this.returnType.setDisabled(isReadOnly("RuleDialog_returnType"));
		this.groupId.setReadonly(isReadOnly("RuleDialog_groupId"));
		this.btnSearchGroupId.setDisabled(isReadOnly("RuleDialog_groupId"));
		
		if("FEES".equals(getRule().getRuleModule()) && "ADDDBS".equals(getRule().getRuleEvent())){
			this.row_feeCharge.setVisible(true);
		}else if("SCORES".equals(getRule().getRuleModule())){
			this.row_feeCharge.setVisible(true);
			this.label_RuleDialog_AddFeeCharges.setVisible(false);
			this.hbox_AddFeeCharges.setVisible(false);
			if("FSCORE".equals(getRule().getRuleEvent())){
				this.label_RuleDialog_GroupId.setVisible(true);
				this.hbox_groupId.setVisible(true);
			}
		}
		
		if(!getRule().isNewRecord() && "SCORES".equals(this.ruleModuleID)){
			this.returnType.setDisabled(true);
		}
		
		if(this.tab_ruleDesign.isVisible()){
			this.tab_ruleDesign.setSelected(true);
		}else{
			this.tab_textRule.setSelected(true);
		}
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.rule.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.ruleCode.setReadonly(true);
		this.ruleCodeDesc.setReadonly(true);
		readOnlyComponent(true, this.ruleModule);
		this.ruleEvent.setReadonly(true);
		this.sqlRule.setReadonly(true);
		this.tab_ruleDesign.setVisible(false);
		this.tab_textRule.setSelected(true);
		this.waiver.setDisabled(true);
		this.waiverPercentage.setDisabled(true);

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
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before
		doRemoveValidation();

		this.ruleCode.setValue("");
		this.ruleCodeDesc.setValue("");
		this.ruleModule.setSelectedIndex(0);
		this.sqlRule.setValue("");
		this.waiver.setChecked(false);
		this.waiverPercentage.setText("0");
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * @throws Exception 
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");
		final Rule aRule = new Rule();
		BeanUtils.copyProperties(getRule(), aRule);
		boolean isNew = false;
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		if(this.tab_ruleDesign.isVisible()){
			readButtonClicked();
		}		
		doSetValidation();
		// fill the Rule object with the components data
		doWriteComponentsToBean(aRule);

		// Write the additional validations as per below example
		// get the selected branch object from the listBox
		// Do data level validations here

		isNew = aRule.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aRule.getRecordType()).equals("")) {
				aRule.setVersion(aRule.getVersion() + 1);
				if (isNew) {
					aRule.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aRule.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aRule.setNewRecord(true);
				}
			}
		} else {
			aRule.setVersion(aRule.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			if (doProcess(aRule, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog(this.window_RuleDialog, "RuleDialog");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This Method used for setting all workFlow details from userWorkSpace and
	 * setting audit details to auditHeader
	 * 
	 * @param aRule
	 *            (Rule)
	 * 
	 * @param tranType
	 *            (String)
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	private boolean doProcess(Rule aRule, String tranType) throws Exception {
		logger.debug("Entering");
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aRule.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aRule.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aRule.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			aRule.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aRule.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aRule);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, aRule))) {
					try {
						if (!isNotes_Entered()) {
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode = getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode + ",";
						}
						nextRoleCode = getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aRule.setTaskId(taskId);
			aRule.setNextTaskId(nextTaskId);
			aRule.setRoleCode(getRole());
			aRule.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aRule, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aRule);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aRule, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aRule, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * This Method used for calling the all Database operations from the service
	 * by passing the auditHeader and operationRefs(Method) as String
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * 
	 * @param method
	 *            (String)
	 * 
	 * @return boolean
	 * @throws Exception
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) throws Exception {
		logger.debug("Entering");
		boolean processCompleted=false;
		Rule aRule = (Rule) auditHeader.getAuditDetail().getModelData();
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		try {
			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(
							PennantConstants.TRAN_DEL)) {
						auditHeader = getRuleService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getRuleService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getRuleService().doApprove(auditHeader);

						if (aRule.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method)
							.equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getRuleService().doReject(auditHeader);

						if (aRule.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(
								PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RuleDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RuleDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
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
			logger.error(e);
			e.printStackTrace();
		}
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * This Method/Event for setting the rule modules and it clears tree if we
	 * select another module and it also prompt the message
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onChange$ruleModule(Event event) throws Exception {		
		logger.debug("Entering" +event.toString());	
		
		this.sqlRule.setValue("");
		this.tab_ruleDesign.setSelected(true);
		this.row_waiver.setVisible(false);
		this.row_waiverDecider.setVisible(false);	
		this.waiverDecider.setSelectedIndex(0);
		this.returnType.setSelectedIndex(0);
		this.waiver.setChecked(false);
		this.groupId.setValue(new Long(0));
		this.lovDescGroupName.setValue("");
		onWaiverChecked();
		
		this.row_feeCharge.setVisible(false);
		this.addFeeCharges.setChecked(false);
		this.label_RuleDialog_GroupId.setVisible(false);
		this.hbox_groupId.setVisible(false);
		
		if(ruleModule.getSelectedIndex() == 0){
			this.ruleType.setValue("");
			this.ruleEvent.setValue("");
			tree.getChildren().clear();
		}else{
			
			String module=ruleModuleList.get(ruleModule.getSelectedIndex()-1).getRbmModule();		
			this.ruleEvent.setValue(ruleModuleList.get(ruleModule.getSelectedIndex()-1).getRbmEvent());
			this.ruleType.setValue(ruleModuleList.get(ruleModule.getSelectedIndex()-1).getRbmFldType());
			
			if("FEES".equals(module) && "ADDDBS".equals(this.ruleEvent.getValue())){
				this.row_feeCharge.setVisible(true);
			} else if("SCORES".equals(getRule().getRuleModule())){
				this.row_feeCharge.setVisible(true);
				this.label_RuleDialog_AddFeeCharges.setVisible(false);
				this.hbox_AddFeeCharges.setVisible(false);
				if("FSCORE".equals(this.ruleEvent.getValue())){
					this.label_RuleDialog_GroupId.setVisible(true);
					this.hbox_groupId.setVisible(true);
				}
			}
			
			if(module.equalsIgnoreCase("FEES")){
				this.row_waiverDecider.setVisible(true);		
				if(!this.ruleEvent.getValue().equalsIgnoreCase("EARLYPAY")){
					this.waiverDecider.setSelectedIndex(1);
					this.row_waiver.setVisible(true);
					this.waiverDecider.setDisabled(true);
				}else{
					this.row_waiverDecider.setVisible(true);
					this.waiverDecider.setSelectedIndex(0);
					this.row_waiver.setVisible(false);
					this.waiverDecider.setDisabled(false);
				}
			}
			if(treechildren != null){
				Component comp=(Component)combo.getParent().getFirstChild().getNextSibling().getNextSibling().getNextSibling().getNextSibling().getNextSibling();
				Combobox cb =new Combobox();
				Textbox tb = new Textbox();
				if(comp instanceof Combobox){
					cb= (Combobox)comp;
				}else if(comp instanceof Textbox){
					tb= (Textbox)comp;
				}
				if(!(cb.getValue()).equals("") || !(tb.getValue().equals(""))){
					final String msg = Labels.getLabel("RuleDialog_message_Data_Modified");
					final String title = Labels.getLabel("message.Information");

					MultiLineMessageBox.doSetTemplate();
					int conf = MultiLineMessageBox.show(msg, title,
							MultiLineMessageBox.YES | MultiLineMessageBox.NO,
							MultiLineMessageBox.QUESTION, true);

					if (conf == MultiLineMessageBox.YES) {
						clearAndBuildTree(module,this.ruleEvent.getValue());
						this.sqlRule.setValue("");
						this.oldVar_ruleModuleTemp = this.ruleModule.getSelectedItem().getValue().toString();
					}	
					else if(conf == MultiLineMessageBox.NO){
						if(!StringUtils.trimToEmpty(this.oldVar_ruleModuleTemp).equals("")){
							for (int i = 0; i < moduleList.size(); i++) {
								if(moduleList.get(i).getValue().equals(this.oldVar_ruleModuleTemp)){									
									ruleModule.setSelectedIndex(i);					
									break;
								}
							}
						}		
					}
				}
				else{
					clearAndBuildTree(module,this.ruleEvent.getValue()); 
				}
			}
			else{
				clearAndBuildTree(module,this.ruleEvent.getValue());
			}
		}

		logger.debug("Leaving" +event.toString());
	}
	
	/**
	 * Method for Checking Event fir Waiver only in Fee Module
	 * @param event
	 */
	public void onCheck$waiver(Event event){
		logger.debug("Entering" + event.toString());
		onWaiverChecked();
		logger.debug("Leaving" + event.toString());
	}
	
	private void onWaiverChecked(){
		if(this.waiver.isChecked()){
			this.space_waiver.setStyle("background-color:red;");
			this.waiverPercentage.setDisabled(false);
		}else{
			this.space_waiver.setStyle("background-color:white;");
			this.waiverPercentage.setText("0");
			this.waiverPercentage.setDisabled(true);
		}
	}

	/**
	 * This method for clearing tree children and build a new tree
	 * @throws Exception
	 */
	public void clearAndBuildTree(String module,String ruleEvent) throws Exception{
		logger.debug("Entering");
		itemCount = 1;
		tree.getChildren().clear();
		objectFieldList = new ArrayList<BMTRBFldDetails>();
		mapCount = 1;
		objectFieldList = getRuleService().getFieldList(module,ruleEvent);
		buildingTree(sqlRuleValueList,this.rule);// designing tree
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Simulation of builded code
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSimulation(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		
		if(!StringUtils.trimToEmpty(this.sqlRule.getValue()).equals("")){
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("fieldObjectMap", fieldObjectMap);
			map.put("ruleDialogCtrl", this);
			map.put("varList", globalVariableList);

			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleResultSimulation.zul", 
					window_RuleDialog,map);
		}else {
			PTMessageUtils.showErrorMessage("Please Build the Rule before Simulation");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Fetch Group Details List for Scoring Module to assign rule
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSearchGroupId(Event event) throws Exception{
		logger.debug("Entering" + event.toString());
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("CategoryType", "F", Filter.OP_EQUAL);
		
		Object dataObject = ExtendedSearchListBox.show(
				this.window_RuleDialog, "CorpScoreGroupDetail", filters);
		if (dataObject instanceof String) {
			this.groupId.setValue(new Long(0));
			this.lovDescGroupName.setValue("");
		} else {
			CorpScoreGroupDetail details = (CorpScoreGroupDetail) dataObject;
			if (details != null) {
				this.groupId.setValue(details.getGroupId());
				this.lovDescGroupName.setValue(details.getGroupId() + "-"
						+ details.getGroupDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Get Audit Header Details
	 * 
	 * @param aRule (Rule)
	 * @param tranType (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(Rule aRule, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aRule.getBefImage(), aRule);
		return new AuditHeader(String.valueOf(aRule.getId()), null, null,
				null, auditDetail, aRule.getUserDetails(), getOverideMap());
	}
	
	//Method for Show Error Message
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_RuleDialog,
					auditHeader);
		} catch (Exception exp) {
			logger.error(e);
		}
	}

	/**
	 * Get the window for entering Notes
	 * 
	 * @param event (Event)
	 * 
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

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
		logger.debug("Leaving" + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}

	// Method for refreshing the list after successful updating
	private void refreshList() {
		logger.debug("Entering");
		if (getRuleListCtrl()!=null) {
			final JdbcSearchObject<Rule> soRule = getRuleListCtrl().getSearchObj();
			getRuleListCtrl().pagingRuleList.setActivePage(0);
			getRuleListCtrl().getPagedListWrapper().setSearchObject(soRule);
			if (getRuleListCtrl().listBoxRule != null) {
				getRuleListCtrl().listBoxRule.getListModel();
			}
		}
		logger.debug("Leaving");
	}

	// Get the notes entered for rejected reason
	private Notes getNotes() {
		logger.debug("Entering");
		Notes notes = new Notes();
		notes.setModuleName("Rule");
		notes.setReference(String.valueOf(getRule().getId()));
		notes.setVersion(getRule().getVersion());
		logger.debug("Leaving");
		return notes;
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

	public Rule getRule() {
		return this.rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	public RuleService getRuleService() {
		return this.ruleService;
	}

	public void setRuleListCtrl(RuleListCtrl ruleListCtrl) {
		this.ruleListCtrl = ruleListCtrl;
	}
	public RuleListCtrl getRuleListCtrl() {
		return this.ruleListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	public int getFieldLength() {
		return fieldLength;
	}

}