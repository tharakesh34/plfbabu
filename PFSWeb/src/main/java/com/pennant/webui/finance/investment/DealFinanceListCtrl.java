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
 * FileName    		:  FinanceMainListCtrl.java                                                   * 	  
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
package com.pennant.webui.finance.investment;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.TreasuaryFinanceService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.search.Filter;
import com.pennant.webui.finance.financemain.model.InvestMentFinanceMainListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/FinanceMain/FinanceMainList.zul file.
 */
public class DealFinanceListCtrl extends GFCBaseListCtrl<FinanceMain> {
	private static final long serialVersionUID = -5901195042041627750L;
	private final static Logger logger = Logger.getLogger(DealFinanceListCtrl.class);

	protected Window window_DealFinanceList;
	protected Borderlayout borderLayout_FinanceMainList;
	protected Paging pagingFinanceMainList;
	protected Listbox listBoxFinanceMain;

	protected Textbox investmentRef;
	protected Textbox finReference;
	protected Textbox finType;
	protected Textbox finCcy;
	protected Textbox scheduleMethod;
	protected Textbox profitDaysBasis;
	protected Datebox finStartDate;
	protected Decimalbox finAmount;
	protected Textbox custID;
	protected Checkbox finIsActive;

	protected Listbox sortOperator_finStartDate;
	protected Listbox sortOperator_finAmount;
	protected Listbox sortOperator_custID;
	protected Listbox sortOperator_finIsActive;
	protected Listbox sortOperator_profitDaysBasis;
	protected Listbox sortOperator_scheduleMethod;
	protected Listbox sortOperator_finCcy;
	protected Listbox sortOperator_finType;
	protected Listbox sortOperator_finReference;
	protected Listbox sortOperator_InvReference;

	protected Listheader listheader_CustomerCIF;
	protected Listheader listheader_CustomerName;
	protected Listheader listheader_FinReference;
	protected Listheader listheader_INVFinReference;
	protected Listheader listheader_ProductName;
	protected Listheader listheader_FinType;
	protected Listheader listheader_FinCcy;
	protected Listheader listheader_ScheduleMethod;
	protected Listheader listheader_FinAmount;
	protected Listheader listheader_FinancingAmount;

	protected Button button_FinanceMainList_NewFinanceMain;
	protected Button button_DealFinanceList_SearchDialog;

	protected JdbcSearchObject<FinanceMain> searchObj;

	private transient FinanceDetailService financeDetailService;
	private transient TreasuaryFinanceService treasuaryFinanceService;
	private Textbox productCategory;// Field for Maintain Different Finance Product Types
	protected int oldVar_sortOperator_finType;
	private InvestmentFinHeader investmentFinHeader;

	/**
	 * default constructor.<br>
	 */
	public DealFinanceListCtrl() {
		super();
	}

	@Override
	protected void doAddFilters() {
		super.doAddFilters();
		searchObject.addFilter(new Filter("InvestmentRef", "", Filter.OP_NOT_EQUAL));
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceMain";
		super.pageRightName = "FinanceMainList";
		super.tableName = "FinanceMain_DView";
		super.queueTableName = "FinanceMain_DView";
		super.enquiryTableName = "FinanceMain_DView";
	}

