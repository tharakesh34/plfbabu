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
 * * FileName : FinQueryDetailListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-05-2018 * *
 * Modified Date : 09-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.loanquery.querydetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.service.loanquery.QueryDetailService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.collateral.collateralsetup.CollateralBasicDetailsCtrl;
import com.pennant.webui.finance.financemain.FinBasicDetailsCtrl;
import com.pennant.webui.loanquery.querydetail.model.QueryDetailListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.web.util.MessageUtil;

/**
 * This is the controller class for the /WEB-INF/pages/LoanQuery/QueryDetail/FinQueryDetailList.zul file.
 * 
 */
public class FinQueryDetailListCtrl extends GFCBaseListCtrl<QueryDetail> {
	private static final long serialVersionUID = 1L;

	protected Window window_FinQueryDetailList;
	protected Borderlayout borderLayout_FinQueryDetailList;
	protected Paging pagingFinQueryDetailList;
	protected Listbox listBoxFinQueryDetail;

	// List headers
	protected Listheader listheader_Id;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_CategoryId;
	protected Listheader listheader_Status;
	protected Listheader listheader_QryNotes;
	protected Listheader listheader_RaisedBy;
	protected Listheader listheader_RaisedOn;
	protected Listheader listheader_Description;
	protected Listheader listheader_UsrLogin;
	// checkRights
	protected Button button_FinQueryDetailList_NewQueryDetail;
	private boolean enquiry = false;
	protected Groupbox finBasicdetails;
	private FinBasicDetailsCtrl finBasicDetailsCtrl;
	private CollateralBasicDetailsCtrl collateralBasicDetailsCtrl;

	private transient QueryDetailService queryDetailService;
	private FinanceMain financeMain = null;
	private LegalDetail legalDetail = null;
	private Sampling sampling = null;
	private String roleCode;

	/**
	 * default constructor.<br>
	 */
	public FinQueryDetailListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		if (financeMain != null) {
			this.searchObject.addFilterEqual("Module", PennantConstants.QUERY_ORIGINATION);
			this.searchObject.addFilterEqual("FinReference", financeMain.getFinReference());
		}
		if (legalDetail != null) {
			this.searchObject.addFilterEqual("Module", PennantConstants.QUERY_LEGAL_VERIFICATION);
			this.searchObject.addFilterEqual("Reference", legalDetail.getLegalReference());
		}

