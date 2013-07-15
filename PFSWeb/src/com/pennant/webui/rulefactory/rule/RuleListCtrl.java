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
 * FileName    		:  RuleListCtrl.java                           
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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.FieldComparator;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.ErrorUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.search.Filter;
import com.pennant.webui.rulefactory.rule.model.RuleListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.PTReportUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/RuleFactory/Rule/RuleList.zul
 * file.<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 */
public class RuleListCtrl extends GFCBaseListCtrl<Rule> implements Serializable {

	private static final long serialVersionUID = -6345351842301484405L;
	private final static Logger logger = Logger.getLogger(RuleListCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RuleList; 							// autowired
	protected Borderlayout 	borderLayout_RuleList; 						// autowired
	protected Paging 		pagingRuleList; 							// autowired
	protected Listbox 		listBoxRule; 								// autowired
	protected Textbox 		ruleModule;									// autowired

	// List headers
	protected Listheader listheader_RuleModule; 						// autowired
	protected Listheader listheader_RuleEvent; 							// autowired
	protected Listheader listheader_RuleCode; 							// autowired
	protected Listheader listheader_RuleCodeDesc;						// autowired
	protected Listheader listheader_RecordStatus; 						// autowired
	protected Listheader listheader_RecordType;							// autowired

	// checkRights
	protected Button btnHelp; 											// autowired
	protected Button button_RuleList_NewRule; 							// autowired
	protected Button button_RuleList_RuleSearchDialog; 					// autowired
	protected Button button_RuleList_PrintList; 						// autowired
	protected Button btnExport;											// autowired
	protected boolean isExportRule = false;

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<Rule> searchObj;
	private transient RuleService ruleService;
	private transient WorkFlowDetails workFlowDetails=null;
	private String ruleModuleName="Rule";	
	/**
	 * default constructor.<br>
	 */
	public RuleListCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected Rule object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_RuleList(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		ModuleMapping moduleMapping = PennantJavaUtil.getModuleMap("Rule");
		boolean wfAvailable=true;

		if(ruleModule.getValue().equals("ELGRULE")){
			this.ruleModuleName="EligibilityRule";
		}else if(ruleModule.getValue().equals("FEES")){
			this.ruleModuleName="FeeRule";
		}else if(ruleModule.getValue().equals("SUBHEAD")){
			this.ruleModuleName="SubHeadRule";
		}else if(ruleModule.getValue().equals("SCORES")){
			this.ruleModuleName="ScoreRule";
		}else if(ruleModule.getValue().equals("PROVSN")){
			this.ruleModuleName="ProvisionRule";
		}else if(ruleModule.getValue().equals("REFUND")){
			this.ruleModuleName="RefundRule";
		}else if(ruleModule.getValue().equals("CLRULE")){
			this.ruleModuleName="CheckRule";
		}

		if (moduleMapping.getWorkflowType()!=null){
			workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Rule");

			if (workFlowDetails==null){
				setWorkFlowEnabled(false);
			}else{
				setWorkFlowEnabled(true);
				setFirstTask(getUserWorkspace().isRoleContains(workFlowDetails.getFirstTaskOwner()));
				setWorkFlowId(workFlowDetails.getId());
			}	
		}else{
			wfAvailable=false;
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

		this.borderLayout_RuleList.setHeight(getBorderLayoutHeight());

		// set the paging params
		this.pagingRuleList.setPageSize(getListRows());
		this.pagingRuleList.setDetailed(true);

		this.listheader_RuleModule.setSortAscending(new FieldComparator("ruleModule", true));
		this.listheader_RuleModule.setSortDescending(new FieldComparator("ruleModule", false));
		this.listheader_RuleEvent.setSortAscending(new FieldComparator("ruleEvent", true));
		this.listheader_RuleEvent.setSortDescending(new FieldComparator("ruleEvent", false));
		this.listheader_RuleCode.setSortAscending(new FieldComparator("ruleCode", true));
		this.listheader_RuleCode.setSortDescending(new FieldComparator("ruleCode", false));
		this.listheader_RuleCodeDesc.setSortAscending(new FieldComparator("ruleCodeDesc", true));
		this.listheader_RuleCodeDesc.setSortDescending(new FieldComparator("ruleCodeDesc", false));

		if (isWorkFlowEnabled()){
			this.listheader_RecordStatus.setSortAscending(new FieldComparator("recordStatus", true));
			this.listheader_RecordStatus.setSortDescending(new FieldComparator("recordStatus", false));
			this.listheader_RecordType.setSortAscending(new FieldComparator("recordType", true));
			this.listheader_RecordType.setSortDescending(new FieldComparator("recordType", false));
		}else{
			this.listheader_RecordStatus.setVisible(false);
			this.listheader_RecordType.setVisible(false);
		}

		// ++ create the searchObject and initial sorting ++//
		this.searchObj = new JdbcSearchObject<Rule>(Rule.class,getListRows());
		this.searchObj.addSort("RuleCode", false);

		if(!StringUtils.trimToEmpty(ruleModule.getValue()).equals("")){
			this.searchObj.addFilterEqual("RuleModule", ruleModule.getValue());	
		}

		// WorkFlow
		if (isWorkFlowEnabled()) {
			this.searchObj.addTabelName("Rules_View");
			if (isFirstTask() && getUserWorkspace().isAllowed("button_RuleList_New"+this.ruleModuleName)) {
				button_RuleList_NewRule.setVisible(true);
			} else {
				button_RuleList_NewRule.setVisible(false);
			}
			this.searchObj.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),isFirstTask());
		}else{
			this.searchObj.addTabelName("Rules_AView");
		}

		setSearchObj(this.searchObj);
		if (!isWorkFlowEnabled() && wfAvailable){
			this.button_RuleList_NewRule.setVisible(false);
			this.button_RuleList_RuleSearchDialog.setVisible(false);
			this.button_RuleList_PrintList.setVisible(false);
			PTMessageUtils.showErrorMessage(PennantJavaUtil.getLabel("WORKFLOW CONFIG NOT FOUND"));
		}else{
			// Set the ListModel for the articles.
			getPagedListWrapper().init(this.searchObj,listBoxRule,pagingRuleList);
			this.listBoxRule.setItemRenderer(new RuleListModelItemRenderer());
		}	
		
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * SetVisible for components by checking if there's a right for it.
	 */
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("RuleList");

		this.button_RuleList_NewRule.setVisible(getUserWorkspace().isAllowed("button_RuleList_New"+this.ruleModuleName));
		this.button_RuleList_RuleSearchDialog.setVisible(getUserWorkspace().isAllowed("button_RuleList_"+this.ruleModuleName+"SearchDialog"));
		this.button_RuleList_PrintList.setVisible(getUserWorkspace().isAllowed("button_RuleList_"+this.ruleModuleName +"PrintList"));
		logger.debug("Leaving");
	}