	public void onCreate$window_DealFinanceList(Event event) {
		// Set the page level components.
		setPageComponents(window_DealFinanceList, borderLayout_FinanceMainList, listBoxFinanceMain,
				pagingFinanceMainList);
		setItemRender(new InvestMentFinanceMainListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_FinanceMainList_NewFinanceMain, RIGHT_NOT_ACCESSIBLE, true);
		registerButton(button_DealFinanceList_SearchDialog);

		registerField("CustCIF", listheader_CustomerCIF, SortOrder.NONE, custID, sortOperator_custID, Operators.STRING);
		registerField("InvestmentRef", listheader_INVFinReference, SortOrder.NONE, investmentRef,
				sortOperator_InvReference, Operators.STRING);

		registerField("FinReference", listheader_FinReference, SortOrder.ASC, finReference, sortOperator_finReference,
				Operators.STRING);
		registerField("FinCcy", listheader_FinCcy, SortOrder.NONE, finCcy, sortOperator_finCcy, Operators.STRING);

		registerField("FinType", listheader_FinType, SortOrder.NONE, finType, sortOperator_finType, Operators.STRING);
		registerField("ScheduleMethod", listheader_ScheduleMethod, SortOrder.NONE, scheduleMethod,
				sortOperator_scheduleMethod, Operators.STRING);

		//registerField("FinIsActive", finIsActive, SortOrder.NONE, sortOperator_finIsActive, Operators.BOOLEAN);

		registerField("LovDescFinFormatter", listheader_FinAmount);
		registerField("FinAmount", listheader_FinancingAmount);
		registerField("LovDescCustCIF");
		registerField("LovDescCustShrtName", listheader_CustomerName);
		registerField("LovDescProductCodeName", listheader_ProductName, SortOrder.ASC);

		// Render the page and display the data.
		doRenderPage();
		search();

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user clicks the search button.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onClick$button_DealFinanceList_SearchDialog(Event event) {
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
	 * @throws InterruptedException
	 */
	public void onClick$button_FinanceMainList_NewFinanceMain(Event event) throws InterruptedException {
		logger.debug("Entering");

		final FinanceDetail aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		aFinanceDetail.setNewRecord(true);

		/*
		 * we can call our SelectFinanceType ZUL-file with parameters. So we can call them with a object of the selected
		 * FinanceMain. For handed over these parameter only a Map is accepted. So we put the FinanceMain object in a
		 * HashMap.
		 */
		Map<String, Object> map = getDefaultArguments();
		map.put("financeMainListCtrl", this);
		map.put("searchObject", this.searchObj);
		map.put("financeDetail", aFinanceDetail);
		map.put("productCategory", this.productCategory.getValue());
		map.put("role", getUserWorkspace().getUserRoles());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/SelectFinanceTypeDialog.zul", null, map);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e);
		}
		logger.debug("Leaving ");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onFinanceMainItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");

		// get the selected FinanceMain object
		final Listitem item = this.listBoxFinanceMain.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceMain aFinanceMain = (FinanceMain) item.getAttribute("data");

			String finReference = aFinanceMain.getFinReference();
			final FinanceDetail financeDetail = new FinanceDetail();
			financeDetail.getFinScheduleData().setFinReference(finReference);
			financeDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

			FinanceDetail afinanceDetail = getTreasuaryFinanceService().getFinanceDetailById(financeDetail,
					finReference);
			this.investmentFinHeader = getTreasuaryFinanceService().getTreasuaryFinHeader(finReference, "_AView");
			this.investmentFinHeader.setFinanceDetail(afinanceDetail);
			aFinanceMain.setInvestmentRef(investmentFinHeader.getInvestmentRef());

			if (aFinanceMain.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("role", getUserWorkspace().getUserRoles());
				map.put("investmentFinHeader", investmentFinHeader);
				map.put("DealFinanceListCtrl", this);

				// Call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/TreasuaryFinance/DealFinanceDetailDialog.zul",
							window_DealFinanceList, map);
				} catch (Exception e) {
					logger.error("Exception: Opening window", e);
					MessageUtil.showErrorMessage(e);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param academic
	 *            The entity that need to be passed to the dialog.
	 */
	protected void doShowDialogPage(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Map<String, Object> arg = getDefaultArguments();
		arg.put("financeDetail", aFinanceDetail);
		arg.put("financeMainListCtrl", this);

		try {
			String productType = aFinanceMain.getLovDescProductCodeName();
			productType = (productType.substring(0, 1)).toUpperCase() + (productType.substring(1)).toLowerCase();
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/" + productType + "FinanceMainDialog.zul",
					this.window_DealFinanceList, arg);
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
			MessageUtil.showErrorMessage(e);
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
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		setSearchValue(sortOperator_finType, this.finType, "FinanceType");

		logger.debug("Leaving " + event.toString());
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public TreasuaryFinanceService getTreasuaryFinanceService() {
		return treasuaryFinanceService;
	}

	public void setTreasuaryFinanceService(TreasuaryFinanceService treasuaryFinanceService) {
		this.treasuaryFinanceService = treasuaryFinanceService;
	}

	public void setInvestmentFinHeader(InvestmentFinHeader investmentFinHeader) {
		this.investmentFinHeader = investmentFinHeader;
	}

	public InvestmentFinHeader getInvestmentFinHeader() {
		return investmentFinHeader;
	}

}