		if (sampling != null) {
			this.searchObject.addFilterEqual("Module", PennantConstants.QUERY_SAMPLING);
			this.searchObject.addFilterEqual("FinReference", sampling.getKeyReference());

		}
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "QueryDetail";
		super.pageRightName = "FinQueryDetailList";
		super.tableName = "QUERYDETAIL_View";
		super.queueTableName = "QUERYDETAIL_View";
		super.enquiryTableName = "QUERYDETAIL_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinQueryDetailList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_FinQueryDetailList, borderLayout_FinQueryDetailList, listBoxFinQueryDetail,
				pagingFinQueryDetailList);

		if (arguments.containsKey("financeMain")) {
			this.financeMain = (FinanceMain) arguments.get("financeMain");
		}

		if (arguments.containsKey("sampling")) {
			this.sampling = (Sampling) arguments.get("sampling");
		}

		if (arguments.containsKey("legalDetail")) {
			this.legalDetail = (LegalDetail) arguments.get("legalDetail");
		}

		if (arguments.containsKey("roleCode")) {
			this.roleCode = (String) arguments.get("roleCode");
		}
		setItemRender(new QueryDetailListModelItemRenderer(roleCode, getUserWorkspace().getLoggedInUser().getUserId()));

		if (arguments.containsKey("finHeaderList")) {
			appendFinBasicDetails((ArrayList<Object>) arguments.get("finHeaderList"));
		} else {
			appendFinBasicDetails(null);
		}

		if (arguments.containsKey("enquiry")) {
			setEnquiry((boolean) arguments.get("enquiry"));
		}

		// Register buttons and fields.
		// registerButton(button_FinQueryDetailList_NewQueryDetail, "button_FinQueryDetailList_NewQueryDetail", true);
		this.button_FinQueryDetailList_NewQueryDetail.setVisible(true);

		if (isEnquiry() || enqiryModule) {
			this.button_FinQueryDetailList_NewQueryDetail.setVisible(false);
		}

		registerField("id");
		registerField("FinID");
		registerField("finReference");
		registerField("categoryId");
		registerField("qryNotes");
		registerField("assignedRole");
		registerField("notifyTo");
		registerField("categoryCode");
		registerField("status");
		registerField("raisedBy");
		registerField("raisedOn");
		registerField("categoryDescription");
		registerField("usrLogin");

		getBorderLayoutHeight();
		this.listBoxFinQueryDetail.setHeight(this.borderLayoutHeight - 210 + "px");
		this.window_FinQueryDetailList.setHeight(this.borderLayoutHeight - 80 + "px");

		doRenderPage();
		search();
	}

	/**
	 * The framework calls this event handler when user clicks the new button. Show the dialog page with a new entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_FinQueryDetailList_NewQueryDetail(Event event) {
		logger.debug(Literal.ENTERING);

		// Create a new entity.
		QueryDetail querydetail = new QueryDetail();
		querydetail.setNewRecord(true);
		querydetail.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowNewDialogPage(querydetail);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for append finance basic details to respective parent tabs
	 */
	private void appendFinBasicDetails(ArrayList<Object> finHeaderList) {
		logger.debug(Literal.ENTERING);

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			String module = (String) arguments.get("moduleName");
			map.put("parentCtrl", this);
			if (finHeaderList != null) {
				map.put("finHeaderList", finHeaderList);
			}

			if (PennantConstants.QUERY_ORIGINATION.equals(module)) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinBasicDetails.zul",
						this.finBasicdetails, map);
			} else {
				map.put("moduleName", module);
				Executions.createComponents("/WEB-INF/pages/Collateral/CollateralSetup/CollateralBasicDetails.zul",
						this.finBasicdetails, map);
			}
		} catch (Exception e) {
			logger.error(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onQueryDetailItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxFinQueryDetail.getSelectedItem();
		final long id = (long) selectedItem.getAttribute("id");
		QueryDetail querydetail = queryDetailService.getQueryDetail(id);

		if (querydetail == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		StringBuilder whereCond = new StringBuilder();
		whereCond.append("  where  Id = ?");

		if (doCheckAuthority(querydetail, whereCond.toString(), new Object[] { querydetail.getId() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && querydetail.getWorkflowId() == 0) {
				querydetail.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(querydetail);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param querydetail The entity that need to be passed to the dialog.
	 */
	private void doShowNewDialogPage(QueryDetail querydetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("queryDetail", querydetail);
		arg.put("finQueryDetailListCtrl", this);
		arg.put("financeMain", financeMain);
		if (this.sampling != null) {
			arg.put("sampling", this.sampling);
		}
		arg.put("roleCode", roleCode);

		if (legalDetail != null) {
			arg.put("legalDetail", legalDetail);
		}
		if (sampling != null) {
			arg.put("sampling", sampling);
		}

		try {
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/QueryDetailNewDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doShowDialogPage(QueryDetail querydetail) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("queryDetail", querydetail);
		arg.put("finQueryDetailListCtrl", this);
		arg.put("financeMain", financeMain);
		arg.put("enquiry", isEnquiry());
		if (legalDetail != null) {
			arg.put("legalDetail", legalDetail);
		}
		if (sampling != null) {
			arg.put("sampling", sampling);
		}
		try {
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/QueryDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error("Exception:", e);
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void setQueryDetailService(QueryDetailService queryDetailService) {
		this.queryDetailService = queryDetailService;
	}

	public FinBasicDetailsCtrl getFinBasicDetailsCtrl() {
		return finBasicDetailsCtrl;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public CollateralBasicDetailsCtrl getCollateralBasicDetailsCtrl() {
		return collateralBasicDetailsCtrl;
	}

	public void setCollateralBasicDetailsCtrl(CollateralBasicDetailsCtrl collateralBasicDetailsCtrl) {
		this.collateralBasicDetailsCtrl = collateralBasicDetailsCtrl;
	}

	public boolean isEnquiry() {
		return enquiry;
	}

	public void setEnquiry(boolean enquiry) {
		this.enquiry = enquiry;
	}

}