	public void onClick$btnExport(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ruleListCtrl", this);
		Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/ExportRuleList.zul", this.window_RuleList, map);
		logger.debug("Leaving" +event.toString());
	}


	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see: com.pennant.webui.sqlbuilder.model.RuleListModelItemRenderer.java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRuleItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" +event.toString());

		// get the selected Rule object
		final Listitem item = this.listBoxRule.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final Rule aRule = (Rule) item.getAttribute("data");
			final Rule rule = getRuleService().getRuleById(aRule.getRuleCode(),
					aRule.getRuleModule(),aRule.getRuleEvent());
			if(rule == null){
				String[] valueParm = new String[3];
				String[] errorParm = new String[3];

				valueParm[0] = aRule.getRuleCode();
				valueParm[1] = aRule.getRuleModule();
				valueParm[2] = aRule.getRuleEvent();

				errorParm[0] = PennantJavaUtil.getLabel("label_RuleCode")+ ":" + valueParm[0];
				errorParm[1] = PennantJavaUtil.getLabel("label_RuleModule")+ ":" +valueParm[1];
				errorParm[2] = PennantJavaUtil.getLabel("label_RuleMEvent")+ ":" + valueParm[2];

				ErrorDetails errorDetails = ErrorUtil.getErrorDetail(
						new ErrorDetails(PennantConstants.KEY_FIELD, "41005",
								errorParm, valueParm), getUserWorkspace().getUserLanguage());
				PTMessageUtils.showErrorMessage(errorDetails.getErrorMessage());
			}else{
				showDetailView(rule);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Call the Rule dialog with a new empty entry. <br>
	 */
	public void onClick$button_RuleList_NewRule(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		// create a new Rule object, We GET it from the backEnd.
		final Rule aRule = getRuleService().getNewRule();
		aRule.setRuleModule(ruleModule.getValue());
		showDetailView(aRule);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param branche
	 * @throws Exception
	 */
	private void showDetailView(Rule aRule) throws Exception {
		logger.debug("Entering");
		/*
		 * We can call our Dialog ZUL-file with parameters. So we can call them
		 * with a object of the selected item. For handed over these parameter
		 * only a Map is accepted. So we put the object in a HashMap.
		 */

		if(aRule.getWorkflowId()==0 && isWorkFlowEnabled()){
			aRule.setWorkflowId(workFlowDetails.getWorkFlowId());
		}

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rule", aRule);
		map.put("ruleListCtrl", this);
		map.put("ruleModuleName", this.ruleModuleName);
		map.put("ruleModule", this.ruleModule.getValue());

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTMessageUtils.showHelpWindow(event, window_RuleList);
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * Refreshes the view by calling the onCreate event manually.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		this.pagingRuleList.setActivePage(0);
		Events.postEvent("onCreate", this.window_RuleList, event);
		this.window_RuleList.invalidate();
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * Method for calling the Rule Dialog
	 * @param event
	 * @throws Exception
	 */
	public void onClick$button_RuleList_RuleSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		/*
		 * we can call our RuleDialog ZU:-file with parameters. So we can
		 * call them with a object of the selected Rule. For handed over
		 * these parameter only a Map is accepted. So we put the Rule object
		 * in a HashMap.
		 */
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ruleListCtrl", this);
		if(!StringUtils.trimToEmpty(ruleModule.getValue()).equals("")){
			this.searchObj.addFilter(new Filter("ruleModule", this.ruleModule.getValue(), Filter.OP_EQUAL));
		}
		map.put("searchObject", this.searchObj);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleSearchDialog.zul",null,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" +event.toString());
	}

	/**
	 * When the rule print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$button_RuleList_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering" +event.toString());
		PTReportUtils.getReport("Rule", getSearchObj());
		logger.debug("Leaving" +event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}
	public RuleService getRuleService() {
		return this.ruleService;
	}

	public JdbcSearchObject<Rule> getSearchObj() {
		return this.searchObj;
	}
	public void setSearchObj(JdbcSearchObject<Rule> searchObj) {
		this.searchObj = searchObj;
	}

}