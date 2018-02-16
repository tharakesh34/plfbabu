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
 * FileName    		:  FinanceWorkFlowListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  19-11-2011    														*
 *                                                                  						*
 * Modified Date    :  19-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 19-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.lmtmasters.financeworkflow;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Row;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.lmtmasters.financeworkflow.model.FinanceWorkFlowListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceWorkFlow/FinanceWorkFlowList.zul file.
 */
public class FinanceWorkFlowListCtrl extends GFCBaseListCtrl<FinanceWorkFlow> {
	private static final long serialVersionUID = 3938682418831790487L;
	private static final Logger logger = Logger.getLogger(FinanceWorkFlowListCtrl.class);

	protected Window window_FinanceWorkFlowList;
	protected Borderlayout borderLayout_FinanceWorkFlowList;
	protected Paging pagingFinanceWorkFlowList;
	protected Listbox listBoxFinanceWorkFlow;

	protected Listheader listheader_FinType;
	protected Listheader listheader_ScreenCode;
	protected Listheader listheader_FinEvent;
	protected Listheader listheader_WorkFlowType;

	protected Textbox finType;
	protected Listbox sortOperator_finType;
	protected Textbox screenCode;
	protected Listbox sortOperator_screenCode;
	protected Combobox  finEvent;  
	protected Listbox  sortOperator_finEvent;
	protected Row row_finevent;
	protected Textbox workFlowType;
	protected Listbox sortOperator_workFlowType;
	
	protected Label label_FinanceWorkFlowSearch_FinType;

	private transient boolean isPromotion = false;
	private transient boolean isVAS =false;
	private transient boolean isCommitment =false;
	private transient boolean isCollateral =false;
	protected Button button_FinanceWorkFlowList_NewFinanceWorkFlow;
	protected Button button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog;

	private transient FinanceWorkFlowService financeWorkFlowService;

	// Page parameters
	private String module;
	private String eventAction;

	/**
	 * default constructor.<br>
	 */
	public FinanceWorkFlowListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		this.module = getArgument("module");
		this.eventAction = getArgument("eventAction");

		String moduleName = "Finance";
		if (PennantConstants.WORFLOW_MODULE_PROMOTION.equals(this.module)) {
			moduleName = "Promotion";
			this.label_FinanceWorkFlowSearch_FinType.setValue(Labels.getLabel("label_FinanceWorkFlowSearch_PromotionCode.value"));
			this.listheader_FinType.setLabel(Labels.getLabel("listheader_PromotionCode.label"));
			isPromotion = true;
		}else if (PennantConstants.WORFLOW_MODULE_COLLATERAL.equals(this.module)) {
			moduleName = "Collateral";
			this.label_FinanceWorkFlowSearch_FinType.setValue(Labels.getLabel("label_FinanceWorkFlowSearch_CollateralType.value"));
			this.listheader_FinType.setLabel(Labels.getLabel("listheader_CollateralType.label"));
			isCollateral =true;
		}else if (PennantConstants.WORFLOW_MODULE_COMMITMENT.equals(this.module)) {
			moduleName = "Commitment";
			this.label_FinanceWorkFlowSearch_FinType.setValue(Labels.getLabel("label_FinanceWorkFlowSearch_Commitment.value"));
			this.listheader_FinType.setLabel(Labels.getLabel("listheader_Commitment.label"));
			isCommitment=true;
		}else if (PennantConstants.WORFLOW_MODULE_VAS.equals(this.module)) {
			moduleName = "VAS";
			this.label_FinanceWorkFlowSearch_FinType.setValue(Labels.getLabel("label_FinanceWorkFlowSearch_VasProduct.value"));
			this.listheader_FinType.setLabel(Labels.getLabel("listheader_VASProductCode.label"));
			isVAS=true;
		}

		super.moduleCode = moduleName+"WorkFlow";
		super.pageRightName = moduleName+"WorkFlowList";
		
