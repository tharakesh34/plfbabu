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
 * FileName    		:  CurrencyListCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.webui.applicationmaster.currency;

import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.service.applicationmaster.CurrencyService;
import com.pennant.webui.applicationmaster.currency.model.CurrencyListModelItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.framework.core.SearchOperator.Operators;
import com.pennanttech.framework.core.constants.SortOrder;

/**
 * This is the controller class for the /WEB-INF/pages/ApplicationMaster/Currency/CurrencyList.zul file.
 */
public class CurrencyListCtrl extends GFCBaseListCtrl<Currency> {
	private static final long serialVersionUID = -7603242416503761389L;
	private static final Logger logger = Logger.getLogger(CurrencyListCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting auto wired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_CurrencyList;
	protected Borderlayout borderLayout_CurrencyList;
	protected Paging pagingCurrencyList;
	protected Listbox listBoxCurrency;

	protected Textbox ccyCode;
	protected Intbox ccyNumber;
	protected Textbox ccyDesc;
	protected Textbox ccySwiftCode;
	protected Checkbox ccyIsActive;

	protected Listbox sortOperator_ccyCode;
	protected Listbox sortOperator_ccyNumber;
	protected Listbox sortOperator_ccyDesc;
	protected Listbox sortOperator_ccySwiftCode;
	protected Listbox sortOperator_ccyIsActive;

	// List headers
	protected Listheader listheader_CcyCode;
	protected Listheader listheader_CcyNumber;
	protected Listheader listheader_CcyDesc;
	protected Listheader listheader_CcySwiftCode;
	protected Listheader listheader_CcyIsActive;

	// checkRights
	protected Button button_CurrencyList_NewCurrency;
	protected Button button_CurrencyList_CurrencySearchDialog;

	private transient CurrencyService currencyService;

	/**
	 * default constructor.<br>
	 */
	public CurrencyListCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "Currency";
		super.pageRightName = "CurrencyList";
		super.tableName = "RMTCurrencies_AView";
		super.queueTableName = "RMTCurrencies_View";
	}

	/**
	 * The framework calls this event handler when an application requests that the window to be created.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCreate$window_CurrencyList(Event event) {
		// Set the page level components.
		setPageComponents(window_CurrencyList, borderLayout_CurrencyList, listBoxCurrency, pagingCurrencyList);
		setItemRender(new CurrencyListModelItemRenderer());

		// Register buttons and fields.
		registerButton(button_CurrencyList_NewCurrency, "button_CurrencyList_NewCurrency", true);
		registerButton(button_CurrencyList_CurrencySearchDialog);

		registerField("ccyCode", listheader_CcyCode, SortOrder.ASC, ccyCode, sortOperator_ccyCode, Operators.STRING);
		registerField("ccyNumber", listheader_CcyNumber, SortOrder.NONE, ccyNumber, sortOperator_ccyNumber,
				Operators.NUMERIC);
		registerField("ccyDesc", listheader_CcyDesc, SortOrder.NONE, ccyDesc, sortOperator_ccyDesc, Operators.STRING);
		registerField("ccySwiftCode", listheader_CcySwiftCode, SortOrder.NONE, ccySwiftCode, sortOperator_ccySwiftCode,
				Operators.STRING);
		registerField("ccyIsActive", listheader_CcyIsActive, SortOrder.NONE, ccyIsActive, sortOperator_ccyIsActive,
				Operators.BOOLEAN);

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
	public void onClick$button_CurrencyList_CurrencySearchDialog(Event event) {
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
	public void onClick$button_CurrencyList_NewCurrency(Event event) {
		logger.debug("Entering");

		// Create a new entity.
		Currency currency = new Currency();
		currency.setNewRecord(true);
		currency.setWorkflowId(getWorkFlowId());

		// Display the dialog page.
		doShowDialogPage(currency);

		logger.debug("Leaving");
	}

	/**
	 * The framework calls this event handler when user opens a record to view it's details. Show the dialog page with
	 * the selected entity.
	 * 
	 * @param event
	 *            An event sent to the event handler of the component.
	 */
	public void onCurrencyItemDoubleClicked(Event event) {
		logger.debug("Entering");

		// Get the selected record.
		Listitem selectedItem = this.listBoxCurrency.getSelectedItem();
		// Get the selected entity.
		String id = (String) selectedItem.getAttribute("id");
		Currency currency = currencyService.getCurrencyById(id);

		if (currency == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		// Check whether the user has authority to change/view the record.
		String whereCond = " AND CcyCode='" + currency.getCcyCode() + "' AND version=" + currency.getVersion() + " ";

		if (doCheckAuthority(currency, whereCond)) {
			// Set the latest work-flow id for the new maintenance request.
			if (isWorkFlowEnabled() && currency.getWorkflowId() == 0) {
				currency.setWorkflowId(getWorkFlowId());
			}
			doShowDialogPage(currency);
		} else {
			MessageUtil.showMessage(Labels.getLabel("info.not_authorized"));
		}

		logger.debug("Leaving");

	}

	/**
	 * Displays the dialog page with the required parameters as map.
	 * 
	 * @param aCurrency
	 *            The entity that need to be passed to the dialog.
	 */
	private void doShowDialogPage(Currency aCurrency) {
		logger.debug("Entering");

		Map<String, Object> arg = getDefaultArguments();
		arg.put("currency", aCurrency);
		arg.put("currencyListCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/ApplicationMaster/Currency/CurrencyDialog.zul", null, arg);
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

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

}