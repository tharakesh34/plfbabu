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
 * FileName    		:  FinanceFlagsListCtrl.java                                            * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.financeFlags;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.service.finance.FinanceFlagsService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.webui.financemanagement.financeFlags.model.FinFlagsListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.jdbc.search.Filter;

public class FinanceFlagsListCtrl extends GFCBaseListCtrl<FinanceFlag> {
	private static final long serialVersionUID = -5081318673331825306L;
	private static final Logger logger = Logger.getLogger(FinanceFlagsListCtrl.class);

	protected Window window_FinanceFlagsList;
	protected Borderlayout borderlayout_FinanceFlags;
	protected Textbox custCIF;
	protected Textbox finReference;
	protected Textbox finType;
	protected Textbox finCcy;
	protected Textbox finBranch;
	protected Textbox scheduleMethod;
	protected Textbox profitDaysBasis;
	protected Paging pagingFinFlagList;

	protected Listbox listBoxFinFlags;
	protected Button btnClose;
	protected Grid grid_FinFlags;
	protected Div div_ToolBar;

	protected Listbox sortOperator_custCIF;
	protected Listbox sortOperator_finReference;
	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finCcy;
	protected Listbox sortOperator_finBranch;
	protected Listbox sortOperator_scheduleMethod;
	protected Listbox sortOperator_profitDaysBasis;

	protected Listheader listheader_FinType;
	protected Listheader listheader_FinProduct;
	protected Listheader listheader_CustCIF;
	protected Listheader listheader_FinRef;
	protected Listheader listheader_FinBranch;
	protected Listheader listheader_FinStartDate;
	protected Listheader listheader_NumberOfTerms;
	protected Listheader listheader_MaturityDate;
	protected Listheader listheader_FinCcy;
	protected Listheader listheader_FinAmount;
	protected Listheader listheader_CurFinAmount;

	protected Button btnHelp;
	protected Button button_FinanceFlagsList_NewFinanceFlags;
	protected Button btnSearch;
	protected Button button_FinanceFlagsList_PrintList;
	protected Button btnRefresh;

	private List<Filter> filterList;
	private transient FinanceFlagsService financeFlagsService;

	private String buildedWhereCondition = "";

