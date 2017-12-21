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

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.service.rulefactory.impl.LimitRuleService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.RuleConstants;
import com.pennant.webui.rulefactory.rule.model.LimitRuleListModelItemRendrer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/RuleFactory/Rule/RuleList.zul
 * file.
 */
public class LimitRuleListCtrl extends GFCBaseListCtrl<LimitFilterQuery>  {
	private static final long serialVersionUID = -6345351842301484405L;
	private static final Logger logger = Logger.getLogger(LimitRuleListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	
	protected Window 		window_LimitRuleList; 			     // autoWired
	protected Borderlayout 	borderLayout_DedupParmList; 	     // autoWired
	protected Paging 		pagingDedupParmList; 			     // autoWired
	protected Listbox 		listBoxDedupParm; 				     // autoWired
	protected Textbox 		queryModule;						 // autoWired
	

	// List headers
	protected Listheader listheader_QueryCode; 			         // autoWired
	protected Listheader listheader_QueryDesc; 			         // autoWired
	protected Listheader listheader_Active;
	
	
	//Search
	protected Listbox sortOperator_queryCode; 					// autoWired
	protected Textbox queryDesc; 				// autoWired
	protected Listbox sortOperator_queryDesc; 	// autoWired
	protected Textbox queryModules; 			// autoWired
	protected Textbox queryCode; 				// autoWired
	protected Listbox sortOperator_queryModule; // autoWired
	protected Textbox sQLQuery; 				// autoWired
	protected Combobox querySubCode;			// autoWired
	protected Listbox sortOperator_querySubCode;// autoWired 
	protected Listbox sortOperator_sQLQuery; 	// autoWired
	protected Listbox 	sortOperator_active;
	protected Checkbox 	active;
	

	// Check Rights
	protected Button btnHelp; 									 // autoWired
	protected Button button_DedupParmList_NewDedupParm; 		 // autoWired
	protected Button button_DedupParmList_DedupParmSearchDialog; // autoWired
	protected Button button_DedupParmList_PrintList; 			 // autoWired

	// NEEDED for the ReUse in the SearchWindow
	protected JdbcSearchObject<LimitFilterQuery> searchObj;
	private transient LimitRuleService limitRuleService;
	private transient WorkFlowDetails workFlowDetails=null;
	
	/**
	 * default constructor.<br>
	 */
	public LimitRuleListCtrl() {
		super();
	}
	
	@Override
	protected void doSetProperties() {
		super.moduleCode = "LimitFilterQuery";
		super.pageRightName = "RuleList";
		super.tableName = "LimitParams_AView";
		super.queueTableName = "LimitParams_View";
		super.enquiryTableName = "LimitParams_AView";
	}
	// Component Events

	
	@Override
	protected void doAddFilters() {

		super.doAddFilters();		
		
		searchObject.addFilterEqual("QueryModule",queryModule.getValue());
	
	}
	/**
	 * Before binding the data and calling the List window we check, if the
	 * ZUL-file is called with a parameter for a selected DedupParam object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_LimitRuleList(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_LimitRuleList, borderLayout_DedupParmList, listBoxDedupParm, pagingDedupParmList);
		setItemRender(new LimitRuleListModelItemRendrer());

		// Register buttons and fields.
		registerButton(button_DedupParmList_NewDedupParm, "button_DedupParmList_NewDedupParm", true);
		registerButton(button_DedupParmList_DedupParmSearchDialog);


		registerField("queryCode", listheader_QueryCode, SortOrder.ASC, queryCode,
				sortOperator_queryCode, Operators.STRING);
		registerField("queryDesc", listheader_QueryDesc, SortOrder.ASC, queryDesc,
				sortOperator_queryDesc, Operators.STRING);
		registerField("active", listheader_Active, SortOrder.NONE, active,
				sortOperator_active, Operators.BOOLEAN);
		
		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
		
	}
	
	/**
	 * Invoke Search
	 */
	public void onClick$button_DedupParmList_DedupParmSearchDialog(Event event) throws Exception {
		logger.debug("Entering" +event.toString());
		search();
		logger.debug("Leaving" +event.toString());
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
	 * Call the DedupParm dialog with a new empty entry. <br>
	 */
	public void onClick$button_DedupParmList_NewDedupParm(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		// create a new DedupParm object, We GET it from the backEnd.
		final LimitFilterQuery aDedupParm = limitRuleService.getNewLimitRule();
		aDedupParm.setNewRecord(true);
		aDedupParm.setQuerySubCode(RuleConstants.EVENT_BANK);
		doShowDialogPage(aDedupParm);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * This method is forwarded from the listBoxes item renderer. <br>
	 * see:
	 * com.pennant.webui.dedup.dedupparm.model.DedupParmListModelItemRenderer
	 * .java <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDedupParmItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// Get the selected record.
				Listitem selectedItem = this.listBoxDedupParm.getSelectedItem();

				// Get the selected entity.
				LimitFilterQuery aDedupParm = (LimitFilterQuery) selectedItem.getAttribute("data");
				final LimitFilterQuery dedupParm = limitRuleService.getLimitRuleByID(aDedupParm.getQueryCode(),queryModule.getValue(),RuleConstants.EVENT_BANK);


				if (dedupParm == null) {
					MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
					return;
				}

				// Check whether the user has authority to change/view the record.
				String whereCond = " AND QueryCode='"+ dedupParm.getQueryCode() + 
						"' AND QueryModule='"+queryModule.getValue() + 
						"' AND QuerySubCode='"+ RuleConstants.EVENT_BANK + 
						"' AND version="+ dedupParm.getVersion() + " ";

				if (doCheckAuthority(dedupParm, whereCond)) {
					// Set the latest work-flow id for the new maintenance request.
					if (isWorkFlowEnabled() && dedupParm.getWorkflowId() == 0) {
						dedupParm.setWorkflowId(getWorkFlowId());
					}
					doShowDialogPage(dedupParm);
				} else {
					MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
				}
		
		logger.debug("Leaving" + event.toString());
	}

	

	/**
	 * Opens the detail view. <br>
	 * Overhanded some params in a map if needed. <br>
	 * 
	 * @param DedupParm
	 *            (aDedupParm)
	 * @throws Exception
	 */
	private void doShowDialogPage(LimitFilterQuery dedupParm) throws Exception {
		logger.debug("Entering");
		Map<String, Object> arg = getDefaultArguments();
		

		if (dedupParm.getWorkflowId() == 0 && isWorkFlowEnabled()) {
			dedupParm.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		dedupParm.setQueryModule(this.queryModule.getValue());
		
		
		arg.put("queryModule", this.queryModule.getValue());
		arg.put("LimitParam", dedupParm);	
		arg.put("limitRuleListCtrl", this);
		arg.put("enqiryModule", super.enqiryModule);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents(
					"/WEB-INF/pages/RulesFactory/Rule/LimitRuleDialog.zul", null,arg);
		} catch (final Exception e) {
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
	public void onClick$btnHelp(Event event) {
		doShowHelp(event);
	}

	/**
	 * When user clicks on "fromApproved"
	 * 
	 * @param event
	 */
	public void onCheck$fromApproved(Event event) {
		search();
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}


	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	

	public void setLimitRuleService(LimitRuleService limitRuleService) {
		this.limitRuleService = limitRuleService;
	}
}