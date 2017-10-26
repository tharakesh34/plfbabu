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
 * FileName    		:  FinanceMainListCtrl.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.finance.financemain;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.ReferenceUtil;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.webui.finance.treasuaryfinance.model.TreasuaryFinHeaderListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;
import com.pennanttech.pff.core.App;
import com.pennanttech.pff.core.App.Database;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/TreasuaryFinance/TreasuaryFinHeaderList.zul file.
 */
public class TreasuryFinHeaderListCtrl extends GFCBaseListCtrl<InvestmentFinHeader> {
	private static final long serialVersionUID = -5901195042041627750L;
	private static final Logger logger = Logger.getLogger(TreasuryFinHeaderListCtrl.class);

	protected Window window_TreasuaryFinHeaderList;
	protected Borderlayout borderLayout_TreasuaryFinHeaderList;
	public Paging pagingTFinHeaderList;
	public Listbox listBoxTrFinHeader;

	protected Textbox finReference;
	protected Listbox sortOperator_finReference;
	protected Decimalbox totPriAmount;
	protected Listbox sortOperator_totPriAmount;
	protected Textbox finCcy;
	protected Listbox sortOperator_finCcy;
	protected Datebox startDate;
	protected Listbox sortOperator_startDate;
	protected Datebox maturityDate;
	protected Listbox sortOperator_maturityDate;

	// List headers
	protected Listheader listheader_InvReqRef;
	protected Listheader listheader_TotPrincipal;
	protected Listheader listheader_FinCcy;
	protected Listheader listheader_StartDate;
	protected Listheader listheader_MaturityDate;

	protected Button button_InvestmentFinHeaderList_NewFinance;
	protected Button button_InvestmentFinHeaderList_SearchDialog;

	private transient TreasuaryFinanceService treasuaryFinanceService;

	/**
	 * default constructor.<br>
	 */
	public TreasuryFinHeaderListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();

		if (getUserWorkspace().getUserRoles() != null && getUserWorkspace().getUserRoles().size() > 0) {
			StringBuilder whereClause = new StringBuilder();

			for (String role : getUserWorkspace().getUserRoles()) {
				if (whereClause.length() > 0) {
					whereClause.append(" OR ");
				}

				whereClause.append("(',' ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" nextRoleCode ");
				if (App.DATABASE == Database.ORACLE || App.DATABASE == Database.PSQL) {
					whereClause.append("||");
				} else {
					whereClause.append("+");
				}
				whereClause.append(" ',' LIKE '%,");
				whereClause.append(role);
				whereClause.append(",%')");
			}

			if (!"".equals(whereClause.toString())) {
				searchObject.addWhereClause(whereClause.toString());
			}
		}

	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "InvestmentFinHeader";
		super.pageRightName = "InvestmentFinHeaderList";
		super.tableName = "InvestmentFinHeader_View";
		super.queueTableName = "InvestmentFinHeader_View";
		super.enquiryTableName = "InvestmentFinHeader_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_TreasuaryFinHeaderList(Event event) {
		// Set the page level components.
		setPageComponents(window_TreasuaryFinHeaderList, borderLayout_TreasuaryFinHeaderList, listBoxTrFinHeader,
				pagingTFinHeaderList);
		setItemRender(new TreasuaryFinHeaderListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_InvestmentFinHeaderList_NewFinance, "button_InvestmentFinHeaderList_NewFinance", true);
		registerButton(button_InvestmentFinHeaderList_SearchDialog);

		registerField("InvestmentRef", listheader_InvReqRef, SortOrder.ASC, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("FinCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.STRING);
		registerField("StartDate", listheader_StartDate, SortOrder.NONE, startDate, sortOperator_startDate,
				Operators.DATE);
		registerField("MaturityDate", listheader_MaturityDate, SortOrder.NONE, maturityDate, sortOperator_maturityDate,
				Operators.DATE);

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
	public void onClick$button_InvestmentFinHeaderList_SearchDialog(Event event) {
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
	public void onClick$button_InvestmentFinHeaderList_NewFinance(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		InvestmentFinHeader aInvestmentFinHeader = new InvestmentFinHeader();
		aInvestmentFinHeader.setNewRecord(true);
		aInvestmentFinHeader.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(getTrasuryFinHeader(aInvestmentFinHeader));

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onTreasuaryFinanceItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		// get the selected FinanceMain object
		final Listitem item = this.listBoxTrFinHeader.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final InvestmentFinHeader aTreasuaryFinHeader = (InvestmentFinHeader) item.getAttribute("data");
			InvestmentFinHeader treasuaryFinHeader = getTreasuaryFinanceService().getTreasuaryFinanceById(
					aTreasuaryFinHeader.getId());

			if (treasuaryFinHeader == null) {
				String[] errParm = new String[1];
				String[] valueParm = new String[1];
				valueParm[0] = aTreasuaryFinHeader.getId();
				errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ":" + valueParm[0];

				ErrorDetails errorDetails;
				errorDetails = ErrorUtil.getErrorDetail(new ErrorDetails(PennantConstants.KEY_FIELD, "41005", errParm,
						valueParm), getUserWorkspace().getUserLanguage());
				MessageUtil.showError(errorDetails.getError());
			} else {
				List<FinanceDetail> financeDetails = null;
				financeDetails = getTreasuaryFinanceService().getFinanceDetails(treasuaryFinHeader);
				treasuaryFinHeader.setFinanceDetailsList(financeDetails);
				doShowDialogPage(treasuaryFinHeader);
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aInvestmentFinHeader
	 *            The entity that need to be passed to the dialog.
	 */
	protected void doShowDialogPage(InvestmentFinHeader aInvestmentFinHeader) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("investmentFinHeader", aInvestmentFinHeader);
		arg.put("treasuryFinHeaderListCtrl", this);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/TreasuaryFinance/TreasuaryFinHeaderDialog.zul", null,
					arg);
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

	private InvestmentFinHeader getTrasuryFinHeader(InvestmentFinHeader treasuaryFinHeader) {
		treasuaryFinHeader.setInvestmentRef(String.valueOf(ReferenceUtil.genInvetmentNewRef()));
		treasuaryFinHeader.setStartDate(DateUtility.getAppDate());
		return treasuaryFinHeader;
	}

	public void setTreasuaryFinanceService(TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return treasuaryFinanceService;
	}

}