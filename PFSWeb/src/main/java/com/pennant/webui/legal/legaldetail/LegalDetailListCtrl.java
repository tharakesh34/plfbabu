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
 * * FileName : LegalDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-06-2018 * * Modified
 * Date : 16-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legaldetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.webui.legal.legaldetail.model.LegalDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Legal/LegalDetail/LegalDetailList.zul file.
 * 
 */
public class LegalDetailListCtrl extends GFCBaseListCtrl<LegalDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_LegalDetailList;
	protected Borderlayout borderLayout_LegalDetailList;
	protected Paging pagingLegalDetailList;
	protected Listbox listBoxLegalDetail;

	// List headers
	protected Listheader listheader_Active;
	protected Listheader listheader_LoanReference;
	protected Listheader listheader_CollaterialReference;
	protected Listheader listheader_Branch;
	protected Listheader listheader_LegalDate;
	protected Listheader listheader_LegalReference;
	protected Listheader listheader_ApplicantName;
	protected Listheader listheader_RequestStage;

	// checkRights
	protected Button button_LegalDetailList_NewLegalDetail;
	protected Button button_LegalDetailList_LegalDetailSearch;

	// Search Fields
	protected Textbox loanReference;
	protected Textbox collaterialReference;
	protected Textbox branch;
	protected Datebox legalDate;
	protected Textbox schedulelevelArea;
	protected Textbox legalDecision;
	protected Textbox legalReference;

	protected Listbox sortOperator_LoanReference;
	protected Listbox sortOperator_CollaterialReference;
	protected Listbox sortOperator_Branch;
	protected Listbox sortOperator_LegalDate;
	protected Listbox sortOperator_SchedulelevelArea;
	protected Listbox sortOperator_LegalDecision;
	protected Listbox sortOperator_LegalReference;
	protected Listbox sortOperator_ApplicantName;
	protected Listbox sortOperator_RequestStage;

	private transient LegalDetailService legalDetailService;
	private String module;
	private transient WorkflowEngine workflowEngine;

	/**
	 * default constructor.<br>
	 */
	public LegalDetailListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		this.module = getArgument("enqiryModule");
		super.moduleCode = "LegalDetail";
		super.pageRightName = "LegalDetailList";
		if (StringUtils.equals(this.module, PennantConstants.YES)) {
			super.tableName = "LegalDetails_View";
			super.queueTableName = "LegalDetails_View";
			super.enquiryTableName = "LegalDetails_View";
		} else {
			super.tableName = "LegalDetails_TView";
			super.queueTableName = "LegalDetails_TView";
			super.enquiryTableName = "LegalDetails_View";
		}
	}

	@Override
	protected void doAddFilters() {
		this.searchObject.clearFilters();
		String legalParam = SysParamUtil.getValueAsString(SMTParameterConstants.LEGAL_DETAILS_DISPLAY_INACTIVE_RECORDS);

		// Add filter for user's role queue.
		if (isWorkFlowEnabled() && !enqiryModule) {
			List<String> roles = new ArrayList<>();
			List<String> actors = workflowEngine.getActors(false);

			for (String role : getUserWorkspace().getUserRoleSet()) {
				if (actors.contains(role)) {
					roles.add(role);
				}
			}

			StringBuilder whereClause = new StringBuilder();

			if (roles.isEmpty()) {
				whereClause.append(" nextRoleCode = '' ");
			} else {
				for (String role : roles) {
					if (whereClause.length() > 0) {
						whereClause.append(" OR ");
					}

					whereClause.append("(',' ");
					whereClause.append(QueryUtil.getQueryConcat());
					whereClause.append(" nextRoleCode ");
					whereClause.append(QueryUtil.getQueryConcat());
					whereClause.append(" ',' LIKE '%,");
					whereClause.append(role);
					whereClause.append(",%')");
				}

				whereClause.append(" ) AND ( ");
				whereClause.append("(");
				whereClause.append(getUsrFinAuthenticationQry(true));
				whereClause.append(")");
			}

			this.searchObject.addWhereClause(whereClause.toString());
		}

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				if (App.DATABASE == Database.ORACLE && "recordType".equals(filter.getProperty())
						&& Filter.OP_NOT_EQUAL == filter.getOperator()) {
					Filter[] filters = new Filter[2];
					filters[0] = Filter.isNull(filter.getProperty());
					filters[1] = filter;

					this.searchObject.addFilterOr(filters);
				} else {
					this.searchObject.addFilter(filter);
				}
			}
		}

		if (!StringUtils.equals(this.module, PennantConstants.YES)) {
			Filter[] fileters = new Filter[1];
			fileters[0] = new Filter("module", PennantConstants.QUERY_LEGAL_VERIFICATION, Filter.OP_EQUAL);
			this.searchObject.addFilterOr(fileters);
		}

		if (StringUtils.isNotBlank(legalParam)) {
			if (!StringUtils.equals(legalParam, PennantConstants.YES)) {
				if (!StringUtils.equals(this.module, PennantConstants.YES)) {
					Filter[] fileters = new Filter[1];
					fileters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
					this.searchObject.addFilterOr(fileters);
				}
			}

		}
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_LegalDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_LegalDetailList, borderLayout_LegalDetailList, listBoxLegalDetail,
				pagingLegalDetailList);
		setItemRender(new LegalDetailListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_LegalDetailList_LegalDetailSearch);

		registerField("loanReference", listheader_LoanReference, SortOrder.NONE, loanReference,
				sortOperator_LoanReference, Operators.STRING);
		registerField("applicantName", listheader_ApplicantName);
		registerField("requestStage", listheader_RequestStage);
		registerField("collateralReference", listheader_CollaterialReference, SortOrder.NONE, collaterialReference,
				sortOperator_CollaterialReference, Operators.STRING);
		registerField("legalReference", listheader_LegalReference, SortOrder.NONE, legalReference,
				sortOperator_LegalReference, Operators.STRING);
		registerField("branchDesc", listheader_Branch, SortOrder.NONE, branch, sortOperator_Branch, Operators.STRING);
		registerField("legalDate", listheader_LegalDate, SortOrder.NONE, legalDate, sortOperator_LegalDate,
				Operators.DATE);
		registerField("legalRemarks");
		registerField("legalId");
		registerField("active");

		workflowEngine = new WorkflowEngine(WorkFlowUtil.getWorkFlowDetails(moduleCode).getWorkFlowXml());

		doSetFieldProperties();
		doRenderPage();

		search();
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		this.legalDate.setFormat(PennantConstants.dateFormat);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_LegalDetailList_LegalDetailSearch(Event event) {
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the refresh button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$btnRefresh(Event event) {
		doReset();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_LegalDetailList_NewLegalDetail(Event event) {
		logger.debug(Literal.ENTERING);
		// Create a new entity.
		LegalDetail legaldetail = new LegalDetail();
		legaldetail.setNewRecord(true);
		legaldetail.setWorkflowId(getWorkFlowId());
		doShowDialogPage(legaldetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onLegalDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxLegalDetail.getSelectedItem();
		final long legalId = (long) selectedItem.getAttribute("legalId");
		LegalDetail legaldetail = getLegalDetailService().getLegalDetail(legalId);

		if (legaldetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		if (!legaldetail.isActive()) {
			final String msg = Labels.getLabel("message.Question.Are_you_sure_to_proceed_this_record_is_inactive");
			if (MessageUtil.confirm(msg) == MessageUtil.NO) {
				return;
			}
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  LegalReference =? ");

		if (doCheckAuthority(legaldetail, whereCond.toString(), new Object[] { legaldetail.getLegalReference() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && legaldetail.getWorkflowId() == 0) {
				legaldetail.setWorkflowId(getWorkFlowId());
			}
			logUserAccess("menu_Item_LegalDetailEnquiry", legaldetail.getLegalReference());
			doShowDialogPage(legaldetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param legaldetail The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(LegalDetail legaldetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("legalDetail", legaldetail);
		arg.put("legalDetailListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user clicks the print button to print the results.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$print(Event event) {
		doPrintResults();
	}

	/**
	 * The framework calls this event handler when user clicks the help button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$help(Event event) {
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

	public LegalDetailService getLegalDetailService() {
		return legalDetailService;
	}

	public void setLegalDetailService(LegalDetailService legalDetailService) {
		this.legalDetailService = legalDetailService;
	}

}