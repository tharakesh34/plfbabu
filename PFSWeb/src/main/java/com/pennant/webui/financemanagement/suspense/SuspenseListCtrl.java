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
 * FileName    		:  SuspenseListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.webui.financemanagement.suspense;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.service.financemanagement.SuspenseService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.webui.financemanagement.suspense.model.SuspenseListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

/**
 * This is the controller class for the /WEB-INF/pages/FinanceManagement/Suspense/SuspenseList.zul file.
 */
public class SuspenseListCtrl extends GFCBaseListCtrl<FinanceSuspHead> {
	private static final long serialVersionUID = 4481377123949925578L;
	private static final Logger logger = Logger.getLogger(SuspenseListCtrl.class);

	protected Window window_SuspenseList;
	protected Borderlayout borderLayout_SuspenseList;
	protected Paging pagingSuspenseList;
	protected Listbox listBoxSuspense;

	protected Listheader listheader_FinReference;
	protected Listheader listheader_CustID;
	protected Listheader listheader_FinIsInSusp;
	protected Listheader listheader_ManualSusp;
	protected Listheader listheader_FinSuspAmt;
	protected Listheader listheader_FinCurSuspAmt;

	protected Textbox finReference;
	protected Textbox custID;
	protected Datebox finSuspDate;
	protected Decimalbox finSuspAmt;
	protected Listbox sortOperator_finSuspAmt;
	protected Decimalbox finCurSuspAmt;
	protected Listbox sortOperator_finCurSuspAmt;
	protected Checkbox manualSusp;

	protected Listbox sortOperator_manualSusp;
	protected Listbox sortOperator_finReference;
	protected Listbox sortOperator_custID;
	protected Listbox sortOperator_finSuspDate;

	protected Label label_SuspenseSearchResult;

	protected Button button_SuspenseList_SuspenseSearchDialog;
	protected Button button_SuspenseList_NewSuspense;

	private SuspenseService suspenseService;
	protected JdbcSearchObject<FinanceSuspHead> searchObj;
	
	protected Textbox moduleName;
	private String rightName = null;
	//private String module = "";

	private String moduleDefiner = "";
	private String buildedWhereCondition = "";

	/**
	 * default constructor.<br>
	 */
	public SuspenseListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		//super.moduleCode = "Academic";
		this.rightName = getArgument("rightName");
	    super.moduleCode = "FinanceSuspHead";
		super.pageRightName = "SuspenseList";
		super.tableName = "FinSuspHead_AView";
		super.queueTableName = "FinSuspHead_View";

		if (!enqiryModule) {
			moduleDefiner = FinanceConstants.FINSER_EVENT_SUSPHEAD;
		}
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
		buildedWhereCondition = buildedWhereCondition.concat(moduleDefiner);
		buildedWhereCondition = buildedWhereCondition.concat("' AND WF.FirstTaskOwner IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat("))) OR NextRoleCode IN(");
		buildedWhereCondition = buildedWhereCondition.concat(rolecodeList);
		buildedWhereCondition = buildedWhereCondition.concat(") ");
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_SuspenseList(Event event) {
		logger.debug("Entering" + event.toString());

		// Set the page level components.
		setPageComponents(window_SuspenseList, borderLayout_SuspenseList, listBoxSuspense, pagingSuspenseList);
		setItemRender(new SuspenseListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_SuspenseList_NewSuspense, "button_SuspenseList_NewSuspense", true);
		registerButton(button_SuspenseList_SuspenseSearchDialog);

		registerField("finReference", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("custID", listheader_CustID, SortOrder.NONE, custID, sortOperator_custID, Operators.STRING);
		registerField("finSuspDate",finSuspDate, SortOrder.NONE,  sortOperator_finSuspDate, Operators.DATE);
		registerField("manualSusp", listheader_ManualSusp, SortOrder.NONE, manualSusp, sortOperator_manualSusp,
				Operators.BOOLEAN);
		registerField("LovDescCustCIFName");
		registerField("finIsInSusp");
		registerField("FinSuspAmt");
		registerField("finCurSuspAmt");
		

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
	public void onClick$button_SuspenseList_SuspenseSearchDialog(Event event) {
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
	public void onClick$button_SuspenseList_NewSuspense(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		FinanceSuspHead aSuspense = new FinanceSuspHead();
		aSuspense.setNewRecord(true);
		aSuspense.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(aSuspense);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onSuspenseItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxSuspense.getSelectedItem();
		boolean isEnquiry = true;
		
		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		String userRole = (String) selectedItem.getAttribute("userRole");

		FinanceSuspHead suspHead = suspenseService.getFinanceSuspHeadById(id, enqiryModule, userRole, moduleDefiner);
		
		if (suspHead == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}
		
		if (StringUtils.isBlank(suspHead.getRecordType()) && !isEnquiry) {			
			try {
				doLoadWorkflow(suspHead.getFinType(), moduleDefiner);
			} catch (Exception e) {
				MessageUtil.showError(e);
				return;
			}
			if (StringUtils.isBlank(userRole)) {
				userRole = getFirstTaskRole();
			}

			suspHead.setWorkflowId(getWorkFlowId());
			
		}
		
		// Check whether the user has authority to change/view the record.
		String whereCond = " AND FinReference='" + suspHead.getFinReference() + "' AND Version="
				+ suspHead.getVersion() + " ";

		if (doCheckAuthority(suspHead, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && suspHead.getWorkflowId() == 0) {
				suspHead.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(suspHead);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aSuspHead
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(FinanceSuspHead aSuspHead) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("suspHead", aSuspHead);

		arg.put("suspenseListCtrl", this);
		arg.put("moduleCode", super.moduleCode);
		arg.put("menuItemRightName", rightName);
		arg.put("moduleDefiner", moduleDefiner);
		arg.put("eventCode", AccountEventConstants.ACCEVENT_NORM_PIS);

		try {

			if (enqiryModule) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/SuspenseDetail/SuspenseDetailEnquiryDialog.zul", null, arg);
			} else if (aSuspHead.isNew()) {
				Executions.createComponents(
						"/WEB-INF/pages/FinanceManagement/SelectFinance/SelectFinReferenceDialog.zul", null, arg);
			} else {
				Executions.createComponents("/WEB-INF/pages/FinanceManagement/Suspense/SuspenseDialog.zul", null, arg);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving");
	}

	public void setSuspenseService(SuspenseService suspenseService) {
		this.suspenseService = suspenseService;
	}

	public JdbcSearchObject<FinanceSuspHead> getSearchObj() {
		return searchObj;
	}

	public void setSearchObj(JdbcSearchObject<FinanceSuspHead> searchObj) {
		this.searchObj = searchObj;
	}
}