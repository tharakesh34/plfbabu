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
 * * FileName : ChequeHeaderListCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-11-2017 * * Modified
 * Date : 27-11-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-11-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.pdc.chequeheader;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.pdc.ChequeHeaderService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.webui.pdc.chequeheader.model.ChequeHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.framework.web.components.SearchFilterControl;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/pdc/ChequeHeader/ChequeHeaderList.zul file.
 * 
 */
public class ChequeHeaderListCtrl extends GFCBaseListCtrl<ChequeHeader> {
	private static final long serialVersionUID = 1L;

	protected Window window_ChequeHeaderList;
	protected Borderlayout borderLayout_ChequeHeaderList;
	protected Paging pagingChequeHeaderList;
	protected Listbox listBoxChequeHeader;

	// List headers
	protected Listheader listheader_FinReference;
	protected Listheader listheader_NoOfCheques;
	protected Listheader listheader_TotalAmount;

	// checkRights
	protected Button button_ChequeHeaderList_NewChequeHeader;
	protected Button button_ChequeHeaderList_ChequeHeaderSearch;

	// Search Fields
	protected Textbox finReference;
	protected Intbox noOfCheques;
	protected Decimalbox totalAmount;

	protected Listbox sortOperator_FinReference;
	protected Listbox sortOperator_NoOfCheques;
	protected Listbox sortOperator_TotalAmt;

	private ChequeHeaderService chequeHeaderService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceDetailService financeDetailService;
	private FinanceWriteoffDAO financeWriteoffDAO;

	/**
	 * default constructor.<br>
	 */
	public ChequeHeaderListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "ChequeHeader";
		super.pageRightName = "ChequeHeaderList";
		super.tableName = "CHEQUEHEADER_AView";
		super.queueTableName = "ChequeDetailMaintain_view";
		super.enquiryTableName = "CHEQUEHEADER_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onCreate$window_ChequeHeaderList(Event event) {
		logger.debug(Literal.ENTERING);
		// Set the page level components.
		setPageComponents(window_ChequeHeaderList, borderLayout_ChequeHeaderList, listBoxChequeHeader,
				pagingChequeHeaderList);
		setItemRender(new ChequeHeaderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_ChequeHeaderList_ChequeHeaderSearch);
		registerButton(button_ChequeHeaderList_NewChequeHeader, "button_ChequeHeaderList_NewChequeHeader", true);

		registerField("headerID");
		registerField("FinID");
		registerField("finReference", listheader_FinReference, SortOrder.NONE, finReference, sortOperator_FinReference,
				Operators.STRING);
		registerField("noOfCheques", listheader_NoOfCheques, SortOrder.NONE, noOfCheques, sortOperator_NoOfCheques,
				Operators.NUMERIC);
		registerField("totalAmount", listheader_TotalAmount, SortOrder.NONE, totalAmount, sortOperator_TotalAmt,
				Operators.NUMERIC);
		registerField("active");

		// Render the page and display the data.
		doRenderPage();

		search();

		this.button_ChequeHeaderList_NewChequeHeader.setVisible(false);
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */
	public void onClick$button_ChequeHeaderList_ChequeHeaderSearch(Event event) {
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
	public void onClick$button_ChequeHeaderList_NewChequeHeader(Event event) {
		logger.debug(Literal.ENTERING);
		// Create a new entity.
		ChequeHeader chequeheader = new ChequeHeader();
		chequeheader.setNewRecord(true);
		chequeheader.setWorkflowId(getWorkFlowId());
		// Display the dialog page.
		doShowDialogPage(chequeheader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event An event sent to the event handler of the component.
	 */

	public void onChequeHeaderItemDoubleClicked(Event event) {
		logger.debug(Literal.ENTERING);
		// Get the selected record.
		Listitem selectedItem = this.listBoxChequeHeader.getSelectedItem();
		final long headerID = (long) selectedItem.getAttribute("headerID");
		ChequeHeader chequeheader = chequeHeaderService.getChequeHeader(headerID);

		if (chequeheader == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Validate Loan is INPROGRESS in any Other Servicing option or NOT ?
		String rcdMntnSts = financeDetailService.getFinanceMainByRcdMaintenance(chequeheader.getFinID());
		if (StringUtils.isNotEmpty(rcdMntnSts) && !FinServiceEvent.CHEQUEDETAILS.equals(rcdMntnSts)) {
			MessageUtil.showError(Labels.getLabel("Finance_Inprogresss_" + rcdMntnSts));
			return;
		}

		boolean writeoffLoan = financeWriteoffDAO.isWriteoffLoan(chequeheader.getFinID(), "");
		if (writeoffLoan) {
			MessageUtil.showMessage(Labels.getLabel("label_Writeoff_Loan"));
			return;
		}

		StringBuffer whereCond = new StringBuffer();
		whereCond.append("  where  HeaderID =?");

		if (doCheckAuthority(chequeheader, whereCond.toString(), new Object[] { chequeheader.getHeaderID() })) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && chequeheader.getWorkflowId() == 0) {
				chequeheader.setWorkflowId(getWorkFlowId());
			}
			logUserAccess("menu_Item_ChequeHeader", chequeheader.getFinReference());
			doShowDialogPage(chequeheader);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param chequeheader The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(ChequeHeader chequeheader) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> arg = getDefaultArguments();
		arg.put("chequeHeader", chequeheader);
		arg.put("chequeHeaderListCtrl", this);
		List<FinanceScheduleDetail> finSchduleList = financeScheduleDetailDAO
				.getFinScheduleDetails(chequeheader.getFinID(), "_View", false);
		arg.put("financeSchedules", finSchduleList);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/PDC/ChequeDetailDialog.zul", null, arg);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
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

	@Override
	protected void doAddFilters() {
		logger.debug(Literal.ENTERING);

		this.searchObject.clearFilters();

		for (SearchFilterControl searchControl : searchControls) {
			Filter filter = searchControl.getFilter();
			if (filter != null) {
				String property = filter.getProperty();
				if (property.equals("totalAmount")) {
					filter.setValue(PennantApplicationUtil.unFormateAmount((BigDecimal) filter.getValue(),
							PennantConstants.defaultCCYDecPos));
				}
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

		if (applyDefaultFilter || isFilterApplied()) {
			if (isWorkFlowEnabled() && !enqiryModule) {
				this.searchObject.addFilterIn("nextRoleCode", getUserWorkspace().getUserRoles(), isFirstTask());
			}
		}

		applyDefaultFilter = true;

		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on "fromWorkFlow"
	 * 
	 * @param event
	 */
	public void onCheck$fromWorkFlow(Event event) {
		search();
	}

	public void setChequeHeaderService(ChequeHeaderService chequeHeaderService) {
		this.chequeHeaderService = chequeHeaderService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

}