		if(isPromotion){
			super.tableName = "LMTPromotionWorkflowdef_AView";
			super.queueTableName = "LMTPromotionWorkflowdef_View";
		}else{
			super.tableName = "LMTFinanceWorkFlowDef_AView";
			super.queueTableName = "LMTFinanceWorkFlowDef_View";
		}
		
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		this.searchObject.addFilter(new Filter("ModuleName", this.module, Filter.OP_EQUAL));
		if (!StringUtils.equals(PennantConstants.WORFLOW_MODULE_COLLATERAL,this.module) &&
				!StringUtils.equals(PennantConstants.WORFLOW_MODULE_VAS,this.module) &&
				!StringUtils.equals(PennantConstants.WORFLOW_MODULE_COMMITMENT,this.module)) {
			if (isPromotion) {
				this.searchObject.addFilter(new Filter("lovDescProductName", "", Filter.OP_NOT_EQUAL));
			} else {
				this.searchObject.addFilter(new Filter("lovDescProductName", "", Filter.OP_EQUAL));
			}

			if (FinanceConstants.FINSER_EVENT_ORG.equals(eventAction)) {
				this.searchObject.addFilter(new Filter("finEvent", new String[] { FinanceConstants.FINSER_EVENT_ORG,
						FinanceConstants.FINSER_EVENT_PREAPPROVAL }, Filter.OP_IN));
			} else {
				this.searchObject.addFilter(new Filter("finEvent", new String[] { FinanceConstants.FINSER_EVENT_ORG,
						FinanceConstants.FINSER_EVENT_PREAPPROVAL }, Filter.OP_NOT_IN));
			}
		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceWorkFlowList(Event event) {
		// Set the page level components.
		setPageComponents(window_FinanceWorkFlowList, borderLayout_FinanceWorkFlowList, listBoxFinanceWorkFlow,
				pagingFinanceWorkFlowList);

		if (FinanceConstants.FINSER_EVENT_ORG.equals(eventAction)) {
			setItemRender(new FinanceWorkFlowListModelItemRenderer(PennantStaticListUtil.getFinServiceEvents(false)));
		} else {
			setItemRender(new FinanceWorkFlowListModelItemRenderer(PennantStaticListUtil.getFinServiceEvents(true)));
		}

		// Register buttons and fields.
		registerButton(button_FinanceWorkFlowList_NewFinanceWorkFlow, "button_" + super.pageRightName
				+ "_NewFinanceWorkFlow", true);
		registerButton(button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog);

		registerField("finType", listheader_FinType, SortOrder.ASC, finType, sortOperator_finType, Operators.STRING);
		registerField("screenCode", listheader_ScreenCode, SortOrder.NONE, screenCode, sortOperator_screenCode,
				Operators.STRING);
		
		registerField("finEvent", listheader_FinEvent,SortOrder.ASC, finEvent, sortOperator_finEvent,Operators.STRING);
		fillComboBox(finEvent, null, PennantStaticListUtil.getFinServiceEvents(true), "");
		
		registerField("workFlowType", listheader_WorkFlowType, SortOrder.NONE, workFlowType, sortOperator_workFlowType,
				Operators.STRING);
		registerField("ModuleName");

		if (FinanceConstants.FINSER_EVENT_ORG.equals(eventAction)) {
			listheader_FinEvent.setVisible(false);
			this.row_finevent.setVisible(false);
		} else {
			listheader_FinEvent.setVisible(true);
			this.row_finevent.setVisible(true);
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
	public void onClick$button_FinanceWorkFlowList_FinanceWorkFlowSearchDialog(Event event) {
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
	public void onClick$button_FinanceWorkFlowList_NewFinanceWorkFlow(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinanceWorkFlow aFinanceWorkFlow = new FinanceWorkFlow();
		aFinanceWorkFlow.setNewRecord(true);
		aFinanceWorkFlow.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aFinanceWorkFlow);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinanceWorkFlowItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinanceWorkFlow.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String finEvent = (String) selectedItem.getAttribute("finEvent");
		String moduleName = (String) selectedItem.getAttribute("moduleName");
 
		FinanceWorkFlow aFinanceWorkFlow = financeWorkFlowService.getFinanceWorkFlowById(id, finEvent, moduleName);

		if (aFinanceWorkFlow == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinType='" + aFinanceWorkFlow.getFinType() + "' AND version="
				+ aFinanceWorkFlow.getVersion() + " ";

		if (doCheckAuthority(aFinanceWorkFlow, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && aFinanceWorkFlow.getWorkflowId() == 0) {
				aFinanceWorkFlow.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(aFinanceWorkFlow);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aFinanceWorkFlow
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceWorkFlow aFinanceWorkFlow) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeWorkFlow", aFinanceWorkFlow);
		arg.put("financeWorkFlowListCtrl", this);
		arg.put("isPromotion", isPromotion);
		arg.put("isCollateral", isCollateral);
		arg.put("isVAS", isVAS);
		arg.put("isCommitment", isCommitment);
		arg.put("eventAction", eventAction);
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceWorkFlow/FinanceWorkFlowDialog.zul",
					null, arg);
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

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}
}