	/**
	 * Default constructor
	 */
	public FinanceFlagsListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceFlag";
		super.pageRightName = "FinanceFlagsList";
		super.tableName = "FinFlagsHeader_View";
		super.queueTableName = "FinFlagsHeader_View";

	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		String rolecodeList = "";
		buildedWhereCondition = "";
		if (getUserWorkspace().getUserRoles() != null && !getUserWorkspace().getUserRoles().isEmpty()) {
			for (String role : getUserWorkspace().getUserRoles()) {
				rolecodeList = rolecodeList.concat(role).concat("','");
			}

			if (StringUtils.isNotEmpty(rolecodeList)) {
				rolecodeList = rolecodeList.substring(0, rolecodeList.length() - 2);
				rolecodeList = "'".concat(rolecodeList);
			}
		}
		if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.POSTGRES) {
			buildedWhereCondition = " (NextRoleCode IS NULL ";
		} else {
			buildedWhereCondition = " (NextRoleCode = '' ";
		}
		buildedWhereCondition = buildedWhereCondition
				.concat(" AND FinType IN(SELECT FinType FROM LMTFInanceworkflowdef WD JOIN WorkFlowDetails WF ");
		buildedWhereCondition = buildedWhereCondition
				.concat(" ON WD.WorkFlowType = WF.WorkFlowType AND WF.WorkFlowActive = 1 ");
		buildedWhereCondition = buildedWhereCondition.concat(" WHERE WD.FinEvent = '");
		buildedWhereCondition = buildedWhereCondition.concat(FinanceConstants.FINSER_EVENT_FINFLAGS);
		buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat("))) OR NextRoleCode IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat(") ");

		searchObject.addWhereClause(getUsrFinAuthenticationQry(false));
		if (StringUtils.isNotEmpty(buildedWhereCondition)) {
			searchObject.addWhereClause(buildedWhereCondition);
		}
		searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(),
				getUserWorkspace().isAllowed("button_FinanceFlagsList_NewFinanceFlags"));

		if (filterList != null && !filterList.isEmpty()) {
			for (Filter filter : filterList) {
				searchObject.addFilter(filter);
			}
		}

	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_FinanceFlagsList(Event event) {
		// Set the page level components.
		setPageComponents(window_FinanceFlagsList, borderlayout_FinanceFlags, listBoxFinFlags, pagingFinFlagList);
		setItemRender(new FinFlagsListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceFlagsList_NewFinanceFlags, "button_FinanceFlagsList_NewFinanceFlags", true);
		registerButton(btnSearch);

		registerField("CustCIF", listheader_CustCIF, SortOrder.NONE, custCIF, sortOperator_custCIF,
				Operators.MULTISELECT);
		registerField("FinReference", listheader_FinRef, SortOrder.NONE, finReference, sortOperator_finReference,
				Operators.MULTISELECT);
		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType,
				Operators.MULTISELECT);
		registerField("finCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.MULTISELECT);
		registerField("finBranch", listheader_FinBranch, SortOrder.NONE, finBranch, sortOperator_finBranch,
				Operators.MULTISELECT);
		registerField("ScheduleMethod", scheduleMethod, SortOrder.NONE, sortOperator_scheduleMethod,
				Operators.MULTISELECT);
		registerField("profitDaysBasis", profitDaysBasis, SortOrder.NONE, sortOperator_profitDaysBasis,
				Operators.MULTISELECT);
		registerField("finCategory");
		registerField("finStartDate");
		registerField("numberOfTerms");
		registerField("maturityDate");
		registerField("feeChargeAmt");

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
	public void onClick$btnSearch(Event event) {
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
	public void onClick$button_FinanceFlagsList_NewFinanceFlags(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinanceFlag aFinanceFlag = new FinanceFlag();
		aFinanceFlag.setNewRecord(true);
		aFinanceFlag.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aFinanceFlag);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinanceFlagsItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinFlags.getSelectedItem();

		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String tableType = StringUtils.isEmpty((String) selectedItem.getAttribute("recordType")) ? "_View" : "_TView";
		FinanceFlag afinanceFlag = financeFlagsService.getFinanceFlagsByRef(id, tableType);

		if (afinanceFlag == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinReference='" + afinanceFlag.getFinReference() + "' AND version="
				+ afinanceFlag.getVersion() + " ";

		if (doCheckAuthority(afinanceFlag, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && afinanceFlag.getWorkflowId() == 0) {
				afinanceFlag.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(afinanceFlag);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param afinanceFlag
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceFlag afinanceFlag) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeFlag", afinanceFlag);
		arg.put("financeFlagsListCtrl", this);
		arg.put("eventCode", FinanceConstants.FINSER_EVENT_FINFLAGS);

		try {
			if (afinanceFlag.isNew()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/SelectFinance/SelectFinReferenceDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/FinanceFlags/FinanceFlagsDialog.zul",
						null, arg);
			}
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

	/**
	 * When user clicks on "btnSearchBranch" button This method displays ExtendedSearchListBox with branch details
	 * 
	 * @param event
	 */
	public void onClick$btnSearchBranch(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finBranch, this.finBranch, "Branch");

		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finType, this.finType, "FinanceType");

		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "SearchFinCurrency"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finCcy, this.finCcy, "Currency");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchCustCIF" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_custCIF, this.custCIF, "Customer");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnSearchSchdMethod" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchSchdMethod(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_scheduleMethod, this.scheduleMethod, "ScheduleMethod");

		logger.debug("Leaving");
	}

	/**
	 * When user clicks on "btnPftDaysBasis" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchPftDaysBasis(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_profitDaysBasis, this.profitDaysBasis, "InterestRateBasisCode");

		logger.debug("Leaving");

	}

	/**
	 * When user clicks on button "SearchFinRef" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinRef(Event event) {
		logger.debug("Entering");

		setSearchValue(sortOperator_finReference, this.finReference, "FinanceFlag");

		logger.debug("Leaving");
	}

	public void setFinanceFlagsService(FinanceFlagsService financeFlagsService) {
		this.financeFlagsService = financeFlagsService;
	}
}
