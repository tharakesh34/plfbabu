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
 * 03-06-2011       Pennant	                 0.1                                        	* 
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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.webui.rulefactory.rule.model.RuleListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennant.webui.util.PTListReportUtils;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/RuleFactory/Rule/RuleList.zul file.
 */
public class RuleListCtrl extends GFCBaseListCtrl<Rule> {
	private static final long serialVersionUID = -6345351842301484405L;
	private static final Logger logger = Logger.getLogger(RuleListCtrl.class);

	protected Window window_RuleList;
	protected Borderlayout borderLayout_RuleList;
	protected Paging pagingRuleList;
	protected Listbox listBoxRule;
	protected Textbox ruleModule;
	protected Textbox 		limitLine;

	protected Listheader listheader_RuleEvent;
	protected Listheader listheader_RuleCode;
	protected Listheader listheader_RuleCodeDesc;

	protected Button button_RuleList_NewRule;
	protected Button button_RuleList_RuleSearchDialog;
	protected Button btnExport;


	// Filtering Fields for Check List rule
	
	protected Textbox ruleCode;
	protected Textbox ruleCodeDesc;
	protected Uppercasebox ruleEvent;

	protected Listbox sortOperator_ruleCode;
	protected Listbox sortOperator_ruleCodeDesc;
	protected Listbox sortOperator_ruleEvent;


	private transient RuleService ruleService;

	private String ruleModuleName = "Rule";

	/**
	 * default constructor.<br>
	 */
	public RuleListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Rule";
		super.pageRightName = "RuleList";
		super.tableName = "Rules_AView";
		super.queueTableName = "Rules_View";
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		super.searchObject.addFilterEqual("ruleModule", this.ruleModule.getValue());
		if (this.limitLine != null && StringUtils.isNotBlank(this.limitLine.getValue())) {
			super.searchObject.addFilterEqual("ruleEvent", this.limitLine.getValue());

		}
	}

	@Override
	protected void doPrintResults() {
		try {
			
			if (limitLine !=null && !StringUtils.isEmpty(limitLine.getValue())) {
				new PTListReportUtils(StringUtils.trimToEmpty(this.ruleModule.getValue())+StringUtils.trimToEmpty(limitLine.getValue()), super.searchObject, this.pagingRuleList.getTotalSize() + 1);
			}else{
				new PTListReportUtils(this.ruleModuleName, super.searchObject, this.pagingRuleList.getTotalSize() + 1);
			}
			
			
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_RuleList(Event event) {

		if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_ELGRULE)) {
			this.ruleModuleName = "EligibilityRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_FEES)) {
			this.ruleModuleName = "FeeRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_RATERULE)) {
			this.ruleModuleName = "RateRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_SUBHEAD)) {
			this.ruleModuleName = "SubHeadRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_SCORES)) {
			this.ruleModuleName = "ScoreRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_PROVSN)) {
			this.ruleModuleName = "ProvisionRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_REFUND)) {
			this.ruleModuleName = "RefundRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_CLRULE)) {
			this.ruleModuleName = "CheckRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_AGRRULE)) {
			this.ruleModuleName = "AgreementRule";
		} else if (StringUtils.equals(ruleModule.getValue(), RuleConstants.MODULE_DOWNPAYRULE)) {
			this.ruleModuleName = "DownpaymentRule";
		}else if(ruleModule.getValue().equals(RuleConstants.MODULE_LMTLINE)){
			this.ruleModuleName="LimitDefRule";
		}else if(ruleModule.getValue().equals(RuleConstants.MODULE_INSRULE)){
			this.ruleModuleName="InsuranceRule";
		}else if(ruleModule.getValue().equals(RuleConstants.MODULE_BOUNCE)){
			this.ruleModuleName="BOUNCE";
		}

		// Set the page level components.
		setPageComponents(window_RuleList, borderLayout_RuleList, listBoxRule, pagingRuleList);
		setItemRender(new RuleListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_RuleList_NewRule, "button_RuleList_New" + this.ruleModuleName, true);
		registerButton(button_RuleList_RuleSearchDialog);

		registerField("ruleCode", listheader_RuleCode, SortOrder.ASC, ruleCode, sortOperator_ruleCode, Operators.STRING);
		registerField("ruleCodeDesc", listheader_RuleCodeDesc, SortOrder.NONE, ruleCodeDesc, sortOperator_ruleCodeDesc,
				Operators.STRING);
		
		if (this.ruleEvent != null) {
			registerField("ruleEvent", listheader_RuleEvent, SortOrder.NONE, ruleEvent, sortOperator_ruleEvent,
					Operators.STRING);
		} else {
			registerField("ruleEvent");
		}

		// Render the page and display the data.
		doRenderPage();

		search();
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_RuleList_RuleSearchDialog(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_RuleList_NewRule(Event event) {
		logger.debug("Entering");

		String ruleModuleValue = this.ruleModule.getValue();
		// Create a new entity.
		Rule aRule = new Rule();
		aRule.setNewRecord(true);
		aRule.setWorkflowId(getWorkFlowId());
		aRule.setRuleModule(ruleModuleValue);
		aRule.setFixedOrVariableLimit(LimitConstants.LIMIT_RULE_FIXED);
		aRule.setRevolving(true);
		aRule.setActive(true);

		if (StringUtils.equalsIgnoreCase(ruleModuleValue, RuleConstants.MODULE_LMTLINE)) {
			aRule.setRuleEvent(this.limitLine.getValue());
		} else if (StringUtils.equalsIgnoreCase(ruleModuleValue, RuleConstants.MODULE_SCORES)) {
			aRule.setRuleEvent("RSCORE");
		} else if (!StringUtils.equalsIgnoreCase(ruleModuleValue, RuleConstants.MODULE_FEES)) {
			aRule.setRuleEvent(ruleModuleValue);
		}

		// Display the dialog page.
		doShowDialogPage(aRule);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onRuleItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxRule.getSelectedItem();

		// Get the selected entity.
		String ruleCode = (String) selectedItem.getAttribute("ruleCode");
		String ruleModule = this.ruleModule.getValue();
		String ruleEvent = (String) selectedItem.getAttribute("ruleEvent");

		Rule rule = ruleService.getRuleById(ruleCode, ruleModule, ruleEvent);

		if (rule == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND RuleCode='" + rule.getRuleCode() + "' AND version=" + rule.getVersion() + " ";

		if (doCheckAuthority(rule, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && rule.getWorkflowId() == 0) {
				rule.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(rule);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param rule
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Rule rule) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("rule", rule);
		arg.put("ruleListCtrl", this);
		arg.put("ruleModuleName", this.ruleModuleName);
//		arg.put("ruleModule", this.ruleModule.getValue());

		try {
			Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/RuleDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
		doShowHelp(event);
	}


	public void onClick$btnExport(Event event) {
		logger.debug("Entering");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ruleListCtrl", this);
		Executions.createComponents("/WEB-INF/pages/RulesFactory/Rule/ExportRuleList.zul", this.window_RuleList, map);

		logger.debug("Leaving");
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

}